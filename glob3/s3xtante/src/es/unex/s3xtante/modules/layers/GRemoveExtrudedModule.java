

package es.unex.s3xtante.modules.layers;

import java.util.Arrays;
import java.util.List;

import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.actions.GButtonGenericAction;
import es.igosoftware.globe.actions.GGenericAction;
import es.igosoftware.globe.actions.IGenericAction;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;


public class GRemoveExtrudedModule
         extends
            GAbstractGlobeModule {


   public GRemoveExtrudedModule(final IGlobeRunningContext context) {
      super(context);
   }


   @Override
   public String getDescription() {
      return "Remove Extruded";
   }


   @Override
   public List<? extends IGenericAction> getGenericActions(final IGlobeRunningContext context) {

      final GGenericAction extruder = new GButtonGenericAction("Remove Extruded", 'R', null, IGenericAction.MenuArea.EDIT, false) {
         @Override
         public void execute() {
            removeExtruded(context);
         }
      };

      return Arrays.asList(extruder);
   }


   protected void removeExtruded(final IGlobeRunningContext context) {

      final LayerList layers = context.getWorldWindModel().getLayerList();
      for (int i = 0; i < layers.size(); i++) {
         final Layer layer = layers.get(i);
         if (layer instanceof RenderableExtrudedLayer) {
            layers.remove(i);
            i--;
         }

      }
   }


   @Override
   public String getVersion() {
      return null;
   }


   @Override
   public String getName() {
      return "Remove Extruded";
   }


}
