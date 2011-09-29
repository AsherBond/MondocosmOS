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


package es.igosoftware.scenegraph;

import java.util.ArrayList;
import java.util.List;

import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.loading.G3DModel;
import es.igosoftware.loading.modelparts.GFace;
import es.igosoftware.loading.modelparts.GModelMesh;
import es.igosoftware.scenegraph.GPositionRenderableLayer.PickResult;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GPair;
import es.igosoftware.util.IFunction;
import es.igosoftware.utils.GWWUtils;
import gov.nasa.worldwind.geom.Box;
import gov.nasa.worldwind.geom.Extent;
import gov.nasa.worldwind.geom.Line;
import gov.nasa.worldwind.geom.Matrix;
import gov.nasa.worldwind.geom.Sphere;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.render.DrawContext;


public class G3DModelNode
         extends
            GMutableNodeAbstract<G3DModelNode>
         implements
            ILeafNode {


   public static enum PickableBoundsType {
      SPHERE,
      BOX;
   }


   private final G3DModel                        _model;

   private Box                                   _boundsInGlobalCoordinates;

   private final List<GPair<GModelMesh, Object>> _pickableMeshes       = new ArrayList<GPair<GModelMesh, Object>>();
   private final List<Extent>                    _pickableMeshesBounds = new ArrayList<Extent>();

   //   private long                                  _lastFrameTimeStamp;

   private final float[]                         _modelViewMatrixArray = new float[16];

   private boolean                               _pickable             = false;
   private boolean                               _renderPickableBounds = false;
   private PickableBoundsType                    _pickableBoundsType   = PickableBoundsType.BOX;


   public G3DModelNode(final String name,
                       final GTransformationOrder order,
                       final G3DModel model) {
      super(name, order);

      GAssert.notNull(model, "model");
      _model = model;

      //      _bounds = GWWUtils.toBox(_model.getBounds());
   }


   @Override
   public void setPickable(final boolean pickable) {
      _pickable = pickable;
   }


   @Override
   public boolean isPickable() {
      return _pickable;
   }


   @Override
   public void preRender(final DrawContext dc,
                         final Matrix parentMatrix,
                         final boolean terrainChanged) {
      //      final long frameTimeStamp = dc.getFrameTimeStamp();
      //      if (frameTimeStamp != _lastFrameTimeStamp) {
      //         _lastFrameTimeStamp = frameTimeStamp;
      //      }
   }


   @Override
   protected void doRender(final DrawContext dc,
                           final Matrix parentMatrix,
                           final boolean terrainChanged) {

      final Matrix modelMatrix = getGlobalMatrix(parentMatrix);

      final Matrix modelViewMatrix = dc.getView().getModelviewMatrix().multiply(modelMatrix);
      GWWUtils.toGLArray(modelViewMatrix, _modelViewMatrixArray);

      if (_renderPickableBounds) {
         if (_pickableMeshesBounds != null) {
            for (final Extent each : _pickableMeshesBounds) {
               GWWUtils.renderExtent(dc, each);
            }
         }
      }

      _model.render(dc, modelMatrix, _modelViewMatrixArray, hasScaleTransformation(), this);
   }


   @Override
   public String toString() {
      return "G3DModelNode [name=" + getName() + ", model=" + _model.getModelData().getFileName().buildPath()
             + getTransformationString() + "]";
   }


   @Override
   public void dispose() {
      super.dispose();

      _model.dispose();
   }


   @Override
   public void cleanCaches() {
      super.cleanCaches();

      _boundsInGlobalCoordinates = null;
   }


   public void addPickableMesh(final GModelMesh mesh,
                               final Object userData) {

      if (mesh.getModel() != _model.getModelData()) {
         throw new RuntimeException("Can't add a mesh of another model");
      }

      setPickable(true);
      _pickableMeshes.add(new GPair<GModelMesh, Object>(mesh, userData));
   }


   @Override
   public Box getBoundsInModelCoordinates(final Matrix parentMatrix,
                                          final boolean matrixChanged) {
      if (matrixChanged || (_boundsInGlobalCoordinates == null)) {
         final Matrix globalMatrix = getGlobalMatrix(parentMatrix);

         final List<Vec4> verticesInGlobalCoordinates = GCollections.collect(_model.getVertices(),
                  new IFunction<IVector3, Vec4>() {
                     @Override
                     public Vec4 apply(final IVector3 element) {
                        final Vec4 vec4 = GWWUtils.toVec4(element);
                        final Vec4 transformed = vec4.transformBy4(globalMatrix);
                        return GWWUtils.toVec3(transformed);
                     }
                  });

         _boundsInGlobalCoordinates = Box.computeBoundingBox(verticesInGlobalCoordinates);
      }

      return _boundsInGlobalCoordinates;
   }


   @Override
   protected boolean doPick(final DrawContext dc,
                            final Matrix parentMatrix,
                            final boolean terrainChanged,
                            final Line ray,
                            final List<PickResult> pickResults) {

      if (terrainChanged || (_pickableMeshesBounds.size() != _pickableMeshes.size())) {
         final Matrix globalMatrix = getGlobalMatrix(parentMatrix);

         _pickableMeshesBounds.clear();

         final List<IVector3> modelVertices = _model.getVertices();

         for (final GPair<GModelMesh, Object> meshAndUserData : _pickableMeshes) {
            final GModelMesh mesh = meshAndUserData._first;

            final List<Vec4> meshVec4s = new ArrayList<Vec4>();

            for (final GFace face : mesh.getFaces()) {
               for (final int vertexIndex : face._vertexIndices) {
                  final IVector3 vertex = modelVertices.get(vertexIndex);

                  final Vec4 vertexVec4 = GWWUtils.toVec4(vertex);
                  final Vec4 transformedVec4 = GWWUtils.toVec3(vertexVec4.transformBy4(globalMatrix));

                  meshVec4s.add(transformedVec4);
               }
            }

            _pickableMeshesBounds.add(calculatePickableBounds(meshVec4s));
         }
      }

      boolean picked = false;

      for (int i = 0; i < _pickableMeshesBounds.size(); i++) {
         final Extent bound = _pickableMeshesBounds.get(i);
         if (bound.intersects(ray)) {
            final GPair<GModelMesh, Object> pair = _pickableMeshes.get(i);
            final GModelMesh mesh = pair._first;
            final Object userData = pair._second;

            final Vec4 position = bound.getCenter();
            pickResults.add(new PickResult(mesh, userData, position));

            picked = true;
         }
      }

      return picked;
   }


   private Extent calculatePickableBounds(final List<Vec4> points) {
      switch (_pickableBoundsType) {
         case BOX:
            return Box.computeBoundingBox(points);
         case SPHERE:
            return Sphere.createBoundingSphere(points.toArray(new Vec4[0]));
      }

      throw new RuntimeException("Unsupported Pickable Bounds type " + _pickableBoundsType);
   }


   public boolean isRenderPickableBounds() {
      return _renderPickableBounds;
   }


   public void setRenderPickableBounds(final boolean renderPickableBounds) {
      _renderPickableBounds = renderPickableBounds;
   }


   public PickableBoundsType getPickableBoundsType() {
      return _pickableBoundsType;
   }


   public void setPickableBoundsType(final PickableBoundsType pickableBoundsType) {
      _pickableBoundsType = pickableBoundsType;
   }

}
