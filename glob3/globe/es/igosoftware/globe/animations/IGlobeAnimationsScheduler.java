

package es.igosoftware.globe.animations;


public interface IGlobeAnimationsScheduler {


   public <T> void startAnimation(final IGlobeAnimation<T> animation);


   public boolean doStep(final long now);


   public GGlobeAnimatorLayer getLayer();


}
