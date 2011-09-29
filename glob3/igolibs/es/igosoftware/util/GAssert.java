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

import java.util.Collection;

import es.igosoftware.logging.GLogger;
import es.igosoftware.logging.ILogger;


public final class GAssert {
   private static final ILogger LOGGER = GLogger.instance();


   public static void notEmpty(final Collection<?> collection,
                               final String description) {
      if (collection == null) {
         processNull(description);
      }
      else if (collection.isEmpty()) {
         processEmpty(description);
      }
   }


   public static void notNullElements(final Collection<?> collection,
                                      final String description) {
      for (final Object element : collection) {
         if (element == null) {
            processNull(description + " element ");
         }
      }
   }


   public static void notNullElements(final Object[] collection,
                                      final String description) {
      for (final Object element : collection) {
         if (element == null) {
            processNull(description + " element ");
         }
      }
   }


   public static void notEmpty(final Object[] array,
                               final String description) {
      if (array == null) {
         processNull(description);
      }
      else if (array.length == 0) {
         processEmpty(description);
      }
   }


   public static void notEmpty(final Iterable<?> iterable,
                               final String description) {
      if (iterable == null) {
         processNull(description);
      }
      else if (!iterable.iterator().hasNext()) {
         processEmpty(description);
      }
   }


   public static void notNull(final Object object,
                              final String description) {
      if (object != null) {
         return;
      }
      processNull(description);
   }


   private static void processEmpty(final String description) {
      final String msg = description + " is empty";
      throwError(msg);
   }


   private static void processNull(final String description) {
      final String msg = description + " is null";
      throwError(msg);
   }


   private static void throwError(final String msg) {
      LOGGER.logSevere(msg);
      throw new IllegalArgumentException(msg);
   }


   private GAssert() {
   }


   public static void isPositiveOrZero(final int value,
                                       final String description) {
      if (value >= 0) {
         return;
      }
      throwError(description + " must be positive or zero (" + value + ")");
   }


   public static void isPositiveOrZero(final float value,
                                       final String description) {
      if (value >= 0) {
         return;
      }
      throwError(description + " must be positive or zero (" + value + ")");
   }


   public static void isPositive(final int value,
                                 final String description) {
      if (value > 0) {
         return;
      }
      throwError(description + " must be positive (" + value + ")");
   }


   public static void isPositive(final long value,
                                 final String description) {
      if (value > 0) {
         return;
      }
      throwError(description + " must be positive (" + value + ")");
   }


   public static void isPositive(final double value,
                                 final String description) {
      if (value > 0) {
         return;
      }
      throwError(description + " must be positive (" + value + ")");
   }


   public static void isBetween(final int value,
                                final int min,
                                final int max,
                                final String description) {
      if ((value < min) || (value > max)) {
         throwError(description + " (" + value + ") " + " is not between " + min + " and " + max);
      }
   }


   public static void notNan(final float value,
                             final String description) {
      if (Float.isNaN(value)) {
         throwError(description + " is Nan");
      }
   }


   public static void notNan(final double value,
                             final String description) {
      if (Double.isNaN(value)) {
         throwError(description + " is Nan");
      }
   }


   public static void notEmpty(final double[] array,
                               final String description) {
      if (array == null) {
         processNull(description);
      }
      else if (array.length == 0) {
         processEmpty(description);
      }
   }


   public static void notEmpty(final float[] array,
                               final String description) {
      if (array == null) {
         processNull(description);
      }
      else if (array.length == 0) {
         processEmpty(description);
      }
   }


   public static void notEmpty(final int[] array,
                               final String description) {
      if (array == null) {
         processNull(description);
      }
      else if (array.length == 0) {
         processEmpty(description);
      }
   }


   public static void notEmpty(final long[] array,
                               final String description) {
      if (array == null) {
         processNull(description);
      }
      else if (array.length == 0) {
         processEmpty(description);
      }
   }


   private static void processSizeError(final String description,
                                        final int expected,
                                        final int current) {
      final String msg = description + " size is incorrect (expected" + expected + " but got " + current + ")";
      throwError(msg);
   }


   public static void isSize(final double[] array,
                             final int size,
                             final String description) {
      if (array == null) {
         processNull(description);
      }
      else if (array.length != size) {
         processSizeError(description, size, array.length);
      }
   }


   public static void isTrue(final boolean bool,
                             final String description) {
      if (!bool) {
         throwError(description);
      }
   }


   public static void isFalse(final boolean bool,
                              final String description) {
      if (bool) {
         throwError(description);
      }
   }


   public static void isSame(final Object object0,
                             final Object object1) {
      if (object0 != object1) {
         throwError(object0 + " is not the same object that " + object1);
      }
   }


   public static void isInstanceOf(final Object object,
                                   final Class<?> klass,
                                   final String description) {
      if (!klass.isInstance(object)) {
         throwError(description + ": is not instance of " + klass);
      }
   }


}
