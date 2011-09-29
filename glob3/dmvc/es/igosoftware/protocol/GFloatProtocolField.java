

package es.igosoftware.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class GFloatProtocolField
         extends
            GProtocolField<Float> {


   public GFloatProtocolField(final boolean isNullable) {
      super(isNullable);
   }


   @Override
   protected void doRead(final DataInputStream input) throws IOException {
      set(input.readFloat());
   }


   @Override
   protected void doWrite(final DataOutputStream output) throws IOException {
      output.writeFloat(get());
   }


}
