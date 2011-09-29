

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GSymbol2D;


public class GRenderUnitResult {


   private final List<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> _nonGroupableSymbols;
   private final List<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> _groupableSymbols;


   GRenderUnitResult(final List<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> groupableSymbols,
                     final List<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> nonGroupableSymbols) {
      _groupableSymbols = groupableSymbols;
      _nonGroupableSymbols = nonGroupableSymbols;
   }


   public List<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> getNonGroupableSymbols() {
      return _nonGroupableSymbols;
   }


   public List<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> getGroupableSymbols() {
      return _groupableSymbols;
   }


}
