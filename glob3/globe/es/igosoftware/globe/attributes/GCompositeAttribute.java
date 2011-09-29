

package es.igosoftware.globe.attributes;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import net.miginfocom.swing.MigLayout;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.IGlobeTranslator;
import es.igosoftware.util.GComponentTitledBorder;
import es.igosoftware.util.GPair;
import es.igosoftware.util.GSwingUtils;
import es.igosoftware.util.GTriplet;
import gov.nasa.worldwind.layers.Layer;


public abstract class GCompositeAttribute<T>
         extends
            GAbstractLayerAttribute<T> {

   private final ILayerAttribute<?>[]                                                       _children;

   private final List<GTriplet<Layer, ILayerAttribute<?>, GPair<Component, EventListener>>> _widgetsInLayerPropertiesPanel = new ArrayList<GTriplet<Layer, ILayerAttribute<?>, GPair<Component, EventListener>>>();

   private final Icon                                                                       _icon;


   protected GCompositeAttribute(final String label,
                                 final Icon icon,
                                 final String description,
                                 final String propertyName) {
      super(label, description, propertyName);

      _icon = icon;
      _children = initializeChildren();

   }


   protected abstract ILayerAttribute<?>[] initializeChildren();


   @Override
   public final String getLabel() {
      return null;
   }


   @Override
   public boolean isVisible() {
      return true;
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
   public final GPair<Component, EventListener> createWidget(final IGlobeRunningContext context,
                                                             final Layer layer) {

      final JPanel panel = new JPanel(new MigLayout("fillx, insets 0 0 0 0, gap 0 1"));
      //      final JPanel panel = new JPanel(new MigLayout("insets 0 0 0 0, gap 0 1"));
      panel.setBackground(Color.WHITE);
      panel.setBorder(createTitledBorder(context, panel));


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
            panel.add(widget._first, "growx, wrap, span 2");
         }
         else {
            panel.add(new JLabel(context.getTranslator().getTranslation(label)), "gap 3");
            panel.add(widget._first, "left, wrap");
         }
      }

      return new GPair<Component, EventListener>(panel, null);
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


}
