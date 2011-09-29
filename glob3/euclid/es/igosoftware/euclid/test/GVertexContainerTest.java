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


package es.igosoftware.euclid.test;

import java.util.Comparator;
import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Test;

import es.igosoftware.euclid.colors.GColorI;
import es.igosoftware.euclid.colors.GColorPrecision;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.GVectorPrecision;
import es.igosoftware.euclid.vector.GVectorUtils;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.verticescontainer.GSubVertexContainer;
import es.igosoftware.euclid.verticescontainer.GVertex3Container;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;


public class GVertexContainerTest {
   final private static float      DEFAULT_INTENSITY = 1f;
   final private static GVector3D  DEFAULT_NORMAL    = GVector3D.X_UP;
   final private static GColorI    DEFAULT_COLOR     = GColorI.WHITE;

   final private static IVector3[] points            = {
                     GVector3D.ZERO, //
                     new GVector3D(1, 0, 0), //
                     new GVector3D(1, 1, 0), //
                     new GVector3D(1, 1, 1), //
                     new GVector3D(1, 1, 2), //
                     new GVector3D(1, 2, 2), //
                     new GVector3D(2, 2, 2), //
                     new GVector3D(3, 3, 3), //
                     new GVector3D(3, 2, 1)
                                                     };

   final private static float[]    intensities       = {
                     GVertexContainerTest.DEFAULT_INTENSITY, //
                     GVertexContainerTest.DEFAULT_INTENSITY, //
                     GVertexContainerTest.DEFAULT_INTENSITY, //
                     0.5f, //
                     0.6f, //
                     GVertexContainerTest.DEFAULT_INTENSITY, //
                     GVertexContainerTest.DEFAULT_INTENSITY, //
                     GVertexContainerTest.DEFAULT_INTENSITY, //
                     GVertexContainerTest.DEFAULT_INTENSITY
                                                     };

   final private static IVector3[] normals           = {
                     GVertexContainerTest.DEFAULT_NORMAL, //
                     GVertexContainerTest.DEFAULT_NORMAL, //
                     GVertexContainerTest.DEFAULT_NORMAL, //
                     GVertexContainerTest.DEFAULT_NORMAL, //
                     GVertexContainerTest.DEFAULT_NORMAL, //
                     GVector3D.Z_UP, //
                     new GVector3D(1, 1, 1).normalized(), //
                     GVertexContainerTest.DEFAULT_NORMAL, //
                     GVertexContainerTest.DEFAULT_NORMAL
                                                     };

   final private static IColor[]   colors            = {
                     GVertexContainerTest.DEFAULT_COLOR, //
                     GVertexContainerTest.DEFAULT_COLOR, //
                     GVertexContainerTest.DEFAULT_COLOR, //
                     GVertexContainerTest.DEFAULT_COLOR, //
                     GVertexContainerTest.DEFAULT_COLOR, //
                     GVertexContainerTest.DEFAULT_COLOR, //
                     GVertexContainerTest.DEFAULT_COLOR, //
                     GColorI.newRGB(0.1f, 0.2f, 0.3f), //
                     GColorI.newRGB(0.11f, 0.22f, 0.33f)
                                                     };

   final private static IVector3[] sortedPoints      = {
                     new GVector3D(3, 2, 1),
                     new GVector3D(1, 0, 0),
                     new GVector3D(1, 1, 2),
                     new GVector3D(3, 3, 3),
                     new GVector3D(2, 2, 2),
                     new GVector3D(1, 1, 1),
                     new GVector3D(0, 0, 0),
                     new GVector3D(1, 2, 2),
                     new GVector3D(1, 1, 0)
                                                     };

   final private static IVector3[] sortedSubPoints   = {
                     new GVector3D(1, 0, 0),
                     new GVector3D(1, 1, 2),
                     new GVector3D(1, 1, 1),
                     new GVector3D(3, 3, 3),
                                                     };


   private static void assertCloseTo(final String description,
                                     final IVector3 expected,
                                     final IVector3 current) {
      if (expected.closeTo(current)) {
         return;
      }

      Assert.fail(description + " expected: " + expected + ", current: " + current);
   }


   private static void assertCloseTo(final String description,
                                     final IColor expected,
                                     final IColor current) {
      if (expected.closeTo(current)) {
         return;
      }

      Assert.fail(description + " expected: " + expected + ", current: " + current);
   }


   private static void testAddVertex(final GVertex3Container container,
                                     final IVector3 point) {
      final int previousSize = container.size();

      container.addPoint(point);

      Assert.assertEquals("size", previousSize + 1, container.size());
      assertCloseTo("Point", point, container.getPoint(previousSize));
      Assert.assertEquals("Intensity", GVertexContainerTest.DEFAULT_INTENSITY, container.getIntensity(previousSize), 0);
      assertCloseTo("Normal", GVertexContainerTest.DEFAULT_NORMAL, container.getNormal(previousSize));
      assertCloseTo("Color", GVertexContainerTest.DEFAULT_COLOR, container.getColor(previousSize));
   }


   private static void testAddVertex(final GVertex3Container container,
                                     final IVector3 point,
                                     final float intensity) {
      final int previousSize = container.size();

      container.addPoint(point, intensity);

      Assert.assertEquals("size", previousSize + 1, container.size());
      assertCloseTo("Point", point, container.getPoint(previousSize));
      Assert.assertEquals("Intensity", intensity, container.getIntensity(previousSize), 0);
      assertCloseTo("Normal", GVertexContainerTest.DEFAULT_NORMAL, container.getNormal(previousSize));
      assertCloseTo("Color", GVertexContainerTest.DEFAULT_COLOR, container.getColor(previousSize));
   }


   private static void testAddVertex(final GVertex3Container container,
                                     final IVector3 point,
                                     final IVector3 normal) {
      final int previousSize = container.size();

      container.addPoint(point, normal);

      Assert.assertEquals("size", previousSize + 1, container.size());
      assertCloseTo("Point", point, container.getPoint(previousSize));
      Assert.assertEquals("Intensity", GVertexContainerTest.DEFAULT_INTENSITY, container.getIntensity(previousSize), 0);
      assertCloseTo("Normal", normal, container.getNormal(previousSize));
      assertCloseTo("Color", GVertexContainerTest.DEFAULT_COLOR, container.getColor(previousSize));
   }


   private static void testAddVertex(final GVertex3Container container,
                                     final IVector3 point,
                                     final IColor color) {
      final int previousSize = container.size();

      container.addPoint(point, color);

      Assert.assertEquals("size", previousSize + 1, container.size());
      assertCloseTo("Point", point, container.getPoint(previousSize));
      Assert.assertEquals("Intensity", GVertexContainerTest.DEFAULT_INTENSITY, container.getIntensity(previousSize), 0);
      assertCloseTo("Normal", GVertexContainerTest.DEFAULT_NORMAL, container.getNormal(previousSize));
      assertCloseTo("Color", color, container.getColor(previousSize));
   }


   private static void testAsSortedContainer(final GVertex3Container container) {

      final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> newContainer;


      final Comparator<IVertexContainer.Vertex<IVector3>> comparador = new Comparator<IVertexContainer.Vertex<IVector3>>() {

         @Override
         public int compare(final IVertexContainer.Vertex<IVector3> vertex1,
                            final IVertexContainer.Vertex<IVector3> vertex2) {
            final IVector3 p1 = vertex1._point;
            final IVector3 p2 = vertex2._point;
            final IVector3 center = container.getAverage()._point;

            final double angle = GVectorUtils.getSignedAngle(center, p1, p2);

            if (angle < 0) {
               return -1;
            }
            else if (angle > 0) {
               return 1;
            }
            return 0;
         }

      };

      //-- Container tests

      newContainer = container.asSortedContainer(comparador);
      Assert.assertEquals("size", container.size(), newContainer.size());

      //      System.out.println("CONTAINER DATA: ");
      //      System.out.println("BEFORE SORTING:    AFTER SORTING:");
      for (int i = 0; i < newContainer.size(); i++) {
         assertCloseTo("Point", sortedPoints[i], newContainer.getPoint(i));
         //         System.out.print(container.getPoint(i).toString() + "    ");
         //         System.out.println(newContainer.getPoint(i).toString());
      }

      //-- Subcontainer tests

      final int[] subIndices = {
                        4,
                        1,
                        7,
                        3
      };
      final GSubVertexContainer<IVector3> subContainer = container.asSubContainer(subIndices);
      final GSubVertexContainer<IVector3> newSubContainer = subContainer.asSortedSubContainer(comparador);
      Assert.assertEquals("size", subContainer.size(), newSubContainer.size());

      //      System.out.println("SUBCONTAINER DATA: ");
      //      System.out.println("BEFORE SORTING:    AFTER SORTING:");
      for (int i = 0; i < newSubContainer.size(); i++) {
         assertCloseTo("SubPoint", sortedSubPoints[i], newSubContainer.getPoint(i));
         //         System.out.print(subContainer.getPoint(i).toString() + "    ");
         //         System.out.println(newSubContainer.getPoint(i).toString());
      }

   }


   @Test
   public void testContainer3WithIntensitiesNormalsAndColors() {
      testContainer(new GVertex3Container(GVectorPrecision.DOUBLE, GColorPrecision.FLOAT3, GProjection.EUCLID, true,
               GVertexContainerTest.DEFAULT_INTENSITY, true, GVertexContainerTest.DEFAULT_COLOR, true,
               GVertexContainerTest.DEFAULT_NORMAL));

      testContainer(new GVertex3Container(GVectorPrecision.FLOAT, GColorPrecision.INT, GProjection.EUCLID, true,
               GVertexContainerTest.DEFAULT_INTENSITY, true, GVertexContainerTest.DEFAULT_COLOR, true,
               GVertexContainerTest.DEFAULT_NORMAL));
   }


   private void testContainer(final GVertex3Container container) {
      Assert.assertEquals("empty", 0, container.size());

      // add vertex with given point, defaults for intensity, normal and color
      testAddVertex(container, points[0]);
      testAddVertex(container, points[1]);
      testAddVertex(container, points[2]);

      // add vertex with given point and intensity, defaults for normal and color
      testAddVertex(container, points[3], intensities[3]);
      testAddVertex(container, points[4], intensities[4]);

      // add vertex with given point and normal, defaults for intensity and color
      testAddVertex(container, points[5], normals[5]);
      testAddVertex(container, points[6], normals[6]);

      // add vertex with given point and color, defaults for intensity and normal
      testAddVertex(container, points[7], colors[7]);
      testAddVertex(container, points[8], colors[8]);

      testAsSortedContainer(container);

      //      for (int i = 0; i < container.getVertexCount(); i++) {
      //         System.out.println("#" + i + " " + container.getPoint(i) + ", i=" + container.getIntensity(i) + ", n="
      //                            + container.getNormal(i) + ", c=" + container.getColor(i));
      //      }

      for (int i = 0; i < points.length; i++) {
         assertCloseTo("point #" + i, points[i], container.getPoint(i));
         Assert.assertEquals("intensity #" + i, intensities[i], container.getIntensity(i), 0);
         assertCloseTo("normal #" + i, normals[i], container.getNormal(i));
         assertCloseTo("color #" + i, colors[i], container.getColor(i));
      }

      int i = 0;
      final Iterator<IVector3> iterator = container.pointsIterator();
      while (iterator.hasNext()) {
         final IVector3 point = iterator.next();
         assertCloseTo("point #" + i, points[i], point);
         i++;
      }
      Assert.assertEquals("iterator iterations", container.size(), i);


      //      System.out.println(Arrays.toString(container.getPointsDoubleArray()));

      //      final DoubleBuffer pointsBuffer = container.getPointsBufferD();
      //      for (int j = 0; j < pointsBuffer.capacity(); j += 3) {
      //         System.out.println(pointsBuffer.get() + ", " + pointsBuffer.get() + ", " + pointsBuffer.get());
      //      }
      //      System.out.println();
   }


   //   @Test
   //   public void testPointsIterator() {
   //      final GVertex3Container container = new GVertex3Container(GVectorPrecision.DOUBLE, GColorPrecision.FLOAT3, true,
   //               DEFAULT_INTENSITY, true, DEFAULT_COLOR, true, DEFAULT_NORMAL);
   //
   //      container.addPoint(new GVector3D(0, 0, 0));
   //
   //      final Iterator<IVector3> iterator = container.pointsIterator();
   //      while (iterator.hasNext()) {
   //         System.out.println(iterator.next());
   //      }
   //
   //      container.makeImmutable();
   //      final GOctree octree = new GOctree("", container, 100, 100, false);
   //   }

}
