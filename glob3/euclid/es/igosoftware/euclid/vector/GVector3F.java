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

import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.matrix.GMatrix33D;
import es.igosoftware.euclid.matrix.GMatrix44D;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GMath;
import es.igosoftware.util.XStringTokenizer;


public class GVector3F
         extends
            GVectorAbstract<IVector3, GAxisAlignedBox>
         implements
            IVector3 {

   private static final class Normalized
            extends
               GVector3F {

      private static final long serialVersionUID = 1L;


      private Normalized(final double x,
                         final double y,
                         final double z) {
         super(x, y, z);
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
      public GVector3F.Normalized normalized() {
         return this;
      }
   }


   private static final long     serialVersionUID  = 1L;

   public static final GVector3F X_UP              = new GVector3F(1, 0, 0).normalized();
   public static final GVector3F X_DOWN            = new GVector3F(-1, 0, 0).normalized();
   public static final GVector3F Y_UP              = new GVector3F(0, 1, 0).normalized();
   public static final GVector3F Y_DOWN            = new GVector3F(0, -1, 0).normalized();
   public static final GVector3F Z_UP              = new GVector3F(0, 0, 1).normalized();
   public static final GVector3F Z_DOWN            = new GVector3F(0, 0, -1).normalized();

   public static final GVector3F UNIT              = new GVector3F(1, 1, 1);
   public static final GVector3F ZERO              = new GVector3F(0, 0, 0);

   public static final GVector3F NEGATIVE_INFINITY = new GVector3F(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY,
                                                            Float.NEGATIVE_INFINITY);
   public static final GVector3F POSITIVE_INFINITY = new GVector3F(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY,
                                                            Float.POSITIVE_INFINITY);


   public static GVector3F load(final DataInputStream input) throws IOException {
      final float x = input.readFloat();
      final float y = input.readFloat();
      final float z = input.readFloat();
      return new GVector3F(x, y, z);
   }


   public static GVector3F parseString(final String string) throws IOException {
      final String[] tokens = XStringTokenizer.getAllTokens(string, ",");
      if (tokens.length != 3) {
         throw new IOException("Invalid string format: " + string);
      }

      final float x = Float.parseFloat(tokens[0]);
      final float y = Float.parseFloat(tokens[1]);
      final float z = Float.parseFloat(tokens[2]);
      return new GVector3F(x, y, z);
   }


   public final float _x;
   public final float _y;
   public final float _z;


   public GVector3F(final float x,
                    final float y,
                    final float z) {
      this(x, y, z, true);
   }


   public GVector3F(final float x,
                    final float y,
                    final float z,
                    final boolean checkNaN) {
      if (checkNaN) {
         GAssert.notNan(x, "x");
         GAssert.notNan(y, "y");
         GAssert.notNan(z, "z");
      }

      _x = x;
      _y = y;
      _z = z;
   }


   private GVector3F(final double x,
                     final double y,
                     final double z) {
      this(x, y, z, true);
   }


   private GVector3F(final double x,
                     final double y,
                     final double z,
                     final boolean checkNaN) {
      if (checkNaN) {
         GAssert.notNan(x, "x");
         GAssert.notNan(y, "y");
         GAssert.notNan(z, "z");
      }

      _x = (float) x;
      _y = (float) y;
      _z = (float) z;
   }


   public GVector3F(final IVector2 vector2d,
                    final double z) {
      this(vector2d.x(), vector2d.y(), z);
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
   public final double z() {
      return _z;
   }


   @Override
   public final GVector3F absoluted() {
      return new GVector3F(Math.abs(_x), Math.abs(_y), Math.abs(_z), false);
   }


   @Override
   public final GVector3F rounded() {
      return new GVector3F(Math.round(_x), Math.round(_y), Math.round(_z), false);
   }


   @Override
   public final GVector3F add(final IVector3 that) {
      return new GVector3F(_x + that.x(), _y + that.y(), _z + that.z(), false);
   }


   @Override
   public final GVector3F add(final double delta) {
      return new GVector3F(_x + delta, _y + delta, _z + delta, false);
   }


   //   @Override
   //   public double angle(final IVector3 that) {
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
      return 3;
   }


   @Override
   public final double squaredDistance(final IVector3 that) {
      final double dx = _x - that.x();
      final double dy = _y - that.y();
      final double dz = _z - that.z();
      return dx * dx + dy * dy + dz * dz;
   }


   @Override
   public final double dot(final IVector3 that) {
      return (_x * that.x() + _y * that.y() + _z * that.z());
   }


   @Override
   public final GVector3F interpolatedTo(final IVector3 that,
                                         final double alpha) {
      //      final double newX = (1.0 - alpha) * x + alpha * that.getX();
      //      final double newY = (1.0 - alpha) * y + alpha * that.getY();
      //      final double newZ = (1.0 - alpha) * z + alpha * that.getZ();

      final double newX = GMath.interpolate(_x, that.x(), alpha);
      final double newY = GMath.interpolate(_y, that.y(), alpha);
      final double newZ = GMath.interpolate(_z, that.z(), alpha);

      return new GVector3F(newX, newY, newZ, false);
   }


   @Override
   public double squaredLength() {
      return (_x * _x + _y * _y + _z * _z);
   }


   @Override
   public final GVector3F negated() {
      return new GVector3F(-_x, -_y, -_z, false);
   }


   @Override
   public final GVector3F reciprocal() {
      return new GVector3F(1.0 / _x, 1.0 / _y, 1.0 / _z);
   }


   @Override
   public GVector3F normalized() {
      final double length = length();
      if (GMath.closeToZero(length)) {
         return this;
      }
      final double newX = _x / length;
      final double newY = _y / length;
      final double newZ = _z / length;
      return new GVector3F.Normalized(newX, newY, newZ);
   }


   @Override
   public final GVector3F scale(final double scale) {
      return new GVector3F(_x * scale, _y * scale, _z * scale);
   }


   @Override
   public final GVector3F scale(final IVector3 that) {
      return new GVector3F(_x * that.x(), _y * that.y(), _z * that.z());
   }


   @Override
   public final GVector3F div(final double scale) {
      return new GVector3F(_x / scale, _y / scale, _z / scale);
   }


   @Override
   public final GVector3F div(final IVector3 that) {
      return new GVector3F(_x / that.x(), _y / that.y(), _z / that.z());
   }


   @Override
   public final GVector3F sub(final IVector3 that) {
      return new GVector3F(_x - that.x(), _y - that.y(), _z - that.z(), false);
   }


   @Override
   public final GVector3F sub(final double delta) {
      return new GVector3F(_x - delta, _y - delta, _z - delta, false);
   }


   @Override
   public final String toString() {
      return "(" + _x + ", " + _y + ", " + _z + ")";
   }


   @Override
   public final GVector3F max(final IVector3 that) {
      return new GVector3F(Math.max(_x, that.x()), Math.max(_y, that.y()), Math.max(_z, that.z()), false);
   }


   @Override
   public final GVector3F min(final IVector3 that) {
      return new GVector3F(Math.min(_x, that.x()), Math.min(_y, that.y()), Math.min(_z, that.z()), false);
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
      temp = Double.doubleToLongBits(_z);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      return result;
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
      final IVector3 other = (IVector3) obj;
      if (Double.doubleToLongBits(_x) != Double.doubleToLongBits(other.x())) {
         return false;
      }
      if (Double.doubleToLongBits(_y) != Double.doubleToLongBits(other.y())) {
         return false;
      }
      return Double.doubleToLongBits(_z) == Double.doubleToLongBits(other.z());
   }


   @Override
   public final GVector2F asVector2() {
      return new GVector2F(_x, _y, false);
   }


   @Override
   public final String asParseableString() {
      return Float.toString(_x) + "," + Float.toString(_y) + "," + Float.toString(_z);
   }


   //   @Override
   //   public GAxisAlignedBox getBounds() {
   //      return new GAxisAlignedBox(this, this);
   //   }


   //   @Override
   //   public GAxisAlignedBox getAxisAlignedBoundingBox() {
   //      return getBounds();
   //   }
   //
   //
   //   @Override 
   //   public boolean contains(final IVector3 vector) {
   //      return false;
   //   }


   @Override
   public final GVector3F cross(final IVector3 that) {
      final double thatX = that.x();
      final double thatY = that.y();
      final double thatZ = that.z();

      final double newX = ((_y * thatZ) - (_z * thatY));
      final double newY = ((_z * thatX) - (_x * thatZ));
      final double newZ = ((_x * thatY) - (_y * thatX));

      return new GVector3F(newX, newY, newZ, false);
   }


   @Override
   public final double get(final byte i) {
      switch (i) {
         case 0:
            return _x;
         case 1:
            return _y;
         case 2:
            return _z;
         default:
            throw new IndexOutOfBoundsException("" + i);
      }
   }


   @Override
   public final double[] getCoordinates() {
      return new double[] {
                        _x,
                        _y,
                        _z
      };
   }


   //   @Override
   //   public boolean isNormalized() {
   //      return (squaredLength() == 1);
   //   }

   @Override
   public final GMutableVector3<GVector3F> asMutable() {
      return new GMutableVector3<GVector3F>(this);
   }


   @Override
   public final boolean between(final IVector3 min,
                                final IVector3 max) {
      final double precision = GMath.maxD(precision(), min.precision(), max.precision());
      return GMath.between(_x, min.x(), max.x(), precision) && GMath.between(_y, min.y(), max.y(), precision)
             && GMath.between(_z, min.z(), max.z(), precision);
   }


   @Override
   public final boolean closeToZero() {
      return GMath.closeToZero(_x) && GMath.closeToZero(_y) && GMath.closeToZero(_z);
   }


   @Override
   public final boolean closeTo(final IVector3 that) {
      final double precision = Math.max(precision(), that.precision());
      return closeTo(that, precision);
   }


   @Override
   public final boolean closeTo(final IVector3 that,
                                final double precision) {
      return GMath.closeTo(x(), that.x(), precision) && GMath.closeTo(y(), that.y(), precision)
             && GMath.closeTo(z(), that.z(), precision);
   }


   @Override
   public final GVector3F transformedBy(final GMatrix44D matrix) {
      final double newX = (matrix._m00 * _x) + (matrix._m01 * _y) + (matrix._m02 * _z) + matrix._m03;
      final double newY = (matrix._m10 * _x) + (matrix._m11 * _y) + (matrix._m12 * _z) + matrix._m13;
      final double newZ = (matrix._m20 * _x) + (matrix._m21 * _y) + (matrix._m22 * _z) + matrix._m23;

      return new GVector3F(newX, newY, newZ);
   }


   @Override
   public final GVector3F transformedBy(final GMatrix33D matrix) {
      final double newX = (matrix._m00 * _x) + (matrix._m01 * _y) + (matrix._m02 * _z);
      final double newY = (matrix._m10 * _x) + (matrix._m11 * _y) + (matrix._m12 * _z);
      final double newZ = (matrix._m20 * _x) + (matrix._m21 * _y) + (matrix._m22 * _z);

      return new GVector3F(newX, newY, newZ);
   }


   @Override
   public final double precision() {
      return GMath.DEFAULT_NUMERICAL_PRECISION_FLOAT;
   }


   @Override
   public final IVector3 nextUp() {
      return new GVector3F(GMath.nextUp(_x), GMath.nextUp(_y), GMath.nextUp(_z), false);
   }


   @Override
   public final IVector3 previousDown() {
      return new GVector3F(GMath.previousDown(_x), GMath.previousDown(_y), GMath.previousDown(_z), false);
   }


   @Override
   public final IVector3 asDouble() {
      return new GVector3D(_x, _y, _z, false);
   }


   @Override
   public final boolean greaterOrEquals(final IVector3 that) {
      final double precision = Math.max(precision(), that.precision());
      return GMath.greaterOrEquals(_x, that.x(), precision) && GMath.greaterOrEquals(_y, that.y(), precision)
             && GMath.greaterOrEquals(_z, that.z(), precision);
   }


   @Override
   public final boolean lessOrEquals(final IVector3 that) {
      final double precision = Math.max(precision(), that.precision());
      return GMath.lessOrEquals(_x, that.x(), precision) && GMath.lessOrEquals(_y, that.y(), precision)
             && GMath.lessOrEquals(_z, that.z(), precision);
   }


   @Override
   public final IVector3 reproject(final GProjection sourceProjection,
                                   final GProjection targetProjection) {
      return sourceProjection.transformPoint(targetProjection, this);
   }


   @Override
   public GAxisAlignedBox getBounds() {
      return new GAxisAlignedBox(this, nextUp());
   }


   @Override
   public GVector3F squared() {
      return new GVector3F(GMath.squared(_x), GMath.squared(_y), GMath.squared(_z));
   }


}
