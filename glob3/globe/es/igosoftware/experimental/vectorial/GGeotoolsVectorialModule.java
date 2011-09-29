/*

 IGO Software SL  -  info@igosoftware.es

 http://www.glob3.org

-------------------------------------------------------------------------------
 Copyright (c) 2010, IGO Software SL
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
     * Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.
     * Neither the name of the IGO Software SL nor the
       names of its contributors may be used to endorse or promote products
       derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL IGO Software SL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-------------------------------------------------------------------------------

*/


package es.igosoftware.experimental.vectorial;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.swing.data.JDataStoreWizard;
import org.geotools.swing.wizard.JWizard;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.GLayerInfo;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.IGlobeTranslator;
import es.igosoftware.globe.ILayerFactoryModule;
import es.igosoftware.globe.ILayerInfo;
import es.igosoftware.io.GFileName;
import es.igosoftware.io.IProgressProducer;


public class GGeotoolsVectorialModule
         extends
            GAbstractGlobeModule
         implements
            ILayerFactoryModule,
            IProgressProducer {


   private final boolean    _verbose;
   private final AtomicLong _tasksCount = new AtomicLong(0);


   public GGeotoolsVectorialModule(final IGlobeRunningContext context,
                                   final boolean verbose) {
      super(context);
      _verbose = verbose;

      context.getProgressReporter().register(this);
   }


   @Override
   public String getName() {
      return "Geotools Vectorial Module";
   }


   @Override
   public String getVersion() {
      return "experimental";
   }


   @Override
   public String getDescription() {
      return "Module for handling vectorial formats from geootools API";
   }


   @Override
   public List<? extends ILayerInfo> getAvailableLayers(final IGlobeRunningContext context) {
      return GLayerInfo.createFromNames(context.getBitmapFactory().getSmallIcon(GFileName.relative("vectorial.png")),
               "Vectorial layer...");
   }


   @Override
   public GVectorial2DLayer addNewLayer(final IGlobeRunningContext context,
                                        final ILayerInfo layerInfo) {
      connectoToDataSource(context);
      return null;
   }


   /**
    * Allow connecting to any datastore factory from geotools: AbstractDataStoreFactory, ArcSDEDataStoreFactory,
    * ArcSDEJNDIDataStoreFactory, DB2NGDataStoreFactory, DB2NGJNDIDataStoreFactory, H2DataStoreFactory, H2JNDIDataStoreFactory,
    * IndexedShapefileDataStoreFactory, JDBCDataStoreFactory, JDBCJNDIDataStoreFactory, MySQLDataStoreFactory,
    * MySQLJNDIDataStoreFactory, OracleNGDataStoreFactory, OracleNGJNDIDataStoreFactory, OracleNGOCIDataStoreFactory,
    * PostgisNGDataStoreFactory, PostgisNGJNDIDataStoreFactory, PreGeneralizedDataStoreFactory, PropertyDataStoreFactory,
    * ShapefileDataStoreFactory, ShapefileDirectoryFactory, SpatiaLiteDataStoreFactory, SpatiaLiteJNDIDataStoreFactory,
    * SQLServerDataStoreFactory, SQLServerJNDIDataStoreFactory, WFSDataStoreFactory
    * 
    * @param application
    */
   private void connectoToDataSource(final IGlobeRunningContext context) {

      //// TODO: read projection or ask user
      final GProjection projection = GProjection.EPSG_4326;

      //final JDataStoreWizard wizard = new JDataStoreWizard(new ShapefileDataStoreFactory());
      //final JDataStoreWizard wizard = new JDataStoreWizard(new PostgisNGDataStoreFactory());
      //final JDataStoreWizard wizard = new JDataStoreWizard(new OracleNGDataStoreFactory());
      final JDataStoreWizard wizard = new JDataStoreWizard();

      //wizard.setNextEnabled(true);
      //wizard.setBackEnabled(true);
      //wizard.getContentPane().setSize(300, 200);
      final int result = wizard.showModalDialog();

      if (result == JWizard.FINISH) {

         final Map<String, Object> connectionParameters = wizard.getConnectionParameters();

         final IGlobeApplication application = context.getApplication();
         final IGlobeTranslator translator = context.getTranslator();

         try {

            final DataStore dataStore = DataStoreFinder.getDataStore(connectionParameters);

            if (dataStore == null) {
               JOptionPane.showMessageDialog(application.getFrame(), "Could not connect. Check parameters", "Connection error",
                        JOptionPane.ERROR_MESSAGE);
               return;
            }

            final String[] layerList = dataStore.getTypeNames();

            //final LayerSelectionDialog layerDialog = new LayerSelectionDialog(application, "PostGIS layer selection", layerList);
            //final String selectedLayer = layerDialog.showLayerSelectionDialog();
            String selectedLayer = null;

            if (layerList.length == 0) {
               JOptionPane.showMessageDialog(application.getFrame(), "No data available", "Data Storage warning",
                        JOptionPane.WARNING_MESSAGE);
            }
            else if (layerList.length == 1) {
               selectedLayer = layerList[0];
            }
            else {

               selectedLayer = (String) JOptionPane.showInputDialog(application.getFrame(),
                        translator.getTranslation("Select a layer"), translator.getTranslation("Datasource layer selection"),
                        JOptionPane.PLAIN_MESSAGE,
                        context.getBitmapFactory().getIcon(GFileName.relative("new-vectorial.png"), 24, 24), layerList, null);
            }

            final String layerName = selectedLayer;

            final Thread worker = new Thread("Geotools vectorial layer module") {
               @Override
               public void run() {
                  try {

                     final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features = GGeotoolsVectorialLoader.readFeatures(
                              dataStore, layerName, projection);

                     final GVectorial2DLayer layer = new GVectorial2DLayer(layerName, features, _verbose);
                     //               layer.setShowExtents(true);
                     context.getWorldWindModel().addLayer(layer);

                     layer.doDefaultAction(context);
                  }
                  catch (final IOException e) {
                     SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                           JOptionPane.showMessageDialog(application.getFrame(),
                                    "Error opening data storage" + "\n\n " + e.getLocalizedMessage(), "Data storage error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                     });
                  }
                  catch (final Exception e) {
                     SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                           JOptionPane.showMessageDialog(application.getFrame(),
                                    "Error opening data storage" + "\n\n " + e.getLocalizedMessage(), "Data storage error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                     });
                  }
                  finally {
                     _tasksCount.decrementAndGet();
                  }

               }
            };
            _tasksCount.incrementAndGet();
            worker.setDaemon(true);
            worker.setPriority(Thread.MIN_PRIORITY);
            worker.start();

         }
         catch (final IOException e) {
            JOptionPane.showMessageDialog(application.getFrame(), "Could not connect. Check parameters", "Connection error",
                     JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
         }

      }
   }


   @Override
   public long runningTasksCount() {
      return _tasksCount.get();
   }


}
