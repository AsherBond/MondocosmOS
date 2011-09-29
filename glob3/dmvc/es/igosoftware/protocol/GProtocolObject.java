

package es.igosoftware.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import es.igosoftware.io.GIOUtils;


public abstract class GProtocolObject
         implements
            IProtocolObject {


   public static <T extends IProtocolObject> T createObject(final Class<? extends T> klass,
                                                            final byte[] bytes) {
      try {
         final T instance = klass.newInstance();
         instance.initializeFromProtocolBytes(bytes, 0);
         return instance;
      }
      catch (final InstantiationException e) {
         throw new RuntimeException(e);
      }
      catch (final IllegalAccessException e) {
         throw new RuntimeException(e);
      }
   }


   protected abstract IProtocolField[] getProtocolFields();


   @Override
   public void initializeFromProtocolBytes(final byte[] bytes,
                                           final int skipBytes) {
      DataInputStream input = null;
      try {
         final ByteArrayInputStream buffer = new ByteArrayInputStream(bytes);
         input = new DataInputStream(buffer);

         final int skipped = input.skipBytes(skipBytes);
         if (skipped != skipBytes) {
            throw new RuntimeException("Can't skip " + skipBytes + " bytes, only skipped " + skipped);
         }

         for (final IProtocolField field : getProtocolFields()) {
            field.read(input);
         }

         initializeFromFields();
      }
      catch (final IOException e) {
         throw new RuntimeException(e);
      }
      finally {
         GIOUtils.gentlyClose(input);
      }
   }


   protected abstract void initializeFromFields();


   @Override
   public byte[] getProtocolBytes() {
      storeIntoFields();

      DataOutputStream output = null;
      try {
         final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         output = new DataOutputStream(buffer);
         for (final IProtocolField field : getProtocolFields()) {
            field.write(output);
         }
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


   protected abstract void storeIntoFields();


   @Override
   public void initializeFromProtocolStream(final DataInputStream input) {
      try {
         for (final IProtocolField field : getProtocolFields()) {
            field.read(input);
         }

         initializeFromFields();
      }
      catch (final IOException e) {
         throw new RuntimeException(e);
      }
   }
}
