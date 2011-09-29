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


package es.igosoftware.euclid.rendering;

import java.nio.FloatBuffer;

import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;


public interface IRenderer {

   public static final int GL_POINTS         = 0x0;

   public static final int GL_POINT_SMOOTH   = 0xb10;

   public static final int GL_LIGHTING       = 0xb50;

   public static final int GL_FLOAT          = 0x1406;

   public static final int GL_VERTEX_ARRAY   = 0x8074;
   public static final int GL_NORMAL_ARRAY   = 0x8075;
   public static final int GL_COLOR_ARRAY    = 0x8076;
   public static final int GL_COLOR_MATERIAL = 0xb57;

   public static final int GL_COMPILE        = 0x1300;


   public void glPointSize(final float pointSize);


   public void glEnable(final int cap);


   public void glEnableClientState(final int cap);


   public void glDisable(final int cap);


   public void glDisableClientState(final int cap);


   public void glVertexPointer(final int size,
                               final int stride,
                               final FloatBuffer buffer);


   public void glNormalPointer(final int stride,
                               final FloatBuffer buffer);


   public void glColorPointer(final int size,
                              final int stride,
                              final FloatBuffer buffer);


   public void glDrawArrays(final int mode,
                            final int first,
                            final int count);


   public void glColor(final float red,
                       final float green,
                       final float blue);


   public void glRotate(final float angle,
                        final float x,
                        final float y,
                        final float z);


   public void glTranslate(final float x,
                           final float y,
                           final float z);


   public void glVertex(final float x,
                        final float y,
                        final float z);


   public void glVertex(final float x,
                        final float y);


   public void glNormal(final float nx,
                        final float ny,
                        final float nz);


   public void glBegin(final int mode);


   public void glEnd();


   public int glGenLists(final int range);


   public void glNewList(final int list,
                         final int mode);


   public void glEndList();


   public void glCallList(final int list);


   // high level functions
   public void renderPointsNormalsColors(final float pointSize,
                                         final boolean antialiasing,
                                         final byte dimensions,
                                         final FloatBuffer points,
                                         final FloatBuffer normals,
                                         final FloatBuffer colors);


   public <VectorT extends IVector<VectorT, ?>> int createDisplayList(final float pointSize,
                                                                      final IVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> vertices);

}
