

package es.igosoftware.euclid.scripting;


public class GIllegalScriptException
         extends
            Exception {


   private static final long serialVersionUID = 1L;


   public GIllegalScriptException(final String msg) {
      super(msg);
   }


   public GIllegalScriptException(final String msg,
                                  final Exception cause) {
      super(msg, cause);
   }

}
