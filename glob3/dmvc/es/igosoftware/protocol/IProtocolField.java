

package es.igosoftware.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public interface IProtocolField<T>
         extends
            Cloneable {


   public void set(final T value);


   public T get();


   public void read(final DataInputStream input) throws IOException;


   public void write(final DataOutputStream output) throws IOException;


   public IProtocolField<T> clone();

}
