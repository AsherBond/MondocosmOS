

package es.igosoftware.euclid.experimental.measurement;


public enum GArea implements IUnit<GArea> {
   SquareCentimeter("cm²", 0.0001, GMeasurementSystem.Metric),
   SquareMeter("m²", 1, GMeasurementSystem.Metric),
   Hectare("ha", 10000, GMeasurementSystem.Metric),
   SquareKilometer("km²", 1000000, GMeasurementSystem.Metric);


   private final GMeasurementSystem _system;
   private final String             _name;
   private final double             _convertionFactor;


   private GArea(final String name,
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
   public IMeasure<GArea> value(final double value) {
      return new GMeasure<GArea>(value, this).simplified();
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
