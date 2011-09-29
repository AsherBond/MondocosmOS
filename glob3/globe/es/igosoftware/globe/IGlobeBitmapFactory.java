

package es.igosoftware.globe;

import java.awt.image.BufferedImage;

import javax.swing.Icon;

import es.igosoftware.io.GFileName;


public interface IGlobeBitmapFactory {

   /**
    * Answer a small icon suitable to fit in menus and toolbars
    */
   public Icon getSmallIcon(final GFileName iconName);


   /**
    * Answer an icon with the given size
    */
   public Icon getIcon(final GFileName iconName,
                       final int width,
                       final int height);


   /**
    * Answer an image without resizing it
    */
   public BufferedImage getImage(final GFileName imageName);


   /**
    * Answer an image with the given size
    */
   public BufferedImage getImage(final GFileName imageName,
                                 final int width,
                                 final int height);

}
