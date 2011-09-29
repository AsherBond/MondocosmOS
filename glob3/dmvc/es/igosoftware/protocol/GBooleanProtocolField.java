

package es.igosoftware.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class GBooleanProtocolField
         extends
            GProtocolField<Boolean> {


   public GBooleanProtocolField(final boolean isNullable) {
      super(isNullable);
   }


   @Override
   protected void doRead(final DataInputStream input) throws IOException {
      set(input.readBoolean());
   }


   @Override
   protected void doWrite(final DataOutputStream output) throws IOException {
      output.writeBoolean(get());
   }

}
