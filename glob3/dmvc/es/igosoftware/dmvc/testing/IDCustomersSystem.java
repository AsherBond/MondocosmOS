

package es.igosoftware.dmvc.testing;

import java.util.List;

import es.igosoftware.dmvc.model.IDAsynchronousExecutionListener;
import es.igosoftware.dmvc.model.IDModel;


public interface IDCustomersSystem
         extends
            IDModel {


   public String getFoo();


   public void setFoo(final String value);


   public void getFooAsync(final IDAsynchronousExecutionListener<String, Exception> listener);


   public void setFooAsync(final String value,
                           final IDAsynchronousExecutionListener<Void, Exception> listener);


   public List<IDCustomer> getCustomers();


   public String getFooSlowly(final int delay);


   public void ping();

}
