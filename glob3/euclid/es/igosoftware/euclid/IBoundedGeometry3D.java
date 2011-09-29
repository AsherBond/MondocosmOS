

package es.igosoftware.euclid;

import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.vector.IVector3;


public interface IBoundedGeometry3D<BoundsT extends IBounds<IVector3, ?>>

         extends
            IBoundedGeometry<IVector3, BoundsT>,
            IGeometry3D {


}
