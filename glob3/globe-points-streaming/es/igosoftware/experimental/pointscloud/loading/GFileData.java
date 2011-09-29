

package es.igosoftware.experimental.pointscloud.loading;

import java.io.Serializable;


public class GFileData
         implements
            Serializable {


   private static final long serialVersionUID = 1L;

   private final int         _taskID;
   private final long        _from;
   private final long        _to;
   private final boolean     _lastPacket;
   private final byte[]      _data;


   GFileData(final int taskID,
             final long from,
             final long to,
             final boolean lastPacket,
             final byte[] data) {
      _taskID = taskID;
      _from = from;
      _to = to;
      _lastPacket = lastPacket;
      _data = data;
   }


   public int getTaskID() {
      return _taskID;
   }


   public long getFrom() {
      return _from;
   }


   public long getTo() {
      return _to;
   }


   public byte[] getData() {
      return _data;
   }


   @Override
   public String toString() {
      return "GFileData [taskID=" + _taskID + ", from=" + _from + ", to=" + _to + ", last=" + _lastPacket + ", data="
             + _data.length + "]";
   }


   public boolean isLastPacket() {
      return _lastPacket;
   }


}
