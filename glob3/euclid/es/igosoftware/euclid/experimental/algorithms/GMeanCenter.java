

package es.igosoftware.euclid.experimental.algorithms;

import java.util.Iterator;

import es.igosoftware.euclid.IGeometry;
import es.igosoftware.euclid.bounding.GNBall;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GPair;
import es.igosoftware.util.IFunction;


public class GMeanCenter<

ValueT,

VectorT extends IVector<VectorT, ?>

>
         implements
            IAlgorithm<

            VectorT, GMeanCenter.Parameters<ValueT, VectorT>,

            VectorT, GMeanCenter.Result<VectorT>

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
      private final GNBall<VectorT, ?> _center;


      private Result(final GNBall<VectorT, ?> circle) {
         this._center = circle;
      }


      public GNBall<VectorT, ?> getCenter() {
         return _center;
      }
   }


   @Override
   public Result<VectorT> apply(final Parameters<ValueT, VectorT> parameters) {
      double dSumWeight = 0;
      double dDifDist = 0;

      Iterator<ValueT> iter = parameters._iterable.iterator();

      VectorT sum = null;
      while (iter.hasNext()) {
         final ValueT feature = iter.next();

         final GPair<IGeometry<VectorT>, Double> geom = parameters._function.apply(feature);
         final VectorT coord = geom._first.getCentroid();

         dSumWeight += geom._second;
         if (sum == null) {
            sum = coord.sub(coord);
         }
         sum = sum.add(coord.scale(geom._second));
      }

      if (sum != null) {
         final VectorT center = sum.div(dSumWeight);

         dSumWeight = 0;

         iter = parameters._iterable.iterator();
         while (iter.hasNext()) {
            final ValueT feature = iter.next();
            final GPair<IGeometry<VectorT>, Double> geom = parameters._function.apply(feature);
            final VectorT coord = geom._first.getCentroid();

            dSumWeight += geom._second;
            final IVector diff = coord.sub(center);
            dDifDist += geom._second * Math.pow(diff.length(), 2);
         }

         final double stdDeviation = Math.sqrt(dDifDist / dSumWeight);

         final GNBall<VectorT, ?> circle = GNBall.create(center, stdDeviation);
         return new Result<VectorT>(circle);
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


}
