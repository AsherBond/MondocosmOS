/*

 IGO Software SL  -  info@igosoftware.es

 http://www.glob3.org

-------------------------------------------------------------------------------
 Copyright (c) 2010, IGO Software SL
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
     * Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.
     * Neither the name of the IGO Software SL nor the
       names of its contributors may be used to endorse or promote products
       derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL IGO Software SL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-------------------------------------------------------------------------------

*/


package es.igosoftware.euclid.vector;

import java.io.DataInputStream;
import java.io.IOException;

import es.igosoftware.euclid.GAngle;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.matrix.GMatrix33D;
import es.igosoftware.euclid.matrix.GMatrix44D;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GMath;


public class GVector2D
         extends
            GVectorAbstract<IVector2, GAxisAlignedRectangle>
         implements
            IVector2 {


   private static final class Normalized
            extends
               GVector2D {

      private static final long serialVersionUID = 1L;


      private Normalized(final double x,
                         final double y) {
         super(x, y);
      }


      @Override
      public boolean isNormalized() {
         return true;
      }


      @Override
      public double length() {
         return 1;
      }


      @Override
      public double squaredLength() {
         return 1;
      }


      @Override
      public GVector2D.Normalized normalized() {
         return this;
      }
   }


   private static final long     serialVersionUID  = 1L;


   public static final GVector2D UNIT              = new GVector2D(1, 1);
   public static final GVector2D ZERO              = new GVector2D(0, 0);

   public static final GVector2D NEGATIVE_INFINITY = new GVector2D(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
   public static final GVector2D POSITIVE_INFINITY = new GVector2D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);


   public static final GVector2D X_UP              = new GVector2D(1, 0).normalized();
   public static final GVector2D Y_UP              = new GVector2D(0, 1).normalized();
   public static final GVector2D X_DOWN            = new GVector2D(-1, 0).normalized();
   public static final GVector2D Y_DOWN            = new GVector2D(0, -1).normalized();


   public static GVector2D fromRhoAngle(final int rho,
                                        final GAngle angle) {
      final double x = rho * angle.cos();
      final double y = rho * angle.sin();
      return new GVector2D(x, y);
   }


   public static IVector2 load(final DataInputStream input) throws IOException {
      final double x = input.readDouble();
      final double y = input.readDouble();
      return new GVector2D(x, y);
   }


   public final double _x;
   public final double _y;


   public GVector2D(final double x,
                    final double y) {
      this(x, y, true);
   }


   public GVector2D(final double x,
                    final double y,
                    final boolean checkNaN) {
      if (checkNaN) {
         GAssert.notNan(x, "x");
         GAssert.notNan(y, "y");
      }

      _x = x;
      _y = y;
   }


   @Override
   public final double x() {
      return _x;
   }


   @Override
   public final double y() {
      return _y;
   }


   @Override
   public final GVector2D absoluted() {
      return new GVector2D(Math.abs(_x), Math.abs(_y), false);
   }


   @Override
   public final GVector2D rounded() {
      return new GVector2D(Math.round(_x), Math.round(_y), false);
   }


   @Override
   public final GVector2D add(final IVector2 that) {
      return new GVector2D(_x + that.x(), _y + that.y(), false);
   }


   @Override
   public final GVector2D add(final double delta) {
      return new GVector2D(_x + delta, _y + delta, false);
   }


   //   @Override
   //   public double angle(final IVector2 that) {
   //      double vDot = dot(that) / (length() * that.length());
   //      if (vDot < -1.0) {
   //         vDot = -1.0;
   //      }
   //      if (vDot > 1.0) {
   //         vDot = 1.0;
   //      }
   //      return Math.acos(vDot);
   //   }


   @Override
   public final byte dimensions() {
      return 2;
   }


   @Override
   public final double squaredDistance(final IVector2 that) {
      final double dx = _x - that.x();
      final double dy = _y - that.y();
      return dx * dx + dy * dy;
   }


   @Override
   public final double dot(final IVector2 that) {
      return (_x * that.x() + _y * that.y());
   }


   @Override
   public final boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final IVector2 other = (IVector2) obj;
      if (Double.doubleToLongBits(_x) != Double.doubleToLongBits(other.x())) {
         return false;
      }
      return Double.doubleToLongBits(_y) == Double.doubleToLongBits(other.y());
   }


   @Override
   public final int hashCode() {
      final int prime = 31;
      int result = 1;
      long temp;
      temp = Double.doubleToLongBits(_x);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(_y);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      return result;
   }


   @Override
   public final GVector2D interpolatedTo(final IVector2 that,
                                         final double alpha) {
      //      final double newX = (1.0 - alpha) * x + alpha * that.getX();
      //      final double newY = (1.0 - alpha) * y + alpha * that.getY();

      final double newX = GMath.interpolate(_x, that.x(), alpha);
      final double newY = GMath.interpolate(_y, that.y(), alpha);

      return new GVector2D(newX, newY);
   }


   @Override
   public double squaredLength() {
      return (_x * _x + _y * _y);
   }


   @Override
   public final GVector2D negated() {
      return new GVector2D(-_x, -_y, false);
   }


   @Override
   public final GVector2D reciprocal() {
      return new GVector2D(1.0 / _x, 1.0 / _y);
   }


   @Override
   public GVector2D normalized() {
      final double length = length();
      if (GMath.closeToZero(length)) {
         return this;
      }
      final double newX = _x / length;
      final double newY = _y / length;
      return new GVector2D.Normalized(newX, newY);
   }


   @Override
   public final GVector2D scale(final double scale) {
      return new GVector2D(_x * scale, _y * scale);
   }


   @Override
   public final GVector2D scale(final IVector2 that) {
      return new GVector2D(_x * that.x(), _y * that.y());
   }


   @Override
   public final GVector2D div(final double scale) {
      return new GVector2D(_x / scale, _y / scale);
   }


   @Override
   public final GVector2D div(final IVector2 that) {
      return new GVector2D(_x / that.x(), _y / that.y());
   }


   @Override
   public final GVector2D sub(final IVector2 that) {
      return new GVector2D(_x - that.x(), _y - that.y(), false);
   }


   @Override
   public final GVector2D sub(final double delta) {
      return new GVector2D(_x - delta, _y - delta, false);
   }


   @Override
   public final String toString() {
      return "(" + _x + ", " + _y + ")";
   }


   @Override
   public final GVector2D max(final IVector2 that) {
      return new GVector2D(Math.max(_x, that.x()), Math.max(_y, that.y()), false);
   }


   @Override
   public final GVector2D min(final IVector2 that) {
      return new GVector2D(Math.min(_x, that.x()), Math.min(_y, that.y()), false);
   }


   @Override
   public final String asParseableString() {
      return Double.toString(_x) + "," + Double.toString(_y);
   }


   //   @Override
   //   public GAxisAlignedRectangle getBounds() {
   //      return new GAxisAlignedRectangle(this, this);
   //   }


   //   @Override
   //   public GAxisAlignedBox getAxisAlignedBoundingBox() {
   //      return getBounds().getAxisAlignedBoundingBox();
   //   }
   //
   //
   //   @Override
   //   public boolean contains(final IVector2 vector) {
   //      return false;
   //   } 


   @Override
   public final double get(final byte i) {
      switch (i) {
         case 0:
            return _x;
         case 1:
            return _y;
         default:
            throw new IndexOutOfBoundsException("" + i);
      }
   }


   @Override
   public final double[] getCoordinates() {
      return new double[] {
                        _x,
                        _y
      };
   }


   //   @Override
   //   public boolean isNormalized() {
   //      return (squaredLength() == 1);
   //   }

   @Override
   public final GMutableVector2<GVector2D> asMutable() {
      return new GMutableVector2<GVector2D>(this);
   }


   @Override
   public final boolean between(final IVector2 min,
                                final IVector2 max) {
      final double precision = GMath.maxD(precision(), min.precision(), max.precision());
      return GMath.between(_x, min.x(), max.x(), precision) && GMath.between(_y, min.y(), max.y(), precision);
   }


   @Override
   public final boolean closeToZero() {
      return GMath.closeToZero(_x) && GMath.closeToZero(_y);
   }


   @Override
   public final boolean closeTo(final IVector2 that) {
      final double precision = Math.max(precision(), that.precision());
      return closeTo(that, precision);
   }


   @Override
   public final boolean closeTo(final IVector2 that,
                                final double precision) {
      return GMath.closeTo(x(), that.x(), precision) && GMath.closeTo(y(), that.y(), precision);
   }


   @Override
   public final GVector2D transformedBy(final GMatrix44D matrix) {
      final double newX = (matrix._m00 * _x) + (matrix._m01 * _y) + matrix._m03;
      final double newY = (matrix._m10 * _x) + (matrix._m11 * _y) + matrix._m13;

      return new GVector2D(newX, newY);
   }


   @Override
   public final GVector2D transformedBy(final GMatrix33D matrix) {
      final double newX = (matrix._m00 * _x) + (matrix._m01 * _y) + matrix._m02;
      final double newY = (matrix._m10 * _x) + (matrix._m11 * _y) + matrix._m12;

      return new GVector2D(newX, newY);
   }


   @Override
   public final double precision() {
      return GMath.DEFAULT_NUMERICAL_PRECISION_DOUBLE;
   }


   @Override
   public final IVector2 asVector2() {
      return this;
   }


   @Override
   public final IVector2 nextUp() {
      return new GVector2D(GMath.nextUp(_x), GMath.nextUp(_y), false);
   }


   @Override
   public final IVector2 previousDown() {
      return new GVector2D(GMath.previousDown(_x), GMath.previousDown(_y), false);
   }


   @Override
   public final IVector2 asDouble() {
      return this;
   }


   @Override
   public final boolean greaterOrEquals(final IVector2 that) {
      final double precision = Math.max(precision(), that.precision());
      return GMath.greaterOrEquals(_x, that.x(), precision) && GMath.greaterOrEquals(_y, that.y(), precision);
   }


   @Override
   public final boolean lessOrEquals(final IVector2 that) {
      final double precision = Math.max(precision(), that.precision());
      return GMath.lessOrEquals(_x, that.x(), precision) && GMath.lessOrEquals(_y, that.y(), precision);
   }


   @Override
   public final IVector2 reproject(final GProjection sourceProjection,
                                   final GProjection targetProjection) {
      //      if (sourceProjection == targetProjection) {
      //         return this;
      //      }
      return sourceProjection.transformPoint(targetProjection, this);
   }


   @Override
   public GAxisAlignedRectangle getBounds() {
      return new GAxisAlignedRectangle(this, nextUp());
   }


   @Override
   public GVector2D squared() {
      return new GVector2D(GMath.squared(_x), GMath.squared(_y));
   }


   @Override
   public GVector2D rotated(final GAngle angle) {
      final double cos = angle.cos();
      final double sin = angle.sin();
      final double x = cos * _x - sin * _y;
      final double y = sin * _x + cos * _y;
      return new GVector2D(x, y);
   }


}
