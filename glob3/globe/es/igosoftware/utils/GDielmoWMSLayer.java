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

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.util.Level;
import gov.nasa.worldwind.util.Tile;
import gov.nasa.worldwind.util.TileUrlBuilder;
import gov.nasa.worldwind.wms.WMSTiledImageLayer;

import java.net.MalformedURLException;
import java.net.URL;


public class GDielmoWMSLayer
         extends
            WMSTiledImageLayer {


   public static enum ImageFormat {
      JPEG(".jpg", "image/jpeg"),
      PNG(".png", "image/png");

      private final String _extension;
      private final String _format;


      private ImageFormat(final String extension,
                          final String format) {
         _extension = extension;
         _format = format;
      }


      public String getExtension() {
         return _extension;
      }


      public String getFormat() {
         return _format;
      }
   }


   public GDielmoWMSLayer(final GDielmoWMSLayer.ImageFormat imageFormat) {
      this(imageFormat, 50000);
   }


   public GDielmoWMSLayer(final GDielmoWMSLayer.ImageFormat imageFormat,
                          final double maxActiveAltitude) {
      super(makeParams(imageFormat));

      setMaxActiveAltitude(maxActiveAltitude);
   }


   private static AVList makeParams(final GDielmoWMSLayer.ImageFormat imageFormat) {
      final AVList params = new AVListImpl();

      params.setValue(AVKey.TILE_WIDTH, 512);
      params.setValue(AVKey.TILE_HEIGHT, 512);
      params.setValue(AVKey.DATA_CACHE_NAME, "Earth/DIELMO");
      params.setValue(AVKey.SERVICE, " http://server.dielmo.com:8080/LidarTerrestreWMSServer/WMSLiDAR");
      params.setValue(AVKey.DATASET_NAME, "DIELMO LIDAR WMS Online");
      params.setValue(AVKey.FORMAT_SUFFIX, imageFormat.getExtension());


      //    LidarTerrestreWMSServer_Infoterra/WMSLiDARREQUEST=GetFeatureInfo&SERVICE=WMS&QUERY_LAYERS=Lidar_infoterra_Mobile_aerial&VERSION=1.1.1&INFO_FORMAT=text/html&LAYERS=Lidar_infoterra_Mobile_aerial&SRS=EPSG:27700&BBOX=441183,291759,452577,297456&WIDTH=512&HEIGHT=256&FORMAT=image/png&STYLES=height&TRANSPARENT=FALSE&x=287&y=68&FEATURE_COUNT=10000&EXCEPTIONS=XML
      params.setValue(AVKey.NUM_LEVELS, 16);
      params.setValue(AVKey.NUM_EMPTY_LEVELS, 5);
      params.setValue(AVKey.LEVEL_ZERO_TILE_DELTA, new LatLon(Angle.fromDegrees(36), Angle.fromDegrees(36)));
      params.setValue(AVKey.SECTOR, Sector.FULL_SPHERE);

      params.setValue(AVKey.TILE_URL_BUILDER, new GDielmoWMSLayer.URLBuilder(imageFormat));

      return params;
   }


   private static class URLBuilder
            implements
               TileUrlBuilder {


      private final GDielmoWMSLayer.ImageFormat _imageFormat;


      private URLBuilder(final ImageFormat imageFormat) {
         _imageFormat = imageFormat;
      }


      @Override
      public URL getURL(final Tile tile,
                        final String altImageFormat) throws MalformedURLException {

         //         http://www.idee.es/wms/PNOA/PNOA?REQUEST=GetMap&VERSION=1.1.1&SERVICE=WMS&SRS=EPSG:25830&BBOX=621273.70693,4415651.72439,624119.18488,4418025.77891&WIDTH=1008&HEIGHT=841&LAYERS=pnoa&STYLES=default&FORMAT=image/jpeg

         final Level level = tile.getLevel();

         final StringBuffer sb = new StringBuffer(level.getService());
         if (sb.lastIndexOf("?") != sb.length() - 1) {
            sb.append("?");
         }

         sb.append("REQUEST=GetMap");
         sb.append("&VERSION=1.1.1");
         sb.append("&SERVICE=WMS");
         sb.append("&SRS=EPSG:4326");

         final Sector sector = tile.getSector();
         sb.append("&BBOX=");
         sb.append(sector.getMinLongitude().getDegrees());
         sb.append(",");
         sb.append(sector.getMinLatitude().getDegrees());
         sb.append(",");
         sb.append(sector.getMaxLongitude().getDegrees());
         sb.append(",");
         sb.append(sector.getMaxLatitude().getDegrees());

         sb.append("&WIDTH=");
         sb.append(level.getTileWidth());
         sb.append("&HEIGHT=");
         sb.append(level.getTileHeight());

         sb.append("&LAYERS=M607-M30_25830");
         sb.append("&STYLES=height");
         //         sb.append("&FORMAT=image/jpeg");
         sb.append("&FORMAT=");
         sb.append(_imageFormat.getFormat());
         sb.append("&TRANSPARENT=TRUE");

         //            System.out.println(sb);

         return new URL(sb.toString());
      }
   }


   @Override
   public String toString() {
      return "DIELMO WMS Online";
   }

}
