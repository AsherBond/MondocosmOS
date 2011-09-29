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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.colors.GColorF;
import es.igosoftware.euclid.colors.GColorI;
import es.igosoftware.euclid.colors.GColorPrecision;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.GVector3F;
import es.igosoftware.euclid.vector.GVectorPrecision;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.verticescontainer.GVertex3Container;
import es.igosoftware.euclid.verticescontainer.IUnstructuredVertexContainer;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;
import es.igosoftware.io.GFileName;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.logging.GLogger;
import es.igosoftware.logging.ILogger;
import es.igosoftware.util.GProgress;


public final class GBinaryPoints3Loader
         extends
            GUnstructuredFilePointsLoader<IVector3> {


   public static void save(final IUnstructuredVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices,
                           final GFileName fileName) throws IOException {
      save(vertices, GProjection.EUCLID, fileName);
   }


   public static void save(final IUnstructuredVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices,
                           final GProjection projection,
                           final GFileName fileName) throws IOException {
      save(vertices, projection, fileName, true);
   }


   public static void save(final IUnstructuredVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices,
                           final GProjection projection,
                           final GFileName fileName,
                           final boolean verbose) throws IOException {

      final long started = System.currentTimeMillis();

      final ILogger logger = GLogger.instance();

      final boolean hasIntensities = vertices.hasIntensities();
      final boolean hasColors = vertices.hasColors();
      final boolean hasNormals = vertices.hasNormals();
      final boolean hasUserData = vertices.hasUserData();

      final int verticesCount = vertices.size();

      if (verbose) {
         logger.logInfo("Saving " + verticesCount + " vertices (intensities=" + hasIntensities + ", colors=" + hasColors
                        + ", normals=" + hasNormals + ", userData=" + hasUserData + ") to " + fileName + "...");
      }

      final DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName.buildPath())));


      final byte dimensions = vertices.dimensions();
      final GVectorPrecision vectorPrecision = vertices.vectorPrecision();
      final GColorPrecision colorPrecision = vertices.colorPrecision();


      // ---------------------------------------------------------------------------------------------------
      // save header 
      output.writeByte(dimensions);

      output.writeInt(verticesCount);

      output.writeInt(projection.ordinal());

      output.writeInt(vectorPrecision.ordinal());

      output.writeInt(colorPrecision == null ? 0 : colorPrecision.ordinal());

      final int optionalData = (hasIntensities ? 1 : 0) | //
                               (hasColors ? 2 : 0) | //
                               (hasNormals ? 4 : 0) | //
                               (hasUserData ? 8 : 0);

      output.writeInt(optionalData);

      final int groupsCount = 1;
      output.writeInt(groupsCount);

      final int groupsShape = 0;
      output.writeInt(groupsShape);

      //      final GAxisAlignedOrthotope<VectorT, ?> bounds = GAxisAlignedOrthotope.minimumOrthotope(vertices);
      final GAxisAlignedOrthotope<IVector3, ?> bounds = vertices.getBounds();
      saveVector(output, dimensions, vectorPrecision, bounds._lower);
      saveVector(output, dimensions, vectorPrecision, bounds._upper);

      final IVector3 referencePoint = vertices.getReferencePoint();
      saveVector(output, dimensions, GVectorPrecision.DOUBLE, referencePoint);

      // save group #1
      output.writeInt(verticesCount);
      // save group data is still unsupported
      // ---------------------------------------------------------------------------------------------------


      // ---------------------------------------------------------------------------------------------------
      // save vertices
      for (int i = 0; i < verticesCount; i++) {
         saveVector(output, dimensions, vectorPrecision, vertices.getPoint(i).sub(referencePoint));

         if (hasIntensities) {
            output.writeFloat(vertices.getIntensity(i));
         }

         if (hasColors) {
            saveColor(output, colorPrecision, vertices.getColor(i));
         }

         if (hasNormals) {
            saveVector(output, dimensions, vectorPrecision, vertices.getNormal(i));
         }

         if (hasUserData) {
            output.writeLong(vertices.getUserData(i));
         }
      }
      // ---------------------------------------------------------------------------------------------------


      output.close();

      if (verbose) {
         final long ellapsed = System.currentTimeMillis() - started;
         logger.logInfo("Saved in " + (ellapsed / 1000.0) + " seconds");
      }
   }


   private static void saveVector(final DataOutputStream output,
                                  final byte dimensions,
                                  final GVectorPrecision precision,
                                  final IVector<?, ?> vector) throws IOException {
      switch (precision) {
         case FLOAT:
            for (byte i = 0; i < dimensions; i++) {
               output.writeFloat((float) vector.get(i));
            }
            break;
         case DOUBLE:
            for (byte i = 0; i < dimensions; i++) {
               output.writeDouble(vector.get(i));
            }
            break;
         default:
            throw new IllegalArgumentException("precision " + precision + " not supported");
      }
   }


   private static void saveColor(final DataOutputStream output,
                                 final GColorPrecision precision,
                                 final IColor color) throws IOException {
      switch (precision) {
         case FLOAT3:
            output.writeFloat(color.getRed());
            output.writeFloat(color.getGreen());
            output.writeFloat(color.getBlue());
            break;
         case INT:
            output.writeInt(GColorI.getRGB(color));
            break;
         default:
            throw new IllegalArgumentException("precision " + precision + " not supported");
      }
   }


   public static class Header {
      private final byte             _dimensions;
      private final int              _verticesCount;
      private final GProjection      _projection;
      private final GVectorPrecision _vectorPrecision;
      private final GColorPrecision  _colorPrecision;
      private final IVector3         _referencePoint;
      private final boolean          _hasIntensities;
      private final boolean          _hasColors;
      private final boolean          _hasNormals;
      private final boolean          _hasUserData;
      private final int              _groupsCount;
      private final int              _groupsShape;
      private final GAxisAlignedBox  _bounds;


      private Header(final DataInputStream input) throws IOException {
         _dimensions = input.readByte();
         if (_dimensions != 3) {
            throw new IOException("Only dimensions 3 are supported");
         }

         _verticesCount = input.readInt();
         if (_verticesCount < 1) {
            throw new IOException("Invalid Vertices Count: " + _verticesCount);
         }

         _projection = GProjection.values()[input.readInt()];

         _vectorPrecision = GVectorPrecision.values()[input.readInt()];

         _colorPrecision = GColorPrecision.values()[input.readInt()];

         final int optionals = input.readInt();
         _hasIntensities = (optionals & 1) != 0;
         _hasColors = (optionals & 2) != 0;
         _hasNormals = (optionals & 4) != 0;
         _hasUserData = (optionals & 8) != 0;

         _groupsCount = input.readInt();
         if (_groupsCount != 1) {
            throw new IOException("Only one group is supported");
         }

         _groupsShape = input.readInt();

         final IVector3 lower = readVector(input);
         final IVector3 upper = readVector(input);
         _bounds = new GAxisAlignedBox(lower, upper);

         _referencePoint = readVector(input, GVectorPrecision.DOUBLE);

         final int group0VerticesCount = input.readInt();
         if (group0VerticesCount != _verticesCount) {
            throw new IOException("The total vertices count (" + _verticesCount + ") doesn't match with groups vertices ("
                                  + group0VerticesCount + ")");
         }
      }


      //      private Header(final ByteBuffer input) throws IOException {
      //         _dimensions = input.get();
      //         if (_dimensions != 3) {
      //            throw new IOException("Only dimensions 3 are supported");
      //         }
      //
      //         _verticesCount = input.getInt();
      //         if (_verticesCount < 1) {
      //            throw new IOException("Invalid Vertices Count: " + _verticesCount);
      //         }
      //
      //         _projection = GProjection.values()[input.getInt()];
      //
      //         _vectorPrecision = GVectorPrecision.values()[input.getInt()];
      //
      //         _colorPrecision = GColorPrecision.values()[input.getInt()];
      //
      //         final int optionals = input.getInt();
      //         _hasIntensities = (optionals & 1) != 0;
      //         _hasColors = (optionals & 2) != 0;
      //         _hasNormals = (optionals & 4) != 0;
      //
      //         _groupsCount = input.getInt();
      //         if (_groupsCount != 1) {
      //            throw new IOException("Only one group is supported");
      //         }
      //
      //         _groupsShape = input.getInt();
      //
      //         final IVector3 lower = readVector(input);
      //         final IVector3 upper = readVector(input);
      //         _bounds = new GAxisAlignedBox(lower, upper);
      //
      //         _referencePoint = readVector(input, GVectorPrecision.DOUBLE);
      //
      //         final int group0VerticesCount = input.getInt();
      //         if (group0VerticesCount != _verticesCount) {
      //            throw new IOException("The total vertices count (" + _verticesCount + ") doesn't match with groups vertices ("
      //                                  + group0VerticesCount + ")");
      //         }
      //      }


      private IVector3 readVector(final DataInputStream input) throws IOException {
         return readVector(input, _vectorPrecision);
      }


      //      private IVector3 readVector(final ByteBuffer input) throws IOException {
      //         return readVector(input, _vectorPrecision);
      //      }


      private IVector3 readVector(final DataInputStream input,
                                  final GVectorPrecision vectorPrecision) throws IOException {
         switch (vectorPrecision) {
            case DOUBLE:
               final double dx = input.readDouble();
               final double dy = input.readDouble();
               final double dz = input.readDouble();
               return new GVector3D(dx, dy, dz);
            case FLOAT:
               final float fx = input.readFloat();
               final float fy = input.readFloat();
               final float fz = input.readFloat();
               return new GVector3F(fx, fy, fz);
            default:
               throw new IOException("Invalid Vector Precision: " + _vectorPrecision);
         }
      }


      //      private IVector3 readVector(final ByteBuffer input,
      //                                     final GVectorPrecision vectorPrecision) throws IOException {
      //         switch (vectorPrecision) {
      //            case DOUBLE:
      //               final double dx = input.getDouble();
      //               final double dy = input.getDouble();
      //               final double dz = input.getDouble();
      //               return new GVector3D(dx, dy, dz);
      //            case FLOAT:
      //               final float fx = input.getFloat();
      //               final float fy = input.getFloat();
      //               final float fz = input.getFloat();
      //               return new GVector3F(fx, fy, fz);
      //            default:
      //               throw new IOException("Invalid Vector Precision: " + _vectorPrecision);
      //         }
      //      }


      private IColor readColor(final DataInputStream input) throws IOException {
         switch (_colorPrecision) {
            case FLOAT3:
               final float red = input.readFloat();
               final float green = input.readFloat();
               final float blue = input.readFloat();
               return GColorF.newRGB(red, green, blue);
            case INT:
               final int rgb = input.readInt();
               return GColorI.newRGB(rgb);
            default:
               throw new IOException("Invalid color precision: " + _colorPrecision);
         }
      }


      //      private IColor readColor(final ByteBuffer input) throws IOException {
      //         switch (_colorPrecision) {
      //            case FLOAT3:
      //               final float red = input.getFloat();
      //               final float green = input.getFloat();
      //               final float blue = input.getFloat();
      //               return GColorF.newRGB(red, green, blue);
      //            case INT:
      //               final int rgb = input.getInt();
      //               return GColorI.newRGB(rgb);
      //            default:
      //               throw new IOException("Invalid color precision: " + _colorPrecision);
      //         }
      //      }


      @Override
      public String toString() {
         return "Header [dimensions=" + _dimensions + ", verticesCount=" + _verticesCount + ", reference point="
                + _referencePoint + ", projection=" + _projection + ", vectorPrecision=" + _vectorPrecision + ", colorPrecision="
                + _colorPrecision + ", intensities=" + _hasIntensities + ", colors=" + _hasColors + ", normals=" + _hasNormals
                + ", userData=" + _hasUserData + ", groupsCount=" + _groupsCount + ", groupsShape=" + _groupsShape + ", bounds="
                + _bounds + "]";
      }


      public boolean sameShape(final Header other) {
         if (this == other) {
            return true;
         }
         if (other == null) {
            return false;
         }

         if (_colorPrecision == null) {
            if (other._colorPrecision != null) {
               return false;
            }
         }
         else if (!_colorPrecision.equals(other._colorPrecision)) {
            return false;
         }
         if (_dimensions != other._dimensions) {
            return false;
         }
         if (_groupsShape != other._groupsShape) {
            return false;
         }
         if (_hasColors != other._hasColors) {
            return false;
         }
         if (_hasIntensities != other._hasIntensities) {
            return false;
         }
         if (_hasNormals != other._hasNormals) {
            return false;
         }
         if (_hasUserData != other._hasUserData) {
            return false;
         }
         if (_projection == null) {
            if (other._projection != null) {
               return false;
            }
         }
         else if (!_projection.equals(other._projection)) {
            return false;
         }
         if (_vectorPrecision == null) {
            if (other._vectorPrecision != null) {
               return false;
            }
         }
         else if (!_vectorPrecision.equals(other._vectorPrecision)) {
            return false;
         }
         return true;
      }


      public GProjection getProjection() {
         return _projection;
      }


      public GAxisAlignedBox getBounds() {
         return _bounds;
      }

   }


   private GBinaryPoints3Loader.Header _referenceHeader;
   private final int                   _maxPoints;


   public GBinaryPoints3Loader(final int flags,
                               final GFileName... fileNames) {
      this(-1, flags, fileNames);
   }


   public GBinaryPoints3Loader(final int maxPoints,
                               final int flags,
                               final GFileName... fileNames) {
      super(flags, fileNames);
      _maxPoints = maxPoints;
   }


   private static DataInputStream openInput(final GFileName fileName) throws IOException {
      InputStream inputStream = new BufferedInputStream(new FileInputStream(fileName.buildPath()), 1024 * 1024 /* 1024Kb buffer size */);
      if (fileName.getName().toLowerCase().endsWith(".gz")) {
         inputStream = new GZIPInputStream(inputStream);
      }
      return new DataInputStream(inputStream);
   }


   //   private static ByteBuffer openInput(final String fileName) throws IOException {
   //      final FileInputStream inputStream = new FileInputStream(fileName);
   //      try {
   //         final FileChannel fc = inputStream.getChannel();
   //         final int size = (int) fc.size();
   //         final ByteBuffer buffer = ByteBuffer.allocate(size);
   //         for (int count = 0; (count >= 0) && buffer.hasRemaining();) {
   //            count = fc.read(buffer);
   //         }
   //         buffer.flip();
   //         return buffer;
   //      }
   //      finally {
   //         GIOUtils.gentlyClose(inputStream);
   //      }
   //   }


   private GBinaryPoints3Loader.Header readHeader(final DataInputStream input) throws IOException {
      final GBinaryPoints3Loader.Header header = new GBinaryPoints3Loader.Header(input);

      if (_referenceHeader == null) {
         _referenceHeader = header;
      }
      else {
         if (!header.sameShape(_referenceHeader)) {
            throw new IOException("Invalid header");
         }
      }

      logInfo(header.toString());

      return header;
   }


   //   private GBinaryPoints3Loader.Header readHeader(final ByteBuffer input) throws IOException {
   //      final GBinaryPoints3Loader.Header header = new GBinaryPoints3Loader.Header(input);
   //
   //      if (_referenceHeader == null) {
   //         _referenceHeader = header;
   //         logInfo(header.toString());
   //      }
   //      else {
   //         if (!header.sameShape(_referenceHeader)) {
   //            throw new IOException("Invalid header");
   //         }
   //      }
   //
   //      return header;
   //   }


   @Override
   protected void startLoad(final int filesCount) {
   }


   @Override
   protected GVertex3Container loadVerticesFromFile(final GFileName fileName) throws IOException {
      final DataInputStream input = openInput(fileName);
      //final ByteBuffer input = openInput(fileName);

      final GBinaryPoints3Loader.Header header = readHeader(input);

      final int pointsToRead;
      if (_maxPoints <= 0) {
         pointsToRead = header._verticesCount;
      }
      else {
         pointsToRead = _maxPoints;
      }

      final GVertex3Container vertices = new GVertex3Container(header._vectorPrecision, header._colorPrecision,
               header._projection, header._referencePoint, pointsToRead, header._hasIntensities, 0, header._hasColors, null,
               header._hasNormals, null, header._hasUserData, 0);

      // grow the container in a shot
      //vertices.ensureCapacity(vertices.size() + pointsToRead);


      final GProgress progress = new GProgress(pointsToRead) {
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


      for (int i = 0; i < pointsToRead; i++) {
         final IVector3 point = header._referencePoint.add(header.readVector(input));

         final float intensity = header._hasIntensities ? input.readFloat() : 0;
         //final float intensity = header._hasIntensities ? input.getFloat() : 0;

         final IColor color = header._hasColors ? header.readColor(input) : null;

         final IVector3 normal = header._hasNormals ? header.readVector(input).normalized() : null;

         final long userData = header._hasUserData ? input.readLong() : 0;

         vertices.addPoint(point, intensity, normal, color, userData);

         if ((i % 1000) == 0) {
            progress.stepsDone(1000);
         }
      }
      progress.finish();

      input.close();

      vertices.trimToSize();
      vertices.makeImmutable();

      return vertices;
   }


   @Override
   protected void endLoad() {
   }


   public GProjection getProjection() {
      if (!isLoaded()) {
         throw new IllegalArgumentException("Not yet loaded!");
      }

      return _referenceHeader._projection;
   }


   public static GBinaryPoints3Loader.Header loadHeader(final GFileName bpFileName) throws IOException {
      DataInputStream input = null;
      try {
         input = openInput(bpFileName);

         return new GBinaryPoints3Loader.Header(input);
      }
      finally {
         GIOUtils.gentlyClose(input);
      }
   }


}
