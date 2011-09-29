

package es.igosoftware.euclid.ntree;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.vector.IVector;


public class GElementGeometryPair<

VectorT extends IVector<VectorT, ?>,

ElementT,

GeometryT extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>

> {


   private final ElementT                          _element;
   private final GeometryT                         _geometry;
   private final GAxisAlignedOrthotope<VectorT, ?> _bounds;


   GElementGeometryPair(final ElementT element,
                        final GeometryT geometry) {
      _element = element;
      _geometry = geometry;

      _bounds = geometry.getBounds().asAxisAlignedOrthotope();
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_element == null) ? 0 : _element.hashCode());
      result = prime * result + ((_geometry == null) ? 0 : _geometry.hashCode());
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
      final GElementGeometryPair other = (GElementGeometryPair) obj;
      if (_element == null) {
         if (other._element != null) {
            return false;
         }
      }
      else if (!_element.equals(other._element)) {
         return false;
      }
      if (_geometry == null) {
         if (other._geometry != null) {
            return false;
         }
      }
      else if (!_geometry.equals(other._geometry)) {
         return false;
      }
      return true;
   }


   @Override
   public String toString() {
      return "[element=" + _element + ", geometry=" + _geometry + "]";
   }


   public ElementT getElement() {
      return _element;
   }


   public GeometryT getGeometry() {
      return _geometry;
   }


   public GAxisAlignedOrthotope<VectorT, ?> getBounds() {
      return _bounds;
   }


}
