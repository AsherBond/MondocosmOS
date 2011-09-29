

package es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions;

import java.awt.image.BufferedImage;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IProjectionTool;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.ISymbolizer2D;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GIcon2DSymbol;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GIconUtils;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GSymbol2DList;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorI2;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GMath;
import es.igosoftware.util.GPair;


public class GIcon2DSymbolizerExpression
         extends
            GExpressionAbstract<IVector2, GSymbol2DList>
         implements
            IGeometry2DSymbolizerExpression<IVector2> {


   private final IMeasure<GArea>                                     _area;
   private final IExpression<IVector2, GPair<String, BufferedImage>> _iconExpression;
   private final IExpression<IVector2, Float>                        _opacityExpression;


   public GIcon2DSymbolizerExpression(final IMeasure<GArea> area,
                                      final IExpression<IVector2, GPair<String, BufferedImage>> iconExpression,
                                      final IExpression<IVector2, Float> opacityExpression) {
      GAssert.notNull(iconExpression, "iconExpression");
      GAssert.notNull(opacityExpression, "opacityExpression");

      _area = area;
      _iconExpression = iconExpression;
      _opacityExpression = opacityExpression;
   }


   @Override
   public double getMaximumSizeInMeters(final IVectorial2DRenderingScaler scaler) {
      return Math.max(_iconExpression.getMaximumSizeInMeters(scaler), _opacityExpression.getMaximumSizeInMeters(scaler));
   }


   @Override
   public void preprocessFeatures(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features) {
      _iconExpression.preprocessFeatures(features);
      _opacityExpression.preprocessFeatures(features);
   }


   @Override
   public void preRender(final IVectorI2 renderExtent,
                         final IProjectionTool projectionTool,
                         final GAxisAlignedRectangle viewport,
                         final ISymbolizer2D symbolizer,
                         final IVectorial2DDrawer drawer) {
      _iconExpression.preRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
      _opacityExpression.preRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
   }


   @Override
   public void postRender(final IVectorI2 renderExtent,
                          final IProjectionTool projectionTool,
                          final GAxisAlignedRectangle viewport,
                          final ISymbolizer2D symbolizer,
                          final IVectorial2DDrawer drawer) {
      _iconExpression.postRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
      _opacityExpression.postRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
   }


   private static IVector2 calculateRectangleExtent(final IMeasure<GArea> area,
                                                    final IVector2 point,
                                                    final IVectorial2DRenderingScaler scaler) {
      //         final double areaInSquaredMeters = area.getValueInReferenceUnits();
      //   
      //         final double radius = GMath.sqrt(areaInSquaredMeters);
      //         final IVector2 pointPlusRadius = scaler.increment(point, radius, radius);
      //         return pointPlusRadius.sub(point).scale(2); // radius times 2 (for extent)

      final double areaInSquaredMeters = area.getValueInReferenceUnits();

      final double radius = GMath.sqrt(areaInSquaredMeters);
      final IVector2 pointPlusRadius = scaler.increment(point, radius, radius);
      return scaler.scaleExtent(pointPlusRadius.sub(point)).scale(2); // radius times 2 (for extent)
   }


   @Override
   public GSymbol2DList evaluate(final IVector2 point,
                                 final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                 final IVectorial2DRenderingScaler scaler) {
      final GPair<String, BufferedImage> nameAndIcon = _iconExpression.evaluate(point, feature, scaler);
      final String iconName = nameAndIcon._first;
      final BufferedImage icon = nameAndIcon._second;

      final float percentFilled = GIconUtils.getPercentFilled(icon);
      final IVector2 extent = calculateRectangleExtent(_area, point, scaler).div(percentFilled);

      final IVector2 scaledPoint = scaler.scaleAndTranslate(point);
      final IVector2 position = scaledPoint.sub(extent.div(2));

      final BufferedImage scaledIcon = GIconUtils.getScaledImage(icon, extent);

      final float opacity = _opacityExpression.evaluate(point, feature, scaler);

      return new GSymbol2DList(new GIcon2DSymbol(position, null, iconName, scaledIcon, opacity, 1000, true));
   }


}
