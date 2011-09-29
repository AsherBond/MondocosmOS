

package es.igosoftware.globe.weather.aemet.data;

import es.igosoftware.euclid.GAngle;
import es.igosoftware.util.GAssert;


public class AEMETVariable<T> {


   private final String   _name;
   private final String   _acronym;
   private final String   _unit;
   private final Class<T> _type;


   AEMETVariable(final String name,
                 final String acronym,
                 final String unit,
                 final Class<T> type) {
      GAssert.notNull(name, "name");
      GAssert.notNull(acronym, "acronym");
      GAssert.notNull(unit, "unit");
      GAssert.notNull(type, "type");

      _name = name.trim();
      _acronym = acronym.trim();
      _unit = unit.trim();
      _type = type;
   }


   public String getName() {
      return _name;
   }


   public String getAcronym() {
      return _acronym;
   }


   public String getUnit() {
      return _unit;
   }


   public Class<T> getType() {
      return _type;
   }


   @Override
   public String toString() {
      //return "AEMETVariable [name=" + _name + ", acronym=" + _acronym + ", unit=" + _unit + ", type=" + _type + "]";
      return _name;
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_acronym == null) ? 0 : _acronym.hashCode());
      result = prime * result + ((_name == null) ? 0 : _name.hashCode());
      result = prime * result + ((_type == null) ? 0 : _type.hashCode());
      result = prime * result + ((_unit == null) ? 0 : _unit.hashCode());
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
      final AEMETVariable other = (AEMETVariable) obj;
      if (_acronym == null) {
         if (other._acronym != null) {
            return false;
         }
      }
      else if (!_acronym.equals(other._acronym)) {
         return false;
      }
      if (_name == null) {
         if (other._name != null) {
            return false;
         }
      }
      else if (!_name.equals(other._name)) {
         return false;
      }
      if (_type == null) {
         if (other._type != null) {
            return false;
         }
      }
      else if (!_type.equals(other._type)) {
         return false;
      }
      if (_unit == null) {
         if (other._unit != null) {
            return false;
         }
      }
      else if (!_unit.equals(other._unit)) {
         return false;
      }
      return true;
   }


   @SuppressWarnings("unchecked")
   T parseValue(final String value,
                final String quality) {
      if (value == null) {
         return null;
      }

      final double q = Double.parseDouble(quality);
      if (q >= 3) {
         return null;
      }

      if (_type == Double.class) {
         return (T) Double.valueOf(Double.parseDouble(value));
      }
      else if (_type == GAngle.class) {
         return (T) GAngle.fromDegrees(Double.parseDouble(value));
      }
      else if (_type == String.class) {
         return (T) value;
      }
      else {
         throw new RuntimeException("Unssuported type: " + _type);
      }
   }


}
