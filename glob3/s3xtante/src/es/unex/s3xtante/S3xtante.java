

package es.unex.s3xtante;

import java.awt.Image;
import java.util.List;

import javax.swing.SwingUtilities;

import es.igosoftware.experimental.vectorial.GVectorial2DModule;
import es.igosoftware.globe.GGlobeApplication;
import es.igosoftware.globe.GGlobeWidget;
import es.igosoftware.globe.GLayersManagerModule;
import es.igosoftware.globe.IGlobeModule;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.modules.layers.GAddRasterLayerModule;
import es.igosoftware.util.GCollections;
import es.unex.s3xtante.modules.layers.GExtruderModule;
import es.unex.s3xtante.modules.layers.GRemoveExtrudedModule;
import es.unex.s3xtante.modules.sextante.GSextanteModule;
import es.unex.s3xtante.modules.tables.GAddTableModule;


public class S3xtante
         extends
            GGlobeApplication {

   private static final long   serialVersionUID = 1L;

   private static final String VERSION          = "0.1";


   public S3xtante() {
      super();
   }


   @Override
   protected String getApplicationName() {
      return "S3XTANTE";
   }


   @Override
   protected List<IGlobeModule> getInitialModules(final IGlobeRunningContext context) {
      return GCollections.asList( //
               new GLayersManagerModule(context), //
               new GVectorial2DModule(context, false), //
               new GSextanteModule(context), //
               new GAddTableModule(context), //
               new GAddRasterLayerModule(context), //
               new GExtruderModule(context), //
               new GRemoveExtrudedModule(context) //
      );
   }


   @Override
   protected Image getImageIcon() {
      return null;
   }


   @Override
   protected String getApplicationVersion() {
      return VERSION;
   }


   public static void main(final String[] args) {
      System.out.println("S3XTANTE 0.1");
      System.out.println("------------\n");

      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            new GGlobeWidget() {
               private static final long serialVersionUID = 1L;


               @Override
               protected GGlobeApplication createApplication() {
                  return new S3xtante();
               }
            }.openInFrame();
         }
      });
   }


}
