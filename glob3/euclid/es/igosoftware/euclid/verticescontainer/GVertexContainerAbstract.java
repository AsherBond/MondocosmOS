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


package es.igosoftware.euclid.verticescontainer;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;

import es.igosoftware.concurrent.GConcurrent;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.colors.GColorF;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.mutability.GMutableAbstract;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.GMath;
import es.igosoftware.util.IRangeEvaluator;


public abstract class GVertexContainerAbstract<

VectorT extends IVector<VectorT, ?>,

VertexT extends IVertexContainer.Vertex<VectorT>,

MutableT extends GVertexContainerAbstract<VectorT, VertexT, MutableT>

>
         extends
            GMutableAbstract<MutableT>
         implements
            IVertexContainer<VectorT, VertexT, MutableT> {


   //   @Override
   //   public IVertexContainer<VectorT, VertexT, ?> composedWith(final IVertexContainer<VectorT, VertexT, ?> container) {
   //      final GCompositeVertexContainer<VectorT, VertexT> composite = new GCompositeVertexContainer<VectorT, VertexT>();
   //      composite.addChild(this);
   //      composite.addChild(container);
   //      return composite;
   //   }

   //   @Override
   //   public IVertexContainer<VectorT, VertexT, ?> composedWith(final IVertexContainer<VectorT, VertexT, ?> container);


   //   @Override
   //   public final GSubVertexContainer<VectorT, VertexT> asSubContainer(final int[] subIndices) {
   //      return new GSubVertexContainer<VectorT, VertexT>(this, subIndices);
   //   }

   @Override
   public final Iterator<VectorT> pointsIterator() {
      return new GPointsIterator<VectorT>(this);
   }


   @Override
   public final Iterable<VectorT> pointsIterable() {
      return new Iterable<VectorT>() {
         @Override
         public Iterator<VectorT> iterator() {
            return new GPointsIterator<VectorT>(GVertexContainerAbstract.this);
         }
      };
   }


   @Override
   public final Iterator<VertexT> vertexIterator() {
      return new GVertexIterator<VectorT, VertexT>(this);
   }


   @Override
   public final Iterable<VertexT> vertexIterable() {
      return new Iterable<VertexT>() {
         @Override
         public Iterator<VertexT> iterator() {
            return new GVertexIterator<VectorT, VertexT>(GVertexContainerAbstract.this);
         }
      };
   }


   @Override
   public final void evaluate(final IVertexContainer.VertexEvaluator<VectorT, VertexT> evaluator) {
      final int size = size();
      for (int i = 0; i < size; i++) {
         evaluator.evaluate(i, getVertex(i));
      }
   }


   @Override
   public void concurrentEvaluate(final IVertexContainer.VertexEvaluator<VectorT, VertexT> evaluator) {
      concurrentEvaluate(evaluator, GConcurrent.AVAILABLE_PROCESSORS * 3);
   }


   @Override
   public void concurrentEvaluate(final IVertexContainer.VertexEvaluator<VectorT, VertexT> evaluator,
                                  final int numberThreads) {
      concurrentEvaluate(new IRangeEvaluator() {
         @Override
         public void evaluate(final int from,
                              final int to) {
            // System.out.println("evaluating from " + from + " to " + to);
            for (int i = from; i <= to; i++) {
               evaluator.evaluate(i, getVertex(i));
            }
         }
      }, numberThreads, GConcurrent.DEFAULT_THREAD_PRIORITY, true);
   }


   @Override
   public void concurrentEvaluate(final IVertexContainer.VertexEvaluator<VectorT, VertexT> evaluator,
                                  final int numberThreads,
                                  final int threadPriority,
                                  final boolean waitFinalization) {

      concurrentEvaluate(new IRangeEvaluator() {
         @Override
         public void evaluate(final int from,
                              final int to) {
            // System.out.println("evaluating from " + from + " to " + to);
            for (int i = from; i <= to; i++) {
               evaluator.evaluate(i, getVertex(i));
            }
         }
      }, numberThreads, threadPriority, waitFinalization);

   }


   @Override
   public void concurrentEvaluate(final IRangeEvaluator rangeEvaluator,
                                  final int numberThreads,
                                  final int threadPriority,
                                  final boolean waitFinalization) {

      if (this == null) {
         return;
      }

      final ExecutorService executor = GConcurrent.createExecutor(numberThreads + 1, threadPriority);

      final int size = size();

      final int step = Math.max(Math.round((float) size / numberThreads), 1);

      int from = 0;
      while (from < size) {
         final int to = Math.min(from + step - 1, size - 1);
         final int finalFrom = from;
         executor.execute(new Runnable() {
            @Override
            public void run() {
               rangeEvaluator.evaluate(finalFrom, to);
            }
         });

         from += step;
      }

      executor.shutdown();
      if (waitFinalization) {
         GConcurrent.awaitTermination(executor);
      }

   }


   @Override
   public String toString() {
      return getStringName() + " size=" + size() + ", intensities=" + hasIntensities() + ", colors=" + hasColors() + ", normals="
             + hasNormals() + ", userData=" + hasUserData() + getStringPostfix();
   }


   protected abstract String getStringName();


   protected abstract String getStringPostfix();


   @Override
   public GAxisAlignedOrthotope<VectorT, ?> getBounds() {
      return GAxisAlignedOrthotope.minimumOrthotope(pointsIterable());
   }


   @Override
   public IVertexContainer.WeightedVertex<VectorT> getAverage() {
      final int size = size();
      if (size == 0) {
         return null;
      }


      VectorT pointSum = getPoint(0).asDouble();

      final VectorT firstNormal = getNormal(0);
      VectorT normalSum = (firstNormal == null) ? null : firstNormal.asDouble();

      double intensitySum = getIntensity(0);

      long userDataSum = getUserData(0);

      final IColor firstColor = getColor(0);
      IVector3 colorSum = (firstColor == null) ? null : asVector(firstColor); // use a vector to sum the colors, to avoid the clamp to [0..1] while adding


      for (int i = 1; i < size; i++) {
         pointSum = pointSum.add(getPoint(i));

         if (normalSum != null) {
            normalSum = normalSum.add(getNormal(i));
         }

         intensitySum += getIntensity(i);

         userDataSum += getUserData(i);

         if (colorSum != null) {
            colorSum = colorSum.add(asVector(getColor(i)));
         }
      }

      final VectorT point = pointSum.div(size);
      final float intensity = (float) (intensitySum / size);
      final long userData = (userDataSum / size);

      final VectorT normal = (normalSum == null) ? null : normalSum.div(size).normalized();
      final IColor color = (colorSum == null) ? null : asColor(colorSum.div(size));

      return new WeightedVertex<VectorT>(point, intensity, normal, color, userData, size);
   }


   protected static GVector3D asVector(final IColor color) {
      return new GVector3D(color.getRed(), color.getGreen(), color.getBlue());
   }


   protected static IColor asColor(final IVector3 iVector3) {
      return GColorF.newRGB((float) iVector3.x(), (float) iVector3.y(), (float) iVector3.z());
   }


   //      @Override
   //      public final IVertexContainer<VectorT, VertexT, ?> select(final IPredicate<VertexT> predicate) {
   //         //final List<Integer> indices = GCollections.rangeList(0, size() - 1);
   //         final int size = size();
   //         final ArrayList<Integer> selectedIndices = new ArrayList<Integer>(size);
   //   
   //         for (int i = 0; i < size; i++) {
   //            final VertexT vertex = getVertex(i);
   //            if (predicate.evaluate(vertex)) {
   //               selectedIndices.add(i);
   //            }
   //         }
   //   
   //         if (selectedIndices.size() == size) {
   //            return this;
   //         }
   //   
   //         return new GSubVertexContainer<VectorT>(this, selectedIndices);
   //      }


   //      @Override
   //      public final IVertexContainer<VectorT, VertexT, ?> collect(final ITransformer<VertexT, VertexT> transformer,
   //                                                                 final VectorT referencePoint) {
   //         final IVertexContainer<VectorT, VertexT, ?> result = newEmptyContainer(size(), referencePoint);
   //   
   //         collectInto(result, transformer);
   //   
   //         return result;
   //      }


   //   protected final void collectInto(final IVertexContainer<VectorT, VertexT, ?> result,
   //                                    final ITransformer<VertexT, VertexT> transformer) {
   //      final int size = size();
   //
   //      for (int i = 0; i < size; i++) {
   //         final VertexT vertex = getVertex(i);
   //         final VertexT collectedVertex = transformer.transform(vertex);
   //         result.addPoint(collectedVertex);
   //      }
   //   }


   //   public IVertexContainer<VectorT, VertexT, ?> reproject(final GProjection sourceProjection,
   //                                                          final GProjection targetProjection) {
   //      if (sourceProjection == targetProjection) {
   //         return this;
   //      }
   //
   //      if (hasNormals()) {
   //         throw new IllegalArgumentException("Reprojection of Vertices Containers with normals is not supported");
   //      }
   //
   //      final VectorT reprojectedReferencePoint = getReferencePoint().reproject(sourceProjection, targetProjection);
   //
   //      return collect(new ITransformer<VertexT, VertexT>() {
   //         @Override
   //         public VertexT transform(final VertexT vertex) {
   //            //            final VectorT projectedPoint = vertex._point.reproject(sourceProjection, targetProjection);
   //            //            return new IVertexContainer.Vertex<VectorT>(projectedPoint, vertex._intensity, vertex._normal, vertex._color,
   //            //                     vertex._userData);
   //            return reprojectVertex(vertex);
   //         }
   //      }, reprojectedReferencePoint);
   //   }


   //public abstract VertexT reprojectVertex(final VertexT vertex);


   @Override
   public abstract boolean equals(final Object object);


   @Override
   public abstract int hashCode();


   //   @Override
   //   public IVertexContainer<VectorT, VertexT, ?> asSortedContainer(final Comparator<VertexT> comparator) {
   //
   //      final int[] indices = GCollections.rangeArray(0, size() - 1);
   //
   //      GCollections.quickSort(indices, new IComparatorInt() {
   //         @Override
   //         public int compare(final int index1,
   //                            final int index2) {
   //            return comparator.compare(getVertex(index1), getVertex(index2));
   //         }
   //      });
   //
   //      final IVertexContainer<VectorT, VertexT, ?> result = newEmptyContainer(size(), getReferencePoint());
   //
   //      for (final int index : indices) {
   //         result.addPoint(getVertex(index));
   //      }
   //
   //      return result;
   //   }


   //   @Override
   //   public GSubVertexContainer<VectorT> asSortedSubContainer(final Comparator<VertexT> comparator) {
   //
   //      final int[] indices = GCollections.rangeArray(0, size() - 1);
   //
   //      GCollections.quickSort(indices, new IComparatorInt() {
   //         @Override
   //         public int compare(final int index1,
   //                            final int index2) {
   //            return comparator.compare(getVertex(index1), getVertex(index2));
   //         }
   //      });
   //
   //      return asSubContainer(indices);
   //   }


   protected boolean basicShapeCheck(final IVertexContainer<?, ?, ?> that) {

      //-- Basic check

      if (this == that) {
         return true;
      }

      if (that == null) {
         return false;
      }

      if (getClass() != that.getClass()) {
         return false;
      }

      //--Basic check form common data

      if (dimensions() != that.dimensions()) {
         return false;
      }

      if (hasIntensities() != that.hasIntensities()) {
         return false;
      }

      if (hasColors() != that.hasColors()) {
         return false;
      }

      if (hasUserData() != that.hasUserData()) {
         return false;
      }

      return hasNormals() == that.hasNormals();
   }


   protected boolean basicPrecisionCheck(final IVertexContainer<?, ?, ?> that) {

      if (vectorPrecision() != that.vectorPrecision()) {
         return false;
      }

      return colorPrecision() == that.colorPrecision();
   }


   protected boolean basicContentsCheck(final IVertexContainer<VectorT, VertexT, ?> that) {

      if (!sameShapeThan(that)) {
         return false;
      }

      final int size = size();

      if (size != that.size()) {
         return false;
      }

      for (int i = 0; i < size; i++) {
         if (!getPoint(i).closeTo(that.getPoint(i))) {
            return false;
         }

         if (hasIntensities()) {
            if (!GMath.closeTo(getIntensity(i), that.getIntensity(i))) {
               return false;
            }
         }

         if (hasColors()) {
            if (!getColor(i).closeTo(that.getColor(i))) {
               return false;
            }
         }

         if (hasNormals()) {
            if (!getNormal(i).closeTo(that.getNormal(i))) {
               return false;
            }
         }

         if (hasUserData()) {
            if (!GMath.closeTo(getUserData(i), that.getUserData(i))) {
               return false;
            }
         }
      }

      return true;
   }


}
