

package es.igosoftware.globe;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;


public interface IGlobeCameraController {


   /**
    * @param position
    * @param elevation
    */
   public void animatedGoTo(final Position position,
                            final double elevation);


   /**
    * @param position
    * @param heading
    * @param pitch
    * @param elevation
    */
   public void animatedGoTo(final Position position,
                            final Angle heading,
                            final Angle pitch,
                            final double elevation);


   /**
    * @param heading
    */
   public void goToHeading(final Angle heading);


   /**
    * @param sector
    */
   public void animatedZoomToSector(final Sector sector);


   /**
    * @param sector
    * @param altitude
    */
   public void animatedZoomToSector(final Sector sector,
                                    final double altitude);


   /**
    * @param position
    * @param elevation
    */
   public void instantlyGoTo(final Position position,
                             final double elevation);


   /**
    * @param position
    * @param heading
    * @param pitch
    * @param elevation
    */
   public void instantlyGoTo(final Position position,
                             final Angle heading,
                             final Angle pitch,
                             final double elevation);


   /**
    * @param sector
    * @return
    */
   public double calculateAltitudeForZooming(final Sector sector);


}
