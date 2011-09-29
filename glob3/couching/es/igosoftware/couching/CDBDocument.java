

package es.igosoftware.couching;

import es.igosoftware.couching.json.JSValue;


public class CDBDocument<T extends JSValue> {


   private final CDBDatabase   _database;
   private final CDBDocumentID _id;
   private final T             _contents;


   CDBDocument(final CDBDatabase database,
               final CDBDocumentID id,
               final T contents) {
      _database = database;
      _id = id;
      _contents = contents;
   }


   public CDBDatabase getDatabase() {
      return _database;
   }


   public CDBDocumentID getId() {
      return _id;
   }


   public T getContents() {
      return _contents;
   }


   @Override
   public String toString() {
      return "CDBDocument [database=" + _database + ", id=" + _id + ", contents=" + _contents + "]";
   }


}
