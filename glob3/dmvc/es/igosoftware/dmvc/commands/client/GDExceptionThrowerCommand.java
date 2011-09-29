

package es.igosoftware.dmvc.commands.client;

import org.jboss.netty.channel.Channel;

import es.igosoftware.dmvc.client.GDClient;
import es.igosoftware.dmvc.server.GDServer;
import es.igosoftware.protocol.GObjectProtocolField;
import es.igosoftware.protocol.IProtocolField;
import es.igosoftware.util.GCollections;


public final class GDExceptionThrowerCommand
         extends
            GDEvaluationAnswerCommand {


   private static final long                     serialVersionUID = 1L;


   private final GObjectProtocolField<Exception> _exception       = new GObjectProtocolField<Exception>();


   public GDExceptionThrowerCommand() {
   }


   public GDExceptionThrowerCommand(final Exception exception,
                                    final int evaluationID,
                                    @SuppressWarnings("unused") final GDServer server) {
      super(evaluationID);
      _exception.set(exception);
   }


   @Override
   public void evaluateInClient(final Channel channel,
                                final GDClient client) {

   }


   @Override
   public String toString() {
      return "ExceptionThrower [exception=" + _exception.get() + ", evaluationID=" + getEvaluationID() + "]";
   }


   @Override
   public Object evaluationResult() throws Exception {
      throw _exception.get();
   }


   @Override
   public boolean isComposite() {
      return false;
   }


   @Override
   protected IProtocolField[] getProtocolFields() {
      return GCollections.concatenate(super.getProtocolFields(), new IProtocolField[] {
         _exception
      });
   }

}
