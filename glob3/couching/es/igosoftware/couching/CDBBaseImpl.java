

package es.igosoftware.couching;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import es.igosoftware.couching.json.JSArray;
import es.igosoftware.couching.json.JSObject;
import es.igosoftware.couching.json.JSParser;
import es.igosoftware.couching.json.JSValue;
import es.igosoftware.io.GIOUtils;


public abstract class CDBBaseImpl {

   protected static final int    DEFAULT_UUIDs_BATCH_REQUEST_SIZE = 10;


   protected static final String _ALL_DBS                         = "_all_dbs";
   protected static final String _STATS                           = "_stats";
   protected static final String VERSION                          = "version";
   protected static final String REASON                           = "reason";
   protected static final String DB_NAME                          = "db_name";
   protected static final String _UUIDs                           = "_uuids";
   protected static final String UUIDs                            = "uuids";

   protected static final String OK_FIELD_NAME                    = "ok";
   protected static final String ERROR_FIELD_NAME                 = "error";
   protected static final String _ID_FIELD_NAME                   = "_id";
   protected static final String _REV_FIELD_NAME                  = "_rev";
   protected static final String ID_FIELD_NAME                    = "id";
   protected static final String REV_FIELD_NAME                   = "rev";
   protected static final String _ALL_DOCS                        = "_all_docs";

   protected static final String TOTAL_ROWS_FIELD_NAME            = "total_rows";
   protected static final String OFFSET_FIELD_NAME                = "offset";
   protected static final String ROWS_FIELD_NAME                  = "rows";
   protected static final String KEY_FIELD_NAME                   = "key";
   protected static final String VALUE_FIELD_NAME                 = "value";


   protected static final Object UUID_MUTEX                       = new Object();

   private static final JSParser JSPARSER                         = new JSParser();


   private static BufferedReader getReader(final HttpURLConnection connection) throws CDBException {
      try {
         connection.connect();

         if (isStatusCodeOK(connection)) {
            return new BufferedReader(new InputStreamReader(connection.getInputStream()));
         }

         return new BufferedReader(new InputStreamReader(connection.getErrorStream()));
      }
      catch (final IOException e) {
         throw new CDBException(e);
      }
   }


   protected static HttpURLConnection openConnection(final URL url,
                                                     final String requestMethod) throws CDBException {
      try {
         final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

         connection.setUseCaches(false);
         connection.setRequestMethod(requestMethod);

         return connection;
      }
      catch (final IOException e) {
         throw new CDBException(e);
      }
   }


   private static boolean isStatusCodeOK(final HttpURLConnection connection) throws CDBException {
      try {
         final int responseCode = connection.getResponseCode();
         return (responseCode == HttpURLConnection.HTTP_OK) || (responseCode == HttpURLConnection.HTTP_CREATED);
      }
      catch (final IOException e) {
         throw new CDBException(e);
      }
   }


   protected static <T extends JSValue> CDBResponse<JSObject<T>> getJSObject(final HttpURLConnection connection)
                                                                                                                throws CDBException {
      return getJSValue(connection);
   }


   protected static <T extends JSValue> CDBResponse<JSArray<T>> getJSArray(final HttpURLConnection connection)
                                                                                                              throws CDBException {
      return getJSValue(connection);
   }


   private static <T extends JSValue> CDBResponse<T> getJSValue(final HttpURLConnection connection) throws CDBException {
      BufferedReader in = null;
      try {
         in = getReader(connection);

         final int responseCode = connection.getResponseCode();
         final String responseMessage = connection.getResponseMessage();

         @SuppressWarnings("unchecked")
         final T contents = (T) JSPARSER.parse(in);

         return new CDBResponse<T>(responseCode, responseMessage, contents);
      }
      catch (final IOException e) {
         throw new CDBException(e);
      }
      finally {
         GIOUtils.gentlyClose(in);
         GIOUtils.gentlyClose(connection);
      }
   }


   protected static <T extends JSValue> void checkError(final CDBResponse<JSObject<T>> response) throws CDBException {
      final JSObject<T> responseObject = response.getContents();
      if (responseObject.hasField(ERROR_FIELD_NAME)) {
         final String error = responseObject.getString(ERROR_FIELD_NAME);
         final String reason = responseObject.hasField(REASON) ? responseObject.getString(REASON) : "< no reason >";
         throw new CDBException(error + ": " + reason);
      }
   }


}
