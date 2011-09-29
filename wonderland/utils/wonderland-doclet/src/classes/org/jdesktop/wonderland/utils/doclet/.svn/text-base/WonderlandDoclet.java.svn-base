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
package org.jdesktop.wonderland.utils.doclet;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.formats.html.HtmlDoclet;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A doclet that processes based on Wonderland annotations.  This doclet
 * wraps the core RootDoc and PackageDoc elements to add a new level of
 * filter based on the Wonderland API stability level.  The desired
 * stability level is specified with the <code>-wonderlandAPI</code> argument
 * to javadoc.
 * <p>
 * The <code>-wonderlandAPI</code> argument must be followed by a list of
 * stability levels separated by commas.  The valid stability levels are
 * <ul><li>stable
 *     <li>experimental
 *     <li>private
 *     <li>internal
 * </ul>
 * These correspond to classes tagged with the corresponding annotation from
 * the <code>org.jdesktop.wonderland</code> package.  So calling the
 * doclet with the arguments <code>-wonderlandAPI experimental,private</code>
 * will generate javadoc for all classes tagged with the 
 * <code>ExperimentalAPI</code> and <code>PrivateAPI</code> annotations.
 * 
 * @author jkaplan
 */
public class WonderlandDoclet extends Doclet {
    /** map from argument name to API name */;
    private static final Map<String, String> API_MAP = 
            new HashMap<String, String>();
    
    static {
        API_MAP.put("stable",       "StableAPI");
        API_MAP.put("experimental", "ExperimentalAPI");
        API_MAP.put("private",      "PrivateAPI");
        API_MAP.put("internal",     "InternalAPI");
    }
    
    /** 
     * Start parsing using the standard HTML doclet
     * @param root the root of the document tree
     * @return true if the parsing is successful, or false if there is an error
     */
    public static boolean start(RootDoc root) {
        HtmlDoclet doclet = new HtmlDoclet();
        
        // get the APIs from the arguments
        String[][] options = root.options();
        String[] types = null;
        for (String[] option : options) {
            if (option[0].toLowerCase().equals("-wonderlandapi")) {
                types = option[1].split(",");
                
                // convert to internal names
                for (int i = 0; i < types.length; i++) {
                    types[i] = API_MAP.get(types[i].toLowerCase());
                }
            }
        }
        
        try {
            // wrap the root object
            ClassLoader loader = WonderlandDoclet.class.getClassLoader();
            Class<?>[] interfaces = new Class<?>[] { RootDoc.class };
            RootDocWrapper handler = new RootDocWrapper(root, types);
            RootDoc wrapper = 
                    (RootDoc) Proxy.newProxyInstance(loader, interfaces, handler);
        
            // overwrite the configuration used by the standard doclet to
            // return our wrapped classes
            doclet.configuration.root = wrapper;
            doclet.configuration.packages = handler.wrappedPackages;
            
            // parse using the standard HTML doclet
            doclet.start(doclet, wrapper);
        } catch (Exception exc) {
            exc.printStackTrace();
            return false;
        }
        return true;
    }
    
    /**
     * Validate standard HTML doclet arguments and also the 
     * <code>-wonderlandAPI</code> argument.
     * @param arg the option that was passes on the command line
     * @return the number of arguments required
     */
    public static int optionLength(String arg) {
        if (arg.toLowerCase().equals("-wonderlandapi")) {
            return 2;
        }
        
        return HtmlDoclet.optionLength(arg);
    }
    
    /**
     * Validate standard HTML doclet arguments and also the
     * <code>-wonderlandAPI</code> argument.
     * @param options the options to validate
     * @param errorReporter how to report an error
     * @return true if the options are valid, or false if not
     */
    public static boolean validOptions(String[][] options, 
                                       DocErrorReporter errorReporter) 
    {
        if (!HtmlDoclet.validOptions(options, errorReporter)) {
            return false;
        }
    
        // check that the -wonderlandAPI option exists
        boolean wonderlandAPI = false;
        boolean validOptions = true;
        for (int i = 0; i < options.length; i++) {
            String[] option = options[i];
            if (option[0].toLowerCase().equals("-wonderlandapi")) {
                wonderlandAPI = true;
            
                String[] apis = option[1].split(",");
                for (String api : apis) {
                    if (!API_MAP.containsKey(api.toLowerCase())) {
                        validOptions = false;
                        break;
                    } 
                }
            }
        }
        
        String usage = "Usage: -wonderlandAPI stable,experimental,internal,private";
        
        // if the -wonderlandAPI argument was not present, throw an error
        if (!wonderlandAPI) {
            errorReporter.printError("No -wonderlandAPI specified.\n" + usage);
            return false;
        }
        
        // if specified APIS are invaliud
        if (!validOptions) {
            errorReporter.printError("Invalid wonderland API.\n" + usage);
            return false;
        }
        
        return true;
    }
    
    /**
     * Utility to filter a set of classes based on annotations.  This method
     * will filter the given list of classes based on whether any of the
     * annotations given in the types argument are present.  This looks only
     * at the class name of the annotation, not the fully-qualified name.
     * @param orig the original set of classes to filter
     * @param types the types of annotations to accept
     * @return the filtered list of classes
     */
    protected static ClassDoc[] filterClasses(ClassDoc[] orig, String... types) {
            if (types == null || types.length == 0) {
                return new ClassDoc[0];
            }
            
            List<String> in = Arrays.asList(types);
            List<ClassDoc> out = new ArrayList<ClassDoc>();
            for (ClassDoc classDoc : orig) {
                for (AnnotationDesc annotation : classDoc.annotations()) {
                    if (in.contains(annotation.annotationType().name())) {
                        out.add(classDoc);
                        break;
                    }
                }
            }
            
            return out.toArray(new ClassDoc[0]);
        }
    
    /**
     * Wrapper for the root that overrides the <code>classes</code> and
     * <code>specifiedPackages</code> methods to use the filter passed
     * in via the <code>-wonderlandAPI</code> option.
     */
    static class RootDocWrapper implements InvocationHandler 
    {
        private RootDoc wrapped;
        private ClassDoc[] wrappedClasses;
        private PackageDoc[] wrappedPackages;
        
        public RootDocWrapper(RootDoc wrapped, String... types) 
                throws Exception 
        {
            this.wrapped = wrapped;
            
            wrappedClasses = filterClasses(wrapped.classes(), types);
            wrappedPackages = getPackages(wrapped.specifiedPackages(), types);
        }
        
        public Object invoke(Object obj, Method method, Object[] args) 
                throws Throwable 
        {
            if (method.getName().equals("classes")) {
                return wrappedClasses;
            } else if (method.getName().equals("specifiedPackages")) {
                return wrappedPackages;
            }
            
            return method.invoke(wrapped, args);
        }
       
        private PackageDoc[] getPackages(PackageDoc[] orig, String... types) {
            List<PackageDoc> wrapped = new ArrayList<PackageDoc>();

            for (PackageDoc p : orig) {
                // wrap the package
                ClassLoader loader = WonderlandDoclet.class.getClassLoader();
                Class<?>[] interfaces = new Class<?>[] { PackageDoc.class };
                InvocationHandler handler = new PackageDocWrapper(p, "ExperimentalAPI");
                PackageDoc pkg =
                        (PackageDoc) Proxy.newProxyInstance(loader, interfaces, handler);
                if (pkg.allClasses().length > 0) {
                    wrapped.add(pkg);
                }
            }
            
            return wrapped.toArray(new PackageDoc[0]);
        }
    }
    
    /**
     * Wrapper for an individual package that overrides all methods that
     * get the various class lists to apply the filter specified in the
     * <code>-wonderlandAPI</code> command line option.
     */
    static class PackageDocWrapper implements InvocationHandler 
    {
        private PackageDoc wrapped;
        private String[] types;
        
        private ClassDoc[] allClasses;
        
        public PackageDocWrapper(PackageDoc wrapped, String... types) {
            this.wrapped = wrapped;
            this.types = types;
            
            allClasses = filterClasses(wrapped.allClasses(true), types);
        }
        
        public Object invoke(Object obj, Method method, Object[] args) 
                throws Throwable 
        {
            if (method.getName().equals("allClasses") && 
                (args == null || args.length == 0 ||
                    ((Boolean) args[0]).booleanValue()))
            {
                // override the allClasses method, but only when
                // the argument is either not given or TRUE
                return allClasses;
            } else if (method.getName().equals("ordinaryClasses") ||
                       method.getName().equals("exceptions") ||
                       method.getName().equals("errors") ||
                       method.getName().equals("enums") ||
                       method.getName().equals("interfaces"))
            {
                // override the various individual class type getter methods
                return filterClasses((ClassDoc[]) method.invoke(wrapped, args), 
                                     types);
            } else if (method.getName().equals("compareTo") &&
                       args[0] instanceof PackageDoc) 
            {
                // hack to avoid a ClassCastException in DocImpl
                return wrapped.name().compareTo(((PackageDoc) args[0]).name());
            }
            
            return method.invoke(wrapped, args);
        }
    }
}
