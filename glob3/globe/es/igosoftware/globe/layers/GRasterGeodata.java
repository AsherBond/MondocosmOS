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

import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Sector;


public class GRasterGeodata {

   public final double _cellsize;
   public final double _yllcorner;
   public final double _xllcorner;
   public final int    _rows;
   public final int    _cols;
   public GProjection  _projection;


   public GRasterGeodata(final double xllcorner,
                         final double yllcorner,
                         final double cellsize,
                         final int rows,
                         final int cols,
                         final GProjection projection) {
      _xllcorner = xllcorner;
      _yllcorner = yllcorner;
      _cellsize = cellsize;
      _cols = cols;
      _rows = rows;
      _projection = projection;
   }


   public Sector getAsSector() {

      final IVector2 min = _projection.transformPoint(GProjection.EPSG_4326, new GVector2D(_xllcorner, _yllcorner));
      final IVector2 max = _projection.transformPoint(GProjection.EPSG_4326, new GVector2D(_xllcorner + _cols * _cellsize,
               _yllcorner + _rows * _cellsize));

      final Sector sector = new Sector(Angle.fromRadians(min.y()), Angle.fromRadians(max.y()), Angle.fromRadians(min.x()),
               Angle.fromRadians(max.x()));

      return sector;

   }

}
