

package es.igosoftware.euclid.experimental.vectorial.rendering.context;

import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.experimental.vectorial.rendering.utils.GAWTPoints;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IPointsContainer;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorI2;
import es.igosoftware.util.GAssert;


public class GVectorial2DRenderingScaler
         implements
            IVectorial2DRenderingScaler {


   private final IVector2              _scale;
   private final GAxisAlignedRectangle _viewport;
   private final GProjection           _projection;
   private final IProjectionTool       _projectionTool;
   private final IVectorI2             _renderExtent;


   public GVectorial2DRenderingScaler(final GAxisAlignedRectangle viewport,
                                      final GProjection projection,
                                      final IProjectionTool projectionTool,
                                      final IVectorI2 renderExtent) {
      GAssert.notNull(viewport, "viewport");
      GAssert.notNull(projection, "projection");
      GAssert.notNull(projectionTool, "projectionTool");
      GAssert.notNull(renderExtent, "renderExtent");
      GAssert.isPositive(renderExtent.x(), "renderExtent.x()");
      GAssert.isPositive(renderExtent.y(), "renderExtent.y()");

      _viewport = viewport;
      _projection = projection;
      _projectionTool = projectionTool;

      _renderExtent = renderExtent;
      _scale = new GVector2D(renderExtent.x(), renderExtent.y()).div(viewport.getExtent());
   }


   @Override
   public final IVector2 scaleExtent(final IVector2 extent) {
      return extent.scale(_scale);
   }


   @Override
   public final IVector2 scaleAndTranslate(final IVector2 point) {
      //      return point.sub(_viewport._lower).scale(_scale);

      final IVector2 scaled = point.sub(_viewport._lower).scale(_scale);
      return new GVector2D(scaled.x(), _renderExtent.y() - scaled.y());
   }


   @Override
   public final GAWTPoints toScaledAndTranslatedPoints(final IPointsContainer<IVector2> polygon) {
      final int nPoints = polygon.getPointsCount();
      final int[] xPoints = new int[nPoints];
      final int[] yPoints = new int[nPoints];

      for (int i = 0; i < nPoints; i++) {
         final IVector2 point = scaleAndTranslate(polygon.getPoint(i));

         xPoints[i] = Math.round((float) point.x());
         yPoints[i] = Math.round((float) point.y());
      }

      return new GAWTPoints(xPoints, yPoints);
   }


   @Override
   public IVector2 increment(final IVector2 position,
                             final double deltaEasting,
                             final double deltaNorthing) {
      return _projectionTool.increment(position, _projection, deltaEasting, deltaNorthing);
   }


   @Override
   public GAxisAlignedOrthotope<IVector2, ?> scaleAndTranslate(final GAxisAlignedOrthotope<IVector2, ?> bounds) {
      final IVector2 scaledLower = scaleAndTranslate(bounds._lower);
      final IVector2 scaledUpper = scaleAndTranslate(bounds._upper);
      return GAxisAlignedOrthotope.create(scaledLower, scaledUpper);
   }


   @Override
   public IVector2 apply(final IVector2 point) {
      return scaleAndTranslate(point);
   }


}
