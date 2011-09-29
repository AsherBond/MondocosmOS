/*

 IGO Software SL  -  info@igosoftware.es

 http://www.glob3.org

-------------------------------------------------------------------------------
 Copyright (c) 2010, IGO Software SL
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
     * Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.
     * Neither the name of the IGO Software SL nor the
       names of its contributors may be used to endorse or promote products
       derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL IGO Software SL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-------------------------------------------------------------------------------

*/


package es.igosoftware.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;

import es.igosoftware.io.GFileName;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.logging.GLogger;
import es.igosoftware.logging.ILogger;
import es.igosoftware.util.GImageUtils;
import es.igosoftware.util.GProgress;


public class GPanoramicCompiler {

   private static final ILogger LOGGER          = GLogger.instance();
   private static boolean       _forceLastLevel = false;


   private static void logInfo(final String msg) {
      LOGGER.logInfo(msg);
   }


   private static void logWarning(final String msg) {
      LOGGER.logWarning(msg);
   }


   private static void logSevere(final String msg) {
      LOGGER.logSevere(msg);
   }


   public static final int    TILE_WIDTH       = 256;
   public static final int    TILE_HEIGHT      = TILE_WIDTH;

   private static final int   INITIAL_COLUMNS  = 2;
   private static final int   INITIAL_ROWS     = 1;

   private static final int   INITIAL_WIDTH    = TILE_WIDTH * INITIAL_COLUMNS;
   private static final int   INITIAL_HEIGHT   = TILE_HEIGHT * INITIAL_ROWS;

   public static final String LEVELS_FILE_NAME = "levels.gz";


   public static void main(final String[] args) throws IOException {
      System.out.println("Panoramic Compiler 0.1");
      System.out.println("----------------------\n");

      if ((args.length != 2) && (args.length != 3)) {
         logSevere("\tInvalid arguments: SourceImageFileName and OutputDirectoryName are mandatory DEBUGFLAG is optional");
         System.exit(1);
      }

      final GFileName sourceImageFileName = GFileName.fromFile(new File(args[0]));
      final GFileName outputDirectoryName = GFileName.fromFile(new File(args[1]));

      final boolean debug;
      if (args.length == 3) {
         debug = args[2].trim().toLowerCase().equals("debug");
         logInfo("** DEBUG MODE **");
      }
      else {
         debug = false;
      }

      if (!sourceImageFileName.exists()) {
         logSevere("\tSourceImageFileName (" + sourceImageFileName + ") doesn't exist");
         System.exit(1);
      }

      process(sourceImageFileName, outputDirectoryName, debug);
   }


   public static class ZoomLevels
            implements
               Serializable {

      private static final long          serialVersionUID = 1L;

      private final ArrayList<ZoomLevel> _levels          = new ArrayList<ZoomLevel>();
      private final int                  _tileWidth;
      private final int                  _tileHeight;


      private ZoomLevels(final int tileWidth,
                         final int tileHeight) {
         _tileWidth = tileWidth;
         _tileHeight = tileHeight;
      }


      private void addLevel(final ZoomLevel zoomLevel) {
         _levels.add(zoomLevel);
      }


      public List<ZoomLevel> getLevels() {
         return Collections.unmodifiableList(_levels);
      }
   }


   public static class ZoomLevel
            implements
               Serializable {
      private static final long serialVersionUID = 1L;

      private final ZoomLevels  _zoomLevels;
      private final int         _level;
      private final int         _width;
      private final int         _height;


      private ZoomLevel(final ZoomLevels zoomLevels,
                        final int level,
                        final int width,
                        final int height) {
         _zoomLevels = zoomLevels;
         _level = level;
         _width = width;
         _height = height;
      }


      @Override
      public String toString() {
         return "Level #" + _level + ", size=" + _width + "x" + _height + ", tiles=" + getWidthInTiles() + "x"
                + getHeightInTiles() + " (" + getTilesCount() + ")";
      }


      private int getTilesCount() {
         return getWidthInTiles() * getHeightInTiles();
      }


      public int getHeightInTiles() {
         return (_height / _zoomLevels._tileHeight);
      }


      public int getWidthInTiles() {
         return (_width / _zoomLevels._tileWidth);
      }


      public int getLevel() {
         return _level;
      }

   }


   private static ZoomLevels calculateZoomLevels(final BufferedImage image) {

      final int imageWidth = image.getWidth();
      final int imageHeight = image.getHeight();

      int level = 0;
      int levelWidth = INITIAL_WIDTH;
      int levelHeight = INITIAL_HEIGHT;

      final ZoomLevels zoomLevels = new ZoomLevels(TILE_WIDTH, TILE_HEIGHT);
      while ((imageWidth > levelWidth) && (imageHeight > levelHeight)) {
         zoomLevels.addLevel(new ZoomLevel(zoomLevels, level, levelWidth, levelHeight));

         level++;
         levelWidth *= 2;
         levelHeight *= 2;
      }

      if (_forceLastLevel) {
         zoomLevels.addLevel(new ZoomLevel(zoomLevels, level, levelWidth, levelHeight));
      }

      zoomLevels._levels.trimToSize();

      return zoomLevels;
   }


   private static BufferedImage fix(final BufferedImage bi,
                                    final int width,
                                    final int height,
                                    final boolean debug,
                                    final int level,
                                    final int row,
                                    final int column) {
      if ((bi.getWidth() == width) && (bi.getHeight() == height) && !debug) {
         return bi;
      }

      final BufferedImage renderedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

      final Graphics2D g2d = renderedImage.createGraphics();
      g2d.setColor(Color.WHITE);
      g2d.fillRect(0, 0, width, height);
      g2d.drawImage(bi, 0, 0, null);

      if (debug) {
         final String msg = "#" + level + ", cell=" + row + "x" + column;
         g2d.setColor(Color.BLACK);
         g2d.drawString(msg, 16, 16);
         g2d.drawString(msg, 18, 18);
         g2d.setColor(Color.WHITE);
         g2d.drawString(msg, 17, 17);

         g2d.setColor(Color.RED);
         g2d.drawRect(0, 0, width - 1, height - 1);
      }

      g2d.dispose();

      return renderedImage;
   }


   private static void saveZoomLevels(final GFileName outputDirectoryName,
                                      final ZoomLevels zoomLevels) throws IOException {
      logInfo("Generating zoom levels information");


      ObjectOutputStream os = null;
      try {
         final File file = GFileName.fromParentAndParts(outputDirectoryName, LEVELS_FILE_NAME).asFile();
         os = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(file)));

         os.writeObject(zoomLevels);

         os.flush();
      }
      finally {
         GIOUtils.gentlyClose(os);
      }

   }


   public static void process(final GFileName sourceImage,
                              final GFileName outputBaseDirectoryName,
                              final boolean debug) throws IOException {

      final GFileName outputDirectory = GFileName.fromParentAndParts(outputBaseDirectoryName, sourceImage.asFile().getName());

      logInfo("Cleaning directory \"" + outputDirectory + "\"");
      GIOUtils.assureEmptyDirectory(outputDirectory, false);

      logInfo("Reading image \"" + sourceImage + "\"");

      //      final BufferedImage image = ImageIO.read(sourceImage);
      final BufferedImage image = JAI.create("fileload", sourceImage.buildPath()).getAsBufferedImage();

      final int width = image.getWidth();
      final int height = image.getHeight();

      logInfo("Image size=" + width + "x" + height);

      if ((height * 2) != width) {
         logWarning("The width of the image is not two times the height");
      }

      final ZoomLevels zoomLevels = calculateZoomLevels(image);

      logInfo("Found " + zoomLevels._levels.size() + " levels");

      //      LOGGER.logIncreaseIdentationLevel();


      saveZoomLevels(outputDirectory, zoomLevels);

      for (final ZoomLevel level : zoomLevels._levels) {
         processLevel(level, image, outputDirectory, debug);
      }
      //      LOGGER.logDecreaseIdentationLevel();

      logInfo("Done!");
   }


   private static void processLevel(final ZoomLevel level,
                                    final BufferedImage image,
                                    final GFileName outputDirectoryName,
                                    final boolean debug) throws IOException {
      logInfo("Processing " + level);
      //      LOGGER.logIncreaseIdentationLevel();

      final File levelDirectory = new File(outputDirectoryName.buildPath(), level._level + File.separator);
      if (!levelDirectory.mkdirs()) {
         throw new IOException("Can't create directory \"" + levelDirectory.getAbsolutePath() + "\"");
      }

      final Image scaledImage;


      if ((level._width == image.getWidth()) && (level._height == image.getHeight())) {
         logInfo("No need to scale image");
         scaledImage = image;
      }
      else {
         logInfo("Scaling image...");

         scaledImage = image.getScaledInstance(level._width, level._height, Image.SCALE_SMOOTH);
      }

      final BufferedImage scaledRenderedImage = GImageUtils.asBufferedImage(scaledImage, BufferedImage.TYPE_3BYTE_BGR);

      //      logInfo("Saving scaled image...");
      //      final File scaledFile = new File(levelDirectory, "scaled.jpg");
      //      ImageIO.write(scaledRenderedImage, "jpeg", scaledFile);


      final GProgress progress = new GProgress(level.getTilesCount()) {

         @Override
         public void informProgress(final long stepsDone,
                                    final double percent,
                                    final long elapsed,
                                    final long estimatedMsToFinish) {
            logInfo("Saving tiles " + progressString(stepsDone, percent, elapsed, estimatedMsToFinish));
         }
      };


      final int scaleImageWidth = scaledRenderedImage.getWidth();
      final int scaleImageHeight = scaledRenderedImage.getHeight();

      for (int column = 0; column < level.getWidthInTiles(); column++) {
         final int tileX = TILE_WIDTH * column;

         int tileWidth = TILE_WIDTH;
         if ((tileX + tileWidth) > scaleImageWidth) {
            tileWidth += (scaleImageWidth - (tileX + tileWidth));
         }

         for (int row = 0; row < level.getHeightInTiles(); row++) {
            final int tileY = TILE_HEIGHT * row;

            int tileHeight = TILE_HEIGHT;
            if ((tileY + tileHeight) > scaleImageHeight) {
               tileHeight += (scaleImageHeight - (tileY + tileHeight));
            }

            final BufferedImage tileImage = fix(scaledRenderedImage.getSubimage(tileX, tileY, tileWidth, tileHeight), TILE_WIDTH,
                     TILE_HEIGHT, debug, level._level, row, column);

            final File tileFile = new File(levelDirectory, row + "-" + column + ".jpg");
            ImageIO.write(tileImage, "jpeg", tileFile);

            progress.stepDone();
         }
      }


      //      LOGGER.logDecreaseIdentationLevel();
   }


}
