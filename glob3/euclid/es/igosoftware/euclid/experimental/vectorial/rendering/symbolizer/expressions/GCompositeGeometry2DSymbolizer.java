

package es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.IGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IProjectionTool;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.ISymbolizer2D;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GSymbol2DList;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorI2;
import es.igosoftware.util.GAssert;


public class GCompositeGeometry2DSymbolizer<GeometryT extends IGeometry2D>
         extends
            GExpressionAbstract<GeometryT, GSymbol2DList>
         implements
            IGeometry2DSymbolizerExpression<GeometryT> {


   private final List<IExpression<GeometryT, GSymbol2DList>> _children;


   public GCompositeGeometry2DSymbolizer(final List<IExpression<GeometryT, GSymbol2DList>> children) {
      GAssert.notEmpty(children, "children");

      _children = new ArrayList<IExpression<GeometryT, GSymbol2DList>>(children); // copy to avoid external modification
   }


   public GCompositeGeometry2DSymbolizer(final IExpression<GeometryT, GSymbol2DList>... children) {
      GAssert.notEmpty(children, "children");

      _children = Arrays.asList(children);
   }


   @Override
   public double getMaximumSizeInMeters(final IVectorial2DRenderingScaler scaler) {
      double max = Double.NEGATIVE_INFINITY;
      for (final IExpression<GeometryT, GSymbol2DList> child : _children) {
         final double current = child.getMaximumSizeInMeters(scaler);
         if (current > max) {
            max = current;
         }
      }
      return max;
   }


   @Override
   public void preprocessFeatures(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features) {
      for (final IExpression<GeometryT, GSymbol2DList> child : _children) {
         child.preprocessFeatures(features);
      }
   }


   @Override
   public void preRender(final IVectorI2 renderExtent,
                         final IProjectionTool projectionTool,
                         final GAxisAlignedRectangle viewport,
                         final ISymbolizer2D symbolizer,
                         final IVectorial2DDrawer drawer) {
      for (final IExpression<GeometryT, GSymbol2DList> child : _children) {
         child.preRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
      }
   }


   @Override
   public void postRender(final IVectorI2 renderExtent,
                          final IProjectionTool projectionTool,
                          final GAxisAlignedRectangle viewport,
                          final ISymbolizer2D symbolizer,
                          final IVectorial2DDrawer drawer) {
      for (final IExpression<GeometryT, GSymbol2DList> child : _children) {
         child.postRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
      }
   }


   @Override
   public GSymbol2DList evaluate(final GeometryT geometry,
                                 final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                 final IVectorial2DRenderingScaler scaler) {


      final GSymbol2DList result = new GSymbol2DList();

      for (final IExpression<GeometryT, GSymbol2DList> child : _children) {
         final GSymbol2DList childSymbols = child.evaluate(geometry, feature, scaler);
         if (childSymbols != null) {
            result.addAll(childSymbols);
         }
      }

      return result;
   }


}
