

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.util.Collection;
import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IProjectionTool;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.ISymbolizer2D;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GSymbol2D;
import es.igosoftware.euclid.experimental.vectorial.rendering.utils.GRenderingQuadtree;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.ntree.GElementGeometryPair;
import es.igosoftware.euclid.ntree.GGeometryNTreeParameters;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorI2;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GStringUtils;


public class GVectorial2DRenderer {

   private final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>           _features;
   private final GRenderingQuadtree<IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> _quadtree;
   private final boolean                                                                                                 _verbose;


   public GVectorial2DRenderer(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features,
                               final boolean verbose) {
      GAssert.notNull(features, "features");

      final long start = System.currentTimeMillis();

      _features = features;
      _verbose = verbose;
      _quadtree = createQuadtree(verbose);

      if (verbose) {
         System.out.println();
         System.out.println("- Created renderer for " + features.size() + " features in "
                            + GStringUtils.getTimeMessage(System.currentTimeMillis() - start, false));
      }
   }


   private GRenderingQuadtree<IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> createQuadtree(final boolean verbose) {
      final GGeometryNTreeParameters.AcceptLeafNodeCreationPolicy acceptLeafNodeCreationPolicy;
      acceptLeafNodeCreationPolicy = new GGeometryNTreeParameters.Accept2DLeafNodeCreationPolicy<

      IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>,

      IBoundedGeometry2D<? extends IFinite2DBounds<?>>

      >() {

         @Override
         public boolean acceptLeafNodeCreation(final int depth,
                                               final GAxisAlignedOrthotope<IVector2, ?> bounds,
                                               final Collection<GElementGeometryPair<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> elements) {
            if (depth >= 20) {
               return true;
            }

            return (elements.size() <= 50);
         }
      };


      final GGeometryNTreeParameters parameters = new GGeometryNTreeParameters(verbose, acceptLeafNodeCreationPolicy,
               GGeometryNTreeParameters.BoundsPolicy.GIVEN, true);

      final GAxisAlignedRectangle bounds = _features.getBounds().asRectangle().expandedByPercent(0.05);
      return new GRenderingQuadtree<IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>(
               "Rendering", _features, parameters, bounds);
   }


   //   public BufferedImage getRenderedImage(final GAxisAlignedRectangle viewport,
   //                                         final int imageWidth,
   //                                         final int imageHeight,
   //                                         final IProjectionTool projectionTool,
   //                                         final ISymbolizer2D symbolizer) {
   //      GAssert.notNull(viewport, "viewport");
   //      GAssert.isPositive(imageWidth, "imageWidth");
   //      GAssert.isPositive(imageHeight, "imageHeight");
   //      GAssert.notNull(renderingStyle, "renderingStyle");
   //
   //      final BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR);
   //      image.setAccelerationPriority(1);
   //
   //      final IVectorial2DDrawer drawer = new GJava2DVectorial2DDrawer(image, true);
   //
   //      render(viewport, image, projectionTool, renderingStyle, drawer);
   //
   //      return image;
   //   }


   public void render(final GAxisAlignedRectangle viewport,
                      final IVectorI2 renderExtent,
                      final IProjectionTool projectionTool,
                      final ISymbolizer2D symbolizer,
                      final IVectorial2DDrawer drawer) {
      GAssert.notNull(viewport, "viewport");
      GAssert.notNull(renderExtent, "renderExtent");
      GAssert.notNull(projectionTool, "projectionTool");
      GAssert.notNull(symbolizer, "symbolizer");
      GAssert.notNull(drawer, "drawer");

      symbolizer.preprocessFeatures(_features);

      symbolizer.preRender(renderExtent, projectionTool, viewport, symbolizer, drawer);

      final IVectorial2DRenderUnit renderUnit = new GVectorial2DRenderUnit();
      final GRenderUnitResult renderUnitResult = renderUnit.render(renderExtent, _quadtree, _features.getProjection(),
               projectionTool, viewport, symbolizer, drawer);

      final IVectorial2DSymbolsRenderer symbolsRenderer = createSymbolsRenderer(renderUnitResult.getNonGroupableSymbols(),
               renderUnitResult.getGroupableSymbols(), symbolizer, drawer);
      symbolsRenderer.draw();

      symbolizer.postRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
   }


   private GVectorial2DSymbolsRenderer createSymbolsRenderer(final List<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> nonGroupableSymbols,
                                                             final List<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> groupableSymbols,
                                                             final ISymbolizer2D symbolizer,
                                                             final IVectorial2DDrawer drawer) {

      return new GVectorial2DSymbolsRenderer(nonGroupableSymbols, groupableSymbols, symbolizer, drawer, _verbose);
   }


}
