

package es.unex.s3xtante.modules.sextante.bindings;

import java.io.File;
import java.io.IOException;

import javax.swing.table.DefaultTableModel;

import es.unex.s3xtante.tables.CSVFileTools;
import es.unex.sextante.dataObjects.AbstractTable;
import es.unex.sextante.dataObjects.IRecordsetIterator;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.IOutputChannel;


public class GlobeTable
         extends
            AbstractTable {

   private String            m_sName;
   private String            m_sFilename;
   private DefaultTableModel m_BaseDataObject;


   public void create(final DefaultTableModel model,
                      final String sFilename) {

      m_BaseDataObject = model;
      m_sFilename = sFilename;
      m_sName = new File(sFilename).getName();

   }


   @Override
   public void addRecord(final Object[] values) {

      final DefaultTableModel table = m_BaseDataObject;
      table.addRow(values);

   }


   public void create(final String sName,
                      final String sFilename,
                      final String[] sFields) {

      try {
         m_sFilename = sFilename;
         m_sName = sName;
         m_BaseDataObject = new DefaultTableModel(sFields, 0);
      }
      catch (final Exception e) {
         e.printStackTrace();
      }

   }


   @Override
   public IRecordsetIterator iterator() {

      return new GlobeRecordsetIterator(m_BaseDataObject);

   }


   @Override
   public String getFieldName(final int i) {

      final DefaultTableModel table = m_BaseDataObject;
      return table.getColumnName(i);

   }


   @Override
   public Class<?> getFieldType(final int i) {

      return String.class;

   }


   @Override
   public int getFieldCount() {

      final DefaultTableModel table = m_BaseDataObject;
      return table.getColumnCount();


   }


   @Override
   public long getRecordCount() {

      final DefaultTableModel table = m_BaseDataObject;
      return table.getRowCount();

   }


   @Override
   public void close() {

   }


   @Override
   public String getName() {

      return m_sName;

   }


   @Override
   public void open() {

   }


   @Override
   public void postProcess() {


      try {
         CSVFileTools.save(m_BaseDataObject, new File(m_sFilename));
      }
      catch (final IOException e) {
         // TODO
      }

   }


   @Override
   public void setName(final String name) {

      m_sName = name;

   }


   @Override
   public void free() {
   }


   @Override
   public Object getBaseDataObject() {

      return m_BaseDataObject;

   }


   @Override
   public IOutputChannel getOutputChannel() {

      return new FileOutputChannel(m_sFilename);

   }

}
