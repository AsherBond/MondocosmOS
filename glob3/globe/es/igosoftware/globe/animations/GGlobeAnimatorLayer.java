

package es.igosoftware.globe.animations;

import es.igosoftware.util.GAssert;
import es.igosoftware.utils.GWWUtils;
import gov.nasa.worldwind.layers.AbstractLayer;
import gov.nasa.worldwind.render.DrawContext;

import java.util.ArrayList;
import java.util.List;


public class GGlobeAnimatorLayer
         extends
            AbstractLayer {


   private final List<IGlobeAnimationsScheduler> _schedulers = new ArrayList<IGlobeAnimationsScheduler>();


   @Override
   public String getName() {
      return "GlobeAnimatorLayer";
   }


   public synchronized void addScheduler(final IGlobeAnimationsScheduler scheduler) {
      GAssert.notNull(scheduler, "scheduler");
      _schedulers.add(scheduler);
   }


   @Override
   protected synchronized void doRender(final DrawContext dc) {
      final long now = dc.getFrameTimeStamp();

      boolean needsRedraw = false;
      for (final IGlobeAnimationsScheduler schedule : _schedulers) {
         if (schedule.doStep(now)) {
            needsRedraw = true;
         }
      }
      if (needsRedraw) {
         GWWUtils.redraw(dc);
      }


      GWWUtils.checkGLErrors(dc);
   }


}
