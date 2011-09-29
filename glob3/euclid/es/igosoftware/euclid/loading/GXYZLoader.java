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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.BitSet;
import java.util.List;

import es.igosoftware.euclid.colors.GColorF;
import es.igosoftware.euclid.colors.GColorPrecision;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.GVector3F;
import es.igosoftware.euclid.vector.GVectorPrecision;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.verticescontainer.GVertex3Container;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;
import es.igosoftware.io.GFileName;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.logging.GLogger;
import es.igosoftware.logging.ILogger;
import es.igosoftware.util.GProgress;
import es.igosoftware.util.XStringTokenizer;


public final class GXYZLoader
         extends
            GUnstructuredFilePointsLoader<IVector3> {


   public static void save(final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices,
                           final GFileName fileName) throws IOException {

      final long started = System.currentTimeMillis();

      final ILogger logger = GLogger.instance();

      final boolean hasColors = vertices.hasColors();
      final boolean hasIntensities = vertices.hasIntensities();

      final int verticesCount = vertices.size();

      logger.logInfo("Saving " + verticesCount + " vertices (intensities=" + hasIntensities + ", colors=" + hasColors + ") to "
                     + fileName + "...");

      final BufferedWriter output = new BufferedWriter(new FileWriter(fileName.buildPath()));

      for (int i = 0; i < verticesCount; i++) {
         final IVector3 point = vertices.getPoint(i);
         output.write(Double.toString(point.x()));
         output.write(" ");
         output.write(Double.toString(point.y()));
         output.write(" ");
         output.write(Double.toString(point.z()));

         if (hasIntensities) {
            output.write(" ");
            output.write(Float.toString(vertices.getIntensity(i)));
         }

         if (hasColors) {
            final IColor color = vertices.getColor(i);
            output.write(String.valueOf(Math.round(color.getRed() * 255)));
            output.write(String.valueOf(Math.round(color.getGreen() * 255)));
            output.write(String.valueOf(Math.round(color.getBlue() * 255)));
         }

         output.newLine();
      }

      output.close();

      final long ellapsed = System.currentTimeMillis() - started;
      logger.logInfo("Saved in " + (ellapsed / 1000.0) + "s");
   }


   private final GVectorPrecision _vectorPrecision;
   private final GColorPrecision  _colorPrecision;
   private final GProjection      _projection;
   private final boolean          _intensitiesFromColor;
   private boolean                _hasIntensities;
   private boolean                _hasColors;


   public GXYZLoader(final GVectorPrecision vectorPrecision,
                     final GColorPrecision colorPrecision,
                     final GProjection projection,
                     final int flags,
                     final GFileName... fileNames) {
      this(vectorPrecision, colorPrecision, projection, false, flags, fileNames);
   }


   public GXYZLoader(final GVectorPrecision vectorPrecision,
                     final GColorPrecision colorPrecision,
                     final GProjection projection,
                     final boolean intensitiesFromColor,
                     final int flags,
                     final GFileName... fileNames) {
      super(flags, fileNames);

      _intensitiesFromColor = intensitiesFromColor;

      _vectorPrecision = vectorPrecision;
      _colorPrecision = colorPrecision;
      _projection = projection;

      if (_intensitiesFromColor) {
         logInfo("Calculating intensity from color");
      }
   }


   private BitSet detectShape() throws IOException {
      final List<GFileName> fileNames = getFileNames();

      if (fileNames.size() == 0) {
         return new BitSet();
      }

      final BitSet shape = detectShape(fileNames.get(0));

      for (int i = 1; i < fileNames.size(); i++) {
         final GFileName fileName = fileNames.get(i);
         if (!detectShape(fileName).equals(shape)) {
            throw new IOException("File \"" + fileName + "\" has a different shape that the others");
         }
      }

      logInfo("Shape detected: intensities=" + shape.get(0) + ", colors=" + shape.get(1));

      return shape;
   }


   private BitSet detectShape(final GFileName fileName) throws IOException {
      BufferedReader input = null;

      final BitSet result = new BitSet();

      try {
         input = new BufferedReader(new FileReader(fileName.buildPath()));

         if (GIOUtils.hasExtension(fileName, "pts")) {
            @SuppressWarnings("unused")
            final int numberOfVertices = Integer.parseInt(input.readLine()); // ignore the very first line of PTS
         }

         final String line = input.readLine();
         if (line != null) {
            final int tokensCount = new XStringTokenizer(line).countTokens();

            if ((tokensCount == 4) || // x y z intensity 
                (tokensCount == 7) // x y z intensity r g b
            ) {
               result.set(0);
            }

            if ((tokensCount == 6) || // x y z r g b
                (tokensCount == 7) // x y z intensity r g b
            ) {
               result.set(1);
            }
         }
      }
      finally {
         GIOUtils.gentlyClose(input);
      }

      return result;
   }


   private void loadPoints(final LineNumberReader input,
                           final GVertex3Container vertices,
                           final boolean isPTS,
                           final GProgress progress) throws IOException {


      String line = null;
      try {
         line = input.readLine();
         while (line != null) {
            final XStringTokenizer tokenizer = new XStringTokenizer(line);
            if (tokenizer.countTokens() == 1) {
               if (isPTS) {
                  final int numberOfVertices = Integer.parseInt(line);
                  vertices.ensureCapacity(vertices.size() + numberOfVertices);
               }
               else {
                  throw new IOException("Invalid Format");
               }
            }
            else {
               final IVector3 point = parsePoint(tokenizer);
               float intensity = parseIntensity(tokenizer);
               IColor color = parseColor(tokenizer);

               if (_intensitiesFromColor) {
                  intensity = intensityFromColor(color);
                  color = null;
               }

               vertices.addPoint(point, intensity, color);
            }

            progress.stepsDone(line.length() + 1);

            line = input.readLine();
         }
      }
      catch (final NumberFormatException e) {
         throw new IOException(e + " in line #" + input.getLineNumber() + ": \"" + line + "\"");
      }
   }


   private static float intensityFromColor(final IColor color) {
      // return color.getLuminance();
      return color.getBrightness();
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

         return GColorF.newRGB(red, green, blue);
      }

      return null;
   }


   @Override
   protected void startLoad(final int filesCount) throws IOException {
      final BitSet intensitiesColors = detectShape();
      _hasIntensities = intensitiesColors.get(0);
      _hasColors = intensitiesColors.get(1);
   }


   @Override
   protected GVertex3Container loadVerticesFromFile(final GFileName fileName) throws IOException {


      final boolean withIntensities = _hasIntensities || (_hasColors && _intensitiesFromColor);
      final boolean withColors = _hasColors && !_intensitiesFromColor;
      final GVertex3Container vertices = new GVertex3Container(_vectorPrecision, _colorPrecision, _projection, withIntensities,
               0, withColors, null, false, null);

      LineNumberReader input = null;
      try {
         input = new LineNumberReader(new InputStreamReader(new FileInputStream(fileName.buildPath())));
         //         input = new BufferedReader(new InputStreamReader(
         //                  new GBufferedFileInputStream(fileName, 32 * 1024 /* 1Mb buffer size */)));


         final GProgress progress = new GProgress(fileName.asFile().length()) {
            @Override
            public void informProgress(final long stepsDone,
                                       final double percent,
                                       final long elapsed,
                                       final long estimatedMsToFinish) {
               if (isFlagged(VERBOSE)) {
                  logInfo("  \"" + fileName.buildPath() + "\" "
                          + progressString(stepsDone, percent, elapsed, estimatedMsToFinish));
               }
            }
         };


         final boolean isPTS = GIOUtils.hasExtension(fileName, "pts");
         if (isPTS) {
            final String line = input.readLine();
            final int numberOfVertices = Integer.parseInt(line);
            vertices.ensureCapacity(vertices.size() + numberOfVertices);

            progress.stepsDone(line.length() + 1);
         }

         loadPoints(input, vertices, isPTS, progress);

         progress.finish();
      }
      finally {
         GIOUtils.gentlyClose(input);
      }


      vertices.trimToSize();
      vertices.makeImmutable();

      return vertices;
   }


   @Override
   protected void endLoad() {
   }


}
