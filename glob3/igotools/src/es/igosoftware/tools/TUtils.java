

package es.igosoftware.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class TUtils {


   //   private static enum Platform {
   //      Linux("linux"),
   //      Windows("windows"),
   //      Apple("apple");
   //
   //      private Platform(@SuppressWarnings("unused") final String directoryName) {
   //         //         _directoryName = directoryName;
   //      }
   //   }
   //
   //
   //   private static enum Bits {
   //      _32("32"),
   //      _64("64");
   //
   //      private Bits(@SuppressWarnings("unused") final String directoryName) {
   //         //         _directoryName = directoryName;
   //      }
   //   }


   //   static {
   //
   //      //      final String osName = System.getProperty("os.name", "").toLowerCase();
   //
   //      //      final boolean isLinux = osName.contains("linux");
   //      //      final boolean isWindows = osName.contains("windows");
   //      //      final boolean isMac = osName.contains("mac") || osName.contains("darwin");
   //
   //      //      if (!isLinux) {
   //      //         throw new RuntimeException("Only Linux platform supported");
   //      //      }
   //
   //      //      final String bits = System.getProperty("sun.arch.data.model", "").trim();
   //      //
   //      //      if (bits.equals("32")) {
   //      //         //         _bits = Bits._32;
   //      //      }
   //      //      else if (bits.equals("64")) {
   //      //         //         _bits = Bits._64;
   //      //      }
   //      //      else {
   //      //         throw new RuntimeException("Can't detect 32/64 bits");
   //      //      }
   //   }


   private TUtils() {
   }


   public static void info(final TProject project,
                           final String string) {
      System.out.println("- " + project.getName() + ": " + string);
   }


   public static void removeDirectory(final File directory) throws IOException {
      if (!directory.exists()) {
         return;
      }

      final File[] children = directory.listFiles();
      for (final File child : children) {
         if (child.isDirectory()) {
            removeDirectory(child);
         }
         else {
            if (!child.delete()) {
               throw new IOException("Can't remove file: " + child.getAbsolutePath());
            }
         }
      }

      if (!directory.delete()) {
         throw new IOException("Can't remove directory: " + directory.getAbsolutePath());
      }
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


   private static String getContents(final InputStream is) throws IOException {
      final BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

      final StringBuffer buffer = new StringBuffer();
      String line = null;
      while ((line = reader.readLine()) != null) {
         buffer.append(line);
         buffer.append('\n');
      }

      return buffer.toString();
   }


   public static String getContents(final File file) throws IOException {
      FileInputStream is = null;

      try {
         is = new FileInputStream(file);

         return getContents(is);
      }
      finally {
         if (is != null) {
            is.close();
         }
      }
   }


   public static void saveContentsTo(final String contents,
                                     final File target,
                                     final TProject project,
                                     final String createMessage,
                                     final String skippedMessage) throws IOException {

      if (target.exists()) {
         final String oldContents = getContents(target);
         if (oldContents.equals(contents)) {
            if (skippedMessage != null) {
               info(project, skippedMessage);
            }
            return;
         }
      }

      final FileWriter writer = new FileWriter(target);
      writer.write(contents);
      writer.flush();
      writer.close();

      if (createMessage != null) {
         info(project, createMessage);
      }
   }


   private static List<String> getPathList(final File file) throws IOException {
      final List<String> result = new ArrayList<String>();

      File currentFile = file.getCanonicalFile();
      while (currentFile != null) {
         result.add(currentFile.getName());
         currentFile = currentFile.getParentFile();
      }

      return result;
   }


   private static String matchPathLists(final List<String> homePath,
                                        final List<String> filePath) {
      final StringBuilder result = new StringBuilder();

      int i = homePath.size() - 1;
      int j = filePath.size() - 1;

      // first eliminate common root
      while ((i >= 0) && (j >= 0) && (homePath.get(i).equals(filePath.get(j)))) {
         i--;
         j--;
      }

      // for each remaining level in the home path, add a ..
      for (; i >= 0; i--) {
         result.append("..");
         result.append(File.separator);
      }

      // for each level in the file path, add the path
      for (; j >= 1; j--) {
         result.append(filePath.get(j));
         result.append(File.separator);
      }

      // file name
      result.append(filePath.get(j));

      return result.toString();
   }


   public static String getRelativePath(final File home,
                                        final File file) throws IOException {
      final List<String> homelist = getPathList(home);
      final List<String> filelist = getPathList(file);
      return matchPathLists(homelist, filelist);
   }


}
