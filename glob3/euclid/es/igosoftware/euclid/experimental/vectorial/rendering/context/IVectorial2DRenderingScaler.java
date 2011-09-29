

package es.igosoftware.euclid.experimental.vectorial.rendering.context;

import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.experimental.vectorial.rendering.utils.GAWTPoints;
import es.igosoftware.euclid.vector.IPointsContainer;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorFunction;


public interface IVectorial2DRenderingScaler
         extends
            IVectorFunction<IVector2> {


   public GAWTPoints toScaledAndTranslatedPoints(final IPointsContainer<IVector2> pointsContainer);


   public IVector2 scaleExtent(final IVector2 extent);


   public IVector2 scaleAndTranslate(final IVector2 point);


   public GAxisAlignedOrthotope<IVector2, ?> scaleAndTranslate(GAxisAlignedOrthotope<IVector2, ?> bounds);


   public IVector2 increment(final IVector2 position,
                             final double deltaEasting,
                             final double deltaNorthing);


}
