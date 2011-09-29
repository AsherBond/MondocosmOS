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


package es.igosoftware.euclid.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

import es.igosoftware.euclid.colors.GColorF;
import es.igosoftware.euclid.colors.GColorI;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.GVector2F;
import es.igosoftware.euclid.vector.GVector2I;
import es.igosoftware.euclid.vector.GVector2S;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.GVector3F;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.vector.IVectorI2;
import es.igosoftware.util.GAssert;


public final class GBufferUtils {

   public static ByteBuffer createByteBuffer(final int capacity,
                                             final boolean allocateDirect) {
      GAssert.isPositiveOrZero(capacity, "capacity");
      final ByteBuffer result;
      if (allocateDirect) {
         result = ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
      }
      else {
         result = ByteBuffer.allocate(capacity).order(ByteOrder.nativeOrder());
      }
      result.clear();
      return result;
   }


   public static DoubleBuffer createDoubleBuffer(final int capacity,
                                                 final boolean allocateDirect) {
      //      final DoubleBuffer result = ByteBuffer.allocateDirect(capacity * 8).order(ByteOrder.nativeOrder()).asDoubleBuffer();
      //      result.clear();
      //      return result;
      return createByteBuffer(capacity * 8, allocateDirect).asDoubleBuffer();
   }


   public static FloatBuffer createFloatBuffer(final int capacity,
                                               final boolean allocateDirect) {
      //      final FloatBuffer result = ByteBuffer.allocateDirect(capacity * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
      //      result.clear();
      //      return result;
      return createByteBuffer(capacity * 4, allocateDirect).asFloatBuffer();
   }


   public static IntBuffer createIntBuffer(final int capacity,
                                           final boolean allocateDirect) {
      //      final IntBuffer result = ByteBuffer.allocateDirect(capacity * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
      //      result.clear();
      //      return result;
      return createByteBuffer(capacity * 4, allocateDirect).asIntBuffer();
   }


   public static ShortBuffer createShortBuffer(final int capacity,
                                               final boolean allocateDirect) {
      return createByteBuffer(capacity * 2, allocateDirect).asShortBuffer();
   }


   public static LongBuffer createLongBuffer(final int capacity,
                                             final boolean allocateDirect) {
      return createByteBuffer(capacity * 8, allocateDirect).asLongBuffer();
   }


   public static GColorF getColor(final FloatBuffer buffer,
                                  final int index) {
      final int index3 = index * 3;
      final float r = buffer.get(index3 + 0);
      final float g = buffer.get(index3 + 1);
      final float b = buffer.get(index3 + 2);
      return GColorF.newRGB(r, g, b);
   }


   public static GColorI getColor(final IntBuffer buffer,
                                  final int index) {
      return GColorI.newRGB(buffer.get(index));
   }


   public static GVector3D getVector3(final DoubleBuffer buffer,
                                      final int index) {
      final int index3 = index * 3;
      final double x = buffer.get(index3 + 0);
      final double y = buffer.get(index3 + 1);
      final double z = buffer.get(index3 + 2);
      return new GVector3D(x, y, z, false);
   }


   public static GVector2D getVector2(final DoubleBuffer buffer,
                                      final int index) {
      final int index3 = index * 2;
      final double x = buffer.get(index3 + 0);
      final double y = buffer.get(index3 + 1);
      return new GVector2D(x, y, false);
   }


   public static GVector3F getVector3(final FloatBuffer buffer,
                                      final int index) {
      final int index3 = index * 3;
      final float x = buffer.get(index3 + 0);
      final float y = buffer.get(index3 + 1);
      final float z = buffer.get(index3 + 2);
      return new GVector3F(x, y, z, false);
   }


   public static GVector2F getVector2(final FloatBuffer buffer,
                                      final int index) {
      final int index3 = index * 2;
      final float x = buffer.get(index3 + 0);
      final float y = buffer.get(index3 + 1);
      return new GVector2F(x, y, false);
   }


   public static GVector2S getVector(final ShortBuffer buffer,
                                     final int index) {
      final int index3 = index * 2;
      final short x = buffer.get(index3 + 0);
      final short y = buffer.get(index3 + 1);
      return new GVector2S(x, y);
   }


   public static GVector2I getVector(final IntBuffer buffer,
                                     final int index) {
      final int index3 = index * 2;
      final int x = buffer.get(index3 + 0);
      final int y = buffer.get(index3 + 1);
      return new GVector2I(x, y);
   }


   public static DoubleBuffer growBuffer(final int newCapacity,
                                         final DoubleBuffer oldBuffer) {
      if (oldBuffer == null) {
         return null;
      }

      final DoubleBuffer newBuffer = createDoubleBuffer(newCapacity, oldBuffer.isDirect());
      oldBuffer.rewind();
      newBuffer.put(oldBuffer);
      return newBuffer;
   }


   public static FloatBuffer shrinkBuffer(final int newCapacity,
                                          final FloatBuffer oldBuffer) {
      if (oldBuffer == null) {
         return null;
      }

      final FloatBuffer newBuffer = createFloatBuffer(newCapacity, oldBuffer.isDirect());
      oldBuffer.rewind();
      for (int i = 0; i < newCapacity; i++) {
         newBuffer.put(oldBuffer.get());
      }
      return newBuffer;
   }


   public static DoubleBuffer shrinkBuffer(final int newCapacity,
                                           final DoubleBuffer oldBuffer) {
      if (oldBuffer == null) {
         return null;
      }

      final DoubleBuffer newBuffer = createDoubleBuffer(newCapacity, oldBuffer.isDirect());
      oldBuffer.rewind();
      for (int i = 0; i < newCapacity; i++) {
         newBuffer.put(oldBuffer.get());
      }
      return newBuffer;
   }


   public static IntBuffer shrinkBuffer(final int newCapacity,
                                        final IntBuffer oldBuffer) {
      if (oldBuffer == null) {
         return null;
      }

      final IntBuffer newBuffer = createIntBuffer(newCapacity, oldBuffer.isDirect());
      oldBuffer.rewind();
      for (int i = 0; i < newCapacity; i++) {
         newBuffer.put(oldBuffer.get());
      }
      return newBuffer;
   }


   public static ShortBuffer shrinkBuffer(final int newCapacity,
                                          final ShortBuffer oldBuffer) {
      if (oldBuffer == null) {
         return null;
      }

      final ShortBuffer newBuffer = createShortBuffer(newCapacity, oldBuffer.isDirect());
      oldBuffer.rewind();
      for (int i = 0; i < newCapacity; i++) {
         newBuffer.put(oldBuffer.get());
      }
      return newBuffer;
   }


   public static LongBuffer shrinkBuffer(final int newCapacity,
                                         final LongBuffer oldBuffer) {
      if (oldBuffer == null) {
         return null;
      }

      final LongBuffer newBuffer = createLongBuffer(newCapacity, oldBuffer.isDirect());
      oldBuffer.rewind();
      for (int i = 0; i < newCapacity; i++) {
         newBuffer.put(oldBuffer.get());
      }
      return newBuffer;
   }


   public static FloatBuffer growBuffer(final int newCapacity,
                                        final FloatBuffer oldBuffer) {
      if (oldBuffer == null) {
         return null;
      }

      final FloatBuffer newBuffer = createFloatBuffer(newCapacity, oldBuffer.isDirect());
      oldBuffer.rewind();
      newBuffer.put(oldBuffer);
      return newBuffer;
   }


   public static IntBuffer growBuffer(final int newCapacity,
                                      final IntBuffer oldBuffer) {
      if (oldBuffer == null) {
         return null;
      }

      final IntBuffer newBuffer = createIntBuffer(newCapacity, oldBuffer.isDirect());
      oldBuffer.rewind();
      newBuffer.put(oldBuffer);
      return newBuffer;
   }


   public static ShortBuffer growBuffer(final int newCapacity,
                                        final ShortBuffer oldBuffer) {
      if (oldBuffer == null) {
         return null;
      }

      final ShortBuffer newBuffer = createShortBuffer(newCapacity, oldBuffer.isDirect());
      oldBuffer.rewind();
      newBuffer.put(oldBuffer);
      return newBuffer;
   }


   public static LongBuffer growBuffer(final int newCapacity,
                                       final LongBuffer oldBuffer) {
      if (oldBuffer == null) {
         return null;
      }

      final LongBuffer newBuffer = createLongBuffer(newCapacity, oldBuffer.isDirect());
      oldBuffer.rewind();
      newBuffer.put(oldBuffer);
      return newBuffer;
   }


   public static void putColor(final FloatBuffer buffer,
                               final int index,
                               final IColor color) {
      final int index3 = index * 3;
      buffer.put(index3 + 0, color.getRed());
      buffer.put(index3 + 1, color.getGreen());
      buffer.put(index3 + 2, color.getBlue());
   }


   public static void putColor(final IntBuffer buffer,
                               final int index,
                               final IColor color) {
      buffer.put(index, GColorI.getRGB(color));
   }


   public static void putVector(final DoubleBuffer buffer,
                                final int index,
                                final IVector3 vector) {
      final int index3 = index * 3;
      buffer.put(index3 + 0, vector.x());
      buffer.put(index3 + 1, vector.y());
      buffer.put(index3 + 2, vector.z());
   }


   //   public static <VectorT extends IVector<VectorT, ?>> void putVector(final DoubleBuffer buffer,
   //                                                                      final int index,
   //                                                                      final VectorT vector) {
   //      final byte dimensions = vector.dimensions();
   //      final int indexD = index * dimensions;
   //
   //      for (byte d = 0; d < dimensions; d++) {
   //         buffer.put(indexD + d, vector.get(d));
   //      }
   //   }


   public static <VectorT extends IVector<VectorT, ?>> void putVector(final FloatBuffer buffer,
                                                                      final int index,
                                                                      final VectorT vector) {
      final byte dimensions = vector.dimensions();
      final int indexD = index * dimensions;

      for (byte d = 0; d < dimensions; d++) {
         buffer.put(indexD + d, (float) vector.get(d));
      }
   }


   public static void putVector(final DoubleBuffer buffer,
                                final int index,
                                final IVector2 vector) {
      final int index3 = index * 2;
      buffer.put(index3 + 0, vector.x());
      buffer.put(index3 + 1, vector.y());
   }


   public static void putVector(final FloatBuffer buffer,
                                final int index,
                                final IVector3 vector) {
      final int index3 = index * 3;
      buffer.put(index3 + 0, (float) vector.x());
      buffer.put(index3 + 1, (float) vector.y());
      buffer.put(index3 + 2, (float) vector.z());
   }


   public static void putVector(final FloatBuffer buffer,
                                final int index,
                                final IVector2 vector) {
      final int index3 = index * 2;
      buffer.put(index3 + 0, (float) vector.x());
      buffer.put(index3 + 1, (float) vector.y());
   }


   public static void putVector(final ShortBuffer buffer,
                                final int index,
                                final IVectorI2 vector) {
      final int index3 = index * 2;
      buffer.put(index3 + 0, (short) vector.x());
      buffer.put(index3 + 1, (short) vector.y());
   }


   public static void putVector(final IntBuffer buffer,
                                final int index,
                                final IVectorI2 vector) {
      final int index3 = index * 2;
      buffer.put(index3 + 0, vector.x());
      buffer.put(index3 + 1, vector.y());
   }


   private GBufferUtils() {
   }


   public static void show(final FloatBuffer buffer) {
      final int size = buffer.capacity();
      for (int i = 0; i < size; i++) {
         System.out.println(buffer.get(i));
      }
   }

}
