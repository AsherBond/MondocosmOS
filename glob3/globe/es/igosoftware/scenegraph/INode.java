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

import es.igosoftware.scenegraph.GPositionRenderableLayer.PickResult;
import gov.nasa.worldwind.Disposable;
import gov.nasa.worldwind.geom.Extent;
import gov.nasa.worldwind.geom.Line;
import gov.nasa.worldwind.geom.Matrix;
import gov.nasa.worldwind.render.DrawContext;

import java.util.List;


public interface INode
         extends
            Disposable {


   public boolean isPickable();


   public boolean pick(final DrawContext dc,
                       final Matrix parentMatrix,
                       final boolean terrainChanged,
                       final Line ray,
                       final List<PickResult> pickResults);


   public String getName();


   public void setVisible(final boolean visible);


   public boolean isVisible();


   //   public Extent getBounds();


   public GGroupNode getParent();


   public GGroupNode getRoot();


   public void setParent(final GGroupNode parent);


   public void reparentTo(final GGroupNode parent);


   public void preRender(final DrawContext dc,
                         final Matrix parentMatrix,
                         final boolean terrainChanged);


   public void render(final DrawContext dc,
                      final Matrix parentMatrix,
                      final boolean terrainChanged);


   public Extent getBoundsInModelCoordinates(final Matrix parentMatrix,
                                             final boolean matrixChanged);


   public void cleanCaches();


   public boolean hasScaleTransformation();


   public void redraw();


   public void addDisposeListener(final Runnable runnable);


   public void acceptVisitor(final IVisitor visitor);


   public int getDepth();


}
