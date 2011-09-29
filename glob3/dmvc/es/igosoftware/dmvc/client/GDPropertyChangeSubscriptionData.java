/**
 * 
 */


package es.igosoftware.dmvc.client;

import java.beans.PropertyChangeListener;


public class GDPropertyChangeSubscriptionData {
   public final Object                 _source;
   public final String                 _propertyName;
   public final PropertyChangeListener _listener;


   public GDPropertyChangeSubscriptionData(final Object source,
                                           final String propertyName,
                                           final PropertyChangeListener listener) {
      _source = source;
      _propertyName = propertyName;
      _listener = listener;
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_listener == null) ? 0 : _listener.hashCode());
      result = prime * result + ((_propertyName == null) ? 0 : _propertyName.hashCode());
      result = prime * result + ((_source == null) ? 0 : _source.hashCode());
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
      final GDPropertyChangeSubscriptionData other = (GDPropertyChangeSubscriptionData) obj;
      if (_listener == null) {
         if (other._listener != null) {
            return false;
         }
      }
      else if (!_listener.equals(other._listener)) {
         return false;
      }
      if (_propertyName == null) {
         if (other._propertyName != null) {
            return false;
         }
      }
      else if (!_propertyName.equals(other._propertyName)) {
         return false;
      }
      if (_source == null) {
         if (other._source != null) {
            return false;
         }
      }
      else if (!_source.equals(other._source)) {
         return false;
      }
      return true;
   }


   @Override
   public String toString() {
      return "GDPropertyChangeSubscriptionData [source=" + _source + ", propertyName=" + _propertyName + ", listener="
             + _listener + "]";
   }

}
