

package es.igosoftware.euclid.experimental.vectorial.rendering.context;

import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IVector2;


public interface IProjectionTool {

   public IVector2 increment(final IVector2 position,
                             final GProjection projection,
                             final double deltaEasting,
                             final double deltaNorthing);

}
