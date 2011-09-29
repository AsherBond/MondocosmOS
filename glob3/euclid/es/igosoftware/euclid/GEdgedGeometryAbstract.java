

package es.igosoftware.euclid;

import java.util.Collections;
import java.util.List;

import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.shape.GSegment;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GMath;


public abstract class GEdgedGeometryAbstract<

VectorT extends IVector<VectorT, ?>,

SegmentT extends GSegment<VectorT, SegmentT, ?>,

BoundsT extends IBounds<VectorT, ?>

>
         extends
            GGeometryAbstract<VectorT>
         implements
            IEdgedGeometry<VectorT, SegmentT, BoundsT> {


   private static final long serialVersionUID = 1L;


   private List<SegmentT>    _edges;


   @Override
   public List<SegmentT> getEdges() {
      if (_edges == null) {
         final List<SegmentT> initialEdges = initializeEdges();
         GAssert.notEmpty(initialEdges, "edges");
         _edges = Collections.unmodifiableList(initialEdges);
      }
      return _edges;
   }


   protected abstract List<SegmentT> initializeEdges();


   @Override
   public VectorT closestPoint(final VectorT point) {
      GAssert.notNull(point, "point");

      if (contains(point)) {
         return point;
      }

      return closestPointOnBoundary(point);
   }


   @Override
   public VectorT closestPointOnBoundary(final VectorT point) {
      GAssert.notNull(point, "point");

      double minDistance = Double.POSITIVE_INFINITY;
      VectorT closestPoint = null;

      for (final SegmentT edge : getEdges()) {
         final VectorT currentPoint = edge.closestPointOnBoundary(point);
         final double currentDistance = currentPoint.squaredDistance(point);

         if (currentDistance <= minDistance) {
            minDistance = currentDistance;
            closestPoint = currentPoint;
         }
      }

      return closestPoint;
   }


   @Override
   public double distanceToBoundary(final VectorT point) {
      return GMath.sqrt(squaredDistance(point));
   }


   @Override
   public double squaredDistanceToBoundary(final VectorT point) {
      return closestPointOnBoundary(point).squaredDistance(point);
   }


   @Override
   public boolean containsOnBoundary(final VectorT point) {
      return GMath.closeToZero(distanceToBoundary(point));
   }


}
