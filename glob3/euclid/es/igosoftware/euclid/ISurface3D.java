

package es.igosoftware.euclid;

import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.vector.IVector3;


public interface ISurface3D<BoundsT extends IBounds<IVector3, BoundsT>>
         extends
            ISurface<IVector3, BoundsT>,
            IBoundedGeometry3D<BoundsT> {


   public double perimeter();

}
