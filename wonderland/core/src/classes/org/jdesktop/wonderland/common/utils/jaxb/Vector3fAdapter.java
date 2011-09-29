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
package org.jdesktop.wonderland.common.utils.jaxb;

import com.jme.math.Vector3f;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.jdesktop.wonderland.common.utils.jaxb.Vector3fAdapter.Vector3fHandler;

/**
 *
 * @author paulby
 */
public class Vector3fAdapter extends XmlAdapter<Vector3fHandler, Vector3f> {


    @Override
    public Vector3f unmarshal(Vector3fHandler v) throws Exception {
        if (v==null)
            return null;
        return new Vector3f(v.x, v.y, v.z);
    }

    @Override
    public Vector3fHandler marshal(Vector3f v) throws Exception {
        if (v==null) {
            return null;
        }
        return new Vector3fHandler(v);
    }

    static public class Vector3fHandler {
        @XmlElement
        private float x;
        @XmlElement
        private float y;
        @XmlElement
        private float z;

        public Vector3fHandler() {
        }

        public Vector3fHandler(Vector3f v) {
            x = v.x;
            y = v.y;
            z = v.z;
        }
    }
}
