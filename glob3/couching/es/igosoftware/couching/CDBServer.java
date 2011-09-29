

package es.igosoftware.couching;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import es.igosoftware.couching.json.JSArray;
import es.igosoftware.couching.json.JSObject;
import es.igosoftware.couching.json.JSString;
import es.igosoftware.couching.json.JSValue;
import es.igosoftware.util.GUtils;


public class CDBServer
         extends
            CDBBaseImpl {

   private final LinkedList<String> _uuidsCache = new LinkedList<String>();

   private final URL                _url;
   private final int                _uuidsBatchRequestSize;


   public CDBServer(final String url) throws MalformedURLException {
      this(new URL(GUtils.required(url, "url is mandatory")));
   }


   public CDBServer(final String url,
                    final int uuidsBatchRequestSize) throws MalformedURLException {
      this(new URL(GUtils.required(url, "url is mandatory")), uuidsBatchRequestSize);
   }


   public CDBServer(final URL url) {
      this(url, DEFAULT_UUIDs_BATCH_REQUEST_SIZE);
   }


   public CDBServer(final URL url,
                    final int uuidsBatchRequestSize) {
      _url = GUtils.required(url, "url is mandatory");
      if ((_url.getProtocol() == null) || !_url.getProtocol().equals("http")) {
         throw new RuntimeException("Invalid url: " + _url);
      }

      if (uuidsBatchRequestSize <= 0) {
         throw new IllegalArgumentException("uuidsBatchRequestSize must be greater than 0");
      }

      _uuidsBatchRequestSize = uuidsBatchRequestSize;
   }


   public String getVersion() throws CDBException {
      final CDBResponse<JSObject<JSValue>> response = getJSObject(openConnection(_url, "GET"));
      return response.getContents().getString(VERSION);
   }


   public List<CDBDatabase> getAllDatabases() throws CDBException {
      final CDBResponse<JSArray<JSString>> arrayResponse = getJSArray(openConnection(getAllDatabasesURL(), "GET"));
      final List<JSString> listResponse = arrayResponse.getContents().getValues();

      final List<CDBDatabase> result = new ArrayList<CDBDatabase>(listResponse.size());
      for (final JSString element : listResponse) {
         result.add(new CDBDatabase(CDBServer.this, element.getValue()));
      }

      return Collections.unmodifiableList(result);
   }


   public JSObject<JSValue> getStatistics() throws CDBException {
      final CDBResponse<JSObject<JSValue>> response = getJSObject(openConnection(getStatisticsURL(), "GET"));
      return response.getContents();
   }


   public URL getURL() {
      return _url;
   }


   public CDBDatabase getDatabase(final String databaseName) throws CDBException {
      return new CDBDatabase(this, databaseName);
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      return prime + _url.hashCode();
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
      final CDBServer other = (CDBServer) obj;
      if (!_url.equals(other._url)) {
         return false;
      }
      return true;
   }


   @Override
   public String toString() {
      return "CDBServer [url=" + _url + "]";
   }


   public String calculateNextUUID() throws CDBException {
      synchronized (UUID_MUTEX) {
         if (_uuidsCache.isEmpty()) {
            fillUUIDsCache();
         }

         return _uuidsCache.removeFirst();
      }
   }


   private void fillUUIDsCache() throws CDBException {
      final URL requestUrl = getUUIDsURL();

      final CDBResponse<JSObject<JSArray<JSString>>> newUUIDs = getJSObject(openConnection(requestUrl, "GET"));

      final JSArray<JSString> array = newUUIDs.getContents().getArray(UUIDs);
      for (final JSString uuid : array) {
         _uuidsCache.add(uuid.getValue());
      }
   }


   private URL getAllDatabasesURL() {
      try {
         return new URL(_url, _ALL_DBS);
      }
      catch (final MalformedURLException e) {
         throw new RuntimeException(e);
      }
   }


   private URL getStatisticsURL() {
      try {
         return new URL(_url, _STATS);
      }
      catch (final MalformedURLException e) {
         throw new RuntimeException(e);
      }
   }


   private URL getUUIDsURL() {
      try {
         return (_uuidsBatchRequestSize == 1) ? //
                                             new URL(_url, _UUIDs) : //
                                             new URL(_url, _UUIDs + "?count=" + _uuidsBatchRequestSize);
      }
      catch (final MalformedURLException e) {
         throw new RuntimeException(e);
      }
   }


}
