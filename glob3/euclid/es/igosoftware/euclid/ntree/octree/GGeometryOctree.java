

package es.igosoftware.euclid.ntree.octree;

import java.util.Collection;

import es.igosoftware.euclid.IBoundedGeometry3D;
import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.bounding.IFinite3DBounds;
import es.igosoftware.euclid.ntree.GGeometryNTree;
import es.igosoftware.euclid.ntree.GGeometryNTreeParameters;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.IFunction;


public class GGeometryOctree<

ElementT,

GeometryT extends IBoundedGeometry3D<? extends IFinite3DBounds<?>>

>
         extends
            GGeometryNTree<IVector3, ElementT, GeometryT> {


   public GGeometryOctree(final String name,
                          final GAxisAlignedBox bounds,
                          final Iterable<? extends ElementT> elements,
                          final IFunction<ElementT, Collection<? extends GeometryT>> transformer,
                          final GGeometryNTreeParameters parameters) {
      super(name, bounds, elements, transformer, parameters);
   }


   @Override
   protected String getTreeName() {
      return "Octree";
   }


}
