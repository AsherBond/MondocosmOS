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

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.colors.GColorF;
import es.igosoftware.euclid.colors.GColorPrecision;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.mutability.IMutable;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.GVectorPrecision;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.IFunction;
import es.igosoftware.util.IPredicate;
import es.igosoftware.util.IRangeEvaluator;


public interface IVertexContainer<

VectorT extends IVector<VectorT, ?>,

VertexT extends IVertexContainer.Vertex<VectorT>,

MutableT extends IMutable<MutableT>

>
         extends
            IMutable<MutableT> {


   /**
    * A holder for all the data that compose a vertex
    * 
    * @param <VectorT>
    */
   public static class Vertex<VectorT extends IVector<VectorT, ?>> {
      public final VectorT _point;
      public final float   _intensity;
      public final VectorT _normal;
      public final IColor  _color;
      public final long    _userData;


      public Vertex(final VectorT point,
                    final float intensity,
                    final VectorT normal,
                    final IColor color,
                    final long userData) {
         _point = point;
         _intensity = intensity;
         _normal = normal;
         _color = color;
         _userData = userData;
      }


      @Override
      public String toString() {
         return "Vertex [point=" + _point + ", intensity=" + _intensity + ", normal=" + _normal + ", color=" + _color
                + ", userData=" + _userData + "]";
      }

   }


   ///////////////////////////////////////////////////////////////////////////////
   public static interface VertexEvaluator<

   VectorT extends IVector<VectorT, ?>,

   VertexT extends IVertexContainer.Vertex<VectorT>

   > {
      public void evaluate(final int index,
                           final VertexT vertex);
   }


   ///////////////////////////////////////////////////////////////////////////////
   public static class WeightedVertex<VectorT extends IVector<VectorT, ?>>
            extends
               Vertex<VectorT> {
      private static IColor asColor(final IVector3 vector) {
         return GColorF.newRGB((float) vector.x(), (float) vector.y(), (float) vector.z());
      }


      private static IVector3 asVector(final IColor color) {
         return new GVector3D(color.getRed(), color.getGreen(), color.getBlue());
      }


      public static <VectorT extends IVector<VectorT, ?>> WeightedVertex<VectorT> getAverage(final WeightedVertex<VectorT>... values) {
         if ((values == null) || (values.length == 0)) {
            return null;
         }

         //         final Iterator<WeightedVertex<VectorT>> iterator = values.iterator();

         final WeightedVertex<VectorT> first = values[0];
         final int firstWeight = first._weight;
         VectorT pointSum = first._point.scale(firstWeight);
         VectorT normalSum = first._normal == null ? null : first._normal.scale(firstWeight);
         float intensitySum = first._intensity * firstWeight;
         IVector3 colorSum = (first._color == null) ? null : asVector(first._color).scale(firstWeight); // use a vector to sum the colors, to avoid the clamp to [0..1] while adding
         long userDataSum = first._userData * firstWeight;
         int weightSum = firstWeight;

         //while (iterator.hasNext()) {
         for (int i = 1; i < values.length; i++) {
            final WeightedVertex<VectorT> current = values[i];
            final int currentWeight = current._weight;

            pointSum = pointSum.add(current._point.scale(currentWeight));

            if (normalSum != null) {
               normalSum = normalSum.add(current._normal.scale(currentWeight));
            }

            intensitySum += current._intensity * currentWeight;

            userDataSum += current._userData * currentWeight;

            if (colorSum != null) {
               colorSum = colorSum.add(asVector(current._color).scale(currentWeight));
            }

            weightSum += currentWeight;
         }

         final VectorT point = pointSum.div(weightSum);
         final float intensity = intensitySum / weightSum;
         final long userData = userDataSum / weightSum;

         final VectorT normal = (normalSum == null) ? null : normalSum.div(weightSum).normalized();
         final IColor color = (colorSum == null) ? null : asColor(colorSum.div(weightSum));

         return new WeightedVertex<VectorT>(point, intensity, normal, color, userData, weightSum);
      }


      public static <VectorT extends IVector<VectorT, ?>> WeightedVertex<VectorT> getAverage(final Collection<WeightedVertex<VectorT>> values) {
         if ((values == null) || values.isEmpty()) {
            return null;
         }

         final Iterator<WeightedVertex<VectorT>> iterator = values.iterator();

         final WeightedVertex<VectorT> first = iterator.next();
         final int firstWeight = first._weight;
         VectorT pointSum = first._point.scale(firstWeight);
         VectorT normalSum = first._normal == null ? null : first._normal.scale(firstWeight);
         float intensitySum = first._intensity * firstWeight;
         long userDataSum = first._userData * firstWeight;
         IVector3 colorSum = (first._color == null) ? null : asVector(first._color).scale(firstWeight); // use a vector to sum the colors, to avoid the clamp to [0..1] while adding
         int weightSum = firstWeight;

         while (iterator.hasNext()) {
            final WeightedVertex<VectorT> current = iterator.next();
            final int currentWeight = current._weight;

            pointSum = pointSum.add(current._point.scale(currentWeight));

            if (normalSum != null) {
               normalSum = normalSum.add(current._normal.scale(currentWeight));
            }

            intensitySum += current._intensity * currentWeight;

            userDataSum += current._userData * currentWeight;

            if (colorSum != null) {
               colorSum = colorSum.add(asVector(current._color).scale(currentWeight));
            }

            weightSum += currentWeight;
         }

         final VectorT point = pointSum.div(weightSum);
         final float intensity = intensitySum / weightSum;
         final long userData = userDataSum / weightSum;

         final VectorT normal = (normalSum == null) ? null : normalSum.div(weightSum).normalized();
         final IColor color = (colorSum == null) ? null : asColor(colorSum.div(weightSum));

         return new WeightedVertex<VectorT>(point, intensity, normal, color, userData, weightSum);
      }


      public final int _weight;


      public WeightedVertex(final VectorT point,
                            final float intensity,
                            final VectorT normal,
                            final IColor color,
                            final long userData,
                            final int weight) {
         super(point, intensity, normal, color, userData);
         _weight = weight;
      }
   }


   public int addPoint(final VertexT vertex);


   public int addPoint(final VectorT point);


   public int addPoint(final VectorT point,
                       final float intensity);


   public int addPoint(final VectorT point,
                       final float intensity,
                       final IColor color);


   public int addPoint(final VectorT point,
                       final float intensity,
                       final VectorT normal,
                       final IColor color);


   public int addPoint(final VectorT point,
                       final IColor color);


   public int addPoint(final VectorT point,
                       final VectorT normal);


   public int addPoint(final VectorT point,
                       final float intensity,
                       final VectorT normal);


   public int addPoint(final VectorT point,
                       final VectorT normal,
                       final IColor color);


   public int addPoint(final VectorT point,
                       final long userData);


   public int addPoint(final VectorT point,
                       final IColor color,
                       final long userData);


   public int addPoint(final VectorT point,
                       final float intensity,
                       final long userData);


   public int addPoint(final VectorT point,
                       final VectorT normal,
                       final long userData);


   public int addPoint(final VectorT point,
                       final float intensity,
                       final VectorT normal,
                       final IColor color,
                       final long userData);


   public GColorPrecision colorPrecision();


   public GProjection projection();


   //public IVertexContainer<VectorT, VertexT, ?> composedWith(final IVertexContainer<VectorT, VertexT, ?> container);


   //public IVertexContainer<VectorT, ?, ?> composedWith(final IVertexContainer<VectorT, ?, ?> container);


   public byte dimensions();


   public void ensureCapacity(final int minCapacity);


   public void evaluate(final IVertexContainer.VertexEvaluator<VectorT, VertexT> evaluator);


   public void concurrentEvaluate(final IVertexContainer.VertexEvaluator<VectorT, VertexT> evaluator);


   public void concurrentEvaluate(final IVertexContainer.VertexEvaluator<VectorT, VertexT> evaluator,
                                  final int numberThreads);


   public void concurrentEvaluate(final IVertexContainer.VertexEvaluator<VectorT, VertexT> evaluator,
                                  final int numberThreads,
                                  final int threadPriority,
                                  final boolean waitFinalization);


   public void concurrentEvaluate(final IRangeEvaluator rangeEvaluator,
                                  final int numberThreads,
                                  final int threadPriority,
                                  final boolean waitFinalization);


   public IVertexContainer.WeightedVertex<VectorT> getAverage();


   public GAxisAlignedOrthotope<VectorT, ?> getBounds();


   public IColor getColor(final int index);


   public float getIntensity(final int index);


   public long getUserData(final int index);


   public double getUserDataAsDouble(final int index);


   public VectorT getNormal(final int index);


   public VectorT getPoint(final int index);


   public VertexT getVertex(final int i);


   public void setColor(final int index,
                        final IColor color);


   public void setIntensity(final int index,
                            final float intensity);


   public void setUserData(final int index,
                           final long userData);


   public void setUserDataFromDouble(final int index,
                                     final double userData);


   public void setNormal(final int index,
                         final VectorT normal);


   public void setPoint(final int index,
                        final VectorT point);


   public void setVertex(final int index,
                         final VertexT vertex);


   public void setPoint(final int index,
                        final VectorT point,
                        final float intensity,
                        final VectorT normal,
                        final IColor color,
                        final long userData);


   //   public void setProjection(GProjection projection);


   public boolean hasColors();


   public boolean hasIntensities();


   public boolean hasNormals();


   public boolean hasUserData();


   public Iterator<VectorT> pointsIterator();


   public Iterable<VectorT> pointsIterable();


   public Iterator<VertexT> vertexIterator();


   public Iterable<VertexT> vertexIterable();


   /**
    * Returns if the receiver has the same contents than the given VertexContainer
    * 
    * @param that
    *           the comparand VertexContainer
    * @return
    */
   public boolean sameContents(final IVertexContainer<VectorT, VertexT, ?> that);


   /**
    * Returns if the receiver has the same precision (both vector and color) than the given VertexContainer
    * 
    * @param that
    *           the comparand VertexContainer
    * @return boolean
    */
   public boolean samePrecision(final IVertexContainer<?, ?, ?> that);


   /**
    * Returns if the receiver has the same shape than the the given VertexContainer
    * 
    * Same shape means: same dimensions(), same hasIntensities(), same hasColors() and same hasNormals()
    * 
    * @param that
    *           the comparand VertexContainer
    * @return boolean
    */
   public boolean sameShapeThan(final IVertexContainer<?, ?, ?> that);


   /**
    * Creates an empty container with the same layout than the receiver
    * 
    * @param initialCapacity
    * @return
    */
   public MutableT newEmptyContainer(final int initialCapacity);


   public MutableT newEmptyContainer(final int initialCapacity,
                                     final GProjection projection);


   public int size();


   public void trimToSize();


   public GVectorPrecision vectorPrecision();


   //   public IVertexContainer<VectorT, VertexT, ?> reproject(final GProjection sourceProjection,
   //            final GProjection targetProjection);
   //   public MutableT reproject(final GProjection sourceProjection,
   //                             final GProjection targetProjection);
   public MutableT reproject(final GProjection targetProjection);


   /**
    * Creates a new container with the same, but sorted vertices
    * 
    * @param comparator
    * @return
    */
   public MutableT asSortedContainer(final Comparator<VertexT> comparator);


   /**
    * Creates a new subContainer with the same, but sorted vertices
    * 
    * @param comparator
    * @return
    */
   public IVertexContainer<VectorT, VertexT, ?> asSortedSubContainer(final Comparator<VertexT> comparator);


   public IVertexContainer<VectorT, VertexT, ?> asSubContainer(final int[] subIndices);


   /**
    * 
    * Return a mutable copy of the container
    * 
    * @param vertices
    * @return
    */
   public MutableT asMutableCopy();


   public MutableT select(final IPredicate<VertexT> predicate);


   public MutableT collect(final IFunction<VectorT, VectorT> referencePointPredicate,
                           final IFunction<VertexT, VertexT> vertexPredicate);


   //public MutableT collect(final ITransformer<VertexT, VertexT> predicate);


}
