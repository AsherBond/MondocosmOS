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
package org.jdesktop.wonderland.modules.appbase.client.view;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import java.awt.Point;
import java.awt.Dimension;
import org.jdesktop.mtgame.EntityComponent;
import org.jdesktop.wonderland.client.input.EventListener;
import org.jdesktop.wonderland.client.jme.input.MouseEvent3D;
import org.jdesktop.wonderland.modules.appbase.client.Window2D;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * A generic view class. A view is an object which can display the contents
 * of a given window in some sort of display environment. The specific display 
 * environment is determined by a developer-implemented subclass of <code>View2DDisplayer</code>. 
 * Often a displayer-specific subclass of <code>View2D</code> will also need to be implemented.
 * <br><br>
 * A view has a set of attributes (such as parent, type, and visibility) which are 
 * determined by the window displayed in the view. Generally, the window-level
 * code transfers attributes like these into the view using the view set methods.
 * For best performance in transferring a batch of window operations into the view,
 * use an argument of update=false in the view set methods and then call view.update().
 *
 * @author deronj
 */

@ExperimentalAPI
public interface View2D {

    /** The type of the view. */
    public enum Type { UNKNOWN, PRIMARY, SECONDARY, POPUP };

    /** A view can be moved above or below another in the stack. */
    public enum RestackOp { ABOVE, BELOW };

    /** Clean up resources. */
    public void cleanup ();

    /** Returns the name of the view. */
    public String getName ();

    /** Returns the displayer in which this view is displayed. */
    public View2DDisplayer getDisplayer ();

    /** Returns the window which the view displays. */
    public Window2D getWindow ();

    /** Specify the type of the view. Update afterward. */
    public void setType (Type type);

    /** Specify the type of the view. Update if specified. */
    public void setType (Type type, boolean update);

    /** Returns the type of the view. */
    public Type getType ();

    /** Set the parent view of this view. Update afterward. */
    public void setParent (View2D parent);

    /** Set the parent view of this view. Update if specified. */
    public void setParent (View2D parent, boolean update);

    /** Returns the parent of this view. */
    public View2D getParent ();

    /** Set whether the app wants the view to be visible. Update afterward. */
    public void setVisibleApp (boolean visible);

    /** Set whether the app wants the view to be visible. Update if specified. */
    public void setVisibleApp (boolean visible, boolean update);

    /** Return whether the app wants the to be visible. */
    public boolean isVisibleApp ();

    /** Set whether the user wants the view to be visible. Update afterward. */
    public void setVisibleUser (boolean visible);

    /** Set whether the user wants the view to be visible. Update if specified. */
    public void setVisibleUser (boolean visible, boolean update);

    /** Return whether the user wants the to be visible. */
    public boolean isVisibleUser ();

    /** 
     * Returns whether the view is actually visible. To be actually visible, a view needs to 
     * have both visibleApp and visibleUser set to true, and all ancestor views need to be 
     * actually visible.
     */
    public boolean isActuallyVisible ();

    /** Specify whether the view should be decorated by a frame. Update afterward. */
    public void setDecorated (boolean decorated);

    /** Specify whether the view should be decorated by a frame. Update if specified. */
    public void setDecorated (boolean decorated, boolean update);

    /** Return whether the view should be decorated by a frame. */
    public boolean isDecorated ();

    /** Specify the frame title (used only when the view is decorated). Update afterward. */
    public void setTitle (String title);

    /** Specify the frame title (used only when the view is decorated). Update if specified. */
    public void setTitle (String title, boolean update);

    /** Returns the frame title. */
    public String getTitle ();

    /** Specify whether the view's frame resize corner is enabled.. Update afterward. */
    public void setUserResizable (boolean userResizable);

    /** Specify whether the view's frame resize corner is enabled. Update if specified. */
    public void setUserResizable (boolean userResizable, boolean update);

    /** Return whether the view's frame resize corner is enabled. The default is false. */
    public boolean isUserResizable ();

    /** 
     * Specify the portion of the window which is displayed by this view. 
     * Update afterward.
       TODO: notyet: public void setWindowAperture (Rectangle aperture);
     */

    /** 
     * Specify the portion of the window which is displayed by this view.
     * Update if specified.
       TODO: notyet: public void setWindowAperture (Rectangle aperture, boolean update);
     */

    /** 
     * Return the portion of the window which is displayed by this view.
       TODO: notyet: public Rectangle getWindowAperture ();
     */

    /** 
     * Specify a geometry node. If <code>geometryNode</code> is null the default geometry node is used.
     * Update afterward.
     */
    public void setGeometryNode (GeometryNode geometryNode);

    /** 
     * Specify a geometry node. If <code>geometryNode</code> is null the default geometry node is used.
     * Update if specified.
     */
    public void setGeometryNode (GeometryNode geometryNode, boolean update);

    /** Returns the geometry node used by this view. */
    public GeometryNode getGeometryNode ();

    /** A size change which comes from the app. Update afterward. */
    public void setSizeApp (Dimension size);

    /** A size change which comes from the app. Update if specified. */
    public void setSizeApp (Dimension size, boolean update);

    /** Returns the app-specified size (in pixels). */
    public Dimension getSizeApp ();

    /** A size change which comes from the user. */
    // TODO: notyet: public void setSizeUser(int width, int height);
    /* TODO: user size getters */

    /** Returns the current width of the view in the local coordinate system of the displayer. */
    public float getDisplayerLocalWidth ();

    /** Returns the current height of the view in the local coordinate system of the displayer. */
    public float getDisplayerLocalHeight ();

    /** 
     * A window close which comes from the user. Close the window of this view.
     */
    public void windowCloseUser ();

    /**  
     * Tells the view that the window's stack position may have changed.
     */
    public void stackChanged (boolean update);

    /** Moves the window of this view to the top of its app's window stack. */
    public void windowRestackToTop ();

    /** Moves the window of this view to the bottom of its app's window stack. */
    public void windowRestackToBottom ();

    /**  
     * Moves this window so that it is above the window of the given sibling view in the app's window stack.
     * If sibling is null, the window is moved to the top of the stack.
     */
    public void windowRestackAbove (View2D sibling);

    /**  
     * Moves this window so that it is below the window of the given sibling view in the app's window stack.
     * If sibling is null, the window is moved to the bottom of the stack.
     */
    public void windowRestackBelow (View2D sibling);

    /** 
     * Specify the size of the displayed pixels of this view.
     * Update afterward. This defaults to the initial pixel scale of the window.
     */
    public void setPixelScale (Vector2f pixelScale);

    /** 
     * Specify the size of the displayed pixels of this view.
     * Update if specified. Defaults to the initial pixel scale of the window.
     */
    public void setPixelScale (Vector2f pixelScale, boolean update);

    /** Return the pixel scale of this view. */
    public Vector2f getPixelScale ();

    /** 
     * Specify the first part the view's offset translation in local coordinates from the center of the 
     * parent to the center of this view. Update immediately. Note: setPixelOffset is the other part of 
     * the offset translation. The two offsets are added to produce the effective offset.
     *
     * NOTE: this part of the offset also applies to primary views. In this case the offset is from
     * the center of the cell to the center of the view.
     *
     * NOTE: this part of the offset only applies to views which are in the world.
     */
    public void setOffset(Vector2f offset);

    /** 
     * Specify the first part view's offset translation in local coordinates from the center of the parent 
     * to the center of this view. Update if specified. Note: setPixelOffset is the other part of the 
     * offset translation. The two offsets are added to produce the effective offset.
     *
     * NOTE: this part of the offset also applies to primary views. In this case the offset is from
     * the center of the cell to the center of the view.
     *
     * NOTE: this part of the offset only applies to views which are in the world.
     */
    public void setOffset(Vector2f offset, boolean update);

    /** Returns the offset translation in local coordinates. */
    public Vector2f getOffset ();

    /** 
     * Specify the second part of view's offset translation as a pixel offset from the top left corner of 
     * the parent to the top left corner of the view. Update immediately. Uses the view's pixel current 
     * scale to convert this pixel offset into local coordinates. Note: setOffset is the other part of the 
     * offset translation. The two offsets are added to produce the effective offset.
     *
     * NOTE: the pixel offset is ignored by primary views.
     */
    public void setPixelOffset(Point pixelOffset);

    /** 
     * Specify the second part of view's offset translation as a pixel offset from the top left corner of 
     * the parent to the top left corner of the view. Update if specified. Uses the view's pixel current 
     * scale to convert this pixel offset into local coordinates. Note: setOffset is the other part of the 
     * offset translation. The two offsets are added to produce the effective offset.
     *
     * NOTE: the pixel offset is ignored by primary views.
     */
    public void setPixelOffset(Point pixelOffset, boolean update);

    /** Returns the offset in terms of pixels. */
    public Point getPixelOffset ();

    /** 
     * Apply the given translation vector as a delta to the current user transform of the view. 
     * Update immediately. Note: ortho and non-ortho modes have separate user transforms.
     * The value of the ortho attribute determines which one is current.
     */
    public void applyDeltaTranslationUser (Vector3f deltaTranslation);

    /** 
     * Apply the given translation vector as a delta to the current user transform of the view. 
     * Update if specified. Note: ortho and non-ortho modes have separate user transforms.
     * The value of the ortho attribute determines which one is current.
     */
    public void applyDeltaTranslationUser (Vector3f deltaTranslation, boolean update);

    /** 
     * Apply all pending updates to the view. 
     * <br><br>
     * NOTE: you must always follow a call to this method with a call to the <code>updateFrame</code> method.
     */
    public void update ();

    /** 
     * Apply all pending updates to the frame of the view. Must be called after the update method.
     * In addition, it must always be called <b>outside</b> the window lock.
     */
    public void updateFrame ();

    /** 
     * Converts the given 3D mouse event into a 2D event and forwards it along to the view's controlArb.
     * NOTE: on the slave, this must be called on the EDT.
     *
     * @param window The window this view displays.
     * @param me3d The 3D mouse event to deliver.
     */
    public void deliverEvent(Window2D window, MouseEvent3D me3d);

    /**
     * Transform the given 3D point in local coordinates into the corresponding point
     * in the pixel space of the view's image. The given point must be in the plane of the view.
     * @param point The point to transform.
     * @param clamp If true return the last position if the argument point is null or the resulting
     * position is outside of the geometry's rectangle. Otherwise, return null if these conditions hold.
     * @return the 2D position of the pixel space the view's image.
     */
    public Point calcPositionInPixelCoordinates(Vector3f point, boolean clamp);

    /**
     * Given a point in the pixel space of the Wonderland canvas calculates 
     * the texel coordinates of the point on the geometry where a
     * ray starting from the current eye position intersects the geometry.
     */
    public Point calcIntersectionPixelOfEyeRay(int x, int y);

    /**
     * Add an event listener to this view.
     * @param listener The listener to add.
     */
    public void addEventListener(EventListener listener);

    /**
     * Remove an event listener from this view.
     * @param listener The listener to remove.
     */
    public void removeEventListener(EventListener listener);

    /**
     * Add an entity component to this view.
     */
    public void addEntityComponent(Class clazz, EntityComponent comp);

    /**
     * Remove an entity component from this view.
     */
    public void removeEntityComponent(Class clazz);
}