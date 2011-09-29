

package es.igosoftware.globe;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import es.igosoftware.io.GFileName;
import es.igosoftware.util.GImageUtils;
import es.igosoftware.util.LRUCache;


final class GGlobeBitmapFactory
         implements
            IGlobeBitmapFactory {


   private static class IconKey {
      private final GFileName _fileName;
      private final int       _width;
      private final int       _height;


      private IconKey(final GFileName fileName,
                      final int width,
                      final int height) {
         _fileName = fileName;
         _width = width;
         _height = height;
      }


      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + _height;
         result = prime * result + ((_fileName == null) ? 0 : _fileName.hashCode());
         result = prime * result + _width;
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
         final IconKey other = (IconKey) obj;
         if (_height != other._height) {
            return false;
         }
         if (_fileName == null) {
            if (other._fileName != null) {
               return false;
            }
         }
         else if (!_fileName.equals(other._fileName)) {
            return false;
         }
         if (_width != other._width) {
            return false;
         }
         return true;
      }
   }


   private final GGlobeApplication                                  _application;

   private final LRUCache<IconKey, Icon, RuntimeException>          _iconsCache;
   private final LRUCache<IconKey, BufferedImage, RuntimeException> _imagesCache;


   GGlobeBitmapFactory(final GGlobeApplication application) {
      _application = application;

      _iconsCache = initializeIconsCache();
      _imagesCache = initializeImagesCache();
   }


   private LRUCache<IconKey, Icon, RuntimeException> initializeIconsCache() {
      return new LRUCache<IconKey, Icon, RuntimeException>(50, new LRUCache.ValueFactory<IconKey, Icon, RuntimeException>() {
         private static final long serialVersionUID = 1L;


         @Override
         public Icon create(final IconKey key) {

            URL url = null;
            for (final GFileName directory : _application.getIconsDirectories()) {
               final GFileName path = GFileName.fromParts(directory, key._fileName);

               url = getClass().getClassLoader().getResource(path.buildPath('/'));
               if (url != null) {
                  break;
               }
            }

            if (url == null) {
               _application.getLogger().logWarning("Can't find an image named: " + key._fileName);
               return null;
            }

            final ImageIcon icon = new ImageIcon(url);

            final Image image = icon.getImage();
            if (image == null) {
               return icon;
            }
            final int width = image.getWidth(null);
            final int height = image.getHeight(null);
            if ((width == -1) || (height == -1)) {
               return icon;
            }
            if ((width == key._width) && (height == key._height)) {
               return icon;
            }

            final Image resizedImage = image.getScaledInstance(key._width, key._height, Image.SCALE_SMOOTH);
            return new ImageIcon(resizedImage);
         }
      });
   }


   private LRUCache<IconKey, BufferedImage, RuntimeException> initializeImagesCache() {
      return new LRUCache<IconKey, BufferedImage, RuntimeException>(50,
               new LRUCache.ValueFactory<IconKey, BufferedImage, RuntimeException>() {
                  private static final long serialVersionUID = 1L;


                  @Override
                  public BufferedImage create(final IconKey key) {

                     URL url = null;
                     for (final GFileName directory : _application.getIconsDirectories()) {
                        final GFileName path = GFileName.fromParts(directory, key._fileName);

                        url = getClass().getClassLoader().getResource(path.buildPath('/'));
                        if (url != null) {
                           break;
                        }
                     }

                     if (url == null) {
                        _application.getLogger().logWarning("Can't find an image named: " + key._fileName);
                        return null;
                     }

                     try {


                        final BufferedImage image = ImageIO.read(url);

                        if (image == null) {
                           return null;
                        }

                        if ((key._width < 0) || (key._height < 0)) {
                           // negatives sizes means no resize
                           return image;
                        }

                        final int width = image.getWidth(null);
                        final int height = image.getHeight(null);
                        if ((width == -1) || (height == -1)) {
                           return image;
                        }
                        if ((width == key._width) && (height == key._height)) {
                           return image;
                        }

                        final Image resizedImage = image.getScaledInstance(key._width, key._height, Image.SCALE_SMOOTH);
                        return GImageUtils.asBufferedImage(resizedImage, BufferedImage.TYPE_4BYTE_ABGR);
                     }
                     catch (final IOException e) {
                        return null;
                     }
                  }
               });
   }


   @Override
   public Icon getIcon(final GFileName iconName,
                       final int width,
                       final int height) {
      return _iconsCache.get(new IconKey(iconName, width, height));
   }


   @Override
   public BufferedImage getImage(final GFileName imageName,
                                 final int width,
                                 final int height) {
      return _imagesCache.get(new IconKey(imageName, width, height));
   }


   @Override
   public BufferedImage getImage(final GFileName imageName) {
      return getImage(imageName, -1, -1);
   }


   @Override
   public Icon getSmallIcon(final GFileName iconName) {
      final int defaultIconSize = _application.getDefaultIconSize();
      return getIcon(iconName, defaultIconSize, defaultIconSize);
   }
}
