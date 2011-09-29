

package es.unex.s3xtante.modules.sextante.bindings;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;

import es.igosoftware.globe.IGlobeVectorLayer;
import es.igosoftware.globe.layers.GGlobeRasterLayer;
import es.unex.s3xtante.tables.Tables;
import es.unex.sextante.core.GeoAlgorithm;
import es.unex.sextante.core.ObjectAndDescription;
import es.unex.sextante.core.OutputObjectsSet;
import es.unex.sextante.dataObjects.I3DRasterLayer;
import es.unex.sextante.dataObjects.IRasterLayer;
import es.unex.sextante.dataObjects.ITable;
import es.unex.sextante.dataObjects.IVectorLayer;
import es.unex.sextante.gui.additionalResults.AdditionalResults;
import es.unex.sextante.gui.core.SextanteGUI;
import es.unex.sextante.gui.settings.SextanteGeneralSettings;
import es.unex.sextante.outputs.IOutputChannel;
import es.unex.sextante.outputs.NullOutputChannel;
import es.unex.sextante.outputs.Output;
import es.unex.sextante.outputs.Output3DRasterLayer;
import es.unex.sextante.outputs.OutputRasterLayer;
import es.unex.sextante.outputs.OutputTable;
import es.unex.sextante.outputs.OutputVectorLayer;
import es.unex.sextante.outputs.OverwriteOutputChannel;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.layers.Layer;


public class GlobePostProcessTask
         implements
            Runnable {

   private final OutputObjectsSet m_Output;
   private final boolean          m_bShowResultsDialog;


   public GlobePostProcessTask(final GeoAlgorithm algorithm,
                               final boolean bShowResultsDialog) {

      m_Output = algorithm.getOutputObjects();
      m_bShowResultsDialog = bShowResultsDialog;

   }


   @Override
   public void run() {

      addResults();

   }


   private boolean addResults() {

      String sDescription;
      boolean bShowAdditionalPanel = false;
      final boolean bUseInternalNames = new Boolean(
               SextanteGUI.getSettingParameterValue(SextanteGeneralSettings.USE_INTERNAL_NAMES)).booleanValue();
      final boolean bModiFyResultsNames = new Boolean(SextanteGUI.getSettingParameterValue(SextanteGeneralSettings.MODIFY_NAMES)).booleanValue();


      for (int i = 0; i < m_Output.getOutputObjectsCount(); i++) {

         final Output out = m_Output.getOutput(i);
         sDescription = out.getDescription();
         final IOutputChannel channel = out.getOutputChannel();
         final Object object = out.getOutputObject();
         if ((out instanceof OutputRasterLayer) || (out instanceof Output3DRasterLayer) || (out instanceof OutputTable)
             || (out instanceof OutputVectorLayer)) {
            if (bUseInternalNames) {
               sDescription = out.getName();
            }
            else if (bModiFyResultsNames) {
               sDescription = SextanteGUI.modifyResultName(sDescription);
            }
            if ((channel instanceof NullOutputChannel) || (channel == null)) {
               continue;
            }
         }
         if (object instanceof IRasterLayer) {
            final GlobeRasterLayer layer = (GlobeRasterLayer) object;
            final GGlobeRasterLayer rl = (GGlobeRasterLayer) layer.getBaseDataObject();
            final Model model = ((GlobeGUIFactory) SextanteGUI.getGUIFactory()).getGlobeModel();
            rl.setPickEnabled(false);
            model.getLayers().add(rl);
         }
         if (object instanceof I3DRasterLayer) {
            /*final WWRasterLayer layer = (WWRasterLayer) object;
            final GGlobeRasterLayer rl = (GGlobeRasterLayer) layer.getBaseDataObject();
            final Model model = ((WWGUIFactory) SextanteGUI.getGUIFactory()).getGlobeModel();
            model.getLayers().add(rl);*/
         }
         else if (object instanceof IVectorLayer) {
            final IVectorLayer layer = (IVectorLayer) object;
            final Model model = ((GlobeGUIFactory) SextanteGUI.getGUIFactory()).getGlobeModel();
            model.getLayers().add((Layer) layer.getBaseDataObject());
            if (channel instanceof OverwriteOutputChannel) {
               final IGlobeVectorLayer globeLayer = (IGlobeVectorLayer) ((OverwriteOutputChannel) channel).getLayer().getBaseDataObject();
               model.getLayers().remove(globeLayer);
            }
         }
         else if (object instanceof ITable) {
            final GlobeTable table = (GlobeTable) object;
            Tables.addTable(table);
         }
         else if (object instanceof String) {
            JTextPane jTextPane;
            JScrollPane jScrollPane;
            jTextPane = new JTextPane();
            jTextPane.setEditable(false);
            jTextPane.setContentType("text/html");
            jTextPane.setText((String) object);
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView(jTextPane);
            jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
            jTextPane.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
            AdditionalResults.addComponent(new ObjectAndDescription(sDescription, jScrollPane));
            bShowAdditionalPanel = true;
         }
         else if (object instanceof Component) {
            AdditionalResults.addComponent(new ObjectAndDescription(sDescription, object));
            bShowAdditionalPanel = true;
         }

      }

      if (bShowAdditionalPanel && m_bShowResultsDialog) {
         AdditionalResults.showPanel();
      }

      return true;

   }
}
