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

import java.io.IOException;

import es.igosoftware.logging.GLogger;


public class GInternetBrowser {

   private static final Runtime  RUNTIME        = Runtime.getRuntime();

   //   private static final String[] LINUX_BROWSERS = { "epiphany", "firefox", "mozilla", "konqueror", "netscape", "opera", "links", "lynx"                             };
   private static final String[] LINUX_BROWSERS = {
                     "firefox",
                     "mozilla",
                     "epiphany",
                     "konqueror",
                     "netscape",
                     "opera",
                     "links",
                     "lynx"
                                                };


   private GInternetBrowser() {
   }


   public static boolean browse(final String url) {
      try {
         if (GUtils.isWindows()) {
            // this doesn't support showing urls in the form of "page.html#nameLink" 
            RUNTIME.exec("rundll32 url.dll,FileProtocolHandler " + url);
         }
         else if (GUtils.isMac()) {
            RUNTIME.exec("open " + url);
         }
         else if (GUtils.isLinux()) {
            // Build a command string which looks like "browser1 "url" || browser2 "url" ||..."
            final StringBuffer cmd = new StringBuffer();
            for (int i = 0; i < LINUX_BROWSERS.length; i++) {
               cmd.append((i == 0 ? "" : " || ") + LINUX_BROWSERS[i] + " \"" + url + "\" ");
            }

            RUNTIME.exec(new String[] {
                              "sh",
                              "-c",
                              cmd.toString()
            });
         }
         else {
            GLogger.instance().logSevere("Unsupported platform (" + System.getProperty("os.name", "") + ")");
            return false;
         }
      }
      catch (final IOException e) {
         GLogger.instance().logSevere(e);
         return false;
      }

      return true;
   }
}
