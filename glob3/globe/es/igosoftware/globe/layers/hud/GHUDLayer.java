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


package es.igosoftware.globe.layers.hud;

import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.util.GAssert;
import es.igosoftware.utils.GWWUtils;
import gov.nasa.worldwind.event.InputHandler;
import gov.nasa.worldwind.layers.AbstractLayer;
import gov.nasa.worldwind.render.DrawContext;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;


public class GHUDLayer
         extends
            AbstractLayer {


   private final List<IHUDElement>    _elements    = new ArrayList<IHUDElement>();
   private boolean                    _initialized = false;
   private final IGlobeRunningContext _context;


   public GHUDLayer(final IGlobeRunningContext context) {
      super();
      setPickEnabled(false);
      _context = context;
   }


   @Override
   public String getName() {
      return "HUD Layer";
   }


   @Override
   protected void doRender(final DrawContext dc) {
      draw(dc);
   }


   @Override
   protected void doPick(final DrawContext dc,
                         final Point pickedPoint) {
      //      draw(dc);
   }


   private void draw(final DrawContext dc) {
      if (!_initialized) {
         _initialized = true;
         initializeMouseEvents();
      }

      for (final IHUDElement each : _elements) {
         if (each.isEnable()) {
            dc.addOrderedRenderable(each);
         }
      }

      GWWUtils.checkGLErrors(dc);
   }


   private void initializeMouseEvents() {

      final InputHandler inputHandler = _context.getWorldWindModel().getWorldWindowGLCanvas().getInputHandler();

      inputHandler.addMouseMotionListener(new MouseMotionAdapter() {
         @Override
         public void mouseMoved(final MouseEvent e) {
            final int x = e.getX();
            final int y = e.getY();

            for (final IHUDElement element : _elements) {
               if (element.isEnable() && element.hasActionListeners()) {
                  final Rectangle screenBounds = element.getLastScreenBounds();
                  if (screenBounds != null) {
                     final boolean mouseOver = screenBounds.contains(x, y);
                     element.setHighlighted(mouseOver);
                  }
               }
            }
         }
      });


      inputHandler.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseExited(final MouseEvent e) {
            for (final IHUDElement element : _elements) {
               //               if (element.isEnable() && element.hasActionListeners()) {
               element.setHighlighted(false);
               //               }
            }
         }


         @Override
         public void mouseClicked(final MouseEvent e) {

            //TODO: find out why we have to comment this...
            //            if (e.isConsumed()) {
            //               return;
            //            }

            boolean consumed = false;

            final int x = e.getX();
            final int y = e.getY();

            for (final IHUDElement element : _elements) {
               if (element.isEnable() && element.hasActionListeners()) {
                  final Rectangle screenBounds = element.getLastScreenBounds();
                  if (screenBounds != null) {
                     final boolean mouseOver = screenBounds.contains(x, y);
                     if (mouseOver) {
                        element.mouseClicked(e);
                        consumed = true;
                     }
                  }
               }
            }

            if (consumed) {
               e.consume();
            }
         }
      });


   }


   public boolean hasElement(final IHUDElement element) {
      return _elements.contains(element);
   }


   public void addElement(final IHUDElement element) {
      GAssert.notNull(element, "element");

      _elements.add(element);
   }

}
