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
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.util.GPair;
import gov.nasa.worldwind.layers.Layer;


public abstract class GAreaLayerAttribute
         extends
            GAbstractLayerAttribute<IMeasure<GArea>> {


   private final double _stepSize;
   private final double _minimum;
   private final double _maximum;


   public GAreaLayerAttribute(final String label,
                              final String description,
                              final String propertyName,
                              final double minimum,
                              final double maximum,
                              final double stepSize) {
      this(label, description, propertyName, false, minimum, maximum, stepSize);
   }


   public GAreaLayerAttribute(final String label,
                              final String description,
                              final String propertyName,
                              final boolean readOnly,
                              final double minimum,
                              final double maximum,
                              final double stepSize) {
      super(label, description, propertyName, readOnly);

      _minimum = minimum;
      _maximum = maximum;
      _stepSize = stepSize;
   }


   @Override
   public final GPair<Component, EventListener> createWidget(final IGlobeRunningContext context,
                                                             final Layer layer) {

      final IMeasure<GArea> length = get();

      final JSpinner valueWidget = new JSpinner(new SpinnerNumberModel(length.getValue(), _minimum, _maximum, _stepSize));
      final JComboBox unitWidget = new JComboBox(GArea.values());
      unitWidget.setSelectedItem(length.getUnit());

      setTooltip(context, valueWidget);
      setTooltip(context, unitWidget);

      if (isReadOnly()) {
         valueWidget.setEnabled(false);
         unitWidget.setEnabled(false);
      }
      else {
         valueWidget.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
               final double newValue = (Double) valueWidget.getValue();
               final GArea newUnit = get().getUnit();

               final IMeasure<GArea> newRange = newUnit.value(newValue);
               set(newRange);
            }
         });

         unitWidget.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
               final double newValue = get().getValue();
               final GArea newUnit = (GArea) unitWidget.getSelectedItem();

               final IMeasure<GArea> newRange = newUnit.value(newValue);
               set(newRange);
            }
         });
      }

      setListener(new IChangeListener() {
         @Override
         public void changed() {
            final IMeasure<GArea> newLength = get();
            valueWidget.setValue(newLength.getValue());
            unitWidget.setSelectedItem(newLength.getUnit());
         }
      });


      final EventListener listener = subscribeToEvents(layer);

      final JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
      row.setBackground(Color.WHITE);
      row.setBorder(BorderFactory.createEmptyBorder());
      row.add(valueWidget);
      row.add(unitWidget);

      return new GPair<Component, EventListener>(row, listener);
   }


   @Override
   public final void cleanupWidget(final Layer layer,
                                   final GPair<Component, EventListener> widget) {
      setListener(null);

      unsubscribeFromEvents(layer, widget._second);
   }


}
