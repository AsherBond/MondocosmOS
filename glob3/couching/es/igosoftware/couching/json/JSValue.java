

package es.igosoftware.couching.json;

import es.igosoftware.util.GStringUtils;


public abstract class JSValue {


   @Override
   public String toString() {
      return toJSONString();
   }


   public abstract String toJSONString();


   public String toPrettyJSONString() {
      return toPrettyJSONString("", 0);
   }


   protected abstract String toPrettyJSONString(final String prefix,
                                                final int level);


   protected String identation(final int level) {
      return GStringUtils.spaces(level * 3);
   }


}
