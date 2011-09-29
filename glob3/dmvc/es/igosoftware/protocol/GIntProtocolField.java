

package es.igosoftware.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class GIntProtocolField
         extends
            GProtocolField<Integer> {


   public GIntProtocolField(final boolean isNullable) {
      super(isNullable);
   }


   @Override
   protected void doRead(final DataInputStream input) throws IOException {
      set(input.readInt());
   }


   @Override
   protected void doWrite(final DataOutputStream output) throws IOException {
      output.writeInt(get());
   }

}
