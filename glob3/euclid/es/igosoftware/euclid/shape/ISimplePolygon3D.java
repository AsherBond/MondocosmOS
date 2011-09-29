

package es.igosoftware.euclid.shape;

import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.vector.IVector3;


public interface ISimplePolygon3D
         extends
            ISimplePolygon<IVector3, GSegment3D, GAxisAlignedBox>,
            IPolygon3D {

}
