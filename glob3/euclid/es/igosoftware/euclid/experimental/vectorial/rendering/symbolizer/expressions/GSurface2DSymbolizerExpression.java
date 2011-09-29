

package es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.IGeometry2D;
import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IProjectionTool;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ICurve2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ISurface2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.ISymbolizer2D;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GSymbol2DList;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorI2;
import es.igosoftware.util.GAssert;


public abstract class GSurface2DSymbolizerExpression<

FeatureGeometryT extends IGeometry2D,

GeometryT extends ISurface2D<? extends IFinite2DBounds<?>>

>
         extends
            GExpressionAbstract<FeatureGeometryT, GSymbol2DList>
         implements
            IGeometry2DSymbolizerExpression<FeatureGeometryT> {


   protected final IExpression<GeometryT, ICurve2DStyle>   _curveStyleExpression;
   protected final IExpression<GeometryT, ISurface2DStyle> _surfaceStyleExpression;


   protected GSurface2DSymbolizerExpression(final IExpression<GeometryT, ICurve2DStyle> curveStyleExpression,
                                            final IExpression<GeometryT, ISurface2DStyle> surfaceStyleExpression) {
      GAssert.notNull(curveStyleExpression, "curveStyleExpression");
      GAssert.notNull(surfaceStyleExpression, "surfaceStyleExpression");

      _curveStyleExpression = curveStyleExpression;
      _surfaceStyleExpression = surfaceStyleExpression;
   }


   @Override
   public double getMaximumSizeInMeters(final IVectorial2DRenderingScaler scaler) {
      return _curveStyleExpression.getMaximumSizeInMeters(scaler) + _surfaceStyleExpression.getMaximumSizeInMeters(scaler);
   }


   @Override
   public final void preprocessFeatures(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features) {
      _curveStyleExpression.preprocessFeatures(features);
      _surfaceStyleExpression.preprocessFeatures(features);
   }


   @Override
   public final void preRender(final IVectorI2 renderExtent,
                               final IProjectionTool projectionTool,
                               final GAxisAlignedRectangle viewport,
                               final ISymbolizer2D symbolizer,
                               final IVectorial2DDrawer drawer) {
      _curveStyleExpression.preRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
      _surfaceStyleExpression.preRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
   }


   @Override
   public final void postRender(final IVectorI2 renderExtent,
                                final IProjectionTool projectionTool,
                                final GAxisAlignedRectangle viewport,
                                final ISymbolizer2D symbolizer,
                                final IVectorial2DDrawer drawer) {
      _curveStyleExpression.postRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
   }


}
