

package es.igosoftware.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import es.igosoftware.io.GIOUtils;


public class GProtocolMultiplexor {

   private final Class<? extends IProtocolObject>[]       _classes;
   private final Constructor<? extends IProtocolObject>[] _constructors;


   public GProtocolMultiplexor(final Class<? extends IProtocolObject>... classes) {
      if (classes.length > 127) {
         throw new IllegalArgumentException("Too many classes");
      }

      _classes = classes;
      _constructors = initializeConstructors(classes);
   }


   @SuppressWarnings({
                     "cast",
                     "unchecked"
   })
   private Constructor<? extends IProtocolObject>[] initializeConstructors(final Class<? extends IProtocolObject>[] classes) {
      final Constructor<? extends IProtocolObject>[] result = (Constructor<? extends IProtocolObject>[]) new Constructor[classes.length];

      for (int i = 0; i < classes.length; i++) {
         final Class<? extends IProtocolObject> klass = classes[i];

         Constructor<? extends IProtocolObject> constructor;
         try {
            constructor = klass.getConstructor(GProtocolMultiplexor.class);
         }
         catch (final NoSuchMethodException e) {
            try {
               constructor = klass.getConstructor();
            }
            catch (final NoSuchMethodException e2) {
               throw new RuntimeException("Can't find a constructor for " + klass);
            }
         }
         result[i] = constructor;
      }

      return result;
   }


   public final byte[] getProtocolBytes(final IProtocolObject object) {

      DataOutputStream output = null;
      try {
         final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         output = new DataOutputStream(buffer);

         final byte classID = getClassID(object.getClass());
         output.writeByte(classID);

         final byte[] bytes = object.getProtocolBytes();
         output.write(bytes);

         output.flush();

         return buffer.toByteArray();
      }
      catch (final IOException e) {
         throw new RuntimeException(e);
      }
      finally {
         GIOUtils.gentlyClose(output);
      }
   }


   private byte getClassID(final Class<? extends IProtocolObject> class1) {
      //      for (final Class<? extends IProtocolObject> klass : _classes) {
      for (int i = 0; i < _classes.length; i++) {
         final Class<? extends IProtocolObject> klass = _classes[i];
         if (klass == class1) {
            return (byte) i;
         }
      }
      throw new RuntimeException("Can't find class " + class1);
   }


   public final IProtocolObject createObject(final DataInputStream input) {
      try {
         final byte classID = input.readByte();

         final Constructor<? extends IProtocolObject> constructor = _constructors[classID];

         final IProtocolObject instance;
         if (constructor.getParameterTypes().length == 0) {
            instance = constructor.newInstance();
         }
         else {
            instance = constructor.newInstance(this);
         }

         instance.initializeFromProtocolStream(input);
         return instance;
      }
      catch (final IOException e) {
         throw new RuntimeException(e);
      }
      catch (final IllegalAccessException e) {
         throw new RuntimeException(e);
      }
      catch (final InstantiationException e) {
         throw new RuntimeException(e);
      }
      catch (final InvocationTargetException e) {
         throw new RuntimeException(e);
      }
   }


   public final IProtocolObject createObject(final byte[] bytes) {
      DataInputStream input = null;
      try {
         final ByteArrayInputStream buffer = new ByteArrayInputStream(bytes);
         input = new DataInputStream(buffer);

         final byte classID = input.readByte();

         //         final Class<? extends IProtocolObject> klass = _classes[classID];
         final Constructor<? extends IProtocolObject> constructor = _constructors[classID];

         final IProtocolObject instance;
         if (constructor.getParameterTypes().length == 0) {
            instance = constructor.newInstance();
         }
         else {
            instance = constructor.newInstance(this);
         }

         instance.initializeFromProtocolBytes(bytes, 1);
         return instance;
      }
      catch (final IndexOutOfBoundsException e) {
         throw new RuntimeException("Invalid classID");
      }
      catch (final IllegalAccessException e) {
         throw new RuntimeException(e);
      }
      catch (final InstantiationException e) {
         throw new RuntimeException(e);
      }
      catch (final IOException e) {
         throw new RuntimeException(e);
      }
      catch (final IllegalArgumentException e) {
         throw new RuntimeException(e);
      }
      catch (final InvocationTargetException e) {
         throw new RuntimeException(e);
      }
      finally {
         GIOUtils.gentlyClose(input);
      }
   }


}
