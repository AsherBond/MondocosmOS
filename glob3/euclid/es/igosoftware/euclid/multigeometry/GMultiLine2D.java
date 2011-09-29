

package es.igosoftware.euclid.multigeometry;

import java.util.List;

import es.igosoftware.euclid.shape.IPolygonalChain2D;


public class GMultiLine2D
         extends
            GMultiGeometry2D<IPolygonalChain2D> {


   private static final long serialVersionUID = 1L;


   public GMultiLine2D(final IPolygonalChain2D... children) {
      super(children);
   }


   public GMultiLine2D(final List<IPolygonalChain2D> children) {
      super(children);
   }


}
