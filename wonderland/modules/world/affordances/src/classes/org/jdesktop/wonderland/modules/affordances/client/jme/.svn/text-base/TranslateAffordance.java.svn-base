/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * Sun designates this particular file as subject to the "Classpath" 
 * exception as provided by Sun in the License file that accompanied 
 * this code.
 */
package org.jdesktop.wonderland.modules.affordances.client.jme;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.GeometricUpdateListener;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Arrow;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState.StateType;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Formatter;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.RenderManager;
import org.jdesktop.mtgame.RenderUpdater;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.input.EventClassListener;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.input.MouseButtonEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseDraggedEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseEvent3D.ButtonId;

/**
 * Visual affordance (manipulator) to move a cell around in the world.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class TranslateAffordance extends Affordance {

    /* The length scaling factor for each arrow */
    private static final float LENGTH_SCALE = 1.5f;

    /* The width (thickness) for each arrow */
    private static final float THICKNESS = 0.10f;

    /* The constant amount to extent the arrows beyond the size of the object */
    private static final float LENGTH_OFFSET = 0.1f;

    /* The current scale of the affordance w.r.t the size of the cell */
    private float currentScale = LENGTH_SCALE;

    /* The original extent of the object, before it was modified */
    private float extent = 0.0f;

    /* The root of the scene graph of the cell */
    private Node sceneRoot = null;

    /** An enumeration of the axis along which to effect the drag motion */
    public enum TranslateAxis {
        X_AXIS, Y_AXIS, Z_AXIS
    }

    /* The nodes representing the double-edged arrows for each axis */
    private Node xNode = null, yNode = null, zNode = null;

    /* The endities representing the double-edged arrows for each axis */
    private Entity xEntity = null, yEntity = null, zEntity = null;

    /* Listener for changes in the transform of the cell */
    private GeometricUpdateListener updateListener = null;

    /* Listeners for drag events for each axis */
    private TranslateDragListener xListener = null, yListener = null, zListener = null;

    /**
     * Creates a new translate affordance given the cell to  which it is going
     * to be added. Also adds the movable component if not already present
     *
     * @param cell
     * @throw AffordanceException Upon error creating the affordance
     */
    public TranslateAffordance(Node sceneRoot) {
        super("Translate");

        // Figure out the bounds of the root entity of the cell and create an
        // arrow to be just a bit larger than that
        this.sceneRoot = sceneRoot;
        BoundingVolume bounds = sceneRoot.getWorldBound();
        if (bounds instanceof BoundingSphere) {
            extent = ((BoundingSphere)bounds).radius;
        }
        else if (bounds instanceof BoundingBox) {
            float xExtent = ((BoundingBox)bounds).xExtent;
            float yExtent = ((BoundingBox)bounds).yExtent;
            float zExtent = ((BoundingBox)bounds).zExtent;
            extent = Math.max(xExtent, Math.max(yExtent, zExtent));
        }
        
        // Fetch the world translation for the root node of the cell and set
        // the translation for this entity root node
        Vector3f translation = sceneRoot.getWorldTranslation();
        rootNode.setLocalTranslation(translation);
        
        // Create a red arrow in the +x direction. We arrow we get back is
        // pointed in the +y direction, so we rotate around the -z axis to
        // orient the arrow properly.
        xEntity = new Entity("Entity X");
        xNode = createArrow("Arrow X", extent + LENGTH_OFFSET, THICKNESS, ColorRGBA.red);
        Quaternion xRotation = new Quaternion().fromAngleAxis((float)Math.PI / 2, new Vector3f(0, 0, -1));
        xNode.setLocalRotation(xRotation);
        xNode.setLocalScale(new Vector3f(1.0f, LENGTH_SCALE, 1.0f));
        xNode.setRenderState(zbuf);
        addSubEntity(xEntity, xNode);
        xListener = addDragListener(xEntity, xNode, TranslateAxis.X_AXIS);

        // Create a green arrow in the +y direction. We arrow we get back is
        // pointed in the +y direction.
        yEntity = new Entity("Entity Y");
        yNode = createArrow("Arrow Y", extent + LENGTH_OFFSET, THICKNESS, ColorRGBA.green);
        yNode.setLocalScale(new Vector3f(1.0f, LENGTH_SCALE, 1.0f));
        yNode.setRenderState(zbuf);
        addSubEntity(yEntity, yNode);
        yListener = addDragListener(yEntity, yNode, TranslateAxis.Y_AXIS);

        // Create a red arrow in the +z direction. We arrow we get back is
        // pointed in the +y direction, so we rotate around the +x axis to
        // orient the arrow properly.
        zEntity = new Entity("Entity Z");
        zNode = createArrow("Arrow Z", extent + LENGTH_OFFSET, THICKNESS, ColorRGBA.blue);
        Quaternion zRotation = new Quaternion().fromAngleAxis((float)Math.PI / 2, new Vector3f(1, 0, 0));
        zNode.setLocalRotation(zRotation);
        zNode.setRenderState(zbuf);
        zNode.setLocalScale(new Vector3f(1.0f, LENGTH_SCALE, 1.0f));
        addSubEntity(zEntity, zNode);
        zListener = addDragListener(zEntity, zNode, TranslateAxis.Z_AXIS);

        // Listen for changes to the cell's translation and apply the same
        // update to the root node of the affordances. We also re-set the size
        // of the affordances: this handles the case where the bounds of the
        // scene graph has changed and we need to update the affordances
        // accordingly.
        sceneRoot.addGeometricUpdateListener(updateListener = new GeometricUpdateListener() {
            public void geometricDataChanged(final Spatial spatial) {
                // We need to perform this work inside a proper updater, to
                // make sure we are MT thread safe
                RenderUpdater u = new RenderUpdater() {
                    public void update(Object obj) {
                        Vector3f translation = spatial.getWorldTranslation();
                        rootNode.setLocalTranslation(translation);
                        setSizeInternal(currentScale);
                        ClientContextJME.getWorldManager().addToUpdateList(rootNode);
                    }
                };
                ClientContextJME.getWorldManager().addRenderUpdater(u, this);
            }
        });
    }

    /**
     * @inheritDoc()
     */
    public void setSize(final float size) {
        // Sets the size of the affordance in a thread-safe manner
        RenderUpdater u = new RenderUpdater() {
            public void update(Object obj) {
                setSizeInternal(size);
            }
        };
        ClientContextJME.getWorldManager().addRenderUpdater(u, this);
    }

    /**
     * Sets the size of the translate affordance, based upon the bounds of the
     * screen graph of the Cell. Calls of this method should make sure it is
     * invoked properly in MT Game to be thread safe.
     */
    private void setSizeInternal(float size) {
        // To set the scale properly, we need to compute the scale w.r.t the
        // current size of the object as a ratio of the original size of the
        // object (in case the size of the object has changed).
        currentScale = size;
        float xScale = 0.0f, yScale = 0.0f, zScale = 0.0f;
        BoundingVolume bounds = sceneRoot.getWorldBound();
        if (bounds instanceof BoundingSphere) {
            float newExtent = ((BoundingSphere)bounds).radius;
            xScale = yScale = zScale = (newExtent / extent) * currentScale;
        }
        else if (bounds instanceof BoundingBox) {
            float newXExtent = ((BoundingBox)bounds).xExtent;
            float newYExtent = ((BoundingBox)bounds).yExtent;
            float newZExtent = ((BoundingBox)bounds).zExtent;
            float newExtent = Math.max(newXExtent, Math.max(newYExtent, newZExtent));
            xScale = yScale = zScale = (newExtent / extent) * currentScale;
        }

        // In order to set the size of the arrows, we just set the scaling. Note
        // that we set the scaling along the +y axis since all arrows are
        // created facing that direction.
        xNode.setLocalScale(new Vector3f(1.0f, xScale, 1.0f));
        yNode.setLocalScale(new Vector3f(1.0f, yScale, 1.0f));
        zNode.setLocalScale(new Vector3f(1.0f, zScale, 1.0f));
        ClientContextJME.getWorldManager().addToUpdateList(xNode);
        ClientContextJME.getWorldManager().addToUpdateList(yNode);
        ClientContextJME.getWorldManager().addToUpdateList(zNode);
    }

    /**
     * @inheritDoc()
     */
    @Override
    public void dispose() {
        // Call the superclass dispose() first, to make sure the affordance
        // is no longer visible
        super.dispose();

        // Clean up all of the listeners so this class gets properly garbage
        // collected.
        sceneRoot.removeGeometricUpdateListener(updateListener);
        xListener.removeFromEntity(xEntity);
        yListener.removeFromEntity(yEntity);
        zListener.removeFromEntity(zEntity);
        xListener = yListener = zListener = null;
        xEntity = yEntity = zEntity = null;
        updateListener = null;
        listenerSet.clear();
    }

    /**
     * Adds a drag listener for the Entity and the node, given the axis along
     * which the drag should take place.
     */
    private TranslateDragListener addDragListener(Entity entity, Node node,
            TranslateAxis direction) {
        
        makeEntityPickable(entity, node);
        TranslateDragListener l = new TranslateDragListener(direction);
        l.addToEntity(entity);
        return l;
    }

    /**
     * Creates a double-ended arrow, given its half-length, thickness and color.
     * Returns a Node representing the new geometry. Fills in the affordance
     * arrow object with each jME arrow object.
     */
    private Node createArrow(String name, float length, float width, ColorRGBA color) {

        // Create the two arrows with the proper name, length, thickness, and
        // color.
        Arrow a1 = new Arrow(name + " 1", length, width);
        a1.setSolidColor(color);
        Arrow a2 = new Arrow(name + " 2", length, width);
        a2.setSolidColor(color);

        // Create the main node and set the material state on the node so the
        // color shows up
        Node n = new Node();
        RenderManager rm = ClientContextJME.getWorldManager().getRenderManager();
        MaterialState matState3 = (MaterialState)rm.createRendererState(StateType.Material);
        matState3.setDiffuse(color);
        n.setRenderState(matState3);

        // Create a sub-node to hold the first arrow. We must translate it up,
        // so that the end is at (0, 0, 0) in the local coordinate space of
        // the node we return
        Node subNode1 = new Node();
        subNode1.setLocalTranslation(0, length / 2, 0);
        subNode1.attachChild(a1);

        // Create a sub-node to hold the second arrow. We must rotate it 180
        // degrees (about the +y axis since arrows by default point up). We
        // also must translate it down. Attach the second arrow to this node.
        Node subNode2 = new Node();
        Quaternion q = new Quaternion().fromAngleAxis((float)Math.PI, new Vector3f(0, 0, 1));
        subNode2.setLocalRotation(q);
        subNode2.setLocalTranslation(0, -length / 2, 0);
        subNode2.attachChild(a2);

        // Attach the first arrow and the subnode to the main node
        n.attachChild(subNode1);
        n.attachChild(subNode2);

        // Set the bounds on the arrows and update them
        a1.setModelBound(new BoundingSphere());
        a1.updateModelBound();
        a2.setModelBound(new BoundingSphere());
        a2.updateModelBound();

        return n;
    }

    private Set<TranslationListener> listenerSet = new HashSet();

    /**
     * Adds a listener for translation events. If the listener has already
     * been added, this method does nothing.
     *
     * @param listener The listener to add
     */
    public void addTranslationListener(TranslationListener listener) {
        synchronized (listenerSet) {
            listenerSet.add(listener);
        }
    }

    /**
     * Removes a listener for translation events. If the listener does not
     * exist, this method does nothing.
     *
     * @param listener The listener to remove
     */
    public void removeTranslationListener(TranslationListener listener) {
        synchronized (listenerSet) {
            listenerSet.remove(listener);
        }
    }

    /**
     * Informs all of the listeners that a translation has begun
     */
    private void fireTranslationStarted() {
        synchronized (listenerSet) {
            for (TranslationListener listener : listenerSet) {
                listener.translationStarted();
            }
        }
    }

    /**
     * Informs all of the listeners of the new translation
     */
    private void fireTranslationChanged(Vector3f translation) {
        synchronized (listenerSet) {
            for (TranslationListener listener : listenerSet) {
                listener.translationPerformed(translation);
            }
        }
    }
    
    /**
     * Listener for translation events.
     */
    public interface TranslationListener {
        /**
         * Indicates that the translation has begun using the affordance.
         */
        public void translationStarted();
        
        /**
         * Indicates that the translation affordance has been moved by a certain
         * amount, giving a Vector3f.
         *
         * @param translation The translation amount as a 3D vector
         */
        public void translationPerformed(Vector3f translation);
    }

    /**
     * Inner class that handles the dragging movement and updates the position
     * of the cell accordingly
     */
    private class TranslateDragListener extends EventClassListener {

        // The axis along which to effect the translation
        private TranslateAxis direction;

        // The intersection point on the entity over which the button was
        // pressed, in world coordinates.
        private Vector3f dragStartWorld;

        // The screen coordinates of the button press event.
        private Point dragStartScreen;

        // The label (and frame) to display the current drag amount
        private JFrame labelFrame = null;
        private JLabel positionLabel = null;

        public TranslateDragListener(TranslateAxis direction) {
            this.direction = direction;

            // Create a label to display the current drag amount. Since this
            // is not visible yet, we do not need to do it in the AWT Event
            // Thread.
            labelFrame = new JFrame();
            labelFrame.setResizable(false);
            labelFrame.setUndecorated(true);
            labelFrame.getContentPane().setLayout(new GridLayout(1, 1));
            JPanel labelPanel = new JPanel();
            labelPanel.setBackground(Color.WHITE);
            labelPanel.setOpaque(true);
            labelFrame.getContentPane().add(labelPanel);
            labelPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            labelPanel.setLayout(new GridLayout());
            positionLabel = new JLabel("0.00");
            labelPanel.add(positionLabel);
            labelPanel.invalidate();
            labelFrame.pack();
        }

        @Override
        public Class[] eventClassesToConsume() {
            return new Class[] { MouseEvent3D.class };
        }

        @Override
        public void commitEvent(Event event) {
            // Fetch and cast some event objects
            MouseEvent3D mouseEvent = (MouseEvent3D)event;
            MouseEvent awtMouseEvent = (MouseEvent)mouseEvent.getAwtEvent();

            // Figure out where the initial mouse button press happened and
            // store the initial position
            if (event instanceof MouseButtonEvent3D) {
                MouseButtonEvent3D buttonEvent = (MouseButtonEvent3D) event;
                if (buttonEvent.isPressed() &&
                        buttonEvent.getButton() == ButtonId.BUTTON1) {
                    
                    // Fetch the initial location of the mouse drag event and
                    // store away the necessary information
                    MouseEvent awtButtonEvent = (MouseEvent) buttonEvent.getAwtEvent();
                    dragStartScreen = new Point(awtButtonEvent.getX(), awtButtonEvent.getY());
                    dragStartWorld = buttonEvent.getIntersectionPointWorld();
                    
                    // Show the position label, make sure we do this in an
                    // AWT Event Thread
                    showPositionLabel(awtMouseEvent);

                    // Tell the listeners that a translation has started.
                    fireTranslationStarted();
                }
                else if (buttonEvent.isReleased() == true) {
                    // Hide the position label, make sure we do this in an
                    // AWT Event Thread
                    hidePositionLabel();
                }
                return;
            }

            // If not a drag motion, just return, we don't care about the event
            if (!(event instanceof MouseDraggedEvent3D)) {
                return;
            }

            // Get the vector of the drag motion from the initial starting
            // point in world coordinates.
            MouseDraggedEvent3D dragEvent = (MouseDraggedEvent3D) event;
            Vector3f dragVector = dragEvent.getDragVectorWorld(dragStartWorld,
                    dragStartScreen, new Vector3f());

            // Figure out how to translate based upon the axis of the affordance
            Vector3f addVector;
            float moved = 0.0f;
            switch (direction) {
                case X_AXIS: 
                    addVector = new Vector3f(dragVector.x, 0, 0);
                    moved = dragVector.x;
                    break;

                case Y_AXIS:
                    addVector = new Vector3f(0, dragVector.y, 0);
                    moved = dragVector.y;
                    break;

                case Z_AXIS:
                    addVector = new Vector3f(0, 0, dragVector.z);
                    moved = dragVector.z;
                    break;

                default:
                    addVector = new Vector3f();
                    break;
            }

            // Update the position label, make sure we do this in an AWT Event
            // Thread
            updatePositionLabel(moved, awtMouseEvent);

            // Move the cell via the moveable comopnent
            fireTranslationChanged(addVector);
        }

        /**
         * Sets the location of the frame holding the label given the current
         * mouse event, using its location.
         *
         * NOTE: This method assumes it is being called within the AWT Event
         * Thread.
         */
        private void setLabelPosition(MouseEvent mouseEvent) {
            Component component = mouseEvent.getComponent();
            Point parentPoint = new Point(component.getLocationOnScreen());
            parentPoint.translate(mouseEvent.getX() + 10, mouseEvent.getY() - 15);
            labelFrame.setLocation(parentPoint);
        }
        
        /**
         * Shows the position label, properly in an AWT Event THread
         */
       private void showPositionLabel(final MouseEvent mouseEvent) {
           SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                   setLabelPosition(mouseEvent);
                   labelFrame.toFront();
                   labelFrame.setVisible(true);
                   labelFrame.repaint();
               }
           });
       }


        /**
         * Hides the position label, properly in an AWT Event Thread
         */
        private void hidePositionLabel() {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    labelFrame.setVisible(false);
                }
            });
        }

        /**
         * Updates the position label with the amount moved, properly in an
         * AWT Event Thread.
         */
        private void updatePositionLabel(float moved, final MouseEvent mouseEvent) {
            // Set the label with the amount that we have dragged it. We display
            // the dragged amount to two decimal points
            final StringBuilder movedString = new StringBuilder();
            Formatter formatter = new Formatter(movedString);
            formatter.format("%.2f", moved);

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    positionLabel.setText(movedString.toString());
                    labelFrame.pack();
                    setLabelPosition(mouseEvent);
                }
            });
        }
    }
}
