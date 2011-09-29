

package es.igosoftware.globe.animations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import es.igosoftware.util.GAssert;
import es.igosoftware.util.GUtils;


public class GGlobeAnimationsScheduler
         implements
            IGlobeAnimationsScheduler {


   //   private final GGlobeApplication     _application;
   private final GGlobeAnimatorLayer   _animatorLayer;
   private final List<IGlobeAnimation> _animations = new LinkedList<IGlobeAnimation>();


   public GGlobeAnimationsScheduler(final GGlobeAnimatorLayer animatorLayer) {
      //      GAssert.notNull(application, "application");
      GAssert.notNull(animatorLayer, "animatorLayer");

      //      _verbose = verbose;

      //      _application = application;
      _animatorLayer = animatorLayer;
      animatorLayer.addScheduler(this);
   }


   @Override
   public <T> void startAnimation(final IGlobeAnimation<T> animation) {
      GAssert.notNull(animation, "animation");

      GAssert.isFalse(animation.isRunning(), "The animation " + animation + " is already started");

      final List<IGlobeAnimation> animationsToStop = new ArrayList<IGlobeAnimation>(_animations.size());
      final T target = animation.getTarget();

      synchronized (_animations) {
         for (final IGlobeAnimation current : _animations) {
            if (GUtils.equals(current.getTarget(), target)) {
               animationsToStop.add(current);
            }
         }

         stopAnimations(animationsToStop);

         _animations.add(animation);
         animation.start();
      }
   }


   private void stopAnimations(final List<IGlobeAnimation> animations) {
      for (final IGlobeAnimation animation : animations) {
         stopAnimation(animation);
      }
   }


   private void stopAnimation(final IGlobeAnimation animation) {
      synchronized (_animations) {
         _animations.remove(animation);
         animation.stop();
      }
   }


   @Override
   public boolean doStep(final long now) {
      synchronized (_animations) {
         final Iterator<IGlobeAnimation> iterator = _animations.iterator();

         final List<IGlobeAnimation> animationsToStop = new ArrayList<IGlobeAnimation>(_animations.size());

         while (iterator.hasNext()) {
            final IGlobeAnimation animation = iterator.next();

            animation.doStep(now);
            if (animation.isDone(now)) {
               animationsToStop.add(animation);
            }
         }

         stopAnimations(animationsToStop);

         return !_animations.isEmpty();
      }
   }


   @Override
   public GGlobeAnimatorLayer getLayer() {
      return _animatorLayer;
   }


}
