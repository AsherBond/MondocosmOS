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


public abstract class GLoggerObject {

   private static final ILogger logger = GLogger.instance();


   public abstract boolean logVerbose();


   public String logName() {
      return null;
   }


   private String namedMsg(final String msg) {
      final String logName = logName();
      if (logName == null) {
         return msg;
      }
      return logName + ": " + msg;
   }


   public void logInfo(final String msg) {
      if (logVerbose()) {
         logger.logInfo(namedMsg(msg));
      }
   }


   public void logInfoUnnamed(final String msg) {
      if (logVerbose()) {
         logger.logInfo(msg);
      }
   }


   public void logSevere(final Throwable e) {
      logger.logSevere(e);
   }


   public void logSevere(final String msg) {
      logger.logSevere(namedMsg(msg));
   }


   public void logSevere(final String msg,
                         final Throwable e) {
      logger.logSevere(msg, e);
   }


   public void logWarning(final String msg) {
      logger.logWarning(namedMsg(msg));
   }


   public void logDecreaseIdentationLevel() {
      logger.logDecreaseIdentationLevel();
   }


   public void logIncreaseIdentationLevel() {
      logger.logIncreaseIdentationLevel();
   }


}
