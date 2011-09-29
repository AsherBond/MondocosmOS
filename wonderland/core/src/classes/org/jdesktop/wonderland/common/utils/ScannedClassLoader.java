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

/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., All Rights Reserved
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
package org.jdesktop.wonderland.common.utils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.scannotation.AnnotationDB;
import org.scannotation.ClasspathUrlFinder;
import sun.misc.Service;

/**
 * A classloader that scans its contents for annotations. It can then be
 * queried by annotation type for all classes implementing the given annotation.
 * @author jkaplan
 */
public class ScannedClassLoader extends URLClassLoader {
    private static final Logger logger =
            Logger.getLogger(ScannedClassLoader.class.getName());

    /** the database of annotations */
    private AnnotationDB annotationDB;

    /**
     * Get a singleton instance for the System class loader
     * @return a singleton scanned version of the System class loader
     */
    public static ScannedClassLoader getSystemScannedClassLoader() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Create a scannned classloader that scans the contents of
     * the system classloader. Use getSystemScannedClassLoader() instead.
     */
    protected ScannedClassLoader() {
        super (new URL[0]);

        createDB(ClasspathUrlFinder.findClassPaths());
    }

    /**
     * Create a scanned classloader that scans the given urls, using the
     * default parent classloader.
     * @param urls the urls to scan
     */

    public ScannedClassLoader(URL[] urls) {
        super (urls);

        createDB(urls);
    }

    /**
     * Create a scanned classloader that scans the given urls.
     * @param urls the urls to scan
     * @param parent the parent classlaoder to delegate to
     */

    public ScannedClassLoader(URL[] urls, ClassLoader parent) {
        super (urls, parent);

        createDB(urls);
    }

    /**
     * Get the name of all classes that are annotated with the given
     * annotation.
     * @param annotation the annotation to search for
     */
    public Set<String> getClasses(Class<? extends Annotation> clazz) {
        String name = clazz.getName();

        // search the index for the given annotation
        Set<String> out = new LinkedHashSet<String>();
        Set<String> classes = annotationDB.getAnnotationIndex().get(name);
        if (classes != null) {
            out.addAll(classes);
        }
        
        // if there is a parent ScannedClassLoader, delegate to it
        ClassLoader parent = getParent();
        while (parent != null) {
            if (parent instanceof ScannedClassLoader) {
                out.addAll(((ScannedClassLoader) parent).getClasses(clazz));
                break;
            }

            parent = parent.getParent();
        }

        return out;
    }

    /**
     * Get instances of every class marked with the given annotation.  The
     * returned iterator will instantiate the object during the call to
     * hasNext() or next().  If there is an error, a ClassScanningError will
     * be thrown by the call to hasNext() or next().  If an error is thrown,
     * the iterator will attempt to recover so subsequent calls don't fail,
     * but the recovery can't be guaranteed.
     * <p>
     * All instantiations will be done using the default, no argument 
     * constructor.
     * <p>
     * This iterator does not support removal.
     * <p>
     * @param annot the annotation to search for
     * @param clazz the class of the return type
     * @return an iterator of instantiated instances of the given type
     */
    public <T> Iterator<T> getInstances(Class<? extends Annotation> annot,
                                        final Class<T> clazz)
    {
        final Iterator<String> classNames = getClasses(annot).iterator();

        return new Iterator<T>() {
            private T next;

            public boolean hasNext() {
                if (next != null) {
                    return true;
                } else {
                    return loadNext();
                }
            }

            public T next() {
                if (!hasNext()) {
                    throw new IllegalStateException("No more elements");
                }

                T out = next;
                next = null;

                return out;
            }

            boolean loadNext() {
                next = null;

                while (next == null && classNames.hasNext()) {
                    String className = classNames.next();

                    try {
                        Class loaded = loadClass(className);
                        if (clazz.isAssignableFrom(loaded) && !Modifier.isAbstract(loaded.getModifiers())) {
                            next = (T) loaded.newInstance();
                        }
                    } catch (InstantiationException ex) {
                        throw new ClassScanningError("Error loading " + className, ex);
                    } catch (IllegalAccessException ex) {
                        throw new ClassScanningError("Error loading " + className, ex);
                    } catch (ClassNotFoundException ex) {
                        throw new ClassScanningError("Error loading " + className, ex);
                    }
                }

                return (next != null);
            }

            public void remove() {
                throw new UnsupportedOperationException("Not supported.");
            }
        };
    }

    /**
     * Get instances of all classes that implement the given interface,
     * defined either by service provider or by annoation.
     * @param annot the annotation to search for
     * @param clazz the class of the return type
     * @return an iterator of instantiated instances of the given type
     */
    public <T> Iterator<T> getAll(Class<? extends Annotation> annot,
                                  Class<T> clazz)
    {
        // get the iterator for service providers
        final Iterator<T> providers = Service.providers(clazz, this);

        // get the iterator for annotations
        final Iterator<T> annots = getInstances(annot, clazz);

        // return a combined iterator
        return new Iterator<T>() {
            private boolean p = true;

            public boolean hasNext() {
                if (p && providers.hasNext()) {
                    return true;
                } else {
                    p = false;
                    return annots.hasNext();
                }
            }

            public T next() {
                if (p) {
                    return providers.next();
                } else {
                    return annots.next();
                }
            }

            public void remove() {
                throw new UnsupportedOperationException("Not supported.");
            }
        };
    }

    @Override
    protected void addURL(URL url) {
        super.addURL(url);

        try {
            annotationDB.scanArchives(getURLs());
        } catch (IOException ioe) {
            logger.log(Level.WARNING, "Error rescanning " + this +
                                     " for annotations", ioe);
        }
    }

    /**
     * Create the annotation database from the given URLs.
     * @param urls the urls to use
     */
    protected synchronized void createDB(URL[] urls) {
        try {
            long start = System.currentTimeMillis();

            annotationDB = new AnnotationDB();
            annotationDB.scanArchives(urls);

            long time = System.currentTimeMillis() - start;
            logger.warning("Scanned classes in " + time + " ms.");
        } catch (IOException ioe) {
            logger.log(Level.WARNING, "Error scanning " +
                       Arrays.toString(urls) + " for annotation", ioe);
        }
    }

   /**
    * SingletonHolder is loaded on the first execution of
    * ScannedClassLoader.getInstance() or the first access to
    * SingletonHolder.INSTANCE, not before.
    */
   private static class SingletonHolder {
     private final static ScannedClassLoader INSTANCE = new ScannedClassLoader();
   }
}
