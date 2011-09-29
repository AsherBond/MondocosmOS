

package es.igosoftware.dmvc.testing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.igosoftware.dmvc.model.GDModel;
import es.igosoftware.dmvc.model.GDProperty;
import es.igosoftware.dmvc.model.IDAsynchronousExecutionListener;
import es.igosoftware.dmvc.model.IDProperty;
import es.igosoftware.util.GUtils;


public class GDCustomersSystem
         extends
            GDModel
         implements
            IDCustomersSystem {


   private final List<IDCustomer> _customers;
   private String                 _foo = "Initial foo value";


   public GDCustomersSystem() {
      _customers = new ArrayList<IDCustomer>();

      _customers.add(new GDCustomer("Michael Jackson"));
      _customers.add(new GDCustomer("Esteban Trabajos (Steve Jobs)"));
      _customers.add(new GDCustomer("Guillermo Puertas (Bill Gates)"));
      _customers.add(new GDCustomer("Alberto Unapiedra (Albert Einstein)"));
   }


   @Override
   public synchronized String getFoo() {
      return _foo;
   }


   @Override
   public synchronized void setFoo(final String foo) {
      if (GUtils.equals(_foo, foo)) {
         return;
      }

      final String oldValue = _foo;
      _foo = foo;
      firePropertyChange("foo", oldValue, _foo);
   }


   @Override
   public String toString() {
      return "GDCustomersSystem";
   }


   @Override
   public List<IDCustomer> getCustomers() {
      return Collections.unmodifiableList(_customers);
   }


   @Override
   public String getFooSlowly(final int delay) {
      GUtils.delay(delay);
      return getFoo();
   }


   @Override
   public void getFooAsync(final IDAsynchronousExecutionListener<String, Exception> listener) {

      //      final Thread delayer = new Thread() {
      //         @Override
      //         public void run() {
      //            Utils.delay(5000);
      //            listener.evaluated(getFoo(), null);
      //         }
      //      };
      //      delayer.start();

      listener.evaluated(getFoo(), null);
   }


   @Override
   public void setFooAsync(final String value,
                           final IDAsynchronousExecutionListener<Void, Exception> listener) {
      setFoo(value);
      listener.evaluated(null, null);
   }


   @Override
   protected List<IDProperty> defaultProperties() {
      final List<IDProperty> result = new ArrayList<IDProperty>();
      result.add(new GDProperty(this, "foo", false));
      return result;
   }


   @Override
   public void ping() {
      System.out.println("PING!");

      setFoo("Foo From PING (force a change)"); // force a change in foo's timestamp
   }


}
