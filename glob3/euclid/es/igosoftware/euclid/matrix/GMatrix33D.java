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


package es.igosoftware.euclid.matrix;

import java.io.PrintStream;

import es.igosoftware.euclid.GAngle;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.GMath;


public abstract class GMatrix33D
         implements
            ISquareMatrix<GMatrix33D, IVector3>,
            Cloneable {


   public static final GMatrix33D IDENTITY = new GMatrix33D.Identity();
   public static final GMatrix33D ZERO     = new GMatrix33D.Zero();


   private static final class Identity
            extends
               GMatrix33D {

      private Identity() {
         super(1, 0, 0, //
               0, 1, 0, //
               0, 0, 1);
      }


      @Override
      protected final String descriptionString() {
         return " IDENTITY";
      }


      @Override
      public double determinant() {
         return 1;
      }


      @Override
      public final boolean isIdentity() {
         return true;
      }


      @Override
      public boolean isZero() {
         return false;
      }


      @Override
      public final GMatrix33D mul(final GMatrix33D that) {
         return that;
      }

   }


   private static final class Plain
            extends
               GMatrix33D {

      private Plain(final double m00,
                    final double m01,
                    final double m02,
                    final double m10,
                    final double m11,
                    final double m12,
                    final double m20,
                    final double m21,
                    final double m22) {
         super(m00, m01, m02, m10, m11, m12, m20, m21, m22);
      }


      @Override
      protected String descriptionString() {
         return "";
      }


      @Override
      public double determinant() {
         // from jMonkey FastMath.determinant();
         //
         //         float fCo00 = m11*m22 - m12*m21;
         //         float fCo10 = m12*m20 - m10*m22;
         //         float fCo20 = m10*m21 - m11*m20;
         //         float fDet = m00*fCo00 + m01*fCo10 + m02*fCo20;
         //         return fDet;

         final double det00 = _m11 * _m22 - _m12 * _m21;
         final double det01 = _m12 * _m20 - _m10 * _m22;
         final double det02 = _m10 * _m21 - _m11 * _m20;

         return GMath.sum(_m00 * det00, _m01 * det01, _m02 * det02);

      }


      @Override
      public boolean isIdentity() {
         return false;
      }


      @Override
      public boolean isZero() {
         return false;
      }


      @Override
      public GMatrix33D mul(final GMatrix33D that) {
         if (that.isIdentity()) {
            return this;
         }
         if (that.isZero()) {
            return GMatrix33D.ZERO;
         }

         final double that00 = that._m00;
         final double that10 = that._m10;
         final double that20 = that._m20;

         final double that01 = that._m01;
         final double that11 = that._m11;
         final double that21 = that._m21;

         final double that02 = that._m02;
         final double that12 = that._m12;
         final double that22 = that._m22;

         final double m00 = GMath.sum((_m00 * that00), (_m01 * that10), (_m02 * that20));
         final double m01 = GMath.sum((_m00 * that01), (_m01 * that11), (_m02 * that21));
         final double m02 = GMath.sum((_m00 * that02), (_m01 * that12), (_m02 * that22));

         final double m10 = GMath.sum((_m10 * that00), (_m11 * that10), (_m12 * that20));
         final double m11 = GMath.sum((_m10 * that01), (_m11 * that11), (_m12 * that21));
         final double m12 = GMath.sum((_m10 * that02), (_m11 * that12), (_m12 * that22));

         final double m20 = GMath.sum((_m20 * that00), (_m21 * that10), (_m22 * that20));
         final double m21 = GMath.sum((_m20 * that01), (_m21 * that11), (_m22 * that21));
         final double m22 = GMath.sum((_m20 * that02), (_m21 * that12), (_m22 * that22));

         return createMatrix( //
                  m00, m01, m02, //
                  m10, m11, m12, //
                  m20, m21, m22);
      }

   }


   private static final class Zero
            extends
               GMatrix33D {

      private Zero() {
         super(0, 0, 0, //
               0, 0, 0, //
               0, 0, 0);
      }


      @Override
      public final GMatrix33D add(final GMatrix33D that) {
         return that;
      }


      @Override
      protected final String descriptionString() {
         return " ZERO";
      }


      @Override
      public double determinant() {
         return 0;
      }


      @Override
      public final boolean isIdentity() {
         return false;
      }


      @Override
      public boolean isZero() {
         return true;
      }


      @Override
      public GMatrix33D mul(final double scale) {
         return this;
      }


      @Override
      public GMatrix33D mul(final GMatrix33D that) {
         return this;
      }


      @Override
      public final GMatrix33D sub(final GMatrix33D that) {
         return that.negated();
      }


      @Override
      public final GMatrix33D negated() {
         return this;
      }

   }


   public static GMatrix33D createMatrix(final double m00,
                                         final double m01,
                                         final double m02,
                                         final double m10,
                                         final double m11,
                                         final double m12,
                                         final double m20,
                                         final double m21,
                                         final double m22) {

      final boolean allButDiagonalIsZero = GMath.closeToZero( // the rest is all zeros
               m01, m02, //
               m10, m12, //
               m20, m21);

      final boolean diagonalIsOne = GMath.closeToOne(m00, m11, m22);
      if ((diagonalIsOne && allButDiagonalIsZero)) {
         return GMatrix33D.IDENTITY;
      }

      final boolean diagonalIsZero = GMath.closeToZero(m00, m11, m22);
      if ((allButDiagonalIsZero && diagonalIsZero)) {
         return GMatrix33D.ZERO;
      }

      return new GMatrix33D.Plain( //
               m00, m01, m02, //
               m10, m11, m12, //
               m20, m21, m22);
   }


   public static GMatrix33D createRotationMatrix(final IVector2 axis,
                                                 final GAngle angle) {
      final IVector2 normalizedAxis = axis.normalized();

      final double cos = angle.cos();
      final double sin = angle.sin();
      final double oneMinusCos = 1.0 - cos;

      final double axisX = normalizedAxis.x();
      final double axisY = normalizedAxis.y();

      final double x2 = axisX * axisX;
      final double y2 = axisY * axisY;

      final double xYM = axisX * axisY * oneMinusCos;

      final double xSin = axisX * sin;
      final double ySin = axisY * sin;

      final double m00 = x2 * oneMinusCos + cos;
      final double m01 = xYM;
      final double m02 = ySin;

      final double m10 = xYM;
      final double m11 = y2 * oneMinusCos + cos;
      final double m12 = -xSin;

      final double m20 = 0 - ySin;
      final double m21 = xSin;
      final double m22 = cos;

      return createMatrix( //
               m00, m01, m02, //
               m10, m11, m12, //
               m20, m21, m22);

   }


   public static GMatrix33D createRotationMatrix(final IVector3 axis,
                                                 final GAngle angle) {
      final IVector3 normalizedAxis = axis.normalized();

      final double cos = angle.cos();
      final double sin = angle.sin();
      final double oneMinusCos = 1.0 - cos;

      final double axisX = normalizedAxis.x();
      final double axisY = normalizedAxis.y();
      final double axisZ = normalizedAxis.z();

      final double x2 = axisX * axisX;
      final double y2 = axisY * axisY;
      final double z2 = axisZ * axisZ;

      final double xYM = axisX * axisY * oneMinusCos;
      final double xZM = axisX * axisZ * oneMinusCos;
      final double yZM = axisY * axisZ * oneMinusCos;

      final double xSin = axisX * sin;
      final double ySin = axisY * sin;
      final double zSin = axisZ * sin;

      final double m00 = x2 * oneMinusCos + cos;
      final double m01 = xYM - zSin;
      final double m02 = xZM + ySin;

      final double m10 = xYM + zSin;
      final double m11 = y2 * oneMinusCos + cos;
      final double m12 = yZM - xSin;

      final double m20 = xZM - ySin;
      final double m21 = yZM + xSin;
      final double m22 = z2 * oneMinusCos + cos;

      return createMatrix( //
               m00, m01, m02, //
               m10, m11, m12, //
               m20, m21, m22);
   }


   public static GMatrix33D createRotationMatrix(final IVector3 start,
                                                 final IVector3 end) {

      final IVector3 normStart = start.normalized();
      final IVector3 normEnd = end.normalized();


      final IVector3 v = normStart.cross(normEnd);

      final double e = normStart.dot(normEnd);
      final double f = Math.abs(e);

      // if "from" and "to" vectors are nearly parallel
      if (GMath.greaterOrEquals(f, 1)) {
         return GMatrix33D.IDENTITY;
      }


      final double vx = v.x();
      final double vy = v.y();
      final double vz = v.z();


      final double h = 1 / (1 + e);
      final double hvx = h * vx;
      final double hvz = h * vz;
      final double hvxy = hvx * vy;
      final double hvxz = hvx * vz;
      final double hvyz = hvz * vy;

      final double m00 = e + hvx * vx;
      final double m01 = hvxy - vz;
      final double m02 = hvxz + vy;

      final double m10 = hvxy + vz;
      final double m11 = e + h * vy * vy;
      final double m12 = hvyz - vx;

      final double m20 = hvxz - vy;
      final double m21 = hvyz + vx;
      final double m22 = e + hvz * vz;

      return createMatrix( //
               m00, m01, m02, //
               m10, m11, m12, //
               m20, m21, m22);

   }


   public static GMatrix33D createScaleMatrix(final double scale) {
      final double m00 = scale;
      final double m01 = 0;
      final double m02 = 0;

      final double m10 = 0;
      final double m11 = scale;
      final double m12 = 0;

      final double m20 = 0;
      final double m21 = 0;
      final double m22 = scale;

      return createMatrix( //
               m00, m01, m02, //
               m10, m11, m12, //
               m20, m21, m22);

   }


   public static GMatrix33D createScaleMatrix(final IVector2 scale) {
      final double m00 = scale.x();
      final double m01 = 0;
      final double m02 = 0;

      final double m10 = 0;
      final double m11 = scale.y();
      final double m12 = 0;

      final double m20 = 0;
      final double m21 = 0;
      final double m22 = 1;

      return createMatrix( //
               m00, m01, m02, //
               m10, m11, m12, //
               m20, m21, m22);

   }


   public static GMatrix33D createScaleMatrix(final IVector3 scale) {
      final double m00 = scale.x();
      final double m01 = 0;
      final double m02 = 0;

      final double m10 = 0;
      final double m11 = scale.y();
      final double m12 = 0;

      final double m20 = 0;
      final double m21 = 0;
      final double m22 = scale.z();

      return createMatrix( //
               m00, m01, m02, //
               m10, m11, m12, //
               m20, m21, m22);

   }


   //   public static GMatrix33D createTranslationMatrix(final IVector2 translation) {
   //      return createMatrix( //
   //               1, 0, translation.x(), //
   //               0, 1, translation.y(), //
   //               0, 0, 1);
   //   }
   //
   //
   //   public static GMatrix33D createTranslationMatrix(final IVector3 translation) {
   //      return createMatrix(//
   //               1, 0, translation.x(), //
   //               0, 1, translation.y(), //
   //               0, 0, translation.z());
   //   }


   public final double _m00;
   public final double _m01;
   public final double _m02;

   public final double _m10;
   public final double _m11;
   public final double _m12;


   public final double _m20;
   public final double _m21;
   public final double _m22;


   private GMatrix33D(final double m00,
                      final double m01,
                      final double m02,
                      final double m10,
                      final double m11,
                      final double m12,
                      final double m20,
                      final double m21,
                      final double m22) {
      _m00 = m00;
      _m01 = m01;
      _m02 = m02;

      _m10 = m10;
      _m11 = m11;
      _m12 = m12;

      _m20 = m20;
      _m21 = m21;
      _m22 = m22;

   }


   @Override
   public GMatrix33D add(final GMatrix33D that) {
      if (that.isZero()) {
         return this;
      }

      return createMatrix( //
               (_m00 + that._m00), (_m01 + that._m01), (_m02 + that._m02), //
               (_m10 + that._m10), (_m11 + that._m11), (_m12 + that._m12), //
               (_m20 + that._m20), (_m21 + that._m21), (_m22 + that._m22));
   }


   private double beautify(final double value) {
      if (GMath.closeToZero(value)) {
         return 0;
      }
      if (GMath.closeTo(value, 1)) {
         return 1;
      }
      if (GMath.closeTo(value, -1)) {
         return -1;
      }
      return value;
   }


   public final boolean closeTo(final GMatrix33D that) {
      return GMath.closeTo(_m00, that._m00) && //
             GMath.closeTo(_m01, that._m01) && //
             GMath.closeTo(_m02, that._m02) && // 

             GMath.closeTo(_m10, that._m10) && // 
             GMath.closeTo(_m11, that._m11) && // 
             GMath.closeTo(_m12, that._m12) && //  

             GMath.closeTo(_m20, that._m20) && // 
             GMath.closeTo(_m21, that._m21) && // 
             GMath.closeTo(_m22, that._m22);
   }


   protected abstract String descriptionString();


   @Override
   public final double get(final int i,
                           final int j) {
      switch (i) {
         case 0:
            switch (j) {
               case 0:
                  return _m00;
               case 1:
                  return _m01;
               case 2:
                  return _m02;
            }
         case 1:
            switch (j) {
               case 0:
                  return _m10;
               case 1:
                  return _m11;
               case 2:
                  return _m12;
            }
         case 2:
            switch (j) {
               case 0:
                  return _m20;
               case 1:
                  return _m21;
               case 2:
                  return _m22;
            }
      }

      throw new IndexOutOfBoundsException(i + " x " + j);
   }


   @Override
   public final int getColumnsCount() {
      return 3;
   }


   @Override
   public final int getRowsCount() {
      return 3;
   }


   @Override
   public final GMatrix33D inverted() {

      final double determinant = determinant();

      if (GMath.closeToZero(determinant)) {
         return null;
      }

      return adjoint().mul(1d / determinant);

   }


   @Override
   public GMatrix33D mul(final double scale) {
      if (GMath.closeTo(scale, 1)) {
         return this;
      }

      return createMatrix( //
               (_m00 * scale), (_m01 * scale), (_m02 * scale), //
               (_m10 * scale), (_m11 * scale), (_m12 * scale), //
               (_m20 * scale), (_m21 * scale), (_m22 * scale));
   }


   @Override
   public GMatrix33D negated() {
      return mul(-1);
   }


   public final void show() {
      show(System.out);
   }


   @Override
   public final void show(final PrintStream out) {
      out.println("[ " + beautify(_m00) + ", " + beautify(_m01) + ", " + beautify(_m02));
      out.println("  " + beautify(_m10) + ", " + beautify(_m11) + ", " + beautify(_m12));
      out.println("  " + beautify(_m20) + ", " + beautify(_m21) + ", " + beautify(_m22) + " ]" + descriptionString()
                  + " determinant=" + determinant());
   }


   @Override
   public GMatrix33D sub(final GMatrix33D that) {
      if (that.isZero()) {
         return this;
      }

      return createMatrix( //
               (_m00 - that._m00), (_m01 - that._m01), (_m02 - that._m02), //
               (_m10 - that._m10), (_m11 - that._m11), (_m12 - that._m12), //
               (_m20 - that._m20), (_m21 - that._m21), (_m22 - that._m22));
   }


   @Override
   public final String toString() {
      return "Matrix44" + descriptionString() + " [ " + "[" + _m00 + ", " + _m01 + ", " + _m02 + "], " + "[" + _m10 + ", " + _m11
             + ", " + _m12 + "], " + "[" + _m20 + ", " + _m21 + ", " + _m22 + "] ]";
   }


   @Override
   public final IVector3 apply(final IVector3 vec) {
      return vec.transformedBy(this);
   }


   @Override
   public final GMatrix33D transposed() {
      return createMatrix( //
               _m00, _m10, _m20, //
               _m01, _m11, _m21, //
               _m02, _m12, _m22);
   }


   @Override
   public Object clone() {
      return this;
   }


   public GMatrix33D adjoint() {

      // matrix cofactors
      final double c00 = _m11 * _m22 - _m12 * _m21;
      final double c01 = _m12 * _m20 - _m10 * _m22;
      final double c02 = _m10 * _m22 - _m11 * _m20;
      final double c10 = _m02 * _m21 - _m01 * _m22;
      final double c11 = _m00 * _m22 - _m02 * _m20;
      final double c12 = _m01 * _m20 - _m00 * _m21;
      final double c20 = _m01 * _m12 - _m02 * _m11;
      final double c21 = _m02 * _m10 - _m00 * _m12;
      final double c22 = _m00 * _m11 - _m01 * _m10;

      // return matrix with transposed cofactors
      return createMatrix( //
               c00, c10, c20, //
               c01, c11, c21, //
               c02, c12, c22);

   }


   //   public static void main(final String[] args) {
   //
   //      final GMatrix33D A = createMatrix(1d, 2d, 3d, 4d, 5d, 3d, 7d, 8d, 1d);
   //
   //      //final GMatrix33D I = createMatrix(1d, 0d, 0d, 0d, 1d, 0d, 0d, 0d, 1d);
   //      final GMatrix33D I = IDENTITY;
   //
   //      A.show();
   //
   //      I.show();
   //
   //      final GMatrix33D B = A.inverted();
   //
   //      if (B != null) {
   //         B.show();
   //      }
   //      else {
   //         System.out.println("La matriz no tiene inversa !");
   //      }
   //
   //      final GMatrix33D C = A.mul(B);
   //      C.show();
   //
   //      if (B != null) {
   //         final GMatrix33D D = B.mul(A);
   //         D.show();
   //      }
   //
   //      final GMatrix33D E = A.mul(I);
   //      E.show();
   //
   //   }

}
