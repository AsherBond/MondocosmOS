

package es.igosoftware.couching;

import java.util.Collections;
import java.util.List;


public class CDBViewResult {


   private final long                _totalRows;
   private final long                _offset;
   private final List<CDBDocumentID> _documentIDs;


   CDBViewResult(final long totalRows,
                 final long offset,
                 final List<CDBDocumentID> documentIDs) {
      _totalRows = totalRows;
      _offset = offset;
      _documentIDs = documentIDs;
   }


   public long getTotalRows() {
      return _totalRows;
   }


   public long getOffset() {
      return _offset;
   }


   public List<CDBDocumentID> getDocumentIDs() {
      return Collections.unmodifiableList(_documentIDs);
   }


   @Override
   public String toString() {
      return "CDBViewResult [totalRows=" + _totalRows + ", offset=" + _offset + ", documentIDs=" + _documentIDs + "]";
   }


}
