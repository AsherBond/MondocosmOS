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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;

import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.IGlobeTranslator;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GComponentTitledBorder;
import es.igosoftware.util.GPair;
import es.igosoftware.util.GPredicate;
import es.igosoftware.util.GSwingUtils;
import es.igosoftware.util.GTriplet;
import gov.nasa.worldwind.layers.Layer;


public class GTabbedGroupAttribute
         implements
            ILayerAttribute<Object> {


   private final String                                                                     _label;
   private final Icon                                                                       _icon;
   private final String                                                                     _description;
   private final List<ILayerAttribute<?>>                                                   _children;
   private final List<GTriplet<Layer, ILayerAttribute<?>, GPair<Component, EventListener>>> _widgetsInLayerPropertiesPanel = new ArrayList<GTriplet<Layer, ILayerAttribute<?>, GPair<Component, EventListener>>>();


   public GTabbedGroupAttribute(final String label,
                                final String description,
                                final ILayerAttribute<?>... children) {
      this(label, null, description, children);
   }


   public GTabbedGroupAttribute(final String label,
                                final Icon icon,
                                final String description,
                                final ILayerAttribute<?>... children) {
      _label = label;
      _icon = icon;
      _description = description;
      _children = Arrays.asList(children);
   }


   public GTabbedGroupAttribute(final String label,
                                final String description,
                                final List<? extends ILayerAttribute<?>> children) {
      this(label, null, description, children);
   }


   public GTabbedGroupAttribute(final String label,
                                final Icon icon,
                                final String description,
                                final List<? extends ILayerAttribute<?>> children) {
      _label = label;
      _icon = icon;
      _description = description;
      _children = new ArrayList<ILayerAttribute<?>>(children);
   }


   @Override
   public boolean isVisible() {
      return GCollections.allSatisfy(_children, new GPredicate<ILayerAttribute<?>>() {
         @Override
         public boolean evaluate(final ILayerAttribute<?> element) {
            return element.isVisible();
         }
      });
   }


   @Override
   public final String getLabel() {
      return null;
   }


   @Override
   public final String getDescription() {
      return _description;
   }


   @Override
   public final GPair<Component, EventListener> createWidget(final IGlobeRunningContext context,
                                                             final Layer layer) {

      //final JPanel panel = new JPanel(new MigLayout("fillx, insets 0 0 0 0, gap 0 1"));
      final JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      panel.setBackground(Color.WHITE);
      panel.setBorder(createTitledBorder(context, panel));
      final JTabbedPane tabbedPanel = new JTabbedPane();
      panel.add(tabbedPanel);


      for (final ILayerAttribute<?> attribute : _children) {
         if (!attribute.isVisible()) {
            continue;
         }

         final GPair<Component, EventListener> widget = attribute.createWidget(context, layer);
         if (widget == null) {
            continue;
         }

         _widgetsInLayerPropertiesPanel.add(new GTriplet<Layer, ILayerAttribute<?>, GPair<Component, EventListener>>(layer,
                  attribute, widget));

         final String label = attribute.getLabel();
         if (label == null) {
            tabbedPanel.addTab(null, null, widget._first, null);
         }
         else {
            tabbedPanel.addTab(label, null, widget._first, null);
         }
      }

      return new GPair<Component, EventListener>(panel, null);
   }


   private Border createTitledBorder(final IGlobeRunningContext context,
                                     final JComponent container) {
      final IGlobeTranslator translator = context.getTranslator();

      final JLabel l;
      if (_icon == null) {
         l = new JLabel(" " + translator.getTranslation(_label) + " ");
      }
      else {
         l = new JLabel(translator.getTranslation(_label) + " ");
         l.setIcon(_icon);
      }
      final JLabel label = GSwingUtils.makeBold(l);
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
   public final void cleanupWidget(final Layer layer2,
                                   final GPair<Component, EventListener> widget2) {
      for (final GTriplet<Layer, ILayerAttribute<?>, GPair<Component, EventListener>> layerAttributeAndWidget : _widgetsInLayerPropertiesPanel) {
         final Layer layer = layerAttributeAndWidget._first;
         final ILayerAttribute<?> attribute = layerAttributeAndWidget._second;
         final GPair<Component, EventListener> widget = layerAttributeAndWidget._third;

         attribute.cleanupWidget(layer, widget);
      }
      _widgetsInLayerPropertiesPanel.clear();
   }


   @Override
   public final Object get() {
      return null;
   }


   @Override
   public final void set(final Object value) {

   }


   @Override
   public final boolean isReadOnly() {
      return true;
   }


   @Override
   public final void setListener(final ILayerAttribute.IChangeListener listener) {

   }


   @Override
   public final void changed() {

   }


}
