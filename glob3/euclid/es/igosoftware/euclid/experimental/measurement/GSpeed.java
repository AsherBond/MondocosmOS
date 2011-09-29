

package es.igosoftware.euclid.experimental.measurement;


public enum GSpeed implements IUnit<GSpeed> {
   KilometerHour("km/h", 0.277777778, GMeasurementSystem.Metric),
   MeterSecond("m/s", 1, GMeasurementSystem.Metric),
   MilesHour("mph", 0.44704, GMeasurementSystem.Imperial),
   Knot("knot", 0.514444442, GMeasurementSystem.Metric),
   Match("mach", 340.286521250, GMeasurementSystem.Metric);


   private final GMeasurementSystem _system;
   private final String             _name;
   private final double             _convertionFactor;


   private GSpeed(final String name,
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
   public IMeasure<GSpeed> value(final double value) {
      return new GMeasure<GSpeed>(value, this).simplified();
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
