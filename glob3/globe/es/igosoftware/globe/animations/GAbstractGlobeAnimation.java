

package es.igosoftware.globe.animations;

import es.igosoftware.util.GAssert;


public abstract class GAbstractGlobeAnimation<T>
         implements
            IGlobeAnimation<T> {


   private final long _time;
   protected long     _startedTime;
   protected boolean  _isRunning;

   protected boolean  _gentlyProgress = true;
   private final T    _target;


   protected GAbstractGlobeAnimation(final T target,
                                     final long time) {
      GAssert.isPositive(time, "time");

      _target = target;
      _time = time;
   }


   @Override
   public T getTarget() {
      return _target;
   }


   @Override
   public boolean isDone(final long now) {
      return (now - _startedTime) >= time();
   }


   protected long time() {
      return _time;
   }


   @Override
   public boolean isRunning() {
      return _isRunning;
   }


   //   @Override
   //   public void doStep(final long now) {
   //   }

   @Override
   public void start() {
      _startedTime = System.currentTimeMillis();
      _isRunning = true;
   }


   @Override
   public void stop() {
      _isRunning = false;
   }


}
