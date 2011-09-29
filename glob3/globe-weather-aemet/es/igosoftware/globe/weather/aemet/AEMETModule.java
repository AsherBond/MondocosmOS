

package es.igosoftware.globe.weather.aemet;

import java.io.IOException;

import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.weather.aemet.data.AEMETData;
import es.igosoftware.globe.weather.aemet.data.AEMETParser;
import es.igosoftware.io.IProgressProducer;


public class AEMETModule
         extends
            GAbstractGlobeModule
         implements
            IProgressProducer {


   private boolean _isRunning = false;


   public AEMETModule(final IGlobeRunningContext context) {
      super(context);
   }


   @Override
   public String getName() {
      return "AEMET Module";
   }


   @Override
   public String getVersion() {
      return "0.1";
   }


   @Override
   public String getDescription() {
      return getName();
   }


   @Override
   public long runningTasksCount() {
      return _isRunning ? 1 : 0;
   }


   @Override
   public void initialize(final IGlobeRunningContext context) {
      super.initialize(context);

      context.getProgressReporter().register(this);

      final Thread worker = new Thread() {
         @Override
         public void run() {
            try {
               final AEMETData data = AEMETParser.loadData();

               context.getWorldWindModel().addLayer(new AEMETLayer(context, data));
            }
            catch (final IOException e) {
               context.getLogger().logSevere("Can't load AEMETData", e);
            }
            finally {
               _isRunning = false;
            }
         }
      };
      worker.setDaemon(true);
      _isRunning = true;
      worker.start();

   }


}
