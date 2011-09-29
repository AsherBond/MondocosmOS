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

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;

import net.miginfocom.swing.MigLayout;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;

import es.igosoftware.euclid.IGeometry2D;
import es.igosoftware.euclid.colors.GColorF;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.experimental.vectorial.rendering.coloring.GUniqueValuesColorizer;
import es.igosoftware.euclid.experimental.vectorial.rendering.coloring.GUniqueValuesDataSet;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GConstantExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.IExpression;
import es.igosoftware.experimental.gui.GUniqueValuesPanel;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.IGlobeTranslator;
import es.igosoftware.util.GComponentTitledBorder;
import es.igosoftware.util.GPair;
import gov.nasa.worldwind.layers.Layer;


public abstract class GColorExpressionLayerAttribute<GeometryT extends IGeometry2D>
         extends
            GAbstractLayerAttribute<GPair<IColor, IExpression<GeometryT, IColor>>> {

   private static final String CONSTANT_OPTION = "Constant";
   private static final String UNIQUE_OPTION   = "Unique";

   //private final JPanel        _widget;
   private String              _selection;
   private JPanel              _colorsPanel;
   private EventHandler        _eventHandler;


   //private IColor              _defaultColor;


   public GColorExpressionLayerAttribute(final String label,
                                         final String description,
                                         final String propertyName) {
      this(label, description, propertyName, false);
   }


   public GColorExpressionLayerAttribute(final String label,
                                         final String description,
                                         final String propertyName,
                                         final boolean readOnly) {
      super(label, description, propertyName, readOnly);

      _selection = CONSTANT_OPTION;
      _eventHandler = new EventHandler();
      //widget = new JPanel();

      //_defaultColor = defaultColor;
   }

   //-- Define class for handling event data from Unique values dialog
   public class EventHandler
            implements
               IEventsHandler {

      @Override
      public void setAction(final Object value) {

         //@SuppressWarnings("unchecked")
         //final GTriplet<IColor, GColorScheme, HashMap<String, IColor>> colorData = (GTriplet<IColor, GColorScheme, HashMap<String, IColor>>) value;
         final GUniqueValuesDataSet colorData = (GUniqueValuesDataSet) value;

         if (colorData._colorScheme != null) {
            set(new GPair<IColor, IExpression<GeometryT, IColor>>(colorData._defaultColor, new GUniqueValuesColorizer<GeometryT>(
                     colorData, false, null)));
         }
         else {
            set(new GPair<IColor, IExpression<GeometryT, IColor>>(colorData._defaultColor,
                     new GConstantExpression<GeometryT, IColor>(colorData._defaultColor)));
         }

         //         if (colorData._colorScheme != null) {
         //            set(new GPair<IColor, IExpression<GeometryT, IColor>>(colorData._defaultColor, new GUniqueValuesColorizer<GeometryT>(
         //                     colorData._fieldName, colorData._colorScheme, colorData._defaultColor, false, null, colorData._colors)));
         //         }
         //         else {
         //            set(new GPair<IColor, IExpression<GeometryT, IColor>>(colorData._defaultColor,
         //                     new GConstantExpression<GeometryT, IColor>(colorData._defaultColor)));
         //         }

      }


      @Override
      public Object getAction() {

         return null;
      }

   }


   @Override
   public final GPair<Component, EventListener> createWidget(final IGlobeRunningContext context,
                                                             final Layer layer) {

      final JPanel widget = new JPanel();
      //_widget.setLayout(new BoxLayout(_widget, BoxLayout.Y_AXIS));
      widget.setLayout(new MigLayout("fillx, flowy, insets 0 0 0 0, gap 0 1"));
      widget.setBorder(createTitledBorder(context, widget));
      //_widget.setAlignmentX(Component.RIGHT_ALIGNMENT);
      widget.setBackground(Color.WHITE);

      final JRadioButton constantButton = new JRadioButton(CONSTANT_OPTION);
      constantButton.setActionCommand(CONSTANT_OPTION);
      constantButton.setSelected(true);
      setTooltip(context, constantButton);

      final JRadioButton uniqueButton = new JRadioButton(UNIQUE_OPTION);
      uniqueButton.setActionCommand(UNIQUE_OPTION);
      setTooltip(context, uniqueButton);

      final ButtonGroup buttonsGroup = new ButtonGroup();
      buttonsGroup.add(constantButton);
      buttonsGroup.add(uniqueButton);

      final JPanel radionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
      radionButtonsPanel.setBackground(Color.WHITE);
      radionButtonsPanel.setBorder(BorderFactory.createEmptyBorder());
      radionButtonsPanel.add(constantButton);
      radionButtonsPanel.add(uniqueButton);


      final ActionListener selectionActionListener = new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {

            _selection = e.getActionCommand();

            _colorsPanel.setVisible(false);
            widget.remove(_colorsPanel);
            if (_selection.equals(CONSTANT_OPTION)) {
               _colorsPanel = createConstantColorsPanel(context);
            }
            else {
               _colorsPanel = createUniqueColorsPanel(context, layer);
            }
            widget.add(_colorsPanel);
            //_widget.add(_colorsPanel, "growx, wrap, span 2");
            _colorsPanel.setVisible(true);

         }
      };


      if (isReadOnly()) {
         constantButton.setEnabled(false);
         uniqueButton.setEnabled(false);
      }
      else {

         constantButton.addActionListener(selectionActionListener);
         uniqueButton.addActionListener(selectionActionListener);

         if (_selection.equals(CONSTANT_OPTION)) {
            _colorsPanel = createConstantColorsPanel(context);
            constantButton.setSelected(true);
         }
         else {
            _colorsPanel = createUniqueColorsPanel(context, layer);
            uniqueButton.setSelected(true);
         }
         //_widget.add(_colorsPanel, "growx, wrap, span 2");
      }

      setListener(new IChangeListener() {
         @Override
         public void changed() {

            _colorsPanel.setVisible(false);
            widget.remove(_colorsPanel);

            if (_selection.equals(CONSTANT_OPTION)) {
               constantButton.setSelected(true);
               _colorsPanel = createConstantColorsPanel(context);
            }
            else {
               uniqueButton.setSelected(true);
               _colorsPanel = createUniqueColorsPanel(context, layer);
            }

            widget.add(_colorsPanel);
            _colorsPanel.setVisible(true);

         }
      });

      final EventListener listener = subscribeToEvents(layer);

      widget.add(radionButtonsPanel);
      widget.add(_colorsPanel);

      return new GPair<Component, EventListener>(widget, listener);
   }


   private JPanel createConstantColorsPanel(final IGlobeRunningContext context) {

      final JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
      panel.setBackground(Color.WHITE);
      //panel.setBorder(BorderFactory.createEmptyBorder());

      final Color defaultColor = get()._first.asAWTColor();

      final JButton colorButton = new JButton("..");
      colorButton.setBackground(defaultColor);

      colorButton.putClientProperty(SubstanceLookAndFeel.COLORIZATION_FACTOR, Double.valueOf(1));
      colorButton.putClientProperty(SubstanceLookAndFeel.BUTTON_NO_MIN_SIZE_PROPERTY, Boolean.TRUE);
      colorButton.putClientProperty(SubstanceLookAndFeel.CORNER_RADIUS, Float.valueOf(1)); // currently is ignored

      final ActionListener constantActionListener = new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            final Color newColor = JColorChooser.showDialog(context.getApplication().getFrame(), getLabel(), defaultColor);

            if (newColor != null) {
               set(new GPair<IColor, IExpression<GeometryT, IColor>>(GColorF.fromAWTColor(newColor),
                        new GConstantExpression<GeometryT, IColor>(GColorF.fromAWTColor(newColor))));
               colorButton.setBackground(newColor);
            }
         }
      };

      colorButton.addActionListener(constantActionListener);
      setTooltip(context, colorButton);
      panel.add(colorButton);

      // set current default color
      //_eventHandler.setAction(new GTriplet<IColor, GColorScheme, HashMap<String, IColor>>(get()._first, null, null));
      final GUniqueValuesDataSet setValue = new GUniqueValuesDataSet(get()._first, null, null, null);
      _eventHandler.setAction(setValue);


      return panel;
   }


   private JPanel createUniqueColorsPanel(final IGlobeRunningContext context,
                                          final Layer layer) {

      final Color defaultColor = get()._first.asAWTColor();

      final JPanel panel = new GUniqueValuesPanel(context, layer, _eventHandler, defaultColor);

      return panel;
   }


   private Border createTitledBorder(final IGlobeRunningContext context,
                                     final JComponent container) {
      //      final JLabel l;
      //      if (_icon == null) {
      //      l = new JLabel(" " + application.getTranslation(_label) + " ");
      //      }
      //      else {
      //         l = new JLabel(application.getTranslation(_label) + " ");
      //         l.setIcon(_icon);
      //      }
      //final JLabel label = GSwingUtils.makeBold(l);

      final IGlobeTranslator translator = context.getTranslator();
      final JLabel label = new JLabel(" " + translator.getTranslation(_label) + " ");
      label.setOpaque(true);
      label.setBackground(Color.WHITE);

      if (_description != null) {
         label.setToolTipText(translator.getTranslation(_description));
      }

      final Border border = BorderFactory.createCompoundBorder( //
               BorderFactory.createLineBorder(Color.GRAY), //
               BorderFactory.createEmptyBorder(3, 3, 3, 3));

      return new GComponentTitledBorder(label, container, border);
   }


   @Override
   public final void cleanupWidget(final Layer layer,
                                   final GPair<Component, EventListener> widget) {

      setListener(null);
      unsubscribeFromEvents(layer, widget._second);
   }


   @Override
   public String getLabel() {
      return null;
   }

}
