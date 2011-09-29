

package es.igosoftware.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public abstract class GProtocolField<T>
         implements
            IProtocolField<T> {


   private final boolean _isNullable;
   private T             _value;


   protected GProtocolField(final boolean isNullable) {
      _isNullable = isNullable;
   }


   @Override
   public final T get() {
      return _value;
   }


   @Override
   public final void set(final T value) {
      _value = value;
   }


   @Override
   public final void read(final DataInputStream input) throws IOException {
      if (_isNullable) {
         final boolean isNull = input.readBoolean();
         if (isNull) {
            set(null);
         }
         else {
            doRead(input);
         }
      }
      else {
         doRead(input);
      }
   }


   protected abstract void doRead(final DataInputStream input) throws IOException;


   @Override
   public final void write(final DataOutputStream output) throws IOException {
      if (_isNullable) {
         final T value = get();
         if (value == null) {
            output.writeBoolean(true); // is null flag
         }
         else {
            output.writeBoolean(false);// is null flag
            doWrite(output);
         }
      }
      else {
         doWrite(output);
      }
   }


   @SuppressWarnings("unchecked")
   @Override
   public GProtocolField<T> clone() {
      try {
         return (GProtocolField<T>) super.clone();
      }
      catch (final CloneNotSupportedException e) {
         throw new RuntimeException(e);
      }
   }


   protected abstract void doWrite(final DataOutputStream output) throws IOException;

}
