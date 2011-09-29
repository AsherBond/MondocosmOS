

package es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.IGeometry2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ICurve2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ISurface2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GOval2DSymbol;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GSymbol2DList;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.shape.GAxisAlignedOval2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GAssert;


public class GOval2DSymbolizerExpression<FeatureGeometryT extends IGeometry2D>
         extends
            GSurface2DSymbolizerExpression<FeatureGeometryT, GAxisAlignedOval2D> {


   private final IExpression<FeatureGeometryT, GAxisAlignedOval2D> _toOvalExpression;


   public GOval2DSymbolizerExpression(final IExpression<FeatureGeometryT, GAxisAlignedOval2D> toOvalExpression,
                                      final IExpression<GAxisAlignedOval2D, ICurve2DStyle> curveStyleExpression,
                                      final IExpression<GAxisAlignedOval2D, ISurface2DStyle> surfaceStyleExpression) {
      super(curveStyleExpression, surfaceStyleExpression);

      GAssert.notNull(toOvalExpression, "toOvalExpression");
      _toOvalExpression = toOvalExpression;
   }


   @Override
   public final double getMaximumSizeInMeters(final IVectorial2DRenderingScaler scaler) {
      return super.getMaximumSizeInMeters(scaler) + _toOvalExpression.getMaximumSizeInMeters(scaler);
   }


   @Override
   public GSymbol2DList evaluate(final FeatureGeometryT geometry,
                                 final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                 final IVectorial2DRenderingScaler scaler) {

      final GAxisAlignedOval2D oval = _toOvalExpression.evaluate(geometry, feature, scaler);
      final GAxisAlignedOval2D scaledOval = oval.transform(scaler);

      final ICurve2DStyle curveStyle = _curveStyleExpression.evaluate(oval, feature, scaler);
      final ISurface2DStyle surfaceStyle = _surfaceStyleExpression.evaluate(oval, feature, scaler);

      return new GSymbol2DList(new GOval2DSymbol(scaledOval, null, surfaceStyle, curveStyle, 10000, true));
   }


}
