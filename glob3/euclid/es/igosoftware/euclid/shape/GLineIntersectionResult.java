

package es.igosoftware.euclid.shape;

import es.igosoftware.euclid.GGeometryAbstract;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GAssert;


public class GLineIntersectionResult<VectorT extends IVector<VectorT, ?>> {

   public static enum Type {
      PARALLEL,
      COINCIDENT,
      NOT_INTERSECTING,
      INTERSECTING;
   }


   private final GLineIntersectionResult.Type _type;
   //private final VectorT                      _point;
   private final GGeometryAbstract<VectorT>   _intersection;


   //   public GLineIntersectionResult(final GLineIntersectionResult.Type type,
   //                                  final VectorT point) {
   //      GAssert.notNull(type, "type");
   //
   //      if (type == GLineIntersectionResult.Type.INTERSECTING) {
   //         GAssert.notNull(point, "point");
   //      }
   //
   //      _type = type;
   //      _point = point;
   //   }

   public GLineIntersectionResult(final GLineIntersectionResult.Type type,
                                  final GGeometryAbstract<VectorT> intersection) {

      GAssert.notNull(type, "type");

      if ((type == GLineIntersectionResult.Type.INTERSECTING) || (type == GLineIntersectionResult.Type.COINCIDENT)) {
         GAssert.notNull(intersection, "intersection");
      }

      _type = type;
      _intersection = intersection;
   }


   public GLineIntersectionResult.Type getType() {
      return _type;
   }


   @SuppressWarnings("unchecked")
   public VectorT getPoint() {

      if (_type == Type.INTERSECTING) {
         return (VectorT) _intersection;
      }

      return null;
   }


   @SuppressWarnings("unchecked")
   public GSegment<VectorT, ?, ?> getSegment() {

      if (_type == Type.COINCIDENT) {
         return (GSegment<VectorT, ?, ?>) _intersection;
      }

      return null;
   }


   @Override
   public String toString() {
      return "GLineIntersectionResult [type=" + _type + ", intersection=" + _intersection + "]";
   }


}
