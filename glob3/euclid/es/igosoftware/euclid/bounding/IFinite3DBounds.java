

package es.igosoftware.euclid.bounding;

import es.igosoftware.euclid.vector.IVector3;


public interface IFinite3DBounds<

GeometryT extends IBounds<IVector3, GeometryT>

>
         extends
            IFiniteBounds<IVector3, GeometryT> {

   @Override
   public GAxisAlignedBox asAxisAlignedOrthotope();


   public double volume();

}
