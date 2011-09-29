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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.colors.GColorPrecision;
import es.igosoftware.euclid.loading.GBinaryPoints3Loader;
import es.igosoftware.euclid.loading.GPointsLoader;
import es.igosoftware.euclid.loading.GXYZLoader;
import es.igosoftware.euclid.octree.GOTLeafNode;
import es.igosoftware.euclid.octree.GOctree;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GMutableVector2;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.GVectorPrecision;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.verticescontainer.IUnstructuredVertexContainer;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;
import es.igosoftware.io.GFileName;
import es.igosoftware.logging.GLoggerObject;
import es.igosoftware.util.GMath;


public class GReliefProcessor
         extends
            GLoggerObject {
   private final GVectorPrecision    _vectorPrecision;
   private final GColorPrecision     _colorPrecision;
   private final GProjection         _projection;

   private final GFileName           _sourceDirectoryName;
   private final String              _extension;
   private final GFileName           _targetDirectoryName;

   private int                       _verticesCounter;
   private GMutableVector2<IVector2> _resolutionAverage;

   private final int                 _maxPointsInLeaf;
   private final boolean             _verbose;


   public GReliefProcessor(final GVectorPrecision vectorPrecision,
                           final GColorPrecision colorPrecision,
                           final GProjection projection,
                           final GFileName sourceDirectoryName,
                           final String extension,
                           final GFileName targetDirectoryName,
                           final int maxPointsInLeaf,
                           final boolean verbose) {
      _vectorPrecision = vectorPrecision;
      _colorPrecision = colorPrecision;
      _projection = projection;
      _sourceDirectoryName = sourceDirectoryName;
      _extension = extension;
      _targetDirectoryName = targetDirectoryName;

      _maxPointsInLeaf = maxPointsInLeaf;
      _verbose = verbose;
   }


   @Override
   public boolean logVerbose() {
      return _verbose;
   }


   public void process() throws IOException {
      final long start = System.currentTimeMillis();


      if (!_sourceDirectoryName.exists()) {
         throw new IOException("source directory (" + _sourceDirectoryName + ") doesn't exists");
      }


      if (!_targetDirectoryName.exists()) {
         logInfo("Creating target directory (" + _targetDirectoryName + ")...");
         _targetDirectoryName.asFile().mkdirs();
      }


      final String[] xyzFilesNames = _sourceDirectoryName.asFile().list(new FilenameFilter() {
         @Override
         public boolean accept(final File dir,
                               final String name) {
            return name.trim().toLowerCase().endsWith("." + _extension);
         }
      });

      Arrays.sort(xyzFilesNames);

      logInfo("Processing " + _sourceDirectoryName + "...");
      logIncreaseIdentationLevel();
      logInfo("Found " + xyzFilesNames.length + " files to process");

      //      final ExecutorService executor = GConcurrent.createExecutor(Runtime.getRuntime().availableProcessors());

      _resolutionAverage = new GMutableVector2<IVector2>(GVector2D.ZERO);
      _verticesCounter = 0;
      for (int i = 0; i < xyzFilesNames.length; i++) {
         final String sourceFileName = xyzFilesNames[i];

         processFile(GFileName.relative(sourceFileName), i + 1, xyzFilesNames.length);
      }


      _resolutionAverage.div(_verticesCounter);

      logDecreaseIdentationLevel();

      final long elapsed = System.currentTimeMillis() - start;
      logInfo("Processed in " + GMath.roundTo(elapsed / 1000f, 1) + " secs");
      logInfo("Points: " + _verticesCounter);
      logInfo("Average Resolution: " + _resolutionAverage);
   }


   private void processFile(final GFileName sourceFileName,
                            final int i,
                            final int filesCount) throws IOException {
      logInfo("Processing " + sourceFileName + " (" + i + "/" + filesCount + ")...");
      logIncreaseIdentationLevel();

      final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices = getVertices(sourceFileName);

      synchronized (this) {
         _verticesCounter += vertices.size();
      }

      calculateResolution(vertices);

      final GOctree gOctree = new GOctree("OctGrid: " + sourceFileName, vertices, new GOctree.Parameters(1000000,
               _maxPointsInLeaf, _verbose));

      for (final GOTLeafNode leaf : gOctree.getAllLeafs()) {
         final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> leafPoints = leaf.getVertices();

         saveVertices(leafPoints);
      }

      logDecreaseIdentationLevel();
   }


   private IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> getVertices(final GFileName sourceFileName)
                                                                                                                       throws IOException {
      final GFileName fullSourceFileName = GFileName.fromParts(_sourceDirectoryName, sourceFileName);

      final int flags = GPointsLoader.DEFAULT_FLAGS | GPointsLoader.VERBOSE;
      final GXYZLoader loader = new GXYZLoader(_vectorPrecision, _colorPrecision, _projection, flags, fullSourceFileName);
      loader.load();

      return loader.getVertices();
   }


   private GFileName targetFullName(final String targetFileName) {
      return GFileName.fromParentAndParts(_targetDirectoryName, targetFileName);
   }


   private void calculateResolution(final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices) {
      final List<Double> xs = new ArrayList<Double>();
      final List<Double> ys = new ArrayList<Double>();

      final Iterator<IVector3> iterator = vertices.pointsIterator();
      while (iterator.hasNext()) {
         final IVector3 point = iterator.next();
         xs.add(point.x());
         ys.add(point.y());
      }

      final GVector2D xyResolution = new GVector2D(averageDelta(xs), averageDelta(ys));
      logInfo("Resolution=" + xyResolution);

      final IVector2 weightResolution = xyResolution.scale(vertices.size());
      synchronized (_resolutionAverage) {
         _resolutionAverage.add(weightResolution);
      }
   }


   private double averageDelta(final List<Double> numbers) {
      Collections.sort(numbers);

      double deltaSum = 0.0;
      int deltaCounter = 0;

      double previous = numbers.get(0);
      for (int i = 1; i < numbers.size(); i++) {
         final double current = numbers.get(i);
         final double delta = current - previous;
         if (delta > Double.MIN_VALUE) {
            deltaSum += delta;
            deltaCounter++;
         }
         previous = current;
      }

      return deltaSum / deltaCounter;
   }


   private void saveVertices(final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices) throws IOException {
      //final GAxisAlignedBox bounds = GAxisAlignedBox.minimumBoundingBox(vertices.pointsIterator());
      final GAxisAlignedOrthotope<IVector3, ?> bounds = vertices.getBounds();
      logInfo("Bounds: " + bounds);

      final String targetFileName = bounds.asParseableString() + ".bp";
      final GFileName targetFullFileName = targetFullName(targetFileName);

      GBinaryPoints3Loader.save((IUnstructuredVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?>) vertices,
               targetFullFileName);
   }


}
