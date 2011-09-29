

package es.igosoftware.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;

import es.igosoftware.logging.GLogger;
import es.igosoftware.logging.ILogger;
import gov.nasa.worldwind.formats.dds.DDSCompressor;
import gov.nasa.worldwind.formats.dds.DXTCompressionAttributes;
import gov.nasa.worldwind.util.WWIO;
import gov.nasa.worldwind.util.WWMath;


public class GTextureLoader {
   private static final ILogger logger = GLogger.instance();


   private GTextureLoader() {
   }


   public static GTexture loadTexture(final URL url,
                                      final boolean mipmap,
                                      final boolean compressTexture) {
      if (url == null) {
         return null;
      }

      //      logger.info("Loading texture " + url + " (mipmap=" + mipmap + ")...");

      try {
         //         final TextureData newTextureData = flipVerticallyIfNecessary(rawLoadTextureData(url, mipmap, compressTexture), url,
         //                  mipmap);
         final TextureData newTextureData = rawLoadTextureData(url, mipmap, compressTexture);
         if (newTextureData == null) {
            logger.logWarning("Can't load texture from " + url);
            return null;
         }

         final Texture newTexture = TextureIO.newTexture(newTextureData);

         if (!WWMath.isPowerOfTwo(newTexture.getImageHeight())) {
            logger.logWarning("Texture " + url + ", height is not power of 2 (" + newTexture.getImageHeight() + ")");
         }
         if (!WWMath.isPowerOfTwo(newTexture.getImageWidth())) {
            logger.logWarning("Texture " + url + ", width is not power of 2 (" + newTexture.getImageWidth() + ")");
         }

         //         logger.info("  Loaded texture " + url + " (mipmap=" + mipmap + "), result=" + newTexture);

         return new GTexture(newTexture);
      }
      catch (final IOException e) {
         logger.logSevere("Error loading texture " + url, e);
      }

      return null;
   }


   private static TextureData rawLoadTextureData(final URL url,
                                                 final boolean mipmap,
                                                 final boolean compress) throws IOException {
      if (compress && !url.toString().toLowerCase().endsWith("dds")) {
         final DXTCompressionAttributes attributes = DDSCompressor.getDefaultCompressionAttributes();
         attributes.setBuildMipmaps(mipmap);

         final ByteBuffer buffer = DDSCompressor.compressImageURL(url, attributes);
         if (buffer == null) {
            return null;
         }

         return TextureIO.newTextureData(WWIO.getInputStreamFromByteBuffer(buffer), mipmap, null);
      }


      return TextureIO.newTextureData(url, mipmap, null);
   }


   //   private static TextureData flipVerticallyIfNecessary(final TextureData textureData,
   //                                                        final URL url,
   //                                                        final boolean mipmap) throws IOException {
   //      // return textureData;
   //
   //      if (textureData == null) {
   //         return null;
   //      }
   //
   //      if (!textureData.getMustFlipVertically()) {
   //         return textureData;
   //      }
   //
   //      final BufferedImage image = ImageIO.read(url);
   //      ImageUtil.flipImageVertically(image);
   //
   //      final TextureData newTextureData = TextureIO.newTextureData(image, mipmap);
   //      newTextureData.setMustFlipVertically(false);
   //      return newTextureData;
   //   }


   private static TextureData rawLoadTextureData(final BufferedImage image,
                                                 final boolean mipmap,
                                                 final boolean compress) throws IOException {
      if (compress) {
         final DXTCompressionAttributes attributes = DDSCompressor.getDefaultCompressionAttributes();
         attributes.setBuildMipmaps(mipmap);

         final DDSCompressor compressor = new DDSCompressor();
         final ByteBuffer buffer = compressor.compressImage(image, attributes);

         return TextureIO.newTextureData(WWIO.getInputStreamFromByteBuffer(buffer), mipmap, null);
      }

      return TextureIO.newTextureData(image, mipmap);
   }


   public static GTexture loadTexture(final BufferedImage image,
                                      final boolean mipmap,
                                      final boolean compressTexture) {
      if (image == null) {
         return null;
      }

      //      logger.info("Loading texture " + url + " (mipmap=" + mipmap + ")...");

      try {
         final TextureData newTextureData = rawLoadTextureData(image, mipmap, compressTexture);

         final Texture newTexture = TextureIO.newTexture(newTextureData);

         if (!WWMath.isPowerOfTwo(newTexture.getImageHeight())) {
            logger.logWarning("Texture " + image + ", height is not power of 2 (" + newTexture.getImageHeight() + ")");
         }
         if (!WWMath.isPowerOfTwo(newTexture.getImageWidth())) {
            logger.logWarning("Texture " + image + ", width is not power of 2 (" + newTexture.getImageWidth() + ")");
         }

         //         logger.info("  Loaded texture " + url + " (mipmap=" + mipmap + "), result=" + newTexture);

         return new GTexture(newTexture);
      }
      catch (final IOException e) {
         logger.logSevere("Error loading texture " + image, e);
      }

      return null;
   }


}
