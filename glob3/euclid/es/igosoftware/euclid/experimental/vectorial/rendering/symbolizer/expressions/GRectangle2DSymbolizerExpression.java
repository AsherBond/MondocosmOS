

package es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.IGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ICurve2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ISurface2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GRectangle2DSymbol;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GSymbol2DList;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GAssert;


public class GRectangle2DSymbolizerExpression<FeatureGeometryT extends IGeometry2D>
         extends
            GSurface2DSymbolizerExpression<FeatureGeometryT, GAxisAlignedRectangle> {


   private final IExpression<FeatureGeometryT, GAxisAlignedRectangle> _toRectangleExpression;


   public GRectangle2DSymbolizerExpression(final IExpression<FeatureGeometryT, GAxisAlignedRectangle> toRectangleExpression,
                                           final IExpression<GAxisAlignedRectangle, ICurve2DStyle> curveStyleExpression,
                                           final IExpression<GAxisAlignedRectangle, ISurface2DStyle> surfaceStyleExpression) {
      super(curveStyleExpression, surfaceStyleExpression);

      GAssert.notNull(toRectangleExpression, "toRectangleExpression");
      _toRectangleExpression = toRectangleExpression;
   }


   @Override
   public final double getMaximumSizeInMeters(final IVectorial2DRenderingScaler scaler) {
      return super.getMaximumSizeInMeters(scaler) + _toRectangleExpression.getMaximumSizeInMeters(scaler);
   }


   @Override
   public GSymbol2DList evaluate(final FeatureGeometryT geometry,
                                 final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                 final IVectorial2DRenderingScaler scaler) {

      final GAxisAlignedRectangle rectangle = _toRectangleExpression.evaluate(geometry, feature, scaler);
      final GAxisAlignedRectangle scaledRectangle = rectangle.transform(scaler);

      final ICurve2DStyle curveStyle = _curveStyleExpression.evaluate(rectangle, feature, scaler);
      final ISurface2DStyle surfaceStyle = _surfaceStyleExpression.evaluate(rectangle, feature, scaler);

      return new GSymbol2DList(new GRectangle2DSymbol(scaledRectangle, null, surfaceStyle, curveStyle, 10000, true));
   }


}
