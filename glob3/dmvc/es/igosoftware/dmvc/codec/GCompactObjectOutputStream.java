

package es.igosoftware.dmvc.codec;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;


public class GCompactObjectOutputStream
         extends
            ObjectOutputStream {

   static final int TYPE_FAT_DESCRIPTOR  = 0;
   static final int TYPE_THIN_DESCRIPTOR = 1;


   public GCompactObjectOutputStream(final OutputStream out) throws IOException {
      super(out);
   }


   @Override
   protected void writeStreamHeader() throws IOException {
      writeByte(STREAM_VERSION);
   }


   @Override
   protected void writeClassDescriptor(final ObjectStreamClass desc) throws IOException {
      final Class<?> clazz = desc.forClass();
      if (clazz.isPrimitive() || clazz.isArray()) {
         write(TYPE_FAT_DESCRIPTOR);
         super.writeClassDescriptor(desc);
      }
      else {
         write(TYPE_THIN_DESCRIPTOR);
         writeUTF(desc.getName());
      }
   }
}
