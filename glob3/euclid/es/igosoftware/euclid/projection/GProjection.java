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


package es.igosoftware.euclid.projection;

import java.util.ArrayList;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.DoubleByReference;

import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.GVector2F;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.GVector3F;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;


public enum GProjection {

   EUCLID(null),
   EPSG_23029("+proj=utm +zone=29 +ellps=intl +units=m +no_defs ", 23029),
   EPSG_23030("+proj=utm +zone=30 +ellps=intl +units=m +no_defs ", 23030),
   EPSG_4326("+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs ", 4326);

   private static final int  NO_EPSG_CODE = -1;

   private final String      _proj4Definition;


   private transient Pointer _proj4Pointer;
   private int               _epsgCode;


   private GProjection(final String proj4Definition,
                       final int epsgCode) {
      _proj4Definition = proj4Definition;
      _epsgCode = epsgCode;
   }


   private GProjection(final String proj4Definition) {
      _proj4Definition = proj4Definition;
      _epsgCode = NO_EPSG_CODE;
   }


   public static GProjection get(final int epsgCode) {
      for (final GProjection proj : GProjection.values()) {
         if (proj._epsgCode == epsgCode) {
            return proj;
         }
      }

      return null;
   }


   public static int getEPSGCode(final GProjection proj) {

      if (proj._epsgCode != NO_EPSG_CODE) {
         return proj._epsgCode;
      }

      return -1;
   }


   public static GProjection[] getEPSGProjections() {

      final GProjection[] projs = values();
      final ArrayList<GProjection> list = new ArrayList<GProjection>();
      for (final GProjection proj : projs) {
         if (proj.isEPSG()) {
            list.add(proj);
         }
      }

      return list.toArray(new GProjection[0]);

   }


   private boolean isEPSG() {

      return _epsgCode != NO_EPSG_CODE;

   }


   public synchronized Pointer getProj4Pointer() {
      if (_proj4Definition == null) {
         return null;
      }

      if (_proj4Pointer == null) {
         _proj4Pointer = GProj4Library.pj_init_plus(_proj4Definition);
      }

      return _proj4Pointer;
   }


   public IVector3 transformPoint(final GProjection targetProjection,
                                  final IVector3 point) {
      if (this == targetProjection) {
         return point;
      }


      if (_proj4Definition == null) {
         throw new IllegalArgumentException("The receiver doesn't contains a Proj4 definition");
      }

      if (targetProjection._proj4Definition == null) {
         throw new IllegalArgumentException("Destination doesn't contains a Proj4 definition");
      }


      //      final double[] xB = { point.x() };
      //      final double[] yB = { point.y() };
      //      final double[] zB = { point.z() };
      final DoubleByReference xB = new DoubleByReference(point.x());
      final DoubleByReference yB = new DoubleByReference(point.y());
      final DoubleByReference zB = new DoubleByReference(point.z());

      final Pointer src = getProj4Pointer();
      final Pointer dst = targetProjection.getProj4Pointer();
      final int errorCode = GProj4Library.pj_transform(src, dst, 1, 0, xB, yB, zB);
      if (errorCode != 0) {
         throw new RuntimeException("GProj4Library.pj_transform() errorCode=" + errorCode + " \""
                                    + GProj4Library.pj_strerrno(errorCode) + "\", point=" + point + ", source=" + this
                                    + ", destination=" + targetProjection);
      }

      if (point instanceof GVector3D) {
         //         return new GVector3D(xB[0], yB[0], zB[0]);
         return new GVector3D(xB.getValue(), yB.getValue(), zB.getValue());
      }
      else if (point instanceof GVector3F) {
         //         return new GVector3F((float) xB[0], (float) yB[0], (float) zB[0]);
         return new GVector3F((float) xB.getValue(), (float) yB.getValue(), (float) zB.getValue());
      }
      else {
         throw new IllegalArgumentException("Unsupported point type " + point.getClass());
      }
   }


   public IVector2 transformPoint(final GProjection targetProjection,
                                  final IVector2 point) {
      if (this == targetProjection) {
         return point;
      }


      if (_proj4Definition == null) {
         throw new IllegalArgumentException("The receiver doesn't contains a Proj4 definition");
      }

      if (targetProjection._proj4Definition == null) {
         throw new IllegalArgumentException("Destination doesn't contains a Proj4 definition");
      }


      //      final double[] xB = { point.x() };
      //      final double[] yB = { point.y() };
      final DoubleByReference xB = new DoubleByReference(point.x());
      final DoubleByReference yB = new DoubleByReference(point.y());


      final Pointer src = getProj4Pointer();
      final Pointer dst = targetProjection.getProj4Pointer();
      final int errorCode = GProj4Library.pj_transform(src, dst, 1, 0, xB, yB, null);
      if (errorCode != 0) {
         throw new RuntimeException("GProj4Library.pj_transform() errorCode=" + errorCode + " \""
                                    + GProj4Library.pj_strerrno(errorCode) + "\", point=" + point + ", source=" + this
                                    + ", destination=" + targetProjection);
      }

      if (point instanceof GVector2D) {
         //         return new GVector2D(xB[0], yB[0]);
         return new GVector2D(xB.getValue(), yB.getValue());
      }
      else if (point instanceof GVector2F) {
         //         return new GVector2F((float) xB[0], (float) yB[0]);
         return new GVector2F((float) xB.getValue(), (float) yB.getValue());
      }
      else {
         throw new IllegalArgumentException("Unsupported point type " + point.getClass());
      }
   }


   public boolean isGeodesic() {
      if (_proj4Definition == null) {
         throw new IllegalArgumentException("The receiver doesn't contains a Proj4 definition");
      }

      return GProj4Library.pj_is_geocent(getProj4Pointer());
   }


   public boolean isLatLong() {
      if (_proj4Definition == null) {
         throw new IllegalArgumentException("The receiver doesn't contains a Proj4 definition");
      }

      return GProj4Library.pj_is_latlong(getProj4Pointer());
   }


   public static void main(final String[] args) {
      //      final IVector3 point = new GVector3F((float) 689523.09, (float) 4278770.23, 100);
      final IVector3 point = new GVector3D(689523.09, 4278770.23, 100);

      final GProjection sourceProjection = EPSG_23029;
      final GProjection targetProjection = EPSG_4326;
      //final GProjection targetProjection = EPSG_23030;


      System.out.println("point: " + point + " (" + sourceProjection + ")");


      System.out.println();
      final IVector3 reprojected = point.reproject(sourceProjection, targetProjection);
      System.out.println("reprojected: " + reprojected + " (" + targetProjection + ")");
      System.out.println("    lat/lon: " + Math.toDegrees(reprojected.y()) + " lon:" + Math.toDegrees(reprojected.x()));

      System.out.println();
      final IVector3 rereprojected = reprojected.reproject(targetProjection, sourceProjection);
      System.out.println("re-reprojected: " + rereprojected);
      System.out.println("         delta: " + rereprojected.sub(point));
   }


}
