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


package es.igosoftware.panoramic;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;

import javax.imageio.ImageIO;
import javax.media.opengl.DebugGL;
import javax.media.opengl.GL;

import com.sun.opengl.util.texture.TextureIO;

import es.igosoftware.euclid.GAngle;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.layers.hud.GHUDIcon;
import es.igosoftware.globe.layers.hud.GHUDLayer;
import es.igosoftware.globe.view.customView.GView;
import es.igosoftware.io.GFileName;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.io.ILoader;
import es.igosoftware.loading.GDisplayListCache;
import es.igosoftware.scenegraph.GElevationAnchor;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GHolder;
import es.igosoftware.util.GPair;
import es.igosoftware.util.GUtils;
import es.igosoftware.utils.GGLUtils;
import es.igosoftware.utils.GGlobeStateKeyCache;
import es.igosoftware.utils.GPanoramicCompiler;
import es.igosoftware.utils.GTexture;
import es.igosoftware.utils.GTextureLoader;
import es.igosoftware.utils.GWWUtils;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Box;
import gov.nasa.worldwind.geom.Extent;
import gov.nasa.worldwind.geom.Frustum;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Matrix;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.geom.Sphere;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.OrderedRenderable;
import gov.nasa.worldwind.util.WWMath;


public class GPanoramic {


   public static final double MINIMUM_FOV = 10;
   public static final double MAXIMUM_FOV = 130;
   public static final double INITIAL_FOV = MAXIMUM_FOV;


   public static interface ActivationListener {
      public void activated();


      public void deactivated();
   }


   private static final int                                        TILE_THETA_SUBDIVISIONS               = 2;
   private static final int                                        TILE_RHO_SUBDIVISIONS                 = 1;
   private static final double                                     MIN_PROYECTED_SIZE                    = 12;

   private static final GGlobeStateKeyCache<PanoramicTile, Extent> EXTENT_CACHE                          = initializeExtentCache();
   private static final float[]                                    FLIP_VERTICALLY_TRANSFORMATION        = initializeFlipVerticallyTransformation();
   private static final GDisplayListCache<PanoramicTile>           QUAD_STRIPS_DISPLAY_LIST_CACHE        = initializeQuadStripsDisplayListCache();

   private static final GFileName                                  DEFAULT_EXIT_ICON_NAME                = GFileName.relative("quit.png");
   private static final GFileName                                  DEFAULT_DOWNLOADING_TEXTURE_FILE_NAME = GFileName.relative("downloading-texture.jpg");


   private static GDisplayListCache<PanoramicTile> initializeQuadStripsDisplayListCache() {
      return new GDisplayListCache<PanoramicTile>(50, false) {
         @Override
         protected void beforeRenderingToDisplayList(final PanoramicTile tile,
                                                     final DrawContext dc) {
            tile.forceLoadTexture();
         }


         @Override
         protected void renderToDisplayList(final PanoramicTile tile,
                                            final DrawContext dc) {
            tile.rawRender(dc);
         }
      };
   }


   private static GGlobeStateKeyCache<PanoramicTile, Extent> initializeExtentCache() {
      return new GGlobeStateKeyCache<PanoramicTile, Extent>( //
               new GGlobeStateKeyCache.Factory<PanoramicTile, Extent>() {
                  @Override
                  public Extent create(final DrawContext dc,
                                       final PanoramicTile tile) {
                     return tile.calculateExtent();
                  }
               });
   }


   private static float[] initializeFlipVerticallyTransformation() {
      final Matrix scaling = Matrix.fromScale(1, -1, 1);
      final Matrix translation = Matrix.fromTranslation(0, -1, 0);

      return GWWUtils.toGLArray(scaling.multiply(translation));
   }

   private final List<GPanoramic.ActivationListener> _activationListeners     = new ArrayList<GPanoramic.ActivationListener>();


   private final IGlobeRunningContext                _context;

   private final String                              _name;
   private final ILoader                             _loader;
   private final GFileName                           _panoramicFileName;
   private final double                              _radius;
   private final Position                            _position;
   private final GAngle                              _heading;

   private Globe                                     _lastGlobe;
   private double                                    _lastVerticalExaggeration;
   private Frustum                                   _lastFrustum;

   private final GElevationAnchor                    _anchor;

   private final List<PanoramicTile>                 _topTiles;

   private Matrix                                    _modelCoordinateOriginTransform;
   private final float[]                             _modelViewMatrixArray    = new float[16];
   private Sphere                                    _boundsInGlobalCoordinates;
   private boolean                                   _renderWireframe         = false;
   private final GPanoramicCompiler.ZoomLevels       _zoomLevels;

   private final List<PanoramicTile>                 _visibleTiles            = new ArrayList<PanoramicTile>(100);

   private final int                                 _maxLevel                = 16;
   private final int                                 _maxResolutionInPanoramic;

   private final Layer                               _layer;
   private final GHUDLayer                           _hudLayer;
   private final GFileName                           _exitIconName;
   private final GHUDIcon                            _hudIcon;
   private double                                    _currentDistanceFromEye;


   private boolean                                   _isHidden;
   private boolean                                   _isActive                = false;
   private boolean                                   _isHighlighted           = false;

   private final GFileName                           _downloadingTextureFileName;
   private final Object                              _downloadingTextureMutex = new Object();
   private GTexture                                  _downloadingTexture      = null;


   private boolean                                   _renderExtent            = false;
   private OrderedRenderable                         _orderedRenderable;


   public GPanoramic(final IGlobeRunningContext context,
                     final Layer layer,
                     final String name,
                     final ILoader loader,
                     final GFileName panoramicFileName,
                     final double radius,
                     final Position position,
                     final GElevationAnchor anchor,
                     final GAngle heading,
                     final GHUDLayer hudLayer) throws IOException {
      this(context, layer, name, loader, panoramicFileName, radius, position, anchor, heading, hudLayer, DEFAULT_EXIT_ICON_NAME,
           DEFAULT_DOWNLOADING_TEXTURE_FILE_NAME);
   }


   public GPanoramic(final IGlobeRunningContext context,
                     final Layer layer,
                     final String name,
                     final ILoader loader,
                     final GFileName panoramicFileName,
                     final double radius,
                     final Position position,
                     final GElevationAnchor anchor,
                     final GAngle heading,
                     final GHUDLayer hudLayer,
                     final GFileName exitIconName) throws IOException {
      this(context, layer, name, loader, panoramicFileName, radius, position, anchor, heading, hudLayer, exitIconName,
           DEFAULT_DOWNLOADING_TEXTURE_FILE_NAME);
   }


   public GPanoramic(final IGlobeRunningContext context,
                     final Layer layer,
                     final String name,
                     final ILoader loader,
                     final GFileName panoramicFileName,
                     final double radius,
                     final Position position,
                     final GElevationAnchor anchor,
                     final GAngle heading,
                     final GHUDLayer hudLayer,
                     final GFileName exitIconName,
                     final GFileName downloadingTextureFileName) throws IOException {
      GAssert.notNull(name, "name");
      GAssert.notNull(loader, "loader");
      GAssert.isPositive(radius, "radius");
      GAssert.notNull(position, "position");
      GAssert.notNull(anchor, "anchor");
      GAssert.notNull(exitIconName, "exitIconName");

      _layer = layer;

      _name = name;
      _loader = loader;
      _panoramicFileName = panoramicFileName;
      _radius = radius;
      _position = position;
      _heading = heading;
      _anchor = anchor;
      _hudLayer = hudLayer;
      _exitIconName = exitIconName;
      _downloadingTextureFileName = downloadingTextureFileName;

      _topTiles = createTopTiles();

      _zoomLevels = readZoomLevels();

      _maxResolutionInPanoramic = _zoomLevels.getLevels().size() - 1;

      _context = context;
      _hudIcon = createHUDIcon(context);

      tryToDownloadTopTextures();
   }


   private void tryToDownloadTopTextures() {
      final Thread worker = new Thread() {
         @Override
         public void run() {
            for (final PanoramicTile tile : _topTiles) {
               // load textures of topTiles with maximum priority
               tile.tryToLoadTexture(Integer.MAX_VALUE);
            }
         }
      };
      worker.setPriority(Thread.MAX_PRIORITY);
      worker.setDaemon(true);
      worker.start();
   }


   protected GHUDIcon createHUDIcon(final IGlobeRunningContext context) {
      if (_hudLayer == null) {
         return null;
      }


      final GHUDIcon hudIcon = new GHUDIcon(context.getBitmapFactory().getImage(_exitIconName, 48, 48),
               GHUDIcon.Position.NORTH_EAST);

      hudIcon.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            deactivate();
         }
      });

      hudIcon.setEnable(false);
      _hudLayer.addElement(hudIcon);

      return hudIcon;
   }


   public boolean isActive() {
      return _isActive;
   }


   public GHUDLayer getHUDLayer() {
      return _hudLayer;
   }


   public Layer getLayer() {
      return _layer;
   }


   public String getName() {
      return _name;
   }


   public Position getPosition() {
      return _position;
   }


   public Sphere getGlobalBounds() {
      return _boundsInGlobalCoordinates;
   }


   public double getCurrentDistanceFromEye() {
      return _currentDistanceFromEye;
   }


   public GElevationAnchor getElevationAnchor() {
      return _anchor;
   }


   private List<PanoramicTile> createTopTiles() {
      final List<PanoramicTile> result = new ArrayList<PanoramicTile>(TILE_RHO_SUBDIVISIONS * TILE_THETA_SUBDIVISIONS);

      final double deltaRho = 180d / TILE_RHO_SUBDIVISIONS;
      final double deltaTheta = 360d / TILE_THETA_SUBDIVISIONS;

      Angle lastLat = Angle.ZERO;

      for (int row = 0; row < TILE_RHO_SUBDIVISIONS; row++) {
         Angle lat = lastLat.addDegrees(deltaRho);
         if (lat.degrees + 1d > 180d) {
            lat = Angle.POS180;
         }

         Angle lastLon = Angle.ZERO;

         for (int col = 0; col < TILE_THETA_SUBDIVISIONS; col++) {
            Angle lon = lastLon.addDegrees(deltaTheta);
            if (lon.degrees + 1d > 360d) {
               lon = Angle.POS360;
            }

            result.add(new PanoramicTile(null, new Sector(lastLat, lat, lastLon, lon), 0, row, col));

            lastLon = lon;
         }

         lastLat = lat;
      }

      return result;
   }


   private GPanoramicCompiler.ZoomLevels readZoomLevels() throws IOException {

      final GFileName fileName = GFileName.fromParentAndParts(_panoramicFileName, GPanoramicCompiler.LEVELS_FILE_NAME);

      final GHolder<GPanoramicCompiler.ZoomLevels> result = new GHolder<GPanoramicCompiler.ZoomLevels>(null);
      final GHolder<IOException> exception = new GHolder<IOException>(null);
      final GHolder<Boolean> done = new GHolder<Boolean>(false);

      _loader.load(fileName, -1, false, Integer.MAX_VALUE, new ILoader.IHandler() {
         @Override
         public void loaded(final File file,
                            final long bytesLoaded,
                            final boolean completeLoaded) {

            if (!completeLoaded) {
               return;
            }


            ObjectInputStream is = null;
            try {
               is = new ObjectInputStream(new GZIPInputStream(new FileInputStream(file)));

               final GPanoramicCompiler.ZoomLevels zoomLevels = (GPanoramicCompiler.ZoomLevels) is.readObject();

               result.set(zoomLevels);
            }
            catch (final IOException e) {
               exception.set(new IOException(getExceptionMessage(file), e));
            }
            catch (final ClassNotFoundException e) {
               exception.set(new IOException(getExceptionMessage(file), e));
            }
            catch (final ClassCastException e) {
               exception.set(new IOException(getExceptionMessage(file), e));
            }
            finally {
               GIOUtils.gentlyClose(is);
            }

            done.set(true);
         }


         protected String getExceptionMessage(final File file) {
            return "error processing file: " + (file == null ? "null" : file.getAbsolutePath());
         }


         @Override
         public void loadError(final IOException e) {
            exception.set(e);
            done.set(true);
         }
      });

      while (!done.get()) {
         GUtils.delay(20);
      }

      if (exception.hasValue()) {
         removeInvalidFileFromCache(fileName);
         throw exception.get();
      }

      return result.get();
   }


   private static double computeSurfaceElevation(final DrawContext dc,
                                                 final LatLon latLon) {

      final Globe globe = dc.getGlobe();

      final Vec4 surfacePoint = dc.getTerrain().getSurfacePoint(latLon.latitude, latLon.longitude, 0);
      if (surfacePoint == null) {
         return globe.getElevation(latLon.latitude, latLon.longitude);
      }

      return globe.computePositionFromPoint(surfacePoint).elevation;
   }


   private void asureModelCoordinateOriginTransform(final DrawContext dc,
                                                    final boolean terrainChanged) {
      if (terrainChanged || (_modelCoordinateOriginTransform == null)) {
         Position position = null;

         switch (_anchor) {
            case SEA_LEVEL:
               position = _position;
               break;
            case SURFACE:
               final double surfaceElevation = computeSurfaceElevation(dc, _position);
               position = new Position(_position.latitude, _position.longitude, surfaceElevation + _position.elevation);
               break;
         }

         final Globe globe = dc.getGlobe();
         final double verticalExaggeration = dc.getVerticalExaggeration();
         _modelCoordinateOriginTransform = GWWUtils.computeModelCoordinateOriginTransform(position, globe, verticalExaggeration);
         _modelCoordinateOriginTransform = _modelCoordinateOriginTransform.multiply(Matrix.fromRotationZ(Angle.fromDegrees(_heading.getDegrees())));

         if (isActive()) {
            _lastContext.getCameraController().instantlyGoTo(getPosition(), 0);
         }
      }
   }


   private boolean isVisible(final DrawContext dc,
                             final boolean terrainChanged) {
      final Sphere bounds = getBoundsInModelCoordinates(terrainChanged);
      if (bounds == null) {
         return true;
      }

      final Frustum frustum = dc.getView().getFrustumInModelCoordinates();

      final boolean isVisibleInFrustum = frustum.intersects(bounds);
      if (!isVisibleInFrustum) {
         return false;
      }

      final double proyectedSize = WWMath.computeSizeInWindowCoordinates(dc, bounds);
      if (proyectedSize < MIN_PROYECTED_SIZE) {
         return false;
      }

      return true;
   }


   private Sphere getBoundsInModelCoordinates(final boolean matrixChanged) {
      if (matrixChanged || (_boundsInGlobalCoordinates == null)) {
         _boundsInGlobalCoordinates = calculateBoundsInModelCoordinates();
      }

      return _boundsInGlobalCoordinates;
   }


   private Sphere calculateBoundsInModelCoordinates() {
      final Vec4 center = Vec4.ZERO;
      final Vec4 back = new Vec4(0, 0, -_radius);
      final Vec4 front = new Vec4(0, 0, +_radius);
      final Vec4 up = new Vec4(0, +_radius, 0);
      final Vec4 down = new Vec4(0, -_radius, 0);

      final Vec4[] transformedPoints = GWWUtils.transform(_modelCoordinateOriginTransform, center, back, front, up, down);

      return Sphere.createBoundingSphere(transformedPoints);
   }


   private boolean isTerrainChanged(final DrawContext dc) {
      final Globe globe = dc.getGlobe();
      final double verticalExaggeration = dc.getVerticalExaggeration();

      final boolean terrainChanged;

      final boolean checkViewport = (_anchor == GElevationAnchor.SURFACE);
      if (checkViewport) {
         final Frustum currentFustum = dc.getView().getFrustumInModelCoordinates();

         terrainChanged = ((globe != _lastGlobe) || (verticalExaggeration != _lastVerticalExaggeration) || (!currentFustum.equals(_lastFrustum)));

         if (terrainChanged) {
            _lastGlobe = globe;
            _lastVerticalExaggeration = verticalExaggeration;
            _lastFrustum = currentFustum;
         }
      }
      else {
         terrainChanged = ((globe != _lastGlobe) || (verticalExaggeration != _lastVerticalExaggeration));

         if (terrainChanged) {
            _lastGlobe = globe;
            _lastVerticalExaggeration = verticalExaggeration;
         }
      }

      return terrainChanged;
   }


   public void doRender(final DrawContext dc) {
      // Diego: No need to check here, GPanoramicLayer.doRender() already did it!
      // if (dc.isPickingMode()) {
      //    return;
      //  }

      final boolean terrainChanged = isTerrainChanged(dc);
      asureModelCoordinateOriginTransform(dc, terrainChanged);

      if (!isVisible(dc, terrainChanged)) {
         return;
      }

      calculateDistanceFromEye(dc);

      final Matrix modelViewMatrix = dc.getView().getModelviewMatrix().multiply(_modelCoordinateOriginTransform);
      GWWUtils.toGLArray(modelViewMatrix, _modelViewMatrixArray);

      selectVisibleTiles(dc, terrainChanged, modelViewMatrix);

      if (!_visibleTiles.isEmpty()) {
         dc.addOrderedRenderable(getOrderedRenderable());
      }
   }


   private OrderedRenderable getOrderedRenderable() {
      if (_orderedRenderable == null) {
         _orderedRenderable = new OrderedRenderable() {
            @Override
            public double getDistanceFromEye() {
               return _currentDistanceFromEye;
            }


            @Override
            public void pick(final DrawContext dc,
                             final Point pickPoint) {
               // do nothing on pick
            }


            @Override
            public void render(final DrawContext dc) {
               final GL gl = new DebugGL(dc.getGL());


               gl.glPushAttrib(GL.GL_TEXTURE_BIT | GL.GL_LIGHTING_BIT | GL.GL_DEPTH_BUFFER_BIT /*| GL.GL_HINT_BIT*/);

               gl.glMatrixMode(GL.GL_MODELVIEW);
               gl.glPushMatrix();
               gl.glLoadMatrixf(_modelViewMatrixArray, 0);

               final float opacity = getOpacity();
               final boolean hasOpacity = (opacity > 0) && (opacity < 1);
               if (hasOpacity) {
                  gl.glEnable(GL.GL_BLEND);
                  gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

                  gl.glColor4f(1, 1, 1, opacity);
               }


               try {
                  gl.glEnable(GL.GL_CULL_FACE);
                  gl.glCullFace(GL.GL_BACK);
                  gl.glDisable(GL.GL_DEPTH_TEST);

                  if (_renderWireframe) {
                     gl.glPolygonMode(GL.GL_FRONT, GL.GL_LINE);
                     gl.glColor3f(1, 1, 1);
                     gl.glDisable(GL.GL_TEXTURE_2D);
                     renderVisibleTiles(dc);
                     gl.glEnable(GL.GL_TEXTURE_2D);
                     gl.glPolygonMode(GL.GL_FRONT, GL.GL_FILL);
                  }
                  else {
                     gl.glPolygonMode(GL.GL_FRONT, GL.GL_FILL);

                     renderVisibleTiles(dc);
                  }
               }
               finally {
                  gl.glEnable(GL.GL_DEPTH_TEST);

                  if (hasOpacity) {
                     gl.glDisable(GL.GL_BLEND);
                  }

                  gl.glMatrixMode(GL.GL_MODELVIEW);
                  gl.glPopMatrix();

                  gl.glPopAttrib();
               }
            }
         };
      }
      return _orderedRenderable;
   }


   private void selectVisibleTiles(final DrawContext dc,
                                   final boolean terrainChanged,
                                   final Matrix modelViewMatrix) {

      final boolean recalculate = terrainChanged || _visibleTiles.isEmpty();
      if (recalculate) {
         _visibleTiles.clear();

         final Frustum frustum = dc.getView().getFrustumInModelCoordinates();
         for (final PanoramicTile tile : _topTiles) {
            selectVisibleTiles(dc, frustum, terrainChanged, tile, modelViewMatrix, 0);
         }
      }
   }


   private void selectVisibleTiles(final DrawContext dc,
                                   final Frustum frustum,
                                   final boolean terrainChanged,
                                   final PanoramicTile tile,
                                   final Matrix modelViewMatrix,
                                   final int currentLevel) {

      if (!tile.isVisible(dc, frustum)) {
         tile.setDeepWasVisible(false);
         return;
      }

      if ((currentLevel >= getMaxLevel()) || tile.atBestResolution() || !tile.needToSplit(dc)) {
         tile.setWasVisible(true);
         _visibleTiles.add(tile);
      }
      else {
         for (final PanoramicTile child : tile.getChildren()) {
            selectVisibleTiles(dc, frustum, terrainChanged, child, modelViewMatrix, currentLevel + 1);
         }
      }
   }


   private void calculateDistanceFromEye(final DrawContext dc) {
      final Vec4 panoramicPoint = GWWUtils.computePointFromPosition(_position, dc.getGlobe(), dc.getVerticalExaggeration());
      final Vec4 eyePoint = dc.getView().getCurrentEyePoint();
      final double distance = panoramicPoint.subtract3(eyePoint).getLength3();
      _currentDistanceFromEye = distance;
   }


   private int getMaxLevel() {
      return isActive() ? _maxLevel : getInactiveMaxLevel();
   }


   protected int getInactiveMaxLevel() {
      return 1;
   }


   private final Set<PanoramicTile> _toRenderSortedByLevel = new TreeSet<PanoramicTile>(new Comparator<PanoramicTile>() {
                                                              @Override
                                                              public int compare(final PanoramicTile t1,
                                                                                 final PanoramicTile t2) {
                                                                 if (t1._level < t2._level) {
                                                                    return -1;
                                                                 }
                                                                 else if (t1._level > t2._level) {
                                                                    return 1;
                                                                 }
                                                                 else {
                                                                    final long c1 = t1._column * 1000000L + t1._row;
                                                                    final long c2 = t2._column * 1000000L + t2._row;
                                                                    if (c1 < c2) {
                                                                       return -1;
                                                                    }
                                                                    else if (c1 > c2) {
                                                                       return 1;
                                                                    }
                                                                    else {
                                                                       return 0;
                                                                    }
                                                                 }
                                                              }
                                                           });

   private IGlobeRunningContext     _lastContext;
   private GView                    _lastView;


   private void renderVisibleTiles(final DrawContext dc) {
      _toRenderSortedByLevel.clear();

      for (final PanoramicTile tile : _visibleTiles) {
         final PanoramicTile whatToRender = tile.whatToRender();
         if (whatToRender != null) {
            whatToRender.setWasVisible(true);
            _toRenderSortedByLevel.add(whatToRender);
         }
      }

      for (final PanoramicTile tile : _toRenderSortedByLevel) {
         tile.render(dc);
      }
   }


   public boolean isRenderWireframe() {
      return _renderWireframe;
   }


   public void setRenderWireframe(final boolean renderWireframe) {
      _renderWireframe = renderWireframe;
   }


   public void setHidden(final boolean hidden) {
      _isHidden = hidden;
   }


   public boolean isHidden() {
      return _isHidden;
   }


   /**
    * Use this method to enter into the panoramic. It sets view-control options (restrict camera movement to rotation only, change
    * how mouse-wheel movement is interpreted, activates its own icon in the HUD-Layer, etc...). Also saves the current camera
    * state.
    * 
    * @param view
    * @param application
    */
   public void activate(final IGlobeRunningContext context) {
      setActive(true);

      _lastContext = context;

      final IGlobeApplication application = context.getApplication();

      application.getWidget().prepareForFullScreen();

      final GView view = context.getWorldWindModel().getView();
      _lastView = view;

      view.enterPanoramic(this);

      if (_activationListeners != null) {
         for (final GPanoramic.ActivationListener listener : _activationListeners) {
            listener.activated();
         }
      }

      application.redraw();
   }


   /**
    * Use this method to exit the panoramic. Should be used in conjunction with
    * <code>activate(GView view, GGlobeApplication application)</code>, as it also takes care of restoring the view state that
    * aforementioned method has saved
    * 
    * @param view
    */
   public void deactivate() {
      setActive(false);

      if (_lastView == null) {
         throw new RuntimeException("Can't be null at this point");
      }

      _lastContext.getApplication().getWidget().prepareForNonFullScreen();

      // pruneTilesToUnactivateMaxDepth();


      GGLUtils.invokeOnOpenGLThread(new Runnable() {
         @Override
         public void run() {
            pruneTilesToUnactivateMaxDepth();
         }
      });

      _lastView.exitPanoramic(this);

      if (_activationListeners != null) {
         for (final GPanoramic.ActivationListener listener : _activationListeners) {
            listener.deactivated();
         }
      }
   }


   private void pruneTilesToUnactivateMaxDepth() {
      final int inactiveMaxLevel = getInactiveMaxLevel();
      for (final PanoramicTile tile : _topTiles) {
         tile.pruneTo(inactiveMaxLevel);
      }
   }


   private void setActive(final boolean value) {
      if (_hudIcon != null) {
         _hudIcon.setEnable(value);
      }
      _isActive = value;
   }


   private float getOpacity() {
      return (_isActive || _isHighlighted) //
                                          ? getActiveOpacity() //
                                          : (float) (getInactiveOpacity() * getLayer().getOpacity());
   }


   protected float getInactiveOpacity() {
      return 0.8f;
   }


   protected float getActiveOpacity() {
      return 1;
   }


   private static final PanoramicTile[] EMPTY_TILE_ARRAY = new PanoramicTile[0];


   private class PanoramicTile {

      private final PanoramicTile             _parent;
      private final Sector                    _sector;
      private final List<List<Vertex>>        _quadStrips;

      private int                             _displayList = -1;

      private final int                       _level;
      private final int                       _row;
      private final int                       _column;

      private ILoader.IHandler                _handler;
      private GPair<BufferedImage, GFileName> _cachedTextureImage;
      private GTexture                        _texture;
      private PanoramicTile[]                 _children    = EMPTY_TILE_ARRAY;
      private boolean                         _wasVisible  = false;


      private PanoramicTile(final PanoramicTile parent,
                            final Sector sector,
                            final int level,
                            final int row,
                            final int column) {
         _parent = parent;
         _sector = sector;
         _level = level;
         _row = row;
         _column = column;

         _quadStrips = initializeQuadStrips();
      }


      private void pruneTo(final int depth) {
         if (_level >= depth) {
            cleanChildren();
         }
         else {
            for (final PanoramicTile child : _children) {
               child.pruneTo(depth);
            }
         }
      }


      private void cleanChildren() {
         for (final PanoramicTile child : _children) {
            child.dispose();
         }
         _children = EMPTY_TILE_ARRAY;
      }


      private void dispose() {
         disposeTexture();

         for (final PanoramicTile child : _children) {
            child.dispose();
         }
      }


      private boolean isVisible(final DrawContext dc,
                                final Frustum frustum) {
         final Extent extent = getExtent(dc);
         return (extent != null) && extent.intersects(frustum);
      }


      private void setWasVisible(final boolean isVisible) {
         if (isVisible == _wasVisible) {
            return;
         }

         final boolean oldWasVisible = _wasVisible;
         _wasVisible = isVisible;

         if (oldWasVisible) {
            disposeTexture();
         }
      }


      private void disposeTexture() {
         if (_texture != null) {
            GGLUtils.disposeTexture(_texture);
            _texture = null;
         }
      }


      private void setDeepWasVisible(final boolean value) {
         setWasVisible(value);

         for (final PanoramicTile child : _children) {
            child.setDeepWasVisible(value);
         }
      }


      private Extent getExtent(final DrawContext dc) {
         return EXTENT_CACHE.get(dc, this);
      }


      private void rawRender(final DrawContext dc) {
         final GL gl = new DebugGL(dc.getGL());

         for (final List<Vertex> quadStrip : _quadStrips) {
            gl.glBegin(GL.GL_QUAD_STRIP);

            for (final Vertex vertex : quadStrip) {
               final IVector2 texCoord = vertex._texCoord;
               gl.glTexCoord2f((float) texCoord.x(), (float) vertex._texCoord.y());

               final IVector3 point = vertex._point;
               gl.glVertex3f((float) point.x(), (float) point.y(), (float) point.z());
            }

            gl.glEnd();
         }
      }


      private void forceLoadTexture() {
         getTexture();
      }


      private List<List<Vertex>> initializeQuadStrips() {
         final Angle deltaLon = _sector.getDeltaLon();
         final Angle deltaLat = _sector.getDeltaLat();

         final int tileSlices = (int) Math.round(deltaLon.degrees / 4);
         final int tileStacks = (int) Math.round(deltaLat.degrees / 2);

         final List<List<Vertex>> result = new ArrayList<List<Vertex>>(tileStacks);

         final double initialRho = _sector.getMinLatitude().radians;
         final double initialTheta = _sector.getMinLongitude().radians;

         final double deltaRho = deltaLat.radians / tileStacks;
         final double deltaTheta = deltaLon.radians / tileSlices;

         final double deltaT = 1.0 / tileStacks;
         final double deltaS = 1.0 / tileSlices;


         for (int i = 0; i < tileStacks; i++) {
            final double t = i * deltaT;

            final double rho = initialRho + (i * deltaRho);
            final double sinRho = Math.sin(rho);
            final double cosRho = Math.cos(rho);
            final double sinRhoPlusDeltaRho = Math.sin(rho + deltaRho);
            final double cosRhoPlusDeltaRho = Math.cos(rho + deltaRho);

            final List<Vertex> quadStrip = new ArrayList<Vertex>(tileSlices + 1);
            result.add(quadStrip);
            for (int j = 0; j <= tileSlices; j++) {
               final double s = j * deltaS;

               final double theta = initialTheta + (j * deltaTheta);
               final double sinTheta = Math.sin(theta);
               final double cosTheta = Math.cos(theta);


               final double x1 = -sinTheta * sinRho;
               final double y1 = cosTheta * sinRho;
               final double z1 = -cosRho;
               final IVector3 point1 = new GVector3D(x1, y1, z1).normalized();
               // final IVector3 normal1 = point1.negated().normalized();
               quadStrip.add(new Vertex(point1.scale(_radius), new GVector2D(1.0 - s, t)));

               final double x2 = -sinTheta * sinRhoPlusDeltaRho;
               final double y2 = cosTheta * sinRhoPlusDeltaRho;
               final double z2 = -cosRhoPlusDeltaRho;
               final IVector3 point2 = new GVector3D(x2, y2, z2).normalized();
               //               final IVector3 normal2 = point2.negated().normalized();
               quadStrip.add(new Vertex(point2.scale(_radius), new GVector2D(1.0 - s, t + deltaT)));
            }
         }

         return result;
      }


      private PanoramicTile whatToRender() {
         tryToLoadTexture(_level);

         if ((getTexture() != null) || hasTextureInCache()) {
            return this;
         }

         final PanoramicTile ancestorWithTexture = getNearestAncestorWithTextureInCache();
         if (ancestorWithTexture != null) {
            return ancestorWithTexture;
         }

         return this;
      }


      private void render(final DrawContext dc) {
         tryToLoadTexture(_level);

         if (_displayList < 0) {
            _displayList = QUAD_STRIPS_DISPLAY_LIST_CACHE.getDisplayList(this, dc);

            if (_displayList < 0) {
               redraw(); // enqueue redraw request to be able to initialize the displayList the next render
               return;
            }
         }

         final GTexture texture = getTextureToRender();

         final GL gl = new DebugGL(dc.getGL());
         bindTexture(texture, gl);
         gl.glCallList(_displayList);
         disableTexture(texture, gl);


         if (_renderExtent) {
            final Extent extent = getExtent(dc);
            GWWUtils.renderExtent(dc, extent);
         }
      }


      private final GTexture getTextureToRender() {
         final GTexture texture = getTexture();
         if (texture != null) {
            return texture;
         }

         return getDownloadingTexture();
      }


      private void tryToLoadTexture(final int priority) {
         if ((_cachedTextureImage == null) && (_handler == null)) {
            asyncLoadTexture(priority);
         }
      }


      private PanoramicTile getNearestAncestorWithTextureInCache() {
         if (_parent == null) {
            return null;
         }

         if (_parent.hasTextureInCache()) {
            return _parent;
         }

         return _parent.getNearestAncestorWithTextureInCache();
      }


      private boolean hasTextureInCache() {
         return (getTexture() != null) || _loader.canLoadFromLocalCache(getTileFileName());
      }


      private void disableTexture(final GTexture texture,
                                  final GL gl) {
         if (texture == null) {
            return;
         }

         if (texture.getMustFlipVertically()) {
            gl.glMatrixMode(GL.GL_TEXTURE);
            gl.glPopMatrix();
         }

         texture.disable();
      }


      private void bindTexture(final GTexture texture,
                               final GL gl) {
         if (texture == null) {
            return;
         }


         if (texture.getMustFlipVertically()) {
            gl.glMatrixMode(GL.GL_TEXTURE);
            gl.glPushMatrix();
            gl.glLoadMatrixf(FLIP_VERTICALLY_TRANSFORMATION, 0);
         }

         texture.enable();
         texture.bind();
      }


      private GTexture getTexture() {
         if (_texture == null) {
            _texture = initializeTexture();
         }

         return _texture;
      }


      private GTexture initializeTexture() {
         if (_cachedTextureImage == null) {
            return null;
         }

         final GTexture texture = GTextureLoader.loadTexture(_cachedTextureImage._first, true, false);
         if (texture == null) {
            _context.getLogger().logWarning("Invalid Texture, removing from cache: " + _cachedTextureImage._second);
            removeInvalidFileFromCache(_cachedTextureImage._second);
         }
         return texture;
      }


      private void asyncLoadTexture(final int priority) {
         final GFileName tileFileName = getTileFileName();

         _handler = new ILoader.IHandler() {
            @Override
            public void loaded(final File file,
                               final long bytesLoaded,
                               final boolean completeLoaded) {
               if (!completeLoaded) {
                  return;
               }

               try {
                  _cachedTextureImage = new GPair<BufferedImage, GFileName>(ImageIO.read(file), tileFileName);
               }
               //  catch (final MalformedURLException e) {
               //     e.printStackTrace();
               //  }
               catch (final IOException e) {
                  _context.getLogger().logSevere(e);
                  removeInvalidFileFromCache(tileFileName);
               }

               downloadFinished();
            }


            @Override
            public void loadError(final IOException e) {
               _context.getLogger().logSevere(e);

               downloadFinished();
            }


            private void downloadFinished() {
               _handler = null;
               //               _loadID = null;
               redraw();
            }
         };

         /* _loadID = */
         _loader.load(tileFileName, -1, false, priority, _handler);
      }


      private GFileName getTileFileName() {
         final String levelDirectoryName = Integer.toString(_level);
         final String tileFileName = _row + "-" + _column + ".jpg";

         return GFileName.fromParentAndParts(_panoramicFileName, levelDirectoryName, tileFileName);
      }


      private boolean atBestResolution() {
         int maxResolutionForTile = _maxResolutionInPanoramic;

         if (_level > 1) {
            if ((_sector.getMinLatitude().degrees <= 0) || (_sector.getMaxLatitude().degrees >= 360)) {
               maxResolutionForTile--;
            }
         }

         return (_level >= maxResolutionForTile);
      }


      @Override
      public String toString() {
         return "PanoramicTile [sector=" + _sector + ", level=" + _level + ", cell=" + _row + "-" + _column + "]";
      }


      private Extent calculateExtent() {
         final List<Vec4> points = new ArrayList<Vec4>();

         for (final List<Vertex> quadStrip : _quadStrips) {
            for (final Vertex vertex : quadStrip) {
               points.add(GWWUtils.transform(_modelCoordinateOriginTransform, GWWUtils.toVec4(vertex._point)));
            }
         }

         final Box box = Box.computeBoundingBox(points);
         final Sphere sphere = Sphere.createBoundingSphere(points.toArray(new Vec4[] {}));

         final boolean useBox = box.getRadius() < sphere.getRadius();
         return useBox ? box : sphere;
      }


      private boolean needToSplit(final DrawContext dc) {
         final Extent extent = getExtent(dc);

         final View view = dc.getView();
         final double depth = Math.abs(extent.getCenter().transformBy4(view.getModelviewMatrix()).z);
         final double radiusInPixels = extent.getRadius() / view.computePixelSizeAtDistance(depth);

         // final double proyectedSize = radiusInPixels * radiusInPixels;
         // return (proyectedSize > (GPanoramicCompiler.TILE_WIDTH * GPanoramicCompiler.TILE_HEIGHT));

         // return (radiusInPixels > GPanoramicCompiler.TILE_WIDTH) || (radiusInPixels > GPanoramicCompiler.TILE_HEIGHT);
         return (radiusInPixels > GPanoramicCompiler.TILE_WIDTH);
      }


      private PanoramicTile[] getChildren() {
         //         final PanoramicTile[] _children = initializeChildren();
         if (_children == EMPTY_TILE_ARRAY) {
            _children = initializeChildren();
         }
         return _children;
      }


      private PanoramicTile[] initializeChildren() {
         final Sector[] sectors = _sector.subdivide();

         final int levelPlusOne = _level + 1;
         final int twoTimesRow = _row * 2;
         final int toTimesColumn = _column * 2;

         final PanoramicTile[] children = new PanoramicTile[4];
         children[0] = new PanoramicTile(this, sectors[0], levelPlusOne, twoTimesRow + 1, toTimesColumn + 1);
         children[1] = new PanoramicTile(this, sectors[1], levelPlusOne, twoTimesRow + 1, toTimesColumn + 0);
         children[2] = new PanoramicTile(this, sectors[2], levelPlusOne, twoTimesRow + 0, toTimesColumn + 1);
         children[3] = new PanoramicTile(this, sectors[3], levelPlusOne, twoTimesRow + 0, toTimesColumn + 0);

         return children;
      }


      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + getLayer().hashCode();
         result = prime * result + _column;
         result = prime * result + _row;
         result = prime * result + _level;
         return result;
      }


      @Override
      public boolean equals(final Object obj) {
         if (this == obj) {
            return true;
         }
         if (obj == null) {
            return false;
         }
         if (getClass() != obj.getClass()) {
            return false;
         }
         final PanoramicTile other = (PanoramicTile) obj;
         if (!getLayer().equals(other.getLayer())) {
            return false;
         }
         if (_column != other._column) {
            return false;
         }
         if (_row != other._row) {
            return false;
         }
         if (_level != other._level) {
            return false;
         }
         return true;
      }


      private Layer getLayer() {
         return GPanoramic.this.getLayer();
      }


   }


   public boolean isRenderExtent() {
      return _renderExtent;
   }


   public void setRenderExtent(final boolean renderExtent) {
      _renderExtent = renderExtent;
   }

   //   private final class PanoramicTileKey {
   //      private final PanoramicTile _parent;
   //      private final Sector        _sector;
   //      private final int           _level;
   //      private final int           _row;
   //      private final int           _column;
   //
   //
   //      private PanoramicTileKey(final PanoramicTile parent,
   //                               final Sector sector,
   //                               final int level,
   //                               final int row,
   //                               final int column) {
   //         _parent = parent;
   //         _sector = sector;
   //         _level = level;
   //         _row = row;
   //         _column = column;
   //      }
   //
   //
   //      @Override
   //      public int hashCode() {
   //         final int prime = 31;
   //         int result = 1;
   //         result = prime * result + getLayer().hashCode();
   //         result = prime * result + _column;
   //         result = prime * result + _level;
   //         //         result = prime * result + ((_parent == null) ? 0 : _parent.hashCode());
   //         result = prime * result + _row;
   //         result = prime * result + ((_sector == null) ? 0 : _sector.hashCode());
   //         return result;
   //      }
   //
   //
   //      @Override
   //      public boolean equals(final Object obj) {
   //         if (this == obj) {
   //            return true;
   //         }
   //         if (obj == null) {
   //            return false;
   //         }
   //         if (getClass() != obj.getClass()) {
   //            return false;
   //         }
   //         final PanoramicTileKey other = (PanoramicTileKey) obj;
   //         if (!getLayer().equals(other.getLayer())) {
   //            return false;
   //         }
   //         if (_column != other._column) {
   //            return false;
   //         }
   //         if (_level != other._level) {
   //            return false;
   //         }
   //         if (_row != other._row) {
   //            return false;
   //         }
   //         if (_sector == null) {
   //            if (other._sector != null) {
   //               return false;
   //            }
   //         }
   //         else if (!_sector.equals(other._sector)) {
   //            return false;
   //         }
   //         return true;
   //      }
   //
   //
   //      private GPanoramic getLayer() {
   //         return GPanoramic.this;
   //      }
   //
   //   }


   private static class Vertex {
      private final IVector3 _point;
      private final IVector2 _texCoord;


      private Vertex(final IVector3 point,
                     final IVector2 texCoord) {
         _point = point;
         _texCoord = texCoord;
      }
   }


   public void addActivationListener(final GPanoramic.ActivationListener listener) {
      GAssert.notNull(listener, "listener");

      _activationListeners.add(listener);
   }


   public void removeActivationListener(final GPanoramic.ActivationListener listener) {
      GAssert.notNull(listener, "listener");

      _activationListeners.remove(listener);
   }


   public boolean acceptExitFromESCKey() {
      return true;
   }


   protected void mouseEntered(@SuppressWarnings("unused") final MouseEvent e) {
      if (!_isHighlighted) {
         _isHighlighted = true;
      }
   }


   protected void mouseExited(@SuppressWarnings("unused") final MouseEvent e) {
      if (_isHighlighted) {
         _isHighlighted = false;
      }
   }


   private void redraw() {
      if (_layer instanceof IGlobeLayer) {
         ((IGlobeLayer) _layer).redraw();
      }
   }


   private GTexture getDownloadingTexture() {
      synchronized (_downloadingTextureMutex) {
         if (_downloadingTextureFileName == null) {
            return null;
         }

         if (_downloadingTexture == null) {
            _downloadingTexture = new GTexture(TextureIO.newTexture(
                     _context.getBitmapFactory().getImage(_downloadingTextureFileName), true));
         }

         return _downloadingTexture;
      }
   }


   public void dispose() {
      if (_downloadingTexture != null) {
         _downloadingTexture.dispose();
      }
   }


   private void removeInvalidFileFromCache(final GFileName fileName) {
      _loader.cleanCacheFor(fileName);
   }


}
