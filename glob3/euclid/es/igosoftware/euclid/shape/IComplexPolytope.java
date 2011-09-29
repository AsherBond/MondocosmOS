

package es.igosoftware.euclid.shape;

import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.vector.IVector;


public interface IComplexPolytope<

VectorT extends IVector<VectorT, ?>,

SegmentT extends GSegment<VectorT, SegmentT, ?>,

BoundsT extends IBounds<VectorT, BoundsT>

>
         extends
            IPolytope<VectorT, SegmentT, BoundsT> {


   public ISimplePolytope<VectorT, SegmentT, BoundsT> getHull();


}
