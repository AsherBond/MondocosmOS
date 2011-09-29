

package es.igosoftware.dmvc.commands.server;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.jboss.netty.channel.Channel;

import es.igosoftware.dmvc.GDSerialization;
import es.igosoftware.dmvc.commands.client.GDAddPropertyChangeListenerResultCommand;
import es.igosoftware.dmvc.commands.client.GDPropertyChangeCommand;
import es.igosoftware.dmvc.commands.client.IDClientCommand;
import es.igosoftware.dmvc.model.GDModel;
import es.igosoftware.dmvc.model.IDProperty;
import es.igosoftware.dmvc.server.GDServer;
import es.igosoftware.protocol.GIntProtocolField;
import es.igosoftware.protocol.GStringProtocolField;
import es.igosoftware.protocol.IProtocolField;


public final class GDAddPropertyChangeListenerCommand
         extends
            GDAbstractServerCommand {


   private static final long          serialVersionUID = 1L;

   private final GIntProtocolField    _modelID         = new GIntProtocolField(false);
   private final GStringProtocolField _propertyName    = new GStringProtocolField();
   private final GIntProtocolField    _sessionID       = new GIntProtocolField(false);
   private final GIntProtocolField    _subscriptionID  = new GIntProtocolField(false);
   private final GIntProtocolField    _valueTimestamp  = new GIntProtocolField(false);


   public GDAddPropertyChangeListenerCommand() {
   }


   public GDAddPropertyChangeListenerCommand(final int modelID,
                                             final String propertyName,
                                             final int sessionID,
                                             final int subscriptionID,
                                             final int valueTimestamp) {
      _modelID.set(modelID);
      _propertyName.set(propertyName);
      _sessionID.set(sessionID);
      _subscriptionID.set(subscriptionID);
      _valueTimestamp.set(valueTimestamp);
   }


   @Override
   public void evaluateInServer(final Channel channel,
                                final GDServer server) {
      final Integer modelID = _modelID.get();
      final GDModel model = server.getModelByID(modelID);
      if (model == null) {
         server.logSevere("Can't find model ID " + modelID);
         return;
      }

      final Integer subscriptionID = _subscriptionID.get();
      final String propertyName = _propertyName.get();
      final Integer sessionID = _sessionID.get();

      final PropertyChangeListener listener = new PropertyChangeListener() {
         @Override
         public void propertyChange(final PropertyChangeEvent evt) {
            if (channel.isOpen()) {
               final Object oldValue = GDSerialization.objectToSerialize(evt.getOldValue(), server);
               final Object newValue = GDSerialization.objectToSerialize(evt.getNewValue(), server);
               final IDClientCommand command = new GDPropertyChangeCommand(subscriptionID, oldValue, newValue);
               server.sendCommand(channel, command);
            }
            else {
               // server.logInfo("Channel got closed, removing listener");
               model.removePropertyChangeListener(propertyName, this);
               server.unregisterPropertyChangeListener(sessionID, subscriptionID);
            }
         }
      };
      server.registerPropertyChangeListener(sessionID, subscriptionID, model, propertyName, listener);

      final int valueTimestamp = _valueTimestamp.get();
      synchronized (model) {
         model.addPropertyChangeListener(propertyName, listener);

         if (valueTimestamp >= 0 /* -1 means no timestamp is present */) {
            final int current = model.getTimestamp(propertyName);
            final IDClientCommand command;
            if (valueTimestamp == current) {
               command = new GDAddPropertyChangeListenerResultCommand(subscriptionID, null);
            }
            else {
               server.logInfo("Timestamp conflict detected in property \"" + propertyName + "\" of " + model);

               final IDProperty property = model.getProperty(propertyName);
               if (property == null) {
                  server.logSevere("Can't find property \"" + propertyName + "\" in " + model);
                  command = new GDAddPropertyChangeListenerResultCommand(subscriptionID, null);
               }
               else {
                  final Object newValue = property.get();
                  command = new GDAddPropertyChangeListenerResultCommand(subscriptionID, GDSerialization.objectToSerialize(
                           newValue, server));
               }
            }
            server.sendCommand(channel, command);
         }
      }
   }


   @Override
   public String toString() {
      return "AddPropertyChangeListener [modelID=" + _modelID.get() + ", propertyName=\"" + _propertyName.get()
             + "\", subscriptionID=" + _subscriptionID.get() + ", valueTimestamp=" + _valueTimestamp.get() + "]";
   }


   @Override
   public boolean isComposite() {
      return false;
   }


   public boolean isYourAnswer(final GDAddPropertyChangeListenerResultCommand answer) {
      return (_subscriptionID.get() == answer.getSubscriptionID());
   }


   @Override
   protected IProtocolField[] getProtocolFields() {
      return new IProtocolField[] {
                        _modelID,
                        _propertyName,
                        _sessionID,
                        _subscriptionID,
                        _valueTimestamp
      };
   }


}
