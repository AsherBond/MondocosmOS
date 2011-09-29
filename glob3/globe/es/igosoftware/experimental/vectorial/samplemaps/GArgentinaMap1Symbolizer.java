

package es.igosoftware.experimental.vectorial.samplemaps;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.colors.GColorF;
import es.igosoftware.euclid.colors.GColorI;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.GLength;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.experimental.vectorial.rendering.coloring.GColorBrewerColorSchemeSet;
import es.igosoftware.euclid.experimental.vectorial.rendering.coloring.GColorScheme;
import es.igosoftware.euclid.experimental.vectorial.rendering.coloring.GUniqueValuesColorizer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IProjectionTool;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ICurve2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ISurface2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.GExpressionsSymbolizer2D;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.ISymbolizer2D;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GCompositeGeometry2DSymbolizer;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GConditionalExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GConstantExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GCreateOval2DExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GCreateRectangle2DExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GCurve2DStyleExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GEmptyExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GIcon2DSymbolizerExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GLengthToFloatExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GOval2DSymbolizerExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GPolygon2DLabelerSymbolizerExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GPolygon2DSymbolizerExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GPolygonalChain2DSymbolizerExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GRectangle2DSymbolizerExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GSurface2DStyleExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GSwitchExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GTransformerExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.IExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GSymbol2DList;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.shape.GAxisAlignedOval2D;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.shape.IPolygonalChain2D;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorI2;
import es.igosoftware.io.GFileName;
import es.igosoftware.util.GMath;
import es.igosoftware.util.GPair;
import es.igosoftware.util.IFunction;


public class GArgentinaMap1Symbolizer
         extends
            GExpressionsSymbolizer2D {

   private static final String  COUNTRY  = "NEV_Countr";
   private static final String  PROVINCE = "NAME_1";
   private static final String  CATEGORY = "CATEGORY";

   private static BufferedImage _backgroundImage;


   private final boolean        _drawBackgroundImage;


   public GArgentinaMap1Symbolizer(final boolean debugRendering,
                                   final double lodMinSize,
                                   final boolean renderLODIgnores,
                                   final boolean clusterSymbols,
                                   final boolean drawBackgroundImage) throws IOException {
      super(debugRendering, lodMinSize, renderLODIgnores, clusterSymbols, //
            createPointSymbolizerExpression(), //
            createPolygonalChainSymbolizerExpression(), //
            createPolygonSymbolizerExpression());

      _drawBackgroundImage = drawBackgroundImage;
   }


   private static IExpression<IPolygonalChain2D, GSymbol2DList> createPolygonalChainSymbolizerExpression() {
      final GCurve2DStyleExpression<IPolygonalChain2D> curveStyleExpression = new GCurve2DStyleExpression<IPolygonalChain2D>(
               new GLengthToFloatExpression<IPolygonalChain2D>(GLength.Meter.value(5)), //
               new GConstantExpression<IPolygonalChain2D, IColor>(GColorF.GRAY), //
               new GConstantExpression<IPolygonalChain2D, Float>(1f));

      return new GPolygonalChain2DSymbolizerExpression(curveStyleExpression);
   }


   private static IExpression<IPolygon2D, GSymbol2DList> createPolygonSymbolizerExpression() {

      final GEmptyExpression<IPolygon2D, Boolean> isArgentinaCondition = new GEmptyExpression<IPolygon2D, Boolean>() {
         @Override
         public Boolean evaluate(final IPolygon2D polygon,
                                 final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                 final IVectorial2DRenderingScaler scaler) {
            final String country = (String) feature.getAttribute(COUNTRY);
            return ((country != null) && country.trim().toLowerCase().equals("argentina"));
         }
      };


      final GConditionalExpression<IPolygon2D, IColor> surfaceColorExpression = new GConditionalExpression<IPolygon2D, IColor>(//
               isArgentinaCondition, //
               new GConstantExpression<IPolygon2D, IColor>(GColorF.newRGB256(204, 224, 143)), //
               new GConstantExpression<IPolygon2D, IColor>(GColorF.GRAY));


      final GCurve2DStyleExpression<IPolygon2D> curveStyleExpression = new GCurve2DStyleExpression<IPolygon2D>(//
               new GConditionalExpression<IPolygon2D, Float>(//
                        isArgentinaCondition, //
                        new GLengthToFloatExpression<IPolygon2D>(GLength.Kilometer.value(2)), //
                        new GLengthToFloatExpression<IPolygon2D>(GLength.Kilometer.value(0.5))), //
               new GTransformerExpression<IPolygon2D, IColor, IColor>(surfaceColorExpression, new IFunction<IColor, IColor>() {
                  @Override
                  public IColor apply(final IColor color) {
                     return color.muchDarker();
                  }
               }), //
               new GConstantExpression<IPolygon2D, Float>(1f));

      final GSurface2DStyleExpression<IPolygon2D> surfaceStyleExpression = new GSurface2DStyleExpression<IPolygon2D>(
               surfaceColorExpression, //
               new GConstantExpression<IPolygon2D, Float>(1f));

      final GPolygon2DSymbolizerExpression polygonSymbolizer = new GPolygon2DSymbolizerExpression(curveStyleExpression,
               surfaceStyleExpression);

      final GConditionalExpression<IPolygon2D, GSymbol2DList> argentinaPolygonsLabeler = new GConditionalExpression<IPolygon2D, GSymbol2DList>(//
               isArgentinaCondition, //
               new GPolygon2DLabelerSymbolizerExpression(PROVINCE), //
               null);

      @SuppressWarnings("unchecked")
      final GCompositeGeometry2DSymbolizer<IPolygon2D> composite = new GCompositeGeometry2DSymbolizer<IPolygon2D>(
               polygonSymbolizer, argentinaPolygonsLabeler);

      return composite;
   }


   private static IExpression<IVector2, GSymbol2DList> createPointSymbolizerExpression() throws IOException {
      final GColorScheme colorScheme = GColorBrewerColorSchemeSet.INSTANCE.getSchemes(9, GColorScheme.Type.Qualitative).get(2);
      final IMeasure<GArea> pointArea = GArea.SquareKilometer.value(100);

      final GFileName symbologyDirectory = GFileName.relative("..", "sample-data", "icons");

      final BufferedImage automotiveIcon = ImageIO.read(GFileName.fromParentAndParts(symbologyDirectory, "automotive-128x128.png").asFile());
      final BufferedImage governmentIcon = ImageIO.read(GFileName.fromParentAndParts(symbologyDirectory, "government-128x128.png").asFile());


      final List<GPair<String, IExpression<IVector2, ? extends GSymbol2DList>>> cases = new ArrayList<GPair<String, IExpression<IVector2, ? extends GSymbol2DList>>>();

      cases.add(new GPair<String, IExpression<IVector2, ? extends GSymbol2DList>>(//
               "tourism", //
               createRectangle2DSymbolizer(CATEGORY, colorScheme, pointArea)));

      cases.add(new GPair<String, IExpression<IVector2, ? extends GSymbol2DList>>(//
               "automotive", //
               createIcon2DSymbolizer(pointArea, automotiveIcon, "automotive")));
      cases.add(new GPair<String, IExpression<IVector2, ? extends GSymbol2DList>>(//
               "government and public services", //
               createIcon2DSymbolizer(pointArea, governmentIcon, "government")));


      return new GSwitchExpression<IVector2, String, GSymbol2DList>(//
               new GEmptyExpression<IVector2, String>() {
                  @Override
                  public String evaluate(final IVector2 geometry,
                                         final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                         final IVectorial2DRenderingScaler scaler) {
                     final String category = (String) feature.getAttribute(CATEGORY);
                     if (category == null) {
                        return null;
                     }
                     return category.trim().toLowerCase();
                  }
               },//
               cases, //
               createOval2DSymbolizer(CATEGORY, colorScheme, pointArea));
   }


   private static IExpression<IVector2, ? extends GSymbol2DList> createIcon2DSymbolizer(final IMeasure<GArea> area,
                                                                                        final BufferedImage icon,
                                                                                        final String iconName) {
      return new GIcon2DSymbolizerExpression(//
               area, //
               new GConstantExpression<IVector2, GPair<String, BufferedImage>>(new GPair<String, BufferedImage>(iconName, icon)), //
               new GConstantExpression<IVector2, Float>(0.75f));
   }


   private static GOval2DSymbolizerExpression<IVector2> createOval2DSymbolizer(final String fieldName,
                                                                               final GColorScheme colorScheme,
                                                                               final IMeasure<GArea> pointArea) {
      final GUniqueValuesColorizer<GAxisAlignedOval2D> surfaceColorExpression = new GUniqueValuesColorizer<GAxisAlignedOval2D>(
               fieldName, colorScheme, GColorI.WHITE, true, new IFunction<Object, String>() {
                  @Override
                  public String apply(final Object element) {
                     if (element == null) {
                        return "";
                     }

                     return element.toString().trim().toLowerCase();
                  }
               });

      final IExpression<GAxisAlignedOval2D, ICurve2DStyle> curveStyleExpression = new GCurve2DStyleExpression<GAxisAlignedOval2D>(
               new GLengthToFloatExpression<GAxisAlignedOval2D>(GLength.Kilometer.value(1)), //
               new GTransformerExpression<GAxisAlignedOval2D, IColor, IColor>(surfaceColorExpression,
                        new IFunction<IColor, IColor>() {
                           @Override
                           public IColor apply(final IColor color) {
                              return color.muchDarker();
                           }
                        }), //
               new GConstantExpression<GAxisAlignedOval2D, Float>(0.75f));

      final IExpression<GAxisAlignedOval2D, ISurface2DStyle> surfaceStyleExpression = new GSurface2DStyleExpression<GAxisAlignedOval2D>(
               surfaceColorExpression, //
               new GConstantExpression<GAxisAlignedOval2D, Float>(0.75f));

      return new GOval2DSymbolizerExpression<IVector2>(//
               new GCreateOval2DExpression(pointArea), //
               curveStyleExpression, //
               surfaceStyleExpression);
   }


   private static GRectangle2DSymbolizerExpression<IVector2> createRectangle2DSymbolizer(final String fieldName,
                                                                                         final GColorScheme colorScheme,
                                                                                         final IMeasure<GArea> pointArea) {
      final GUniqueValuesColorizer<GAxisAlignedRectangle> surfaceColorExpression = new GUniqueValuesColorizer<GAxisAlignedRectangle>(
               fieldName, colorScheme, GColorI.WHITE, true, new IFunction<Object, String>() {
                  @Override
                  public String apply(final Object element) {
                     if (element == null) {
                        return "";
                     }

                     return element.toString().trim().toLowerCase();
                  }
               });

      final IExpression<GAxisAlignedRectangle, ICurve2DStyle> curveStyleExpression = new GCurve2DStyleExpression<GAxisAlignedRectangle>(
               new GLengthToFloatExpression<GAxisAlignedRectangle>(GLength.Kilometer.value(1)), //
               new GTransformerExpression<GAxisAlignedRectangle, IColor, IColor>(surfaceColorExpression,
                        new IFunction<IColor, IColor>() {
                           @Override
                           public IColor apply(final IColor color) {
                              return color.muchDarker();
                           }
                        }), //
               new GConstantExpression<GAxisAlignedRectangle, Float>(0.75f));

      final IExpression<GAxisAlignedRectangle, ISurface2DStyle> surfaceStyleExpression = new GSurface2DStyleExpression<GAxisAlignedRectangle>(
               surfaceColorExpression, //
               new GConstantExpression<GAxisAlignedRectangle, Float>(0.75f));

      return new GRectangle2DSymbolizerExpression<IVector2>(//
               new GCreateRectangle2DExpression(pointArea), //
               curveStyleExpression, //
               surfaceStyleExpression);
   }


   private static BufferedImage getBackgroundImage(final GAxisAlignedRectangle viewport) {
      if (_backgroundImage == null) {
         try {
            _backgroundImage = createBackgroundImage(viewport);
         }
         catch (final IOException e) {
            e.printStackTrace();
         }
      }
      return _backgroundImage;
   }


   private static BufferedImage createBackgroundImage(final GAxisAlignedRectangle viewport) throws IOException {
      final GFileName blueMarbleFileName = GFileName.relative("..", "sample-data", "bitmaps", "oceans.jpg");

      final BufferedImage fullBlueMarble = ImageIO.read(blueMarbleFileName.asFile());
      final GAxisAlignedRectangle blueMarbleBounds = new GAxisAlignedRectangle(new GVector2D(-Math.PI, -Math.PI / 2),
               new GVector2D(Math.PI, Math.PI / 2));
      final GVector2D blueMarbleImageExtent = new GVector2D(fullBlueMarble.getWidth(), fullBlueMarble.getHeight());

      final GAxisAlignedRectangle imageRegion = getRegionOfImage(blueMarbleBounds, blueMarbleImageExtent, viewport);

      final int x = GMath.toRoundedInt(imageRegion._lower.x());
      final int y = GMath.toRoundedInt(imageRegion._lower.y());
      final int w = GMath.toRoundedInt(imageRegion._extent.x());
      final int h = GMath.toRoundedInt(imageRegion._extent.y());

      final BufferedImage result = fullBlueMarble.getSubimage(x, y, w, h);
      System.out.println("- Created background image (" + result.getWidth() + "x" + result.getHeight() + ")");
      return result;
   }


   private static GAxisAlignedRectangle getRegionOfImage(final GAxisAlignedRectangle imageRegion,
                                                         final GVector2D imageExtent,
                                                         final GAxisAlignedRectangle viewPort) {
      final IVector2 imageRegionLower = imageRegion._lower;
      final IVector2 imageRegionExtent = imageRegion._extent;

      final IVector2 regionLower = viewPort._lower.sub(imageRegionLower).div(imageRegionExtent).scale(imageExtent);
      final IVector2 regionUpper = viewPort._upper.sub(imageRegionLower).div(imageRegionExtent).scale(imageExtent);

      final IVector2 regionExtent = regionUpper.sub(regionLower);

      final IVector2 regionLowerFlipped = new GVector2D(regionLower.x(), imageExtent.y() - regionLower.y() - regionExtent.y());

      return new GAxisAlignedRectangle(regionLowerFlipped, regionLowerFlipped.add(regionExtent));
   }


   @Override
   public void preRender(final IVectorI2 renderExtent,
                         final IProjectionTool projectionTool,
                         final GAxisAlignedRectangle viewport,
                         final ISymbolizer2D renderingStyle,
                         final IVectorial2DDrawer drawer) {

      drawer.fillRect(0, 0, renderExtent.x(), renderExtent.y(), GColorF.newRGB256(211, 237, 249).darker().asAWTColor());

      if (_drawBackgroundImage) {
         final BufferedImage backgroundImage = getBackgroundImage(viewport);
         if (backgroundImage != null) {
            drawer.drawImage(backgroundImage, 0, 0, renderExtent.x(), renderExtent.y());
         }
      }

      super.preRender(renderExtent, projectionTool, viewport, renderingStyle, drawer);
   }


}
