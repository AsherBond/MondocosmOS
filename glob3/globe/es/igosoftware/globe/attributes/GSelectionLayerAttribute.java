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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.EventListener;

import javax.swing.JComboBox;

import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GPair;
import gov.nasa.worldwind.layers.Layer;


public abstract class GSelectionLayerAttribute<T>
         extends
            GAbstractLayerAttribute<T> {


   private final T[] _options;


   public GSelectionLayerAttribute(final String label,
                                   final String description,
                                   final String propertyName,
                                   final T[] options) {
      this(label, description, propertyName, false, options);
   }


   public GSelectionLayerAttribute(final String label,
                                   final String description,
                                   final String propertyName,
                                   final boolean readOnly,
                                   final T[] options) {
      super(label, description, propertyName, readOnly);

      GAssert.notEmpty(options, "options");

      _options = Arrays.copyOf(options, options.length); // copy to avoid external modifications
   }


   @Override
   public final GPair<Component, EventListener> createWidget(final IGlobeRunningContext context,
                                                             final Layer layer) {
      final JComboBox widget = new JComboBox(_options);
      setTooltip(context, widget);

      widget.setSelectedItem(get());
      if (isReadOnly()) {
         widget.setEnabled(false);
      }
      else {
         widget.addActionListener(new ActionListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void actionPerformed(final ActionEvent e) {
               set((T) widget.getSelectedItem());
               //widget.setBackground(newColor);
            }
         });
      }

      setListener(new IChangeListener() {
         @Override
         public void changed() {
            //widget.setBackground(get(layer));
         }
      });

      final EventListener listener = subscribeToEvents(layer);

      return new GPair<Component, EventListener>(widget, listener);
   }


   @Override
   public final void cleanupWidget(final Layer layer,
                                   final GPair<Component, EventListener> widget) {
      setListener(null);

      unsubscribeFromEvents(layer, widget._second);
   }
}
