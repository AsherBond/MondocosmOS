

package es.igosoftware.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public abstract class GArrayProtocolField<T extends IProtocolObject>
         extends
            GProtocolField<T[]> {


   private GProtocolMultiplexor _multiplexor;


   public GArrayProtocolField(final boolean isNullable) {
      super(isNullable);
   }


   public void setMultiplexor(final GProtocolMultiplexor multiplexor) {
      _multiplexor = multiplexor;
   }


   @SuppressWarnings("unchecked")
   @Override
   protected void doRead(final DataInputStream input) throws IOException {
      final int length = input.readInt();
      if (length < 0) {
         set(null);
      }
      else {
         final T[] value = createArray(length);
         for (int i = 0; i < length; i++) {
            value[i] = (T) _multiplexor.createObject(input);
         }
         set(value);
      }
   }


   protected abstract T[] createArray(final int length);


   @Override
   protected void doWrite(final DataOutputStream output) throws IOException {
      final T[] value = get();
      if (value == null) {
         output.writeInt(-1);
      }
      else {
         output.writeInt(value.length);
         for (final T child : value) {
            output.write(_multiplexor.getProtocolBytes(child));
         }
      }
   }


}
