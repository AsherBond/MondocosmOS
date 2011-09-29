

package es.igosoftware.protocol;

import java.io.DataInputStream;


public interface IProtocolObject {


   public void initializeFromProtocolBytes(final byte[] bytes,
                                           final int skipBytes);


   public void initializeFromProtocolStream(final DataInputStream input);


   public byte[] getProtocolBytes();


}
