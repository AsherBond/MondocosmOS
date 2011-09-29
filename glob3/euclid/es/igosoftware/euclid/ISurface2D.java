

package es.igosoftware.euclid;

import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorFunction;


public interface ISurface2D<BoundsT extends IBounds<IVector2, BoundsT>>
         extends
            ISurface<IVector2, BoundsT>,
            IBoundedGeometry2D<BoundsT> {

   @Override
   public ISurface2D<BoundsT> transform(final IVectorFunction<IVector2> transformer);


   public double area();


   public double perimeter();

}
