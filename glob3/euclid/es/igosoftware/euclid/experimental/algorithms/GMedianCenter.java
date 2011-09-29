

package es.igosoftware.euclid.experimental.algorithms;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import es.igosoftware.euclid.IGeometry;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GPair;
import es.igosoftware.util.IFunction;


public class GMedianCenter<

ValueT,

VectorT extends IVector<VectorT, ?>

>
         implements
            IAlgorithm<

            VectorT, GMedianCenter.Parameters<ValueT, VectorT>,

            VectorT, GMedianCenter.Result<VectorT>

            > {

   public static class Parameters<ValueT, VectorT extends IVector<VectorT, ?>>
            implements
               IAlgorithmParameters<VectorT> {
      private final Iterable<ValueT>                                     _iterable;
      private final IFunction<ValueT, GPair<IGeometry<VectorT>, Double>> _function;


      public Parameters(final Iterable<ValueT> iterable,
                        final IFunction<ValueT, GPair<IGeometry<VectorT>, Double>> function) {
         this._iterable = iterable;
         this._function = function;
      }
   }
   public static class Result<VectorT extends IVector<VectorT, ?>>
            implements
               IAlgorithmResult<VectorT> {
      private final VectorT _center;


      private Result(final VectorT center) {
         this._center = center;
      }


      public VectorT getCenter() {
         return _center;
      }
   }


   @SuppressWarnings("unchecked")
   @Override
   public Result<VectorT> apply(final Parameters<ValueT, VectorT> parameters) {

      final Iterator<ValueT> iter = parameters._iterable.iterator();

      TreeSet<GPair<Double, Integer>>[] coordSets = null;
      int numElements = 0;
      while (iter.hasNext()) {
         final ValueT feature = iter.next();

         final GPair<IGeometry<VectorT>, Double> geom = parameters._function.apply(feature);
         final VectorT coord = geom._first.getCentroid();

         if (coordSets == null) {
            coordSets = new TreeSet[coord.dimensions()];
            for (int i = 0; i < coord.dimensions(); i++) {
               coordSets[i] = new TreeSet<GPair<Double, Integer>>(new OrdinateComparator());
            }
         }

         for (int times = 0; times < geom._second; times++) {
            for (int i = 0; i < coord.dimensions(); i++) {
               coordSets[i].add(new GPair(coord.get((byte) i), numElements));
            }
            numElements++;
         }
      }

      if (coordSets != null) {
         final boolean even = numElements / 2 == numElements / 2.0;
         int medianPosition = numElements / 2;
         if (even) {
            medianPosition = medianPosition - 1;
         }

         final Iterator<GPair<Double, Integer>>[] iterators = new Iterator[coordSets.length];
         for (int i = 0; i < iterators.length; i++) {
            iterators[i] = coordSets[i].iterator();
         }
         for (int i = 0; i < medianPosition; i++) {
            for (final Iterator<GPair<Double, Integer>> iterator : iterators) {
               iterator.next();
            }
         }

         VectorT ret = null;
         if (iterators.length == 2) {
            Double x = iterators[0].next().getFirst();
            Double y = iterators[1].next().getFirst();
            if (even) {
               x = (x + iterators[0].next().getFirst()) / 2;
               y = (y + iterators[1].next().getFirst()) / 2;
            }
            ret = (VectorT) new GVector2D(x, y);
         }
         else if (iterators.length == 3) {
            Double x = iterators[0].next().getFirst();
            Double y = iterators[1].next().getFirst();
            Double z = iterators[2].next().getFirst();
            if (even) {
               x = (x + iterators[0].next().getFirst()) / 2;
               y = (y + iterators[1].next().getFirst()) / 2;
               z = (z + iterators[2].next().getFirst()) / 2;
            }
            ret = (VectorT) new GVector3D(x, y, z);
         }
         else {
            throw new UnsupportedOperationException("Only 2d and 3d supported");
         }
         return new Result<VectorT>(ret);

      }
      return null;
   }


   @Override
   public String getName() {
      return "Mean center";
   }


   @Override
   public String getDescription() {
      // TODO Auto-generated method stub
      return null;
   }


   private class OrdinateComparator
            implements
               Comparator<GPair<Double, Integer>> {

      @Override
      public int compare(final GPair<Double, Integer> o1,
                         final GPair<Double, Integer> o2) {
         final Double ordinate1 = o1.getFirst();
         final Double ordinate2 = o2.getFirst();
         final int doubleComparison = ordinate1.compareTo(ordinate2);
         if (doubleComparison == 0) {
            return o1.getSecond() - o2.getSecond();
         }
         return doubleComparison;
      }

   }

}
