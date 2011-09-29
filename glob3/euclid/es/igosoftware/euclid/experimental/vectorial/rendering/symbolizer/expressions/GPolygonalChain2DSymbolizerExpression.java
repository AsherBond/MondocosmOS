

package es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ICurve2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GPolygonalChain2DSymbol;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GSymbol2DList;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.shape.IPolygonalChain2D;
import es.igosoftware.euclid.vector.IVector2;


public class GPolygonalChain2DSymbolizerExpression
         extends
            GCurve2DSymbolizerExpression<IPolygonalChain2D> {


   public GPolygonalChain2DSymbolizerExpression(final IExpression<IPolygonalChain2D, ICurve2DStyle> curveStyleExpression) {
      super(curveStyleExpression);
   }


   @Override
   public GSymbol2DList evaluate(final IPolygonalChain2D polygonalChain,
                                 final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                 final IVectorial2DRenderingScaler scaler) {
      final IPolygonalChain2D scaledPolygonalChain = polygonalChain.transform(scaler);

      final ICurve2DStyle curveStyle = _curveStyleExpression.evaluate(polygonalChain, feature, scaler);

      return new GSymbol2DList(new GPolygonalChain2DSymbol(scaledPolygonalChain, null, curveStyle, 20));
   }


}
