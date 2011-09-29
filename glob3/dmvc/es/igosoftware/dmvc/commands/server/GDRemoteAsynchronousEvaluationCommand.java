

package es.igosoftware.dmvc.commands.server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

import org.jboss.netty.channel.Channel;

import es.igosoftware.dmvc.GDSerialization;
import es.igosoftware.dmvc.GDUtils;
import es.igosoftware.dmvc.commands.client.GDAsynchronousEvaluationResultCommand;
import es.igosoftware.dmvc.commands.client.IDClientCommand;
import es.igosoftware.dmvc.model.GDModel;
import es.igosoftware.dmvc.model.IDAsynchronousExecutionListener;
import es.igosoftware.dmvc.server.GDServer;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GHolder;
import es.igosoftware.util.GPair;


public final class GDRemoteAsynchronousEvaluationCommand
         extends
            GDRemoteEvaluationCommand {

   private static final long serialVersionUID = 1L;


   public GDRemoteAsynchronousEvaluationCommand() {
   }


   public GDRemoteAsynchronousEvaluationCommand(final int modelID,
                                                final int methodID,
                                                final Object[] args) {
      super(modelID, //
            methodID, //
            Arrays.copyOf(args, args.length - 1)); // don't send the last argument as it is the listener 
   }


   @Override
   public void evaluateInServer(final Channel channel,
                                final GDServer server) {
      // TODO: Use a executor to make it really Asynchronous ?

      final IDClientCommand command = getEvaluationResultCommand(channel, server);

      // send the result asynchronously
      server.sendAsynchronousCommand(channel, command);
   }


   private final IDClientCommand getEvaluationResultCommand(final Channel channel,
                                                            final GDServer server) {
      final Integer modelID = _modelID.get();
      final Integer evaluationID = _evaluationID.get();

      final GDModel model = server.getModelByID(modelID);
      if (model == null) {
         final RuntimeException e = new RuntimeException("Invalid modelID (" + modelID + ")");
         return new GDAsynchronousEvaluationResultCommand(null, e, evaluationID, server);
      }


      Method method = null;
      Object[] materializedArgs = null;
      try {
         method = GDUtils.getMethod(model.getClass(), _methodID.get());
         materializedArgs = (Object[]) GDSerialization.materializeInServer(_args.get(), channel, server);

         final GPair<Object, Exception> resultPair = executeAsynchronousMethod(model, method, materializedArgs);
         final Object result = resultPair._first;
         final Exception exception = resultPair._second;

         server.evaluated(channel, model, method, materializedArgs, result, exception);

         final RuntimeException evaluationException = (exception == null) ? null : new RuntimeException(toString(), exception);
         return new GDAsynchronousEvaluationResultCommand(result, evaluationException, evaluationID, server);
      }
      catch (final Exception e) {
         server.logSevere("Exception while evaluating " + this, e);
         server.evaluated(channel, model, method, materializedArgs, null, e);

         final RuntimeException evaluationException = new RuntimeException(toString(), e);
         return new GDAsynchronousEvaluationResultCommand(null, evaluationException, evaluationID, server);
      }
   }


   private GPair<Object, Exception> executeAsynchronousMethod(final GDModel model,
                                                              final Method method,
                                                              final Object[] materializedArgs) throws IllegalAccessException,
                                                                                              InvocationTargetException,
                                                                                              InterruptedException {
      final GHolder<GPair<Object, Exception>> resultHolder = new GHolder<GPair<Object, Exception>>(null);
      final Semaphore listenerEvaluatedSemaphore = new Semaphore(0);

      final IDAsynchronousExecutionListener<Object, Exception> listener = new IDAsynchronousExecutionListener<Object, Exception>() {
         @Override
         public void evaluated(final Object result,
                               final Exception exception) {
            resultHolder.set(new GPair<Object, Exception>(result, exception));
            listenerEvaluatedSemaphore.release();
         }
      };

      final Object[] argsAndListener = Arrays.copyOf(materializedArgs, materializedArgs.length + 1);
      argsAndListener[argsAndListener.length - 1] = listener;

      method.invoke(model, argsAndListener);

      listenerEvaluatedSemaphore.acquire();

      return resultHolder.get();
   }


   @Override
   public String toString() {
      return "AsynchronousRemoteEvaluation [modelID=" + _modelID.get() + ", methodID=" + _methodID.get() + ", evaluationID="
             + _evaluationID.get() + ", args=" + GCollections.toString(_args.get()) + "]";
   }


}
