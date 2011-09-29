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

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import es.igosoftware.euclid.colors.GColorF;
import es.igosoftware.euclid.colors.GColorI;
import es.igosoftware.experimental.pointscloud.rendering.GPointsCloudLayer;
import es.igosoftware.io.GFileName;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.io.ILoader;
import es.igosoftware.util.GMath;
import es.igosoftware.utils.GWWUtils;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.DrawContext;


final class GSGPointsLoader {
   private static final int BYTES_PER_VECTOR3F = 3 * 4; // x, y, z * float 


   static class Buffers {
      final int         _availablePoints;
      final FloatBuffer _pointsBuffer;
      final FloatBuffer _colorsBuffer;


      Buffers(final int availablePoints,
              final FloatBuffer pointsBuffer,
              final FloatBuffer colorsBuffer) {
         _availablePoints = availablePoints;
         _pointsBuffer = pointsBuffer;
         _colorsBuffer = colorsBuffer;
      }

   }


   private final GSGPointsNode     _node;
   private final GPointsCloudLayer _layer;

   private int                     _wantedPoints;
   private float                   _priority;

   private int                     _availablePoints;
   private FloatBuffer             _pointsBuffer;
   private FloatBuffer             _colorsBuffer;
   //   private GSGPointsLoader.Buffers _buffers;

   private boolean                 _loading      = false;
   boolean                         _errorLoading = false;
   private DrawContext             _dc;
   private final int               _bytesPerPoint;


   GSGPointsLoader(final GSGPointsNode node) {
      _node = node;
      _layer = _node._layer;

      final int bytesPerColor = _layer.hasColors() ? 4 : 0;
      final int bytesPerIntensity = _layer.hasIntensities() ? 4 : 0;
      final int bytesPerNormal = _layer.hasNormals() ? BYTES_PER_VECTOR3F : 0;
      _bytesPerPoint = BYTES_PER_VECTOR3F + bytesPerColor + bytesPerIntensity + bytesPerNormal;
   }


   void setPriority(final float priority) {
      _priority = priority;
   }


   synchronized void setWantedPoints(final int wantedPoints) {
      _wantedPoints = wantedPoints;
   }


   synchronized boolean isloading() {
      return _loading;
   }


   synchronized boolean isIncomplete() {
      return _wantedPoints > _availablePoints;
   }


   synchronized GSGPointsLoader.Buffers getBuffers() {
      if ((_availablePoints <= 0) || (_pointsBuffer == null)) {
         return null;
      }
      return new GSGPointsLoader.Buffers(_availablePoints, _pointsBuffer, _colorsBuffer);
   }


   synchronized void reload() {
      _layer.getLoader().cancelAllLoads(_node.getPointsFileName());

      _availablePoints = 0;
      _wantedPoints = 0;

      _loading = false;

      if (_pointsBuffer != null) {
         _pointsBuffer.clear();
         _pointsBuffer = null;
      }

      if (_colorsBuffer != null) {
         _colorsBuffer.clear();
         _colorsBuffer = null;
      }
   }


   synchronized boolean wantsToRun() {
      return !_loading && (_wantedPoints > _availablePoints);
   }


   synchronized void run() {
      if (_errorLoading) {
         // don't try to load again if the last time the loading process ended with error
         return;
      }

      if (_loading) {
         return;
      }

      _loading = true;

      final int bytesToLoad = _wantedPoints * _bytesPerPoint;
      final GFileName pointsFileName = _node.getPointsFileName();
      _layer.getLoader().load(pointsFileName, bytesToLoad, true, Math.round(_priority), createHandler(pointsFileName));
   }


   void setDC(final DrawContext dc) {
      _dc = dc;
   }


   private ILoader.IHandler createHandler(final GFileName pointsFileName) {
      return new ILoader.IHandler() {
         @Override
         public void loadError(final IOException e) {
            System.err.println("Error=" + e + " trying to load " + pointsFileName);
            synchronized (GSGPointsLoader.this) {
               _errorLoading = true;
               _loading = false;
            }
         }


         @Override
         public void loaded(final File file,
                            final long bytesLoaded,
                            final boolean completeLoaded) {
            try {
               final int loadedPoints = (int) bytesLoaded / _bytesPerPoint;

               //            if (_wantedPoints != loadedPoints) {
               //               System.out.println("Loaded " + loadedPoints + " of " + _wantedPoints + " requested");
               //            }


               DataInputStream input = null;
               try {
                  input = new DataInputStream(new BufferedInputStream(new FileInputStream(file.getAbsolutePath())));

                  processInput(loadedPoints, input);
               }
               catch (final IOException e) {
                  e.printStackTrace();
                  synchronized (GSGPointsLoader.this) {
                     _errorLoading = true;
                  }
               }
               finally {
                  GIOUtils.gentlyClose(input);
               }

               _layer.redraw();
            }
            finally {
               synchronized (GSGPointsLoader.this) {
                  _loading = false;
               }
            }

         }


         private void processInput(final int loadedPoints,
                                   final DataInputStream input) throws IOException {
            final Globe globe = _dc.getView().getGlobe();
            final double verticalExaggeration = _dc.getVerticalExaggeration();


            final FloatBuffer pointsBuffer = createFloatBufferForPoints(loadedPoints);

            final boolean hasIntensities = _layer.hasIntensities();
            final boolean hasNormals = _layer.hasNormals();
            final boolean hasColors = _layer.hasColors();

            final boolean colorFromElevation = _node._colorFromElevation;
            final Color pointsColor = _node._pointsColor;
            final boolean createColorsBuffer = hasColors || hasIntensities || colorFromElevation || (pointsColor != null);
            final FloatBuffer colorsBuffer = createColorsBuffer ? createFloatBufferForPoints(loadedPoints) : null;


            final int from = tryToMoveDataFromOldBuffers(input, pointsBuffer, colorsBuffer);

            float minIntensity = _layer.getMinIntensity();
            float maxIntensity = _layer.getMaxIntensity();

            //if (GMath.lessOrEquals(maxIntensity, 1)) {
            if ((maxIntensity - minIntensity) <= 0) {
               minIntensity = 0;
               maxIntensity = 1;
            }
            final float deltaIntensity = maxIntensity - minIntensity;

            final double minElevation = _layer.getMinElevation();
            final double deltaElevation = _layer.getMaxElevation() - minElevation;
            final Position referencePosition = _node._referencePosition;

            final Vec4 referencePoint4 = _node._referencePoint4;

            for (int i = from; (i < loadedPoints) && (i < _wantedPoints); i++) {
               final int i3 = i * 3;

               final Position position = readAndProcessPosition(input, globe, verticalExaggeration, pointsBuffer,
                        referencePosition, referencePoint4, i3);

               if (hasIntensities) {
                  readIntensity(input, hasColors, colorFromElevation, colorsBuffer, minIntensity, deltaIntensity, i3);
               }

               if (hasNormals) {
                  readNormal(input);
               }

               if (hasColors) {
                  readAndProcessColor(input, colorFromElevation, colorsBuffer, i3);
               }

               if (colorFromElevation && (colorsBuffer != null)) {
                  final float alpha = (float) ((position.elevation - minElevation) / deltaElevation);

                  final GColorF color = interpolateColorFromRamp(GColorF.BLUE, RAMP, alpha);
                  //final GColorF color = GColorF.BLACK.interpolatedTo(GColorF.WHITE, alpha);

                  colorsBuffer.put(i3 + 0, color.getRed());
                  colorsBuffer.put(i3 + 1, color.getGreen());
                  colorsBuffer.put(i3 + 2, color.getBlue());
               }
               else if ((pointsColor != null) && (colorsBuffer != null)) {
                  colorsBuffer.put(i3 + 0, pointsColor.getRed() / 255f);
                  colorsBuffer.put(i3 + 1, pointsColor.getGreen() / 255f);
                  colorsBuffer.put(i3 + 2, pointsColor.getBlue() / 255f);
               }

               //                  synchronized (GSGPointsLoader.this) {
               _availablePoints = i + 1;
               //                  }

               //               if (i % 250 == 0) {
               //                  _layer.redraw();
               //               }
            }
         }


         private void readAndProcessColor(final DataInputStream input,
                                          final boolean colorFromElevation,
                                          final FloatBuffer colorsBuffer,
                                          final int i3) throws IOException {
            final GColorI color = GColorI.newRGB(input.readInt());
            if (!colorFromElevation) {
               colorsBuffer.put(i3 + 0, color.getRed());
               colorsBuffer.put(i3 + 1, color.getGreen());
               colorsBuffer.put(i3 + 2, color.getBlue());
            }
         }


         private void readNormal(final DataInputStream input) throws IOException {
            //                final GVector3F normal = readVector3F(input);
            @SuppressWarnings("unused")
            final float normalX = input.readFloat();
            @SuppressWarnings("unused")
            final float normalY = input.readFloat();
            @SuppressWarnings("unused")
            final float notmalZ = input.readFloat();
         }


         private void readIntensity(final DataInputStream input,
                                    final boolean hasColors,
                                    final boolean colorFromElevation,
                                    final FloatBuffer colorsBuffer,
                                    final float minIntensity,
                                    final float deltaIntensity,
                                    final int i3) throws IOException {
            final float intensity = input.readFloat();
            if (!hasColors && !colorFromElevation) {
               // color from intensity
               final float gray = (intensity - minIntensity) / deltaIntensity;

               colorsBuffer.put(i3 + 0, gray);
               colorsBuffer.put(i3 + 1, gray);
               colorsBuffer.put(i3 + 2, gray);
            }
         }


         private Position readAndProcessPosition(final DataInputStream input,
                                                 final Globe globe,
                                                 final double verticalExaggeration,
                                                 final FloatBuffer pointsBuffer,
                                                 final Position referencePosition,
                                                 final Vec4 referencePoint4,
                                                 final int i3) throws IOException {
            final float x = input.readFloat();
            final float y = input.readFloat();
            final float z = input.readFloat();

            final double latitude = referencePosition.latitude.radians + x;
            final double longitude = referencePosition.longitude.radians + y;
            final double elevation = referencePosition.elevation + z;
            final Position position = Position.fromRadians(latitude, longitude, elevation);

            final Vec4 point4 = GWWUtils.computePointFromPosition(position, globe, verticalExaggeration);

            pointsBuffer.put(i3 + 0, (float) (point4.x - referencePoint4.x));
            pointsBuffer.put(i3 + 1, (float) (point4.y - referencePoint4.y));
            pointsBuffer.put(i3 + 2, (float) (point4.z - referencePoint4.z));

            return position;
         }


         private int tryToMoveDataFromOldBuffers(final DataInputStream input,
                                                 final FloatBuffer pointsBuffer,
                                                 final FloatBuffer colorsBuffer) throws IOException {
            int from = 0;

            synchronized (GSGPointsLoader.this) {
               if ((_availablePoints > 0) && (_pointsBuffer != null)) {
                  //                     System.out.println("Recycle old buffer with " + _availablePoints + " points, wanted " + _wantedPoints);
                  _pointsBuffer.rewind();
                  pointsBuffer.put(_pointsBuffer);
                  _pointsBuffer.clear();
                  if ((colorsBuffer != null) && (_colorsBuffer != null)) {
                     _colorsBuffer.rewind();
                     colorsBuffer.put(_colorsBuffer);
                     _colorsBuffer.clear();
                  }
                  from = _availablePoints;
                  final int toSkip = from * _bytesPerPoint;
                  final int skiped = input.skipBytes(toSkip);
                  if (skiped != toSkip) {
                     throw new IOException("Can't skip " + toSkip + " bytes, only skipped " + skiped + " bytes");
                  }
               }
               else {
                  _availablePoints = 0;
               }
               _pointsBuffer = pointsBuffer;
               _colorsBuffer = colorsBuffer;
            }

            return from;
         }


      };

   }


   private static FloatBuffer createFloatBufferForPoints(final int pointsCount) {
      final int capacity = pointsCount * BYTES_PER_VECTOR3F;
      return ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder()).asFloatBuffer();
   }


   public float getPriority() {
      return _priority;
   }


   private static final GColorF[] RAMP = new GColorF[] {
                     GColorF.CYAN,
                     GColorF.GREEN,
                     GColorF.YELLOW,
                     GColorF.RED
                                       };


   private static GColorF interpolateColorFromRamp(final GColorF colorFrom,
                                                   final GColorF[] ramp,
                                                   final float alpha) {
      final float rampStep = 1f / ramp.length;

      final int toI;
      if (GMath.closeTo(alpha, 1)) {
         toI = ramp.length - 1;
      }
      else {
         toI = (int) (alpha / rampStep);
      }

      final GColorF from;
      if (toI == 0) {
         from = colorFrom;
      }
      else {
         from = ramp[toI - 1];
      }

      final float colorAlpha = (alpha % rampStep) / rampStep;
      return from.mixedWidth(ramp[toI], colorAlpha);
   }


}
