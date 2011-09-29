

package es.igosoftware.dmvc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import es.igosoftware.dmvc.codec.GCompactObjectInputStream;
import es.igosoftware.dmvc.codec.GCompactObjectOutputStream;
import es.igosoftware.dmvc.model.GDModel;
import es.igosoftware.dmvc.model.IDModel;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.util.GUtils;
import es.igosoftware.util.LRUCache;


public final class GDUtils {


   private GDUtils() {
   }


   public static byte[] getSerializedBytes(final Object object) {
      ObjectOutputStream output = null;
      try {
         final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         output = new GCompactObjectOutputStream(new GZIPOutputStream(buffer));
         // output = new GCompactObjectOutputStream(buffer);
         output.writeObject(object);
         output.flush();
         output.close();

         return buffer.toByteArray();
      }
      catch (final IOException e) {
         throw new RuntimeException(e);
      }
      finally {
         GIOUtils.gentlyClose(output);
      }
   }


   public static Object getObject(final byte[] serialized) {
      GCompactObjectInputStream input = null;
      try {
         final ByteArrayInputStream buffer = new ByteArrayInputStream(serialized);
         input = new GCompactObjectInputStream(new GZIPInputStream(buffer));
         // input = new GCompactObjectInputStream(buffer);
         return input.readObject();
      }
      catch (final IOException e) {
         throw new RuntimeException(e);
      }
      catch (final ClassNotFoundException e) {
         throw new RuntimeException(e);
      }
      finally {
         GIOUtils.gentlyClose(input);
      }
   }


   @SuppressWarnings("unchecked")
   public static Class<? extends IDModel> getModelInterface(final Class<? extends GDModel> modelClass) {
      final Class<?>[] implementedInterfaces = modelClass.getInterfaces();

      Class<?> found = null;
      for (final Class<?> inter : implementedInterfaces) {
         if ((inter != IDModel.class) && IDModel.class.isAssignableFrom(inter)) {
            if (found == null) {
               found = inter;
            }
            else {
               throw new RuntimeException("Model " + modelClass + " implements more than one sub-interface of IDModel (" + found
                                          + " and " + inter + ")");
            }
         }
      }

      if (found == null) {
         final Class<?> superClass = modelClass.getSuperclass();
         if ((superClass != GDModel.class) && GDModel.class.isAssignableFrom(superClass)) {
            return getModelInterface((Class<? extends GDModel>) superClass);
         }

         throw new RuntimeException("Model " + modelClass + " doesn't implement an sub-interface of IDModel");
      }

      return (Class<? extends IDModel>) found;
   }


   private static final LRUCache<Class<? extends IDModel>, List<Method>, RuntimeException> MODEL_INTERFACES_METHODS_CACHE;

   static {
      final LRUCache.ValueFactory<Class<? extends IDModel>, List<Method>, RuntimeException> factory = new LRUCache.ValueFactory<Class<? extends IDModel>, List<Method>, RuntimeException>() {
         private static final long serialVersionUID = 1L;


         @Override
         public List<Method> create(final Class<? extends IDModel> modelInterface) throws RuntimeException {
            final List<Method> methods = new ArrayList<Method>(Arrays.asList(modelInterface.getMethods()));
            final Iterator<Method> iterator = methods.iterator();
            while (iterator.hasNext()) {
               final Method method = iterator.next();
               final Class<?> declaringClass = method.getDeclaringClass();
               if ((declaringClass == IDSerializable.class) || (declaringClass == IDModel.class)) {
                  iterator.remove();
               }
            }

            Collections.sort(methods, new Comparator<Method>() {
               @Override
               public int compare(final Method o1,
                                  final Method o2) {
                  return o1.toString().compareTo(o2.toString());
               }
            });

            synchronized (System.out) {
               System.out.println("-----------------------------------------------------------------------------------------------");
               System.out.println(" Methods IDs for " + modelInterface);
               for (int i = 0; i < methods.size(); i++) {
                  System.out.println("    #" + i + " " + methods.get(i));
               }
               System.out.println("-----------------------------------------------------------------------------------------------");
            }

            return methods;
         }
      };
      MODEL_INTERFACES_METHODS_CACHE = new LRUCache<Class<? extends IDModel>, List<Method>, RuntimeException>(200, factory);
   }


   private static List<Method> getModelInterfaceMethods(final Class<? extends IDModel> modelClass) {
      return MODEL_INTERFACES_METHODS_CACHE.get(modelClass);
   }


   public static Method getMethod(final Class<? extends GDModel> modelClass,
                                  final int methodID) {
      final Class<? extends IDModel> modelInterface = getModelInterface(modelClass);
      final List<Method> methods = getModelInterfaceMethods(modelInterface);
      return methods.get(methodID);
   }


   public static int getMethodID(final Class<? extends IDModel> modelInterface,
                                 final Method method) {
      final List<Method> methods = getModelInterfaceMethods(modelInterface);
      return methods.indexOf(method);
   }


   public static final String resultString(final Object result) {
      if (result == null) {
         return "null";
      }
      else if (result instanceof Object[]) {
         return "array (" + ((Object[]) result).length + " elements)";
      }
      else if (result instanceof Collection) {
         return result.getClass().getCanonicalName() + " (" + ((Collection) result).size() + " elements)";
      }
      else {
         return GUtils.toString(result);
      }
   }

}
