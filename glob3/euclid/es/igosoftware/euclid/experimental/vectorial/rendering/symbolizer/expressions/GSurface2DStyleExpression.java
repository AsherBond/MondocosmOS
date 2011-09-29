

package es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IProjectionTool;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.GSurface2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ISurface2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.ISymbolizer2D;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorI2;
import es.igosoftware.util.GAssert;


public class GSurface2DStyleExpression<GeometryT extends ISurface2D<? extends IFinite2DBounds<?>>>
         extends
            GExpressionAbstract<GeometryT, ISurface2DStyle> {


   private final IExpression<GeometryT, IColor> _colorExpression;
   private final IExpression<GeometryT, Float>  _opacityExpression;


   public GSurface2DStyleExpression(final IExpression<GeometryT, IColor> colorExpression,
                                    final IExpression<GeometryT, Float> opacityExpression) {
      GAssert.notNull(colorExpression, "colorExpression");
      GAssert.notNull(opacityExpression, "opacityExpression");

      _colorExpression = colorExpression;
      _opacityExpression = opacityExpression;
   }


   @Override
   public double getMaximumSizeInMeters(final IVectorial2DRenderingScaler scaler) {
      return 0;
   }


   @Override
   public void preprocessFeatures(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features) {
      _colorExpression.preprocessFeatures(features);
      _opacityExpression.preprocessFeatures(features);
   }


   @Override
   public void preRender(final IVectorI2 renderExtent,
                         final IProjectionTool projectionTool,
                         final GAxisAlignedRectangle viewport,
                         final ISymbolizer2D symbolizer,
                         final IVectorial2DDrawer drawer) {
      _colorExpression.preRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
      _opacityExpression.preRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
   }


   @Override
   public void postRender(final IVectorI2 renderExtent,
                          final IProjectionTool projectionTool,
                          final GAxisAlignedRectangle viewport,
                          final ISymbolizer2D symbolizer,
                          final IVectorial2DDrawer drawer) {
      _colorExpression.postRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
      _opacityExpression.postRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
   }


   @Override
   public ISurface2DStyle evaluate(final GeometryT surface,
                                   final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                   final IVectorial2DRenderingScaler scaler) {
      final IColor color = _colorExpression.evaluate(surface, feature, scaler);
      final Float opacity = _opacityExpression.evaluate(surface, feature, scaler);

      return new GSurface2DStyle(color, opacity);
   }


}
