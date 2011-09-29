/**
 * Open Wonderland
 *
 * Copyright (c) 2011, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */
package org.jdesktop.wonderland.modules.sharedstate.common;

import javax.xml.bind.annotation.XmlRootElement;
import org.jdesktop.wonderland.common.cell.state.annotation.ServerState;

/**
 * Shared data representation of a double
 * @author jkaplan
 * @author jagwire
 */
@ServerState
@XmlRootElement(name="shared-double")
public class SharedDouble extends SharedData {
    private static final long serialVersionUID = 1L;
    private double value;

    /**
     * No arg constructor needed by jaxb.  Use valueOf() when constructing
     * this class directly.
     */
    public SharedDouble() {
    }

    /**
     * Use valueOf() instead of this
     * @param value the value
     */
    private SharedDouble(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SharedDouble other = (SharedDouble) obj;
        if (this.value != other.value) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.value) ^ 
                                 (Double.doubleToLongBits(this.value) >>> 32));
        return hash;
    }

    public static SharedDouble valueOf(double value) {
        return new SharedDouble(value);
    }

    public static SharedDouble valueOf(String value) {
        return valueOf(Double.parseDouble(value));
    }
}