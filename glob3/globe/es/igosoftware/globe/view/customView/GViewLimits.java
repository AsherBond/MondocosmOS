/*

 IGO Software SL  -  info@igosoftware.es

 http://www.glob3.org

-------------------------------------------------------------------------------
 Copyright (c) 2010, IGO Software SL
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
     * Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.
     * Neither the name of the IGO Software SL nor the
       names of its contributors may be used to endorse or promote products
       derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL IGO Software SL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-------------------------------------------------------------------------------

*/


package es.igosoftware.globe.view.customView;

import es.igosoftware.util.GAssert;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.RestorableSupport;
import gov.nasa.worldwind.view.BasicViewPropertyLimits;
import gov.nasa.worldwind.view.orbit.OrbitView;
import gov.nasa.worldwind.view.orbit.OrbitViewLimits;


public class GViewLimits
         extends
            BasicViewPropertyLimits
         implements
            OrbitViewLimits {

   private Sector _centerLocationLimits;
   private double _minCenterElevation;
   private double _maxCenterElevation;
   private double _minZoom;
   private double _maxZoom;


   public GViewLimits() {
      _centerLocationLimits = Sector.FULL_SPHERE;
      _minCenterElevation = -Double.MAX_VALUE;
      _maxCenterElevation = Double.MAX_VALUE;
      minHeading = Angle.NEG180;
      maxHeading = Angle.POS180;
      minPitch = Angle.ZERO;
      maxPitch = Angle.POS90;
      _minZoom = 0;
      _maxZoom = Double.MAX_VALUE;
   }


   @Override
   public double[] getCenterElevationLimits() {
      return new double[] {
                        _minCenterElevation,
                        _maxCenterElevation
      };
   }


   @Override
   public Sector getCenterLocationLimits() {
      return _centerLocationLimits;
   }


   @Override
   public double[] getZoomLimits() {
      return new double[] {
                        _minZoom,
                        _maxZoom
      };
   }


   @Override
   public void setCenterElevationLimits(final double minValue,
                                        final double maxValue) {
      _minCenterElevation = minValue;
      _maxCenterElevation = maxValue;
   }


   @Override
   public void setCenterLocationLimits(final Sector sector) {
      if (sector == null) {
         final String message = Logging.getMessage("nullValue.SectorIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      _centerLocationLimits = sector;
   }


   @Override
   public void setZoomLimits(final double minValue,
                             final double maxValue) {
      _minZoom = minValue;
      _maxZoom = maxValue;
   }


   public static void applyLimits(final OrbitView view,
                                  final OrbitViewLimits viewLimits) {
      GAssert.notNull(view, "view");
      GAssert.notNull(viewLimits, "viewLimits");

      view.setCenterPosition(limitCenterPosition(view.getCenterPosition(), viewLimits));
      view.setHeading(limitHeading(view.getHeading(), viewLimits));
      view.setPitch(limitPitch(view.getPitch(), viewLimits));
      view.setZoom(limitZoom(view.getZoom(), viewLimits));
   }


   public static Position limitCenterPosition(final Position position,
                                              final OrbitViewLimits viewLimits) {
      GAssert.notNull(position, "position");
      GAssert.notNull(viewLimits, "viewLimits");

      return new Position(limitCenterLocation(position.getLatitude(), position.getLongitude(), viewLimits), limitCenterElevation(
               position.getElevation(), viewLimits));
   }


   public static LatLon limitCenterLocation(final Angle latitude,
                                            final Angle longitude,
                                            final OrbitViewLimits viewLimits) {
      GAssert.notNull(latitude, "latitude");
      GAssert.notNull(longitude, "longitude");
      GAssert.notNull(viewLimits, "viewLimits");

      final Sector limits = viewLimits.getCenterLocationLimits();
      Angle newLatitude = latitude;
      Angle newLongitude = longitude;

      if (latitude.compareTo(limits.getMinLatitude()) < 0) {
         newLatitude = limits.getMinLatitude();
      }
      else if (latitude.compareTo(limits.getMaxLatitude()) > 0) {
         newLatitude = limits.getMaxLatitude();
      }

      if (longitude.compareTo(limits.getMinLongitude()) < 0) {
         newLongitude = limits.getMinLongitude();
      }
      else if (longitude.compareTo(limits.getMaxLongitude()) > 0) {
         newLongitude = limits.getMaxLongitude();
      }

      return new LatLon(newLatitude, newLongitude);
   }


   public static double limitCenterElevation(final double value,
                                             final OrbitViewLimits viewLimits) {
      GAssert.notNull(viewLimits, "viewLimits");

      final double[] limits = viewLimits.getCenterElevationLimits();
      double newValue = value;

      if (value < limits[0]) {
         newValue = limits[0];
      }
      else if (value > limits[1]) {
         newValue = limits[1];
      }

      return newValue;
   }


   public static double limitZoom(final double value,
                                  final OrbitViewLimits viewLimits) {
      GAssert.notNull(viewLimits, "viewLimits");

      final double[] limits = viewLimits.getZoomLimits();
      double newValue = value;

      if (value < limits[0]) {
         newValue = limits[0];
      }
      else if (value > limits[1]) {
         newValue = limits[1];
      }

      return newValue;
   }


   //**************************************************************//
   //******************** Restorable State  ***********************//
   //**************************************************************//

   @Override
   public void getRestorableState(final RestorableSupport rs,
                                  final RestorableSupport.StateObject context) {
      super.getRestorableState(rs, context);

      rs.addStateValueAsSector(context, "centerLocationLimits", _centerLocationLimits);
      rs.addStateValueAsDouble(context, "minCenterElevation", _minCenterElevation);
      rs.addStateValueAsDouble(context, "maxCenterElevation", _maxCenterElevation);
      rs.addStateValueAsDouble(context, "minZoom", _minZoom);
      rs.addStateValueAsDouble(context, "maxZoom", _maxZoom);
   }


   @Override
   public void restoreState(final RestorableSupport rs,
                            final RestorableSupport.StateObject context) {
      super.restoreState(rs, context);

      final Sector sector = rs.getStateValueAsSector(context, "centerLocationLimits");
      if (sector != null) {
         setCenterLocationLimits(sector);
      }

      // Min and max center elevation.
      double[] minAndMaxValue = getCenterElevationLimits();
      Double min = rs.getStateValueAsDouble(context, "minCenterElevation");
      if (min != null) {
         minAndMaxValue[0] = min;
      }

      Double max = rs.getStateValueAsDouble(context, "maxCenterElevation");
      if (max != null) {
         minAndMaxValue[1] = max;
      }

      if ((min != null) || (max != null)) {
         setCenterElevationLimits(minAndMaxValue[0], minAndMaxValue[1]);
      }

      // Min and max zoom value.        
      minAndMaxValue = getZoomLimits();
      min = rs.getStateValueAsDouble(context, "minZoom");
      if (min != null) {
         minAndMaxValue[0] = min;
      }

      max = rs.getStateValueAsDouble(context, "maxZoom");
      if (max != null) {
         minAndMaxValue[1] = max;
      }

      if ((min != null) || (max != null)) {
         setZoomLimits(minAndMaxValue[0], minAndMaxValue[1]);
      }
   }

}
