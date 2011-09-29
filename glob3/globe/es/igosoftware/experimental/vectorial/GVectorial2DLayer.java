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


package es.igosoftware.experimental.vectorial;


import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.GVectorial2DRenderer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.GJava2DVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IProjectionTool;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.ISymbolizer2D;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.GVector2I;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.experimental.vectorial.symbolizer.GGlobeVectorialSymbolizer2D;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.IGlobeVector2Layer;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.GBooleanLayerAttribute;
import es.igosoftware.globe.attributes.GGroupAttribute;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.io.GFileName;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GPair;
import es.igosoftware.util.GPredicate;
import es.igosoftware.util.GUtils;
import es.igosoftware.util.IFunction;
import es.igosoftware.util.LRUCache;
import es.igosoftware.util.LRUCache.Entry;
import es.igosoftware.utils.GGlobeStateKeyCache;
import es.igosoftware.utils.GWWUtils;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.geom.Box;
import gov.nasa.worldwind.geom.Frustum;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.AbstractLayer;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.SurfaceImage;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import javax.swing.Icon;


public class GVectorial2DLayer
         extends
            AbstractLayer
         implements
            IGlobeVector2Layer {

   private static final int TEXTURE_WIDTH              = 256;
   private static final int TEXTURE_HEIGHT             = 256;

   private static final int TIMEOUT_FOR_CACHED_RESULTS = 200;

   private static final int BYTES_PER_PIXEL            = 4 /* rgba */
                                                       * 4 /* 4 bytes x integer*/;
   private static final int TEXTURE_SIZE_IN_BYTES      = TEXTURE_WIDTH * TEXTURE_HEIGHT * BYTES_PER_PIXEL;

   //   private static final IProjectionTool PROJECTION_TOOL            = new IProjectionTool() {
   //                                                                      @Override
   //                                                                      public IVector2 increment(final IVector2 position,
   //                                                                                                final GProjection projection,
   //                                                                                                final double deltaEasting,
   //                                                                                                final double deltaNorthing) {
   //                                                                         return GWWUtils.increment(position, projection,
   //                                                                                  deltaEasting, deltaNorthing);
   //                                                                      }
   //                                                                   };

   private static final class RenderingKey {
      private final GVectorial2DLayer     _layer;
      private final GAxisAlignedRectangle _tileBounds;
      private final ISymbolizer2D         _symbolizer;
      private final Globe                 _globe;


      private RenderingKey(final GVectorial2DLayer layer,
                           final GAxisAlignedRectangle tileSectorBounds,
                           final Globe globe) {
         _layer = layer;
         _tileBounds = tileSectorBounds;
         _symbolizer = layer._symbolizer;
         _globe = globe;
      }


      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((_globe == null) ? 0 : _globe.hashCode());
         result = prime * result + ((_layer == null) ? 0 : _layer.hashCode());
         result = prime * result + ((_symbolizer == null) ? 0 : _symbolizer.hashCode());
         result = prime * result + ((_tileBounds == null) ? 0 : _tileBounds.hashCode());
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
         final RenderingKey other = (RenderingKey) obj;
         if (_globe == null) {
            if (other._globe != null) {
               return false;
            }
         }
         else if (!_globe.equals(other._globe)) {
            return false;
         }
         if (_layer == null) {
            if (other._layer != null) {
               return false;
            }
         }
         else if (!_layer.equals(other._layer)) {
            return false;
         }
         if (_symbolizer == null) {
            if (other._symbolizer != null) {
               return false;
            }
         }
         else if (!_symbolizer.equals(other._symbolizer)) {
            return false;
         }
         if (_tileBounds == null) {
            if (other._tileBounds != null) {
               return false;
            }
         }
         else if (!_tileBounds.equals(other._tileBounds)) {
            return false;
         }
         return true;
      }


      @Override
      public String toString() {
         return "RenderingKey [layer=" + _layer + ", tileBounds=" + _tileBounds + ", symbolizer=" + _symbolizer + "]";
      }


   }


   private static final LRUCache<RenderingKey, Future<BufferedImage>, RuntimeException> IMAGES_CACHE;


   private static final LinkedList<RendererFutureTask>                                  RENDERING_TASKS = new LinkedList<RendererFutureTask>();


   private static class RendererFutureTask
            extends
               FutureTask<BufferedImage> {

      private final double          _priority;
      private GVectorial2DLayer     _layer;
      private GAxisAlignedRectangle _tileBounds;


      private RendererFutureTask(final RenderingKey key,
                                 final double priority) {
         super(new Callable<BufferedImage>() {


            @Override
            public BufferedImage call() throws Exception {
               final GVectorial2DLayer layer = key._layer;
               final Globe globe = key._globe;
               final GVectorial2DRenderer renderer = layer._renderer;

               final BufferedImage image = new BufferedImage(TEXTURE_WIDTH, TEXTURE_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
               image.setAccelerationPriority(1);
               final IVectorial2DDrawer drawer = new GJava2DVectorial2DDrawer(image);

               final IProjectionTool projectionTool = new IProjectionTool() {
                  @Override
                  public IVector2 increment(final IVector2 position,
                                            final GProjection projection,
                                            final double deltaEasting,
                                            final double deltaNorthing) {
                     return GWWUtils.increment(position, projection, deltaEasting, deltaNorthing, globe);
                  }
               };

               renderer.render(key._tileBounds, new GVector2I(TEXTURE_WIDTH, TEXTURE_HEIGHT), projectionTool, key._symbolizer,
                        drawer);

               layer.redraw();

               return image;
            }


         });

         _priority = priority;
         _tileBounds = key._tileBounds;
         _layer = key._layer;
      }

   }


   private static class RenderingWorker
            extends
               Thread {

      private static int _workerID = 0;


      private RenderingWorker() {
         super("Rendering Worker #" + _workerID++);
         setDaemon(true);
         setPriority(Thread.MIN_PRIORITY);
      }


      @Override
      public void run() {
         while (true) {
            final RendererFutureTask task = findTask();

            if (task == null) {
               GUtils.delay(500);
            }
            else {
               task.run();
            }
         }
      }

   }


   private static RendererFutureTask findTask() {
      synchronized (RENDERING_TASKS) {
         if (RENDERING_TASKS.isEmpty()) {
            return null;
         }

         final RendererFutureTask selectedTask = findBestTaskForCurrentTiles();
         //         if (selectedTask == null) {
         //            selectedTask = findBestTask();
         //         } 

         if (selectedTask != null) {
            RENDERING_TASKS.remove(selectedTask);
            return selectedTask;
         }
      }

      return null;
   }


   private static RendererFutureTask findBestTaskForCurrentTiles() {
      double biggestPriority = Double.NEGATIVE_INFINITY;
      RendererFutureTask selectedTask = null;

      for (final RendererFutureTask task : RENDERING_TASKS) {
         final List<Tile> currentTiles = task._layer._currentTiles;
         synchronized (currentTiles) {
            for (final Tile currentTile : currentTiles) {
               if (task._tileBounds.equals(currentTile._tileBounds)) {
                  final double currentPriority = task._priority;
                  if (currentPriority > biggestPriority) {
                     biggestPriority = currentPriority;
                     selectedTask = task;
                  }
               }
            }
         }
      }

      return selectedTask;
   }


   static {
      final int numberOfThreads = Math.max(Runtime.getRuntime().availableProcessors(), 1);
      //      final int numberOfThreads = 1;
      for (int i = 0; i < numberOfThreads; i++) {
         new RenderingWorker().start();
      }

      final LRUCache.SizePolicy<RenderingKey, Future<BufferedImage>, RuntimeException> sizePolicy;
      sizePolicy = new LRUCache.SizePolicy<GVectorial2DLayer.RenderingKey, Future<BufferedImage>, RuntimeException>() {
         final private long _maxImageCacheSizeInBytes = Runtime.getRuntime().maxMemory() / 3;


         @Override
         public boolean isOversized(final List<Entry<RenderingKey, Future<BufferedImage>, RuntimeException>> entries) {
            final long totalBytes = entries.size() * TEXTURE_SIZE_IN_BYTES;

            return (totalBytes > _maxImageCacheSizeInBytes);
         }
      };


      final LRUCache.ValueFactory<RenderingKey, Future<BufferedImage>, RuntimeException> factory;
      factory = new LRUCache.ValueFactory<RenderingKey, Future<BufferedImage>, RuntimeException>() {
         @Override
         public Future<BufferedImage> create(final RenderingKey key) {
            final double priority = key._tileBounds.area();
            final RendererFutureTask future = new RendererFutureTask(key, priority);
            synchronized (RENDERING_TASKS) {
               RENDERING_TASKS.add(future);
            }
            return future;

            // return _renderer.render(key._tileSectorBounds, key._attributes);
         }
      };

      IMAGES_CACHE = new LRUCache<RenderingKey, Future<BufferedImage>, RuntimeException>(sizePolicy, factory, 0);
   }


   private boolean _showRenderingInProcess;


   private static final class Tile {

      private static final BufferedImage  NO_ANCESTOR_CONTRIBUTION = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR);

      private final Tile                  _parent;
      private final GAxisAlignedRectangle _tileBounds;
      private final IVector2              _tileBoundsExtent;
      private final GVectorial2DLayer     _layer;

      private SurfaceImage                _surfaceImage;
      private BufferedImage               _ancestorContribution;

      private final Globe                 _globe;


      private Tile(final Tile parent,
                   final GAxisAlignedRectangle tileBounds,
                   final GVectorial2DLayer layer,
                   final Globe globe) {

         _parent = parent;

         _tileBounds = tileBounds;
         _tileBoundsExtent = _tileBounds.getExtent();

         _layer = layer;
         _globe = globe;
      }


      private Box getBox(final DrawContext dc) {
         return BOX_CACHE.get(dc, _tileBounds);
      }


      private double computeProjectedPixels(final DrawContext dc) {
         //         final LatLon[] tileSectorCorners = _tileSector.getCorners();

         final List<IVector2> tileSectorCorners = _tileBounds.getVertices();

         final Vec4 firstProjected = GWWUtils.getScreenPoint(dc, tileSectorCorners.get(0));
         double minX = firstProjected.x;
         double maxX = firstProjected.x;
         double minY = firstProjected.y;
         double maxY = firstProjected.y;

         for (int i = 1; i < tileSectorCorners.size(); i++) {
            final Vec4 projected = GWWUtils.getScreenPoint(dc, tileSectorCorners.get(i));

            if (projected.x < minX) {
               minX = projected.x;
            }
            if (projected.y < minY) {
               minY = projected.y;
            }
            if (projected.x > maxX) {
               maxX = projected.x;
            }
            if (projected.y > maxY) {
               maxY = projected.y;
            }
         }


         // calculate the area of the rectangle
         final double width = maxX - minX;
         final double height = maxY - minY;
         final double area = Math.abs(width) * Math.abs(height);
         return area;
      }


      private boolean needToSplit(final DrawContext dc) {
         return computeProjectedPixels(dc) > (TEXTURE_WIDTH * TEXTURE_HEIGHT);
      }


      //      public boolean isResolved() {
      //         final Future<BufferedImage> renderedImageFuture = IMAGES_CACHE.get(createRenderingKey());
      //
      //         return (renderedImageFuture.isDone());
      //      }


      private Tile[] slit() {
         final GAxisAlignedRectangle[] sectors = _tileBounds.subdividedAtCenter();

         final Tile[] subTiles = new GVectorial2DLayer.Tile[4];
         subTiles[0] = new Tile(this, sectors[0], _layer, _globe);
         subTiles[1] = new Tile(this, sectors[1], _layer, _globe);
         subTiles[2] = new Tile(this, sectors[2], _layer, _globe);
         subTiles[3] = new Tile(this, sectors[3], _layer, _globe);

         return subTiles;
      }


      private void preRender(final DrawContext dc) {
         if ((_surfaceImage == null) || (_ancestorContribution != null)) {
            final Future<BufferedImage> renderedImageFuture = IMAGES_CACHE.get(createRenderingKey());

            if (renderedImageFuture.isDone()) {
               try {
                  setImageToSurfaceImage(renderedImageFuture.get());
                  _ancestorContribution = null;
               }
               catch (final InterruptedException e) {}
               catch (final ExecutionException e) {}
            }
         }


         if ((_surfaceImage == null) && (_ancestorContribution == null)) {
            _ancestorContribution = calculateAncestorContribution();
            if (_ancestorContribution != NO_ANCESTOR_CONTRIBUTION) {
               setImageToSurfaceImage(_ancestorContribution);
            }
         }


         if (_surfaceImage != null) {
            _surfaceImage.preRender(dc);
         }

      }


      private void setImageToSurfaceImage(final BufferedImage image) {
         if (image == null) {
            return;
         }

         final Sector tileSector = GWWUtils.toSector(_tileBounds, GProjection.EPSG_4326);
         if (_surfaceImage == null) {
            _surfaceImage = new SurfaceImage(image, tileSector);
         }
         else {
            _surfaceImage.setImageSource(image, tileSector);
         }
      }


      private BufferedImage calculateAncestorContribution() {
         final GPair<Tile, BufferedImage> ancestorAndImage = findNearestAncestorWithImage();

         if (ancestorAndImage == null) {
            return NO_ANCESTOR_CONTRIBUTION;
         }

         final BufferedImage ancestorImage = ancestorAndImage._second;
         if (ancestorImage == null) {
            return NO_ANCESTOR_CONTRIBUTION;
         }

         final Tile ancestor = ancestorAndImage._first;
         ancestor.moveUpInCache();

         final IVector2 scale = _tileBoundsExtent.div(ancestor._tileBoundsExtent);

         final GVector2D imageExtent = new GVector2D(TEXTURE_WIDTH, TEXTURE_HEIGHT);

         final IVector2 topLeft = _tileBounds._lower.sub(ancestor._tileBounds._lower).scale(scale).div(_tileBoundsExtent).scale(
                  imageExtent);

         final IVector2 widthAndHeight = imageExtent.scale(scale);

         final int width = Math.round((float) widthAndHeight.x());
         final int height = Math.round((float) widthAndHeight.y());

         if ((width < 1) || (height < 1)) {
            return null;
         }

         final int x = Math.round((float) topLeft.x());
         final int y = Math.round((float) -(topLeft.y() + widthAndHeight.y() - TEXTURE_HEIGHT)); // flip y


         //         final BufferedImage calculatedImage = new BufferedImage(_layer._attributes._imageWidth, _layer._attributes._imageHeight,
         //                  BufferedImage.TYPE_4BYTE_ABGR);
         //         final Graphics2D g2d = calculatedImage.createGraphics();
         //
         //         g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         //         g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
         //         g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
         //
         //         // destination
         //         final int dx1 = 0;
         //         final int dy1 = 0;
         //         final int dx2 = _layer._attributes._imageWidth;
         //         final int dy2 = _layer._attributes._imageHeight;
         //
         //         // source
         //         final int sx1 = x;
         //         final int sy1 = y;
         //         final int sx2 = x + width;
         //         final int sy2 = y + height;
         //         g2d.drawImage(ancestorImage, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
         //         g2d.dispose();
         //
         //         return calculatedImage;

         final BufferedImage gray = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
         final Graphics2D g2d = gray.createGraphics();
         if (_layer._showRenderingInProcess) {
            g2d.setBackground(new Color(0, 0, 0, 0.4f));
            g2d.clearRect(0, 0, width, height);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN));
         }
         g2d.drawImage(ancestorImage, 0, 0, width, height, x, y, x + width, y + height, null);
         g2d.dispose();
         return gray;

         //         try {
         //            return ancestorImage.getSubimage(x, y, width, height);
         //         }
         //         catch (final RasterFormatException e) {}
         //
         //
         //         return NO_ANCESTOR_CONTRIBUTION;
      }


      private RenderingKey createRenderingKey() {
         return new RenderingKey(_layer, _tileBounds, _globe);
      }


      private GPair<Tile, BufferedImage> findNearestAncestorWithImage() {
         Tile ancestor = _parent;
         synchronized (IMAGES_CACHE) {
            while (ancestor != null) {
               final RenderingKey ancestorKey = ancestor.createRenderingKey();
               final Future<BufferedImage> futureImage = IMAGES_CACHE.getValueOrNull(ancestorKey);
               if ((futureImage != null) && futureImage.isDone()) {
                  try {
                     return new GPair<Tile, BufferedImage>(ancestor, futureImage.get());
                  }
                  catch (final InterruptedException e) {}
                  catch (final ExecutionException e) {}
               }

               ancestor = ancestor._parent;
            }
         }
         return null;
      }


      private void render(final DrawContext dc) {
         if (_surfaceImage != null) {
            _surfaceImage.render(dc);
         }
      }


      private void moveUpInCache() {
         IMAGES_CACHE.get(createRenderingKey());
         //         IMAGES_CACHE.moveUp(createRenderingKey());
      }


   }


   private static final GGlobeStateKeyCache<GAxisAlignedOrthotope<IVector2, ?>, Box>                           BOX_CACHE;

   static {
      BOX_CACHE = new GGlobeStateKeyCache<GAxisAlignedOrthotope<IVector2, ?>, Box>(
               new GGlobeStateKeyCache.Factory<GAxisAlignedOrthotope<IVector2, ?>, Box>() {
                  @Override
                  public Box create(final DrawContext dc,
                                    final GAxisAlignedOrthotope<IVector2, ?> bounds) {
                     final Globe globe = dc.getView().getGlobe();
                     final double verticalExaggeration = dc.getVerticalExaggeration();

                     final Sector sector = GWWUtils.toSector(bounds, GProjection.EPSG_4326);

                     return Sector.computeBoundingBox(globe, verticalExaggeration, sector);
                  }
               });
   }


   private Sector                                                                                              _polygonsSector;


   private GVectorial2DRenderer                                                                                _renderer;
   private final String                                                                                        _name;
   //   private GAxisAlignedOrthotope<IVector2, ?>                                                                  _polygonsBounds;
   private GAxisAlignedRectangle                                                                               _polygonsBounds;
   private final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> _features;


   private List<Tile>                                                                                          _topTiles;
   private final List<Tile>                                                                                    _currentTiles               = new ArrayList<Tile>();


   private final int                                                                                           _fillColorAlpha             = 127;
   private final int                                                                                           _borderColorAlpha           = 255;

   private View                                                                                                _lastView;


   private long                                                                                                _lastCurrentTilesCalculated = -1;


   private final GGlobeVectorialSymbolizer2D                                                                   _symbolizer                 = new GGlobeVectorialSymbolizer2D(
                                                                                                                                                    this);
   private final boolean                                                                                       _verbose;


   //   public GVectorial2DLayer(final String name,
   //                            final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features) {
   //      this(name, features, false);
   //   }


   public GVectorial2DLayer(final String name,
                            final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features,
                            final boolean verbose) {
      GAssert.notNull(name, "name");
      GAssert.notNull(features, "features");

      _name = name;
      _features = features;

      _verbose = verbose;

      featuresChanged(); // force initial calculation of features related info

   }


   private void featuresChanged() {
      _polygonsBounds = _features.getBounds().asRectangle();
      _polygonsSector = GWWUtils.toSector(_polygonsBounds, _features.getProjection());

      if (_polygonsSector == null) {
         _polygonsSector = Sector.FULL_SPHERE;
      }

      _renderer = _features.isEmpty() ? null : new GVectorial2DRenderer(_features, _verbose);
   }


   @Override
   public String getName() {
      return _name;
   }


   private static List<GAxisAlignedRectangle> createTopLevelSectors(final GAxisAlignedRectangle polygonsSector) {

      final List<GAxisAlignedRectangle> allTopLevelSectors = createTopLevelSectors();

      final List<GAxisAlignedRectangle> intersectingSectors = GCollections.select(allTopLevelSectors,
               new GPredicate<GAxisAlignedRectangle>() {
                  @Override
                  public boolean evaluate(final GAxisAlignedRectangle sector) {
                     return sector.touches(polygonsSector);
                  }
               });


      final List<GAxisAlignedRectangle> reducedSectors = GCollections.collect(intersectingSectors,
               new IFunction<GAxisAlignedRectangle, GAxisAlignedRectangle>() {
                  @Override
                  public GAxisAlignedRectangle apply(final GAxisAlignedRectangle sector) {
                     //return tryToReduce(sector);
                     return sector.intersection(polygonsSector);
                  }


                  //                  private GAxisAlignedRectangle tryToReduce(final GAxisAlignedRectangle sector) {
                  //                     if (polygonsSector.isFullInside(sector)) {
                  //                        final GAxisAlignedRectangle[] subdivisions = sector.subdividedAtCenter();
                  //
                  //                        GAxisAlignedRectangle lastTouchedSubdivision = null;
                  //                        for (final GAxisAlignedRectangle subdivision : subdivisions) {
                  //                           if (subdivision.touches(polygonsSector)) {
                  //                              if (lastTouchedSubdivision != null) {
                  //                                 return sector;
                  //                              }
                  //                              lastTouchedSubdivision = subdivision;
                  //                           }
                  //                        }
                  //
                  //                        return tryToReduce(lastTouchedSubdivision);
                  //                     }
                  //
                  //                     return sector;
                  //                  }
               });

      return reducedSectors;
   }


   @Override
   public Icon getIcon(final IGlobeRunningContext context) {
      return context.getBitmapFactory().getSmallIcon(GFileName.relative("vectorial.png"));
   }


   @Override
   public Sector getExtent() {
      return _polygonsSector;
   }


   //   @Override
   //   public GProjection getProjection() {
   //      return GProjection.EPSG_4326;
   //   }


   @Override
   public void redraw() {
      // fire event to force a redraw
      GWWUtils.redraw(_lastView);
   }


   @Override
   public List<ILayerAttribute<?>> getLayerAttributes(final IGlobeRunningContext context) {
      final List<ILayerAttribute<?>> result = new ArrayList<ILayerAttribute<?>>();

      addAdvancedAttributes(result);

      return result;
   }


   private void addAdvancedAttributes(final List<ILayerAttribute<?>> result) {
      final ILayerAttribute<?> showRenderingInProcess = new GBooleanLayerAttribute("Show Rendering In Process",
               "Visualize when the rendering is not yet done", "ShowRenderingInProcess") {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Boolean get() {
            return isShowRenderingInProcess();
         }


         @Override
         public void set(final Boolean value) {
            setShowRenderingInProcess(value);
         }
      };

      //      final ILayerAttribute<?> debugRendering = new GBooleanLayerAttribute("Debug Rendering", "Set the debug rendering setting",
      //               "DebugRendering") {
      //         @Override
      //         public boolean isVisible() {
      //            return true;
      //         }
      //
      //
      //         @Override
      //         public Boolean get() {
      //            return isDebugRendering();
      //         }
      //
      //
      //         @Override
      //         public void set(final Boolean value) {
      //            setDebugRendering(value);
      //         }
      //      };

      result.add(new GGroupAttribute("Advanced", "Settings for advanced users", showRenderingInProcess));
   }


   public int getFillColorAlpha() {
      return _fillColorAlpha;
   }


   public int getBorderColorAlpha() {
      return _borderColorAlpha;
   }


   @Override
   public void clearCache() {
      //      _topTiles = null;
      if (_topTiles != null) {
         for (final Tile topTile : _topTiles) {
            topTile._surfaceImage = null;
         }
      }

      IMAGES_CACHE.clear(new LRUCache.ValuePredicate<GVectorial2DLayer.RenderingKey, Future<BufferedImage>, RuntimeException>() {
         @Override
         public boolean evaluate(final RenderingKey key,
                                 final Future<BufferedImage> value,
                                 final RuntimeException exception) {
            return (key._layer == GVectorial2DLayer.this);
         }
      });

      redraw();
   }


   @Override
   public void doDefaultAction(final IGlobeRunningContext context) {
      context.getCameraController().animatedZoomToSector(getExtent());
   }


   @Override
   public List<? extends ILayerAction> getLayerActions(final IGlobeRunningContext context) {
      return null;
   }


   private Box getBox(final DrawContext dc) {
      // return Sector.computeBoundingBox(globe, verticalExaggeration, _sector);

      return BOX_CACHE.get(dc, _polygonsBounds);
   }


   //   public void setShowExtents(final boolean showExtents) {
   //      _showExtents = showExtents;
   //      redraw();
   //   }


   //   public boolean isShowExtents() {
   //      return _showExtents;
   //   }


   //   private float computeProjectedPixels(final DrawContext dc) {
   //      final long now = System.currentTimeMillis();
   //
   //      // cache the result for TIMEOUT_FOR_CACHED_RESULTS ms
   //      if ((_lastComputedProjectedPixelsTime > 0) || ((now - _lastComputedProjectedPixelsTime) <= TIMEOUT_FOR_CACHED_RESULTS)) {
   //         return _lastComputedProjectedPixels;
   //      }
   //
   //      final Vec4 firstProjected = GWWUtils.getScreenPoint(dc, _sectorCorners[0]);
   //      double minX = firstProjected.x;
   //      double maxX = firstProjected.x;
   //      double minY = firstProjected.y;
   //      double maxY = firstProjected.y;
   //
   //      for (int i = 1; i < _sectorCorners.length; i++) {
   //         final Vec4 projected = GWWUtils.getScreenPoint(dc, _sectorCorners[i]);
   //
   //         if (projected.x < minX) {
   //            minX = projected.x;
   //         }
   //         if (projected.y < minY) {
   //            minY = projected.y;
   //         }
   //         if (projected.x > maxX) {
   //            maxX = projected.x;
   //         }
   //         if (projected.y > maxY) {
   //            maxY = projected.y;
   //         }
   //      }
   //
   //
   //      // calculate the area of the rectangle
   //      final double width = maxX - minX;
   //      final double height = maxY - minY;
   //      final double area = width * height;
   //      final float result = (float) area;
   //
   //      _lastComputedProjectedPixelsTime = now;
   //      _lastComputedProjectedPixels = result;
   //
   //      return result;
   //   }


   private void selectVisibleTiles(final DrawContext dc,
                                   final Tile tile,
                                   final Frustum frustum,
                                   final int currentLevel) {
      if (!tile._tileBounds.touches(_polygonsBounds)) {
         return;
      }

      if (!tile.getBox(dc).intersects(frustum)) {
         return;
      }

      final int maxLevel = 22;
      if ((currentLevel < maxLevel - 1) && tile.needToSplit(dc)) {
         final Tile[] children = tile.slit();

         //         final boolean allResolved = GCollections.allSatisfy(children, new GPredicate<Tile>() {
         //            @Override
         //            public boolean evaluate(final Tile element) {
         //               return tile.isResolved();
         //            }
         //         });
         //         final boolean allResolved = true;
         //
         //         if (allResolved) {
         for (final Tile child : children) {
            selectVisibleTiles(dc, child, frustum, currentLevel + 1);
         }
         return;
         //         }
      }

      _currentTiles.add(tile);
   }


   private void calculateCurrentTiles(final DrawContext dc) {
      if (_topTiles == null) {
         final List<GAxisAlignedRectangle> topLevelSectors = createTopLevelSectors(_polygonsBounds);

         _topTiles = new ArrayList<Tile>(topLevelSectors.size());
         for (final GAxisAlignedRectangle topLevelSector : topLevelSectors) {
            _topTiles.add(new Tile(null, topLevelSector, this, dc.getGlobe()));
         }
      }

      final long now = System.currentTimeMillis();

      // cache the result for TIMEOUT_FOR_CACHED_RESULTS ms
      if ((_lastCurrentTilesCalculated > 0) && ((now - _lastCurrentTilesCalculated) <= TIMEOUT_FOR_CACHED_RESULTS)) {
         return;
      }

      final Frustum frustum = dc.getView().getFrustumInModelCoordinates();
      synchronized (_currentTiles) {
         _currentTiles.clear();
         for (final Tile tile : _topTiles) {
            selectVisibleTiles(dc, tile, frustum, 0);
         }
      }

      _lastCurrentTilesCalculated = now;
   }


   @Override
   public boolean isLayerInView(final DrawContext dc) {
      if (_features.isEmpty()) {
         return false;
      }

      final Box extent = getBox(dc);
      final Frustum frustum = dc.getView().getFrustumInModelCoordinates();
      if (!extent.intersects(frustum)) {
         return false;
      }

      //      final boolean bigEnough = (computeProjectedPixels(dc) >= 25);
      //      return bigEnough;
      return true;
   }


   @Override
   protected final void doPreRender(final DrawContext dc) {
      _lastView = dc.getView();

      calculateCurrentTiles(dc);

      for (final Tile tile : _currentTiles) {
         tile.preRender(dc);
      }
   }


   @Override
   protected final void doRender(final DrawContext dc) {
      // already done in isLayerInView();
      //      final boolean bigEnough = (computeProjectedPixels(dc) >= 25);
      //      if (!bigEnough) {
      //         return;
      //      }

      //      if (_showExtents) {
      //         renderExtents(dc);
      //      }

      for (final Tile tile : _currentTiles) {
         tile.render(dc);
      }

      if (_topTiles != null) {
         for (final Tile topTile : _topTiles) {
            topTile.moveUpInCache();
         }
      }


      GWWUtils.checkGLErrors(dc);
   }


   //   private void renderExtents(final DrawContext dc) {
   //      //      getBox(dc).render(dc);
   //
   //      final GL gl = dc.getGL();
   //      GWWUtils.pushOffset(gl);
   //
   //      for (final Tile tile : _currentTiles) {
   //         tile.renderExtent(dc);
   //      }
   //
   //      GWWUtils.popOffset(gl);
   //   }


   private static GAxisAlignedRectangle[] createWordQuadrants() {
      final GAxisAlignedRectangle wholeWorld = new GAxisAlignedRectangle(new GVector2D(-Math.PI, -Math.PI / 2), new GVector2D(
               Math.PI, Math.PI / 2));


      final GAxisAlignedRectangle[] hemispheres = wholeWorld.subdividedByY();
      final GAxisAlignedRectangle south = hemispheres[0];
      final GAxisAlignedRectangle north = hemispheres[1];


      final GAxisAlignedRectangle[] northDivisions = north.subdividedByX();
      final GAxisAlignedRectangle northWest = northDivisions[0];
      final GAxisAlignedRectangle northEast = northDivisions[1];


      final GAxisAlignedRectangle[] southDivisions = south.subdividedByX();
      final GAxisAlignedRectangle southWest = southDivisions[0];
      final GAxisAlignedRectangle southEast = southDivisions[1];


      return new GAxisAlignedRectangle[] {
                        northWest,
                        northEast,
                        southWest,
                        southEast
      };
   }


   private static List<GAxisAlignedRectangle> createTopLevelSectors() {
      final GAxisAlignedRectangle[] wordQuadrants = createWordQuadrants();

      final GAxisAlignedRectangle[] northWestDivisions = wordQuadrants[0].subdividedByX();
      final GAxisAlignedRectangle[] northEastDivisions = wordQuadrants[1].subdividedByX();
      final GAxisAlignedRectangle[] southWestDivisions = wordQuadrants[2].subdividedByX();
      final GAxisAlignedRectangle[] southEastDivisions = wordQuadrants[3].subdividedByX();

      return Arrays.asList( //
               northWestDivisions[0], northWestDivisions[1], //
               northEastDivisions[0], northEastDivisions[1], //
               southWestDivisions[0], southWestDivisions[1], //
               southEastDivisions[0], southEastDivisions[1]);
   }


   @Override
   public GGlobeVectorialSymbolizer2D getSymbolizer() {
      return _symbolizer;
   }


   @Override
   public IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> getFeaturesCollection() {
      return _features;
   }


   @Override
   public String toString() {
      return "GPolygon2DLayer [name=" + _name + ", features=" + _features + "]";
   }


   public boolean isShowRenderingInProcess() {
      return _showRenderingInProcess;
   }


   public void setShowRenderingInProcess(final boolean newValue) {
      if (newValue == _showRenderingInProcess) {
         return;
      }

      _showRenderingInProcess = newValue;

      firePropertyChange("ShowRenderingInProcess", !newValue, newValue);
   }


}
