

package es.unex.s3xtante.modules.layers;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import es.igosoftware.euclid.colors.GColorI;
import es.igosoftware.euclid.colors.GColorPrecision;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.GVectorPrecision;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.verticescontainer.GVertex3Container;
import es.igosoftware.experimental.ndimensional.IMultidimensionalData;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GRange;
import es.igosoftware.util.IRangeEvaluator;
import es.igosoftware.utils.GPositionBox;
import es.igosoftware.utils.GWWUtils;
import es.unex.sextante.core.AnalysisExtent;
import es.unex.sextante.dataObjects.I3DRasterLayer;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;


public class G3DASCIILayerMultidimensionalData
         implements
            IMultidimensionalData {


   private static final int BYTES_PER_VECTOR3F = 3 * 4;


   private static double    _minValue;
   private static double    _maxValue;

   private I3DRasterLayer   _layer;
   private final String     _name;

   private GPositionBox     _box               = null;

   private final File       _file;

   private Position         _position;

   private IColor[][][]     _data;


   public G3DASCIILayerMultidimensionalData(final File file) {

      _file = file;
      _name = file.getName();

      initializeData();

   }


   private void initializeData() {

      try {
         _layer = es.unex.sextante.io3d.ASCII3DFileTools.readFile(_file);

         final AnalysisExtent extent = _layer.getLayerExtent();
         _position = new Position(LatLon.fromDegrees(extent.getYMin(), extent.getXMin()), extent.getZMin());
         _box = new GPositionBox(_position, GWWUtils.increment(_position, extent.getNX() * extent.getCellSize(),
                  extent.getNY() * extent.getCellSize(), extent.getNZ() * extent.getCellSizeZ()));

         calculateExtremeValues(_layer);

         _data = new IColor[_layer.getNX()][_layer.getNY()][_layer.getNZ()];

         final double dRange = _maxValue - _minValue;

         for (int z = 0; z < _layer.getNZ(); z++) {
            for (int y = 0; y < _layer.getNY(); y++) {
               for (int x = 0; x < _layer.getNX(); x++) {
                  final double dValue = _layer.getCellValueAsDouble(x, y, z);
                  final float color = (float) ((dValue - _minValue) / dRange);
                  final GColorI iColor = GColorI.newRGB(color, color, color);
                  _data[x][y][z] = iColor;
               }
            }

         }

      }
      catch (final Exception e) {
         e.printStackTrace();
         _layer = null;
      }

   }


   private static void calculateExtremeValues(final I3DRasterLayer layer) {

      _minValue = Double.MAX_VALUE;
      _maxValue = Double.NEGATIVE_INFINITY;

      for (int z = 0; z < layer.getNZ(); z++) {
         for (int y = 0; y < layer.getNY(); y++) {
            for (int x = 0; x < layer.getNX(); x++) {
               final double dValue = layer.getCellValueAsDouble(x, y, z);
               _minValue = Math.min(_minValue, dValue);
               _maxValue = Math.max(_maxValue, dValue);
            }
         }
      }

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
         return _layer.getNX();
      }
      else if (dimensionName.equals("y")) {
         return _layer.getNY();
      }
      else if (dimensionName.equals("z")) {
         return _layer.getNZ();
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

                     final Position position = GWWUtils.increment(_position, x * _layer.getCellSize(), y * _layer.getCellSizeZ(),
                              (_layer.getNZ() - z) * _layer.getCellSizeZ());

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


      final int pointsCount = vertexContainer.size();

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
