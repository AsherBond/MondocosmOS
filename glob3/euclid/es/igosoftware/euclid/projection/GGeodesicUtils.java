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


package es.igosoftware.euclid.projection;

import es.igosoftware.euclid.vector.IVector2;


public class GGeodesicUtils {
   private GGeodesicUtils() {
   }


   // Earth's quadratic mean radius for WGS-84
   private static final double EARTH_QUADRATIC_RADIUS_IN_METERS = 6372797.560856;


   /**
    * Computes the arc, in radians, between two WGS-84 positions.
    */
   public static double arcInRadians(final IVector2 from,
                                     final IVector2 to) {
      final double fromLatitude = from.y();
      final double fromLongitude = from.x();
      final double toLatitude = to.y();
      final double toLongidute = to.x();

      final double latitudeArc = fromLatitude - toLatitude;
      final double longitudeArc = fromLongitude - toLongidute;

      double latitudeH = Math.sin(latitudeArc * 0.5);
      latitudeH *= latitudeH;

      double lontitudeH = Math.sin(longitudeArc * 0.5);
      lontitudeH *= lontitudeH;

      final double tmp = Math.cos(fromLatitude) * Math.cos(toLatitude);
      return 2.0 * Math.asin(Math.sqrt(latitudeH + tmp * lontitudeH));
   }


   /**
    * Computes the distance, in meters, between two WGS-84 positions.
    */
   public static double distanceInMeters(final IVector2 from,
                                         final IVector2 to) {
      return EARTH_QUADRATIC_RADIUS_IN_METERS * arcInRadians(from, to);
   }


}
