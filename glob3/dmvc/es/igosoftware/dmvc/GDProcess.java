

package es.igosoftware.dmvc;

import java.util.HashMap;
import java.util.Map;

import es.igosoftware.logging.GLoggerObject;
import es.igosoftware.protocol.GProtocolMultiplexor;
import es.igosoftware.util.GCollections;


public abstract class GDProcess
         extends
            GLoggerObject {


   private final boolean              _verbose;
   private final Map<Integer, byte[]> _fragments = new HashMap<Integer, byte[]>();


   protected GDProcess(final boolean verbose) {
      super();
      _verbose = verbose;
   }


   @Override
   public boolean logVerbose() {
      return _verbose;
   }


   public byte[] accumulateFragment(final int groupID,
                                    final byte[] fragment) {
      final byte[] accumulated;
      synchronized (_fragments) {
         final byte[] previous = _fragments.get(groupID);
         if (previous == null) {
            accumulated = fragment;
         }
         else {
            accumulated = GCollections.concatenate(previous, fragment);
         }
         _fragments.put(groupID, accumulated);
      }
      return accumulated;
   }


   public void removeAccumulation(final int groupID) {
      synchronized (_fragments) {
         _fragments.remove(groupID);
      }
   }


   public abstract GProtocolMultiplexor getMultiplexor();
}
