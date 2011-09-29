

package es.igosoftware.couching.json;


public class JSBoolean
         extends
            JSPrimitive<Boolean> {

   public static final JSBoolean TRUE  = new JSBoolean(true);
   public static final JSBoolean FALSE = new JSBoolean(false);


   private JSBoolean(final Boolean value) {
      super(value);
   }


   @Override
   public String toJSONString() {
      return getValue() ? "true" : "false";
   }


}
