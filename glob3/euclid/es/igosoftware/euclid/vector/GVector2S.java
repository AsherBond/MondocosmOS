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
import java.io.DataOutputStream;
import java.io.IOException;

import es.igosoftware.util.GAssert;
import es.igosoftware.util.GMath;


public class GVector2S
         implements
            IVectorI2 {

   private static final long     serialVersionUID   = 1L;


   public static final GVector2S UNIT               = new GVector2S(1, 1);
   public static final GVector2S ZERO               = new GVector2S(0, 0);

   public static final GVector2S NEGATIVE_MIN_VALUE = new GVector2S(Short.MIN_VALUE, Short.MIN_VALUE);
   public static final GVector2S POSITIVE_MAX_VALUE = new GVector2S(Short.MAX_VALUE, Short.MAX_VALUE);


   public static final GVector2S X_UP               = new GVector2S(1, 0);
   public static final GVector2S Y_UP               = new GVector2S(0, 1);
   public static final GVector2S X_DOWN             = new GVector2S(-1, 0);
   public static final GVector2S Y_DOWN             = new GVector2S(0, -1);


   public final IVectorI2 load(final DataInputStream input) throws IOException {
      final short x = input.readShort();
      final short y = input.readShort();
      return new GVector2S(x, y);
   }


   public final void save(final DataOutputStream output) throws IOException {
      output.writeShort(_x);
      output.writeShort(_y);
   }

   public final short _x;
   public final short _y;


   public GVector2S(final short x,
                    final short y) {
      GAssert.notNan(x, "x");
      GAssert.notNan(y, "y");
      _x = x;
      _y = y;
   }


   public GVector2S(final int x,
                    final int y) {
      this((short) x, (short) y);
   }


   @Override
   public final int x() {

      return _x;
   }


   @Override
   public final int y() {

      return _y;
   }


   @Override
   public GVector2S scale(final IVectorI2 that) {
      return new GVector2S(_x * that.x(), _y * that.y());
   }


   @Override
   public GVector2S scale(final int scale) {
      return new GVector2S(_x * scale, _y * scale);
   }


   @Override
   public GVector2S add(final IVectorI2 that) {
      return new GVector2S(_x + that.x(), _y + that.y());
   }


   @Override
   public GVector2S add(final int delta) {
      return new GVector2S(_x + delta, _y + delta);
   }


   @Override
   public GVector2S sub(final IVectorI2 that) {
      return new GVector2S(_x - that.x(), _y - that.y());
   }


   @Override
   public GVector2S sub(final int delta) {
      return new GVector2S(_x - delta, _y - delta);
   }


   @Override
   public String asParseableString() {
      return Short.toString(_x) + "," + Short.toString(_y);
   }


   @Override
   public final String toString() {
      return "(" + _x + ", " + _y + ")";
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + _x;
      result = prime * result + _y;
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
      final GVector2S other = (GVector2S) obj;
      if (_x != other._x) {
         return false;
      }
      if (_y != other._y) {
         return false;
      }
      return true;
   }


   @Override
   public boolean between(final IVectorI2 min,
                          final IVectorI2 max) {
      return GMath.between(_x, min.x(), max.x()) && GMath.between(_y, min.y(), max.y());
   }


   @Override
   public boolean greaterOrEquals(final IVectorI2 that) {
      return GMath.greaterOrEquals(_x, that.x()) && GMath.greaterOrEquals(_y, that.y());
   }


   @Override
   public boolean lessOrEquals(final IVectorI2 that) {
      return GMath.lessOrEquals(_x, that.x()) && GMath.greaterOrEquals(_y, that.y());
   }


   @Override
   public byte dimensions() {
      return 2;
   }


   @Override
   public int get(final byte i) {
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
   public int[] getCoordinates() {
      return new int[] {
                        _x,
                        _y
      };
   }


}
