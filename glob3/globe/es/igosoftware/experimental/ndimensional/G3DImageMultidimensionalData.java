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


package es.igosoftware.experimental.ndimensional;

import es.igosoftware.euclid.colors.GColorI;
import es.igosoftware.euclid.colors.GColorPrecision;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.GVectorPrecision;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.verticescontainer.GVertex3Container;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GRange;
import es.igosoftware.util.GTriplet;
import es.igosoftware.util.IRangeEvaluator;
import es.igosoftware.utils.GPositionBox;
import es.igosoftware.utils.GWWUtils;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;


public class G3DImageMultidimensionalData
         implements
            IMultidimensionalData {

   private static final int   BYTES_PER_VECTOR3F = 3 * 4; // x, y, z * float 


   private final String       _name;
   private final String       _directoryName;
   private final String       _imagesExtension;
   private final Position     _position;

   private final int          _width;
   private final int          _heigth;
   private final int          _depth;
   private final GPositionBox _box;
   private final IColor[][][] _data;


   private final double       _metersPerX;
   private final double       _metersPerY;
   private final double       _metersPerZ;


   public G3DImageMultidimensionalData(final String name,
                                       final String directoryName,
                                       final String imagesExtension,
                                       final Position position,
                                       final double metersPerX,
                                       final double metersPerY,
                                       final double metersPerZ) throws IOException {
      GAssert.notNull(name, "name");
      GAssert.notNull(directoryName, "directoryName");
      GAssert.notNull(imagesExtension, "imagesExtension");
      GAssert.notNull(position, "position");
      GAssert.isPositive(metersPerX, "metersPerX");
      GAssert.isPositive(metersPerY, "metersPerY");
      GAssert.isPositive(metersPerZ, "metersPerZ");

      _name = name;
      _directoryName = directoryName;
      _imagesExtension = imagesExtension;

      _position = position;
      _metersPerX = metersPerX;
      _metersPerY = metersPerY;
      _metersPerZ = metersPerZ;

      final GTriplet<Integer, Integer, Integer> extent = initializeExtent();
      _width = extent._first;
      _heigth = extent._second;
      _depth = extent._third;

      _box = new GPositionBox(_position,
               GWWUtils.increment(_position, _width * _metersPerX, _heigth * _metersPerY, _depth * _metersPerZ));

      _data = initializeData();
   }


   private IColor[][][] initializeData() throws IOException {
      final IColor[][][] result = new IColor[_width][_heigth][_depth];

      final String[] imagesNames = getImagesNames();

      for (int z = 0; z < _depth; z++) {
         final String imageName = imagesNames[z];
         final File imageFile = new File(_directoryName, imageName);

         final BufferedImage image = ImageIO.read(imageFile.toURI().toURL());
         if (image == null) {
            throw new RuntimeException("Can't read image " + imageFile.getAbsolutePath());
         }

         for (int x = 0; x < _width; x++) {
            for (int y = 0; y < _heigth; y++) {
               final int pixel = image.getRGB(x, y);
               final Color color = new Color(pixel);

               final GColorI iColor = GColorI.newRGB(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);

               //               final boolean hasColor = !color.equals(Color.BLACK);
               final boolean hasColor = iColor.getLuminance() >= 0.5;
               result[x][y][z] = hasColor ? iColor : null;
            }
         }

      }

      return result;
   }


   private GTriplet<Integer, Integer, Integer> initializeExtent() throws IOException {
      final String[] images = getImagesNames();

      if (images.length == 0) {
         throw new RuntimeException("Can't find any image with extention \"" + _imagesExtension + "\" in directory \""
                                    + _directoryName + "\"");
      }


      final File sampleImageFile = new File(_directoryName, images[0]);

      final BufferedImage sampleImage = ImageIO.read(sampleImageFile.toURI().toURL());
      if (sampleImage == null) {
         throw new RuntimeException("Can't read image " + sampleImageFile.getAbsolutePath());
      }

      final int width = sampleImage.getWidth();
      final int height = sampleImage.getHeight();
      final int depth = images.length;

      return new GTriplet<Integer, Integer, Integer>(width, height, depth);
   }


   private String[] getImagesNames() {
      final File directory = new File(_directoryName);
      if (!directory.exists()) {
         throw new RuntimeException("Can't find directory \"" + _directoryName + "\"");
      }

      final String[] images = directory.list(new FilenameFilter() {
         @Override
         public boolean accept(final File dir,
                               final String name) {
            return name.toLowerCase().endsWith(_imagesExtension.toLowerCase());
         }
      });

      Arrays.sort(images);

      return images;
   }


   @Override
   public String getName() {
      return _name;
   }


   @Override
   public String getTimeDimensionName() {
      return null;
   }


   @Override
   public List<String> getDimensionsNames() {
      return Arrays.asList("x", "y", "z");
   }


   @Override
   public List<String> getNonTimeDimensionsNames() {
      return getDimensionsNames();
   }


   @Override
   public int getTimeDimensionLength() {
      return 0;
   }


   @Override
   public List<String> getAvailableValueVariablesNames() {
      return Arrays.asList("color");
   }


   @Override
   public List<String> getAvailableVectorVariablesNames() {
      return null;
   }


   @Override
   public int getDimensionLength(final String dimensionName) {
      GAssert.notNull(dimensionName, "dimensionName");


      if (dimensionName.equals("x")) {
         return _width;
      }
      else if (dimensionName.equals("y")) {
         return _heigth;
      }
      else if (dimensionName.equals("z")) {
         return _depth;
      }
      else {
         throw new RuntimeException("Invalid dimension name \"" + dimensionName + "\"");
      }

   }


   @Override
   public GPositionBox getBox() {
      return _box;
   }


   @Override
   public IMultidimensionalData.VectorsCloud calculateVectorsCloud(final String variableName,
                                                                   final int time,
                                                                   final Globe globe,
                                                                   final double verticalExaggeration,
                                                                   final Vec4 referencePoint,
                                                                   final float factor,
                                                                   final VectorColorization colorization,
                                                                   final Map<String, GRange<Integer>> dimensionsRanges) {
      return null;
   }


   @Override
   public IMultidimensionalData.PointsCloud calculateValuePointsCloud(final String variableName,
                                                                      final int time,
                                                                      final Globe globe,
                                                                      final double verticalExaggeration,
                                                                      final Vec4 referencePoint,
                                                                      final Map<String, GRange<Integer>> dimensionsRanges,
                                                                      final float alpha) {
      final int xFrom = dimensionsRanges.get("x")._lower;
      final int xTo = dimensionsRanges.get("x")._upper;

      final int yFrom = dimensionsRanges.get("y")._lower;
      final int yTo = dimensionsRanges.get("y")._upper;

      final int zFrom = dimensionsRanges.get("z")._lower;
      final int zTo = dimensionsRanges.get("z")._upper;

      final int initialCapacity = (xTo - xFrom + 1) * (yTo - yFrom + 1) * (zTo - zFrom + 1);
      final GVertex3Container vertexContainer = new GVertex3Container(GVectorPrecision.FLOAT, GColorPrecision.INT,
               GProjection.EUCLID, initialCapacity, false, 0, true, GColorI.WHITE, false, null);


      final List<Integer> xList = GCollections.rangeList(xFrom, xTo);

      GCollections.concurrentEvaluate(xList, new IRangeEvaluator() {
         @Override
         public void evaluate(final int from,
                              final int to) {
            for (int x = from; x <= to; x++) {
               final IColor[][] xs = _data[x];
               //               System.out.println("x=" + x);

               for (int y = yFrom; y < yTo + 1; y++) {
                  final IColor[] ys = xs[y];
                  for (int z = zFrom; z < zTo + 1; z++) {
                     final IColor color = ys[z];

                     //               System.out.println(x + "," + y + "," + z + " -> " + color);

                     if (color == null) {
                        continue;
                     }

                     final Position position = GWWUtils.increment(_position, x * _metersPerX, y * _metersPerY, (_depth - z)
                                                                                                               * _metersPerZ);

                     final Vec4 point4 = GWWUtils.computePointFromPosition(position, globe, verticalExaggeration);

                     final GVector3D point = new GVector3D(point4.x - referencePoint.x, point4.y - referencePoint.y,
                              point4.z - referencePoint.z);

                     synchronized (vertexContainer) {
                        vertexContainer.addPoint(point, color);
                     }
                  }
               }
            }
         }
      });


      //      for (int x = xFrom; x < xTo + 1; x++) {
      //         final Color[][] xs = _data[x];
      //
      //         System.out.println("x=" + x);
      //
      //         for (int y = yFrom; y < yTo + 1; y++) {
      //            final Color[] ys = xs[y];
      //            for (int z = zFrom; z < zTo + 1; z++) {
      //               final Color color = ys[z];
      //
      //               //               System.out.println(x + "," + y + "," + z + " -> " + color);
      //
      //               if (color == null) {
      //                  continue;
      //               }
      //
      //               final Position position = GWWUtils.increment(_position, x * _metersPerVoxel, y * _metersPerVoxel,
      //                        z * _metersPerVoxel);
      //
      //               final Vec4 point4 = GWWUtils.toVec4(position, globe, verticalExaggeration);
      //
      //               final GVector3D point = new GVector3D(point4.x - referencePoint.x, point4.y - referencePoint.y, point4.z
      //                                                                                                               - referencePoint.z);
      //
      //               vertexContainer.addPoint(point,
      //                        GColorI.newRGB(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f));
      //            }
      //         }
      //      }


      final int pointsCount = vertexContainer.size();
      //      System.out.println("pointsCount=" + pointsCount);

      final FloatBuffer pointsBuffer = ByteBuffer.allocateDirect(pointsCount * BYTES_PER_VECTOR3F).order(ByteOrder.nativeOrder()).asFloatBuffer();
      pointsBuffer.rewind();
      final FloatBuffer colorsBuffer = ByteBuffer.allocateDirect(pointsCount * (BYTES_PER_VECTOR3F + 4)).order(
               ByteOrder.nativeOrder()).asFloatBuffer();
      colorsBuffer.rewind();

      for (int i = 0; i < pointsCount; i++) {
         final IVector3 point = vertexContainer.getPoint(i);
         pointsBuffer.put((float) point.x());
         pointsBuffer.put((float) point.y());
         pointsBuffer.put((float) point.z());

         final IColor color = vertexContainer.getColor(i);
         colorsBuffer.put(color.getRed());
         colorsBuffer.put(color.getGreen());
         colorsBuffer.put(color.getBlue());
         colorsBuffer.put(alpha);
      }


      return new IMultidimensionalData.PointsCloud(pointsBuffer, colorsBuffer);
   }

}
