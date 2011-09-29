

package es.igosoftware.globe;

import es.igosoftware.globe.view.customView.GView;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.terrain.SectorGeometryList;

import java.util.List;


public interface IGlobeWorldWindModel {


   public WorldWindowGLCanvas getWorldWindowGLCanvas();


   public GView getView();


   public Model getModel();


   public Globe getGlobe();


   public SectorGeometryList getTerrain();


   public List<? extends IGlobeLayer> getGlobeLayers();


   public LayerList getLayerList();


   public boolean addLayer(final Layer layer);


   public void removeLayer(final Layer layer);


}
