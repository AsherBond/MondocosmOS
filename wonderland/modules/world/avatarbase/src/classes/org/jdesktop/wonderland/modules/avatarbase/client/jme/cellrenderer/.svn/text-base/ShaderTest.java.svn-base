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
package org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer;

import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.scene.state.RenderState.StateType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GLException;
import org.jdesktop.mtgame.RenderManager;
import org.jdesktop.mtgame.processor.WorkProcessor.WorkCommit;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.SceneWorker;

/**
 * Test if certain shaders will work on this card.
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public class ShaderTest {
    private static final Logger logger =
            Logger.getLogger(ShaderTest.class.getName());


    private boolean tested = false;
    private boolean result = false;

    public static ShaderTest getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * No public constructor.  Use getInstance() instead.
     */
    protected ShaderTest() {
    }


    public synchronized boolean testShaders() {
        if (!tested) {
            result = doTest();
            tested = true;
        }

        return result;
    }

    /**
     * Check whether the graphics card will actually support the types of
     * shaders used by avatars.
     */
    private boolean doTest() {
        boolean out = true;

        // make sure we can install shaders
        out &= tryShader("default_vertex.glsl", "default_fragment.glsl", null);

        // make sure a sample avatar shader compiles
        out &= tryShader("avatar_vertex.glsl", "avatar_fragment.glsl", null);

        // try binding an array of 55 matrices (as used by the avatars)
        out &= tryShader("pose_vertex.glsl", "default_fragment.glsl",
                new ShaderVariableBinder()
        {
            public void bind(GLSLShaderObjectsState state) {
                float[] vals = new float[55 * 16];
                state.setUniformMatrix4Array("pose", vals, false);
            }
        });

        return out;
    }

    /**
     * Try a particular combination of shaders and variables.  Return
     * true if the shader can be applied with no exceptions, or false if
     * there are exceptions.
     * @param vertexFile the resource for the vertex shader file
     * @param fragmentFile the resource for the fragment shader file
     * @param binder a helper to bind any variables needed by the shader
     * @return true if the shader can be applied with no exceptions, or false if
     * there are exceptions
     */
    private boolean tryShader(final String vertexFile,
                              final String fragmentFile,
                              final ShaderVariableBinder binder)
    {
        // the sceneworker will set this value
        final MutableBoolean out = new MutableBoolean();
        final Semaphore done = new Semaphore(0);

        final String vertex = readShader(vertexFile);
         final String fragment = readShader(fragmentFile);

        SceneWorker.addWorker(new WorkCommit() {
            public void commit() {
                try {
                    RenderManager rm = ClientContextJME.getWorldManager().getRenderManager();
                    GLSLShaderObjectsState shaderState = (GLSLShaderObjectsState)rm.createRendererState(StateType.GLSLShaderObjects);
                    shaderState.setEnabled(true);
                    shaderState.load(vertex, fragment);

                    if (binder != null) {
                        binder.bind(shaderState);
                    }

                    shaderState.apply();
                } catch (GLException ex) {
                    logger.log(Level.WARNING, "Unable to load avatar sample " +
                               "shader. High quality avatars are not available.",
                               ex);
                    out.value = false;
                } finally {
                    done.release();
                }
            }
        });

        try {
            done.acquire();
            return out.value;
        } catch (InterruptedException ie) {
            return false;
        }
    }

    private String readShader(String fileName) {
        if (fileName == null) {
            return null;
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(
                    getClass().getResourceAsStream("resources/" + fileName)));
        StringBuffer out = new StringBuffer();

        try {
            String line;
            while ((line = in.readLine()) != null) {
                out.append(line);
                out.append("\n");
            }
        } catch (IOException ioe) {
            logger.log(Level.WARNING, "Error reading " + fileName, ioe);
            return null;
        }

        return out.toString();
    }

    private static class MutableBoolean {
        volatile boolean value = true;
    }

    private interface ShaderVariableBinder {
        public void bind(GLSLShaderObjectsState state);
    }

    public static final class SingletonHolder {
        private static final ShaderTest INSTANCE = new ShaderTest();
    }

}