

package es.unex.s3xtante.modules.sextante.bindings;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GFilterIterator;
import es.igosoftware.utils.GJTSUtils;
import es.unex.sextante.core.Sextante;
import es.unex.sextante.dataObjects.FeatureImpl;
import es.unex.sextante.dataObjects.IFeature;
import es.unex.sextante.dataObjects.IFeatureIterator;
import es.unex.sextante.dataObjects.vectorFilters.IVectorLayerFilter;
import es.unex.sextante.exceptions.IteratorException;


public class GlobeFeatureIterator
         implements
            IFeatureIterator {

   private final GFilterIterator<IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>> _iterator;


   public GlobeFeatureIterator(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> features,
                            final List<IVectorLayerFilter> filters) {

      final List<SextanteFilterPredicate> predicates = new ArrayList<SextanteFilterPredicate>();
      predicates.add(new SextanteFilterPredicate());
      for (final IVectorLayerFilter filter : filters) {
         predicates.add(new SextanteFilterPredicate(filter));

      }

      _iterator = new GFilterIterator<IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>>(
               features.iterator(), predicates.toArray(new SextanteFilterPredicate[0]));

   }


   @Override
   public boolean hasNext() {

      return _iterator.hasNext();


   }


   @Override
   public IFeature next() throws IteratorException {

      try {
         final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> globeFeature = _iterator.next();
         final IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>> euclidGeom = globeFeature.getDefaultGeometry();
         final List<Object> record = globeFeature.getAttributes();
         final Geometry jtsGeom = GJTSUtils.toJTS(euclidGeom);
         final List<Object> recordWithoutGeom = new ArrayList<Object>();
         for (int i = 0; i < record.size(); i++) {
            if (!(record.get(i) instanceof Geometry)) {
               recordWithoutGeom.add(record.get(i));
            }
         }
         final IFeature sextanteFeature = new FeatureImpl(jtsGeom, recordWithoutGeom.toArray(new Object[0]));
         return sextanteFeature;
      }
      catch (final Exception e) {
         Sextante.addErrorToLog(e);
         throw new IteratorException();
      }

   }


   @Override
   public void close() {
   }

}
