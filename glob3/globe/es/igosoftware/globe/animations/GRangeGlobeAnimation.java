

package es.igosoftware.globe.animations;

import es.igosoftware.util.GMath;


public abstract class GRangeGlobeAnimation<T>
         extends
            GAbstractGlobeAnimation<T> {


   private final double _from;
   private final double _to;


   protected GRangeGlobeAnimation(final T target,
                                  final long time,
                                  final double from,
                                  final double to) {
      super(target, time);

      _from = from;
      _to = to;
   }


   @Override
   public void doStep(final long now) {
      setValue(getCurrentValue(now));
   }


   protected abstract void setValue(final double value);


   private double getCurrentValue(final long now) {
      final double prog = currentProgress(now);

      return _from + ((_to - _from) * prog);
   }


   private double currentProgress(final long now) {
      final double result = _gentlyProgress //
                                           ? gentlyProgress(now) //
                                           : linealProgress(now);
      return GMath.clamp(result, 0, 1);
   }


   private double linealProgress(final long now) {
      return ((now - _startedTime) / time());
   }


   private double gentlyProgress(final long now) {
      final double prog = progress(now);
      if (prog >= 1) {
         return 1;
      }

      return gentlyProgress(prog, 0.6, 0.85);
   }


   private double gentlyProgress(final double value,
                                 final double lower,
                                 final double upper) {

      // This method converts a linear proportion done to a slow in - slow out proportion. 

      final double upperSquared = upper * upper;
      final double lowerTimesUpper = lower * upper;
      final double valueSquared = value * value;

      final double upperLessOne = upper - 1;
      final double lowerLessOne = lower - 1;

      final double result;
      if (value < lower) {
         result = (upperLessOne / (lower * (upperSquared - lowerTimesUpper + lowerLessOne))) * valueSquared;
      }
      else {
         if (value > upper) {
            final double a3 = 1 / (upperSquared - lowerTimesUpper + lowerLessOne);

            final double b3 = -2 * a3;

            final double c3 = 1 + a3;

            result = (a3 * valueSquared) + (b3 * value) + c3;
         }
         else {
            final double m = 2 * upperLessOne / (upperSquared - lowerTimesUpper + lowerLessOne);

            final double b2 = (0 - m) * lower / 2;

            result = m * value + b2;
         }
      }


      return result;
   }


   private double progress(final long now) {
      // Answer the progress (0 to 1) of the receiver, calculated from the startedTime

      return ((double) now - _startedTime) / time();
   }


}
