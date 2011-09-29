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


package es.igosoftware.panoramic;

import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.globe.IGlobeCameraController;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.IGlobeSymbolizer;
import es.igosoftware.globe.IGlobeVectorLayer;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.animations.GGlobeAnimatorLayer;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.globe.layers.hud.GHUDLayer;
import es.igosoftware.globe.view.GInputState;
import es.igosoftware.globe.view.customView.GView;
import es.igosoftware.io.GFileName;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.IFunction;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.event.InputHandler;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Line;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.geom.Sphere;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.AbstractLayer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.render.DrawContext;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;


public class GPanoramicLayer
         extends
            AbstractLayer
         implements
            IGlobeVectorLayer {


   public static interface PickListener {
      public void picked(final GPanoramic pickedPanoramic);
   }


   private final IGlobeRunningContext               _context;
   private final String                             _name;
   private final List<GPanoramic>                   _panoramics    = new ArrayList<GPanoramic>();

   private final List<GPanoramicLayer.PickListener> _pickListeners = new ArrayList<GPanoramicLayer.PickListener>();

   private final Set<Layer>                         _hiddenLayers  = new HashSet<Layer>();

   private boolean                                  _isInitialized = false;

   private Sector                                   _extent;


   public GPanoramicLayer(final IGlobeRunningContext context,
                          final String name) {
      GAssert.notNull(context, "context");
      GAssert.notNull(name, "name");

      _context = context;
      _name = name;
   }


   @Override
   public String getName() {
      return _name;
   }


   public void addPanoramic(final GPanoramic panoramic) {
      if (isDuplicateName(panoramic.getName())) {
         throw new RuntimeException("A Panoramic with the name " + panoramic.getName() + " already exists!!");
      }

      _extent = null; // remove cached extent value to force a recalculation
      _panoramics.add(panoramic);

      panoramic.addActivationListener(new GPanoramic.ActivationListener() {
         @Override
         public void activated() {
            hideOtherLayers(panoramic.getHUDLayer());
            hideOtherPanoramics(panoramic);
            redraw();
         }


         @Override
         public void deactivated() {
            unhideHiddenPanoramics();
            unhideHiddenLayers();
            redraw();
         }
      });
   }


   private boolean isDuplicateName(final String name) {
      for (final GPanoramic panoramic : _panoramics) {
         if (name.equals(panoramic.getName())) {
            return true;
         }
      }
      return false;
   }


   @Override
   public Icon getIcon(final IGlobeRunningContext context) {
      return context.getBitmapFactory().getSmallIcon(GFileName.relative("panoramic.png"));
   }


   @Override
   public Sector getExtent() {
      if (isEnabled()) {
         if (_extent == null) {
            _extent = calculateExtent();
         }
         return _extent;
      }

      return null;
   }


   private Sector calculateExtent() {
      final List<Sphere> panoramicsBounds = GCollections.collect(_panoramics, new IFunction<GPanoramic, Sphere>() {
         @Override
         public Sphere apply(final GPanoramic panoramic) {
            return panoramic.getGlobalBounds();
         }
      });

      final Globe globe = _context.getWorldWindModel().getGlobe();
      final Sphere totalbounds = Sphere.createBoundingSphere(panoramicsBounds);

      return Sector.boundingSector(globe, globe.computePositionFromPoint(totalbounds.getCenter()), totalbounds.getRadius());
   }


   //   @Override
   //   public GProjection getProjection() {
   //      return GProjection.EPSG_4326;
   //   }


   @Override
   public void redraw() {
      firePropertyChange(AVKey.LAYER, null, this);
   }


   @Override
   public List<ILayerAttribute<?>> getLayerAttributes(final IGlobeRunningContext context) {
      return null;
   }


   @Override
   public void doDefaultAction(final IGlobeRunningContext context) {
      if (isEnabled()) {

         final GView view = context.getWorldWindModel().getView();
         if (view.getInputState() == GInputState.PANORAMICS) {
            return;
         }

         final Sector sector = getExtent();

         final IGlobeCameraController cameraController = context.getCameraController();
         final double altitude = cameraController.calculateAltitudeForZooming(sector);
         cameraController.animatedGoTo(new Position(sector.getCentroid(), 0), Angle.ZERO, Angle.fromDegrees(75), altitude);
      }
   }


   @Override
   public List<? extends ILayerAction> getLayerActions(final IGlobeRunningContext context) {
      return null;
   }


   @Override
   public IGlobeFeatureCollection getFeaturesCollection() {
      return null;
   }


   @Override
   protected void doRender(final DrawContext dc) {
      if (dc.isPickingMode()) {
         return;
      }

      if (!_isInitialized) {
         initializeEvents();
         _isInitialized = true;

      }

      for (final GPanoramic panoramic : _panoramics) {
         if (!panoramic.isHidden()) {
            panoramic.doRender(dc);
         }
      }


   }


   private Line createRay(final MouseEvent evt) {
      final View view = _context.getWorldWindModel().getView();
      return view.computeRayFromScreenPoint(evt.getX(), evt.getY());
   }


   private void initializeEvents() {
      final InputHandler inputHandler = _context.getWorldWindModel().getWorldWindowGLCanvas().getInputHandler();

      inputHandler.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(final MouseEvent e) {
            final Line ray = createRay(e);

            if (tryToPick(ray)) {
               e.consume();
            }
         }
      });

      inputHandler.addMouseMotionListener(new MouseAdapter() {
         private GPanoramic _lastPanoramicOnMouse = null;


         @Override
         public void mouseMoved(final MouseEvent e) {
            final GPanoramic pickedPanoramic = getClosestIntersertingPanoramic(createRay(e));

            if ((pickedPanoramic == null) || pickedPanoramic.isActive()) {
               if (_lastPanoramicOnMouse != null) {
                  _lastPanoramicOnMouse.mouseExited(e);

                  _lastPanoramicOnMouse = null;
               }
            }
            else if (_lastPanoramicOnMouse != pickedPanoramic) {
               if (_lastPanoramicOnMouse != null) {
                  _lastPanoramicOnMouse.mouseExited(e);
               }

               pickedPanoramic.mouseEntered(e);

               _lastPanoramicOnMouse = pickedPanoramic;
            }
         }
      });


   }


   private boolean tryToPick(final Line ray) {
      final GPanoramic pickedPanoramic = getClosestIntersertingPanoramic(ray);
      if (pickedPanoramic == null) {
         return false;
      }

      if (!pickedPanoramic.isActive()) {
         pickedPanoramic.activate(_context);

         for (final GPanoramicLayer.PickListener listener : _pickListeners) {
            listener.picked(pickedPanoramic);
         }

         return true;
      }

      return false;
   }


   private GPanoramic getClosestIntersertingPanoramic(final Line ray) {
      double closestDistance = Double.MAX_VALUE;
      GPanoramic closestPanoramic = null;

      for (final GPanoramic candidatePanoramic : _panoramics) {
         if (candidatePanoramic.getGlobalBounds().intersects(ray)) {
            final double candidateDistance = candidatePanoramic.getCurrentDistanceFromEye();
            if (candidateDistance < closestDistance) {
               closestDistance = candidateDistance;
               closestPanoramic = candidatePanoramic;
            }
         }
      }

      return closestPanoramic;
   }


   private void hideOtherPanoramics(final GPanoramic visiblePanoramic) {
      for (final GPanoramic panoramic : _panoramics) {
         if (panoramic == visiblePanoramic) {
            continue;
         }
         panoramic.setHidden(true);
      }
   }


   private void unhideHiddenPanoramics() {
      for (final GPanoramic panoramic : _panoramics) {
         panoramic.setHidden(false);
      }
   }


   private void hideOtherLayers(final GHUDLayer hudLayer) {
      for (final Layer layer : _context.getWorldWindModel().getLayerList()) {
         if ((layer == this) || (layer == hudLayer)) {
            continue;
         }

         if (layer instanceof GGlobeAnimatorLayer) {
            continue;
         }

         if (layer.isEnabled()) {
            _hiddenLayers.add(layer);
            layer.setEnabled(false);
         }
      }
   }


   private void unhideHiddenLayers() {
      for (final Layer layer : _hiddenLayers) {
         layer.setEnabled(true);
      }
      _hiddenLayers.clear();
   }


   @Override
   public IGlobeSymbolizer getSymbolizer() {
      return null;
   }


   @Override
   public void dispose() {
      super.dispose();

      for (final GPanoramic panoramic : _panoramics) {
         panoramic.dispose();
      }
   }


}
