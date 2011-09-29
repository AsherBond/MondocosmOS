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

import java.text.DecimalFormat;


public class GStringUtils {

   private static final long          KILO           = 1024;
   private static final long          MEGA           = KILO * KILO;
   private static final long          GIGA           = KILO * MEGA;
   private static final long          TERA           = KILO * GIGA;


   private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("##0.00%");
   private static final DecimalFormat SPACE_FORMAT   = new DecimalFormat("##0.#");

   private static final String        SPACES;
   private static final String        DASHES;
   private static final String        SHARPS;

   private static final String        NULL_STRING    = "<null>";

   static {
      String sp = "                                                                               ";
      sp = sp + sp;
      sp = sp + sp;
      sp = sp + sp;
      SPACES = sp;

      String d = "-------------------------------------------------------------------------------";
      d = d + d;
      d = d + d;
      d = d + d;
      DASHES = d;

      String s = "###############################################################################";
      s = s + s;
      s = s + s;
      s = s + s;
      SHARPS = s;
   }


   private GStringUtils() {
   }


   public static int getLevenshteinDistance(final String s,
                                            final String t) {
      GAssert.notNull(s, "s");
      GAssert.notNull(t, "t");

      /*
        The difference between this impl. and the previous is that, rather 
         than creating and retaining a matrix of size s.length()+1 by t.length()+1, 
         we maintain two single-dimensional arrays of length s.length()+1.  The first, d,
         is the 'current working' distance array that maintains the newest distance cost
         counts as we iterate through the characters of String s.  Each time we increment
         the index of String t we are comparing, d is copied to p, the second int[].  Doing so
         allows us to retain the previous cost counts as required by the algorithm (taking 
         the minimum of the cost count to the left, up one, and diagonally up and to the left
         of the current cost count being calculated).  (Note that the arrays aren't really 
         copied anymore, just switched...this is clearly much better than cloning an array 
         or doing a System.arraycopy() each time  through the outer loop.)

         Effectively, the difference between the two implementations is this one does not 
         cause an out of memory condition when calculating the LD over two very large strings.              
      */

      final int n = s.length(); // length of s
      final int m = t.length(); // length of t

      if (n == 0) {
         return m;
      }
      else if (m == 0) {
         return n;
      }

      int p[] = new int[n + 1]; //'previous' cost array, horizontally
      int d[] = new int[n + 1]; // cost array, horizontally
      int _d[]; //placeholder to assist in swapping p and d

      // indexes into strings s and t
      int i; // iterates through s
      int j; // iterates through t

      char t_j; // jth character of t

      int cost; // cost


      for (i = 0; i <= n; i++) {
         p[i] = i;
      }

      for (j = 1; j <= m; j++) {
         t_j = t.charAt(j - 1);
         d[0] = j;

         for (i = 1; i <= n; i++) {
            cost = s.charAt(i - 1) == t_j ? 0 : 1;
            // minimum of cell to the left+1, to the top+1, diagonally left and up +cost                            
            d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
         }

         // copy current distance counts to 'previous row' distance counts
         _d = p;
         p = d;
         d = _d;
      }

      // our last action in the above loop was to switch d and p, so p now 
      // actually has the most recent cost counts
      return p[n];
   }


   public static String formatPercent(final long value,
                                      final long total) {
      return formatPercent((double) value / total);
   }


   public static String formatPercent(final double percent) {
      return PERCENT_FORMAT.format(percent);
      //return GMath.roundTo(percent * 100, 2) + "%";
   }


   public static String getTimeMessage(final long ms) {
      return getTimeMessage(ms, true);
   }


   public static String getTimeMessage(final long ms,
                                       final boolean rounded) {
      if (ms < 1000) {
         return ms + "ms";
      }

      if (ms < 60000) {
         final double seconds = ms / 1000d;
         return (rounded ? Math.round(seconds) : seconds) + "s";
      }

      final long minutes = ms / 60000;
      final double seconds = (ms - (minutes * 60000d)) / 1000d;
      if (seconds <= 0) {
         return minutes + "m";
      }
      return minutes + "m " + (rounded ? Math.round(seconds) : seconds) + "s";
   }


   public static String toString(final Object obj) {
      if (obj == null) {
         return GStringUtils.NULL_STRING;
      }
      return obj.toString();
   }


   public static String toString(final Object[] collection) {
      if (collection == null) {
         return GStringUtils.NULL_STRING;
      }

      final StringBuilder buffer = new StringBuilder();
      boolean first = true;
      for (final Object o : collection) {
         if (!first) {
            first = false;
            buffer.append(",");
         }
         buffer.append(o);
      }

      return buffer.toString();
   }


   public static String spaces(final int count) {
      return GStringUtils.SPACES.substring(0, count);
   }


   public static String dashes(final int count) {
      return GStringUtils.DASHES.substring(0, count);
   }


   public static String sharps(final int count) {
      return GStringUtils.SHARPS.substring(0, Math.min(count, GStringUtils.SHARPS.length() - 1));
   }


   public static String getSpaceMessage(final double bytes) {
      if (bytes < (KILO * 0.8)) {
         return bytes + "B";
      }

      if (bytes < (MEGA * 0.8)) {
         final double kilos = bytes / KILO;
         return SPACE_FORMAT.format(kilos) + "kB";
      }

      if (bytes < (GIGA * 0.8)) {
         final double megas = bytes / MEGA;
         return SPACE_FORMAT.format(megas) + "MB";
      }

      if (bytes < (TERA * 0.8)) {
         final double gigas = bytes / GIGA;
         return SPACE_FORMAT.format(gigas) + "GB";
      }

      final double teras = bytes / TERA;
      return SPACE_FORMAT.format(teras) + "TB";
   }


   //   public static void main(final String[] args) {
   //      final double bytes = 1024d * 1024 * 1024 * 1024 * 1024;
   //      System.out.println(bytes);
   //      System.out.println(getSpaceMessage(bytes));
   //   }

}
