

package es.igosoftware.globe;

import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.experimental.vectorial.symbolizer.GGlobeVectorialSymbolizer2D;


public interface IGlobeVector2Layer
         extends
            IGlobeVectorLayer<IVector2> {


   @Override
   public GGlobeVectorialSymbolizer2D getSymbolizer();


   public void clearCache();


}
