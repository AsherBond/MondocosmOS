

package es.igosoftware.dmvc;


import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import es.igosoftware.dmvc.codec.GDProtocolObjectDecoder;
import es.igosoftware.dmvc.codec.GDProtocolObjectEncoder;
import es.igosoftware.dmvc.commands.IDCommand;
import es.igosoftware.dmvc.commands.client.GDAddPropertyChangeListenerResultCommand;
import es.igosoftware.dmvc.commands.client.GDAsynchronousEvaluationResultCommand;
import es.igosoftware.dmvc.commands.client.GDCompositeClientCommand;
import es.igosoftware.dmvc.commands.client.GDEvaluationResultCommand;
import es.igosoftware.dmvc.commands.client.GDExceptionThrowerCommand;
import es.igosoftware.dmvc.commands.client.GDFragmentClientCommand;
import es.igosoftware.dmvc.commands.client.GDInitializeClientCommand;
import es.igosoftware.dmvc.commands.client.GDPropertyChangeCommand;
import es.igosoftware.dmvc.commands.server.GDAddPropertyChangeListenerCommand;
import es.igosoftware.dmvc.commands.server.GDCompositeServerCommand;
import es.igosoftware.dmvc.commands.server.GDFragmentServerCommand;
import es.igosoftware.dmvc.commands.server.GDRemoteAsynchronousEvaluationCommand;
import es.igosoftware.dmvc.commands.server.GDRemoteSynchronousEvaluationCommand;
import es.igosoftware.dmvc.commands.server.GDRemovePropertyChangeListenerCommand;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.logging.GLogger;
import es.igosoftware.logging.ILogger;
import es.igosoftware.protocol.GProtocolMultiplexor;
import es.igosoftware.util.GPair;
import es.igosoftware.util.GUtils;


@ChannelPipelineCoverage("all")
public abstract class GDHandler
         extends
            SimpleChannelUpstreamHandler {

   private static final long                              NANO_SECONDS_TO_POLL;

   static {
      NANO_SECONDS_TO_POLL = GUtils.isDevelopment() ? 10000 : 1;
   }


   private static final ILogger                           logger                     = GLogger.instance();
   private static final boolean                           LOG_COMMANDS               = true;
   private static final boolean                           LOG_FRAGMENT_COMMANDS      = false;

   //   private final AtomicLong                               _messagesCount             = new AtomicLong();
   private final BlockingQueue<GPair<Channel, IDCommand>> _commandsQueue             = new LinkedBlockingQueue<GPair<Channel, IDCommand>>();
   private final BlockingQueue<GPair<Channel, IDCommand>> _asynchronousCommandsQueue = new LinkedBlockingQueue<GPair<Channel, IDCommand>>();
   private final GProtocolMultiplexor                     _multiplexor;


   private class SendCommandsWorkerThread
            extends
               Thread {

      private SendCommandsWorkerThread() {
         setName("dMVC Send Commands Worker Thread");
         setPriority(Thread.MAX_PRIORITY);
      }


      @Override
      public void run() {
         try {
            final ArrayList<GPair<Channel, IDCommand>> queuedCommands = new ArrayList<GPair<Channel, IDCommand>>();
            final ArrayList<IDCommand> commandsInChannel = new ArrayList<IDCommand>();

            while (true) {
               final GPair<Channel, IDCommand> firstCommand = _commandsQueue.poll(NANO_SECONDS_TO_POLL, TimeUnit.NANOSECONDS);
               if (firstCommand == null) {
                  tryToSendAsynchronousCommands();
                  continue;
               }

               queuedCommands.clear();
               queuedCommands.add(firstCommand);

               Thread.sleep(2); // give some time to accumulate commands

               GPair<Channel, IDCommand> nextCommand;
               while ((nextCommand = _commandsQueue.poll()) != null) {
                  queuedCommands.add(nextCommand);
               }

               if (queuedCommands.size() == 1) {
                  // queued 1 command, just send it
                  sendCommandNow(firstCommand._first, firstCommand._second);
               }
               else {
                  // queued more than 1 command, group then by channel
                  final LinkedList<GPair<Channel, IDCommand>> toProcess = new LinkedList<GPair<Channel, IDCommand>>(
                           queuedCommands);

                  while (!toProcess.isEmpty()) {
                     commandsInChannel.clear();

                     final GPair<Channel, IDCommand> firstCommandInChannel = toProcess.removeFirst();
                     commandsInChannel.add(firstCommandInChannel._second);

                     final Iterator<GPair<Channel, IDCommand>> toProcessIterator = toProcess.iterator();
                     while (toProcessIterator.hasNext()) {
                        final GPair<Channel, IDCommand> current = toProcessIterator.next();
                        if (current._first == firstCommandInChannel._first) {
                           commandsInChannel.add(current._second);
                           toProcessIterator.remove();
                        }
                     }

                     if (commandsInChannel.size() == 1) {
                        sendCommandNow(firstCommandInChannel._first, firstCommandInChannel._second);
                     }
                     else {
                        final IDCommand composite = firstCommandInChannel._second.createComposite(commandsInChannel,
                                 getMultiplexor());
                        sendCommandNow(firstCommandInChannel._first, composite);
                     }
                  }
               }
            }
         }
         catch (final InterruptedException e) {
            logSevere(e.getLocalizedMessage(), e);
         }
      }


      private void tryToSendAsynchronousCommands() {
         final GPair<Channel, IDCommand> command = _asynchronousCommandsQueue.poll();
         if (command != null) {
            sendCommandNow(command._first, command._second);
         }
      }


      private void sendCommandNow(final Channel chanel,
                                  final IDCommand command) {
         //         if (LOG_COMMANDS) {
         //            final int sizeInBytes = getSerializedSize(command);
         //            logInfo("Sending: " + command + " (" + sizeInBytes + "b) to " + chanel.getRemoteAddress());
         //         }
         logSendingCommand(command, chanel.getRemoteAddress());
         chanel.write(command);
      }
   }


   protected GDHandler() {
      logInfo("Creating Handler " + getClass());
      _multiplexor = initializeMultiplexor();

      new SendCommandsWorkerThread().start();
   }


   //   public long getMessagesCount() {
   //      return _messagesCount.get();
   //   }


   @SuppressWarnings("unchecked")
   private GProtocolMultiplexor initializeMultiplexor() {
      final GProtocolMultiplexor multiplexor = new GProtocolMultiplexor(GDAddPropertyChangeListenerResultCommand.class,
               GDAsynchronousEvaluationResultCommand.class, GDCompositeClientCommand.class, GDEvaluationResultCommand.class,
               GDExceptionThrowerCommand.class, GDFragmentClientCommand.class, GDInitializeClientCommand.class,
               GDPropertyChangeCommand.class, GDAddPropertyChangeListenerCommand.class, GDCompositeServerCommand.class,
               GDFragmentServerCommand.class, GDRemoteAsynchronousEvaluationCommand.class,
               GDRemoteSynchronousEvaluationCommand.class, GDRemovePropertyChangeListenerCommand.class);

      return multiplexor;
   }


   public void sendCommand(final Channel channel,
                           final IDCommand command) {
      //      e.getChannel().write(command);
      _commandsQueue.add(new GPair<Channel, IDCommand>(channel, command));
   }


   public void sendAsynchronousCommand(final Channel channel,
                                       final IDCommand command) {
      try {
         logSendingCommand(command, channel.getLocalAddress());

         final List<IDCommand> fragmentsCommands = command.createFragmentsCommands(_multiplexor);
         for (final IDCommand fragmentCommand : fragmentsCommands) {
            _asynchronousCommandsQueue.add(new GPair<Channel, IDCommand>(channel, fragmentCommand));
         }
      }
      catch (final IOException e) {
         logSevere("Exception while creating asynchronous commands", e);
      }
   }


   @Override
   public final void channelOpen(final ChannelHandlerContext ctx,
                                 final ChannelStateEvent e) throws Exception {
      final ChannelPipeline pipeline = e.getChannel().getPipeline();
      //      pipeline.addFirst("encoder", new GDZipObjectEncoder());
      //      pipeline.addFirst("decoder", new GDZipObjectDecoder(512 * 1024));

      // pipeline.addFirst("encoder", new ObjectEncoder());
      // pipeline.addFirst("decoder", new ObjectDecoder(512 * 1024));

      pipeline.addFirst("encoder", new GDProtocolObjectEncoder(_multiplexor));
      pipeline.addFirst("decoder", new GDProtocolObjectDecoder(512 * 1024, _multiplexor));
   }


   @Override
   public void channelConnected(final ChannelHandlerContext ctx,
                                final ChannelStateEvent e) {
      logInfo("Connection from " + e.getChannel().getRemoteAddress());
   }


   @Override
   public void messageReceived(final ChannelHandlerContext ctx,
                               final MessageEvent e) {
      //      _messagesCount.incrementAndGet();
      final IDCommand command = (IDCommand) e.getMessage();
      logReceivedCommand(command, e.getRemoteAddress());
   }


   public void logReceivedCommand(final IDCommand command,
                                  final SocketAddress socketAddress) {
      if (LOG_COMMANDS) {
         if (!command.isFragmentCommand() || LOG_FRAGMENT_COMMANDS) {
            final String sizeInBytes = getSerializedSize(command);
            logInfo("Received: " + command + " (" + sizeInBytes + ") from " + socketAddress);
         }
      }
   }


   public void logSendingCommand(final IDCommand command,
                                 final SocketAddress socketAddress) {
      if (LOG_COMMANDS) {
         if (!command.isFragmentCommand() || LOG_FRAGMENT_COMMANDS) {
            final String sizeInBytes = getSerializedSize(command);
            logInfo("Sending: " + command + " (" + sizeInBytes + ") from " + socketAddress);
         }
      }
   }


   private String getSerializedSize(final IDCommand command) {
      final byte[] rawBytes = _multiplexor.getProtocolBytes(command);

      final int rawSize = rawBytes.length;
      final int compressedSize = GIOUtils.compress(rawBytes).length;

      if (compressedSize < rawSize) {
         return (compressedSize + 4) + "bytes (compressed from " + (rawSize + 4) + "bytes)";
      }
      return (rawSize + 4) + "bytes";
   }


   @Override
   public void handleUpstream(final ChannelHandlerContext ctx,
                              final ChannelEvent e) throws Exception {
      //      if (!((e instanceof ChannelStateEvent) && (((ChannelStateEvent) e).getState() != ChannelState.INTEREST_OPS))) {
      //         logInfo("handleUpstream: " + e);
      //      }

      //      if ((e instanceof ChannelStateEvent) && (((ChannelStateEvent) e).getState() != ChannelState.INTEREST_OPS)) {
      //         logInfo(e.toString());
      //      }

      super.handleUpstream(ctx, e);
   }


   @Override
   public final void exceptionCaught(final ChannelHandlerContext ctx,
                                     final ExceptionEvent e) {
      logSevere("Unexpected exception from downstream.", e.getCause());
      closeChannel(e.getChannel());
   }


   protected final void closeChannel(final Channel channel) {
      logInfo("Closing " + channel);
      channel.close();
   }


   @Override
   public void channelClosed(final ChannelHandlerContext ctx,
                             final ChannelStateEvent e) throws Exception {
      super.channelClosed(ctx, e);
   }


   public final void logInfo(final String msg) {
      logger.logInfo("dMVC: " + msg);
   }


   public final void logWarning(final String msg) {
      logger.logWarning("dMVC: " + msg);
   }


   public final void logSevere(final String msg,
                               final Throwable e) {
      logger.logSevere("dMVC: " + msg, e);
   }


   public final void logSevere(final String msg) {
      logger.logSevere("dMVC: " + msg);
   }


   public GProtocolMultiplexor getMultiplexor() {
      return _multiplexor;
   }
}
