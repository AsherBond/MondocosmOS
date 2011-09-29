

package es.igosoftware.io;

public abstract class GAbstractLoader
         implements
            ILoader,
            IProgressProducer {


   protected GAbstractLoader(final IProgressReporter progressReporter) {
      //      _progressReporter = progressReporter;

      if (progressReporter != null) {
         progressReporter.register(this);
      }
   }


}
