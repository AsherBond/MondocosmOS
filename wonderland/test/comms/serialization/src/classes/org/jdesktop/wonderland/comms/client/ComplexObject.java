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
package org.jdesktop.wonderland.comms.client;

import javax.swing.JButton;

/**
 *
 * @author paulby
 */
public class ComplexObject extends SimpleObject {
    
    private Foo foo;
    
    public ComplexObject(long testLong) {
        super(testLong);
        foo = new Foo();
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ComplexObject))
            return false;
        
        if (!foo.equals(((ComplexObject)o).foo))
            return false;
        
        return super.equals(o);
    }
    
    class Foo extends JButton {
        public Foo() {
            super();
            setName("test");
        }
        
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Foo))
                return false;
            
            if (!getName().equals(((Foo)o).getName()))
                return false;
            
            
            return true;
        }
    }
}
