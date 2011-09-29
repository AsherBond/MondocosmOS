

package es.igosoftware.couching;

import es.igosoftware.couching.json.JSValue;


public class CDBResponse<T extends JSValue> {

   private final int    _responseCode;
   private final String _responseMessage;
   private final T      _contents;


   CDBResponse(final int responseCode,
               final String responseMessage,
               final T contents) {
      _responseCode = responseCode;
      _responseMessage = responseMessage;
      _contents = contents;
   }


   public int getResponseCode() {
      return _responseCode;
   }


   public String getResponseMessage() {
      return _responseMessage;
   }


   public T getContents() {
      return _contents;
   }


   @Override
   public String toString() {
      return "CDBResponse [responseCode=" + _responseCode + ", responseMessage=" + _responseMessage + ", contents=" + _contents
             + "]";
   }


}
