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


package es.igosoftware.euclid.loading;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import es.igosoftware.euclid.colors.GColorF;
import es.igosoftware.euclid.colors.GColorI;
import es.igosoftware.euclid.colors.GColorPrecision;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.matrix.GMatrix33D;
import es.igosoftware.euclid.matrix.GMatrix44D;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector2I;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.GVector3F;
import es.igosoftware.euclid.vector.GVectorPrecision;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.verticescontainer.GPtx3Group;
import es.igosoftware.euclid.verticescontainer.GStructuredPtxVertex3Container;
import es.igosoftware.euclid.verticescontainer.GStructuredSubVertexContainer;
import es.igosoftware.euclid.verticescontainer.IStructuredVertexContainer;
import es.igosoftware.euclid.verticescontainer.IStructuredVertexContainer.StructuredVertex;
import es.igosoftware.io.GFileName;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.util.GMath;
import es.igosoftware.util.GProgress;
import es.igosoftware.util.GStringUtils;
import es.igosoftware.util.XStringTokenizer;


public final class GPTX3Loader
         extends
            GStructuredFilePointsLoader<IVector3, GPtx3Group> {

   private static final int       MAX_MARK_SIZE = 1024 * 1024 * 256; // max size of input file for reset after mark.

   private final GVectorPrecision _vectorPrecision;
   private final GColorPrecision  _colorPrecision;
   private final GProjection      _projection;
   private final boolean          _intensitiesFromColor;
   private final boolean          _includeEmptyPoints;
   private final boolean          _includeEmptyButColoredPoints;

   private boolean                _hasIntensities;
   private boolean                _hasColors;
   //   private boolean                _hasNormals;
   //   private boolean                _hasUserData;

   // header
   private int                    _columns;
   private int                    _rows;
   private IVector3               _translationVector;
   private GMatrix33D             _rotationMatrix;
   private GMatrix44D             _transformationMatrix;


   public GPTX3Loader(final GVectorPrecision vectorPrecision,
                      final GColorPrecision colorPrecision,
                      final GProjection projection,
                      final int flags,
                      final GFileName... fileNames) {
      this(vectorPrecision, colorPrecision, projection, false, false, false, flags, fileNames);
   }


   public GPTX3Loader(final GVectorPrecision vectorPrecision,
                      final GColorPrecision colorPrecision,
                      final GProjection projection,
                      final boolean intensitiesFromColor,
                      final boolean includeEmptyPoints,
                      final boolean includeEmptyButColoredPoints,
                      final int flags,
                      final GFileName... fileNames) {

      super(flags, fileNames);

      _intensitiesFromColor = intensitiesFromColor;
      _includeEmptyPoints = includeEmptyPoints;
      _includeEmptyButColoredPoints = includeEmptyButColoredPoints;
      _vectorPrecision = vectorPrecision;
      _colorPrecision = colorPrecision;
      _projection = projection;

   }


   //   private BitSet detectShape() throws IOException {
   //
   //      return null;
   //   }
   //
   //
   //   private BitSet detectShape(final String fileName) throws IOException {
   //
   //      return null;
   //   }


   private float intensityFromColor(final IColor color) {
      //      return color.getLuminance();
      return color.getBrightness();
   }


   private void readHeader(final LineNumberReader input,
                           final GProgress progress) throws IOException {

      int headerLength = 0;
      String line = null;
      try {
         line = input.readLine();
         _columns = Integer.parseInt(line);
         headerLength += line.length();
         line = input.readLine();
         _rows = Integer.parseInt(line);
         headerLength += line.length();

         // translation
         line = input.readLine();
         final double[] translationA = XStringTokenizer.nextDoubleTokens(line, 3);
         headerLength += line.length();
         _translationVector = new GVector3D(translationA[0], translationA[1], translationA[2]);

         // rotation matrix
         line = input.readLine();
         final double[] rotationA0 = XStringTokenizer.nextDoubleTokens(line, 3);
         headerLength += line.length();
         line = input.readLine();
         final double[] rotationA1 = XStringTokenizer.nextDoubleTokens(line, 3);
         headerLength += line.length();
         line = input.readLine();
         final double[] rotationA2 = XStringTokenizer.nextDoubleTokens(line, 3);
         headerLength += line.length();

         _rotationMatrix = GMatrix33D.createMatrix(rotationA0[0], rotationA0[1], rotationA0[2], //    
                  rotationA1[0], rotationA1[1], rotationA1[2], //
                  rotationA2[0], rotationA2[1], rotationA2[2]);
         _rotationMatrix = _rotationMatrix.transposed();

         // complete transformation matrix (includes translation and rotation)
         line = input.readLine();
         final double[] completeA0 = XStringTokenizer.nextDoubleTokens(line, 4);
         headerLength += line.length();
         line = input.readLine();
         final double[] completeA1 = XStringTokenizer.nextDoubleTokens(line, 4);
         headerLength += line.length();
         line = input.readLine();
         final double[] completeA2 = XStringTokenizer.nextDoubleTokens(line, 4);
         headerLength += line.length();
         line = input.readLine();
         final double[] completeA3 = XStringTokenizer.nextDoubleTokens(line, 4);
         headerLength += line.length();

         _transformationMatrix = GMatrix44D.createMatrix(completeA0[0], completeA0[1], completeA0[2], completeA0[3], //
                  completeA1[0], completeA1[1], completeA1[2], completeA1[3], //
                  completeA2[0], completeA2[1], completeA2[2], completeA2[3], //
                  completeA3[0], completeA3[1], completeA3[2], completeA3[3]);
         _transformationMatrix = _transformationMatrix.transpose();

         progress.stepsDone(headerLength);

      }
      catch (final NumberFormatException e) {
         throw new IOException(e + " in line #" + input.getLineNumber() + ": \"" + line + "\"");
      }

   }


   //   private void readPoints(final LineNumberReader input,
   //                           final GStructuredPtxVertex3Container vertices,
   //                           final GPtx3Group ptxGroup,
   //                           final boolean includeEmptyPoints,
   //                           final GProgress progress) throws IOException {
   //
   //      int readLines = 0;
   //      int rejected = 0;
   //      int accepted = 0;
   //      int col = 0;
   //      int row = 0;
   //
   //      final int pointsCount = getPointsCount();
   //      vertices.ensureCapacity(vertices.size() + pointsCount);
   //
   //      String line = null;
   //      try {
   //         line = input.readLine();
   //         boolean inGroup = true;
   //
   //         while ((line != null) && inGroup) {
   //
   //            readLines++;
   //
   //            final XStringTokenizer tokenizer = new XStringTokenizer(line);
   //
   //            final IVector3 point = parsePoint(tokenizer);
   //
   //            if ((!includeEmptyPoints) && (point.closeToZero())) {
   //
   //               rejected++;
   //            }
   //            else {
   //
   //               accepted++;
   //
   //               float intensity = parseIntensity(tokenizer);
   //               IColor color = parseColor(tokenizer);
   //
   //               if (_intensitiesFromColor) {
   //                  intensity = intensityFromColor(color);
   //                  color = null;
   //               }
   //
   //               final GVector2I rowColumn = new GVector2I(row, col);
   //
   //               vertices.addPoint(point, intensity, color, rowColumn, ptxGroup);
   //
   //            }
   //
   //            row++;
   //            if (row >= _rows) {
   //               col++;
   //               row = 0;
   //            }
   //
   //            progress.stepsDone(line.length() + 1);
   //
   //            inGroup = readLines < pointsCount;
   //
   //            if (inGroup) {
   //               line = input.readLine();
   //            }
   //
   //         }
   //
   //         if (pointsCount != readLines) {
   //            throw new IOException("Invalid points count, expected: " + pointsCount + ", read: " + readLines);
   //         }
   //
   //         vertices.trimToSize(); // compact memory
   //
   //         logInfo("Group #" + ptxGroup.hashCode() + " read " + readLines + " lines, got " + accepted + " valid points, rejected: "
   //                 + rejected);
   //
   //      }
   //      catch (final NumberFormatException e) {
   //         throw new IOException(e + " in line #" + input.getLineNumber() + ": \"" + line + "\"");
   //      }
   //
   //   }

   private void readPoints(final LineNumberReader input,
                           final GStructuredPtxVertex3Container vertices,
                           final GPtx3Group ptxGroup,
                           final boolean includeEmptyPoints,
                           final boolean includeEmptyButColoredPoints,
                           final GProgress progress) throws IOException {

      int readLines = 0;
      int rejected = 0;
      int accepted = 0;
      int col = 0;
      int row = 0;

      final int pointsCount = getPointsCount();
      vertices.ensureCapacity(vertices.size() + pointsCount);

      String line = null;
      try {
         line = input.readLine();
         boolean inGroup = true;

         while ((line != null) && inGroup) {

            readLines++;

            final XStringTokenizer tokenizer = new XStringTokenizer(line);

            final IVector3 point = parsePoint(tokenizer);

            if ((!includeEmptyPoints) && (point.closeToZero())) {

               float intensity = parseIntensity(tokenizer);
               IColor color = parseColor(tokenizer);

               if (_intensitiesFromColor) {
                  intensity = intensityFromColor(color);
                  color = null;
               }

               // if ((includeEmptyButColoredPoints) && (!(GMath.closeToZero(intensity)) && (color.closeTo(GColorF.BLACK)))) {
               if ((includeEmptyButColoredPoints) && (!GMath.closeToZero(intensity))) {

                  accepted++;

                  final GVector2I rowColumn = new GVector2I(row, col);

                  vertices.addPoint(point, intensity, color, rowColumn, ptxGroup);

               }
               else {
                  rejected++;
               }
            }
            else {

               accepted++;

               float intensity = parseIntensity(tokenizer);
               IColor color = parseColor(tokenizer);

               if (_intensitiesFromColor) {
                  intensity = intensityFromColor(color);
                  color = null;
               }

               final GVector2I rowColumn = new GVector2I(row, col);

               vertices.addPoint(point, intensity, color, rowColumn, ptxGroup);

            }

            row++;
            if (row >= _rows) {
               col++;
               row = 0;
            }

            progress.stepsDone(line.length() + 1);

            inGroup = readLines < pointsCount;

            if (inGroup) {
               line = input.readLine();
            }

         }

         if (pointsCount != readLines) {
            throw new IOException("Invalid points count, expected: " + pointsCount + ", read: " + readLines);
         }

         vertices.trimToSize(); // compact memory

         logInfo("Group #" + ptxGroup.hashCode() + " read " + readLines + " lines, got " + accepted + " valid points, rejected: "
                 + rejected);

      }
      catch (final NumberFormatException e) {
         throw new IOException(e + " in line #" + input.getLineNumber() + ": \"" + line + "\"");
      }

   }


   public int getPointsCount() {
      return _columns * _rows;
   }


   private IVector3 parsePoint(final XStringTokenizer tokenizer) {

      if (_vectorPrecision == GVectorPrecision.DOUBLE) {
         final double x = tokenizer.nextDoubleToken();
         final double y = tokenizer.nextDoubleToken();
         final double z = tokenizer.nextDoubleToken();
         return new GVector3D(x, y, z);
      }

      final float x = tokenizer.nextFloatToken();
      final float y = tokenizer.nextFloatToken();
      final float z = tokenizer.nextFloatToken();
      return new GVector3F(x, y, z);
   }


   private float parseIntensity(final XStringTokenizer tokenizer) {
      return _hasIntensities ? tokenizer.nextFloatToken() : 0;
   }


   private IColor parseColor(final XStringTokenizer tokenizer) {

      if (_hasColors) {

         final float red = (float) tokenizer.nextIntToken() / 255;
         final float green = (float) tokenizer.nextIntToken() / 255;
         final float blue = (float) tokenizer.nextIntToken() / 255;

         if (_colorPrecision == GColorPrecision.FLOAT3) {
            return GColorF.newRGB(red, green, blue);
         }

         return GColorI.newRGB(red, green, blue);
      }

      return null;
   }


   @Override
   protected void startLoad(final int filesCount) {
      //      final BitSet intensitiesColors = detectShape();
      //      _hasIntensities = intensitiesColors.get(0);
      //      _hasColors = intensitiesColors.get(1);
      _hasIntensities = true;
      _hasColors = true;
   }


   @Override
   protected GStructuredPtxVertex3Container loadVerticesFromFile(final GFileName fileName) throws IOException {


      LineNumberReader input = null;
      try {
         input = new LineNumberReader(new InputStreamReader(new FileInputStream(fileName.buildPath())));

         final GProgress progress = new GProgress(fileName.asFile().length()) {
            @Override
            public void informProgress(final long stepsDone,
                                       final double percent,
                                       final long elapsed,
                                       final long estimatedMsToFinish) {
               if (isFlagged(VERBOSE)) {
                  logInfo("  \"" + fileName.buildPath() + "\" " + progressString(stepsDone, percent, elapsed, estimatedMsToFinish));
               }
            }
         };

         readHeader(input, progress);

         final GPtx3Group firstPtxGroup = new GPtx3Group(_vectorPrecision, _colorPrecision, _projection, _hasIntensities,
                  _hasColors, false, false, _rows, _columns, _translationVector, _rotationMatrix, _transformationMatrix);

         final GStructuredPtxVertex3Container vertices = new GStructuredPtxVertex3Container(_vectorPrecision, _colorPrecision,
                  _projection, getPointsCount(), _hasIntensities || (_hasColors && _intensitiesFromColor), 0,
                  _hasColors && !_intensitiesFromColor, null, false, null, false, 0, true, GVectorPrecision.INT, true,
                  GVector2I.ZERO, firstPtxGroup);

         readPoints(input, vertices, firstPtxGroup, _includeEmptyPoints, _includeEmptyButColoredPoints, progress);

         input.mark(MAX_MARK_SIZE); // save last valid line of the group.
         while (input.readLine() != null) {

            input.reset();

            readHeader(input, progress);

            final GPtx3Group ptxGroup = new GPtx3Group(_vectorPrecision, _colorPrecision, _projection, _hasIntensities,
                     _hasColors, false, false, _rows, _columns, _translationVector, _rotationMatrix, _transformationMatrix);

            readPoints(input, vertices, ptxGroup, _includeEmptyPoints, _includeEmptyButColoredPoints, progress);

            input.mark(MAX_MARK_SIZE); // save last valid line of the group.

         }

         progress.finish();

         vertices.trimToSize();
         vertices.makeImmutable();

         return vertices;

      }
      finally {
         GIOUtils.gentlyClose(input);
      }

   }


   @Override
   protected void endLoad() {

   }


   //   public static void save(final GStructuredPtxVertex3Container vertices,
   //                           final String fileName) {
   //
   //      //      final long started = System.currentTimeMillis();
   //      //
   //      //      final Logger logger = Logger.instance();
   //      //
   //      //
   //      //      logger.info("Saving " + verticesCount + " vertices (intensities=" + hasIntensities + ", colors=" + hasColors + ") to "
   //      //                  + fileName + "...");
   //      //
   //      //
   //      //      final long ellapsed = System.currentTimeMillis() - started;
   //      //      logger.info("Saved in " + (ellapsed / 1000.0) + "s");
   //
   //   }


   public static void main(final String[] args) {

      //GUtils.delay(30000);

      final GFileName sourceDirectoryName = GFileName.absolute("home", "dgd", "Escritorio", "SamplePointsClouds");

      final String fileName = "guadiloba.ptx";

      final GFileName inputFileName = GFileName.fromParentAndParts(sourceDirectoryName, fileName);

      final GProjection projection = GProjection.EUCLID;
      //final GProjection projection = GProjection.EPSG_23029;
      final boolean intensitiesFromColor = false;
      final boolean includeEmptyPoints = false;
      final boolean includeEmptyButColoredPoints = false;

      System.out.println("Loading ptx files..");
      try {
         final GPTX3Loader loader = new GPTX3Loader(GVectorPrecision.FLOAT, GColorPrecision.INT, projection,
                  intensitiesFromColor, includeEmptyPoints, includeEmptyButColoredPoints, GPointsLoader.DEFAULT_FLAGS
                                                                                          | GPointsLoader.VERBOSE, inputFileName);

         loader.load();

         final GStructuredPtxVertex3Container vertices = (GStructuredPtxVertex3Container) loader.getVertices();

         System.out.println("VERTICES: ");
         System.out.println(vertices.toString());
         System.out.println();

         final StructuredVertex vertex = vertices.getVertex(777);
         System.out.println("Vertex 777= " + vertex.toString());
         System.out.println();

         final List<GPtx3Group> groupList = vertices.getGroups();
         final HashMap<GPtx3Group, GPtx3Group> groupsMap = new HashMap<GPtx3Group, GPtx3Group>(groupList.size());

         System.out.println("GROUPS INFORMATION: ");
         for (final GPtx3Group group : groupList) {
            System.out.println("GROUP: " + group.hashCode());
            final GStructuredSubVertexContainer subVertices = (GStructuredSubVertexContainer) vertices.getGroupVertices(group);
            System.out.println(subVertices.toString());

            final StructuredVertex rawVertex = subVertices.getRawVertex(777);
            System.out.println("Raw vertex 777= " + rawVertex.toString());
            final StructuredVertex transfVertex = subVertices.getTransformedVertex(777);
            System.out.println("Transformed vertex 777= " + transfVertex.toString());
            System.out.println();

            // Get new groups with reference point
            final IVector3 referencePoint = (IVector3) subVertices.getAverage()._point;
            final GPtx3Group newGroup = group.newEmptyContainer(group.size(), referencePoint);
            groupsMap.put(group, newGroup);
         }

         System.out.println("--------------------------------------------------------------------------------------------");
         //--------------------------------------------------------------------------------------------
         //-- New container but with reference points
         final long started = System.currentTimeMillis();

         final GStructuredPtxVertex3Container newVertices = vertices.newEmptyContainer(vertices.size());

         final Iterator<StructuredVertex<IVector3, GPtx3Group>> verticesIterator = vertices.vertexIterator();

         while (verticesIterator.hasNext()) {
            final IStructuredVertexContainer.StructuredVertex<IVector3, GPtx3Group> structuredVertex = verticesIterator.next();
            newVertices.addPoint(structuredVertex._point, structuredVertex._intensity, structuredVertex._normal,
                     structuredVertex._color, structuredVertex._userData, structuredVertex._rowColumn,
                     groupsMap.get(structuredVertex._group));
         }

         System.out.println("NEW VERTICES: ");
         System.out.println(newVertices.toString());
         System.out.println();

         final StructuredVertex newVertex = newVertices.getVertex(777);
         System.out.println("Vertex 777= " + newVertex.toString());
         System.out.println();

         final List<GPtx3Group> newGroupList = newVertices.getGroups();

         System.out.println("NEW GROUPS INFORMATION: ");
         for (final GPtx3Group group : newGroupList) {
            System.out.println("GROUP: " + group.hashCode());
            final GStructuredSubVertexContainer newSubVertices = (GStructuredSubVertexContainer) newVertices.getGroupVertices(group);
            System.out.println(newSubVertices.toString());

            final StructuredVertex rawVertex = newSubVertices.getRawVertex(777);
            System.out.println("Raw vertex 777= " + rawVertex.toString());
            final StructuredVertex transfVertex = newSubVertices.getTransformedVertex(777);
            System.out.println("Transformed vertex 777= " + transfVertex.toString());
            System.out.println();
         }

         final long elapsed = System.currentTimeMillis() - started;
         System.out.println("NEW in " + elapsed + " ms. ( " + GStringUtils.getTimeMessage(elapsed) + " )");
         System.out.println();

         System.out.println("--------------------------------------------------------------------------------------------");
         //--------------------------------------------------------------------------------------------
         //-- New container but with transformed data inside
         final long started2 = System.currentTimeMillis();

         final GStructuredPtxVertex3Container transformedVertices = newVertices.asTransformedContainer();

         System.out.println("TRANFORMED NEW VERTICES: ");
         System.out.println(transformedVertices.toString());
         System.out.println();

         final StructuredVertex transformedVertex = transformedVertices.getVertex(777);
         System.out.println("Vertex 777= " + transformedVertex.toString());
         System.out.println();

         final List<GPtx3Group> transfGroupList = transformedVertices.getGroups();

         System.out.println("TRANSFORMED GROUPS INFORMATION: ");
         for (final GPtx3Group group : transfGroupList) {
            System.out.println("GROUP: " + group.hashCode());
            final GStructuredSubVertexContainer transfSubVertices = (GStructuredSubVertexContainer) transformedVertices.getGroupVertices(group);
            System.out.println(transfSubVertices.toString());

            final StructuredVertex rawVertex = transfSubVertices.getRawVertex(777);
            System.out.println("Raw vertex 777= " + rawVertex.toString());
            final StructuredVertex transfVertex = transfSubVertices.getTransformedVertex(777);
            System.out.println("Transformed vertex 777= " + transfVertex.toString());
            System.out.println();
         }

         final long elapsed2 = System.currentTimeMillis() - started2;
         System.out.println("TRANSFORMED in " + elapsed2 + " ms. ( " + GStringUtils.getTimeMessage(elapsed2) + " )");
         System.out.println();

         System.out.println("--------------------------------------------------------------------------------------------");
         //--------------------------------------------------------------------------------------------
         //-- New container but with reprojected data inside
         //         final long started3 = System.currentTimeMillis();
         //
         //         final GStructuredPtxVertex3Container reprojectedVertices = newVertices.reproject(GProjection.EPSG_4326);
         //
         //         System.out.println("TRANFORMED NEW VERTICES: ");
         //         System.out.println(reprojectedVertices.toString());
         //         System.out.println();
         //
         //         final StructuredVertex reprojectedVertex = reprojectedVertices.getVertex(777);
         //         System.out.println("Vertex 777= " + reprojectedVertex.toString());
         //         System.out.println();
         //
         //         final List<GPtx3Group> reprojectedGroupList = reprojectedVertices.getGroups();
         //
         //         System.out.println("REPROJECTED GROUPS INFORMATION: ");
         //         for (final GPtx3Group group : reprojectedGroupList) {
         //            System.out.println("GROUP: " + group.hashCode());
         //            final GStructuredSubVertexContainer reprojSubVertices = (GStructuredSubVertexContainer) reprojectedVertices.getGroupVertices(group);
         //            System.out.println(reprojSubVertices.toString());
         //
         //            final StructuredVertex rawVertex = reprojSubVertices.getRawVertex(777);
         //            System.out.println("Raw vertex 777= " + rawVertex.toString());
         //            final StructuredVertex transfVertex = reprojSubVertices.getTransformedVertex(777);
         //            System.out.println("Transformed vertex 777= " + transfVertex.toString());
         //            System.out.println();
         //         }
         //
         //         final long elapsed3 = System.currentTimeMillis() - started3;
         //         System.out.println("REPROJECTED in " + elapsed3 + " ms. ( " + GUtils.getTimeMessage(elapsed3) + " )");
         //         System.out.println();


      }
      catch (final IOException e) {

         e.printStackTrace();
      }

   }
}
