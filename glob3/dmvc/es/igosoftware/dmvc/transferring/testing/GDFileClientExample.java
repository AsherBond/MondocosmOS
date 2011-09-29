

package es.igosoftware.dmvc.transferring.testing;

import java.io.File;
import java.io.IOException;

import es.igosoftware.dmvc.client.GDClient;
import es.igosoftware.dmvc.transferring.GDFileClient;
import es.igosoftware.dmvc.transferring.IDFileServer;
import es.igosoftware.io.GFileName;
import es.igosoftware.util.GProcessor;


public class GDFileClientExample {

   public static void main(final String[] args) throws IOException {
      System.out.println("GDFileClientExample 0.1");
      System.out.println("-----------------------\n");


      // Print usage if no argument is specified.
      if (args.length > 2) {
         System.err.println("Usage: " + GDFileClientExample.class.getSimpleName() + " [<host> [<port>]]");
         return;
      }

      // Parse options.
      final String host = (args.length >= 1) ? args[0] : "127.0.0.1";
      final int port = (args.length >= 2) ? Integer.parseInt(args[1]) : 8080;


      final String cacheDirectoryName = "/tmp/cache";
      //      try {
      //         GIOUtils.assureEmptyDirectory(cacheDirectoryName);
      //      }
      //      catch (final IOException e) {
      //         e.printStackTrace();
      //      }


      final GDClient client = new GDClient(host, port, true);

      final int sessionID = client.getSessionID();
      System.out.println("Session ID=" + sessionID);


      final IDFileServer fileServer = (IDFileServer) client.getRootObject();
      System.out.println("Root Model=" + fileServer);


      final GDFileClient fileClient = new GDFileClient(fileServer, cacheDirectoryName);

      final File unexistingFile = fileClient.getFile(GFileName.relative("unexisting.file"));
      System.out.println("unexisting.file: " + unexistingFile);


      for (int i = 0; i < 5; i++) {
         final int finalI = i;
         fileClient.getFile(GFileName.relative("lipsum.txt"), new GProcessor<File>() {
            @Override
            public void process(final File element) {
               System.out.println("lipsum.txt #" + finalI + ": " + element.getAbsolutePath());
            }
         });
      }


      //      for (int i = 0; i < 5; i++) {
      //         final int finalI = i;
      //         final long start = System.currentTimeMillis();
      //         fileClient.getFile("pacman.jpeg", new GProcessor<File>() {
      //            @Override
      //            public void process(final File element) {
      //               final long elapsed = System.currentTimeMillis() - start;
      //               System.out.println("got pacman.jpeg #" + finalI + ": " + element.getAbsolutePath() + " in " + elapsed + "ms");
      //            }
      //         });
      //      }

   }

}
