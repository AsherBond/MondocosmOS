

package es.igosoftware.euclid.ntree;

import java.util.Collection;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.vector.IVector;


public class GGTLeafNode<

VectorT extends IVector<VectorT, ?>,

ElementT,

GeometryT extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>

>
         extends
            GGTNode<VectorT, ElementT, GeometryT> {


   GGTLeafNode(final GGTInnerNode<VectorT, ElementT, GeometryT> parent,
               final GAxisAlignedOrthotope<VectorT, ?> bounds,
               final Collection<GElementGeometryPair<VectorT, ElementT, GeometryT>> elements) {
      super(parent, bounds, elements);
   }


   @Override
   public void depthFirstAcceptVisitor(final IGTDepthFirstVisitor<VectorT, ElementT, GeometryT> visitor)
                                                                                                        throws IGTBreadFirstVisitor.AbortVisiting {
      visitor.visitLeafNode(this);
   }


   @Override
   public int getLeafNodesCount() {
      return 1;
   }


   @Override
   public int getInnerNodesCount() {
      return 0;
   }


   @Override
   public final Collection<GElementGeometryPair<VectorT, ElementT, GeometryT>> getAllElements() {
      return getElements();
   }


   @Override
   public final int getAllElementsCount() {
      return getElementsCount();
   }


   @Override
   protected void validate() {
      if (getParent().getChildIndex(this) == -1) {
         System.out.println("invalid parent");
      }
   }


   @Override
   public String toString() {
      return "GGTLeafNode [id=" + getId() + ", depth=" + getDepth() + ", bounds=" + getBounds() + ", elements="
             + getElementsCount() + "]";
   }


}
