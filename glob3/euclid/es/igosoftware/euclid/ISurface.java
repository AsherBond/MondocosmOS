

package es.igosoftware.euclid;

import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVectorFunction;


/**
 * In mathematics, specifically in topology, a surface is a two-dimensional topological manifold. The most familiar examples are
 * those that arise as the boundaries of solid objects in ordinary three-dimensional Euclidean space R3 — for example, the surface
 * of a ball. On the other hand, there are surfaces, such as the Klein bottle, that cannot be embedded in three-dimensional
 * Euclidean space without introducing singularities or self-intersections.<br/>
 * <br/>
 * See http://en.wikipedia.org/wiki/Surface
 * 
 * @author dgd
 * 
 * @param <VectorT>
 * @param <BoundsT>
 */
public interface ISurface<

VectorT extends IVector<VectorT, ?>,

BoundsT extends IBounds<VectorT, BoundsT>

>
         extends
            IBoundedGeometry<VectorT, BoundsT> {


   public ISurface<VectorT, BoundsT> transform(final IVectorFunction<VectorT> transformer);


}
