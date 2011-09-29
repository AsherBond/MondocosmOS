

package es.igosoftware.dmvc.commands.server;

import java.beans.PropertyChangeListener;

import org.jboss.netty.channel.Channel;

import es.igosoftware.dmvc.model.GDModel;
import es.igosoftware.dmvc.server.GDServer;
import es.igosoftware.protocol.GIntProtocolField;
import es.igosoftware.protocol.IProtocolField;
import es.igosoftware.util.GPair;


public final class GDRemovePropertyChangeListenerCommand
         extends
            GDAbstractServerCommand {


   private static final long       serialVersionUID = 1L;

   private final GIntProtocolField _modelID         = new GIntProtocolField(false);
   //   private final GStringProtocolField _propertyName    = new GStringProtocolField();
   private final GIntProtocolField _subscriptionID  = new GIntProtocolField(false);
   private final GIntProtocolField _sessionID       = new GIntProtocolField(false);


   public GDRemovePropertyChangeListenerCommand() {
   }


   public GDRemovePropertyChangeListenerCommand(final int modelID,
                                                //                                                final String propertyName,
                                                final int sessionID,
                                                final int subscriptionID) {
      _modelID.set(modelID);
      //      _propertyName.set(propertyName);
      _sessionID.set(sessionID);
      _subscriptionID.set(subscriptionID);
   }


   @Override
   public void evaluateInServer(final Channel channel,
                                final GDServer server) {
      final GDModel model = server.getModelByID(_modelID.get());
      if (model == null) {
         server.logSevere("Can't find model ID " + _modelID.get());
         return;
      }

      final GPair<String, PropertyChangeListener> propertyNameAndListener = server.unregisterPropertyChangeListener(
               _sessionID.get(), _subscriptionID.get());
      if (propertyNameAndListener != null) {
         model.removePropertyChangeListener(propertyNameAndListener._first, propertyNameAndListener._second);
      }
   }


   //   @Override
   //   public String toString() {
   //      return "RemovePropertyChangeListener [modelID=" + _modelID.get() + ", propertyName=\"" + _propertyName.get()
   //             + "\", subscriptionID=" + _subscriptionID.get() + "]";
   //   }

   @Override
   public String toString() {
      return "RemovePropertyChangeListener [modelID=" + _modelID.get() + ", subscriptionID=" + _subscriptionID.get() + "]";
   }


   @Override
   public boolean isComposite() {
      return false;
   }


   //   @Override
   //   protected IProtocolField[] getProtocolFields() {
   //      return new IProtocolField[] { _modelID, _propertyName, _subscriptionID, _sessionID };
   //   }
   @Override
   protected IProtocolField[] getProtocolFields() {
      return new IProtocolField[] {
                        _modelID,
                        _subscriptionID,
                        _sessionID
      };
   }


}
