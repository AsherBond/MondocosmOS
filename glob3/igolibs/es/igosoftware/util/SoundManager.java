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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import es.igosoftware.logging.GLogger;
import es.igosoftware.logging.ILogger;


public final class SoundManager {
   private final static ILogger logger = GLogger.instance();


   public interface SoundPlayingHandler {
      public void soundStopped();
   }

   private final static SoundManager instance = new SoundManager();


   public static SoundManager instance() {
      return instance;
   }


   // private constructor, use singleton
   private SoundManager() {

   }

   private Clip    clipLine;
   private boolean isPlaying  = false;
   private boolean isStopping = false;


   public void playSound(final String soundName) {
      playSounds(new String[] {
         soundName
      });
   }


   public synchronized void playSoundInBackground(final String soundName,
                                                  final boolean loop) {
      isPlaying = true;
      isStopping = false;

      if (!tryToPlaySoundInBackground(soundName, loop)) {
         stopSound();
      }
   }


   public void playSounds(final String[] soundsNames) {
      final List<String> soundsNamesList = new ArrayList<String>();

      for (final String eachSoundName : soundsNames) {
         soundsNamesList.add(eachSoundName);
      }

      playSounds(soundsNamesList);
   }


   public void playSounds(final List<String> soundsNames) {
      playSounds(soundsNames, null);
   }

   private Runnable onFinishHandler;


   public synchronized void playSounds(final List<String> soundsNames,
                                       final Runnable onFinish) {
      isPlaying = true;
      isStopping = false;
      onFinishHandler = onFinish;

      if (soundsNames.isEmpty()) {
         stopSound();
         return;
      }

      final Iterator<String> soundsNamesIterator = soundsNames.iterator();

      final SoundPlayingHandler handler = new SoundPlayingHandler() {
         @Override
         public void soundStopped() {

            if (isStopping) {
               return;
            }

            if (soundsNamesIterator.hasNext()) {
               GUtils.delay(250);

               if (!tryToPlaySound(soundsNamesIterator.next(), this)) {
                  stopSound();
               }
            }
            else {
               stopSound();
            }
         }
      };

      if (!tryToPlaySound(soundsNamesIterator.next(), handler)) {
         stopSound();
      }
   }


   private boolean tryToPlaySound(final String soundFileName,
                                  final SoundPlayingHandler handler) {
      Exception ex = null;
      try {
         rawPlaySound(soundFileName, handler);
         return true;
      }
      catch (final UnsupportedAudioFileException e) {
         ex = e;
      }
      catch (final IOException e) {
         ex = e;
      }
      catch (final LineUnavailableException e) {
         ex = e;
      }
      catch (final IllegalArgumentException e) {
         ex = e;
      }

      logger.logSevere("Error trying to play: " + soundFileName);
      logger.logSevere(ex);

      stopSound();
      return false;
   }


   private boolean tryToPlaySoundInBackground(final String soundFileName,
                                              final boolean loop) {
      Exception ex = null;
      try {
         rawPlaySoundInBackground(soundFileName, loop);
         return true;
      }
      catch (final UnsupportedAudioFileException e) {
         ex = e;
      }
      catch (final IOException e) {
         ex = e;
      }
      catch (final LineUnavailableException e) {
         ex = e;
      }
      catch (final IllegalArgumentException e) {
         ex = e;
      }

      logger.logSevere("Error trying to play: " + soundFileName);
      logger.logSevere(ex);

      stopSound();
      return false;
   }


   private class SoundBackgroundThread
            extends
               Thread {
      private final AudioInputStream audioInputStream;
      private SourceDataLine         source;
      private int                    total;
      private final boolean          loop;

      final private String           soundFileName;


      // private boolean forceStop = false;

      private SoundBackgroundThread(final String soundFileName1,
                                    final boolean loop1) throws IOException, LineUnavailableException,
               UnsupportedAudioFileException {

         setName("Background SoundManager Thread");
         setDaemon(true);
         loop = loop1;
         soundFileName = soundFileName1;
         final File soundFile = new File(soundFileName);

         audioInputStream = AudioSystem.getAudioInputStream(soundFile);
         final AudioFormat format = audioInputStream.getFormat(); // This need's for reconstruct the audiofile
         final DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

         try {
            if (AudioSystem.isLineSupported(info)) {
               source = (SourceDataLine) AudioSystem.getLine(info);
               total = audioInputStream.available();
               source.open(format);

            }
            else {
               logger.logSevere("DataLine not supported");
            }
         }
         catch (final IllegalArgumentException e) {
            logger.logSevere(e);
            stopSound();
         }
      }


      @Override
      public void run() {

         if (source == null) {
            return;
         }

         if (source.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            final FloatControl masterFC = (FloatControl) source.getControl(FloatControl.Type.MASTER_GAIN);
            masterFC.setValue(masterFC.getMaximum());
         }

         final int buffersize = source.getBufferSize() /* 10240 */;
         final byte[] data = new byte[buffersize];
         source.start();

         int read = buffersize;
         // Well, now play!!!
         while (!isStopping && (read < total)) {
            try {
               read = audioInputStream.read(data, 0, data.length);
            }
            catch (final IOException e) {
               e.printStackTrace();
               read = -1;
            }
            if (read == -1) {
               break; // End of audiostream.
            }
            source.write(data, 0, read);
            GUtils.delay(25);
         }

         if (!isStopping && loop) {
            playSoundInBackground(soundFileName, true);
         }
      }


      private void forceStop() {
         if (source == null) {
            return;
         }

         if (source.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            final FloatControl masterFC = (FloatControl) source.getControl(FloatControl.Type.MASTER_GAIN);
            masterFC.setValue(masterFC.getMinimum());
         }

         source.stop();
      }
   }

   private SoundBackgroundThread backgroundSoundThread;


   private synchronized void rawPlaySoundInBackground(final String soundFileName,
                                                      final boolean loop) throws UnsupportedAudioFileException, IOException,
                                                                         LineUnavailableException {

      final File soundFile = new File(soundFileName);

      final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
      final AudioFormat format = audioInputStream.getFormat(); // This need's for reconstruct the audiofile
      final DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

      try {
         if (AudioSystem.isLineSupported(info)) {
            backgroundSoundThread = new SoundBackgroundThread(soundFileName, loop);
            backgroundSoundThread.start();
         }
         else {
            logger.logSevere("DataLine not supported");
         }
      }
      catch (final IllegalArgumentException e) {
         logger.logSevere(e);
         stopSound();
      }
   }


   private synchronized void rawPlaySound(final String soundFileName,
                                          final SoundPlayingHandler handler) throws UnsupportedAudioFileException, IOException,
                                                                            LineUnavailableException {

      final File soundFile = new File(soundFileName);

      final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
      final AudioFormat format = audioInputStream.getFormat();
      final DataLine.Info info = new DataLine.Info(Clip.class, format);

      try {
         clipLine = (Clip) AudioSystem.getLine(info);

         if (clipLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            final FloatControl masterFC = (FloatControl) clipLine.getControl(FloatControl.Type.MASTER_GAIN);
            masterFC.setValue(masterFC.getMaximum());
         }

         clipLine.addLineListener(new LineListener() {
            @Override
            public void update(final LineEvent event) {
               if (event.getType().equals(LineEvent.Type.STOP)) {
                  clipLine.close();
                  clipLine = null;
               }
               else if (event.getType().equals(LineEvent.Type.CLOSE)) {
                  handler.soundStopped();
               }
            }
         });

         clipLine.start();
         clipLine.open(audioInputStream);
         clipLine.loop(0);

      }
      catch (final IllegalArgumentException e) {
         logger.logSevere(e);
         stopSound();
         handler.soundStopped();
      }
   }


   public synchronized boolean isPlaying() {
      return isPlaying;
   }


   public synchronized void stopSound() {
      isPlaying = false;
      isStopping = true;

      if (backgroundSoundThread != null) {
         backgroundSoundThread.forceStop();
         backgroundSoundThread = null;
      }

      if (clipLine != null) {
         clipLine.stop();
         clipLine = null;
      }

      if (onFinishHandler != null) {
         onFinishHandler.run();
         onFinishHandler = null;
      }

      GUtils.delay(100);
   }
}
