

package es.unex.s3xtante.tables;

import java.util.ArrayList;

import es.unex.sextante.dataObjects.ITable;


public class Tables {

   private static ArrayList<ITable> m_Tables = new ArrayList<ITable>();


   public static ITable[] getTables() {

      return m_Tables.toArray(new ITable[0]);

   }


   public static void addTable(final ITable table) {

      m_Tables.add(table);

   }

}
