

package es.igosoftware.utils;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import es.igosoftware.io.GIOUtils;


public class GJarCleanerExample {


   public static void clean(final String inputFileName,
                            final String outputFileName) throws IOException {

      final JarFile inputFile = new JarFile(inputFileName);

      final JarOutputStream outputJar = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(outputFileName)));

      final Enumeration<? extends JarEntry> entries = inputFile.entries();
      while (entries.hasMoreElements()) {
         final JarEntry entry = entries.nextElement();

         if (skipEntry(entry)) {

            continue;
         }

         final JarEntry newEntry = new JarEntry(entry);
         outputJar.putNextEntry(newEntry);
         GIOUtils.copy(inputFile.getInputStream(entry), outputJar);
         outputJar.closeEntry();
      }

      outputJar.flush();
      outputJar.close();

   }


   private static final String[] SKIP_WILDCARDS = new String[] {
                     ".svn",
                     "all_contents",
                     "descripciones",
                     "dvd",
                     "imagenes",
                     "modelos3d",
                     "new_data_war",
                     "panoramics",
                     "videos",
                     "documentación",
                     "masters",
                     ".pdf",
                     ".doc",
                     ".flv",
                     ".jar",
                     "http-cache",
                     "3d-icons",
                     "PlanarPanos",
                     "planarPanos",
                     "Documentation",
                     "documentation",
                     "junit",
                     "jtexample",
                     "CHANGES",
                     "geotools",
                     "opengis",
                     "worldwindow",
                     "ucar",
                     "pushpins"
                                                };


   private static boolean skipEntry(final JarEntry entry) {
      final String name = entry.toString();
      for (final String wildcard : SKIP_WILDCARDS) {
         if (name.contains(wildcard)) {
            System.out.println("Removing " + entry + " reason : " + wildcard);
            return true;
         }
      }

      return false;
   }


   public static void main(final String[] args) throws IOException {
      GJarCleanerExample.clean("/home/fpulido/Escritorio/deploy/museofrontera.jar",
               "/home/fpulido/Escritorio/deploy/museofronteraSMALL.jar");
   }

}
