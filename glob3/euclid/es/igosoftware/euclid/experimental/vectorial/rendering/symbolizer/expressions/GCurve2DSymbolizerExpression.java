

package es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.ICurve2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IProjectionTool;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ICurve2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.ISymbolizer2D;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GSymbol2DList;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorI2;
import es.igosoftware.util.GAssert;


public abstract class GCurve2DSymbolizerExpression<GeometryT extends ICurve2D<? extends IFinite2DBounds<?>>>
         extends
            GExpressionAbstract<GeometryT, GSymbol2DList>
         implements
            IGeometry2DSymbolizerExpression<GeometryT> {


   protected final IExpression<GeometryT, ICurve2DStyle> _curveStyleExpression;


   protected GCurve2DSymbolizerExpression(final IExpression<GeometryT, ICurve2DStyle> curveStyleExpression) {
      GAssert.notNull(curveStyleExpression, "curveStyleExpression");

      _curveStyleExpression = curveStyleExpression;
   }


   @Override
   public final double getMaximumSizeInMeters(final IVectorial2DRenderingScaler scaler) {
      return _curveStyleExpression.getMaximumSizeInMeters(scaler);
   }


   @Override
   public final void preprocessFeatures(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features) {
      _curveStyleExpression.preprocessFeatures(features);
   }


   @Override
   public final void preRender(final IVectorI2 renderExtent,
                               final IProjectionTool projectionTool,
                               final GAxisAlignedRectangle viewport,
                               final ISymbolizer2D symbolizer,
                               final IVectorial2DDrawer drawer) {
      _curveStyleExpression.preRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
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
