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


package es.igosoftware.euclid.bounding;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import es.igosoftware.euclid.shape.GSegment2D;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IPointsContainer;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorFunction;
import es.igosoftware.util.GMath;


public final class GAxisAlignedRectangle
         extends
            GAxisAlignedOrthotope<IVector2, GAxisAlignedRectangle>
         implements
            IBounds2D<GAxisAlignedRectangle>,
            IFinite2DBounds<GAxisAlignedRectangle> {

   private static final long                 serialVersionUID = 1L;

   public static final GAxisAlignedRectangle EMPTY            = new GAxisAlignedRectangle(GVector2D.ZERO, GVector2D.ZERO);


   public static GAxisAlignedRectangle minimumBoundingRectangle(final Collection<? extends IPointsContainer<IVector2>> pointsContainers) {
      double minX = Double.POSITIVE_INFINITY;
      double minY = Double.POSITIVE_INFINITY;

      double maxX = Double.NEGATIVE_INFINITY;
      double maxY = Double.NEGATIVE_INFINITY;

      for (final IPointsContainer<IVector2> pointsContainer : pointsContainers) {
         for (final IVector2 point : pointsContainer) {
            final double x = point.x();
            final double y = point.y();

            minX = Math.min(minX, x);
            minY = Math.min(minY, y);

            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
         }
      }

      final IVector2 lower = new GVector2D(minX, minY);
      final IVector2 upper = new GVector2D(maxX, maxY);

      return new GAxisAlignedRectangle(lower, upper);
   }


   public static GAxisAlignedRectangle minimumBoundingRectangle(final Iterable<? extends IVector2> points) {
      double minX = Double.POSITIVE_INFINITY;
      double minY = Double.POSITIVE_INFINITY;

      double maxX = Double.NEGATIVE_INFINITY;
      double maxY = Double.NEGATIVE_INFINITY;

      for (final IVector2 point : points) {
         final double x = point.x();
         final double y = point.y();

         minX = Math.min(minX, x);
         minY = Math.min(minY, y);

         maxX = Math.max(maxX, x);
         maxY = Math.max(maxY, y);
      }

      final IVector2 lower = new GVector2D(minX, minY);
      final IVector2 upper = new GVector2D(maxX, maxY);

      return new GAxisAlignedRectangle(lower, upper);
   }


   public static GAxisAlignedRectangle minimumBoundingRectangle(final Iterator<? extends IVector2> points) {
      double minX = Double.POSITIVE_INFINITY;
      double minY = Double.POSITIVE_INFINITY;

      double maxX = Double.NEGATIVE_INFINITY;
      double maxY = Double.NEGATIVE_INFINITY;

      while (points.hasNext()) {
         final IVector2 point = points.next();

         final double x = point.x();
         final double y = point.y();

         minX = Math.min(minX, x);
         minY = Math.min(minY, y);

         maxX = Math.max(maxX, x);
         maxY = Math.max(maxY, y);
      }

      final IVector2 lower = new GVector2D(minX, minY);
      final IVector2 upper = new GVector2D(maxX, maxY);

      return new GAxisAlignedRectangle(lower, upper);
   }


   public static GAxisAlignedRectangle minimumBoundingRectangle(final IVector2... points) {
      double minX = Double.POSITIVE_INFINITY;
      double minY = Double.POSITIVE_INFINITY;

      double maxX = Double.NEGATIVE_INFINITY;
      double maxY = Double.NEGATIVE_INFINITY;

      for (final IVector2 point : points) {
         final double x = point.x();
         final double y = point.y();

         minX = Math.min(minX, x);
         minY = Math.min(minY, y);

         maxX = Math.max(maxX, x);
         maxY = Math.max(maxY, y);
      }

      final IVector2 lower = new GVector2D(minX, minY);
      final IVector2 upper = new GVector2D(maxX, maxY);

      return new GAxisAlignedRectangle(lower, upper);
   }


   public static GAxisAlignedRectangle merge(final GAxisAlignedRectangle... boxes) {
      double minX = Double.POSITIVE_INFINITY;
      double minY = Double.POSITIVE_INFINITY;

      double maxX = Double.NEGATIVE_INFINITY;
      double maxY = Double.NEGATIVE_INFINITY;

      for (final GAxisAlignedRectangle box : boxes) {
         final IVector2 currentLower = box._lower;
         final IVector2 currentUpper = box._upper;

         minX = Math.min(minX, currentLower.x());
         minY = Math.min(minY, currentLower.y());

         maxX = Math.max(maxX, currentUpper.x());
         maxY = Math.max(maxY, currentUpper.y());
      }


      if (minX == Double.POSITIVE_INFINITY) {
         return GAxisAlignedRectangle.EMPTY;
      }

      final IVector2 lower = new GVector2D(minX, minY);
      final IVector2 upper = new GVector2D(maxX, maxY);
      return new GAxisAlignedRectangle(lower, upper);
   }


   public static GAxisAlignedRectangle merge(final Iterable<GAxisAlignedRectangle> boxes) {
      double minX = Double.POSITIVE_INFINITY;
      double minY = Double.POSITIVE_INFINITY;

      double maxX = Double.NEGATIVE_INFINITY;
      double maxY = Double.NEGATIVE_INFINITY;

      for (final GAxisAlignedRectangle box : boxes) {
         final IVector2 currentLower = box._lower;
         final IVector2 currentUpper = box._upper;

         minX = Math.min(minX, currentLower.x());
         minY = Math.min(minY, currentLower.y());

         maxX = Math.max(maxX, currentUpper.x());
         maxY = Math.max(maxY, currentUpper.y());
      }


      if (minX == Double.POSITIVE_INFINITY) {
         return GAxisAlignedRectangle.EMPTY;
      }

      final IVector2 lower = new GVector2D(minX, minY);
      final IVector2 upper = new GVector2D(maxX, maxY);
      return new GAxisAlignedRectangle(lower, upper);
   }


   public static <PointT extends IVector2> GAxisAlignedRectangle load(final Class<PointT> pointClass,
                                                                      final String fileName) throws IOException {
      DataInputStream input = null;

      try {
         input = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName)));

         return load(pointClass, input);
      }
      finally {
         if (input != null) {
            input.close();
         }
      }
   }


   public static <PointT extends IVector2> GAxisAlignedRectangle load(final Class<PointT> pointClass,
                                                                      final DataInputStream input) throws IOException {

      try {
         final Method loadMethod = pointClass.getMethod("load", DataInputStream.class);

         final IVector2 lower = (IVector2) loadMethod.invoke(null, input);
         final IVector2 upper = (IVector2) loadMethod.invoke(null, input);
         //      final IVector2 lower = IVector2.load(input);
         //      final IVector2 upper = IVector2.load(input);
         return new GAxisAlignedRectangle(lower, upper);
      }
      catch (final Exception e) {
         throw new IOException(e);
      }
   }


   public GAxisAlignedRectangle(final IVector2 lower,
                                final IVector2 upper) {
      super(lower, upper);
   }


   @Override
   protected String getStringName() {
      return "Rectangle";
   }


   @Override
   public boolean touchesWithRectangle(final GAxisAlignedRectangle that) {
      final IVector2 thatLower = that._lower;
      final IVector2 thatUpper = that._upper;

      return ((_upper.x() > thatLower.x()) && (thatUpper.x() > _lower.x()) && (_upper.y() > thatLower.y()) && (thatUpper.y() > _lower.y()));
   }


   public boolean neighborWithRectangle(final GAxisAlignedRectangle that) {

      final double epsilon = 0.0001;
      return neighborWithRectangle(that, epsilon);
   }


   public boolean neighborWithRectangle(final GAxisAlignedRectangle that,
                                        final double epsilon) {

      final IVector2 thatLower = that._lower;
      final IVector2 thatUpper = that._upper;

      return (GMath.greaterOrEquals(_upper.x() + epsilon, thatLower.x()))
             && (GMath.greaterOrEquals(thatUpper.x() + epsilon, _lower.x()))
             && (GMath.greaterOrEquals(_upper.y() + epsilon, thatLower.y()))
             && (GMath.greaterOrEquals(thatUpper.y() + epsilon, _lower.y()));

   }


   @Override
   public boolean touches(final IBounds2D<?> that) {
      return that.touchesWithRectangle(this);
   }


   @Override
   public GAxisAlignedRectangle expandedByDistance(final double delta) {
      return new GAxisAlignedRectangle(_lower.sub(delta), _upper.add(delta));
   }


   @Override
   public GAxisAlignedRectangle expandedByDistance(final IVector2 delta) {
      return new GAxisAlignedRectangle(_lower.sub(delta), _upper.add(delta));
   }


   @Override
   public GAxisAlignedRectangle expandedByPercent(final double percent) {
      final IVector2 delta = _extent.scale(percent);
      return expandedByDistance(delta);
   }


   @Override
   public GAxisAlignedRectangle expandedByPercent(final IVector2 percent) {
      final IVector2 delta = _extent.scale(percent);
      return expandedByDistance(delta);
   }


   @Override
   public List<IVector2> getVertices() {
      final List<IVector2> v = new ArrayList<IVector2>(4);

      v.add(new GVector2D(_lower.x(), _lower.y()));
      v.add(new GVector2D(_lower.x(), _upper.y()));

      v.add(new GVector2D(_upper.x(), _lower.y()));
      v.add(new GVector2D(_upper.x(), _upper.y()));

      return Collections.unmodifiableList(v);
   }


   public List<GSegment2D> getEdges() {
      final List<GSegment2D> edges = new ArrayList<GSegment2D>(4);

      edges.add(new GSegment2D(new GVector2D(_lower.x(), _lower.y()), new GVector2D(_lower.x(), _upper.y())));
      edges.add(new GSegment2D(new GVector2D(_lower.x(), _upper.y()), new GVector2D(_upper.x(), _upper.y())));

      edges.add(new GSegment2D(new GVector2D(_upper.x(), _upper.y()), new GVector2D(_upper.x(), _lower.y())));
      edges.add(new GSegment2D(new GVector2D(_upper.x(), _lower.y()), new GVector2D(_lower.x(), _lower.y())));

      return Collections.unmodifiableList(edges);
   }


   @Override
   public boolean touchesWithDisk(final GDisk disk) {
      final IVector2 diskCenter = disk._center;
      final double diskRadius = disk._radius;
      return (Math.abs(_center.x() - diskCenter.x()) < (diskRadius + _extent.x()))
             && (Math.abs(_center.y() - diskCenter.y()) < (diskRadius + _extent.y()));
   }


   @Override
   public GAxisAlignedRectangle getBounds() {
      return this;
   }


   @Override
   public GAxisAlignedRectangle translatedBy(final IVector2 delta) {
      return new GAxisAlignedRectangle(_lower.add(delta), _upper.add(delta));
   }


   public GAxisAlignedRectangle[] subdividedByX() {
      return subdividedByFactorX(0.5);
   }


   public GAxisAlignedRectangle[] subdividedByY() {
      return subdividedByFactorY(0.5);
   }


   public GAxisAlignedRectangle[] subdividedByFactorX(final double factor) {
      if (GMath.closeToZero(_extent.x())) {
         throw new IllegalArgumentException("can't subdivided on an empty axis");
      }
      if (!(GMath.lessOrEquals(factor, 1.0) && GMath.greaterOrEquals(factor, 0.0))) {
         throw new IllegalArgumentException("Argument should be between 0.0 and 1.0");
      }

      final double offset = (_upper.x() - _lower.x()) * factor;
      final double pivot = _lower.x() + offset;
      //final double center = (_lower.x() + _upper.x()) * percent;

      final GAxisAlignedRectangle sub0 = new GAxisAlignedRectangle(_lower, new GVector2D(GMath.previousDown(pivot), _upper.y()));
      final GAxisAlignedRectangle sub1 = new GAxisAlignedRectangle(new GVector2D(pivot, _lower.y()), _upper);

      return new GAxisAlignedRectangle[] {
                        sub0,
                        sub1
      };
   }


   public GAxisAlignedRectangle[] subdividedByFactorY(final double factor) {
      if (GMath.closeToZero(_extent.y())) {
         throw new IllegalArgumentException("can't subdivided on an empty axis");
      }
      if (!(GMath.lessOrEquals(factor, 1.0) && GMath.greaterOrEquals(factor, 0.0))) {
         throw new IllegalArgumentException("Argument should be between 0.0 and 1.0");
      }

      final double offset = (_upper.y() - _lower.y()) * factor;
      final double pivot = _lower.y() + offset;
      //final double center = (_lower.y() + _upper.y()) * percent;

      final GAxisAlignedRectangle sub0 = new GAxisAlignedRectangle(_lower, new GVector2D(_upper.x(), GMath.previousDown(pivot)));
      final GAxisAlignedRectangle sub1 = new GAxisAlignedRectangle(new GVector2D(_lower.x(), pivot), _upper);

      return new GAxisAlignedRectangle[] {
                        sub0,
                        sub1
      };
   }


   /**
    * Subdivide a GAxisAlignedRectangle by numberX in X axis
    * 
    * @param numberX
    * @return list with numberX elements of type GAxisAlignedRectangle result from subdivision
    */
   public GAxisAlignedRectangle[] subdividedByNumberX(final int numberX) {

      if (!GMath.greaterOrEquals(numberX, 1)) {
         throw new IllegalArgumentException("Argument must be greater than 1");
      }

      final GAxisAlignedRectangle[] result = new GAxisAlignedRectangle[numberX];

      final double edgeX = _extent.x() / numberX;

      final double lowerLowerX = _lower.x();

      for (int i = 0; i < numberX; i++) {

         final double lowerX = lowerLowerX + i * edgeX;
         final double upperX = GMath.previousDown(lowerX + edgeX);

         final GAxisAlignedRectangle box = new GAxisAlignedRectangle(new GVector2D(lowerX, _lower.y()), new GVector2D(upperX,
                  _upper.y()));
         result[i] = box;
      }

      return result;
   }


   /**
    * Subdivide a GAxisAlignedRectangle by numberY in Y axis
    * 
    * @param numberY
    * @return list with numberY elements of type GAxisAlignedRectangle result from subdivision
    */
   public GAxisAlignedRectangle[] subdividedByNumberY(final int numberY) {

      if (!GMath.greaterOrEquals(numberY, 1)) {
         throw new IllegalArgumentException("Argument must be greater than 1");
      }

      final GAxisAlignedRectangle[] result = new GAxisAlignedRectangle[numberY];

      final double edgeY = _extent.y() / numberY;

      final double lowerLowerY = _lower.y();

      for (int i = 0; i < numberY; i++) {

         final double lowerY = lowerLowerY + i * edgeY;
         final double upperY = GMath.previousDown(lowerY + edgeY);

         final GAxisAlignedRectangle box = new GAxisAlignedRectangle(new GVector2D(_lower.x(), lowerY), new GVector2D(_upper.x(),
                  upperY));
         result[i] = box;
      }

      return result;
   }


   @Override
   public boolean touchesWithCapsule2D(final GCapsule2D capsule) {

      final IVector2 segmentClosestPoint = capsule._segment.closestPoint(_center);
      final double capsuleRadius = capsule._radius;

      return (Math.abs(_center.x() - segmentClosestPoint.x()) < (capsuleRadius + _extent.x()))
             && (Math.abs(_center.y() - segmentClosestPoint.y()) < (capsuleRadius + _extent.y()));

   }


   @Override
   public GAxisAlignedRectangle asAxisAlignedOrthotope() {
      return this;
   }


   @Override
   public GAxisAlignedRectangle[] subdividedByAxis(final byte axis) {
      switch (axis) {
         case 0:
            return subdividedByX();
         case 1:
            return subdividedByY();
         default:
            throw new IllegalArgumentException("Invalid axis=" + axis);
      }
   }


   @Override
   public GAxisAlignedRectangle[] subdividedAtCenter() {
      final GAxisAlignedRectangle[] divisionsAtX = subdividedByX();

      final GAxisAlignedRectangle[] children0 = divisionsAtX[0].subdividedByY();
      final GAxisAlignedRectangle[] children1 = divisionsAtX[1].subdividedByY();

      return new GAxisAlignedRectangle[] {
                        children0[0],
                        children0[1],
                        children1[0],
                        children1[1]
      };
   }


   @Override
   public GAxisAlignedRectangle[] subdividedAt(final IVector2 pivot) {
      final GAxisAlignedRectangle[] result = new GAxisAlignedRectangle[4];

      for (int i = 0; i < 4; i++) {
         result[i] = splitByXY(i, pivot);
      }

      return result;
   }


   private GAxisAlignedRectangle splitByXY(final int key,
                                           final IVector2 pivot) {
      final int xKey = key & 1;
      final int yKey = key & 2;

      final double octLowerX = (xKey == 0) ? _lower.x() : pivot.x();
      final double octLowerY = (yKey == 0) ? _lower.y() : pivot.y();

      double octUpperX = (xKey == 0) ? pivot.x() : _upper.x();
      double octUpperY = (yKey == 0) ? pivot.y() : _upper.y();

      if (octUpperX < _upper.x()) {
         octUpperX = GMath.previousDown(octUpperX);
      }
      if (octUpperY < _upper.y()) {
         octUpperY = GMath.previousDown(octUpperY);
      }

      return new GAxisAlignedRectangle(new GVector2D(octLowerX, octLowerY), new GVector2D(octUpperX, octUpperY));
   }


   @Override
   public boolean touches(final GAxisAlignedRectangle that) {
      return touchesWithRectangle(that);
   }


   @Override
   public boolean touches(final GAxisAlignedOrthotope<IVector2, ?> that) {
      return touchesWithRectangle((GAxisAlignedRectangle) that);
   }


   @Override
   public boolean touchesBounds(final IBounds<IVector2, ?> that) {
      return touches((IBounds2D<?>) that);
   }


   private double getWidth() {
      return _upper.x() - _lower.x();
   }


   private double getHeight() {
      return _upper.y() - _lower.y();
   }


   @Override
   public GAxisAlignedRectangle mergedWith(final GAxisAlignedOrthotope<IVector2, ?> that) {
      return new GAxisAlignedRectangle(_lower.min(that._lower), _upper.max(that._upper));
   }


   @Override
   public GAxisAlignedRectangle clamp(final GAxisAlignedOrthotope<IVector2, ?> that) {
      return new GAxisAlignedRectangle(_lower.clamp(that._lower, that._upper), _upper.clamp(that._lower, that._upper));
   }


   @Override
   public GAxisAlignedRectangle transform(final IVectorFunction<IVector2> transformer) {
      if (transformer == null) {
         return this;
      }
      return new GAxisAlignedRectangle(transformer.apply(_lower), transformer.apply(_upper));
   }


   public GAxisAlignedRectangle intersection(final GAxisAlignedRectangle that) {
      if (!touches(that)) {
         return null;
      }

      return new GAxisAlignedRectangle(_lower.max(that._lower), _upper.min(that._upper));
   }


   @Override
   public double area() {
      final double width = getWidth();
      final double height = getHeight();
      return width * height;
   }


   @Override
   public double perimeter() {
      final double width = getWidth();
      final double height = getHeight();
      return (width + height) * 2;
   }


   @Override
   public GSegment2D getVerticalBisector() {
      return getVerticalBisectorAt(_center.x());
   }


   public GSegment2D getVerticalBisectorAt(final double x) {
      final IVector2 from = new GVector2D(x, _lower.y());
      final IVector2 to = new GVector2D(x, _upper.y());

      return new GSegment2D(from, to);
   }


   @Override
   public GSegment2D getHorizontalBisector() {
      return getHorizontalBisectorAt(_center.y());
   }


   public GSegment2D getHorizontalBisectorAt(final double y) {
      final IVector2 from = new GVector2D(_lower.x(), y);
      final IVector2 to = new GVector2D(_upper.x(), y);

      return new GSegment2D(from, to);
   }


   public GSegment2D getSouthWestToNorthEastBisector() {
      return new GSegment2D(_lower, _upper);
   }


   public GSegment2D getNorthWestToSouthEastBisector() {
      return new GSegment2D(//
               new GVector2D(_lower.x(), _upper.y()), //
               new GVector2D(_upper.x(), _lower.y()) //
      );

   }


   @Override
   public GAxisAlignedRectangle scale(final double scale) {
      return new GAxisAlignedRectangle(_lower.scale(scale), _upper.scale(scale));
   }


   @Override
   public GAxisAlignedRectangle scale(final IVector2 scale) {
      return new GAxisAlignedRectangle(_lower.scale(scale), _upper.scale(scale));
   }


}
