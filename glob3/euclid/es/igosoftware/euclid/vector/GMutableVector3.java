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


package es.igosoftware.euclid.vector;

import es.igosoftware.euclid.GGeometryAbstract;
import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.matrix.GMatrix33D;
import es.igosoftware.euclid.matrix.GMatrix44D;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.util.GAssert;


public final class GMutableVector3<T extends IVector3>
         extends
            GGeometryAbstract<IVector3>
         implements
            IVector3 {

   private static final long serialVersionUID = 1L;


   private IVector3          _value;


   public GMutableVector3(final T value) {
      set(value);
   }


   @Override
   public synchronized GMutableVector3<T> absoluted() {
      _value = _value.absoluted();
      return this;
   }


   @Override
   public synchronized GMutableVector3<T> add(final double delta) {
      _value = _value.add(delta);
      return this;
   }


   @Override
   public synchronized GMutableVector3<T> add(final IVector3 that) {
      _value = _value.add(that);
      return this;
   }


   @Override
   public synchronized double angle(final IVector3 that) {
      return _value.angle(that);
   }


   @Override
   public GMutableVector3<T> asMutable() {
      return this;
   }


   @Override
   public String asParseableString() {
      return _value.asParseableString();
   }


   @Override
   public IVector2 asVector2() {
      return _value.asVector2();
   }


   @Override
   public boolean between(final IVector3 min,
                          final IVector3 max) {
      return _value.between(min, max);
   }


   @Override
   public IVector3 clamp(final IVector3 lower,
                         final IVector3 upper) {
      _value = _value.clamp(lower, upper);
      return _value;
   }


   @Override
   public IVector3 closestPoint(final IVector3 point) {
      return _value.closestPoint(point);
   }


   @Override
   public boolean closeTo(final IVector3 that) {
      return _value.closeTo(that);
   }


   @Override
   public boolean closeTo(final IVector3 that,
                          final double precision) {
      return _value.closeTo(that, precision);
   }


   @Override
   public boolean closeToZero() {
      return _value.closeToZero();
   }


   @Override
   public boolean contains(final IVector3 point) {
      return _value.contains(point);
   }


   @Override
   public synchronized GMutableVector3<T> cross(final IVector3 that) {
      _value = _value.cross(that);
      return this;
   }


   @Override
   public synchronized byte dimensions() {
      return _value.dimensions();
   }


   @Override
   public synchronized double distance(final IVector3 that) {
      return _value.distance(that);
   }


   @Override
   public synchronized GMutableVector3<T> div(final double scale) {
      _value = _value.div(scale);
      return this;
   }


   @Override
   public synchronized GMutableVector3<T> div(final IVector3 that) {
      _value = _value.div(that);
      return this;
   }


   @Override
   public synchronized double dot(final IVector3 that) {
      return _value.dot(that);
   }


   @Override
   public double get(final byte i) {
      return _value.get(i);
   }


   @Override
   public double[] getCoordinates() {
      return _value.getCoordinates();
   }


   @SuppressWarnings("unchecked")
   public T getValue() {
      return (T) _value;
   }


   @Override
   public synchronized double x() {
      return _value.x();
   }


   @Override
   public synchronized double y() {
      return _value.y();
   }


   @Override
   public synchronized double z() {
      return _value.z();
   }


   @Override
   public synchronized GMutableVector3<T> interpolatedTo(final IVector3 that,
                                                         final double alpha) {
      _value = _value.interpolatedTo(that, alpha);
      return this;
   }


   @Override
   public boolean isNormalized() {
      return _value.isNormalized();
   }


   @Override
   public synchronized double length() {
      return _value.length();
   }


   @Override
   public synchronized GMutableVector3<T> max(final IVector3 that) {
      _value = _value.max(that);
      return this;
   }


   @Override
   public synchronized GMutableVector3<T> min(final IVector3 that) {
      _value = _value.min(that);
      return this;
   }


   @Override
   public synchronized GMutableVector3<T> negated() {
      _value = _value.negated();
      return this;
   }


   //   @Override
   //   public IBounds<IVector3> getBounds() {
   //      return _value.getBounds();
   //   }


   @Override
   public synchronized GMutableVector3<T> normalized() {
      _value = _value.normalized();
      return this;
   }


   //   @Override
   //   public GAxisAlignedBox getAxisAlignedBoundingBox() {
   //      return _value.getAxisAlignedBoundingBox();
   //   }


   @Override
   public double precision() {
      return _value.precision();
   }


   @Override
   public synchronized GMutableVector3<T> reciprocal() {
      _value = _value.reciprocal();
      return this;
   }


   @Override
   public synchronized GMutableVector3<T> rounded() {
      _value = _value.rounded();
      return this;
   }


   @Override
   public synchronized GMutableVector3<T> scale(final double scale) {
      _value = _value.scale(scale);
      return this;
   }


   @Override
   public synchronized GMutableVector3<T> scale(final IVector3 that) {
      _value = _value.scale(that);
      return this;
   }


   public void set(final IVector3 value) {
      GAssert.notNull(value, "value");

      _value = value;
   }


   @Override
   public synchronized double squaredDistance(final IVector3 that) {
      return _value.squaredDistance(that);
   }


   @Override
   public synchronized double squaredLength() {
      return _value.squaredLength();
   }


   @Override
   public synchronized GMutableVector3<T> sub(final double delta) {
      _value = _value.sub(delta);
      return this;
   }


   @Override
   public synchronized GMutableVector3<T> sub(final IVector3 that) {
      _value = _value.sub(that);
      return this;
   }


   @Override
   public String toString() {
      return "Mutable" + _value;
   }


   @Override
   public IVector3 transformedBy(final GMatrix44D matrix) {
      _value = _value.transformedBy(matrix);
      return _value;
   }


   @Override
   public IVector3 transformedBy(final GMatrix33D matrix) {
      _value = _value.transformedBy(matrix);
      return _value;
   }


   @Override
   public IVector3 nextUp() {
      _value = _value.nextUp();
      return _value;
   }


   @Override
   public IVector3 previousDown() {
      _value = _value.previousDown();
      return _value;
   }


   @Override
   public IVector3 asDouble() {
      _value = _value.asDouble();
      return _value;
   }


   @Override
   public boolean greaterOrEquals(final IVector3 that) {
      return _value.greaterOrEquals(that);
   }


   @Override
   public boolean lessOrEquals(final IVector3 that) {
      return _value.lessOrEquals(that);
   }


   @Override
   public IVector3 reproject(final GProjection sourceProjection,
                             final GProjection targetProjection) {
      _value = _value.reproject(sourceProjection, targetProjection);
      return _value;
   }


   @Override
   public IVector3 getCentroid() {
      return _value.getCentroid();
   }


   @Override
   public GAxisAlignedBox getBounds() {
      return _value.getBounds();
   }


   @Override
   public IVector3 closestPointOnBoundary(final IVector3 point) {
      return this;
   }


   @Override
   public double squaredDistanceToBoundary(final IVector3 point) {
      return _value.squaredDistance(point);
   }


   @Override
   public double distanceToBoundary(final IVector3 point) {
      return _value.distance(point);
   }


   @Override
   public boolean containsOnBoundary(final IVector3 point) {
      return _value.containsOnBoundary(point);
   }


   @Override
   public boolean closeTo(final IBoundedGeometry<IVector3, GAxisAlignedBox> that) {
      if (that instanceof IVector3) {
         return _value.closeTo((IVector3) that);
      }
      return false;
   }


   @Override
   public IVector3 squared() {
      return _value.squared();
   }

}
