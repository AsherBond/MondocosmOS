/**
 * 
 */


package es.igosoftware.dmvc.transferring;

import java.io.Serializable;

import es.igosoftware.io.GFileName;


public class GDFileRequest
         implements
            Serializable {
   private static final long serialVersionUID = 1L;

   private final GFileName   _fileName;


   public GDFileRequest(final GFileName fileName) {
      _fileName = fileName;
   }


   public GFileName getFileName() {
      return _fileName;
   }


   @Override
   public String toString() {
      return "GDFileRequest [fileName=" + _fileName + "]";
   }


}
