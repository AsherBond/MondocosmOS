

package es.unex.s3xtante.modules.sextante.bindings;

import java.util.List;

import com.vividsolutions.jts.geom.Geometry;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GPredicate;
import es.igosoftware.utils.GJTSUtils;
import es.unex.sextante.dataObjects.FeatureImpl;
import es.unex.sextante.dataObjects.IFeature;
import es.unex.sextante.dataObjects.vectorFilters.IVectorLayerFilter;


public class SextanteFilterPredicate
         extends
            GPredicate<IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>> {

   private final IVectorLayerFilter _filter;


   public SextanteFilterPredicate(final IVectorLayerFilter filter) {

      _filter = filter;

   }


   public SextanteFilterPredicate() {

      _filter = null;

   }


   @Override
   public boolean evaluate(final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> globeFeature) {

      if (_filter == null) {
         return true;
      }

      final IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>> euclidGeom = globeFeature.getDefaultGeometry();
      final List<Object> record = globeFeature.getAttributes();
      final Geometry jtsGeom = GJTSUtils.toJTS(euclidGeom);
      final IFeature sextanteFeature = new FeatureImpl(jtsGeom, record.toArray());
      return _filter.accept(sextanteFeature, 0/*this index dos es not apply here, so we pass zero*/);
   }

}
