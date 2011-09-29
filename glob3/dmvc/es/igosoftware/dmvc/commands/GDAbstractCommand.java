

package es.igosoftware.dmvc.commands;

import java.util.List;

import es.igosoftware.protocol.GProtocolMultiplexor;
import es.igosoftware.protocol.GProtocolObject;
import es.igosoftware.util.GCollections;


public abstract class GDAbstractCommand
         extends
            GProtocolObject
         implements
            IDCommand {

   private static final long   serialVersionUID             = 1L;

   private static final Object FragmentsGroupIDCounterMutex = new Object();
   private static int          FragmentsGroupIDCounter      = 0;


   protected final byte[][] getFragments(final GProtocolMultiplexor multiplexor) {
      final byte[] serialized = multiplexor.getProtocolBytes(this);

      return GCollections.split(serialized, FRAGMENT_SIZE);
   }


   protected final static int generateFragmentsGroupID() {
      synchronized (FragmentsGroupIDCounterMutex) {
         return FragmentsGroupIDCounter++;
      }
   }


   @Override
   public final List<IDCommand> createFragmentsCommands(final GProtocolMultiplexor multiplexor) {
      final byte[][] fragments = getFragments(multiplexor);
      final int fragmentsGroupID = generateFragmentsGroupID();

      return getFragmentsCommands(fragments, fragmentsGroupID);
   }


   protected abstract List<IDCommand> getFragmentsCommands(final byte[][] fragments,
                                                           final int fragmentsGroupID);


   @Override
   protected final void initializeFromFields() {
   }


   @Override
   protected final void storeIntoFields() {
   }


   @Override
   public boolean isFragmentCommand() {
      return false;
   }

}
