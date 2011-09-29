

package es.igosoftware.couching.json;


public abstract class JSPrimitive<T>
         extends
            JSValue {


   private final T _value;


   protected JSPrimitive(final T value) {
      _value = value;
   }


   public T getValue() {
      return _value;
   }


   @Override
   protected String toPrettyJSONString(final String prefix,
                                       final int level) {
      return identation(level) + prefix + toJSONString();
   }


}
