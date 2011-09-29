

package es.igosoftware.globe;

import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.util.GCollections;
import gov.nasa.worldwind.layers.Layer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;


final class GGlobeLayerDataManager
         implements
            IGlobeLayerDataManager {

   private final GGlobeApplication                                            _application;
   // weak, so when the layer dies also the associated data dies
   private final WeakHashMap<Layer, List<List<? extends ILayerAction>>>       _layerActionsPerLayer    = new WeakHashMap<Layer, List<List<? extends ILayerAction>>>();
   private final WeakHashMap<Layer, List<List<? extends ILayerAttribute<?>>>> _layerAttributesPerLayer = new WeakHashMap<Layer, List<List<? extends ILayerAttribute<?>>>>();


   GGlobeLayerDataManager(final GGlobeApplication application) {
      _application = application;
   }


   @Override
   public List<List<? extends ILayerAction>> getLayerActionsGroups(final IGlobeRunningContext context,
                                                                   final Layer layer) {
      if (layer == null) {
         return Collections.emptyList();
      }

      List<List<? extends ILayerAction>> actions = _layerActionsPerLayer.get(layer);
      if (actions == null) {
         actions = calculateLayerActions(context, layer);
         _layerActionsPerLayer.put(layer, actions);
      }

      return actions;
   }


   private List<List<? extends ILayerAction>> calculateLayerActions(final IGlobeRunningContext context,
                                                                    final Layer layer) {
      final ArrayList<List<? extends ILayerAction>> result = new ArrayList<List<? extends ILayerAction>>();

      // actions from modules
      for (final IGlobeModule module : _application.getModules()) {
         if (module != null) {
            final List<? extends ILayerAction> actions = GCollections.selectNotNull(module.getLayerActions(context, layer));
            if ((actions != null) && !actions.isEmpty()) {
               result.add(actions);
            }
         }
      }

      if (layer instanceof IGlobeLayer) {
         // actions from the layer itself 
         final IGlobeLayer globeLayer = (IGlobeLayer) layer;
         final List<? extends ILayerAction> actions = GCollections.selectNotNull(globeLayer.getLayerActions(context));
         if ((actions != null) && !actions.isEmpty()) {
            result.add(actions);
         }
      }

      result.trimToSize();

      return result;
   }


   @Override
   public List<List<? extends ILayerAttribute<?>>> getLayerAttributesGroups(final IGlobeRunningContext context,
                                                                            final Layer layer) {
      if (layer == null) {
         return Collections.emptyList();
      }

      List<List<? extends ILayerAttribute<?>>> attributes = _layerAttributesPerLayer.get(layer);
      if (attributes == null) {
         attributes = calculateLayerAttributes(context, layer);
         _layerAttributesPerLayer.put(layer, attributes);
      }

      return attributes;
   }


   private List<List<? extends ILayerAttribute<?>>> calculateLayerAttributes(final IGlobeRunningContext context,
                                                                             final Layer layer) {
      final ArrayList<List<? extends ILayerAttribute<?>>> result = new ArrayList<List<? extends ILayerAttribute<?>>>();

      // attributes from modules
      for (final IGlobeModule module : _application.getModules()) {
         if (module != null) {
            final List<? extends ILayerAttribute<?>> attr = GCollections.selectNotNull(module.getLayerAttributes(context, layer));
            if ((attr != null) && !attr.isEmpty()) {
               result.add(attr);
            }
         }
      }

      if (layer instanceof IGlobeLayer) {
         final IGlobeLayer globeLayer = (IGlobeLayer) layer;

         // attributes from the rendering style
         final IGlobeSymbolizer renderingStyle = globeLayer.getSymbolizer();
         if (renderingStyle != null) {
            final List<? extends ILayerAttribute<?>> attr = GCollections.selectNotNull(renderingStyle.getLayerAttributes(context,
                     globeLayer));
            if ((attr != null) && !attr.isEmpty()) {
               result.add(attr);
            }
         }

         // attributes from the layer itself 
         final List<? extends ILayerAttribute<?>> attr = GCollections.selectNotNull(globeLayer.getLayerAttributes(context));
         if ((attr != null) && !attr.isEmpty()) {
            result.add(attr);
         }
      }

      result.trimToSize();

      return result;
   }


}
