

package es.igosoftware.euclid.multigeometry;

import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.vector.IVector2;


public class GMultiGeometry2D<

ChildrenGeometryT extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>

>
         extends
            GMultiGeometry<IVector2, ChildrenGeometryT, GAxisAlignedRectangle>
         implements
            IBoundedGeometry2D<GAxisAlignedRectangle> {


   private static final long serialVersionUID = 1L;


   public GMultiGeometry2D(final ChildrenGeometryT... children) {
      super(children);
   }


   public GMultiGeometry2D(final List<ChildrenGeometryT> children) {
      super(children);
   }


   @Override
   public boolean closeTo(final IBoundedGeometry<IVector2, GAxisAlignedRectangle> that) {
      //
      //      if (that instanceof GMultiGeometry2D) {
      //         @SuppressWarnings("unchecked")
      //         final GMultiGeometry2D<ChildrenGeometryT> thatMG = (GMultiGeometry2D<ChildrenGeometryT>) that;
      //
      //         if (_children.size() != thatMG._children.size()) {
      //            return false;
      //         }
      //
      //         for (int i = 0; i < _children.size(); i++) {
      //            if (!_children.get(i).closeTo(thatMG._children.get(i))) {
      //               return false;
      //            }
      //         }
      //
      //         return false;
      //      }
      //
      //      return false;
      throw new RuntimeException("Not yet implemented");
   }


}
