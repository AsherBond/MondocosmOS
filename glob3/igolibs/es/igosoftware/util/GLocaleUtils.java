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

import java.util.Locale;
import java.util.ResourceBundle;


public final class GLocaleUtils {
   private static Locale currentLocale;


   static {
      setCurrentLocale(Locale.getDefault());
   }


   private GLocaleUtils() {
   }


   public static void setCurrentLocale(final Locale newLocale) {
      currentLocale = newLocale;
      if (GUtils.isDevelopment()) {
         System.out.println("Current Language: " + getCurrentLanguage());
      }
      Locale.setDefault(newLocale);
   }


   public static Locale getCurrentLocale() {
      return currentLocale;
   }


   public static String getCurrentLanguage() {
      return getCurrentLocale().getLanguage();
   }


   public static ResourceBundle getResourceBundle() {
      return ResourceBundle.getBundle("locale.ResourceBundle", getCurrentLocale());
   }


   public static String getString(final String key) {
      return getResourceBundle().getString(key);
   }


   public static String getString(final String key,
                                  final String language) {
      return ResourceBundle.getBundle("locale.ResourceBundle", new Locale(language)).getString(key);
   }

}
