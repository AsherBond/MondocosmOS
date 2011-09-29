

package es.igosoftware.experimental.wms;

import java.util.Arrays;
import java.util.List;

import es.igosoftware.experimental.gui.GWMSDialog;
import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.actions.GButtonGenericAction;
import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.io.GFileName;


public class GWMSModule
         extends
            GAbstractGlobeModule {


   public GWMSModule(final IGlobeRunningContext context) {
      super(context);
   }


   @Override
   public String getName() {
      return "WMS Module";
   }


   @Override
   public String getVersion() {
      return "0.1";
   }


   @Override
   public String getDescription() {
      return "Web Map Services Module for Glob3";
   }


   @Override
   public List<? extends IGenericAction> getGenericActions(final IGlobeRunningContext context) {
      final IGenericAction addWMSLayer = new GButtonGenericAction("Add WMS layer", 'W', context.getBitmapFactory().getSmallIcon(
               GFileName.relative("earth-add.png")), IGenericAction.MenuArea.FILE, true) {

         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public void execute() {
            addNewLayer(context);
         }
      };

      return Arrays.asList(addWMSLayer);
   }


   public IGlobeLayer addNewLayer(final IGlobeRunningContext context) {

      final GWMSDialog dialog = new GWMSDialog(context);
      dialog.showWMSDialog();

      //      if (newLayer != null) {
      //         final LayerList layers = application.getLayerList();
      //         newLayer.setOpacity(0.7);
      //         layers.add(newLayer);
      //         System.out.println("Añadida WMS layer !");
      //      }
      //      else {
      //         System.out.println("POS NO PUEDO AÑADIR LAYER !");
      //      }

      return null;
   }


   //-------------------------------------------------------------------------------------------------------


}
