

package es.igosoftware.globe;

import es.igosoftware.globe.view.customView.GView;
import es.igosoftware.util.GAssert;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.terrain.SectorGeometryList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


final class GGlobeWorldWindModel
         implements
            IGlobeWorldWindModel {

   private final WorldWindowGLCanvas _wwGLCanvas;


   GGlobeWorldWindModel(final WorldWindowGLCanvas wwGLCanvas) {
      _wwGLCanvas = wwGLCanvas;
   }


   @Override
   public WorldWindowGLCanvas getWorldWindowGLCanvas() {
      return _wwGLCanvas;
   }


   @Override
   public Model getModel() {
      return getWorldWindowGLCanvas().getModel();
   }


   @Override
   public GView getView() {
      final View view = getWorldWindowGLCanvas().getView();
      if (view instanceof GView) {
         return (GView) view;
      }
      throw new RuntimeException("Invalid view class: " + view.getClass() + ".  Only " + GView.class + " is supported");
   }


   @Override
   public Globe getGlobe() {
      return getModel().getGlobe();
   }


   @Override
   public List<? extends IGlobeLayer> getGlobeLayers() {
      final List<IGlobeLayer> result = new ArrayList<IGlobeLayer>();

      final LayerList layerList = getLayerList();
      for (int i = 0; i < layerList.size(); i++) {
         final Layer candidate = layerList.get(i);
         if (candidate instanceof IGlobeLayer) {
            final IGlobeLayer globeLayer = (IGlobeLayer) candidate;
            result.add(globeLayer);
         }
      }

      return Collections.unmodifiableList(result);
   }


   @Override
   public SectorGeometryList getTerrain() {
      return getWorldWindowGLCanvas().getSceneController().getTerrain();
   }


   @Override
   public LayerList getLayerList() {
      return getModel().getLayers();
   }


   @Override
   public boolean addLayer(final Layer layer) {
      GAssert.notNull(layer, "layer");

      return getLayerList().add(layer);
   }


   @Override
   public void removeLayer(final Layer layer) {
      GAssert.notNull(layer, "layer");

      getLayerList().remove(layer);
   }


}
