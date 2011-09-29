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


public abstract class GMatrix44D
         implements
            ISquareMatrix<GMatrix44D, IVector3>,
            Cloneable {


   public static final GMatrix44D IDENTITY = new GMatrix44D.Identity();
   public static final GMatrix44D ZERO     = new GMatrix44D.Zero();


   private static final class Identity
            extends
               GMatrix44D {

      private Identity() {
         super(1, 0, 0, 0, //
               0, 1, 0, 0, //
               0, 0, 1, 0, //
               0, 0, 0, 1);
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
      public final GMatrix44D mul(final GMatrix44D that) {
         return that;
      }
   }


   private static final class Plain
            extends
               GMatrix44D {

      private Plain(final double m00,
                    final double m01,
                    final double m02,
                    final double m03,
                    final double m10,
                    final double m11,
                    final double m12,
                    final double m13,
                    final double m20,
                    final double m21,
                    final double m22,
                    final double m23,
                    final double m30,
                    final double m31,
                    final double m32,
                    final double m33) {
         super(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
      }


      @Override
      protected String descriptionString() {
         return "";
      }


      @Override
      public double determinant() {
         // from jMonkey FastMath.determinant();

         final double det01 = (_m20 * _m31) - (_m21 * _m30);
         final double det02 = (_m20 * _m32) - (_m22 * _m30);
         final double det03 = (_m20 * _m33) - (_m23 * _m30);
         final double det12 = (_m21 * _m32) - (_m22 * _m31);
         final double det13 = (_m21 * _m33) - (_m23 * _m31);
         final double det23 = (_m22 * _m33) - (_m23 * _m32);

         //         final double t0 = _m00 * (_m11 * det23 - _m12 * det13 + _m13 * det12);
         //         final double t1 = _m01 * (_m10 * det23 - _m12 * det03 + _m13 * det02);
         //         final double t2 = _m02 * (_m10 * det13 - _m11 * det03 + _m13 * det01);
         //         final double t3 = _m03 * (_m10 * det12 - _m11 * det02 + _m12 * det01);

         final double t0 = _m00 * GMath.sum(_m11 * det23, -_m12 * det13, _m13 * det12);
         final double t1 = _m01 * GMath.sum(_m10 * det23, -_m12 * det03, _m13 * det02);
         final double t2 = _m02 * GMath.sum(_m10 * det13, -_m11 * det03, _m13 * det01);
         final double t3 = _m03 * GMath.sum(_m10 * det12, -_m11 * det02, _m12 * det01);

         //      return (t0 - t1 + t2 - t3);
         return GMath.sum(t0, -t1, t2, -t3);
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
      public GMatrix44D mul(final GMatrix44D that) {
         if (that.isIdentity()) {
            return this;
         }
         if (that.isZero()) {
            return GMatrix44D.ZERO;
         }

         final double that00 = that._m00;
         final double that10 = that._m10;
         final double that20 = that._m20;
         final double that30 = that._m30;

         final double that01 = that._m01;
         final double that11 = that._m11;
         final double that21 = that._m21;
         final double that31 = that._m31;

         final double that02 = that._m02;
         final double that12 = that._m12;
         final double that22 = that._m22;
         final double that32 = that._m32;

         final double that03 = that._m03;
         final double that13 = that._m13;
         final double that23 = that._m23;
         final double that33 = that._m33;


         //         final double m00 = (_m00 * that00) + (_m01 * that10) + (_m02 * that20) + (_m03 * that30);
         //         final double m01 = (_m00 * that01) + (_m01 * that11) + (_m02 * that21) + (_m03 * that31);
         //         final double m02 = (_m00 * that02) + (_m01 * that12) + (_m02 * that22) + (_m03 * that32);
         //         final double m03 = (_m00 * that03) + (_m01 * that13) + (_m02 * that23) + (_m03 * that33);
         //
         //         final double m10 = (_m10 * that00) + (_m11 * that10) + (_m12 * that20) + (_m13 * that30);
         //         final double m11 = (_m10 * that01) + (_m11 * that11) + (_m12 * that21) + (_m13 * that31);
         //         final double m12 = (_m10 * that02) + (_m11 * that12) + (_m12 * that22) + (_m13 * that32);
         //         final double m13 = (_m10 * that03) + (_m11 * that13) + (_m12 * that23) + (_m13 * that33);
         //
         //         final double m20 = (_m20 * that00) + (_m21 * that10) + (_m22 * that20) + (_m23 * that30);
         //         final double m21 = (_m20 * that01) + (_m21 * that11) + (_m22 * that21) + (_m23 * that31);
         //         final double m22 = (_m20 * that02) + (_m21 * that12) + (_m22 * that22) + (_m23 * that32);
         //         final double m23 = (_m20 * that03) + (_m21 * that13) + (_m22 * that23) + (_m23 * that33);
         //
         //         final double m30 = (_m30 * that00) + (_m31 * that10) + (_m32 * that20) + (_m33 * that30);
         //         final double m31 = (_m30 * that01) + (_m31 * that11) + (_m32 * that21) + (_m33 * that31);
         //         final double m32 = (_m30 * that02) + (_m31 * that12) + (_m32 * that22) + (_m33 * that32);
         //         final double m33 = (_m30 * that03) + (_m31 * that13) + (_m32 * that23) + (_m33 * that33);

         final double m00 = GMath.sum((_m00 * that00), (_m01 * that10), (_m02 * that20), (_m03 * that30));
         final double m01 = GMath.sum((_m00 * that01), (_m01 * that11), (_m02 * that21), (_m03 * that31));
         final double m02 = GMath.sum((_m00 * that02), (_m01 * that12), (_m02 * that22), (_m03 * that32));
         final double m03 = GMath.sum((_m00 * that03), (_m01 * that13), (_m02 * that23), (_m03 * that33));

         final double m10 = GMath.sum((_m10 * that00), (_m11 * that10), (_m12 * that20), (_m13 * that30));
         final double m11 = GMath.sum((_m10 * that01), (_m11 * that11), (_m12 * that21), (_m13 * that31));
         final double m12 = GMath.sum((_m10 * that02), (_m11 * that12), (_m12 * that22), (_m13 * that32));
         final double m13 = GMath.sum((_m10 * that03), (_m11 * that13), (_m12 * that23), (_m13 * that33));

         final double m20 = GMath.sum((_m20 * that00), (_m21 * that10), (_m22 * that20), (_m23 * that30));
         final double m21 = GMath.sum((_m20 * that01), (_m21 * that11), (_m22 * that21), (_m23 * that31));
         final double m22 = GMath.sum((_m20 * that02), (_m21 * that12), (_m22 * that22), (_m23 * that32));
         final double m23 = GMath.sum((_m20 * that03), (_m21 * that13), (_m22 * that23), (_m23 * that33));

         final double m30 = GMath.sum((_m30 * that00), (_m31 * that10), (_m32 * that20), (_m33 * that30));
         final double m31 = GMath.sum((_m30 * that01), (_m31 * that11), (_m32 * that21), (_m33 * that31));
         final double m32 = GMath.sum((_m30 * that02), (_m31 * that12), (_m32 * that22), (_m33 * that32));
         final double m33 = GMath.sum((_m30 * that03), (_m31 * that13), (_m32 * that23), (_m33 * that33));

         return createMatrix( //
                  m00, m01, m02, m03, //
                  m10, m11, m12, m13, //
                  m20, m21, m22, m23, //
                  m30, m31, m32, m33);
      }


   }


   private static final class Zero
            extends
               GMatrix44D {

      private Zero() {
         super(0, 0, 0, 0, //
               0, 0, 0, 0, //
               0, 0, 0, 0, //
               0, 0, 0, 0);
      }


      @Override
      public final GMatrix44D add(final GMatrix44D that) {
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
      public GMatrix44D mul(final double scale) {
         return this;
      }


      @Override
      public GMatrix44D mul(final GMatrix44D that) {
         return this;
      }


      @Override
      public final GMatrix44D sub(final GMatrix44D that) {
         return that.negated();
      }


      @Override
      public final GMatrix44D negated() {
         return this;
      }
   }


   public static GMatrix44D createMatrix(final double m00,
                                         final double m01,
                                         final double m02,
                                         final double m03,
                                         final double m10,
                                         final double m11,
                                         final double m12,
                                         final double m13,
                                         final double m20,
                                         final double m21,
                                         final double m22,
                                         final double m23,
                                         final double m30,
                                         final double m31,
                                         final double m32,
                                         final double m33) {

      final boolean allButDiagonalIsZero = GMath.closeToZero( // the rest is all zeros
               m01, m02, m03, //
               m10, m12, m13, //
               m20, m21, m23, //
               m30, m31, m32);

      final boolean diagonalIsOne = GMath.closeToOne(m00, m11, m22, m33);
      if ((diagonalIsOne && allButDiagonalIsZero)) {
         return GMatrix44D.IDENTITY;
      }

      final boolean diagonalIsZero = GMath.closeToZero(m00, m11, m22, m33);
      if ((allButDiagonalIsZero && diagonalIsZero)) {
         return GMatrix44D.ZERO;
      }

      return new GMatrix44D.Plain( //
               m00, m01, m02, m03, //
               m10, m11, m12, m13, //
               m20, m21, m22, m23, //
               m30, m31, m32, m33);
   }


   public static GMatrix44D createRotationMatrix(final IVector2 axis,
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
      final double m03 = 0;

      final double m10 = xYM;
      final double m11 = y2 * oneMinusCos + cos;
      final double m12 = -xSin;
      final double m13 = 0;

      final double m20 = 0 - ySin;
      final double m21 = xSin;
      final double m22 = cos;
      final double m23 = 0;

      final double m30 = 0;
      final double m31 = 0;
      final double m32 = 0;
      final double m33 = 1;

      return createMatrix( //
               m00, m01, m02, m03, //
               m10, m11, m12, m13, //
               m20, m21, m22, m23, //
               m30, m31, m32, m33);

   }


   public static GMatrix44D createRotationMatrix(final IVector3 axis,
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
      final double m03 = 0;

      final double m10 = xYM + zSin;
      final double m11 = y2 * oneMinusCos + cos;
      final double m12 = yZM - xSin;
      final double m13 = 0;

      final double m20 = xZM - ySin;
      final double m21 = yZM + xSin;
      final double m22 = z2 * oneMinusCos + cos;
      final double m23 = 0;

      final double m30 = 0;
      final double m31 = 0;
      final double m32 = 0;
      final double m33 = 1;

      return createMatrix( //
               m00, m01, m02, m03, //
               m10, m11, m12, m13, //
               m20, m21, m22, m23, //
               m30, m31, m32, m33);
   }


   public static GMatrix44D createRotationMatrix(final IVector3 start,
                                                 final IVector3 end) {

      final IVector3 normStart = start.normalized();
      final IVector3 normEnd = end.normalized();


      final IVector3 v = normStart.cross(normEnd);

      final double e = normStart.dot(normEnd);
      final double f = Math.abs(e);

      // if "from" and "to" vectors are nearly parallel
      if (GMath.greaterOrEquals(f, 1)) {
         return GMatrix44D.IDENTITY;
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
      final double m03 = 0;

      final double m10 = hvxy + vz;
      final double m11 = e + h * vy * vy;
      final double m12 = hvyz - vx;
      final double m13 = 0;

      final double m20 = hvxz - vy;
      final double m21 = hvyz + vx;
      final double m22 = e + hvz * vz;
      final double m23 = 0;

      final double m30 = 0;
      final double m31 = 0;
      final double m32 = 0;
      final double m33 = 1;

      return createMatrix( //
               m00, m01, m02, m03, //
               m10, m11, m12, m13, //
               m20, m21, m22, m23, //
               m30, m31, m32, m33);

   }


   public static GMatrix44D createScaleMatrix(final double scale) {
      final double m00 = scale;
      final double m01 = 0;
      final double m02 = 0;
      final double m03 = 0;

      final double m10 = 0;
      final double m11 = scale;
      final double m12 = 0;
      final double m13 = 0;

      final double m20 = 0;
      final double m21 = 0;
      final double m22 = scale;
      final double m23 = 0;

      final double m30 = 0;
      final double m31 = 0;
      final double m32 = 0;
      final double m33 = 1;

      return createMatrix( //
               m00, m01, m02, m03, //
               m10, m11, m12, m13, //
               m20, m21, m22, m23, //
               m30, m31, m32, m33);

   }


   public static GMatrix44D createScaleMatrix(final IVector2 scale) {
      final double m00 = scale.x();
      final double m01 = 0;
      final double m02 = 0;
      final double m03 = 0;

      final double m10 = 0;
      final double m11 = scale.y();
      final double m12 = 0;
      final double m13 = 0;

      final double m20 = 0;
      final double m21 = 0;
      final double m22 = 1;
      final double m23 = 0;

      final double m30 = 0;
      final double m31 = 0;
      final double m32 = 0;
      final double m33 = 1;

      return createMatrix( //
               m00, m01, m02, m03, //
               m10, m11, m12, m13, //
               m20, m21, m22, m23, //
               m30, m31, m32, m33);

   }


   public static GMatrix44D createScaleMatrix(final IVector3 scale) {
      final double m00 = scale.x();
      final double m01 = 0;
      final double m02 = 0;
      final double m03 = 0;

      final double m10 = 0;
      final double m11 = scale.y();
      final double m12 = 0;
      final double m13 = 0;

      final double m20 = 0;
      final double m21 = 0;
      final double m22 = scale.z();
      final double m23 = 0;

      final double m30 = 0;
      final double m31 = 0;
      final double m32 = 0;
      final double m33 = 1;

      return createMatrix( //
               m00, m01, m02, m03, //
               m10, m11, m12, m13, //
               m20, m21, m22, m23, //
               m30, m31, m32, m33);

   }


   public static GMatrix44D createTranslationMatrix(final IVector2 translation) {
      return createMatrix( //
               1, 0, 0, translation.x(), //
               0, 1, 0, translation.y(), //
               0, 0, 1, 0, //
               0, 0, 0, 1);
   }


   public static GMatrix44D createTranslationMatrix(final IVector3 translation) {
      return createMatrix(//
               1, 0, 0, translation.x(), //
               0, 1, 0, translation.y(), //
               0, 0, 1, translation.z(), //
               0, 0, 0, 1);
   }


   public final double _m00;
   public final double _m01;
   public final double _m02;
   public final double _m03;

   public final double _m10;
   public final double _m11;
   public final double _m12;
   public final double _m13;

   public final double _m20;
   public final double _m21;
   public final double _m22;
   public final double _m23;

   public final double _m30;
   public final double _m31;
   public final double _m32;
   public final double _m33;


   private GMatrix44D(final double m00,
                      final double m01,
                      final double m02,
                      final double m03,
                      final double m10,
                      final double m11,
                      final double m12,
                      final double m13,
                      final double m20,
                      final double m21,
                      final double m22,
                      final double m23,
                      final double m30,
                      final double m31,
                      final double m32,
                      final double m33) {
      _m00 = m00;
      _m01 = m01;
      _m02 = m02;
      _m03 = m03;

      _m10 = m10;
      _m11 = m11;
      _m12 = m12;
      _m13 = m13;

      _m20 = m20;
      _m21 = m21;
      _m22 = m22;
      _m23 = m23;

      _m30 = m30;
      _m31 = m31;
      _m32 = m32;
      _m33 = m33;
   }


   @Override
   public GMatrix44D add(final GMatrix44D that) {
      if (that.isZero()) {
         return this;
      }

      return createMatrix( //
               (_m00 + that._m00), (_m01 + that._m01), (_m02 + that._m02), (_m03 + that._m03), //
               (_m10 + that._m10), (_m11 + that._m11), (_m12 + that._m12), (_m13 + that._m13), //
               (_m20 + that._m20), (_m21 + that._m21), (_m22 + that._m22), (_m23 + that._m23), //
               (_m30 + that._m30), (_m31 + that._m31), (_m32 + that._m32), (_m33 + that._m33));
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


   public final boolean closeTo(final GMatrix44D that) {
      return GMath.closeTo(_m00, that._m00) && //
             GMath.closeTo(_m01, that._m01) && //
             GMath.closeTo(_m02, that._m02) && // 
             GMath.closeTo(_m03, that._m03) && // 

             GMath.closeTo(_m10, that._m10) && // 
             GMath.closeTo(_m11, that._m11) && // 
             GMath.closeTo(_m12, that._m12) && // 
             GMath.closeTo(_m13, that._m13) && // 

             GMath.closeTo(_m20, that._m20) && // 
             GMath.closeTo(_m21, that._m21) && // 
             GMath.closeTo(_m22, that._m22) && // 
             GMath.closeTo(_m23, that._m23) && // 

             GMath.closeTo(_m30, that._m30) && // 
             GMath.closeTo(_m31, that._m31) && // 
             GMath.closeTo(_m32, that._m32) && // 
             GMath.closeTo(_m33, that._m33);
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
               case 3:
                  return _m03;
            }
         case 1:
            switch (j) {
               case 0:
                  return _m10;
               case 1:
                  return _m11;
               case 2:
                  return _m12;
               case 3:
                  return _m13;
            }
         case 2:
            switch (j) {
               case 0:
                  return _m20;
               case 1:
                  return _m21;
               case 2:
                  return _m22;
               case 3:
                  return _m23;
            }
         case 3:
            switch (j) {
               case 0:
                  return _m30;
               case 1:
                  return _m31;
               case 2:
                  return _m32;
               case 3:
                  return _m33;
            }
      }

      throw new IndexOutOfBoundsException(i + " x " + j);
   }


   @Override
   public final int getColumnsCount() {
      return 4;
   }


   @Override
   public final int getRowsCount() {
      return 4;
   }


   @Override
   public final GMatrix44D inverted() {
      final double a0 = _m00 * _m11 - _m01 * _m10;
      final double a1 = _m00 * _m12 - _m02 * _m10;
      final double a2 = _m00 * _m13 - _m03 * _m10;
      final double a3 = _m01 * _m12 - _m02 * _m11;
      final double a4 = _m01 * _m13 - _m03 * _m11;
      final double a5 = _m02 * _m13 - _m03 * _m12;

      final double b0 = _m20 * _m31 - _m21 * _m30;
      final double b1 = _m20 * _m32 - _m22 * _m30;
      final double b2 = _m20 * _m33 - _m23 * _m30;
      final double b3 = _m21 * _m32 - _m22 * _m31;
      final double b4 = _m21 * _m33 - _m23 * _m31;
      final double b5 = _m22 * _m33 - _m23 * _m32;

      final double determinant = a0 * b5 - a1 * b4 + a2 * b3 + a3 * b2 - a4 * b1 + a5 * b0;

      if (GMath.closeToZero(determinant)) {
         //         final int TODO;
         //         final double det = determinant();
         //         if (det != determinant) {
         //            System.out.println("WARNING: inconsistency between determinants (" + determinant + " -> " + det + ")");
         //         }

         return null;
      }

      final double m00 = (+_m11 * b5 - _m12 * b4 + _m13 * b3) / determinant;
      final double m10 = (-_m10 * b5 + _m12 * b2 - _m13 * b1) / determinant;
      final double m20 = (+_m10 * b4 - _m11 * b2 + _m13 * b0) / determinant;
      final double m30 = (-_m10 * b3 + _m11 * b1 - _m12 * b0) / determinant;
      final double m01 = (-_m01 * b5 + _m02 * b4 - _m03 * b3) / determinant;
      final double m11 = (+_m00 * b5 - _m02 * b2 + _m03 * b1) / determinant;
      final double m21 = (-_m00 * b4 + _m01 * b2 - _m03 * b0) / determinant;
      final double m31 = (+_m00 * b3 - _m01 * b1 + _m02 * b0) / determinant;
      final double m02 = (+_m31 * a5 - _m32 * a4 + _m33 * a3) / determinant;
      final double m12 = (-_m30 * a5 + _m32 * a2 - _m33 * a1) / determinant;
      final double m22 = (+_m30 * a4 - _m31 * a2 + _m33 * a0) / determinant;
      final double m32 = (-_m30 * a3 + _m31 * a1 - _m32 * a0) / determinant;
      final double m03 = (-_m21 * a5 + _m22 * a4 - _m23 * a3) / determinant;
      final double m13 = (+_m20 * a5 - _m22 * a2 + _m23 * a1) / determinant;
      final double m23 = (-_m20 * a4 + _m21 * a2 - _m23 * a0) / determinant;
      final double m33 = (+_m20 * a3 - _m21 * a1 + _m22 * a0) / determinant;

      return createMatrix( //
               m00, m01, m02, m03, //
               m10, m11, m12, m13, //
               m20, m21, m22, m23, //
               m30, m31, m32, m33);
   }


   @Override
   public GMatrix44D mul(final double scale) {
      if (GMath.closeTo(scale, 1)) {
         return this;
      }

      return createMatrix( //
               (_m00 * scale), (_m01 * scale), (_m02 * scale), (_m03 * scale), //
               (_m10 * scale), (_m11 * scale), (_m12 * scale), (_m13 * scale), //
               (_m20 * scale), (_m21 * scale), (_m22 * scale), (_m23 * scale), //
               (_m30 * scale), (_m31 * scale), (_m32 * scale), (_m33 * scale));
   }


   @Override
   public GMatrix44D negated() {
      return mul(-1);
   }


   public final void show() {
      show(System.out);
   }


   @Override
   public final void show(final PrintStream out) {
      out.println("[ " + beautify(_m00) + ", " + beautify(_m01) + ", " + beautify(_m02) + ", " + beautify(_m03));
      out.println("  " + beautify(_m10) + ", " + beautify(_m11) + ", " + beautify(_m12) + ", " + beautify(_m13));
      out.println("  " + beautify(_m20) + ", " + beautify(_m21) + ", " + beautify(_m22) + ", " + beautify(_m23));
      out.println("  " + beautify(_m30) + ", " + beautify(_m31) + ", " + beautify(_m32) + ", " + beautify(_m33) + " ]"
                  + descriptionString() + " determinant=" + determinant());
   }


   @Override
   public GMatrix44D sub(final GMatrix44D that) {
      if (that.isZero()) {
         return this;
      }

      return createMatrix( //
               (_m00 - that._m00), (_m01 - that._m01), (_m02 - that._m02), (_m03 - that._m03), //
               (_m10 - that._m10), (_m11 - that._m11), (_m12 - that._m12), (_m13 - that._m13), //
               (_m20 - that._m20), (_m21 - that._m21), (_m22 - that._m22), (_m23 - that._m23), //
               (_m30 - that._m30), (_m31 - that._m31), (_m32 - that._m32), (_m33 - that._m33));
   }


   @Override
   public final String toString() {
      return "Matrix44" + descriptionString() + " [ " + "[" + _m00 + ", " + _m01 + ", " + _m02 + ", " + _m03 + "], " + "[" + _m10
             + ", " + _m11 + ", " + _m12 + ", " + _m13 + "], " + "[" + _m20 + ", " + _m21 + ", " + _m22 + ", " + _m23 + "], "
             + "[" + _m30 + ", " + _m31 + ", " + _m32 + ", " + _m33 + "] ]";
   }


   @Override
   public final IVector3 apply(final IVector3 vec) {
      return vec.transformedBy(this);
   }


   @Override
   public final GMatrix44D transposed() {
      return createMatrix( //
               _m00, _m10, _m20, _m30, //
               _m01, _m11, _m21, _m31, //
               _m02, _m12, _m22, _m32, //
               _m03, _m13, _m23, _m33);
   }


   @Override
   public Object clone() {
      return this;
   }


   public GMatrix44D adjoint() {
      final double a0 = _m00 * _m11 - _m01 * _m10;
      final double a1 = _m00 * _m12 - _m02 * _m10;
      final double a2 = _m00 * _m13 - _m03 * _m10;
      final double a3 = _m01 * _m12 - _m02 * _m11;
      final double a4 = _m01 * _m13 - _m03 * _m11;
      final double a5 = _m02 * _m13 - _m03 * _m12;
      final double b0 = _m20 * _m31 - _m21 * _m30;
      final double b1 = _m20 * _m32 - _m22 * _m30;
      final double b2 = _m20 * _m33 - _m23 * _m30;
      final double b3 = _m21 * _m32 - _m22 * _m31;
      final double b4 = _m21 * _m33 - _m23 * _m31;
      final double b5 = _m22 * _m33 - _m23 * _m32;

      final double m00 = +_m11 * b5 - _m12 * b4 + _m13 * b3;
      final double m10 = -_m10 * b5 + _m12 * b2 - _m13 * b1;
      final double m20 = +_m10 * b4 - _m11 * b2 + _m13 * b0;
      final double m30 = -_m10 * b3 + _m11 * b1 - _m12 * b0;
      final double m01 = -_m01 * b5 + _m02 * b4 - _m03 * b3;
      final double m11 = +_m00 * b5 - _m02 * b2 + _m03 * b1;
      final double m21 = -_m00 * b4 + _m01 * b2 - _m03 * b0;
      final double m31 = +_m00 * b3 - _m01 * b1 + _m02 * b0;
      final double m02 = +_m31 * a5 - _m32 * a4 + _m33 * a3;
      final double m12 = -_m30 * a5 + _m32 * a2 - _m33 * a1;
      final double m22 = +_m30 * a4 - _m31 * a2 + _m33 * a0;
      final double m32 = -_m30 * a3 + _m31 * a1 - _m32 * a0;
      final double m03 = -_m21 * a5 + _m22 * a4 - _m23 * a3;
      final double m13 = +_m20 * a5 - _m22 * a2 + _m23 * a1;
      final double m23 = -_m20 * a4 + _m21 * a2 - _m23 * a0;
      final double m33 = +_m20 * a3 - _m21 * a1 + _m22 * a0;

      return createMatrix( //
               m00, m01, m02, m03, //
               m10, m11, m12, m13, //
               m20, m21, m22, m23, //
               m30, m31, m32, m33);
   }


   public GMatrix44D transpose() {

      return createMatrix( //
               _m00, _m10, _m20, _m30, //
               _m01, _m11, _m21, _m31, //
               _m02, _m12, _m22, _m32, //
               _m03, _m13, _m23, _m33);

   }

}
