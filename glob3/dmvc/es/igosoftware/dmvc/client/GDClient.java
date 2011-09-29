

package es.igosoftware.dmvc.client;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import es.igosoftware.dmvc.GDProcess;
import es.igosoftware.dmvc.commands.client.GDAddPropertyChangeListenerResultCommand;
import es.igosoftware.dmvc.commands.client.GDEvaluationAnswerCommand;
import es.igosoftware.dmvc.commands.client.IDClientCommand;
import es.igosoftware.dmvc.commands.server.GDAddPropertyChangeListenerCommand;
import es.igosoftware.dmvc.commands.server.GDRemoteSynchronousEvaluationCommand;
import es.igosoftware.dmvc.commands.server.IDServerCommand;
import es.igosoftware.dmvc.model.GDRemoteModel;
import es.igosoftware.dmvc.model.IDAsynchronousExecutionListener;
import es.igosoftware.dmvc.model.IDModel;
import es.igosoftware.protocol.GProtocolMultiplexor;
import es.igosoftware.util.GUtils;


public class GDClient
         extends
            GDProcess {


   private final String                                                        _host;
   private final int                                                           _port;

   private final GDClientHandler                                               _handler;

   private int                                                                 _sessionID;
   private Object                                                              _rootObject;

   private final Map<Integer, WeakReference<IDModel>>                          _remoteModels                       = new HashMap<Integer, WeakReference<IDModel>>(
                                                                                                                            256);

   private final Map<Integer, GDPropertyChangeSubscriptionData>                _propertyChangeSubscriptions        = new HashMap<Integer, GDPropertyChangeSubscriptionData>();
   private int                                                                 _propertyChangeSubscriptionsCounter = 0;

   private final Map<Long, IDAsynchronousExecutionListener<Object, Exception>> _asynchronousExecutionListeners     = new HashMap<Long, IDAsynchronousExecutionListener<Object, Exception>>();


   public GDClient(final String host,
                   final int port,
                   final boolean verbose) throws IOException {
      super(verbose);
      _host = host;
      _port = port;

      _handler = start();
   }


   private GDClientHandler start() throws IOException {
      // Initialize the timer that schedules subsequent reconnection attempts.
      //      final Timer timer = new HashedWheelTimer();

      // Configure the client.
      final ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
               Executors.newCachedThreadPool(), Runtime.getRuntime().availableProcessors()));


      //      bootstrap.getPipeline().addLast("timeout", new ReadTimeoutHandler(timer, READ_TIMEOUT));

      // Set up the default event pipeline.
      final GDClientHandler handler = new GDClientHandler(this);
      bootstrap.getPipeline().addLast("handler", handler);

      final InetSocketAddress remoteAddress = new InetSocketAddress(_host, _port);
      //      bootstrap.setOption("remoteAddress", remoteAddress);

      // Start the connection attempt.
      final ChannelFuture connectionFuture = bootstrap.connect(remoteAddress);
      connectionFuture.awaitUninterruptibly();

      if (!connectionFuture.isSuccess()) {
         connectionFuture.getChannel().getCloseFuture().awaitUninterruptibly();
         bootstrap.releaseExternalResources();

         throw new IOException(connectionFuture.getCause());
      }

      return handler;
   }


   private boolean isInitialized() {
      return _rootObject != null;
   }


   public void initializeFromServer(final int sessionID,
                                    final Object rootObject) {
      if (isInitialized()) {
         throw new RuntimeException("Client already initialized");
      }

      _sessionID = sessionID;
      _rootObject = rootObject;
   }


   private void ensureInitialization() {
      if (isInitialized()) {
         return;
      }

      logInfo("Waiting for initialization from server");
      do {
         GUtils.delay(5);
      }
      while (!isInitialized());
      logInfo("Initialization done!");
   }


   public Object getRootObject() {
      ensureInitialization();

      return _rootObject;
   }


   public int getSessionID() {
      ensureInitialization();

      return _sessionID;
   }


   public IDModel materializeInClient(final GDRemoteModel remoteModel) {
      final Integer modelID = remoteModel.getModelID();
      synchronized (_remoteModels) {
         final WeakReference<IDModel> currentRemoteModel = _remoteModels.get(modelID);
         if ((currentRemoteModel == null) || (currentRemoteModel.get() == null)) {
            final IDModel remoteModelMaterializedInClient = remoteModel.justMaterializedInClient(this);

            _remoteModels.put(modelID, new WeakReference<IDModel>(remoteModelMaterializedInClient));

            return remoteModelMaterializedInClient;
         }

         return currentRemoteModel.get();
      }
   }


   public void sendCommand(final Channel channel,
                           final IDServerCommand command) {
      _handler.sendCommand(channel, command);
   }


   public GDEvaluationAnswerCommand waitForAnswer(final GDRemoteSynchronousEvaluationCommand command) {
      return _handler.waitForAnswer(command);
   }


   public GDAddPropertyChangeListenerResultCommand waitForAnswer(final GDAddPropertyChangeListenerCommand command) {
      return _handler.waitForAnswer(command);
   }


   public int registerPropertyChangeListener(final GDPropertyChangeSubscriptionData propertyChangeSubscription) {
      synchronized (_propertyChangeSubscriptions) {
         final int listenerID = _propertyChangeSubscriptionsCounter++;
         _propertyChangeSubscriptions.put(listenerID, propertyChangeSubscription);
         return listenerID;
      }
   }


   public GDPropertyChangeSubscriptionData getPropertyChangeListener(final int subscriptionID) {
      synchronized (_propertyChangeSubscriptions) {
         return _propertyChangeSubscriptions.get(subscriptionID);
      }
   }


   public int removePropertyChangeListener(final GDPropertyChangeSubscriptionData propertyChangeSubscriptionData) {
      synchronized (_propertyChangeSubscriptions) {
         for (final Entry<Integer, GDPropertyChangeSubscriptionData> entry : _propertyChangeSubscriptions.entrySet()) {
            if (entry.getValue().equals(propertyChangeSubscriptionData)) {
               final Integer subscriptionID = entry.getKey();
               _propertyChangeSubscriptions.remove(subscriptionID);
               return subscriptionID;
            }
         }
      }

      logSevere("Can't find a subscription " + propertyChangeSubscriptionData);
      return -1;
   }


   public void registerAsynchronousExecutionListener(final long evaluationID,
                                                     final IDAsynchronousExecutionListener<Object, Exception> asyncExecutionListener) {
      synchronized (_asynchronousExecutionListeners) {
         final IDAsynchronousExecutionListener<Object, Exception> previous = _asynchronousExecutionListeners.put(evaluationID,
                  asyncExecutionListener);
         if (previous != null) {
            logWarning("Already registered an asynchronous listener with evaluationID=" + evaluationID + " " + previous);
         }
      }
   }


   public IDAsynchronousExecutionListener<Object, Exception> getAndRemoveAsynchronousExecutionListener(final long evaluationID) {
      synchronized (_asynchronousExecutionListeners) {
         return _asynchronousExecutionListeners.remove(evaluationID);
      }
   }


   @Override
   public GProtocolMultiplexor getMultiplexor() {
      return _handler.getMultiplexor();
   }


   public void logReceivedCommand(final IDClientCommand command,
                                  final SocketAddress remoteAddress) {
      _handler.logReceivedCommand(command, remoteAddress);
   }


   @Override
   public String logName() {
      return "dMVC-Client";
   }

}
