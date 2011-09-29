

package es.igosoftware.panoramic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.igosoftware.io.GFileName;
import es.igosoftware.utils.GPanoramicCompiler;


public class GBatchPanoramicCompiler {


   public static void main(final String[] args) {
      System.out.println("Batch  Panoramic Compiler 0.1");
      System.out.println("----------------------\n");
      if ((args.length != 2) && (args.length != 3)) {
         //logSevere("\tInvalid arguments: SourceImageDirectorName and OutputDirectoryName are mandatory DEBUGFLAG is optional");
         System.exit(1);
      }

      final GFileName sourceImageDirectoryName = GFileName.fromFile(new File(args[0]));
      final GFileName outputDirectoryName = GFileName.fromFile(new File(args[1]));

      //      final boolean debug;
      //      if (args.length == 3) {
      //         debug = args[2].trim().toLowerCase().equals("debug");
      //         //logInfo("** DEBUG MODE **");
      //      }
      //      else {
      //         debug = false;
      //      }

      final File sourceDir = sourceImageDirectoryName.asFile();

      if (!sourceDir.exists()) {
         //logSevere("\tSourceDir (" + sourceDir + ") doesn't exist");
         System.exit(1);
      }

      process(sourceDir, sourceImageDirectoryName, outputDirectoryName);
   }


   private static void process(final File sourceDir,
                               final GFileName inDir,
                               final GFileName outputDirectoryName) {

      boolean isWorking = false;

      //get File Names:
      final String[] images = sourceDir.list();
      final List<String> imagesNames = new ArrayList<String>();

      if (images == null) {
         System.out.println("Directory is either empty or does not exist!");
         System.exit(1);
      }
      else {
         for (final String filename : images) {
            imagesNames.add(filename);
            System.out.println(filename);
         }
      }


      //call GPlanarPanoramicCompiler
      for (final String fileName : imagesNames) {
         System.out.println("first file: " + fileName);
         if (!isWorking) {
            isWorking = true;
            if (fileName.endsWith(".jpg")) {
               try {
                  System.out.println("calling processImage");
                  GPanoramicCompiler.process(GFileName.fromParentAndParts(inDir, fileName), outputDirectoryName, false);
               }
               catch (final IOException e) {
                  e.printStackTrace();
               }
               finally {
                  System.out.println("feddich!");
                  isWorking = false;
               }
            }
            else {
               System.out.println("ignoring " + fileName + "...");
               isWorking = false;
            }
         }
      }
   }


}
