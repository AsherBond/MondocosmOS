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


package es.igosoftware.globe.layers;

import es.igosoftware.globe.IGlobeRasterLayer;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.IGlobeSymbolizer;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.SurfaceImage;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.List;

import javax.swing.Icon;


public class GGlobeRasterLayer
         extends
            RenderableLayer
         implements
            IGlobeRasterLayer {

   final private SurfaceImage    _surfaceImage;
   final private GRasterRenderer _renderer;
   final private WritableRaster  _raster;
   final private GRasterGeodata  _extent;

   private double                _noDataValue = -99999d;


   public GGlobeRasterLayer(final Object imageSource,
                            final GRasterGeodata extent) {

      super();

      _extent = extent;

      if (imageSource instanceof WritableRaster) {
         _raster = (WritableRaster) imageSource;
         _renderer = new GRasterRenderer(this);
         _renderer.setColoringMethod(GRasterRenderer.COLORING_METHOD_COLOR_RAMP);
         final BufferedImage img = _renderer.getImage();
         _surfaceImage = new SurfaceImage(img, extent.getAsSector());
         addRenderable(_surfaceImage);
      }
      else if (imageSource instanceof BufferedImage) {
         final BufferedImage img = (BufferedImage) imageSource;
         _raster = (WritableRaster) img.getData();
         _renderer = new GRasterRenderer(this);
         _renderer.setColoringMethod(GRasterRenderer.COLORING_METHOD_RGB);
         _surfaceImage = new SurfaceImage(imageSource, extent.getAsSector());
         addRenderable(_surfaceImage);
      }
      else {
         throw new RuntimeException("Image source not supported: " + imageSource);
      }
   }


   @Override
   public Sector getExtent() {
      return _surfaceImage.getSector();
   }


   public GRasterRenderer getRenderer() {
      return _renderer;
   }


   @Override
   public void redraw() {
      final BufferedImage img = _renderer.getImage();
      if (img != null) {
         _surfaceImage.setImageSource(img, _surfaceImage.getSector());
      }
   }


   @Override
   public double getNoDataValue() {
      return _noDataValue;
   }


   public void setNoDataValue(final double noDataValue) {
      _noDataValue = noDataValue;
   }


   @Override
   public WritableRaster getRaster() {
      return _raster;
   }


   @Override
   public GRasterGeodata getRasterGeodata() {
      return _extent;
   }


   @Override
   public Icon getIcon(final IGlobeRunningContext context) {
      return null;
   }


   //   @Override
   //   public GProjection getProjection() {
   //      return _extent._projection;
   //   }


   @Override
   public List<ILayerAttribute<?>> getLayerAttributes(final IGlobeRunningContext context) {
      return null;
   }


   @Override
   public void doDefaultAction(final IGlobeRunningContext context) {
      context.getCameraController().animatedZoomToSector(getExtent());
   }


   @Override
   public List<? extends ILayerAction> getLayerActions(final IGlobeRunningContext context) {
      return null;
   }


   @Override
   public IGlobeSymbolizer getSymbolizer() {
      // TODO Auto-generated method stub
      return null;
   }

}
