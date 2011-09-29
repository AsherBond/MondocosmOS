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


package es.igosoftware.euclid.pointscloud;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import es.igosoftware.concurrent.GConcurrent;
import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.colors.GColorPrecision;
import es.igosoftware.euclid.loading.GBinaryPoints3Loader;
import es.igosoftware.euclid.loading.GPointsLoader;
import es.igosoftware.euclid.loading.GXYZLoader;
import es.igosoftware.euclid.octree.GOctree;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.GVectorPrecision;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.verticescontainer.GVertex3Container;
import es.igosoftware.euclid.verticescontainer.IUnstructuredVertexContainer;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;
import es.igosoftware.euclid.verticescontainer.IVertexContainer.WeightedVertex;
import es.igosoftware.io.GFileName;
import es.igosoftware.logging.GLoggerObject;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GMath;
import es.igosoftware.util.GPredicate;
import es.igosoftware.util.GProgress;


public abstract class GPointsCloud
         extends
            GLoggerObject {


   private static final class Point2I {
      private final int x;
      private final int y;


      private Point2I(final int x1,
                      final int y1) {
         x = x1;
         y = y1;
      }


      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + x;
         result = prime * result + y;
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
         final GPointsCloud.Point2I other = (GPointsCloud.Point2I) obj;
         if (x != other.x) {
            return false;
         }
         return y == other.y;
      }


      @Override
      public String toString() {
         return "(" + x + "," + y + ")";
      }
   }


   private final GFileName       _sourceDirectoryName;
   private final String          _sourceExtension;
   private final GFileName       _targetDirectoryName;
   private final GProjection     _projection;
   private final GAxisAlignedBox _filterBounds;


   public GPointsCloud(final GFileName sourceDirectoryName,
                       final String sourceExtension,
                       final GProjection projection,
                       final GAxisAlignedBox filterBounds,
                       final GFileName targetDirectoryName) {
      _sourceDirectoryName = sourceDirectoryName;
      _sourceExtension = sourceExtension;
      _projection = projection;
      _filterBounds = filterBounds;
      _targetDirectoryName = targetDirectoryName;
   }


   @Override
   public boolean logVerbose() {
      return true;
   }


   public void process(final GVector2D... resolutions) throws IOException {
      final File sourceDirectory = _sourceDirectoryName.asFile();

      final String[] sourceFilesNames = sourceDirectory.list(new FilenameFilter() {
         @Override
         public boolean accept(final File dir,
                               final String name) {
            return name.endsWith("." + _sourceExtension);
         }
      });

      Arrays.sort(sourceFilesNames);

      ensureTargetDirectory();

      //ensureBinaryConversion(sourceDirectory, sourceFilesNames);

      final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices = loadVertices(sourceFilesNames);

      processVertices(vertices, resolutions);
   }


   private void ensureTargetDirectory() throws IOException {
      final File targetDirectory = _targetDirectoryName.asFile();
      if (!targetDirectory.exists()) {
         if (!targetDirectory.mkdirs()) {
            throw new IOException("can't create target directory: \"" + _targetDirectoryName + "\"");
         }
      }
   }


   private IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> loadVertices(final String[] sourceFilesNames)
                                                                                                                         throws IOException {
      final GFileName allFileName = GFileName.fromParentAndParts(_targetDirectoryName, "__all__.bp");

      ensureAllFile(sourceFilesNames, allFileName);

      final GBinaryPoints3Loader loader = new GBinaryPoints3Loader(GPointsLoader.DEFAULT_FLAGS | GPointsLoader.VERBOSE,
               allFileName);


      loader.load();

      final IUnstructuredVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices = loader.getVertices();

      if (_filterBounds != null) {
         //final Object filteredVertices = vertices.select(new IPredicate<IVertexContainer.Vertex<IVector3>>() {
         final IUnstructuredVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> filteredVertices = vertices.select(new GPredicate<IVertexContainer.Vertex<IVector3>>() {
            @Override
            public boolean evaluate(final IVertexContainer.Vertex<IVector3> vertex) {
               return _filterBounds.contains(vertex._point);
            }
         });

         return filteredVertices;
      }

      return vertices;
   }


   private void ensureAllFile(final String[] sourceFilesNames,
                              final GFileName allFileName) throws IOException {

      if (allFileName.asFile().exists()) {
         return;
      }

      final GFileName[] sourceFullFilesNames = new GFileName[sourceFilesNames.length];

      for (int i = 0; i < sourceFilesNames.length; i++) {
         sourceFullFilesNames[i] = GFileName.fromParentAndParts(_sourceDirectoryName, sourceFilesNames[i]);
      }


      // System.out.println(binaryFilesNames);
      final GXYZLoader loader = new GXYZLoader(GVectorPrecision.FLOAT, GColorPrecision.INT, _projection,
               GPointsLoader.DEFAULT_FLAGS | GPointsLoader.VERBOSE, sourceFullFilesNames);

      loader.load();

      final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices = loader.getVertices();

      logInfo("Calculating reference point...");
      final IVector3 referencePoint = vertices.getAverage()._point.rounded();
      logInfo("Reference point: " + referencePoint);

      logInfo("Converting to float with reference point...");
      final GVertex3Container floatVertices = new GVertex3Container(GVectorPrecision.FLOAT, vertices.colorPrecision(),
               vertices.projection(), referencePoint, vertices.size(), vertices.hasIntensities(), vertices.hasColors(),
               vertices.hasNormals());

      for (int i = 0; i < vertices.size(); i++) {
         floatVertices.addPoint(vertices.getVertex(i));
      }

      GBinaryPoints3Loader.save(floatVertices, _projection, allFileName);
   }


   private void processVertices(final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices,
                                final GVector2D[] resolutions) {
      logInfo("Processing " + vertices);
      final GAxisAlignedOrthotope<IVector3, ?> bounds = vertices.getBounds();
      //      logInfo("Bounds: " + bounds);
      //      logInfo("Extent: " + bounds.getExtent());


      final GOctree.DuplicatesPolicy duplicatesPolicy = new GOctree.DuplicatesPolicy() {
         @Override
         public int[] removeDuplicates(final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices1,
                                       final int[] verticesIndexes) {
            final Set<IVector3> selectedPoints = new HashSet<IVector3>();
            final List<Integer> selectedIndices = new ArrayList<Integer>();

            for (final int index : verticesIndexes) {
               final IVector3 point = vertices1.getPoint(index);
               if (!selectedPoints.contains(point)) {
                  selectedPoints.add(point);
                  selectedIndices.add(index);
               }
            }

            return GCollections.toIntArray(selectedIndices);
         }
      };

      final GOctree octree = new GOctree("Vertices", vertices, new GOctree.Parameters(Double.POSITIVE_INFINITY, 1024, true,
               duplicatesPolicy));

      for (final GVector2D resolution : resolutions) {
         reduceVertices(vertices, bounds, octree, resolution);
      }
   }


   private void reduceVertices(final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices,
                               final GAxisAlignedOrthotope<IVector3, ?> bounds,
                               final GOctree octree,
                               final GVector2D resolution) {
      final Point2I gridExtent = calculateGridExtent(bounds, resolution);

      logInfo("Resolution: " + resolution + ", Grid Extent: " + gridExtent);


      final GProgress progress = new GProgress(gridExtent.x * gridExtent.y) {
         @Override
         public void informProgress(final long stepsDone,
                                    final double percent,
                                    final long elapsed,
                                    final long estimatedMsToFinish) {
            logInfo("  " + progressString(stepsDone, percent, elapsed, estimatedMsToFinish));
         }
      };

      processGridInitialization(vertices, bounds, resolution, gridExtent.x, gridExtent.y);

      final ExecutorService executor = GConcurrent.createExecutor(GConcurrent.AVAILABLE_PROCESSORS * 64);

      final int batchWidth = Math.max(Math.round((float) (bounds._extent.x() / resolution._x)) / 200, 1);
      final int batchHeight = Math.max(Math.round((float) (bounds._extent.y() / resolution._y)) / 200, 1);
      logInfo("  Batch Size: " + batchWidth + "x" + batchHeight);
      for (int xFrom = 0; xFrom < gridExtent.x; xFrom += batchWidth) {
         for (int yFrom = 0; yFrom < gridExtent.y; yFrom += batchHeight) {
            final int xTo = Math.min(xFrom + batchWidth, gridExtent.x);
            final int yTo = Math.min(yFrom + batchHeight, gridExtent.y);

            final int finalXFrom = xFrom;
            final int finalYFrom = yFrom;

            executor.execute(new Runnable() {
               @Override
               public void run() {
                  processLeafs(bounds, octree, resolution, gridExtent.x, gridExtent.y, progress, finalXFrom, finalYFrom, xTo, yTo);
               }
            });

         }
      }

      executor.shutdown();
      GConcurrent.awaitTermination(executor);

      processGridFinalization(vertices, resolution, gridExtent.x, gridExtent.y);
   }


   protected abstract void processGridInitialization(final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices,
                                                     final GAxisAlignedOrthotope<IVector3, ?> bounds,
                                                     final GVector2D resolution,
                                                     final int gridWidth,
                                                     final int gridHeight);


   protected abstract void processGridLeaf(final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> leafVertices,
                                           final GVector2D resolution,
                                           final int gridWidth,
                                           final int gridHeight,
                                           final int x,
                                           final int y);


   protected abstract void processGridFinalization(final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices,
                                                   final GVector2D resolution,
                                                   final int gridWidth,
                                                   final int gridHeight);


   private void processLeafs(final GAxisAlignedOrthotope<IVector3, ?> bounds,
                             final GOctree octree,
                             final GVector2D resolution,
                             final int gridWidth,
                             final int gridHeight,
                             final GProgress progress,
                             final int xFrom,
                             final int yFrom,
                             final int xTo,
                             final int yTo) {

      final int width = xTo - xFrom;
      final int height = yTo - yFrom;

      if ((width == 1) && (height == 1)) {
         final int x = xFrom;
         final int y = yFrom;
         final GAxisAlignedBox leafBounds = calculateBoundsForLeaf(bounds, resolution, gridWidth, gridHeight, x, y);
         final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> leafVertices = octree.getVerticesInRegion(leafBounds);
         synchronized (this) {
            processGridLeaf(leafVertices, resolution, gridWidth, gridHeight, x, y);
         }
         progress.stepDone();
      }
      else {
         final List<GAxisAlignedBox> boundsList = new ArrayList<GAxisAlignedBox>(width * height);
         final GAxisAlignedBox[][] leafBoundsGrid = new GAxisAlignedBox[width][height];

         for (int x = xFrom; x < xTo; x++) {
            for (int y = yFrom; y < yTo; y++) {
               final GAxisAlignedBox leafBounds = calculateBoundsForLeaf(bounds, resolution, gridWidth, gridHeight, x, y);
               leafBoundsGrid[x - xFrom][y - yFrom] = leafBounds;
               boundsList.add(leafBounds);
            }
         }

         final GAxisAlignedBox completeBounds = GAxisAlignedBox.merge(boundsList);
         boundsList.clear(); // release some memory

         final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> completeBoundsVertices = octree.getVerticesInRegion(completeBounds);

         if (completeBoundsVertices.size() == 0) {
            for (int x = xFrom; x < xTo; x++) {
               for (int y = yFrom; y < yTo; y++) {
                  synchronized (this) {
                     processGridLeaf(completeBoundsVertices, resolution, gridWidth, gridHeight, x, y);
                  }
                  progress.stepDone();
               }
            }
         }
         else {

            final GOctree.Parameters parameters = new GOctree.Parameters(Math.max(resolution._x, resolution._y), 512, false);
            final GOctree completeBoundsOctree = new GOctree("CompleteBounsOctree (" + xFrom + "x" + yFrom + " -> " + xTo + "x"
                                                             + yTo + ")", completeBoundsVertices, parameters);

            for (int x = xFrom; x < xTo; x++) {
               for (int y = yFrom; y < yTo; y++) {
                  final GAxisAlignedBox leafBounds = leafBoundsGrid[x - xFrom][y - yFrom];

                  final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> leafVertices = completeBoundsOctree.getVerticesInRegion(leafBounds);

                  synchronized (this) {
                     processGridLeaf(leafVertices, resolution, gridWidth, gridHeight, x, y);
                  }
                  progress.stepDone();
               }
            }

         }
      }
   }


   private GAxisAlignedBox calculateBoundsForLeaf(final GAxisAlignedOrthotope<IVector3, ?> bounds,
                                                  final GVector2D resolution,
                                                  final int gridWidth,
                                                  final int gridHeight,
                                                  final int x,
                                                  final int y) {
      final double lowerX = bounds._lower.x() + (x * resolution._x);
      final double upperX = (x < gridWidth - 1) ? GMath.previousDown(lowerX + resolution._x) : lowerX + resolution._x;

      final double lowerY = bounds._lower.y() + (y * resolution._y);
      final double upperY = (y < gridHeight - 1) ? GMath.previousDown(lowerY + resolution._y) : lowerY + resolution._y;

      final GVector3D leafLower = new GVector3D(lowerX, lowerY, bounds._lower.z());
      final GVector3D leafUpper = new GVector3D(upperX, upperY, bounds._upper.z());
      return new GAxisAlignedBox(leafLower, leafUpper);
   }


   private static GPointsCloud.Point2I calculateGridExtent(final GAxisAlignedOrthotope<IVector3, ?> bounds,
                                                           final GVector2D resolution) {
      final IVector3 extent = bounds.getExtent();
      int columnsCount = Math.round((float) (extent.x() / resolution.x()));
      int rowsCount = Math.round((float) (extent.y() / resolution.y()));

      if ((columnsCount * resolution._x) < extent.x()) {
         columnsCount++;
      }
      if ((rowsCount * resolution._y) < extent.y()) {
         rowsCount++;
      }

      return new GPointsCloud.Point2I(columnsCount, rowsCount);
   }


   public static void main(final String[] args) throws IOException {
      System.out.println("Points Cloud 0.1");
      System.out.println("----------------\n");


      final GFileName sourceDirectoryName = GFileName.absolute("home", "dgd", "Desktop", "WORKING", "globe-caceres-data", "mdt");
      final String sourceExtension = "txt";
      final GProjection projection = GProjection.EPSG_23029;

      final GFileName targetDirectoryName = GFileName.absolute("home", "dgd", "Desktop", "WORKING", "mdt-binary");

      final GPointsCloud instance = new GPointsCloud(sourceDirectoryName, sourceExtension, projection, null, targetDirectoryName) {

         private GVertex3Container _simplifiedVertices;


         @Override
         protected void processGridInitialization(final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices,
                                                  final GAxisAlignedOrthotope<IVector3, ?> bounds,
                                                  final GVector2D resolution,
                                                  final int gridWidth,
                                                  final int gridHeight) {
            _simplifiedVertices = new GVertex3Container(vertices.vectorPrecision(), vertices.colorPrecision(),
                     vertices.projection(), gridWidth * gridHeight, vertices.hasIntensities(), vertices.hasColors(),
                     vertices.hasNormals());

         }


         @Override
         protected void processGridLeaf(final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> leafVertices,
                                        final GVector2D resolution,
                                        final int gridWidth,
                                        final int gridHeight,
                                        final int x,
                                        final int y) {
            final WeightedVertex<IVector3> average = leafVertices.getAverage();
            if (average != null) {
               _simplifiedVertices.addPoint(average);
            }
         }


         @Override
         protected void processGridFinalization(final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices,
                                                final GVector2D resolution,
                                                final int gridWidth,
                                                final int gridHeight) {
            logInfo("Simplified Vertices:" + _simplifiedVertices);

            try {
               GXYZLoader.save(_simplifiedVertices,
                        GFileName.absolute("home", "dgd", "Desktop", "simplified" + resolution._x + "x" + resolution._y + ".xyz"));
            }
            catch (final IOException e) {
               e.printStackTrace();
            }

         }


      };

      instance.process(/*new GVector2D(200, 200), new GVector2D(100, 100), new GVector2D(50, 50)*/new GVector2D(25, 25),
               new GVector2D(10, 10));
   }


}
