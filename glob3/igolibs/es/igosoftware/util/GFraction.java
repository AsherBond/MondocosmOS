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

import java.io.Serializable;


public class GFraction
         implements
            Serializable,
            Comparable<GFraction> {

   private static final long     serialVersionUID = 1L;


   public static final GFraction ZERO             = create(0, 1);
   public static final GFraction ONE              = create(1, 1);
   public static final GFraction MAX_VALUE        = create(Long.MAX_VALUE, 1);
   public static final GFraction MIN_VALUE        = create(Long.MIN_VALUE, 1);


   private final long            _numerator;
   private final long            _denominator;


   public static GFraction create(final long numerator,
                                  final long denominator) {
      return new GFraction(numerator, denominator).reduced();
   }


   private GFraction(final long numerator,
                     final long denominator) {
      if (denominator == 0) {
         throw new RuntimeException("denominator can't be zero");
      }

      _numerator = numerator;
      _denominator = denominator;
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (_denominator ^ (_denominator >>> 32));
      result = prime * result + (int) (_numerator ^ (_numerator >>> 32));
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
      final GFraction other = (GFraction) obj;
      if (_denominator != other._denominator) {
         return false;
      }
      if (_numerator != other._numerator) {
         return false;
      }
      return true;
   }


   @Override
   public String toString() {
      return "(" + _numerator + "/" + _denominator + ")";
   }


   public double doubleValue() {
      return (double) _numerator / _denominator;
   }


   public float floatValue() {
      return (float) doubleValue();
   }


   public boolean equals(final GFraction that) {
      if (_numerator == 0) {
         return (that._numerator == 0);
      }

      return (_numerator * that._denominator) == (that._numerator * _denominator);
   }


   public boolean lessThan(final GFraction that) {
      return (_numerator * that._denominator) < (that._numerator * _denominator);
   }


   public boolean lessOrEqualsThan(final GFraction that) {
      return (_numerator * that._denominator) <= (that._numerator * _denominator);
   }


   public boolean greaterThan(final GFraction that) {
      return (_numerator * that._denominator) > (that._numerator * _denominator);
   }


   public boolean greaterOrEqualsThan(final GFraction that) {
      return (_numerator * that._denominator) >= (that._numerator * _denominator);
   }


   @Override
   public int compareTo(final GFraction that) {
      final long a = (_numerator * that._denominator);
      final long b = (that._numerator * _denominator);

      if (a < b) {
         return -1;
      }
      if (a > b) {
         return 1;
      }
      return 0;
   }


   public GFraction add(final GFraction that) {
      final long num = _numerator * that._denominator + that._numerator * _denominator;
      final long denom = _denominator * that._denominator;
      return create(num, denom);
   }


   public GFraction subtract(final GFraction that) {
      final long num = _numerator * that._denominator - that._numerator * _denominator;
      final long denom = _denominator * that._denominator;
      return create(num, denom);
   }


   public GFraction divide(final GFraction that) {
      final long num = _numerator * that._denominator;
      final long denom = _denominator * that._numerator;
      return create(num, denom);
   }


   public GFraction divide(final long numerator) {
      return divide(new GFraction(numerator, 1));
   }


   public GFraction multiply(final GFraction that) {
      final long num = _numerator * that._numerator;
      final long denom = _denominator * that._denominator;
      return create(num, denom);
   }


   public GFraction reduced() {
      long numerator = _numerator;
      long denominator = _denominator;

      final long g = gcd(numerator, denominator);

      if (g != 0) {
         numerator /= g;
         denominator /= g;
      }

      if (denominator < 0) {
         numerator = -numerator;
         denominator = -denominator;
      }

      if ((numerator == _numerator) && (denominator == _denominator)) {
         return this;
      }

      return create(numerator, denominator);
   }


   private static long gcd(long m,
                           long n) {
      while (n != 0) {
         final long r = m % n;
         m = n;
         n = r;
      }
      return m;
   }


   public long getNumerator() {
      return _numerator;
   }


   public long getDenominator() {
      return _denominator;
   }


   public GFraction min(final GFraction that) {
      return lessThan(that) ? this : that;
   }


   public GFraction max(final GFraction that) {
      return lessThan(that) ? that : this;
   }


   public boolean between(final GFraction min,
                          final GFraction max) {
      return (greaterOrEqualsThan(min) && lessOrEqualsThan(max));
   }


   //   public static void main(final String[] args) {
   //    System.out.println("Faction 0.1");
   //    System.out.println("-----------\n");
   //
   //    final GFraction oneThird = GFraction.create(10, 30);
   //    final GFraction oneHalf = GFraction.create(1, 2);
   //
   //    System.out.println(oneThird.add(oneHalf));
   //
   //    //         System.out.println(oneThird);
   //    //         System.out.println(oneHalf);
   //    //   
   //    //         System.out.println(oneThird.lessThan(oneHalf));
   //    //         System.out.println(oneThird.greaterThan(oneHalf));
   //    //   
   //    //         System.out.println(oneThird.compareTo(oneThird));
   //
   // }

}
