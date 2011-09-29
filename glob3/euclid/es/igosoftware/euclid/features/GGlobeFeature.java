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


package es.igosoftware.euclid.features;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GAssert;


public class GGlobeFeature<

VectorT extends IVector<VectorT, ?>,

GeometryT extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>

>
         implements
            IGlobeFeature<VectorT, GeometryT> {

   private final GeometryT                             _geometry;
   private final List<Object>                          _attributes;

   private IGlobeFeatureCollection<VectorT, GeometryT> _featureCollection;


   public GGlobeFeature(final GeometryT geometry,
                        final List<Object> attributes) {
      GAssert.notNull(geometry, "geometry");
      GAssert.notNull(attributes, "attributes");

      _geometry = geometry;
      _attributes = new ArrayList<Object>(attributes);
   }


   @Override
   public GeometryT getDefaultGeometry() {
      return _geometry;
   }


   @Override
   public List<Object> getAttributes() {
      return Collections.unmodifiableList(_attributes);
   }


   //   @Override
   //   public Object getAttribute(final int index) {
   //      return _attributes.get(index);
   //   }


   @Override
   public String toString() {
      return "GGlobeFeature [geometry=" + _geometry + ", attributes=" + _attributes + "]";
   }


   @Override
   public void setFeatureCollection(final IGlobeFeatureCollection<VectorT, GeometryT> featureCollection) {
      if (_featureCollection != null) {
         throw new RuntimeException("featureCollection already set");
      }

      GAssert.isTrue(featureCollection.getFieldsCount() == _attributes.size(), "Fields and Attributes don't match");

      _featureCollection = featureCollection;
   }


   @Override
   public Object getAttribute(final String fieldName) {
      return _attributes.get(_featureCollection.getFieldIndex(fieldName));
   }


   @Override
   public boolean hasAttribute(final String fieldName) {
      return _featureCollection.hasField(fieldName);
   }

}
