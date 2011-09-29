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


package es.igosoftware.io;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import es.igosoftware.util.GAssert;
import es.igosoftware.util.GUtils;


public class GIOUtils {

   private static final String ILLEGAL_FILE_NAME_CHARACTERS = "[" + "?/\\\\=+<>:;\\,\"\\|^\\[\\]" + "]";
   private static File         CURRENT_DIRECTORY            = null;


   private GIOUtils() {
   }


   public static byte[] compress(final byte[] bytes) {
      try {
         final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         final GZIPOutputStream gzip = new GZIPOutputStream(buffer, 1024);
         gzip.write(bytes);
         gzip.flush();
         gzip.close();

         return buffer.toByteArray();
      }
      catch (final IOException e) {
         throw new RuntimeException(e);
      }
   }


   public static byte[] uncompress(final byte[] compressedBytes) {
      try {
         final int uncompressedSize = uncompressedSize(compressedBytes);

         final ByteArrayInputStream buffer = new ByteArrayInputStream(compressedBytes);
         final GZIPInputStream gzip = new GZIPInputStream(buffer, 1024);

         final byte[] result = new byte[uncompressedSize];
         for (int i = 0; i < uncompressedSize; i++) {
            result[i] = (byte) gzip.read();
         }

         gzip.close();

         return result;
      }
      catch (final IOException e) {
         throw new RuntimeException(e);
      }
   }


   private static final int uncompressedSize(final byte[] compressedBytes) throws IOException {

      GZIPInputStream gzip = null;

      try {
         gzip = new GZIPInputStream(new ByteArrayInputStream(compressedBytes));

         int size = 0;
         while (gzip.read() != -1) {
            size++;
         }
         gzip.close();

         return size;
      }
      finally {
         GIOUtils.gentlyClose(gzip);
      }
   }


   public static void copy(final File fromFile,
                           final File toFile) throws IOException {

      File destinationFile = toFile;

      if (!fromFile.exists()) {
         throw new IOException("FileCopy: " + "no such source file: " + fromFile.getAbsolutePath());
      }
      if (!fromFile.isFile()) {
         throw new IOException("FileCopy: " + "can't copy directory: " + fromFile.getAbsolutePath());
      }
      if (!fromFile.canRead()) {
         throw new IOException("FileCopy: " + "source file is unreadable: " + fromFile.getAbsolutePath());
      }

      if (destinationFile.isDirectory()) {
         destinationFile = new File(destinationFile, fromFile.getName());
      }

      if (destinationFile.exists()) {
         if (!destinationFile.canWrite()) {
            throw new IOException("FileCopy: " + "destination file is unwriteable: " + destinationFile.getAbsolutePath());
         }
      }
      else {
         String parent = destinationFile.getParent();
         if (parent == null) {
            parent = System.getProperty("user.dir");
         }
         final File dir = new File(parent);
         if (!dir.exists()) {
            throw new IOException("FileCopy: " + "destination directory doesn't exist: " + parent);
         }
         if (dir.isFile()) {
            throw new IOException("FileCopy: " + "destination is not a directory: " + parent);
         }
         if (!dir.canWrite()) {
            throw new IOException("FileCopy: " + "destination directory is unwriteable: " + parent);
         }
      }

      FileChannel from = null;
      FileChannel to = null;
      try {
         from = new FileInputStream(fromFile).getChannel();
         to = new FileOutputStream(destinationFile).getChannel();
         to.transferFrom(from, 0, from.size());

         //         final byte[] buffer = new byte[4096];
         //         int bytesRead;
         //
         //         while ((bytesRead = from.read(buffer)) != -1) {
         //            to.write(buffer, 0, bytesRead); // write
         //         }
      }
      finally {
         GIOUtils.gentlyClose(to);
         GIOUtils.gentlyClose(from);
      }
   }


   public static void copy(final GFileName fromFileName,
                           final GFileName toFileName) throws IOException {
      final File fromFile = fromFileName.asFile();
      File toFile = toFileName.asFile();

      if (!fromFile.exists()) {
         throw new IOException("FileCopy: " + "no such source file: " + fromFileName);
      }
      if (!fromFile.isFile()) {
         throw new IOException("FileCopy: " + "can't copy directory: " + fromFileName);
      }
      if (!fromFile.canRead()) {
         throw new IOException("FileCopy: " + "source file is unreadable: " + fromFileName);
      }

      if (toFile.isDirectory()) {
         toFile = new File(toFile, fromFile.getName());
      }

      if (toFile.exists()) {
         if (!toFile.canWrite()) {
            throw new IOException("FileCopy: " + "destination file is unwriteable: " + toFileName);
         }
      }
      else {
         String parent = toFile.getParent();
         if (parent == null) {
            parent = System.getProperty("user.dir");
         }
         final File dir = new File(parent);
         if (!dir.exists()) {
            throw new IOException("FileCopy: " + "destination directory doesn't exist: " + parent);
         }
         if (dir.isFile()) {
            throw new IOException("FileCopy: " + "destination is not a directory: " + parent);
         }
         if (!dir.canWrite()) {
            throw new IOException("FileCopy: " + "destination directory is unwriteable: " + parent);
         }
      }

      FileInputStream from = null;
      FileOutputStream to = null;
      try {
         from = new FileInputStream(fromFile);
         to = new FileOutputStream(toFile);
         final byte[] buffer = new byte[4096];
         int bytesRead;

         while ((bytesRead = from.read(buffer)) != -1) {
            to.write(buffer, 0, bytesRead); // write
         }
      }
      finally {
         if (from != null) {
            try {
               from.close();
            }
            catch (final IOException e) {}
         }
         if (to != null) {
            try {
               to.close();
            }
            catch (final IOException e) {}
         }
      }
   }


   public static void copy(final ReadableByteChannel src,
                           final WritableByteChannel dest) throws IOException {
      final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
      while (src.read(buffer) != -1) {
         // prepare the buffer to be drained
         buffer.flip();
         // write to the channel, may block
         dest.write(buffer);
         // If partial transfer, shift remainder down
         // If buffer is empty, same as doing clear()
         buffer.compact();
      }
      // EOF will leave buffer in fill state
      buffer.flip();
      // make sure the buffer is fully drained.
      while (buffer.hasRemaining()) {
         dest.write(buffer);
      }
   }


   public static void copy(final InputStream in,
                           final OutputStream out) throws IOException {
      final byte[] buf = new byte[4096];
      int len;
      while ((len = in.read(buf)) > 0) {
         out.write(buf, 0, len);
      }
      in.close();

      //      final ReadableByteChannel inputChannel = Channels.newChannel(in);
      //      final WritableByteChannel outputChannel = Channels.newChannel(out);
      //      try {
      //         copy(inputChannel, outputChannel);
      //      }
      //      finally {
      //         GIOUtils.gentlyClose(inputChannel);
      //         GIOUtils.gentlyClose(outputChannel);
      //      }
   }


   public static void gentlyClose(final Socket socket) {
      if (socket == null) {
         return;
      }

      try {
         socket.close();
      }
      catch (final IOException e) {}
   }


   public static void gentlyClose(final ServerSocket socket) {
      if (socket == null) {
         return;
      }

      try {
         socket.close();
      }
      catch (final IOException e) {}
   }


   public static void gentlyClose(final Closeable closeable) {
      if (closeable == null) {
         return;
      }

      try {
         closeable.close();
      }
      catch (final IOException e) {}

   }


   public static void assureEmptyDirectory(final GFileName directoryName,
                                           final boolean verbose) throws IOException {
      final File directory = directoryName.asFile();

      if (!directory.exists()) {
         if (verbose) {
            System.out.println("- Creating directory \"" + directoryName + "\"");
         }
         if (!directory.mkdirs()) {
            throw new IOException("Can't create directory \"" + directoryName + "\"");
         }
         return;
      }

      // the directory already exists, clean the contents
      cleanDirectory(directoryName, verbose);
   }


   public static void cleanDirectory(final File directory,
                                     final boolean verbose) throws IOException {
      if (!directory.exists()) {
         throw new IOException("Directory \"" + directory.getAbsolutePath() + "\" doesn't exist");
      }

      if (!directory.isDirectory()) {
         throw new IOException("The path \"" + directory.getAbsolutePath() + "\" is not a directory");
      }

      final File[] children = directory.listFiles();
      for (final File child : children) {
         if (child.isDirectory()) {
            cleanDirectory(child, verbose);
         }

         if (child.delete()) {
            if (verbose) {
               System.out.println("- Deleted \"" + child.getPath() + "\"");
            }
         }
         else {
            throw new IOException("Can't delete \"" + child.getAbsolutePath() + "\"");
         }
      }
   }


   public static void cleanDirectory(final GFileName directoryName,
                                     final boolean verbose) throws IOException {
      final File directory = directoryName.asFile();
      cleanDirectory(directory, verbose);
   }


   /*
     * Get the extension of a file.
     */
   public static String getExtension(final File file) {
      String ext = null;
      final String s = file.getName();
      final int i = s.lastIndexOf('.');

      if ((i > 0) && (i < s.length() - 1)) {
         ext = s.substring(i + 1).toLowerCase();
      }

      return ext;
   }


   /*
    * Get the name of a file without extension.
    */
   public static String getRawName(final File file) {

      String name = file.getName();
      final String ext = getExtension(file);

      if (ext != null) {
         //name = name.replace("." + ext, "");
         name = name.substring(0, name.lastIndexOf("."));
      }

      return name;
   }


   public static byte[] getBytesFromFile(final File file) throws IOException {
      InputStream is = null;
      try {
         is = new FileInputStream(file);

         // Get the size of the file
         final long length = file.length();

         if (length > Integer.MAX_VALUE) {
            throw new IOException("file too large");
         }

         // Create the byte array to hold the data
         final byte[] bytes = new byte[(int) length];

         // Read in the bytes
         int offset = 0;
         int numRead = 0;
         while ((offset < bytes.length) && ((numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)) {
            offset += numRead;
         }

         // Ensure all the bytes have been read in
         if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
         }

         return bytes;
      }
      finally {
         gentlyClose(is);
      }
   }


   public static void copy(final byte[] data,
                           final File file) throws IOException {
      FileOutputStream output = null;
      try {
         output = new FileOutputStream(file);
         output.write(data);
      }
      finally {
         GIOUtils.gentlyClose(output);
      }
   }


   public static String getContents(final InputStream is) throws IOException {
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
         GIOUtils.gentlyClose(is);
      }
   }


   public static String replaceIllegalFileNameCharacters(final String fileName) {
      GAssert.notNull(fileName, "fileName");

      return fileName.replaceAll(ILLEGAL_FILE_NAME_CHARACTERS, "_");
   }


   public static String buildPath(final boolean isAbsolute,
                                  final String... parts) {
      return buildPath(isAbsolute, File.separatorChar, parts);
   }


   public static String buildPath(final boolean isAbsolute,
                                  final char separator,
                                  final String... parts) {
      GAssert.notEmpty(parts, "parts");

      final StringBuilder buffer = new StringBuilder();

      for (final String part : parts) {
         if (part == null) {
            continue;
         }

         if (isAbsolute || (buffer.length() > 0)) {
            buffer.append(separator);
         }

         buffer.append(part.replaceAll(ILLEGAL_FILE_NAME_CHARACTERS, "_"));
      }

      return buffer.toString();
   }


   public static File buildFile(final String... parts) {
      GAssert.notEmpty(parts, "parts");

      File current = new File(parts[0]);
      for (int i = 1; i < parts.length; i++) {
         current = new File(current, parts[i]);
      }

      return current;
   }


   //   public static File buildFile(final File firstPart,
   //                                final String... parts) {
   //      GAssert.notNull(firstPart, "firstPart");
   //
   //      File current = firstPart;
   //      for (final String part : parts) {
   //         current = new File(current, part);
   //      }
   //
   //      return current;
   //   }


   public static String convertToURLPath(final String path) {


      return path.replace('\\', '/');
   }


   public static String getUniqueID(final File file) {
      return file.getName() + Long.toHexString(file.lastModified()) + Long.toHexString(file.length());
   }


   public static boolean hasExtension(final GFileName fileName,
                                      final String extension) {
      return fileName.getName().toLowerCase().endsWith("." + extension.toLowerCase());
   }


   /**
    * Answer the last visited directory, or the user-home
    */
   public static File getCurrentDirectory() {
      return (CURRENT_DIRECTORY == null) ? new File(System.getProperty("user.home")) : CURRENT_DIRECTORY;
   }


   public static void setCurrentDirectory(final File currentDirectory) {
      if (currentDirectory != null) {
         if (currentDirectory.isDirectory() && currentDirectory.exists()) {
            CURRENT_DIRECTORY = currentDirectory;
         }
      }
   }


   public static void gentlyClose(final HttpURLConnection connection) {
      connection.disconnect();
   }


   public static GFileName getCacheDirectory(final String... cacheName) {
      return GFileName.fromParentAndParts(getCacheDirectory(), cacheName);
   }


   public static GFileName getCacheDirectory() {
      final String home = System.getProperty("user.home");
      if (home == null) {
         throw new RuntimeException("Can't find the user's home directory");
      }

      final GFileName homeFileName = GFileName.fromFile(new File(home));

      if (GUtils.isLinux()) {
         return GFileName.fromParentAndParts(homeFileName, "var", "cache");
      }

      if (GUtils.isMac()) {
         return GFileName.fromParentAndParts(homeFileName, "Library", "Caches");
      }

      if (GUtils.isWindows()) {
         return GFileName.fromParentAndParts(homeFileName, "Application Data");
      }

      throw new RuntimeException("Unsupported Operative System");
   }


   //   public static void main(final String[] args) {
   //      System.out.println(getCacheDirectory("glob3"));
   //   }


}
