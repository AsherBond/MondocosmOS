

package es.igosoftware.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;


public class GStringProtocolField
         extends
            GProtocolField<String> {

   private static final Charset UTF8 = Charset.forName("UTF-8");


   public GStringProtocolField() {
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
         set(new String(bytes, UTF8));
      }
   }


   @Override
   protected void doWrite(final DataOutputStream output) throws IOException {
      final String value = get();
      if (value == null) {
         output.writeInt(-1);
      }
      else {
         final byte[] bytes = value.getBytes(UTF8);
         output.writeInt(bytes.length);
         output.write(bytes);
      }
   }

}
