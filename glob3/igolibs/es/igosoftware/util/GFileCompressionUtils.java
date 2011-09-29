

package es.igosoftware.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import es.igosoftware.io.GFileName;
import es.igosoftware.io.GIOUtils;


public class GFileCompressionUtils {

   private static final int BUFFER_SIZE = 1024 * 8; //-- 8Kb

   List<String>             _fileList;
   final String             _targetDir;


   GFileCompressionUtils() {
      _fileList = new ArrayList<String>();
      _targetDir = "";
   }


   GFileCompressionUtils(final String targetDir) {
      _targetDir = targetDir;
      _fileList = new ArrayList<String>();
      generateFileList(new File(_targetDir));

   }


   public static void extractFile(final GFileName source) throws IOException {
      extractFile(source.buildPath());
   }


   public static void extractFile(final File source) throws IOException {
      extractFile(source.getPath());
   }


   public static void extractFile(final String source) throws IOException {

      //final String target = GIOUtils.getCurrentDirectory().getPath();
      final String target = new File(source).getParent(); // extract to the same folder of source file

      extractFile(source, target);
   }


   public static void extractFile(final GFileName source,
                                  final GFileName targetDir) throws IOException {
      extractFile(source.buildPath(), targetDir.buildPath());
   }


   public static void extractFile(final File source,
                                  final File targetDir) throws IOException {
      extractFile(source.getPath(), targetDir.getPath());
   }


   public static void extractFile(final String source,
                                  final String targetDir) throws IOException {

      //create output directory if not exists
      final File dir = new File(targetDir);
      if (!dir.exists()) {
         dir.mkdir();
      }

      final GFileName f = GFileName.fromFile(new File(source));

      if (GIOUtils.hasExtension(f, "zip") || GIOUtils.hasExtension(f, "ZIP")) {
         unZipFile(source, targetDir);
      }
      else if (GIOUtils.hasExtension(f, "gz") || GIOUtils.hasExtension(f, "GZ")) {
         unGZipFile(source, targetDir);
      }
      else {
         throw new IllegalArgumentException("Invalid file extension: " + source);
      }
   }


   public static void compressFile(final GFileName source) throws IllegalArgumentException, IOException {

      compressFile(source.buildPath());
   }


   public static void compressFile(final File source) throws IllegalArgumentException, IOException {

      compressFile(source.getPath());
   }


   public static void compressFile(final String source) throws IllegalArgumentException, IOException {

      //      final File src = new File(source);
      //      final GFileName currentDir = GFileName.fromFile(GIOUtils.getCurrentDirectory());
      //      final String target = GFileName.fromParentAndParts(currentDir, src.getName()).buildPath();

      final String target = source; // .zip or .gz extension will be added for compressed file

      compressFile(source, target);

   }


   public static void compressFile(final GFileName source,
                                   final GFileName target) throws IllegalArgumentException, IOException {

      compressFile(source.buildPath(), target.buildPath());
   }


   public static void compressFile(final File source,
                                   final File target) throws IllegalArgumentException, IOException {

      compressFile(source.getPath(), target.getPath());
   }


   public static void compressFile(final String source,
                                   final String target) throws IllegalArgumentException, IOException {

      //create parent directory if not exists
      final File parent = new File(target).getParentFile();
      if (!parent.exists()) {
         parent.mkdir();
      }

      final File f = new File(source);

      if (f.isDirectory()) {
         zipDirectory(source, target);
      }
      else {
         gzipFile(source, target);
      }
   }


   private static void gzipFile(final String source,
                                final String target) throws IOException {

      final String targetName = target + ".gz";

      final FileInputStream in = new FileInputStream(source);

      System.out.println("Compressing file " + source);

      final GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(targetName));
      //final GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(targetName), BUFFER_SIZE);
      System.out.println("Output to: " + targetName);

      final byte[] buffer = new byte[BUFFER_SIZE];
      int bytesRead;
      while ((bytesRead = in.read(buffer)) != -1) {
         //while ((bytesRead = in.read()) != -1) {
         out.write(buffer, 0, bytesRead);
         //out.write(bytesRead);
      }
      in.close();
      out.close();

      System.out.println("Terminated !");
   }


   private static void zipDirectory(final String sourceDir,
                                    final String target) throws IOException, IllegalArgumentException {

      final String targetName = target + ".zip";

      // Check that the directory is a directory, and get its contents
      final File dir = new File(sourceDir);
      if (!dir.isDirectory()) {
         throw new IllegalArgumentException("Not a directory:  " + sourceDir);
      }
      System.out.println("Compressing directory " + sourceDir);

      final ZipOutputStream out = new ZipOutputStream(new FileOutputStream(targetName));

      final GFileCompressionUtils utils = new GFileCompressionUtils(sourceDir);
      //utils.generateFileList(dir);

      final byte[] buffer = new byte[BUFFER_SIZE]; // Create a buffer for copying
      int bytesRead;
      System.out.println("Output to Zip: " + targetName);

      for (final String file : utils.getFileList()) {
         System.out.println("File Added : " + file);

         final File newFile = new File(utils.getTargetDir() + File.separator + file);

         //final FileInputStream in = new FileInputStream(file); // Stream to read file
         final FileInputStream in = new FileInputStream(newFile); // Stream to read file
         final ZipEntry entry = new ZipEntry(file); // Make a ZipEntry
         out.putNextEntry(entry); // Store entry

         while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
         }
         in.close();
      }
      out.close();

      System.out.println("Terminated !");
   }


   /**
    * Traverse a directory and get all files, and add the file into fileList
    * 
    * @param node
    *           file or directory
    */
   private void generateFileList(final File node) {

      //add file only
      if (node.isFile()) {
         _fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));
      }

      if (node.isDirectory()) {
         final String[] subNode = node.list();
         for (final String filename : subNode) {
            generateFileList(new File(node, filename));
         }
      }

   }


   /**
    * Format the file path for zip
    * 
    * @param file
    *           file path
    * @return Formatted file path
    */
   private String generateZipEntry(final String file) {

      //final File f = new File(file);
      //return f.getPath();

      return file.substring(_targetDir.length() + 1, file.length());

   }


   private List<String> getFileList() {
      return _fileList;
   }


   private String getTargetDir() {
      return _targetDir;
   }


   /**
    * unZipFile
    * 
    * @param source
    *           input zip file
    * @param targetDir
    *           zip file output folder
    * @throws IOException
    */
   private static void unZipFile(final String source,
                                 final String targetDir) throws IOException {

      final File dir = new File(targetDir);
      if (!dir.isDirectory()) {
         throw new IllegalArgumentException("Not a directory:  " + targetDir);
      }

      System.out.println("Extracting file: " + source);

      //get the zip file content
      final ZipInputStream in = new ZipInputStream(new FileInputStream(source));
      //get the zipped file list entry
      ZipEntry entry = in.getNextEntry();

      System.out.println("Output to: " + targetDir);

      final byte[] buffer = new byte[BUFFER_SIZE];

      while (entry != null) {

         final String fileName = entry.getName();
         final File newFile = new File(targetDir + File.separator + fileName);
         //final File newFile = GFileName.fromParentAndParts(GFileName.fromFile(dir), fileName).asFile();

         System.out.println("file unzip : " + newFile.getAbsoluteFile());

         //create all non exists folders
         //else you will hit FileNotFoundException for compressed folder
         new File(newFile.getParent()).mkdirs();

         final FileOutputStream out = new FileOutputStream(newFile);

         int bytesRead;
         while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
         }

         out.close();
         entry = in.getNextEntry();
      }

      in.closeEntry();
      in.close();

      System.out.println("Terminated !");

   }


   private static void unGZipFile(final String source,
                                  final String targetDir) throws IOException {

      final File dir = new File(targetDir);
      if (!dir.isDirectory()) {
         throw new IllegalArgumentException("Not a directory:  " + targetDir);
      }

      System.out.println("Extracting file: " + source);

      //create output file is not exists
      final String fileName = GIOUtils.getRawName(new File(source));

      final File targetFile = new File(targetDir + File.separator + fileName);
      //final File targetFile = GFileName.fromParentAndParts(GFileName.fromFile(dir), fileName).asFile();

      System.out.println("Output to: " + targetDir);

      GZIPInputStream in;
      in = new GZIPInputStream(new FileInputStream(source));
      final OutputStream out = new FileOutputStream(targetFile);

      final byte[] buffer = new byte[BUFFER_SIZE];
      int bytesRead;
      while ((bytesRead = in.read(buffer)) != -1) {
         out.write(buffer, 0, bytesRead);
      }
      in.close();
      out.close();

      System.out.println("Terminated !");
   }


   /**
    * @param args
    * @throws IOException
    * @throws IllegalArgumentException
    */
   public static void main(final String[] args) {

      //final String from = ".";
      final GFileName sourceDirectoryName = GFileName.absolute("home", "fpulido", "Escritorio", "Vectorial");
      final GFileName sourceFile = GFileName.fromParentAndParts(sourceDirectoryName, "conversación_con_Diego_10-03-2011");
      final GFileName targetDirectoryName = GFileName.fromParentAndParts(sourceDirectoryName, "Compressed");
      final GFileName targetFile = GFileName.fromParentAndParts(targetDirectoryName, sourceFile.getName());

      final GFileName sourceDirectory = GFileName.fromParentAndParts(sourceDirectoryName, "shp");
      final GFileName targetZipFile = GFileName.fromParentAndParts(targetDirectoryName, sourceDirectory.getName());

      final GFileName unCompressedDirectoryName = GFileName.fromParentAndParts(sourceDirectoryName, "Uncompressed");
      final GFileName sourceGZFile = GFileName.fromFile(new File(targetFile.buildPath() + ".gz"));
      final GFileName sourceZipFile = GFileName.fromFile(new File(targetZipFile.buildPath() + ".zip"));
      //      final GFileName targetUnGZFile = GFileName.fromParentAndParts(unCompressedDirectoryName, targetFile.getName());
      //      final GFileName targetUnZipFile = GFileName.fromParentAndParts(unCompressedDirectoryName, targetZipFile.getName());


      try {
         compressFile(sourceFile, targetFile);
         compressFile(sourceDirectory, targetZipFile);

         compressFile(sourceFile);
         compressFile(sourceDirectory);

         extractFile(sourceGZFile, unCompressedDirectoryName);
         extractFile(sourceZipFile, unCompressedDirectoryName);

         extractFile(sourceGZFile);
         extractFile(sourceZipFile);
      }
      catch (final IllegalArgumentException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      catch (final IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }


   }

}
