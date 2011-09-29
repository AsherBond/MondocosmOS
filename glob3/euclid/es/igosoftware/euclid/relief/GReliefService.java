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


package es.igosoftware.euclid.relief;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.bounding.IBounds3D;
import es.igosoftware.euclid.loading.GBinaryPoints3Loader;
import es.igosoftware.euclid.loading.GPointsLoader;
import es.igosoftware.euclid.octree.GOctree;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.GVectorUtils;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.verticescontainer.GCompositeVertexContainer;
import es.igosoftware.euclid.verticescontainer.IUnstructuredVertexContainer;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;
import es.igosoftware.io.GFileName;
import es.igosoftware.logging.GLoggerObject;
import es.igosoftware.util.GMath;
import es.igosoftware.util.LRUCache;


public class GReliefService
         extends
            GLoggerObject {

   private static final boolean                                       VERBOSE    = true;
   private static final int                                           CACHE_SIZE = 90;

   private final String                                               _name;
   private final Map<GAxisAlignedBox, GFileName>                      _tiles     = new HashMap<GAxisAlignedBox, GFileName>();
   public final GAxisAlignedBox                                       bounds;
   private final LRUCache<GAxisAlignedBox, GOctree, RuntimeException> _cache;


   public GReliefService(final String name,
                         final GFileName directoryName) throws IOException {
      _name = name;

      initializeTiles(directoryName);

      bounds = GAxisAlignedBox.merge(_tiles.keySet());

      logInfo("Initialized: tiles=" + _tiles.size() + ", bounds=" + bounds);

      _cache = initializeCache();
   }


   //   public double getCacheHitsRatio() {
   //      return _cache.getHitsRatio();
   //   }

   public void showStatistics() {
      logInfo("Statistics:");
      logInfoUnnamed("  Cache calls: " + _cache.getCallsCount());
      logInfoUnnamed("  Cache hits: " + _cache.getHitsCount() + " (" + GMath.roundTo(100 * _cache.getHitsRatio(), 2) + "%)");
   }


   @Override
   public boolean logVerbose() {
      return GReliefService.VERBOSE;
   }


   @Override
   public String logName() {
      return _name;
   }


   private LRUCache<GAxisAlignedBox, GOctree, RuntimeException> initializeCache() {
      final LRUCache.ValueFactory<GAxisAlignedBox, GOctree, RuntimeException> factory = new LRUCache.ValueFactory<GAxisAlignedBox, GOctree, RuntimeException>() {
         private static final long serialVersionUID = 1L;


         @Override
         public GOctree create(final GAxisAlignedBox box) {
            try {
               final GFileName fileName = _tiles.get(box);

               //final String shortFileName = new File(fileName).getName();
               //logInfo("Loading \"" + shortFileName + "\", box=" + box);
               logInfo("Cache miss, loading " + box);

               final GBinaryPoints3Loader loader = new GBinaryPoints3Loader(GPointsLoader.DEFAULT_FLAGS
               /*| GPointsLoader.VERBOSE*/, fileName);
               loader.load();

               final IUnstructuredVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices = loader.getVertices();

               return new GOctree(fileName.buildPath(), vertices, new GOctree.Parameters(2000, 2000,
                        GReliefService.VERBOSE && false));
            }
            catch (final IOException e) {
               e.printStackTrace();
               return null;
            }

         }
      };

      return new LRUCache<GAxisAlignedBox, GOctree, RuntimeException>(GReliefService.CACHE_SIZE, factory);
   }


   private void initializeTiles(final GFileName directoryName) throws IOException {
      final File directory = directoryName.asFile();

      if (!directory.exists()) {
         throw new IOException("Directory " + directoryName + " doesn't exists");
      }

      logInfo("Reading " + directory.getAbsolutePath() + "...");


      final String[] filesNames = directory.list(new FilenameFilter() {
         @Override
         public boolean accept(final File dir,
                               final String name) {
            return name.trim().toLowerCase().endsWith(".bp");
         }
      });

      Arrays.sort(filesNames);

      logInfo("Found " + filesNames.length + " files");

      for (final String boxFileName : filesNames) {
         processFile(directory, boxFileName);
      }

   }


   private void processFile(final File directory,
                            final String fileName) throws IOException {
      final GAxisAlignedBox box = GAxisAlignedBox.parseString(GVector3D.class, fileName.substring(0, fileName.length() - 3));

      //      System.out.println(fileName);
      //      System.out.println(box);
      _tiles.put(box, GFileName.fromFile(new File(directory, fileName)));
   }


   public IUnstructuredVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> getVertices(final IBounds3D<?> area) {
      final List<GAxisAlignedBox> touchedTiles = getTouchedTiles(area);

      if (touchedTiles.isEmpty()) {
         return new GCompositeVertexContainer<IVector3>();
      }

      IUnstructuredVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> allPoints = (IUnstructuredVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?>) _cache.get(
               touchedTiles.get(0)).getVerticesInRegion(area);


      for (int i = 1; i < touchedTiles.size(); i++) {
         final GAxisAlignedBox touchedTile = touchedTiles.get(i);
         allPoints = allPoints.composedWith((IUnstructuredVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?>) _cache.get(
                  touchedTile).getVerticesInRegion(area));
      }

      return allPoints;
   }


   public double getAverageZ(final IBounds3D<?> area) {
      return GVectorUtils.getAverageZ(getVertices(area));
   }


   public double getMaxZ(final IBounds3D<?> area) {
      return GVectorUtils.getMaxZ(getVertices(area));
   }


   //   public double getAverageZ(final IBoundedGeometry<IVector2, ?, ?> geometry) {
   //      return GVectorUtils.getAverageZ(getVertices(geometry));
   //   }
   //
   //
   //   public double getMaxZ(final IBoundedGeometry<IVector2, ?, ?> geometry) {
   //      return GVectorUtils.getMaxZ(getVertices(geometry));
   //   }


   //   public IVertexContainer<IVector3,?> getVertices(final IBoundedGeometry<IVector2, ?, ?> geometry) {
   //      final IBounds<IVector2, ?> geometryBounds = geometry.getBounds();
   //
   //      final IVertexContainer<IVector3,?> verticesInBounds = getVertices(geometryBounds.asBox());
   //
   //      final IVertexContainer<IVector3,?> verticesInGeometry = verticesInBounds.selectByPoints(new IPredicate<IVector3>() {
   //         @Override
   //         public boolean evaluate(final IVector3 element) {
   //            return geometry.contains(element.asVector2());
   //         }
   //      });
   //
   //      return verticesInGeometry;
   //   }


   private List<GAxisAlignedBox> getTouchedTiles(final IBounds3D<?> area) {
      final ArrayList<GAxisAlignedBox> touchedTiles = new ArrayList<GAxisAlignedBox>(_tiles.size());

      for (final GAxisAlignedBox tileBox : _tiles.keySet()) {
         if (tileBox.touches(area)) {
            touchedTiles.add(tileBox);
         }
      }

      touchedTiles.trimToSize();

      return touchedTiles;
   }


   public static void main(final String[] args) throws IOException {
      System.out.println("LIDAR 0.1");
      System.out.println("----------\n");


      final GReliefService lidar = new GReliefService("LIDAR", GFileName.relative("..", "globe-caceres", "data", "lidar"));
      final GReliefService mdt = new GReliefService("MDT", GFileName.relative("..", "globe-caceres", "data", "mdt"));

      final GAxisAlignedBox completeBounds = GAxisAlignedBox.merge(lidar.bounds, mdt.bounds);
      System.out.println("Complete Bounds=" + completeBounds);


      final double xFrom = 730000.5;
      final double xTo = xFrom + 2000;//730122.5;

      final double yFrom = 4373000.5;
      final double yTo = yFrom + 2000;//4373375.5;

      final double zFrom = completeBounds._lower.z();
      final double zTo = completeBounds._upper.z();


      final GVector3D lower = new GVector3D(xFrom, yFrom, zFrom);
      final GVector3D upper = new GVector3D(xTo, yTo, zTo);
      process(mdt, lidar, new GAxisAlignedBox(lower, upper));


      final GVector3D offset = new GVector3D(-100, -100, 0);
      process(mdt, lidar, new GAxisAlignedBox(lower.add(offset), upper.add(offset)));

      mdt.showStatistics();
      lidar.showStatistics();
   }


   private static void process(final GReliefService mdt,
                               final GReliefService lidar,
                               final IBounds3D<?> area) {
      final double mdtZ = mdt.getAverageZ(area);
      final double lidarZ = lidar.getAverageZ(area);
      final double delta = mdtZ - lidarZ;
      System.out.println("Area=" + area + ", Average Z mdt=" + mdtZ + ", lidar=" + lidarZ + ", delta=" + delta);
   }


}
