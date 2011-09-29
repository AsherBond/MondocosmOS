

package es.igosoftware.io;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import es.igosoftware.util.GAssert;
import es.igosoftware.util.GCollections;


/**
 * <p>
 * This is a utility class that should be used instead of Strings to describe paths and URLs.
 * 
 * <p>
 * The problem with using Strings for paths (and, to a lesser extent, URLs), is that they may vary depending on the operating
 * system ("/" on Unix-Systems vs. "\" on Windows, different set of forbidden characters in filenames, etc). The GFileName class
 * ensures platform independence. The basic idea is to specify the different parts of a path as separate Strings, without the
 * separators.
 * 
 * 
 * <p>
 * There are several static constructors that allow constructing a GFileName from a given {@link File}, other GFileNames, as an
 * absolute or relative path.
 * 
 * <p>
 * Usage:
 * <ul>
 * <li>create an absolute path '/home/user/development/test.java':<br>
 * GFileName absFileName = GFileName.absolute("home", "user", "development", "test.java");<br>
 * <li>combine with an existing GFileName:<br>
 * GFileName absBaseDirectory = GFileName.absolute("home", "user", "documents");//base directory<br>
 * GFileName wordDocPath = GFileName.fromParentAndParts(absBaseDirectory, "document1.doc");//.doc file in the base directory<br>
 * GFileName pdfDocPath = GFileName.fromParentAndParts(absBaseDirectory, "document2.pdf");//.pdf file in the base directory<br>
 * 
 * 
 * 
 * 
 * 
 @author IGO-Software
 * 
 * 
 * 
 */
public class GFileName
         implements
            Serializable {


   private static final long     serialVersionUID  = 1L;


   public static final GFileName CURRENT_DIRECTORY = new GFileName(false, ".");


   /**
    * Construct a GFileName from a given {@link File}
    * 
    * @param {@link File} file
    * @return a GFileName
    */
   public static GFileName fromFile(final File file) {
      GAssert.notNull(file, "file");

      final String separatorRegExp;
      if (File.separatorChar == '/') {
         separatorRegExp = "/";
      }
      else if (File.separatorChar == '\\') {
         separatorRegExp = "\\\\";
      }
      else {
         throw new RuntimeException("File.separator not supported: " + File.separatorChar);
      }

      final String[] parts = file.getPath().split(separatorRegExp);
      return new GFileName(file.isAbsolute(), parts);
   }


   public static GFileName relative(final String... parts) {
      return new GFileName(false, parts);
   }


   public static GFileName absolute(final String... parts) {
      return new GFileName(true, parts);
   }


   public static GFileName fromParts(final GFileName... parts) {
      GAssert.notEmpty(parts, "parts");

      for (int i = 1; i < parts.length; i++) {
         final GFileName part = parts[i];
         if (part._isAbsolute) {
            throw new RuntimeException("Only the first part can be absolute. Error part #" + i + " is absolute: " + part);
         }
      }

      final ArrayList<String> allParts = new ArrayList<String>();

      for (final GFileName fileNamePart : parts) {
         for (final String part : fileNamePart._parts) {
            allParts.add(part);
         }
      }

      return new GFileName(parts[0]._isAbsolute, allParts.toArray(new String[0]));
   }


   /**
    * Creates a new GFileName relative to an existing one
    * 
    * @param parent
    *           a GFileName
    * @param parts
    *           one or more Strings describing the last parts of the path to be constructed
    * @return a new GFileName combining the parent-GFileName and the specified parts
    */
   public static GFileName fromParentAndParts(final GFileName parent,
                                              final String... parts) {
      return new GFileName(parent, parts);
   }


   private final boolean  _isAbsolute;
   private final String[] _parts;


   private File           _file;


   private GFileName(final boolean isAbsolute,
                     final String... parts) {
      GAssert.notEmpty(parts, "parts");

      validateParts(parts);

      _isAbsolute = isAbsolute;
      _parts = Arrays.copyOf(parts, parts.length);
   }


   private GFileName(final GFileName parent,
                     final String... parts) {
      GAssert.notNull(parent, "parent");
      GAssert.notEmpty(parts, "parts");

      validateParts(parts);

      _parts = GCollections.concatenate(parent._parts, parts);
      _isAbsolute = parent._isAbsolute;
   }


   private static void validateParts(final String... parts) {
      for (final String part : parts) {
         if (part.contains("/") || part.contains("\\")) {
            throw new RuntimeException("Invalid fileName. Slashes ('/' or '\\') are not allowed in file-name parts. "
                                       + Arrays.toString(parts));
         }
      }
   }


   /**
    * Obtain the complete path as one single String, with "/" or "\" filled in according to the platform being used
    * 
    * @return the complete path as one single String
    */
   public String buildPath() {
      return GIOUtils.buildPath(_isAbsolute, _parts);
   }


   /**
    * Obtain the complete path as one single String, specifying the separator character (usually "/" or "\")
    * 
    * @param separator
    * @return the complete path as one single String, using the specified separator char
    */
   public String buildPath(final char separator) {
      return GIOUtils.buildPath(_isAbsolute, separator, _parts);
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + Arrays.hashCode(_parts);
      return result;
   }


   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final GFileName other = (GFileName) obj;
      if (!Arrays.equals(_parts, other._parts)) {
         return false;
      }
      return true;
   }


   @Override
   public String toString() {
      return "GFileName [" + buildPath() + (_isAbsolute ? "  ABSOLUTE " : "") + "]";
   }


   public GFileName getParent() {
      if (_parts.length == 1) {
         return CURRENT_DIRECTORY;
      }

      return new GFileName(_isAbsolute, Arrays.copyOf(_parts, _parts.length - 1));
   }


   /**
    * 
    * @return the GFileName as a {@link File}
    */
   public File asFile() {
      if (_file == null) {
         _file = new File(buildPath());
      }
      return _file;
   }


   public boolean isAbsolute() {
      return _isAbsolute;
   }


   public String getName() {
      return _parts[_parts.length - 1];
   }


   public static void main(final String[] args) {
      System.out.println("GFileName 0.1");
      System.out.println("-------------\n");


      final GFileName fromParts1 = GFileName.relative("dir1", "dir2", "dir3");
      showFileNameInfo(fromParts1);

      final GFileName fromParentAndParts = GFileName.fromParentAndParts(fromParts1, "fileName.ext");
      showFileNameInfo(fromParentAndParts);

      showFileNameInfo(GFileName.fromParts(fromParts1, fromParentAndParts));

      showFileNameInfo(GFileName.relative("fileName.ext"));

      showFileNameInfo(GFileName.fromFile(new File("/pepe")));


      final String parts[] = "dir1/dir2\\filename.ext".split("[/\\\\]");
      System.out.println(Arrays.toString(parts));
   }


   private static void showFileNameInfo(final GFileName fileName) {
      System.out.println(fileName);
      System.out.println("  Parent: " + fileName.getParent());
      System.out.println();
   }


   /**
    * Test whether a the file specified by the GFileName exists
    * 
    * @return
    */
   public boolean exists() {
      return asFile().exists();
   }


   public GFileName withPrefixedName(final String prefix) {
      return GFileName.fromParentAndParts(getParent(), prefix + getName());
   }


   public boolean isDirectory() {
      return asFile().exists();
   }


}
