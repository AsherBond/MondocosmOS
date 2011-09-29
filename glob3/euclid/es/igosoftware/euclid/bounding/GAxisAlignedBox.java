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

import es.igosoftware.euclid.shape.GPlane;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.IPointsContainer;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.GMath;
import es.igosoftware.util.XStringTokenizer;


public final class GAxisAlignedBox
         extends
            GAxisAlignedOrthotope<IVector3, GAxisAlignedBox>
         implements
            IBounds3D<GAxisAlignedBox>,
            IFinite3DBounds<GAxisAlignedBox> {

   private static final long           serialVersionUID = 1L;

   public static final GAxisAlignedBox EMPTY            = new GAxisAlignedBox(GVector3D.ZERO, GVector3D.ZERO);


   public static <PointT extends IVector3> GAxisAlignedBox load(final Class<PointT> pointClass,
                                                                final DataInputStream input) throws IOException {

      try {
         final Method loadMethod = pointClass.getMethod("load", DataInputStream.class);

         final IVector3 lower = (IVector3) loadMethod.invoke(null, input);
         final IVector3 upper = (IVector3) loadMethod.invoke(null, input);
         return new GAxisAlignedBox(lower, upper);
      }
      catch (final Exception e) {
         throw new IOException(e);
      }
   }


   public static <PointT extends IVector3> GAxisAlignedBox load(final Class<PointT> pointClass,
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


   public static GAxisAlignedBox minimumBoundingBox(final Collection<? extends IPointsContainer<IVector3>> pointsContainers) {
      double minX = Double.POSITIVE_INFINITY;
      double minY = Double.POSITIVE_INFINITY;
      double minZ = Double.POSITIVE_INFINITY;

      double maxX = Double.NEGATIVE_INFINITY;
      double maxY = Double.NEGATIVE_INFINITY;
      double maxZ = Double.NEGATIVE_INFINITY;

      for (final IPointsContainer<IVector3> pointsContainer : pointsContainers) {
         for (final IVector3 point : pointsContainer) {
            final double x = point.x();
            final double y = point.y();
            final double z = point.z();

            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            minZ = Math.min(minZ, z);

            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
            maxZ = Math.max(maxZ, z);
         }
      }

      final IVector3 lower = new GVector3D(minX, minY, minZ);
      final IVector3 upper = new GVector3D(maxX, maxY, maxZ);
      return new GAxisAlignedBox(lower, upper);
   }


   public static GAxisAlignedBox merge(final GAxisAlignedBox... boxes) {
      double minX = Double.POSITIVE_INFINITY;
      double minY = Double.POSITIVE_INFINITY;
      double minZ = Double.POSITIVE_INFINITY;

      double maxX = Double.NEGATIVE_INFINITY;
      double maxY = Double.NEGATIVE_INFINITY;
      double maxZ = Double.NEGATIVE_INFINITY;

      for (final GAxisAlignedBox box : boxes) {
         final IVector3 currentLower = box._lower;
         final IVector3 currentUpper = box._upper;

         minX = Math.min(minX, currentLower.x());
         minY = Math.min(minY, currentLower.y());
         minZ = Math.min(minZ, currentLower.z());

         maxX = Math.max(maxX, currentUpper.x());
         maxY = Math.max(maxY, currentUpper.y());
         maxZ = Math.max(maxZ, currentUpper.z());
      }


      if (minX == Double.POSITIVE_INFINITY) {
         return GAxisAlignedBox.EMPTY;
      }

      final IVector3 lower = new GVector3D(minX, minY, minZ);
      final IVector3 upper = new GVector3D(maxX, maxY, maxZ);
      return new GAxisAlignedBox(lower, upper);
   }


   public static GAxisAlignedBox merge(final Iterable<GAxisAlignedBox> boxes) {
      if (!boxes.iterator().hasNext()) {
         return null;
      }


      double minX = Double.POSITIVE_INFINITY;
      double minY = Double.POSITIVE_INFINITY;
      double minZ = Double.POSITIVE_INFINITY;

      double maxX = Double.NEGATIVE_INFINITY;
      double maxY = Double.NEGATIVE_INFINITY;
      double maxZ = Double.NEGATIVE_INFINITY;

      for (final GAxisAlignedBox box : boxes) {
         final IVector3 currentLower = box._lower;
         final IVector3 currentUpper = box._upper;

         minX = Math.min(minX, currentLower.x());
         minY = Math.min(minY, currentLower.y());
         minZ = Math.min(minZ, currentLower.z());

         maxX = Math.max(maxX, currentUpper.x());
         maxY = Math.max(maxY, currentUpper.y());
         maxZ = Math.max(maxZ, currentUpper.z());
      }


      //      if (minX == Double.POSITIVE_INFINITY) {
      //         return GAxisAlignedBox.EMPTY;
      //      }

      final GVector3D lower = new GVector3D(minX, minY, minZ);
      final GVector3D upper = new GVector3D(maxX, maxY, maxZ);
      return new GAxisAlignedBox(lower, upper);
   }


   public static GAxisAlignedBox minimumBoundingBox(final Iterable<? extends IVector3> points) {
      return minimumBoundingBox(points.iterator());
   }


   public static GAxisAlignedBox minimumBoundingBox(final Iterator<? extends IVector3> iterator) {
      double minX = Double.POSITIVE_INFINITY;
      double minY = Double.POSITIVE_INFINITY;
      double minZ = Double.POSITIVE_INFINITY;

      double maxX = Double.NEGATIVE_INFINITY;
      double maxY = Double.NEGATIVE_INFINITY;
      double maxZ = Double.NEGATIVE_INFINITY;

      while (iterator.hasNext()) {
         final IVector3 point = iterator.next();
         final double x = point.x();
         final double y = point.y();
         final double z = point.z();

         minX = Math.min(minX, x);
         minY = Math.min(minY, y);
         minZ = Math.min(minZ, z);

         maxX = Math.max(maxX, x);
         maxY = Math.max(maxY, y);
         maxZ = Math.max(maxZ, z);
      }

      final IVector3 lower = new GVector3D(minX, minY, minZ);
      final IVector3 upper = new GVector3D(maxX, maxY, maxZ);

      return new GAxisAlignedBox(lower, upper);
   }


   public static <PointT extends IVector3> GAxisAlignedBox parseString(final Class<PointT> pointClass,
                                                                       final String string) throws IOException {
      try {
         final String[] tokens = XStringTokenizer.getAllTokens(string, "_");
         if (tokens.length != 2) {
            throw new IOException("Invalid string format: " + string);
         }

         final Method parseMethod = pointClass.getMethod("parseString", String.class);

         final IVector3 lower = (IVector3) parseMethod.invoke(null, tokens[0]);
         final IVector3 upper = (IVector3) parseMethod.invoke(null, tokens[1]);
         return new GAxisAlignedBox(lower, upper);
      }
      catch (final Exception e) {
         throw new IOException(e + ": " + string);
      }
   }


   public GAxisAlignedBox(final IVector3 lower,
                          final IVector3 upper) {
      super(lower, upper);
   }


   @Override
   public GAxisAlignedBox expandedByDistance(final double delta) {
      return new GAxisAlignedBox(_lower.sub(delta), _upper.add(delta));
   }


   @Override
   public GAxisAlignedBox expandedByDistance(final IVector3 delta) {
      return new GAxisAlignedBox(_lower.sub(delta), _upper.add(delta));
   }


   @Override
   public GAxisAlignedBox expandedByPercent(final double percent) {
      final IVector3 delta = _extent.scale(percent);
      return expandedByDistance(delta);
   }


   @Override
   public GAxisAlignedBox expandedByPercent(final IVector3 percent) {
      final IVector3 delta = _extent.scale(percent);
      return expandedByDistance(delta);
   }


   @Override
   public GAxisAlignedBox getBounds() {
      return this;
   }


   @Override
   protected String getStringName() {
      return "Box";
   }


   @Override
   public List<IVector3> getVertices() {
      final List<IVector3> v = new ArrayList<IVector3>(8);

      v.add(new GVector3D(_lower.x(), _lower.y(), _lower.z()));
      v.add(new GVector3D(_lower.x(), _lower.y(), _upper.z()));
      v.add(new GVector3D(_lower.x(), _upper.y(), _upper.z()));
      v.add(new GVector3D(_lower.x(), _upper.y(), _lower.z()));

      v.add(new GVector3D(_upper.x(), _lower.y(), _lower.z()));
      v.add(new GVector3D(_upper.x(), _lower.y(), _upper.z()));
      v.add(new GVector3D(_upper.x(), _upper.y(), _upper.z()));
      v.add(new GVector3D(_upper.x(), _upper.y(), _lower.z()));

      return Collections.unmodifiableList(v);
   }


   public double getVolume() {
      return _extent.x() * _extent.y() * _extent.z();
   }


   //   public GAxisAlignedBox reproject(final GProjection sourceProjection,
   //                                    final GProjection targetProjection) {
   //      if (sourceProjection == targetProjection) {
   //         return this;
   //      }
   //
   //      final IVector3 projectedLower = _lower.reproject(sourceProjection, targetProjection);
   //      final IVector3 projectedUpper = _upper.reproject(sourceProjection, targetProjection);
   //      return new GAxisAlignedBox(projectedLower, projectedUpper);
   //   }


   private GAxisAlignedBox splitByXY(final int key,
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

      return new GAxisAlignedBox(new GVector3D(octLowerX, octLowerY, _lower.z()), new GVector3D(octUpperX, octUpperY, _upper.z()));
   }


   private GAxisAlignedBox splitByXYZ(final int key,
                                      final IVector3 pivot) {
      final int xKey = key & 1;
      final int yKey = key & 2;
      final int zKey = key & 4;

      final double octLowerX = (xKey == 0) ? _lower.x() : pivot.x();
      final double octLowerY = (yKey == 0) ? _lower.y() : pivot.y();
      final double octLowerZ = (zKey == 0) ? _lower.z() : pivot.z();

      double octUpperX = (xKey == 0) ? pivot.x() : _upper.x();
      double octUpperY = (yKey == 0) ? pivot.y() : _upper.y();
      double octUpperZ = (zKey == 0) ? pivot.z() : _upper.z();

      if (octUpperX < _upper.x()) {
         octUpperX = GMath.previousDown(octUpperX);
      }
      if (octUpperY < _upper.y()) {
         octUpperY = GMath.previousDown(octUpperY);
      }
      if (octUpperZ < _upper.z()) {
         octUpperZ = GMath.previousDown(octUpperZ);
      }

      return new GAxisAlignedBox(new GVector3D(octLowerX, octLowerY, octLowerZ), new GVector3D(octUpperX, octUpperY, octUpperZ));
   }


   public GAxisAlignedBox[] subdividedByX() {
      final double pivot = (_lower.x() + _upper.x()) / 2;
      return subdividedByX(pivot);
   }


   public GAxisAlignedBox[] subdividedByY() {
      final double pivot = (_lower.y() + _upper.y()) / 2;
      return subdividedByY(pivot);
   }


   public GAxisAlignedBox[] subdividedByZ() {
      final double pivot = (_lower.z() + _upper.z()) / 2;
      return subdividedByZ(pivot);
   }


   public GAxisAlignedBox[] subdividedByFactorX(final double factor) {
      if (!(GMath.lessOrEquals(factor, 1.0) && GMath.greaterOrEquals(factor, 0.0))) {
         throw new IllegalArgumentException("Argument should be between 0.0 and 1.0");
      }
      final double offset = (_upper.x() - _lower.x()) * factor;
      final double pivot = _lower.x() + offset;
      return subdividedByX(pivot);
   }


   public GAxisAlignedBox[] subdividedByFactorY(final double factor) {
      if (!(GMath.lessOrEquals(factor, 1.0) && GMath.greaterOrEquals(factor, 0.0))) {
         throw new IllegalArgumentException("Argument should be between 0.0 and 1.0");
      }
      final double offset = (_upper.y() - _lower.y()) * factor;
      final double pivot = _lower.y() + offset;
      return subdividedByY(pivot);
   }


   public GAxisAlignedBox[] subdividedByFactorZ(final double factor) {
      if (!(GMath.lessOrEquals(factor, 1.0) && GMath.greaterOrEquals(factor, 0.0))) {
         throw new IllegalArgumentException("Argument should be between 0.0 and 1.0");
      }
      final double offset = (_upper.z() - _lower.z()) * factor;
      final double pivot = _lower.z() + offset;
      return subdividedByZ(pivot);
   }


   public GAxisAlignedBox[] subdividedByX(final double pivot) {
      if (GMath.closeToZero(_extent.x())) {
         throw new IllegalArgumentException("can't subdivided on an empty axis");
      }

      final GAxisAlignedBox sub0 = new GAxisAlignedBox(_lower, new GVector3D(GMath.previousDown(pivot), _upper.y(), _upper.z()));
      final GAxisAlignedBox sub1 = new GAxisAlignedBox(new GVector3D(pivot, _lower.y(), _lower.z()), _upper);

      return new GAxisAlignedBox[] {
                        sub0,
                        sub1
      };
   }


   public GAxisAlignedBox[] subdividedByY(final double pivot) {
      if (GMath.closeToZero(_extent.y())) {
         throw new IllegalArgumentException("can't subdivided on an empty axis");
      }

      final GAxisAlignedBox sub0 = new GAxisAlignedBox(_lower, new GVector3D(_upper.x(), GMath.previousDown(pivot), _upper.z()));
      final GAxisAlignedBox sub1 = new GAxisAlignedBox(new GVector3D(_lower.x(), pivot, _lower.z()), _upper);

      return new GAxisAlignedBox[] {
                        sub0,
                        sub1
      };
   }


   public GAxisAlignedBox[] subdividedByZ(final double pivot) {
      if (GMath.closeToZero(_extent.z())) {
         throw new IllegalArgumentException("can't subdivided on an empty axis");
      }

      final GAxisAlignedBox sub0 = new GAxisAlignedBox(_lower, new GVector3D(_upper.x(), _upper.y(), GMath.previousDown(pivot)));
      final GAxisAlignedBox sub1 = new GAxisAlignedBox(new GVector3D(_lower.x(), _lower.y(), pivot), _upper);

      return new GAxisAlignedBox[] {
                        sub0,
                        sub1
      };
   }


   public GAxisAlignedBox[] subdividedByXY() {
      return subdividedByXY(_center.asVector2());
   }


   public GAxisAlignedBox[] subdividedByXY(final IVector2 pivot) {
      final GAxisAlignedBox[] result = new GAxisAlignedBox[4];

      for (int i = 0; i < 4; i++) {
         result[i] = splitByXY(i, pivot);
      }

      return result;
   }


   /**
    * Subdivide a GAxisAlignedBox by numberX in X axis and numberY in axis Y.
    * 
    * @param numberX
    * @param numberY
    * @return list with (numberX * numberY) elements of type GAxisAlignedBox result from subdivision
    */
   public GAxisAlignedBox[] subdividedByNumberXY(final int numberX,
                                                 final int numberY) {

      if (!(GMath.greaterOrEquals(numberX, 1) && GMath.greaterOrEquals(numberY, 1))) {
         throw new IllegalArgumentException("Argument must be greater than 1");
      }

      final GAxisAlignedBox[] result = new GAxisAlignedBox[numberX * numberY];

      final double edgeX = _extent.x() / numberX;
      final double edgeY = _extent.y() / numberY;

      int index = 0;

      final double lowerLowerX = _lower.x();
      final double lowerLowerY = _lower.y();

      for (int i = 0; i < numberX; i++) {

         final double lowerX = lowerLowerX + i * edgeX;
         final double upperX = GMath.previousDown(lowerX + edgeX);

         for (int j = 0; j < numberY; j++) {

            final double lowerY = lowerLowerY + j * edgeY;
            final double upperY = GMath.previousDown(lowerY + edgeY);

            final GAxisAlignedBox box = new GAxisAlignedBox(new GVector3D(lowerX, lowerY, _lower.z()), new GVector3D(upperX,
                     upperY, _upper.z()));
            result[index] = box;
            index++;
         }
      }

      return result;
   }


   /**
    * Subdivide a GAxisAlignedBox by numberX in X axis
    * 
    * @param numberX
    * @return list with numberX elements of type GAxisAlignedBox result from subdivision
    */
   public GAxisAlignedBox[] subdividedByNumberX(final int numberX) {

      if (!GMath.greaterOrEquals(numberX, 1)) {
         throw new IllegalArgumentException("Argument must be greater than 1");
      }

      final GAxisAlignedBox[] result = new GAxisAlignedBox[numberX];

      final double edgeX = _extent.x() / numberX;

      final double lowerLowerX = _lower.x();

      for (int i = 0; i < numberX; i++) {

         final double lowerX = lowerLowerX + i * edgeX;
         final double upperX = GMath.previousDown(lowerX + edgeX);

         final GAxisAlignedBox box = new GAxisAlignedBox(new GVector3D(lowerX, _lower.y(), _lower.z()), new GVector3D(upperX,
                  _upper.y(), _upper.z()));
         result[i] = box;
      }

      return result;
   }


   /**
    * Subdivide a GAxisAlignedBox by numberY in Y axis
    * 
    * @param numberY
    * @return list with numberY elements of type GAxisAlignedBox result from subdivision
    */
   public GAxisAlignedBox[] subdividedByNumberY(final int numberY) {

      if (!GMath.greaterOrEquals(numberY, 1)) {
         throw new IllegalArgumentException("Argument must be greater than 1");
      }

      final GAxisAlignedBox[] result = new GAxisAlignedBox[numberY];

      final double edgeY = _extent.y() / numberY;

      final double lowerLowerY = _lower.y();

      for (int i = 0; i < numberY; i++) {

         final double lowerY = lowerLowerY + i * edgeY;
         final double upperY = GMath.previousDown(lowerY + edgeY);

         final GAxisAlignedBox box = new GAxisAlignedBox(new GVector3D(_lower.x(), lowerY, _lower.z()), new GVector3D(_upper.x(),
                  upperY, _upper.z()));
         result[i] = box;
      }

      return result;
   }


   /**
    * Subdivide a GAxisAlignedBox by numberZ in Z axis
    * 
    * @param numberZ
    * @return list with numberZ elements of type GAxisAlignedBox result from subdivision
    */
   public GAxisAlignedBox[] subdividedByNumberZ(final int numberZ) {

      if (!GMath.greaterOrEquals(numberZ, 1)) {
         throw new IllegalArgumentException("Argument must be greater than 1");
      }

      final GAxisAlignedBox[] result = new GAxisAlignedBox[numberZ];

      final double edgeZ = _extent.y() / numberZ;

      final double lowerLowerZ = _lower.z();

      for (int i = 0; i < numberZ; i++) {

         final double lowerZ = lowerLowerZ + i * edgeZ;
         final double upperZ = GMath.previousDown(lowerZ + edgeZ);

         final GAxisAlignedBox box = new GAxisAlignedBox(new GVector3D(_lower.x(), _lower.y(), lowerZ), new GVector3D(_upper.x(),
                  _upper.y(), upperZ));
         result[i] = box;
      }

      return result;
   }


   @Override
   public GAxisAlignedBox[] subdividedAtCenter() {
      return subdividedAt(_center);
   }


   @Override
   public GAxisAlignedBox[] subdividedAt(final IVector3 pivot) {
      final GAxisAlignedBox[] result = new GAxisAlignedBox[8];

      for (int i = 0; i < 8; i++) {
         result[i] = splitByXYZ(i, pivot);
      }

      return result;
   }


   @Override
   public boolean touches(final IBounds3D<?> that) {
      return that.touchesWithBox(this);
   }


   @Override
   public boolean touchesBounds(final IBounds<IVector3, ?> that) {
      return touches((IBounds3D<?>) that);
   }


   @Override
   public boolean touchesWithBall(final GBall ball) {
      final IVector3 ballCenter = ball._center;
      final double ballRadius = ball._radius;
      return (Math.abs(_center.x() - ballCenter.x()) < (ballRadius + _extent.x()))
             && (Math.abs(_center.y() - ballCenter.y()) < (ballRadius + _extent.y()))
             && (Math.abs(_center.z() - ballCenter.z()) < (ballRadius + _extent.z()));
   }


   @Override
   public boolean touchesWithBox(final GAxisAlignedBox that) {
      return GBoundingUtils.touchesWithBox(this, that);
   }


   @Override
   public boolean touchesWithPlane(final GPlane plane) {
      return plane.touchesWithBox(this);
   }


   @Override
   public GAxisAlignedBox translatedBy(final IVector3 delta) {
      return new GAxisAlignedBox(_lower.add(delta), _upper.add(delta));
   }


   @Override
   public boolean touchesWithCapsule3D(final GCapsule3D capsule) {

      final IVector3 segmentClosestPoint = capsule._segment.closestPoint(_center);
      final double capsuleRadius = capsule._radius;

      return (Math.abs(_center.x() - segmentClosestPoint.x()) < (capsuleRadius + _extent.x()))
             && (Math.abs(_center.y() - segmentClosestPoint.y()) < (capsuleRadius + _extent.y()))
             && (Math.abs(_center.z() - segmentClosestPoint.z()) < (capsuleRadius + _extent.z()));

   }


   @Override
   public GAxisAlignedBox asAxisAlignedOrthotope() {
      return this;
   }


   @Override
   public GAxisAlignedBox[] subdividedByAxis(final byte axis) {
      switch (axis) {
         case 0:
            return subdividedByX();
         case 1:
            return subdividedByY();
         case 2:
            return subdividedByZ();
         default:
            throw new IllegalArgumentException("Invalid axis=" + axis);
      }
   }


   @Override
   public boolean touches(final GAxisAlignedBox that) {
      return touchesWithBox(that);
   }


   @Override
   public boolean touches(final GAxisAlignedOrthotope<IVector3, ?> that) {
      return touchesWithBox((GAxisAlignedBox) that);
   }


   @Override
   public GAxisAlignedBox mergedWith(final GAxisAlignedOrthotope<IVector3, ?> that) {
      return new GAxisAlignedBox(_lower.min(that._lower), _upper.max(that._upper));
   }


   @Override
   public GAxisAlignedBox clamp(final GAxisAlignedOrthotope<IVector3, ?> that) {
      return new GAxisAlignedBox(_lower.clamp(that._lower, that._upper), _upper.clamp(that._lower, that._upper));

   }


   @Override
   public double volume() {
      return _extent.x() * _extent.y() * _extent.z();
   }


   @Override
   public GAxisAlignedBox scale(final double scale) {
      return new GAxisAlignedBox(_lower.scale(scale), _upper.scale(scale));
   }


   @Override
   public GAxisAlignedBox scale(final IVector3 scale) {
      return new GAxisAlignedBox(_lower.scale(scale), _upper.scale(scale));
   }


}
