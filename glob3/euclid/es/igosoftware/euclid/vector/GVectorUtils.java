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


package es.igosoftware.euclid.vector;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import es.igosoftware.euclid.verticescontainer.IVertexContainer;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.IComparatorInt;


public final class GVectorUtils {

   public static int compare(final IVector2 p1,
                             final IVector2 p2) {
      final double x1 = p1.x();
      final double x2 = p2.x();

      if (x1 > x2) {
         return 1;
      }
      if (x1 < x2) {
         return -1;
      }

      return Double.compare(p1.y(), p2.y());
   }


   public static int compare(final IVector3 p1,
                             final IVector3 p2) {
      final double x1 = p1.x();
      final double x2 = p2.x();

      if (x1 > x2) {
         return 1;
      }
      if (x1 < x2) {
         return -1;
      }

      final double y1 = p1.y();
      final double y2 = p2.y();

      if (y1 > y2) {
         return 1;
      }
      if (y1 < y2) {
         return -1;
      }

      return Double.compare(p1.z(), p2.z());
   }


   public static IVector2 getAverage2(final Collection<IVector2> vectors) {
      if (vectors.isEmpty()) {
         return null;
      }

      return GVectorUtils.kahanSum2(vectors).div(vectors.size());
   }


   public static IVector3 getAverage3(final Collection<IVector3> vectors) {
      if (vectors.isEmpty()) {
         return null;
      }

      return GVectorUtils.kahanSum3(vectors).div(vectors.size());
   }


   public static IVector3 getAverage3(final IVector3... vectors) {
      if (vectors.length == 0) {
         return null;
      }

      return GVectorUtils.kahanSum(vectors).div(vectors.length);
   }


   public static IVector3 getAverage3(final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices,
                                      final int[] verticesIndexes) {
      final int verticesCount = verticesIndexes.length;
      if (verticesCount == 0) {
         return null;
      }

      return GVectorUtils.kahanSum(vertices, verticesIndexes).div(verticesCount);
   }


   private static <T extends IVector<T, ?>> double getAverageDimension(final byte dimension,
                                                                       final Iterable<T> vectors) {
      final Iterator<T> iterator = vectors.iterator();

      if (!iterator.hasNext()) {
         return Double.NaN;
      }

      int vectorsCount = 1;
      final T firstVector = iterator.next();
      if (firstVector.dimensions() < dimension - 1) {
         throw new IllegalArgumentException("Invalid dimension (" + dimension + ") for vectors with dimensions="
                                            + firstVector.dimensions());
      }

      double sum = firstVector.get(dimension);
      double compensation = 0; //A running compensation for lost low-order bits.

      while (iterator.hasNext()) {
         final double value = firstVector.get(dimension);

         final double y = value - compensation; //So far, so good: c is zero.
         final double t = sum + y; //Alas, sum is big, y small, so low-order digits of y are lost.
         compensation = (t + sum) - y; //(t - sum) recovers the high-order part of y; subtracting y recovers -(low part of y)
         sum = t; //Algebraically, c should always be zero. Beware eagerly optimising compilers!

         vectorsCount++;
      }

      return sum / vectorsCount;
   }


   private static <T extends IVector<T, ?>> double getAverageDimension(final byte dimension,
                                                                       final IVertexContainer<T, IVertexContainer.Vertex<T>, ?> vectors) {
      final Iterator<T> iterator = vectors.pointsIterator();

      if (!iterator.hasNext()) {
         return Double.NaN;
      }

      int vectorsCount = 1;
      final T firstVector = iterator.next();
      if (firstVector.dimensions() < dimension - 1) {
         throw new IllegalArgumentException("Invalid dimension (" + dimension + ") for vectors with dimensions="
                                            + firstVector.dimensions());
      }

      double sum = firstVector.get(dimension);
      double compensation = 0; //A running compensation for lost low-order bits.

      while (iterator.hasNext()) {
         final double value = firstVector.get(dimension);

         final double y = value - compensation; //So far, so good: c is zero.
         final double t = sum + y; //Alas, sum is big, y small, so low-order digits of y are lost.
         compensation = (t + sum) - y; //(t - sum) recovers the high-order part of y; subtracting y recovers -(low part of y)
         sum = t; //Algebraically, c should always be zero. Beware eagerly optimising compilers!

         vectorsCount++;
      }

      return sum / vectorsCount;
   }


   public static double getAverageX(final Iterable<IVector3> vectors) {
      return getAverageDimension((byte) 0, vectors);
   }


   public static double getAverageY(final Iterable<IVector3> vectors) {
      return getAverageDimension((byte) 1, vectors);
   }


   public static double getAverageZ(final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vectors) {
      return getAverageDimension((byte) 2, vectors);
   }


   public static double getAverageZ(final Iterable<IVector3> vectors) {
      return getAverageDimension((byte) 2, vectors);
   }


   public static double getMaxZ(final Iterable<IVector3> vectors) {
      double maxZ = Double.NEGATIVE_INFINITY;

      for (final IVector3 vector : vectors) {
         final double currentZ = vector.z();
         if (currentZ > maxZ) {
            maxZ = currentZ;
         }
      }

      return maxZ;
   }


   public static double getMaxZ(final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vectors) {
      double maxZ = Double.NEGATIVE_INFINITY;

      for (int i = 0; i < vectors.size(); i++) {
         final IVector3 vector = vectors.getPoint(i);
         final double currentZ = vector.z();
         if (currentZ > maxZ) {
            maxZ = currentZ;
         }
      }

      return maxZ;
   }


   public static double getSignedAngle(final IVector3 normal,
                                       final IVector3 v1,
                                       final IVector3 v2) {
      final IVector3 tempCross = v1.cross(v2);

      final double angle = Math.atan2(tempCross.length(), v1.dot(v2));

      return tempCross.dot(normal) < 0 ? -angle : angle;
   }


   public static IVector2 kahanSum2(final Iterable<IVector2> vectors) {
      return kahanSum(GVector2D.ZERO, vectors);
   }


   public static IVector3 kahanSum3(final Iterable<IVector3> vectors) {
      return kahanSum(GVector3D.ZERO, vectors);
   }


   public static IVector2 kahanSum(final IVector2... vectors) {
      return kahanSum(GVector2D.ZERO, vectors);
   }


   public static IVector3 kahanSum(final IVector3... vectors) {
      return kahanSum(GVector3D.ZERO, vectors);
   }


   public static IVector3 kahanSum(final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices,
                                   final int[] verticesIndexes) {
      return kahanSum(GVector3D.ZERO, vertices, verticesIndexes);
   }


   private static <T extends IVector<T, ?>> T kahanSum(final T zero,
                                                       final Iterable<T> vectors) {
      // from http://en.wikipedia.org/wiki/Kahan_summation_algorithm

      final Iterator<T> iterator = vectors.iterator();

      if (!iterator.hasNext()) {
         return zero;
      }

      T sum = iterator.next();
      T compensation = zero; //A running compensation for lost low-order bits.

      while (iterator.hasNext()) {
         final T vector = iterator.next();

         final T y = vector.sub(compensation); //So far, so good: c is zero.
         final T t = sum.add(y); //Alas, sum is big, y small, so low-order digits of y are lost.
         compensation = t.sub(sum).sub(y); //(t - sum) recovers the high-order part of y; subtracting y recovers -(low part of y)
         sum = t; //Algebraically, c should always be zero. Beware eagerly optimising compilers!
      }

      return sum;
   }


   private static <T extends IVector<T, ?>> T kahanSum(final T zero,
                                                       final IVertexContainer<T, IVertexContainer.Vertex<T>, ?> vertices,
                                                       final int[] verticesIndexes) {
      // from http://en.wikipedia.org/wiki/Kahan_summation_algorithm

      final int verticesCount = verticesIndexes.length;
      if (verticesCount == 0) {
         return zero;
      }

      T sum = vertices.getPoint(verticesIndexes[0]);
      T compensation = zero; //A running compensation for lost low-order bits.

      for (int i = 1; i < verticesCount; i++) {
         final T vector = vertices.getPoint(verticesIndexes[i]);

         final T y = vector.sub(compensation); //So far, so good: c is zero.
         final T t = sum.add(y); //Alas, sum is big, y small, so low-order digits of y are lost.
         compensation = t.sub(sum).sub(y); //(t - sum) recovers the high-order part of y; subtracting y recovers -(low part of y)
         sum = t; //Algebraically, c should always be zero. Beware eagerly optimising compilers!
      }

      return sum;
   }


   private static <T extends IVector<T, ?>> T kahanSum(final T zero,
                                                       final T... vectors) {
      // from http://en.wikipedia.org/wiki/Kahan_summation_algorithm

      if (vectors.length == 0) {
         return zero;
      }

      T sum = vectors[0];
      T compensation = zero; //A running compensation for lost low-order bits.

      for (int i = 1; i < vectors.length; i++) {
         final T vector = vectors[i];

         final T y = vector.sub(compensation); //So far, so good: c is zero.
         final T t = sum.add(y); //Alas, sum is big, y small, so low-order digits of y are lost.
         compensation = t.sub(sum).sub(y); //(t - sum) recovers the high-order part of y; subtracting y recovers -(low part of y)
         sum = t; //Algebraically, c should always be zero. Beware eagerly optimising compilers!
      }

      return sum;
   }


   public static void main(final String[] args) {

      final int samplesCount = 1000000;
      final List<IVector2> valuesF = new ArrayList<IVector2>(samplesCount);
      final List<IVector2> valuesD = new ArrayList<IVector2>(samplesCount);

      final Random random = new Random();
      for (int i = 0; i < samplesCount; i++) {
         final double value = random.nextDouble() * 50000;
         valuesD.add(new GVector2D(value, value));
         valuesF.add(new GVector2F((float) value, (float) value));
      }


      final IVector2 sumD = GVectorUtils.plainSum2(valuesD);
      final IVector2 kahanSumD = GVectorUtils.kahanSum2(valuesD);
      final IVector2 referenceSum = sumD;

      show(GVectorUtils.kahanSum2(valuesF), GVectorUtils.plainSum2(valuesF), referenceSum);

      System.out.println();

      show(kahanSumD, sumD, referenceSum);
   }


   public static IVector2 max(final IVector2... vectors) {
      if (vectors.length < 1) {
         throw new IllegalArgumentException("At least one vector is needed");
      }

      IVector2 result = vectors[0];
      for (int i = 1; i < vectors.length; i++) {
         result = result.max(vectors[i]);
      }

      return result;
   }


   public static IVector3 max(final IVector3... vectors) {
      if (vectors.length < 1) {
         throw new IllegalArgumentException("At least one vector is needed");
      }

      IVector3 result = vectors[0];
      for (int i = 1; i < vectors.length; i++) {
         result = result.max(vectors[i]);
      }

      return result;
   }


   public static IVector2 min(final IVector2... vectors) {
      if (vectors.length < 1) {
         throw new IllegalArgumentException("At least one vector is needed");
      }

      IVector2 result = vectors[0];
      for (int i = 1; i < vectors.length; i++) {
         result = result.min(vectors[i]);
      }

      return result;
   }


   public static IVector3 min(final IVector3... vectors) {
      if (vectors.length < 1) {
         throw new IllegalArgumentException("At least one vector is needed");
      }

      IVector3 result = vectors[0];
      for (int i = 1; i < vectors.length; i++) {
         result = result.min(vectors[i]);
      }

      return result;
   }


   public static IVector<?, ?> newMutableVector(final double[] coordinates) {
      final int dimensions = coordinates.length;
      if (dimensions == 2) {
         return new GMutableVector2<IVector2>(GVector2D.ZERO);
      }
      else if (dimensions == 3) {
         return new GMutableVector3<IVector3>(GVector3D.ZERO);
      }
      else {
         throw new IllegalArgumentException("Can't create a Vector of " + dimensions + " dimensions");
      }
   }


   public static IVector<?, ?> newVector(final double[] coordinates) {
      final int dimensions = coordinates.length;
      if (dimensions == 2) {
         return new GVector2D(coordinates[0], coordinates[1]);
      }
      else if (dimensions == 3) {
         return new GVector3D(coordinates[0], coordinates[1], coordinates[2]);
      }
      else {
         throw new IllegalArgumentException("Can't create a Vector of " + dimensions + " dimensions");
      }
   }


   public static IVector2 plainSum2(final Iterable<IVector2> vectors) {
      return plainSum(GVector2D.ZERO, vectors);
   }


   public static IVector3 plainSum3(final Iterable<IVector3> vectors) {
      return plainSum(GVector3D.ZERO, vectors);
   }


   private static <T extends IVector<T, ?>> T plainSum(final T zero,
                                                       final Iterable<T> vectors) {
      T sum = zero;
      for (final T vector : vectors) {
         sum = sum.add(vector);
      }

      //      final Iterator<T> iterator = vectors.iterator();
      //
      //      if (!iterator.hasNext()) {
      //         return zero;
      //      }
      //
      //      T sum = iterator.next();
      //
      //      while (iterator.hasNext()) {
      //         final T vector = iterator.next();
      //         sum = sum.add(vector);
      //      }

      return sum;
   }


   private static <T extends IVector2> void show(final T kahanSum,
                                                 final T sum,
                                                 final T referenceSum) {


      final IVector2 delta = kahanSum.sub(referenceSum);
      //final T percent = delta.scale(100).div(sum);
      final IVector2 percent = delta.scale(100).div(referenceSum);

      System.out.println("kahan sum=" + kahanSum);
      System.out.println("plain sum=" + sum);
      System.out.println("  difference=" + delta);
      System.out.println("  percent=" + percent);
   }


   public static void sortClockwise(final IVector3 center,
                                    final IVector3... vectors) {
      final IVector3 centerV = center;

      Arrays.sort(vectors, new Comparator<IVector3>() {
         @Override
         public int compare(final IVector3 p1,
                            final IVector3 p2) {
            final double angle = getSignedAngle(centerV, p1, p2);

            if (angle < 0) {
               return -1;
            }
            else if (angle > 0) {
               return 1;
            }
            return 0;
         }
      });
   }


   public static void sortClockwise(final IVector3 center,
                                    final List<IVector3> vectors) {
      final IVector3 centerV = center;

      Collections.sort(vectors, new Comparator<IVector3>() {
         @Override
         public int compare(final IVector3 p1,
                            final IVector3 p2) {
            final double angle = getSignedAngle(centerV, p1, p2);

            if (angle < 0) {
               return -1;
            }
            else if (angle > 0) {
               return 1;
            }
            return 0;
         }
      });
   }


   private GVectorUtils() {
   }


   public static IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> sortClockwise(final IVector3 center,
                                                                                                final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices) {
      final int[] sortedVerticesIndices = GCollections.rangeArray(0, vertices.size() - 1);
      GCollections.quickSort(sortedVerticesIndices, new IComparatorInt() {
         @Override
         public int compare(final int index1,
                            final int index2) {
            final IVector3 p1 = vertices.getPoint(index1);
            final IVector3 p2 = vertices.getPoint(index2);
            final double angle = getSignedAngle(center, p1, p2);

            if (angle < 0) {
               return -1;
            }
            else if (angle > 0) {
               return 1;
            }
            return 0;
         }
      });

      return vertices.asSubContainer(sortedVerticesIndices);
   }


   public static IVector<?, ?> createD(final double[] dimensionsValues) {
      final int dimensions = dimensionsValues.length;

      if (dimensions == 2) {
         return new GVector2D(dimensionsValues[0], dimensionsValues[1]);
      }
      else if (dimensions == 3) {
         return new GVector3D(dimensionsValues[0], dimensionsValues[1], dimensionsValues[2]);
      }
      else {
         throw new IllegalArgumentException("dimensions " + dimensions + " not yet supported");
      }
   }


   public static IVector<?, ?> createF(final double[] dimensionsValues) {
      final int dimensions = dimensionsValues.length;

      if (dimensions == 2) {
         return new GVector2F((float) dimensionsValues[0], (float) dimensionsValues[1]);
      }
      else if (dimensions == 3) {
         return new GVector3F((float) dimensionsValues[0], (float) dimensionsValues[1], (float) dimensionsValues[2]);
      }
      else {
         throw new IllegalArgumentException("dimensions " + dimensions + " not yet supported");
      }
   }


   public static IVector<?, ?> createF(final float[] dimensionsValues) {
      final int dimensions = dimensionsValues.length;

      if (dimensions == 2) {
         return new GVector2F(dimensionsValues[0], dimensionsValues[1]);
      }
      else if (dimensions == 3) {
         return new GVector3F(dimensionsValues[0], dimensionsValues[1], dimensionsValues[2]);
      }
      else {
         throw new IllegalArgumentException("dimensions " + dimensions + " not yet supported");
      }
   }


   public static <VectorT extends IVector<VectorT, ?>> VectorT getAverage(final Iterable<VectorT> vectors) {
      final Iterator<VectorT> iterator = vectors.iterator();

      if (!iterator.hasNext()) {
         return null;
      }

      int count = 1;
      VectorT sum = iterator.next().asDouble(); // as double to give accurate averages
      while (iterator.hasNext()) {
         count++;
         sum = sum.add(iterator.next());
      }

      return sum.div(count);
   }


   public static <VectorT extends IVector<VectorT, ?>> VectorT getAverage(final VectorT... vectors) {
      final int count = vectors.length;
      if (count == 0) {
         return null;
      }

      VectorT sum = vectors[0].asDouble(); // as double to give accurate averages
      for (int i = 1; i < count; i++) {
         sum = sum.add(vectors[i]);
      }

      return sum.div(count);
   }


}
