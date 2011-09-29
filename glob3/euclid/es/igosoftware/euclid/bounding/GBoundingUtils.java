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


package es.igosoftware.euclid.bounding;

import es.igosoftware.euclid.vector.IVector3;


public final class GBoundingUtils {

   private GBoundingUtils() {
   }


   //Boxes
   public static boolean touchesWithBox(final GAxisAlignedBox box1,
                                        final GAxisAlignedBox box2) {
      // from Real-Time Collision Detection - Christer Ericson
      //   page 79

      final IVector3 lower1 = box1._lower;
      final IVector3 upper1 = box1._upper;
      final IVector3 lower2 = box2._lower;
      final IVector3 upper2 = box2._upper;

      // Exit with no intersection if separated along an axis
      if ((upper1.x() < lower2.x()) || (lower1.x() > upper2.x())) {
         return false;
      }
      if ((upper1.y() < lower2.y()) || (lower1.y() > upper2.y())) {
         return false;
      }
      if ((upper1.z() < lower2.z()) || (lower1.z() > upper2.z())) {
         return false;
      }

      // Overlapping on all axes means AABBs are intersecting
      return true;
   }


   //   public static void main(final String[] args) {
   //      test(5.1);
   //      test(10000000000000000000000000000000000000000d);
   //      test(0);
   //      test(1);
   //      test(-1);
   //   }
   //
   //
   //   private static void test(final double d) {
   //      //      final double greater = Math.nextAfter(d, Double.POSITIVE_INFINITY);
   //      //      final double less = Math.nextAfter(d, Double.NEGATIVE_INFINITY);
   //      final double greater = GMath.nextUp(d);
   //      final double less = GMath.previousDown(d);
   //
   //      System.out.println(d + " greater: " + greater + " less: " + less);
   //      if (!(greater > d)) {
   //         System.err.println("greater not greater");
   //      }
   //      if (!(less < d)) {
   //         System.err.println("less not less");
   //      }
   //
   //   }

}
