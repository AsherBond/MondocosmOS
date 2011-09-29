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

import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.util.GMath;
import es.igosoftware.util.GPair;
import gov.nasa.worldwind.layers.Layer;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.EventListener;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public abstract class GFloatLayerAttribute
         extends
            GAbstractLayerAttribute<Float> {


   public static enum WidgetType {
      SPINNER,
      SLIDER,
      TEXTBOX;
   }


   private final float      _minimum;
   private final float      _maximum;
   private final float      _stepSize;
   private final WidgetType _widgetType;


   public GFloatLayerAttribute(final String label,
                               final String description,
                               final String propertyName,
                               final float minimum,
                               final float maximum,
                               final GFloatLayerAttribute.WidgetType widgetType,
                               final float stepSize) {
      super(label, description, propertyName);
      _minimum = minimum;
      _maximum = maximum;
      _stepSize = stepSize;
      _widgetType = widgetType;
   }


   public GFloatLayerAttribute(final String label,
                               final String description,
                               final String propertyName,
                               final boolean readOnly,
                               final float minimum,
                               final float maximum,
                               final GFloatLayerAttribute.WidgetType widgetType,
                               final float stepSize) {
      super(label, description, propertyName, readOnly);
      _minimum = minimum;
      _maximum = maximum;
      _stepSize = stepSize;
      _widgetType = widgetType;
   }


   @Override
   public final void cleanupWidget(final Layer layer,
                                   final GPair<Component, EventListener> widget) {
      setListener(null);

      unsubscribeFromEvents(layer, widget._second);
   }


   @Override
   public final GPair<Component, EventListener> createWidget(final IGlobeRunningContext context,
                                                             final Layer layer) {

      final JComponent widget;
      switch (_widgetType) {
         case SLIDER:
            widget = createSlider();
            break;

         case SPINNER:
            widget = createSpinner();
            break;

         case TEXTBOX:
            widget = createTextBox();
            break;

         default:
            return null;
      }

      setTooltip(context, widget);

      final EventListener listener = subscribeToEvents(layer);
      return new GPair<Component, EventListener>(widget, listener);
   }


   private JTextField createTextBox() {
      final JTextField text = new JTextField();
      text.setMinimumSize(new Dimension(100, 20));
      text.setText(get().toString());

      if (isReadOnly()) {
         text.setEnabled(false);
      }
      else {
         text.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(final FocusEvent e) {
               final JTextField textField = (JTextField) e.getSource();
               final String content = textField.getText();
               if (content.length() != 0) {
                  try {
                     final float f = Float.parseFloat(content);
                     if (f > _maximum) {
                        textField.setText(Float.toString(_maximum));
                     }
                     if (f < _minimum) {
                        textField.setText(Float.toString(_minimum));
                     }
                     set(GMath.roundTo(Float.parseFloat(textField.getText()), _stepSize));
                  }
                  catch (final NumberFormatException nfe) {
                     Toolkit.getDefaultToolkit().beep();
                     textField.requestFocus();
                  }
               }
            }
         });
      }

      setListener(new IChangeListener() {
         @Override
         public void changed() {
            text.setText(get().toString());
         }
      });

      return text;
   }


   private JSlider createSlider() {

      final int intMin = toInt(_minimum, _stepSize);
      final int intMax = toInt(_maximum, _stepSize);
      final int intValue = toInt(get(), _stepSize);

      final JSlider slider = new JSlider(SwingConstants.HORIZONTAL, intMin, intMax, intValue) {
         private static final long serialVersionUID = 1L;


         @Override
         public Dimension getPreferredSize() {
            final Dimension superPreferredSize = super.getPreferredSize();
            return new Dimension(superPreferredSize.width / 2, superPreferredSize.height * 2 / 3);
            // return new Dimension(superPreferredSize.width * 2 / 3, superPreferredSize.height * 2 / 3);
         }
      };

      slider.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
      final int intRange = intMax - intMin;
      final int majorTickSpacing = GMath.integerDivisionBy(intRange, new int[] {
                        5,
                        4,
                        3
      }, intRange / 2);
      final int minorTickSpacing = GMath.integerDivisionBy(majorTickSpacing, new int[] {
                        4,
                        3,
                        2
      }, 1);
      slider.setMajorTickSpacing(majorTickSpacing);
      slider.setMinorTickSpacing(minorTickSpacing);
      slider.setPaintTicks(true);
      slider.setPaintLabels(false);
      slider.setSnapToTicks(false);


      if (isReadOnly()) {
         slider.setEnabled(false);
      }
      else {
         slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
               if (slider.getValueIsAdjusting()) {
                  return;
               }

               final float floatValue = toFloat(slider.getValue(), _stepSize);
               set(GMath.roundTo(floatValue, _stepSize));
            }
         });
      }

      setListener(new IChangeListener() {
         @Override
         public void changed() {
            slider.setValue(toInt(get(), _stepSize));
         }
      });

      return slider;
   }


   private static int toInt(final float value,
                            final float stepSize) {
      return (int) (value / stepSize);
   }


   private static float toFloat(final int value,
                                final float stepSize) {
      return (value * stepSize);
   }


   private JSpinner createSpinner() {
      final SpinnerNumberModel model = new SpinnerNumberModel(get(), Float.valueOf(_minimum), Float.valueOf(_maximum),
               Float.valueOf(_stepSize));

      final JSpinner spinner = new JSpinner(model);

      if (isReadOnly()) {
         spinner.setEnabled(false);
      }
      else {
         spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
               set(GMath.roundTo((Float) spinner.getValue(), _stepSize));
            }
         });
      }

      setListener(new IChangeListener() {
         @Override
         public void changed() {
            spinner.setValue(get());
         }
      });

      return spinner;
   }


}
