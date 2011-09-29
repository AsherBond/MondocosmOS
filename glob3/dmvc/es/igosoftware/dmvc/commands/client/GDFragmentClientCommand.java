

package es.igosoftware.dmvc.commands.client;

import org.jboss.netty.channel.Channel;

import es.igosoftware.dmvc.client.GDClient;
import es.igosoftware.protocol.GBooleanProtocolField;
import es.igosoftware.protocol.GByteArrayProtocolField;
import es.igosoftware.protocol.GIntProtocolField;
import es.igosoftware.protocol.IProtocolField;


public final class GDFragmentClientCommand
         extends
            GDAbstractClientCommand {

   private static final long             serialVersionUID = 1L;


   private final GIntProtocolField       _groupID         = new GIntProtocolField(false);
   private final GByteArrayProtocolField _bytes           = new GByteArrayProtocolField();
   private final GBooleanProtocolField   _isLastInGroup   = new GBooleanProtocolField(false);


   public GDFragmentClientCommand() {
   }


   public GDFragmentClientCommand(final int groupID,
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
   public boolean isSynchronousEvaluationAnswer() {
      return false;
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
   public void evaluateInClient(final Channel channel,
                                final GDClient client) {
      final int groupID = _groupID.get();
      final byte[] accumulated = client.accumulateFragment(groupID, _bytes.get());
      if (_isLastInGroup.get()) {
         client.removeAccumulation(groupID);

         try {
            final IDClientCommand command = (IDClientCommand) client.getMultiplexor().createObject(accumulated);

            client.logReceivedCommand(command, channel.getRemoteAddress());

            command.evaluateInClient(channel, client);
         }
         catch (final Exception e) {
            client.logSevere(e);
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
