

package es.igosoftware.euclid.ntree;

import java.util.Collection;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.IBoundedGeometry3D;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.bounding.IFinite3DBounds;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.GAssert;


public class GGeometryNTreeParameters {
   public static enum BoundsPolicy {
      GIVEN,

      MINIMUM,

      REGULAR,
      REGULAR_AND_CENTERED,

      DIMENSIONS_MULTIPLE_OF_SMALLEST,
      DIMENSIONS_MULTIPLE_OF_SMALLEST_AND_CENTERED;
   }


   public static interface AcceptLeafNodeCreationPolicy<

   VectorT extends IVector<VectorT, ?>,

   ElementT,

   GeometryT extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>

   > {
      public boolean acceptLeafNodeCreation(final int depth,
                                            final GAxisAlignedOrthotope<VectorT, ?> bounds,
                                            final Collection<GElementGeometryPair<VectorT, ElementT, GeometryT>> elements);
   }


   public static interface Accept3DLeafNodeCreationPolicy<

   ElementT,

   GeometryT extends IBoundedGeometry3D<? extends IFinite3DBounds<?>>

   >
            extends
               AcceptLeafNodeCreationPolicy<IVector3, ElementT, GeometryT> {
   }


   public static interface Accept2DLeafNodeCreationPolicy<

   ElementT,

   GeometryT extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>


   >
            extends
               AcceptLeafNodeCreationPolicy<IVector2, ElementT, GeometryT> {

   }


   private static class DefaultAcceptLeafNodeCreationPolicy<

   VectorT extends IVector<VectorT, ?>,

   ElementT,

   GeometryT extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>

   >
            implements
               AcceptLeafNodeCreationPolicy<VectorT, ElementT, GeometryT> {

      private final int _maxDepth;
      private final int _maxElementsInLeafs;


      private DefaultAcceptLeafNodeCreationPolicy(final int maxDepth,
                                                  final int maxElementsInLeafs) {
         GAssert.isPositive(maxDepth, "maxDepth");
         GAssert.isPositive(maxElementsInLeafs, "maxElementsInLeafs");

         _maxDepth = maxDepth;
         _maxElementsInLeafs = maxElementsInLeafs;
      }


      @Override
      public boolean acceptLeafNodeCreation(final int depth,
                                            final GAxisAlignedOrthotope<VectorT, ?> bounds,
                                            final Collection<GElementGeometryPair<VectorT, ElementT, GeometryT>> elements) {
         if (depth >= _maxDepth) {
            return true;
         }

         return (elements.size() <= _maxElementsInLeafs);
      }
   }


   final boolean                      _verbose;
   final AcceptLeafNodeCreationPolicy _acceptLeafNodeCreationPolicy;
   final BoundsPolicy                 _boundsPolicy;
   final boolean                      _multiThread;


   public GGeometryNTreeParameters(final boolean verbose,
                                   final int maxDepth,
                                   final int maxElementsInLeafs,
                                   final BoundsPolicy boundsPolicy,
                                   final boolean multiThread) {
      this(verbose, new DefaultAcceptLeafNodeCreationPolicy(maxDepth, maxElementsInLeafs), boundsPolicy, multiThread);
   }


   public GGeometryNTreeParameters(final boolean verbose,
                                   final AcceptLeafNodeCreationPolicy acceptLeafNodeCreationPolicy,
                                   final BoundsPolicy boundsPolicy,
                                   final boolean multiThread) {
      GAssert.notNull(acceptLeafNodeCreationPolicy, "acceptLeafNodeCreationPolicy");
      GAssert.notNull(boundsPolicy, "boundsPolicy");

      _verbose = verbose;
      _acceptLeafNodeCreationPolicy = acceptLeafNodeCreationPolicy;
      _boundsPolicy = boundsPolicy;
      _multiThread = multiThread;
   }


}
