

package es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GMath;


public class GCreateRectangle2DExpression
         extends
            GEmptyExpression<IVector2, GAxisAlignedRectangle> {

   private final IMeasure<GArea> _area;


   public GCreateRectangle2DExpression(final IMeasure<GArea> area) {
      _area = area;
   }


   private static IVector2 calculateRectangleExtent(final IMeasure<GArea> pointArea,
                                                    final IVector2 point,
                                                    final IVectorial2DRenderingScaler scaler) {
      final double areaInSquaredMeters = pointArea.getValueInReferenceUnits();

      final double radius = GMath.sqrt(areaInSquaredMeters);
      final IVector2 pointPlusRadius = scaler.increment(point, radius, radius);
      return pointPlusRadius.sub(point).scale(2); // radius times 2 (for extent)
   }


   @Override
   public double getMaximumSizeInMeters(final IVectorial2DRenderingScaler scaler) {
      return GMath.sqrt(_area.getValueInReferenceUnits());
   }


   @Override
   public GAxisAlignedRectangle evaluate(final IVector2 point,
                                         final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                         final IVectorial2DRenderingScaler scaler) {
      final IVector2 extent = calculateRectangleExtent(_area, point, scaler);
      final IVector2 position = point.sub(extent.div(2));

      return new GAxisAlignedRectangle(position, position.add(extent));
   }


}
