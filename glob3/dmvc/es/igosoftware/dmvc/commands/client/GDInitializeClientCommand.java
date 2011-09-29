

package es.igosoftware.dmvc.commands.client;

import org.jboss.netty.channel.Channel;

import es.igosoftware.dmvc.GDSerialization;
import es.igosoftware.dmvc.client.GDClient;
import es.igosoftware.dmvc.server.GDServer;
import es.igosoftware.protocol.GIntProtocolField;
import es.igosoftware.protocol.GObjectProtocolField;
import es.igosoftware.protocol.IProtocolField;
import es.igosoftware.util.GUtils;


public final class GDInitializeClientCommand
         extends
            GDAbstractClientCommand {

   private static final long                  serialVersionUID = 1L;

   private final GIntProtocolField            _sessionID       = new GIntProtocolField(false);
   private final GObjectProtocolField<Object> _rootObject      = new GObjectProtocolField<Object>();


   public GDInitializeClientCommand() {
   }


   public GDInitializeClientCommand(final int sessionID,
                                    final GDServer server) {
      _sessionID.set(sessionID);
      _rootObject.set(GDSerialization.objectToSerialize(server.getRootObject(), server));
   }


   @Override
   public void evaluateInClient(final Channel channel,
                                final GDClient client) {
      final Object materialized = GDSerialization.materializeInClient(_rootObject.get(), channel, client);
      client.initializeFromServer(_sessionID.get(), materialized);
   }


   @Override
   public String toString() {
      return "InitializeClient [sessionID=" + _sessionID.get() + ", rootObject=" + GUtils.toString(_rootObject.get()) + "]";
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
   protected IProtocolField[] getProtocolFields() {
      return new IProtocolField[] {
                        _sessionID,
                        _rootObject
      };
   }

}
