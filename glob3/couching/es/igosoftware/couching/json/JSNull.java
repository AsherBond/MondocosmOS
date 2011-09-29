

package es.igosoftware.couching.json;


public class JSNull
         extends
            JSPrimitive<Void> {

   public static final JSNull INSTANCE = new JSNull();


   private JSNull() {
      super(null);
   }


   @Override
   public String toJSONString() {
      return "null";
   }


}
