

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.ICurve2D;
import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.GVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IProjectionTool;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.ISymbolizer2D;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GSymbol2D;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GSymbol2DList;
import es.igosoftware.euclid.experimental.vectorial.rendering.utils.GRenderingQuadtree;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.multigeometry.GMultiGeometry2D;
import es.igosoftware.euclid.ntree.GElementGeometryPair;
import es.igosoftware.euclid.ntree.GGTInnerNode;
import es.igosoftware.euclid.ntree.GGTNode;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorI2;


class GVectorial2DRenderUnit
         implements
            IVectorial2DRenderUnit {


   //   private static final boolean CLUSTER_RENDERING = true;


   @Override
   public GRenderUnitResult render(final IVectorI2 renderExtent,
                                   final GRenderingQuadtree<IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> quadtree,
                                   final GProjection projection,
                                   final IProjectionTool projectionTool,
                                   final GAxisAlignedRectangle viewport,
                                   final ISymbolizer2D symbolizer,
                                   final IVectorial2DDrawer drawer) {

      final IVectorial2DRenderingScaler scaler = new GVectorial2DRenderingScaler(viewport, projection, projectionTool,
               renderExtent);

      final GAxisAlignedRectangle extendedViewport = calculateExtendedViewport(viewport, scaler, symbolizer);

      final List<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> groupableSymbols = new LinkedList<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>();
      final List<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> nonGroupableSymbols = new LinkedList<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>();

      processNode(quadtree.getRoot(), extendedViewport, symbolizer, scaler, drawer, groupableSymbols, nonGroupableSymbols);

      return new GRenderUnitResult(groupableSymbols, nonGroupableSymbols);
   }


   private static GAxisAlignedRectangle calculateExtendedViewport(final GAxisAlignedRectangle viewport,
                                                                  final IVectorial2DRenderingScaler scaler,
                                                                  final ISymbolizer2D symbolizer) {
      final double maximumSize = symbolizer.getMaximumSizeInMeters(scaler) * 2;

      IVector2 lower = scaler.increment(viewport._lower, -maximumSize, -maximumSize);
      if (lower == null) {
         lower = scaler.increment(viewport._lower, -maximumSize, 0);
         if (lower == null) {
            lower = scaler.increment(viewport._lower, 0, -maximumSize);
            if (lower == null) {
               lower = viewport._lower;
            }
         }
      }

      IVector2 upper = scaler.increment(viewport._upper, maximumSize, maximumSize);
      if (upper == null) {
         upper = scaler.increment(viewport._upper, maximumSize, 0);
         if (upper == null) {
            upper = scaler.increment(viewport._upper, 0, maximumSize);
            if (upper == null) {
               upper = viewport._upper;
            }
         }
      }

      return new GAxisAlignedRectangle(lower, upper);
   }


   private static void processNode(final GGTNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> node,
                                   final GAxisAlignedRectangle extendedRegion,
                                   final ISymbolizer2D symbolizer,
                                   final IVectorial2DRenderingScaler scaler,
                                   final IVectorial2DDrawer drawer,
                                   final List<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> groupableSymbols,
                                   final List<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> nonGroupableSymbols) {

      final GAxisAlignedRectangle nodeBounds = node.getBounds().asRectangle();

      if (!nodeBounds.touches(extendedRegion)) {
         return;
      }


      //      final IVector2 nodeExtent = nodeBounds.expandedByDistance(renderingStyle.getMaximumSizeInMeters(scaler)).getExtent();
      final GAxisAlignedRectangle scaledBounds = scaler.scaleAndTranslate(nodeBounds).asRectangle();

      if (scaledBounds.area() * 2 <= symbolizer.getLODMinSize()) {
         if (symbolizer.isDebugRendering()) {
            final GAxisAlignedOrthotope<IVector2, ?> scaledNodeBounds = scaler.scaleAndTranslate(nodeBounds);
            drawer.fillRect(scaledNodeBounds, Color.RED);
         }

         return;
      }


      addSymbols(symbolizer.getNodeSymbols(node, scaler), groupableSymbols, nonGroupableSymbols);


      if (node instanceof GGTInnerNode) {
         final GGTInnerNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> inner;
         inner = (GGTInnerNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>) node;

         for (final GGTNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> child : inner.getChildren()) {
            processNode(child, extendedRegion, symbolizer, scaler, drawer, groupableSymbols, nonGroupableSymbols);
         }
      }

      for (final GElementGeometryPair<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> pair : node.getElements()) {
         drawGeometry(pair.getGeometry(), pair.getElement(), extendedRegion, symbolizer, scaler, drawer, groupableSymbols,
                  nonGroupableSymbols);
      }
   }


   private static void drawGeometry(final IBoundedGeometry2D<? extends IFinite2DBounds<?>> geometry,
                                    final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                    final GAxisAlignedRectangle extendedRegion,
                                    final ISymbolizer2D symbolizer,
                                    final IVectorial2DRenderingScaler scaler,
                                    final IVectorial2DDrawer drawer,
                                    final List<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> groupableSymbols,
                                    final List<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> nonGroupableSymbols) {

      if (!geometry.getBounds().asAxisAlignedOrthotope().touches(extendedRegion)) {
         return;
      }

      if (geometry instanceof GMultiGeometry2D) {
         @SuppressWarnings("unchecked")
         final GMultiGeometry2D<IBoundedGeometry2D<? extends IFinite2DBounds<?>>> multigeometry = (GMultiGeometry2D<IBoundedGeometry2D<? extends IFinite2DBounds<?>>>) geometry;
         for (final IBoundedGeometry2D<? extends IFinite2DBounds<?>> child : multigeometry) {
            drawGeometry(child, feature, extendedRegion, symbolizer, scaler, drawer, groupableSymbols, nonGroupableSymbols);
         }
      }
      else if (geometry instanceof IVector2) {
         final IVector2 point = (IVector2) geometry;
         addSymbols(symbolizer.getPointSymbols(point, feature, scaler), groupableSymbols, nonGroupableSymbols);
      }
      else if (geometry instanceof ICurve2D<?>) {
         final ICurve2D<? extends IFinite2DBounds<?>> curve = (ICurve2D<? extends IFinite2DBounds<?>>) geometry;
         addSymbols(symbolizer.getCurveSymbols(curve, feature, scaler), groupableSymbols, nonGroupableSymbols);
      }
      else if (geometry instanceof ISurface2D<?>) {
         final ISurface2D<? extends IFinite2DBounds<?>> surface = (ISurface2D<? extends IFinite2DBounds<?>>) geometry;
         addSymbols(symbolizer.getSurfaceSymbols(surface, feature, scaler), groupableSymbols, nonGroupableSymbols);
      }
      else {
         System.out.println("Warning: geometry type " + geometry.getClass() + " not supported");
      }

   }


   private static void addSymbols(final GSymbol2DList symbols,
                                  final List<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> groupableSymbols,
                                  final List<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> nonGroupableSymbols) {
      if (symbols == null) {
         return;
      }

      for (final GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> symbol : symbols.getSymbols()) {
         if (symbol == null) {
            continue;
         }

         if (symbol.isGroupable()) {
            //            symbol.setPosition(groupableSymbols.size());
            groupableSymbols.add(symbol);
         }
         else {
            //            symbol.setPosition(nonGroupableSymbols.size());
            nonGroupableSymbols.add(symbol);
         }
      }
   }

}
