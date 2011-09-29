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

import java.io.File;
import java.io.IOException;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

import es.igosoftware.euclid.colors.GColorI;
import es.igosoftware.euclid.colors.GColorPrecision;
import es.igosoftware.euclid.loading.GBinaryPoints3Loader;
import es.igosoftware.euclid.loading.GPointsLoader;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.GVectorPrecision;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.verticescontainer.GVertex3Container;
import es.igosoftware.euclid.verticescontainer.IUnstructuredVertexContainer;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;
import es.igosoftware.io.GFileName;


public class GBinaryPointsFormatTest {

   @Test
   public void testSave() throws IOException {
      // intensities, normals and colors
      testSaveAndLoad(new GVertex3Container(GVectorPrecision.DOUBLE, GColorPrecision.INT, GProjection.EUCLID, true, true, true));
      testSaveAndLoad(new GVertex3Container(GVectorPrecision.FLOAT, GColorPrecision.FLOAT3, GProjection.EUCLID, true, true, true));

      // normals and colors
      testSaveAndLoad(new GVertex3Container(GVectorPrecision.DOUBLE, GColorPrecision.INT, GProjection.EUCLID, false, true, true));
      testSaveAndLoad(new GVertex3Container(GVectorPrecision.FLOAT, GColorPrecision.FLOAT3, GProjection.EUCLID, false, true, true));

      // intensities and colors
      testSaveAndLoad(new GVertex3Container(GVectorPrecision.DOUBLE, GColorPrecision.INT, GProjection.EUCLID, true, false, true));
      testSaveAndLoad(new GVertex3Container(GVectorPrecision.FLOAT, GColorPrecision.FLOAT3, GProjection.EUCLID, true, false, true));

      // intensities and normals
      testSaveAndLoad(new GVertex3Container(GVectorPrecision.DOUBLE, GColorPrecision.INT, GProjection.EUCLID, true, true, false));
      testSaveAndLoad(new GVertex3Container(GVectorPrecision.FLOAT, GColorPrecision.FLOAT3, GProjection.EUCLID, true, true, false));

      // intensities
      testSaveAndLoad(new GVertex3Container(GVectorPrecision.DOUBLE, GColorPrecision.INT, GProjection.EUCLID, true, false, false));
      testSaveAndLoad(new GVertex3Container(GVectorPrecision.FLOAT, GColorPrecision.FLOAT3, GProjection.EUCLID, true, false,
               false));

      // normals 
      testSaveAndLoad(new GVertex3Container(GVectorPrecision.DOUBLE, GColorPrecision.INT, GProjection.EUCLID, false, true, false));
      testSaveAndLoad(new GVertex3Container(GVectorPrecision.FLOAT, GColorPrecision.FLOAT3, GProjection.EUCLID, false, true,
               false));

      // colors
      testSaveAndLoad(new GVertex3Container(GVectorPrecision.DOUBLE, GColorPrecision.INT, GProjection.EUCLID, false, false, true));
      testSaveAndLoad(new GVertex3Container(GVectorPrecision.FLOAT, GColorPrecision.FLOAT3, GProjection.EUCLID, false, false,
               true));
   }


   private void testSaveAndLoad(final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices)
                                                                                                                throws IOException {
      populateVertices3(vertices);

      final GFileName fileName = saveVertices(vertices);

      final GBinaryPoints3Loader loader = new GBinaryPoints3Loader(GPointsLoader.DEFAULT_FLAGS, fileName);

      loader.load();

      final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> readVertices = loader.getVertices();

      Assert.assertTrue("Precision", vertices.samePrecision(readVertices));
      Assert.assertTrue("Shape", vertices.sameShapeThan(readVertices));
      Assert.assertTrue("Contents", vertices.sameContents(readVertices));

      //      Assert.assertEquals("Dimensions", vertices.dimensions(), readVertices.dimensions());
      //
      //      Assert.assertSame("Vector Precision", vertices.vectorPrecision(), readVertices.vectorPrecision());
      //      Assert.assertSame("Color Precision", vertices.colorPrecision(), readVertices.colorPrecision());
      //
      //      Assert.assertSame("hasIntensities", vertices.hasIntensities(), readVertices.hasIntensities());
      //      Assert.assertSame("hasColors", vertices.hasColors(), readVertices.hasColors());
      //      Assert.assertSame("hasNormals", vertices.hasNormals(), readVertices.hasNormals());
      //
      //      Assert.assertEquals("size", vertices.size(), readVertices.size());
      //
      //      for (int i = 0; i < vertices.size(); i++) {
      //         Assert.assertEquals("point #" + i, vertices.getPoint(i), readVertices.getPoint(i));
      //
      //         if (vertices.hasIntensities()) {
      //            Assert.assertEquals("intensity #" + i, vertices.getIntensity(i), readVertices.getIntensity(i));
      //         }
      //
      //         if (vertices.hasColors()) {
      //            Assert.assertTrue("color #" + i, vertices.getColor(i).closeTo(readVertices.getColor(i)));
      //         }
      //
      //         if (vertices.hasNormals()) {
      //            Assert.assertTrue("normal #" + i, vertices.getNormal(i).closeTo(readVertices.getNormal(i)));
      //         }
      //      }

   }


   private void populateVertices3(final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices) {
      vertices.addPoint(new GVector3D(1, 10, 100), 1, GVector3D.X_UP, GColorI.WHITE);
      vertices.addPoint(new GVector3D(2, 20, 200), 2, GVector3D.Y_UP, GColorI.BLACK);
      vertices.addPoint(new GVector3D(3, 30, 300), 3, GVector3D.Z_UP, GColorI.newRGB(0.1f, 0.2f, 0.3f));
      vertices.addPoint(new GVector3D(4, 40, 400), 4, GVector3D.X_DOWN, GColorI.newRGB(0.2f, 0.4f, 0.6f));
      vertices.addPoint(new GVector3D(5, 50, 500), 5, GVector3D.Y_DOWN, GColorI.newRGB(0.3f, 0.8f, 0.9f));
      vertices.addPoint(new GVector3D(6, 60, 600), 6, GVector3D.Z_DOWN, GColorI.newRGB(0.4f, 0.8f, 0.9f));

      final Random random = new Random();
      for (int i = 0; i < 100000; i++) {
         final GVector3D point = new GVector3D(random.nextDouble(), random.nextDouble(), random.nextDouble());
         final float intensity = random.nextFloat();
         final GVector3D normal = new GVector3D(random.nextDouble(), random.nextDouble(), random.nextDouble()).normalized();
         final GColorI color = GColorI.newRGB(random.nextFloat(), random.nextFloat(), random.nextFloat());
         vertices.addPoint(point, intensity, normal, color);
      }
   }


   private GFileName saveVertices(final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices)
                                                                                                                  throws IOException {
      final File file = File.createTempFile("GBinaryPointsFormatTest-", ".bp");
      file.deleteOnExit();

      final GFileName fileName = GFileName.fromFile(file);

      GBinaryPoints3Loader.save((IUnstructuredVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?>) vertices,
               GProjection.EUCLID, fileName, false);

      return fileName;
   }

}
