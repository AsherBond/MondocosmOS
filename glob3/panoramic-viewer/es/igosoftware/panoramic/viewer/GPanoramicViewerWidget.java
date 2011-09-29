

package es.igosoftware.panoramic.viewer;

import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.swing.SwingUtilities;

import es.igosoftware.globe.GGlobeWidget;


public class GPanoramicViewerWidget
         extends
            GGlobeWidget<GPanoramicViewerApplication> {


   private static final long serialVersionUID = 1L;


   static {
      System.setProperty("glob3.use_substance", "false");
   }


   @Override
   protected GPanoramicViewerApplication createApplication() {
      return new GPanoramicViewerApplication();
   }


   @Override
   public void prepareForFullScreen() {
   }


   @Override
   public void prepareForNonFullScreen() {
   }


   public void switchToPanoramic(final String panoramicName) {
      privilegedSwitchToPanoramic(panoramicName);
   }


   private void privilegedSwitchToPanoramic(final String panoramicName) {
      // IMPORTANT:
      //
      // The methods invoked from Javascript need to be executed inside a PrivilegedAction
      // to avoid some random security exceptions when running as an applet.

      AccessController.doPrivileged(new PrivilegedAction<Object>() {
         @Override
         public Object run() {
            getApplication().switchToPanoramic(panoramicName);

            return null;
         }
      });
   }


   public static void main(final String[] args) {
      GGlobeWidget.initializeGUI();

      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            new GPanoramicViewerWidget().openInFrame();
         }
      });
   }


}
