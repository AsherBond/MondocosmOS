

package es.igosoftware.globe.demo;

import es.igosoftware.globe.GGlobeWidget;


public class GGlobeDemoWidget
         extends
            GGlobeWidget<GGlobeDemo> {


   private static final long serialVersionUID = 1L;


   @Override
   protected GGlobeDemo createApplication() {
      return new GGlobeDemo();
   }


}
