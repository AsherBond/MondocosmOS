

package es.igosoftware.globe;

import es.igosoftware.globe.animations.IGlobeAnimationsScheduler;
import es.igosoftware.io.IProgressReporter;
import es.igosoftware.logging.ILogger;


public interface IGlobeRunningContext {


   public ILogger getLogger();


   public IGlobeBitmapFactory getBitmapFactory();


   public IGlobeApplication getApplication();


   public IGlobeTranslator getTranslator();


   public IGlobeCameraController getCameraController();


   public IGlobeLayerDataManager getLayerDataManager();


   public IGlobeWorldWindModel getWorldWindModel();


   public IGlobeAnimationsScheduler getAnimationsScheduler();


   public IProgressReporter getProgressReporter();


}
