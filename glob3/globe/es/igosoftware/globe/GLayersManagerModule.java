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


package es.igosoftware.globe;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;

import es.igosoftware.globe.actions.GButtonGenericAction;
import es.igosoftware.globe.actions.GButtonLayerAction;
import es.igosoftware.globe.actions.GCheckBoxLayerAction;
import es.igosoftware.globe.actions.GGroupGenericAction;
import es.igosoftware.globe.actions.GGroupLayerAction;
import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.io.GFileName;
import es.igosoftware.util.GPair;
import es.igosoftware.util.GTriplet;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;


public class GLayersManagerModule
         extends
            GAbstractGlobeModule {

   private final boolean                                                                    _showOnlyGlobeLayers;

   private JList                                                                            _layersJList;

   private final JPanel                                                                     _layerPropertiesPanel;
   private final List<GTriplet<Layer, ILayerAttribute<?>, GPair<Component, EventListener>>> _widgetsInLayerPropertiesPanel;

   private final boolean                                                                    _autoAddSingleLayer;


   public GLayersManagerModule(final IGlobeRunningContext context) {
      this(context, true, true);
   }


   public GLayersManagerModule(final IGlobeRunningContext context,
                               final boolean autoAddSingleLayer) {
      this(context, autoAddSingleLayer, true);
   }


   public GLayersManagerModule(final IGlobeRunningContext context,
                               final boolean autoAddSingleLayer,
                               final boolean showOnlyGlobeLayers) {
      super(context);
      _layerPropertiesPanel = new JPanel(new MigLayout("fillx, insets 0 0 0 0, gap 0 1"));
      _layerPropertiesPanel.setBackground(Color.WHITE);

      _layerPropertiesPanel.putClientProperty(SubstanceLookAndFeel.COLORIZATION_FACTOR, Double.valueOf(1));

      _widgetsInLayerPropertiesPanel = new ArrayList<GTriplet<Layer, ILayerAttribute<?>, GPair<Component, EventListener>>>();

      _autoAddSingleLayer = autoAddSingleLayer;

      _showOnlyGlobeLayers = showOnlyGlobeLayers;
   }


   @Override
   public String getName() {
      return "Layers Manager";
   }


   @Override
   public String getVersion() {
      return "0.1";
   }


   @Override
   public String getDescription() {
      return "Layers Manager";
   }


   @Override
   public List<? extends IGenericAction> getGenericActions(final IGlobeRunningContext context) {
      final ArrayList<ModuleAndLayerInfo> allLayerInfos = getAllLayerInfos(context);
      if (allLayerInfos.isEmpty()) {
         return Collections.emptyList();
      }

      final IGenericAction[] children = new IGenericAction[allLayerInfos.size()];

      for (int i = 0; i < children.length; i++) {
         final ModuleAndLayerInfo moduleAndLayerInfo = allLayerInfos.get(i);
         final ILayerInfo layerInfo = moduleAndLayerInfo._layerInfo;

         children[i] = new GButtonGenericAction(layerInfo.getName(), layerInfo.getIcon(), null, false) {
            @Override
            public void execute() {
               createNewLayer(context, moduleAndLayerInfo._module, layerInfo);
            }
         };
      }

      final GGroupGenericAction addLayerMenu = new GGroupGenericAction("Add a layer", 'A',
               context.getBitmapFactory().getSmallIcon(GFileName.relative("add.png")), IGenericAction.MenuArea.FILE, true,
               children);

      return Collections.singletonList(addLayerMenu);
   }

   private static class ModuleAndLayerInfo {
      private final ILayerFactoryModule _module;
      private final ILayerInfo          _layerInfo;


      private ModuleAndLayerInfo(final ILayerFactoryModule module,
                                 final ILayerInfo layerInfo) {
         _module = module;
         _layerInfo = layerInfo;
      }


      @Override
      public String toString() {
         return _module.getName() + ": " + _layerInfo.getName();
      }
   }


   private ArrayList<ModuleAndLayerInfo> getAllLayerInfos(final IGlobeRunningContext context) {
      final ArrayList<ModuleAndLayerInfo> result = new ArrayList<ModuleAndLayerInfo>();

      for (final IGlobeModule module : context.getApplication().getModules()) {
         if (!(module instanceof ILayerFactoryModule)) {
            continue;
         }

         final ILayerFactoryModule layerFactoryModule = (ILayerFactoryModule) module;

         final List<? extends ILayerInfo> moduleLayerInfos = layerFactoryModule.getAvailableLayers(context);
         if (moduleLayerInfos != null) {
            for (final ILayerInfo moduleLayerInfo : moduleLayerInfos) {
               result.add(new ModuleAndLayerInfo(layerFactoryModule, moduleLayerInfo));
            }
         }
      }

      return result;
   }


   private void createNewLayer(final IGlobeRunningContext context,
                               final ILayerFactoryModule module,
                               final ILayerInfo layerInfo) {
      final Frame frame = context.getApplication().getFrame();
      final Cursor currentCursor = Cursor.getDefaultCursor();
      if (frame != null) {
         frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      }

      try {
         final IGlobeLayer newLayer = module.addNewLayer(context, layerInfo);

         if (newLayer != null) {
            newLayer.doDefaultAction(context);
            selectLayer(newLayer);
         }
      }
      finally {
         if (frame != null) {
            frame.setCursor(currentCursor);
         }
      }
   }


   public void selectLayer(final IGlobeLayer layer) {
      if ((_layersJList != null) && (layer != null)) {
         _layersJList.setSelectedValue(layer, true);
      }
   }


   @Override
   public List<? extends ILayerAction> getLayerActions(final IGlobeRunningContext context,
                                                       final Layer layer) {

      final ArrayList<ModuleAndLayerInfo> allLayerInfos = getAllLayerInfos(context);

      final ILayerAction toggleVisible = new GCheckBoxLayerAction("Visible", ' ', null, true, true) {
         @Override
         public boolean isVisible() {
            return (layer != null);
         }


         @Override
         public void execute() {
            layer.setEnabled(!layer.isEnabled());
         }


         @Override
         public JMenuItem createMenuWidget(final IGlobeRunningContext context1) {
            final JMenuItem widget = super.createMenuWidget(context1);

            widget.setSelected(layer.isEnabled());

            layer.addPropertyChangeListener("Enabled", new PropertyChangeListener() {
               @Override
               public void propertyChange(final PropertyChangeEvent evt) {
                  widget.setSelected((Boolean) evt.getNewValue());
               }
            });

            return widget;
         }


         @Override
         public JToggleButton createToolbarWidget(final IGlobeRunningContext context1) {
            final JToggleButton widget = super.createToolbarWidget(context1);

            widget.setSelected(layer.isEnabled());

            layer.addPropertyChangeListener("Enabled", new PropertyChangeListener() {
               @Override
               public void propertyChange(final PropertyChangeEvent evt) {
                  widget.setSelected((Boolean) evt.getNewValue());
               }
            });


            return widget;
         }
      };


      final ILayerAction addLayer;
      if (allLayerInfos.isEmpty()) {
         addLayer = null;
      }
      else if (_autoAddSingleLayer && (allLayerInfos.size() == 1)) {
         addLayer = new GButtonLayerAction("Add a layer", 'A', context.getBitmapFactory().getSmallIcon(
                  GFileName.relative("add.png")), false) {
            @Override
            public boolean isVisible() {
               return true;
            }


            @Override
            public void execute() {
               final ModuleAndLayerInfo moduleAndLayerInfo = allLayerInfos.get(0);
               createNewLayer(context, moduleAndLayerInfo._module, moduleAndLayerInfo._layerInfo);
            }
         };
      }
      else {
         final ILayerAction[] children = new ILayerAction[allLayerInfos.size()];

         for (int i = 0; i < children.length; i++) {
            final ModuleAndLayerInfo moduleAndLayerInfo = allLayerInfos.get(i);
            final ILayerInfo layerInfo = moduleAndLayerInfo._layerInfo;

            children[i] = new GButtonLayerAction(layerInfo.getName(), layerInfo.getIcon(), false) {
               @Override
               public void execute() {
                  createNewLayer(context, moduleAndLayerInfo._module, layerInfo);
               }


               @Override
               public boolean isVisible() {
                  return true;
               }
            };
         }


         addLayer = new GGroupLayerAction("Add a layer", context.getBitmapFactory().getSmallIcon(GFileName.relative("add.png")),
                  false, children) {
            @Override
            public boolean isVisible() {
               return true;
            }
         };
      }


      final ILayerAction zoomToLayer = new GButtonLayerAction("Zoom to layer", 'Z', context.getBitmapFactory().getSmallIcon(
               GFileName.relative("zoom.png")), true) {
         @Override
         public boolean isVisible() {
            return (layer != null) && (layer instanceof IGlobeLayer) && (((IGlobeLayer) layer).getExtent() != null);
         }


         @Override
         public void execute() {
            context.getCameraController().animatedZoomToSector(((IGlobeLayer) layer).getExtent());
         }
      };


      final ILayerAction removeLayer = new GButtonLayerAction("Remove layer", 'R', context.getBitmapFactory().getSmallIcon(
               GFileName.relative("remove.png")), true) {
         @Override
         public boolean isVisible() {
            return (layer != null);
         }


         @Override
         public void execute() {
            removeLayer(context, layer);
         }
      };


      final ILayerAction moveUp = new GButtonLayerAction("Move up", 'U', context.getBitmapFactory().getSmallIcon(
               GFileName.relative("up.png")), false) {
         @Override
         public boolean isVisible() {
            return (layer != null);
         }


         @Override
         public boolean isEnabled() {
            final List<? extends Layer> layers = getLayers(context);
            final int layerPosition = layers.indexOf(layer);
            return (layerPosition > 0);
         }


         @Override
         public void execute() {
            final LayerList wwLayersList = context.getWorldWindModel().getLayerList();

            final List<? extends Layer> layers = getLayers(context);
            final int layerPosition = layers.indexOf(layer);

            final Layer previousLayer = layers.get(layerPosition - 1);
            final int previousLayerIndexInWWLayerList = wwLayersList.indexOf(previousLayer);

            wwLayersList.remove(layer);
            wwLayersList.add(previousLayerIndexInWWLayerList, layer);

            if (_layersJList != null) {
               _layersJList.setSelectedIndex(layerPosition - 1);
            }
         }
      };


      final ILayerAction moveDown = new GButtonLayerAction("Move down", 'D', context.getBitmapFactory().getSmallIcon(
               GFileName.relative("down.png")), false) {
         @Override
         public boolean isVisible() {
            return (layer != null);
         }


         @Override
         public boolean isEnabled() {
            final List<? extends Layer> layers = getLayers(context);
            final int layerPosition = layers.indexOf(layer);

            return (layerPosition < (layers.size() - 1));
         }


         @Override
         public void execute() {
            final LayerList wwLayersList = context.getWorldWindModel().getLayerList();

            final List<? extends Layer> layers = getLayers(context);
            final int layerPosition = layers.indexOf(layer);

            final Layer nextLayer = layers.get(layerPosition + 1);
            final int nextLayerIndexInWWLayerList = wwLayersList.indexOf(nextLayer);

            wwLayersList.remove(layer);
            wwLayersList.add(nextLayerIndexInWWLayerList, layer);

            if (_layersJList != null) {
               _layersJList.setSelectedIndex(layerPosition + 1);
            }
         }
      };

      return Arrays.asList(addLayer, toggleVisible, zoomToLayer, removeLayer, moveUp, moveDown);
   }


   @Override
   public List<GPair<String, Component>> getPanels(final IGlobeRunningContext context) {
      final ArrayList<GPair<String, Component>> panels = new ArrayList<GPair<String, Component>>();

      panels.add(new GPair<String, Component>("Layers", createLayersPanel(context)));

      return panels;
   }


   private Component createLayersPanel(final IGlobeRunningContext context) {
      final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
      splitPane.setBorder(BorderFactory.createEmptyBorder());
      splitPane.setContinuousLayout(true);
      splitPane.setOneTouchExpandable(true);
      splitPane.setDividerLocation(Math.round(context.getApplication().initialDimension().height / 6f)
                                   + splitPane.getInsets().left);

      splitPane.setTopComponent(wrapInJScrollPane(createLayersJList(context)));
      splitPane.setBottomComponent(wrapInJScrollPane(_layerPropertiesPanel));

      return splitPane;
   }


   private JScrollPane wrapInJScrollPane(final Component component) {
      final JScrollPane scrollPane = new JScrollPane(component);
      scrollPane.setBorder(BorderFactory.createEmptyBorder());
      return scrollPane;
   }


   private static class GlobeLayersListModel
            extends
               AbstractListModel {
      private static final long           serialVersionUID = 1L;

      private final List<? extends Layer> _globeLayers;


      private GlobeLayersListModel(final List<? extends Layer> globeLayers) {
         _globeLayers = globeLayers;
      }


      @Override
      public Layer getElementAt(final int index) {
         return _globeLayers.get(index);
      }


      @Override
      public int getSize() {
         return _globeLayers.size();
      }
   }


   private static class LayerRenderer
            extends
               DefaultListCellRenderer {
      private static final long          serialVersionUID = 1L;

      private final IGlobeRunningContext _context;


      private LayerRenderer(final IGlobeRunningContext context) {
         _context = context;
      }


      @Override
      public Component getListCellRendererComponent(final JList list,
                                                    final Object value,
                                                    final int index,
                                                    final boolean isSelected,
                                                    final boolean hasFocus) {
         final Layer layer = (Layer) value;

         final JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
         label.setIcon(getLayerIcon(layer));
         label.setText(layer.getName());

         return label;
      }


      private Icon getLayerIcon(final Layer layer) {
         if (layer instanceof IGlobeLayer) {
            return ((IGlobeLayer) layer).getIcon(_context);
         }

         return _context.getBitmapFactory().getSmallIcon(GFileName.relative("non-globe-layer.png"));
      }
   }


   private JList createLayersJList(final IGlobeRunningContext context) {

      final LayerList layerList = context.getWorldWindModel().getLayerList();


      _layersJList = new JList(new GlobeLayersListModel(getLayers(context)));
      _layersJList.setBackground(Color.WHITE);
      _layersJList.putClientProperty(SubstanceLookAndFeel.COLORIZATION_FACTOR, Double.valueOf(1));


      layerList.addPropertyChangeListener(AVKey.LAYERS, new PropertyChangeListener() {
         @Override
         public void propertyChange(final PropertyChangeEvent evt) {
            _layersJList.setModel(new GlobeLayersListModel(getLayers(context)));
         }
      });

      _layersJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      _layersJList.setBorder(BorderFactory.createEmptyBorder());

      _layersJList.setCellRenderer(new LayerRenderer(context));

      _layersJList.addListSelectionListener(new ListSelectionListener() {
         @Override
         public void valueChanged(final ListSelectionEvent e) {
            if (e.getValueIsAdjusting() == false) {
               final List<? extends Layer> layers = ((GlobeLayersListModel) _layersJList.getModel())._globeLayers;

               final Layer selectedLayer;
               final int layerPosition = _layersJList.getSelectedIndex();
               if (layerPosition == -1) {
                  selectedLayer = null;
               }
               else {
                  selectedLayer = layers.get(layerPosition);
               }
               changedSelectedLayer(context, selectedLayer);
            }
         }
      });


      _layersJList.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(final MouseEvent mouseEvent) {
            final int button = mouseEvent.getButton();
            final int clickCount = mouseEvent.getClickCount();

            // right click
            if ((button == MouseEvent.BUTTON3) && (clickCount == 1)) {
               final int layerPosition = selectBasedOnMousePosition(mouseEvent);

               final List<? extends Layer> layers = ((GlobeLayersListModel) _layersJList.getModel())._globeLayers;
               final Layer layer = (layerPosition < 0) ? null : layers.get(layerPosition);

               popupContextMenu(context, layer, _layersJList, mouseEvent);
            }

            // double click
            if ((button == MouseEvent.BUTTON1) && (clickCount == 2)) {
               final int layerPosition = selectBasedOnMousePosition(mouseEvent);

               final List<? extends Layer> layers = ((GlobeLayersListModel) _layersJList.getModel())._globeLayers;

               if (layerPosition < 0) {
                  return;
               }

               final Layer layer = layers.get(layerPosition);
               if (layer instanceof IGlobeLayer) {
                  ((IGlobeLayer) layer).doDefaultAction(context);
               }
            }
         }


         private int selectBasedOnMousePosition(final MouseEvent mouseEvent) {
            final int layerPosition = _layersJList.locationToIndex(mouseEvent.getPoint());

            if (layerPosition != _layersJList.getSelectedIndex()) {
               _layersJList.setSelectedIndex(layerPosition);
            }

            return layerPosition;
         }
      });

      return _layersJList;
   }


   private List<? extends Layer> getLayers(final IGlobeRunningContext context) {
      return _showOnlyGlobeLayers ? context.getWorldWindModel().getGlobeLayers() : context.getWorldWindModel().getLayerList();
   }


   protected void popupContextMenu(final IGlobeRunningContext context,
                                   final Layer layer,
                                   final JList list,
                                   final MouseEvent event) {
      final JPopupMenu menu = new JPopupMenu();
      menu.setLightWeightPopupEnabled(false);

      final List<List<? extends ILayerAction>> layerActionsGroups = context.getLayerDataManager().getLayerActionsGroups(context,
               layer);
      createLayerActionsMenuItems(context, menu, layerActionsGroups);

      final Point pt = SwingUtilities.convertPoint(event.getComponent(), event.getPoint(), list);
      menu.show(list, pt.x, pt.y);
   }


   private void createLayerActionsMenuItems(final IGlobeRunningContext context,
                                            final JPopupMenu menu,
                                            final List<List<? extends ILayerAction>> layersActionsGroups) {

      for (final List<? extends ILayerAction> layerActions : layersActionsGroups) {
         if (layerActions == null) {
            continue;
         }

         boolean firstActionOnMenu = true;
         for (final ILayerAction layerAction : layerActions) {
            if (layerAction.isVisible()) {
               final JMenuItem menuItem = layerAction.createMenuWidget(context);

               if (firstActionOnMenu) {
                  firstActionOnMenu = false;
                  if (menu.getComponents().length > 0) {
                     menu.addSeparator();
                  }
               }

               menu.add(menuItem);
            }
         }
      }
   }


   protected void changedSelectedLayer(final IGlobeRunningContext context,
                                       final Layer selectedLayer) {
      cleanLayerPropertiesPanel();

      if (selectedLayer != null) {
         createLayerPropertiesWidgets(context, selectedLayer);
      }

      // force redraw
      _layerPropertiesPanel.invalidate();
      _layerPropertiesPanel.validate();
      _layerPropertiesPanel.repaint();

      _layerPropertiesPanel.requestFocus();
      _layerPropertiesPanel.requestFocusInWindow();
   }


   private void cleanLayerPropertiesPanel() {
      _layerPropertiesPanel.removeAll();

      for (final GTriplet<Layer, ILayerAttribute<?>, GPair<Component, EventListener>> layerAttributeAndWidget : _widgetsInLayerPropertiesPanel) {
         final Layer layer = layerAttributeAndWidget._first;
         final ILayerAttribute<?> attribute = layerAttributeAndWidget._second;
         final GPair<Component, EventListener> widget = layerAttributeAndWidget._third;

         attribute.cleanupWidget(layer, widget);
      }
      _widgetsInLayerPropertiesPanel.clear();
   }


   private void createLayerPropertiesWidgets(final IGlobeRunningContext context,
                                             final Layer layer) {
      final JToolBar toolbar = new JToolBar();
      toolbar.setRollover(true);
      toolbar.setBorder(BorderFactory.createEmptyBorder());
      toolbar.setFloatable(false);
      final IGlobeLayerDataManager layerDataManager = context.getLayerDataManager();

      createLayerActionsToolbarItems(context, toolbar, layerDataManager.getLayerActionsGroups(context, layer));

      if (toolbar.getComponentCount() > 0) {
         _layerPropertiesPanel.add(toolbar, "growx, wrap, span 2");
      }

      createAttributesWidgets(context, layer, layerDataManager.getLayerAttributesGroups(context, layer));
   }


   private void createLayerActionsToolbarItems(final IGlobeRunningContext context,
                                               final JToolBar toolbar,
                                               final List<List<? extends ILayerAction>> layersActionsGroups) {
      for (final List<? extends ILayerAction> layerActions : layersActionsGroups) {

         if (layerActions == null) {
            continue;
         }

         for (final ILayerAction layerAction : layerActions) {
            if (layerAction.isShowOnToolBar() && layerAction.isVisible()) {
               final Component widget = layerAction.createToolbarWidget(context);
               if (widget != null) {
                  toolbar.add(widget);
               }
            }
         }
      }
   }


   private void createAttributesWidgets(final IGlobeRunningContext context,
                                        final Layer layer,
                                        final List<List<? extends ILayerAttribute<?>>> layerAttributesGroups) {

      for (final List<? extends ILayerAttribute<?>> layerAttributes : layerAttributesGroups) {
         if (layerAttributes == null) {
            continue;
         }

         boolean firstAttributeOnPanel = true;

         for (final ILayerAttribute<?> attribute : layerAttributes) {
            if (!attribute.isVisible()) {
               continue;
            }

            final GPair<Component, EventListener> widget = attribute.createWidget(context, layer);
            if (widget == null) {
               continue;
            }

            if (firstAttributeOnPanel) {
               firstAttributeOnPanel = false;
               final Component[] components = _layerPropertiesPanel.getComponents();
               if ((components.length > 0) && (!(components[components.length - 1] instanceof JToolBar))
                   && (!(components[components.length - 1] instanceof JPanel))) {
                  _layerPropertiesPanel.add(new JSeparator(SwingConstants.HORIZONTAL), "growx, wrap, span 2");
               }
            }

            _widgetsInLayerPropertiesPanel.add(new GTriplet<Layer, ILayerAttribute<?>, GPair<Component, EventListener>>(layer,
                     attribute, widget));
            final String label = attribute.getLabel();
            if (label == null) {
               //            _layerPropertiesPanel.add(widget._first, "wrap, gap 3, span 2");
               _layerPropertiesPanel.add(widget._first, "growx, wrap, span 2");
            }
            else {
               final IGlobeTranslator translator = context.getTranslator();
               final JLabel labelWidget = new JLabel(translator.getTranslation(label));
               _layerPropertiesPanel.add(labelWidget, "gap 3");
               if (attribute.getDescription() != null) {
                  labelWidget.setToolTipText(translator.getTranslation(attribute.getDescription()));
               }
               _layerPropertiesPanel.add(widget._first, "left, wrap");
            }
         }
      }
   }


   @Override
   public List<? extends ILayerAttribute<?>> getLayerAttributes(final IGlobeRunningContext context,
                                                                final Layer layer) {
      //      final GBooleanLayerAttribute visible = new GBooleanLayerAttribute("Visible", "Make the layer visible/invisible", "Enabled") {
      //         @Override
      //         public boolean isVisible() {
      //            return true;
      //         }
      //
      //
      //         @Override
      //         public Boolean get() {
      //            return layer.isEnabled();
      //         }
      //
      //
      //         @Override
      //         public void set(final Boolean value) {
      //            layer.setEnabled(value);
      //         }
      //      };
      //
      //
      //      return Arrays.asList(visible);

      return null;
   }


   @Override
   public void initializeTranslations(final IGlobeRunningContext context) {
      final IGlobeTranslator translator = context.getTranslator();
      translator.addTranslation("es", "Layers", "Capas");
      translator.addTranslation("es", "Add a layer", "Agregar una capa");
      translator.addTranslation("es", "Zoom to layer", "Zoom a la capa");
      translator.addTranslation("es", "Remove layer", "Remover la capa");
      translator.addTranslation("es", "Move up", "Mover hacia arriba");
      translator.addTranslation("es", "Move down", "Mover hacia abajo");
      translator.addTranslation("es", "Visible", "Visible");
      translator.addTranslation("es", "CRS", "CRS");
      translator.addTranslation("es", "Select a layer", "Elija una capa");
   }


   private void removeLayer(final IGlobeRunningContext context,
                            final Layer layer) {

      final IGlobeApplication application = context.getApplication();
      final IGlobeTranslator translator = context.getTranslator();

      final String[] options = {
                        translator.getTranslation("Yes"),
                        translator.getTranslation("No")
      };
      final String title = translator.getTranslation("Layer: ") + layer.getName();
      final String message = translator.getTranslation("Are you sure to remove the layer?");

      final int answer = JOptionPane.showOptionDialog(application.getFrame(), message, title, JOptionPane.YES_NO_OPTION,
               JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

      if (answer == 0) {
         context.getWorldWindModel().removeLayer(layer);
      }
   }


}
