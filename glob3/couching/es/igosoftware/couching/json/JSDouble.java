

package es.igosoftware.couching.json;


public class JSDouble
         extends
            JSPrimitive<Double> {


   public JSDouble(final Double value) {
      super(value);
   }


   @Override
   public String toJSONString() {
      return getValue().toString();
   }


}
