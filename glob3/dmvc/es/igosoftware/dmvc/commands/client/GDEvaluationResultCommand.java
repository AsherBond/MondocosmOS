

package es.igosoftware.dmvc.commands.client;

import org.jboss.netty.channel.Channel;

import es.igosoftware.dmvc.GDSerialization;
import es.igosoftware.dmvc.GDUtils;
import es.igosoftware.dmvc.client.GDClient;
import es.igosoftware.dmvc.server.GDServer;
import es.igosoftware.protocol.GObjectProtocolField;
import es.igosoftware.protocol.IProtocolField;
import es.igosoftware.util.GCollections;


public final class GDEvaluationResultCommand
         extends
            GDEvaluationAnswerCommand {

   private static final long                  serialVersionUID = 1L;


   private final GObjectProtocolField<Object> _result          = new GObjectProtocolField<Object>();

   transient private Object                   _materializedResult;


   public GDEvaluationResultCommand() {
   }


   public GDEvaluationResultCommand(final Object result,
                                    final int evaluationID,
                                    final GDServer server) {
      super(evaluationID);
      _result.set(GDSerialization.objectToSerialize(result, server));
   }


   @Override
   public void evaluateInClient(final Channel channel,
                                final GDClient client) {
      _materializedResult = GDSerialization.materializeInClient(_result.get(), channel, client);
   }


   @Override
   public Object evaluationResult() {
      return _materializedResult;
   }


   @Override
   public boolean isComposite() {
      return false;
   }


   @Override
   public String toString() {
      return "EvaluationResult [result=" + GDUtils.resultString(_result.get()) + ", evaluationID=" + getEvaluationID() + "]";
   }


   @Override
   protected IProtocolField[] getProtocolFields() {
      return GCollections.concatenate(super.getProtocolFields(), new IProtocolField[] {
         _result
      });
   }


}
