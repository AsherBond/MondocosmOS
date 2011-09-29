

package es.igosoftware.dmvc.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.netty.channel.Channel;

import es.igosoftware.dmvc.IDSerializable;
import es.igosoftware.dmvc.client.GDClient;
import es.igosoftware.dmvc.server.GDServer;


public abstract class GDModel
         implements
            IDModel {


   private final PropertyChangeSupport _propertyChangeSupport = new PropertyChangeSupport(this);
   private final List<IDProperty>      _properties;

   private final Map<String, Integer>  _propertiesTimestamps  = new HashMap<String, Integer>();


   public GDModel() {
      final List<IDProperty> properties = Collections.unmodifiableList(defaultProperties());
      validateProperties(properties);
      _properties = properties;
   }


   private void validateProperties(final List<IDProperty> properties) {
      if (properties == null) {
         throw new RuntimeException(getClass() + ".defaultProperties() answered null");
      }

      for (final IDProperty property : properties) {
         property.validate(this);
      }
   }


   protected abstract List<IDProperty> defaultProperties();


   @Override
   public Object objectToSerialize(final GDServer server) {
      return server.getRemoteModel(this);
   }


   @Override
   public Object objectToSerialize(final GDClient client) {
      throw new RuntimeException("Can't serialize from client");
   }


   @Override
   public final boolean equals(final Object another) {
      // ensure identity comparison
      return (this == another);
   }


   @Override
   public final int hashCode() {
      // ensure identity comparison
      return System.identityHashCode(this);
   }


   @Override
   public IDSerializable materializeInClient(final Channel channel,
                                             final GDClient client) {
      throw new RuntimeException("Can't materialize in client");
   }


   @Override
   public IDSerializable materializeInServer(final Channel channel,
                                             final GDServer server) {
      throw new RuntimeException("Can't materialize in server");
   }


   @Override
   public void addPropertyChangeListener(final String propertyName,
                                         final PropertyChangeListener listener) {
      synchronized (_propertyChangeSupport) {
         _propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
      }
   }


   @Override
   public void removePropertyChangeListener(final String propertyName,
                                            final PropertyChangeListener listener) {
      synchronized (_propertyChangeSupport) {
         _propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
      }
   }


   @Override
   public void firePropertyChange(final String propertyName,
                                  final Object oldValue,
                                  final Object newValue) {
      synchronized (_propertyChangeSupport) {
         _propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);

         updateTimestamp(propertyName);
      }
   }


   private void updateTimestamp(final String propertyName) {
      synchronized (_propertiesTimestamps) {
         final Integer currentTS = _propertiesTimestamps.get(propertyName);
         if (currentTS == null) {
            final int newTS = 1;
            _propertiesTimestamps.put(propertyName, newTS);
         }
         else {
            final int newTS = currentTS + 1;
            _propertiesTimestamps.put(propertyName, newTS);
         }
      }
   }


   @Override
   public List<IDProperty> getProperties() {
      return _properties;
   }


   public int getTimestamp(final String propertyName) {
      synchronized (_propertiesTimestamps) {
         final Integer ts = _propertiesTimestamps.get(propertyName);
         return (ts == null) ? 0 : ts;
      }
   }


   public IDProperty getProperty(final String propertyName) {
      for (final IDProperty property : _properties) {
         if (property.getName().equals(propertyName)) {
            return property;
         }
      }
      return null;
   }

}
