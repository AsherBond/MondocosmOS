

package es.igosoftware.dmvc.commands.server;

import org.jboss.netty.channel.Channel;

import es.igosoftware.dmvc.server.GDServer;
import es.igosoftware.protocol.GBooleanProtocolField;
import es.igosoftware.protocol.GByteArrayProtocolField;
import es.igosoftware.protocol.GIntProtocolField;
import es.igosoftware.protocol.IProtocolField;


public final class GDFragmentServerCommand
         extends
            GDAbstractServerCommand {

   private static final long             serialVersionUID = 1L;


   private final GIntProtocolField       _groupID         = new GIntProtocolField(false);
   private final GByteArrayProtocolField _bytes           = new GByteArrayProtocolField();
   private final GBooleanProtocolField   _isLastInGroup   = new GBooleanProtocolField(false);


   public GDFragmentServerCommand() {
   }


   public GDFragmentServerCommand(final int groupID,
                                  final byte[] bytes,
                                  final boolean isLastInGroup) {
      _groupID.set(groupID);
      _bytes.set(bytes);
      _isLastInGroup.set(isLastInGroup);
   }


   @Override
   public boolean isFragmentCommand() {
      return true;
   }


   @Override
   public boolean isComposite() {
      return false;
   }


   @Override
   public String toString() {
      return "FragmentClientCommand [groupID=" + _groupID.get() + ", isLastInGroup=" + _isLastInGroup.get() + ", bytes="
             + _bytes.get().length + "]";
   }


   @Override
   public void evaluateInServer(final Channel channel,
                                final GDServer server) {
      final int groupID = _groupID.get();
      final byte[] accumulated = server.accumulateFragment(groupID, _bytes.get());
      if (_isLastInGroup.get()) {
         server.removeAccumulation(groupID);

         try {
            final IDServerCommand command = (IDServerCommand) server.getMultiplexor().createObject(accumulated);

            server.logReceivedCommand(command, channel.getRemoteAddress());

            command.evaluateInServer(channel, server);
         }
         catch (final Exception e) {
            server.logSevere(e);
         }
      }
   }


   @Override
   protected IProtocolField[] getProtocolFields() {
      return new IProtocolField[] {
                        _groupID,
                        _bytes,
                        _isLastInGroup
      };
   }


}
