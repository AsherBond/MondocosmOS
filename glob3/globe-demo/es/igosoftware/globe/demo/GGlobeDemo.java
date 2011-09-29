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


package es.igosoftware.globe.demo;


import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.swing.SwingUtilities;

import es.igosoftware.concurrent.GConcurrent;
import es.igosoftware.euclid.GAngle;
import es.igosoftware.experimental.ndimensional.G3DImageMultidimensionalData;
import es.igosoftware.experimental.ndimensional.GMultidimensionalDataModule;
import es.igosoftware.experimental.ndimensional.IMultidimensionalData;
import es.igosoftware.experimental.pointscloud.rendering.GPointsCloudModule;
import es.igosoftware.experimental.vectorial.GGeotoolsVectorialModule;
import es.igosoftware.experimental.vectorial.GShapeLoaderDropHandler;
import es.igosoftware.experimental.vectorial.GVectorial2DModule;
import es.igosoftware.experimental.wms.GWMSModule;
import es.igosoftware.globe.GDragAndDropModule;
import es.igosoftware.globe.GGlobeApplication;
import es.igosoftware.globe.GGlobeWidget;
import es.igosoftware.globe.GHomePositionModule;
import es.igosoftware.globe.GLayersManagerModule;
import es.igosoftware.globe.GStatisticsModule;
import es.igosoftware.globe.IGlobeModule;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.layers.hud.GHUDLayer;
import es.igosoftware.globe.modules.GEnableDisableLayerModule;
import es.igosoftware.globe.modules.GFullScreenModule;
import es.igosoftware.globe.modules.gazetteer.GGazetteerModule;
import es.igosoftware.globe.modules.view.GFlatWorldModule;
import es.igosoftware.globe.modules.view.GShowLatLonGraticuleModule;
import es.igosoftware.globe.modules.view.GShowMeasureToolModule;
import es.igosoftware.globe.modules.view.GShowUTMGraticuleModule;
import es.igosoftware.globe.modules.view.GStereoViewerModule;
import es.igosoftware.globe.modules.view.GTakeScreenshotModule;
import es.igosoftware.globe.weather.aemet.AEMETModule;
import es.igosoftware.io.GFileName;
import es.igosoftware.io.GHttpLoader;
import es.igosoftware.io.ILoader;
import es.igosoftware.io.pointscloud.GPointsCloudFileLoader;
import es.igosoftware.io.pointscloud.IPointsCloudLoader;
import es.igosoftware.loading.G3DModel;
import es.igosoftware.loading.GAsyncObjLoader;
import es.igosoftware.loading.modelparts.GMaterial;
import es.igosoftware.loading.modelparts.GModelData;
import es.igosoftware.loading.modelparts.GModelMesh;
import es.igosoftware.panoramic.GPanoramic;
import es.igosoftware.panoramic.GPanoramicLayer;
import es.igosoftware.scenegraph.G3DModelNode;
import es.igosoftware.scenegraph.GElevationAnchor;
import es.igosoftware.scenegraph.GGroupNode;
import es.igosoftware.scenegraph.GPositionRenderableLayer;
import es.igosoftware.scenegraph.GTransformationOrder;
import es.igosoftware.util.GUtils;
import es.igosoftware.utils.GPNOAWMSLayer;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.EarthFlat;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.SurfaceIcon;


public class GGlobeDemo
         extends
            GGlobeApplication {
   private static final long    serialVersionUID                 = 1L;


   private static final boolean INCLUDES_MULTIDIMENSIONAL_MODULE = false;


   private GHUDLayer            _hudLayer;


   public GGlobeDemo() {
      super("en");
   }


   @Override
   public String getApplicationName() {
      return "Globe Demo";
   }


   @Override
   public String getApplicationVersion() {
      return "0.1";
   }


   @Override
   public Image getImageIcon() {
      return GUtils.getImage("globe-icon.png", getClass().getClassLoader());
   }


   @Override
   protected LayerList getDefaultLayers(final IGlobeRunningContext context) {
      final LayerList layers = super.getDefaultLayers(context);

      // layers.add(new OSMMapnikLayer());
      // layers.add(new TerrainProfileLayer());
      layers.add(new GPNOAWMSLayer(GPNOAWMSLayer.ImageFormat.JPEG));

      try {
         final ILoader loader = new GHttpLoader(new URL("http://glob3.sourceforge.net/"), true, context.getProgressReporter());

         //         layers.add(createCaceres3DModelLayer(context, loader));
         ayncLoadCaceres3DModel(context, loader, layers);

         createHUDLayer(context, layers);

         try {
            layers.add(createPanoramicLayer(context, loader));
         }
         catch (final RuntimeException e) {
            e.printStackTrace();
         }
      }
      catch (final MalformedURLException e) {
         context.getLogger().logSevere(e);
      }

      //      final int ___remove_spain_render;
      //      addSpain(layers);

      return layers;
   }


   //   private void addSpain(final LayerList layers) {
   //      final RenderableLayer renderableLayer = new RenderableLayer();
   //      final SurfacePolygon polygon = new SurfacePolygon(toLatLon(PeninsularSpainShape.create(false)));
   //      final ShapeAttributes attributes = new BasicShapeAttributes();
   //      attributes.setDrawInterior(true);
   //      attributes.setDrawOutline(true);
   //      attributes.setInteriorMaterial(new Material(new Color(1, 1, 0, 0.5f)));
   //      attributes.setInteriorOpacity(0.4f);
   //      polygon.setAttributes(attributes);
   //      renderableLayer.addRenderable(polygon);
   //      layers.add(renderableLayer);
   //   }
   //
   //
   //   private static List<LatLon> toLatLon(final GSimplePolygon2D polygon) {
   //      final List<LatLon> result = new ArrayList<LatLon>(polygon.getPointsCount());
   //
   //      for (final IVector2 point : polygon) {
   //         result.add(GWWUtils.toLatLon(point.asVector2(), GProjection.EPSG_4326));
   //      }
   //
   //      return result;
   //   }


   private void createHUDLayer(final IGlobeRunningContext context,
                               final LayerList layers) {
      //      final GHUDIcon hudIcon = new GHUDIcon(
      //               context.getBitmapFactory().getImage(GFileName.relative("icons", "earth.png"), 48, 48), GHUDIcon.Position.SOUTH);
      //
      //      hudIcon.addActionListener(new ActionListener() {
      //         @Override
      //         public void actionPerformed(final ActionEvent e) {
      //            System.out.println("Clicked on the earth icon!");
      //            JOptionPane.showConfirmDialog(getWidget().getFrame(), "Clicked on the earth icon!");
      //         }
      //      });

      _hudLayer = new GHUDLayer(context);
      //      _hudLayer.addElement(hudIcon);

      layers.add(_hudLayer);
   }


   private void ayncLoadCaceres3DModel(final IGlobeRunningContext context,
                                       final ILoader loader,
                                       final LayerList layers) {
      GConcurrent.getDefaultExecutor().submit(new Runnable() {
         @Override
         public void run() {
            loadCaceres3DModel(context, loader, layers);
         }
      });
   }


   private void loadCaceres3DModel(final IGlobeRunningContext context,
                                   final ILoader loader,
                                   final LayerList layers) {


      final GAsyncObjLoader.IHandler handler = new GAsyncObjLoader.IHandler() {
         @Override
         public void loadError(final IOException e) {
            context.getLogger().logSevere(e);
         }


         @Override
         public void loaded(final GModelData modelData) {
            hackCaceres3DModel(modelData);

            final G3DModel model = new G3DModel(modelData);
            final G3DModelNode caceres3DModelNode = new G3DModelNode("Caceres3D",
                     GTransformationOrder.ROTATION_SCALE_TRANSLATION, model);

            final GGroupNode caceres3DRootNode = new GGroupNode("Caceres3D root", GTransformationOrder.ROTATION_SCALE_TRANSLATION);
            caceres3DRootNode.setHeading(GAngle.NEG90);
            caceres3DRootNode.addChild(caceres3DModelNode);

            final GPositionRenderableLayer layer = new GPositionRenderableLayer(context, "Cáceres", true);

            layer.addNode(caceres3DRootNode, new Position(Angle.fromDegrees(39.4737), Angle.fromDegrees(-6.3710), 24.7),
                     GElevationAnchor.SEA_LEVEL);
            layers.add(layer);
         }
      };


      final boolean tryToDownladGZ = true;
      final boolean verbose = true;
      final GAsyncObjLoader objLoader = new GAsyncObjLoader(loader, tryToDownladGZ, verbose);

      final GFileName fileName = GFileName.relative("globe-demo-data", "models", "caceres3d.obj");


      objLoader.load(fileName, handler);
   }


   private GPanoramicLayer createPanoramicLayer(final IGlobeRunningContext context,
                                                final ILoader loader) {

      final String panoramicName = "Barrancos";
      final GFileName panoramicFileName = GFileName.relative("globe-demo-data", "panoramics", panoramicName);

      final GPanoramicLayer panoramicLayer = new GPanoramicLayer(context, panoramicName);

      GConcurrent.getDefaultExecutor().submit(new Runnable() {
         @Override
         public void run() {
            try {
               //         final Position position = new Position(Angle.fromDegrees(39.4737), Angle.fromDegrees(-6.3910), 0);
               final Position position = new Position(Angle.fromDegrees(38.1319), Angle.fromDegrees(-6.9780), 0);

               final GPanoramic panoramic = new GPanoramic(context, panoramicLayer, panoramicName, loader, panoramicFileName,
                        500, position, GElevationAnchor.SURFACE, GAngle.POS180, _hudLayer);
               panoramicLayer.addPanoramic(panoramic);
            }
            catch (final IOException e) {
               context.getLogger().logSevere(e);
            }
         }
      });

      return panoramicLayer;
   }


   private void hackCaceres3DModel(final GModelData model) {
      for (final GModelMesh mesh : model.getMeshes()) {
         GMaterial material = mesh.getMaterial();


         if (material == null) {
            material = new GMaterial("");
            material._diffuseColor = Color.WHITE;
            mesh.setMaterial(material);
         }
         else {
            if (material.hasTexture()) {
               material._diffuseColor = Color.WHITE;
            }
         }

         material._emissiveColor = new Color(0.2f, 0.2f, 0.2f);
      }
   }


   @Override
   protected List<IGlobeModule> getInitialModules(final IGlobeRunningContext context) {

      final RenderableLayer renderableLayer = new RenderableLayer();
      getWorldWindModel().addLayer(renderableLayer);

      final GFlatWorldModule flatWorldModule = new GFlatWorldModule(context) {
         @Override
         protected Globe createNonFlatGlobe() {
            return createGlobe();
         }


         @Override
         protected Globe createFlatGlobe() {
            return new EarthFlat();
         }
      };

      final LayerList layers = context.getWorldWindModel().getLayerList();

      final GMultidimensionalDataModule mdModule = INCLUDES_MULTIDIMENSIONAL_MODULE //
                                                                                   ? createMultidimensionalModule(context) //
                                                                                   : null;

      return Arrays.asList(
               new GLayersManagerModule(context, false, true), //
               new GVectorial2DModule(context, false), //
               new GGeotoolsVectorialModule(context, false), //
               new GWMSModule(context), //
               createPointsCloudModule(context), //
               mdModule, //
               flatWorldModule, //
               new GShowLatLonGraticuleModule(context), //
               new GShowUTMGraticuleModule(context), //
               new GShowMeasureToolModule(context, false), //
               new GFullScreenModule(context), //
               new GStereoViewerModule(context, false), //
               new GStatisticsModule(context), //
               new GDragAndDropModule(new GShapeLoaderDropHandler(context, false, false)), //
               new GTakeScreenshotModule(context), //
               createIGOModule(context, renderableLayer), //
               createCISL2011Module(context, renderableLayer), //
               createFOSS4G2011Module(context, renderableLayer),
               new GEnableDisableLayerModule(context, layers.getLayerByName(GEnableDisableLayerModule.MS_Virtual_Earth_Aerial),
                        true), //
               new GEnableDisableLayerModule(context, layers.getLayerByName(GEnableDisableLayerModule.Political_Boundaries),
                        false), //
               new GGazetteerModule(context), //
               new AEMETModule(context) // 
      );
   }


   private GMultidimensionalDataModule createMultidimensionalModule(final IGlobeRunningContext context) {
      GMultidimensionalDataModule multidimensionalDataModule = null;
      try {
         multidimensionalDataModule = new GMultidimensionalDataModule(context, loadMultidimensionalData(context));
      }
      catch (final Exception e) {
         getLogger().logWarning("Error initializing GMultidimensionalDataModule");
      }
      return multidimensionalDataModule;
   }


   private static IMultidimensionalData[] loadMultidimensionalData(final IGlobeRunningContext context) {
      try {
         final Position headPosition = new Position(Angle.fromDegrees(39.4737), Angle.fromDegrees(-6.3710), 0);

         return new IMultidimensionalData[] {
            new G3DImageMultidimensionalData("Mr Head", "data/cthead-8bit", ".png", headPosition, 10, 10, 20)
         };
      }
      catch (final IOException e) {
         context.getLogger().logWarning("Can't load multidimensional data");
      }

      return null;
   }


   private GPointsCloudModule createPointsCloudModule(final IGlobeRunningContext context) {
      GPointsCloudModule pointsCloudModule = null;
      try {
         final IPointsCloudLoader loader = new GPointsCloudFileLoader(GFileName.relative("data", "pointsclouds"),
                  context.getProgressReporter());
         // final IPointsCloudLoader loader = new GPointsCloudFileLoader(GFileName.absolute("home", "dgd", "Escritorio", "LOD"), context.getProgressReporter());
         pointsCloudModule = new GPointsCloudModule(context, loader);
      }
      catch (final IllegalArgumentException e) {
         getLogger().logWarning("Can't load PointsCloud module ");
      }
      return pointsCloudModule;
   }


   private GHomePositionModule createFOSS4G2011Module(final IGlobeRunningContext context,
                                                      final RenderableLayer renderableLayer) {
      final Position position = Position.fromDegrees(39.7424, -104.9891);
      final GFileName iconName = GFileName.relative("foss4g-2011.png");
      final String label = "Go to FOSS4G 2011";

      return createIconAndHomeModule(context, renderableLayer, position, iconName, label);
   }


   private GHomePositionModule createCISL2011Module(final IGlobeRunningContext context,
                                                    final RenderableLayer renderableLayer) {
      final Position position = Position.fromDegrees(-34.5845, -58.3982);
      final GFileName iconName = GFileName.relative("cisl-2011.png");
      final String label = "Go to CISL 2011";

      return createIconAndHomeModule(context, renderableLayer, position, iconName, label);
   }


   private GHomePositionModule createIGOModule(final IGlobeRunningContext context,
                                               final RenderableLayer renderableLayer) {
      final Position position = Position.fromDegrees(39.4791, -6.3748);
      final GFileName iconName = GFileName.relative("igo-icon.png");
      final String label = "Go to IGO Software";

      return createIconAndHomeModule(context, renderableLayer, position, iconName, label);
   }


   private GHomePositionModule createIconAndHomeModule(final IGlobeRunningContext context,
                                                       final RenderableLayer renderableLayer,
                                                       final Position position,
                                                       final GFileName iconName,
                                                       final String label) {
      final SurfaceIcon surfaceIcon = new SurfaceIcon(context.getBitmapFactory().getImage(iconName), position);
      surfaceIcon.setMaxSize(750);
      renderableLayer.addRenderable(surfaceIcon);
      return new GHomePositionModule(context, position, 300, false, iconName, label);
   }


   private static void checkDataDirectory() {
      final File dataDirectory = new File("data");
      if (!dataDirectory.exists()) {
         final String message = "\n\n" + //
                                "Can't find the directory data. Some samples will not work\n" + //
                                "\n" + //
                                "what to do:\n" + //
                                "  - Go to http://sourceforge.net/projects/glob3/files/globe-demo/\n" + //
                                "  - Download the file data.zip\n" + //
                                "  - Uncompress the file in the directory: " + new File("data").getAbsolutePath() + //
                                "\n\n";
         System.out.println(message);
      }
   }


   public static void main(final String[] args) {
      GGlobeWidget.initializeGUI();

      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            checkDataDirectory();

            new GGlobeDemoWidget().openInFrame();
         }
      });
   }


}
