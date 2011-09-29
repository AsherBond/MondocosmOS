

package es.igosoftware.euclid.shape;

import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.vector.IVector;


public interface ISimplePolytope<

VectorT extends IVector<VectorT, ?>,

SegmentT extends GSegment<VectorT, SegmentT, ?>,

BoundsT extends IBounds<VectorT, BoundsT>

>
         extends
            IPolytope<VectorT, SegmentT, BoundsT> {

}
