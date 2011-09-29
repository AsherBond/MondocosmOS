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


package es.igosoftware.globe.modules.view;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.globe.IGlobeRasterLayer;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.IGlobeVectorLayer;
import es.igosoftware.globe.layers.GRasterGeodata;
import es.igosoftware.util.GPair;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;


public class GInfoToolListener
         implements
            MouseListener {

   private final IGlobeRunningContext _context;


   //   private final WorldWindowGLCanvas  _ww;


   public GInfoToolListener(final IGlobeRunningContext context) {
      _context = context;
      //      _ww = _context.getWorldWindModel().getWorldWindowGLCanvas();
   }


   @Override
   public void mouseClicked(final MouseEvent e) {
      if (e.getClickCount() == 1) {
         final GPointInfo[] info = getInfo(_context.getWorldWindModel().getWorldWindowGLCanvas().getCurrentPosition());
         if (info.length != 0) {
            showInfo(info);
         }
      }
      e.consume();
   }


   @Override
   public void mouseEntered(final MouseEvent e) {
      e.consume();
   }


   @Override
   public void mouseExited(final MouseEvent e) {
      e.consume();
   }


   @Override
   public void mousePressed(final MouseEvent e) {
      e.consume();
   }


   @Override
   public void mouseReleased(final MouseEvent e) {
      e.consume();
   }


   private void showInfo(final GPointInfo[] info) {
      GInfoToolDialog dialog = GInfoToolDialog.getCurrentInfoDialog();
      if (dialog == null) {
         dialog = new GInfoToolDialog(_context.getApplication().getFrame(), info);
         dialog.setVisible(true);
      }
      else {
         dialog.updateInfo(info);
      }
   }


   @SuppressWarnings("unchecked")
   private GPointInfo[] getInfo(final Position currentPosition) {

      final ArrayList<GPointInfo> info = new ArrayList<GPointInfo>();

      final double elevation = _context.getWorldWindModel().getGlobe().getElevation(currentPosition.latitude,
               currentPosition.longitude);

      GPointInfo pinfo = new GPointInfo("Terrain model", new GPair[] {
         new GPair<String, Object>("Elevation", Double.valueOf(elevation))
      });
      info.add(pinfo);
      final LayerList layers = _context.getWorldWindModel().getLayerList();
      for (int i = 0; i < layers.size(); i++) {
         final Layer layer = layers.get(i);
         if (layer instanceof IGlobeRasterLayer) {
            final IGlobeRasterLayer gRasterLayer = (IGlobeRasterLayer) layer;
            final Sector extent = gRasterLayer.getExtent();
            if (extent.contains(currentPosition)) {
               final GRasterGeodata geodata = gRasterLayer.getRasterGeodata();
               final IVector2 transformedPt = GProjection.EPSG_4326.transformPoint(geodata._projection, new GVector2D(
                        currentPosition.longitude.degrees, currentPosition.latitude.degrees));
               final double dX = transformedPt.x();
               final double dY = transformedPt.y();
               final int iCol = (int) ((dX - geodata._xllcorner) / geodata._cellsize);
               final int iRow = (int) ((dY - geodata._yllcorner) / geodata._cellsize);
               final WritableRaster raster = gRasterLayer.getRaster();
               final GPair<String, Object>[] values = new GPair[raster.getNumBands()];
               for (int iBand = 0; iBand < values.length; iBand++) {
                  final Double value = new Double(raster.getSampleDouble(iCol, iRow, iBand));
                  values[iBand] = new GPair<String, Object>("Band " + Integer.toString(iBand + 1), value);
               }
               pinfo = new GPointInfo(gRasterLayer.getName(), values);
               info.add(pinfo);
            }
         }
         else if (layer instanceof IGlobeVectorLayer) {

         }

      }
      return info.toArray(new GPointInfo[0]);
   }
}
