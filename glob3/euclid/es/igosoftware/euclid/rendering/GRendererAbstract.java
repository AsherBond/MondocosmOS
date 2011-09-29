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

import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;


public abstract class GRendererAbstract
         implements
            IRenderer {

   @Override
   public void renderPointsNormalsColors(final float pointSize,
                                         final boolean antialiasing,
                                         final byte dimensions,
                                         final FloatBuffer points,
                                         final FloatBuffer normals,
                                         final FloatBuffer colors) {
      if ((points == null) || (points.capacity() == 0)) {
         return;
      }

      glPointSize(pointSize);

      if (antialiasing) {
         glEnable(GL_POINT_SMOOTH);
      }
      else {
         glDisable(GL_POINT_SMOOTH);
      }

      glEnableClientState(GL_VERTEX_ARRAY);
      glVertexPointer(dimensions, 0, points);

      final boolean useNormals = (dimensions == 3) && (normals != null);
      if (useNormals) {
         glEnableClientState(GL_NORMAL_ARRAY);

         glNormalPointer(0, normals);
      }
      else {
         glDisable(GL_LIGHTING);
      }

      if (colors != null) {
         glEnableClientState(GL_COLOR_ARRAY);
         glEnable(GL_COLOR_MATERIAL);

         glColorPointer(3, 0, colors);
      }


      glDrawArrays(GL_POINTS, 0, points.capacity() / dimensions);


      //clean up
      if (colors != null) {
         glDisableClientState(GL_COLOR_ARRAY);
         glDisable(GL_COLOR_MATERIAL);
      }

      if (useNormals) {
         glDisableClientState(GL_NORMAL_ARRAY);
      }
      else {
         glEnable(GL_LIGHTING);
      }

      glDisableClientState(GL_VERTEX_ARRAY);
   }


   @Override
   public <VectorT extends IVector<VectorT, ?>> int createDisplayList(final float pointSize,
                                                                      final IVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> vertices) {
      final int listID = glGenLists(1);

      glNewList(listID, GL_COMPILE);
      {
         glPointSize(pointSize);

         glBegin(GL_POINTS);
         {
            final int verticesCount = vertices.size();
            final byte dimensions = vertices.dimensions();

            final byte x = (byte) 0;
            final byte y = (byte) 1;
            final byte z = (byte) 2;

            for (int i = 0; i < verticesCount; i++) {
               final VectorT point = vertices.getPoint(i);
               if (dimensions == 3) {
                  glVertex((float) point.get(x), (float) point.get(y), (float) point.get(z));
               }
               else {
                  glVertex((float) point.get(x), (float) point.get(y));
               }

               if (vertices.hasColors()) {
                  final IColor color = vertices.getColor(i);
                  glColor(color.getRed(), color.getGreen(), color.getBlue());
               }

               if ((dimensions == 3) && vertices.hasNormals()) {
                  final VectorT normal = vertices.getNormal(i);
                  glNormal((float) normal.get(x), (float) normal.get(y), (float) normal.get(z));
               }
            }
         }
         glEnd();
      }
      glEndList();

      return listID;
   }
}
