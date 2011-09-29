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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.colors.GColorI;
import es.igosoftware.euclid.colors.GColorPrecision;
import es.igosoftware.euclid.octree.GOctree;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.GVectorPrecision;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.verticescontainer.GVertex3Container;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;
import es.igosoftware.util.GCollections;


public class GOctreeTest {

   @Test
   public void test1() {

      //      final GVertex3Container vertices = new GVertex3Container(GVectorPrecision.DOUBLE, GColorPrecision.INT, false, 0, true,
      //               GColorI.WHITE, false, null);
      //
      //      final double bottom = -50;
      //      final double top = 50;
      //      final double step = 0.5;
      //
      //      for (double x = bottom; x < top; x += step) {
      //         for (double y = bottom; y < top; y += step) {
      //            for (double z = bottom; z < top; z += step) {
      //               vertices.addPoint(new GVector3D(x, y, z));
      //            }
      //         }
      //      }
      //
      //      System.out.println(vertices);
      //
      //      vertices.makeImmutable();
      //      final GOctree octree = new GOctree("test", vertices, 10, Integer.MAX_VALUE, true, true);
      //
      //
      //      octree.depthFirstAcceptVisitor(new GOTLeafVisitor() {
      //         @Override
      //         public void visitLeaf(final GOTLeafNode leaf) {
      //            final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> leafVertices = leaf.getVertices();
      //            //                  System.out.println(leafVertices);
      //            Assert.assertEquals("vertices per leaf", 8000, leafVertices.size());
      //         }
      //      });

   }


   @Test
   public void testVerticesInRegion() {
      final GVertex3Container vertices = new GVertex3Container(GVectorPrecision.FLOAT, GColorPrecision.INT, GProjection.EUCLID,
               false, 0, true, GColorI.WHITE, false, null);

      //      final double bottom = 1;
      //      final double top = 100;
      //      //final double step = 0.5;
      //      final double step = 0.5;
      //
      //      for (double x = bottom; x <= top; x += step) {
      //         for (double y = bottom; y <= top; y += step) {
      //            vertices.addPoint(new GVector3D(x, y, 0));
      //         }
      //      }

      final Random random = new Random();
      for (int i = 0; i < 500000; i++) {
         final GVector3D point = new GVector3D(random.nextDouble() * 100, random.nextDouble() * 100, 0);
         vertices.addPoint(point);
      }

      vertices.makeImmutable();
      final GOctree octree = new GOctree("test", vertices, new GOctree.Parameters(10, Integer.MAX_VALUE, false, true, true));

      checkVerticesInRegion(octree, new GAxisAlignedBox(GVector3D.ZERO, new GVector3D(5, 5, 0)));

      checkVerticesInRegion(octree, new GAxisAlignedBox(new GVector3D(5, 5, 0), new GVector3D(5, 5, 0)));

      checkVerticesInRegion(octree, new GAxisAlignedBox(GVector3D.ZERO, GVector3D.ZERO));

      checkVerticesInRegion(octree, new GAxisAlignedBox(GVector3D.ZERO, new GVector3D(100, 100, 0)));
   }


   private void checkVerticesInRegion(final GOctree octree,
                                      final GAxisAlignedBox region) {


      final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> returned = octree.getVerticesInRegion(region);

      //      System.out.println("Region: " + region + ", vertices: " + returned);

      // check that all the returned points are inside the region
      for (int i = 0; i < returned.size(); i++) {
         final IVector3 point = returned.getPoint(i);
         Assert.assertTrue("point in region " + point, region.contains(point));
      }


      // check no point outside the returned vertices are inside the region
      final HashSet<IVector3> returnedSet = new HashSet<IVector3>();
      for (int i = 0; i < returned.size(); i++) {
         returnedSet.add(returned.getPoint(i));
      }

      final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices = octree.getOriginalVertices();
      for (int i = 0; i < vertices.size(); i++) {
         final IVector3 pointOutOfRegion = vertices.getPoint(i);
         if (returnedSet.contains(pointOutOfRegion)) {
            continue;
         }

         Assert.assertFalse("point out of region " + pointOutOfRegion, region.contains(pointOutOfRegion));
      }

   }


   @Test
   public void testDuplicationPolicy() {
      final GVertex3Container vertices = new GVertex3Container(GVectorPrecision.FLOAT, GColorPrecision.INT, GProjection.EUCLID,
               false, 0, true, GColorI.WHITE, false, null);

      final double bottom = 1;
      final double top = 100;
      final double step = 1;

      for (double x = bottom; x <= top; x += step) {
         for (double y = bottom; y <= top; y += step) {
            vertices.addPoint(new GVector3D(x, y, 0));
            vertices.addPoint(new GVector3D(x, y, 0)); // adds a duplicated
         }
      }

      vertices.makeImmutable();
      //      System.out.println(vertices);

      final GOctree.DuplicatesPolicy duplicatesPolicy = new GOctree.DuplicatesPolicy() {
         @Override
         public int[] removeDuplicates(final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices1,
                                       final int[] verticesIndexes) {
            final Set<IVector3> selectedPoints = new HashSet<IVector3>();
            final List<Integer> selectedIndices = new ArrayList<Integer>();

            for (final int index : verticesIndexes) {
               final IVector3 point = vertices1.getPoint(index);
               if (!selectedPoints.contains(point)) {
                  selectedPoints.add(point);
                  selectedIndices.add(index);
               }
            }

            return GCollections.toIntArray(selectedIndices);
         }
      };

      final GOctree octree = new GOctree("test", vertices, new GOctree.Parameters(10, Integer.MAX_VALUE, false, true, true,
               duplicatesPolicy));

      Assert.assertEquals(octree.getVerticesIndexesCount(), 10000);
   }


   private void assertCloseTo(final IVector3 expected,
                              final IVector3 got) {
      if (expected.closeTo(got)) {
         return;
      }

      Assert.fail("expected " + expected + " but got " + got);
   }


   @Test
   public void testClosestPoint() {
      final GVertex3Container vertices = new GVertex3Container(GVectorPrecision.FLOAT, GColorPrecision.INT, GProjection.EUCLID,
               false, 0, true, GColorI.WHITE, false, null);

      final double bottom = 1;
      final double top = 100;
      final double step = 1;

      for (double x = bottom; x <= top; x += step) {
         for (double y = bottom; y <= top; y += step) {
            vertices.addPoint(new GVector3D(x, y, 0));
         }
      }

      vertices.makeImmutable();
      //      System.out.println(vertices);


      final GOctree octree = new GOctree("test", vertices, new GOctree.Parameters(Double.POSITIVE_INFINITY, 1, false, true, true));

      assertCloseTo(new GVector3D(1, 1, 0), octree.getNearestPoint(GVector3D.ZERO));
      assertCloseTo(new GVector3D(1, 1, 0), octree.getNearestPoint(new GVector3D(-1000, -1000, -1000)));
      assertCloseTo(new GVector3D(100, 100, 0), octree.getNearestPoint(new GVector3D(1000, 1000, 1000)));
      assertCloseTo(new GVector3D(2, 2, 0), octree.getNearestPoint(new GVector3D(2, 2, 0)));
      assertCloseTo(new GVector3D(2, 2, 0), octree.getNearestPoint(new GVector3D(1.99, 1.99, 0)));
      assertCloseTo(new GVector3D(2, 2, 0), octree.getNearestPoint(new GVector3D(2.01, 2.01, 0)));
      assertCloseTo(new GVector3D(13, 13, 0), octree.getNearestPoint(new GVector3D(13.38, 13.38, 0)));
   }

}
