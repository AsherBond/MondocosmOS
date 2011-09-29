

package es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.ICurve2D;
import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IProjectionTool;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GSymbol2DList;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.ntree.GGTNode;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorI2;


public interface ISymbolizer2D {


   /* -------------------------------------------------------------------------------------- */
   /* general */
   public boolean isDebugRendering();


   public double getLODMinSize();


   public boolean isRenderLODIgnores();


   public void preprocessFeatures(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features);


   public void preRender(final IVectorI2 renderExtent,
                         final IProjectionTool projectionTool,
                         final GAxisAlignedRectangle viewport,
                         final ISymbolizer2D symbolizer,
                         final IVectorial2DDrawer drawer);


   public void postRender(final IVectorI2 renderExtent,
                          final IProjectionTool projectionTool,
                          final GAxisAlignedRectangle viewport,
                          final ISymbolizer2D symbolizer,
                          final IVectorial2DDrawer drawer);


   public double getMaximumSizeInMeters(final IVectorial2DRenderingScaler scaler);


   public boolean isClusterSymbols();


   /* -------------------------------------------------------------------------------------- */
   /* nodes */
   public GSymbol2DList getNodeSymbols(final GGTNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> node,
                                       final IVectorial2DRenderingScaler scaler);


   /* -------------------------------------------------------------------------------------- */
   /* point style */
   public GSymbol2DList getPointSymbols(final IVector2 point,
                                        final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                        final IVectorial2DRenderingScaler scaler);


   /* -------------------------------------------------------------------------------------- */
   /* curves */
   public GSymbol2DList getCurveSymbols(final ICurve2D<? extends IFinite2DBounds<?>> curve,
                                        final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                        final IVectorial2DRenderingScaler scaler);


   /* -------------------------------------------------------------------------------------- */
   /* surfaces */
   public GSymbol2DList getSurfaceSymbols(final ISurface2D<? extends IFinite2DBounds<?>> surface,
                                          final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                          final IVectorial2DRenderingScaler scaler);


}
