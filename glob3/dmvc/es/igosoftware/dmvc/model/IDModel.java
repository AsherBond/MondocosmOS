

package es.igosoftware.dmvc.model;

import java.beans.PropertyChangeListener;
import java.util.List;

import es.igosoftware.dmvc.IDSerializable;


public interface IDModel
         extends
            IDSerializable {


   public void addPropertyChangeListener(final String propertyName,
                                         final PropertyChangeListener listener);


   public void removePropertyChangeListener(final String propertyName,
                                            final PropertyChangeListener listener);


   public void firePropertyChange(final String propertyName,
                                  final Object oldValue,
                                  final Object newValue);


   public List<IDProperty> getProperties();

}
