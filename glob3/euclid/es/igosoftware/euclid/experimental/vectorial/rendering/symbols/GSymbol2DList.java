

package es.igosoftware.euclid.experimental.vectorial.rendering.symbols;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;


public class GSymbol2DList {

   private static final long                                                                 serialVersionUID = 1L;


   private final List<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> _symbols;


   public GSymbol2DList() {
      _symbols = new ArrayList<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>();
   }


   public GSymbol2DList(final GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> symbol) {
      _symbols = new ArrayList<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>(1);
      _symbols.add(symbol);
   }


   public GSymbol2DList(final GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>... symbols) {
      _symbols = Arrays.asList(symbols);
   }


   public GSymbol2DList(final Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> symbols) {
      _symbols = new ArrayList<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>(symbols);
   }


   public GSymbol2DList(final int initialCapacity) {
      _symbols = new ArrayList<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>(initialCapacity);
   }


   public GSymbol2DList(final GSymbol2DList symbols) {
      _symbols = new ArrayList<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>(symbols._symbols);
   }


   public void add(final GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> symbol) {
      _symbols.add(symbol);
   }


   public void addAll(final Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> symbols) {
      _symbols.addAll(symbols);
   }


   public void addAll(final GSymbol2DList symbols) {
      _symbols.addAll(symbols._symbols);
   }


   public List<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> getSymbols() {
      return _symbols;
   }


}
