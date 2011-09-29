

package es.igosoftware.euclid.experimental.measurement;


public enum GTime implements IUnit<GTime> {
   Millisecond("ms", 0.001, GMeasurementSystem.Metric),
   Second("s", 1, GMeasurementSystem.Metric),
   Minute("min", 60, GMeasurementSystem.Metric),
   Hour("h", 3600, GMeasurementSystem.Metric);


   private final GMeasurementSystem _system;
   private final String             _name;
   private final double             _convertionFactor;


   private GTime(final String name,
                 final double convertionFactor,
                 final GMeasurementSystem system) {
      _name = name;
      _convertionFactor = convertionFactor;
      _system = system;
   }


   @Override
   public String getName() {
      return _name;
   }


   @Override
   public double convertionFactor() {
      return _convertionFactor;
   }


   @Override
   public IMeasure<GTime> value(final double value) {
      return new GMeasure<GTime>(value, this).simplified();
   }


   @Override
   public String toString() {
      return getName();
   }


   @Override
   public GMeasurementSystem getSystem() {
      return _system;
   }


}
