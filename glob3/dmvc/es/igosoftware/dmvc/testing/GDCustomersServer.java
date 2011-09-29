

package es.igosoftware.dmvc.testing;

import es.igosoftware.dmvc.server.GDServer;


public class GDCustomersServer {

   @SuppressWarnings("unused")
   public static void main(final String[] args) {
      System.out.println("GDCustomersServer 0.1");
      System.out.println("---------------------\n");

      if (args.length > 1) {
         System.err.println("Usage: " + GDCustomersServer.class.getSimpleName() + " [<port>]");
         return;
      }

      final int port = (args.length >= 1) ? Integer.parseInt(args[0]) : 8000;

      final GDCustomersSystem rootObject = new GDCustomersSystem();
      new GDServer(port, rootObject, true);

      //      final Thread setter = new Thread("Setter") {
      //         @Override
      //         public void run() {
      //            int i = 0;
      //            while (true) {
      //               Utils.delay(500); // 2 sets per second
      //               //Utils.delay(1);
      //               rootObject.setFoo("Foo Value from Server #" + i++);
      //            }
      //         }
      //      };
      //      setter.setDaemon(true);
      //      setter.start();
   }

}
