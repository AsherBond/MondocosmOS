

package es.igosoftware.utils;

import javax.media.opengl.Threading;

import es.igosoftware.logging.GLogger;
import es.igosoftware.logging.ILogger;


public class GGLUtils {


   private GGLUtils() {
   }

   private static final boolean VERBOSE_TEXTURE_DISPOSING = false;

   private static final ILogger logger                    = GLogger.instance();


   public static void invokeOnOpenGLThread(final Runnable runnable) {
      if (Threading.isSingleThreaded() && !Threading.isOpenGLThread()) {
         Threading.invokeOnOpenGLThread(runnable);
      }
      else {
         runnable.run();
      }
   }


   public static void disposeTexture(final GTexture texture) {
      if ((texture == null) || !texture.hasGLTexture()) {
         if (VERBOSE_TEXTURE_DISPOSING) {
            logger.logInfo("- ignoring dispose of null texture");
         }
         return;
      }


      // test if the texture has an opengl side resource
      if (texture.getTextureObject() == 0) {
         if (VERBOSE_TEXTURE_DISPOSING) {
            logger.logWarning("- the texture (" + texture + ") TextureObject is ZERO");
         }
         return;
      }


      GGLUtils.invokeOnOpenGLThread(new Runnable() {
         @Override
         public void run() {
            if (VERBOSE_TEXTURE_DISPOSING) {
               logger.logInfo("- disposing texture: " + texture);
            }
            texture.dispose();
         }
      });

   }


}
