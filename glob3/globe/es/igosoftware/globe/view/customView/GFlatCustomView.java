

package es.igosoftware.globe.view.customView;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.render.DrawContext;


public class GFlatCustomView
         extends
            GView {


   @SuppressWarnings("hiding")
   private static final double MINIMUM_FAR_DISTANCE = 100;


   public GFlatCustomView() {
   }


   @Override
   public double computeFarClipDistance() {
      // Use the current eye point to auto-configure the far clipping plane distance.
      final Vec4 eyePoint = getCurrentEyePoint();
      return computeFarClipDistance(eyePoint);
   }


   @Override
   public double computeHorizonDistance() {
      // Use the eye point from the last call to apply() to compute horizon distance.
      final Vec4 eyePoint = getEyePoint();
      return this.computeHorizonDistance(eyePoint);
   }


   @Override
   protected void doApply(@SuppressWarnings("hiding") final DrawContext dc) {
      // Invoke superclass functionality.
      super.doApply(dc);
   }


   protected double computeFarClipDistance(final Vec4 eyePoint) {
      final double far = this.computeHorizonDistance(eyePoint);
      return far < MINIMUM_FAR_DISTANCE ? MINIMUM_FAR_DISTANCE : far;
   }


   protected double computeHorizonDistance(final Vec4 eyePoint) {
      double horizon = 0;
      // Compute largest distance to flat globe 'corners'.
      if ((globe != null) && (eyePoint != null)) {
         double dist = 0;
         Vec4 p;
         // Use max distance to six points around the map
         p = globe.computePointFromPosition(Angle.POS90, Angle.NEG180, 0); // NW
         dist = Math.max(dist, eyePoint.distanceTo3(p));
         p = globe.computePointFromPosition(Angle.POS90, Angle.POS180, 0); // NE
         dist = Math.max(dist, eyePoint.distanceTo3(p));
         p = globe.computePointFromPosition(Angle.NEG90, Angle.NEG180, 0); // SW
         dist = Math.max(dist, eyePoint.distanceTo3(p));
         p = globe.computePointFromPosition(Angle.NEG90, Angle.POS180, 0); // SE
         dist = Math.max(dist, eyePoint.distanceTo3(p));
         p = globe.computePointFromPosition(Angle.ZERO, Angle.POS180, 0); // E
         dist = Math.max(dist, eyePoint.distanceTo3(p));
         p = globe.computePointFromPosition(Angle.ZERO, Angle.NEG180, 0); // W
         dist = Math.max(dist, eyePoint.distanceTo3(p));
         horizon = dist;
      }
      return horizon;
   }

}
