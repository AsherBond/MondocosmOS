

package es.unex.s3xtante.modules.sextante.bindings;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;

import es.igosoftware.euclid.features.GField;
import es.igosoftware.euclid.projection.GProjection;
import es.unex.sextante.core.AnalysisExtent;
import es.unex.sextante.core.OutputFactory;
import es.unex.sextante.dataObjects.IRasterLayer;
import es.unex.sextante.dataObjects.ITable;
import es.unex.sextante.dataObjects.IVectorLayer;
import es.unex.sextante.exceptions.UnsupportedOutputChannelException;
import es.unex.sextante.gui.core.DefaultTaskMonitor;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.IOutputChannel;


public class GlobeOutputFactory
         extends
            OutputFactory {

   @Override
   public IVectorLayer getNewVectorLayer(final String sName,
                                         final int iShapeType,
                                         final Class[] types,
                                         final String[] sFields,
                                         final IOutputChannel channel,
                                         final Object crs) throws UnsupportedOutputChannelException {

      final List<GField> fields = new ArrayList<GField>(types.length);
      for (int i = 0; i < sFields.length; i++) {
         fields.add(new GField(sFields[i], types[i]));
      }

      if (channel instanceof FileOutputChannel) {
         final String filename = (((FileOutputChannel) channel).getFilename());
         final GlobeVectorLayer vectorLayer = new GlobeVectorLayer(sName, iShapeType, fields, filename, (GProjection) crs, false);
         return vectorLayer;
      }
      throw new UnsupportedOutputChannelException();

   }


   @Override
   public IRasterLayer getNewRasterLayer(final String sName,
                                         final int iDataType,
                                         final AnalysisExtent extent,
                                         final int iBands,
                                         final IOutputChannel channel,
                                         final Object crs) throws UnsupportedOutputChannelException {

      GProjection proj;

      if ((crs == null) || !(crs instanceof GProjection)) {
         proj = (GProjection) getDefaultCRS();
      }
      else {
         proj = (GProjection) crs;
      }
      if (channel instanceof FileOutputChannel) {
         final String sFilename = ((FileOutputChannel) channel).getFilename();
         final GlobeRasterLayer layer = new GlobeRasterLayer();
         layer.create(sName, sFilename, extent, iDataType, iBands, proj);
         return layer;
      }
      throw new UnsupportedOutputChannelException();

   }


   @Override
   public ITable getNewTable(final String sName,
                             final Class types[],
                             final String[] sFields,
                             final IOutputChannel channel) throws UnsupportedOutputChannelException {

      if (channel instanceof FileOutputChannel) {
         final String sFilename = ((FileOutputChannel) channel).getFilename();
         final GlobeTable table = new GlobeTable();
         table.create(sName, sFilename, sFields);
         return table;
      }
      throw new UnsupportedOutputChannelException();

   }


   @Override
   protected String getTempFolder() {

      return System.getProperty("java.io.tmpdir");

   }


   @Override
   public String[] getRasterLayerOutputExtensions() {

      return new String[] {
         "asc"
      };

   }


   @Override
   public String[] getVectorLayerOutputExtensions() {

      return new String[] {
         "shp"
      };

   }


   @Override
   public String[] getTableOutputExtensions() {

      return new String[] {
         "csv"
      };

   }


   @Override
   public DefaultTaskMonitor getTaskMonitor(final String sTitle,
                                            final boolean bDeterminate,
                                            final JDialog parent) {

      return new DefaultTaskMonitor(sTitle, bDeterminate, parent);

   }


   @Override
   public Object getDefaultCRS() {

      //this is useless right now
      return GProjection.EPSG_4326;

   }


   @Override
   public IVectorLayer getNewVectorLayer(final String name,
                                         final int shapeType,
                                         final Class[] types,
                                         final String[] fields,
                                         final IOutputChannel channel,
                                         final Object crs,
                                         final int[] fieldSize) throws UnsupportedOutputChannelException {

      return getNewVectorLayer(name, shapeType, types, fields, channel, crs);

   }

}
