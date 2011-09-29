

package es.igosoftware.euclid.ntree.quadtree;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.ntree.IGTBreadFirstVisitor;
import es.igosoftware.euclid.vector.IVector2;


public interface IQuadtreeBreadFirstVisitor<

ElementT,

GeometryT extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>

>
         extends
            IGTBreadFirstVisitor<IVector2, ElementT, GeometryT> {

}
