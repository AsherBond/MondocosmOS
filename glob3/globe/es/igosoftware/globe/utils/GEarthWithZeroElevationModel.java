

package es.igosoftware.globe.utils;

import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.globes.EllipsoidalGlobe;
import gov.nasa.worldwind.terrain.ZeroElevationModel;


public class GEarthWithZeroElevationModel
         extends
            EllipsoidalGlobe {


   public GEarthWithZeroElevationModel() {
      super(Earth.WGS84_EQUATORIAL_RADIUS, Earth.WGS84_POLAR_RADIUS, Earth.WGS84_ES, new ZeroElevationModel());
   }


   @Override
   public String toString() {
      return "Earth";
   }


}
