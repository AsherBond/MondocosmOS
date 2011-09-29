

package es.igosoftware.tools;


public class TBuildException
         extends
            Exception {


   private static final long serialVersionUID = 1L;


   public TBuildException(final TProject project,
                          final String message,
                          final Throwable cause) {
      super(((project == null) ? "" : project.getName() + ": ") + message, cause);
   }


   public TBuildException(final TProject project,
                          final String message) {
      super(((project == null) ? "" : project.getName() + ": ") + message);
   }

}
