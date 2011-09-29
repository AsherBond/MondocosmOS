

package es.unex.s3xtante.tables;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.table.DefaultTableModel;


public class CSVFileTools {

   public static DefaultTableModel read(final File file) {

      final DefaultTableModel model = new DefaultTableModel();
      BufferedReader fin = null;

      try {
         String s = new String();
         fin = new BufferedReader(new FileReader(file));
         if ((s = fin.readLine()) != null) {
            final String[] sHeaders = s.split(",");
            model.setColumnIdentifiers(sHeaders);
         }
         while ((s = fin.readLine()) != null) {
            model.addRow(s.split(","));
         }
      }
      catch (final Exception e) {

      }

      if (fin != null) {
         try {
            fin.close();
         }
         catch (final IOException e) {}
      }

      return model;

   }


   public static void save(final DefaultTableModel model,
                           final File file) throws IOException {

      final FileWriter writer = new FileWriter(file);
      final BufferedWriter out = new BufferedWriter(writer);

      int i;
      for (i = 0; i < model.getColumnCount() - 1; i++) {
         out.write(model.getColumnName(i) + ",");
      }
      out.write(model.getColumnName(i) + "\n");

      for (int j = 0; j < model.getRowCount(); j++) {
         for (i = 0; i < model.getColumnCount() - 1; i++) {
            out.write(model.getValueAt(j, i) + ",");
         }
         out.write(model.getValueAt(j, i) + ",");
      }

      out.close();

   }
}
