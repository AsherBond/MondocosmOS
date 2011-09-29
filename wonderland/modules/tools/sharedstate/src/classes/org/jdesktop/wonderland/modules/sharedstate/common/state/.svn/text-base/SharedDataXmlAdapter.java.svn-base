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
package org.jdesktop.wonderland.modules.sharedstate.common.state;

import org.jdesktop.wonderland.modules.sharedstate.common.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author jkaplan
 */
class SharedDataXmlAdapter extends XmlAdapter<SharedXML, SharedData> {
    @Override
    public SharedData unmarshal(SharedXML xml) throws Exception {
        if (xml.getType().equals("integer")) {
            return SharedInteger.valueOf(xml.getValue());
        } else if (xml.getType().equals("string")) {
            return SharedString.valueOf(xml.getValue());
        } else if (xml.getType().equals("boolean")) {
            return SharedBoolean.valueOf(xml.getValue());
        } else {
            return null;
        }
    }

    @Override
    public SharedXML marshal(SharedData v) throws Exception {
        if (v instanceof SharedInteger) {
            return new SharedXML("integer", v.toString());
        } else if (v instanceof SharedString) {
            return new SharedXML("string", v.toString());
        } else if (v instanceof SharedBoolean) {
            return new SharedXML("boolean", v.toString());
        } else {
            return null;
        }
    }
}
