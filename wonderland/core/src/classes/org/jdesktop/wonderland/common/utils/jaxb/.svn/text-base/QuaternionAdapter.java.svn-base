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

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.jdesktop.wonderland.common.utils.jaxb.QuaternionAdapter.QuaternionHandler;

/**
 *
 * @author paulby
 */
public class QuaternionAdapter extends XmlAdapter<QuaternionHandler, Quaternion> {

    @Override
    public Quaternion unmarshal(QuaternionHandler q) throws Exception {
        if (q==null)
            return null;
        Vector3f axis = new Vector3f(q.x, q.y, q.z);
        Quaternion ret = new Quaternion();
        ret.fromAngleAxis(q.angle, axis);
        return ret;
    }

    @Override
    public QuaternionHandler marshal(Quaternion q) throws Exception {
            if (q==null) {
                return null;
            }
        return new QuaternionHandler(q);
    }

    static public class QuaternionHandler {
        @XmlElement
        private float x;
        @XmlElement
        private float y;
        @XmlElement
        private float z;
        @XmlElement
        private float angle;

        public QuaternionHandler() {
        }

        public QuaternionHandler(Quaternion q) {
            Vector3f axis=new Vector3f();
            float a = q.toAngleAxis(axis);

            x = axis.x;
            y = axis.y;
            z = axis.z;
            angle = a;
        }
    }
}
