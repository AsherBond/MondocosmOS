

package es.igosoftware.euclid.ntree.quadtree;

import java.util.Collection;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.ntree.GGeometryNTree;
import es.igosoftware.euclid.ntree.GGeometryNTreeParameters;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.IFunction;


public class GGeometryQuadtree<

ElementT,

GeometryT extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>

>
         extends
            GGeometryNTree<IVector2, ElementT, GeometryT> {


   public GGeometryQuadtree(final String name,
                            final GAxisAlignedRectangle bounds,
                            final Iterable<? extends ElementT> elements,
                            final IFunction<ElementT, Collection<? extends GeometryT>> transformer,
                            final GGeometryNTreeParameters parameters) {
      super(name, bounds, elements, transformer, parameters);
   }


   @Override
   protected String getTreeName() {
      return "Quadtree";
   }

}
