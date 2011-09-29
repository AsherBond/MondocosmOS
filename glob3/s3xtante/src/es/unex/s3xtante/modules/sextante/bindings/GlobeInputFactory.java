

package es.unex.s3xtante.modules.sextante.bindings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.experimental.vectorial.GShapefileTools;
import es.igosoftware.experimental.vectorial.GVectorial2DLayer;
import es.igosoftware.globe.IGlobeRasterLayer;
import es.igosoftware.globe.IGlobeVectorLayer;
import es.igosoftware.globe.layers.GESRIAsciiFileTools;
import es.igosoftware.globe.layers.GGlobeRasterLayer;
import es.igosoftware.util.GAssert;
import es.unex.s3xtante.tables.CSVFileTools;
import es.unex.s3xtante.tables.Tables;
import es.unex.sextante.core.AbstractInputFactory;
import es.unex.sextante.core.NamedExtent;
import es.unex.sextante.core.Sextante;
import es.unex.sextante.dataObjects.I3DRasterLayer;
import es.unex.sextante.dataObjects.IDataObject;
import es.unex.sextante.dataObjects.ITable;
import es.unex.sextante.dataObjects.IVectorLayer;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;


/**
 * An input factory to get data objects from WW into SEXTANTE
 * 
 * @author volaya
 * 
 */
public class GlobeInputFactory
         extends
            AbstractInputFactory {

   private final Model _model;


   public GlobeInputFactory(final Model model) {
      GAssert.notNull(model, "model");

      _model = model;
   }


   @Override
   public void createDataObjects() {

      IDataObject obj;
      final ArrayList<IDataObject> layers = new ArrayList<IDataObject>();
      final LayerList layerList = _model.getLayers();

      for (int i = 0; i < layerList.size(); i++) {
         final Layer layer = layerList.get(i);
         if (layer instanceof IGlobeVectorLayer) {
            final IGlobeVectorLayer globeVectorLayer = (IGlobeVectorLayer) layer;
            obj = new GlobeVectorLayer(globeVectorLayer, false);
            layers.add(obj);
         }
         else if (layer instanceof IGlobeRasterLayer) {
            obj = new GlobeRasterLayer();
            ((GlobeRasterLayer) obj).create((IGlobeRasterLayer) layer);
            layers.add(obj);
         }
      }

      final ITable[] tables = Tables.getTables();

      m_Objects = new IDataObject[layers.size() + tables.length];

      for (int i = 0; i < layers.size(); i++) {
         m_Objects[i] = layers.get(i);
      }

      for (int i = 0; i < tables.length; i++) {
         m_Objects[i + layers.size()] = tables[i];
      }

   }


   @Override
   public NamedExtent[] getPredefinedExtents() {
      return new NamedExtent[0];
   }


   @Override
   public String[] getRasterLayerInputExtensions() {
      return new String[] {
                        "tif",
                        "asc"
      };
   }


   @Override
   public String[] getTableInputExtensions() {
      return new String[] {
         "csv"
      };
   }


   @Override
   public String[] getVectorLayerInputExtensions() {
      return new String[] {
         "shp"
      };
   }


   @Override
   public void close(final String sName) {
      // TODO
   }


   @Override
   public IDataObject openDataObjectFromFile(final String sFilename) {


      if (sFilename.endsWith("shp")) {
         try {
            final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> fc = GShapefileTools.readFile(new File(
                     sFilename));
            final IVectorLayer layer = new GlobeVectorLayer(new GVectorial2DLayer(new File(sFilename).getName(), fc, false),
                     false);
            return layer;
         }
         catch (final IOException e) {
            Sextante.addErrorToLog(e);
            return null;
         }
      }
      else if (sFilename.endsWith("asc")) {
         try {
            final GGlobeRasterLayer gl = GESRIAsciiFileTools.readFile(new File(sFilename), GProjection.EPSG_4326);
            final GlobeRasterLayer layer = new GlobeRasterLayer();
            layer.create(gl);
            return layer;
         }
         catch (final Exception e) {
            Sextante.addErrorToLog(e);
            return null;
         }
      }
      else if (sFilename.endsWith("csv")) {
         final DefaultTableModel model = CSVFileTools.read(new File(sFilename));
         if (model != null) {
            final GlobeTable table = new GlobeTable();
            table.create(model, sFilename);
            return table;
         }
         return null;
      }
      else {
         return null;
      }
   }


   @Override
   public I3DRasterLayer[] get3DRasterLayers() {
      return new I3DRasterLayer[0];
   }


   @Override
   public String[] get3DRasterLayerInputExtensions() {
      return new String[] {
         "asc3D"
      };
   }

}
