

package es.igosoftware.euclid;

import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.vector.IVector2;


public interface IBoundedGeometry2D<BoundsT extends IBounds<IVector2, ?>>

         extends
            IBoundedGeometry<IVector2, BoundsT>,
            IGeometry2D {


}
