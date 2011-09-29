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


package es.igosoftware.experimental.pointscloud.rendering.scenegraph;

import es.igosoftware.euclid.colors.GColorF;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.pointscloud.octree.GPCLeafNode;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.experimental.pointscloud.rendering.GPointsCloudLayer;
import es.igosoftware.io.GFileName;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GMath;
import es.igosoftware.utils.GPositionBox;
import es.igosoftware.utils.GWWUtils;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.DrawContext;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;


public final class GSGPointsNode
         extends
            GSGNode {


   private final String          _id;
   private final int[]           _lodIndices;
   private final int             _pointsCount;
   final Position                _referencePosition;
   private final GPositionBox    _minimumBox;

   Vec4                          _referencePoint4;
   final IVector3                _referencePoint;
   private boolean               _wasVisible;
   boolean                       _colorFromElevation;

   private final GSGPointsLoader _pointsLoader;
   Color                         _pointsColor;


   GSGPointsNode(final GPCLeafNode node,
                 final GProjection projection,
                 final GPointsCloudLayer layer) {
      super(node.getBounds(), projection, layer);

      _id = node.getId();
      _lodIndices = node.getLodIndices();
      _pointsCount = node.getPointsCount();
      GAssert.isTrue(_pointsCount > 0, "_pointsCount");

      _referencePoint = node.getReferencePoint();
      _referencePosition = GWWUtils.toPosition(_referencePoint, projection);
      _minimumBox = new GPositionBox(node.getMinimumBounds(), projection);

      _pointsLoader = new GSGPointsLoader(this);
   }


   @Override
   protected final void doPreRender(final DrawContext dc,
                                    final boolean changed) {
      if ((_referencePoint4 == null) || changed) {
         final Globe globe = dc.getView().getGlobe();
         final double verticalExaggeration = dc.getVerticalExaggeration();

         _referencePoint4 = GWWUtils.computePointFromPosition(_referencePosition, globe, verticalExaggeration);
      }

      boolean reload = false;
      if (isVisible(dc)) {
         _wasVisible = true;
      }
      else {
         if (_wasVisible) {
            _wasVisible = false;

            reload = true;
         }
      }

      if (changed || reload) {
         reload();
      }
   }


   @Override
   public void reload() {
      _pointsLoader.reload();
   }


   GFileName getPointsFileName() {
      return GFileName.fromParentAndParts(_layer.getPointsCloudName(), "tile-" + _id + ".points");
   }


   private int moveToCompleteLODLevel(final int pointsCount) {
      //      if (_pointsCount <= 0) {
      //         return 0;
      //      }

      int bigggerIndexLessThanPointsCount = -1;
      for (final int lodIndex : _lodIndices) {
         if (lodIndex > pointsCount) {
            return bigggerIndexLessThanPointsCount + 1;
         }
         if (lodIndex > bigggerIndexLessThanPointsCount) {
            bigggerIndexLessThanPointsCount = lodIndex;
         }
      }

      return pointsCount;
   }


   @Override
   protected final int doRender(final DrawContext dc) {

      final float projectedPixels = getProjectedPixels(dc);
      if (projectedPixels <= 0) {
         return 0;
      }

      //      final int pointsToDrawBasedOnProjectedPixels = (int) Math.round(projectedPixels * GMath.sqrt(_layer.getQualityFactor()));
      final int pointsToDrawBasedOnProjectedPixels = Math.round(projectedPixels * _layer.getQualityFactor()
                                                                * _layer.getQualityFactor());

      final boolean useLOD = pointsToDrawBasedOnProjectedPixels < _pointsCount;
      final int neededPoints = useLOD ? pointsToDrawBasedOnProjectedPixels : _pointsCount;
      if (neededPoints <= 0) {
         return 0;
      }

      //      final SurfaceImage icon = getIcon(useLOD);
      //      if (icon != null) {
      //         icon.preRender(dc);
      //         icon.render(dc);
      //      }

      final View view = dc.getView();
      final GL gl = dc.getGL();

      _pointsLoader.setDC(dc);

      final GSGPointsLoader.Buffers buffers = _pointsLoader.getBuffers();

      final int availablePoints = (buffers == null) ? 0 : buffers._availablePoints;
      //      final int wantedPoints = getNextLevel(availablePoints) + 1;
      final int wantedPoints = moveToCompleteLODLevel(neededPoints);
      _pointsLoader.setWantedPoints(wantedPoints);

      //final double distance = view.getEyePoint().distanceToSquared3(_referencePoint4);
      final double distance = view.getCenterPoint().distanceToSquared3(_referencePoint4);
      setPriority((int) -distance * (wantedPoints - availablePoints));
      //      setPriority(projectedPixels * (wantedPoints - availablePoints));

      //< if ((buffers == null) || (buffers._pointsBuffer == null) || (buffers._availablePoints <= 0)) {
      if (buffers == null) {
         renderReferencePoint(gl);
         return 1;
      }

      final int pointsToDraw = moveToCompleteLODLevel(Math.min(buffers._availablePoints, neededPoints));

      //      if (false) {
      //         System.out.println("projectedPixels=" + projectedPixels + //
      //                            ", pointsToDrawBasedOnProjectedPixels=" + pointsToDrawBasedOnProjectedPixels + //
      //                            ", useLOD=" + useLOD + //
      //                            ", neededPoints=" + neededPoints + //
      //                            ", wantedPoints=" + wantedPoints + //
      //                            ", pointsToDraw=" + pointsToDraw + //
      //                            ", lodIndices=" + Arrays.toString(_lodIndices));
      //      }

      gl.glPointSize(calculatePointSize(projectedPixels, pointsToDraw));

      //      final float[] linear = { 0.0f, 0.12f / 5, 0.0f };
      //      //                  final float[] quadratic = { 0.0f, 0.0f, 0.01f };
      //      gl.glPointParameterfv(GL.GL_POINT_DISTANCE_ATTENUATION, linear, 0);

      if (_layer.getSmooth()) {
         gl.glEnable(GL.GL_BLEND);
         gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
         gl.glHint(GL.GL_POINT_SMOOTH_HINT, GL.GL_NICEST);

         gl.glEnable(GL.GL_POINT_SMOOTH);
      }
      else {
         gl.glDisable(GL.GL_BLEND);

         gl.glDisable(GL.GL_POINT_SMOOTH);
      }

      view.pushReferenceCenter(dc, _referencePoint4);
      GWWUtils.pushOffset(gl);

      //      final boolean directRendering = (pointsToDraw <= 32); // to few points, use direct rendering
      final boolean directRendering = false;
      //      final boolean directRendering = true;
      if (directRendering) {
         directRender(gl, pointsToDraw, useLOD, buffers);
      }
      else {
         vertexArrayRender(gl, pointsToDraw, useLOD, buffers);
      }

      //      if (dc.getGLRuntimeCapabilities().isUseVertexBufferObject()) {
      //         if (!vboRender(dc, pointsToDraw, useLOD, buffers)) {
      //            vertexArrayRender(gl, pointsToDraw, useLOD, buffers);
      //         }
      //      }
      //      else {
      //         vertexArrayRender(gl, pointsToDraw, useLOD, buffers);
      //      }

      GWWUtils.popOffset(gl);
      view.popReferenceCenter(dc);

      gl.glDisable(GL.GL_POINT_SMOOTH);

      gl.glPointSize(1);

      gl.glColor3f(1, 1, 1);

      //      gl.glPopAttrib();

      return pointsToDraw;
   }


   //   private SurfaceImage _loadingIcon;
   //   private SurfaceImage _moreDetailIcon;


   //   private SurfaceImage getIcon(final boolean useLOD) {
   //      //      if (_pointsLoader.isloading() || _pointsLoader.wantsToRun()) {
   //      //         return getLoadingIcon();
   //      //      }
   //      //
   //      //      if (useLOD) {
   //      //         return getMoreDetailIcon();
   //      //      }
   //
   //      return null;
   //   }


   //   private SurfaceImage getLoadingIcon() {
   //      if (_loadingIcon == null) {
   //         _loadingIcon = new SurfaceImage("bitmaps/textures/hour-glass.png", getBoxForProjectedPixels()._sector);
   //         _loadingIcon.setOpacity(0.75);
   //         _loadingIcon.setPickEnabled(false);
   //
   //         //         _loadingIcon.setVisible(true);
   //         //         _loadingIcon.setScale(0.15);
   //         //         _loadingIcon.setMaxSize(5e3);
   //         //         _loadingIcon.setHeading(null); // follow eye - always facing
   //      }
   //
   //      return _loadingIcon;
   //   }
   //
   //
   //   private SurfaceImage getMoreDetailIcon() {
   //      if (_moreDetailIcon == null) {
   //         _moreDetailIcon = new SurfaceImage("bitmaps/textures/magnifying-glass.png", getBoxForProjectedPixels()._sector);
   //         _moreDetailIcon.setOpacity(0.75);
   //         _moreDetailIcon.setPickEnabled(false);
   //
   //         //         _loadingIcon.setVisible(true);
   //         //         _loadingIcon.setScale(0.15);
   //         //         _loadingIcon.setMaxSize(5e3);
   //         //         _loadingIcon.setHeading(null); // follow eye - always facing
   //      }
   //
   //      return _moreDetailIcon;
   //   }


   private float calculatePointSize(final float projectedPixels,
                                    final int pointsToDraw) {
      if (_layer.getDynamicPointSize()) {
         final double representedPoints = ((double) projectedPixels / pointsToDraw);
         final float pointSize = (float) GMath.sqrt(representedPoints) * _layer.getPointSize();
         if (pointSize < 0.01) {
            return 0.01f;
         }
         return pointSize;
      }
      return _layer.getPointSize();
   }


   //   private void setPointsColor(final GL gl,
   //                               final boolean useLOD) {
   //
   //      //      gl.glColorMask(useLOD, useLOD, useLOD, useLOD)
   //
   //      if (_layer.getColorFromState()) {
   //         if (_errorLoading.get()) {
   //            gl.glColor3f(1, 0, 0);
   //         }
   //         else if (_loading || (_wantedPoints > _availablePoints.get())) {
   //            if (useLOD) {
   //               gl.glColor3f(0.7f, 1f, 0.7f);
   //            }
   //            else {
   //               gl.glColor3f(0.7f, 0.7f, 1);
   //            }
   //         }
   //         else {
   //            if (useLOD) {
   //               gl.glColor3f(1, 1, 0.7f);
   //            }
   //            else {
   //               gl.glColor3f(1, 1, 1);
   //            }
   //         }
   //      }
   //      else {
   //         gl.glColor3f(1, 1, 1);
   //      }
   //   }


   private IColor getReferenceColor(final boolean useLOD) {
      if (_layer.getColorFromState()) {
         synchronized (_pointsLoader) {
            if (_pointsLoader._errorLoading) {
               return GColorF.newRGB(1, 0, 0);
            }
            //else if (_loading || (_wantedPoints > _availablePoints.get())) {
            else if (_pointsLoader.isloading() || _pointsLoader.isIncomplete()) {
               if (useLOD) {
                  return GColorF.newRGB(0.7f, 1f, 0.7f);
               }
               return GColorF.newRGB(0.7f, 0.7f, 1);
            }
            else {
               if (useLOD) {
                  return GColorF.newRGB(1, 1, 0.7f);
               }
               return GColorF.WHITE;
            }
         }
      }

      return GColorF.WHITE;
   }


   private void renderReferencePoint(final GL gl) {
      gl.glPointSize(_layer.getPointSize() * 2);
      gl.glColor3f(0, 0, 1);

      gl.glBegin(GL.GL_POINTS);
      {
         gl.glVertex3d(_referencePoint4.x, _referencePoint4.y, _referencePoint4.z);
      }
      gl.glEnd();

      gl.glColor3f(1, 1, 1);
      gl.glPointSize(1);
   }


   private void directRender(final GL gl,
                             final int pointsToDraw,
                             final boolean useLOD,
                             final GSGPointsLoader.Buffers buffers) {
      final FloatBuffer pointsBuffer = buffers._pointsBuffer;
      final FloatBuffer colorsBuffer = buffers._colorsBuffer;

      final IColor referenceColor = getReferenceColor(useLOD);

      //      synchronized (pointsBuffer) {
      gl.glBegin(GL.GL_POINTS);
      try {
         pointsBuffer.rewind();
         if (colorsBuffer == null) {
            gl.glColor3f(referenceColor.getRed(), referenceColor.getGreen(), referenceColor.getBlue());
         }
         else {
            colorsBuffer.rewind();
         }
         //         gl.glColor3f(1, 1, 1);
         //      synchronized (_buffer) {
         //         final float[] pointA = new float[] { 0, 0, 0 };
         //         final float[] colorA = new float[] { 0, 0, 0 };
         final int to = Math.min(pointsBuffer.capacity() / (3 * 4), pointsToDraw);
         for (int i = 0; i < to; i++) {
            final int i3 = i * 3;
            if (colorsBuffer != null) {
               final float red = colorsBuffer.get(i3 + 0) * referenceColor.getRed();
               final float green = colorsBuffer.get(i3 + 1) * referenceColor.getGreen();
               final float blue = colorsBuffer.get(i3 + 2) * referenceColor.getBlue();
               gl.glColor3f(red, green, blue);
               //               colorsBuffer.get(colorA);
               //               gl.glColor3fv(colorA, 0);

            }


            final float x = pointsBuffer.get(i3 + 0);
            final float y = pointsBuffer.get(i3 + 1);
            final float z = pointsBuffer.get(i3 + 2);
            gl.glVertex3f(x, y, z);
            //            pointsBuffer.get(pointA);
            //            gl.glVertex3fv(pointA, 0);
         }
         //      }
      }
      finally {
         gl.glEnd();
      }
      //      }
   }


   //   private final Object vboCacheKey = new Object();


   //   private void fillVbo(final DrawContext dc,
   //                        final GSGPointsLoader.Buffers buffers) {
   //      final FloatBuffer pointsBuffer = buffers._pointsBuffer;
   //
   //      final GL gl = dc.getGL();
   //
   //      //Create a new bufferId
   //      final int glBuf[] = new int[1];
   //      gl.glGenBuffers(1, glBuf, 0);
   //
   //      // Load the buffer
   //      gl.glBindBuffer(GL.GL_ARRAY_BUFFER, glBuf[0]);
   //      gl.glBufferData(GL.GL_ARRAY_BUFFER, pointsBuffer.limit() * 4, pointsBuffer, GL.GL_STATIC_DRAW);
   //
   //      // Add it to the gpu resource cache
   //      dc.getGpuResourceCache().put(this.vboCacheKey, glBuf, GpuResourceCache.VBO_BUFFERS, pointsBuffer.limit() * 4);
   //   }


   //   private boolean vboRender(final DrawContext dc,
   //                             final int pointsToDraw,
   //                             final boolean useLOD,
   //                             final GSGPointsLoader.Buffers buffers) {
   //      int[] vboId = (int[]) dc.getGpuResourceCache().get(this.vboCacheKey);
   //      if (vboId == null) {
   //         this.fillVbo(dc, buffers);
   //         vboId = (int[]) dc.getGpuResourceCache().get(this.vboCacheKey);
   //         if (vboId == null) {
   //            return false;
   //         }
   //      }
   //
   //      final FloatBuffer pointsBuffer = buffers._pointsBuffer;
   //      final FloatBuffer colorsBuffer = colorizeBuffer(buffers._colorsBuffer, useLOD);
   //
   //      final GL gl = dc.getGL();
   //
   //      gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboId[0]);
   //      gl.glInterleavedArrays(GL.GL_C3F_V3F, 0, 0);
   //      final int count = Math.min(pointsBuffer.capacity() / 3, pointsToDraw);
   //      gl.glDrawArrays(GL.GL_POINTS, 0, count);
   //      gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
   //
   //      return true;
   //   }


   private void vertexArrayRender(final GL gl,
                                  final int pointsToDraw,
                                  final boolean useLOD,
                                  final GSGPointsLoader.Buffers buffers) {
      final FloatBuffer pointsBuffer = buffers._pointsBuffer;
      final FloatBuffer colorsBuffer = colorizeBuffer(buffers._colorsBuffer, useLOD);
      //      gl.glColor3f(1, 1, 1);

      if (colorsBuffer == null) {
         final IColor referenceColor = getReferenceColor(useLOD);
         gl.glColor3f(referenceColor.getRed(), referenceColor.getGreen(), referenceColor.getBlue());
      }

      gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
      if (colorsBuffer != null) {
         gl.glEnableClientState(GL.GL_COLOR_ARRAY);
      }
      gl.glEnable(GL.GL_COLOR_MATERIAL);

      //      synchronized (pointsBuffer) {
      gl.glVertexPointer(3, GL.GL_FLOAT, 0, pointsBuffer.rewind());
      if (colorsBuffer != null) {
         gl.glColorPointer(3, GL.GL_FLOAT, 0, colorsBuffer.rewind());
      }

      final int count = Math.min(pointsBuffer.capacity() / 3, pointsToDraw);
      gl.glDrawArrays(GL.GL_POINTS, 0, count);
      //      }

      gl.glDisable(GL.GL_COLOR_MATERIAL);
      gl.glDisableClientState(GL.GL_COLOR_ARRAY);
      gl.glDisableClientState(GL.GL_VERTEX_ARRAY);

   }


   private FloatBuffer colorizeBuffer(final FloatBuffer originalColorsBuffer,
                                      final boolean useLOD) {
      if (originalColorsBuffer == null) {
         return null;
      }

      if (!_layer.getColorFromState()) {
         return originalColorsBuffer;
      }

      final IColor referenceColor = getReferenceColor(useLOD);
      final FloatBuffer colorizedBuffer = ByteBuffer.allocateDirect(originalColorsBuffer.capacity() * 4).order(
               ByteOrder.nativeOrder()).asFloatBuffer();

      colorizedBuffer.rewind();
      originalColorsBuffer.rewind();
      for (int i = 0; i < originalColorsBuffer.capacity(); i += 3) {
         final float red = originalColorsBuffer.get() * referenceColor.getRed();
         final float green = originalColorsBuffer.get() * referenceColor.getGreen();
         final float blue = originalColorsBuffer.get() * referenceColor.getBlue();

         colorizedBuffer.put(red);
         colorizedBuffer.put(green);
         colorizedBuffer.put(blue);
      }

      return colorizedBuffer;
   }


   @Override
   public final void initialize(final DrawContext dc) {
      //      System.out.println("Initializating " + this);
      _layer.registerNodeTask(this);
   }


   public final boolean wantsToRun() {
      //return !_loading && (_wantedPoints > _availablePoints.get());
      return _pointsLoader.wantsToRun();
   }


   public void run() {
      _pointsLoader.run();
   }


   @Override
   public void setColorFromElevation(final boolean colorFromElevation) {
      if (colorFromElevation == _colorFromElevation) {
         return;
      }

      _colorFromElevation = colorFromElevation;
      _pointsLoader.reload();

      reload();
   }


   @Override
   public void setPointsColor(final Color pointsColor) {
      if (pointsColor == _pointsColor) {
         return;
      }

      _pointsColor = pointsColor;
      _pointsLoader.reload();

      reload();
   }


   @Override
   protected final GPositionBox getBoxForProjectedPixels() {
      return _minimumBox;
   }


   @Override
   protected void setPriority(final float priority) {
      super.setPriority(priority);
      _pointsLoader.setPriority(priority);
   }


   @Override
   public float getPriority() {
      return _pointsLoader.getPriority();
   }


   //   private int getNextLevel(final int pointsCount) {
   //      int bigggerIndexLessThanPointsCount = 0;
   //      for (int i = 0; i < _lodIndices.length - 1; i++) {
   //         final int lodIndex = _lodIndices[i];
   //         if (lodIndex >= pointsCount) {
   //            return lodIndex;
   //         }
   //         if (lodIndex >= bigggerIndexLessThanPointsCount) {
   //            bigggerIndexLessThanPointsCount = i;
   //         }
   //      }
   //
   //      return _lodIndices[_lodIndices.length - 1];
   //   }

}
