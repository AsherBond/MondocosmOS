

package es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.IGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IProjectionTool;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.ISymbolizer2D;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorI2;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.IFunction;


public class GTransformerExpression<GeometryT extends IGeometry2D, ExpressionResultT, ResultT>
         extends
            GExpressionAbstract<GeometryT, ResultT> {


   private final IExpression<GeometryT, ExpressionResultT> _expression;
   private final IFunction<ExpressionResultT, ResultT>     _function;


   public GTransformerExpression(final IExpression<GeometryT, ExpressionResultT> expression,
                                 final IFunction<ExpressionResultT, ResultT> function) {
      GAssert.notNull(expression, "expression");
      GAssert.notNull(function, "function");

      _expression = expression;
      _function = function;
   }


   @Override
   public double getMaximumSizeInMeters(final IVectorial2DRenderingScaler scaler) {
      return _expression.getMaximumSizeInMeters(scaler);
   }


   @Override
   public void preprocessFeatures(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features) {
      _expression.preprocessFeatures(features);
   }


   @Override
   public void preRender(final IVectorI2 renderExtent,
                         final IProjectionTool projectionTool,
                         final GAxisAlignedRectangle viewport,
                         final ISymbolizer2D symbolizer,
                         final IVectorial2DDrawer drawer) {
      _expression.preRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
   }


   @Override
   public void postRender(final IVectorI2 renderExtent,
                          final IProjectionTool projectionTool,
                          final GAxisAlignedRectangle viewport,
                          final ISymbolizer2D symbolizer,
                          final IVectorial2DDrawer drawer) {
      _expression.postRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
   }


   @Override
   public ResultT evaluate(final GeometryT geometry,
                           final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                           final IVectorial2DRenderingScaler scaler) {
      final ExpressionResultT expressionEvaluation = _expression.evaluate(geometry, feature, scaler);
      return _function.apply(expressionEvaluation);
   }


}
