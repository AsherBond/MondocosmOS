

package es.igosoftware.couching.json;

public class JSObjectField<T extends JSValue> {
   private final String _name;
   private final T      _value;


   public JSObjectField(final String name,
                        final T value) {
      _name = name;
      _value = value;
   }


   @Override
   public JSObjectField<T> clone() {
      return new JSObjectField<T>(_name, _value);
   }


   @Override
   public String toString() {
      return "JSObject.Field [name=" + _name + ", value=" + _value + "]";
   }


   public String getName() {
      return _name;
   }


   public T getValue() {
      return _value;
   }


}
