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

import java.util.Arrays;


public final class GMath {
   public static final double  LOG_10      = Math.log(10);
   public static final double  LOG_2       = Math.log(2);

   public static final double  PI          = 3.14159265358979323846264338327950288;

   public static final double  TWO_PI      = PI * 2;
   public static final double  HALF_PI     = PI / 2;
   public static final double  QUARTER_PI  = PI / 4;

   public static final double  DEGREES_60  = Math.toRadians(60);
   public static final double  DEGREES_360 = TWO_PI;

   private static final int    RADIX;

   private static final double MACHINE_PRECISION_DOUBLE;
   private static final float  MACHINE_PRECISION_FLOAT;

   public static final double  DEFAULT_NUMERICAL_PRECISION_DOUBLE;
   public static final float   DEFAULT_NUMERICAL_PRECISION_FLOAT;

   static {
      RADIX = computeRadix();

      MACHINE_PRECISION_DOUBLE = computeMachinePrecisionDouble();
      MACHINE_PRECISION_FLOAT = computeMachinePrecisionFloat();

      DEFAULT_NUMERICAL_PRECISION_DOUBLE = Math.sqrt(MACHINE_PRECISION_DOUBLE);

      DEFAULT_NUMERICAL_PRECISION_FLOAT = (float) Math.sqrt(MACHINE_PRECISION_FLOAT);

      //      System.out.println("Machine Double Precision: " + MACHINE_PRECISION_DOUBLE + " " + Math.pow(2, -53));
      //      System.out.println("Machine Float Precision: " + MACHINE_PRECISION_FLOAT + " " + (float) Math.pow(2, -24));
      //      System.out.println();
      //      System.out.println("Numerical Double Precision: " + DEFAULT_NUMERICAL_PRECISION_DOUBLE);
      //      System.out.println("Numerical Float Precision: " + DEFAULT_NUMERICAL_PRECISION_FLOAT);
   }


   private static int computeRadix() {
      double a = 1.0d;
      double tmp1, tmp2;
      do {
         a += a;
         tmp1 = a + 1.0d;
         tmp2 = tmp1 - a;
      }
      while (tmp2 - 1.0d != 0.0d);
      double b = 1.0d;
      int radix = 0;
      while (radix == 0) {
         b += b;
         tmp1 = a + b;
         radix = (int) (tmp1 - a);
      }
      return radix;
   }


   private static double computeMachinePrecisionDouble() {
      final double inverseRadix = 1.0d / RADIX;
      double machinePrecision = 1.0d;
      double tmp = 1.0d + machinePrecision;
      while (tmp - 1.0d != 0.0d) {
         machinePrecision *= inverseRadix;
         tmp = 1.0d + machinePrecision;
      }
      return machinePrecision;
   }


   private static float computeMachinePrecisionFloat() {
      final float inverseRadix = 1.0f / RADIX;
      float machinePrecision = 1.0f;
      float tmp = 1.0f + machinePrecision;
      while (tmp - 1.0f != 0.0f) {
         machinePrecision *= inverseRadix;
         tmp = 1.0f + machinePrecision;
      }
      return machinePrecision;
   }


   private GMath() {
   }


   public static boolean greaterOrEquals(final double value,
                                         final double reference) {
      return (value >= reference) || closeTo(value, reference);
   }


   public static boolean greaterOrEquals(final double value,
                                         final double reference,
                                         final double precision) {
      return (value >= reference) || closeTo(value, reference, precision);
   }


   public static boolean greaterOrEquals(final float value,
                                         final float reference) {
      return (value >= reference) || closeTo(value, reference);
   }


   public static boolean greaterOrEquals(final int value,
                                         final int reference) {
      return (value >= reference);
   }


   public static boolean greaterOrEquals(final long value,
                                         final long reference) {
      return (value >= reference);
   }


   public static boolean lessOrEquals(final double value,
                                      final double reference) {
      return (value <= reference) || closeTo(value, reference);
   }


   public static boolean lessOrEquals(final double value,
                                      final double reference,
                                      final double precision) {
      return (value <= reference) || closeTo(value, reference, precision);
   }


   public static boolean lessOrEquals(final float value,
                                      final float reference) {
      return (value <= reference) || closeTo(value, reference);
   }


   public static boolean lessOrEquals(final int value,
                                      final int reference) {
      return (value <= reference);
   }


   public static boolean lessOrEquals(final long value,
                                      final long reference) {
      return (value <= reference);
   }


   public static boolean between(final double value,
                                 final double min,
                                 final double max,
                                 final double precision) {
      return ((value >= min) && (value <= max)) || closeTo(value, min, precision) || closeTo(value, max, precision);
   }


   public static boolean between(final float value,
                                 final float min,
                                 final float max,
                                 final float precision) {
      return ((value >= min) && (value <= max)) || closeTo(value, min, precision) || closeTo(value, max, precision);
   }


   public static boolean between(final double value,
                                 final double min,
                                 final double max) {
      return ((value >= min) && (value <= max)) || closeTo(value, min) || closeTo(value, max);
   }


   public static boolean between(final float value,
                                 final float min,
                                 final float max) {
      return ((value >= min) && (value <= max)) || closeTo(value, min) || closeTo(value, max);
   }


   public static boolean between(final int value,
                                 final int min,
                                 final int max) {
      return ((value >= min) && (value <= max));
   }


   public static boolean between(final long value,
                                 final long min,
                                 final long max) {
      return ((value >= min) && (value <= max));
   }


   public static double clamp(final double value,
                              final double min,
                              final double max) {
      return (value < min) ? min : ((value > max) ? max : value);
   }


   public static float clamp(final float value,
                             final float min,
                             final float max) {
      return (value < min) ? min : ((value > max) ? max : value);
   }


   public static int clamp(final int value,
                           final int min,
                           final int max) {
      return (value < min) ? min : ((value > max) ? max : value);
   }


   public static long clamp(final long value,
                            final long min,
                            final long max) {
      return (value < min) ? min : ((value > max) ? max : value);
   }


   public static boolean closeTo(final double num1,
                                 final double num2) {
      return closeTo(num1, num2, DEFAULT_NUMERICAL_PRECISION_DOUBLE);
   }


   public static boolean closeTo(final double num1,
                                 final float num2) {
      return closeTo(num1, num2, DEFAULT_NUMERICAL_PRECISION_FLOAT);
   }


   public static boolean closeTo(final float num1,
                                 final double num2) {
      return closeTo(num1, num2, DEFAULT_NUMERICAL_PRECISION_FLOAT);
   }


   public static boolean closeTo(final double a,
                                 final double b,
                                 final double precision) {

      if (Double.isNaN(a) || Double.isNaN(b)) {
         return false;
      }

      if (Double.doubleToLongBits(a) == Double.doubleToLongBits(b)) {
         return true;
      }

      if (Double.isInfinite(a)) {
         return false;
      }
      if (Double.isInfinite(b)) {
         return false;
      }


      final double norm = Math.max(Math.abs(a), Math.abs(b));
      return (norm < precision) || (Math.abs(a - b) <= (precision * Math.max(norm, 1)));
      //return (norm < precision) || (Math.abs(a - b) <= precision);
   }


   public static boolean closeTo(final float num1,
                                 final float num2) {
      return closeTo(num1, num2, DEFAULT_NUMERICAL_PRECISION_FLOAT);
   }


   public static boolean closeTo(final float a,
                                 final float b,
                                 final float precision) {

      if (Float.isNaN(a) || Float.isNaN(b)) {
         return false;
      }

      if (Float.floatToIntBits(a) == Float.floatToIntBits(b)) {
         return true;
      }


      if (Float.isInfinite(a)) {
         return false;
      }
      if (Float.isInfinite(b)) {
         return false;
      }


      final double norm = Math.max(Math.abs(a), Math.abs(b));
      return (norm < precision) || (Math.abs(a - b) <= (precision * Math.max(norm, 1)));
      //return (norm < precision) || (Math.abs(a - b) <= precision);
   }


   public static boolean closeToZero(final double num) {
      return Math.abs(num) < DEFAULT_NUMERICAL_PRECISION_DOUBLE;
   }


   public static boolean closeToZero(final float num) {
      return Math.abs(num) < DEFAULT_NUMERICAL_PRECISION_FLOAT;
   }


   public static double interpolate(final double from,
                                    final double to,
                                    final double alpha) {
      return from + (to - from) * alpha;
   }


   public static float interpolate(final float from,
                                   final float to,
                                   final float alpha) {
      return from + (to - from) * alpha;
   }


   public static double roundTo(final double value,
                                final int decimals) {
      final double factor = 1 / Math.pow(10, decimals);

      return roundTo(value, factor);
   }


   public static float roundTo(final float value,
                               final int decimals) {
      final float factor = 1 / (float) Math.pow(10, decimals);

      return roundTo(value, factor);
   }


   public static double roundTo(final double value,
                                final double scale) {
      return Math.round(value / scale) * scale;
   }


   public static float roundTo(final float value,
                               final float scale) {
      return Math.round(value / scale) * scale;
   }


   //   public static double max(final double value1) {
   //      return value1;
   //   }


   //   public static double max(final double value1,
   //                            final double value2) {
   //      if (value1 > value2) {
   //         return value1;
   //      }
   //      return value2;
   //   }


   public static double maxD(final double... values) {
      if (values.length == 0) {
         throw new IllegalArgumentException("Empty values");
      }

      double max = values[0];
      for (int i = 1; i < values.length; i++) {
         if (values[i] > max) {
            max = values[i];
         }
      }

      return max;
   }


   public static float maxF(final float... values) {
      if (values.length == 0) {
         throw new IllegalArgumentException("Empty values");
      }

      float max = values[0];
      for (int i = 1; i < values.length; i++) {
         if (values[i] > max) {
            max = values[i];
         }
      }

      return max;
   }


   public static float minF(final float... values) {
      if (values.length == 0) {
         throw new IllegalArgumentException("Empty values");
      }

      float min = values[0];
      for (int i = 1; i < values.length; i++) {
         if (values[i] < min) {
            min = values[i];
         }
      }

      return min;
   }


   public static double minD(final double... values) {
      if (values.length == 0) {
         throw new IllegalArgumentException("Empty values");
      }

      double min = values[0];
      for (int i = 1; i < values.length; i++) {
         if (values[i] < min) {
            min = values[i];
         }
      }

      return min;
   }


   //   public static double mul(final double value1) {
   //      return value1;
   //   }
   //
   //
   //   public static double mul(final double value1,
   //                            final double value2) {
   //      return value1 * value2;
   //   }
   //
   //
   //   public static double mul(final double value1,
   //                            final double value2,
   //                            final double value3) {
   //      return value1 * value2 * value3;
   //   }
   //
   //
   //   public static double mul(final double... values) {
   //      if (values.length == 0) {
   //         throw new IllegalArgumentException("Empty values");
   //      }
   //
   //      double mul = values[0];
   //      for (int i = 1; i < values.length; i++) {
   //         mul *= values[i];
   //      }
   //
   //      return mul;
   //   }


   public static double kahanSum(final double... values) {
      // from http://en.wikipedia.org/wiki/Kahan_summation_algorithm

      if (values.length == 0) {
         return 0;
      }

      double sum = values[0];
      double compensation = 0; //A running compensation for lost low-order bits.

      for (int i = 1; i < values.length; i++) {
         final double y = values[i] - compensation; //So far, so good: c is zero.
         final double t = sum + y; //Alas, sum is big, y small, so low-order digits of y are lost.
         compensation = (t - sum) - y; //(t - sum) recovers the high-order part of y; subtracting y recovers -(low part of y)
         sum = t; //Algebraically, c should always be zero. Beware eagerly optimising compilers!
      }

      return sum;
   }


   public static float kahanSum(final float... values) {
      // from http://en.wikipedia.org/wiki/Kahan_summation_algorithm

      if (values.length == 0) {
         return 0;
      }

      float sum = values[0];
      float compensation = 0; //A running compensation for lost low-order bits.

      for (int i = 1; i < values.length; i++) {
         final float y = values[i] - compensation; //So far, so good: c is zero.
         final float t = sum + y; //Alas, sum is big, y small, so low-order digits of y are lost.
         compensation = (t - sum) - y; //(t - sum) recovers the high-order part of y; subtracting y recovers -(low part of y)
         sum = t; //Algebraically, c should always be zero. Beware eagerly optimising compilers!
      }

      return sum;
   }


   public static float sum(final float... values) {
      return kahanSum(values);
   }


   public static double sum(final double... values) {
      return kahanSum(values);
   }


   public static float plainSum(final float... values) {
      float sum = 0;
      for (final float value : values) {
         sum += value;
      }
      return sum;
   }


   public static double plainSum(final double... values) {
      double sum = 0;
      for (final double value : values) {
         sum += value;
      }
      return sum;
   }


   public static boolean negativeOrZero(final double value) {
      return (value <= 0) || closeToZero(value);
   }


   public static boolean negativeOrZero(final float value) {
      return (value <= 0) || closeToZero(value);
   }


   public static boolean positiveOrZero(final double value) {
      return (value > 0) || closeToZero(value);
   }


   public static boolean closeToZero(final double... values) {
      for (final double value : values) {
         if (!closeToZero(value)) {
            return false;
         }
      }
      return true;
   }


   public static boolean closeToOne(final double... values) {
      for (final double value : values) {
         if (!closeTo(value, 1)) {
            return false;
         }
      }
      return true;
   }


   private static double reduceSinAngle(final double rawRadians) {
      double radians = rawRadians % TWO_PI; // put us in -2PI to +2PI space

      if (Math.abs(radians) > PI) { // put us in -PI to +PI space
         radians -= TWO_PI;
      }
      if (Math.abs(radians) > HALF_PI) {// put us in -PI/2 to +PI/2 space
         radians = PI - radians;
      }

      return radians;
   }


   public static double sin(final double rawRadians) {


      final double radians = reduceSinAngle(rawRadians); // limits angle to between -PI/2 and +PI/2

      final double result;
      if (Math.abs(radians) <= QUARTER_PI) {
         result = Math.sin(radians);
      }
      else {
         result = Math.cos(HALF_PI - radians);
      }


      //final double result = Math.sin(radians);


      if (closeToZero(result)) {
         return 0;
      }
      if (closeTo(result, 1)) {
         return 1;
      }
      if (closeTo(result, -1)) {
         return -1;
      }
      return result;
   }


   public static double cos(final double radians) {
      return sin(radians + HALF_PI);
   }


   public static double previousDown(final double number) {
      return Math.nextAfter(number, Double.NEGATIVE_INFINITY);
   }


   public static double nextUp(final double number) {
      // return Math.nextAfter(value, Double.POSITIVE_INFINITY);
      return Math.nextUp(number);
   }


   public static float previousDown(final float number) {
      return Math.nextAfter(number, Double.NEGATIVE_INFINITY);
   }


   public static float nextUp(final float number) {
      // return Math.nextAfter(value, Double.POSITIVE_INFINITY);
      return Math.nextUp(number);
   }


   public static double log2(final double x) {
      return Math.log(x) / LOG_2;
   }


   public static int pow(final int a,
                         final int b) {
      return (int) Math.pow(a, b);
   }


   public static double getMedian(final double[] values) {
      final int valuesSize = values.length;
      assert (valuesSize > 0) : values;

      // short cut for size 1
      if (valuesSize == 1) {
         return values[0];
      }

      // short cut for size 2
      if (valuesSize == 2) {
         return (values[0] + values[1]) / 2.0;
      }

      Arrays.sort(values);

      final int middle = valuesSize / 2;

      if ((valuesSize % 2) == 0) {
         return (values[middle - 1] + values[middle]) / 2.0;
      }

      return values[middle];
   }


   public static float getMedian(final float[] values) {
      final int valuesSize = values.length;
      assert (valuesSize > 0) : values;

      // short cut for size 1
      if (valuesSize == 1) {
         return values[0];
      }

      // short cut for size 2
      if (valuesSize == 2) {
         return (values[0] + values[1]) / 2f;
      }

      Arrays.sort(values);

      final int middle = valuesSize / 2;

      if ((valuesSize % 2) == 0) {
         return (values[middle - 1] + values[middle]) / 2f;
      }

      return values[middle];
   }


   public static float pseudoModule(final float numerator,
                                    final float denominator) {

      final float result = numerator / denominator;
      final int intPart = (int) result; //integer part 
      final float fracPart = result - intPart; //fractional part

      if (closeTo(fracPart, 1.0f)) {
         return 0;
      }

      return fracPart * denominator;
   }


   public static double pseudoModule(final double numerator,
                                     final double denominator) {

      final double result = numerator / denominator;
      final long intPart = (long) result; //integer part 
      final double fracPart = result - intPart; //fractional part

      if (closeTo(fracPart, 1.0d)) {
         return 0;
      }

      return fracPart * denominator;
   }


   //   public static int gcd(final int p,
   //                         final int q) {
   //      if (q == 0) {
   //         return p;
   //      }
   //      return gcd(q, p % q);
   //   }
   //
   //
   //   public static long gcd(final long p,
   //                          final long q) {
   //      if (q == 0) {
   //         return p;
   //      }
   //      return gcd(q, p % q);
   //   }


   /*
    * "Nice Numbers for Graph Labels" by Paul Heckbert
    * from "Graphics Gems", Academic Press, 1990
    */
   public static double niceNumber(final double x,
                                   final boolean round) {

      /* exponent of x */
      final int expv = (int) Math.floor(Math.log(x) / LOG_10);

      final double tenPowExpv = Math.pow(10, expv);

      /* fractional part of x */
      final double fraction = x / tenPowExpv; /* between 1 and 10 */

      final double niceFraction = getRoundedFraction(round, fraction);

      return niceFraction * tenPowExpv;
   }


   private static double getRoundedFraction(final boolean round,
                                            final double fraction) {

      if (round) {
         if (fraction < 1.5) {
            return 1;
         }
         else if (fraction < 3) {
            return 2;
         }
         else if (fraction < 7) {
            return 5;
         }
         else {
            return 10;
         }
      }
      else if (fraction <= 1) {
         return 1;
      }
      else if (fraction <= 2) {
         return 2;
      }
      else if (fraction <= 5) {
         return 5;
      }
      else {
         return 10;
      }
   }


   public static int toInt(final long longValue) {
      if ((longValue < Integer.MIN_VALUE) || (longValue > Integer.MAX_VALUE)) {
         throw new RuntimeException("Can't cast the long " + longValue + " into an int");
      }
      return (int) longValue;
   }


   public static int toRoundedInt(final double doubleValue) {
      return toInt(Math.round(doubleValue));
   }


   public static boolean isDivisible(final int value,
                                     final int divisor) {
      return (value % divisor) == 0;
   }


   public static boolean isDivisible(final long value,
                                     final long divisor) {
      return (value % divisor) == 0;
   }


   public static int integerDivisionBy(final int value,
                                       final int[] divisors,
                                       final int defaultValue) {
      for (final int divisor : divisors) {
         if (GMath.isDivisible(value, divisor)) {
            return value / divisor;
         }
      }
      return defaultValue;
   }


   public static double sqrt(final double value) {
      if (closeToZero(value)) {
         return 0;
      }

      return Math.sqrt(value);
   }


   public static double average(final double value1,
                                final double value2) {
      return (value1 + value2) / 2;
   }


   public static double average(final double value1,
                                final double value2,
                                final double value3) {
      return (value1 + value2 + value3) / 3;
   }


   public static double squared(final double value) {
      return value * value;
   }


   public static double squared(final float value) {
      return (double) value * value;
   }


   public static long squared(final int value) {
      return (long) value * value;
   }


   public static long squared(final long value) {
      return value * value;
   }

}
