

package es.igosoftware.experimental.vectorial.symbolizer;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.ICurve2D;
import es.igosoftware.euclid.IGeometry2D;
import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.colors.GColorF;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.GLength;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ICurve2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ISurface2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.GExpressionsSymbolizer2D;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GConstantExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GCreateOval2DExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GCurve2DStyleExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GLengthToFloatExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GOval2DSymbolizerExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GPolygon2DSymbolizerExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GPolygonalChain2DSymbolizerExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GSurface2DStyleExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.IExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GSymbol2DList;
import es.igosoftware.euclid.features.GGeometryType;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.shape.GAxisAlignedOval2D;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.shape.IPolygonalChain2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.IGlobeSymbolizer;
import es.igosoftware.globe.IGlobeVector2Layer;
import es.igosoftware.globe.attributes.GAreaLayerAttribute;
import es.igosoftware.globe.attributes.GBooleanLayerAttribute;
import es.igosoftware.globe.attributes.GFloatLayerAttribute;
import es.igosoftware.globe.attributes.GGroupAttribute;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.io.GFileName;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GMath;
import es.igosoftware.util.GUtils;


public class GGlobeVectorialSymbolizer2D
         extends
            GExpressionsSymbolizer2D
         implements
            IGlobeSymbolizer {

   // curves attributes
   private static final IMeasure<GLength>                               DEFAULT_CURVE_BORDER_WIDTH             = GLength.Meter.value(10);
   private static final IColor                                          DEFAULT_CURVE_COLOR                    = GColorF.YELLOW;
   private static final float                                           DEFAULT_CURVE_OPACITY                  = 1;
   private static final GConstantExpression<IPolygonalChain2D, IColor>  DEFAULT_CURVE_COLOR_EXPRESSION         = new GConstantExpression<IPolygonalChain2D, IColor>(
                                                                                                                        DEFAULT_CURVE_COLOR);

   // surface attributes
   private static final IColor                                          DEFAULT_SURFACE_COLOR                  = GColorF.BLUE;
   private static final float                                           DEFAULT_SURFACE_OPACITY                = 1;
   private static final IMeasure<GLength>                               DEFAULT_SURFACE_CURVE_BORDER_WIDTH     = GLength.Meter.value(10);
   private static final IColor                                          DEFAULT_SURFACE_CURVE_COLOR            = DEFAULT_SURFACE_COLOR.muchDarker();
   private static final float                                           DEFAULT_SURFACE_CURVE_OPACITY          = DEFAULT_SURFACE_OPACITY;
   private static final GConstantExpression<IPolygon2D, IColor>         DEFAULT_SURFACE_COLOR_EXPRESSION       = new GConstantExpression<IPolygon2D, IColor>(
                                                                                                                        DEFAULT_SURFACE_COLOR);
   private static final GConstantExpression<IPolygon2D, IColor>         DEFAULT_SURFACE_CURVE_COLOR_EXPRESSION = new GConstantExpression<IPolygon2D, IColor>(
                                                                                                                        DEFAULT_SURFACE_CURVE_COLOR);

   // points attributes
   private static final IMeasure<GArea>                                 DEFAULT_POINT_AREA                     = GArea.SquareKilometer.value(1);
   private static final IColor                                          DEFAULT_POINT_SURFACE_COLOR            = GColorF.MAGENTA;
   private static final float                                           DEFAULT_POINT_SURFACE_OPACITY          = 1;
   private static final IMeasure<GLength>                               DEFAULT_POINT_CURVE_BORDER_WIDTH       = GLength.Meter.value(10);
   private static final IColor                                          DEFAULT_POINT_CURVE_COLOR              = DEFAULT_POINT_SURFACE_COLOR.muchDarker();
   private static final float                                           DEFAULT_POINT_CURVE_OPACITY            = DEFAULT_POINT_SURFACE_OPACITY;
   private static final GConstantExpression<GAxisAlignedOval2D, IColor> DEFAULT_POINT_CURVE_COLOR_EXPRESSION   = new GConstantExpression<GAxisAlignedOval2D, IColor>(
                                                                                                                        DEFAULT_POINT_CURVE_COLOR);
   private static final GConstantExpression<GAxisAlignedOval2D, IColor> DEFAULT_POINT_SURFACE_COLOR_EXPRESSION = new GConstantExpression<GAxisAlignedOval2D, IColor>(
                                                                                                                        DEFAULT_SURFACE_COLOR);

   private final IGlobeVector2Layer                                     _layer;

   // curve attributes
   private IExpression<IPolygonalChain2D, ICurve2DStyle>                _curveStyleExpression                  = createCurveStyleExpression(
                                                                                                                        DEFAULT_CURVE_BORDER_WIDTH,
                                                                                                                        DEFAULT_CURVE_COLOR,
                                                                                                                        DEFAULT_CURVE_OPACITY);

   // surface attributes
   private IExpression<IPolygon2D, ICurve2DStyle>                       _surfaceCurveStyleExpression           = createCurveStyleExpression(
                                                                                                                        DEFAULT_SURFACE_CURVE_BORDER_WIDTH,
                                                                                                                        DEFAULT_SURFACE_CURVE_COLOR,
                                                                                                                        DEFAULT_SURFACE_CURVE_OPACITY);
   private IExpression<IPolygon2D, ISurface2DStyle>                     _surfaceStyleExpression                = createSurfaceStyleExpression(
                                                                                                                        DEFAULT_SURFACE_COLOR,
                                                                                                                        DEFAULT_SURFACE_OPACITY);

   // points attributes
   private IMeasure<GArea>                                              _pointArea                             = DEFAULT_POINT_AREA;
   private IExpression<GAxisAlignedOval2D, ICurve2DStyle>               _pointCurveStyleExpression             = createCurveStyleExpression(
                                                                                                                        DEFAULT_POINT_CURVE_BORDER_WIDTH,
                                                                                                                        DEFAULT_POINT_CURVE_COLOR,
                                                                                                                        DEFAULT_POINT_CURVE_OPACITY);
   private IExpression<GAxisAlignedOval2D, ISurface2DStyle>             _pointSurfaceStyleExpression           = createSurfaceStyleExpression(
                                                                                                                        DEFAULT_POINT_SURFACE_COLOR,
                                                                                                                        DEFAULT_POINT_SURFACE_OPACITY);


   public GGlobeVectorialSymbolizer2D(final IGlobeVector2Layer layer) {
      super(false, 2, true, false, //

            createPointExpression(
                     DEFAULT_POINT_AREA, //                   
                     GGlobeVectorialSymbolizer2D.<GAxisAlignedOval2D> createCurveStyleExpression(
                              DEFAULT_POINT_CURVE_BORDER_WIDTH, DEFAULT_POINT_CURVE_COLOR, DEFAULT_POINT_CURVE_OPACITY), //
                     GGlobeVectorialSymbolizer2D.<GAxisAlignedOval2D> createSurfaceStyleExpression(DEFAULT_POINT_SURFACE_COLOR,
                              DEFAULT_POINT_SURFACE_OPACITY)), //

            createCurveExpression(GGlobeVectorialSymbolizer2D.<IPolygonalChain2D> createCurveStyleExpression(
                     DEFAULT_CURVE_BORDER_WIDTH, DEFAULT_CURVE_COLOR, DEFAULT_CURVE_OPACITY)), //

            createSurfaceExpression(//
                     GGlobeVectorialSymbolizer2D.<IPolygon2D> createCurveStyleExpression(DEFAULT_SURFACE_CURVE_BORDER_WIDTH,
                              DEFAULT_SURFACE_CURVE_COLOR, DEFAULT_SURFACE_CURVE_OPACITY), //
                     GGlobeVectorialSymbolizer2D.<IPolygon2D> createSurfaceStyleExpression(DEFAULT_SURFACE_COLOR,
                              DEFAULT_SURFACE_OPACITY)) //
      );

      GAssert.notNull(layer, "layer");

      _layer = layer;
   }


   private static IExpression<? extends ICurve2D<? extends IFinite2DBounds<?>>, GSymbol2DList> createCurveExpression(final IExpression<IPolygonalChain2D, ICurve2DStyle> curveStyleExpression) {
      return new GPolygonalChain2DSymbolizerExpression(curveStyleExpression);
   }


   private static <GeometryT extends IGeometry2D> IExpression<GeometryT, ICurve2DStyle> createCurveStyleExpression(final IMeasure<GLength> borderWidth,
                                                                                                                   final IColor color,
                                                                                                                   final float opacity) {
      return new GCurve2DStyleExpression<GeometryT>( //
               new GLengthToFloatExpression<GeometryT>(borderWidth), //
               new GConstantExpression<GeometryT, IColor>(color), //
               new GConstantExpression<GeometryT, Float>(opacity));
   }


   private static IExpression<IVector2, GSymbol2DList> createPointExpression(final IMeasure<GArea> pointArea,
                                                                             final IExpression<GAxisAlignedOval2D, ICurve2DStyle> curveStyleExpression,
                                                                             final IExpression<GAxisAlignedOval2D, ISurface2DStyle> surfaceStyleExpression) {
      final IExpression<IVector2, GAxisAlignedOval2D> toOvalExpression = new GCreateOval2DExpression(pointArea);

      return new GOval2DSymbolizerExpression<IVector2>(toOvalExpression, curveStyleExpression, surfaceStyleExpression);
   }


   private static IExpression<? extends ISurface2D<? extends IFinite2DBounds<?>>, GSymbol2DList> createSurfaceExpression(final IExpression<IPolygon2D, ICurve2DStyle> curveStyleExpression,
                                                                                                                         final IExpression<IPolygon2D, ISurface2DStyle> surfaceStyleExpression) {
      return new GPolygon2DSymbolizerExpression(curveStyleExpression, surfaceStyleExpression);
   }


   private static <GeometryT extends ISurface2D<? extends IFinite2DBounds<?>>> GSurface2DStyleExpression<GeometryT> createSurfaceStyleExpression(final IColor color,
                                                                                                                                                 final float opacity) {
      return new GSurface2DStyleExpression<GeometryT>( //
               new GConstantExpression<GeometryT, IColor>(color), //
               new GConstantExpression<GeometryT, Float>(opacity));
   }


   private ILayerAttribute<?> createAdvancedLayerAttributes() {

      final GBooleanLayerAttribute clusterSymbols = new GBooleanLayerAttribute("Cluster Symbols",
               "Set the Cluster-Symbols option", "ClusterSymbols") {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Boolean get() {
            return isClusterSymbols();
         }


         @Override
         public void set(final Boolean value) {
            setClusterSymbols(value);
         }
      };


      final GFloatLayerAttribute lodMinSize = new GFloatLayerAttribute("LOD Min Size", "", "LODMinSize", 0, 10,
               GFloatLayerAttribute.WidgetType.SPINNER, 1) {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Float get() {
            return (float) getLODMinSize();
         }


         @Override
         public void set(final Float value) {
            setLODMinSize(value);
         }
      };

      final GBooleanLayerAttribute renderLODIgnores = new GBooleanLayerAttribute("Render LOD Ignores",
               "Set the RenderLODIgnores option", "RenderLODIgnores") {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Boolean get() {
            return isRenderLODIgnores();
         }


         @Override
         public void set(final Boolean value) {
            setRenderLODIgnores(value);
         }
      };


      final GBooleanLayerAttribute debugRendering = new GBooleanLayerAttribute("Debug Rendering", "Set the debug rendering mode",
               "DebugRendering") {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Boolean get() {
            return isDebugRendering();
         }


         @Override
         public void set(final Boolean value) {
            setDebugRendering(value);
         }
      };


      return new GGroupAttribute("Advanced", "Advanced settings", clusterSymbols, lodMinSize, renderLODIgnores, debugRendering);
      //return new GTabbedGroupAttribute("Advanced", "Advanced settings", clusterSymbols, lodMinSize, renderLODIgnores, debugRendering);
   }


   private ILayerAttribute<?> createCurvesLayerAttributes(final IGlobeRunningContext context) {

      //      return new GCurveStyleExpressionAttribute<IPolygonalChain2D>("Curves Style",
      //               application.getSmallIcon(GFileName.relative("curves-style.png")), "Curves rendering settings",
      //               "CurveStyleExpression", DEFAULT_CURVE_BORDER_WIDTH, DEFAULT_CURVE_COLOR, DEFAULT_CURVE_OPACITY) {
      //         @Override
      //         public IExpression<IPolygonalChain2D, ICurve2DStyle> get() {
      //            return _curveStyleExpression;
      //         }
      //
      //
      //         @Override
      //         public void set(final IExpression<IPolygonalChain2D, ICurve2DStyle> value) {
      //            setCurveStyleExpression(value);
      //         }
      //      };

      return new GCurveStyleExpressionAttribute<IPolygonalChain2D>("Curves Style", context.getBitmapFactory().getSmallIcon(
               GFileName.relative("curves-style.png")), "Curves rendering settings", "CurveStyleExpression",
               DEFAULT_CURVE_BORDER_WIDTH, DEFAULT_CURVE_COLOR, DEFAULT_CURVE_COLOR_EXPRESSION, DEFAULT_CURVE_OPACITY) {

         @Override
         public IExpression<IPolygonalChain2D, ICurve2DStyle> get() {
            return _curveStyleExpression;
         }


         @Override
         public void set(final IExpression<IPolygonalChain2D, ICurve2DStyle> value) {
            setCurveStyleExpression(value);
         }
      };

   }


   private ILayerAttribute<?> createPointsLayerAttributes(final IGlobeRunningContext context) {

      final GAreaLayerAttribute pointArea = new GAreaLayerAttribute("Size", "Set the point size", "PointArea", 0, 1000, 1) {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public IMeasure<GArea> get() {
            return _pointArea;
         }


         @Override
         public void set(final IMeasure<GArea> value) {
            setPointArea(value);
         }
      };


      //      final GCurveStyleExpressionAttribute<GAxisAlignedOval2D> curveStyle = new GCurveStyleExpressionAttribute<GAxisAlignedOval2D>(
      //               "Border", null, "Points border style", "PointCurveStyleExpression", DEFAULT_POINT_CURVE_BORDER_WIDTH,
      //               DEFAULT_POINT_CURVE_COLOR, DEFAULT_POINT_CURVE_OPACITY) {
      //         @Override
      //         public IExpression<GAxisAlignedOval2D, ICurve2DStyle> get() {
      //            return _pointCurveStyleExpression;
      //         }
      //
      //
      //         @Override
      //         public void set(final IExpression<GAxisAlignedOval2D, ICurve2DStyle> value) {
      //            setPointCurveStyleExpression(value);
      //         }
      //      };
      //
      //
      //      final GSurfaceStyleExpressionAttribute<GAxisAlignedOval2D> surfaceStyle = new GSurfaceStyleExpressionAttribute<GAxisAlignedOval2D>(
      //               "Surface", null, "Points surface style", "PointSurfaceStyleExpression", DEFAULT_POINT_SURFACE_COLOR,
      //               DEFAULT_POINT_SURFACE_OPACITY) {
      //         @Override
      //         public IExpression<GAxisAlignedOval2D, ISurface2DStyle> get() {
      //            return _pointSurfaceStyleExpression;
      //         }
      //
      //
      //         @Override
      //         public void set(final IExpression<GAxisAlignedOval2D, ISurface2DStyle> value) {
      //            setPointSurfaceStyleExpression(value);
      //         }
      //      };


      final GCurveStyleExpressionAttribute<GAxisAlignedOval2D> curveStyle = new GCurveStyleExpressionAttribute<GAxisAlignedOval2D>(
               "Border", null, "Points border style", "PointCurveStyleExpression", DEFAULT_POINT_CURVE_BORDER_WIDTH,
               DEFAULT_POINT_CURVE_COLOR, DEFAULT_POINT_CURVE_COLOR_EXPRESSION, DEFAULT_POINT_CURVE_OPACITY) {
         @Override
         public IExpression<GAxisAlignedOval2D, ICurve2DStyle> get() {
            return _pointCurveStyleExpression;
         }


         @Override
         public void set(final IExpression<GAxisAlignedOval2D, ICurve2DStyle> value) {
            setPointCurveStyleExpression(value);
         }
      };


      final GSurfaceStyleExpressionAttribute<GAxisAlignedOval2D> surfaceStyle = new GSurfaceStyleExpressionAttribute<GAxisAlignedOval2D>(
               "Surface", null, "Points surface style", "PointSurfaceStyleExpression", DEFAULT_POINT_SURFACE_COLOR,
               DEFAULT_POINT_SURFACE_COLOR_EXPRESSION, DEFAULT_POINT_SURFACE_OPACITY) {
         @Override
         public IExpression<GAxisAlignedOval2D, ISurface2DStyle> get() {
            return _pointSurfaceStyleExpression;
         }


         @Override
         public void set(final IExpression<GAxisAlignedOval2D, ISurface2DStyle> value) {
            setPointSurfaceStyleExpression(value);
         }
      };


      //      return new GGroupAttribute("Points Style", application.getSmallIcon(GFileName.relative("points-style.png")),
      //               "Points rendering settings", pointArea, curveStyle, surfaceStyle);

      return new GGroupAttribute("Points Style", context.getBitmapFactory().getSmallIcon(GFileName.relative("points-style.png")),
               "Points rendering settings", pointArea, curveStyle, surfaceStyle);
   }


   private ILayerAttribute<?> createSurfacesLayerAttributes(final IGlobeRunningContext context) {

      //      final GCurveStyleExpressionAttribute<IPolygon2D> curveStyle = new GCurveStyleExpressionAttribute<IPolygon2D>("Border",
      //               null, "Surfaces border style", "SurfaceCurveStyleExpression", DEFAULT_SURFACE_CURVE_BORDER_WIDTH,
      //               DEFAULT_SURFACE_CURVE_COLOR, DEFAULT_SURFACE_CURVE_OPACITY) {
      //         @Override
      //         public IExpression<IPolygon2D, ICurve2DStyle> get() {
      //            return _surfaceCurveStyleExpression;
      //         }
      //
      //
      //         @Override
      //         public void set(final IExpression<IPolygon2D, ICurve2DStyle> value) {
      //            setSurfaceCurveStyleExpression(value);
      //         }
      //      };


      //      final GSurfaceStyleExpressionAttribute<IPolygon2D> surfaceStyle = new GSurfaceStyleExpressionAttribute<IPolygon2D>(
      //               "Surface", null, "Surfaces surface style", "SurfaceStyleExpression", DEFAULT_SURFACE_COLOR,
      //               DEFAULT_SURFACE_OPACITY) {
      //         @Override
      //         public IExpression<IPolygon2D, ISurface2DStyle> get() {
      //            return _surfaceStyleExpression;
      //         }
      //
      //
      //         @Override
      //         public void set(final IExpression<IPolygon2D, ISurface2DStyle> value) {
      //            setSurfaceStyleExpression(value);
      //         }
      //      };


      final GCurveStyleExpressionAttribute<IPolygon2D> curveStyle = new GCurveStyleExpressionAttribute<IPolygon2D>("Border",
               null, "Surfaces border style", "SurfaceCurveStyleExpression", DEFAULT_SURFACE_CURVE_BORDER_WIDTH,
               DEFAULT_SURFACE_CURVE_COLOR, DEFAULT_SURFACE_CURVE_COLOR_EXPRESSION, DEFAULT_SURFACE_CURVE_OPACITY) {
         @Override
         public IExpression<IPolygon2D, ICurve2DStyle> get() {
            return _surfaceCurveStyleExpression;
         }


         @Override
         public void set(final IExpression<IPolygon2D, ICurve2DStyle> value) {
            setSurfaceCurveStyleExpression(value);
         }
      };


      final GSurfaceStyleExpressionAttribute<IPolygon2D> surfaceStyle = new GSurfaceStyleExpressionAttribute<IPolygon2D>(
               "Surface", null, "Surfaces surface style", "SurfaceStyleExpression", DEFAULT_SURFACE_COLOR,
               DEFAULT_SURFACE_COLOR_EXPRESSION, DEFAULT_SURFACE_OPACITY) {
         @Override
         public IExpression<IPolygon2D, ISurface2DStyle> get() {
            return _surfaceStyleExpression;
         }


         @Override
         public void set(final IExpression<IPolygon2D, ISurface2DStyle> value) {
            setSurfaceStyleExpression(value);
         }
      };


      return new GGroupAttribute("Surfaces Style", context.getBitmapFactory().getSmallIcon(
               GFileName.relative("surfaces-style.png")), "Surfaces rendering settings", curveStyle, surfaceStyle);
   }


   @Override
   public List<? extends ILayerAttribute<?>> getLayerAttributes(final IGlobeRunningContext context,
                                                                final IGlobeLayer unusedLayer) {
      if (unusedLayer != _layer) {
         throw new RuntimeException("Invalid layer");
      }

      final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> featuresCollection = _layer.getFeaturesCollection();


      final EnumSet<GGeometryType> geometriesTypes = featuresCollection.getGeometryType();

      final ArrayList<ILayerAttribute<?>> result = new ArrayList<ILayerAttribute<?>>(4);

      if (geometriesTypes.contains(GGeometryType.POINT)) {
         result.add(createPointsLayerAttributes(context));
      }

      if (geometriesTypes.contains(GGeometryType.CURVE)) {
         result.add(createCurvesLayerAttributes(context));
      }

      if (geometriesTypes.contains(GGeometryType.SURFACE)) {
         result.add(createSurfacesLayerAttributes(context));
      }

      result.add(createAdvancedLayerAttributes());

      result.trimToSize();
      return result;
   }


   @Override
   public void setClusterSymbols(final boolean clusterSymbols) {
      if (clusterSymbols == isClusterSymbols()) {
         return;
      }

      super.setClusterSymbols(clusterSymbols);

      _layer.firePropertyChange("ClusterSymbols", !clusterSymbols, clusterSymbols);

      styleChanged();
   }


   @Override
   public void setCurveExpression(final IExpression<? extends ICurve2D<? extends IFinite2DBounds<?>>, GSymbol2DList> curveExpression) {
      final IExpression<ICurve2D<? extends IFinite2DBounds<?>>, GSymbol2DList> oldCurveExpression = getCurveExpression();

      if (GUtils.equals(curveExpression, oldCurveExpression)) {
         return;
      }

      super.setCurveExpression(curveExpression);

      _layer.firePropertyChange("CurveExpression", oldCurveExpression, curveExpression);

      styleChanged();
   }


   private void setCurveStyleExpression(final IExpression<IPolygonalChain2D, ICurve2DStyle> curveStyleExpression) {
      final IExpression<IPolygonalChain2D, ICurve2DStyle> oldCurveStyleExpression = _curveStyleExpression;
      if (GUtils.equals(curveStyleExpression, oldCurveStyleExpression)) {
         return;
      }

      _curveStyleExpression = curveStyleExpression;

      _layer.firePropertyChange("CurveStyleExpression", oldCurveStyleExpression, curveStyleExpression);

      updateCurveExpression();
   }


   @Override
   public void setDebugRendering(final boolean debugRendering) {
      if (debugRendering == isDebugRendering()) {
         return;
      }

      super.setDebugRendering(debugRendering);

      _layer.firePropertyChange("DebugRendering", !debugRendering, debugRendering);

      styleChanged();
   }


   @Override
   public void setLODMinSize(final double lodMinSize) {
      final double oldLodMinSize = getLODMinSize();

      if (GMath.closeTo(lodMinSize, oldLodMinSize)) {
         return;
      }

      super.setLODMinSize(lodMinSize);

      _layer.firePropertyChange("LODMinSize", oldLodMinSize, lodMinSize);

      styleChanged();
   }


   private void setPointArea(final IMeasure<GArea> pointArea) {
      final IMeasure<GArea> oldPointArea = _pointArea;
      if (GUtils.equals(pointArea, oldPointArea)) {
         return;
      }

      _pointArea = pointArea;

      _layer.firePropertyChange("PointArea", oldPointArea, pointArea);

      updatePointExpression();
   }


   private void setPointCurveStyleExpression(final IExpression<GAxisAlignedOval2D, ICurve2DStyle> pointCurveStyleExpression) {
      final IExpression<GAxisAlignedOval2D, ICurve2DStyle> oldCurveStyleExpression = _pointCurveStyleExpression;
      if (GUtils.equals(pointCurveStyleExpression, oldCurveStyleExpression)) {
         return;
      }

      _pointCurveStyleExpression = pointCurveStyleExpression;

      _layer.firePropertyChange("PointCurveStyleExpression", oldCurveStyleExpression, pointCurveStyleExpression);

      updatePointExpression();
   }


   @Override
   public void setPointExpression(final IExpression<IVector2, GSymbol2DList> pointExpression) {
      final IExpression<IVector2, GSymbol2DList> oldPointExpression = getPointExpression();

      if (GUtils.equals(pointExpression, oldPointExpression)) {
         return;
      }

      super.setPointExpression(pointExpression);

      _layer.firePropertyChange("PointExpression", oldPointExpression, pointExpression);

      styleChanged();
   }


   private void setPointSurfaceStyleExpression(final IExpression<GAxisAlignedOval2D, ISurface2DStyle> pointSurfaceStyleExpression) {
      final IExpression<GAxisAlignedOval2D, ISurface2DStyle> oldSurfaceStyleExpression = _pointSurfaceStyleExpression;
      if (GUtils.equals(pointSurfaceStyleExpression, oldSurfaceStyleExpression)) {
         return;
      }

      _pointSurfaceStyleExpression = pointSurfaceStyleExpression;

      _layer.firePropertyChange("PointSurfaceStyleExpression", oldSurfaceStyleExpression, pointSurfaceStyleExpression);

      updatePointExpression();
   }


   @Override
   public void setRenderLODIgnores(final boolean renderLODIgnores) {
      if (renderLODIgnores == isRenderLODIgnores()) {
         return;
      }

      super.setRenderLODIgnores(renderLODIgnores);

      _layer.firePropertyChange("RenderLODIgnores", !renderLODIgnores, renderLODIgnores);

      styleChanged();
   }


   private void setSurfaceCurveStyleExpression(final IExpression<IPolygon2D, ICurve2DStyle> surfaceCurveStyleExpression) {
      final IExpression<IPolygon2D, ICurve2DStyle> oldSurfaceCurveStyleExpression = _surfaceCurveStyleExpression;
      if (GUtils.equals(surfaceCurveStyleExpression, oldSurfaceCurveStyleExpression)) {
         return;
      }

      _surfaceCurveStyleExpression = surfaceCurveStyleExpression;

      _layer.firePropertyChange("SurfaceCurveStyleExpression", oldSurfaceCurveStyleExpression, surfaceCurveStyleExpression);

      updateSurfaceExpression();
   }


   @Override
   public void setSurfaceExpression(final IExpression<? extends ISurface2D<? extends IFinite2DBounds<?>>, GSymbol2DList> surfaceExpression) {
      final IExpression<ISurface2D<? extends IFinite2DBounds<?>>, GSymbol2DList> oldSurfaceExpression = getSurfaceExpression();

      if (GUtils.equals(surfaceExpression, oldSurfaceExpression)) {
         return;
      }

      super.setSurfaceExpression(surfaceExpression);

      _layer.firePropertyChange("SurfaceExpression", oldSurfaceExpression, surfaceExpression);

      styleChanged();
   }


   private void setSurfaceStyleExpression(final IExpression<IPolygon2D, ISurface2DStyle> surfaceStyleExpression) {
      final IExpression<IPolygon2D, ISurface2DStyle> oldSurfaceStyleExpression = _surfaceStyleExpression;
      if (GUtils.equals(surfaceStyleExpression, oldSurfaceStyleExpression)) {
         return;
      }

      _surfaceStyleExpression = surfaceStyleExpression;

      _layer.firePropertyChange("SurfaceStyleExpression", oldSurfaceStyleExpression, surfaceStyleExpression);

      updateSurfaceExpression();
   }


   private void styleChanged() {
      _layer.clearCache();
      _layer.redraw();
   }


   private void updateCurveExpression() {
      setCurveExpression(createCurveExpression(_curveStyleExpression));
   }


   private void updatePointExpression() {
      setPointExpression(createPointExpression(_pointArea, _pointCurveStyleExpression, _pointSurfaceStyleExpression));
   }


   private void updateSurfaceExpression() {
      setSurfaceExpression(createSurfaceExpression(_surfaceCurveStyleExpression, _surfaceStyleExpression));
   }


}
