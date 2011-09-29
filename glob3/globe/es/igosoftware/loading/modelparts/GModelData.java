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


package es.igosoftware.loading.modelparts;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.io.GFileName;
import es.igosoftware.loading.G3DModel;
import es.igosoftware.logging.GLogger;
import es.igosoftware.logging.ILogger;
import es.igosoftware.utils.GTexture;


public class GModelData
         implements
            Serializable {


   private static final ILogger logger = GLogger.instance();


   public static enum FaceCullingMode {
      BACK,
      FRONT,
      FRONT_AND_BACK;
   }


   private static final long      serialVersionUID   = 1L;

   private final GFileName        _fileName;

   private final List<IVector3>   _vertices          = new ArrayList<IVector3>();
   private final List<IVector3>   _normals           = new ArrayList<IVector3>();
   private final List<IVector2>   _texCoords         = new ArrayList<IVector2>();

   private final List<GMaterial>  _materials         = new ArrayList<GMaterial>();
   private final List<GModelMesh> _meshes            = new ArrayList<GModelMesh>();

   //   private final List<GModelMesh>  _transparentMeshes = new ArrayList<GModelMesh>();

   private GAxisAlignedBox        _bounds            = null;

   private boolean                _useTexture        = false;
   private boolean                _renderAsWireframe = false;
   private boolean                _useLighting       = true;

   private Boolean                _isOpaque          = null;

   private FaceCullingMode        _faceCullingMode   = FaceCullingMode.BACK;

   private G3DModel               _3DModel;


   // Constructor
   public GModelData(final GFileName fileName) {
      _fileName = fileName;
   }


   //Materials
   public void addMaterial(final GMaterial mat) {
      _materials.add(mat);
   }


   public List<GMaterial> getMaterials() {
      return Collections.unmodifiableList(_materials);
   }


   //Meshes
   public void addMesh(final GModelMesh mesh) {
      _isOpaque = null;
      mesh.setModel(this);
      _meshes.add(mesh);
   }


   //   public void addTransparentMesh(final GModelMesh mesh) {
   //      _isOpaque = false;
   //      mesh.setModel(this);
   //      _transparentMeshes.add(mesh);
   //   }


   //   public void sortMeshes() {
   //      final List<GModelMesh> meshesToRemove = new ArrayList<GModelMesh>();
   //      for (final GModelMesh mesh : _meshes) {
   //         if (!mesh.isOpaque()) {
   //            addTransparentMesh(mesh);
   //            meshesToRemove.add(mesh);
   //         }
   //      }
   //      for (final GModelMesh mesh : meshesToRemove) {
   //         _meshes.remove(mesh);
   //      }
   //
   //
   //   }


   public List<GModelMesh> getMeshes() {
      return Collections.synchronizedList(_meshes);

   }


   //   public List<GModelMesh> getTransparentMeshes() {
   //      return Collections.synchronizedList(_transparentMeshes);
   //      //return _transparentMeshes;
   //   }


   public void addVertex(final IVector3 vertex) {
      _bounds = null; // invalidate bounds to force recalculation
      _vertices.add(vertex);
   }


   public void addNormal(final IVector3 normal) {
      _normals.add(normal);
   }


   public void addTexCoord(final IVector2 uv) {
      _texCoords.add(uv);
   }


   public GFileName getFileName() {
      return _fileName;
   }


   public GAxisAlignedBox getBounds() {
      if (_bounds == null) {
         _bounds = GAxisAlignedBox.minimumBoundingBox(_vertices);
      }
      return _bounds;
   }


   public boolean isUsingLighting() {
      return _useLighting;
   }


   public boolean isUsingTexture() {
      return _useTexture;
   }


   public void setUseTexture(final boolean useTexture) {
      _useTexture = useTexture;
   }


   public void setUseLighting(final boolean useLighting) {
      _useLighting = useLighting;
   }


   public boolean isRenderAsWireframe() {
      return _renderAsWireframe;
   }


   public void setRenderAsWireframe(final boolean renderAsWireframe) {
      _renderAsWireframe = renderAsWireframe;
   }


   public List<IVector3> getVertices() {
      return Collections.unmodifiableList(_vertices);
   }


   public List<IVector3> getNormals() {
      return Collections.unmodifiableList(_normals);
   }


   public List<IVector2> getTexCoords() {
      return Collections.unmodifiableList(_texCoords);
   }


   public boolean isOpaque() {
      if (_isOpaque == null) {
         _isOpaque = Boolean.TRUE;
         for (final GModelMesh mesh : _meshes) {
            if (!mesh.isOpaque()) {
               _isOpaque = Boolean.FALSE;
               return false;
            }
         }
      }

      return _isOpaque.booleanValue();
   }


   @Override
   public String toString() {
      return "GModelData [name=" + _fileName + "]";
   }


   public FaceCullingMode getFaceCullingMode() {
      return _faceCullingMode;
   }


   public void setFaceCullingMode(final FaceCullingMode faceCullingMode) {
      _faceCullingMode = faceCullingMode;
   }


   public void showStatistics() {
      logger.logInfo("  Name: " + _fileName);

      logger.logInfo("  Vertices: " + _vertices.size());
      logger.logInfo("  Normals: " + _normals.size());
      logger.logInfo("  TexCoords: " + _texCoords.size());

      logger.logInfo("  Meshes: " + _meshes.size());
      logger.logInfo("  Materials: " + _materials.size());

      logger.logInfo("  Bound: " + getBounds());
      logger.logInfo("  Use Texture: " + _useTexture);

      logger.logInfo("  Render as Wireframe: " + _renderAsWireframe);
      logger.logInfo("  Use Lighting: " + _useLighting);

      logger.logInfo("  Opaque: " + isOpaque());

      logger.logInfo("  Face Culling: " + _faceCullingMode);

      int facesCount = 0;
      int verticesIndicesCount = 0;
      for (final GModelMesh mesh : _meshes) {
         //         mesh.showStatistics();

         final List<GFace> faces = mesh.getFaces();
         facesCount += faces.size();
         for (final GFace face : faces) {
            verticesIndicesCount += face._vertexIndices.length;
         }
      }

      logger.logInfo("  Faces: " + facesCount);
      logger.logInfo("  Total Faces VerticesIndices: " + verticesIndicesCount);
      logger.logInfo("  VerticesIndices per Face (average): " + ((float) verticesIndicesCount / facesCount));
   }


   public void set3DModel(final G3DModel model) {
      if (_3DModel != null) {
         throw new RuntimeException("The receiver (" + this + ") already has an " + G3DModel.class.getSimpleName());
      }

      _3DModel = model;
   }


   public void dispose() {
      for (final GModelMesh mesh : _meshes) {
         mesh.dispose();
      }
   }


   public G3DModel get3DModel() {
      return _3DModel;
   }


   GTexture getTexture(final URL url,
                       final boolean mipmap) {
      return _3DModel.getTexture(url, mipmap);
   }


}
