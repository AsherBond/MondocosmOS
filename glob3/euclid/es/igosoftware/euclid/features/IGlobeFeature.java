

package es.igosoftware.euclid.features;

import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.vector.IVector;


public interface IGlobeFeature<

VectorT extends IVector<VectorT, ?>,

GeometryT extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>

> {

   public GeometryT getDefaultGeometry();


   public void setFeatureCollection(final IGlobeFeatureCollection<VectorT, GeometryT> featureCollection);


   public List<Object> getAttributes();


   public boolean hasAttribute(final String fieldName);


   public Object getAttribute(final String fieldName);


}
