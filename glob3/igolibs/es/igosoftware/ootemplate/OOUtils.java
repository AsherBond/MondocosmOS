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


package es.igosoftware.ootemplate;

import java.util.HashMap;
import java.util.Map;


public class OOUtils {
   private OOUtils() {
   }

   private static final Map<String, String> ENTITIES = new HashMap<String, String>();


   static {
      OOUtils.ENTITIES.put(">", "&gt;");
      OOUtils.ENTITIES.put("<", "&lt;");
      OOUtils.ENTITIES.put("&", "&amp;");
      OOUtils.ENTITIES.put("\"", "&quot;");
      OOUtils.ENTITIES.put("'", "&apos;");
      OOUtils.ENTITIES.put("\\", "&#092;");
      OOUtils.ENTITIES.put("\u00a9", "&copy;");
      OOUtils.ENTITIES.put("\u00ae", "&reg;");
   }


   public static final String escape(final String s) {
      final StringBuffer buffer = new StringBuffer(s.length() * 2);

      for (int i = 0; i < s.length(); i++) {
         final char ch = s.charAt(i);
         if (((ch >= 63) && (ch <= 90)) || ((ch >= 97) && (ch <= 122)) || (ch == ' ')) {
            buffer.append(ch);
         }
         else {
            final String encoded = OOUtils.ENTITIES.get(String.valueOf(ch));
            if (encoded == null) {
               buffer.append(ch);
            }
            else {
               buffer.append(encoded);
            }
         }
      }

      return buffer.toString();
   }
}
