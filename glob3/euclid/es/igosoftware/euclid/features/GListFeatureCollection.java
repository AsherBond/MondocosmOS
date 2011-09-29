

package es.igosoftware.euclid.features;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.IBoundedGeometry3D;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.bounding.IFinite3DBounds;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.IFunction;


public class GListFeatureCollection<

VectorT extends IVector<VectorT, ?>,

FeatureGeometryT extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>

>
         implements
            IGlobeFeatureCollection<VectorT, FeatureGeometryT> {


   public static <

   FeatureGeometryT extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>

   > GListFeatureCollection<IVector2, FeatureGeometryT> from2DGeometryList(final GProjection projection,
                                                                           final List<FeatureGeometryT> geometries) {

      final List<GField> fields = Collections.emptyList();

      final List<IGlobeFeature<IVector2, FeatureGeometryT>> features = GCollections.collect(geometries,
               new IFunction<FeatureGeometryT, IGlobeFeature<IVector2, FeatureGeometryT>>() {
                  @Override
                  public IGlobeFeature<IVector2, FeatureGeometryT> apply(final FeatureGeometryT geometry) {
                     return new GGlobeFeature<IVector2, FeatureGeometryT>(geometry, Collections.emptyList());
                  }
               });

      return new GListFeatureCollection<IVector2, FeatureGeometryT>(projection, fields, features);
   }


   public static <

   FeatureGeometryT extends IBoundedGeometry3D<? extends IFinite3DBounds<?>>

   > GListFeatureCollection<IVector3, FeatureGeometryT> from3DGeometryList(final GProjection projection,
                                                                           final List<FeatureGeometryT> geometries) {

      final List<GField> fields = Collections.emptyList();

      final List<IGlobeFeature<IVector3, FeatureGeometryT>> features = GCollections.collect(geometries,
               new IFunction<FeatureGeometryT, IGlobeFeature<IVector3, FeatureGeometryT>>() {
                  @Override
                  public IGlobeFeature<IVector3, FeatureGeometryT> apply(final FeatureGeometryT geometry) {
                     return new GGlobeFeature<IVector3, FeatureGeometryT>(geometry, Collections.emptyList());
                  }
               });

      return new GListFeatureCollection<IVector3, FeatureGeometryT>(projection, fields, features);
   }

   private final GProjection                                    _projection;
   private final List<GField>                                   _fields;
   private final List<IGlobeFeature<VectorT, FeatureGeometryT>> _features;

   private GAxisAlignedOrthotope<VectorT, ?>                    _bounds;

   private EnumSet<GGeometryType>                               _geometryType;


   public GListFeatureCollection(final GProjection projection,
                                 final List<GField> fields,
                                 final List<IGlobeFeature<VectorT, FeatureGeometryT>> features) {
      GAssert.notNull(projection, "projection");
      GAssert.notNull(fields, "fields");
      GAssert.notNull(features, "features");

      _projection = projection;

      // creates copies of the lists to protect the modifications from outside
      _fields = new ArrayList<GField>(fields);
      _features = new ArrayList<IGlobeFeature<VectorT, FeatureGeometryT>>(features);
      for (final IGlobeFeature<VectorT, FeatureGeometryT> feature : _features) {
         feature.setFeatureCollection(this);
      }
   }


   @Override
   public GProjection getProjection() {
      return _projection;
   }


   @Override
   public void acceptVisitor(final IGlobeFeatureCollection.IFeatureVisitor<VectorT, FeatureGeometryT> visitor) {
      try {
         for (int i = 0; i < _features.size(); i++) {
            visitor.visit(_features.get(i), i);
         }
      }
      catch (final IGlobeFeatureCollection.AbortVisiting e) {
         // ignore exception, just exit the for loop
      }
   }


   @Override
   public String toString() {
      return "GListFeatureCollection [projection=" + _projection + ", fields=" + _fields.size() + ", features="
             + _features.size() + "]";
   }


   @Override
   public Iterator<IGlobeFeature<VectorT, FeatureGeometryT>> iterator() {
      return Collections.unmodifiableList(_features).iterator();
   }


   @Override
   public IGlobeFeature<VectorT, FeatureGeometryT> get(final long index) {
      return _features.get(toInt(index));
   }


   private int toInt(final long index) {
      if (index < 0) {
         throw new IndexOutOfBoundsException("index #" + index + " is negative");
      }
      if (index > Integer.MAX_VALUE) {
         throw new IndexOutOfBoundsException("index #" + index + " is bigger that Integer.MAX_VALUE");
      }

      return (int) index; // safe to cast here as the bounds was just checked
   }


   @Override
   public boolean isEmpty() {
      return _features.isEmpty();
   }


   @Override
   public long size() {
      return _features.size();
   }


   @Override
   public EnumSet<GGeometryType> getGeometryType() {
      // lazy initialized to avoid an iteration on _features if GeometriesTypes is not needed

      if (_geometryType == null) {
         _geometryType = calculateGeometriesTypes();
      }

      return _geometryType;
   }


   private EnumSet<GGeometryType> calculateGeometriesTypes() {
      final EnumSet<GGeometryType> result = EnumSet.noneOf(GGeometryType.class);

      for (final IGlobeFeature<VectorT, FeatureGeometryT> feature : _features) {
         result.addAll(GGeometryType.getGeometryType(feature.getDefaultGeometry()));
         if (result.containsAll(GGeometryType.ALL)) {
            return GGeometryType.ALL;
         }
      }

      return result;
   }


   @Override
   public List<GField> getFields() {
      return Collections.unmodifiableList(_fields);
   }


   @Override
   public GAxisAlignedOrthotope<VectorT, ?> getBounds() {
      if (_bounds == null) {
         _bounds = calculateBounds();
      }

      return _bounds;
   }


   private GAxisAlignedOrthotope<VectorT, ?> calculateBounds() {

      if (_features.isEmpty()) {
         return null;
      }

      final GAxisAlignedOrthotope<VectorT, ?> firstBounds = _features.get(0).getDefaultGeometry().getBounds().asAxisAlignedOrthotope();
      VectorT minLower = firstBounds._lower.asDouble();
      VectorT maxUpper = firstBounds._upper.asDouble();

      for (int i = 1; i < _features.size(); i++) {
         final GAxisAlignedOrthotope<VectorT, ?> bounds = _features.get(i).getDefaultGeometry().getBounds().asAxisAlignedOrthotope();

         minLower = minLower.min(bounds._lower);
         maxUpper = maxUpper.max(bounds._upper);
      }

      return GAxisAlignedOrthotope.create(minLower, maxUpper);
   }


   @Override
   public int getFieldIndex(final String fieldName) {
      for (int i = 0; i < _fields.size(); i++) {
         if (_fields.get(i).getName().equals(fieldName)) {
            return i;
         }
      }
      return -1;
   }


   @Override
   public boolean hasField(final String fieldName) {
      return getFieldIndex(fieldName) >= 0;
   }


   @Override
   public int getFieldsCount() {
      return _fields.size();
   }


}
