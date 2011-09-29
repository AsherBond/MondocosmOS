

package es.igosoftware.dmvc.model;

import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import org.jboss.netty.channel.Channel;

import es.igosoftware.dmvc.GDSerialization;
import es.igosoftware.dmvc.GDUtils;
import es.igosoftware.dmvc.IDSerializable;
import es.igosoftware.dmvc.client.GDClient;
import es.igosoftware.dmvc.client.GDPropertyChangeSubscriptionData;
import es.igosoftware.dmvc.commands.client.GDAddPropertyChangeListenerResultCommand;
import es.igosoftware.dmvc.commands.client.GDEvaluationAnswerCommand;
import es.igosoftware.dmvc.commands.server.GDAddPropertyChangeListenerCommand;
import es.igosoftware.dmvc.commands.server.GDRemoteAsynchronousEvaluationCommand;
import es.igosoftware.dmvc.commands.server.GDRemoteSynchronousEvaluationCommand;
import es.igosoftware.dmvc.commands.server.GDRemovePropertyChangeListenerCommand;
import es.igosoftware.dmvc.commands.server.IDServerCommand;
import es.igosoftware.dmvc.server.GDServer;
import es.igosoftware.util.GPair;


public final class GDRemoteModel
         implements
            IDModel,
            Serializable {

   private static final class ModelStub
            implements
               InvocationHandler,
               IDSerializable {

      private final GDRemoteModel _remoteModel;


      private ModelStub(final GDRemoteModel remoteModel) {
         _remoteModel = remoteModel;
      }


      @Override
      public Object invoke(final Object proxy,
                           final Method method,
                           final Object[] args) throws Exception {

         final Class<?> declaringClass = method.getDeclaringClass();

         if (declaringClass == Object.class) {
            return invokeObjectMethod(proxy, method, args);
         }

         final GPair<IDProperty, Method> propertyAndMethod = _remoteModel.getPropertyAndMethod(method.getName(), args);
         if (propertyAndMethod != null) {
            // reroute the method over the property so it (the property) has the opportunity to handle the cached value
            if (args == null) {
               return propertyAndMethod._second.invoke(propertyAndMethod._first);
            }
            return propertyAndMethod._second.invoke(propertyAndMethod._first, args);
         }

         if ((declaringClass == IDModel.class) || method.getName().equals("objectToSerialize")) {
            return method.invoke(_remoteModel, args);
         }

         return invokeRemoteMethod(proxy, method, args);
      }


      private Object invokeObjectMethod(final Object proxy,
                                        final Method method,
                                        final Object[] args) {
         final String name = method.getName();

         if (name.equals("hashCode")) {
            return Integer.valueOf(System.identityHashCode(proxy));
         }
         else if (name.equals("equals")) {
            return (proxy == args[0]) ? Boolean.TRUE : Boolean.FALSE;
         }
         else if (name.equals("toString")) {
            return "GDRemoteModel [modelID=" + _remoteModel._modelID + ", modelInterface=" + _remoteModel._modelInterfaceName
                   + "]";
         }
         else {
            throw new IllegalArgumentException("Unexpected Object method: " + method);
         }
      }


      private Object invokeRemoteMethod(@SuppressWarnings("unused") final Object proxy,
                                        final Method method,
                                        final Object[] args) throws Exception {

         if (isAsynchronousMethod(method)) {
            _remoteModel.asynchronousRemoteInvocation(method, args);
            return null;
         }

         return _remoteModel.synchronousRemoteInvocation(method, args);
      }


      private boolean isAsynchronousMethod(final Method method) {
         final Class<?>[] parameterTypes = method.getParameterTypes();
         return (parameterTypes.length > 0)
                && IDAsynchronousExecutionListener.class.isAssignableFrom(parameterTypes[parameterTypes.length - 1]);
      }


      @Override
      public IDSerializable materializeInClient(final Channel channel,
                                                final GDClient client) {
         throw new RuntimeException("Can't materialize in client");
      }


      @Override
      public IDSerializable materializeInServer(final Channel channel,
                                                final GDServer server) {
         throw new RuntimeException("Can't materialize in client");
      }


      @Override
      public Object objectToSerialize(final GDServer server) {
         throw new RuntimeException("Can't serialize in server");
      }


      @Override
      public Object objectToSerialize(final GDClient client) {
         return _remoteModel;
      }
   }


   private static final long                  serialVersionUID = 1L;


   private final int                          _modelID;
   private final String                       _modelInterfaceName;
   private final List<IDProperty>             _properties;

   private transient Class<? extends IDModel> _modelInterface;
   private transient Channel                  _channel;
   private transient GDClient                 _client;


   public GDRemoteModel(final int modelID,
                        final Class<? extends IDModel> modelInterface,
                        final List<IDProperty> properties) {
      _modelID = modelID;
      _modelInterface = modelInterface;
      _modelInterfaceName = modelInterface.getName();
      _properties = properties;
   }


   public GPair<IDProperty, Method> getPropertyAndMethod(final String methodName,
                                                         final Object[] args) {
      for (final IDProperty property : _properties) {
         final Method propertyMethod = property.getPropertyMethod(methodName, args);
         if (propertyMethod != null) {
            return new GPair<IDProperty, Method>(property, propertyMethod);
         }
      }
      return null;
   }


   @Override
   public Object objectToSerialize(final GDServer server) {
      return this;
   }


   @Override
   public String toString() {
      return "GDRemoteModel [modelID=" + _modelID + ", modelInterface=" + _modelInterfaceName + ", properties=" + _properties
             + "]";
   }


   @Override
   public int hashCode() {
      //return (_modelID ^ (_modelID >>> 32));
      return _modelID;
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
      final GDRemoteModel other = (GDRemoteModel) obj;
      if (_modelID != other._modelID) {
         return false;
      }
      return true;
   }


   @Override
   public IDModel materializeInClient(final Channel channel,
                                      final GDClient client) {
      _channel = channel;
      _client = client;
      return client.materializeInClient(this);
   }


   public int getModelID() {
      return _modelID;
   }


   @SuppressWarnings("unchecked")
   public IDModel justMaterializedInClient(@SuppressWarnings("unused") final GDClient client) {
      if (_modelInterface != null) {
         throw new RuntimeException(this + " already materialized in client");
      }

      try {
         _modelInterface = (Class<? extends IDModel>) Class.forName(_modelInterfaceName);
      }
      catch (final ClassNotFoundException e) {
         throw new RuntimeException("Exception while materializing in client", e);
      }

      for (final IDProperty property : _properties) {
         property.justMaterializedInClient(this);
      }

      final InvocationHandler invocationHandler = new ModelStub(this);
      return (IDModel) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] {
         _modelInterface
      }, invocationHandler);
   }


   @Override
   public GDModel materializeInServer(final Channel channel,
                                      final GDServer server) {
      return server.getModelByID(_modelID);
   }


   @Override
   public void firePropertyChange(final String propertyName,
                                  final Object oldValue,
                                  final Object newValue) {
      throw new RuntimeException("Can't fire events from a RemoteModel");
   }


   @Override
   public void addPropertyChangeListener(final String propertyName,
                                         final PropertyChangeListener listener) {
      //      final GDPropertyChangeSubscriptionData propertyChangeSubscriptionData = new GDPropertyChangeSubscriptionData(this,
      //               propertyName, listener);
      //      final int subscriptionID = _client.registerPropertyChangeListener(propertyChangeSubscriptionData);
      //
      //      final IDServerCommand command = new GDAddPropertyChangeListenerCommand(_modelID, propertyName, _client.getSessionID(),
      //               subscriptionID, -1);
      //      _client.sendCommand(_channel, command);
      rawAddPropertyChangeListener(propertyName, listener, -1);
   }


   public Object rawAddPropertyChangeListener(final String propertyName,
                                              final PropertyChangeListener listener,
                                              final int valueTimestamp) {
      final GDPropertyChangeSubscriptionData propertyChangeSubscriptionData = new GDPropertyChangeSubscriptionData(this,
               propertyName, listener);
      final int subscriptionID = _client.registerPropertyChangeListener(propertyChangeSubscriptionData);

      final GDAddPropertyChangeListenerCommand command = new GDAddPropertyChangeListenerCommand(_modelID, propertyName,
               _client.getSessionID(), subscriptionID, valueTimestamp);
      _client.sendCommand(_channel, command);

      if (valueTimestamp >= 0) {
         final GDAddPropertyChangeListenerResultCommand answer = _client.waitForAnswer(command);
         answer.evaluateInClient(_channel, _client);
         return answer.getNewValue();
      }
      return null;
   }


   @Override
   public void removePropertyChangeListener(final String propertyName,
                                            final PropertyChangeListener listener) {
      //      final GDPropertyChangeSubscriptionData propertyChangeSubscriptionData = new GDPropertyChangeSubscriptionData(this,
      //               propertyName, listener);
      //
      //      final int subscriptionID = _client.removePropertyChangeListener(propertyChangeSubscriptionData);
      //      if (subscriptionID < 0) {
      //         return;
      //      }
      //
      //      final IDServerCommand command = new GDRemovePropertyChangeListenerCommand(_modelID, propertyName, _client.getSessionID(),
      //               subscriptionID);
      //      _client.sendCommand(_channel, command);
      rawRemovePropertyChangeListener(propertyName, listener);
   }


   public void rawRemovePropertyChangeListener(final String propertyName,
                                               final PropertyChangeListener listener) {
      final GDPropertyChangeSubscriptionData propertyChangeSubscriptionData = new GDPropertyChangeSubscriptionData(this,
               propertyName, listener);

      final int subscriptionID = _client.removePropertyChangeListener(propertyChangeSubscriptionData);
      if (subscriptionID < 0) {
         return;
      }

      final IDServerCommand command = new GDRemovePropertyChangeListenerCommand(_modelID, /*propertyName, */
      _client.getSessionID(), subscriptionID);
      _client.sendCommand(_channel, command);
   }


   @Override
   public Object objectToSerialize(final GDClient client) {
      // don't send properties back to server
      return new GDRemoteModel(_modelID, _modelInterface, null);
   }


   @Override
   public List<IDProperty> getProperties() {
      return _properties;
   }


   public Class<? extends IDModel> getModelInterface() {
      return _modelInterface;
   }


   public Object synchronousRemoteInvocation(final Method method,
                                             final Object[] args) throws Exception {
      final int methodID = GDUtils.getMethodID(_modelInterface, method);
      final Object[] argsToSerialize = GDSerialization.objectToSerialize(args, _client);

      // send remote command to evaluate in server
      final GDRemoteSynchronousEvaluationCommand command = new GDRemoteSynchronousEvaluationCommand(_modelID, methodID,
               argsToSerialize);
      _client.sendCommand(_channel, command);

      // wait for server answer
      final GDEvaluationAnswerCommand response = _client.waitForAnswer(command);
      response.evaluateInClient(_channel, _client);
      return response.evaluationResult();
   }


   @SuppressWarnings("unchecked")
   public void asynchronousRemoteInvocation(final Method method,
                                            final Object[] args) throws Exception {

      final Object[] argsToSerialize = GDSerialization.objectToSerialize(args, _client);
      //         final String[] argsClassesNames = getArgsClassesNames(method.getParameterTypes());

      final int methodID = GDUtils.getMethodID(_modelInterface, method);

      final GDRemoteAsynchronousEvaluationCommand command = new GDRemoteAsynchronousEvaluationCommand(_modelID, methodID,
               argsToSerialize);

      final IDAsynchronousExecutionListener<Object, Exception> listener = (IDAsynchronousExecutionListener<Object, Exception>) args[args.length - 1];
      _client.registerAsynchronousExecutionListener(command.getEvaluationID(), listener);

      _client.sendCommand(_channel, command);
   }


}
