

package es.igosoftware.globe.animations;

public abstract class GOpacityRangeGlobeAnimation<T>
         extends
            GRangeGlobeAnimation<T> {


   protected GOpacityRangeGlobeAnimation(final T target,
                                         final long time) {
      super(target, time, 0, 1);
   }


}
