

package es.igosoftware.euclid.multigeometry;

import java.util.List;

import es.igosoftware.euclid.shape.IPolygon2D;


public class GMultiPolygon2D
         extends
            GMultiGeometry2D<IPolygon2D> {


   private static final long serialVersionUID = 1L;


   public GMultiPolygon2D(final IPolygon2D... children) {
      super(children);
   }


   public GMultiPolygon2D(final List<IPolygon2D> children) {
      super(children);
   }


}
