

package es.igosoftware.dmvc.commands.server;

import java.lang.reflect.Method;

import org.jboss.netty.channel.Channel;

import es.igosoftware.dmvc.GDSerialization;
import es.igosoftware.dmvc.GDUtils;
import es.igosoftware.dmvc.commands.client.GDEvaluationAnswerCommand;
import es.igosoftware.dmvc.commands.client.GDEvaluationResultCommand;
import es.igosoftware.dmvc.commands.client.GDExceptionThrowerCommand;
import es.igosoftware.dmvc.commands.client.IDClientCommand;
import es.igosoftware.dmvc.model.GDModel;
import es.igosoftware.dmvc.server.GDServer;
import es.igosoftware.util.GCollections;


public final class GDRemoteSynchronousEvaluationCommand
         extends
            GDRemoteEvaluationCommand {

   private static final long serialVersionUID = 1L;


   public GDRemoteSynchronousEvaluationCommand() {
   }


   public GDRemoteSynchronousEvaluationCommand(final int modelID,
                                               final int methodID,
                                               final Object[] args) {
      super(modelID, //
            methodID, //
            args);
   }


   @Override
   public void evaluateInServer(final Channel channel,
                                final GDServer server) {
      final IDClientCommand command = getEvaluationResultCommand(channel, server);

      // send the result immediately
      server.sendCommand(channel, command);
   }


   private final IDClientCommand getEvaluationResultCommand(final Channel channel,
                                                            final GDServer server) {
      final Integer evaluationID = _evaluationID.get();
      final Integer modelID = _modelID.get();

      final GDModel model = server.getModelByID(modelID);
      if (model == null) {
         final RuntimeException e = new RuntimeException("Invalid modelID (" + modelID + ")");
         return new GDExceptionThrowerCommand(e, evaluationID, server);
      }


      Method method = null;
      Object[] materializedArgs = null;
      try {
         method = GDUtils.getMethod(model.getClass(), _methodID.get());
         materializedArgs = (Object[]) GDSerialization.materializeInServer(_args.get(), channel, server);

         final Object result = method.invoke(model, materializedArgs);

         server.evaluated(channel, model, method, materializedArgs, result, null);

         return new GDEvaluationResultCommand(result, evaluationID, server);
      }
      catch (final Exception e) {
         server.evaluated(channel, model, method, materializedArgs, null, e);

         final RuntimeException evaluationException = new RuntimeException(toString(), e);
         return new GDExceptionThrowerCommand(evaluationException, evaluationID, server);
      }
   }


   @Override
   public String toString() {
      return "SynchronousRemoteEvaluation [modelID=" + _modelID.get() + ", methodID=" + _methodID.get() + ", evaluationID="
             + _evaluationID.get() + ", args=" + GCollections.toString(_args.get()) + "]";
   }


   public boolean isYourAnswer(final GDEvaluationAnswerCommand answer) {
      return (_evaluationID.get() == answer.getEvaluationID());
   }


}
