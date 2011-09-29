

package es.igosoftware.dmvc.codec;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;


public class GCompactObjectInputStream
         extends
            ObjectInputStream {

   private final ClassLoader _classLoader;


   public GCompactObjectInputStream(final InputStream in) throws IOException {
      this(in, null);
   }


   private GCompactObjectInputStream(final InputStream in,
                                     final ClassLoader classLoader) throws IOException {
      super(in);
      _classLoader = classLoader;
   }


   @Override
   protected void readStreamHeader() throws IOException, StreamCorruptedException {
      final int version = readByte() & 0xFF;
      if (version != STREAM_VERSION) {
         throw new StreamCorruptedException("Unsupported version: " + version);
      }
   }


   @Override
   protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
      final int type = read();
      if (type < 0) {
         throw new EOFException();
      }
      switch (type) {
         case GCompactObjectOutputStream.TYPE_FAT_DESCRIPTOR:
            return super.readClassDescriptor();
         case GCompactObjectOutputStream.TYPE_THIN_DESCRIPTOR:
            final String className = readUTF();
            final Class<?> clazz = loadClass(className);
            return ObjectStreamClass.lookup(clazz);
         default:
            throw new StreamCorruptedException("Unexpected class descriptor type: " + type);
      }
   }


   @Override
   protected Class<?> resolveClass(final ObjectStreamClass desc) throws IOException, ClassNotFoundException {
      final String className = desc.getName();
      try {
         return loadClass(className);
      }
      catch (final ClassNotFoundException ex) {
         return super.resolveClass(desc);
      }
   }


   protected Class<?> loadClass(final String className) throws ClassNotFoundException {
      Class<?> clazz;
      ClassLoader classLoader = _classLoader;
      if (classLoader == null) {
         classLoader = Thread.currentThread().getContextClassLoader();
      }

      if (classLoader != null) {
         clazz = classLoader.loadClass(className);
      }
      else {
         clazz = Class.forName(className);
      }
      return clazz;
   }
}
