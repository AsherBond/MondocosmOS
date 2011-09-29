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

import es.igosoftware.euclid.GAngle;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.scenegraph.GPositionRenderableLayer.PickResult;
import es.igosoftware.util.GAssert;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Extent;
import gov.nasa.worldwind.geom.Frustum;
import gov.nasa.worldwind.geom.Line;
import gov.nasa.worldwind.geom.Matrix;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.util.WWMath;


public abstract class GNodeAbstract
         implements
            INode {

   private static final double        MIN_PROYECTED_SIZE = 4;

   private final String               _name;
   private boolean                    _visible           = true;
   private GGroupNode                 _parent;


   // transformations
   protected GAngle                   _heading;
   protected GAngle                   _roll;
   protected GAngle                   _pitch;

   //protected double                   _scale;
   protected IVector3                 _scale;
   protected IVector3                 _translation;

   private final GTransformationOrder _transformOrder;

   private Matrix                     _localMatrix       = Matrix.IDENTITY;

   private ArrayList<Runnable>        _disposeListeners;

   // cache
   private Matrix                     _globalMatrix;
   private Matrix                     _lastParentMatrix;

   private boolean                    _alreadyDisposed   = false;


   protected GNodeAbstract(final String name,
                           final GTransformationOrder order) {
      this(name, order, GAngle.ZERO, GAngle.ZERO, GAngle.ZERO, 1, GVector3D.ZERO);
   }


   protected GNodeAbstract(final String name,
                           final GTransformationOrder transformOrder,
                           final GAngle heading,
                           final GAngle roll,
                           final GAngle pitch,
                           final double scale,
                           final IVector3 translation) {
      this(name, transformOrder, heading, roll, pitch, scale, scale, scale, translation);
   }


   protected GNodeAbstract(final String name,
                           final GTransformationOrder transformOrder,
                           final GAngle heading,
                           final GAngle roll,
                           final GAngle pitch,
                           final double scaleX,
                           final double scaleY,
                           final double scaleZ,
                           final IVector3 translation) {
      GAssert.notNull(name, "name");
      GAssert.notNull(transformOrder, "transformOrder");

      _name = name;
      _transformOrder = transformOrder;

      _heading = heading;
      _roll = roll;
      _pitch = pitch;
      _scale = new GVector3D(scaleX, scaleY, scaleZ);
      _translation = translation;

      calculateLocalTransformMatrix();
   }


   @Override
   public void setVisible(final boolean visible) {
      _visible = visible;
   }


   @Override
   public String getName() {
      return _name;
   }


   public Matrix getLocalMatrix() {
      return _localMatrix;
   }


   @Override
   public GGroupNode getParent() {
      return _parent;
   }


   public GTransformationOrder getTransformOrder() {
      return _transformOrder;
   }


   @Override
   public boolean isVisible() {
      return _visible;
   }


   @Override
   public abstract String toString();


   @Override
   public final void render(final DrawContext dc,
                            final Matrix parentMatrix,
                            final boolean terrainChanged) {
      if (isVisible(dc, parentMatrix, terrainChanged)) {
         doRender(dc, parentMatrix, terrainChanged);
      }
   }


   private boolean isVisible(final DrawContext dc,
                             final Matrix parentMatrix,
                             final boolean terrainChanged) {
      if (!_visible) {
         return false;
      }

      final Extent bounds = getBoundsInModelCoordinates(parentMatrix, terrainChanged);

      final Frustum frustum = dc.getView().getFrustumInModelCoordinates();

      if (bounds == null) {
         return true;
      }

      final boolean isVisibleInFrustum = frustum.intersects(bounds);
      if (!isVisibleInFrustum) {
         return false;
      }

      final double proyectedSize = WWMath.computeSizeInWindowCoordinates(dc, bounds);
      if (proyectedSize < MIN_PROYECTED_SIZE) {
         return false;
      }

      return true;
   }


   @Override
   public boolean pick(final DrawContext dc,
                       final Matrix parentMatrix,
                       final boolean terrainChanged,
                       final Line ray,
                       final List<PickResult> pickResults) {

      if (!isPickable()) {
         return false;
      }

      if (!isVisible(dc, parentMatrix, terrainChanged)) {
         return false;
      }

      //      final Matrix globalMatrix = getGlobalMatrix(parentMatrix);
      return doPick(dc, parentMatrix, terrainChanged, ray, pickResults);
   }


   protected abstract boolean doPick(final DrawContext dc,
                                     final Matrix parentMatrix,
                                     final boolean terrainChanged,
                                     final Line ray,
                                     final List<PickResult> pickResults);


   protected final void calculateLocalTransformMatrix() {
      _localMatrix = Matrix.IDENTITY;

      switch (_transformOrder) {
         case ROTATION_SCALE_TRANSLATION:
            applyRotationToLocalMatrix();
            applyScaleToLocalMatrix();
            applyTranslationToLocalMatrix();
            break;

         case ROTATION_TRANSLATION_SCALE:
            applyRotationToLocalMatrix();
            applyTranslationToLocalMatrix();
            applyScaleToLocalMatrix();
            break;

         case TRANSLATION_ROTATION_SCALE:
            applyTranslationToLocalMatrix();
            applyRotationToLocalMatrix();
            applyScaleToLocalMatrix();
            break;

         case SCALE_TRANSLATION_ROTATION:
            applyScaleToLocalMatrix();
            applyTranslationToLocalMatrix();
            applyRotationToLocalMatrix();
            break;
      }

   }


   private void applyTranslationToLocalMatrix() {
      if (!_translation.closeToZero()) {
         _localMatrix = _localMatrix.multiply(Matrix.fromTranslation(_translation.x(), _translation.y(), _translation.z()));
      }
   }


   private void applyScaleToLocalMatrix() {
      if ((_scale.x() != 1) || (_scale.y() != 1) || (_scale.z() != 1)) {
         _localMatrix = _localMatrix.multiply(Matrix.fromScale(_scale.x(), _scale.y(), _scale.z()));

      }
   }


   private void applyRotationToLocalMatrix() {
      if (!_heading.isZero()) {
         _localMatrix = _localMatrix.multiply(Matrix.fromRotationZ(Angle.fromDegrees(_heading.getDegrees())));
      }
      if (!_pitch.isZero()) {
         _localMatrix = _localMatrix.multiply(Matrix.fromRotationX(Angle.fromDegrees(_pitch.getDegrees())));
      }
      if (!_roll.isZero()) {
         _localMatrix = _localMatrix.multiply(Matrix.fromRotationY(Angle.fromDegrees(_roll.getDegrees())));
      }
   }


   protected abstract void doRender(final DrawContext dc,
                                    final Matrix parentMatrix,
                                    final boolean terrainChanged);


   @Override
   public GGroupNode getRoot() {
      if (_parent == null) {
         return (GGroupNode) this;
      }
      return _parent.getRoot();
   }


   protected final Matrix getGlobalMatrix(final Matrix parentMatrix) {
      if ((_lastParentMatrix != parentMatrix) || (_globalMatrix == null)) {
         _globalMatrix = calculateGlobalMatrix(parentMatrix);

         _lastParentMatrix = parentMatrix;
      }
      return _globalMatrix;
   }


   private final Matrix calculateGlobalMatrix(final Matrix parentMatrix) {
      if (_localMatrix == Matrix.IDENTITY) {
         return parentMatrix;
      }

      //      if ((parentMatrix == Matrix.IDENTITY) || (parentMatrix == null)) {
      if (parentMatrix == Matrix.IDENTITY) {
         return _localMatrix;
      }

      return parentMatrix.multiply(_localMatrix);
   }


   @Override
   public void setParent(final GGroupNode parent) {
      if (parent == null) {
         if (_parent == null) {
            throw new RuntimeException(this + " is already an orphan");
         }
      }
      else {
         if (_parent != null) {
            throw new RuntimeException(this + " already has a parent");
         }
      }

      cleanCaches();

      _parent = parent;
   }


   @Override
   public boolean hasScaleTransformation() {
      if ((_scale.x() != 1) || (_scale.y() != 1) || (_scale.z() != 1)) {
         return true;
      }

      if (_parent != null) {
         return _parent.hasScaleTransformation();
      }

      return false;
   }


   @Override
   public void redraw() {
      final GGroupNode root = getRoot();
      if (root != null) {
         final GPositionRenderableLayer layer = root.getLayer();
         if (layer != null) {
            layer.redraw();
         }
      }
   }


   @Override
   public void addDisposeListener(final Runnable runnable) {
      if (_disposeListeners == null) {
         _disposeListeners = new ArrayList<Runnable>();
      }
      _disposeListeners.add(runnable);
   }


   @Override
   public void dispose() {
      if (_alreadyDisposed) {
         throw new RuntimeException(this + " already disposed");
      }
      _alreadyDisposed = true;

      if (_disposeListeners != null) {
         for (final Runnable listener : _disposeListeners) {
            listener.run();
         }

         _disposeListeners = null;
      }
   }


   @Override
   public void acceptVisitor(final IVisitor visitor) {
      visitor.visit(this);
   }


   @Override
   public int getDepth() {
      if (_parent == null) {
         return 0;
      }

      return _parent.getDepth() + 1;
   }


   protected final CharSequence getTransformationString() {
      final StringBuffer buffer = new StringBuffer();

      if (!_translation.closeToZero()) {
         buffer.append(", translation=");
         buffer.append(_translation);
      }

      if ((_scale.x() != 1) || (_scale.y() != 1) || (_scale.z() != 1)) {
         buffer.append(", scale=");
         buffer.append(_scale);
      }

      if (!_heading.isZero()) {
         buffer.append(", heading=");
         buffer.append(_heading);
      }


      if (!_pitch.isZero()) {
         buffer.append(", pitch=");
         buffer.append(_pitch);
      }

      if (!_roll.isZero()) {
         buffer.append(", roll=");
         buffer.append(_roll);
      }

      return buffer;
   }


   public GAngle getHeading() {
      return _heading;
   }


   public GAngle getRoll() {
      return _roll;
   }


   public GAngle getPitch() {
      return _pitch;
   }


   public IVector3 getScale() {
      return _scale;
   }


   public IVector3 getTranslation() {
      return _translation;
   }


   @Override
   public void cleanCaches() {
      _globalMatrix = null;
      _lastParentMatrix = null;

      //      calculateLocalTransformMatrix();
   }

}
