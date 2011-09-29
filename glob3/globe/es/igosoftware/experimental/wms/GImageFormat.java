

package es.igosoftware.experimental.wms;

public enum GImageFormat {

   JPEG(".jpg", "image/jpeg"),
   PNG(".png", "image/png");

   private final String _extension;
   private final String _format;


   private GImageFormat(final String extension,
                        final String format) {
      _extension = extension;
      _format = format;
   }


   public String getExtension() {
      return _extension;
   }


   public String getFormat() {
      return _format;
   }
}
