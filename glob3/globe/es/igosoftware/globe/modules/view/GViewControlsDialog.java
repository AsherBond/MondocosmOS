/*

 IGO Software SL  -  info@igosoftware.es

 http://www.glob3.org

-------------------------------------------------------------------------------
 Copyright (c) 2010, IGO Software SL
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
     * Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.
     * Neither the name of the IGO Software SL nor the
       names of its contributors may be used to endorse or promote products
       derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL IGO Software SL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-------------------------------------------------------------------------------

*/


package es.igosoftware.globe.modules.view;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.ViewControlsLayer;
import gov.nasa.worldwind.layers.ViewControlsSelectListener;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class GViewControlsDialog
         extends
            JDialog {

   /**
    * 
    */
   private static final long serialVersionUID    = 1L;
   private ViewControlsLayer m_ViewControlsLayer = null;
   private final WorldWindow m_WW;


   public GViewControlsDialog(final WorldWindow ww) {

      super((JDialog) null, true);

      setLocationRelativeTo(null);

      m_WW = ww;
      try {
         final List<Layer> layers = ww.getModel().getLayers().getLayersByClass(ViewControlsLayer.class);
         if (layers.size() != 0) {
            m_ViewControlsLayer = (ViewControlsLayer) layers.get(0);
         }
         else {
            m_ViewControlsLayer = new ViewControlsLayer();
            ww.getModel().getLayers().add(m_ViewControlsLayer);
            ww.addSelectListener(new ViewControlsSelectListener(ww, m_ViewControlsLayer));
         }
         getContentPane().add(makeControlPanel());
         pack();
      }
      catch (final Exception e) {
         e.printStackTrace();
      }

   }


   private JPanel makeControlPanel() {

      final JPanel controlPanel = new JPanel();
      controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
      controlPanel.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9), new TitledBorder("View Controls")));
      controlPanel.setToolTipText("Select active view controls");

      // Radio buttons - layout
      final JPanel layoutPanel = new JPanel(new GridLayout(0, 2, 0, 0));
      layoutPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      final ButtonGroup group = new ButtonGroup();
      JRadioButton button = new JRadioButton("Horizontal", true);
      group.add(button);
      button.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent actionEvent) {
            m_ViewControlsLayer.setLayout(AVKey.HORIZONTAL);
            m_WW.redraw();
         }
      });
      layoutPanel.add(button);
      button = new JRadioButton("Vertical", false);
      group.add(button);
      button.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent actionEvent) {
            m_ViewControlsLayer.setLayout(AVKey.VERTICAL);
            m_WW.redraw();
         }
      });
      layoutPanel.add(button);

      // Scale slider
      final JPanel scalePanel = new JPanel(new GridLayout(0, 1, 0, 0));
      scalePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      scalePanel.add(new JLabel("Scale:"));
      final JSlider scaleSlider = new JSlider(1, 20, 10);
      scaleSlider.addChangeListener(new ChangeListener() {
         @Override
         public void stateChanged(final ChangeEvent event) {
            m_ViewControlsLayer.setScale(((JSlider) event.getSource()).getValue() / 10d);
            m_WW.redraw();
         }
      });
      scalePanel.add(scaleSlider);

      // Check boxes
      final JPanel checkPanel = new JPanel(new GridLayout(0, 2, 0, 0));
      checkPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

      JCheckBox check = new JCheckBox("Pan");
      check.setSelected(m_ViewControlsLayer.isShowPanControls());
      check.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent actionEvent) {
            m_ViewControlsLayer.setShowPanControls(((JCheckBox) actionEvent.getSource()).isSelected());
            m_WW.redraw();
         }
      });
      checkPanel.add(check);

      check = new JCheckBox("Look");
      check.setSelected(m_ViewControlsLayer.isShowLookControls());
      check.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent actionEvent) {
            m_ViewControlsLayer.setShowLookControls(((JCheckBox) actionEvent.getSource()).isSelected());
            m_WW.redraw();
         }
      });
      checkPanel.add(check);

      check = new JCheckBox("Zoom");
      check.setSelected(m_ViewControlsLayer.isShowZoomControls());
      check.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent actionEvent) {
            m_ViewControlsLayer.setShowZoomControls(((JCheckBox) actionEvent.getSource()).isSelected());
            m_WW.redraw();
         }
      });
      checkPanel.add(check);

      check = new JCheckBox("Heading");
      check.setSelected(m_ViewControlsLayer.isShowHeadingControls());
      check.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent actionEvent) {
            m_ViewControlsLayer.setShowHeadingControls(((JCheckBox) actionEvent.getSource()).isSelected());
            m_WW.redraw();
         }
      });
      checkPanel.add(check);

      check = new JCheckBox("Pitch");
      check.setSelected(m_ViewControlsLayer.isShowPitchControls());
      check.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent actionEvent) {
            m_ViewControlsLayer.setShowPitchControls(((JCheckBox) actionEvent.getSource()).isSelected());
            m_WW.redraw();
         }
      });
      checkPanel.add(check);

      check = new JCheckBox("Field of view");
      check.setSelected(m_ViewControlsLayer.isShowFovControls());
      check.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent actionEvent) {
            m_ViewControlsLayer.setShowFovControls(((JCheckBox) actionEvent.getSource()).isSelected());
            m_WW.redraw();
         }
      });
      checkPanel.add(check);

      check = new JCheckBox("Vertical exaggeration");
      check.setSelected(m_ViewControlsLayer.isShowVeControls());
      check.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent actionEvent) {
            m_ViewControlsLayer.setShowVeControls(((JCheckBox) actionEvent.getSource()).isSelected());
            m_WW.redraw();
         }
      });
      checkPanel.add(check);

      controlPanel.add(layoutPanel);
      controlPanel.add(scalePanel);
      controlPanel.add(checkPanel);
      return controlPanel;
   }

}
