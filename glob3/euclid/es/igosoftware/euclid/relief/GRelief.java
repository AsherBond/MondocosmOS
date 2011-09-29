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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.imageio.ImageIO;

import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.loading.GBinaryPoints3Loader;
import es.igosoftware.euclid.loading.GPointsLoader;
import es.igosoftware.euclid.vector.GMutableVector3;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;
import es.igosoftware.io.GFileName;
import es.igosoftware.logging.GLoggerObject;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GMath;
import es.igosoftware.util.IFunction;


public class GRelief
         extends
            GLoggerObject {
   private static final class GPoint2I {
      private final int x;
      private final int y;


      private GPoint2I(final int x1,
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
         final GRelief.GPoint2I other = (GRelief.GPoint2I) obj;
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


   public static GRelief createReliefFromImage(final String name,
                                               final BufferedImage image,
                                               final GAxisAlignedBox bounds,
                                               final boolean verbose) {
      final GPoint2I gridExtent = new GRelief.GPoint2I(image.getWidth(), image.getHeight());
      final IVector2 resolution = bounds._extent.asVector2().div(new GVector2D(gridExtent.x, gridExtent.y));

      final Iterable<IVector3> points = initializePointsFromImage(image, gridExtent, bounds);

      return new GRelief(name, points.iterator(), resolution, verbose);
   }


   private static Iterable<IVector3> initializePointsFromImage(final BufferedImage image,
                                                               final GPoint2I gridExtent,
                                                               final GAxisAlignedBox bounds) {
      final int width = gridExtent.x;
      final int height = gridExtent.y;

      final double lowerX = bounds._lower.x();
      final double upperX = bounds._upper.x();
      final double lowerY = bounds._lower.y();
      final double upperY = bounds._upper.y();
      final double lowerZ = bounds._lower.z();
      final double upperZ = bounds._upper.z();

      final ArrayList<IVector3> result = new ArrayList<IVector3>(width * height);

      for (int column = 0; column < width; column++) {
         final double x = GMath.interpolate(lowerX, upperX, (double) column / (width - 1));

         for (int row = 0; row < height; row++) {
            final double y = GMath.interpolate(lowerY, upperY, (double) row / (height - 1));

            final int rgb = image.getRGB(column, row);
            final double r = rgb & 0xFF0000;
            final double g = rgb & 0x00FF00;
            final double b = rgb & 0x0000FF;
            final double gray = (r + g + b) / 3;

            final double zScale = gray / 255;

            final double z = GMath.interpolate(lowerZ, upperZ, zScale);

            final GVector3D point = new GVector3D(x, y, z);
            result.add(point);
         }
      }

      return result;
   }


   private final String                             _name;
   //private final Iterable<? extends T> _points;
   private final IVector2                           _resolution;

   private final GAxisAlignedOrthotope<IVector3, ?> _bounds;

   private final GRelief.GPoint2I                   _gridExtent;
   private final Set<IVector3>[][]                  _grid;

   private final boolean                            _verbose;


   public GRelief(final String name,
                  final GPointsLoader<IVector3, IVertexContainer.Vertex<IVector3>> loader,
                  final IVector2 resolution,
                  final boolean verbose) throws IOException {
      _name = name;
      _verbose = verbose;

      logInfo("Initializing...");

      logInfo("Loading points...");
      loader.load();

      final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> points = loader.getVertices();
      _resolution = resolution;

      //_bounds = GAxisAlignedBox.minimumBoundingBox(points.pointsIterator());
      _bounds = points.getBounds();

      _gridExtent = initializeGridExtent();

      _grid = initializeGrid(points.pointsIterator());

      showStatistics();
   }


   public GRelief(final String name,
                  final Iterator<IVector3> points,
                  final IVector2 resolution,
                  final boolean verbose) {
      _name = name;
      _verbose = verbose;

      logInfo("Initializing...");

      _resolution = resolution;

      _bounds = GAxisAlignedBox.minimumBoundingBox(points);
      //      _bounds = GAxisAlignedBox.minimumBoundingBox(points);

      _gridExtent = initializeGridExtent();

      _grid = initializeGrid(points);

      showStatistics();
   }


   public BufferedImage createImage() {
      final double lowerZ = _bounds._lower.z();
      final double upperZ = _bounds._upper.z();

      return createImage(lowerZ, upperZ);
   }


   public BufferedImage createImage(final double lowerZ,
                                    final double upperZ) {
      final BufferedImage bi = new BufferedImage(_gridExtent.x, _gridExtent.y, BufferedImage.TYPE_INT_RGB);

      final double deltaZ = upperZ - lowerZ;

      for (int x = 0; x < _gridExtent.x; x++) {
         for (int y = 0; y < _gridExtent.y; y++) {
            final double delta = GMath.clamp((getAverageZ(x, y) - lowerZ) / deltaZ, 0, 1);
            final int gray = Math.round((float) delta * 255);
            final int rgb = gray << 16 | gray << 8 | gray;
            bi.setRGB(x, _gridExtent.y - 1 - y, rgb);

            //bi.createGraphics().dr
         }
      }

      return bi;
   }


   public double getAverageZ(final int x,
                             final int y) {
      if ((x < 0) || (y < 0) || (x >= _gridExtent.x) || (y >= _gridExtent.y)) {
         return Double.NaN;
      }

      final Set<IVector3> leafPoints = _grid[x][y];

      double totalZ = 0;
      for (final IVector3 point : leafPoints) {
         totalZ += point.z();
      }

      return totalZ / leafPoints.size();
   }


   public GAxisAlignedOrthotope<IVector3, ?> getBounds() {
      return _bounds;
   }


   public int getColumnsCount() {
      return _gridExtent.x;
   }


   private GRelief.GPoint2I getKey(final IVector3 point) {
      final IVector3 lower = _bounds._lower;

      final int keyX = (int) ((point.x() - lower.x()) / _resolution.x());
      final int keyY = (int) ((point.y() - lower.y()) / _resolution.y());

      return new GRelief.GPoint2I(keyX, keyY);
   }


   private GRelief.GPoint2I getKey(final IVector2 point) {
      final IVector3 lower = _bounds._lower;

      final int keyX = (int) ((point.x() - lower.x()) / _resolution.x());
      final int keyY = (int) ((point.y() - lower.y()) / _resolution.y());

      return new GRelief.GPoint2I(keyX, keyY);
   }


   private IVector2 getPositionOfKey(final GRelief.GPoint2I key) {
      final IVector3 lower = _bounds._lower;

      final double x = key.x * _resolution.x() + lower.x();
      final double y = key.y * _resolution.y() + lower.y();
      return new GVector2D(x, y);
   }


   private Set<IVector3>[][] initializeGrid(final Iterator<IVector3> pointsIterator) {
      @SuppressWarnings("unchecked")
      final Set<IVector3>[][] result = new HashSet[_gridExtent.x][_gridExtent.y];

      for (int x = 0; x < _gridExtent.x; x++) {
         for (int y = 0; y < _gridExtent.y; y++) {
            result[x][y] = new HashSet<IVector3>(1);
         }
      }

      while (pointsIterator.hasNext()) {
         final IVector3 point = pointsIterator.next();
         final GRelief.GPoint2I key = getKey(point);
         result[key.x][key.y].add(point);
      }

      return result;
   }


   private GRelief.GPoint2I initializeGridExtent() {
      final IVector3 extent = _bounds._extent;
      final int columnsCount = (int) (extent.x() / _resolution.x()) + 1;
      final int rowsCount = (int) (extent.y() / _resolution.y()) + 1;
      return new GRelief.GPoint2I(columnsCount, rowsCount);
   }


   public Collection<IVector3> getPoints(final int x,
                                         final int y) {
      return Collections.unmodifiableSet(_grid[x][y]);
   }


   public Collection<IVector3> getPointsFromArea(final int xCenter,
                                                 final int yCenter,
                                                 final int outset) {
      return getPointsFromArea(xCenter - outset, yCenter - outset, xCenter + outset, yCenter + outset);
   }


   public Collection<IVector3> getPoints(final GRelief.GPoint2I center,
                                         final int outset) {
      return getPointsFromArea(center.x - outset, center.y - outset, center.x + outset, center.y + outset);
   }


   public Collection<IVector3> getPointsFromArea(final int xFrom,
                                                 final int yFrom,
                                                 final int xTo,
                                                 final int yTo) {

      final int campledXFrom = GMath.clamp(xFrom, 0, _gridExtent.x - 1);
      final int campledYFrom = GMath.clamp(yFrom, 0, _gridExtent.y - 1);
      final int campledXTo = GMath.clamp(xTo, 0, _gridExtent.x - 1);
      final int campledYTo = GMath.clamp(yTo, 0, _gridExtent.y - 1);

      final Set<IVector3> result = new HashSet<IVector3>();
      for (int x = campledXFrom; x <= campledXTo; x++) {
         for (int y = campledYFrom; y <= campledYTo; y++) {
            result.addAll(_grid[x][y]);
         }
      }

      return Collections.unmodifiableCollection(result);
   }


   public IVector2 getResolution() {
      return _resolution;
   }


   public int getRowsCount() {
      return _gridExtent.y;
   }


   public Collection<Double> getZs(final int x,
                                   final int y) {
      final Collection<Double> zs = GCollections.collect(_grid[x][y], new IFunction<IVector3, Double>() {
         @Override
         public Double apply(final IVector3 element) {
            return element.z();
         }
      });

      return Collections.unmodifiableCollection(zs);
   }


   public double getZ(final IVector2 position) {
      final GRelief.GPoint2I key = getKey(position);

      final IVector2 keyPosition = getPositionOfKey(key);

      if (keyPosition.equals(position)) {
         // the leaf at key represent the exact position, no need to interpolate
         return getAverageZ(key.x, key.y);
      }

      return getInterpolatedZ(position);
   }


   private double getInterpolatedZ(final IVector2 position) {
      final GRelief.GPoint2I key = getKey(position);

      final int campledXFrom = GMath.clamp(key.x - 1, 0, _gridExtent.x - 1);
      final int campledYFrom = GMath.clamp(key.y - 1, 0, _gridExtent.y - 1);
      final int campledXTo = GMath.clamp(key.x + 1, 0, _gridExtent.x - 1);
      final int campledYTo = GMath.clamp(key.y + 1, 0, _gridExtent.y - 1);

      GRelief.GPoint2I nearestNeighborKey = null;
      double nearestNeighborDistance = Double.POSITIVE_INFINITY;
      for (int x = campledXFrom; x <= campledXTo; x++) {
         for (int y = campledYFrom; y <= campledYTo; y++) {
            if ((x == key.x) && (y == key.y)) {
               continue;
            }

            final GRelief.GPoint2I currentKey = new GRelief.GPoint2I(x, y);
            final double currentDistance = getPositionOfKey(currentKey).distance(position);
            if (currentDistance < nearestNeighborDistance) {
               nearestNeighborKey = currentKey;
               nearestNeighborDistance = currentDistance;
            }
         }
      }

      //System.out.println("nearestNeighborKey=" + nearestNeighborKey);

      if (nearestNeighborKey == null) {
         return Double.NaN;
      }

      final double from = getAverageZ(key.x, key.y);
      final double to = getAverageZ(nearestNeighborKey.x, nearestNeighborKey.y);

      final IVector2 positionOfKey = getPositionOfKey(key);
      final double alpha = positionOfKey.distance(position) / getPositionOfKey(nearestNeighborKey).distance(positionOfKey);

      return GMath.interpolate(from, to, alpha);
   }


   //   public GRelief<IVector3> resampleTo(final String name,
   //                                       final IVector2 resolution) {
   //      //      final ArrayList<IVector3> points = new ArrayList<IVector3>();
   //      //
   //      //      for (double x = _bounds.lower.getX(); x <= _bounds.upper.getX(); x += resolution.getX()) {
   //      //         for (double y = _bounds.lower.getY(); y <= _bounds.upper.getY(); y += resolution.getY()) {
   //      //            points.add(new GVector3D(x, y, getZ(new GVector2D(x, y))));
   //      //         }
   //      //      }
   //      //
   //      //      return new GRelief<IVector3>(name, points, resolution, _verbose);
   //      return new GRelief<IVector3>(name, _points, resolution, _verbose);
   //   }


   @Override
   public String logName() {
      return "GRelief " + _name;
   }


   @Override
   public boolean logVerbose() {
      return _verbose;
   }


   /**
    * Replace the contents of each leaf with the average (point) of the contained points
    */
   public void downsizeByAverage() {

      int previousPoints = 0;
      int downsizedPoints = 0;
      for (int x = 0; x < _gridExtent.x; x++) {
         for (int y = 0; y < _gridExtent.y; y++) {
            final Set<IVector3> points = _grid[x][y];
            final int pointsCount = points.size();
            previousPoints += pointsCount;
            if (pointsCount > 1) {
               final Set<IVector3> newSet = new HashSet<IVector3>(1);
               newSet.add(getAverage(points));

               //               final Set<T> newSet = Collections.singleton(getAverage(points));
               _grid[x][y] = newSet;

               downsizedPoints += (pointsCount - 1);
            }
         }
      }

      if (_verbose) {
         final float percent = GMath.roundTo(100f * downsizedPoints / previousPoints, 2);
         logInfo("Downsized " + downsizedPoints + " points (" + percent + "%)");
         showStatistics();
      }

   }


   private IVector3 getAverage(final Collection<IVector3> points) {
      final Iterator<IVector3> iterator = points.iterator();

      final GMutableVector3<IVector3> sum = new GMutableVector3<IVector3>(iterator.next());

      while (iterator.hasNext()) {
         sum.add(iterator.next());
      }

      sum.div(points.size());

      return sum.getValue();
   }


   private void showStatistics() {
      if (!_verbose) {
         return;
      }

      logInfo("Statistics:");
      logIncreaseIdentationLevel();


      int pointsCounter = 0;
      int emptyleafs = 0;
      for (int x = 0; x < _gridExtent.x; x++) {
         for (int y = 0; y < _gridExtent.y; y++) {
            final Set<IVector3> leafPoints = _grid[x][y];
            final int pointsCount = leafPoints.size();
            pointsCounter += pointsCount;
            if (pointsCount == 0) {
               emptyleafs++;
            }
            //if (pointsCount > 1) {
            //    logWarning("leaf " + x + "x" + y + " has " + pointsCount + " points " + points);
            //}
         }
      }

      logInfoUnnamed("Points=" + pointsCounter);
      logInfoUnnamed("Bounds=" + _bounds);
      logInfoUnnamed("Extent=" + _bounds._extent);
      logInfoUnnamed("Grid=" + _gridExtent + " (" + (_gridExtent.x * _gridExtent.y) + " leafs)");
      logInfoUnnamed("Empty leafs=" + emptyleafs);
      logInfoUnnamed("Average Points per leafs=" + ((double) pointsCounter / (_gridExtent.x * _gridExtent.y)));
      logDecreaseIdentationLevel();
   }


   public static void main(final String[] args) throws IOException {
      System.out.println("GRelief 0.1");
      System.out.println("-----------\n");

      final GFileName fileName = GFileName.relative("..", "globe-caceres", "data", "mdt",
               "PNOA_EXT_NW_2006_30K_EL_mdtgint_h10_0677_3-4_huso29.bp");
      final GVector2D resolution = new GVector2D(5, 5);


      final GBinaryPoints3Loader loader = new GBinaryPoints3Loader(GPointsLoader.DEFAULT_FLAGS | GPointsLoader.VERBOSE, fileName);

      loader.load();
      final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> points = loader.getVertices();
      final GRelief relief = new GRelief("GConcurrentTest", points.pointsIterator(), resolution, true);

      //final GRelief<IVector3> relief = new GRelief<IVector3>("GConcurrentTest", loader, resolution, true);

      //      System.out.println(relief(1498, 1008));
      //      processXY(1498, 1008));

      //System.out.println(relief.getZs(0, 0));


      //      System.out.println(relief.getPointsFromArea(0, 0, 2, 2));
      //      System.out.println(relief.getPointsFromArea(1, 1, 1));
      //      System.out.println(relief.getPointsFromArea(0, 0, 1));
      //
      //      System.out.println(relief.getZ(new GVector2D(719820.0, 4384565.0)));
      //      System.out.println(relief.getZ(new GVector2D(719820.0 + 2.5, 4384565.0 + 2.5)));

      final BufferedImage image = relief.createImage();
      ImageIO.write(image, "png", new File("/home/dgd/Desktop/test.png"));

      final GRelief reducedRelief = new GRelief("Reduced GConcurrentTest", points.pointsIterator(), resolution.scale(2), true);
      reducedRelief.downsizeByAverage();
      ImageIO.write(reducedRelief.createImage(), "png", new File("/home/dgd/Desktop/test-reduced.png"));

      final double scale = 0.5;
      final BufferedImage scaledImage = new BufferedImage((int) (image.getWidth() * scale), (int) (image.getHeight() * scale),
               BufferedImage.TYPE_INT_RGB);
      final Graphics2D graphics2D = scaledImage.createGraphics();
      final AffineTransform xform = AffineTransform.getScaleInstance(scale, scale);
      graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      graphics2D.drawImage(image, xform, null);
      graphics2D.dispose();

      ImageIO.write(scaledImage, "png", new File("/home/dgd/Desktop/test-scaled-bicubic.png"));

      //      List<IVector3> points = new ArrayList<IVector3>();
      //
      //      final int extent = 100;
      //      for (int x = 0; x < extent; x++) {
      //         for (int y = 0; y < extent; y++) {
      //            final double z = x + y * 5;
      //            //final double z = x * x - y * y;
      //            points.add(new GVector3D(x, y, z));
      //         }
      //      }
      //
      //      final GRelief<IVector3> relief = new GRelief<IVector3>("GConcurrentTest", points, new GVector2D(1, 1), true);
      //
      //      System.out.println(relief.getZ(new GVector2D(10, 90)));
      //      System.out.println(relief.getZ(new GVector2D(10.5, 90.5))); // 463
      //
      //      ImageIO.write(relief.createImage(), "png", new File("/home/dgd/Desktop/test.png"));
      //
      //      final GRelief<IVector3> reducedRelief = relief.resampleTo("Reduced GConcurrentTest", new GVector2D(2, 2));
      //      ImageIO.write(reducedRelief.createImage(), "png", new File("/home/dgd/Desktop/test-reduced.png"));
      //
      //      final GRelief<IVector3> resampledRelief = relief.resampleTo("Resampled GConcurrentTest", new GVector2D(0.25, 0.25));
      //      ImageIO.write(resampledRelief.createImage(), "png", new File("/home/dgd/Desktop/test-resampled.png"));

   }


}
