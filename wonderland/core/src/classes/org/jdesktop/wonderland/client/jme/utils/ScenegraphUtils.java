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

package org.jdesktop.wonderland.client.jme.utils;

import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.jme.utils.traverser.ProcessNodeInterface;
import org.jdesktop.wonderland.client.jme.utils.traverser.TreeScan;
import org.jdesktop.wonderland.common.cell.CellTransform;

/**
 * Various utilties for the JME scene graph
 *
 * @author paulby
 * @author Bernard Horan
 */
public class ScenegraphUtils {

    /**
     * Print the children of the parameter node on the parameter buffer
     * @param aNode the node whose children are to be printed
     * @param buffer the buffer that will contain the tree of printed nodes
     */
    public static void printChildren(Node aNode, StringBuffer buffer) {
        printChildren(aNode, buffer, 0);
    }

    /**
     * Print the children of the parameter node on the parameter buffer, using
     * an indent count to produce an indented tree
     * @param aNode the node whose children are to be printed
     * @param buffer the buffer that will contain the tree of printed nodes
     * @param indent a count of how deep this branch is
     */
    public static void printChildren(Node aNode, StringBuffer buffer, int indent) {
        StringBuffer indentBuffer = new StringBuffer();
        for (int i = 0; i < indent ; i++) {
            indentBuffer.append('\t');
        }
        buffer.append(indentBuffer.toString());
        buffer.append(aNode.toString());
        buffer.append('\n');
        List<Spatial> children = aNode.getChildren();
        if (children == null) {
            return;
        }
        indentBuffer.append('\t');
        for (Iterator<Spatial> it = children.iterator(); it.hasNext();) {
            Spatial spatial = it.next();
            if (spatial instanceof Node) {
                printChildren((Node) spatial, buffer, indent +1);
            } else {
                buffer.append(indentBuffer.toString());
                buffer.append(spatial.toString());
                buffer.append('\n');
            }
        }
    }

    /**
     * Print the parents of the parameter spatial on the parameter buffer
     * @param aSpatial the spatial whose parents are to be printed
     * @param buffer the buffer that will contain the tree of printed spatials
     */
    public static void printParents(Spatial aSpatial, StringBuffer buffer) {
        printParents(aSpatial, buffer, 0);
    }

    /**
     * Print the parents of the parameter spatial on the parameter buffer, using
     * an indent count to indent the printing
     * @param aSpatial the spatial whose parents are to be printed
     * @param buffer the buffer that will contain the tree of printed spatials
     * @param indent a count of how deep the parent is up the tree
     */
    public static void printParents(Spatial aSpatial, StringBuffer buffer, int indent) {
         StringBuffer indentBuffer = new StringBuffer();
        for (int i = 0; i < indent ; i++) {
            indentBuffer.append('\t');
        }
        buffer.append(indentBuffer.toString());
        buffer.append(aSpatial.toString());
        buffer.append('\n');
        Node parent = aSpatial.getParent();
        if (parent != null) {
            printParents(parent, buffer, indent + 1);
        }
    }


    /**
     * Traverse the scene graph from rootNode and return the first
     * node with the given name. Returns null if no node is found
     *
     * @param rootNode the root of the graph
     * @param name the name to search for
     * @return the named node, or null
     */
    public static Spatial findNamedNode(Node rootNode, String name) {
        FindNamedNodeListener listener = new FindNamedNodeListener(name);

        TreeScan.findNode(rootNode, listener);

        return listener.getResult();
    }

    /**
     * Scan the graph and add all named nodes (non null names only) to the nodeMap.
     * If there are duplicate names a logger warning will be printed and previous
     * name/node will be overwritten
     *
     * @param rootNode root of graph
     * @param nodeMap the HashMap to which nodes are added
     */
    public static void getNamedNodes(Node rootNode, final HashMap<String, Spatial> nodeMap) {
        TreeScan.findNode(rootNode, new ProcessNodeInterface() {

            public boolean processNode(Spatial node) {
                if (node.getName()!=null) {
                    Spatial old = nodeMap.put(node.getName(), node);
                    if (old!=null)
                        Logger.getLogger(ScenegraphUtils.class.getName()).warning("Duplicate node name in scene "+node.getName());
                }
                return true;
            }
        });
    }

    /**
     * Given the world coordinates of a child compute the childs localTransform 
     * asssuming it will become a child of the parent with the supplied world transform
     * 
     * @param parent
     * @param childWorldTransform
     * @return
     */
    public static CellTransform computeChildTransform(CellTransform parentWorld, CellTransform childWorldTransform) {

        CellTransform pInv = parentWorld.clone(null).invert();

        CellTransform newChildLocal = new CellTransform();
        newChildLocal.mul(pInv, childWorldTransform);

        return newChildLocal;
    }

    private static Node createTestNode() {
        Node rootNode = new Node("Root");
        for (int i = 100; i <= 500; i +=100) {
            Node iNode = new Node(String.valueOf(i));
            rootNode.attachChild(iNode);
            for (int j = 10; j <= 50; j +=10) {
                Node jNode = new Node(String.valueOf(i+j));
                iNode.attachChild(jNode);
                for (int k = 1; k <= 5; k++) {
                    TriMesh kMesh = new TriMesh(String.valueOf(i+j+k));
                    jNode.attachChild(kMesh);
                }
            }
        }
        return rootNode;
    }

    private static void testPrintNamedNodes(Node testNode) {
        System.out.println("TEST: PRINTING NAMED NODES");
        HashMap<String, Spatial> nodeMap = new HashMap<String, Spatial>();
        getNamedNodes(testNode, nodeMap);
        Set<String> mapKeys = nodeMap.keySet();
        for (String key : mapKeys) {
            System.out.println("key: " + key + " --> " + nodeMap.get(key));
        }
    }

    private static void testNamedNode(Node testNode, String string) {
        System.out.println("TEST: FINDING NAMED NODE");
        Spatial found = findNamedNode(testNode, string);
        if (found != null) {
            System.out.println("Found spatial for " + string + " --> " + found);
        } else {
            System.err.println("Failed to find node for " + string);
        }
    }

    private static void testPrintChildren(Node testNode) {
        System.out.println("TEST: PRINT CHILDREN");
        StringBuffer buffer = new StringBuffer();
        printChildren(testNode, buffer);
        System.out.println(buffer.toString());
    }

    private static void testPrintParents(Node testNode, String string) {
        System.out.println("TEST: PRINT PARENTS");
        Spatial found = findNamedNode(testNode, string);
        if (found != null) {
            StringBuffer buffer = new StringBuffer();
            printParents(found, buffer);
            System.out.println(buffer.toString());
        } else {
            System.err.println("Failed to find node for " + string);
        }
    }

    /**
     * Given a list of cell transforms, multiply them together and return the
     * 'world' transform of the leaf transform. This mimics the transform calculation in the scene
     * graph, where the first element of the array is the graph root and the last
     * element is the leaf.
     * @param graphLocal list of cell transforms
     * @return the 'world' transform of the leaf
     */
//    public static CellTransform computeGraph(ArrayList<CellTransform> graphLocal) {
//        CellTransform result = new CellTransform();
//        for(int i=0; i<graphLocal.size(); i++) {
//            result.mul(graphLocal.get(i));
//        }
//        return result;
//    }

    static class FindNamedNodeListener implements ProcessNodeInterface {

        private String nodeName;
        private Spatial result=null;

        public FindNamedNodeListener(String name) {
            nodeName = name;
        }

        public boolean processNode(Spatial node) {
            if (node.getName().equals(nodeName)) {
                result = node;
                return false;
            }
            return true;
        }

        public Spatial getResult() {
            return result;
        }
    }

    public static void main(String[] args) {
        Node testNode = createTestNode();
        testPrintNamedNodes(testNode);
        testNamedNode(testNode, "444");
        testPrintChildren(testNode);
        testPrintParents(testNode, "444");
    }

}
