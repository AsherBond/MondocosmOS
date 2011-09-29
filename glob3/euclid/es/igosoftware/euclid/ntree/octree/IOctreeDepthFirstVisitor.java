

package es.igosoftware.euclid.ntree.octree;

import es.igosoftware.euclid.IBoundedGeometry3D;
import es.igosoftware.euclid.bounding.IFinite3DBounds;
import es.igosoftware.euclid.ntree.IGTDepthFirstVisitor;
import es.igosoftware.euclid.vector.IVector3;


public interface IOctreeDepthFirstVisitor<

ElementT,

GeometryT extends IBoundedGeometry3D<? extends IFinite3DBounds<?>>

>
         extends
            IGTDepthFirstVisitor<IVector3, ElementT, GeometryT> {

}
