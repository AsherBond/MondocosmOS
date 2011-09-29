

package es.igosoftware.utils;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

import es.igosoftware.logging.GLogger;
import es.igosoftware.logging.ILogger;


public class GTexture {

   private static final ILogger logger = GLogger.instance();


   private final Texture        _glTexture;
   private boolean              _bind;
   private boolean              _enable;


   private GL                   _lastSeenGL;


   public GTexture(final Texture glTexture) {
      _glTexture = glTexture;
   }


   public boolean hasGLTexture() {
      return (_glTexture != null);
   }


   public int getWidth() {
      return _glTexture.getWidth();
   }


   public int getHeight() {
      return _glTexture.getHeight();
   }


   public TextureCoords getImageTexCoords() {
      return _glTexture.getImageTexCoords();
   }


   public int getTextureObject() {
      return _glTexture.getTextureObject();
   }


   public boolean getMustFlipVertically() {
      return _glTexture.getMustFlipVertically();
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_glTexture == null) ? 0 : _glTexture.hashCode());
      return result;
   }


   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final GTexture other = (GTexture) obj;
      if (_glTexture == null) {
         if (other._glTexture != null) {
            return false;
         }
      }
      else if (!_glTexture.equals(other._glTexture)) {
         return false;
      }
      return true;
   }


   private void logWarning(final String msg) {
      logger.logWarning(msg + " (" + this + ")");
      showStackTrace();
   }


   private void showStackTrace() {
      final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

      // start from 3 to remove the 3 topmost traces
      for (int i = 3; i < stackTrace.length; i++) {
         final String traceString = stackTrace[i].toString();
         if (!traceString.startsWith("es.igosoftware")) {
            break;
         }
         logger.logWarning("  " + traceString);
      }
   }


   public void enable() {
      if (_enable) {
         logWarning("Trying to enable an already enable texture.");
      }
      else {
         _glTexture.enable();
         _lastSeenGL = GLU.getCurrentGL();
         _enable = true;
      }
   }


   public void disable() {
      if (_enable) {
         GGLUtils.invokeOnOpenGLThread(new Runnable() {
            @Override
            public void run() {
               // _glTexture.disable();
               _lastSeenGL.glDisable(_glTexture.getTarget());
               _enable = false;
            }
         });
      }
      else {
         logWarning("Trying to disable a non-enable texture.");
      }
   }


   public void bind() {
      //      if ( _bind) {
      //         logWarning("Trying to bind an already bind texture.");
      //      }
      //      else {
      _glTexture.bind();
      _lastSeenGL = GLU.getCurrentGL();
      //         GLU.getCurrentGL().glBindTexture(target, texID); 
      _bind = true;
      //      }
   }


   public void dispose() {
      if (_bind) {
         //         _lastSeenGL = GLU.getCurrentGL();
         //         GLU.getCurrentGL().glDeleteTextures(1, new int[] {texID}, 0);
         //         texID = 0;         
         _bind = false;


         GGLUtils.invokeOnOpenGLThread(new Runnable() {
            @Override
            public void run() {
               // _glTexture.dispose();
               _lastSeenGL.glDeleteTextures(1, new int[] {
                  _glTexture.getTextureObject()
               }, 0);
               // _glTexture.texID = 0;
               _enable = false;
            }
         });


      }
      //      else {
      //         logWarning("Trying to dispose an non bind texture.");
      //      }
   }


}
