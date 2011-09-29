

package es.igosoftware.euclid.ntree;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.vector.IVector;


public interface IGTBreadFirstVisitor<

VectorT extends IVector<VectorT, ?>,

ElementT,

GeometryT extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>

> {

   public static class AbortVisiting
            extends
               Exception {
      private static final long serialVersionUID = 1L;
   }


   public void visitOctree(final GGeometryNTree<VectorT, ElementT, GeometryT> octree) throws IGTBreadFirstVisitor.AbortVisiting;


   public void visitInnerNode(final GGTInnerNode<VectorT, ElementT, GeometryT> inner) throws IGTBreadFirstVisitor.AbortVisiting;


   public void visitLeafNode(final GGTLeafNode<VectorT, ElementT, GeometryT> leaf) throws IGTBreadFirstVisitor.AbortVisiting;

}
