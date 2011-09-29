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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EventListener;

import javax.swing.JComponent;

import es.igosoftware.globe.GGlobeComponent;
import es.igosoftware.globe.IGlobeRunningContext;
import gov.nasa.worldwind.layers.Layer;


public abstract class GAbstractLayerAttribute<T>
         extends
            GGlobeComponent
         implements
            ILayerAttribute<T> {


   protected final String                  _label;
   protected final String                  _description;
   protected final boolean                 _readOnly;
   protected final String                  _propertyName;

   private ILayerAttribute.IChangeListener _listener;


   protected GAbstractLayerAttribute(final String label,
                                     final String description) {
      this(label, description, null, false);
   }


   protected GAbstractLayerAttribute(final String label,
                                     final String description,
                                     final boolean readOnly) {
      this(label, description, null, readOnly);
   }


   protected GAbstractLayerAttribute(final String label,
                                     final String description,
                                     final String propertyName) {
      this(label, description, propertyName, false);
   }


   protected GAbstractLayerAttribute(final String label,
                                     final String description,
                                     final String propertyName,
                                     final boolean readOnly) {
      //      GAssert.notNull(label, "label");

      _label = label;
      _description = description;
      _propertyName = propertyName;
      _readOnly = readOnly;
   }


   @Override
   public String getLabel() {
      return _label;
   }


   @Override
   public boolean isReadOnly() {
      return _readOnly;
   }


   @Override
   public final void setListener(final ILayerAttribute.IChangeListener listener) {
      if ((_listener != null) && (listener != null)) {
         throw new IllegalArgumentException("Listener already set");
      }

      _listener = listener;
   }


   @Override
   public final void changed() {
      if (_listener != null) {
         _listener.changed();
      }
   }


   protected final String getPropertyName() {
      return _propertyName;
   }


   protected final EventListener subscribeToEvents(final Layer layer) {
      final String propertyName = getPropertyName();
      if (propertyName == null) {
         return null;
      }

      final PropertyChangeListener listener = new PropertyChangeListener() {
         @Override
         public void propertyChange(final PropertyChangeEvent evt) {
            changed();
         }
      };
      layer.addPropertyChangeListener(propertyName, listener);
      return listener;
   }


   protected final void unsubscribeFromEvents(final Layer layer,
                                              final EventListener listener) {
      final String propertyName = getPropertyName();
      if ((listener == null) || (propertyName == null)) {
         return;
      }

      layer.removePropertyChangeListener(propertyName, (PropertyChangeListener) listener);
   }


   @Override
   public String getDescription() {
      return _description;
   }


   protected void setTooltip(final IGlobeRunningContext context,
                             final JComponent widget) {
      if (_description != null) {
         widget.setToolTipText(context.getTranslator().getTranslation(_description));
      }
   }

}
