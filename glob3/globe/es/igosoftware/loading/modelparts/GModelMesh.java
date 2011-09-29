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

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.igosoftware.euclid.mutability.GMutableAbstract;
import es.igosoftware.euclid.mutability.IMutable;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.io.GFileName;
import es.igosoftware.loading.GResourceRetriever;
import es.igosoftware.logging.GLogger;
import es.igosoftware.logging.ILogger;
import es.igosoftware.util.GPair;
import es.igosoftware.utils.GGLUtils;
import es.igosoftware.utils.GTexture;
import es.igosoftware.utils.GWWUtils;
import gov.nasa.worldwind.geom.Matrix;
import gov.nasa.worldwind.geom.Vec4;


public class GModelMesh
         extends
            GMutableAbstract<GModelMesh>
         implements
            Serializable,
            IMutable.ChangeListener {

   private static final long    serialVersionUID = 1L;

   private static final ILogger logger           = GLogger.instance();


   private final String         _name;

   //   public int                _materialID      = 0;
   private GMaterial            _material;
   private final List<GFace>    _faces           = new ArrayList<GFace>();

   public boolean               _hasTexCoords    = false;
   public boolean               _hasNormals      = false;

   public boolean               _smoothShadeMode = true;

   private GModelData           _model;

   private transient Vec4       _centroid;
   private transient Vec4       _centroidVec4;
   private transient Matrix     _lastModelMatrix;

   private GTexture             _texture;


   public GModelMesh(final String name) {
      _name = name;
   }


   public void addFace(final GFace face) {
      _faces.add(face);

      _centroidVec4 = null;
   }


   public List<GFace> getFaces() {
      return Collections.unmodifiableList(_faces);
   }


   public String getName() {
      return _name;
   }


   public GMaterial getMaterial() {
      return _material;
   }


   public void setMaterial(final GMaterial material) {
      if (_material != null) {
         _material.removeChangeListener(this);
      }

      _material = material;

      if (_material != null) {
         _material.addChangeListener(this);
      }
   }


   public GModelData getModel() {
      return _model;
   }


   public void setModel(final GModelData model) {
      _model = model;
   }


   public boolean isOpaque() {
      if (_material != null) {
         final Color color = _material._diffuseColor;
         if (color != null) {
            if (color.getAlpha() < 255) {
               return false;
            }
         }

         final GFileName textureFileName = _material.getTextureFileName();
         if (textureFileName != null) {
            final String textureFileNameLowerCase = textureFileName.buildPath().toLowerCase();

            if (textureFileNameLowerCase.endsWith(".png")) {
               return false;
            }
            if (textureFileNameLowerCase.endsWith(".tga")) {
               return false;
            }
         }
      }

      return true;
   }


   public Vec4 getCentroid(final Matrix modelMatrix) {

      if ((modelMatrix != _lastModelMatrix) || (_centroidVec4 == null)) {
         //         System.out.println("Calculating centroid for " + this);
         _lastModelMatrix = modelMatrix;

         final Vec4 centroid = getCentroid();

         final Vec4 transformed = centroid.transformBy4(modelMatrix);

         _centroidVec4 = GWWUtils.toVec3(transformed);
      }

      return _centroidVec4;
   }


   private Vec4 getCentroid() {
      if (_centroid == null) {
         final List<IVector3> vertices = _model.getVertices();

         IVector3 min = GVector3D.POSITIVE_INFINITY;
         IVector3 max = GVector3D.NEGATIVE_INFINITY;

         for (final GFace face : _faces) {
            for (final int vertexIndex : face._vertexIndices) {

               final IVector3 vertex = vertices.get(vertexIndex);
               min = min.min(vertex);
               max = max.max(vertex);
            }
         }

         _centroid = GWWUtils.toVec4(min.add(max).scale(0.5));
      }

      return _centroid;
   }


   @Override
   public String toString() {
      return "GModelMesh [name=" + _name + ", model=" + _model.getFileName() + ", faces=" + _faces.size() + "]";
   }


   @Override
   public void mutableChanged() {
      changed();
   }


   private URL getTextureURL() {
      if (_material == null) {
         return null;
      }

      final GFileName textureFileName = _material.getTextureFileName();
      if (textureFileName == null) {
         return null;
      }


      // try textureFileName as Absolute
      final File textureFile = textureFileName.asFile();
      if (textureFile.exists()) {
         try {
            return textureFile.toURI().toURL();
         }
         catch (final MalformedURLException e) {
            e.printStackTrace();
         }

         try {
            return GResourceRetriever.getResourceAsUrl(textureFileName);
         }
         catch (final IOException e) {
            logger.logSevere("Load of texture " + textureFileName + " failed", e);
         }
      }


      // try textureFileName as relative to the directory containing the .obj file
      if (!textureFileName.isAbsolute()) {
         final GFileName textureFullFileName = GFileName.fromParts(getModel().getFileName().getParent(), textureFileName);

         final File textureFullFile = textureFullFileName.asFile();
         if (textureFullFile.exists()) {
            try {
               return textureFullFile.toURI().toURL();
            }
            catch (final MalformedURLException e) {
               e.printStackTrace();
            }
         }

         try {
            return GResourceRetriever.getResourceAsUrl(textureFullFileName);
         }
         catch (final IOException e) {
            logger.logSevere("Load of texture " + textureFullFileName + " failed", e);
         }
      }

      // oops, I can't find the texture
      logger.logSevere("Load of texture " + textureFileName + " failed");

      return null;
   }


   public void dispose() {
      if (_texture != null) {
         GGLUtils.disposeTexture(_texture);
         _texture = null;
      }
   }


   public GTexture getTexture() {
      if (_texture == null) {
         _texture = initializeTexture();
      }
      return _texture;
   }


   private GTexture initializeTexture() {
      if (!_hasTexCoords) {
         return null;
      }

      if (_material == null) {
         return null;
      }

      return _model.getTexture(getTextureURL(), _material._mipmap);
   }


   public GPair<URL, Boolean> getTextureData() {
      return new GPair<URL, Boolean>(getTextureURL(), _material._mipmap);
   }

}
