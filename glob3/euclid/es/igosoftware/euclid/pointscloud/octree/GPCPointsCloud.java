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


package es.igosoftware.euclid.pointscloud.octree;

import java.io.Serializable;
import java.util.Map;

import es.igosoftware.euclid.octree.GOTInnerNode;
import es.igosoftware.euclid.projection.GProjection;


public class GPCPointsCloud
         implements
            Serializable {


   private static final long  serialVersionUID = 1L;


   private final GPCInnerNode _root;
   private final GProjection  _projection;
   private final int          _verticesCount;

   private final boolean      _hasColors;
   private final boolean      _hasNormals;
   private final boolean      _hasIntensities;

   private final float        _minIntensity;
   private final float        _maxIntensity;

   private final double       _minElevation;
   private final double       _maxElevation;


   public GPCPointsCloud(final GOTInnerNode root,
                         final Map<String, GPCLeafNode> leafNodes,
                         final GProjection projection,
                         final int verticesCount,
                         final boolean hasIntensities,
                         final boolean hasNormals,
                         final boolean hasColors,
                         final float minIntensity,
                         final float maxIntensity,
                         final double minElevation,
                         final double maxElevation) {
      _root = new GPCInnerNode(root, leafNodes);
      _projection = projection;
      _verticesCount = verticesCount;

      _hasIntensities = hasIntensities;
      _hasNormals = hasNormals;
      _hasColors = hasColors;

      _minIntensity = minIntensity;
      _maxIntensity = maxIntensity;

      _minElevation = minElevation;
      _maxElevation = maxElevation;
   }


   public GProjection getProjection() {
      return _projection;
   }


   public GPCInnerNode getRoot() {
      return _root;
   }


   public int getVerticesCount() {
      return _verticesCount;
   }


   public boolean hasIntensities() {
      return _hasIntensities;
   }


   public boolean hasNormals() {
      return _hasNormals;
   }


   public boolean hasColors() {
      return _hasColors;
   }


   public float getMinIntensity() {
      return _minIntensity;
   }


   public float getMaxIntensity() {
      return _maxIntensity;
   }


   public double getMinElevation() {
      return _minElevation;
   }


   public double getMaxElevation() {
      return _maxElevation;
   }

}
