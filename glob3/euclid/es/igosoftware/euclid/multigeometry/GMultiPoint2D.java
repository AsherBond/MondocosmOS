

package es.igosoftware.euclid.multigeometry;

import java.util.List;

import es.igosoftware.euclid.vector.IPointsContainer;
import es.igosoftware.euclid.vector.IVector2;


public class GMultiPoint2D
         extends
            GMultiGeometry2D<IVector2>
         implements
            IPointsContainer<IVector2> {


   private static final long serialVersionUID = 1L;


   public GMultiPoint2D(final IVector2... children) {
      super(children);
   }


   public GMultiPoint2D(final List<IVector2> children) {
      super(children);
   }


   @Override
   public List<IVector2> getPoints() {
      return getChildren();
   }


   @Override
   public IVector2 getPoint(final int index) {
      return getChild(index);
   }


   @Override
   public int getPointsCount() {
      return getChildrenCount();
   }


}
