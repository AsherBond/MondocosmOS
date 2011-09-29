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


package es.igosoftware.euclid.test;

import junit.framework.Assert;

import org.junit.Test;

import es.igosoftware.euclid.GAngle;
import es.igosoftware.euclid.matrix.GMatrix44D;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.GMath;


public class GMatrixTest {

   private static void assertCloseTo(final String description,
                                     final GMatrix44D expected,
                                     final GMatrix44D current) {
      if (expected.closeTo(current)) {
         return;
      }

      System.out.println("Expected:");
      System.out.println("---------");
      expected.show();

      System.out.println();

      System.out.println("Current:");
      System.out.println("--------");
      current.show();

      Assert.fail(description);
   }


   private GMatrix44D createRotationMatrix() {
      return GMatrix44D.createRotationMatrix(GVector3D.X_UP, GAngle.fromDegrees(30));
   }


   private GMatrix44D createScaleMatrix() {
      return GMatrix44D.createScaleMatrix(2);
   }


   private GMatrix44D createTranslationMatrix() {
      return GMatrix44D.createTranslationMatrix(new GVector3D(1, 10, 100));
   }


   @Test
   public void testBasics() {
      final GMatrix44D a = createScaleMatrix();
      Assert.assertFalse("IDENTITY", a.isZero());
      Assert.assertFalse("IDENTITY", a.isIdentity());
   }


   @Test
   public void testDeterminant() {
      final GMatrix44D a = createScaleMatrix();
      final GMatrix44D b = createTranslationMatrix();
      final GMatrix44D c = createRotationMatrix();

      testDeterminantProperties(a);
      testDeterminantProperties(b);
      testDeterminantProperties(c);

      testDeterminantProperties(a, b);
      testDeterminantProperties(a, c);
      testDeterminantProperties(b, c);
   }


   private void testDeterminantProperties(final GMatrix44D a) {
      final GMatrix44D transposedA = a.transposed();

      Assert.assertEquals("det(a) = det(aT)", a.determinant(), transposedA.determinant());

      final GMatrix44D invertedA = a.inverted();

      Assert.assertTrue("det(a-1) = 1/det(a)", GMath.closeTo(invertedA.determinant(), 1 / a.determinant()));
   }


   private void testDeterminantProperties(final GMatrix44D a,
                                          final GMatrix44D b) {
      final GMatrix44D ab = a.mul(b);
      final GMatrix44D ba = b.mul(a);

      Assert.assertEquals(ab.determinant(), a.determinant() * b.determinant());
      Assert.assertEquals(ba.determinant(), a.determinant() * b.determinant());
   }


   @Test
   public void testIdentityBasics() {
      final GMatrix44D identity = GMatrix44D.IDENTITY;


      Assert.assertFalse("IDENTITY", identity.isZero());
      Assert.assertTrue("IDENTITY", identity.isIdentity());


      Assert.assertEquals("identity determinant", 1.0, identity.determinant());


      for (int i = 0; i < identity.getColumnsCount(); i++) {
         for (int j = 0; j < identity.getRowsCount(); j++) {
            final double expected = i == j ? 1.0 : 0.0;
            Assert.assertEquals(expected, identity.get(i, j));
         }
      }

   }


   @Test
   public void testInverse() {
      testInverseProperties(createScaleMatrix());
      testInverseProperties(createTranslationMatrix());
      testInverseProperties(createScaleMatrix());
   }


   private void testInverseProperties(final GMatrix44D a) {
      final GMatrix44D invertedA = a.inverted();
      Assert.assertTrue("inverted", a.mul(invertedA).isIdentity());
      Assert.assertTrue("inverted", invertedA.mul(a).isIdentity());
   }


   @Test
   public void testMul() {
      final GMatrix44D a = createScaleMatrix();
      final GMatrix44D b = createTranslationMatrix();
      final GMatrix44D c = createRotationMatrix();

      testMulIdentityProperties(GMatrix44D.IDENTITY);
      testMulIdentityProperties(a);
      testMulIdentityProperties(b);
      testMulIdentityProperties(c);

      final GMatrix44D ab_c = a.mul(b).mul(c);
      final GMatrix44D a_bc = a.mul(b.mul(c));

      assertCloseTo("associativity: (ab)c = a(bc)", ab_c, a_bc);

      final GMatrix44D zero = GMatrix44D.ZERO;
      Assert.assertEquals("zero", zero.mul(1), zero);
   }


   private void testMulIdentityProperties(final GMatrix44D a) {
      Assert.assertSame("aI = a", a.mul(GMatrix44D.IDENTITY), a);
      Assert.assertSame("Ia = a", GMatrix44D.IDENTITY.mul(a), a);

      Assert.assertSame("a*1 = a", a.mul(1), a);


      Assert.assertSame("a*zero = zero", a.mul(GMatrix44D.ZERO), GMatrix44D.ZERO);
      Assert.assertSame("zero*a = zero", GMatrix44D.ZERO.mul(a), GMatrix44D.ZERO);

   }


   @Test
   public void testNegate() {
      testNegateProperties(createScaleMatrix());
      testNegateProperties(createTranslationMatrix());
      testNegateProperties(createScaleMatrix());
   }


   private void testNegateProperties(final GMatrix44D a) {
      final GMatrix44D negatedNegatedA = a.negated().negated();

      assertCloseTo("a = --a", a, negatedNegatedA);
   }


   @Test
   public void testSum() {
      final GMatrix44D a = createScaleMatrix();
      final GMatrix44D b = createTranslationMatrix();
      final GMatrix44D c = createRotationMatrix();

      testSumWithZero(a);
      testSumWithZero(b);
      testSumWithZero(c);

      testSumProperties(a, b);
      testSumProperties(a, c);
      testSumProperties(b, c);

      testSumProperties(a, b, c);
      testSumProperties(a, b, GMatrix44D.IDENTITY);
      testSumProperties(a, c, GMatrix44D.IDENTITY);
      testSumProperties(b, c, GMatrix44D.IDENTITY);
   }


   private void testSumProperties(final GMatrix44D a,
                                  final GMatrix44D b) {
      final GMatrix44D aPlusB = a.add(b);
      final GMatrix44D bPlusA = b.add(a);
      assertCloseTo("commutativity: a+b = b+a", aPlusB, bPlusA);


      final GMatrix44D aMinusB = a.sub(b);
      final GMatrix44D aPlusNegatedB = a.add(b.negated());
      assertCloseTo("a-b = a+(-b)", aMinusB, aPlusNegatedB);
   }


   private void testSumProperties(final GMatrix44D a,
                                  final GMatrix44D b,
                                  final GMatrix44D c) {
      final GMatrix44D ab_c = a.add(b).add(c);
      final GMatrix44D a_bc = a.add(b.add(c));
      assertCloseTo("ab(c) = a(bc)", ab_c, a_bc);

      final GMatrix44D ac_Plus_bc = a.mul(c).add(b.mul(c));
      final GMatrix44D aPlusB_c = a.add(b).mul(c);
      assertCloseTo("distributivity: ac + bc = (a+b)c", ac_Plus_bc, aPlusB_c);
   }


   private void testSumWithZero(final GMatrix44D a) {
      Assert.assertSame("a+0 = a", a, a.add(GMatrix44D.ZERO));
      Assert.assertSame("0+a = a", a, GMatrix44D.ZERO.add(a));

      Assert.assertSame("a-0 = a", a, a.sub(GMatrix44D.ZERO));
      assertCloseTo("0-a = -a", a.negated(), GMatrix44D.ZERO.sub(a));
   }


   @Test
   public void testTranslation() {
      final GMatrix44D translation = GMatrix44D.createTranslationMatrix(new GVector3D(1, 10, 100));
      final IVector3 vector3 = new GVector3D(2, 20, 200);

      final IVector3 translated3 = translation.apply(vector3);
      Assert.assertTrue(translated3.closeTo(new GVector3D(3, 30, 300)));


      //      final IVector2 vector2 = new GVector2D(2, 20);
      //
      //      final IVector2 translated2 = translation.transform(vector2);
      //      Assert.assertTrue(translated2.closeTo(new GVector2D(3, 30)));
   }


   @Test
   public void testTranslationMul() {
      final GMatrix44D a = GMatrix44D.createTranslationMatrix(new GVector3D(1, 10, 100));
      final GMatrix44D b = GMatrix44D.createTranslationMatrix(new GVector3D(2, 20, 200));
      final GMatrix44D c = GMatrix44D.createTranslationMatrix(new GVector3D(3, 30, 300));

      assertCloseTo("", c, a.mul(b));
      assertCloseTo("", c, b.mul(a));
   }


   @Test
   public void testTranspose() {
      final GMatrix44D a = createScaleMatrix();
      final GMatrix44D b = createTranslationMatrix();
      final GMatrix44D c = createRotationMatrix();

      testTransposeProperties(a);
      testTransposeProperties(b);
      testTransposeProperties(c);

      testTransposeProperties(a, b);
      testTransposeProperties(a, c);
      testTransposeProperties(b, c);
   }


   private void testTransposeProperties(final GMatrix44D a) {
      final GMatrix44D transposedA = a.transposed();

      Assert.assertEquals("det(a) = det(aT)", a.determinant(), transposedA.determinant());

      final GMatrix44D transposedTransposedA = a.transposed().transposed();
      assertCloseTo("a = aTT", a, transposedTransposedA);

      final double n = 10;
      final GMatrix44D n_ATransposed = a.mul(n).transposed();
      final GMatrix44D aTransposed_n = a.transposed().mul(n);
      assertCloseTo("(n*A)T = n*(AT)", n_ATransposed, aTransposed_n);
   }


   private void testTransposeProperties(final GMatrix44D a,
                                        final GMatrix44D b) {
      final GMatrix44D aPlusB_T = a.add(b).transposed();
      final GMatrix44D aT_plus_bT = a.transposed().add(b.transposed());

      assertCloseTo("(a+b)T = aT + bT", aPlusB_T, aT_plus_bT);

   }


   @Test
   public void testZeroBasics() {

      Assert.assertEquals("", GMatrix44D.ZERO, GMatrix44D.createMatrix(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));

      final GMatrix44D zero = GMatrix44D.ZERO;

      Assert.assertTrue("ZERO", zero.isZero());
      Assert.assertFalse("ZERO", zero.isIdentity());


      Assert.assertEquals("zero determinant", 0.0, zero.determinant());

      Assert.assertSame("-zero = zero", zero, zero.negated());

      for (int i = 0; i < zero.getColumnsCount(); i++) {
         for (int j = 0; j < zero.getRowsCount(); j++) {
            Assert.assertEquals(0.0, zero.get(i, j));
         }
      }
   }

}
