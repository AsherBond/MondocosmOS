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

import es.igosoftware.globe.animations.GGlobeAnimationsScheduler;
import es.igosoftware.globe.animations.GGlobeAnimatorLayer;
import es.igosoftware.globe.animations.IGlobeAnimationsScheduler;
import es.igosoftware.globe.modules.view.GCompassNavigationModule;
import es.igosoftware.globe.utils.GOnFirstRenderLayer;
import es.igosoftware.globe.view.customView.GView;
import es.igosoftware.io.GFileName;
import es.igosoftware.io.IProgressReporter;
import es.igosoftware.logging.GLogger;
import es.igosoftware.logging.ILogger;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GPair;
import es.igosoftware.util.GSwingUtils;
import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.CompassLayer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.ViewControlsLayer;
import gov.nasa.worldwind.layers.ViewControlsSelectListener;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwindx.examples.util.StatusLayer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


public abstract class GGlobeApplication
         implements
            IGlobeApplication {


   static {
      Configuration.setValue(AVKey.VIEW_CLASS_NAME, GView.class.getName());
   }


   private static final long          serialVersionUID       = 1L;


   private final IGlobeRunningContext _runningContext;
   private final List<IGlobeModule>   _modules;

   private GGlobeWidget               _widget;


   private long                       _lastRunningTasksCount = 0;


   protected GGlobeApplication() {
      this(Locale.getDefault().getLanguage());
   }


   protected GGlobeApplication(final String language) {
      _runningContext = initializeRunningContext(language, new GGlobeAnimatorLayer());

      getLogger().logInfo("Starting " + getApplicationNameAndVersion() + "...");

      final Globe globe = createGlobe();
      final Model model = createModel(globe);

      getWorldWindowGLCanvas().setModel(model);

      //      _widget = createWidget();

      _modules = initializeModules();
   }


   protected WorldWindowGLCanvas createWorldWindowGLCanvas() {
      return new WorldWindowGLCanvas();
   }


   protected IGlobeRunningContext initializeRunningContext(final String initialLanguage,
                                                           final GGlobeAnimatorLayer animatorLayer) {
      return new IGlobeRunningContext() {
         private final GGlobeApplication         _application      = GGlobeApplication.this;
         private final ILogger                   _logger           = new GGlobeLogger(_application, _application.defaultLogger());
         private final IGlobeBitmapFactory       _bitmapFactory    = new GGlobeBitmapFactory(_application);
         private final IGlobeTranslator          _translator       = new GGlobeTranslator(_application, initialLanguage);
         private final IGlobeCameraController    _cameraController = new GCameraController(_application);
         private final IGlobeLayerDataManager    _layerDataManager = new GGlobeLayerDataManager(_application);
         private final IGlobeWorldWindModel      _worldWindModel   = new GGlobeWorldWindModel(
                                                                            _application.createWorldWindowGLCanvas());
         private final IGlobeAnimationsScheduler _animator         = new GGlobeAnimationsScheduler(animatorLayer);
         private final IProgressReporter         _progressReporter = new GProgressReporter(_application);


         @Override
         public IGlobeApplication getApplication() {
            return _application;
         }


         @Override
         public ILogger getLogger() {
            return _logger;
         }


         @Override
         public IGlobeBitmapFactory getBitmapFactory() {
            return _bitmapFactory;
         }


         @Override
         public IGlobeTranslator getTranslator() {
            return _translator;
         }


         @Override
         public IGlobeCameraController getCameraController() {
            return _cameraController;
         }


         @Override
         public IGlobeLayerDataManager getLayerDataManager() {
            return _layerDataManager;
         }


         @Override
         public IGlobeWorldWindModel getWorldWindModel() {
            return _worldWindModel;
         }


         @Override
         public IGlobeAnimationsScheduler getAnimationsScheduler() {
            return _animator;
         }


         @Override
         public IProgressReporter getProgressReporter() {
            return _progressReporter;
         }
      };
   }


   protected IGlobeRunningContext getRunningContext() {
      return _runningContext;
   }


   protected ILogger getLogger() {
      return getRunningContext().getLogger();
   }


   protected IGlobeWorldWindModel getWorldWindModel() {
      return getRunningContext().getWorldWindModel();
   }


   protected WorldWindowGLCanvas getWorldWindowGLCanvas() {
      return getWorldWindModel().getWorldWindowGLCanvas();
   }


   protected Globe createGlobe() {
      return new Earth();
   }


   private List<IGlobeModule> initializeModules() {
      //      final GHolder<List<IGlobeModule>> modules = new GHolder<List<IGlobeModule>>(null);
      final List<IGlobeModule> modules = new ArrayList<IGlobeModule>();

      final Set<IGlobeModule> buggyModules = new HashSet<IGlobeModule>();

      GSwingUtils.invokeInSwingThread(new Runnable() {
         @Override
         public void run() {
            try {
               final IGlobeRunningContext context = getRunningContext();

               modules.addAll(getInitialModules(context));
               modules.addAll(getDefaultModules(context));

               for (final IGlobeModule module : modules) {
                  if (module != null) {
                     try {
                        module.initialize(context);
                        module.initializeTranslations(context);
                     }
                     catch (final Exception e) {
                        buggyModules.add(module);
                        getLogger().logSevere("Exception while initialing module: " + module.getClass(), e);
                     }
                  }
               }
            }
            catch (final Exception e) {
               getLogger().logSevere("Exception while initializing modules", e);
            }
         }
      });

      //      if (modules.hasValue()) {
      //         return GCollections.select(modules.get(), new GPredicate<IGlobeModule>() {
      //            @Override
      //            public boolean evaluate(final IGlobeModule element) {
      //               return (element != null) && !buggyModules.contains(element);
      //            }
      //         });
      modules.removeAll(buggyModules);

      final Iterator<IGlobeModule> iterator = modules.iterator();
      while (iterator.hasNext()) {
         final IGlobeModule module = iterator.next();
         if (module == null) {
            iterator.remove();
         }
      }

      return modules;
      //      }

      //      return new IGlobeModule[0];
      //      return Collections.emptyList();
   }


   @Override
   public Dimension initialDimension() {
      return new Dimension(1024, 768);
   }


   protected List<GFileName> getIconsDirectories() {
      return Arrays.asList(GFileName.relative("bitmaps", "icons"), GFileName.relative("bitmaps"),
               GFileName.relative("..", "globe", "bitmaps", "icons"), GFileName.relative("globe", "bitmaps", "icons"));
   }


   protected List<GFileName> getImagesDirectories() {
      return Arrays.asList(GFileName.relative("bitmaps", "icons"), GFileName.relative("bitmaps"),
               GFileName.relative("..", "globe", "bitmaps", "icons"), GFileName.relative("globe", "bitmaps", "icons"));
   }


   protected Model createModel(final Globe globe) {
      final LayerList defaultLayers = getDefaultLayers(getRunningContext());

      defaultLayers.add(new GOnFirstRenderLayer() {
         @Override
         protected void execute(final DrawContext dc) {
            onFirstRender(getRunningContext(), dc);
         }
      });

      final GGlobeAnimatorLayer animationLayer = getRunningContext().getAnimationsScheduler().getLayer();
      if (animationLayer != null) {
         defaultLayers.add(animationLayer);
      }

      final Model model = new BasicModel(globe, defaultLayers);

      // model.setShowWireframeExterior(true);
      // model.setShowWireframeInterior(true);
      // model.setShowTessellationBoundingVolumes(true);

      return model;
   }


   protected LayerList getDefaultLayers(final IGlobeRunningContext context) {
      final LayerList layers = new BasicModel().getLayers();

      layers.getLayerByName("NASA Blue Marble Image").setEnabled(true);
      layers.getLayerByName("Blue Marble (WMS) 2004").setEnabled(true);
      layers.getLayerByName("i-cubed Landsat").setEnabled(true);

      createStatusLayer(context, layers);
      createViewControlsLayer(context, layers);

      configureCompassInteraction(context, layers);

      return layers;
   }


   private void createStatusLayer(final IGlobeRunningContext context,
                                  final LayerList layers) {
      final StatusLayer statusLayer = new StatusLayer();
      statusLayer.setEventSource(context.getWorldWindModel().getWorldWindowGLCanvas());
      layers.add(statusLayer);
   }


   private void createViewControlsLayer(final IGlobeRunningContext context,
                                        final LayerList layers) {
      final ViewControlsLayer viewControlsLayer = new ViewControlsLayer();
      layers.add(viewControlsLayer);

      final WorldWindowGLCanvas canvas = context.getWorldWindModel().getWorldWindowGLCanvas();
      canvas.addSelectListener(new ViewControlsSelectListener(canvas, viewControlsLayer));
   }


   private void configureCompassInteraction(final IGlobeRunningContext context,
                                            final LayerList layers) {
      // Find Compass layer and enable picking
      boolean isCompassFound = false;
      for (final Layer layer : layers) {
         if (layer instanceof CompassLayer) {
            layer.setPickEnabled(true);
            isCompassFound = true;
         }
      }

      if (!isCompassFound) {
         return;
      }

      // Add select listener to handle drag events on the compass
      final IGlobeWorldWindModel model = context.getWorldWindModel();
      model.getWorldWindowGLCanvas().addSelectListener(new SelectListener() {
         private Angle _dragStartHeading = null;


         @Override
         public void selected(final SelectEvent event) {
            if (event.getTopObject() instanceof CompassLayer) {
               final View view = model.getView();

               final Angle heading = (Angle) event.getTopPickedObject().getValue("Heading");

               if (event.isDrag() && (_dragStartHeading == null)) {
                  _dragStartHeading = heading;
                  event.consume();
               }
               else if (event.isRollover() && (_dragStartHeading != null)) {
                  view.stopAnimations();
                  view.setHeading(Angle.fromDegrees(-heading.degrees));
               }
               else if (event.isDragEnd()) {
                  _dragStartHeading = null;
               }
               else if (event.isLeftDoubleClick()) {
                  context.getCameraController().goToHeading(Angle.ZERO);
               }
            }
         }
      });
   }


   protected int getDefaultIconSize() {
      return 20;
   }


   protected void init() {

   }


   protected String getApplicationNameAndVersion() {
      final String version = getApplicationVersion();
      if ((version == null) || version.isEmpty()) {
         return getApplicationName();
      }
      return getApplicationName() + " (" + version + ")";
   }


   protected float getLeftPanelWidthRatio() {
      return 0.2f;
   }


   protected List<GPair<String, Component>> getApplicationPanels(@SuppressWarnings("unused") final IGlobeRunningContext context) {
      return null;
   }


   protected void exit() {
      getWorldWindModel().getLayerList().add(new GOnFirstRenderLayer() {
         @Override
         protected void execute(final DrawContext dc) {
            getLogger().logInfo("Closing " + getApplicationNameAndVersion() + "...");

            disposeLayers();

            finalizeModules();

            final Thread finalizer = new Thread() {
               @Override
               public void run() {
                  proceedToExit();
               }
            };
            finalizer.setPriority(Thread.MAX_PRIORITY);
            finalizer.start();
         }
      });
   }


   private void finalizeModules() {
      getLogger().logInfo("Finalizing modules...");

      for (final IGlobeModule module : _modules) {
         //         getLogger().logInfo("  Finalizing module: " + module.getName() + " (" + module.getVersion() + ")");
         try {
            module.finalize(getRunningContext());
         }
         catch (final Exception e) {
            getLogger().logSevere("Error finalizing module: " + module.getName() + " (" + module.getVersion() + ")");
         }
      }
   }


   private void disposeLayers() {
      getLogger().logInfo("Disposing layers...");

      final LayerList layerList = getWorldWindModel().getLayerList();
      while (!layerList.isEmpty()) {
         final Layer layer = layerList.remove(0);
         //         getLogger().logInfo("  Disposing layer: " + layer.getName());
         try {
            layer.dispose();
         }
         catch (final Exception e) {
            getLogger().logSevere("Error disposing layer: " + layer.getName(), e);
         }
      }
   }


   protected void proceedToExit() {
      getLogger().logInfo("Proceeding to exit.");

      getWidget().dispose();
   }


   @Override
   public GGlobeWidget getWidget() {
      return _widget;
   }


   @Override
   public Frame getFrame() {
      return getWidget().getFrame();
   }


   @Override
   public void redraw() {
      final WorldWindowGLCanvas wwGLCanvas = getWorldWindowGLCanvas();
      if (wwGLCanvas != null) {
         wwGLCanvas.redrawNow();
      }

      //      if (_redrawListener != null) {
      //         _redrawListener.run();
      //      }
   }


   protected abstract String getApplicationName();


   protected abstract String getApplicationVersion();


   protected abstract Image getImageIcon();


   protected Map<String, Map<String, String>> initializeTranslations() {
      final HashMap<String, Map<String, String>> translations = new HashMap<String, Map<String, String>>();


      final HashMap<String, String> spanish = new HashMap<String, String>();
      spanish.put("Exit", "Salir");
      spanish.put("File", "Archivo");
      spanish.put("View", "Vista");
      spanish.put("Analysis", "Análisis");
      spanish.put("Navigation", "Navegación");
      spanish.put("Help", "Ayuda");
      translations.put("es", spanish);


      final HashMap<String, String> german = new HashMap<String, String>();
      german.put("Exit", "Verlassen");
      german.put("File", "Datei");
      german.put("View", "Ansicht");
      german.put("Analysis", "Analyse");
      german.put("Navigation", "Navigation");
      german.put("Help", "Hilfe");
      translations.put("de", german);

      final HashMap<String, String> portugese = new HashMap<String, String>();
      portugese.put("Exit", "Sair");
      portugese.put("File", "Arquivo");
      portugese.put("View", "Vista");
      portugese.put("Analysis", "Análise");
      portugese.put("Navigation", "Navegação");
      portugese.put("Help", "Ajuda");
      translations.put("pt", portugese);


      return translations;
   }


   protected abstract List<IGlobeModule> getInitialModules(final IGlobeRunningContext context);


   protected List<IGlobeModule> getDefaultModules(final IGlobeRunningContext context) {
      return GCollections.asList(new GCompassNavigationModule(context));
   }


   @Override
   public final List<IGlobeModule> getModules() {
      return _modules;
   }


   protected ILogger defaultLogger() {
      return GLogger.instance();
   }


   void setWidget(final GGlobeWidget widget) {
      if (_widget != null) {
         throw new RuntimeException("The application already has a widget");
      }
      _widget = widget;
   }


   protected void onFirstRender(@SuppressWarnings("unused") final IGlobeRunningContext context,
                                @SuppressWarnings("unused") final DrawContext dc) {

   }


   protected final void setRunningTasksCount(final long runningTasksCount) {

      if (runningTasksCount != _lastRunningTasksCount) {
         changedRunningTasksCount(runningTasksCount);
      }
   }


   protected void changedRunningTasksCount(final long runningTasksCount) {
      //      final int ___remove_print;
      //      System.out.println(this + " changedRunningTasksCount(): " + runningTasksCount);

      _lastRunningTasksCount = runningTasksCount;

      final GGlobeWidget widget = getWidget();
      if (widget == null) {
         // notify without widget
         final String msg = (runningTasksCount > 0) //
                                                   ? Long.toString(runningTasksCount) //
                                                   : "STOPED";
         getLogger().logInfo("RunningTasksCount: " + msg);

         _lastRunningTasksCount = 0; // clean to force a new try to find the widget
      }
      else {
         // notify trought the widget
         if (widget.changedRunningTasksCount(runningTasksCount)) {
            redraw();
         }
      }
   }


}
