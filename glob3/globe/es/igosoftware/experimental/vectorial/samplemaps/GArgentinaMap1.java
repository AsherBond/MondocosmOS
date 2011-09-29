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


package es.igosoftware.experimental.vectorial.samplemaps;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.GVectorial2DRenderer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.GJava2DVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IProjectionTool;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.ISymbolizer2D;
import es.igosoftware.euclid.features.GCompositeFeatureCollection;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector2I;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorI2;
import es.igosoftware.experimental.vectorial.GShapeLoader;
import es.igosoftware.io.GFileName;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.util.GStringUtils;
import es.igosoftware.util.GUtils;
import es.igosoftware.utils.GWWUtils;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.globes.Globe;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;


public class GArgentinaMap1 {
   private static final Globe EARTH = new Earth();


   public static void main(final String[] args) throws IOException {
      System.out.println("Argentina Map 1");
      System.out.println("---------------\n");

      final long start = System.currentTimeMillis();

      final GProjection projection = GProjection.EPSG_4326;

      final GFileName shapeDirectory = GFileName.relative("..", "sample-data", "shp");

      final GFileName pointsFileName = GFileName.fromParentAndParts(shapeDirectory, "argentina",
               "americas_south_america_argentina_poi.shp");

      final GFileName surfacesFileName = GFileName.fromParentAndParts(shapeDirectory, "world",
               "10m_admin_1_states_provinces_shp.shp");

      final GFileName linesFileName = GFileName.fromParentAndParts(shapeDirectory, "argentina",
               "americas_south_america_argentina_highway.shp");


      final IGlobeFeatureCollection<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> pointsFeatures = GShapeLoader.readFeatures(
               pointsFileName, projection);

      final IGlobeFeatureCollection<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> surfacesFeatures = GShapeLoader.readFeatures(
               surfacesFileName, projection);

      final IGlobeFeatureCollection<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> linesFeatures = GShapeLoader.readFeatures(
               linesFileName, projection);

      @SuppressWarnings("unchecked")
      final IGlobeFeatureCollection<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> compositeFeatures = new GCompositeFeatureCollection<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>(
               surfacesFeatures, linesFeatures, pointsFeatures);


      final GAxisAlignedRectangle viewport = pointsFeatures.getBounds().asRectangle().expandedByPercent(0.05);


      final GFileName directoryName = GFileName.relative("temporary-data", "render");
      final boolean renderLODIgnores = true;
      final double lodMinSize = 4;
      final int textureDimension = 256;
      final boolean debugRendering = false;
      final boolean drawBackgroundImage = true;
      final boolean clusterSymbols = true;

      final IVectorI2 imageExtent = calculateImageExtent(textureDimension, viewport);

      final GVectorial2DRenderer renderer = new GVectorial2DRenderer(compositeFeatures, true);
      final ISymbolizer2D symbolizer = new GArgentinaMap1Symbolizer(debugRendering, lodMinSize, renderLODIgnores, clusterSymbols,
               drawBackgroundImage);

      GIOUtils.assureEmptyDirectory(directoryName, false);

      System.out.println();

      final boolean profile = false;
      if (profile) {
         System.out.println();
         System.out.println(" CONNECT PROFILER ");
         System.out.println();
         GUtils.delay(30000);
         System.out.println("- Running... ");
      }

      final int depth = 0;
      final int maxDepth = 0;
      render(renderer, symbolizer, viewport, imageExtent, directoryName, depth, maxDepth);

      System.out.println();
      System.out.println("- done in " + GStringUtils.getTimeMessage(System.currentTimeMillis() - start, false));
   }


   private static IVectorI2 calculateImageExtent(final int textureDimension,
                                                 final GAxisAlignedRectangle viewport) {
      final IVector2 viewportExtent = viewport.getExtent();

      final int imageWidth;
      final int imageHeight;
      if (viewportExtent.x() > viewportExtent.y()) {
         imageHeight = textureDimension;
         imageWidth = (int) Math.round(viewportExtent.x() / viewportExtent.y() * textureDimension);
      }
      else {
         imageWidth = textureDimension;
         imageHeight = (int) Math.round(viewportExtent.y() / viewportExtent.x() * textureDimension);
      }

      return new GVector2I(imageWidth, imageHeight);
   }


   private static void render(final GVectorial2DRenderer renderer,
                              final ISymbolizer2D symbolizer,
                              final GAxisAlignedRectangle viewport,
                              final IVectorI2 renderExtent,
                              final GFileName directoryName,
                              final int depth,
                              final int maxDepth) throws IOException {

      final long start = System.currentTimeMillis();

      final BufferedImage image = new BufferedImage(renderExtent.x(), renderExtent.y(), BufferedImage.TYPE_4BYTE_ABGR);
      image.setAccelerationPriority(1);

      final IVectorial2DDrawer drawer = new GJava2DVectorial2DDrawer(image);

      final IProjectionTool projectionTool = new IProjectionTool() {
         @Override
         public IVector2 increment(final IVector2 position,
                                   final GProjection projection,
                                   final double deltaEasting,
                                   final double deltaNorthing) {
            return GWWUtils.increment(position, projection, deltaEasting, deltaNorthing, EARTH);
         }
      };

      renderer.render(viewport, renderExtent, projectionTool, symbolizer, drawer);

      final String imageName = "" + depth;
      final GFileName fileName = GFileName.fromParentAndParts(directoryName, imageName + ".png");
      ImageIO.write(image, "png", fileName.asFile());

      System.out.println("- Rendered \"" + imageName + ".png\" (" + renderExtent.x() + "x" + renderExtent.y() + ") in "
                         + GStringUtils.getTimeMessage(System.currentTimeMillis() - start, false));

      if (depth < maxDepth) {
         render(renderer, symbolizer, viewport, renderExtent.scale(2), directoryName, depth + 1, maxDepth);
      }
   }


}
