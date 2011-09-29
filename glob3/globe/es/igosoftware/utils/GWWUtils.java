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

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;

import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.logging.GLogger;
import es.igosoftware.logging.ILogger;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.IFunction;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Box;
import gov.nasa.worldwind.geom.Cylinder;
import gov.nasa.worldwind.geom.Extent;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Matrix;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.geom.Sphere;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.geom.coords.UTMCoord;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.DrawContext;


public final class GWWUtils {

   private GWWUtils() {
      // private constructor, only public static methods in this class
   }


   private static final ILogger logger                         = GLogger.instance();

   private static final int     DEFAULT_LATITUDE_SUBDIVISIONS  = 5;
   private static final int     DEFAULT_LONGITUDE_SUBDIVISIONS = 10;
   private static final Globe   EARTH                          = new Earth();


   public static void checkGLErrors(final DrawContext dc) {
      final GL gl = dc.getGL();

      for (int err = gl.glGetError(); err != GL.GL_NO_ERROR; err = gl.glGetError()) {
         final String msg = dc.getGLU().gluErrorString(err) + " (error number: " + err + ")";
         logger.logSevere("GLError: " + msg);
      }
   }


   public static Matrix computeModelCoordinateOriginTransform(final Position position,
                                                              final Globe globe,
                                                              final double verticalExaggeration) {

      //      final Vec4 positionVec4 = GWWUtils.computePointFromPosition(position, globe, verticalExaggeration);
      //
      //      Matrix matrix = Matrix.fromTranslation(positionVec4);
      //      matrix = matrix.multiply(Matrix.fromRotationY(position.getLongitude()));
      //      matrix = matrix.multiply(Matrix.fromRotationX(position.getLatitude().multiply(-1)));
      //
      //      return matrix;


      final Angle xRotation = position.getLatitude().multiply(-1);
      final Angle yRotation = position.getLongitude();

      final Vec4 positionVec4 = GWWUtils.computePointFromPosition(position, globe, verticalExaggeration);

      final double cosX = xRotation.cos();
      final double cosY = yRotation.cos();
      final double sinX = xRotation.sin();
      final double sinY = yRotation.sin();

      return new Matrix( //
               cosY, 0, sinY, positionVec4.x, //
               sinX * sinY, cosX, -sinX * cosY, positionVec4.y, //
               -(cosX * sinY), sinX, cosX * cosY, positionVec4.z, //
               0, 0, 0, 1);
   }


   public static double computeSurfaceElevation(final DrawContext dc,
                                                final LatLon latLon) {


      //      final Vec4 surfacePoint = GGlobeApplication.instance().getTerrain().getSurfacePoint(latLon);
      final Vec4 surfacePoint = dc.getTerrain().getSurfacePoint(latLon.latitude, latLon.longitude, 0);


      final Globe globe = dc.getGlobe();

      if (surfacePoint == null) {
         return globe.getElevation(latLon.latitude, latLon.longitude);
      }

      return globe.computePositionFromPoint(surfacePoint).elevation;
   }


   public static GAxisAlignedBox createAxisAlignedBox(final Sector sector,
                                                      final double lowerZ,
                                                      final double upperZ) {
      final GVector3D lower = new GVector3D(sector.getMinLongitude().radians, sector.getMinLatitude().radians, lowerZ);
      final GVector3D upper = new GVector3D(sector.getMaxLongitude().radians, sector.getMaxLatitude().radians, upperZ);

      return new GAxisAlignedBox(lower, upper);
   }


   public static List<Sector> createTopLevelSectors() {
      return createTopLevelSectors(DEFAULT_LATITUDE_SUBDIVISIONS, DEFAULT_LONGITUDE_SUBDIVISIONS);
   }


   public static List<Sector> createTopLevelSectors(final int latitudeSubdivisions,
                                                    final int longitudeSubdivisions) {

      final List<Sector> result = new ArrayList<Sector>(latitudeSubdivisions * longitudeSubdivisions);

      final double deltaLatitute = 180d / latitudeSubdivisions;
      final double deltaLongitude = 360d / longitudeSubdivisions;


      Angle lastLatitude = Angle.NEG90;

      for (int row = 0; row < latitudeSubdivisions; row++) {
         Angle latitude = lastLatitude.addDegrees(deltaLatitute);
         if (latitude.getDegrees() + 1d > 90d) {
            latitude = Angle.POS90;
         }

         Angle lastLongitude = Angle.NEG180;

         for (int column = 0; column < longitudeSubdivisions; column++) {
            Angle longitude = lastLongitude.addDegrees(deltaLongitude);
            if (longitude.getDegrees() + 1d > 180d) {
               longitude = Angle.POS180;
            }

            result.add(new Sector(lastLatitude, latitude, lastLongitude, longitude));

            lastLongitude = longitude;
         }

         lastLatitude = latitude;
      }

      return result;
   }


   public static Vec4 getScreenPoint(final DrawContext dc,
                                     final IVector2 location) {
      return getScreenPoint(dc, new LatLon(Angle.fromRadians(location.y()), Angle.fromRadians(location.x())));
   }


   public static Vec4 getScreenPoint(final DrawContext dc,
                                     final LatLon location) {
      final Globe globe = dc.getGlobe();
      final View view = dc.getView();

      if ((globe == null) || (view == null)) {
         return null;
      }

      final Vec4 modelPoint = globe.computePointFromLocation(location);
      if (modelPoint == null) {
         return null;
      }

      return toVec3(view.project(modelPoint));
   }


   public static Vec4 getScreenPoint(final DrawContext dc,
                                     final Position position) {
      final Globe globe = dc.getGlobe();
      final View view = dc.getView();

      if ((globe == null) || (view == null)) {
         return null;
      }

      final Vec4 modelPoint = globe.computePointFromPosition(position);
      if (modelPoint == null) {
         return null;
      }

      return toVec3(view.project(modelPoint));
   }


   public static IVector2 increment(final IVector2 position,
                                    final GProjection projection,
                                    final double deltaEasting,
                                    final double deltaNorthing,
                                    final Globe globe) {
      final LatLon latLon = GWWUtils.toLatLon(position, projection);

      final LatLon newUTM = GWWUtils.increment(latLon, deltaEasting, deltaNorthing, globe);

      final GVector2D result = new GVector2D(newUTM.getLongitude().radians, newUTM.getLatitude().radians);
      //      System.out.println("position: " + position + ", deltaEasting: " + deltaEasting + ", deltaNorthing: " + deltaNorthing
      //                         + ", result: " + result);
      return result;
   }


   public static LatLon increment(final LatLon position,
                                  final double deltaEasting,
                                  final double deltaNorthing,
                                  final Globe globe) {

      try {
         final UTMCoord utm = UTMCoord.fromLatLon(position.latitude, position.longitude, globe);

         final double newEasting = utm.getEasting() + deltaEasting;
         final double newNorthing = utm.getNorthing() + deltaNorthing;

         final UTMCoord newUTM = UTMCoord.fromUTM(utm.getZone(), utm.getHemisphere(), newEasting, newNorthing, globe);

         return new LatLon(newUTM.getLatitude(), newUTM.getLongitude());
      }
      catch (final IllegalArgumentException e) {
         //         System.err.println(e);
         e.printStackTrace();
         return null;
      }
   }


   public static Position increment(final Position position,
                                    final double deltaEasting,
                                    final double deltaNorthing,
                                    final double deltaElevation) {
      return increment(position, deltaEasting, deltaNorthing, deltaElevation, EARTH);
   }


   public static Position increment(final Position position,
                                    final double deltaEasting,
                                    final double deltaNorthing,
                                    final double deltaElevation,
                                    final Globe globe) {
      try {
         final UTMCoord utm = UTMCoord.fromLatLon(position.latitude, position.longitude, globe);

         final double newEasting = utm.getEasting() + deltaEasting;
         final double newNorthing = utm.getNorthing() + deltaNorthing;
         final double newElevation = position.getElevation() + deltaElevation;

         final UTMCoord newUTM = UTMCoord.fromUTM(utm.getZone(), utm.getHemisphere(), newEasting, newNorthing, globe);

         return new Position(newUTM.getLatitude(), newUTM.getLongitude(), newElevation);
      }
      catch (final IllegalArgumentException e) {
         //         System.err.println(e);
         e.printStackTrace();
         return null;
      }
   }


   public static Position max(final Position pos1,
                              final Position pos2) {
      final double latitude = Math.max(pos1.latitude.radians, pos2.latitude.radians);
      final double longitude = Math.max(pos1.longitude.radians, pos2.longitude.radians);
      final double elevation = Math.max(pos1.elevation, pos2.elevation);
      return Position.fromRadians(latitude, longitude, elevation);
   }


   public static Position min(final Position pos1,
                              final Position pos2) {
      final double latitude = Math.min(pos1.latitude.radians, pos2.latitude.radians);
      final double longitude = Math.min(pos1.longitude.radians, pos2.longitude.radians);
      final double elevation = Math.min(pos1.elevation, pos2.elevation);
      return Position.fromRadians(latitude, longitude, elevation);
   }


   public static void popOffset(final GL gl) {
      gl.glMatrixMode(GL.GL_PROJECTION);
      gl.glPopMatrix();
      gl.glPopAttrib();
   }


   public static void pushOffset(final GL gl) {
      // Modify the projection transform to shift the depth values slightly toward the camera in order to
      // ensure the points are selected during depth buffering.

      final float[] matrix = new float[16];
      gl.glGetFloatv(GL.GL_PROJECTION_MATRIX, matrix, 0);
      //matrix[10] *= 0.99; // TODO: See Lengyel 2 ed. Section 9.1.2 to compute optimal/minimal offset
      matrix[10] *= 0.8; // TODO: See Lengyel 2 ed. Section 9.1.2 to compute optimal/minimal offset

      gl.glPushAttrib(GL.GL_TRANSFORM_BIT);
      gl.glMatrixMode(GL.GL_PROJECTION);
      gl.glPushMatrix();
      gl.glLoadMatrixf(matrix, 0);
   }


   public static void renderExtent(final DrawContext dc,
                                   final Extent extent) {
      if (extent instanceof Box) {
         ((Box) extent).render(dc);
      }
      else if (extent instanceof Sphere) {
         ((Sphere) extent).render(dc);
      }
      else if (extent instanceof Cylinder) {
         ((Cylinder) extent).render(dc);
      }
      else {
         System.out.println("Unsupported bounds type " + extent.getClass());
      }


   }


   public static void renderQuad(final DrawContext dc,
                                 final LatLon[] vertices,
                                 final float red,
                                 final float green,
                                 final float blue) {
      final GL gl = dc.getGL();

      final Globe globe = dc.getGlobe();
      final double verticalExaggeration = dc.getVerticalExaggeration();

      gl.glPushAttrib(GL.GL_DEPTH_BUFFER_BIT | GL.GL_POLYGON_BIT | GL.GL_TEXTURE_BIT | GL.GL_ENABLE_BIT | GL.GL_CURRENT_BIT);

      gl.glEnable(GL.GL_CULL_FACE);
      gl.glCullFace(GL.GL_BACK);

      gl.glPolygonMode(GL.GL_FRONT, GL.GL_LINE);

      gl.glColor3f(red, green, blue);

      gl.glBegin(GL.GL_QUADS);
      for (final LatLon vertex : vertices) {
         final Vec4 vec4 = GWWUtils.computePointFromPosition(vertex, globe, verticalExaggeration);
         gl.glVertex3d(vec4.x, vec4.y, vec4.z);
      }
      gl.glEnd();

      gl.glColor3f(1, 1, 1);

      gl.glPopAttrib();
   }


   public static void renderQuad(final DrawContext dc,
                                 final Position[] vertices,
                                 final float red,
                                 final float green,
                                 final float blue) {
      final GL gl = dc.getGL();

      final Globe globe = dc.getGlobe();
      final double verticalExaggeration = dc.getVerticalExaggeration();

      gl.glPushAttrib(GL.GL_DEPTH_BUFFER_BIT | GL.GL_POLYGON_BIT | GL.GL_TEXTURE_BIT | GL.GL_ENABLE_BIT | GL.GL_CURRENT_BIT);

      gl.glEnable(GL.GL_CULL_FACE);
      gl.glCullFace(GL.GL_BACK);

      gl.glPolygonMode(GL.GL_FRONT, GL.GL_LINE);

      gl.glColor3f(red, green, blue);

      gl.glBegin(GL.GL_QUADS);
      for (final Position vertex : vertices) {
         final Vec4 vec4 = GWWUtils.computePointFromPosition(vertex, globe, verticalExaggeration);
         gl.glVertex3d(vec4.x, vec4.y, vec4.z);
      }
      gl.glEnd();

      gl.glColor3f(1, 1, 1);

      gl.glPopAttrib();
   }


   public static void renderSector(final DrawContext dc,
                                   final Sector sector,
                                   final float red,
                                   final float green,
                                   final float blue) {
      final Angle lowerLatitude = sector.getMinLatitude();
      final Angle upperLatitude = sector.getMaxLatitude();
      final Angle lowerLongitude = sector.getMinLongitude();
      final Angle upperLongitude = sector.getMaxLongitude();

      final LatLon[] vertices = new LatLon[] {
                        new LatLon(lowerLatitude, lowerLongitude),
                        new LatLon(lowerLatitude, upperLongitude),
                        new LatLon(upperLatitude, upperLongitude),
                        new LatLon(upperLatitude, lowerLongitude)
      };

      renderQuad(dc, vertices, red, green, blue);
   }


   public static GAxisAlignedRectangle toAxisAlignedRectangle(final Sector sector,
                                                              final GProjection projection) {
      if (sector == null) {
         return null;
      }

      final GVector2D lower = new GVector2D(sector.getMinLongitude().radians, sector.getMinLatitude().radians);
      final GVector2D upper = new GVector2D(sector.getMaxLongitude().radians, sector.getMaxLatitude().radians);

      final IVector2 reprojectedLower = lower.reproject(GProjection.EPSG_4326, projection);
      final IVector2 reprojectedUpper = upper.reproject(GProjection.EPSG_4326, projection);

      return new GAxisAlignedRectangle(reprojectedLower, reprojectedUpper);
   }


   public static Box toBox(final GAxisAlignedBox aaBox) {
      if (aaBox == null) {
         return null;
      }

      final List<Vec4> points = GCollections.collect(aaBox.getVertices(), new IFunction<IVector3, Vec4>() {
         @Override
         public Vec4 apply(final IVector3 element) {
            return GWWUtils.toVec4(element);
         }
      });

      return Box.computeBoundingBox(points);
   }


   public static float[] toGLArray(final Matrix matrix) {
      return toGLArray(matrix, new float[16]);
   }


   public static float[] toGLArray(final Matrix matrix,
                                   final float[] array) {
      // Row 1
      array[0] = (float) matrix.m11;
      array[4] = (float) matrix.m12;
      array[8] = (float) matrix.m13;
      array[12] = (float) matrix.m14;

      // Row 2
      array[1] = (float) matrix.m21;
      array[5] = (float) matrix.m22;
      array[9] = (float) matrix.m23;
      array[13] = (float) matrix.m24;

      // Row 3
      array[2] = (float) matrix.m31;
      array[6] = (float) matrix.m32;
      array[10] = (float) matrix.m33;
      array[14] = (float) matrix.m34;

      // Row 4
      array[3] = (float) matrix.m41;
      array[7] = (float) matrix.m42;
      array[11] = (float) matrix.m43;
      array[15] = (float) matrix.m44;

      return array;
   }


   public static LatLon toLatLon(final IVector2 point,
                                 final GProjection projection) {
      final IVector2 geodesicPoint = point.reproject(projection, GProjection.EPSG_4326);
      return LatLon.fromRadians(geodesicPoint.y(), geodesicPoint.x());
   }


   public static Position toPosition(final IVector3 point,
                                     final GProjection projection) {
      final IVector3 geodesicPoint = point.reproject(projection, GProjection.EPSG_4326);
      return Position.fromRadians(geodesicPoint.y(), geodesicPoint.x(), geodesicPoint.z());
   }


   public static Sector toSector(final GAxisAlignedOrthotope<IVector2, ?> boundingRectangle,
                                 final GProjection projection) {
      if (boundingRectangle == null) {
         return null;
      }

      final IVector2 lower = boundingRectangle._lower.reproject(projection, GProjection.EPSG_4326);
      final IVector2 upper = boundingRectangle._upper.reproject(projection, GProjection.EPSG_4326);

      final Angle minLatitude = Angle.fromRadiansLatitude(lower.y());
      final Angle maxLatitude = Angle.fromRadiansLatitude(upper.y());

      final Angle minLongitude = Angle.fromRadiansLongitude(lower.x());
      final Angle maxLongitude = Angle.fromRadiansLongitude(upper.x());

      return new Sector(minLatitude, maxLatitude, minLongitude, maxLongitude);
   }


   public static Sector toSector(final Rectangle2D boundingRectangle,
                                 final GProjection projection) {
      final IVector2 min = new GVector2D(boundingRectangle.getMinX(), boundingRectangle.getMinY()).reproject(projection,
               GProjection.EPSG_4326);
      final IVector2 max = new GVector2D(boundingRectangle.getMaxX(), boundingRectangle.getMaxY()).reproject(projection,
               GProjection.EPSG_4326);

      final Angle minLatitude = Angle.fromRadiansLatitude(min.y());
      final Angle maxLatitude = Angle.fromRadiansLatitude(max.y());

      final Angle minLongitude = Angle.fromRadiansLongitude(min.x());
      final Angle maxLongitude = Angle.fromRadiansLongitude(max.x());

      return new Sector(minLatitude, maxLatitude, minLongitude, maxLongitude);
   }


   public static Vec4 toVec3(final Vec4 vec4) {
      final double w = vec4.w;
      if (w == 1) {
         return vec4;
      }
      return new Vec4(vec4.x / w, vec4.y / w, vec4.z / w);
   }


   public static Vec4 toVec4(final IVector3 point) {
      return new Vec4(point.x(), point.y(), point.z());
   }


   private static Vec4 computePointFromPosition(final LatLon position,
                                                final Globe globe,
                                                final double verticalExaggeration) {
      return computePointFromPosition(position, globe, verticalExaggeration, 0);
   }


   private static Vec4 computePointFromPosition(final LatLon position,
                                                final Globe globe,
                                                final double verticalExaggeration,
                                                final double metersOffset) {
      return globe.computePointFromPosition( //
               position.latitude, //
               position.longitude, //
               metersOffset * verticalExaggeration);
   }


   public static Vec4 computePointFromPosition(final Position position,
                                               final Globe globe,
                                               final double verticalExaggeration) {
      return computePointFromPosition(position, globe, verticalExaggeration, 0);
   }


   public static Vec4 computePointFromPosition(final Position position,
                                               final Globe globe,
                                               final double verticalExaggeration,
                                               final double metersOffset) {
      return globe.computePointFromPosition( //
               position.latitude, //
               position.longitude, //
               (position.elevation + metersOffset) * verticalExaggeration);
   }


   public static Vec4 transform(final Matrix matrix,
                                final Vec4 point) {
      if (point == null) {
         return null;
      }

      return GWWUtils.toVec3(point.transformBy4(matrix));
   }


   public static Vec4[] transform(final Matrix matrix,
                                  final Vec4... points) {
      final Vec4[] result = new Vec4[points.length];
      for (int i = 0; i < result.length; i++) {
         result[i] = transform(matrix, points[i]);
      }

      return result;
   }


   public static void redraw(final DrawContext dc) {
      if (dc != null) {
         redraw(dc.getView());
      }
   }


   public static void redraw(final View view) {
      if (view != null) {
         view.firePropertyChange(AVKey.VIEW, null, view);
      }
   }


}
