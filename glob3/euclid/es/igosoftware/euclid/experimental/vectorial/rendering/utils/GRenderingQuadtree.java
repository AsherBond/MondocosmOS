

package es.igosoftware.euclid.experimental.vectorial.rendering.utils;

import java.util.Collection;
import java.util.Collections;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.multigeometry.GMultiGeometry2D;
import es.igosoftware.euclid.ntree.GGeometryNTreeParameters;
import es.igosoftware.euclid.ntree.quadtree.GGeometryQuadtree;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.IFunction;


public class GRenderingQuadtree<

FeatureT extends IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>

>
         extends
            GGeometryQuadtree<FeatureT, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> {


   public GRenderingQuadtree(final String name,
                             final Iterable<? extends FeatureT> elements,
                             final GGeometryNTreeParameters parameters,
                             final GAxisAlignedRectangle bounds) {
      super(name, bounds, elements,
            new IFunction<FeatureT, Collection<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>() {
               @Override
               public Collection<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> apply(final FeatureT feature) {
                  final IBoundedGeometry2D<? extends IFinite2DBounds<?>> geometry = feature.getDefaultGeometry();

                  if (geometry instanceof GMultiGeometry2D) {
                     @SuppressWarnings("unchecked")
                     final GMultiGeometry2D<IBoundedGeometry2D<? extends IFinite2DBounds<?>>> multigeometry = (GMultiGeometry2D<IBoundedGeometry2D<? extends IFinite2DBounds<?>>>) geometry;

                     return multigeometry.getChildren();
                  }

                  return Collections.singleton(geometry);
               }
            }, parameters);
   }

}
