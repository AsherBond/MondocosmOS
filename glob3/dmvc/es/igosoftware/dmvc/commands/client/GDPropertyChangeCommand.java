

package es.igosoftware.dmvc.commands.client;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.jboss.netty.channel.Channel;

import es.igosoftware.dmvc.GDSerialization;
import es.igosoftware.dmvc.client.GDClient;
import es.igosoftware.dmvc.client.GDPropertyChangeSubscriptionData;
import es.igosoftware.protocol.GIntProtocolField;
import es.igosoftware.protocol.GObjectProtocolField;
import es.igosoftware.protocol.IProtocolField;
import es.igosoftware.util.GUtils;


public final class GDPropertyChangeCommand
         extends
            GDAbstractClientCommand {

   private static final long                  serialVersionUID = 1L;

   private final GIntProtocolField            _subscriptionID  = new GIntProtocolField(false);
   private final GObjectProtocolField<Object> _oldValue        = new GObjectProtocolField<Object>();
   private final GObjectProtocolField<Object> _newValue        = new GObjectProtocolField<Object>();


   public GDPropertyChangeCommand() {
   }


   public GDPropertyChangeCommand(final int subscriptionID,
                                  final Object oldValue,
                                  final Object newValue) {
      _subscriptionID.set(subscriptionID);
      _oldValue.set(oldValue);
      _newValue.set(newValue);
   }


   @Override
   public void evaluateInClient(final Channel channel,
                                final GDClient client) {
      final GDPropertyChangeSubscriptionData propertyChangeSubscription = client.getPropertyChangeListener(_subscriptionID.get());
      if (propertyChangeSubscription == null) {
         client.logWarning("Received an PropertyChangeEvent (" + this + ") and not listener was found");
      }
      else {
         final PropertyChangeListener listener = propertyChangeSubscription._listener;
         final Object source = propertyChangeSubscription._source;
         final String propertyName = propertyChangeSubscription._propertyName;

         final Object materializedOldValue = GDSerialization.materializeInClient(_oldValue.get(), channel, client);
         final Object materializedNewValue = GDSerialization.materializeInClient(_newValue.get(), channel, client);

         listener.propertyChange(new PropertyChangeEvent(source, propertyName, materializedOldValue, materializedNewValue));
      }
   }


   @Override
   public boolean isComposite() {
      return false;
   }


   @Override
   public boolean isSynchronousEvaluationAnswer() {
      return false;
   }


   @Override
   public String toString() {
      return "PropertyChange [oldValue=" + GUtils.toString(_oldValue.get()) + ", newValue=" + GUtils.toString(_newValue.get())
             + ", subscriptionID=" + _subscriptionID.get() + "]";
   }


   @Override
   protected IProtocolField[] getProtocolFields() {
      return new IProtocolField[] {
                        _subscriptionID,
                        _oldValue,
                        _newValue
      };
   }

}
