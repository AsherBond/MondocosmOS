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

import com.jme.image.Texture2D;
import com.jme.math.Matrix4f;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.math.Vector2f;
import com.jme.scene.Node;
import java.awt.Point;
import com.jme.scene.state.TextureState;
import java.awt.Dimension;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.jme.input.InputManager3D;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.modules.appbase.client.Window2D;
import com.jme.image.Texture;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.client.jme.JmeClientMain;

/**
 * If you want to customize the geometry of a displayer, implement
 * this interface and pass an instance to displayer.setGeometryNode.
 *
 * @author deronj
 */
@ExperimentalAPI
public abstract class GeometryNode extends Node {

    private static final Logger logger = Logger.getLogger(GeometryNode.class.getName());

    /** The displayer for which the geometry object was created. */
    private View2D view;

    /** The texture to be displayed. */
    private Texture2D texture;

    /** Used by calcPositionInPixelCoordinates. */
    private Point lastPosition = null;

    /**
     * Create a new instance of ViewGeometryObject. Attach your geometry as
     * children of this Node.
     * @param displayer The displayer the geometry is to display.
     */
    public GeometryNode (View2D  view) {
        super("GeometryNode for " + view.getName());
        this.view = view;
    }

    /**
     * Clean up resources. Any resources you allocate for the
     * object should be detached in here. Must be called in a render loop safe way.
     *
     * NOTE: It is normal for one ViewObjectGeometry to get stuck in
     * the Java heap (because it is reference by Wonderland picker last
     * picked node). So make sure there aren't large objects, such as a
     * texture, attached to it.
     */
    public void cleanup() {
        view = null;
    }

    /**
     * Specify the size of the geometry node. In your subclass method you should make the 
     * corresponding change to your geometry. This must be called in a render loop safe way.
     * (i.e. from inside a render updater or commit method).
     */
    public abstract void setSize (float width, float height);

    /**
     * Specify the texture to be displayed. In your subclass method you should make your geometry display
     * this texture. Must be called in a render loop safe way.
     */
    public void setTexture(Texture2D texture) {
        this.texture = texture;
    }

    /**
     * Specify the texture coordinates to be used. You should use widthRatio and heightRatio to update
     * the texture coordinates of your geometry. Must be called in a render loop safe way.
     * @param widthRatio The ratio of the displayer width to the rounded up size of the texture width.
     * @param heightRatio The ratio of the displayer height to the rounded up size of the texture height.
     */
    public abstract void setTexCoords(float widthRatio, float heightRatio);

    /**
     * Specifies the transform of the geometry node. 
     * Must be called in a render loop safe way.
     */
    public void setTransform (CellTransform transform) {
        setLocalRotation(transform.getRotation(null));
        setLocalTranslation(transform.getTranslation(null));
    }

    /**
     * Returns the actual (rounded up) size of the texture.
     */
    public Dimension getTextureSize () {
        if (texture != null) {
            return new Dimension(texture.getImage().getWidth(),
                                 texture.getImage().getHeight());
        } else {
            return null;
        }
    }

    /**
     * Transform the given 3D point in world coordinates into the corresponding point
     * in the texel space of the geometry. The given point must be in the plane of the window
     * but it doesn't necessarily need to be within the rectangle of the geometry.
     * <br><br>
     * Note: works when called with both a vector or a point.
     * @param point The point to transform.
     * @param clamp If true return the last position if the argument point is null or the resulting
     * position is outside of the geometry's rectangle. Otherwise, return null if these conditions hold.
     * @return the 2D position of the pixel space the window's image.
     */
    public Point calcPositionInPixelCoordinates(Vector3f point, boolean clamp) {
        if (point == null) {
            if (clamp) {
                return lastPosition;
            } else {
                lastPosition = null;
                return null;
            }
        }
        logger.fine("world point = " + point);

        // The first thing we do is transform the point into local coords
        // TODO: perf: avoid doing a matrix invert every time
        Matrix4f local2World = getLocalToWorldMatrix(null);
        Matrix4f world2Local = local2World.invert();
        Vector3f pointLocal = world2Local.mult(point, new Vector3f()); 
        logger.fine("local point = " + pointLocal);

        // First calculate the actual coordinates of the top left corner of the view in local coords.
        float width = view.getDisplayerLocalWidth();
        float height = view.getDisplayerLocalHeight();
        Vector3f topLeftLocal = new Vector3f(-width / 2f, height / 2f, 0f);
        logger.fine("topLeftLocal = " + topLeftLocal);

        // Now calculate the x and y coords relative to the view
        float x = pointLocal.x - topLeftLocal.x;
        float y = (topLeftLocal.y - pointLocal.y);
        logger.fine("x = " + x);
        logger.fine("y = " + y);

        x /= width;
        y /= height;
        logger.fine("x pct = " + x);
        logger.fine("y pct = " + y);

        // Assumes window is never scaled
        if (clamp) {
            if (x < 0 || x >= 1 || y < 0 || y >= 1) {
                logger.fine("Outside!");
                return lastPosition;
            }
        }

        // TODO
        Window2D window = view.getWindow();
        int winWidth = window.getWidth();
        int winHeight = window.getHeight();
        logger.fine("winWidth = " + winWidth);
        logger.fine("winHeight = " + winHeight);

        logger.fine("Final xy " + (int) (x * winWidth) + ", " + (int) (y * winHeight));
        lastPosition = new Point((int) (x * winWidth), (int) (y * winHeight));
        return lastPosition;
    }

    /**
     * Returns the texture displayed by this geometry.
     */
    public abstract Texture getTexture();

    /**
     * Returns the texture state.
     */
    public abstract TextureState getTextureState();

    /** 
     * Set the ortho Z order (used only when the geometry's render component is in ortho mode).
     * Must be called in a render loop safe way.
     */
    public abstract void setOrthoZOrder (int zOrder);

    /** Returns the Z order. */
    public abstract int getOrthoZOrder();

    /**
     * Given a point in the pixel space of the Wonderland canvas calculates
     * the texel coordinates of the point on the geometry where a
     * ray starting from the current eye position intersects the geometry.
     * This method is used only for drag events that start on an view which is in the 
     * world (that is, it is not in the ortho plane).
     *
     * Note on subclassing:
     *
     * If the geometry is nonplanar it is recommended that the subclass
     * implement this method by performing a pick. If this pick misses the
     * subclass will need to decide how to handle the miss. One possible way to
     * handle this is to assume that there is a planar "halo" surrounding the
     * the window with which the ray can be intersected.
     */
    public Point calcIntersectionPixelOfEyeRay(int x, int y) {

        // Calculate the ray
        Ray rayWorld = InputManager3D.getInputManager().pickRayWorld(x, y);

        // Calculate an arbitrary point on the plane (in this case, the top left corner)
        float width = view.getDisplayerLocalWidth();
        float height = view.getDisplayerLocalHeight();
        Vector3f topLeftLocal = new Vector3f(-width / 2f, height / 2f, 0f);
        Matrix4f local2World = getLocalToWorldMatrix(null);
        Vector3f topLeftWorld = local2World.mult(topLeftLocal, new Vector3f());

        // Calculate the plane normal
        Vector3f planeNormalWorld = getPlaneNormalWorld();
        
        // Now find the intersection of the ray with the plane
        Vector3f intPointWorld = calcPlanarIntersection(rayWorld, topLeftWorld, planeNormalWorld);
        if (intPointWorld == null) {
            return null;
        }
        logger.fine("intPointWorld = " + intPointWorld);

        // TODO: opt: we can optimize the following by reusing some of the intermediate
        // results from the previous steps
        Point pt = calcPositionInPixelCoordinates(intPointWorld, false);
        logger.fine("pixel position = " + pt);
        return pt;
    }

    /**
     * Returns the plane normal of the window in world coordinates.
     */
    protected Vector3f getPlaneNormalWorld() {
        // Find two vectors on the plane and take the cross product and then normalize

        float width = view.getDisplayerLocalWidth();
        float height = view.getDisplayerLocalHeight();
        Vector3f topLeftLocal = new Vector3f(-width / 2f, height / 2f, 0f);
        Vector3f topRightLocal = new Vector3f(width / 2f, height / 2f, 0f);
        Vector3f bottomLeftLocal = new Vector3f(-width / 2f, -height / 2f, 0f);
        Vector3f bottomRightLocal = new Vector3f(width / 2f, -height / 2f, 0f);
        logger.fine("topLeftLocal = " + topLeftLocal);
        logger.fine("topRightLocal = " + topRightLocal);
        logger.fine("bottomLeftLocal = " + bottomLeftLocal);
        logger.fine("bottomRightLocal = " + bottomRightLocal);
        Matrix4f local2World = getLocalToWorldMatrix(null); // TODO: prealloc
        Vector3f topLeftWorld = local2World.mult(topLeftLocal, new Vector3f()); // TODO:prealloc
        Vector3f topRightWorld = local2World.mult(topRightLocal, new Vector3f()); // TODO:prealloc
        Vector3f bottomLeftWorld = local2World.mult(bottomLeftLocal, new Vector3f()); // TODO:prealloc
        Vector3f bottomRightWorld = local2World.mult(bottomRightLocal, new Vector3f()); // TODO:prealloc
        logger.fine("topLeftWorld = " + topLeftWorld);
        logger.fine("topRightWorld = " + topRightWorld);
        logger.fine("bottomLeftWorld = " + bottomLeftWorld);
        logger.fine("bottomRightWorld = " + bottomRightWorld);

        Vector3f leftVec = bottomLeftWorld.subtract(topLeftWorld);
        Vector3f topVec = topRightWorld.subtract(topLeftWorld);
        return leftVec.cross(topVec).normalize();
    }

    /**
     * Calculates the point in world coordinates where the given ray intersects the "world plane" 
     * of this geometry. Returns null if the ray doesn't intersect the plane.
     * <br><br>
     * All inputs are in world coordinates.     
     * <br><br>
     * @param ray The ray.
     * @param planePoint A point on the plane.
     * @param planeNormal The plane normal vector.
     * @return The intersection point.
     */
    protected Vector3f calcPlanarIntersection(Ray ray, Vector3f planePoint, Vector3f planeNormal) {

        // Ray Equation is X = P + t * V
        // Plane Equation is (X - P0) dot N = 0
        //
        // where
        //     X is a point on the ray 
        //     P = Starting point for ray (ray.getOrigin())
        //     t = distance along ray to intersection point
        //     V = Direction vector of Ray (ray.getDirection())
        //     P0 = known point on plane (planePoint)
        //     N  = Normal for plane (planeNormal)
        //
        // Combine equations to calculate t:
        //
        // t = [ (P0 - P) dot N ] / (V dot N)
        //
        // Then substitute t into the Ray Equation to get the intersection point.
        //
        // Source: Various: Lars Bishop book, Geometry Toolbox, Doug T.

        Vector3f pointDiffVec = new Vector3f(planePoint);
        pointDiffVec.subtractLocal(ray.getOrigin());
        float numerator = planeNormal.dot(pointDiffVec);
        float denominator = planeNormal.dot(ray.getDirection());
        if (denominator == 0f) {
            // No intersection
            return null;
        }
        float t = numerator / denominator;

        // Now plug t into the Ray Equation is X = P + t * V
        Vector3f x = ray.getDirection().mult(t).add(ray.getOrigin());
        return x;
    }

    /**
     * Given a point in the pixel space of the Wonderland canvas calculates
     * the texel coordinates of the point on the geometry where a
     * ray starting from the current eye position intersects the geometry.
     * This method is used only for drag events that start on an ortho view.
     *
     * Note on subclassing:
     *
     * If the geometry is nonplanar it is recommended that the subclass
     * implement this method by performing a pick. If this pick misses the
     * subclass will need to decide how to handle the miss. One possible way to
     * handle this is to assume that there is a planar "halo" surrounding the
     * the window with which the ray can be intersected.
     */
    public Point calcIntersectionPixelOfEyeRayOrtho(int x, int y) {
        View2DEntity v2e = (View2DEntity) view;

        // Calculate an arbitrary point on the plane (in this case, the top left corner)
        int width = (int) v2e.getDisplayerLocalWidth();
        int height = (int) v2e.getDisplayerLocalHeight();

        Vector2f locOrtho = calcLocationOrtho();

        // Compute top left in canvas coords
        int canvasHeight = JmeClientMain.getFrame().getCanvas().getHeight();
        Point topLeft = new Point();
        topLeft.x = (int)locOrtho.x - width/2;
        topLeft.y = canvasHeight - (int)locOrtho.y - height/2;
        Point bottomRight = new Point(topLeft.x + width - 1, topLeft.y + height - 1);

        // Convert to view local coords
        float xLocal = x - topLeft.x;
        float yLocal = y - topLeft.y;

        // Convert to pixel coords
        Vector2f pixelScale = v2e.getPixelScaleOrtho();
        int xPixel = (int)(xLocal / pixelScale.x);
        int yPixel = (int)(yLocal / pixelScale.y);

        Point pt = new Point(xPixel, yPixel);
        logger.fine("pixel position = " + pt);
        return pt;
    }

    /**
     * Calculate the center position of this view in the ortho coordinate system.
     */
    private Vector2f calcLocationOrtho () {

        if (view.getType() == View2D.Type.PRIMARY ||
            view.getType() == View2D.Type.UNKNOWN) {
            return ((View2DEntity)view).getLocationOrtho();
        }

        // Sum pixel offsets up to the ultimate parent
        Point pixelOffsetTotal = new Point();
        View2D parent = view.getParent();
        while (parent != null) {
            Point pixelOffset = view.getPixelOffset();
            pixelOffsetTotal.x += pixelOffset.x;
            pixelOffsetTotal.y += pixelOffset.y;
            view = parent;
            parent = view.getParent();
        }

        // Convert pixel offset to an offset in local coords
        View2DEntity v2e = (View2DEntity) view;
        Vector2f pixelScale = v2e.getPixelScaleOrtho();
        Vector2f locationOrtho = new Vector2f((float) pixelOffsetTotal.x, (float) pixelOffsetTotal.y);
        locationOrtho.x *= pixelScale.x;
        locationOrtho.y *= pixelScale.y;

        // Now add in the location of the ultimate parent in ortho coordinates.
        locationOrtho = locationOrtho.add(v2e.getLocationOrtho());

        return locationOrtho;
    }
}

