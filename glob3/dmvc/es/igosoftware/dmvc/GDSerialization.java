

package es.igosoftware.dmvc;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.netty.channel.Channel;

import es.igosoftware.dmvc.client.GDClient;
import es.igosoftware.dmvc.server.GDServer;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.IFunction;


public class GDSerialization {


   @SuppressWarnings("unchecked")
   public static Object materializeInClient(final Object object,
                                            final Channel channel,
                                            final GDClient client) {

      if (object == null) {
         return null;
      }

      if (object instanceof IDSerializable) {
         final IDSerializable serializable = (IDSerializable) object;
         return serializable.materializeInClient(channel, client);
      }

      if (object instanceof Object[]) {
         final Object[] array = (Object[]) object;
         final Object[] result = new Object[array.length];
         for (int i = 0; i < array.length; i++) {
            result[i] = GDSerialization.materializeInClient(array[i], channel, client);
         }
         return result;
      }

      if (object instanceof Iterable<?>) {
         final IFunction<Object, Object> transformer = new IFunction<Object, Object>() {
            @Override
            public Object apply(final Object element) {
               return GDSerialization.materializeInClient(element, channel, client);
            }
         };

         if (object instanceof List<?>) {
            return GCollections.collect((List<Object>) object, transformer);
         }

         if (object instanceof Set<?>) {
            return GCollections.collect((Set<Object>) object, transformer);
         }

         if (object instanceof Map<?, ?>) {
            return GCollections.collect((Map<Object, Object>) object, transformer, transformer);
         }

         if (object instanceof Collection<?>) {
            return GCollections.collect((Collection<Object>) object, transformer);
         }

         return GCollections.collect((Iterable<Object>) object, transformer);
      }

      return object;
   }


   @SuppressWarnings("unchecked")
   public static Object materializeInServer(final Object object,
                                            final Channel channel,
                                            final GDServer server) {

      if (object == null) {
         return null;
      }

      if (object instanceof IDSerializable) {
         final IDSerializable serializable = (IDSerializable) object;
         return serializable.materializeInServer(channel, server);
      }

      if (object instanceof Object[]) {
         final Object[] array = (Object[]) object;
         final Object[] result = new Object[array.length];
         for (int i = 0; i < array.length; i++) {
            result[i] = GDSerialization.materializeInServer(array[i], channel, server);
         }
         return result;
      }


      if (object instanceof Iterable<?>) {

         final IFunction<Object, Object> transformer = new IFunction<Object, Object>() {
            @Override
            public Object apply(final Object element) {
               return GDSerialization.materializeInServer(element, channel, server);
            }
         };

         if (object instanceof List<?>) {
            return GCollections.collect((List<Object>) object, transformer);
         }

         if (object instanceof Set<?>) {
            return GCollections.collect((Set<Object>) object, transformer);
         }

         if (object instanceof Map<?, ?>) {
            return GCollections.collect((Map<Object, Object>) object, transformer, transformer);
         }

         if (object instanceof Collection<?>) {
            return GCollections.collect((Collection<Object>) object, transformer);
         }

         return GCollections.collect((Iterable<Object>) object, transformer);
      }

      return object;
   }


   @SuppressWarnings("unchecked")
   public static Object objectToSerialize(final Object object,
                                          final GDClient client) {
      if (object == null) {
         return null;
      }

      if (object instanceof IDSerializable) {
         final IDSerializable serializable = (IDSerializable) object;
         return serializable.objectToSerialize(client);
         //throw new RuntimeException("Serialization of models not yet implemented");
      }

      if (object instanceof Object[]) {
         final Object[] array = (Object[]) object;
         final Object[] result = new Object[array.length];
         for (int i = 0; i < array.length; i++) {
            result[i] = GDSerialization.objectToSerialize(array[i], client);
         }
         return result;
      }

      if (object instanceof Iterable<?>) {
         final IFunction<Object, Object> transformer = new IFunction<Object, Object>() {
            @Override
            public Object apply(final Object element) {
               return GDSerialization.objectToSerialize(element, client);
            }
         };

         if (object instanceof List<?>) {
            return GCollections.collect((List<Object>) object, transformer);
         }

         if (object instanceof Set<?>) {
            return GCollections.collect((Set<Object>) object, transformer);
         }

         if (object instanceof Map<?, ?>) {
            return GCollections.collect((Map<Object, Object>) object, transformer, transformer);
         }

         if (object instanceof Collection<?>) {
            return GCollections.collect((Collection<Object>) object, transformer);
         }

         return GCollections.collect((Iterable<Object>) object, transformer);
      }

      return object;
   }


   @SuppressWarnings("unchecked")
   public static Object objectToSerialize(final Object object,
                                          final GDServer server) {
      if (object == null) {
         return null;
      }

      if (object instanceof IDSerializable) {
         final IDSerializable serializable = (IDSerializable) object;
         return serializable.objectToSerialize(server);
         //throw new RuntimeException("Serialization of models not yet implemented");
      }

      if (object instanceof Object[]) {
         final Object[] array = (Object[]) object;
         final Object[] result = new Object[array.length];
         for (int i = 0; i < array.length; i++) {
            result[i] = GDSerialization.objectToSerialize(array[i], server);
         }
         return result;
      }

      if (object instanceof Iterable<?>) {
         final IFunction<Object, Object> transformer = new IFunction<Object, Object>() {
            @Override
            public Object apply(final Object element) {
               return GDSerialization.objectToSerialize(element, server);
            }
         };

         if (object instanceof List<?>) {
            return GCollections.collect((List<Object>) object, transformer);
         }

         if (object instanceof Set<?>) {
            return GCollections.collect((Set<Object>) object, transformer);
         }

         if (object instanceof Map<?, ?>) {
            return GCollections.collect((Map<Object, Object>) object, transformer, transformer);
         }

         if (object instanceof Collection<?>) {
            return GCollections.collect((Collection<Object>) object, transformer);
         }

         return GCollections.collect((Iterable<Object>) object, transformer);
      }

      return object;
   }


   public static Object[] objectToSerialize(final Object[] array,
                                            final GDClient client) {
      if (array == null) {
         return null;
      }

      final Object[] result = new Object[array.length];
      for (int i = 0; i < array.length; i++) {
         result[i] = GDSerialization.objectToSerialize(array[i], client);
      }
      return result;

   }


   private GDSerialization() {
   }


}
