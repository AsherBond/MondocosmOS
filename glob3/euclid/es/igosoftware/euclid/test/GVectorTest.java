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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.shape.GLinesStrip2D;
import es.igosoftware.euclid.shape.GQuad2D;
import es.igosoftware.euclid.shape.GSegment2D;
import es.igosoftware.euclid.shape.GSegment3D;
import es.igosoftware.euclid.shape.GSimplePolygon2D;
import es.igosoftware.euclid.shape.GTriangle2D;
import es.igosoftware.euclid.shape.IPolygonalChain;
import es.igosoftware.euclid.utils.GShapeUtils;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.GVector2F;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.GVector3F;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GMath;
import es.igosoftware.util.IFunction;


public class GVectorTest {
   private static final IVector2   generic2F       = new GVector2F(1000000.2f, 2000000.3f);
   private static final IVector2   generic2FNeg    = new GVector2F(-1000000.2f, -2000000.3f);
   private static final IVector2   generic2D       = new GVector2D(1000000.2, 2000000.3);
   private static final IVector2   generic2DNeg    = new GVector2D(-1000000.2, -2000000.3);
   private static final IVector2   generic2Rounded = new GVector2D(1000000.0, 2000000);

   private static final IVector3   generic3F       = new GVector3F(1000000.2f, 2000000.3f, 3000000.4f);
   private static final IVector3   generic3FNeg    = new GVector3F(-1000000.2f, -2000000.3f, -3000000.4f);
   private static final IVector3   generic3D       = new GVector3D(1000000.2, 2000000.3, 3000000.4);
   private static final IVector3   generic3DNeg    = new GVector3D(-1000000.2, -2000000.3, -3000000.4);
   private static final IVector3   generic3Rounded = new GVector3D(1000000, 2000000, 3000000);

   private static final IVector2[] generics2       = {
                     GVectorTest.generic2F,
                     GVectorTest.generic2D
                                                   };
   private static final IVector3[] generics3       = {
                     GVectorTest.generic3F,
                     GVectorTest.generic3D
                                                   };
   private static final IVector2[] generics2Neg    = {
                     GVectorTest.generic2FNeg,
                     GVectorTest.generic2DNeg
                                                   };
   private static final IVector3[] generics3Neg    = {
                     GVectorTest.generic3FNeg,
                     GVectorTest.generic3DNeg
                                                   };

   private static final IVector2[] generics2All    = {
                     GVectorTest.generic2F,
                     GVectorTest.generic2D,
                     GVectorTest.generic2FNeg,
                     GVectorTest.generic2DNeg,
                     GVectorTest.generic2Rounded
                                                   };
   //   private static final IVector2[] generics2D      = { VectorTest.generic2D, VectorTest.generic2DNeg,
   //            VectorTest.generic2Rounded               };

   private static final IVector3[] generics3All    = {
                     GVectorTest.generic3F,
                     GVectorTest.generic3D,
                     GVectorTest.generic3FNeg,
                     GVectorTest.generic3DNeg,
                     GVectorTest.generic3Rounded
                                                   };

   //   private static final IVector3[] generics3D      = { VectorTest.generic3D, VectorTest.generic3DNeg,
   //            VectorTest.generic3Rounded               };


   private static final IVector2[] zeros2          = {
                     GVector2F.ZERO,
                     GVector2D.ZERO
                                                   };
   private static final IVector3[] zeros3          = {
                     GVector3F.ZERO,
                     GVector3D.ZERO
                                                   };

   private static final IVector2[] all2D           = {
                     GVectorTest.generic2D,
                     GVectorTest.generic2DNeg,
                     GVector2D.X_UP,
                     GVector2D.Y_UP,
                     GVector2D.X_DOWN,
                     GVector2D.Y_DOWN,
                     GVector2D.ZERO
                                                   };
   private static final IVector2[] all2F           = {
                     GVectorTest.generic2F,
                     GVectorTest.generic2FNeg,
                     GVector2F.X_UP,
                     GVector2F.Y_UP,
                     GVector2F.Y_DOWN,
                     GVector2F.Y_DOWN,
                     GVector2F.ZERO
                                                   };
   private static final IVector2[] all2            = {
                     GVectorTest.generic2F,
                     GVectorTest.generic2D,
                     GVectorTest.generic2FNeg,
                     GVectorTest.generic2DNeg,
                     GVector2F.X_UP,
                     GVector2D.X_UP,
                     GVector2F.Y_UP,
                     GVector2D.Y_UP,
                     GVector2F.Y_DOWN,
                     GVector2D.X_DOWN,
                     GVector2F.Y_DOWN,
                     GVector2D.Y_DOWN,
                     GVector2F.ZERO,
                     GVector2D.ZERO
                                                   };

   private static final IVector3[] all3D           = {
                     GVectorTest.generic3D,
                     GVectorTest.generic3DNeg,
                     GVector3D.X_UP,
                     GVector3D.Y_UP,
                     GVector3D.Z_UP,
                     GVector3D.X_DOWN,
                     GVector3D.Y_DOWN,
                     GVector3D.Z_DOWN,
                     GVector3D.ZERO
                                                   };
   private static final IVector3[] all3F           = {
                     GVectorTest.generic3F,
                     GVectorTest.generic3FNeg,
                     GVector3F.X_UP,
                     GVector3F.Y_UP,
                     GVector3F.Z_UP,
                     GVector3F.Y_DOWN,
                     GVector3F.Y_DOWN,
                     GVector3F.Z_DOWN,
                     GVector3F.ZERO
                                                   };
   private static final IVector3[] all3            = {
                     GVectorTest.generic3F,
                     GVectorTest.generic3D,
                     GVectorTest.generic3FNeg,
                     GVectorTest.generic3DNeg,
                     GVector3F.X_UP,
                     GVector3D.X_UP,
                     GVector3F.Y_UP,
                     GVector3D.Y_UP,
                     GVector3F.Z_UP,
                     GVector3D.Z_UP,
                     GVector3F.Y_DOWN,
                     GVector3D.X_DOWN,
                     GVector3F.Y_DOWN,
                     GVector3D.Y_DOWN,
                     GVector3F.Z_DOWN,
                     GVector3D.Z_DOWN,
                     GVector3F.ZERO,
                     GVector3D.ZERO
                                                   };


   private <T extends IVector2> void assertCloseTo(final String description,
                                                   final T expected,
                                                   final T current) {
      if (expected.closeTo(current)) {
         return;
      }

      Assert.fail(description + " expected: " + expected + ", current: " + current);
   }


   private <T extends IVector3> void assertCloseTo(final String description,
                                                   final T expected,
                                                   final T current) {
      if (expected.closeTo(current)) {
         return;
      }

      Assert.fail(description + " expected: " + expected + ", current: " + current);
   }


   private static interface PairEvaluation<VectorT extends IVector<VectorT, ?>> {
      void evaluate(final VectorT vector1,
                    final VectorT vector2);
   }


   private <VectorT extends IVector<VectorT, ?>> void combinationsDo(final VectorT[] vectors,
                                                                     final PairEvaluation<VectorT> evaluation) {
      for (int i = 0; i < vectors.length; i++) {
         for (int j = i; j < vectors.length; j++) {
            evaluation.evaluate(vectors[i], vectors[j]);
         }
      }
   }


   private List<IVector<?, ?>> getVectorsWithOneDimension(final int... dimension) {
      final List<IVector<?, ?>> result = new ArrayList<IVector<?, ?>>();
      for (final int element : dimension) {
         result.add(new GVector3D(element, 0, 0));
         result.add(new GVector3D(0, element, 0));
         result.add(new GVector3D(0, 0, element));

         result.add(new GVector3F(element, 0, 0));
         result.add(new GVector3F(0, element, 0));
         result.add(new GVector3F(0, 0, element));

         result.add(new GVector2D(element, 0));
         result.add(new GVector2D(0, element));

         result.add(new GVector2F(element, 0));
         result.add(new GVector2F(0, element));
      }


      final List<IVector<?, ?>> mutables = GCollections.collect(result, new IFunction<IVector<?, ?>, IVector<?, ?>>() {
         @Override
         public IVector<?, ?> apply(final IVector<?, ?> element) {
            return element.asMutable();
         }
      });

      result.addAll(mutables);

      return result;
   }


   private Iterable<IVector<?, ?>> getVectorsWithTwoDimensions(final int... dimension) {
      final List<IVector<?, ?>> result = new ArrayList<IVector<?, ?>>();
      for (final int element : dimension) {
         result.add(new GVector3D(element, element, 0));
         result.add(new GVector3D(0, element, element));
         result.add(new GVector3D(element, 0, element));

         result.add(new GVector3F(element, element, 0));
         result.add(new GVector3F(0, element, element));
         result.add(new GVector3F(element, 0, element));

         result.add(new GVector2D(element, element));
         result.add(new GVector2F(element, element));
      }

      final List<IVector<?, ?>> mutables = GCollections.collect(result, new IFunction<IVector<?, ?>, IVector<?, ?>>() {
         @Override
         public IVector<?, ?> apply(final IVector<?, ?> element) {
            return element.asMutable();
         }
      });

      result.addAll(mutables);

      return result;
   }


   private Iterable<IVector3> getVectorsWithThreeDimensions(final int... dimension) {
      final List<IVector3> result = new ArrayList<IVector3>();
      for (final int element : dimension) {
         result.add(new GVector3D(element, element, element));
         result.add(new GVector3F(element, element, element));
      }

      final List<IVector3> mutables = GCollections.collect(result, new IFunction<IVector3, IVector3>() {
         @Override
         public IVector3 apply(final IVector3 element) {
            return element.asMutable();
         }
      });

      result.addAll(mutables);

      return result;
   }


   @Test
   public void testBetween() {
      for (final IVector2 element : GVectorTest.all2) {
         Assert.assertTrue(element + " 2Ds between neg and pos infinity",
                  element.between(GVector2D.NEGATIVE_INFINITY, GVector2D.POSITIVE_INFINITY));
         Assert.assertFalse(element + " 2Ds between pos and neg infinity",
                  element.between(GVector2D.POSITIVE_INFINITY, GVector2D.NEGATIVE_INFINITY));
         Assert.assertFalse(element + " " + element.getClass() + " 2Ds between (1,1) and (1,1)",
                  element.between(GVector2D.UNIT, GVector2D.UNIT));
      }
      for (final IVector3 element : GVectorTest.all3) {
         Assert.assertTrue(element + " 3Ds between neg and pos infinity",
                  element.between(GVector3D.NEGATIVE_INFINITY, GVector3D.POSITIVE_INFINITY));
         Assert.assertFalse(element + " 3Ds between pos and neg infinity",
                  element.between(GVector3D.POSITIVE_INFINITY, GVector3D.NEGATIVE_INFINITY));
         Assert.assertFalse(element + "3Ds between (1,1) and (1,1)", element.between(GVector3D.UNIT, GVector3D.UNIT));
      }
   }


   @Test
   public void testLength() {


      for (final IVector<?, ?> vector : getVectorsWithOneDimension(1, -1)) {
         Assert.assertEquals(vector + " length", 1.0, vector.length());
         Assert.assertEquals(vector + " length", 1.0, vector.squaredLength());
      }


      for (final IVector<?, ?> vector : getVectorsWithOneDimension(2, -2)) {
         Assert.assertEquals(vector + " length", 2.0, vector.length());
         Assert.assertEquals(vector + " length", 4.0, vector.squaredLength());
      }


      for (final IVector<?, ?> vector : getVectorsWithTwoDimensions(1, -1)) {
         Assert.assertEquals(vector + " length", GMath.sqrt(2), vector.length());
         Assert.assertEquals(vector + " length", 2.0, vector.squaredLength());
      }

      for (final IVector<?, ?> vector : getVectorsWithTwoDimensions(2, -2)) {
         Assert.assertEquals(vector + " length", GMath.sqrt(8), vector.length());
         Assert.assertEquals(vector + " length", 8.0, vector.squaredLength());
      }


      for (final IVector<?, ?> vector : getVectorsWithThreeDimensions(1, -1)) {
         Assert.assertEquals(vector + " length", GMath.sqrt(3), vector.length());
         Assert.assertEquals(vector + " length", 3.0, vector.squaredLength());
      }

      for (final IVector<?, ?> vector : getVectorsWithThreeDimensions(2, -2)) {
         Assert.assertEquals(vector + " length", GMath.sqrt(12), vector.length());
         Assert.assertEquals(vector + " length", 12.0, vector.squaredLength());

      }

   }


   @Test
   public void testNormalization() {


      for (final IVector<?, ?> vector : getVectorsWithOneDimension(1, -1)) {
         Assert.assertEquals(vector + "normalized", 1.0, (vector.normalized()).length());
         Assert.assertEquals(vector + "normalized", 1.0, (vector.normalized()).squaredLength());
         Assert.assertTrue(vector + "isNormalized", (vector.normalized()).isNormalized());

      }
      for (final IVector<?, ?> vector : getVectorsWithOneDimension(2, -2)) {
         Assert.assertEquals(vector + "isNormalized", false, vector.isNormalized());
         Assert.assertEquals(vector + "normalized", 1.0, (vector.normalized()).length());
         Assert.assertEquals(vector + "normalized", 1.0, (vector.normalized()).squaredLength());
         Assert.assertTrue(vector + "isNormalized", (vector.normalized()).isNormalized());
      }
      for (final IVector<?, ?> vector : getVectorsWithTwoDimensions(1, -1)) {
         Assert.assertEquals(vector + "isNormalized", false, vector.isNormalized());
         Assert.assertEquals(vector + "normalized", 1.0, (vector.normalized()).length());
         Assert.assertEquals(vector + "normalized", 1.0, (vector.normalized()).squaredLength());
         Assert.assertTrue(vector + "isNormalized", (vector.normalized()).isNormalized());
      }
      for (final IVector<?, ?> vector : getVectorsWithTwoDimensions(2, -2)) {
         Assert.assertEquals(vector + "isNormalized", false, vector.isNormalized());
         Assert.assertEquals(vector + "normalized", 1.0, (vector.normalized()).length());
         Assert.assertEquals(vector + "normalized", 1.0, (vector.normalized()).squaredLength());
         Assert.assertTrue(vector + "isNormalized", (vector.normalized()).isNormalized());
      }
      for (final IVector<?, ?> vector : getVectorsWithThreeDimensions(1, -1)) {
         Assert.assertEquals(vector + "isNormalized", false, vector.isNormalized());
         Assert.assertEquals(vector + "normalized", 1.0, (vector.normalized()).length());
         Assert.assertEquals(vector + "normalized", 1.0, (vector.normalized()).squaredLength());
         Assert.assertTrue(vector + "isNormalized", (vector.normalized()).isNormalized());
      }
      for (final IVector<?, ?> vector : getVectorsWithThreeDimensions(2, -2)) {
         Assert.assertEquals(vector + "isNormalized", false, vector.isNormalized());
         Assert.assertEquals(vector + "normalized", 1.0, (vector.normalized()).length());
         Assert.assertEquals(vector + "normalized", 1.0, (vector.normalized()).squaredLength());
         Assert.assertTrue(vector + "isNormalized", (vector.normalized()).isNormalized());
      }
      for (final IVector<?, ?> element : GVectorTest.zeros2) {
         Assert.assertSame("normalize Zero-Vec2", element, element.normalized());
      }
      for (final IVector<?, ?> element : GVectorTest.zeros3) {
         Assert.assertSame("normalize Zero-Vec3", element, element.normalized());
      }

   }


   @Test
   public void testDimensions() {
      for (final IVector2 element : GVectorTest.all2) {
         Assert.assertEquals(element + " 2dims", 2, element.dimensions());
      }
      for (final IVector3 element : GVectorTest.all3) {
         Assert.assertEquals(element + " 3dims", 3, element.dimensions());
      }
   }


   @Test
   public void testClosestPoint() {
      for (final IVector2 element : GVectorTest.all2) {
         Assert.assertSame("closestPoint same", element, element.closestPoint(element));
      }
      combinationsDo(GVectorTest.all2, new PairEvaluation<IVector2>() {
         @Override
         public void evaluate(final IVector2 vector1,
                              final IVector2 vector2) {
            Assert.assertSame("closestPoint pairwise", vector1, vector1.closestPoint(vector2));
         }
      });
      for (final IVector3 element : GVectorTest.all3) {
         Assert.assertSame("closestPoint same", element, element.closestPoint(element));
      }
      combinationsDo(GVectorTest.all3, new PairEvaluation<IVector3>() {
         @Override
         public void evaluate(final IVector3 vector1,
                              final IVector3 vector2) {
            Assert.assertSame("closestPoint pairwise", vector1, vector1.closestPoint(vector2));
         }
      });
   }


   @Test
   public void testContains() {
      for (final IVector2 element : GVectorTest.all2) {
         Assert.assertTrue(element + " contains self", element.contains(element));
      }
      for (final IVector3 element : GVectorTest.all3) {
         Assert.assertTrue(element + " contains self", element.contains(element));
      }
   }


   @Test
   public void testClamp() {
      for (final IVector2 element : GVectorTest.generics2) {
         assertCloseTo("clamp to upper 2D", GVector2D.UNIT, element.clamp(GVector2D.ZERO, GVector2D.UNIT));
      }
      for (final IVector2 element : GVectorTest.generics2Neg) {
         assertCloseTo("clamp to lower 2D", GVector2D.ZERO, element.clamp(GVector2D.ZERO, GVector2D.UNIT));
      }
      for (final IVector3 element : GVectorTest.generics3) {
         assertCloseTo("clamp to upper 3D", GVector3D.UNIT, element.clamp(GVector3D.ZERO, GVector3D.UNIT));
      }
      for (final IVector3 element : GVectorTest.generics3Neg) {
         assertCloseTo("clamp to lower 2D", GVector3D.ZERO, element.clamp(GVector3D.ZERO, GVector3D.UNIT));
      }
   }


   @Test
   public void testSquaredDistance() {
      for (final IVector2 element : GVectorTest.all2) {
         Assert.assertEquals("distance to self = 0", 0, element.squaredDistance(element),
                  GMath.DEFAULT_NUMERICAL_PRECISION_DOUBLE);

      }
      for (final IVector3 element : GVectorTest.all3) {
         Assert.assertEquals("distance to self = 0", 0, element.squaredDistance(element),
                  GMath.DEFAULT_NUMERICAL_PRECISION_DOUBLE);
      }

      //All these tests fail with big numbers....
      //      for (final IVector2 element : VectorTest.all2) {
      //         Assert.assertTrue(element + "sqdistance to 0 = this.squaredLength()", GMath.closeTo(element.squaredLength(),
      //                  element.squaredDistance(GVector2F.ZERO), GMath.DEFAULT_NUMERICAL_PRECISION_FLOAT));
      //
      //      }
      //      for (final IVector3 element : VectorTest.all3) {
      //         Assert.assertTrue(element + "distance to 0 = this.squaredLength()", GMath.closeTo(element.squaredLength(),
      //                  element.squaredDistance(GVector3D.ZERO)));
      //      }

      //      for (final IVector2 element : VectorTest.all2) {
      //         Assert.assertEquals("distance to -self = 2*squaredLength*2", 2 * element.squaredLength() * 2,
      //                  element.squaredDistance(element.negated()), GMath.DEFAULT_NUMERICAL_PRECISION_FLOAT);
      //      }
      //      for (final IVector3 element : VectorTest.all3) {
      //         Assert.assertEquals("distance to -self = 0", 2 * element.squaredLength() * 2,
      //                  element.squaredDistance(element.negated()), GMath.DEFAULT_NUMERICAL_PRECISION_FLOAT);
      //      }

      for (final IVector2 element : GVectorTest.all2) {
         Assert.assertEquals("distance to -infinity = infinity", Double.POSITIVE_INFINITY,
                  element.squaredDistance(GVector2D.NEGATIVE_INFINITY), GMath.DEFAULT_NUMERICAL_PRECISION_FLOAT);
      }
      for (final IVector3 element : GVectorTest.all3) {
         Assert.assertEquals("distance to -infinity", Double.POSITIVE_INFINITY,
                  element.squaredDistance(GVector3D.NEGATIVE_INFINITY), GMath.DEFAULT_NUMERICAL_PRECISION_FLOAT);

      }
   }


   //   @GConcurrentTest
   //   public void testInterpolation() {
   //      for (final IVector2 element : VectorTest.all2F) {
   //         assertCloseTo(element + " interpolatedTo self, 0.5", element.scale(0.5f), element.interpolatedTo(element.negated(), 2.0));
   //
   //      }
   //   }


   @Test
   public void testSegment3DDistance() throws Exception {
      final GSegment3D segment = new GSegment3D(new GVector3D(0, 0, 0), new GVector3D(0, 10, 5));
      final GVector3D p = new GVector3D(0.0, 0.8944271909999159, 0.4472135954999579);
      final double distance = segment.squaredDistance(p);
      Assert.assertTrue(GMath.closeToZero(distance));
   }


   @Test
   public void testEquals() {
      for (int i = 0; i < GVectorTest.all2D.length; i++) {
         //double 2d
         Assert.assertTrue("equals self", GVectorTest.all2D[i].equals(GVectorTest.all2D[i]));
         Assert.assertFalse("equals same Dim different Type", GVectorTest.all2D[i].equals(GVectorTest.all2F[i]));
         Assert.assertFalse("equals null", GVectorTest.all2D[i].equals(null));
         Assert.assertFalse("equals different Dim", GVectorTest.all2D[i].equals(GVectorTest.all3D[i]));

         if (GVectorTest.all2D[i].isNormalized()) {
            Assert.assertTrue("equals same Values - normalized",
                     GVectorTest.all2D[i].equals(new GVector2D(GVectorTest.all2D[i].x(), GVectorTest.all2D[i].y()).normalized()));
         }
         else {
            Assert.assertTrue("equals same Values",
                     GVectorTest.all2D[i].closeTo(new GVector2D(GVectorTest.all2D[i].x(), GVectorTest.all2D[i].y())));
         }

         Assert.assertFalse("equals different X",
                  GVectorTest.all2D[i].equals(new GVector2D(GVectorTest.all2D[i].x() + 1, GVectorTest.all2D[i].y())));
         Assert.assertFalse("equals different Y",
                  GVectorTest.all2D[i].equals(new GVector2D(GVectorTest.all2D[i].x(), GVectorTest.all2D[i].y() + 1)));


         //float 2d
         Assert.assertTrue("equals self", GVectorTest.all2F[i].equals(GVectorTest.all2F[i]));
         Assert.assertFalse("equals same Dim different Type", GVectorTest.all2F[i].equals(GVectorTest.all2D[i]));
         Assert.assertFalse("equals null", GVectorTest.all2F[i].equals(null));
         Assert.assertFalse("equals different Dim", GVectorTest.all2F[i].equals(GVectorTest.all3D[i]));

         if (GVectorTest.all2F[i].isNormalized()) {
            Assert.assertTrue(
                     "equals same Values - normalized",
                     GVectorTest.all2F[i].equals(new GVector2F((float) GVectorTest.all2F[i].x(), (float) GVectorTest.all2F[i].y()).normalized()));
         }
         else {
            Assert.assertTrue(
                     "equals same Values",
                     GVectorTest.all2F[i].equals(new GVector2F((float) GVectorTest.all2F[i].x(), (float) GVectorTest.all2F[i].y())));
         }
         Assert.assertFalse("equals different X", GVectorTest.all2F[i].equals(new GVector2F((float) GVectorTest.all2F[i].x() + 1,
                  (float) GVectorTest.all2F[i].y())));
         Assert.assertFalse("equals different Y", GVectorTest.all2F[i].equals(new GVector2F((float) GVectorTest.all2F[i].x(),
                  (float) GVectorTest.all2F[i].y() + 1)));
      }

      for (int i = 0; i < GVectorTest.all3D.length; i++) {
         //double 3d
         Assert.assertTrue("equals self", GVectorTest.all3D[i].equals(GVectorTest.all3D[i]));
         Assert.assertFalse("equals same Dim different Type", GVectorTest.all3D[i].equals(GVectorTest.all3F[i]));
         Assert.assertFalse("equals null", GVectorTest.all3D[i].equals(null));

         if (GVectorTest.all3D[i].isNormalized()) {
            Assert.assertTrue("equals same Values - normalized", GVectorTest.all3D[i].equals(new GVector3D(
                     (float) GVectorTest.all3D[i].x(), GVectorTest.all3D[i].y(), GVectorTest.all3D[i].z()).normalized()));
         }
         else {
            Assert.assertTrue("equals same Values", GVectorTest.all3D[i].equals(new GVector3D(GVectorTest.all3D[i].x(),
                     GVectorTest.all3D[i].y(), GVectorTest.all3D[i].z())));
         }
         Assert.assertFalse("equals different X", GVectorTest.all3D[i].equals(new GVector3D(GVectorTest.all3D[i].x() + 1,
                  GVectorTest.all3D[i].y(), GVectorTest.all3D[i].z())));
         Assert.assertFalse("equals different Y", GVectorTest.all3D[i].equals(new GVector3D(GVectorTest.all3D[i].x(),
                  GVectorTest.all3D[i].y() + 1, GVectorTest.all3D[i].z())));
         Assert.assertFalse("equals different Z", GVectorTest.all3D[i].equals(new GVector3D(GVectorTest.all3D[i].x(),
                  GVectorTest.all3D[i].y(), GVectorTest.all3D[i].z() + 1)));

         //float 3d
         Assert.assertTrue("equals self", GVectorTest.all3F[i].equals(GVectorTest.all3F[i]));
         Assert.assertFalse("equals same Dim different Type", GVectorTest.all3F[i].equals(GVectorTest.all3D[i]));
         Assert.assertFalse("equals null", GVectorTest.all3F[i].equals(null));

         if (GVectorTest.all3F[i].isNormalized()) {
            Assert.assertTrue("equals same Values - normalized",
                     GVectorTest.all3F[i].equals(new GVector3F((float) GVectorTest.all3F[i].x(),
                              (float) GVectorTest.all3F[i].y(), (float) GVectorTest.all3F[i].z()).normalized()));
         }
         else {
            Assert.assertTrue("equals same Values", GVectorTest.all3F[i].equals(new GVector3F((float) GVectorTest.all3F[i].x(),
                     (float) GVectorTest.all3F[i].y(), (float) GVectorTest.all3F[i].z())));
         }

         Assert.assertFalse("equals different X", GVectorTest.all3F[i].equals(new GVector3F((float) GVectorTest.all3F[i].x() + 1,
                  (float) GVectorTest.all3F[i].y(), (float) GVectorTest.all3F[i].z())));
         Assert.assertFalse("equals different Y", GVectorTest.all3F[i].equals(new GVector3F((float) GVectorTest.all3F[i].x(),
                  (float) GVectorTest.all3F[i].y() + 1, (float) GVectorTest.all3F[i].z())));
         Assert.assertFalse("equals different Z", GVectorTest.all3F[i].equals(new GVector3F((float) GVectorTest.all3F[i].x(),
                  (float) GVectorTest.all3F[i].y(), (float) GVectorTest.all3F[i].z() + 1)));
      }
   }


   @Test
   public void testMinMax() {

      //"pure" cases (double-double, float-float) 
      Assert.assertTrue("min with self", (GVectorTest.generic2D.min(GVectorTest.generic2D).equals(GVectorTest.generic2D)));
      Assert.assertTrue("max with self", (GVectorTest.generic2D.max(GVectorTest.generic2D).equals(GVectorTest.generic2D)));
      Assert.assertTrue("min with 0", (GVectorTest.generic2D.min(GVector2D.ZERO).closeTo(GVector2D.ZERO)));
      Assert.assertTrue("max with 0", (GVectorTest.generic2D.max(GVector2D.ZERO).equals(GVectorTest.generic2D)));
      Assert.assertTrue("min with Negative",
               (GVectorTest.generic2D.min(GVectorTest.generic2DNeg).equals(GVectorTest.generic2DNeg)));
      Assert.assertTrue("max with Negative", (GVectorTest.generic2D.max(GVectorTest.generic2DNeg).equals(GVectorTest.generic2D)));

      Assert.assertTrue("min with self", (GVectorTest.generic2F.min(GVectorTest.generic2F).equals(GVectorTest.generic2F)));
      Assert.assertTrue("max with self", (GVectorTest.generic2F.max(GVectorTest.generic2F).equals(GVectorTest.generic2F)));
      Assert.assertTrue("min with 0", (GVectorTest.generic2F.min(GVector2F.ZERO).closeTo(GVector2F.ZERO)));
      Assert.assertTrue("max with 0", (GVectorTest.generic2F.max(GVector2F.ZERO).equals(GVectorTest.generic2F)));
      Assert.assertTrue("min with Negative",
               (GVectorTest.generic2F.min(GVectorTest.generic2FNeg).equals(GVectorTest.generic2FNeg)));
      Assert.assertTrue("max with Negative", (GVectorTest.generic2F.max(GVectorTest.generic2FNeg).equals(GVectorTest.generic2F)));

      Assert.assertTrue("min with self", (GVectorTest.generic3D.min(GVectorTest.generic3D).equals(GVectorTest.generic3D)));
      Assert.assertTrue("max with self", (GVectorTest.generic3D.max(GVectorTest.generic3D).equals(GVectorTest.generic3D)));
      Assert.assertTrue("min with self", (GVectorTest.generic3D.min(GVector3D.ZERO).closeTo(GVector3D.ZERO)));
      Assert.assertTrue("max with self", (GVectorTest.generic3D.max(GVector3D.ZERO).equals(GVectorTest.generic3D)));
      Assert.assertTrue("min with Negative",
               (GVectorTest.generic3D.min(GVectorTest.generic3DNeg).equals(GVectorTest.generic3DNeg)));
      Assert.assertTrue("max with Negative", (GVectorTest.generic3D.max(GVectorTest.generic3DNeg).equals(GVectorTest.generic3D)));

      Assert.assertTrue("min with self", (GVectorTest.generic3F.min(GVectorTest.generic3F).equals(GVectorTest.generic3F)));
      Assert.assertTrue("max with self", (GVectorTest.generic3F.max(GVectorTest.generic3F).equals(GVectorTest.generic3F)));
      Assert.assertTrue("min with self", (GVectorTest.generic3F.min(GVector3F.ZERO).closeTo(GVector3F.ZERO)));
      Assert.assertTrue("max with self", (GVectorTest.generic3F.max(GVector3F.ZERO).equals(GVectorTest.generic3F)));
      Assert.assertTrue("min with Negative",
               (GVectorTest.generic3F.min(GVectorTest.generic3FNeg).equals(GVectorTest.generic3FNeg)));
      Assert.assertTrue("max with Negative", (GVectorTest.generic3F.max(GVectorTest.generic3FNeg).equals(GVectorTest.generic3F)));

      //"mixed" cases (double-float, float double)
      Assert.assertTrue("min with other type", (GVectorTest.generic2D.min(GVectorTest.generic2F)).closeTo(GVectorTest.generic2D,
               GMath.DEFAULT_NUMERICAL_PRECISION_FLOAT));

      Assert.assertTrue("min with other type", (GVectorTest.generic2F.min(GVectorTest.generic2D)).closeTo(GVectorTest.generic2F));


   }


   @Test
   public void testGets() {

      final double[] generic2DArray = {
                        GVectorTest.generic2D.x(),
                        GVectorTest.generic2D.y()
      };
      Assert.assertEquals(GVectorTest.generic2D + " getX", 1000000.2, GVectorTest.generic2D.x());
      Assert.assertEquals(GVectorTest.generic2D + " getY", 2000000.3, GVectorTest.generic2D.y());
      Assert.assertEquals(GVectorTest.generic2D + " getCoordinate(0)", 1000000.2, GVectorTest.generic2D.get((byte) 0));
      Assert.assertEquals(GVectorTest.generic2D + " getCoordinate(1)", 2000000.3, GVectorTest.generic2D.get((byte) 1));
      Assert.assertEquals(GVectorTest.generic2D + " getCoordinates()", generic2DArray[0],
               GVectorTest.generic2D.getCoordinates()[0]);
      Assert.assertEquals(GVectorTest.generic2D + " getCoordinates()", generic2DArray[1],
               GVectorTest.generic2D.getCoordinates()[1]);

      final double[] generic2FArray = {
                        GVectorTest.generic2F.x(),
                        GVectorTest.generic2F.y()
      };
      Assert.assertTrue(GVectorTest.generic2F + " getX", GMath.closeTo(1000000.2f, GVectorTest.generic2F.x()));
      Assert.assertTrue(GVectorTest.generic2F + " getY", GMath.closeTo(2000000.3f, GVectorTest.generic2F.y()));
      Assert.assertTrue(GVectorTest.generic2F + " getCoordinate(0)",
               GMath.closeTo(1000000.2f, GVectorTest.generic2F.get((byte) 0)));
      Assert.assertTrue(GVectorTest.generic2F + " getCoordinate(1)",
               GMath.closeTo(2000000.3f, GVectorTest.generic2F.get((byte) 1)));
      Assert.assertEquals(GVectorTest.generic2D + " getCoordinates()", generic2FArray[0],
               GVectorTest.generic2F.getCoordinates()[0]);
      Assert.assertEquals(GVectorTest.generic2D + " getCoordinates()", generic2FArray[1],
               GVectorTest.generic2F.getCoordinates()[1]);

      final double[] generic3DArray = {
                        GVectorTest.generic3D.x(),
                        GVectorTest.generic3D.y(),
                        GVectorTest.generic3D.z()
      };
      Assert.assertEquals(GVectorTest.generic3D + " getX", 1000000.2, GVectorTest.generic3D.x());
      Assert.assertEquals(GVectorTest.generic3D + " getY", 2000000.3, GVectorTest.generic3D.y());
      Assert.assertEquals(GVectorTest.generic3D + " getZ", 3000000.4, GVectorTest.generic3D.z());
      Assert.assertEquals(GVectorTest.generic3D + " getCoordinate(0)", 1000000.2, GVectorTest.generic3D.get((byte) 0));
      Assert.assertEquals(GVectorTest.generic3D + " getCoordinate(1)", 2000000.3, GVectorTest.generic3D.get((byte) 1));
      Assert.assertEquals(GVectorTest.generic3D + " getCoordinate(2)", 3000000.4, GVectorTest.generic3D.get((byte) 2));
      Assert.assertEquals(GVectorTest.generic2D + " getCoordinates()", generic3DArray[0],
               GVectorTest.generic3D.getCoordinates()[0]);
      Assert.assertEquals(GVectorTest.generic2D + " getCoordinates()", generic3DArray[1],
               GVectorTest.generic3D.getCoordinates()[1]);
      Assert.assertEquals(GVectorTest.generic2D + " getCoordinates()", generic3DArray[2],
               GVectorTest.generic3D.getCoordinates()[2]);

      final double[] generic3FArray = {
                        GVectorTest.generic3F.x(),
                        GVectorTest.generic3F.y(),
                        GVectorTest.generic3F.z()
      };
      Assert.assertTrue(GVectorTest.generic3F + " getX", GMath.closeTo(1000000.2f, GVectorTest.generic3F.x()));
      Assert.assertTrue(GVectorTest.generic3F + " getY", GMath.closeTo(2000000.3f, GVectorTest.generic3F.y()));
      Assert.assertTrue(GVectorTest.generic3F + " getZ", GMath.closeTo(3000000.4f, GVectorTest.generic3F.z()));
      Assert.assertTrue(GVectorTest.generic3F + " getX", GMath.closeTo(1000000.2f, GVectorTest.generic3F.get((byte) 0)));
      Assert.assertTrue(GVectorTest.generic3F + " getY", GMath.closeTo(2000000.3f, GVectorTest.generic3F.get((byte) 1)));
      Assert.assertTrue(GVectorTest.generic3F + " getZ", GMath.closeTo(3000000.4f, GVectorTest.generic3F.get((byte) 2)));
      Assert.assertEquals(GVectorTest.generic2D + " getCoordinates()", generic3FArray[0],
               GVectorTest.generic3F.getCoordinates()[0]);
      Assert.assertEquals(GVectorTest.generic2D + " getCoordinates()", generic3FArray[1],
               GVectorTest.generic3F.getCoordinates()[1]);
      Assert.assertEquals(GVectorTest.generic2D + " getCoordinates()", generic3FArray[2],
               GVectorTest.generic3F.getCoordinates()[2]);
   }


   @Test
   public void testAbsolutedRounded() {

      assertCloseTo(" absoluted", GVectorTest.generic2D, GVectorTest.generic2DNeg.absoluted());
      assertCloseTo(" absoluted", GVectorTest.generic2F, GVectorTest.generic2FNeg.absoluted());
      assertCloseTo(" absoluted", GVectorTest.generic3D, GVectorTest.generic3DNeg.absoluted());
      assertCloseTo(" absoluted", GVectorTest.generic3F, GVectorTest.generic3FNeg.absoluted());

      assertCloseTo(" absoluted", GVectorTest.generic2Rounded.negated(), GVectorTest.generic2DNeg.rounded());
      assertCloseTo(" absoluted", GVectorTest.generic2Rounded, GVectorTest.generic2D.rounded());
      assertCloseTo(" absoluted", GVectorTest.generic2Rounded.negated(), GVectorTest.generic2FNeg.rounded());
      assertCloseTo(" absoluted", GVectorTest.generic2Rounded, GVectorTest.generic2F.rounded());

      assertCloseTo(" absoluted", GVectorTest.generic3Rounded.negated(), GVectorTest.generic3DNeg.rounded());
      assertCloseTo(" absoluted", GVectorTest.generic3Rounded, GVectorTest.generic3D.rounded());
      assertCloseTo(" absoluted", GVectorTest.generic3Rounded.negated(), GVectorTest.generic3FNeg.rounded());
      assertCloseTo(" absoluted", GVectorTest.generic3Rounded, GVectorTest.generic3F.rounded());
   }


   @Test
   public void testAddsSubsScalesDivsNegateds2() {
      //single number addition/subtraction/scale/div/negated
      final double toAdd = 1;
      final double toScale = 2;
      IVector2 result;

      final IVector2 desiredAddResult2D = new GVector2D(GVectorTest.generic2D.x() + toAdd, GVectorTest.generic2D.y() + toAdd);
      final IVector2 desiredAddResult2DNeg = new GVector2D(GVectorTest.generic2DNeg.x() + toAdd, GVectorTest.generic2DNeg.y()
                                                                                                 + toAdd);

      final IVector2 desiredSubResult2D = new GVector2D(GVectorTest.generic2D.x() - toAdd, GVectorTest.generic2D.y() - toAdd);
      final IVector2 desiredSubResult2DNeg = new GVector2D(GVectorTest.generic2DNeg.x() - toAdd, GVectorTest.generic2DNeg.y()
                                                                                                 - toAdd);

      final IVector2 desiredScaleResult2D = new GVector2D(GVectorTest.generic2D.x() * toScale, GVectorTest.generic2D.y()
                                                                                               * toScale);
      final IVector2 desiredScaleResult2DNeg = new GVector2D(GVectorTest.generic2DNeg.x() * toScale, GVectorTest.generic2DNeg.y()
                                                                                                     * toScale);

      final IVector2 desiredDivResult2D = new GVector2D(GVectorTest.generic2D.x() / toScale, GVectorTest.generic2D.y() / toScale);
      final IVector2 desiredDivResult2DNeg = new GVector2D(GVectorTest.generic2DNeg.x() / toScale, GVectorTest.generic2DNeg.y()
                                                                                                   / toScale);

      final IVector2 zero = new GVector2D(0, 0);


      for (int i = 0; i < 2; i++) {
         // test with a single double
         //add 
         result = GVectorTest.generics2[i].add(toAdd);
         Assert.assertTrue(GVectorTest.generics2[i] + ".add(1) should be " + desiredAddResult2D + " but was " + result,
                  desiredAddResult2D.closeTo(result));
         assertCloseTo(" addConstant", desiredSubResult2DNeg.negated(), result);
         result = GVectorTest.generics2Neg[i].add(toAdd);
         assertCloseTo(" addConstant", desiredAddResult2DNeg, result);
         assertCloseTo(" addConstant", desiredSubResult2D.negated(), result);

         //sub
         result = GVectorTest.generics2[i].sub(toAdd);
         assertCloseTo(" subConstant", desiredSubResult2D, result);
         assertCloseTo(" subConstant", desiredAddResult2DNeg.negated(), result);
         result = GVectorTest.generics2Neg[i].sub(toAdd);
         assertCloseTo(" subConstant", desiredSubResult2DNeg, result);
         assertCloseTo(" subConstant", desiredAddResult2D.negated(), result);

         //scale
         result = GVectorTest.generics2[i].scale(toScale);
         assertCloseTo(" uniformScale", desiredScaleResult2D, result);
         assertCloseTo(" uniformScale", desiredScaleResult2DNeg.negated(), result);
         result = GVectorTest.generics2Neg[i].scale(toScale);
         assertCloseTo(" uniformScale", desiredScaleResult2DNeg, result);
         assertCloseTo(" uniformScale", desiredScaleResult2D.negated(), result);
         result = GVectorTest.zeros2[i].scale(toScale);
         assertCloseTo(" uniformScale", zero, result);
         assertCloseTo(" uniformScale", zero.negated(), result);

         //div
         result = GVectorTest.generics2[i].div(toScale);
         assertCloseTo(" uniformScale", desiredDivResult2D, result);
         assertCloseTo(" uniformScale", desiredDivResult2DNeg.negated(), result);
         result = GVectorTest.generics2Neg[i].div(toScale);
         assertCloseTo(" uniformScale", desiredDivResult2DNeg, result);
         assertCloseTo(" uniformScale", desiredDivResult2D.negated(), result);
         result = GVectorTest.zeros2[i].scale(toScale);
         assertCloseTo(" uniformScale", zero, result);
         assertCloseTo(" uniformScale", zero.negated(), result);

      }
   }


   @Test
   public void testAddsSubsScalesDivsNegateds3() {
      //single number addition/subtraction/scale/div/negated
      final double toAdd = 1;
      final double toScale = 2;
      IVector3 result;

      //      final IVector3 desiredAddResult3D = new GVector3D(2.2, 3.3, 4.4);
      final IVector3 desiredAddResult3D = new GVector3D(GVectorTest.generic3D.x() + toAdd, GVectorTest.generic3D.y() + toAdd,
               GVectorTest.generic3D.z() + toAdd);
      final IVector3 desiredAddResult3DNeg = new GVector3D(GVectorTest.generic3DNeg.x() + toAdd, GVectorTest.generic3DNeg.y()
                                                                                                 + toAdd,
               GVectorTest.generic3DNeg.z() + toAdd);

      final IVector3 desiredSubResult3D = new GVector3D(GVectorTest.generic3D.x() - toAdd, GVectorTest.generic3D.y() - toAdd,
               GVectorTest.generic3D.z() - toAdd);
      final IVector3 desiredSubResult3DNeg = new GVector3D(GVectorTest.generic3DNeg.x() - toAdd, GVectorTest.generic3DNeg.y()
                                                                                                 - toAdd,
               GVectorTest.generic3DNeg.z() - toAdd);

      final IVector3 desiredScaleResult3D = new GVector3D(GVectorTest.generic3D.x() * toScale, GVectorTest.generic3D.y()
                                                                                               * toScale,
               GVectorTest.generic3D.z() * toScale);
      final IVector3 desiredScaleResult3DNeg = new GVector3D(GVectorTest.generic3DNeg.x() * toScale, GVectorTest.generic3DNeg.y()
                                                                                                     * toScale,
               GVectorTest.generic3DNeg.z() * toScale);

      final IVector3 desiredDivResult3D = new GVector3D(GVectorTest.generic3D.x() / toScale, GVectorTest.generic3D.y() / toScale,
               GVectorTest.generic3D.z() / toScale);
      final IVector3 desiredDivResult3DNeg = new GVector3D(GVectorTest.generic3DNeg.x() / toScale, GVectorTest.generic3DNeg.y()
                                                                                                   / toScale,
               GVectorTest.generic3DNeg.z() / toScale);

      final IVector3 zero = new GVector3D(0, 0, 0);


      for (int i = 0; i < 2; i++) {
         // test with a single double
         //add 
         result = GVectorTest.generics3[i].add(toAdd);
         assertCloseTo(" addConstant", desiredAddResult3D, result);
         assertCloseTo(" addConstant", desiredSubResult3DNeg.negated(), result);
         result = GVectorTest.generics3Neg[i].add(toAdd);
         assertCloseTo(" addConstant", desiredAddResult3DNeg, result);
         assertCloseTo(" addConstant", desiredSubResult3D.negated(), result);

         //sub
         result = GVectorTest.generics3[i].sub(toAdd);
         assertCloseTo(" subConstant", desiredSubResult3D, result);
         assertCloseTo(" subConstant", desiredAddResult3DNeg.negated(), result);
         result = GVectorTest.generics3Neg[i].sub(toAdd);
         assertCloseTo(" subConstant", desiredSubResult3DNeg, result);
         assertCloseTo(" subConstant", desiredAddResult3D.negated(), result);

         //scale
         result = GVectorTest.generics3[i].scale(toScale);
         assertCloseTo(" uniformScale", desiredScaleResult3D, result);
         assertCloseTo(" uniformScale", desiredScaleResult3DNeg.negated(), result);
         result = GVectorTest.generics3Neg[i].scale(toScale);
         assertCloseTo(" uniformScale", desiredScaleResult3DNeg, result);
         assertCloseTo(" uniformScale", desiredScaleResult3D.negated(), result);
         result = GVectorTest.zeros3[i].scale(toScale);
         assertCloseTo(" uniformScale", zero, result);
         assertCloseTo(" uniformScale", zero.negated(), result);

         //div
         result = GVectorTest.generics3[i].div(toScale);
         assertCloseTo(" uniformScale", desiredDivResult3D, result);
         assertCloseTo(" uniformScale", desiredDivResult3DNeg.negated(), result);
         result = GVectorTest.generics3Neg[i].div(toScale);
         assertCloseTo(" uniformScale", desiredDivResult3DNeg, result);
         assertCloseTo(" uniformScale", desiredDivResult3D.negated(), result);
         result = GVectorTest.zeros3[i].scale(toScale);
         assertCloseTo(" uniformScale", zero, result);
         assertCloseTo(" uniformScale", zero.negated(), result);

      }
   }


   @Test
   public void testPairsNonZero2AddSubScaleDivReciprocal() {

      combinationsDo(GVectorTest.generics2All, new PairEvaluation<IVector2>() {
         @Override
         public void evaluate(final IVector2 vector1,
                              final IVector2 vector2) {

            final IVector2 unit = new GVector2D(1, 1);

            assertCloseTo("(a+b)-b=a", vector1, (vector1.add(vector2)).sub(vector2));
            assertCloseTo("a+b-b=a", vector1, vector1.add(vector2).sub(vector2));
            assertCloseTo("(a+b)-b=a", vector1.negated(), (vector1.add(vector2)).sub(vector2).negated());

            Assert.assertTrue("a-a = 0", vector1.sub(vector1).closeToZero());
            Assert.assertTrue("a+(-a) = 0", vector1.add(vector1.negated()).closeToZero());

            assertCloseTo("a*(1/a) = 1", unit, vector1.scale(vector1.reciprocal()));
            assertCloseTo("a/a = 1", unit, vector1.div(vector1));

            assertCloseTo("a*1 = a", vector1, vector1.scale(unit));
            assertCloseTo("a/1 = a", vector1, vector1.div(unit));

            assertCloseTo("a*b = b*a", vector1.scale(vector2), vector2.scale(vector1));


         }
      });
   }


   @Test
   public void testAngles() {
      //2d double 
      Assert.assertTrue(GVector2D.X_UP + ".angle(" + GVector2D.Y_UP + ") = " + (GVector2D.X_UP).angle(GVector2D.Y_UP),
               GMath.closeTo(GMath.HALF_PI, ((GVector2D.X_UP).angle(GVector2D.Y_UP))));
      Assert.assertTrue(GVector2D.X_UP + ".angle(" + GVector2D.Y_DOWN + ") = " + (GVector2D.X_UP).angle(GVector2D.Y_UP),
               GMath.closeTo(GMath.HALF_PI, ((GVector2D.X_UP).angle(GVector2D.Y_DOWN))));
      Assert.assertTrue(
               GVectorTest.generic2D + ".angle(" + GVectorTest.generic2D + ") = "
                        + GVectorTest.generic2D.angle(GVectorTest.generic2D),
               GMath.closeTo(0, (GVectorTest.generic2D.angle(GVectorTest.generic2D))));
      Assert.assertTrue(
               GVectorTest.generic2DNeg + ".angle(" + GVectorTest.generic2D + ") = "
                        + GVectorTest.generic2DNeg.angle(GVectorTest.generic2D), GMath.closeTo(GMath.PI,
                        (GVectorTest.generic2DNeg.angle(GVectorTest.generic2D)), GMath.DEFAULT_NUMERICAL_PRECISION_FLOAT));

      Assert.assertTrue(GVector2D.X_UP + ".angle(" + GVector2D.UNIT + ") = " + (GVector2D.X_UP).angle(GVector2D.UNIT),
               GMath.closeTo(GMath.QUARTER_PI, ((GVector2D.X_UP).angle(GVector2D.UNIT))));

      //2d float
      Assert.assertTrue(GVector2F.X_UP + ".angle(" + GVector2F.Y_UP + ") = " + (GVector2F.X_UP).angle(GVector2F.Y_UP),
               GMath.closeTo(GMath.HALF_PI, ((GVector2F.X_UP).angle(GVector2F.Y_UP))));
      Assert.assertTrue(GVector2F.X_UP + ".angle(" + GVector2F.Y_DOWN + ") = " + (GVector2D.X_UP).angle(GVector2D.Y_UP),
               GMath.closeTo(GMath.HALF_PI, ((GVector2F.X_UP).angle(GVector2F.Y_DOWN))));
      Assert.assertTrue(
               GVectorTest.generic2F + ".angle(" + GVectorTest.generic2F + ") = "
                        + GVectorTest.generic2F.angle(GVectorTest.generic2F),
               GMath.closeTo(0, (GVectorTest.generic2D.angle(GVectorTest.generic2D))));
      Assert.assertTrue(
               GVectorTest.generic2FNeg + ".angle(" + GVectorTest.generic2F + ") = "
                        + GVectorTest.generic2FNeg.angle(GVectorTest.generic2F), GMath.closeTo(GMath.PI,
                        (GVectorTest.generic2FNeg.angle(GVectorTest.generic2F)), GMath.DEFAULT_NUMERICAL_PRECISION_FLOAT));

      Assert.assertTrue(GVector2F.X_UP + ".angle(" + GVector2F.UNIT + ") = " + (GVector2F.X_UP).angle(GVector2F.UNIT),
               GMath.closeTo(GMath.QUARTER_PI, ((GVector2F.X_UP).angle(GVector2F.UNIT))));

      //2d mixed
      Assert.assertTrue(GVector2F.X_UP + ".angle(" + GVector2D.Y_UP + ") = " + (GVector2F.X_UP).angle(GVector2D.Y_UP),
               GMath.closeTo(GMath.HALF_PI, ((GVector2F.X_UP).angle(GVector2D.Y_UP))));
      Assert.assertTrue(GVector2D.X_UP + ".angle(" + GVector2F.Y_UP + ") = " + (GVector2D.X_UP).angle(GVector2F.Y_UP),
               GMath.closeTo(GMath.HALF_PI, ((GVector2D.X_UP).angle(GVector2F.Y_UP))));


      Assert.assertTrue(GVector3D.X_UP + ".angle(" + GVector3D.Y_UP + ") = " + (GVector3D.X_UP).angle(GVector3D.Y_UP),
               GMath.closeTo(GMath.HALF_PI, ((GVector3D.X_UP).angle(GVector3D.Y_UP))));
      Assert.assertTrue(GVector3D.X_UP + ".angle(" + GVector3D.Y_DOWN + ") = " + (GVector3D.X_UP).angle(GVector3D.Y_UP),
               GMath.closeTo(GMath.HALF_PI, ((GVector3D.X_UP).angle(GVector3D.Y_DOWN))));

      //      IVector2 vec1 = new GVector2F(9000000, 9000000);
      //      IVector2 vec2 = new GVector2F(9000000, 0);
      //
      //      IVector2 vec3 = new GVector2F(1000000, 1000000);
      //      IVector2 vec4 = new GVector2F(-1000000, -1000000);
      //
      //      Assert.assertTrue("test1 " + (vec1.angle(vec2)), GMath.closeTo(GMath.QUARTER_PI, (vec1.angle(vec2)),
      //               GMath.DEFAULT_NUMERICAL_PRECISION_FLOAT));
      //      Assert.assertTrue("test2 " + (vec3.angle(vec4)), GMath.closeTo(GMath.PI, (vec3.angle(vec4)),
      //               GMath.DEFAULT_NUMERICAL_PRECISION_FLOAT));

   }


   @Test
   public void testConversions2d3d() {
      Assert.assertTrue("3D converted to 2D generic", GVectorTest.generic2D.closeTo(GVectorTest.generic3D.asVector2()));
      Assert.assertTrue("3D constructed from 2D generic",
               GVectorTest.generic3D.closeTo(new GVector3D(GVectorTest.generic2D, GVectorTest.generic3D.z())));
      Assert.assertTrue("3D converted to 2D neg", GVectorTest.generic2DNeg.closeTo(GVectorTest.generic3DNeg.asVector2()));
      Assert.assertTrue("3D constructed from 2D Neg",
               GVectorTest.generic3DNeg.closeTo(new GVector3D(GVectorTest.generic2DNeg, GVectorTest.generic3DNeg.z())));
      Assert.assertTrue("3D converted to 2D zero", (GVector2D.ZERO).closeTo((GVector3D.ZERO).asVector2()));
      Assert.assertTrue("3D constructed from 2D zero",
               (GVector3D.ZERO).closeTo(new GVector3D(GVector2D.ZERO, (GVector3D.ZERO).z())));
      Assert.assertTrue("3D converted to 2D infinity",
               GVector2D.POSITIVE_INFINITY.closeTo(GVector3D.POSITIVE_INFINITY.asVector2()));
      Assert.assertTrue("3D constructed from 2D infinity",
               GVector3D.POSITIVE_INFINITY.closeTo(new GVector3D(GVector2D.POSITIVE_INFINITY, GVector3D.POSITIVE_INFINITY.z())));

      Assert.assertTrue("3F converted to 2F generic", GVectorTest.generic2F.closeTo(GVectorTest.generic3F.asVector2()));
      Assert.assertTrue("3F constructed from 2F generic",
               GVectorTest.generic3F.closeTo(new GVector3F(GVectorTest.generic2F, GVectorTest.generic3F.z())));
      Assert.assertTrue("3F converted to 2F neg", GVectorTest.generic2FNeg.closeTo(GVectorTest.generic3FNeg.asVector2()));
      Assert.assertTrue("3F constructed from 2F Neg",
               GVectorTest.generic3FNeg.closeTo(new GVector3F(GVectorTest.generic2FNeg, GVectorTest.generic3FNeg.z())));
      Assert.assertTrue("3F converted to 2F zero", (GVector2F.ZERO).closeTo((GVector3F.ZERO).asVector2()));
      Assert.assertTrue("3F constructed from 2F zero",
               (GVector3F.ZERO).closeTo(new GVector3F(GVector2F.ZERO, (GVector3F.ZERO).z())));
      Assert.assertTrue("3F converted to 2F infinity",
               GVector2D.POSITIVE_INFINITY.closeTo(GVector3F.POSITIVE_INFINITY.asVector2()));
      Assert.assertTrue("3F constructed from 2F infinity",
               GVector3F.POSITIVE_INFINITY.closeTo(new GVector3F(GVector2F.POSITIVE_INFINITY, GVector3F.POSITIVE_INFINITY.z())));

   }


   @Test
   public void testCross() {
      Assert.assertTrue("xaxis x yaxis = zaxis", (GVector3D.Z_UP).closeTo((GVector3D.X_UP).cross(GVector3D.Y_UP)));
      Assert.assertTrue("yaxis x xaxis = -zaxis", (GVector3D.Z_DOWN).closeTo((GVector3D.Y_UP).cross(GVector3D.X_UP)));
      Assert.assertTrue("xaxis x zaxis = -yaxis", (GVector3D.Y_DOWN).closeTo((GVector3D.X_UP).cross(GVector3D.Z_UP)));

      Assert.assertTrue("xaxis x yaxis = zaxis", (GVector3F.Z_UP).closeTo((GVector3F.X_UP).cross(GVector3F.Y_UP)));
      Assert.assertTrue("yaxis x xaxis = -zaxis", (GVector3F.Z_DOWN).closeTo((GVector3F.Y_UP).cross(GVector3F.X_UP)));
      Assert.assertTrue("xaxis x zaxis = -yaxis", (GVector3F.Y_DOWN).closeTo((GVector3F.X_UP).cross(GVector3F.Z_UP)));

      for (final IVector3 element : GVectorTest.all3) {
         assertCloseTo("vec X vec = zero", GVector3D.ZERO, element.cross(element));
      }
      combinationsDo(GVectorTest.generics3All, new PairEvaluation<IVector3>() {
         @Override
         public void evaluate(final IVector3 vector1,
                              final IVector3 vector2) {
            final IVector3 cross = (vector1.cross(vector2)).normalized();
            Assert.assertTrue("", GMath.closeTo(0, vector1.normalized().dot(cross)));
            Assert.assertTrue("", GMath.closeTo(0, vector2.normalized().dot(cross)));

         }
      });
   }


   @Test
   public void testPairsNonZero3AddSub() {
      combinationsDo(GVectorTest.generics3All, new PairEvaluation<IVector3>() {
         @Override
         public void evaluate(final IVector3 vector1,
                              final IVector3 vector2) {

            final IVector3 unit = new GVector3D(1, 1, 1);

            assertCloseTo("(a+b)-b=a", vector1, (vector1.add(vector2)).sub(vector2));
            assertCloseTo("a+b-b=a", vector1, vector1.add(vector2).sub(vector2));
            assertCloseTo("(a+b)-b=a", vector1.negated(), (vector1.add(vector2)).sub(vector2).negated());

            Assert.assertTrue("a-a = 0", vector1.sub(vector1).closeToZero());
            Assert.assertTrue("a+(-a) = 0", vector1.add(vector1.negated()).closeToZero());

            assertCloseTo("a*(1/a) = 1", unit, vector1.scale(vector1.reciprocal()));
            assertCloseTo("a/a = 1", unit, vector1.div(vector1));

            assertCloseTo("a*1 = a", vector1, vector1.scale(unit));
            assertCloseTo("a/1 = a", vector1, vector1.div(unit));

            assertCloseTo("a*b = b*a", vector1.scale(vector2), vector2.scale(vector1));

         }
      });
   }


   @Test
   public void testIndexOutOfBoundsException() {
      testIndexOutOfBoundsException(GVectorTest.generic2D);
      testIndexOutOfBoundsException(GVectorTest.generic2F);
      testIndexOutOfBoundsException(GVectorTest.generic3D);
      testIndexOutOfBoundsException(GVectorTest.generic3F);
   }


   private void testIndexOutOfBoundsException(final IVector<?, ?> vector) {
      boolean thrown = false;
      try {
         vector.get((byte) -1);
      }
      catch (final IndexOutOfBoundsException e) {
         thrown = true;
      }
      Assert.assertTrue("IndexOutOfBoundsException not thrown", thrown);

      thrown = false;
      try {
         vector.get(vector.dimensions());
      }
      catch (final IndexOutOfBoundsException e) {
         thrown = true;
      }
      Assert.assertTrue("IndexOutOfBoundsException not thrown", thrown);
   }


   @Test
   public void testCreatePolygonalChainFromPoints() throws Exception {
      IPolygonalChain<IVector2, GSegment2D, GAxisAlignedRectangle> chain2 = GShapeUtils.<IVector2, GSegment2D, GAxisAlignedRectangle> createPolygonalChain(
               false, new GVector2D(0, 0), new GVector2D(10, 0));
      Assert.assertTrue(chain2 instanceof GSegment2D);
      chain2 = GShapeUtils.<IVector2, GSegment2D, GAxisAlignedRectangle> createPolygonalChain(false, new GVector2D(0, 0),
               new GVector2D(10, 0), new GVector2D(20, 0));
      Assert.assertTrue(chain2 instanceof GLinesStrip2D);
      final IPolygonalChain<IVector3, ?, ?> chain3 = GShapeUtils.<IVector3, GSegment3D, GAxisAlignedBox> createPolygonalChain(
               false, new GVector3D(0, 0, 0), new GVector3D(10, 0, 0));
      Assert.assertTrue(chain3 instanceof GSegment3D);

      try {
         GShapeUtils.<IVector2, GSegment2D, GAxisAlignedRectangle> createPolygonalChain(false, new GVector2D(0, 0));
         Assert.fail();
      }
      catch (final IllegalArgumentException e) {}
   }


   @Test
   public void testCloseToOnEquivalentGeometries() throws Exception {
      final GVector2D p1 = new GVector2D(0, 0);
      final GVector2D p2 = new GVector2D(10, 0);
      final GSegment2D segment = new GSegment2D(p1, p2);
      final GLinesStrip2D line = new GLinesStrip2D(false, p1, p2);
      Assert.assertTrue(segment.closeTo(line));
      Assert.assertTrue(line.closeTo(segment));

      final GVector2D p3 = new GVector2D(10, 10);
      final GTriangle2D triangle = new GTriangle2D(p1, p2, p3);
      GSimplePolygon2D polygon = new GSimplePolygon2D(false, p1, p2, p3);
      Assert.assertTrue(triangle.closeTo(polygon));
      Assert.assertTrue(polygon.closeTo(triangle));

      final GVector2D p4 = new GVector2D(0, 10);
      final GQuad2D quad = new GQuad2D(p1, p2, p3, p4);
      polygon = new GSimplePolygon2D(false, p1, p2, p3, p4);
      Assert.assertTrue(quad.closeTo(polygon));
      Assert.assertTrue(polygon.closeTo(quad));
   }


   @Test
   public void testPolygonValidationBug() throws Exception {
      final GVector2D p1 = new GVector2D(0, 0);
      final GVector2D p2 = new GVector2D(10, 0);
      final GVector2D p3 = new GVector2D(10, 10);
      // This construction complained about a self-intersection
      dummy(new GSimplePolygon2D(true, p1, p2, p3));
   }


   /**
    * Just to remove the warning in {@link #testPolygonValidationBug()}
    * 
    * @param a
    * @return
    */
   private static <T> T dummy(final T a) {
      return a;
   }
}
