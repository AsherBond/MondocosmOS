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


package es.igosoftware.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

import es.igosoftware.util.GStringUtils;


public final class GLogger
         implements
            ILogger {
   private static final DateFormat format   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS: ");
   private static final GLogger    instance = new GLogger();


   synchronized public static ILogger instance() {
      return GLogger.instance;
   }


   private int _identationLevel = 0;


   private GLogger() {
   }


   private String level() {
      return GStringUtils.spaces(_identationLevel * 2);
   }


   @Override
   synchronized public void logIncreaseIdentationLevel() {
      _identationLevel++;
   }


   @Override
   synchronized public void logDecreaseIdentationLevel() {
      if (_identationLevel > 0) {
         _identationLevel--;
      }
   }


   private String timestamp() {
      synchronized (format) {
         return format.format(Calendar.getInstance().getTime());
      }
   }


   @Override
   synchronized public void logInfo(final String msg) {
      final String notUsedString = "XIXIXIXIXIXIXIXIXIXI";
      final StringTokenizer lines = new StringTokenizer(msg.replaceAll("\n", notUsedString + "\n"), "\n", false);

      final String prefix = timestamp() + level();
      //final String secondPrefix = Utils.spaces(prefix.length());
      while (lines.hasMoreTokens()) {
         final String line = lines.nextToken().replaceAll(notUsedString, "");
         System.out.println(prefix + line);

         //prefix = secondPrefix;
      }
   }


   @Override
   synchronized public void logSevere(final String msg) {
      System.err.println(timestamp() + level() + "SEVERE: " + msg);
   }


   @Override
   synchronized public void logSevere(final Throwable e) {
      System.err.println(timestamp() + level() + "SEVERE: " + e);
      e.printStackTrace(System.err);
   }


   @Override
   synchronized public void logSevere(final String msg,
                                      final Throwable e) {
      System.err.println(timestamp() + level() + "SEVERE: " + msg + " " + e);
      e.printStackTrace(System.err);
   }


   @Override
   synchronized public void logWarning(final String msg) {
      System.err.println(timestamp() + level() + "WARNING: " + msg);
   }


}
