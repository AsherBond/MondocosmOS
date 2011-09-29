

package es.igosoftware.couching;

import java.io.IOException;


public class CDBException
         extends
            IOException {

   private static final long serialVersionUID = 1L;


   public CDBException(final String message,
                       final Throwable cause) {
      super(message, cause);
   }


   public CDBException(final String message) {
      super(message);
   }


   public CDBException(final Throwable cause) {
      super(cause);
   }


}
