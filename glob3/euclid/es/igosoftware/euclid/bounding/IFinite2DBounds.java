

package es.igosoftware.euclid.bounding;

import es.igosoftware.euclid.vector.IVector2;


public interface IFinite2DBounds<

GeometryT extends IBounds<IVector2, GeometryT>

>
         extends
            IFiniteBounds<IVector2, GeometryT> {

   @Override
   public GAxisAlignedRectangle asAxisAlignedOrthotope();


   public double area();


}
