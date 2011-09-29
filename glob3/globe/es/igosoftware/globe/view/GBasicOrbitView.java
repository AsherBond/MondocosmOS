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


package es.igosoftware.globe.view;

import es.igosoftware.util.GMath;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.view.ViewUtil;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import gov.nasa.worldwind.view.orbit.BasicOrbitViewLimits;


public class GBasicOrbitView
         extends
            BasicOrbitView {


   @Override
   protected double computeNearDistance(final Position eyePosition1) {
      double near = 0;

      if ((eyePosition1 != null) && (dc != null)) {
         final double elevation = ViewUtil.computeElevationAboveSurface(dc, eyePosition1);
         final double tanHalfFov = fieldOfView.tanHalfAngle();
         near = elevation / (2 * GMath.sqrt(2 * tanHalfFov * tanHalfFov + 1));
      }

      return (near < 0.001) ? 0.001 : near / 4;
   }


   @Override
   public void setCenterPosition(final Position center1) {
      if (center1 == null) {
         final String message = Logging.getMessage("nullValue.PositionIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }
      if ((center1.getLatitude().degrees < -90) || (center1.getLatitude().degrees > 90)) {
         final String message = Logging.getMessage("generic.LatitudeOutOfRange", center1.getLatitude());
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      center = normalizedCenterPosition(center1);
      center = BasicOrbitViewLimits.limitCenterPosition(center, getOrbitViewLimits());

   }


}
