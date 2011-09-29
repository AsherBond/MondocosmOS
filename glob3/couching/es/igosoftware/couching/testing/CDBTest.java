

package es.igosoftware.couching.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.junit.Test;

import es.igosoftware.couching.CDBDatabase;
import es.igosoftware.couching.CDBDocument;
import es.igosoftware.couching.CDBDocumentID;
import es.igosoftware.couching.CDBException;
import es.igosoftware.couching.CDBServer;
import es.igosoftware.couching.CDBViewResult;
import es.igosoftware.couching.json.JSObject;
import es.igosoftware.couching.json.JSObjectField;
import es.igosoftware.couching.json.JSString;
import es.igosoftware.couching.json.JSValue;


public class CDBTest {
   private static final String DB_URL_STRING = "http://localhost:5984"; // select here the CouchDB instance to test
   private static final URL    DB_URL;

   static {
      try {
         DB_URL = new URL(DB_URL_STRING);
      }
      catch (final MalformedURLException e) {
         throw new RuntimeException(e);
      }
   }

   private static final String TEST_DB_NAME  = "test-database";


   @Test
   public void testServerVersion() throws CDBException {
      final CDBServer server = new CDBServer(DB_URL);

      final String version = server.getVersion();
      System.out.println(server + ", version: " + version);
   }


   @Test
   public void testServerStatistics() throws CDBException {
      final CDBServer server = new CDBServer(DB_URL);

      final JSObject<JSValue> statistics = server.getStatistics();
      System.out.println("Server Statistics: " + statistics.toPrettyJSONString());
   }


   @Test
   public void testServerAllDatabases() throws CDBException {
      final CDBServer server = new CDBServer(DB_URL);

      final List<CDBDatabase> databases = server.getAllDatabases();
      System.out.println("Server Statistics: " + databases);
   }


   @Test
   public void testCreateAndDeleteDatabase() throws CDBException {
      final CDBServer server = new CDBServer(DB_URL);

      final CDBDatabase db = server.getDatabase(TEST_DB_NAME);
      if (!db.exists()) {
         db.create();
         assertTrue(db.exists());
      }

      final JSObject<JSValue> statistics = db.getStatistics();
      System.out.println(TEST_DB_NAME + " Statistics: " + statistics.toPrettyJSONString());

      db.delete();
      assertFalse(db.exists());
   }


   @Test
   public void testCreateDocument() throws CDBException {
      final CDBServer server = new CDBServer(DB_URL);

      final CDBDatabase db = server.getDatabase(TEST_DB_NAME);
      if (!db.exists()) {
         db.create();
         assertTrue(db.exists());
      }

      @SuppressWarnings("unchecked")
      final JSObject<JSValue> contents = new JSObject<JSValue>(new JSObjectField<JSValue>("key1", new JSString("value1")));

      final CDBDocumentID documentID = db.createDocument(contents);

      final CDBDocument<JSObject<JSValue>> document = db.getDocument(documentID);

      assertEquals(document.getId(), documentID);

      assertTrue(document.getContents().hasField("key1"));
      assertEquals(document.getContents().getString("key1"), "value1");


      final CDBViewResult allDocuments = db.getAllDocuments();

      assertEquals(allDocuments.getTotalRows(), 1);
      assertEquals(allDocuments.getOffset(), 0);
      assertEquals(allDocuments.getDocumentIDs().size(), 1);
      assertEquals(allDocuments.getDocumentIDs().get(0), documentID);

      db.delete();
      assertFalse(db.exists());
   }


}
