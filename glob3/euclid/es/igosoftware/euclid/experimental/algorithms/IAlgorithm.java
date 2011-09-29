

package es.igosoftware.euclid.experimental.algorithms;

import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.IFunction;


public interface IAlgorithm<

ParametersVectorT extends IVector<ParametersVectorT, ?>,

ParametersT extends IAlgorithmParameters<ParametersVectorT>,

ResultVectorT extends IVector<ResultVectorT, ?>,

ResultT extends IAlgorithmResult<ResultVectorT>

>
         extends
            IFunction<ParametersT, ResultT> {


   public String getName();


   public String getDescription();


}
