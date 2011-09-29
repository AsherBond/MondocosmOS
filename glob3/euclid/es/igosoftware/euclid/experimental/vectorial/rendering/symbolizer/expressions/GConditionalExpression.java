

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
import es.igosoftware.util.GMath;


public class GConditionalExpression<GeometryT extends IGeometry2D, ResultT>
         extends
            GExpressionAbstract<GeometryT, ResultT> {


   private final IExpression<GeometryT, Boolean>           _condition;
   private final IExpression<GeometryT, ? extends ResultT> _trueExpression;
   private final IExpression<GeometryT, ? extends ResultT> _falseExpression;


   public GConditionalExpression(final IExpression<GeometryT, Boolean> condition,
                                 final IExpression<GeometryT, ? extends ResultT> trueExpression,
                                 final IExpression<GeometryT, ? extends ResultT> falseExpression) {
      GAssert.notNull(condition, "condition");

      _condition = condition;
      _trueExpression = orNullExpression(trueExpression);
      _falseExpression = orNullExpression(falseExpression);
   }


   @SuppressWarnings("unchecked")
   private IExpression<GeometryT, ResultT> orNullExpression(final IExpression<GeometryT, ? extends ResultT> expression) {
      return (expression == null) ? GNullExpression.INSTANCE : expression;
   }


   @Override
   public double getMaximumSizeInMeters(final IVectorial2DRenderingScaler scaler) {
      return GMath.maxD( //
               _condition.getMaximumSizeInMeters(scaler), //
               _trueExpression.getMaximumSizeInMeters(scaler), //
               _falseExpression.getMaximumSizeInMeters(scaler));
   }


   @Override
   public void preprocessFeatures(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features) {
      _condition.preprocessFeatures(features);
      _trueExpression.preprocessFeatures(features);
      _falseExpression.preprocessFeatures(features);
   }


   @Override
   public void preRender(final IVectorI2 renderExtent,
                         final IProjectionTool projectionTool,
                         final GAxisAlignedRectangle viewport,
                         final ISymbolizer2D symbolizer,
                         final IVectorial2DDrawer drawer) {
      _condition.preRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
      _trueExpression.preRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
      _falseExpression.preRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
   }


   @Override
   public void postRender(final IVectorI2 renderExtent,
                          final IProjectionTool projectionTool,
                          final GAxisAlignedRectangle viewport,
                          final ISymbolizer2D symbolizer,
                          final IVectorial2DDrawer drawer) {
      _condition.postRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
      _trueExpression.postRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
      _falseExpression.postRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
   }


   @Override
   public ResultT evaluate(final GeometryT geometry,
                           final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                           final IVectorial2DRenderingScaler scaler) {

      return _condition.evaluate(geometry, feature, scaler) ? _trueExpression.evaluate(geometry, feature, scaler)
                                                           : _falseExpression.evaluate(geometry, feature, scaler);
   }


}
