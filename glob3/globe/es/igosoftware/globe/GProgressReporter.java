

package es.igosoftware.globe;

import java.util.ArrayList;
import java.util.List;

import es.igosoftware.io.IProgressProducer;
import es.igosoftware.io.IProgressReporter;
import es.igosoftware.util.GAssert;


public class GProgressReporter
         implements
            IProgressReporter {

   private final GGlobeApplication       _application;
   private final List<IProgressProducer> _progressProducers     = new ArrayList<IProgressProducer>();

   private final List<IProgressProducer> _copyProgressProducers = new ArrayList<IProgressProducer>();


   public GProgressReporter(final GGlobeApplication application) {
      _application = application;

      startWorker();
   }


   private void startWorker() {
      final Thread worker = new Thread() {
         @Override
         public void run() {
            try {
               while (true) {
                  Thread.sleep(100);
                  updateProgress();
               }
            }
            catch (final InterruptedException e) {
               // do nothing, just exit from run()
            }
         }
      };
      worker.setPriority(Thread.MIN_PRIORITY);
      worker.setDaemon(true);
      worker.start();
   }


   private void updateProgress() {
      synchronized (_progressProducers) {
         _copyProgressProducers.clear();
         _copyProgressProducers.addAll(_progressProducers);
      }


      long runningTasksCount = 0;
      for (final IProgressProducer progressProducer : _copyProgressProducers) {
         runningTasksCount += progressProducer.runningTasksCount();
      }
      setRunningTasksCount(runningTasksCount);
   }


   private void setRunningTasksCount(final long runningTasksCount) {
      _application.setRunningTasksCount(runningTasksCount);
   }


   @Override
   public void register(final IProgressProducer progressProducer) {
      GAssert.notNull(progressProducer, "progressProducer");

      synchronized (_progressProducers) {
         _progressProducers.add(progressProducer);
      }
   }


}
