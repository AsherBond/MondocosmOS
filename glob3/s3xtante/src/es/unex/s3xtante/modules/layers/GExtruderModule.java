

package es.unex.s3xtante.modules.layers;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.actions.GButtonGenericAction;
import es.igosoftware.globe.actions.GGenericAction;
import es.igosoftware.globe.actions.IGenericAction;
import es.unex.sextante.dataObjects.IFeature;
import es.unex.sextante.dataObjects.IFeatureIterator;
import es.unex.sextante.dataObjects.IVectorLayer;
import es.unex.sextante.exceptions.IteratorException;
import es.unex.sextante.gui.core.SextanteGUI;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.ExtrudedPolygon;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;


public class GExtruderModule
         extends
            GAbstractGlobeModule {


   private static final double SMIDGEN = 0.00000001;


   private double              _min;
   private double              _max;


   public GExtruderModule(final IGlobeRunningContext context) {
      super(context);
   }


   @Override
   public String getDescription() {
      return "Extruder";
   }


   @Override
   public List<? extends IGenericAction> getGenericActions(final IGlobeRunningContext context) {
      final GGenericAction extruder = new GButtonGenericAction("Extruder", 'X', null, IGenericAction.MenuArea.EDIT, false) {
         @Override
         public void execute() {
            extrude(context);
         }
      };

      return Arrays.asList(extruder);
   }


   protected void extrude(final IGlobeRunningContext context) {

      SextanteGUI.getInputFactory().createDataObjects();
      final IVectorLayer[] layers = SextanteGUI.getInputFactory().getVectorLayers(IVectorLayer.SHAPE_TYPE_POLYGON);
      final IVectorLayer[] layers2 = SextanteGUI.getInputFactory().getVectorLayers(IVectorLayer.SHAPE_TYPE_LINE);
      final IVectorLayer[] allLayers = new IVectorLayer[layers.length + layers2.length];
      System.arraycopy(layers, 0, allLayers, 0, layers.length);
      System.arraycopy(layers2, 0, allLayers, layers.length, layers2.length);
      if (layers.length != 0) {
         final IVectorLayer layer = (IVectorLayer) JOptionPane.showInputDialog(null, "Layer to extrude", "Extrude",
                  JOptionPane.PLAIN_MESSAGE, null, allLayers, allLayers[0]);
         if (layer != null) {
            createExtrudedLayer(layer, context);
         }
      }


   }


   private void createExtrudedLayer(final IVectorLayer layer,
                                    final IGlobeRunningContext context) {

      final int heightField = layer.getFieldIndexByName("HEIGHT");
      if (heightField == -1) {
         return;
      }

      final int hoursField = layer.getFieldIndexByName("TIME");

      if (hoursField != -1) {
         calculateColorRamp(layer, hoursField);
      }


      final RenderableLayer renderableLayer = new RenderableExtrudedLayer();
      final ArrayList<LatLon> pathLocations = new ArrayList<LatLon>();
      final IFeatureIterator iter = layer.iterator();
      ExtrudedPolygon pgon;
      while (iter.hasNext()) {
         pathLocations.clear();
         IFeature feature;
         try {
            feature = iter.next();
            final Geometry geom = feature.getGeometry();
            Coordinate[] coords = null;
            if (layer.getShapeType() == IVectorLayer.SHAPE_TYPE_LINE) {
               coords = new Coordinate[4];
               final Coordinate[] geomCoords = geom.getCoordinates();
               coords[0] = geomCoords[0];
               coords[1] = geomCoords[1];
               coords[2] = new Coordinate(geomCoords[1].x + SMIDGEN, geomCoords[1].y + SMIDGEN);
               coords[3] = new Coordinate(geomCoords[0].x + SMIDGEN, geomCoords[0].y + SMIDGEN);
            }
            else {
               coords = geom.getCoordinates();
            }
            double dHeight;
            try {
               dHeight = Double.parseDouble(feature.getRecord().getValue(heightField).toString());
            }
            catch (final NumberFormatException e) {
               dHeight = 0;
            }

            final ShapeAttributes attributes = new BasicShapeAttributes();
            Material material;
            if (hoursField != -1) {
               material = new Material(getColorFromValue(Double.parseDouble(feature.getRecord().getValue(hoursField).toString())));
            }
            else {
               material = Material.BLUE;
            }

            attributes.setInteriorMaterial(material);
            attributes.setOutlineOpacity(1);
            attributes.setInteriorOpacity(1);
            attributes.setOutlineMaterial(Material.BLACK);
            attributes.setOutlineWidth(2);
            attributes.setDrawOutline(true);
            attributes.setDrawInterior(true);
            attributes.setEnableLighting(true);

            for (final Coordinate element : coords) {
               pathLocations.add(LatLon.fromRadians(element.y, element.x));
            }
            pgon = new ExtrudedPolygon(pathLocations, dHeight);
            pgon.setSideAttributes(attributes);
            pgon.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
            pgon.setSideHighlightAttributes(attributes);
            pgon.setCapAttributes(attributes);
            renderableLayer.addRenderable(pgon);
         }
         catch (final IteratorException e1) {}
      }
      iter.close();

      context.getWorldWindModel().addLayer(renderableLayer);
   }


   private Color getColorFromValue(final double dValue) {

      final float fValue = (float) ((_max - dValue) / (_max - _min));
      return new Color(fValue, 0f, 0f);

   }


   private void calculateColorRamp(final IVectorLayer layer,
                                   final int iField) {

      _min = Double.MAX_VALUE;
      _max = Double.NEGATIVE_INFINITY;
      final IFeatureIterator iter = layer.iterator();
      while (iter.hasNext()) {
         IFeature feature;
         try {
            feature = iter.next();
            final double dValue = Double.parseDouble(feature.getRecord().getValue(iField).toString());
            _min = Math.min(_min, dValue);
            _max = Math.max(_max, dValue);
         }
         catch (final Exception e) {}
      }

   }


   @Override
   public String getVersion() {
      return null;
   }


   @Override
   public String getName() {
      return "Extruder";
   }


}
