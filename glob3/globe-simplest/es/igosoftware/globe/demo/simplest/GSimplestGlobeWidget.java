

package es.igosoftware.globe.demo.simplest;

import javax.swing.SwingUtilities;

import es.igosoftware.globe.GGlobeWidget;


public class GSimplestGlobeWidget
         extends
            GGlobeWidget<GSimplestGlobeApplication> {


   private static final long serialVersionUID = 1L;


   @Override
   protected GSimplestGlobeApplication createApplication() {
      return new GSimplestGlobeApplication();
   }


   public static void main(final String[] args) {
      GGlobeWidget.initializeGUI();

      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            new GSimplestGlobeWidget().openInFrame();
         }
      });
   }


}
