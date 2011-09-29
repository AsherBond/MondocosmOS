

package es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ICurve2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ISurface2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GPolygon2DSymbol;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GSymbol2DList;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.vector.IVector2;


public class GPolygon2DSymbolizerExpression
         extends
            GSurface2DSymbolizerExpression<IPolygon2D, IPolygon2D> {


   public GPolygon2DSymbolizerExpression(final IExpression<IPolygon2D, ICurve2DStyle> curveStyleExpression,
                                         final IExpression<IPolygon2D, ISurface2DStyle> surfaceStyleExpression) {
      super(curveStyleExpression, surfaceStyleExpression);
   }


   @Override
   public GSymbol2DList evaluate(final IPolygon2D polygon,
                                 final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                 final IVectorial2DRenderingScaler scaler) {
      final IPolygon2D scaledPolygon = polygon.transform(scaler);
      final ICurve2DStyle curveStyle = _curveStyleExpression.evaluate(polygon, feature, scaler);
      final ISurface2DStyle surfaceStyle = _surfaceStyleExpression.evaluate(polygon, feature, scaler);

      return new GSymbol2DList(new GPolygon2DSymbol(scaledPolygon, null, surfaceStyle, curveStyle, 10));
   }


}
