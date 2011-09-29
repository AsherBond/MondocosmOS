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

import static java.util.Locale.ENGLISH;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


public final class GUtils {


   public static final double   SMALL_NUM = 0.000001;

   private final static boolean isDevelopment;


   private GUtils() {
   }

   private static final boolean isLinux;
   private static final boolean isWindows;
   private static final boolean isMac;

   static {
      isDevelopment = System.getProperty("development", "off").equalsIgnoreCase("on");

      final String osName = System.getProperty("os.name", "").toLowerCase();

      isLinux = osName.contains("linux");
      isWindows = osName.contains("windows");
      isMac = osName.contains("mac") || osName.contains("darwin");

      //      if (isDevelopment()) {
      //         System.out.println("Development mode.");
      //         if (isLinux) {
      //            System.out.println("** Detected: Linux **");
      //         }
      //         if (isWindows) {
      //            System.out.println("** Detected: Windows **");
      //         }
      //         if (isMac) {
      //            System.out.println("** Detected: Mac **");
      //         }
      //      }
   }


   public static boolean isDevelopment() {
      return GUtils.isDevelopment;
   }


   public static boolean isLinux() {
      return GUtils.isLinux;
   }


   public static boolean isWindows() {
      return GUtils.isWindows;
   }


   public static boolean isMac() {
      return GUtils.isMac;
   }


   public static boolean is64Bits() {
      return "64".equals(System.getProperty("sun.arch.data.model"));
   }


   public static void delay(final long millis) {
      delay(millis, 0);
   }


   public static void delay(final long millis,
                            final int nanos) {
      if (millis < 0) {
         return;
      }

      try {
         Thread.sleep(millis, nanos);
      }
      catch (final InterruptedException e) {}
   }


   public static Image getImage(final String imageName,
                                final ClassLoader classLoader) {
      final URL url = classLoader.getResource("bitmaps/" + imageName);
      if (url == null) {
         return null;
      }

      try {
         return ImageIO.read(url);
      }
      catch (final IOException e) {
         e.printStackTrace();
      }

      return null;
   }


   public static Image getImage(final String imageName) {
      return Toolkit.getDefaultToolkit().getImage("bitmaps/" + imageName);
   }


   public static ImageIcon getImageIcon(final String iconName) {
      return new ImageIcon(getImage(iconName));
   }


   public static ImageIcon getImageIcon(final String iconName,
                                        final ClassLoader classLoader) {
      final Image image = getImage(iconName, classLoader);
      if (image == null) {
         return null;
      }
      return new ImageIcon(image);
   }


   public static void showMemoryInfo() {
      final Runtime runtime = Runtime.getRuntime();

      final long freeMemory = runtime.freeMemory();
      final long totalMemory = runtime.totalMemory();
      final long maxMemory = runtime.maxMemory();

      System.out.println("------------------------------------------------------");
      System.out.println(" Free memory      : " + GStringUtils.getSpaceMessage(freeMemory));
      System.out.println(" Total memory     : " + GStringUtils.getSpaceMessage(totalMemory));
      System.out.println(" Max memory       : " + GStringUtils.getSpaceMessage(maxMemory));
      System.out.println(" Total free memory: " + GStringUtils.getSpaceMessage(freeMemory + (maxMemory - totalMemory)));
      System.out.println("------------------------------------------------------");
   }


   public static void main(final String[] args) {
      showMemoryInfo();
   }


   public static boolean equals(final Object obj1,
                                final Object obj2) {
      if (obj1 == obj2) {
         return true;
      }

      if (obj1 == null) {
         return obj2 == null;
      }

      if (obj2 == null) {
         return false;
      }

      return obj1.equals(obj2);
   }


   public static void renameOldFile(final String fileName) {
      final File file = new File(fileName);
      if (file.exists()) {
         final File oldFile = new File(fileName + ".old");
         if (oldFile.exists()) {
            oldFile.delete();
         }

         file.renameTo(oldFile);
      }
   }


   public static <T extends Exception> void checkExceptions(final List<T> exceptions) throws T {
      if (exceptions.isEmpty()) {
         return;
      }

      for (final T exception : exceptions) {
         exception.printStackTrace(System.err);
      }

      throw exceptions.get(0);
   }


   public static String toString(final Object object) {
      if (object == null) {
         return "null";
      }

      if (object instanceof CharSequence) {
         return "\"" + object + "\"";
      }

      if (object instanceof Object[]) {
         return Arrays.toString((Object[]) object);
      }

      return String.valueOf(object);
   }


   /**
    * Returns a String which capitalizes the first letter of the string.
    */
   public static String capitalize(final String name) {
      if ((name == null) || (name.length() == 0)) {
         return name;
      }
      return name.substring(0, 1).toUpperCase(ENGLISH) + name.substring(1);
   }


   public static <T> T required(final T object,
                                final String msg) throws NullPointerException {
      if (object == null) {
         if (msg == null) {
            throw new NullPointerException();
         }
         throw new NullPointerException(msg);
      }

      return object;
   }


}
