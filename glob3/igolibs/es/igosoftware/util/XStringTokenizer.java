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

import java.util.ArrayList;
import java.util.StringTokenizer;


public final class XStringTokenizer
         extends
            StringTokenizer {

   public static double[] nextDoubleTokens(final String str,
                                           final int count) {
      final XStringTokenizer instance = new XStringTokenizer(str);
      final double[] result = instance.nextDoubleTokens(count);
      if (instance.hasMoreTokens()) {
         throw new NumberFormatException("The given string has more than " + count + " tokens");
      }
      return result;
   }


   public static String[] getAllTokens(final String str) {
      return new XStringTokenizer(str).getAllTokens();
   }


   public static String[] getAllTokens(final String str,
                                       final String delim) {
      return new XStringTokenizer(str, delim).getAllTokens();
   }


   public static String[] getAllTokens(final String str,
                                       final String delim,
                                       final boolean returnDelims) {
      return new XStringTokenizer(str, delim, returnDelims).getAllTokens();
   }


   public String[] getAllTokens() {
      final ArrayList<String> result = new ArrayList<String>();
      while (hasMoreTokens()) {
         result.add(nextToken());
      }
      return result.toArray(new String[0]);
   }


   public XStringTokenizer(final String str) {
      super(str);
   }


   public XStringTokenizer(final String str,
                           final String delim) {
      super(str, delim);
   }


   public XStringTokenizer(final String str,
                           final String delim,
                           final boolean returnDelims) {
      super(str, delim, returnDelims);
   }


   public double nextDoubleToken() {
      return Double.parseDouble(nextToken());
   }


   public double[] nextDoubleTokens(final int count) {
      final double[] result = new double[count];
      for (int i = 0; i < count; i++) {
         result[i] = nextDoubleToken();
      }
      return result;
   }


   public float nextFloatToken() {
      return Float.parseFloat(nextToken());
   }


   public int nextIntToken() {
      return Integer.parseInt(nextToken());
   }

}
