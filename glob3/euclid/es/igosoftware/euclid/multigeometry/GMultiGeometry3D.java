

package es.igosoftware.euclid.multigeometry;

import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.IBoundedGeometry3D;
import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.bounding.IFinite3DBounds;
import es.igosoftware.euclid.vector.IVector3;


public class GMultiGeometry3D<

ChildrenGeometryT extends IBoundedGeometry3D<? extends IFinite3DBounds<?>>

>
         extends
            GMultiGeometry<IVector3, ChildrenGeometryT, GAxisAlignedBox>
         implements
            IBoundedGeometry3D<GAxisAlignedBox> {


   private static final long serialVersionUID = 1L;


   public GMultiGeometry3D(final ChildrenGeometryT... children) {
      super(children);
   }


   public GMultiGeometry3D(final List<ChildrenGeometryT> children) {
      super(children);
   }


   @Override
   public boolean closeTo(final IBoundedGeometry<IVector3, GAxisAlignedBox> that) {
      throw new RuntimeException("Not yet implemented");
   }


}
