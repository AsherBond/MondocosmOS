

package es.igosoftware.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class GByteArrayProtocolField
         extends
            GProtocolField<byte[]> {

   public GByteArrayProtocolField() {
      super(false);
   }


   @Override
   protected void doRead(final DataInputStream input) throws IOException {
      final int length = input.readInt();
      if (length < 0) {
         set(null);
      }
      else {
         final byte[] bytes = new byte[length];
         for (int i = 0; i < length; i++) {
            bytes[i] = input.readByte();
         }
         set(bytes);
      }
   }


   @Override
   protected void doWrite(final DataOutputStream output) throws IOException {
      final byte[] value = get();
      if (value == null) {
         output.writeInt(-1);
      }
      else {
         output.writeInt(value.length);
         output.write(value);
      }
   }

}
