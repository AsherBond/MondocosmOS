

package es.igosoftware.dmvc.commands.client;

import org.jboss.netty.channel.Channel;

import es.igosoftware.dmvc.GDSerialization;
import es.igosoftware.dmvc.client.GDClient;
import es.igosoftware.protocol.GIntProtocolField;
import es.igosoftware.protocol.GObjectProtocolField;
import es.igosoftware.protocol.IProtocolField;


public class GDAddPropertyChangeListenerResultCommand
         extends
            GDAbstractClientCommand {


   private static final long                  serialVersionUID = 1L;


   private final GIntProtocolField            _subscriptionID  = new GIntProtocolField(false);
   private final GObjectProtocolField<Object> _newValue        = new GObjectProtocolField<Object>();

   transient private Object                   _materializedNewValue;


   public GDAddPropertyChangeListenerResultCommand() {
   }


   public GDAddPropertyChangeListenerResultCommand(final int subscriptionID,
                                                   final Object newValue) {
      _subscriptionID.set(subscriptionID);
      _newValue.set(newValue);
   }


   @Override
   public void evaluateInClient(final Channel channel,
                                final GDClient client) {
      _materializedNewValue = GDSerialization.materializeInClient(_newValue.get(), channel, client);
   }


   @Override
   public boolean isSynchronousEvaluationAnswer() {
      return false;
   }


   @Override
   public boolean isComposite() {
      return false;
   }


   @Override
   public String toString() {
      return "AddPropertyChangeListenerResultCommand [subscriptionID=" + _subscriptionID.get() + ", newValue=" + _newValue.get()
             + "]";
   }


   public Object getNewValue() {
      return _materializedNewValue;
   }


   public int getSubscriptionID() {
      return _subscriptionID.get();
   }


   @Override
   protected IProtocolField[] getProtocolFields() {
      return new IProtocolField[] {
                        _subscriptionID,
                        _newValue
      };
   }

}
