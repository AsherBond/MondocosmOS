

package es.igosoftware.dmvc.server;

import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import es.igosoftware.dmvc.GDProcess;
import es.igosoftware.dmvc.GDSerialization;
import es.igosoftware.dmvc.GDUtils;
import es.igosoftware.dmvc.commands.client.IDClientCommand;
import es.igosoftware.dmvc.commands.server.IDServerCommand;
import es.igosoftware.dmvc.model.GDModel;
import es.igosoftware.dmvc.model.GDRemoteModel;
import es.igosoftware.dmvc.model.IDModel;
import es.igosoftware.dmvc.model.IDProperty;
import es.igosoftware.logging.GLogger;
import es.igosoftware.logging.ILogger;
import es.igosoftware.protocol.GProtocolMultiplexor;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GPair;


public class GDServer
         extends
            GDProcess {

   private static final ILogger                                             logger                   = GLogger.instance();


   private final int                                                        _port;
   private final Object                                                     _rootObject;
   private final GDServerHandler                                            _handler;

   // ID Calculation stuff
   private int                                                              _idCounter               = 0;
   private final Map<GDModel, Integer>                                      _modelID                 = new HashMap<GDModel, Integer>();
   private final Map<Integer, GDModel>                                      _idModel                 = new HashMap<Integer, GDModel>();
   //   private final Random                                                     _random                  = new Random();

   final private Map<PropertyChangeListenerKey, PropertyChangeListenerData> _propertyChangeListeners = new HashMap<PropertyChangeListenerKey, PropertyChangeListenerData>();

   private final IDEvaluationListener[]                                     _evaluationListeners;


   public GDServer(final int port,
                   final Object rootObject,
                   final boolean verbose) {
      this(port, rootObject, new IDEvaluationListener[0], verbose);
   }


   public GDServer(final int port,
                   final Object rootObject,
                   final IDEvaluationListener[] evaluationListeners,
                   final boolean verbose) {
      super(verbose);
      GAssert.isPositive(port, "port");
      GAssert.notNull(rootObject, "rootObject");

      _port = port;
      _rootObject = rootObject;
      _evaluationListeners = Arrays.copyOf(evaluationListeners, evaluationListeners.length);

      _handler = start();
   }


   private GDServerHandler start() {
      logger.logInfo("Starting server at port " + _port);

      // Configure the server.
      final ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
               Executors.newCachedThreadPool(), Runtime.getRuntime().availableProcessors()));

      // Set up the default event pipeline.
      final GDServerHandler handler = new GDServerHandler(this);
      bootstrap.getPipeline().addLast("handler", handler);

      // Bind and start to accept incoming connections.
      bootstrap.bind(new InetSocketAddress(_port));

      return handler;
   }


   public final Object getRootObject() {
      return _rootObject;
   }


   public final int getModelID(final GDModel model) {
      synchronized (_modelID) {
         final Integer id = _modelID.get(model);
         if (id != null) {
            return id;
         }

         final int newID = calculateNewID();
         _modelID.put(model, newID);
         synchronized (_idModel) {
            _idModel.put(newID, model);
         }
         return newID;
      }
   }


   private int calculateNewID() {
      //      _idCounter++;
      //      final long randomID = _random.nextInt();
      //      return (randomID << 32) | _idCounter;
      return _idCounter++;
   }


   public final GDModel getModelByID(final int id) {
      synchronized (_idModel) {
         return _idModel.get(id);
      }
   }


   public final GDModel getModelByID(final Integer id) {
      synchronized (_idModel) {
         return _idModel.get(id);
      }
   }


   @SuppressWarnings("unchecked")
   public final GDRemoteModel getRemoteModel(final GDModel model) {
      // TODO: Consider recycle previous created RemoteModel

      final Class<? extends IDModel> modelInterface = GDUtils.getModelInterface(model.getClass());

      final List<IDProperty> aspects = (List<IDProperty>) GDSerialization.objectToSerialize(model.getProperties(), this);
      return new GDRemoteModel(getModelID(model), modelInterface, aspects);
   }


   public void sendCommand(final Channel channel,
                           final IDClientCommand command) {
      _handler.sendCommand(channel, command);
   }


   public void sendAsynchronousCommand(final Channel channel,
                                       final IDClientCommand command) {
      _handler.sendAsynchronousCommand(channel, command);
   }


   @Override
   public boolean logVerbose() {
      return true;
   }


   private static class PropertyChangeListenerKey {
      private final int _sessionID;
      private final int _subscriptionID;


      public PropertyChangeListenerKey(final int sessionID,
                                       final int subscriptionID) {
         _sessionID = sessionID;
         _subscriptionID = subscriptionID;
      }


      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + _sessionID;
         result = prime * result + _subscriptionID;
         return result;
      }


      @Override
      public boolean equals(final Object obj) {
         if (this == obj) {
            return true;
         }
         if (obj == null) {
            return false;
         }
         if (getClass() != obj.getClass()) {
            return false;
         }
         final PropertyChangeListenerKey other = (PropertyChangeListenerKey) obj;
         if (_sessionID != other._sessionID) {
            return false;
         }
         if (_subscriptionID != other._subscriptionID) {
            return false;
         }
         return true;
      }


      @Override
      public String toString() {
         return "PropertyChangeListener [sessionID=" + _sessionID + ", subscriptionID=" + _subscriptionID + "]";
      }

   }

   private static class PropertyChangeListenerData {
      private final GDModel                _model;
      private final String                 _propertyName;
      private final PropertyChangeListener _listener;


      private PropertyChangeListenerData(final GDModel model,
                                         final String propertyName,
                                         final PropertyChangeListener listener) {
         _model = model;
         _propertyName = propertyName;
         _listener = listener;
      }

   }


   public void registerPropertyChangeListener(final int sessionID,
                                              final int subscriptionID,
                                              final GDModel model,
                                              final String propertyName,
                                              final PropertyChangeListener listener) {
      synchronized (_propertyChangeListeners) {
         final PropertyChangeListenerKey key = new PropertyChangeListenerKey(sessionID, subscriptionID);
         if (_propertyChangeListeners.containsKey(key)) {
            logWarning("Listener already registered " + _propertyChangeListeners.get(key));
         }

         _propertyChangeListeners.put(key, new PropertyChangeListenerData(model, propertyName, listener));
      }
   }


   //   public PropertyChangeListener unregisterPropertyChangeListener(final int sessionID,
   //                                                                  final int subscriptionID) {
   //      synchronized (_propertyChangeListeners) {
   //         final PropertyChangeListenerKey key = new PropertyChangeListenerKey(sessionID, subscriptionID);
   //
   //         final PropertyChangeListenerData listenerData = _propertyChangeListeners.remove(key);
   //         if (listenerData == null) {
   //            logWarning("Can't find listener for " + key);
   //            return null;
   //         }
   //
   //         return listenerData._listener;
   //      }
   //   }

   public GPair<String, PropertyChangeListener> unregisterPropertyChangeListener(final int sessionID,
                                                                                 final int subscriptionID) {
      synchronized (_propertyChangeListeners) {
         final PropertyChangeListenerKey key = new PropertyChangeListenerKey(sessionID, subscriptionID);

         final PropertyChangeListenerData listenerData = _propertyChangeListeners.remove(key);
         if (listenerData == null) {
            logWarning("Can't find listener for " + key);
            return null;
         }

         return new GPair<String, PropertyChangeListener>(listenerData._propertyName, listenerData._listener);
      }
   }


   public void channelConnected(final Channel channel,
                                final int sessionID) {
      logInfo("RemoteAddress=" + channel.getRemoteAddress() + ", Session=" + sessionID + " connected.");
   }


   public void channelClosed(final Channel channel,
                             final int sessionID) {
      logInfo("RemoteAddress=" + channel.getRemoteAddress() + ", Session=" + sessionID + " closed.");

      synchronized (_propertyChangeListeners) {
         final Iterator<Entry<PropertyChangeListenerKey, PropertyChangeListenerData>> iterator = _propertyChangeListeners.entrySet().iterator();
         while (iterator.hasNext()) {
            final Entry<PropertyChangeListenerKey, PropertyChangeListenerData> entry = iterator.next();
            final PropertyChangeListenerKey listenerKey = entry.getKey();

            final int listenerSessionID = listenerKey._sessionID;
            if (listenerSessionID == sessionID) {
               final PropertyChangeListenerData listenerData = entry.getValue();

               logInfo("  Removing listener for session=" + sessionID + ", listenerID=" + listenerKey._subscriptionID
                       + ", propertyName=\"" + listenerData._propertyName + "\"");

               listenerData._model.removePropertyChangeListener(listenerData._propertyName, listenerData._listener);

               iterator.remove();
            }
         }
      }
   }


   public void evaluated(final Channel channel,
                         final GDModel model,
                         final Method method,
                         final Object[] materializedArgs,
                         final Object result,
                         final Exception exception) {
      for (final IDEvaluationListener evaluationListener : _evaluationListeners) {
         try {
            evaluationListener.evaluation(channel, model, method, materializedArgs, result, exception);
         }
         catch (final Exception e) {
            logSevere("Exception while evaluating " + evaluationListener, e);
         }
      }
   }


   @Override
   public GProtocolMultiplexor getMultiplexor() {
      return _handler.getMultiplexor();
   }


   public void logReceivedCommand(final IDServerCommand command,
                                  final SocketAddress remoteAddress) {
      _handler.logReceivedCommand(command, remoteAddress);
   }


   @Override
   public String logName() {
      return "dMVC-Server";
   }

}
