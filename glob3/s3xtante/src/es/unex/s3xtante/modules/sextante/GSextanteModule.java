

package es.unex.s3xtante.modules.sextante;

import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;

import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.actions.GButtonGenericAction;
import es.igosoftware.globe.actions.GGenericAction;
import es.igosoftware.globe.actions.IGenericAction;
import es.unex.s3xtante.modules.sextante.bindings.GlobeGUIFactory;
import es.unex.s3xtante.modules.sextante.bindings.GlobeInputFactory;
import es.unex.s3xtante.modules.sextante.bindings.GlobeOutputFactory;
import es.unex.s3xtante.modules.sextante.bindings.GlobePostProcessTaskFactory;
import es.unex.sextante.gui.additionalResults.AdditionalResults;
import es.unex.sextante.gui.core.SextanteGUI;
import es.unex.sextante.gui.history.History;
import gov.nasa.worldwind.Model;


public class GSextanteModule
         extends
            GAbstractGlobeModule {


   public GSextanteModule(final IGlobeRunningContext context) {
      super(context);
   }


   private String getJarsFolder() {

      final String sPath = System.getProperty("user.dir") + "/lib/sextante";

      return sPath;

   }


   private String getHelpPath() {

      final String sPath = System.getProperty("user.dir") + "/sextante_help";
      return sPath;

   }


   @Override
   public String getDescription() {

      return "SEXTANTE Toolbox";

   }


   @Override
   public List<? extends IGenericAction> getGenericActions(final IGlobeRunningContext context) {

      final GGenericAction toolbox = new GButtonGenericAction("SEXTANTE Toolbox", 'T', new ImageIcon("images/sextante.gif"),
               IGenericAction.MenuArea.ANALYSIS, false) {

         @Override
         public void execute() {

            SextanteGUI.getGUIFactory().showToolBoxDialog();
         }

      };

      final GGenericAction modeler = new GButtonGenericAction("SEXTANTE Modeler", 'M', new ImageIcon("images/model.png"),
               IGenericAction.MenuArea.ANALYSIS, false) {

         @Override
         public void execute() {

            SextanteGUI.getGUIFactory().showModelerDialog();
         }

      };

      final GGenericAction commandline = new GButtonGenericAction("SEXTANTE Command Line", 'C', new ImageIcon(
               "images/terminal.png"), IGenericAction.MenuArea.ANALYSIS, false) {

         @Override
         public void execute() {

            SextanteGUI.getGUIFactory().showCommandLineDialog();

         }

      };

      final GGenericAction history = new GButtonGenericAction("SEXTANTE History", 'H', new ImageIcon("images/history.gif"),
               IGenericAction.MenuArea.ANALYSIS, false) {

         @Override
         public void execute() {

            SextanteGUI.getGUIFactory().showHistoryDialog();

         }

      };

      final GGenericAction results = new GButtonGenericAction("SEXTANTE Results", 'R', new ImageIcon("images/chart.gif"),
               IGenericAction.MenuArea.ANALYSIS, false) {

         @Override
         public void execute() {

            SextanteGUI.getGUIFactory().showAdditionalResultsDialog(AdditionalResults.getComponents());
         }

      };

      final GGenericAction explorer = new GButtonGenericAction("SEXTANTE Data Explorer", 'E', new ImageIcon(
               "images/documenter.png"), IGenericAction.MenuArea.ANALYSIS, false) {

         @Override
         public void execute() {

            SextanteGUI.getGUIFactory().showDataExplorer();
         }

      };

      return Arrays.asList(toolbox, modeler, commandline, history, results, explorer);
   }


   @Override
   public void initialize(final IGlobeRunningContext context) {

      SextanteGUI.setHelpPath(getHelpPath());
      final Model model = context.getWorldWindModel().getModel();
      es.unex.sextante.core.Sextante.initialize(getJarsFolder());
      SextanteGUI.initialize();
      SextanteGUI.setGUIFactory(new GlobeGUIFactory(model));
      SextanteGUI.setMainFrame(context.getApplication().getFrame());
      SextanteGUI.setInputFactory(new GlobeInputFactory(model));
      SextanteGUI.setOutputFactory(new GlobeOutputFactory());
      SextanteGUI.setPostProcessTaskFactory(new GlobePostProcessTaskFactory());
      History.startSession();

   }


   @Override
   public String getVersion() {
      return null;
   }


   @Override
   public String getName() {
      return "SEXTANTE Toolbox";
   }


}
