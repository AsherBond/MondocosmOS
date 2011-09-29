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
import java.util.EventListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.util.GPair;
import es.igosoftware.util.GRange;
import gov.nasa.worldwind.layers.Layer;


public abstract class GRangeLayerAttribute<T extends Number & Comparable<T>>
         extends
            GAbstractLayerAttribute<GRange<T>> {


   private final GRange<T> _minimumMaximum;
   private final T         _stepSize;


   public GRangeLayerAttribute(final String label,
                               final String description,
                               final String propertyName,
                               final boolean readOnly,
                               final GRange<T> minimumMaximum,
                               final T stepSize) {
      super(label, description, propertyName, readOnly);

      _minimumMaximum = minimumMaximum;
      _stepSize = stepSize;
   }


   @Override
   public final GPair<Component, EventListener> createWidget(final IGlobeRunningContext context,
                                                             final Layer layer) {

      final GRange<T> value = get();

      final JSpinner fromWidget = new JSpinner(new SpinnerNumberModel(value._lower, _minimumMaximum._lower,
               _minimumMaximum._upper, _stepSize));
      final JSpinner toWidget = new JSpinner(new SpinnerNumberModel(value._upper, _minimumMaximum._lower, _minimumMaximum._upper,
               _stepSize));

      setTooltip(context, fromWidget);
      setTooltip(context, toWidget);

      if (isReadOnly()) {
         fromWidget.setEnabled(false);
         toWidget.setEnabled(false);
      }
      else {
         fromWidget.addChangeListener(new ChangeListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void stateChanged(final ChangeEvent e) {
               final T from = (T) fromWidget.getValue();
               T to = get()._upper;

               if (from.compareTo(to) > 0) {
                  to = from;
               }

               final GRange<T> newRange = new GRange<T>(from, to);
               set(newRange);
            }
         });

         toWidget.addChangeListener(new ChangeListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void stateChanged(final ChangeEvent e) {
               T from = get()._lower;
               final T to = (T) toWidget.getValue();

               if (to.compareTo(from) < 0) {
                  from = to;
               }

               final GRange<T> newRange = new GRange<T>(from, to);
               set(newRange);
            }
         });
      }

      setListener(new IChangeListener() {
         @Override
         public void changed() {
            final GRange<T> range = get();
            fromWidget.setValue(range._lower);
            toWidget.setValue(range._upper);
         }
      });


      final EventListener listener = subscribeToEvents(layer);

      final JPanel row = new JPanel(new FlowLayout());
      row.setBackground(Color.WHITE);
      row.setBorder(BorderFactory.createEmptyBorder());
      row.add(fromWidget);
      row.add(new JLabel("-"));
      row.add(toWidget);

      return new GPair<Component, EventListener>(row, listener);
   }


   @Override
   public final void cleanupWidget(final Layer layer,
                                   final GPair<Component, EventListener> widget) {
      setListener(null);

      unsubscribeFromEvents(layer, widget._second);
   }


}
