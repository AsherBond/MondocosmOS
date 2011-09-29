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

import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GMath;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Box;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.DrawContext;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;


public final class GPositionBox {
   public static final GPositionBox                            EMPTY = new GPositionBox(Position.ZERO, Position.ZERO);

   private static final GGlobeStateKeyCache<GPositionBox, Box> EXTENTS_CACHE;

   static {
      EXTENTS_CACHE = new GGlobeStateKeyCache<GPositionBox, Box>(new GGlobeStateKeyCache.Factory<GPositionBox, Box>() {
         @Override
         public Box create(final DrawContext dc,
                           final GPositionBox box) {

            //            return Cylinder.computeVerticalBoundingCylinder(globe, verticalExaggeration, box._sector, box._lower.elevation,
            //                     GMath.nextUp(box._upper.elevation));

            final Globe globe = dc.getGlobe();
            final double verticalExaggeration = dc.getVerticalExaggeration();

            final Position[] vertices = box.getVertices();
            final List<Vec4> points = new ArrayList<Vec4>(vertices.length);
            for (final Position vertex : vertices) {
               points.add(GWWUtils.computePointFromPosition(vertex, globe, verticalExaggeration));
            }
            return Box.computeBoundingBox(points);
         }
      });
   }


   public static GPositionBox merge(final Iterable<GPositionBox> boxes) {
      double minLatitude = Double.POSITIVE_INFINITY;
      double minLongitude = Double.POSITIVE_INFINITY;
      double minElevation = Double.POSITIVE_INFINITY;

      double maxLatitude = Double.NEGATIVE_INFINITY;
      double maxLongitude = Double.NEGATIVE_INFINITY;
      double maxElevation = Double.NEGATIVE_INFINITY;

      for (final GPositionBox box : boxes) {
         final Position currentLower = box._lower;
         final Position currentUpper = box._upper;

         minLatitude = Math.min(minLatitude, currentLower.latitude.radians);
         minLongitude = Math.min(minLongitude, currentLower.longitude.radians);
         minElevation = Math.min(minElevation, currentLower.elevation);

         maxLatitude = Math.max(maxLatitude, currentUpper.latitude.radians);
         maxLongitude = Math.max(maxLongitude, currentUpper.longitude.radians);
         maxElevation = Math.max(maxElevation, currentUpper.elevation);
      }


      if (minLatitude == Double.POSITIVE_INFINITY) {
         return GPositionBox.EMPTY;
      }

      final Position lower = Position.fromRadians(minLatitude, minLongitude, minElevation);
      final Position upper = Position.fromRadians(maxLatitude, maxLongitude, maxElevation);
      return new GPositionBox(lower, upper);
   }


   public final Position    _lower;
   public final Position    _upper;

   private final Position[] _vertices;
   public final Position    _center;
   public final Sector      _sector;


   private final Position[] _bottomQuadVertices;


   public GPositionBox(final GAxisAlignedOrthotope<IVector3, ?> box,
                       final GProjection projection) {
      this(GWWUtils.toPosition(box._lower, projection), GWWUtils.toPosition(box._upper, projection));
   }


   public GPositionBox(final IVector3 lower,
                       final IVector3 upper,
                       final GProjection projection) {
      this(GWWUtils.toPosition(lower, projection), GWWUtils.toPosition(upper, projection));
   }


   public GPositionBox(final Sector sector,
                       final double minElevation,
                       final double maxElevation) {
      _sector = sector;

      final Position lower = new Position(sector.getMinLatitude(), sector.getMinLongitude(), minElevation);
      final Position upper = new Position(sector.getMaxLatitude(), sector.getMaxLongitude(), maxElevation);
      _lower = GWWUtils.min(lower, upper);
      _upper = GWWUtils.max(lower, upper);

      _center = initializeCenter();
      _vertices = initializeVertices();
      _bottomQuadVertices = initializeBottomQuadVertices();
   }


   public GPositionBox(final Position lower,
                       final Position upper) {
      GAssert.notNull(lower, "lower");
      GAssert.notNull(upper, "upper");

      _lower = GWWUtils.min(lower, upper);
      _upper = GWWUtils.max(lower, upper);

      _sector = new Sector(_lower.latitude, _upper.latitude, _lower.longitude, _upper.longitude);

      _center = initializeCenter();
      _vertices = initializeVertices();
      _bottomQuadVertices = initializeBottomQuadVertices();
   }


   private Position[] initializeBottomQuadVertices() {
      return new Position[] {
                        new Position(_lower.latitude, _lower.longitude, _lower.elevation),
                        new Position(_lower.latitude, _upper.longitude, _lower.elevation),
                        new Position(_upper.latitude, _upper.longitude, _lower.elevation),
                        new Position(_upper.latitude, _lower.longitude, _lower.elevation)
      };
   }


   private Position[] initializeVertices() {
      return new Position[] { //
                        new Position(_lower.latitude, _lower.longitude, _lower.elevation),
                        new Position(_lower.latitude, _lower.longitude, _upper.elevation),
                        new Position(_lower.latitude, _upper.longitude, _upper.elevation),
                        new Position(_lower.latitude, _upper.longitude, _lower.elevation),

                        new Position(_upper.latitude, _lower.longitude, _lower.elevation),
                        new Position(_upper.latitude, _lower.longitude, _upper.elevation),
                        new Position(_upper.latitude, _upper.longitude, _upper.elevation),
                        new Position(_upper.latitude, _upper.longitude, _lower.elevation)
      };
   }


   private Position initializeCenter() {
      final Angle centerLatitude = Angle.average(_lower.latitude, _upper.latitude);
      final Angle centerLongitude = Angle.average(_lower.longitude, _upper.longitude);
      final double centerElevation = (_lower.elevation + _upper.elevation) / 2;
      return new Position(centerLatitude, centerLongitude, centerElevation);
   }


   public GPositionBox[] subdivideOverLatitudeAndLongitude() {
      final Angle midLat = Angle.average(_lower.latitude, _upper.latitude);
      final Angle midLon = Angle.average(_lower.longitude, _upper.longitude);

      final double minEle = _lower.elevation;
      final double maxEle = _upper.elevation;

      //      if (upperLatitude < _upper.latitude.radians) {
      //         upperLatitude = GMath.previousDown(upperLatitude);
      //      }
      //      if (upperLongitude < _upper.longitude.radians) {
      //         upperLongitude = GMath.previousDown(upperLongitude);
      //      }
      //      if (upperElevation < _upper.elevation) {
      //         upperElevation = GMath.previousDown(upperElevation);
      //      }

      return new GPositionBox[] {
                        new GPositionBox(new Position(_lower.latitude, midLon, minEle), new Position(midLat, _lower.longitude,
                                 maxEle)),
                        new GPositionBox(new Position(_lower.latitude, _upper.longitude, minEle), new Position(midLat, midLon,
                                 maxEle)),
                        new GPositionBox(new Position(midLat, midLon, minEle), new Position(_upper.latitude, _lower.longitude,
                                 maxEle)),
                        new GPositionBox(new Position(midLat, _upper.longitude, minEle), new Position(_upper.latitude, midLon,
                                 maxEle))
      };
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + _lower.hashCode();
      result = prime * result + _upper.hashCode();
      return result;
   }


   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }

      if (obj == null) {
         return false;
      }

      if (getClass() != obj.getClass()) {
         return false;
      }

      final GPositionBox other = (GPositionBox) obj;

      return _lower.equals(other._lower);
   }


   @Override
   public String toString() {
      //return "GPositionBox [" + _lower + " -> " + _upper + "]";
      return "[" + _lower + " -> " + _upper + "]";
   }


   public Position[] getVertices() {
      return _vertices;
   }


   public Box getExtent(final DrawContext dc) {
      return EXTENTS_CACHE.get(dc, this);
   }


   public void render(final DrawContext dc) {
      final GL gl = dc.getGL();
      GWWUtils.pushOffset(gl);
      GWWUtils.renderQuad(dc, _bottomQuadVertices, 1, 1, 0);
      GWWUtils.popOffset(gl);
      // GWWUtils.renderQuad(dc, _topQuadVertices, 1, 1, 0);
      // GWWUtils.renderQuad(dc, _middleQuadVertices, 1, 1, 0);
   }


   public Position getCenter() {
      return _center;
   }


   public boolean contains(final Position position) {
      return GMath.between(position.latitude.degrees, _lower.latitude.degrees, _upper.latitude.degrees)
             && GMath.between(position.longitude.degrees, _lower.longitude.degrees, _upper.longitude.degrees)
             && GMath.between(position.elevation, _lower.elevation, _upper.elevation);
   }


   public GAxisAlignedBox asAxisAlignedBox() {
      final GVector3D lower = new GVector3D(_lower.longitude.radians, _lower.latitude.radians, _lower.elevation);
      final GVector3D upper = new GVector3D(_upper.longitude.radians, _upper.latitude.radians, _upper.elevation);

      return new GAxisAlignedBox(lower, upper);
   }


   public Sector asSector() {
      return new Sector(_lower.latitude, _upper.latitude, _lower.longitude, _upper.longitude);
   }


}
