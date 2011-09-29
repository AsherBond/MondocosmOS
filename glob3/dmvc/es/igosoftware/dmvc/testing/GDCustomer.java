

package es.igosoftware.dmvc.testing;

import java.util.ArrayList;
import java.util.List;

import es.igosoftware.dmvc.model.GDModel;
import es.igosoftware.dmvc.model.GDProperty;
import es.igosoftware.dmvc.model.IDProperty;


public class GDCustomer
         extends
            GDModel
         implements
            IDCustomer {

   private final String _name;


   public GDCustomer(final String name) {
      _name = name;
   }


   @Override
   public String getName() {
      return _name;
   }


   @Override
   public String toString() {
      return "GDCustomer [name=" + _name + "]";
   }


   @Override
   protected List<IDProperty> defaultProperties() {
      final List<IDProperty> result = new ArrayList<IDProperty>();
      result.add(new GDProperty(this, "name", false));
      return result;
   }

}
