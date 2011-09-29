

package es.igosoftware.dmvc.commands.server;

import java.util.Arrays;
import java.util.List;

import org.jboss.netty.channel.Channel;

import es.igosoftware.dmvc.server.GDServer;
import es.igosoftware.protocol.GArrayProtocolField;
import es.igosoftware.protocol.GProtocolMultiplexor;
import es.igosoftware.protocol.IProtocolField;


public final class GDCompositeServerCommand
         extends
            GDAbstractServerCommand {

   private static final long                          serialVersionUID = 1L;


   private final GArrayProtocolField<IDServerCommand> _children        = new GArrayProtocolField<IDServerCommand>(false) {
                                                                          @Override
                                                                          protected IDServerCommand[] createArray(final int length) {
                                                                             return new IDServerCommand[length];
                                                                          }
                                                                       };


   //   private final GProtocolMultiplexor                 _multiplexor;


   public GDCompositeServerCommand(final GProtocolMultiplexor multiplexor) {
      //      _multiplexor = multiplexor;
      _children.setMultiplexor(multiplexor);
   }


   public GDCompositeServerCommand(final List<IDServerCommand> children,
                                   final GProtocolMultiplexor multiplexor) {
      _children.set(children.toArray(new IDServerCommand[0]));
      //      _multiplexor = multiplexor;
      _children.setMultiplexor(multiplexor);
   }


   @Override
   public void evaluateInServer(final Channel channel,
                                final GDServer server) {
      //      for (final IDServerCommand child : _children) {
      //         child.evaluateInServer(channel, server);
      //      }
      throw new RuntimeException("Can't directly evaluate a composite");
   }


   @Override
   public String toString() {
      return "CompositeServer " + Arrays.toString(_children.get());
   }


   @Override
   public boolean isComposite() {
      return true;
   }


   public IDServerCommand[] getChildren() {
      return _children.get();
   }


   @Override
   protected IProtocolField[] getProtocolFields() {
      return new IProtocolField[] {
         _children
      };
   }

}
