

package es.igosoftware.euclid.bounding;

import es.igosoftware.euclid.vector.IVector;


public interface IFiniteBounds<

VectorT extends IVector<VectorT, ?>,

GeometryT extends IBounds<VectorT, GeometryT>

>
         extends
            IBounds<VectorT, GeometryT> {


   public GAxisAlignedOrthotope<VectorT, ?> asAxisAlignedOrthotope();


}
