

package es.igosoftware.logging;


public interface ILogger {


   public void logInfo(final String msg);


   public void logWarning(final String msg);


   public void logSevere(final String msg);


   public void logSevere(final Throwable e);


   public void logSevere(final String msg,
                         final Throwable e);


   public void logIncreaseIdentationLevel();


   public void logDecreaseIdentationLevel();


}
