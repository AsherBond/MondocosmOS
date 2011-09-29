

package es.igosoftware.globe.demo.simplest;

import java.awt.Image;
import java.util.Collections;
import java.util.List;

import es.igosoftware.globe.GGlobeApplication;
import es.igosoftware.globe.IGlobeModule;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.util.GUtils;


public class GSimplestGlobeApplication
         extends
            GGlobeApplication {


   @Override
   protected String getApplicationName() {
      return "Simplest Globe";
   }


   @Override
   protected String getApplicationVersion() {
      return "0.1";
   }


   @Override
   protected Image getImageIcon() {
      return GUtils.getImage("globe-icon.png", getClass().getClassLoader());
   }


   @Override
   protected List<IGlobeModule> getInitialModules(final IGlobeRunningContext context) {
      return Collections.emptyList();
   }


}
