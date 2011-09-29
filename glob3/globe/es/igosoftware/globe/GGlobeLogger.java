

package es.igosoftware.globe;

import es.igosoftware.logging.ILogger;


final class GGlobeLogger
         implements
            ILogger {

   private final GGlobeApplication _application;
   private final ILogger           _logger;


   GGlobeLogger(final GGlobeApplication application,
                final ILogger logger) {
      _application = application;
      _logger = logger;
   }


   @Override
   public void logWarning(final String msg) {
      _logger.logWarning(decoratedMsg(msg));
   }


   @Override
   public void logSevere(final String msg,
                         final Throwable e) {
      _logger.logSevere(decoratedMsg(msg), e);
   }


   @Override
   public void logSevere(final Throwable e) {
      _logger.logSevere(_application.getApplicationName(), e);
   }


   @Override
   public void logSevere(final String msg) {
      _logger.logSevere(decoratedMsg(msg));
   }


   @Override
   public void logInfo(final String msg) {
      _logger.logInfo(decoratedMsg(msg));
   }


   private String decoratedMsg(final String msg) {
      return _application.getApplicationName() + ": " + msg;
   }


   @Override
   public void logIncreaseIdentationLevel() {
      _logger.logIncreaseIdentationLevel();
   }


   @Override
   public void logDecreaseIdentationLevel() {
      _logger.logDecreaseIdentationLevel();
   }


}
