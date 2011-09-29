

package es.unex.s3xtante.modules.sextante.bindings;

import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import es.igosoftware.experimental.vectorial.GVectorial2DLayer;
import es.igosoftware.globe.IGlobeRasterLayer;
import es.unex.sextante.core.GeoAlgorithm;
import es.unex.sextante.dataObjects.IDataObject;
import es.unex.sextante.gui.additionalResults.AdditionalResultsDialog;
import es.unex.sextante.gui.cmd.BSHDialog;
import es.unex.sextante.gui.core.DefaultGUIFactory;
import es.unex.sextante.gui.core.SextanteGUI;
import es.unex.sextante.gui.history.HistoryDialog;
import es.unex.sextante.gui.modeler.ModelAlgorithm;
import es.unex.sextante.gui.modeler.ModelerDialog;
import gov.nasa.worldwind.Model;


public class GlobeGUIFactory
         extends
            DefaultGUIFactory {

   private final Model m_Model;


   public GlobeGUIFactory(final Model model) {

      m_Model = model;

   }


   @Override
   public void showBatchProcessingDialog(final GeoAlgorithm alg,
                                         final JDialog parent) {

      JOptionPane.showMessageDialog(parent, "Batch processing not yet implemented");

   }


   @Override
   public void showModelerDialog() {

      final ModelerDialog dialog = new ModelerDialog(SextanteGUI.getMainFrame());
      dialog.pack();
      dialog.setVisible(true);

   }


   @Override
   public void showModelerDialog(final ModelAlgorithm alg) {

      final ModelerDialog dialog = new ModelerDialog(SextanteGUI.getMainFrame());
      dialog.getModelerPanel().checkChangesAndOpenModel(alg.getFilename());
      dialog.pack();
      dialog.setVisible(true);

   }


   @Override
   public void showAdditionalResultsDialog(final ArrayList components) {

      if (components.size() != 0) {
         final AdditionalResultsDialog dialog = new AdditionalResultsDialog(components, SextanteGUI.getMainFrame());
         dialog.pack();
         dialog.setVisible(true);
      }

   }


   @Override
   public void showHistoryDialog() {

      final HistoryDialog dialog = new HistoryDialog(SextanteGUI.getMainFrame());
      SextanteGUI.setLastCommandOrigin(SextanteGUI.HISTORY);
      SextanteGUI.setLastCommandOriginParentDialog(dialog);
      m_History = dialog.getHistoryPanel();
      dialog.pack();
      dialog.setVisible(true);

      m_History = null;

   }


   @Override
   public void showCommandLineDialog() {

      final BSHDialog dialog = new BSHDialog(SextanteGUI.getMainFrame());
      SextanteGUI.setLastCommandOrigin(SextanteGUI.COMMANDLINE);
      SextanteGUI.setLastCommandOriginParentDialog(dialog);
      dialog.pack();
      dialog.setVisible(true);

   }


   public Model getGlobeModel() {

      return m_Model;

   }


   @Override
   public void addToView(final IDataObject obj,
                         final String viewName) {

      final Object baseObj = obj.getBaseDataObject();
      if (baseObj instanceof GVectorial2DLayer) {
         m_Model.getLayers().add((GVectorial2DLayer) baseObj);
      }
      else if (baseObj instanceof IGlobeRasterLayer) {
         m_Model.getLayers().add((IGlobeRasterLayer) baseObj);
      }

   }
}
