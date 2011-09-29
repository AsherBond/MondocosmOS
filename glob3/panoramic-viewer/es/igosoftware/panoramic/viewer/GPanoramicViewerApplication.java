

package es.igosoftware.panoramic.viewer;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import es.igosoftware.euclid.GAngle;
import es.igosoftware.globe.GGlobeApplication;
import es.igosoftware.globe.IGlobeModule;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.layers.hud.GHUDIcon;
import es.igosoftware.globe.layers.hud.GHUDLayer;
import es.igosoftware.globe.utils.GEarthWithZeroElevationModel;
import es.igosoftware.globe.utils.GOnFirstRenderLayer;
import es.igosoftware.io.GFileName;
import es.igosoftware.io.GHttpLoader;
import es.igosoftware.io.ILoader;
import es.igosoftware.panoramic.GPanoramic;
import es.igosoftware.panoramic.GPanoramicLayer;
import es.igosoftware.scenegraph.GElevationAnchor;
import es.igosoftware.util.GPair;
import es.igosoftware.util.GUtils;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.render.DrawContext;


public class GPanoramicViewerApplication
         extends
            GGlobeApplication {


   private GPanoramic _panoramicBarrancos;
   private GPanoramic _panoramicAlodroal;


   @Override
   protected String getApplicationName() {
      return "Panoramic Viewer";
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


   @Override
   protected List<IGlobeModule> getDefaultModules(final IGlobeRunningContext context) {
      return Collections.emptyList();
   }


   void switchToPanoramic(final String panoramicName) {

      final GPanoramic panoramic;

      if ("Barrancos".equals(panoramicName)) {
         panoramic = _panoramicBarrancos;
      }
      else if ("Alodroal".equals(panoramicName)) {
         panoramic = _panoramicAlodroal;
      }
      else {
         getLogger().logWarning("Can't switch to panoramic: " + panoramicName);
         return;
      }

      final GPanoramic currentPanoramic = getRunningContext().getWorldWindModel().getView().getPanoramic();
      if (currentPanoramic != panoramic) {

         getRunningContext().getWorldWindModel().getLayerList().add(new GOnFirstRenderLayer() {
            @Override
            protected void execute(final DrawContext dc) {
               currentPanoramic.deactivate();

               getRunningContext().getCameraController().instantlyGoTo(panoramic.getPosition(), 0);
               panoramic.activate(getRunningContext());
            }
         });


      }
   }


   @Override
   protected void onFirstRender(final IGlobeRunningContext context,
                                final DrawContext dc) {
      super.onFirstRender(context, dc);


      final GPanoramic firstPanoramic = _panoramicBarrancos;
      context.getCameraController().instantlyGoTo(firstPanoramic.getPosition(), 0);
      firstPanoramic.activate(context);
   }


   private GPanoramicLayer createPanoramicLayer(final IGlobeRunningContext context,
                                                final GHUDLayer hudLayer) throws IOException {

      final GPanoramicLayer panoramicLayer = new GPanoramicLayer(context, "Panoramics");

      //      final GFileName root = GFileName.absolute("home", "dgd", "Escritorio", "ASSORTED_STUFF", "PruebaPanoramicas", "PANOS");
      //      final ILoader loader = new GFileLoader(root, context.getProgressReporter());
      final ILoader loader = new GHttpLoader(new URL("http://glob3.sourceforge.net/"), true, context.getProgressReporter());

      _panoramicBarrancos = createPanoramic(context, hudLayer, panoramicLayer, loader, "Barrancos", new Position(Angle.ZERO,
               Angle.MINUTE, 0), GAngle.POS180);
      //      _panoramicBarrancos = createPanoramic(context, hudLayer, panoramicLayer, loader,
      //               "360_Alburquerque_stamariamercado_desdeAltarMayor.jpg", new Position(Angle.ZERO, Angle.MINUTE, 0), GAngle.POS180);
      panoramicLayer.addPanoramic(_panoramicBarrancos);

      _panoramicAlodroal = createPanoramic(context, hudLayer, panoramicLayer, loader, "Alodroal", new Position(Angle.ZERO,
               Angle.MINUTE, 0), GAngle.POS180);
      panoramicLayer.addPanoramic(_panoramicAlodroal);

      return panoramicLayer;
   }


   private GPanoramic createPanoramic(final IGlobeRunningContext context,
                                      final GHUDLayer hudLayer,
                                      final GPanoramicLayer panoramicLayer,
                                      final ILoader loader,
                                      final String name,
                                      final Position position,
                                      final GAngle heading) throws IOException {
      final GFileName fileName = GFileName.relative("globe-demo-data", "panoramics", name);
      // final GFileName fileName = GFileName.relative(name);

      return new GPanoramic(context, panoramicLayer, name, loader, fileName, 50, position, GElevationAnchor.SURFACE, heading,
               hudLayer) {
         @Override
         protected float getInactiveOpacity() {
            return 1;
         }


         @Override
         protected float getActiveOpacity() {
            return 1;
         }


         @Override
         public boolean acceptExitFromESCKey() {
            return false;
         }


         @Override
         protected GHUDIcon createHUDIcon(final IGlobeRunningContext unusedContext) {
            return null;
         }
      };
   }


   @Override
   protected LayerList getDefaultLayers(final IGlobeRunningContext context) {
      // no super call to start with an empty layer list
      final LayerList layers = new LayerList();

      final GHUDLayer hudLayer = new GHUDLayer(context);
      layers.add(hudLayer);

      try {
         layers.add(createPanoramicLayer(context, hudLayer));
      }
      catch (final IOException e) {
         context.getLogger().logSevere(e);
      }

      return layers;
   }


   @Override
   protected Globe createGlobe() {
      return new GEarthWithZeroElevationModel();
   }


   private Component createSwitchPanoramicButton(final String panoramicName) {
      final JButton button = new JButton(panoramicName);
      button.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            switchToPanoramic(panoramicName);
         }
      });
      return button;
   }


   @Override
   protected List<GPair<String, Component>> getApplicationPanels(final IGlobeRunningContext context) {
      if (getWidget().isApplet()) {
         return Collections.emptyList();
      }


      final List<GPair<String, Component>> panels = new ArrayList<GPair<String, Component>>();

      final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
      panel.add(createSwitchPanoramicButton("Barrancos"));
      panel.add(createSwitchPanoramicButton("Alodroal"));

      panels.add(new GPair<String, Component>(getApplicationName(), panel));

      return panels;
   }


}
