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


package es.igosoftware.experimental.pointscloud.rendering;

import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.pointscloud.octree.GPCPointsCloud;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.experimental.pointscloud.rendering.scenegraph.GSGGroupNode;
import es.igosoftware.experimental.pointscloud.rendering.scenegraph.GSGPointsNode;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.IGlobeSymbolizer;
import es.igosoftware.globe.IGlobeVectorLayer;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.io.GFileName;
import es.igosoftware.io.ILoader;
import es.igosoftware.io.IProgressProducer;
import es.igosoftware.io.pointscloud.IPointsCloudLoader;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GHolder;
import es.igosoftware.util.GIntHolder;
import es.igosoftware.util.GMath;
import es.igosoftware.util.GStringUtils;
import es.igosoftware.util.GUtils;
import es.igosoftware.utils.GWWUtils;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.AbstractLayer;
import gov.nasa.worldwind.render.DrawContext;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.swing.Icon;


public final class GPointsCloudLayer
         extends
            AbstractLayer
         implements
            IGlobeVectorLayer,
            IProgressProducer {

   public static final float           MIN_QUALITYFACTOR  = 0.5f;
   public static final float           MAX_QUALITYFACTOR  = 8;
   public static final float           STEP_QUALITYFACTOR = 0.5f;

   public static final float           MIN_POINT_SIZE     = 1;
   public static final float           MAX_POINT_SIZE     = 16;
   public static final float           STEP_POINT_SIZE    = 1;


   private final GFileName             _pointsCloudName;
   private final IPointsCloudLoader    _loader;

   private final GHolder<GSGGroupNode> _rootNode          = new GHolder<GSGGroupNode>(null);
   private final GIntHolder            _pointsCount       = new GIntHolder(0);
   private boolean                     _hasIntensities    = false;
   private boolean                     _hasNormals        = false;
   private boolean                     _hasColors         = false;


   private boolean                     _verbose           = false;
   private boolean                     _showExtents       = false;

   private float                       _pointSize         = 1;
   private boolean                     _smooth            = false;
   private int                         _lastRendered      = -1;
   protected GProjection               _projection;
   private boolean                     _dynamicPointSize  = false;
   private boolean                     _colorFromState;
   private boolean                     _initialized       = false;
   private Globe                       _lastGlobe;
   private double                      _lastVerticalExaggeration;
   private final List<GSGPointsNode>   _tasksQueue        = new ArrayList<GSGPointsNode>();
   private float                       _minIntensity;
   private float                       _maxIntensity;
   private boolean                     _colorFromElevation;
   private double                      _minElevation;
   private double                      _maxElevation;
   private float                       _qualityFactor     = 1f;
   private long                        _lastFrameTime     = -1;
   private int                         _framesCount;
   private boolean                     _autoQuality       = false;
   private Color                       _pointsColor       = null;
   private String                      _renderingStatistics;


   public GPointsCloudLayer(final IGlobeRunningContext context,
                            final GFileName pointsCloudName,
                            final IPointsCloudLoader loader) {
      GAssert.notNull(pointsCloudName, "pointsCloudName");
      GAssert.notNull(loader, "loader");

      _pointsCloudName = pointsCloudName;
      _loader = loader;

      loadPointsCloud(context);

      context.getProgressReporter().register(this);
   }


   private void loadPointsCloud(final IGlobeRunningContext context) {
      final long started = System.currentTimeMillis();

      final long now = System.currentTimeMillis();

      _loader.load(GFileName.fromParentAndParts(_pointsCloudName, "tree.object.gz"), -1, false, Integer.MIN_VALUE,
               new ILoader.IHandler() {

                  @Override
                  public void loadError(final IOException e) {
                     System.err.println("Error=" + e + " while loading " + _pointsCloudName + "/tree.object.gz");
                  }


                  @Override
                  public void loaded(final File file,
                                     final long bytesLoaded,
                                     final boolean completeLoaded) {
                     if (!completeLoaded) {
                        return;
                     }

                     try {
                        final ObjectInputStream input = new ObjectInputStream(
                                 new GZIPInputStream(new FileInputStream(file), 2048));

                        final GPCPointsCloud pointsCloud = (GPCPointsCloud) input.readObject();
                        _hasIntensities = pointsCloud.hasIntensities();
                        _hasNormals = pointsCloud.hasNormals();
                        _hasColors = pointsCloud.hasColors();
                        _minIntensity = pointsCloud.getMinIntensity();
                        _maxIntensity = pointsCloud.getMaxIntensity();
                        _minElevation = pointsCloud.getMinElevation();
                        _maxElevation = pointsCloud.getMaxElevation();

                        _projection = pointsCloud.getProjection();
                        _rootNode.set(new GSGGroupNode(pointsCloud.getRoot(), _projection, GPointsCloudLayer.this));
                        _pointsCount.set(pointsCloud.getVerticesCount());

                        if (_colorFromElevation) {
                           _rootNode.get().setColorFromElevation(true);
                        }

                        final long elapsed = now - started;
                        System.out.println(getName() + ": Loaded points cloud structure data in "
                                           + GStringUtils.getTimeMessage(elapsed) + //
                                           " (points=" + NumberFormat.getNumberInstance().format(pointsCloud.getVerticesCount()) + //
                                           ", bounds=" + pointsCloud.getRoot().getBounds() + //
                                           ", projection=" + pointsCloud.getProjection() + //
                                           ", intensity range=" + _minIntensity + " -> " + _maxIntensity + //
                                           ", elevation range=" + _minElevation + " -> " + _maxElevation + //
                                           ")");
                     }
                     catch (final IOException e) {
                        e.printStackTrace(System.err);
                     }
                     catch (final ClassNotFoundException e) {
                        e.printStackTrace(System.err);
                     }
                     catch (final Throwable e) {
                        e.printStackTrace(System.err);
                     }


                     doDefaultAction(context);
                  }


               });
   }


   @Override
   protected void doPreRender(final DrawContext dc) {
      final GSGGroupNode node = _rootNode.get();
      if (node == null) {
         return;
      }

      if (!_initialized) {
         initialize(dc);
      }

      final View view = dc.getView();
      //      final Frustum frustum = view.getFrustumInModelCoordinates();
      final Globe globe = view.getGlobe();
      final double verticalExaggeration = dc.getVerticalExaggeration();


      final boolean changed = (_lastGlobe != globe) || (_lastVerticalExaggeration != verticalExaggeration);
      if (changed) {
         _lastGlobe = globe;
         _lastVerticalExaggeration = verticalExaggeration;
      }

      node.preRender(dc, changed);

      final long now = System.currentTimeMillis();
      if (_lastFrameTime < 0) {
         _framesCount = 0;
         _lastFrameTime = now;
      }
      else if ((_lastFrameTime + now) > 5000) {
         final float averageTimePerFrame = (float) (now - _lastFrameTime) / _framesCount;
         //         System.out.println("FPS=" + (1000f / averageTimePerFrame));

         if (_autoQuality) {
            if (averageTimePerFrame > 150) {
               final float qualityFactor = GMath.clamp(getQualityFactor() - STEP_QUALITYFACTOR, MIN_QUALITYFACTOR,
                        MAX_QUALITYFACTOR);
               setQualityFactor(qualityFactor);
            }
            else if (averageTimePerFrame < 100) {
               final float qualityFactor = GMath.clamp(getQualityFactor() + STEP_QUALITYFACTOR, MIN_QUALITYFACTOR,
                        MAX_QUALITYFACTOR);
               setQualityFactor(qualityFactor);
            }
         }

         _framesCount = 0;
         _lastFrameTime = now;
      }
      _framesCount++;
   }


   private void initialize(final DrawContext dc) {
      if (_initialized) {
         return;
      }

      _initialized = true;
      //      dc.setVerticalExaggeration(dc.getVerticalExaggeration() * 8);

      initializeWorkers();

      //      ((BasicView) dc.getView()).setNearClipDistance(0.2);

      final GSGGroupNode node = _rootNode.get();
      node.initialize(dc);
   }


   @Override
   protected void doRender(final DrawContext dc) {
      final GSGGroupNode node = _rootNode.get();
      if (node == null) {
         return;
      }

      //      final View view = dc.getView();
      //      //      final Frustum frustum = view.getFrustumInModelCoordinates();
      //      final Globe globe = view.getGlobe();
      //      final double verticalExaggeration = dc.getVerticalExaggeration();

      final int rendered = node.render(dc);

      if (_verbose) {
         if (rendered != _lastRendered) {
            _lastRendered = rendered;

            final int pointsCount = _pointsCount.get();
            //            GStringUtils.getSpaceMessage(pointsCount)


            setRenderingStatistics(NumberFormat.getNumberInstance().format(rendered) + " ("
                                   + GStringUtils.formatPercent(rendered, pointsCount) + ")");
         }
      }


      GWWUtils.checkGLErrors(dc);
   }


   private void setRenderingStatistics(final String renderingStatistics) {
      _renderingStatistics = renderingStatistics;
      firePropertyChange("RenderingStatistics", null, _renderingStatistics);
   }


   public String getRenderingStatistics() {
      return _renderingStatistics;
   }


   public String getPointsCount() {
      return NumberFormat.getNumberInstance().format(_pointsCount.get());
   }


   public void setVerbose(final boolean verbose) {
      _verbose = verbose;
   }


   public void setSmooth(final boolean smooth) {
      _smooth = smooth;
      redraw();
   }


   public boolean getSmooth() {
      return _smooth;
   }


   public void increasePointSize() {
      setPointSize(_pointSize + STEP_POINT_SIZE);
   }


   public void decreasePointSize() {
      setPointSize(_pointSize - STEP_POINT_SIZE);
   }


   public void setPointSize(final float pointsSize) {
      final float clamped = GMath.clamp(pointsSize, MIN_POINT_SIZE, MAX_POINT_SIZE);

      if (GMath.closeTo(clamped, _pointSize)) {
         return;
      }
      _pointSize = pointsSize;
      redraw();
   }


   public float getPointSize() {
      return _pointSize;
   }


   public void setShowExtents(final boolean showExtents) {
      _showExtents = showExtents;
      redraw();
   }


   public boolean isShowExtents() {
      return _showExtents;
   }


   public boolean isVerbose() {
      return _verbose;
   }


   public ILoader getLoader() {
      return _loader;
   }


   public GFileName getPointsCloudName() {
      return _pointsCloudName;
   }


   //   @Override
   //   public GProjection getProjection() {
   //      return _projection;
   //   }


   public boolean getDynamicPointSize() {
      return _dynamicPointSize;
   }


   public void setDynamicPointSize(final boolean dynamicPointSize) {
      _dynamicPointSize = dynamicPointSize;
      redraw();
   }


   public void setColorFromState(final boolean colorFromState) {
      _colorFromState = colorFromState;
      redraw();
   }


   public boolean getColorFromState() {
      return _colorFromState;
   }


   private void initializeWorkers() {
      final int workersCount = Math.max(Runtime.getRuntime().availableProcessors() / 2, 2);
      //      final int workersCount = 1;

      final ThreadGroup group = new ThreadGroup("Node Workers Group");
      group.setDaemon(true);

      for (int i = 0; i < workersCount; i++) {
         createWorker(group, i);
      }
   }


   private void createWorker(final ThreadGroup group,
                             final int i) {
      final Thread thread = new Thread(group, "Node Worker #" + i) {
         @Override
         public void run() {
            while (true) {
               GSGPointsNode taskToRun = null;
               synchronized (_tasksQueue) {
                  for (final GSGPointsNode candidateTask : _tasksQueue) {
                     if (!candidateTask.wantsToRun()) {
                        continue;
                     }

                     if ((taskToRun == null) || (candidateTask.getPriority() > taskToRun.getPriority())) {
                        taskToRun = candidateTask;
                     }
                  }
               }

               if (taskToRun == null) {
                  GUtils.delay(100);
                  continue;
               }

               try {
                  synchronized (taskToRun) {
                     if (taskToRun.wantsToRun()) {
                        taskToRun.run();
                     }
                  }
               }
               catch (final Throwable e) {
                  e.printStackTrace(System.err);
               }
            }
         }
      };

      thread.setDaemon(true);
      thread.setPriority(Thread.MIN_PRIORITY);
      //thread.setPriority(Thread.MAX_PRIORITY);

      thread.start();
   }


   public void registerNodeTask(final GSGPointsNode pointsNode) {
      synchronized (_tasksQueue) {
         _tasksQueue.add(pointsNode);
      }
   }


   public boolean hasIntensities() {
      //      final GSGGroupNode node = _rootNode.get();
      //      if (node == null) {
      //         return false;
      //      }
      return _hasIntensities;
   }


   public boolean hasNormals() {
      //      final GSGGroupNode node = _rootNode.get();
      //      if (node == null) {
      //         return false;
      //      }
      return _hasNormals;
   }


   public boolean hasColors() {
      //      final GSGGroupNode node = _rootNode.get();
      //      if (node == null) {
      //         return false;
      //      }
      return _hasColors;
   }


   public float getMinIntensity() {
      return _minIntensity;
   }


   public float getMaxIntensity() {
      return _maxIntensity;
   }


   public boolean getColorFromElevation() {
      return _colorFromElevation;
   }


   public void setColorFromElevation(final boolean colorFromElevation) {
      if (colorFromElevation == _colorFromElevation) {
         return;
      }

      _rootNode.get().setColorFromElevation(colorFromElevation);
      _colorFromElevation = colorFromElevation;
      redraw();
   }


   public double getMinElevation() {
      return _minElevation;
   }


   public double getMaxElevation() {
      return _maxElevation;
   }


   public void reload() {
      final GSGGroupNode node = _rootNode.get();
      if (node == null) {
         return;
      }
      node.reload();
      redraw();
   }


   //   public Position getHomePosition() {
   //      final GSGGroupNode node = _rootNode.get();
   //      if (node == null) {
   //         return null;
   //      }
   //
   //      if (_homePosition == null) {
   //         _homePosition = node.getHomePosition();
   //      }
   //
   //      return _homePosition;
   //   }


   public float getQualityFactor() {
      return _qualityFactor;
   }


   public void setQualityFactor(final float qualityFactor) {
      if (qualityFactor == _qualityFactor) {
         return;
      }
      final float oldValue = _qualityFactor;
      _qualityFactor = qualityFactor;
      firePropertyChange("QualityFactor", oldValue, _qualityFactor);
      redraw();
   }


   public Color getPointsColor() {
      return (_pointsColor == null) ? Color.WHITE : _pointsColor;
   }


   public void setPointsColor(final Color pointsColor) {
      if ((pointsColor == _pointsColor) || pointsColor.equals(_pointsColor)) {
         return;
      }
      final Color oldValue = _pointsColor;
      _pointsColor = pointsColor;
      firePropertyChange("QualityFactor", oldValue, _qualityFactor);
      if (_pointsColor.equals(Color.WHITE)) {
         _pointsColor = null;
      }
      _rootNode.get().setPointsColor(_pointsColor);
      redraw();
   }


   public boolean getAutoQuality() {
      return _autoQuality;
   }


   public void setAutoQuality(final boolean autoTuneQuality) {
      _autoQuality = autoTuneQuality;
   }


   @Override
   public String getName() {
      return _pointsCloudName.buildPath('/');
   }


   @Override
   public Icon getIcon(final IGlobeRunningContext context) {
      return context.getBitmapFactory().getSmallIcon(GFileName.relative("pointscloud.png"));
   }


   @Override
   public Sector getExtent() {
      final GSGGroupNode node = _rootNode.get();
      if (node == null) {
         return null;
      }

      return node.getBox()._sector;
   }


   @Override
   public IGlobeFeatureCollection getFeaturesCollection() {
      return null;
   }


   @Override
   public final void redraw() {
      // fire event to force a redraw
      firePropertyChange(AVKey.LAYER, null, this);
   }


   @Override
   public List<ILayerAttribute<?>> getLayerAttributes(final IGlobeRunningContext context) {
      return null;
   }


   @Override
   public void doDefaultAction(final IGlobeRunningContext context) {
      context.getCameraController().animatedZoomToSector(getExtent());
   }


   @Override
   public List<? extends ILayerAction> getLayerActions(final IGlobeRunningContext context) {
      return null;
   }


   @Override
   public IGlobeSymbolizer getSymbolizer() {
      return null;
   }


   @Override
   public long runningTasksCount() {
      synchronized (_tasksQueue) {
         //         return _tasksQueue.size();
         long count = 0;
         for (final GSGPointsNode task : _tasksQueue) {
            if (task.wantsToRun()) {
               count++;
            }
         }
         return count;
      }
   }


}
