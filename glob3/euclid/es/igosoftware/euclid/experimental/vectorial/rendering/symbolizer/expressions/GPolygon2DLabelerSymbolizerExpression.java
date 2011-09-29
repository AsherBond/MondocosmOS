

package es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IProjectionTool;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.ISymbolizer2D;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GLabel2DSymbol;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GSymbol2DList;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.multigeometry.GMultiGeometry2D;
import es.igosoftware.euclid.shape.GSegment2D;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorI2;
import es.igosoftware.util.GAssert;


public class GPolygon2DLabelerSymbolizerExpression
         extends
            GExpressionAbstract<IPolygon2D, GSymbol2DList>
         implements
            IGeometry2DSymbolizerExpression<IPolygon2D> {

   private final String _attributeName;


   public GPolygon2DLabelerSymbolizerExpression(final String attributeName) {
      GAssert.notNull(attributeName, "attributeName");

      _attributeName = attributeName;
   }


   @Override
   public double getMaximumSizeInMeters(final IVectorial2DRenderingScaler scaler) {
      return 0;
   }


   @Override
   public void preprocessFeatures(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features) {
   }


   @Override
   public void preRender(final IVectorI2 renderExtent,
                         final IProjectionTool projectionTool,
                         final GAxisAlignedRectangle viewport,
                         final ISymbolizer2D symbolizer,
                         final IVectorial2DDrawer drawer) {
   }


   @Override
   public void postRender(final IVectorI2 renderExtent,
                          final IProjectionTool projectionTool,
                          final GAxisAlignedRectangle viewport,
                          final ISymbolizer2D symbolizer,
                          final IVectorial2DDrawer drawer) {

   }


   @Override
   public GSymbol2DList evaluate(final IPolygon2D polygon,
                                 final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                 final IVectorial2DRenderingScaler scaler) {

      final IPolygon2D surface = polygon;

      final String label = (String) feature.getAttribute(_attributeName);

      if ((label == null) || label.trim().isEmpty()) {
         return null;
      }


      if (feature.getDefaultGeometry() instanceof GMultiGeometry2D) {
         @SuppressWarnings("unchecked")
         final GMultiGeometry2D<IBoundedGeometry2D<? extends IFinite2DBounds<?>>> multigeometry = (GMultiGeometry2D<IBoundedGeometry2D<? extends IFinite2DBounds<?>>>) feature.getDefaultGeometry();
         final IBoundedGeometry2D<? extends IFinite2DBounds<?>> biggestGeometry = getBiggestGeometry(multigeometry);
         if (biggestGeometry != surface) {
            return null;
         }
      }


      final IVector2 centroid = surface.getCentroid();
      final IVector2 position;
      if (surface.contains(centroid)) {
         position = scaler.scaleAndTranslate(centroid);
      }
      else {
         final GAxisAlignedRectangle bounds = polygon.getBounds();

         final GSegment2D bisector1 = bounds.getVerticalBisector();
         final GSegment2D bisector2 = bounds.getVerticalBisectorAt(centroid.x());

         final List<GSegment2D> segments = new ArrayList<GSegment2D>();
         segments.addAll(polygon.getIntersections(bisector1));
         segments.addAll(polygon.getIntersections(bisector2));

         GSegment2D largestSegment = null;
         double largestLenght = Double.NEGATIVE_INFINITY;
         for (final GSegment2D segment : segments) {
            final double currentLenght = segment.length();
            if (currentLenght > largestLenght) {
               largestLenght = currentLenght;
               largestSegment = segment;
            }
         }

         if (largestSegment == null) {
            position = scaler.scaleAndTranslate(centroid);
         }
         else {
            position = scaler.scaleAndTranslate(largestSegment.getCentroid());
         }

      }

      final Font font = new Font("Serif", Font.BOLD, 25);

      return new GSymbol2DList(new GLabel2DSymbol(position, label, font));
   }


   private static IBoundedGeometry2D<? extends IFinite2DBounds<?>> getBiggestGeometry(final GMultiGeometry2D<IBoundedGeometry2D<? extends IFinite2DBounds<?>>> multigeometry) {
      IBoundedGeometry2D<? extends IFinite2DBounds<?>> biggestGeometry = null;
      double biggestArea = Double.NEGATIVE_INFINITY;
      for (final IBoundedGeometry2D<? extends IFinite2DBounds<?>> geometry : multigeometry) {
         final double currentArea = geometry.getBounds().area();
         if (currentArea > biggestArea) {
            biggestArea = currentArea;
            biggestGeometry = geometry;
         }
      }

      return biggestGeometry;
   }


}
