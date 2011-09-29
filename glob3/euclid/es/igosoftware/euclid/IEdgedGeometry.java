

package es.igosoftware.euclid;

import java.util.List;

import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.shape.GSegment;
import es.igosoftware.euclid.vector.IPointsContainer;
import es.igosoftware.euclid.vector.IVector;


public interface IEdgedGeometry<

VectorT extends IVector<VectorT, ?>,

SegmentT extends GSegment<VectorT, SegmentT, ?>,

BoundsT extends IBounds<VectorT, ?>

>
         extends
            IBoundedGeometry<VectorT, BoundsT>,
            IPointsContainer<VectorT> {


   public boolean isSelfIntersected();


   public List<SegmentT> getEdges();


}
