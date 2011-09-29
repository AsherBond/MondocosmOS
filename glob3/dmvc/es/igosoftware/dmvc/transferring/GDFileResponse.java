/**
 * 
 */


package es.igosoftware.dmvc.transferring;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;


public class GDFileResponse
         implements
            Serializable {

   private static final long serialVersionUID      = 1L;

   private static final long DEFAULT_LAST_MODIFIED = Long.MIN_VALUE;


   public static enum Status {
      OK,
      FILE_NOT_FOUND,
      IO_EXCEPTION,
      FILE_TOO_BIG,
      CANT_READ_FILE;
   }


   private final GDFileResponse.Status _status;
   private final String                _errorMessage;
   private final byte[]                _bytes;
   private final long                  _lastModified;


   public GDFileResponse(final IOException e) {
      this(GDFileResponse.Status.IO_EXCEPTION, e.getMessage(), null, DEFAULT_LAST_MODIFIED);
   }


   public GDFileResponse(final GDFileResponse.Status status) {
      this(status, null, null, DEFAULT_LAST_MODIFIED);
   }


   public GDFileResponse(final GDFileResponse.Status status,
                         final byte[] bytes,
                         final long lastModified) {
      this(status, null, bytes, lastModified);
   }


   private GDFileResponse(final GDFileResponse.Status status,
                          final String errorMessage,
                          final byte[] bytes,
                          final long lastModified) {
      _status = status;
      _errorMessage = errorMessage;
      _bytes = bytes;
      _lastModified = lastModified;
   }


   public GDFileResponse.Status getStatus() {
      return _status;
   }


   public String getErrorMessage() {
      return _errorMessage;
   }


   public byte[] getBytes() {
      return _bytes;
   }


   @Override
   public String toString() {
      final int bytesLength = (_bytes == null) ? 0 : _bytes.length;
      return "GDFileResponse [status=" + _status + ", errorMessage=" + _errorMessage + ", bytes=" + bytesLength
             + ", lastModified=" + DateFormat.getInstance().format(new Date(_lastModified)) + "]";
   }


   public long getLastModified() {
      return _lastModified;
   }


}
