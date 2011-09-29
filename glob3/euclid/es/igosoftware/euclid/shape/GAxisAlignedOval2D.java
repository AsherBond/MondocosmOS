

package es.igosoftware.euclid.shape;

import es.igosoftware.euclid.GGeometryAbstract;
import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorFunction;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GMath;


public class GAxisAlignedOval2D
         extends
            GGeometryAbstract<IVector2>
         implements
            ISurface2D<GAxisAlignedRectangle> {

   private static final long     serialVersionUID = 1L;


   public final IVector2         _center;
   public final IVector2         _radius;


   private GAxisAlignedRectangle _bounds;


   public GAxisAlignedOval2D(final IVector2 center,
                             final IVector2 radius) {
      GAssert.notNull(center, "center");
      GAssert.notNull(radius, "radius");
      GAssert.isPositive(radius.x(), "radius.x()");
      GAssert.isPositive(radius.y(), "radius.y()");

      _center = center;
      _radius = radius;
   }


   @Override
   public byte dimensions() {
      return _center.dimensions();
   }


   @Override
   public boolean contains(final IVector2 point) {
      throw new RuntimeException("Not yet implemented!");
   }


   @Override
   public double squaredDistance(final IVector2 point) {
      throw new RuntimeException("Not yet implemented!");
   }


   @Override
   public IVector2 closestPoint(final IVector2 point) {
      throw new RuntimeException("Not yet implemented!");
   }


   @Override
   public double precision() {
      return _center.precision();
   }


   @Override
   public IVector2 getCentroid() {
      return _center;
   }


   @Override
   public GAxisAlignedRectangle getBounds() {
      if (_bounds == null) {
         _bounds = new GAxisAlignedRectangle(_center.sub(_radius), _center.add(_radius));
      }
      return _bounds;
   }


   @Override
   public IVector2 closestPointOnBoundary(final IVector2 point) {
      throw new RuntimeException("Not yet implemented!");
   }


   @Override
   public double squaredDistanceToBoundary(final IVector2 point) {
      throw new RuntimeException("Not yet implemented!");
   }


   @Override
   public double distanceToBoundary(final IVector2 point) {
      throw new RuntimeException("Not yet implemented!");
   }


   @Override
   public boolean containsOnBoundary(final IVector2 point) {
      throw new RuntimeException("Not yet implemented!");
   }


   @Override
   public String toString() {
      return "GAxisAlignedEllipse2D [center=" + _center + ", radius=" + _radius + "]";
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_center == null) ? 0 : _center.hashCode());
      result = prime * result + ((_radius == null) ? 0 : _radius.hashCode());
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
      final GAxisAlignedOval2D other = (GAxisAlignedOval2D) obj;
      if (_center == null) {
         if (other._center != null) {
            return false;
         }
      }
      else if (!_center.equals(other._center)) {
         return false;
      }
      if (_radius == null) {
         if (other._radius != null) {
            return false;
         }
      }
      else if (!_radius.equals(other._radius)) {
         return false;
      }
      return true;
   }


   @Override
   public GAxisAlignedOval2D transform(final IVectorFunction<IVector2> transformer) {
      if (transformer == null) {
         return this;
      }

      final GAxisAlignedRectangle transformedBounds = getBounds().transform(transformer);

      return new GAxisAlignedOval2D(transformedBounds._center, transformedBounds.getExtent().div(2));
   }


   @Override
   public boolean closeTo(final IBoundedGeometry<IVector2, GAxisAlignedRectangle> that) {
      if (that instanceof GAxisAlignedOval2D) {
         final GAxisAlignedOval2D thatAAE = (GAxisAlignedOval2D) that;
         return _center.closeTo(thatAAE._center) && _radius.closeTo(thatAAE._radius);
      }
      return false;
   }


   @Override
   public double area() {
      return Math.PI * _radius.x() * _radius.y();
   }


   @Override
   public double perimeter() {
      // Ramanujan approximation from http://mathforum.org/library/drmath/view/54847.html

      final double A = _radius.x();
      final double B = _radius.y();

      final double A3 = A * 3;
      final double B3 = B * 3;

      return Math.PI * ((A3 + B3) - GMath.sqrt((A + B3) * (B + A3)));
   }


}
