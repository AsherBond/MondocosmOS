

package es.igosoftware.couching.json;

public class JSLong
         extends
            JSPrimitive<Long> {


   public JSLong(final Long value) {
      super(value);
   }


   @Override
   public String toJSONString() {
      return getValue().toString();
   }


}
