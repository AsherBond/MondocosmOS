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


package es.igosoftware.euclid.shape;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.vector.GVectorUtils;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GAssert;


public abstract class GTriangle<

VectorT extends IVector<VectorT, ?>,

SegmentT extends GSegment<VectorT, SegmentT, ?>,

BoundsT extends IBounds<VectorT, BoundsT>

>
         extends
            GPolytopeAbstract<VectorT, SegmentT, BoundsT>
         implements
            ISimplePolygon<VectorT, SegmentT, BoundsT> {

   private static final long serialVersionUID = 1L;


   public final VectorT      _v0;
   public final VectorT      _v1;
   public final VectorT      _v2;


   public GTriangle(final VectorT v0,
                    final VectorT v1,
                    final VectorT v2) {
      GAssert.notNull(v0, "v0");
      GAssert.notNull(v1, "v1");
      GAssert.notNull(v2, "v2");

      _v0 = v0;
      _v1 = v1;
      _v2 = v2;
   }


   @Override
   public final byte dimensions() {
      return _v0.dimensions();
   }


   @Override
   public final double precision() {
      return _v0.precision();
   }


   @Override
   @SuppressWarnings("unchecked")
   public final List<VectorT> getPoints() {
      return Collections.unmodifiableList(Arrays.asList((VectorT[]) new IVector[] {
                        _v0,
                        _v1,
                        _v2
      }));
   }


   @Override
   public final int getPointsCount() {
      return 3;
   }


   @Override
   public VectorT getPoint(final int index) {
      switch (index) {
         case 0:
            return _v0;
         case 1:
            return _v1;
         case 2:
            return _v2;
         default:
            throw new IndexOutOfBoundsException();
      }
   }


   @Override
   public final Iterator<VectorT> iterator() {
      return getPoints().iterator();
   }


   @Override
   public final String toString() {
      return "Triangle (" + _v0 + " " + _v1 + " " + _v2 + ")";
   }


   @Override
   public final int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_v0 == null) ? 0 : _v0.hashCode());
      result = prime * result + ((_v1 == null) ? 0 : _v1.hashCode());
      result = prime * result + ((_v2 == null) ? 0 : _v2.hashCode());
      return result;
   }


   @Override
   public final boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final GTriangle<?, ?, ?> other = (GTriangle<?, ?, ?>) obj;
      if (_v0 == null) {
         if (other._v0 != null) {
            return false;
         }
      }
      else if (!_v0.equals(other._v0)) {
         return false;
      }
      if (_v1 == null) {
         if (other._v1 != null) {
            return false;
         }
      }
      else if (!_v1.equals(other._v1)) {
         return false;
      }
      if (_v2 == null) {
         if (other._v2 != null) {
            return false;
         }
      }
      else if (!_v2.equals(other._v2)) {
         return false;
      }
      return true;
   }


   //   public final List<SegmentT> getEdges() {
   //      if (_edges == null) {
   //         _edges = Collections.unmodifiableList(initializeEdges());
   //      }
   //      return _edges;
   //   }


   @Override
   protected abstract List<SegmentT> initializeEdges();


   @Override
   public double squaredDistance(final VectorT point) {
      if (contains(point)) {
         return 0;
      }

      return squaredDistanceToBoundary(point);
   }


   //   @Override
   //   public double squaredDistanceToBoundary(final VectorT point) {
   //      double min = Double.POSITIVE_INFINITY;
   //
   //      for (final SegmentT edge : getEdges()) {
   //         final double current = edge.squaredDistance(point);
   //         if (current < min) {
   //            min = current;
   //         }
   //      }
   //
   //      return min;
   //   }


   @Override
   public boolean isConvex() {
      return true;
   }


   @SuppressWarnings("unchecked")
   @Override
   public VectorT getCentroid() {
      return GVectorUtils.getAverage(_v0, _v1, _v2);
   }


   @Override
   public boolean closeTo(final IBoundedGeometry<VectorT, BoundsT> that) {
      if (that instanceof ISimplePolygon) {
         @SuppressWarnings("unchecked")
         final ISimplePolygon<VectorT, ?, BoundsT> thatTriangle = (ISimplePolygon<VectorT, ?, BoundsT>) that;
         if (thatTriangle.getPointsCount() == 3) {
            return _v0.closeTo(thatTriangle.getPoint(0)) && //
                   _v1.closeTo(thatTriangle.getPoint(1)) && //
                   _v2.closeTo(thatTriangle.getPoint(2));
         }
      }
      return false;
   }


   @Override
   public final boolean contains(final VectorT point) {
      if (!getBounds().contains(point)) {
         return false;
      }

      return isTriangleContainsPoint(_v0, _v1, _v2, point);
   }


   public static <VectorT extends IVector<VectorT, ?>> boolean isTriangleContainsPoint(final VectorT a,
                                                                                       final VectorT b,
                                                                                       final VectorT c,
                                                                                       final VectorT point) {

      // from "Real-Time Collision Detection" (Christer Ericson) page 204

      // Translate point and triangle so that point lies at origin
      final VectorT ta = a.sub(point);
      final VectorT tb = b.sub(point);
      final VectorT tc = c.sub(point);

      final double ab = ta.dot(tb);
      final double ac = ta.dot(tc);
      final double bc = tb.dot(tc);
      final double cc = tc.dot(tc);

      // Make sure plane normals for pab and pbc point in the same direction
      if (bc * ac - cc * ab < 0.0f) {
         return false;
      }

      // Make sure plane normals for pab and pca point in the same direction
      final double bb = tb.dot(tb);
      if (ab * bc - ac * bb < 0.0f) {
         return false;
      }

      // Otherwise P must be in (or on) the triangle
      return true;
   }


   //   private static void renderTriangle(final GTriangle2D triangle,
   //                                      final Graphics2D g2d) {
   //      g2d.setColor(new Color(255, 255, 255, 127));
   //      final int[] xPoints = { GMath.toRoundedInt(triangle._v0.x()), GMath.toRoundedInt(triangle._v1.x()), GMath.toRoundedInt(triangle._v2.x()) };
   //      final int[] yPoints = { GMath.toRoundedInt(triangle._v0.y()), GMath.toRoundedInt(triangle._v1.y()), GMath.toRoundedInt(triangle._v2.y()) };
   //      g2d.fillPolygon(xPoints, yPoints, xPoints.length);
   //
   //      //      g2d.setColor(new Color(255, 255, 255, 255));
   //      //      g2d.drawPolygon(xPoints, yPoints, xPoints.length);
   //   }
   //
   //
   //   private static void renderQuad(final GQuad2D quad,
   //                                  final Graphics2D g2d) {
   //      g2d.setColor(new Color(255, 255, 255, 127));
   //      final int[] xPoints = { GMath.toRoundedInt(quad._v0.x()), GMath.toRoundedInt(quad._v1.x()), GMath.toRoundedInt(quad._v2.x()), GMath.toRoundedInt(quad._v3.x()) };
   //      final int[] yPoints = { GMath.toRoundedInt(quad._v0.y()), GMath.toRoundedInt(quad._v1.y()), GMath.toRoundedInt(quad._v2.y()), GMath.toRoundedInt(quad._v3.y()) };
   //      g2d.fillPolygon(xPoints, yPoints, xPoints.length);
   //
   //      g2d.setColor(new Color(255, 255, 255, 255));
   //      g2d.drawPolygon(xPoints, yPoints, xPoints.length);
   //   }
   //
   //
   //   public static void main(final String[] args) throws IOException {
   //      System.out.println("Test Point in Triangle");
   //      System.out.println("----------------------\n");
   //
   //      final Random random = new Random(0);
   //
   //      //      final GTriangle2D triangle = new GTriangle2D(new GVector2D(128, 10), new GVector2D(320, 10), new GVector2D(10, 320));
   //      final GTriangle2D geometry = new GTriangle2D(new GVector2D(320, 64), new GVector2D(640 - 64, 480 - 64), new GVector2D(10,
   //               320));
   //      //      final GQuad2D geometry = new GQuad2D(new GVector2D(480, 64), new GVector2D(640 - 64, 480 - 64), new GVector2D(240, 320),
   //      //               new GVector2D(64, 64));
   //      System.out.println(geometry.isConvex());
   //
   //      final BufferedImage image = new BufferedImage(640, 480, BufferedImage.TYPE_3BYTE_BGR);
   //
   //      final Graphics2D g2d = image.createGraphics();
   //      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
   //      g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
   //      g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
   //      g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
   //      g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
   //      g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
   //
   //      renderTriangle(geometry, g2d);
   //      //      for (final GTriangle2D triangle : geometry.triangulate()) {
   //      //         renderTriangle(triangle, g2d);
   //      //      }
   //      //      renderQuad(geometry, g2d);
   //
   //      for (int i = 0; i < 8000; i++) {
   //         final int x = random.nextInt(640);
   //         final int y = random.nextInt(480);
   //
   //         final boolean pointInTriangle = geometry.contains(new GVector2D(x, y));
   //         final Color color = pointInTriangle ? Color.GREEN : Color.RED;
   //         g2d.setColor(color);
   //         g2d.drawOval(x - 1, y - 1, 2, 2);
   //      }
   //
   //      g2d.dispose();
   //
   //      ImageIO.write(image, "jpg", new File("/home/dgd/Escritorio/PointsInTriangle.jpg"));
   //
   //      System.out.println("- done!");
   //   }


   public double perimeter() {
      return _v0.distance(_v1) + _v1.distance(_v2) + _v2.distance(_v0);
   }


}
