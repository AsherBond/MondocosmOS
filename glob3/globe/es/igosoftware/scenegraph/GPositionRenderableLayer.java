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

import es.igosoftware.euclid.GAngle;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.IGlobeSymbolizer;
import es.igosoftware.globe.IGlobeVectorLayer;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.io.GFileLoader;
import es.igosoftware.io.GFileName;
import es.igosoftware.io.ILoader;
import es.igosoftware.loading.G3DModel;
import es.igosoftware.loading.GAsyncObjLoader;
import es.igosoftware.loading.modelparts.GMaterial;
import es.igosoftware.loading.modelparts.GModelData;
import es.igosoftware.loading.modelparts.GModelMesh;
import es.igosoftware.scenegraph.utils.GPrintSceneGraphStructureVisitor;
import es.igosoftware.util.GAssert;
import es.igosoftware.utils.GWWUtils;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.Frustum;
import gov.nasa.worldwind.geom.Line;
import gov.nasa.worldwind.geom.Matrix;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.geom.Sphere;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.AbstractLayer;
import gov.nasa.worldwind.render.DrawContext;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.media.opengl.GL;
import javax.swing.Icon;


public class GPositionRenderableLayer
         extends
            AbstractLayer
         implements
            IGlobeVectorLayer {

   private static final GFileName DEFAULT_ICON_NAME = GFileName.relative("3d.png");


   ////////////////////////////////////////////////////////////////////////////////////////////
   public static class PickResult {
      private final Object _graphicObject;
      private final Object _userData;
      private final Vec4   _position;


      PickResult(final Object graphicObject,
                 final Object userData,
                 final Vec4 position) {
         _graphicObject = graphicObject;
         _userData = userData;
         _position = position;
      }


      public Object getGraphicObject() {
         return _graphicObject;
      }


      public Object getUserData() {
         return _userData;
      }


      @Override
      public String toString() {
         return "PickResult [graphicObject=" + _graphicObject + ", userData=" + _userData + "]";
      }
   }

   ////////////////////////////////////////////////////////////////////////////////////////////
   public static interface PickListener {
      public void picked(final List<PickResult> result);
   }


   private static class NodeAndPosition {
      private final GGroupNode       _rootNode;

      private Position               _position;
      private final GElevationAnchor _anchor;
      //      private final double[]         _modelCoordinateOriginTransformArray = new double[16];
      private Matrix                 _modelCoordinateOriginTransform;


      private NodeAndPosition(final GGroupNode renderable,
                              final Position position,
                              final GElevationAnchor anchor) {
         _rootNode = renderable;
         _position = position;
         _anchor = anchor;
      }


      private void setPosition(final Position position) {
         _position = position;

         _modelCoordinateOriginTransform = null;
      }


      public Sphere getBoundingSphere() {
         if (_modelCoordinateOriginTransform == null) {
            return null;
         }

         return _rootNode.getBoundsInModelCoordinates(_modelCoordinateOriginTransform, false);
      }


      private void dispose() {
         _rootNode.dispose();
      }


      private void render(final DrawContext dc,
                          final boolean terrainChanged) {
         asureModelCoordinateOriginTransform(dc, terrainChanged);

         _rootNode.render(dc, _modelCoordinateOriginTransform, terrainChanged);
      }


      private void asureModelCoordinateOriginTransform(final DrawContext dc,
                                                       final boolean terrainChanged) {
         if (terrainChanged || (_modelCoordinateOriginTransform == null)) {
            Position position = null;

            switch (_anchor) {
               case SEA_LEVEL:
                  position = _position;
                  break;
               case SURFACE:
                  final double surfaceElevation = GWWUtils.computeSurfaceElevation(dc, _position);
                  position = new Position(_position.latitude, _position.longitude, surfaceElevation + _position.elevation);
                  break;
            }

            final Globe globe = dc.getGlobe();
            final double verticalExaggeration = dc.getVerticalExaggeration();
            _modelCoordinateOriginTransform = GWWUtils.computeModelCoordinateOriginTransform(position, globe,
                     verticalExaggeration);
         }
      }


      private void preRender(final DrawContext dc,
                             final boolean terrainChanged) {
         asureModelCoordinateOriginTransform(dc, terrainChanged);

         _rootNode.preRender(dc, _modelCoordinateOriginTransform, terrainChanged);
      }


      private boolean pick(final DrawContext dc,
                           final Line ray,
                           final boolean terrainChanged,
                           final List<PickResult> pickResults) {
         return _rootNode.pick(dc, _modelCoordinateOriginTransform, terrainChanged, ray, pickResults);
      }


   }

   //////////////////////////////////////////////////////////////////////////////////////////////////


   private final List<NodeAndPosition> _rootNodes      = new ArrayList<NodeAndPosition>();

   private Globe                       _lastGlobe;
   private double                      _lastVerticalExaggeration;
   private Frustum                     _lastFrustum;

   private final String                _name;

   private boolean                     _pushOffsetHack = false;
   private boolean                     _checkGLErrors  = false;

   private DrawContext                 _lastDC;

   private boolean                     _initialized    = false;

   private List<PickListener>          _pickListeners;

   private boolean                     _checkViewPort  = false;


   private final boolean               _dumpSceneGraph;

   private final GFileName             _iconName;
   private final IGlobeRunningContext  _context;

   private final GAsyncObjLoader       _asyncObjLoader;


   public GPositionRenderableLayer(final IGlobeRunningContext context,
                                   final String name,
                                   final boolean dumpSceneGraph) {
      this(context, name, DEFAULT_ICON_NAME, dumpSceneGraph);
   }


   public GPositionRenderableLayer(final IGlobeRunningContext context,
                                   final String name,
                                   final GFileName iconName,
                                   final boolean dumpSceneGraph) {
      _context = context;
      _name = name;
      _iconName = iconName;
      _dumpSceneGraph = dumpSceneGraph;

      final ILoader loader = new GFileLoader(GFileName.CURRENT_DIRECTORY, context.getProgressReporter());
      _asyncObjLoader = new GAsyncObjLoader(loader, false, true);
   }


   public void add3DModel(final GFileName objPath,
                          final String name,
                          final GAngle heading,
                          final Position position) {

      _asyncObjLoader.load(objPath, new GAsyncObjLoader.IHandler() {
         @Override
         public void loadError(final IOException e) {
            _context.getLogger().logSevere(e);
         }


         @Override
         public void loaded(final GModelData modelData) {
            preprocess3DModel(modelData);

            addModelData(modelData, name, position, heading);
         }
      });


   }


   public void add3DModel(final GFileName objPath,
                          final String name,
                          final Position position) {
      add3DModel(objPath, name, GAngle.ZERO, position);
   }


   private void preprocess3DModel(final GModelData model) {
      for (final GModelMesh mesh : model.getMeshes()) {
         GMaterial material = mesh.getMaterial();

         if (material == null) {
            material = new GMaterial("");
            material._diffuseColor = Color.WHITE;
            mesh.setMaterial(material);
         }
      }
   }


   private void initializeEvents() {
      final WorldWindowGLCanvas wwGLCanvas = _context.getWorldWindModel().getWorldWindowGLCanvas();

      wwGLCanvas.getInputHandler().addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(final MouseEvent e) {
            //            System.out.println(e);

            final View view = wwGLCanvas.getView();

            final Line ray = view.computeRayFromScreenPoint(e.getX(), e.getY());
            //    System.out.println("   " + ray + "\n");

            //    final Position position = view.computePositionFromScreenPoint(e.getX(), e.getY());
            //    System.out.println("   " + position);

            if (pick(ray)) {
               e.consume();
            }
         }
      });

   }


   @Override
   public String getName() {
      return _name;
   }


   //   @Override
   //   protected void doPick(final DrawContext dc,
   //                         final Point pickPoint) {
   //      draw(dc, pickPoint);
   //   }


   @Override
   protected void doPreRender(final DrawContext dc) {
      super.doPreRender(dc);

      if (dc.isPickingMode()) {
         return;
      }
      synchronized (_rootNodes) {
         if (!_initialized) {
            _initialized = true;

            initializeEvents();

            if (_dumpSceneGraph) {
               final GPrintSceneGraphStructureVisitor visitor = new GPrintSceneGraphStructureVisitor();
               for (final NodeAndPosition rootAndPosition : _rootNodes) {
                  rootAndPosition._rootNode.acceptVisitor(visitor);
                  System.out.println();
               }
            }
         }


         _lastDC = dc;

         final boolean terrainChanged = isTerrainChanged(dc);

         for (final NodeAndPosition rootAndPosition : _rootNodes) {
            rootAndPosition.preRender(dc, terrainChanged);
         }
      }
   }


   @Override
   protected void doRender(final DrawContext dc) {

      if (dc.isPickingMode()) {
         return;
      }

      final GL gl = dc.getGL();

      if (_pushOffsetHack) {
         GWWUtils.pushOffset(gl);
      }

      final boolean terrainChanged = isTerrainChanged(dc);

      synchronized (_rootNodes) {
         for (final NodeAndPosition rootAndPosition : _rootNodes) {
            rootAndPosition.render(dc, terrainChanged);
         }
      }

      if (_pushOffsetHack) {
         GWWUtils.popOffset(gl);
      }

      if (_checkGLErrors) {
         GWWUtils.checkGLErrors(dc);
      }


      GWWUtils.checkGLErrors(dc);
   }


   private boolean pick(final Line ray) {
      if (_lastDC == null) {
         return false;
      }

      if ((_pickListeners == null) || _pickListeners.isEmpty()) {
         return false;
      }


      final boolean terrainChanged = isTerrainChanged(_lastDC);

      synchronized (_rootNodes) {
         final List<PickResult> pickResults = new ArrayList<PickResult>();

         boolean picked = false;
         for (final NodeAndPosition rootAndPosition : _rootNodes) {
            if (rootAndPosition.pick(_lastDC, ray, terrainChanged, pickResults)) {
               picked = true;
            }
         }

         final Vec4 eyePosition = _lastDC.getView().getEyePoint();
         Collections.sort(pickResults, new Comparator<PickResult>() {
            @Override
            public int compare(final PickResult o1,
                               final PickResult o2) {
               final double distance1 = o1._position.distanceToSquared3(eyePosition);
               final double distance2 = o2._position.distanceToSquared3(eyePosition);

               return Double.compare(distance1, distance2);
            }
         });

         final List<PickResult> unmodifiablePickResults = Collections.unmodifiableList(pickResults);
         for (final PickListener listener : _pickListeners) {
            listener.picked(unmodifiablePickResults);
         }

         return picked;
      }
   }


   private boolean isTerrainChanged(final DrawContext dc) {

      final Globe globe = dc.getGlobe();
      final double verticalExaggeration = dc.getVerticalExaggeration();

      final boolean terrainChanged;

      if (_checkViewPort) {
         //         final Rectangle currentViewport = dc.getView().getViewport();

         final Frustum currentFrustum = dc.getView().getFrustumInModelCoordinates();

         //         terrainChanged = ((globe != _lastGlobe) || (verticalExaggeration != _lastVerticalExaggeration) || ((_checkViewPort) && (currentViewport != _lastViewport)));
         terrainChanged = ((globe != _lastGlobe) || (verticalExaggeration != _lastVerticalExaggeration) || ((_checkViewPort) && (!currentFrustum.equals(_lastFrustum))));

         if (terrainChanged) {
            _lastGlobe = globe;
            _lastVerticalExaggeration = verticalExaggeration;
            _lastFrustum = currentFrustum;
         }
      }
      else {
         terrainChanged = ((globe != _lastGlobe) || (verticalExaggeration != _lastVerticalExaggeration));

         if (terrainChanged) {
            _lastGlobe = globe;
            _lastVerticalExaggeration = verticalExaggeration;
         }
      }

      return terrainChanged;

      //
      //      final Globe globe = dc.getGlobe();
      //      final double verticalExaggeration = dc.getVerticalExaggeration();
      //      final Rectangle currentViewport = dc.getView().getViewport();
      //
      //      final boolean terrainChanged = ((globe != _lastGlobe) || (verticalExaggeration != _lastVerticalExaggeration) || ((_checkViewPort) && (currentViewport != _lastViewport)));
      //      //      final boolean terrainChanged = ((globe != _lastGlobe) || (verticalExaggeration != _lastVerticalExaggeration));
      //      if (terrainChanged) {
      //         _lastGlobe = globe;
      //         _lastVerticalExaggeration = verticalExaggeration;
      //         _lastViewport = currentViewport;
      //      }
      //
      //      return terrainChanged;
   }


   @Override
   public Sector getExtent() {

      if (_rootNodes.isEmpty()) {
         return null;
      }

      if (isEnabled()) {
         final List<Sphere> objectsBounds = new ArrayList<Sphere>();

         for (final NodeAndPosition nodeAndPosition : _rootNodes) {
            final Sphere boundingSphere = nodeAndPosition.getBoundingSphere();
            if (boundingSphere != null) {
               objectsBounds.add(boundingSphere);
            }
         }

         if (objectsBounds.isEmpty()) {
            return null;
         }

         final Globe globe = _context.getWorldWindModel().getGlobe();
         final Sphere totalbounds = Sphere.createBoundingSphere(objectsBounds);
         final Sector totalSector = Sector.boundingSector(globe, globe.computePositionFromPoint(totalbounds.getCenter()),
                  totalbounds.getRadius());

         return totalSector;
      }

      return null;
   }


   @Override
   public Icon getIcon(final IGlobeRunningContext context) {
      return context.getBitmapFactory().getSmallIcon(_iconName);
   }


   //   @Override
   //   public GProjection getProjection() {
   //      return GProjection.EPSG_4326;
   //   }


   public void setPosition(final GGroupNode renderable,
                           final Position position) {
      boolean changedPosition = false;
      synchronized (_rootNodes) {
         for (final NodeAndPosition rootAndPosition : _rootNodes) {
            if (rootAndPosition._rootNode == renderable) {
               rootAndPosition.setPosition(position);
               changedPosition = true;
            }
         }
      }

      if (changedPosition) {
         _checkViewPort = true;
         _lastGlobe = null; // force recalculation
         redraw();
      }
      else {
         throw new RuntimeException("Renderable: " + renderable + " not found in layer " + this);
      }
   }


   public void addNode(final GGroupNode root,
                       final Position position,
                       final GElevationAnchor anchor) {
      GAssert.notNull(root, "root");
      GAssert.notNull(position, "position");
      GAssert.notNull(anchor, "anchor");

      if (root.getParent() != null) {
         throw new RuntimeException("Can't add to the layer a non-root node");
      }

      synchronized (_rootNodes) {
         root.setLayer(this);

         if (anchor == GElevationAnchor.SURFACE) {
            _checkViewPort = true;
            _lastGlobe = null; // force recalculation
         }

         _rootNodes.add(new NodeAndPosition(root, position, anchor));
      }

      redraw();
   }


   @Override
   public void dispose() {
      synchronized (_rootNodes) {
         for (final NodeAndPosition rootAndPosition : _rootNodes) {
            rootAndPosition.dispose();
         }
         _rootNodes.clear();
      }

      super.dispose();
   }


   @Override
   public void redraw() {
      // fire event to force a redraw
      firePropertyChange(AVKey.LAYER, null, this);
   }


   @Override
   public IGlobeFeatureCollection getFeaturesCollection() {
      return null;
   }


   public boolean isPushOffsetHack() {
      return _pushOffsetHack;
   }


   public void setPushOffsetHack(final boolean pushOffsetHack) {
      _pushOffsetHack = pushOffsetHack;
   }


   public void setCheckGLErrors(final boolean checkGLErrors) {
      _checkGLErrors = checkGLErrors;
   }


   public void addPickListener(final GPositionRenderableLayer.PickListener pickListener) {
      if (_pickListeners == null) {
         _pickListeners = new ArrayList<GPositionRenderableLayer.PickListener>();
      }
      _pickListeners.add(pickListener);
   }


   @Override
   public List<ILayerAttribute<?>> getLayerAttributes(final IGlobeRunningContext context) {
      return null;
   }


   @Override
   public void doDefaultAction(final IGlobeRunningContext context) {
      if (isEnabled()) {
         context.getCameraController().animatedZoomToSector(getExtent());
      }
   }


   @Override
   public List<? extends ILayerAction> getLayerActions(final IGlobeRunningContext context) {
      return null;
   }


   @Override
   public IGlobeSymbolizer getSymbolizer() {
      return null;
   }


   private void addModelData(final GModelData modelData,
                             final String name,
                             final Position position,
                             final GAngle heading) {
      final G3DModel model = new G3DModel(modelData);
      final G3DModelNode modelNode = new G3DModelNode(name, GTransformationOrder.ROTATION_SCALE_TRANSLATION, model);

      final GGroupNode modelRootNode = new GGroupNode("Root " + name, GTransformationOrder.ROTATION_SCALE_TRANSLATION);
      modelRootNode.setHeading(heading);
      modelRootNode.addChild(modelNode);

      addNode(modelRootNode, position, GElevationAnchor.SEA_LEVEL);
   }


}
