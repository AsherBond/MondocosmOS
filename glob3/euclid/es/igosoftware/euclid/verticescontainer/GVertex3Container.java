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


package es.igosoftware.euclid.verticescontainer;

import es.igosoftware.euclid.colors.GColorI;
import es.igosoftware.euclid.colors.GColorPrecision;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.GVectorPrecision;
import es.igosoftware.euclid.vector.IVector3;


public final class GVertex3Container
         extends
            GVertexContainerWithDefaultsAbstract<IVector3, GVertex3Container> {


   public GVertex3Container(final GVectorPrecision vectorPrecision,
                            final GColorPrecision colorPrecision,
                            final GProjection projection,
                            final boolean withIntensities,
                            final boolean withColors,
                            final boolean withNormals) {
      this(vectorPrecision, colorPrecision, projection, withIntensities, 0, withColors, null, withNormals, null);
   }


   public GVertex3Container(final GVectorPrecision vectorPrecision,
                            final GColorPrecision colorPrecision,
                            final GProjection projection,
                            final boolean withIntensities,
                            final boolean withColors,
                            final boolean withNormals,
                            final boolean withUserData) {
      this(vectorPrecision, colorPrecision, projection, withIntensities, 0, withColors, null, withNormals, null, withUserData, 0);
   }


   public GVertex3Container(final GVectorPrecision vectorPrecision,
                            final GColorPrecision colorPrecision,
                            final GProjection projection,
                            final int initialCapacity,
                            final boolean withIntensities,
                            final boolean withColors,
                            final boolean withNormals) {
      this(vectorPrecision, colorPrecision, projection, GVector3D.ZERO, initialCapacity, withIntensities, 0, withColors, null,
           withNormals, null);
   }


   public GVertex3Container(final GVectorPrecision vectorPrecision,
                            final GColorPrecision colorPrecision,
                            final GProjection projection,
                            final int initialCapacity,
                            final boolean withIntensities,
                            final boolean withColors,
                            final boolean withNormals,
                            final boolean withUserData) {
      this(vectorPrecision, colorPrecision, projection, GVector3D.ZERO, initialCapacity, withIntensities, 0, withColors, null,
           withNormals, null, withUserData, 0);
   }


   public GVertex3Container(final GVectorPrecision vectorPrecision,
                            final GColorPrecision colorPrecision,
                            final GProjection projection,
                            final boolean withIntensities,
                            final float defaultIntensity,
                            final boolean withColors,
                            final IColor defaultColor,
                            final boolean withNormals,
                            final IVector3 defaultNormal) {
      this(vectorPrecision, colorPrecision, projection, GVector3D.ZERO, 3, withIntensities, defaultIntensity, withColors,
           defaultColor, withNormals, defaultNormal);
   }


   public GVertex3Container(final GVectorPrecision vectorPrecision,
                            final GColorPrecision colorPrecision,
                            final GProjection projection,
                            final boolean withIntensities,
                            final float defaultIntensity,
                            final boolean withColors,
                            final IColor defaultColor,
                            final boolean withNormals,
                            final IVector3 defaultNormal,
                            final boolean withUserData,
                            final long defaultUserData) {
      this(vectorPrecision, colorPrecision, projection, GVector3D.ZERO, 3, withIntensities, defaultIntensity, withColors,
           defaultColor, withNormals, defaultNormal, withUserData, defaultUserData);
   }


   public GVertex3Container(final GVectorPrecision vectorPrecision,
                            final GColorPrecision colorPrecision,
                            final GProjection projection,
                            final IVector3 referencePoint,
                            final int initialCapacity,
                            final boolean withIntensities,
                            final boolean withColors,
                            final boolean withNormals) {
      this(vectorPrecision, colorPrecision, projection, referencePoint, initialCapacity, withIntensities, 0, withColors, null,
           withNormals, null);
   }


   public GVertex3Container(final GVectorPrecision vectorPrecision,
                            final GColorPrecision colorPrecision,
                            final GProjection projection,
                            final IVector3 referencePoint,
                            final int initialCapacity,
                            final boolean withIntensities,
                            final boolean withColors,
                            final boolean withNormals,
                            final boolean withUserData) {
      this(vectorPrecision, colorPrecision, projection, referencePoint, initialCapacity, withIntensities, 0, withColors, null,
           withNormals, null, withUserData, 0);
   }


   public GVertex3Container(final GVectorPrecision vectorPrecision,
                            final GColorPrecision colorPrecision,
                            final GProjection projection,
                            final IVector3 referencePoint,
                            final boolean withIntensities,
                            final float defaultIntensity,
                            final boolean withColors,
                            final IColor defaultColor,
                            final boolean withNormals,
                            final IVector3 defaultNormal) {
      this(vectorPrecision, colorPrecision, projection, referencePoint, 3, withIntensities, defaultIntensity, withColors,
           defaultColor, withNormals, defaultNormal);
   }


   public GVertex3Container(final GVectorPrecision vectorPrecision,
                            final GColorPrecision colorPrecision,
                            final GProjection projection,
                            final IVector3 referencePoint,
                            final boolean withIntensities,
                            final float defaultIntensity,
                            final boolean withColors,
                            final IColor defaultColor,
                            final boolean withNormals,
                            final IVector3 defaultNormal,
                            final boolean withUserData,
                            final long defaultUserData) {
      this(vectorPrecision, colorPrecision, projection, referencePoint, 3, withIntensities, defaultIntensity, withColors,
           defaultColor, withNormals, defaultNormal, withUserData, defaultUserData);
   }


   public GVertex3Container(final GVectorPrecision vectorPrecision,
                            final GColorPrecision colorPrecision,
                            final GProjection projection,
                            final IVector3 referencePoint,
                            final int initialCapacity,
                            final boolean withIntensities,
                            final float defaultIntensity,
                            final boolean withColors,
                            final IColor defaultColor,
                            final boolean withNormals,
                            final IVector3 defaultNormal) {
      this(vectorPrecision, colorPrecision, projection, referencePoint, initialCapacity, withIntensities, defaultIntensity,
           withColors, defaultColor, withNormals, defaultNormal, false, 0);
   }


   public GVertex3Container(final GVectorPrecision vectorPrecision,
                            final GColorPrecision colorPrecision,
                            final GProjection projection,
                            final int initialCapacity,
                            final boolean withIntensities,
                            final float defaultIntensity,
                            final boolean withColors,
                            final IColor defaultColor,
                            final boolean withNormals,
                            final IVector3 defaultNormal) {
      this(vectorPrecision, colorPrecision, projection, GVector3D.ZERO, initialCapacity, withIntensities, defaultIntensity,
           withColors, defaultColor, withNormals, defaultNormal, false, 0);
   }


   public GVertex3Container(final GVectorPrecision vectorPrecision,
                            final GColorPrecision colorPrecision,
                            final GProjection projection,
                            final IVector3 referencePoint,
                            final int initialCapacity,
                            final boolean withIntensities,
                            final float defaultIntensity,
                            final boolean withColors,
                            final IColor defaultColor,
                            final boolean withNormals,
                            final IVector3 defaultNormal,
                            final boolean withUserData,
                            final long defaultUserData) {
      super(vectorPrecision, colorPrecision, projection, referencePoint, initialCapacity, withIntensities, defaultIntensity,
            withColors, defaultColor, withNormals, defaultNormal, withUserData, defaultUserData);
   }


   public GVertex3Container(final GVectorPrecision vectorPrecision,
                            final GColorPrecision colorPrecision,
                            final GProjection projection,
                            final int initialCapacity,
                            final boolean withIntensities,
                            final float defaultIntensity,
                            final boolean withColors,
                            final IColor defaultColor,
                            final boolean withNormals,
                            final IVector3 defaultNormal,
                            final boolean withUserData,
                            final long defaultUserData) {
      super(vectorPrecision, colorPrecision, projection, GVector3D.ZERO, initialCapacity, withIntensities, defaultIntensity,
            withColors, defaultColor, withNormals, defaultNormal, withUserData, defaultUserData);
   }


   @Override
   public byte dimensions() {
      return 3;
   }


   @Override
   protected GVertexContainerWithDefaultsAbstract.VectorHandler<IVector3> initializePointsHandler() {
      switch (_vectorPrecision) {
         case FLOAT:
            return new Vector3HandlerF(_capacity);
         case DOUBLE:
            return new Vector3HandlerD(_capacity);
         default:
            throw new IllegalArgumentException("Invalid vector precision");
      }
   }


   @Override
   protected GVertexContainerWithDefaultsAbstract.VectorHandler<IVector3> initializeNormalsHandler() {
      switch (_vectorPrecision) {
         case FLOAT:
            return new Vector3HandlerF(_capacity);
         case DOUBLE:
            return new Vector3HandlerD(_capacity);
         default:
            throw new IllegalArgumentException("Invalid vector precision");
      }
   }


   @Override
   protected String getStringName() {
      return "GVertex3Container";
   }


   @Override
   public GVertex3Container newEmptyContainer(final int initialCapacity) {
      return newEmptyContainer(initialCapacity, _projection, GVector3D.ZERO);
   }


   @Override
   public GVertex3Container newEmptyContainer(final int initialCapacity,
                                              final GProjection projection) {
      return newEmptyContainer(initialCapacity, projection, GVector3D.ZERO);
   }


   @Override
   public GVertex3Container newEmptyContainer(final int initialCapacity,
                                              final IVector3 referencePoint) {
      return newEmptyContainer(initialCapacity, _projection, referencePoint);
   }


   @Override
   public GVertex3Container newEmptyContainer(final int initialCapacity,
                                              final GProjection projection,
                                              final IVector3 referencePoint) {

      return new GVertex3Container(_vectorPrecision, _colorPrecision, projection, referencePoint, initialCapacity,
               hasIntensities(), _defaultIntensity, hasColors(), _defaultColor, hasNormals(), _defaultNormal, hasUserData(),
               _defaultUserData);

   }


   public static void main(final String[] args) {

      final GVertex3Container container3 = new GVertex3Container(GVectorPrecision.FLOAT, GColorPrecision.INT,
               GProjection.EPSG_23029, false, true, false);

      container3.addPoint(new GVector3D(1, 10, 100.0003345), GColorI.newRGB(0.1f, 0.11f, 0.111f));
      container3.addPoint(new GVector3D(2, 20.1220045, 200), GColorI.newRGB(0.2f, 0.22f, 0.222f));
      container3.addPoint(new GVector3D(3.10000234, 30, 300), GColorI.newRGB(0.3f, 0.33f, 0.333f));

      System.out.println(container3.toString());

      final GVertex3Container container3Reprojected = container3.reproject(GProjection.EPSG_4326);

      System.out.println(container3Reprojected.toString());

      for (int i = 0; i < container3.size(); i++) {
         final IVertexContainer.Vertex<IVector3> vertex = container3.getVertex(i);
         System.out.println(vertex.toString());
      }

      //final IVertexContainer.Vertex<IVector3> v1 = container3.getVertex(0);


      //               final ByteBuffer points3Colors = container3.getPointsAndColorsByteBuffer();
      //               System.out.println(points3Colors);
      //         
      //               GBufferUtils.show(points3Colors.asFloatBuffer());
      //   

      //      final GVertex2Container container2 = new GVertex2Container(GVectorPrecision.FLOAT, GColorPrecision.INT, false, true, false);
      //
      //      container2.addPoint(new GVector2D(1, 10), GColorI.newRGB(0.1f, 0.11f, 0.111f));
      //      container2.addPoint(new GVector2D(2, 20), GColorI.newRGB(0.2f, 0.22f, 0.222f));
      //      container2.addPoint(new GVector2D(3, 30), GColorI.newRGB(0.3f, 0.33f, 0.333f));
      //
      //      final ByteBuffer points2Colors = container2.getPointsAndColorsByteBuffer();
      //      System.out.println(points2Colors);
      //
      //      GBufferUtils.show(points2Colors.asFloatBuffer());
   }


}
