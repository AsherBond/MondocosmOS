

package es.igosoftware.euclid.experimental.vectorial.rendering.styling;

import java.awt.Paint;


public interface ISurface2DStyle
         extends
            IStyle {


   public Paint getSurfacePaint();


   public boolean isGroupableWith(final ISurface2DStyle that);


}
