

package es.igosoftware.globe.weather.aemet;

import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVectorUtils;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.globe.weather.aemet.data.AEMETVariable;
import es.igosoftware.globe.weather.aemet.data.GInterpolator;
import es.igosoftware.globe.weather.aemet.data.Lapse;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GMath;
import es.igosoftware.util.IFunction;
import es.igosoftware.utils.GWWUtils;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Matrix;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.AbstractSurfaceObject;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.util.GLUTessellatorSupport;
import gov.nasa.worldwind.util.OGLStackHandler;
import gov.nasa.worldwind.util.OGLUtil;
import gov.nasa.worldwind.util.SurfaceTileDrawContext;
import gov.nasa.worldwind.util.WWMath;

import java.awt.Color;
import java.awt.Rectangle;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;
import javax.media.opengl.glu.GLUtessellatorCallback;
import javax.media.opengl.glu.GLUtessellatorCallbackAdapter;

import com.sun.opengl.util.BufferUtil;


public class GInterpolatedSurface
         extends
            AbstractSurfaceObject {


   private static final double[] MODEL_VIEW_ARRAY = new double[16];


   static class ColoredVertex {
      private final LatLon _position;
      private final Color  _color;


      ColoredVertex(final LatLon position,
                    final Color color) {
         _position = position;
         _color = color;
      }


      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((_color == null) ? 0 : _color.hashCode());
         result = prime * result + ((_position == null) ? 0 : _position.hashCode());
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
         final ColoredVertex other = (ColoredVertex) obj;
         if (_color == null) {
            if (other._color != null) {
               return false;
            }
         }
         else if (!_color.equals(other._color)) {
            return false;
         }
         if (_position == null) {
            if (other._position != null) {
               return false;
            }
         }
         else if (!_position.equals(other._position)) {
            return false;
         }
         return true;
      }


      @Override
      public String toString() {
         return "ColoredVertex [position=" + _position + ", color=" + _color + "]";
      }
   }


   private static class CacheKey {
      private final Globe  _globe;
      private final double _edgeIntervalsPerDegree;


      private CacheKey(final DrawContext dc,
                       final double edgeIntervalsPerDegree) {
         _globe = dc.getGlobe();
         _edgeIntervalsPerDegree = edgeIntervalsPerDegree;
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
         final CacheKey other = (CacheKey) obj;
         if (Double.doubleToLongBits(_edgeIntervalsPerDegree) != Double.doubleToLongBits(other._edgeIntervalsPerDegree)) {
            return false;
         }
         if (_globe == null) {
            if (other._globe != null) {
               return false;
            }
         }
         else if (!_globe.equals(other._globe)) {
            return false;
         }
         return true;
      }


      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         final long temp = Double.doubleToLongBits(_edgeIntervalsPerDegree);
         result = prime * result + (int) (temp ^ (temp >>> 32));
         result = prime * result + ((_globe == null) ? 0 : _globe.hashCode());
         return result;
      }
   }


   private static class ColoredPolygon
            implements
               Iterable<ColoredVertex> {


      private static final long         serialVersionUID = 1L;

      private final List<ColoredVertex> _vertices;

      private Sector                    _sector;


      private ColoredPolygon() {
         this(new ArrayList<ColoredVertex>());
      }


      private ColoredPolygon(final List<ColoredVertex> vertices) {
         _vertices = vertices;
      }


      private void add(final ColoredVertex vertex) {
         _vertices.add(vertex);
         //         _extent = null;
         _sector = null;
         //         EXTENT_CACHE.invalidate(this);
      }


      private void set(final int index,
                       final ColoredVertex vertex) {
         _vertices.set(index, vertex);
         //         _extent = null;
         _sector = null;
         //         EXTENT_CACHE.invalidate(this);
      }


      private int size() {
         return _vertices.size();
      }


      private ColoredVertex get(final int index) {
         return _vertices.get(index);
      }


      @Override
      public Iterator<ColoredVertex> iterator() {
         return _vertices.iterator();
      }


      private Sector getSector() {
         if (_sector == null) {
            _sector = calculateSector();
         }
         return _sector;
      }


      private Sector calculateSector() {
         final List<LatLon> locations = GCollections.collect(_vertices, new IFunction<ColoredVertex, LatLon>() {
            @Override
            public LatLon apply(final ColoredVertex element) {
               return element._position;
            }
         });
         return Sector.boundingSector(locations);
      }


      //      private Sector calculateExtent(final DrawContext dc) {
      //         final List<? extends Vec4> points = GCollections.collect(_vertices, new IFunction<ColoredVertex, Vec4>() {
      //            @Override
      //            public Vec4 apply(final ColoredVertex element) {
      //               final Position position = new Position(element._position, 0);
      //               return GWWUtils.computePointFromPosition(position, dc.getGlobe(), dc.getVerticalExaggeration());
      //            }
      //         });
      //
      //         //         return Box.computeBoundingBox(points);
      //         //         return Sector.boundingSector( )
      //      }
   }

   //   private static final GGlobeStateKeyCache<ColoredPolygon, Sector> EXTENT_CACHE = new GGlobeStateKeyCache<ColoredPolygon, Sector>( //
   //                                                                                          new GGlobeStateKeyCache.Factory<ColoredPolygon, Sector>() {
   //                                                                                             @Override
   //                                                                                             public Sector create(final DrawContext dc,
   //                                                                                                                  final ColoredPolygon polygon) {
   //                                                                                                return polygon.calculateExtent(dc);
   //                                                                                             }
   //                                                                                          });

   private static class CacheEntry {
      private final List<ColoredPolygon> _polygons;
      private final Object               _globeStateKey;


      private CacheEntry(final List<ColoredPolygon> polygons,
                         final DrawContext dc) {
         _polygons = polygons;
         _globeStateKey = dc.getGlobe().getStateKey(dc);
      }


      private boolean isValid(final DrawContext dc) {
         return _globeStateKey.equals(dc.getGlobe().getStateKey(dc));
      }
   }


   private static FloatBuffer  vertexBuffer;


   private static final int    DEFAULT_MAX_EDGE_INTERVALS = 100;
   private static final int    DEFAULT_MIN_EDGE_INTERVALS = 0;
   private static final double TEXELS_PER_EDGE_INTERVAL   = 50;
   private static final double INTERVALS_PER_TEXEL        = 1.0 / TEXELS_PER_EDGE_INTERVAL;


   private static enum Order {
      COUNTER_CLOCKWISE,
      CLOCKWISE;
   }

   private final AEMETLayer                _layer;
   private final List<GInterpolatedArea>   _areas;
   private AEMETVariable<Double>           _variable;
   private Lapse                           _lapse;
   private final Position                  _referencePosition;
   private final Sector                    _bounds;
   private final int                       _maxEdgeIntervals = DEFAULT_MAX_EDGE_INTERVALS;
   private final int                       _minEdgeIntervals = DEFAULT_MIN_EDGE_INTERVALS;
   private final OGLStackHandler           _stackHandler     = new OGLStackHandler();
   private final List<ColoredPolygon>      _activeGeometries = new ArrayList<ColoredPolygon>();    // re-determined each frame
   private final Map<CacheKey, CacheEntry> _polygonsCache    = new HashMap<CacheKey, CacheEntry>();


   private final GInterpolator             _interpolator;


   GInterpolatedSurface(final AEMETLayer layer,
                        final AEMETVariable<Double> variable,
                        final Lapse lapse,
                        final List<GInterpolatedArea> areas) {
      GAssert.notNull(layer, "layer");
      GAssert.notNull(variable, "variable");
      GAssert.notNull(lapse, "lapse");
      GAssert.notEmpty(areas, "areas");

      _layer = layer;
      _interpolator = layer._interpolator;
      _areas = areas;
      _variable = variable;

      _lapse = lapse;

      _referencePosition = initializeReferencePosition();
      _bounds = initializeBounds();
   }


   private Position initializeReferencePosition() {
      final List<IVector3> positions = GCollections.collect(_areas, new IFunction<GInterpolatedArea, IVector3>() {
         @Override
         public IVector3 apply(final GInterpolatedArea element) {
            return element.getReferencePosition();
         }
      });

      final IVector3 averagePosition = GVectorUtils.getAverage(positions);
      return GWWUtils.toPosition(averagePosition, GProjection.EPSG_4326);
   }


   private Sector initializeBounds() {
      final List<GAxisAlignedRectangle> areasBounds = GCollections.collect(_areas,
               new IFunction<GInterpolatedArea, GAxisAlignedRectangle>() {
                  @Override
                  public GAxisAlignedRectangle apply(final GInterpolatedArea element) {
                     return element.getBounds();
                  }
               });

      final GAxisAlignedRectangle mergedBounds = GAxisAlignedRectangle.merge(areasBounds);
      return GWWUtils.toSector(mergedBounds, GProjection.EPSG_4326);
   }


   @Override
   public List<Sector> getSectors(final DrawContext dc) {
      return Collections.singletonList(_bounds);
   }


   @Override
   protected void drawGeographic(final DrawContext dc,
                                 final SurfaceTileDrawContext sdc) {
      beginDrawing(dc, sdc);
      try {
         doDrawGeographic(dc, sdc);
      }
      finally {
         endDrawing(dc);
      }
   }


   private void beginDrawing(final DrawContext dc,
                             final SurfaceTileDrawContext sdc) {
      final GL gl = dc.getGL();

      _stackHandler.pushAttrib(gl, GL.GL_COLOR_BUFFER_BIT // For alpha test func and ref, blend func
                                   | GL.GL_CURRENT_BIT // For current color.
                                   | GL.GL_ENABLE_BIT // For disable depth test.
                                   | GL.GL_LINE_BIT // For line width, line smooth, line stipple.
                                   | GL.GL_POLYGON_BIT // For cull enable and cull face.
                                   | GL.GL_TRANSFORM_BIT); // For matrix mode.

      _stackHandler.pushClientAttrib(gl, GL.GL_CLIENT_VERTEX_ARRAY_BIT);

      _stackHandler.pushTextureIdentity(gl);
      _stackHandler.pushProjection(gl);
      _stackHandler.pushModelview(gl);

      // Enable the alpha test.
      gl.glEnable(GL.GL_ALPHA_TEST);
      gl.glAlphaFunc(GL.GL_GREATER, 0.0f);

      // Disable the depth test.
      gl.glDisable(GL.GL_DEPTH_TEST);

      // Enable backface culling.
      gl.glEnable(GL.GL_CULL_FACE);
      gl.glCullFace(GL.GL_BACK);

      // Enable client vertex arrays.
      gl.glEnableClientState(GL.GL_VERTEX_ARRAY);

      // Enable blending.
      if (!dc.isPickingMode()) {
         gl.glEnable(GL.GL_BLEND);
      }

      applyModelviewTransform(dc, sdc);
   }


   private void endDrawing(final DrawContext dc) {
      final GL gl = dc.getGL();

      //               if ((texture != null) && !dc.isPickingMode()) {
      //                  gl.glTexGeni(GL.GL_S, GL.GL_TEXTURE_GEN_MODE, OGLUtil.DEFAULT_TEXTURE_GEN_MODE);
      //                  gl.glTexGeni(GL.GL_T, GL.GL_TEXTURE_GEN_MODE, OGLUtil.DEFAULT_TEXTURE_GEN_MODE);
      //                  gl.glTexGendv(GL.GL_S, GL.GL_OBJECT_PLANE, OGLUtil.DEFAULT_TEXTURE_GEN_S_OBJECT_PLANE, 0);
      //                  gl.glTexGendv(GL.GL_T, GL.GL_OBJECT_PLANE, OGLUtil.DEFAULT_TEXTURE_GEN_T_OBJECT_PLANE, 0);
      //                  gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
      //               }

      _stackHandler.pop(gl);
   }


   private Position getReferencePosition() {
      return _referencePosition;
   }


   private void applyModelviewTransform(final DrawContext dc,
                                        final SurfaceTileDrawContext sdc) {
      // Apply the geographic to surface tile coordinate transform.
      Matrix modelview = sdc.getModelviewMatrix();

      // If the SurfaceShape has a non-null reference position, transform to the local coordinate system that has its
      // origin at the reference position.
      final Position referencePosition = getReferencePosition();
      if (referencePosition != null) {
         final Matrix refMatrix = Matrix.fromTranslation(referencePosition.getLongitude().degrees,
                  referencePosition.getLatitude().degrees, 0);
         modelview = modelview.multiply(refMatrix);
      }

      dc.getGL().glMultMatrixd(modelview.toArray(MODEL_VIEW_ARRAY, 0, false), 0);
   }


   private void doDrawGeographic(final DrawContext dc,
                                 final SurfaceTileDrawContext sdc) {
      if (getOpacity() <= 0.01f) {
         return;
      }

      final boolean drawInterior = _layer.isRenderTriangulationSurface();
      final boolean drawOutline = _layer.isRenderTriangulationEdges();

      if (drawInterior || drawOutline) {
         determineActiveGeometry(dc, sdc);
         if (_activeGeometries.isEmpty()) {
            return;
         }

         if (drawInterior) {
            drawInterior(dc);
         }

         if (drawOutline) {
            drawOutline(dc);
         }
      }

   }


   private void drawOutline(final DrawContext dc) {
      final Position referencePosition = getReferencePosition();
      if (referencePosition == null) {
         return;
      }

      applyOutlineState(dc);

      final GL gl = dc.getGL();

      for (final ColoredPolygon drawLocations : _activeGeometries) {
         if ((vertexBuffer == null) || (vertexBuffer.capacity() < 2 * drawLocations.size())) {
            vertexBuffer = BufferUtil.newFloatBuffer(2 * drawLocations.size());
         }
         vertexBuffer.clear();

         for (final ColoredVertex ll : drawLocations) {
            vertexBuffer.put((float) (ll._position.getLongitude().degrees - referencePosition.getLongitude().degrees));
            vertexBuffer.put((float) (ll._position.getLatitude().degrees - referencePosition.getLatitude().degrees));
         }
         vertexBuffer.flip();

         gl.glVertexPointer(2, GL.GL_FLOAT, 0, vertexBuffer);
         gl.glDrawArrays(GL.GL_LINE_STRIP, 0, drawLocations.size());
      }
   }


   private void applyOutlineState(final DrawContext dc) {
      final GL gl = dc.getGL();

      // Apply line width state
      double lineWidth = 0.5;
      if (dc.isPickingMode()) {
         if (lineWidth != 0) {
            lineWidth += 5;
         }
      }
      gl.glLineWidth((float) lineWidth);

      // Apply line smooth state
      if (!dc.isPickingMode()) {
         gl.glEnable(GL.GL_LINE_SMOOTH);
      }
      else {
         gl.glDisable(GL.GL_LINE_SMOOTH);
      }

      //      // Apply line stipple state.
      //      if (dc.isPickingMode()) {
      //         gl.glDisable(GL.GL_LINE_STIPPLE);
      //      }
      //      else {
      //         gl.glEnable(GL.GL_LINE_STIPPLE);
      //         gl.glLineStipple(attributes.getOutlineStippleFactor(), attributes.getOutlineStipplePattern());
      //      }

      if (!dc.isPickingMode()) {
         // Apply blending in non-premultiplied color mode.
         OGLUtil.applyBlending(gl, false);
         // Set the current RGBA color to the outline color and opacity. Convert the floating point opacity from the
         // range [0, 1] to the unsigned byte range [0, 255].
         final Color color = Color.BLACK;
         final int alpha = (int) (255 * getOpacity() + 0.5);
         gl.glColor4ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue(), (byte) alpha);
      }

      // Disable textures.
      gl.glDisable(GL.GL_TEXTURE_2D);
      gl.glDisable(GL.GL_TEXTURE_GEN_S);
      gl.glDisable(GL.GL_TEXTURE_GEN_T);
   }


   private float getOpacity() {
      return 1;
   }


   private void drawInterior(final DrawContext dc) {
      applyInteriorState(dc);

      tessellateInterior(dc);
   }


   private void tessellateInteriorVertices(final GLU glu,
                                           final GLUtessellator tess) {
      if (_activeGeometries.isEmpty()) {
         return;
      }

      final Position referencePosition = getReferencePosition();
      if (referencePosition == null) {
         return;
      }

      glu.gluTessBeginPolygon(tess, null);

      for (final ColoredPolygon geometry : _activeGeometries) {
         glu.gluTessBeginContour(tess);
         for (final ColoredVertex vertex : geometry) {
            final double[] vertexA = new double[3];
            vertexA[0] = vertex._position.getLongitude().degrees - referencePosition.getLongitude().degrees;
            vertexA[1] = vertex._position.getLatitude().degrees - referencePosition.getLatitude().degrees;
            // vertexA[2] = 0;

            glu.gluTessVertex(tess, vertexA, 0, new MyVertex((float) vertexA[0], (float) vertexA[1], (float) vertexA[2],
                     vertex._color));
         }
         glu.gluTessEndContour(tess);
      }

      glu.gluTessEndPolygon(tess);

      return;
   }


   private static class MyVertex {
      private final float _x;
      private final float _y;
      private final float _z;
      private final float _r;
      private final float _g;
      private final float _b;
      private final float _a;


      private MyVertex(final float x,
                       final float y,
                       final float z,
                       final Color color) {
         _x = x;
         _y = y;
         _z = z;
         _r = color.getRed() / 255f;
         _g = color.getGreen() / 255f;
         _b = color.getBlue() / 255f;
         _a = color.getAlpha() / 255f;
      }

   }


   private static class MyGLUtessellatorCallback
            extends
               GLUtessellatorCallbackAdapter {

      private static final MyVertex EMPTY_VERTEX = new MyVertex(0, 0, 0, new Color(0, 0, 0, 0));

      private final GL              _gl;


      private MyGLUtessellatorCallback(final GL gl) {
         GAssert.notNull(gl, "gl");

         _gl = gl;
      }


      @Override
      public void combine(final double[] coords,
                          final Object[] rawData,
                          final float[] weight,
                          final Object[] outData) {
         final float x = (float) coords[0];
         final float y = (float) coords[1];
         final float z = (float) coords[2];

         final Color color = calculateColor(rawData, weight);

         outData[0] = new MyVertex(x, y, z, color);
      }


      private static Color calculateColor(final Object[] rawData,
                                          final float[] w) {
         final MyVertex vertex0 = (rawData[0] == null) ? EMPTY_VERTEX : (MyVertex) rawData[0];
         final MyVertex vertex1 = (rawData[1] == null) ? EMPTY_VERTEX : (MyVertex) rawData[1];
         final MyVertex vertex2 = (rawData[2] == null) ? EMPTY_VERTEX : (MyVertex) rawData[2];
         final MyVertex vertex3 = (rawData[3] == null) ? EMPTY_VERTEX : (MyVertex) rawData[3];

         final float r = (w[0] * vertex0._r) + (w[1] * vertex1._r) + (w[2] * vertex2._r) + (w[3] * vertex3._r);
         final float g = (w[0] * vertex0._g) + (w[1] * vertex1._g) + (w[2] * vertex2._g) + (w[3] * vertex3._g);
         final float b = (w[0] * vertex0._b) + (w[1] * vertex1._b) + (w[2] * vertex2._b) + (w[3] * vertex3._b);
         final float a = (w[0] * vertex0._a) + (w[1] * vertex1._a) + (w[2] * vertex2._a) + (w[3] * vertex3._a);

         return new Color(r, g, b, a);
      }


      @Override
      public void begin(final int type) {
         _gl.glBegin(type);
      }


      @Override
      public void vertex(final Object vertexData) {
         final MyVertex vertex = (MyVertex) vertexData;

         _gl.glColor4f(vertex._r, vertex._g, vertex._b, vertex._a);
         _gl.glVertex3f(vertex._x, vertex._y, vertex._z);
      }


      @Override
      public void end() {
         _gl.glEnd();
      }


   }


   private void tessellateInterior(final DrawContext dc) {
      final GLU glu = dc.getGLU();

      final GLUtessellatorCallback cb = new MyGLUtessellatorCallback(dc.getGL());

      final GLUTessellatorSupport glts = new GLUTessellatorSupport();
      glts.beginTessellation(glu, cb, new Vec4(0, 0, 1));
      try {
         tessellateInteriorVertices(glu, glts.getGLUtessellator());
      }
      finally {
         glts.endTessellation(glu);
      }
   }


   private void applyInteriorState(final DrawContext dc) {
      final GL gl = dc.getGL();

      if (!dc.isPickingMode()) {
         // Apply blending in non-premultiplied color mode.
         OGLUtil.applyBlending(gl, false);

         //         final Color color = _color;
         //         gl.glColor4ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue(), (byte) color.getAlpha());
      }

      // Disable textures.
      gl.glDisable(GL.GL_TEXTURE_2D);
      gl.glDisable(GL.GL_TEXTURE_GEN_S);
      gl.glDisable(GL.GL_TEXTURE_GEN_T);
   }


   private void determineActiveGeometry(final DrawContext dc,
                                        final SurfaceTileDrawContext sdc) {
      _activeGeometries.clear();

      final List<ColoredPolygon> polygons = getCachedPolygons(dc, sdc);
      if (polygons == null) {
         return;
      }


      final Sector sector = sdc.getSector();

      for (final ColoredPolygon polygon : polygons) {
         //         if (!polygon.getExtent(dc).intersects(frustum)) {
         if (!sector.intersects(polygon.getSector())) {
            //            final int remove_print;
            //            System.out.println("Ignored not visible polygon, sector: " + sector);
            continue;
         }


         // If the locations cross the international dateline, then reflect the locations on the side opposite
         // the SurfaceTileDrawContext's sector. This causes all locations to be positive or negative, and render
         // correctly into a single non dateline-spanning geographic viewport.
         if (locationsCrossDateLine(polygon._vertices)) {
            final boolean inWesternHemisphere = sector.getMaxLongitude().degrees < 0;

            final ColoredPolygon newPolygon = new ColoredPolygon(polygon._vertices);

            for (int i = 0; i < newPolygon.size(); i++) {
               final ColoredVertex ll = newPolygon.get(i);

               final LatLon latLon = ll._position;
               final Color fromColor = ll._color;
               if (inWesternHemisphere && (latLon.getLongitude().degrees > 0)) {
                  final LatLon newLatLon = LatLon.fromDegrees(latLon.getLatitude().degrees, latLon.getLongitude().degrees - 360);
                  newPolygon.set(i, new ColoredVertex(newLatLon, fromColor));
               }
               else if (!inWesternHemisphere && (latLon.getLongitude().degrees < 0)) {
                  final LatLon newLatLon = LatLon.fromDegrees(latLon.getLatitude().degrees, latLon.getLongitude().degrees + 360);
                  newPolygon.set(i, new ColoredVertex(newLatLon, fromColor));
               }
            }
            _activeGeometries.add(newPolygon);
         }
         else {
            _activeGeometries.add(polygon);
         }
      }
   }


   private double computeEdgeIntervalsPerDegree(final SurfaceTileDrawContext sdc) {
      final Rectangle viewport = sdc.getViewport();
      final Sector sector = sdc.getSector();

      final double texelsPerDegree = Math.max(//
               viewport.width / sector.getDeltaLonDegrees(), //
               viewport.height / sector.getDeltaLatDegrees());

      return INTERVALS_PER_TEXEL * texelsPerDegree;
   }


   private List<ColoredPolygon> getCachedPolygons(final DrawContext dc,
                                                  final SurfaceTileDrawContext sdc) {
      final double edgeIntervalsPerDegree = computeEdgeIntervalsPerDegree(sdc);
      final CacheKey key = new CacheKey(dc, edgeIntervalsPerDegree);
      CacheEntry entry = _polygonsCache.get(key);
      if ((entry == null) || !entry.isValid(dc)) {
         entry = new CacheEntry(createPolygons(edgeIntervalsPerDegree), dc);
         _polygonsCache.put(key, entry);
      }
      return entry._polygons;
   }


   private List<ColoredPolygon> createPolygons(final double edgeIntervalsPerDegree) {
      final List<ColoredPolygon> polygons = new ArrayList<ColoredPolygon>();

      for (final GInterpolatedArea area : _areas) {
         final List<ColoredVertex> geometry = area.getGeometry(_variable, _lapse, _interpolator);
         final ColoredPolygon polygon = generateIntermediateVertices(geometry, edgeIntervalsPerDegree);

         if (computeWindingOrderOfLocations(polygon) != Order.COUNTER_CLOCKWISE) {
            Collections.reverse(polygon._vertices);
         }

         polygons.add(polygon);
      }

      return polygons.isEmpty() ? null : polygons;
   }


   private static Order computeWindingOrderOfLocations(final ColoredPolygon polygon) {
      Iterator<ColoredVertex> iterator = polygon.iterator();
      if (!iterator.hasNext()) {
         return Order.COUNTER_CLOCKWISE;
      }

      if (locationsCrossDateLine(polygon._vertices)) {
         iterator = makeDatelineCrossingLocationsPositive(polygon).iterator();
      }

      double area = 0;
      final ColoredVertex firstLocation = iterator.next();
      ColoredVertex location = firstLocation;

      while (iterator.hasNext()) {
         final ColoredVertex nextLocation = iterator.next();

         area += location._position.getLongitude().degrees * nextLocation._position.getLatitude().degrees;
         area -= nextLocation._position.getLongitude().degrees * location._position.getLatitude().degrees;

         location = nextLocation;
      }

      // Include the area connecting the last point to the first point, if they're not already equal.
      if (!location.equals(firstLocation)) {
         area += location._position.getLongitude().degrees * firstLocation._position.getLatitude().degrees;
         area -= firstLocation._position.getLongitude().degrees * location._position.getLatitude().degrees;
      }

      return (area < 0) ? Order.CLOCKWISE : Order.COUNTER_CLOCKWISE;
   }


   private static boolean locationsCrossDateLine(final List<ColoredVertex> vertices) {
      ColoredVertex vertex = null;
      for (final ColoredVertex nextVertex : vertices) {
         if (vertex != null) {
            // A segment cross the line if end pos have different longitude signs
            // and are more than 180 degrees longitude apart
            final LatLon pos = vertex._position;
            final LatLon nextPos = nextVertex._position;
            if (Math.signum(pos.getLongitude().degrees) != Math.signum(nextPos.getLongitude().degrees)) {
               final double delta = Math.abs(pos.getLongitude().degrees - nextPos.getLongitude().degrees);
               if ((delta > 180) && (delta < 360)) {
                  return true;
               }
            }
         }
         vertex = nextVertex;
      }

      return false;
   }


   private static ColoredPolygon makeDatelineCrossingLocationsPositive(final ColoredPolygon polygon) {
      final ColoredPolygon result = new ColoredPolygon();

      for (final ColoredVertex vertex : polygon) {
         if (vertex == null) {
            continue;
         }

         final LatLon position = vertex._position;
         if (position.getLongitude().degrees < 0) {
            final LatLon newLatLon = LatLon.fromDegrees(position.getLatitude().degrees, position.getLongitude().degrees + 360);
            result.add(new ColoredVertex(newLatLon, vertex._color));
         }
         else {
            result.add(vertex);
         }
      }

      return result;
   }


   private ColoredPolygon generateIntermediateVertices(final List<ColoredVertex> vertices,
                                                       final double edgeIntervalsPerDegree) {

      final ColoredPolygon polygon = new ColoredPolygon();

      ColoredVertex firstVertex = null;
      ColoredVertex lastVertex = null;

      for (final ColoredVertex vertex : vertices) {
         if (firstVertex == null) {
            firstVertex = vertex;
         }

         if (lastVertex != null) {
            addIntermediateVertices(lastVertex, vertex, edgeIntervalsPerDegree, polygon);
         }

         polygon.add(vertex);
         lastVertex = vertex;
      }

      if ((firstVertex != null) && (lastVertex != null) && !firstVertex.equals(lastVertex)) {
         addIntermediateVertices(lastVertex, firstVertex, edgeIntervalsPerDegree, polygon);
         polygon.add(firstVertex);
      }

      return polygon;
   }


   private void addIntermediateVertices(final ColoredVertex from,
                                        final ColoredVertex to,
                                        final double edgeIntervalsPerDegree,
                                        final ColoredPolygon polygon) {
      final Angle pathLength = LatLon.greatCircleDistance(from._position, to._position);

      final double edgeIntervals = WWMath.clamp(edgeIntervalsPerDegree * pathLength.degrees, _minEdgeIntervals, _maxEdgeIntervals);
      final int numEdgeIntervals = (int) Math.ceil(edgeIntervals);

      if (numEdgeIntervals > 1) {
         final double headingRadians = LatLon.greatCircleAzimuth(from._position, to._position).radians;
         final double stepSizeRadians = pathLength.radians / (numEdgeIntervals + 1);

         for (int i = 1; i <= numEdgeIntervals; i++) {
            final LatLon newLatLon = LatLon.greatCircleEndPosition(from._position, headingRadians, i * stepSizeRadians);
            final Angle distance = LatLon.greatCircleDistance(from._position, newLatLon);
            final Color newColor = interpolateColor(from._color, to._color, distance.degrees, pathLength.degrees);
            polygon.add(new ColoredVertex(newLatLon, newColor));
         }
      }
   }


   private static Color interpolateColor(final Color from,
                                         final Color to,
                                         final double amount,
                                         final double maximum) {
      return interpolateColor(from, to, amount / maximum);
   }


   private static Color interpolateColor(final Color from,
                                         final Color to,
                                         final double alpha) {
      return interpolateColor(from, to, (float) alpha);
   }


   private static Color interpolateColor(final Color from,
                                         final Color to,
                                         final float alpha) {

      final float frac1 = GMath.clamp(alpha, 0, 1);
      final float frac2 = 1f - frac1;

      final float newRed = (from.getRed() * frac2) + (to.getRed() * frac1);
      final float newGreen = (from.getGreen() * frac2) + (to.getGreen() * frac1);
      final float newBlue = (from.getBlue() * frac2) + (to.getBlue() * frac1);
      final float newAlpha = (from.getAlpha() * frac2) + (to.getAlpha() * frac1);

      return new Color(toInt(newRed), toInt(newGreen), toInt(newBlue), toInt(newAlpha));
   }


   private static int toInt(final float value) {
      return Math.round(value);
   }


   public void invalidate() {
      updateModifiedTime();
      _polygonsCache.clear();
   }


   public void setLapse(final Lapse lapse) {
      if (_lapse == lapse) {
         return;
      }
      _lapse = lapse;
      invalidate();
   }


   public void setVariable(final AEMETVariable<Double> variable) {
      if (_variable == variable) {
         return;
      }
      _variable = variable;
      invalidate();
   }


}
