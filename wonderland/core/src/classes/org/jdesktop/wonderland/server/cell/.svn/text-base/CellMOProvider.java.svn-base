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
package org.jdesktop.wonderland.server.cell;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Provider that loads cells.  Providers must have a no-argument constructor.
 * <p>
 * A Provider exists to instantiate cells, for example cells loaded from
 * modules.  Since these modules are not typically in the Wonderland
 * classpath at compile time, cells can be loaded dynamically using these
 * providers. There is no compile-time argument checking when cells are loaded
 * this way, so an incorrect set of arguments will lead to a runtime exception.
 * <p>
 * Each provider supports a certain set of cell types, identified
 * by name.  While this name may be the fully-qualified class name of a cell,
 * it may also be a shorthand, such as "SlideShowCellGLO".  If multiple
 * providers support the same name, it is undefined which provider will
 * be called for that name.  In general, the system iterates through
 * each provider.  The value returned by the first provider to return a
 * non-null value (either an instantiated cell or an exception) will be
 * passed back to the client.  If no provider accepts the given type name,
 * the call will return null.
 * <p>
 * Each provider is responsible for loading cells using its own mechanisms.
 * A default, reflection-based mechanism is provided to load cells using
 * a constructor that accepts the given argument list.
 * @author jkaplan
 */
public abstract class CellMOProvider {

    /**
     * Load a cell of the given type.  If this provider supports the given
     * type name, it should return an instantiated cell or throw an exception.
     * Otherwise this method should return null.
     *
     * @param typeName the name of the cell type to load -- this is an
     * abstract name (e.g. "SampleCellGLO") and not a fully-qualified
     * class name.
     * @param args the arguments to use while loading the cell
     * @return an instantiated cell with the given type name, or null
     * if the given cell type is not supported
     * @throws LoadCellMOException if there is an error loading the cell
     */
    public abstract CellMO loadCellMO(String typeName, Object... args)
        throws LoadCellMOException;

    /**
     * Load a cell through reflection.
     * <p>
     * It is very important to note that this method takes the given arguments
     * and finds the best matching constructor.  A matching constructor
     * is one which has the same number of arguments as the provided arguments,
     * and each argument is of a type that the given argument can be cast to.
     * While this is similar to regular Java method invocation, it is more 
     * likely to find conflicts than regular Java 
     * @param clazz the class type to load
     * @param args the argument list to use when loading
     * @return a cell of the given class, constructed with the given
     * arguments
     * @throws NoSuchMethodException if no constructor can be found in
     * the given class for the given arguments
     * @throws InvocationTargetException if there is an error creating
     * the cell
     * @throws IllegalAccessException if there is a permission problem
     * @throws IllegalArgumentException if there is an error in one
     * of the arguments
     * @throws InstantiationException if there is another kind of error
     */
    public <T extends CellMO> T createCell(Class<T> clazz, 
                                                      Object... args)
        throws NoSuchMethodException, InvocationTargetException, 
               IllegalAccessException, IllegalArgumentException, 
               InstantiationException
    {    
        // find the best matching constructor
        Constructor<T> ctor = bestMatchingConstructor(clazz, args);
        
        // instantiate the cell
        return ctor.newInstance(args);
    }
    
    
    /**
     * Find the best matching constructor for the given class and arguments
     * @param clazz the class to search
     * @param args the arguments to compare
     * @return the best matching constructor
     * @throws NoSuchMethodException if there is no matching constructor or
     * an ambiguous match
     */
    @SuppressWarnings("unchecked")
    protected <T> Constructor<T> bestMatchingConstructor(Class<T> clazz, 
                                                         Object... args) 
        throws NoSuchMethodException
    {
        // find all matching constructors
        List<Constructor<T>> ctors = new ArrayList<Constructor<T>>();
        Constructor[] cArray = clazz.getConstructors();
        for (int i = 0; i < cArray.length; i++) {
            if (matches(cArray[i], args)) {
                ctors.add(cArray[i]);
            }
        }
        
        if (ctors.isEmpty()) {
            // no matches
            throw new NoSuchMethodException("No matching constructor.");
        } else if (ctors.size() == 1) {
            // shortcut if there is only one matching method
            return ctors.get(0);
        }
        
        // sort constructors
        Collections.sort(ctors, new Comparator<Constructor>() {
            public int compare(Constructor c0, Constructor c1) {
                return compare(c0, c1);
            }
        });
        
        // make sure the first is the highest, and not equal to the second
        Constructor res = ctors.get(0);
        Constructor second = ctors.get(1);
        
        if (compare(res, second) == 1) {
            // the first is highest, return it
            return res;
        } else {
            // the first two are equal -- this is an ambiguous case, so
            // we need to throw an exception
            throw new NoSuchMethodException("Ambiguous constructors: " + res + 
                    " and " + second);
        }
    }

    
    /**
     * Test if a given constructor accepts the given list of arguments
     * @param ctor the constructor to test
     * @param args the arguments
     * @return true if this constructor could be called with the given
     * arguments, or false if not
     */
    protected boolean matches(Constructor ctor, Object... args) {
        Class<?>[] params = ctor.getParameterTypes();

        // make sure this constructor takes the right number of arguments
        if (params.length != args.length) {
            return false;
        }

        // compare the type of each argument
        for (int i = 0; i < args.length; i++) {
            Class<?> argClazz = args[i].getClass();
            if (!params[i].isAssignableFrom(argClazz) && 
                !matchPrimitive(params[i], argClazz)) 
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Match two classes, one of which is a primitive type and a matching 
     * non-primitive type.
     * @param prim the primitive class to look at
     * @param other the second class to look at
     * @return true if the primitive type matches the non-primitive type
     * in autoboxing.
     */
    protected final boolean matchPrimitive(Class<?> prim, Class<?> other) {
        if (!prim.isPrimitive()) {
            return false;
        }

        // bad -- is there a better way to do this?
        if (prim == boolean.class) {
            return (Boolean.class.isAssignableFrom(other));
        } else if (prim == byte.class) {
            return (Byte.class.isAssignableFrom(other));
        } else if (prim == char.class) {
            return (Character.class.isAssignableFrom(other));
        } else if (prim == short.class) {
            return (Short.class.isAssignableFrom(other));
        } else if (prim == int.class) {
            return (Integer.class.isAssignableFrom(other));
        } else if (prim == float.class) {
            return (Float.class.isAssignableFrom(other));
        } else if (prim == double.class) {
            return (Double.class.isAssignableFrom(other));
        } else {
            // XXX shouldn't happen XXX
            throw new IllegalArgumentException("Unhandled primitive: " + prim);
        }
    }

    /**
     * Compare two constructors.  These constructors must
     * be known to match using the <code>matches()</code> method
     * in order to be compared.
     * <p>
     * The general idea is to look at each argument and see if it
     * can be cast to the comparable argument of the other class.
     * The goal is to find a constructor that is <i>more specific</i>
     * than another constructor.  A constructor is more specific than
     * another iff all its parameter types are assignable to
     * the type of the other constructor, but not vice versa.  For
     * example if one constructor takes a String argument and another
     * takes an Object, the one that takes the String is more specific
     * because String can be cast to Object, but not vice-versa.
     * <p>
     * If some arguments of one constructor are more specific and some
     * arguments of the other are more specific, the constructors are
     * equally specific.
     * <p>
     * This method compares two constructors, and returns 1 if the
     * first constructor is more specific than the second, 0 if they
     * are equally specific and -1 if the second constructor is
     * more specific than the first.
     * @param c0 the first constructor
     * @param c1 the second constructor
     * @return 1 if c0 is more specific than c1, 0 if they are equally
     * specific and -1 if c1 is more specific than c0.
     */
    public int compare(Constructor c0, Constructor c1) {
        Class<?>[] c0Params = c0.getParameterTypes();
        Class<?>[] c1Params = c1.getParameterTypes();

        // counts of more or less specific arguments
        int moreCount = 0;
        int lessCount = 0;

        for (int i = 0; i < c0Params.length; i++) {
            boolean more = c1Params[i].isAssignableFrom(c0Params[i]);
            boolean less = c0Params[i].isAssignableFrom(c1Params[i]);

            if (more && !less) {
                moreCount++;
            } else if (less && !more) {
                lessCount++;
            }
        }

        if (moreCount > 0 && lessCount == 0) {
            return 1;
        } else if (lessCount > 0 && moreCount == 0) {
            return -1;
        } else {
            return 0;
        }
    }
}
