

package es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions;

import java.util.ArrayList;
import java.util.List;

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
import es.igosoftware.util.GPair;


public class GSwitchExpression<GeometryT extends IGeometry2D, KeyT, ResultT>
         extends
            GExpressionAbstract<GeometryT, ResultT> {


   private final IExpression<GeometryT, KeyT>                                 _keyExpression;
   private final List<GPair<KeyT, IExpression<GeometryT, ? extends ResultT>>> _casesExpressions;
   private final IExpression<GeometryT, ? extends ResultT>                    _defaultExpression;


   public GSwitchExpression(final IExpression<GeometryT, KeyT> keyExpression,
                            final List<GPair<KeyT, IExpression<GeometryT, ? extends ResultT>>> casesExpressions,
                            final IExpression<GeometryT, ? extends ResultT> defaultExpression) {
      GAssert.notNull(keyExpression, "caseExpression");

      _keyExpression = keyExpression;
      _casesExpressions = new ArrayList<GPair<KeyT, IExpression<GeometryT, ? extends ResultT>>>(casesExpressions); // copy to avoid external modifications
      _defaultExpression = orNullExpression(defaultExpression);
   }


   @SuppressWarnings("unchecked")
   private IExpression<GeometryT, ResultT> orNullExpression(final IExpression<GeometryT, ? extends ResultT> expression) {
      return (expression == null) ? GNullExpression.INSTANCE : expression;
   }


   @Override
   public double getMaximumSizeInMeters(final IVectorial2DRenderingScaler scaler) {
      double max = Math.max(_keyExpression.getMaximumSizeInMeters(scaler), _defaultExpression.getMaximumSizeInMeters(scaler));

      for (final GPair<KeyT, IExpression<GeometryT, ? extends ResultT>> casePair : _casesExpressions) {
         final IExpression<GeometryT, ? extends ResultT> caseExpression = casePair._second;
         max = Math.max(max, caseExpression.getMaximumSizeInMeters(scaler));
      }

      return max;
   }


   @Override
   public void preprocessFeatures(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features) {
      _keyExpression.preprocessFeatures(features);
      _defaultExpression.preprocessFeatures(features);
      for (final GPair<KeyT, IExpression<GeometryT, ? extends ResultT>> casePair : _casesExpressions) {
         final IExpression<GeometryT, ? extends ResultT> caseExpression = casePair._second;
         caseExpression.preprocessFeatures(features);
      }
   }


   @Override
   public void preRender(final IVectorI2 renderExtent,
                         final IProjectionTool projectionTool,
                         final GAxisAlignedRectangle viewport,
                         final ISymbolizer2D symbolizer,
                         final IVectorial2DDrawer drawer) {
      _keyExpression.preRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
      _defaultExpression.preRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
      for (final GPair<KeyT, IExpression<GeometryT, ? extends ResultT>> casePair : _casesExpressions) {
         final IExpression<GeometryT, ? extends ResultT> caseExpression = casePair._second;
         caseExpression.preRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
      }
   }


   @Override
   public void postRender(final IVectorI2 renderExtent,
                          final IProjectionTool projectionTool,
                          final GAxisAlignedRectangle viewport,
                          final ISymbolizer2D symbolizer,
                          final IVectorial2DDrawer drawer) {
      _keyExpression.postRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
      _defaultExpression.postRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
      for (final GPair<KeyT, IExpression<GeometryT, ? extends ResultT>> casePair : _casesExpressions) {
         final IExpression<GeometryT, ? extends ResultT> caseExpression = casePair._second;
         caseExpression.postRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
      }
   }


   @Override
   public ResultT evaluate(final GeometryT geometry,
                           final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                           final IVectorial2DRenderingScaler scaler) {

      final KeyT key = _keyExpression.evaluate(geometry, feature, scaler);

      for (final GPair<KeyT, IExpression<GeometryT, ? extends ResultT>> casePair : _casesExpressions) {
         final KeyT caseKey = casePair._first;
         if (caseKey.equals(key)) {
            final IExpression<GeometryT, ? extends ResultT> caseExpression = casePair._second;
            return caseExpression.evaluate(geometry, feature, scaler);
         }
      }

      return _defaultExpression.evaluate(geometry, feature, scaler);
   }


}
