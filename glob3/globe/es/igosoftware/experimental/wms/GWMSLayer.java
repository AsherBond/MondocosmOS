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


package es.igosoftware.experimental.wms;

import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.IGlobeSymbolizer;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.io.GFileName;
import es.igosoftware.io.GIOUtils;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.wms.WMSTiledImageLayer;

import java.io.File;
import java.util.List;

import javax.swing.Icon;


public class GWMSLayer
         extends
            WMSTiledImageLayer
         implements
            IGlobeLayer {

   private final Sector             _sector;

   private final static GProjection PROJECTION          = GProjection.EPSG_4326;
   private final static double      MAX_ACTIVE_ALTITUDE = 1000000;              //from 1000 Km


   //private final WMSLayerCapabilities _layerCapabilities;

   public GWMSLayer(final WMSCapabilities caps,
                    final StringBuffer layerNames,
                    final StringBuffer styleNames,
                    final String name,
                    final GImageFormat imageFormat) {
      this(caps, layerNames, styleNames, imageFormat, name, MAX_ACTIVE_ALTITUDE);
   }


   public GWMSLayer(final WMSCapabilities caps,
                    final StringBuffer layerNames,
                    final StringBuffer styleNames,
                    final GImageFormat imageFormat) {
      this(caps, layerNames, styleNames, imageFormat, null, MAX_ACTIVE_ALTITUDE);
   }


   public GWMSLayer(final WMSCapabilities caps,
                    final StringBuffer layerNames,
                    final GImageFormat imageFormat) {
      this(caps, layerNames, null, imageFormat, null, MAX_ACTIVE_ALTITUDE);
   }


   public GWMSLayer(final WMSCapabilities caps,
                    final StringBuffer layerNames) {
      this(caps, layerNames, null, GImageFormat.PNG, null, MAX_ACTIVE_ALTITUDE);
   }


   public GWMSLayer(final WMSCapabilities caps,
                    final StringBuffer layerNames,
                    final StringBuffer styleNames,
                    final GImageFormat imageFormat,
                    final String name,
                    final double maxActiveAltitude) {

      //super(makeParams(imageFormat));
      super(makeParams(caps, layerNames, styleNames, name, imageFormat));

      setMaxActiveAltitude(maxActiveAltitude);

      //_sector = Sector.FULL_SPHERE;
      _sector = caps.getNamedLayers().get(0).getGeographicBoundingBox();
      //_projection = GProjection.EPSG_4326;
      //_layerCapabilities = caps.getNamedLayers().get(0);
   }


   private static AVList makeParams(final WMSCapabilities caps,
                                    final StringBuffer layerNames,
                                    final StringBuffer styleNames,
                                    final String name,
                                    final GImageFormat imageFormat) {

      final AVList initParams = new AVListImpl();

      //final String serviceTitle = caps.getServiceInformation().getServiceTitle();
      final String serviceName = GIOUtils.buildPath(true, caps.getServiceInformation().getServiceName());
      //final String layerName = caps.getNamedLayers().get(0).getName();
      //final String dataSetName = serviceName + ": " + caps.getVersion();
      final String dataSetName = serviceName + "_" + caps.getVersion();

      initParams.setValue(AVKey.LAYER_NAMES, layerNames.toString());
      if ((styleNames != null) && (styleNames.length() > 0)) {
         initParams.setValue(AVKey.STYLE_NAMES, styleNames.toString());
      }

      final AVList params = WMSTiledImageLayer.wmsGetParamsFromCapsDoc(caps, initParams);

      params.setValue(AVKey.TILE_WIDTH, 512);
      params.setValue(AVKey.TILE_HEIGHT, 512);
      //params.setValue(AVKey.DATA_CACHE_NAME, "Earth/" + serviceName);
      params.setValue(AVKey.DATA_CACHE_NAME, "Earth" + File.separator + serviceName);
      params.setValue(AVKey.DATASET_NAME, dataSetName);
      params.setValue(AVKey.FORMAT_SUFFIX, imageFormat.getExtension());

      params.setValue(AVKey.NUM_LEVELS, 16);
      params.setValue(AVKey.NUM_EMPTY_LEVELS, 5);
      params.setValue(AVKey.LEVEL_ZERO_TILE_DELTA, new LatLon(Angle.fromDegrees(36), Angle.fromDegrees(36)));
      params.setValue(AVKey.SECTOR, Sector.FULL_SPHERE);
      params.setValue(AVKey.ICON_NAME, "earth.png");
      //params.setValue(AVKey.EXPIRY_TIME, 5000);
      params.setValue(AVKey.URL_READ_TIMEOUT, 5000);
      //params.setValue(AVKey.WAKEUP_TIMEOUT, 5000);
      params.setValue(AVKey.PROJECTION_EPSG_CODE, GProjection.getEPSGCode(PROJECTION));

      //      final Object styles = params.getValue(AVKey.STYLE_NAMES);
      //      System.out.println("STYLES: " + styles.toString());

      //      if ((styleNames != null) && (styleNames.length() > 0)) {
      //         params.setValue(AVKey.STYLE_NAMES, styleNames.toString());
      //      }

      if ((name != null) && (name.length() > 0)) {
         params.setValue(AVKey.DISPLAY_NAME, name);
      }

      params.setValue(AVKey.OPACITY, 0.7);

      return params;
   }


   @Override
   public String toString() {
      return "Glob3 WMS Layer";
   }


   @Override
   public Icon getIcon(final IGlobeRunningContext context) {
      return context.getBitmapFactory().getSmallIcon(GFileName.relative("earth.png"));
   }


   @Override
   public Sector getExtent() {
      return _sector;
   }


   //   @Override
   //   public GProjection getProjection() {
   //      return PROJECTION;
   //   }


   @Override
   public void redraw() {
      firePropertyChange(AVKey.LAYER, null, this);
   }


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
      return null;
   }


}
