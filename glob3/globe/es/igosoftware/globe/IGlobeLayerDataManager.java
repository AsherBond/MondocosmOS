

package es.igosoftware.globe;

import java.util.List;

import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import gov.nasa.worldwind.layers.Layer;


public interface IGlobeLayerDataManager {


   public List<List<? extends ILayerAction>> getLayerActionsGroups(final IGlobeRunningContext context,
                                                                   final Layer layer);


   public List<List<? extends ILayerAttribute<?>>> getLayerAttributesGroups(final IGlobeRunningContext context,
                                                                            final Layer layer);


}
