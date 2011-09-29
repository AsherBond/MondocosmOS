

package es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.shape.GAxisAlignedOval2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GMath;


public class GCreateOval2DExpression
         extends
            GEmptyExpression<IVector2, GAxisAlignedOval2D> {

   private final IMeasure<GArea> _area;


   public GCreateOval2DExpression(final IMeasure<GArea> area) {
      _area = area;
   }


   @Override
   public double getMaximumSizeInMeters(final IVectorial2DRenderingScaler scaler) {
      return GMath.sqrt(_area.getValueInReferenceUnits() / Math.PI);
   }


   private IVector2 calculateOvalExtent(final IVector2 point,
                                        final IVectorial2DRenderingScaler scaler) {
      //      final double areaInSquaredMeters = pointArea.getValueInReferenceUnits();
      //
      //      final double radius = GMath.sqrt(areaInSquaredMeters / Math.PI);
      //      final IVector2 pointPlusRadius = scaler.increment(point, radius, radius);
      //      return pointPlusRadius.sub(point).scale(2); // radius times 2 (for extent)

      final double areaInSquaredMeters = _area.getValueInReferenceUnits();

      final double radius = GMath.sqrt(areaInSquaredMeters / Math.PI);
      final IVector2 pointPlusRadius = scaler.increment(point, radius, radius);

      return pointPlusRadius.sub(point).scale(2); // radius times 2 (for extent)
   }


   @Override
   public GAxisAlignedOval2D evaluate(final IVector2 point,
                                      final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                      final IVectorial2DRenderingScaler scaler) {
      final IVector2 extent = calculateOvalExtent(point, scaler);
      final IVector2 position = point.sub(extent.div(2));

      return new GAxisAlignedOval2D(position, extent);
   }


}
