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


package es.igosoftware.globe.attributes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.JXColorSelectionButton;
import org.jdesktop.swingx.JXMultiThumbSlider;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.color.ColorUtil;
import org.jdesktop.swingx.color.GradientThumbRenderer;
import org.jdesktop.swingx.color.GradientTrackRenderer;
import org.jdesktop.swingx.multislider.Thumb;
import org.jdesktop.swingx.multislider.ThumbListener;


/**
 * <p>
 * A specialized JXPanel that allows the user to construct and choose a Gradient. The returned values will be one of:
 * LinearGradientPaint or RadialGradientPaint.
 * </p>
 * 
 * <p>
 * <b>Dependency</b>: Because this class relies on LinearGradientPaint and RadialGradientPaint, it requires the optional
 * MultipleGradientPaint.jar
 * </p>
 * 
 * @author joshy
 */
public class GGradientChooserDialog
         extends
            JXPanel {

   private static final long         serialVersionUID = 1L;

   /**
    * The multi-thumb slider to use for the gradient stops
    */
   private JXMultiThumbSlider<Color> slider;
   private JButton                   deleteThumbButton;
   private JButton                   addThumbButton;
   private JPanel                    topPanel;
   private JTextField                colorField;
   private JXColorSelectionButton    changeColorButton;
   private JSpinner                  colorLocationSpinner;
   private JSpinner                  alphaSpinner;
   private JSlider                   alphaSlider;

   private MultipleGradientPaint     gradient;


   /**
    * Creates new JXGradientChooser
    */
   public GGradientChooserDialog() {
      initComponents2();
   }


   /**
    * Returns the MultipleGradientPaint currently choosen by the user.
    * 
    * @return the currently selected gradient
    */
   public MultipleGradientPaint getGradient() {
      return gradient;
   }

   private boolean thumbsMoving = false;


   /**
    * Sets the gradient within this panel to the new gradient. This will delete the old gradient all of it's settings, resetting
    * the slider, gradient type selection, and other gradient configuration options to match the new gradient.
    * 
    * @param mgrad
    *           The desired gradient.
    */
   public void setGradient(final LinearGradientPaint mgrad) {
      if (gradient == mgrad) {
         return;
      }
      if (mgrad == null) {
         gradient = null;
         return;
      }

      final float[] fracts = mgrad.getFractions();
      final Color[] colors = mgrad.getColors();

      if (!thumbsMoving) {
         // update the slider properly
         if (slider.getModel().getThumbCount() != mgrad.getColors().length) {
            // removing all thumbs;
            while (slider.getModel().getThumbCount() > 0) {
               slider.getModel().removeThumb(0);
            }
            // add them back
            for (int i = 0; i < fracts.length; i++) {
               slider.getModel().addThumb(fracts[i], colors[i]);
            }
         }
         else {
            for (int i = 0; i < fracts.length; i++) {
               slider.getModel().getThumbAt(i).setObject(colors[i]);
               slider.getModel().getThumbAt(i).setPosition(fracts[i]);
            }
         }
      }
      else {
         //log.fine("not updating because it's moving");
      }

      //reflectedRadio.setSelected()
      final MultipleGradientPaint old = getGradient();
      gradient = mgrad;
      firePropertyChange("gradient", old, getGradient());
      repaint();
   }


   private void updateFromStop(final Thumb<Color> thumb) {
      if (thumb == null) {
         updateFromStop(-1, -1, Color.black);
      }
      else {
         updateFromStop(1, thumb.getPosition(), thumb.getObject());
      }
   }


   private void updateFromStop(final int thumb,
                               final float position,
                               final Color color) {
      //log.fine("updating: " + thumb + " " + position + " " + color);
      if (thumb == -1) {
         colorLocationSpinner.setEnabled(false);
         alphaSpinner.setEnabled(false);
         alphaSlider.setEnabled(false);
         colorField.setEnabled(false);
         changeColorButton.setEnabled(false);
         changeColorButton.setBackground(Color.black);
         deleteThumbButton.setEnabled(false);
      }
      else {
         colorLocationSpinner.setEnabled(true);
         alphaSpinner.setEnabled(true);
         alphaSlider.setEnabled(true);
         colorField.setEnabled(true);
         changeColorButton.setEnabled(true);
         colorLocationSpinner.setValue((int) (100 * position));
         colorField.setText(Integer.toHexString(color.getRGB()).substring(2));
         alphaSpinner.setValue(color.getAlpha() * 100 / 255);
         alphaSlider.setValue(color.getAlpha() * 100 / 255);
         changeColorButton.setBackground(color);
         deleteThumbButton.setEnabled(true);
      }
      updateDeleteButtons();
   }


   private void updateDeleteButtons() {
      if (slider.getModel().getThumbCount() <= 2) {
         deleteThumbButton.setEnabled(false);
      }
   }


   private void updateGradientProperty() {
      firePropertyChange("gradient", null, getGradient());
      final int iThumbs = slider.getModel().getThumbCount();
      final Color[] color = new Color[iThumbs];
      final float[] fractions = new float[iThumbs];
      final List<Thumb<Color>> thumbs = slider.getModel().getSortedThumbs();
      for (int i = 0; i < thumbs.size(); i++) {
         final Thumb<Color> thumb = thumbs.get(i);
         color[i] = thumb.getObject();
         fractions[i] = thumb.getPosition();
      }
      gradient = new LinearGradientPaint(0f, 0f, 1f, 1f, fractions, color);
   }


   private void initComponents() {
      // declarations for anonymous components
      JPanel jPanel1, jPanel2, jPanel4;
      JLabel jLabel1, jLabel5, jLabel2, jLabel6, jLabel4, jLabel7;
      // pre-init stuff
      slider = new JXMultiThumbSlider<Color>();

      java.awt.GridBagConstraints gridBagConstraints;

      jPanel1 = new javax.swing.JPanel();
      topPanel = new javax.swing.JPanel();
      jPanel2 = new javax.swing.JPanel();
      jLabel1 = new javax.swing.JLabel();
      jLabel5 = new javax.swing.JLabel();
      colorField = new javax.swing.JTextField();
      jLabel2 = new javax.swing.JLabel();
      jLabel6 = new javax.swing.JLabel();
      colorLocationSpinner = new javax.swing.JSpinner();
      jLabel4 = new javax.swing.JLabel();
      jLabel7 = new javax.swing.JLabel();
      alphaSpinner = new javax.swing.JSpinner();
      changeColorButton = new JXColorSelectionButton();
      alphaSlider = new javax.swing.JSlider();
      //slider = new javax.swing.JSlider();
      jPanel4 = new javax.swing.JPanel();
      addThumbButton = new javax.swing.JButton();
      deleteThumbButton = new javax.swing.JButton();
      //gradientPreview = new javax.swing.JPanel();

      //setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
      jPanel1.setLayout(new java.awt.GridBagLayout());

      topPanel.setLayout(new java.awt.GridBagLayout());

      topPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Gradient"));
      jPanel2.setLayout(new java.awt.GridBagLayout());

      jLabel1.setText("Color:");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.ipadx = 2;
      gridBagConstraints.ipady = 2;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
      gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
      jPanel2.add(jLabel1, gridBagConstraints);

      jLabel5.setText("#");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
      jPanel2.add(jLabel5, gridBagConstraints);

      colorField.setColumns(6);
      colorField.setEnabled(false);
      colorField.setPreferredSize(null);
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      jPanel2.add(colorField, gridBagConstraints);

      jLabel2.setText("Location:");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
      gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
      jPanel2.add(jLabel2, gridBagConstraints);

      jLabel6.setText("%");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridy = 1;
      jPanel2.add(jLabel6, gridBagConstraints);

      colorLocationSpinner.setEnabled(false);
      colorLocationSpinner.setPreferredSize(null);
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      jPanel2.add(colorLocationSpinner, gridBagConstraints);

      jLabel4.setText("Opacity:");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridy = 2;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
      gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
      jPanel2.add(jLabel4, gridBagConstraints);

      jLabel7.setText("%");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridy = 2;
      jPanel2.add(jLabel7, gridBagConstraints);

      alphaSpinner.setEnabled(false);
      alphaSpinner.setPreferredSize(null);
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      jPanel2.add(alphaSpinner, gridBagConstraints);

      changeColorButton.setText("00");
      changeColorButton.setEnabled(false);
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.fill = java.awt.GridBagConstraints.NONE;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
      gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
      jPanel2.add(changeColorButton, gridBagConstraints);

      alphaSlider.setEnabled(false);
      alphaSlider.setPreferredSize(new java.awt.Dimension(20, 25));
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      jPanel2.add(alphaSlider, gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
      topPanel.add(jPanel2, gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      topPanel.add(slider, gridBagConstraints);

      jPanel4.setLayout(new java.awt.GridLayout(1, 0, 2, 0));

      addThumbButton.setText("Add");
      jPanel4.add(addThumbButton);

      deleteThumbButton.setText("Delete");
      jPanel4.add(deleteThumbButton);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
      topPanel.add(jPanel4, gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      jPanel1.add(topPanel, gridBagConstraints);


   }// </editor-fold>


   private void initComponents2() {
      initComponents();
      setLayout(new BorderLayout());
      add(topPanel, BorderLayout.NORTH);

      // do event handling stuff
      //create the actions and load them in the action map
      final AddThumbAction addThumbAction = new AddThumbAction();
      final DeleteThumbAction deleteThumbAction = new DeleteThumbAction();
      deleteThumbAction.setEnabled(false); //disabled to begin with
      //TODO Add to the action map with proper keys, etc
      final ActionMap actions = getActionMap();
      actions.put("add-thumb", addThumbAction);
      actions.put("delete-thumb", deleteThumbAction);
      //actions.put("change-color", changeColorAction);
      addThumbButton.setAction(addThumbAction);
      deleteThumbButton.setAction(deleteThumbAction);
      changeColorButton.addPropertyChangeListener("background", new PropertyChangeListener() {
         @Override
         public void propertyChange(final PropertyChangeEvent evt) {
            selectColorForThumb();
            updateGradientProperty();
         }
      });
      colorLocationSpinner.addChangeListener(new ChangeLocationListener());
      final ChangeAlphaListener changeAlphaListener = new ChangeAlphaListener();
      alphaSpinner.addChangeListener(changeAlphaListener);
      alphaSlider.addChangeListener(changeAlphaListener);

      ///To still refactor below::
      final SpinnerNumberModel alpha_model = new SpinnerNumberModel(100, 0, 100, 1);
      alphaSpinner.setModel(alpha_model);
      final SpinnerNumberModel location_model = new SpinnerNumberModel(100, 0, 100, 1);
      colorLocationSpinner.setModel(location_model);

      slider.setOpaque(false);
      slider.setPreferredSize(new Dimension(100, 35));
      slider.getModel().setMinimumValue(0f);
      slider.getModel().setMaximumValue(1.0f);

      slider.getModel().addThumb(0, Color.black);
      slider.getModel().addThumb(0.5f, Color.red);
      slider.getModel().addThumb(1.0f, Color.white);

      slider.setThumbRenderer(new GradientThumbRenderer());
      slider.setTrackRenderer(new GradientTrackRenderer());
      slider.addMultiThumbListener(new StopListener());

   }

   // called whenever the color location spinner is changed
   private final class ChangeLocationListener
            implements
               ChangeListener {
      @Override
      public void stateChanged(final ChangeEvent evt) {
         if (slider.getSelectedIndex() >= 0) {
            final Thumb<Color> thumb = slider.getModel().getThumbAt(slider.getSelectedIndex());
            thumb.setPosition((Integer) colorLocationSpinner.getValue() / 100f);
            updateFromStop(thumb);
            updateGradientProperty();
         }
      }
   }

   // called when the alpha slider moves
   private final class ChangeAlphaListener
            implements
               ChangeListener {
      @Override
      public void stateChanged(final ChangeEvent changeEvent) {
         if ((slider.getSelectedIndex() >= 0) && !thumbsMoving) {
            // get the selected thumb
            final Thumb<Color> thumb = slider.getModel().getThumbAt(slider.getSelectedIndex());
            // get the new alpha value
            final int alpha = changeEvent.getSource() == alphaSpinner ? (Integer) alphaSpinner.getValue()
                                                                     : alphaSlider.getValue();


            // calc new color and set it on thumb
            Color col = thumb.getObject();
            col = ColorUtil.setAlpha(col, alpha * 255 / 100);
            thumb.setObject(col);

            // set the new alpha value on the other alpha control
            if (changeEvent.getSource() == alphaSpinner) {
               alphaSlider.setValue(alpha);
            }
            else {
               alphaSpinner.setValue(alpha);
            }

         }
      }
   }


   private final class AddThumbAction
            extends
               AbstractActionExt {
      private static final long serialVersionUID = 1L;


      public AddThumbAction() {
         super("Add");
      }


      @Override
      public void actionPerformed(final ActionEvent actionEvent) {
         final float pos = 0.2f;
         final Color color = Color.black;
         @SuppressWarnings("unused")
         final int num = slider.getModel().addThumb(pos, color);
         //log.fine("new number = " + num);
         /*
         for (int i = 0; i < slider.getModel().getThumbCount(); i++) {
             float pos2 = slider.getModel().getThumbAt(i).getPosition();
             if (pos2 < pos) {
                 continue;
             }
             slider.getModel().insertThumb(pos, color, i);
             updateFromStop(i,pos,color);
             break;
         }
          */

      }
   }

   private final class DeleteThumbAction
            extends
               AbstractActionExt {
      private static final long serialVersionUID = 1L;


      public DeleteThumbAction() {
         super("Delete");
      }


      @Override
      public void actionPerformed(final ActionEvent actionEvent) {
         final int index = slider.getSelectedIndex();
         if (index >= 0) {
            slider.getModel().removeThumb(index);
            updateFromStop(-1, -1, null);
         }
      }
   }

   private class StopListener
            implements
               ThumbListener {

      public StopListener() {
         super();
      }


      @Override
      public void thumbMoved(final int thumb,
                             final float pos) {
         //log.fine("moved: " + thumb + " " + pos);
         final Color color = slider.getModel().getThumbAt(thumb).getObject();
         thumbsMoving = true;
         updateFromStop(thumb, pos, color);
         updateDeleteButtons();
         thumbsMoving = false;

      }


      @Override
      public void thumbSelected(final int thumb) {

         if (thumb == -1) {
            updateFromStop(-1, -1, Color.black);
            return;
         }
         thumbsMoving = true;
         final float pos = slider.getModel().getThumbAt(thumb).getPosition();
         final Color color = slider.getModel().getThumbAt(thumb).getObject();
         //log.fine("selected = " + thumb + " " + pos + " " + color);
         updateFromStop(thumb, pos, color);
         updateDeleteButtons();
         slider.repaint();
         thumbsMoving = false;

      }


      @Override
      public void mousePressed(final MouseEvent e) {
         if (e.getClickCount() > 1) {
            selectColorForThumb();
         }
      }
   }


   private void selectColorForThumb() {
      final int index = slider.getSelectedIndex();
      if (index >= 0) {
         final Color color = changeColorButton.getBackground();
         slider.getModel().getThumbAt(index).setObject(color);
         updateFromStop(index, slider.getModel().getThumbAt(index).getPosition(), color);
      }
   }


   /**
    * This static utility method <b>cannot</b> be called from the ETD, or your application will lock up. Call it from a separate
    * thread or create a new Thread with a Runnable.
    * 
    * @param comp
    *           The component to use when finding a top level window or frame for the dialog.
    * @param title
    *           The desired title of the gradient chooser dialog.
    * @param mgrad
    *           The gradient to initialize the chooser too.
    * @return The gradient the user chose.
    */
   public static MultipleGradientPaint showDialog(final Component comp,
                                                  final String title,
                                                  final LinearGradientPaint mgrad) {

      final Component root = SwingUtilities.getRoot(comp);
      final JDialog dialog = new JDialog((JFrame) root, title, true);
      final GGradientChooserDialog picker = new GGradientChooserDialog();
      if (mgrad != null) {
         picker.setGradient(mgrad);
      }
      dialog.add(picker);


      final JPanel panel = new JPanel();
      final JButton cancel = new JButton("Cancel");
      cancel.addActionListener(new ActionListener() {


         @Override
         public void actionPerformed(final ActionEvent actionEvent) {
            picker.setGradient(null);
            dialog.setVisible(false);

         }
      });
      final JButton okay = new JButton("Ok");
      okay.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent actionEvent) {
            dialog.setVisible(false);
         }
      });
      okay.setDefaultCapable(true);


      final GridLayout gl = new GridLayout();
      gl.setHgap(2);
      panel.setLayout(gl);
      panel.add(cancel);
      panel.add(okay);

      final JPanel p2 = new JPanel();
      p2.setLayout(new GridBagLayout());
      final GridBagConstraints gbc = new GridBagConstraints();
      gbc.anchor = GridBagConstraints.EAST;
      gbc.weightx = 1.0;
      p2.add(panel, gbc);
      dialog.add(p2, "South");

      dialog.getRootPane().setDefaultButton(okay);
      dialog.pack();
      dialog.setResizable(false);
      dialog.setVisible(true);

      return picker.getGradient();
   }


   /**
    * Creates a string representation of a {@code MultipleGradientPaint}. This string is used for debugging purposes. Its contents
    * cannot be guaranteed between releases.
    * 
    * @param paint
    *           the {@code paint} to create a string for
    * @return a string representing the supplied {@code paint}
    */
   public static String toString(final MultipleGradientPaint paint) {
      final StringBuffer buffer = new StringBuffer();
      buffer.append(paint.getClass().getName());
      final Color[] colors = paint.getColors();
      final float[] values = paint.getFractions();
      buffer.append("[");
      for (int i = 0; i < colors.length; i++) {
         buffer.append("#").append(Integer.toHexString(colors[i].getRGB()));
         buffer.append(":");
         buffer.append(values[i]);
         buffer.append(", ");
      }
      buffer.append("]");
      return buffer.toString();
   }

}
