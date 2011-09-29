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
package org.jdesktop.wonderland.testharness.master;

import com.jme.math.Vector3f;
import java.awt.Point;
import java.util.EnumSet;
import java.util.Properties;
import java.util.Set;
import org.jdesktop.wonderland.testharness.manager.common.SimpleTestDirectorMessage.UserActionType;

/**
 *
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public class CityUserManagerImpl implements UserManager {
    private static final String BLOCKS_PROP = "blocks";
    private static final String BLOCKS_DEFAULT = "10";
    private int blocks;

    private static final String AVENUES_PROP = "avenues";
    private static final String AVENUES_DEFAULT = "5";
    private int avenues;

    private static final String BLOCK_SIZE_PROP = "blockSize";
    private static final String BLOCK_SIZE_DEFAULT = "91.4";
    private float blockSize;
    
    private static final String AVENUE_SIZE_PROP = "avenueSize";
    private static final String AVENUE_SIZE_DEFAULT = "304.8";
    private float avenueSize;

    private static final int NUM_MOVES = 50;
    private static final double RETURN_FREQ = .05;

    // broken!!
    private static final double NO_MOVE_FREQ = 0;

    
    private enum Direction {
        N, S, E, W;

        Point move(Point p) {
            switch (this) {
                case N:
                    return new Point(p.x, p.y + 1);
                case S:
                    return new Point(p.x, p.y - 1);
                case E:
                    return new Point(p.x + 1, p.y);
                case W:
                    return new Point(p.x - 1, p.y);
                default:
                    throw new IllegalStateException("Unknown direction " + this);
            }
        }
    };

    public void initialize(Properties props) {
        blocks = Integer.parseInt(props.getProperty(BLOCKS_PROP, BLOCKS_DEFAULT));
        avenues = Integer.parseInt(props.getProperty(AVENUES_PROP, AVENUES_DEFAULT));
        blockSize = Float.parseFloat(props.getProperty(BLOCK_SIZE_PROP, BLOCK_SIZE_DEFAULT));
        avenueSize = Float.parseFloat(props.getProperty(AVENUE_SIZE_PROP, AVENUE_SIZE_DEFAULT));
  
        System.out.println("[CityUserManager] Initialized.  Blocks: " +
                           blocks + " avenues: " + avenues);

    }

    public User createUser(String username, UserContext context) {
        User user = new User(username, context);
        user.setSpeed(15f);
        user.doWalk(createWalkPattern());
        
        return user;
    }

    public void destroyUser(User user) {
    }
    
    public void changeUserAction(User user, UserActionType userActionType) {
        switch (userActionType) {
            case WALK:
                user.doWalk(createWalkPattern());
                break;
            case IDLE:
                user.doWalk(null);
        }
    }

    protected Vector3f[] createWalkPattern() {
        Point cur = new Point((int) Math.ceil(avenues / 2f),
                              (int) Math.ceil(blocks / 2f));
        Point prev = cur;
        Point[] points = new Point[NUM_MOVES * 2];

        // first point is always the center
        points[0] = cur;
        
        // populate half the points with random values
        for (int i = 1; i < NUM_MOVES; i++) {
            points[i] = nextPoint(prev, cur);
            prev = cur;
            cur = points[i];
        }

        // now walk back to the start
        for (int i = 0; i < NUM_MOVES; i++) {
            points[NUM_MOVES + i] = points[NUM_MOVES - (i + 1)];
        }

        // for (Point p : points) {
        //    System.out.println(p.toString() + " -> " + toVector(p));
        // }

        return toVector(points);
    }

    protected Point nextPoint(Point prev, Point cur) {
        Direction[] valid = getValidDirections(cur);
        double[] weights = getDirectionWeights(prev, cur, valid);

        Direction dir = null;
        double rand = Math.random();
        for (int i = 0; i < valid.length; i++) {
            if (rand < weights[i]) {
                dir = valid[i];
                break;
            }
        }

        if (dir == null) {
            return cur;
        }

        return dir.move(cur);
    }

    private double[] getDirectionWeights(Point prev, Point cur,
                                         Direction[] valid)
    {
        double[] weights = new double[valid.length];

        double dirWeight = (1.0 - NO_MOVE_FREQ - RETURN_FREQ) / (valid.length - 1);
        double curWeight = 0;

        for (int i = 0; i < weights.length; i++) {
            if (valid[i].move(cur).equals(prev)) {
                curWeight += RETURN_FREQ;
            } else {
                curWeight += dirWeight;
            }

            weights[i] = curWeight;
        }

        return weights;
    }

    private Direction[] getValidDirections(Point cur) {
        Set<Direction> out = EnumSet.noneOf(Direction.class);

        if (cur.x > 0) {
            out.add(Direction.W);
        }
        if (cur.x < avenues) {
            out.add(Direction.E);
        }
        if (cur.y > 0) {
            out.add(Direction.S);
        }
        if (cur.y < blocks) {
            out.add(Direction.N);
        }

        return out.toArray(new Direction[0]);
    }

    protected Vector3f[] toVector(Point[] points) {
        Vector3f[] out = new Vector3f[points.length];
        for (int i = 0; i < points.length; i++) {
            out[i] = toVector(points[i]);
        }

        return out;
    }

    protected Vector3f toVector(Point point) {
        return new Vector3f(getX(point.x, point.y), 0f,
                                  getZ(point.x, point.y));
    }

    public float getX(int avenue, int street) {
        System.out.println("ave: " + avenue + " size: " + avenueSize +
                           " aves: " + avenues);

        return (avenueSize * (avenue - (avenues / 2f))) - (avenueSize / 2f);
    }

    public float getZ(int avenue, int street) {
        return (blockSize * (street - (blocks / 2f))) - (blockSize / 2f);
    }
}
