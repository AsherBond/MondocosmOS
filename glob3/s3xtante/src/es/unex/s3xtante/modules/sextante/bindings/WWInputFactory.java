

package es.unex.s3xtante.modules.sextante.bindings;

import java.util.ArrayList;

import es.igosoftware.globe.IGlobeRasterLayer;
import es.igosoftware.globe.IGlobeVector2Layer;
import es.igosoftware.util.GAssert;
import es.unex.s3xtante.tables.Tables;
import es.unex.sextante.core.AbstractInputFactory;
import es.unex.sextante.core.NamedExtent;
import es.unex.sextante.dataObjects.I3DRasterLayer;
import es.unex.sextante.dataObjects.IDataObject;
import es.unex.sextante.dataObjects.ITable;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;


/**
 * An input factory to get data objects from WW into SEXTANTE
 * 
 * @author volaya
 * 
 */
public class WWInputFactory
         extends
            AbstractInputFactory {

   private final Model _model;


   public WWInputFactory(final Model model) {
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
         if (layer instanceof IGlobeVector2Layer) {
            final IGlobeVector2Layer globeVector2Layer = (IGlobeVector2Layer) layer;
            obj = new WWVectorLayer(globeVector2Layer.getName(), globeVector2Layer.getFeaturesCollection());
            layers.add(obj);
         }
         else if (layer instanceof IGlobeRasterLayer) {
            obj = new WWRasterLayer();
            ((WWRasterLayer) obj).create((IGlobeRasterLayer) layer);
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
      return new String[] { "tif", "asc" };
   }


   @Override
   public String[] getTableInputExtensions() {
      return new String[] { "csv" };
   }


   @Override
   public String[] getVectorLayerInputExtensions() {
      return new String[] { "shp" };
   }


   @Override
   public void close(final String sName) {
      // TODO
   }


   @Override
   public IDataObject openDataObjectFromFile(final String sFilename) {

      return null;
      //TODO:*****************
      /*if (sFilename.endsWith("shp")) {

      }
      else if (sFilename.endsWith("asc")) {

      }
      else if (sFilename.endsWith("tif")) {

      }
      else if (sFilename.endsWith("dbf")) {

      }

      else {
         return null;
      }*/

   }


   @Override
   public I3DRasterLayer[] get3DRasterLayers() {
      return new I3DRasterLayer[0];
   }


   @Override
   public String[] get3DRasterLayerInputExtensions() {
      return new String[] { "asc3D" };
   }

}
