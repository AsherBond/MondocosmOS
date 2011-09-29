

package es.unex.s3xtante.modules.tables;

import java.io.File;
import java.util.Collections;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.table.DefaultTableModel;

import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.actions.GButtonGenericAction;
import es.igosoftware.globe.actions.IGenericAction;
import es.unex.s3xtante.modules.sextante.bindings.GlobeTable;
import es.unex.s3xtante.tables.CSVFileTools;
import es.unex.s3xtante.tables.Tables;
import es.unex.sextante.gui.algorithm.GenericFileFilter;


public class GAddTableModule
         extends
            GAbstractGlobeModule {

   public GAddTableModule(final IGlobeRunningContext context) {
      super(context);
   }


   @Override
   public String getDescription() {
      return "Add Table";
   }


   @Override
   public List<? extends IGenericAction> getGenericActions(final IGlobeRunningContext context) {

      final IGenericAction graticule = new GButtonGenericAction("Add table", ' ', null, IGenericAction.MenuArea.FILE, false) {

         @Override
         public void execute() {

            final JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new GenericFileFilter(new String[] {
               "csv"
            }, "Comma-separated values (*.csv)"));
            final int returnVal = fc.showOpenDialog(context.getApplication().getFrame());

            if (returnVal == JFileChooser.APPROVE_OPTION) {
               final String sFilename = fc.getSelectedFile().getAbsolutePath();
               try {
                  final DefaultTableModel model = CSVFileTools.read(new File(sFilename));
                  if (model != null) {
                     final GlobeTable table = new GlobeTable();
                     table.create(model, sFilename);
                     Tables.addTable(table);
                  }
               }
               catch (final Exception e) {
                  //TODO:
               }
            }
         }

      };

      //      return new IGenericAction[] { graticule };
      return Collections.singletonList(graticule);
   }


   @Override
   public String getName() {
      return "Add table";
   }


   @Override
   public String getVersion() {
      return null;
   }


}
