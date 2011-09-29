

package es.igosoftware.euclid;

import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GMath;


public class GLine2D
         extends
            GLine<IVector2>
         implements
            IGeometry2D {


   private static final long serialVersionUID = 1L;


   public GLine2D(final IVector2 a,
                  final IVector2 b) {
      super(a, b);
   }


   @Override
   public String toString() {
      return "GLine2D [a=" + _a + ", b=" + _b + "]";
   }


   public IVector2 getIntersectionPoint(final GLine2D that) {
      // from: http://paulbourke.net/geometry/lineline2d/

      final double bxMinusAx = _b.x() - _a.x();
      final double byMinusAy = _b.y() - _a.y();

      final double denom = (that._b.y() - that._a.y()) * bxMinusAx - (that._b.x() - that._a.x()) * byMinusAy;
      final double numera = (that._b.x() - that._a.x()) * (_a.y() - that._a.y()) - (that._b.y() - that._a.y())
                            * (_a.x() - that._a.x());

      /* Are the line parallel */
      if (GMath.closeToZero(denom)) {
         return null;
      }

      /* Is the intersection along the the segments */
      final double mua = numera / denom;

      final double x = _a.x() + mua * bxMinusAx;
      final double y = _a.y() + mua * byMinusAy;
      return new GVector2D(x, y);
   }
}
