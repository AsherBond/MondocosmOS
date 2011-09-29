

package es.igosoftware.dmvc.commands;

import java.io.IOException;
import java.util.List;

import es.igosoftware.protocol.GProtocolMultiplexor;
import es.igosoftware.protocol.IProtocolObject;


public interface IDCommand
         extends
            IProtocolObject {


   public static final int FRAGMENT_SIZE = 1024;


   public IDCommand createComposite(final List<IDCommand> commands,
                                    final GProtocolMultiplexor multiplexor);


   public boolean isFragmentCommand();


   public boolean isComposite();


   public List<IDCommand> createFragmentsCommands(final GProtocolMultiplexor multiplexor) throws IOException;


}
