

package es.igosoftware.dmvc.testing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.List;

import es.igosoftware.dmvc.client.GDClient;
import es.igosoftware.dmvc.model.IDAsynchronousExecutionListener;
import es.igosoftware.util.GUtils;


public class GDCustomersClient {


   public static void main(final String[] args) throws IOException {
      System.out.println("GDCustomersClient 0.1");
      System.out.println("---------------------\n");


      // Print usage if no argument is specified.
      if (args.length > 2) {
         System.err.println("Usage: " + GDClient.class.getSimpleName() + " [<host> [<port>]]");
         return;
      }

      // Parse options. 
      final String host = (args.length >= 1) ? args[0] : "127.0.0.1";
      final int port = (args.length >= 2) ? Integer.parseInt(args[1]) : 8000;

      final GDClient client = new GDClient(host, port, true);

      final int sessionID = client.getSessionID();
      System.out.println("Session ID=" + sessionID);


      final IDCustomersSystem system = (IDCustomersSystem) client.getRootObject();
      System.out.println("Root Model=" + system);

      system.ping();

      final PropertyChangeListener listener = new PropertyChangeListener() {
         @Override
         public void propertyChange(final PropertyChangeEvent evt) {
            System.out.println("    >> changed foo from " + GUtils.toString(evt.getOldValue()) + " to "
                               + GUtils.toString(evt.getNewValue()));
         }
      };
      system.addPropertyChangeListener("foo", listener);

      System.out.println(system.getFoo());

      //      //      try {
      //      //         ((IDProperty<String>) system.getProperties().get(0)).set("Value from property");
      //      //      }
      //      //      catch (final Exception e) {
      //      //         e.printStackTrace();
      //      //      }
      //
      system.setFooAsync("Foo Value from Async execution", new IDAsynchronousExecutionListener<Void, Exception>() {
         @Override
         public void evaluated(final Void result,
                               final Exception exception) {
            System.out.println("-----> ASYNCHRONOUS RESULT: setFooAsync(), result=" + result + ", exception=" + exception);
         }
      });

      system.getFooAsync(new IDAsynchronousExecutionListener<String, Exception>() {
         @Override
         public void evaluated(final String result,
                               final Exception exception) {
            System.out.println("-----> ASYNCHRONOUS RESULT: getFooAsync(), result=" + result + ", exception=" + exception);
         }
      });

      final String fooResult = system.getFoo();
      System.out.println("Root Model foo()=" + fooResult);

      System.out.println("Root Model slow foo()=" + system.getFooSlowly(500 /* 0.5 seconds */));


      //      final PropertyChangeListener listener = new PropertyChangeListener() {
      //         @Override
      //         public void propertyChange(final PropertyChangeEvent evt) {
      //            System.out.println("    >> changed foo from " + Utils.toString(evt.getOldValue()) + " to "
      //                               + Utils.toString(evt.getNewValue()));
      //         }
      //      };
      //      system.addPropertyChangeListener("foo", listener);

      system.setFoo("New Foo String from " + sessionID);
      system.setFoo("Just another new Foo String from " + sessionID);


      //      system.removePropertyChangeListener("foo", listener);
      //
      //      system.setFoo("A foo value after removing the listener" + sessionID);

      final List<IDCustomer> customers1 = system.getCustomers();
      System.out.println("Customers=" + customers1);
      for (final IDCustomer customer : customers1) {
         System.out.println("  " + customer.getName());
      }


      //      final List<IDCustomer> customers2 = system.getCustomers();
      //      System.out.println("Customers=" + customers2);
      //      for (final IDCustomer customer : customers2) {
      //         System.out.println("  " + customer.getName());
      //      }
      //
      //      System.out.println(customers1.get(0) == customers2.get(0));

   }
}
