

package es.igosoftware.dmvc.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.channel.Channel;

import es.igosoftware.dmvc.GDSerialization;
import es.igosoftware.dmvc.IDSerializable;
import es.igosoftware.dmvc.client.GDClient;
import es.igosoftware.dmvc.server.GDServer;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GUtils;


public class GDProperty
         implements
            IDProperty {

   private static final Object[]                  EMPTY_OBJECT_ARRAY = new Object[] {};

   private static final long                      serialVersionUID   = 1L;

   private static Method                          READ_METHOD;
   private static Method                          WRITE_METHOD;
   private static Method                          ADD_PROPERTY_CHANGE_LISTENER;
   private static Method                          REMOVE_PROPERTY_CHANGE_LISTENER;
   private static Method                          FIRE_PROPERTY_CHANGE;

   static {
      for (final Method method : GDProperty.class.getMethods()) {
         final String methodName = method.getName();
         if (methodName.equals("get")) {
            READ_METHOD = method;
         }
         else if (methodName.equals("set")) {
            WRITE_METHOD = method;
         }
         else if (methodName.equals("addPropertyChangeListener")) {
            ADD_PROPERTY_CHANGE_LISTENER = method;
         }
         else if (methodName.equals("removePropertyChangeListener")) {
            REMOVE_PROPERTY_CHANGE_LISTENER = method;
         }
         else if (methodName.equals("firePropertyChange")) {
            FIRE_PROPERTY_CHANGE = method;
         }
      }
   }


   private transient final GDModel                _model;                                                      // transient, only the modelID will be serialized
   private int                                    _modelID;
   private transient GDRemoteModel                _remoteModel;                                                // transient, only the modelID will be serialized

   private final String                           _propertyName;
   private final String                           _writeMethodName;

   private final boolean                          _isConstant;

   private Object                                 _value;
   private int                                    _valueTimestamp;

   private transient List<PropertyChangeListener> _listeners         = new ArrayList<PropertyChangeListener>();
   private transient PropertyChangeListener       _remoteModelListener;


   public GDProperty(final GDModel model,
                     final String propertyName,
                     final String writeMethodName,
                     final boolean isConstant) {
      _model = model;
      _propertyName = propertyName;
      _writeMethodName = writeMethodName;
      _isConstant = isConstant;

      final Method read = getReadMethod(model.getClass());
      if (read == null) {
         throw new RuntimeException("Can't find a read method for property \"" + _propertyName + "\" in model "
                                    + model.getClass());
      }
   }


   public GDProperty(final GDModel model,
                     final String name,
                     final boolean isConstant) {
      this(model, name, getWriteMethodName(model, name), isConstant);
   }


   private static String getWriteMethodName(final GDModel model,
                                            final String propertyName) {
      final String writeMethodName = "set" + GUtils.capitalize(propertyName);
      return hasMethodNamed(model, writeMethodName) ? writeMethodName : null;
   }


   private static boolean hasMethodNamed(final GDModel model,
                                         final String methodName) {
      final Method[] methods = model.getClass().getMethods();
      for (final Method method : methods) {
         if (method.getName().equals(methodName)) {
            return true;
         }
      }
      return false;
   }


   public boolean isReadOnly() {
      return (_writeMethodName == null);
   }


   @Override
   public Object get() {
      final boolean connectedToServer = isConnectedToServer();

      if (connectedToServer && (_value != null)) {
         // answer the cached value
         return _value;
      }

      if (_isConstant && (_value != null)) {
         return _value;
      }

      final Object value;
      if (_model == null) {
         // remote invocation
         final Method readMethod = getReadMethod(_remoteModel.getModelInterface());

         try {
            value = _remoteModel.synchronousRemoteInvocation(readMethod, EMPTY_OBJECT_ARRAY);
            if (connectedToServer) {
               _value = value;
               _valueTimestamp = -1;
            }
         }
         catch (final Exception e) {
            throw new RuntimeException("exception while getting value from property", e);
         }

      }
      else {
         // local invocation
         value = get(_model);
      }

      return value;
   }


   private boolean isConnectedToServer() {
      synchronized (_listeners) {
         return !_listeners.isEmpty();
      }
   }


   @Override
   public void set(final Object value) throws Exception {
      if (isReadOnly()) {
         throw new RuntimeException("Can't set on a read-only property");
      }

      if (_model == null) {
         // remote invocation
         final Method writeMethod = getWriteMethod(_remoteModel.getModelInterface());

         final Object result = _remoteModel.synchronousRemoteInvocation(writeMethod, new Object[] {
            value
         });
         if (result != null) {
            throw new RuntimeException("method " + writeMethod + " returned a value");
         }
      }
      else {
         // local invocation
         set(_model, value);
      }

      //      _value = value;
      //      _valueTimestamp = -1;
   }


   private void set(final GDModel model,
                    final Object value) {
      try {
         final Method write = getWriteMethod(model.getClass());
         write.invoke(model, value);
      }
      catch (final IllegalAccessException e) {
         throw new RuntimeException(e);
      }
      catch (final SecurityException e) {
         throw new RuntimeException(e);
      }
      catch (final IllegalArgumentException e) {
         throw new RuntimeException(e);
      }
      catch (final InvocationTargetException e) {
         throw new RuntimeException(e);
      }
   }


   @Override
   public IDSerializable materializeInClient(final Channel channel,
                                             final GDClient client) {
      _value = GDSerialization.materializeInClient(_value, channel, client);
      return this;
   }


   @Override
   public IDSerializable materializeInServer(final Channel channel,
                                             final GDServer server) {
      throw new RuntimeException("Can't materialize an aspect in server");
   }


   @Override
   public Object objectToSerialize(final GDServer server) {
      _modelID = server.getModelID(_model);
      _value = GDSerialization.objectToSerialize(get(_model), server);
      _valueTimestamp = _model.getTimestamp(_propertyName);
      return this;
   }


   private Object get(final IDModel model) {
      try {
         final Method read = getReadMethod(model.getClass());
         return read.invoke(model);
      }
      catch (final IllegalAccessException e) {
         throw new RuntimeException(e);
      }
      catch (final SecurityException e) {
         throw new RuntimeException(e);
      }
      catch (final IllegalArgumentException e) {
         throw new RuntimeException(e);
      }
      catch (final InvocationTargetException e) {
         throw new RuntimeException(e);
      }
   }


   private Method getReadMethod(final Class<? extends IDModel> klass) {
      final String capitalizePropertyName = GUtils.capitalize(_propertyName);
      final String getMethodName = "get" + capitalizePropertyName;
      final String isMethodName = "is" + capitalizePropertyName;

      for (final Method method : klass.getMethods()) {
         final String methodName = method.getName();
         if (methodName.equals(getMethodName) || methodName.equals(isMethodName)) {
            return method;
         }
      }

      return null;
   }


   private Method getWriteMethod(final Class<? extends IDModel> klass) {
      if (_writeMethodName == null) {
         return null;
      }

      final Method[] methods = klass.getMethods();
      for (final Method method : methods) {
         if (method.getName().equals(_writeMethodName)) {
            return method;
         }
      }

      return null;
   }


   @Override
   public Object objectToSerialize(final GDClient client) {
      throw new RuntimeException("Can't serialize an aspect from client");
   }


   @Override
   public String toString() {
      return "Property [modelID=" + _modelID + ", name=" + _propertyName + ", readOnly=" + isReadOnly() + ", value=" + _value
             + ", ts=" + _valueTimestamp + "]";
   }


   @Override
   public void validate(final GDModel model) {
      GAssert.isSame(model, _model);

      // get read method now to force a RuntimeException if not found
      getReadMethod(model.getClass());

      // get write method now to force a RuntimeException if not found
      getWriteMethod(model.getClass());
   }


   @Override
   public void justMaterializedInClient(final GDRemoteModel remoteModel) {
      _listeners = new ArrayList<PropertyChangeListener>();
      _remoteModel = remoteModel;
   }


   @Override
   public Method getPropertyMethod(final String methodName,
                                   final Object[] args) {
      if (isReadMethod(methodName)) {
         return READ_METHOD;
      }
      else if (isWriteMethod(methodName)) {
         return WRITE_METHOD;
      }
      else if (methodName.equals("addPropertyChangeListener") && _propertyName.equals(args[0])) {
         return ADD_PROPERTY_CHANGE_LISTENER;
      }
      else if (methodName.equals("removePropertyChangeListener") && _propertyName.equals(args[0])) {
         return REMOVE_PROPERTY_CHANGE_LISTENER;
      }
      else if (methodName.equals("firePropertyChange") && _propertyName.equals(args[0])) {
         return FIRE_PROPERTY_CHANGE;
      }

      return null;
   }


   private boolean isReadMethod(final String methodName) {
      final Method method = getReadMethod(_remoteModel.getModelInterface());
      if ((method != null) && method.getName().equals(methodName)) {
         return true;
      }
      return false;
   }


   private boolean isWriteMethod(final String methodName) {
      final Method method = getWriteMethod(_remoteModel.getModelInterface());
      if ((method != null) && method.getName().equals(methodName)) {
         return true;
      }
      return false;
   }


   private void checkPropertyName(final String propertyName) {
      if (!_propertyName.equals(propertyName)) {
         throw new RuntimeException("Can't subscribe to an property other that \"" + _propertyName + "\" (was \"" + propertyName
                                    + "\")");
      }
   }


   public void addPropertyChangeListener(final String propertyName,
                                         final PropertyChangeListener listener) {
      checkPropertyName(propertyName);

      if (listener == null) {
         return;
      }

      if (_isConstant) {
         // the property is constant, it doesn't make sense to really suscribe to events
         return;
      }

      synchronized (_listeners) {
         if (_listeners.isEmpty()) {
            _remoteModelListener = new PropertyChangeListener() {
               @Override
               public void propertyChange(final PropertyChangeEvent evt) {
                  setValue(evt.getNewValue());
                  firePropertyChange(evt);
               }
            };
            final Object newValue = _remoteModel.rawAddPropertyChangeListener(_propertyName, _remoteModelListener,
                     _valueTimestamp);
            if (newValue != null) {
               setValue(newValue);
            }
         }

         _listeners.add(listener);
      }
   }


   protected void setValue(final Object newValue) {
      _value = newValue;
      _valueTimestamp = -1;
   }


   public void removePropertyChangeListener(final String propertyName,
                                            final PropertyChangeListener listener) {
      checkPropertyName(propertyName);

      if (listener == null) {
         return;
      }

      if (_isConstant) {
         // the property is constant, it doesn't make sense to really suscribe to events
         return;
      }

      synchronized (_listeners) {
         _listeners.remove(listener);

         if (_listeners.isEmpty()) {
            _remoteModel.rawRemovePropertyChangeListener(_propertyName, _remoteModelListener);
            _remoteModelListener = null;
         }
      }
   }


   public void firePropertyChange(final String propertyName,
                                  final Object oldValue,
                                  final Object newValue) {
      if (!propertyName.equals(_propertyName)) {
         throw new RuntimeException("Inconsistency between " + propertyName + " and " + _propertyName);
      }
      final PropertyChangeEvent event = new PropertyChangeEvent(_remoteModel, _propertyName, oldValue, newValue);
      firePropertyChange(event);
   }


   public void firePropertyChange(final PropertyChangeEvent event) {
      checkPropertyName(event.getPropertyName());

      synchronized (_listeners) {
         if (_listeners.isEmpty()) {
            return;
         }

         for (final PropertyChangeListener listener : _listeners) {
            listener.propertyChange(event);
         }
      }
   }


   @Override
   public String getName() {
      return _propertyName;
   }

}
