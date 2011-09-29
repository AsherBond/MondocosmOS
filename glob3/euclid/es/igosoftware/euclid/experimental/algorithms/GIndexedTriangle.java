

package es.igosoftware.euclid.experimental.algorithms;

import java.util.List;

import es.igosoftware.euclid.shape.GTriangle2D;
import es.igosoftware.euclid.shape.GTriangle3D;
import es.igosoftware.euclid.vector.IPointsContainer;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;


public class GIndexedTriangle {
   public final int _i0;
   public final int _i1;
   public final int _i2;


   GIndexedTriangle(final int i0,
                    final int i1,
                    final int i2) {
      _i0 = i0;
      _i1 = i1;
      _i2 = i2;
   }


   @Override
   public String toString() {
      return "IndexedTriangle [" + _i0 + ", " + _i1 + ", " + _i2 + "]";
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + _i0;
      result = prime * result + _i1;
      result = prime * result + _i2;
      return result;
   }


   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final GIndexedTriangle other = (GIndexedTriangle) obj;
      if (_i0 != other._i0) {
         return false;
      }
      if (_i1 != other._i1) {
         return false;
      }
      if (_i2 != other._i2) {
         return false;
      }
      return true;
   }


   public GTriangle2D asTriangle2(final IPointsContainer<IVector2> container) {
      return new GTriangle2D(container.getPoint(_i0), container.getPoint(_i1), container.getPoint(_i2));
   }


   public GTriangle3D asTriangle3(final IPointsContainer<IVector3> container) {
      return new GTriangle3D(container.getPoint(_i0), container.getPoint(_i1), container.getPoint(_i2));
   }


   public GTriangle2D asTriangle2(final List<IVector2> container) {
      return new GTriangle2D(container.get(_i0), container.get(_i1), container.get(_i2));
   }


   public GTriangle3D asTriangle3(final List<IVector3> container) {
      return new GTriangle3D(container.get(_i0), container.get(_i1), container.get(_i2));
   }


}
