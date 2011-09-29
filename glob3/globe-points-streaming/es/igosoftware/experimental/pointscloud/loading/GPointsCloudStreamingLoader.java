

package es.igosoftware.experimental.pointscloud.loading;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.igosoftware.dmvc.client.GDClient;
import es.igosoftware.io.GFileName;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.io.ILoader;
import es.igosoftware.io.pointscloud.IPointsCloudLoader;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GPair;


public class GPointsCloudStreamingLoader
         implements
            IPointsCloudLoader {


   //   private static class FileDataRequest {
   //      private final int                    _taskID;
   //      private final long                   _from;
   //      private final long                   _to;
   //      private final List<ILoader.IHandler> _handlers = new ArrayList<ILoader.IHandler>();
   //
   //
   //      private FileDataRequest(final int taskID,
   //                              final long from,
   //                              final long to) {
   //         _taskID = taskID;
   //         _from = from;
   //         _to = to;
   //      }
   //
   //
   //   }
   //
   //
   //   private static class FileData {
   //      private final String                _fileName;
   //      private final long                  _available = 0;
   //      private final List<FileDataRequest> _requested = new ArrayList<FileDataRequest>();
   //
   //
   //      private FileData(final String fileName) {
   //         _fileName = fileName;
   //      }
   //
   //   }


   private final GDClient                                              _client;
   private final int                                                   _sessionID;
   private final IPointsStreamingServer                                _server;

   private final Object                                                _mutex     = new Object();
   //   private final Map<String, ILoader.LoadTaskID>           _tasksIDs  = new HashMap<String, ILoader.LoadTaskID>();
   private final Map<ILoader.LoadID, GPair<ILoader.IHandler, Boolean>> _handlers  = new HashMap<ILoader.LoadID, GPair<ILoader.IHandler, Boolean>>();
   private final Map<ILoader.LoadID, byte[]>                           _fragments = new HashMap<ILoader.LoadID, byte[]>();


   public GPointsCloudStreamingLoader(final String host,
                                      final int port) throws IOException {


      _client = new GDClient(host, port, true);

      _sessionID = _client.getSessionID();

      _server = (IPointsStreamingServer) _client.getRootObject();

      _server.addPropertyChangeListener("FileData_" + _sessionID, new PropertyChangeListener() {
         @Override
         public void propertyChange(final PropertyChangeEvent evt) {
            final GFileData result = (GFileData) evt.getNewValue();
            processFileData(result);
         }
      });
   }


   @Override
   public List<String> getPointsCloudsNames() {
      return _server.getPointsCloudsNames();
   }


   private void processFileData(final GFileData result) {
      final LoadID taskID = new ILoader.LoadID(result.getTaskID());

      byte[] accumulated = null;
      final byte[] data = result.getData();

      synchronized (_mutex) {
         accumulated = _fragments.get(taskID);
         if (accumulated == null) {
            accumulated = data;
         }
         else {
            accumulated = GCollections.concatenate(accumulated, data);
         }

         final boolean cancel = false;
         final boolean isLastPacket = result.isLastPacket();
         try {
            final File file = File.createTempFile("temp", ".cache");
            file.deleteOnExit();

            GIOUtils.copy(accumulated, file);

            final GPair<IHandler, Boolean> handlerAndReportsInconpleteLoads = _handlers.get(taskID);
            if (handlerAndReportsInconpleteLoads != null) {
               final boolean reportsInconpleteLoads = handlerAndReportsInconpleteLoads._second;
               if (isLastPacket || reportsInconpleteLoads) {
                  final ILoader.IHandler handler = handlerAndReportsInconpleteLoads._first;
                  handler.loaded(file, accumulated.length, isLastPacket);
               }
            }

            file.delete();
         }
         catch (final IOException e) {
            e.printStackTrace();
         }
         //         catch (final ILoader.AbortLoading e) {
         //            _server.cancel(taskID.getID());
         //            cancel = true;
         //         }

         if (cancel || isLastPacket) {
            _handlers.remove(taskID);
            _fragments.remove(taskID);
         }
         else {
            _fragments.put(taskID, accumulated);
         }
      }

   }


   @Override
   public ILoader.LoadID load(final GFileName fileName,
                              final long bytesToLoad,
                              final boolean reportIncompleteLoads,
                              final int priority,
                              final ILoader.IHandler handler) {


      //      synchronized (_mutex) {
      //         final Integer taskID = _tasksIDs.get(fileName);
      //         if (taskID != null) {
      //            _server.cancel(taskID);
      //         }
      //      cancelLoading(fileName);
      //      }

      final ILoader.LoadID taskID = new ILoader.LoadID(_server.loadFile(_sessionID, fileName, 0, bytesToLoad, priority));
      synchronized (_mutex) {
         _handlers.put(taskID, new GPair<ILoader.IHandler, Boolean>(handler, reportIncompleteLoads));
      }
      return taskID;
   }


   @Override
   public void cancelLoad(final ILoader.LoadID id) {

      synchronized (_mutex) {
         _handlers.remove(id);
      }

      if (id != null) {
         _server.cancel(id.getID());
      }
   }


   @Override
   public void cancelAllLoads(final GFileName fileName) {
      throw new RuntimeException("Not yet implemented!");
   }


   @Override
   public boolean canLoadFromLocalCache(final GFileName fileName) {
      throw new RuntimeException("Not yet implemented!");
   }


   @Override
   public void cleanCacheFor(final GFileName fileName) {
      throw new RuntimeException("Not yet implemented!");
   }


}
