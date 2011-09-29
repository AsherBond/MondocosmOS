

package es.igosoftware.euclid.shape;

import java.util.List;

import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.vector.IVector2;


public interface IComplexPolygon2D
         extends
            IComplexPolygon<IVector2, GSegment2D, GAxisAlignedRectangle>,
            IPolygon2D {


   @Override
   public ISimplePolygon2D getHull();


   @Override
   public abstract List<ISimplePolygon2D> getHoles();


}
