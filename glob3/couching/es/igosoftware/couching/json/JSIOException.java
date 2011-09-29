

package es.igosoftware.couching.json;

import java.io.IOException;


public class JSIOException
         extends
            IOException {

   private static final long serialVersionUID = 1L;


   public JSIOException(final String message,
                        final Throwable cause) {
      super(message, cause);
   }


   public JSIOException(final String message) {
      super(message);
   }


}
