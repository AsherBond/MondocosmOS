

package es.igosoftware.euclid;

import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVectorFunction;


/**
 * In mathematics, a curve (sometimes also called curved line) is, generally speaking, an object similar to a line but which is
 * not required to be straight. This entails that a line is a special case of curve, namely a curve with null curvature. Often
 * curves in two-dimensional (plane curves) or three-dimensional (space curves) Euclidean space are of interest.<br/>
 * <br/>
 * See http://en.wikipedia.org/wiki/Curve
 * 
 * @author dgd
 * 
 * @param <VectorT>
 * @param <BoundsT>
 */
public interface ICurve<

VectorT extends IVector<VectorT, ?>,

BoundsT extends IBounds<VectorT, BoundsT>

>
         extends
            IBoundedGeometry<VectorT, BoundsT> {


   public ICurve<VectorT, BoundsT> transform(final IVectorFunction<VectorT> transformer);


}
