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

import es.igosoftware.globe.view.GInputState;
import es.igosoftware.panoramic.GPanoramic;
import es.igosoftware.util.GMath;
import es.igosoftware.utils.GWWUtils;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.awt.ViewInputAttributes;
import gov.nasa.worldwind.awt.ViewInputAttributes.ActionAttributes;
import gov.nasa.worldwind.awt.ViewInputAttributes.DeviceAttributes;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.view.orbit.OrbitView;
import gov.nasa.worldwind.view.orbit.OrbitViewInputHandler;

import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;


public class GViewInputHandler
         extends
            OrbitViewInputHandler {


   public GViewInputHandler() {
      super();
   }


   //**************************************************************//
   //********************  View Change Events  ********************//
   //**************************************************************//


   @Override
   protected void onMoveTo(final Position focalPosition,
                           final ViewInputAttributes.DeviceAttributes deviceAttributes,
                           final ViewInputAttributes.ActionAttributes actionAttribs) {

      final View view = getView();
      if (view == null) { // include this test to ensure any derived implementation performs it
         return;
      }

      GInputState inputState = null;
      if (view instanceof GView) {
         inputState = ((GView) view).getInputState();
      }
      if ((inputState != null) && (!inputState.isMoving())) {
         return;
      }

      super.onMoveTo(focalPosition, deviceAttributes, actionAttribs);
   }


   @Override
   protected void onHorizontalTranslateAbs(final Angle latitudeChange,
                                           final Angle longitudeChange,
                                           final ViewInputAttributes.ActionAttributes actionAttribs) {

      stopGoToAnimators();
      stopUserInputAnimators(VIEW_ANIM_HEADING, VIEW_ANIM_PITCH, VIEW_ANIM_ZOOM);

      final View view = getView();
      if (view == null) { // include this test to ensure any derived implementation performs it
         return;
      }

      GInputState inputState = null;
      if (view instanceof GView) {
         inputState = ((GView) view).getInputState();
      }
      if ((inputState != null) && (!inputState.isMoving())) {
         return;
      }

      super.onHorizontalTranslateAbs(latitudeChange, longitudeChange, actionAttribs);

   }


   @Override
   protected void onHorizontalTranslateRel(final double forwardInput,
                                           final double sideInput,
                                           final double totalForwardInput,
                                           final double totalSideInput,
                                           final ViewInputAttributes.DeviceAttributes deviceAttributes,
                                           final ViewInputAttributes.ActionAttributes actionAttributes) {
      final View view = getView();
      GInputState inputState = null;
      if (view instanceof GView) {
         inputState = ((GView) view).getInputState();
      }
      if ((inputState != null) && (!inputState.isMoving())) {
         onRotateView(Angle.fromDegrees(-sideInput * 0.5), Angle.fromDegrees(forwardInput * 0.5), actionAttributes);
         return;
      }

      super.onHorizontalTranslateRel(forwardInput, sideInput, totalForwardInput, totalSideInput, deviceAttributes,
               actionAttributes);

   }


   @Override
   protected void onRotateView(final Angle headingChange,
                               final Angle pitchChange,
                               final ActionAttributes actionAttribs) {
      final View view = getView();
      if (view == null) { // include this test to ensure any derived implementation performs it
         return;
      }


      if (view instanceof GView) {
         final GView customView = (GView) view;
         final GInputState inputState = customView.getInputState();
         if (!headingChange.equals(Angle.ZERO)) {

            if (inputState == GInputState.PANORAMICS) {
               //for panoramics lower rotating speed when zoomed in
               changeHeading(
                        (GView) view,
                        uiAnimControl,
                        (headingChange.multiply(-1.0)).multiply(0.4 * (view.getFieldOfView().divide(GPanoramic.MAXIMUM_FOV)).degrees),
                        actionAttribs);
            }
            else {
               changeHeading((GView) view, uiAnimControl, headingChange, actionAttribs);
            }
         }

         if (!pitchChange.equals(Angle.ZERO)) {

            if (inputState == GInputState.PANORAMICS) {
               //for panoramics lower rotating speed when zoomed in
               changePitch((GView) view, uiAnimControl,
                        pitchChange.multiply(0.4 * (view.getFieldOfView().divide(GPanoramic.MAXIMUM_FOV)).degrees), actionAttribs);
            }
            else {
               changePitch((GView) view, uiAnimControl, pitchChange, actionAttribs);
            }
         }
      }
   }


   //
   //
   /**
    * This Method was overwritten in order to avoid the annoying switch in direction of rotation
    */
   @Override
   protected void onRotateView(double headingInput,
                               double pitchInput,
                               final double totalHeadingInput,
                               final double totalPitchInput,
                               final DeviceAttributes deviceAttributes,
                               final ActionAttributes actionAttributes) {
      stopGoToAnimators();
      stopUserInputAnimators(VIEW_ANIM_CENTER, VIEW_ANIM_ZOOM);

      if (actionAttributes.getMouseActions() != null) {}
      else {
         final double length = GMath.sqrt(headingInput * headingInput + pitchInput * pitchInput);
         if (length > 0.0) {
            headingInput /= length;
            pitchInput /= length;
         }
      }

      final Angle headingChange = Angle.fromDegrees(headingInput * getScaleValueRotate(actionAttributes));
      final Angle pitchChange = Angle.fromDegrees(pitchInput * getScaleValueRotate(actionAttributes));

      onRotateView(headingChange, pitchChange, actionAttributes);
   }


   public void instantlyGoTo(final LatLon lookAtPos,
                             final double distance) {
      final OrbitView view = (OrbitView) getView();
      stopAnimators();
      view.setCenterPosition(new Position(lookAtPos.latitude, lookAtPos.longitude, view.getGlobe().getElevation(
               lookAtPos.latitude, lookAtPos.longitude)));
      view.setZoom(distance);
      GWWUtils.redraw(view);
   }


   /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   // Custom input Event handling
   //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   @Override
   protected void handleMouseWheelMoved(final MouseWheelEvent e) {
      boolean eventHandled = false;

      final View view = getView();
      if (view instanceof GView) {
         final GInputState inputState = ((GView) view).getInputState();
         if ((inputState != null) && (inputState.isPanoramicZoom())) {
            eventHandled = onChangeFieldOfView(e, view);
         }
      }

      if (!eventHandled) {
         super.handleMouseWheelMoved(e);
      }
   }


   @Override
   protected void handleKeyPressed(final KeyEvent e) {
      boolean eventHandled = false;

      if (e.getKeyCode() == 27) { // Escape
         final View view = getView();
         if (view instanceof GView) {
            final GView gView = (GView) view;
            final GInputState inputState = gView.getInputState();
            if (inputState == GInputState.PANORAMICS) {
               eventHandled = tryToExitFromPanoramic(gView);
            }
         }
      }

      if (!eventHandled) {
         super.handleKeyPressed(e);
      }
   }


   private boolean onChangeFieldOfView(final MouseWheelEvent e,
                                       final View view) {


      final double oldFov = view.getFieldOfView().degrees;
      final double newFov = oldFov + (e.getWheelRotation() * 5) * view.getFieldOfView().degrees / GPanoramic.MAXIMUM_FOV;

      view.setFieldOfView(Angle.fromDegrees(GMath.clamp(newFov, GPanoramic.MINIMUM_FOV, GPanoramic.MAXIMUM_FOV)));
      e.consume();

      return true;
   }


   private boolean tryToExitFromPanoramic(final GView view) {
      final GPanoramic panoramic = view.getPanoramic();
      if (panoramic != null) {
         if (panoramic.acceptExitFromESCKey()) {
            panoramic.deactivate();
            return true;
         }
      }

      return false;
   }


}
