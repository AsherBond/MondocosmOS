

package es.unex.s3xtante.modules.sextante.bindings;

import javax.swing.table.DefaultTableModel;

import es.unex.sextante.dataObjects.IRecord;
import es.unex.sextante.dataObjects.IRecordsetIterator;
import es.unex.sextante.dataObjects.RecordImpl;


public class WWRecordsetIterator
         implements
            IRecordsetIterator {

   private final DefaultTableModel m_Model;
   private int                     m_iRecord;


   public WWRecordsetIterator(final DefaultTableModel model) {

      m_Model = model;
      m_iRecord = 0;

   }


   @Override
   public boolean hasNext() {

      return m_iRecord < m_Model.getRowCount();

   }


   @Override
   public IRecord next() {

      final Object obj[] = new Object[m_Model.getColumnCount()];
      for (int i = 0; i < obj.length; i++) {
         obj[i] = m_Model.getValueAt(m_iRecord, i);
      }
      final RecordImpl record = new RecordImpl(obj);
      m_iRecord++;
      return record;

   }


   @Override
   public void close() {

   }

}
