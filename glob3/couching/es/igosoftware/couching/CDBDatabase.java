

package es.igosoftware.couching;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import es.igosoftware.couching.json.JSArray;
import es.igosoftware.couching.json.JSObject;
import es.igosoftware.couching.json.JSString;
import es.igosoftware.couching.json.JSValue;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.util.GUtils;


public class CDBDatabase
         extends
            CDBBaseImpl {


   private final CDBServer _server;
   private final String    _name;
   private final URL       _url;


   CDBDatabase(final CDBServer server,
               final String name) throws CDBException {
      _server = GUtils.required(server, "server is mandatory");
      _name = GUtils.required(name, "name is mandatory");

      try {
         _url = new URL(_server.getURL(), _name);
      }
      catch (final MalformedURLException e) {
         throw new CDBException(e);
      }
   }


   public CDBServer getServer() {
      return _server;
   }


   public String getName() {
      return _name;
   }


   public boolean exists() throws CDBException {
      final HttpURLConnection connection = openConnection(_url, "GET");

      final JSObject<JSValue> response = getJSObject(connection).getContents();

      if (response.hasField(ERROR_FIELD_NAME)) {
         return false;
      }

      if (response.hasField(DB_NAME) && response.getString(DB_NAME).equals(_name)) {
         return true;
      }

      return false;
   }


   @Override
   public String toString() {
      return "CDBDatabase [" + _server.getURL() + "/" + _name + "/]";
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + _name.hashCode();
      result = prime * result + _server.hashCode();
      return result;
   }


   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final CDBDatabase other = (CDBDatabase) obj;
      if (!_name.equals(other._name)) {
         return false;
      }
      if (!_server.equals(other._server)) {
         return false;
      }
      return true;
   }


   public void create() throws CDBException {
      final HttpURLConnection connection = openConnection(_url, "PUT");

      final CDBResponse<JSObject<JSValue>> response = getJSObject(connection);
      checkError(response);
   }


   public void delete() throws CDBException {
      final HttpURLConnection connection = openConnection(_url, "DELETE");

      final CDBResponse<JSObject<JSValue>> response = getJSObject(connection);
      checkError(response);
   }


   public <T extends JSValue> JSObject<T> getStatistics() throws CDBException {
      final HttpURLConnection connection = openConnection(_url, "GET");

      final CDBResponse<JSObject<T>> response = getJSObject(connection);
      checkError(response);

      return response.getContents();
   }


   public <T extends JSValue> CDBDocumentID createDocument(final JSObject<T> contents) throws CDBException {

      if (contents.hasField(_ID_FIELD_NAME)) {
         throw new CDBException("the contents has a field \"" + _ID_FIELD_NAME + "\"");
      }
      if (contents.hasField(_REV_FIELD_NAME)) {
         throw new CDBException("the contents has a field \"" + _REV_FIELD_NAME + "\"");
      }

      final String id = _server.calculateNextUUID();

      BufferedWriter writer = null;
      try {
         final HttpURLConnection connection = openConnection(getDocumentURL(id), "PUT");
         connection.setRequestProperty("Content-type", "application/json");
         connection.setDoOutput(true);

         writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));

         writer.append(contents.toJSONString());
         writer.flush();

         final CDBResponse<JSObject<JSValue>> response = getJSObject(connection);

         checkError(response);

         final JSObject<JSValue> responseContents = response.getContents();

         if (!responseContents.hasField(OK_FIELD_NAME)) {
            throw new CDBException("Response has not the field \"" + OK_FIELD_NAME + "\"");
         }

         if (!responseContents.getBoolean(OK_FIELD_NAME)) {
            throw new CDBException("Field \"" + OK_FIELD_NAME + "\" has a false value");
         }

         final String asignedID = responseContents.getString(ID_FIELD_NAME);
         final String rev = responseContents.getString(REV_FIELD_NAME);

         return new CDBDocumentID(asignedID, rev);
      }
      catch (final CDBException e) {
         throw e;
      }
      catch (final IOException e) {
         throw new CDBException(e);
      }
      finally {
         GIOUtils.gentlyClose(writer);
      }

   }


   public <T extends JSValue> CDBDocument<JSObject<T>> getDocument(final CDBDocumentID id) throws CDBException {
      final URL url = getDocumentURL(id);

      final HttpURLConnection connection = openConnection(url, "GET");

      final CDBResponse<JSObject<T>> response = getJSObject(connection);

      checkError(response);

      final JSObject<T> responseContents = response.getContents();
      final CDBDocumentID newID = new CDBDocumentID(responseContents.getString("_id"), responseContents.getString("_rev"));
      return new CDBDocument<JSObject<T>>(this, newID, responseContents.copyWithoutFields("_id", "_rev"));
   }


   private URL getDocumentURL(final String id,
                              final String revision) {
      try {
         return (revision == null) ? //
                                  new URL(_url, _name + "/" + id) : //
                                  new URL(_url, _name + "/" + id + "?rev" + revision);
      }
      catch (final MalformedURLException e) {
         throw new RuntimeException(e);
      }
   }


   private URL getDocumentURL(final String id) {
      return getDocumentURL(id, null);
   }


   private URL getDocumentURL(final CDBDocumentID id) {
      return getDocumentURL(id.getId(), id.getRevision());
   }


   public CDBViewResult getAllDocuments() throws CDBException {
      try {
         final URL url = new URL(_url, _name + "/" + _ALL_DOCS);

         final HttpURLConnection connection = openConnection(url, "GET");

         final CDBResponse<JSObject<JSValue>> response = getJSObject(connection);

         checkError(response);

         final long totalRows = response.getContents().getLong(TOTAL_ROWS_FIELD_NAME);
         final long offset = response.getContents().getLong(OFFSET_FIELD_NAME);

         final JSArray<JSObject<JSValue>> rows = response.getContents().getArray(ROWS_FIELD_NAME);

         final List<CDBDocumentID> documentIDs = new ArrayList<CDBDocumentID>((int) totalRows);
         for (final JSObject<JSValue> row : rows) {
            final String id = row.getString(ID_FIELD_NAME);
            //            final String key = row.getString(KEY_FIELD_NAME);

            final JSObject<JSString> value = row.getObject(VALUE_FIELD_NAME);
            final String revision = value.getString(REV_FIELD_NAME);

            documentIDs.add(new CDBDocumentID(id, revision));
         }

         return new CDBViewResult(totalRows, offset, documentIDs);
      }
      catch (final MalformedURLException e) {
         throw new RuntimeException(e);
      }
   }

}
