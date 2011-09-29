

package es.igosoftware.globe.animations;


public interface IGlobeAnimation<T> {

   public T getTarget();


   public boolean isRunning();


   public void start();


   public void doStep(final long now);


   public boolean isDone(long now);


   public void stop();


}
