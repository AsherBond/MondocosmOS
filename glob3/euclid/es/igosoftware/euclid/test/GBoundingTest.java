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

import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.vector.GVector3D;


public class GBoundingTest {

   private static final GVector3D       doubleUnit     = GVector3D.UNIT.scale(2);
   private static final GVector3D       doubleUnitNeg  = GVector3D.UNIT.scale(-2);


   private static final GAxisAlignedBox unitCube       = new GAxisAlignedBox(GVector3D.NEGATIVE_UNIT, GVector3D.UNIT);
   private static final GAxisAlignedBox doubleUnitCube = new GAxisAlignedBox(GBoundingTest.doubleUnitNeg,
                                                                GBoundingTest.doubleUnit);
   private static final GAxisAlignedBox cube111222     = new GAxisAlignedBox(GVector3D.UNIT, GBoundingTest.doubleUnit);
   private static final GAxisAlignedBox cube111222Neg  = new GAxisAlignedBox(GBoundingTest.doubleUnitNeg, GVector3D.NEGATIVE_UNIT);
   private static final GAxisAlignedBox cube000222Neg  = new GAxisAlignedBox(GVector3D.ZERO, GBoundingTest.doubleUnit);


   @Test
   public void boxBoxTest() {

      //case inside
      Assert.assertTrue("doubleUnitCube touches unitCube", GBoundingTest.doubleUnitCube.touchesWithBox(GBoundingTest.unitCube));
      Assert.assertTrue("unitCube touches doubleUnitCube", GBoundingTest.unitCube.touchesWithBox(GBoundingTest.doubleUnitCube));

      //case equals
      Assert.assertTrue("unitCube touches itself", GBoundingTest.unitCube.touchesWithBox(GBoundingTest.unitCube));

      //case outside
      Assert.assertFalse("cube111222 does not touch cube111222Neg",
               GBoundingTest.cube111222.touchesWithBox(GBoundingTest.cube111222Neg));
      Assert.assertFalse("cube111222Neg does not touch cube111222",
               GBoundingTest.cube111222.touchesWithBox(GBoundingTest.cube111222Neg));

      //case intersect
      Assert.assertTrue("cube000222Neg touches unitCube", GBoundingTest.cube000222Neg.touchesWithBox(GBoundingTest.unitCube));
      Assert.assertTrue("unitCube touches cube000222Neg", GBoundingTest.unitCube.touchesWithBox(GBoundingTest.cube000222Neg));

      //case intersect in one point
      Assert.assertTrue("unitCube touches cube111222", GBoundingTest.unitCube.touchesWithBox(GBoundingTest.cube111222));

   }


   @Test
   public void testContains() {
      testContains(new GAxisAlignedBox(new GVector3D(0, 0, 0), new GVector3D(100, 100, 100)));
      testContains(new GAxisAlignedBox(new GVector3D(-100, -10, -1), new GVector3D(100, 10, 1)));
   }


   private void testContains(final GAxisAlignedBox bounds) {
      final double delta = 0.00001;

      Assert.assertTrue("lower in box", bounds.contains(bounds._lower));
      Assert.assertTrue("upper in box", bounds.contains(bounds._upper));

      Assert.assertFalse("lessThanLower in box", bounds.contains(bounds._lower.sub(delta)));
      Assert.assertFalse("greaterThanUpper in box", bounds.contains(bounds._upper.add(delta)));

      final double step = 0.4;
      for (double x = bounds._lower.x(); x < bounds._upper.x(); x += step) {
         for (double y = bounds._lower.y(); y < bounds._upper.y(); y += step) {
            for (double z = bounds._lower.z(); z < bounds._upper.z(); z += step) {
               final GVector3D point = new GVector3D(x, y, z);
               assertContains(bounds, point);
            }
         }
      }

   }


   private void assertContains(final GAxisAlignedBox bounds,
                               final GVector3D point) {
      final boolean contains = bounds.contains(point);
      if (!contains) {
         Assert.fail(point + " in " + bounds);
      }
   }

}
