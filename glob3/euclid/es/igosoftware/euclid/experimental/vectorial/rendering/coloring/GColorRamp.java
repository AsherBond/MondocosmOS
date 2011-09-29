

package es.igosoftware.euclid.experimental.vectorial.rendering.coloring;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import es.igosoftware.euclid.colors.GColorF;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.io.GFileName;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GMath;


public class GColorRamp {


   public static GColorRamp fromImage(final GFileName fileName) throws IOException {
      final BufferedImage image = ImageIO.read(fileName.asFile());

      if (image.getHeight() != 1) {
         System.out.println("Warning, invalid ramp image format.");
      }

      final int colorsCount = image.getWidth();

      final IColor[] colors = new IColor[colorsCount];
      for (int i = 0; i < colorsCount; i++) {
         final int rgb = image.getRGB(i, 0);

         colors[i] = GColorF.fromAWTColor(new Color(rgb));
      }

      return new GColorRamp(colors);
   }


   private final IColor[] _colors;
   private final float[]  _steps;


   public GColorRamp(final IColor... colors) {
      this(colors, createDefaultSteps(colors));
   }


   private static float[] createDefaultSteps(final IColor... colors) {
      final float[] result = new float[colors.length];
      final float step = 1f / (colors.length - 1);
      for (int i = 0; i < colors.length; i++) {
         result[i] = step * i;
      }
      return result;
   }


   public GColorRamp(final IColor[] colors,
                     final float[] steps) {
      GAssert.notEmpty(colors, "colors");
      GAssert.notEmpty(steps, "steps");
      GAssert.isTrue(colors.length == steps.length, "colors.length == steps.length");

      _colors = Arrays.copyOf(colors, colors.length);
      _steps = Arrays.copyOf(steps, steps.length);
   }


   @Override
   public String toString() {
      return "GColorRamp [colors=" + Arrays.toString(_colors) + ", steps=" + Arrays.toString(_steps) + "]";
   }


   public IColor getColor(final float alpha) {
      if (_colors.length == 1) {
         return _colors[0];
      }

      if (alpha <= 0) {
         return _colors[0];
      }
      if (alpha >= 1) {
         return _colors[_colors.length - 1];
      }

      int baseColorIndex = -1;
      float baseStep = 0;
      //      for (int i = 0; i < _steps.length; i++) {
      for (int i = _steps.length - 1; i >= 0; i--) {
         final float step = _steps[i];
         if (alpha <= step) {
            baseStep = step;
            baseColorIndex = i;
         }
      }
      final float deltaStep = baseStep - alpha;

      if (GMath.closeToZero(deltaStep)) {
         return _colors[baseColorIndex];
      }

      final float localAlpha = 1 - (deltaStep * (_colors.length - 1));
      //      System.out.println("alpha=" + alpha + ", baseStep=" + baseStep + ", deltaStep=" + deltaStep + "  --->" + localAlpha);

      return _colors[baseColorIndex - 1].mixedWidth(_colors[baseColorIndex], localAlpha);
   }


   //   public static GColorRamp fromAWTLinealGradient(final LinearGradientPaint gradient) {
   //
   //      //-- TODO fpg
   //      //final Color[] colorsList = gradient.getColors();
   //
   //
   //      return null;
   //   }


   //   public static LinearGradientPaint asAWTLinealGradient(final GColorRamp ramp) {
   //
   //      //-- TODO fpg
   //      //return new LinearGradientPaint(null, null, null, null);
   //
   //
   //      return null;
   //   }


   public static void main(final String[] args) throws IOException {
      System.out.println("GColorRamp 0.1");
      System.out.println("--------------\n");

      //      System.out.println(GColorF.GREEN.muchLighter());
      //      System.out.println(GColorF.GREEN.muchDarker());
      //      System.out.println();


      final GColorRamp ramp = new GColorRamp( //
               GColorF.CYAN, //
               GColorF.YELLOW, //
               GColorF.RED //
      );

      //      final GColorRamp ramp = GColorRamp.fromImage(GFileName.fromFile(new File(
      //               "/home/dgd/Escritorio/GLOB3-Repository/glob3/globe-weather-aemet/data/temperature-ramp.png")));

      //      GColorF.newRGB256(194, 0, 0), //
      //      GColorF.newRGB256(97, 0, 0) //

      //      final GColorRamp ramp = new GColorRamp(GColorF.RED.wheel(16));
      //      final GColorRamp ramp = new GColorRamp(GColorF.BLACK, GColorF.WHITE);
      //            final GColorRamp ramp = new GColorRamp(GColorF.BLUE, GColorF.CYAN, GColorF.GREEN, GColorF.YELLOW, GColorF.RED);
      //      final GColorRamp ramp = new GColorRamp(GColorF.GREEN.muchDarker(), GColorF.GREEN, GColorF.GREEN.muchLighter());
      System.out.println(ramp);

      //      for (float alpha = 0.4f; alpha <= 0.6f; alpha += 0.01f) {
      for (float alpha = 0; alpha <= 1; alpha += 0.1f) {
         final IColor color = ramp.getColor(alpha);
         System.out.println("   " + alpha + " -> " + color);
      }

      final BufferedImage image = new BufferedImage(1280, 512, BufferedImage.TYPE_4BYTE_ABGR);
      final Graphics2D g2d = image.createGraphics();

      for (int row = 0; row < image.getWidth(); row++) {
         final float alpha = (float) row / image.getWidth();
         //         System.out.println(alpha);
         final IColor color = ramp.getColor(alpha);
         g2d.setColor(color.asAWTColor());
         //         g2d.drawLine(0, row, image.getWidth(), row);
         g2d.drawLine(row, 0, row, image.getHeight());
      }

      g2d.dispose();

      ImageIO.write(image, "png", new File("/home/dgd/Escritorio/ramp.png"));
   }


}
