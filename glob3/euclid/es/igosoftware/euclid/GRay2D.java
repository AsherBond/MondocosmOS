

package es.igosoftware.euclid;

import es.igosoftware.euclid.vector.IVector2;


public class GRay2D
         extends
            GRay<IVector2>
         implements
            IGeometry2D {


   private static final long serialVersionUID = 1L;


   public GRay2D(final IVector2 a,
                 final IVector2 b) {
      super(a, b);
   }


   @Override
   public String toString() {
      return "GRay2D [a=" + _a + ", b=" + _b + "]";
   }

}
