

package es.igosoftware.euclid.bounding;

import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.euclid.shape.GSegment2D;
import es.igosoftware.euclid.vector.IVector2;


public interface IBounds2D<GeometryT extends IBounds<IVector2, GeometryT>>
         extends
            IBounds<IVector2, GeometryT>,
            ISurface2D<GeometryT> {


   public boolean touches(final IBounds2D<?> that);


   public boolean touchesWithRectangle(final GAxisAlignedRectangle rectangle);


   public boolean touchesWithDisk(final GDisk disk);


   public boolean touchesWithCapsule2D(final GCapsule2D capsule);


   public GSegment2D getHorizontalBisector();


   public GSegment2D getVerticalBisector();


}
