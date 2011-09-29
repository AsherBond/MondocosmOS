

package es.igosoftware.dmvc.commands.client;

import java.util.Arrays;
import java.util.List;

import org.jboss.netty.channel.Channel;

import es.igosoftware.dmvc.client.GDClient;
import es.igosoftware.protocol.GArrayProtocolField;
import es.igosoftware.protocol.GProtocolMultiplexor;
import es.igosoftware.protocol.IProtocolField;


public final class GDCompositeClientCommand
         extends
            GDAbstractClientCommand {

   private static final long                          serialVersionUID = 1L;


   private final GArrayProtocolField<IDClientCommand> _children        = new GArrayProtocolField<IDClientCommand>(false) {
                                                                          @Override
                                                                          protected IDClientCommand[] createArray(final int length) {
                                                                             return new IDClientCommand[length];
                                                                          }
                                                                       };


   //   private final GProtocolMultiplexor                 _multiplexor;


   public GDCompositeClientCommand(final GProtocolMultiplexor multiplexor) {
      //      _multiplexor = multiplexor;
      _children.setMultiplexor(multiplexor);
   }


   public GDCompositeClientCommand(final List<IDClientCommand> children,
                                   final GProtocolMultiplexor multiplexor) {
      _children.set(children.toArray(new IDClientCommand[0]));
      //      _multiplexor = multiplexor;
      _children.setMultiplexor(multiplexor);
   }


   @Override
   public void evaluateInClient(final Channel channel,
                                final GDClient client) {
      //      for (final IDClientCommand child : _children) {
      //         child.evaluateInClient(channel, client);
      //      } 
      throw new RuntimeException("Can't directly evaluate a composite");
   }


   @Override
   public String toString() {
      return "CompositeClient " + Arrays.toString(_children.get());
   }


   @Override
   public boolean isComposite() {
      return true;
   }


   @Override
   public boolean isSynchronousEvaluationAnswer() {
      throw new RuntimeException("Can't answer isEvaluationAnswer()");
   }


   public IDClientCommand[] getChildren() {
      return _children.get();
   }


   @Override
   protected IProtocolField[] getProtocolFields() {
      return new IProtocolField[] {
         _children
      };
   }


}
