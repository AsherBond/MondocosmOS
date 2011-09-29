

package es.igosoftware.euclid.shape;

import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorFunction;


public interface ISimplePolygon2D
         extends
            ISimplePolygon<IVector2, GSegment2D, GAxisAlignedRectangle>,
            IPolygon2D {


   @Override
   public ISimplePolygon2D transform(final IVectorFunction<IVector2> transformer);

}
