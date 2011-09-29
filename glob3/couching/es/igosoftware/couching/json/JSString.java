

package es.igosoftware.couching.json;


public class JSString
         extends
            JSPrimitive<String> {


   public JSString(final String value) {
      super(value);
   }


   @Override
   public String toJSONString() {
      return "\"" + getValue() + "\"";
   }


}
