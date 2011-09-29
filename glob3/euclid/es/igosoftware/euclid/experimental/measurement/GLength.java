

package es.igosoftware.euclid.experimental.measurement;


public enum GLength implements IUnit<GLength> {
   Millimeter("mm", 0.001, GMeasurementSystem.Metric),
   Centimeter("cm", 0.01, GMeasurementSystem.Metric),
   Meter("m", 1, GMeasurementSystem.Metric),
   Kilometer("km", 1000, GMeasurementSystem.Metric),
   Mile("mile", 1610.31, GMeasurementSystem.Imperial),
   Nautic_Mile("nm", 1851.85, GMeasurementSystem.Imperial),
   Yard("yard", 0.91440, GMeasurementSystem.Imperial),
   Feet("feet", 0.30480, GMeasurementSystem.Imperial),
   Hecto_Feet("hectofeet", 30.48, GMeasurementSystem.Imperial),
   Inch("inch", 0.02540, GMeasurementSystem.Imperial);


   private final GMeasurementSystem _system;
   private final String             _name;
   private final double             _convertionFactor;


   private GLength(final String name,
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
   public IMeasure<GLength> value(final double value) {
      return new GMeasure<GLength>(value, this).simplified();
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
