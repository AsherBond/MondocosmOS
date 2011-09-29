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

import es.igosoftware.globe.GCameraState;
import es.igosoftware.globe.view.GInputState;
import es.igosoftware.globe.view.GPanoramicViewLimits;
import es.igosoftware.panoramic.GPanoramic;
import es.igosoftware.utils.GWWUtils;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import gov.nasa.worldwind.view.orbit.OrbitViewLimits;


public class GView
         extends
            BasicOrbitView {


   private GInputState     _inputState;

   private GCameraState    _savedCameraState;

   private GPanoramic      _panoramic;
   private OrbitViewLimits _lastViewLimits;


   public GView() {
      loadConfigurationValues();

      viewInputHandler = new GViewInputHandler();
      viewLimits = new GViewLimits();

      _inputState = GInputState.ORBIT;

      collisionSupport.setCollisionThreshold(COLLISION_THRESHOLD);
      collisionSupport.setNumIterations(COLLISION_NUM_ITERATIONS);
      getViewInputHandler().setStopOnFocusLost(false);
   }


   @Override
   public void setNearClipDistance(final double clipDistance) {
      super.setNearClipDistance(clipDistance);
   }


   public void enterPanoramic(final GPanoramic panoramic) {
      if (hasCameraState()) {
         throw new RuntimeException("The View already has a camera state");
      }
      saveCameraState();

      _panoramic = panoramic;
      _lastViewLimits = getOrbitViewLimits();

      setInputState(GInputState.PANORAMICS);

      //      final int Diego_at_work;
      //      instantlyGoTo(panoramic.getPosition(), 0);

      stopAnimations();
      setCenterPosition(panoramic.getPosition());

      setOrbitViewLimits(new GPanoramicViewLimits());

      setFieldOfView(Angle.fromDegrees(GPanoramic.INITIAL_FOV));
      setPitch(Angle.fromDegrees(90));

      setZoom(0);
      GWWUtils.redraw(this);
   }


   public void exitPanoramic(final GPanoramic panoramic) {
      if (panoramic != _panoramic) {
         System.out.println("This is not the same panoramic that you entered...");
      }
      setInputState(GInputState.ORBIT);

      setOrbitViewLimits(_lastViewLimits);
      restoreCameraState();

      _panoramic = null;
      _lastViewLimits = null;

   }


   public GPanoramic getPanoramic() {
      return _panoramic;
   }


   public void setInputState(final GInputState state) {
      _inputState = state;
   }


   public GInputState getInputState() {
      return _inputState;
   }


   @Override
   protected void resolveCollisionsWithPitch() {
      if ((_inputState != null) && _inputState.isDetectCollisions()) {
         super.resolveCollisionsWithPitch();
      }
   }


   @Override
   protected void resolveCollisionsWithCenterPosition() {
      if ((_inputState != null) && _inputState.isDetectCollisions()) {
         super.resolveCollisionsWithCenterPosition();
      }
   }


   public boolean hasCameraState() {
      return (_savedCameraState != null);
   }


   public void saveCameraState() {
      _savedCameraState = new GCameraState(this);
   }


   public void restoreCameraState() {
      if (!hasCameraState()) {
         throw new RuntimeException("The View has not a saved camera state");
      }

      stopAnimations();

      _savedCameraState.restoreView(this);
      _savedCameraState = null;
   }


   //

   /**
    * 
    * @param position
    * @param distance
    * 
    *           Does the same as <code>goTo(Position position, double distance)</code> but without an animation
    */
   public void instantlyGoTo(final LatLon position,
                             final double distance) {
      ((GViewInputHandler) viewInputHandler).instantlyGoTo(position, distance);
   }


   //   protected static final double MINIMUM_NEAR_DISTANCE = 0.1;
   //   protected static final double MINIMUM_FAR_DISTANCE  = 100;
   //
   //
   //   @Override
   //   protected double computeNearDistance(final Position eyePosition1) {
   //      double near = 0;
   //      if ((eyePosition1 != null) && (dc != null)) {
   //         final double elevation = ViewUtil.computeElevationAboveSurface(dc, eyePosition1);
   //         final double tanHalfFov = fieldOfView.tanHalfAngle();
   //         near = elevation / (2 * Math.sqrt(2 * tanHalfFov * tanHalfFov + 1));
   //      }
   //
   //      return near < MINIMUM_NEAR_DISTANCE ? MINIMUM_NEAR_DISTANCE : near;
   //   }
   //
   //
   //   @Override
   //   protected double computeFarDistance(final Position eyePosition1) {
   //      double far = 0;
   //      if (eyePosition1 != null) {
   //         far = computeHorizonDistance(eyePosition1);
   //      }
   //
   //      return far < MINIMUM_FAR_DISTANCE ? MINIMUM_FAR_DISTANCE : far;
   //   }


}
