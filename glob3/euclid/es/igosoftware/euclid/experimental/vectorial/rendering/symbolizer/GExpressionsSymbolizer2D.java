

package es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.ICurve2D;
import es.igosoftware.euclid.IGeometry2D;
import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IProjectionTool;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.GNullSurface2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ICurve2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ISurface2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GNullExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.IExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GRectangle2DSymbol;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GSymbol2DList;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.ntree.GGTInnerNode;
import es.igosoftware.euclid.ntree.GGTNode;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorI2;
import es.igosoftware.util.GMath;


public class GExpressionsSymbolizer2D
         implements
            ISymbolizer2D {


   private static final ICurve2DStyle                                           INNER_NODE_STYLE = new ICurve2DStyle() {
                                                                                                    @Override
                                                                                                    public Stroke getBorderStroke() {
                                                                                                       return new BasicStroke(1);
                                                                                                    }


                                                                                                    @Override
                                                                                                    public Paint getBorderPaint() {
                                                                                                       return new Color(0f, 1f,
                                                                                                                0f, 0.5f).darker().darker();
                                                                                                    }


                                                                                                    @Override
                                                                                                    public boolean isGroupableWith(final ICurve2DStyle that) {
                                                                                                       return false;
                                                                                                    }
                                                                                                 };

   private static final ICurve2DStyle                                           LEAF_NODE_STYLE  = new ICurve2DStyle() {
                                                                                                    @Override
                                                                                                    public Stroke getBorderStroke() {
                                                                                                       return new BasicStroke(
                                                                                                                1,
                                                                                                                BasicStroke.CAP_ROUND,
                                                                                                                BasicStroke.JOIN_ROUND,
                                                                                                                10, new float[] {
                     2,
                     2
                                                                                                                }, 0);
                                                                                                    }


                                                                                                    @Override
                                                                                                    public Paint getBorderPaint() {
                                                                                                       return new Color(0f, 1f,
                                                                                                                0f, 0.5f);
                                                                                                    }


                                                                                                    @Override
                                                                                                    public boolean isGroupableWith(final ICurve2DStyle that) {
                                                                                                       return false;
                                                                                                    }
                                                                                                 };


   private boolean                                                              _debugRendering;
   private double                                                               _lodMinSize;
   private boolean                                                              _renderLODIgnores;
   private boolean                                                              _clusterSymbols;

   private IExpression<IVector2, GSymbol2DList>                                 _pointExpression;
   private IExpression<ICurve2D<? extends IFinite2DBounds<?>>, GSymbol2DList>   _curveExpression;
   private IExpression<ISurface2D<? extends IFinite2DBounds<?>>, GSymbol2DList> _surfaceExpression;


   public GExpressionsSymbolizer2D(final double lodMinSize,
                                   final boolean renderLODIgnores,
                                   final boolean clusterSymbols,
                                   final IExpression<IVector2, GSymbol2DList> pointExpression,
                                   final IExpression<? extends ICurve2D<? extends IFinite2DBounds<?>>, GSymbol2DList> curveExpression,
                                   final IExpression<? extends ISurface2D<? extends IFinite2DBounds<?>>, GSymbol2DList> surfaceExpression) {
      this(false, lodMinSize, renderLODIgnores, clusterSymbols, pointExpression, curveExpression, surfaceExpression);
   }


   public GExpressionsSymbolizer2D(final boolean debugRendering,
                                   final double lodMinSize,
                                   final boolean renderLODIgnores,
                                   final boolean clusterSymbols,
                                   final IExpression<IVector2, GSymbol2DList> pointExpression,
                                   final IExpression<? extends ICurve2D<? extends IFinite2DBounds<?>>, GSymbol2DList> curveExpression,
                                   final IExpression<? extends ISurface2D<? extends IFinite2DBounds<?>>, GSymbol2DList> surfaceExpression) {

      _debugRendering = debugRendering;
      _lodMinSize = lodMinSize;
      _renderLODIgnores = renderLODIgnores;
      _clusterSymbols = clusterSymbols;

      _pointExpression = expressionOrNull(pointExpression);
      _curveExpression = expressionOrNull(curveExpression);
      _surfaceExpression = expressionOrNull(surfaceExpression);
   }


   @SuppressWarnings("unchecked")
   private static <GeometryT extends IGeometry2D> IExpression<GeometryT, GSymbol2DList> expressionOrNull(final IExpression<? extends GeometryT, GSymbol2DList> expression) {
      return (expression == null) ? GNullExpression.INSTANCE : expression;
   }


   @Override
   public boolean isDebugRendering() {
      return _debugRendering;
   }


   @Override
   public double getLODMinSize() {
      return _lodMinSize;
   }


   @Override
   public boolean isRenderLODIgnores() {
      return _renderLODIgnores;
   }


   @Override
   public void preprocessFeatures(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features) {
      _pointExpression.preprocessFeatures(features);
      _curveExpression.preprocessFeatures(features);
      _surfaceExpression.preprocessFeatures(features);
   }


   @Override
   public void preRender(final IVectorI2 renderExtent,
                         final IProjectionTool projectionTool,
                         final GAxisAlignedRectangle viewport,
                         final ISymbolizer2D symbolizer,
                         final IVectorial2DDrawer drawer) {
      _pointExpression.preRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
      _curveExpression.preRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
      _surfaceExpression.preRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
   }


   @Override
   public void postRender(final IVectorI2 renderExtent,
                          final IProjectionTool projectionTool,
                          final GAxisAlignedRectangle viewport,
                          final ISymbolizer2D symbolizer,
                          final IVectorial2DDrawer drawer) {
      _pointExpression.postRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
      _curveExpression.postRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
      _surfaceExpression.postRender(renderExtent, projectionTool, viewport, symbolizer, drawer);
   }


   @Override
   public double getMaximumSizeInMeters(final IVectorial2DRenderingScaler scaler) {
      return GMath.maxD(//
               _pointExpression.getMaximumSizeInMeters(scaler), //
               _curveExpression.getMaximumSizeInMeters(scaler), //
               _surfaceExpression.getMaximumSizeInMeters(scaler));
   }


   @Override
   public boolean isClusterSymbols() {
      return _clusterSymbols;
   }


   @Override
   public GSymbol2DList getNodeSymbols(final GGTNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> node,
                                       final IVectorial2DRenderingScaler scaler) {
      if (!isDebugRendering()) {
         return null;
      }

      if (node.getElementsCount() == 0) {
         return null;
      }

      final boolean isInner = (node instanceof GGTInnerNode);

      final ISurface2DStyle surfaceStyle = GNullSurface2DStyle.INSTANCE;
      final ICurve2DStyle curveStyle = isInner ? INNER_NODE_STYLE : LEAF_NODE_STYLE;

      final GAxisAlignedRectangle scaledBounds = (GAxisAlignedRectangle) scaler.scaleAndTranslate(node.getBounds());
      final GRectangle2DSymbol boundsRectangle = new GRectangle2DSymbol(scaledBounds, null, surfaceStyle, curveStyle,
               Integer.MAX_VALUE, false);

      //      final IVector2 position = scaledBounds._center;
      //      final String msg = "" + node.getAllElementsCount();
      //      final Font font = new Font("Dialog", Font.PLAIN, 8);
      //
      //      final GLabel2DSymbol label = new GLabel2DSymbol(position, msg, font);
      //      //      return Collections.singleton(boundsRectangle);
      //      @SuppressWarnings("unchecked")
      //      final GSymbol2DList symbols = new GSymbol2DList(boundsRectangle, label);
      //      return symbols;
      return new GSymbol2DList(boundsRectangle);
   }


   @Override
   public GSymbol2DList getPointSymbols(final IVector2 point,
                                        final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                        final IVectorial2DRenderingScaler scaler) {
      return _pointExpression.evaluate(point, feature, scaler);
   }


   @Override
   public GSymbol2DList getCurveSymbols(final ICurve2D<? extends IFinite2DBounds<?>> curve,
                                        final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                        final IVectorial2DRenderingScaler scaler) {
      return _curveExpression.evaluate(curve, feature, scaler);
   }


   @Override
   public GSymbol2DList getSurfaceSymbols(final ISurface2D<? extends IFinite2DBounds<?>> surface,
                                          final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                          final IVectorial2DRenderingScaler scaler) {
      return _surfaceExpression.evaluate(surface, feature, scaler);
   }


   public void setLODMinSize(final double lodMinSize) {
      _lodMinSize = lodMinSize;
   }


   public IExpression<IVector2, GSymbol2DList> getPointExpression() {
      return _pointExpression;
   }


   public void setPointExpression(final IExpression<IVector2, GSymbol2DList> pointExpression) {
      _pointExpression = expressionOrNull(pointExpression);
   }


   public IExpression<ICurve2D<? extends IFinite2DBounds<?>>, GSymbol2DList> getCurveExpression() {
      return _curveExpression;
   }


   public void setCurveExpression(final IExpression<? extends ICurve2D<? extends IFinite2DBounds<?>>, GSymbol2DList> curveExpression) {
      _curveExpression = expressionOrNull(curveExpression);
   }


   public IExpression<ISurface2D<? extends IFinite2DBounds<?>>, GSymbol2DList> getSurfaceExpression() {
      return _surfaceExpression;
   }


   public void setSurfaceExpression(final IExpression<? extends ISurface2D<? extends IFinite2DBounds<?>>, GSymbol2DList> surfaceExpression) {
      _surfaceExpression = expressionOrNull(surfaceExpression);
   }


   public void setDebugRendering(final boolean debugRendering) {
      _debugRendering = debugRendering;
   }


   public void setRenderLODIgnores(final boolean renderLODIgnores) {
      _renderLODIgnores = renderLODIgnores;
   }


   public void setClusterSymbols(final boolean clusterSymbols) {
      _clusterSymbols = clusterSymbols;
   }


}
