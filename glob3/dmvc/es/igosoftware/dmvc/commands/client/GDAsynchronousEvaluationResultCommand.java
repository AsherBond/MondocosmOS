

package es.igosoftware.dmvc.commands.client;

import org.jboss.netty.channel.Channel;

import es.igosoftware.dmvc.GDSerialization;
import es.igosoftware.dmvc.GDUtils;
import es.igosoftware.dmvc.client.GDClient;
import es.igosoftware.dmvc.model.IDAsynchronousExecutionListener;
import es.igosoftware.dmvc.server.GDServer;
import es.igosoftware.protocol.GIntProtocolField;
import es.igosoftware.protocol.GObjectProtocolField;
import es.igosoftware.protocol.IProtocolField;


public final class GDAsynchronousEvaluationResultCommand
         extends
            GDAbstractClientCommand {

   private static final long                     serialVersionUID = 1L;


   private final GIntProtocolField               _evaluationID    = new GIntProtocolField(false);
   private final GObjectProtocolField<Object>    _result          = new GObjectProtocolField<Object>();
   private final GObjectProtocolField<Exception> _exception       = new GObjectProtocolField<Exception>();


   public GDAsynchronousEvaluationResultCommand() {
   }


   public GDAsynchronousEvaluationResultCommand(final Object result,
                                                final Exception exception,
                                                final int evaluationID,
                                                final GDServer server) {
      _evaluationID.set(evaluationID);
      _result.set(GDSerialization.objectToSerialize(result, server));
      _exception.set(exception);
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
   public void evaluateInClient(final Channel channel,
                                final GDClient client) {
      final IDAsynchronousExecutionListener<Object, Exception> listener = client.getAndRemoveAsynchronousExecutionListener(_evaluationID.get());
      // No need to check for null because the next line will throw a NullPointerException

      final Object materializedResult = GDSerialization.materializeInClient(_result.get(), channel, client);
      final Exception materializedException = (Exception) GDSerialization.materializeInClient(_exception.get(), channel, client);

      new Thread() {
         @Override
         public void run() {
            listener.evaluated(materializedResult, materializedException);
         }
      }.start();
      // listener.evaluated(materializedResult, materializedException);
   }


   @Override
   protected IProtocolField[] getProtocolFields() {
      return new IProtocolField[] {
                        _evaluationID,
                        _result,
                        _exception
      };
   }


   @Override
   public String toString() {
      return "AsynchronousEvaluationResultCommand [evaluationID=" + _evaluationID.get() + ", result="
             + GDUtils.resultString(_result.get()) + ", exception=" + _exception.get() + "]";
   }


}
