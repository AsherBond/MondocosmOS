

package es.igosoftware.euclid.shape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import es.igosoftware.euclid.GEdgedGeometryAbstract;
import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.vector.GVectorUtils;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GAssert;


public abstract class GLinesStrip<

VectorT extends IVector<VectorT, ?>,

SegmentT extends GSegment<VectorT, SegmentT, ?>,

BoundsT extends IBounds<VectorT, BoundsT>

>
         extends
            GEdgedGeometryAbstract<VectorT, SegmentT, BoundsT>
         implements
            IPolygonalChain<VectorT, SegmentT, BoundsT> {


   private static final long     serialVersionUID = 1L;

   protected final List<VectorT> _points;


   protected GLinesStrip(final boolean validate,
                         final VectorT... points) {
      GAssert.notEmpty(points, "points");
      GAssert.notNullElements(points, "points");

      _points = new ArrayList<VectorT>(points.length);
      for (final VectorT point : points) {
         _points.add(point);
      }
      if (validate) {
         validate();
      }
   }


   protected GLinesStrip(final boolean validate,
                         final List<VectorT> points) {
      GAssert.notEmpty(points, "points");
      GAssert.notNullElements(points, "points");

      _points = new ArrayList<VectorT>(points);
      if (validate) {
         validate();
      }
   }


   protected void validate() {
      if (_points.size() < 2) {
         throw new IllegalArgumentException("A LineStrip must have at least 2 points");
      }


      for (int i = 0; i < _points.size(); i++) {
         final VectorT current = _points.get(i);

         final int nextI = (i + 1) % _points.size();
         final VectorT next = _points.get(nextI);

         if (current.closeTo(next)) {
            //         if (current.equals(next)) {
            throw new IllegalArgumentException("Two consecutive points (#" + i + "/#" + nextI + ") can't be the same");
         }
      }


      if (isSelfIntersected()) {
         //throw new IllegalArgumentException("Can't create a self-intersected polygon " + this);
         throw new IllegalArgumentException("Can't create a self-intersected polygon");
      }

   }


   @Override
   public abstract boolean isSelfIntersected();


   @Override
   public final List<VectorT> getPoints() {
      return Collections.unmodifiableList(_points);
   }


   @Override
   public VectorT getPoint(final int index) {
      return _points.get(index);
   }


   @Override
   public double precision() {
      return _points.get(0).precision();
   }


   @Override
   public byte dimensions() {
      return _points.get(0).dimensions();
   }


   @Override
   public int getPointsCount() {
      return _points.size();
   }


   @Override
   public boolean contains(final VectorT point) {
      for (final SegmentT edge : getEdges()) {
         if (edge.contains(point)) {
            return true;
         }
      }
      return false;
   }


   @Override
   public double squaredDistance(final VectorT point) {
      double shortestDistance = Double.POSITIVE_INFINITY;

      for (final SegmentT edge : getEdges()) {
         final double currentDistance = edge.squaredDistance(point);
         if (currentDistance < shortestDistance) {
            shortestDistance = currentDistance;
         }
      }

      return shortestDistance;
   }


   @Override
   public Iterator<VectorT> iterator() {
      return getPoints().iterator();
   }


   public boolean isConvex() {
      return false;
   }


   @Override
   public VectorT getCentroid() {
      return GVectorUtils.getAverage(_points);
   }


   @Override
   public GLinesStrip<VectorT, SegmentT, BoundsT> clone() {
      return this;
   }


   @Override
   public VectorT closestPoint(final VectorT point) {
      return closestPointOnBoundary(point);
   }


   @Override
   public boolean isClosed() {
      final VectorT first = _points.get(0);
      final VectorT last = _points.get(_points.size() - 1);
      return first.closeTo(last);
   }


   @Override
   public String toString() {
      return "GLinesStrip [points=" + _points.size() + "]";
   }


   public double getLength() {
      double result = 0;
      for (int i = 1; i < _points.size(); i++) {
         result += _points.get(i - 1).distance(_points.get(i));
      }
      return result;
   }


   @Override
   public boolean closeTo(final IBoundedGeometry<VectorT, BoundsT> that) {
      if (that instanceof IPolygonalChain) {
         @SuppressWarnings("unchecked")
         final IPolygonalChain<VectorT, ?, BoundsT> thatLineStrip = (IPolygonalChain<VectorT, ?, BoundsT>) that;

         if (_points.size() != thatLineStrip.getPointsCount()) {
            return false;
         }

         for (int i = 0; i < _points.size(); i++) {
            if (!_points.get(i).closeTo(thatLineStrip.getPoint(i))) {
               return false;
            }
         }

         return true;
      }
      return false;
   }


   public double perimeter() {
      double perimeter = 0;

      for (int i = 1; i < _points.size(); i++) {
         perimeter += _points.get(i - 1).distance(_points.get(i));
      }

      return perimeter;
   }


}
