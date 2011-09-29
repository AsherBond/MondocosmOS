

package es.unex.s3xtante.modules.sextante.bindings;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureWriter;
import org.geotools.data.FileDataStoreFactorySpi;
import org.geotools.data.Query;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.features.GField;
import es.igosoftware.euclid.features.GListFeatureCollection;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.shape.IPolygonalChain2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.experimental.vectorial.GShapefileTools;
import es.igosoftware.io.GFileName;
import es.igosoftware.utils.GJTSUtils;
import es.unex.sextante.dataObjects.AbstractVectorLayer;
import es.unex.sextante.dataObjects.IFeatureIterator;
import es.unex.sextante.dataObjects.IVectorLayer;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.IOutputChannel;


public class WWVectorLayer
         extends
            AbstractVectorLayer {

   private GFileName                                                                                                     _filename;
   private String                                                                                                        _name;
   private GProjection                                                                                                   _projection;
   private List<GField>                                                                                                  _fields;
   private IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> _features;


   public WWVectorLayer(final String name,
                        final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> features) {
      _name = name;
      initializeFromFeatures(features);
   }


   private void initializeFromFeatures(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> features) {
      _features = features;
      _projection = features.getProjection();
      _fields = features.getFields();
   }


   public WWVectorLayer(final String name,
                        final List<GField> fields,
                        final GFileName filename,
                        final GProjection projection) {

      _filename = filename;
      _name = name;
      _fields = fields;

      _projection = projection;

      //Le estoy pasando un null porque no se como hacer esto...
      _features = new GListFeatureCollection<IVector2, IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>(
               projection, fields, null);
   }


   @Override
   public void open() {
   }


   @Override
   public void close() {
   }


   @Override
   public void addFeature(final Geometry jtsGeometry,
                          final Object[] values) {
      //      if (_features instanceof IGlobeMutableFeatureCollection) {
      //         final GListMutableFeatureCollection<IVector2, IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> mutable = (GListMutableFeatureCollection<IVector2, IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>) _features;
      //         mutable.add(new GGlobeFeature<IVector2, IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>(
      //                  GJTSUtils.toEuclid(jtsGeometry), Arrays.asList(values)));
      //      }
   }


   @Override
   public String getFieldName(final int i) {
      return _fields.get(i).getName();
   }


   @Override
   public Class<?> getFieldType(final int i) {
      return _fields.get(i).getType();
   }


   @Override
   public int getFieldCount() {
      return _fields.size();
   }


   private static int getShapeType(final IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>> geometry) {
      if (geometry instanceof IPolygon2D) {
         return IVectorLayer.SHAPE_TYPE_POLYGON;
      }
      else if (geometry instanceof IPolygonalChain2D) {
         return IVectorLayer.SHAPE_TYPE_LINE;
      }
      else if (geometry instanceof IVector2) {
         return IVectorLayer.SHAPE_TYPE_POINT;
      }
      else {
         throw new RuntimeException("Unsupported geometry type (" + geometry + ")");
      }
   }


   @Override
   public String getName() {
      return _name;
   }


   @Override
   public void postProcess() {
      saveShapefile();

      try {
         initializeFromFeatures(GShapefileTools.readFile(_filename.asFile()));
      }
      catch (final IOException e) {
         e.printStackTrace();
      }

   }


   public void saveShapefile() {

      try {
         saveFeatures(_features);
      }
      catch (final IOException e) {
         e.printStackTrace();
      }
   }


   @SuppressWarnings("unchecked")
   private void saveFeatures(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> features)
                                                                                                                                                          throws IOException {

      final SimpleFeatureType featureType = buildFeatureType(_name, getShapeType(), _fields, DefaultGeographicCRS.WGS84);
      final DataStore dataStore = createDatastore(_filename, featureType);
      dataStore.createSchema(featureType);
      final Query query = new Query(_name, Filter.INCLUDE);
      final FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = dataStore.getFeatureSource(query.getTypeName());
      final SimpleFeatureType ft = featureSource.getSchema();
      final FeatureWriter<SimpleFeatureType, SimpleFeature> featWriter = dataStore.getFeatureWriterAppend(ft.getTypeName(),
               Transaction.AUTO_COMMIT);

      for (final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature : features) {
         final Geometry gtsGeometry = GJTSUtils.toJTS(feature.getDefaultGeometry());

         final List<Object> attributes = new ArrayList<Object>();
         attributes.addAll(Arrays.asList(feature.getAttributes()));
         final SimpleFeature sf = featWriter.next();
         sf.setAttributes(attributes);
         sf.setDefaultGeometry(gtsGeometry);
         featWriter.write();

      }
      featWriter.close();
   }


   private DataStore createDatastore(final GFileName filename,
                                     final SimpleFeatureType m_FeatureType) throws IOException {

      final File file = filename.asFile();
      final Map<String, Serializable> params = new HashMap<String, Serializable>();
      params.put(ShapefileDataStoreFactory.URLP.key, file.toURI().toURL());
      params.put(ShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.key, false);

      final FileDataStoreFactorySpi factory = new ShapefileDataStoreFactory();
      final ShapefileDataStore dataStore = (ShapefileDataStore) factory.createNewDataStore(params);
      dataStore.createSchema(m_FeatureType);
      return dataStore;

   }


   private static SimpleFeatureType buildFeatureType(final String sName,
                                                     final int iShapeType,
                                                     final List<GField> fields,
                                                     final CoordinateReferenceSystem crs) {
      final SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
      builder.setName(sName);

      final AttributeTypeBuilder attBuilder = new AttributeTypeBuilder();
      builder.add(toGeometryAttribute(iShapeType, crs, attBuilder));
      builder.setDefaultGeometry("geom");
      for (final GField field : fields) {
         final AttributeType type = attBuilder.binding(field.getType()).buildType();
         final AttributeDescriptor descriptor = attBuilder.buildDescriptor(field.getName(), type);
         builder.add(descriptor);
      }
      return builder.buildFeatureType();

   }


   private static GeometryDescriptor toGeometryAttribute(final int shapeType,
                                                         final CoordinateReferenceSystem crs,
                                                         final AttributeTypeBuilder builder) {

      final Class<?> s[] = { Point.class, MultiLineString.class, MultiPolygon.class };
      final GeometryType buildGeometryType = builder.crs(crs).binding(s[shapeType]).buildGeometryType();
      return builder.buildDescriptor("geom", buildGeometryType);

   }


   @Override
   public Rectangle2D getFullExtent() {

      return getRectangle3DBounds(_features);

   }


   private Rectangle2D getRectangle3DBounds(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> features) {

      double xMin = Double.POSITIVE_INFINITY;
      double xMax = Double.NEGATIVE_INFINITY;
      double yMin = Double.POSITIVE_INFINITY;
      double yMax = Double.NEGATIVE_INFINITY;

      for (final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature : features) {
         final GAxisAlignedOrthotope<IVector2, ?> envelope = feature.getDefaultGeometry().getBounds().asAxisAlignedOrthotope();
         xMin = Math.min(xMin, envelope._lower.x());
         yMin = Math.min(yMin, envelope._lower.y());
         xMax = Math.max(xMax, envelope._upper.x());
         yMax = Math.max(yMax, envelope._upper.y());
      }

      return new Rectangle2D.Double(xMin, yMin, xMax - xMin, yMax - yMin);
   }


   @Override
   public Object getCRS() {
      return _projection;
   }


   @Override
   public void setName(final String name) {
      _name = name;
   }


   @Override
   public int getShapeType() {

      if (getShapesCount() == 0) {
         return IVectorLayer.SHAPE_TYPE_WRONG;
      }

      return getShapeType(_features.get(0).getDefaultGeometry());

   }


   @Override
   public Object getBaseDataObject() {

      return _features;

   }


   @Override
   public IFeatureIterator iterator() {

      return new WWFeatureIterator(_features, getFilters());


   }


   @Override
   public boolean canBeEdited() {

      return false;

   }


   @Override
   public void free() {
   }


   @Override
   public IOutputChannel getOutputChannel() {

      return new FileOutputChannel(_filename.buildPath());

   }


}
