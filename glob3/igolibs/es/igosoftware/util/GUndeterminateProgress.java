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


public abstract class GUndeterminateProgress
         implements
            IProgress {

   //private static final MessageFormat FORMATTER = new MessageFormat("{0,number,#0.00}%");

   private long       _steps;
   private long       _done;
   private final long _started;
   private long       _lastInformTime;
   private final long _timeToInform;
   private boolean    _finished;


   public GUndeterminateProgress() {
      this(10);
   }


   public GUndeterminateProgress(final long secondsToInform) {
      _steps = 0;
      _done = 0;
      _finished = false;

      final long now = System.currentTimeMillis();
      _started = now;
      _lastInformTime = now;
      _timeToInform = secondsToInform * 1000;
   }


   @Override
   public void stepDone() {
      stepsDone(1);
   }


   public String progressString(final long elapsed) {

      final int maxBarSteps = 80;
      final int doneBarsSteps = (int) _steps % (maxBarSteps + 1);

      if (_finished) {
         return "[" + GStringUtils.sharps(maxBarSteps) + "] " + _done + " steps" + " [Finished in "
                + GStringUtils.getTimeMessage(elapsed) + "]";
      }

      final String bar = "[" + GStringUtils.sharps(doneBarsSteps) + GStringUtils.dashes(maxBarSteps - doneBarsSteps) + "]";

      return bar + " " + _done + " steps" + " [Elapsed time: " + GStringUtils.getTimeMessage(elapsed) + "]";
   }


   @Override
   public synchronized void stepsDone(final long steps) {
      _done += steps;
      processSteps();
   }


   @Override
   public synchronized void finish() {
      _finished = true;
      processSteps();
   }


   private void processSteps() {

      final long now = System.currentTimeMillis();
      final long elapsedSinceLastInform = now - _lastInformTime;
      if ((_finished) || (elapsedSinceLastInform > _timeToInform)) {
         _lastInformTime = now;

         final long elapsed = now - _started;
         _steps++;
         informProgress(elapsed);
      }
   }


   public abstract void informProgress(final long elapsed);


   public static void main(final String[] args) {
      System.out.println("GUndeterminateProgress 0.1");
      System.out.println("--------------------------\n");


      //final int steps = 10000;
      final int timeToInform = 1;
      final GUndeterminateProgress progress = new GUndeterminateProgress(timeToInform) {
         @Override
         public void informProgress(final long elapsed) {
            System.out.println("Loading file 'brasil.gml' " + progressString(elapsed));
         }
      };

      for (int i = 0; i < 16000; i++) {
         GUtils.delay(5);
         progress.stepDone();
      }
      progress.finish();

   }

}
