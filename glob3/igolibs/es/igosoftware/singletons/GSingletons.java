

package es.igosoftware.singletons;

import java.util.HashMap;
import java.util.Map;

import es.igosoftware.logging.GLogger;
import es.igosoftware.logging.ILogger;
import es.igosoftware.util.GAssert;


public class GSingletons {
   public static interface IFactory<T> {
      public T create();
   }


   private static final GSingletons _instance = new GSingletons();


   public static GSingletons instance() {
      return _instance;
   }


   private final Map<Class<?>, Object>      _instances = new HashMap<Class<?>, Object>();
   private final Map<Class<?>, IFactory<?>> _factories = new HashMap<Class<?>, IFactory<?>>();


   private GSingletons() {
      registerDefaultLoggerSingleton();
   }


   private void registerDefaultLoggerSingleton() {
      registerLazyInstance(ILogger.class, new IFactory<ILogger>() {
         @Override
         public ILogger create() {
            return GLogger.instance();
         }
      });
   }


   public ILogger getLogger() {
      return getMandatoryInstance(ILogger.class);
   }


   public synchronized <T> void registerLazyInstance(final Class<T> klass,
                                                     final IFactory<T> factory) {
      GAssert.notNull(klass, "klass");
      GAssert.notNull(factory, "factory");

      final IFactory<?> current = _factories.get(klass);
      if (current != null) {
         System.err.println("WARNING: factory for " + klass + " was [" + current + "] replaced with [" + factory + "]");
      }

      _factories.put(klass, factory);
   }


   public synchronized <T> void registerInstance(final Class<T> klass,
                                                 final T instance) {
      GAssert.notNull(klass, "klass");
      GAssert.notNull(instance, "instance");

      final Object current = _instances.get(klass);
      if (current != null) {
         System.err.println("WARNING: instance of " + klass + " was [" + current + "] replaced with [" + instance + "]");
      }

      _instances.put(klass, instance);
   }


   @SuppressWarnings("unchecked")
   public synchronized <T> T getInstanceOrNull(final Class<T> klass) {
      GAssert.notNull(klass, "klass");

      T instance = (T) _instances.get(klass);

      if (instance == null) {
         final IFactory<T> factory = (IFactory<T>) _factories.get(klass);
         if (factory != null) {
            instance = factory.create();
            _instances.put(klass, instance);

            _factories.remove(klass);
         }
      }

      return instance;
   }


   public synchronized <T> T getMandatoryInstance(final Class<T> klass) {
      final T instanceOrNull = getInstanceOrNull(klass);
      if (instanceOrNull == null) {
         throw new RuntimeException("Mandatory instance of " + klass + " was not found!");
      }
      return instanceOrNull;
   }


   @Override
   public String toString() {
      return "GInstancesManager [instances=" + _instances + ", factories=" + _factories + "]";
   }


}
