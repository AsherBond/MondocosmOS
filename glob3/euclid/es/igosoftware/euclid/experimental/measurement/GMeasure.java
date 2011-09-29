

package es.igosoftware.euclid.experimental.measurement;

import es.igosoftware.util.GAssert;


public class GMeasure<UnitT extends IUnit<UnitT>>
         implements
            IMeasure<UnitT> {


   private final double _value;
   private final UnitT  _unit;


   GMeasure(final double value,
            final UnitT unit) {
      GAssert.notNull(unit, "unit");

      _value = value;
      _unit = unit;
   }


   @Override
   public double getValue() {
      return _value;
   }


   @Override
   public UnitT getUnit() {
      return _unit;
   }


   private static <UnitT extends IUnit<UnitT>> IMeasure<UnitT> createMeasure(final double value,
                                                                             final UnitT unit) {
      return new GMeasure<UnitT>(value, unit).simplified();
   }


   @SuppressWarnings("unchecked")
   IMeasure<UnitT> simplified() {
      final double convertedValue = _value * _unit.convertionFactor();

      IUnit closestUnit = null;
      long maxInteger = Long.MAX_VALUE;
      for (final IUnit unit : _unit.getClass().getEnumConstants()) {
         if (unit.getSystem() != _unit.getSystem()) {
            continue;
         }

         final long integerPart = Math.abs((long) (convertedValue / unit.convertionFactor()));

         if (integerPart == 0) {
            continue;
         }

         if (integerPart < maxInteger) {
            maxInteger = integerPart;
            closestUnit = unit;
         }
      }

      if ((closestUnit == null) || (closestUnit == _unit)) {
         return this.tryToReduceDecimals();
      }

      return new GMeasure<UnitT>(convertedValue / closestUnit.convertionFactor(), (UnitT) closestUnit);
   }


   @SuppressWarnings("unchecked")
   private IMeasure<UnitT> tryToReduceDecimals() {
      final double convertedValue = _value * _unit.convertionFactor();

      IUnit closestUnit = null;
      double decimalsClosestToZero = Double.POSITIVE_INFINITY;
      for (final IUnit unit : _unit.getClass().getEnumConstants()) {
         if (unit.getSystem() != _unit.getSystem()) {
            continue;
         }


         final long integerPart = (long) (convertedValue / unit.convertionFactor());
         if (integerPart != 0) {
            continue;
         }

         final double currentDecimals = Math.abs((convertedValue / unit.convertionFactor()) - integerPart);

         if (currentDecimals > decimalsClosestToZero) {
            decimalsClosestToZero = currentDecimals;
            closestUnit = unit;
         }
      }

      if ((closestUnit == null) || (closestUnit == _unit)) {
         return this;
      }

      return new GMeasure<UnitT>(convertedValue / closestUnit.convertionFactor(), (UnitT) closestUnit);
   }


   @Override
   public IMeasure<UnitT> add(final IMeasure<UnitT> that) {
      final UnitT thatUnit = that.getUnit();
      final double thatValue = that.getValue();

      if (thatUnit == _unit) {
         return createMeasure(_value + thatValue, _unit);
      }

      final double conversionFactor = thatUnit.convertionFactor() / _unit.convertionFactor();
      return createMeasure(_value + (thatValue * conversionFactor), _unit);
   }


   @Override
   public IMeasure<UnitT> sub(final IMeasure<UnitT> that) {
      final UnitT thatUnit = that.getUnit();
      final double thatValue = that.getValue();

      if (thatUnit == _unit) {
         return createMeasure(_value - thatValue, _unit);
      }

      final double conversionFactor = thatUnit.convertionFactor() / _unit.convertionFactor();
      return createMeasure(_value - (thatValue * conversionFactor), _unit);
   }


   @Override
   public String toString() {
      return _value + _unit.getName();
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_unit == null) ? 0 : _unit.hashCode());
      long temp;
      temp = Double.doubleToLongBits(_value);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      return result;
   }


   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final GMeasure other = (GMeasure) obj;
      if (_unit == null) {
         if (other._unit != null) {
            return false;
         }
      }
      else if (!_unit.equals(other._unit)) {
         return false;
      }
      if (Double.doubleToLongBits(_value) != Double.doubleToLongBits(other._value)) {
         return false;
      }
      return true;
   }


   @Override
   public double getValueInReferenceUnits() {
      return _value * _unit.convertionFactor();
   }


   @Override
   public IMeasure<UnitT> max(final IMeasure<UnitT> that) {
      final UnitT thatUnit = that.getUnit();
      final double thatValue = that.getValue();

      if (thatUnit == _unit) {
         return (_value > thatValue) ? this : that;
      }

      final double conversionFactor = thatUnit.convertionFactor() / _unit.convertionFactor();
      return (_value > (thatValue * conversionFactor)) ? this : that;
   }


   @Override
   public IMeasure<UnitT> min(final IMeasure<UnitT> that) {
      final UnitT thatUnit = that.getUnit();
      final double thatValue = that.getValue();

      if (thatUnit == _unit) {
         return (_value < thatValue) ? this : that;
      }

      final double conversionFactor = thatUnit.convertionFactor() / _unit.convertionFactor();
      return (_value < (thatValue * conversionFactor)) ? this : that;
   }


}
