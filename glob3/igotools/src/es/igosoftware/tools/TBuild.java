

package es.igosoftware.tools;

import java.io.File;
import java.io.IOException;


public class TBuild {


   public static void main(final String[] args) throws TBuildException, IOException {
      System.out.println("TBuild 0.1");
      System.out.println("----------\n");

      final long start = System.currentTimeMillis();

      if (args.length < 1) {
         System.err.println("- ERROR: Project directory argument is not present.");
         System.exit(1);
      }

      final String projectDirectory = args[0];

      final String deployString = System.getProperty("deploy", "no").trim().toLowerCase();
      final boolean deploy = deployString.equalsIgnoreCase("yes") || deployString.equalsIgnoreCase("true");


      final String templatesDirectoryName = System.getProperty("templates.directory", "./templates");
      final File templatesDirectory = new File(templatesDirectoryName);

      final String igotoolsDirectoryName = System.getProperty("igotools.home", null);
      if (igotoolsDirectoryName == null) {
         throw new TBuildException(null, "- ERROR: Property igotools.home is not present.");
      }
      final File igotoolsDirectory = new File(igotoolsDirectoryName);

      final TProject project = TProject.getProject(projectDirectory);

      if (deploy) {
         System.out.println("***** DEPLOYING: " + project.getName() + " *****\n");
      }

      //      project.clean();
      //      project.compile(templatesDirectory);
      //      if (deploy) {
      //         project.deploy(templatesDirectory);
      //      }
      project.generateAntBuild(igotoolsDirectory, templatesDirectory);

      System.out.println("\n- Build finished in " + TUtils.getTimeMessage(System.currentTimeMillis() - start));
   }
}
