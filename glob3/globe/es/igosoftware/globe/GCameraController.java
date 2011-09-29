

package es.igosoftware.globe;

import es.igosoftware.globe.view.customView.GView;
import es.igosoftware.globe.view.customView.GViewInputHandler;
import es.igosoftware.util.GMath;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.view.orbit.OrbitView;
import gov.nasa.worldwind.view.orbit.OrbitViewInputHandler;


final class GCameraController
         implements
            IGlobeCameraController {

   private final GGlobeApplication _application;


   GCameraController(final GGlobeApplication application) {
      _application = application;
   }


   @Override
   public void animatedGoTo(final Position position,
                            final double elevation) {
      _application.getWorldWindModel().getView().goTo(position, elevation);
   }


   @Override
   public void animatedGoTo(final Position position,
                            final Angle heading,
                            final Angle pitch,
                            final double elevation) {
      final View view = _application.getWorldWindModel().getView();
      if ((heading == null) || (pitch == null)) {
         view.goTo(position, elevation);
         return;
      }


      if (view instanceof GView) {
         final GViewInputHandler inputHandler = (GViewInputHandler) (view).getViewInputHandler();

         inputHandler.stopAnimators();
         inputHandler.addPanToAnimator(position, heading, pitch, elevation, true);

         _application.redraw();
      }
      else if (view instanceof OrbitView) {
         final OrbitViewInputHandler inputHandler = (OrbitViewInputHandler) ((OrbitView) view).getViewInputHandler();

         inputHandler.stopAnimators();
         inputHandler.addPanToAnimator(position, heading, pitch, elevation, true);

         _application.redraw();
      }
      else {
         view.goTo(position, elevation);
      }

   }


   @Override
   public void goToHeading(final Angle heading) {
      final View view = _application.getWorldWindModel().getView();

      final Angle currentHeading = view.getHeading();

      if ((heading == null) || GMath.closeTo(heading.degrees, currentHeading.degrees)) {
         return;
      }


      if (view instanceof GView) {
         final GViewInputHandler inputHandler = (GViewInputHandler) (view).getViewInputHandler();

         inputHandler.stopAnimators();
         inputHandler.addHeadingAnimator(currentHeading, heading);

         _application.redraw();
      }
      else if (view instanceof OrbitView) {
         final OrbitViewInputHandler inputHandler = (OrbitViewInputHandler) ((OrbitView) view).getViewInputHandler();

         inputHandler.stopAnimators();
         inputHandler.addHeadingAnimator(currentHeading, heading);

         _application.redraw();
      }
      else {
         // fall back to change without animation
         view.setHeading(heading);
      }
   }


   @Override
   public void instantlyGoTo(final Position position,
                             final double elevation) {
      final GView view = _application.getWorldWindModel().getView();
      view.instantlyGoTo(position, elevation);
   }


   @Override
   public void instantlyGoTo(final Position position,
                             final Angle heading,
                             final Angle pitch,
                             final double elevation) {
      if ((heading == null) || (pitch == null)) {
         _application.getWorldWindModel().getView().goTo(position, elevation);
      }
      else {
         final GView view = _application.getWorldWindModel().getView();

         final GViewInputHandler customViewInputHandler = (GViewInputHandler) view.getViewInputHandler();

         customViewInputHandler.stopAnimators();
         view.setCenterPosition(new Position(position.latitude, position.longitude, view.getGlobe().getElevation(
                  position.latitude, position.longitude)));
         view.setHeading(heading);
         view.setPitch(pitch);
         view.setZoom(elevation);

         _application.redraw();
      }
   }


   @Override
   public void animatedZoomToSector(final Sector sector) {
      if (sector == null) {
         return;
      }

      final double altitude = calculateAltitudeForZooming(sector);
      if (altitude < 0) {
         return;
      }

      animatedGoTo(new Position(sector.getCentroid(), 0), Angle.ZERO, Angle.ZERO, altitude);
   }


   @Override
   public double calculateAltitudeForZooming(final Sector sector) {
      final IGlobeWorldWindModel worldWindModel = _application.getWorldWindModel();

      final GView view = worldWindModel.getView();
      if (view == null) {
         return -1;
      }

      final Globe globe = worldWindModel.getGlobe();
      if (globe == null) {
         return -1;
      }

      final double w = 0.5 * sector.getDeltaLonRadians() * globe.getEquatorialRadius();
      final double altitude = w / view.getFieldOfView().tanHalfAngle();
      return altitude;
   }


   @Override
   public void animatedZoomToSector(final Sector sector,
                                    final double altitudeIncrease) {
      if (sector == null) {
         return;
      }

      //      final IGlobeWorldWindModel worldWindModel = _application.getWorldWindModel();
      //
      //      final View view = worldWindModel.getView();
      //      if (view == null) {
      //         return;
      //      }
      //
      //      final Globe globe = worldWindModel.getGlobe();
      //      if (globe == null) {
      //         return;
      //      }
      //
      //      final double w = 0.5 * sector.getDeltaLonRadians() * globe.getEquatorialRadius();
      //      final double altitude = w / view.getFieldOfView().tanHalfAngle();

      final double altitude = calculateAltitudeForZooming(sector);
      if (altitude < 0) {
         return;
      }
      _application.getRunningContext().getCameraController().animatedGoTo(new Position(sector.getCentroid(), 0), Angle.ZERO,
               Angle.fromDegrees(45), altitude + altitudeIncrease);

   }


}
