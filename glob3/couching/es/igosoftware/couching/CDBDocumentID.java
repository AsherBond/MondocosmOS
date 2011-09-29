

package es.igosoftware.couching;

import es.igosoftware.util.GAssert;


public class CDBDocumentID {

   private final String _id;
   private final String _revision;


   public CDBDocumentID(final String id,
                        final String revision) {
      GAssert.notNull(id, "id");

      _id = id;
      _revision = revision;
   }


   public String getId() {
      return _id;
   }


   public String getRevision() {
      return _revision;
   }


   @Override
   public String toString() {
      return "CDBDocumentID [id=" + _id + ", revision=" + _revision + "]";
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_id == null) ? 0 : _id.hashCode());
      result = prime * result + ((_revision == null) ? 0 : _revision.hashCode());
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
      final CDBDocumentID other = (CDBDocumentID) obj;
      if (_id == null) {
         if (other._id != null) {
            return false;
         }
      }
      else if (!_id.equals(other._id)) {
         return false;
      }
      if (_revision == null) {
         if (other._revision != null) {
            return false;
         }
      }
      else if (!_revision.equals(other._revision)) {
         return false;
      }
      return true;
   }


}
