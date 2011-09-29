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


package es.igosoftware.util;

import static es.igosoftware.util.GComparableUtils.max;
import static es.igosoftware.util.GComparableUtils.min;

import java.util.Iterator;


public class GRange<T extends Comparable<T>> {

   public final T _lower;
   public final T _upper;


   public GRange(final T lower,
                 final T upper) {
      GAssert.notNull(lower, "lower");
      GAssert.notNull(upper, "upper");


      final T max;
      final T min;
      if (lower.compareTo(upper) > 0) {
         min = upper;
         max = lower;
      }
      else {
         min = lower;
         max = upper;
      }

      _lower = min;
      _upper = max;
   }


   @Override
   public String toString() {
      return "GRange [" + _lower + " -> " + _upper + "]";
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_lower == null) ? 0 : _lower.hashCode());
      result = prime * result + ((_upper == null) ? 0 : _upper.hashCode());
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

      @SuppressWarnings("unchecked")
      final GRange<T> other = (GRange<T>) obj;
      if (_lower == null) {
         if (other._lower != null) {
            return false;
         }
      }
      else if (_lower.compareTo(other._lower) != 0) {
         return false;
      }
      if (_upper == null) {
         if (other._upper != null) {
            return false;
         }
      }
      else if (_upper.compareTo(other._upper) != 0) {
         return false;
      }
      return true;
   }


   public T getLower() {
      return _lower;
   }


   public T getUpper() {
      return _upper;
   }


   public GRange<T> mergedWith(final GRange<T> that) {
      final T lower = min(_lower, that._lower);
      final T upper = max(_upper, that._upper);

      return new GRange<T>(lower, upper);
   }


   public GRange<T> mergedWith(final GRange<T>... ranges) {
      GAssert.notEmpty(ranges, "ranges");

      T lower = _lower;
      T upper = _upper;


      for (final GRange<T> range : ranges) {
         lower = min(lower, range._lower);
         upper = max(upper, range._upper);
      }

      return new GRange<T>(lower, upper);
   }


   public static <T extends Comparable<T>> GRange<T> merge(final GRange<T>... ranges) {
      GAssert.notEmpty(ranges, "ranges");

      T lower = ranges[0]._lower;
      T upper = ranges[0]._upper;


      for (int i = 1; i < ranges.length; i++) {
         final GRange<T> range = ranges[i];

         lower = min(lower, range._lower);
         upper = max(upper, range._upper);
      }

      return new GRange<T>(lower, upper);
   }


   public static <T extends Comparable<T>> GRange<T> merge(final Iterable<GRange<T>> ranges) {
      GAssert.notNull(ranges, "ranges");

      final Iterator<GRange<T>> iterator = ranges.iterator();
      if (!iterator.hasNext()) {
         return null;
      }

      final GRange<T> first = iterator.next();
      T lower = first._lower;
      T upper = first._upper;

      while (iterator.hasNext()) {
         final GRange<T> range = iterator.next();

         lower = min(lower, range._lower);
         upper = max(upper, range._upper);
      }

      return new GRange<T>(lower, upper);
   }


   //   public static void main(final String[] args) {
   //      System.out.println("GRange 0.1");
   //      System.out.println("----------\n");
   //
   //      final GRange<Double> range = new GRange<Double>(1.0, 10.0);
   //      System.out.println(range);
   //      final GRange<Double> range2 = range.mergedWith(new GRange<Double>(2.0, 2.0));
   //      System.out.println(range2);
   //   }


}
