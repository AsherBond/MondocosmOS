/*

 IGO Software SL  -  info@igosoftware.es

 http://www.glob3.org

-------------------------------------------------------------------------------
 Copyright (c) 2010, IGO Software SL
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
     * Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.
     * Neither the name of the IGO Software SL nor the
       names of its contributors may be used to endorse or promote products
       derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL IGO Software SL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-------------------------------------------------------------------------------

*/


package es.igosoftware.experimental.pointscloud.loading;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jboss.netty.channel.Channel;

import es.igosoftware.dmvc.model.GDModel;
import es.igosoftware.dmvc.model.IDProperty;
import es.igosoftware.dmvc.server.GDServer;
import es.igosoftware.io.GFileName;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GMath;


public class GPointsStreamingServer
         extends
            GDModel
         implements
            IPointsStreamingServer {

   //   private static final int         ____TODO_CHANGE_GROUP_SIZE_dgd = 0;
   private static final int         POINTS_GROUP_SIZE    = 1024;

   private final File               _rootDirectory;
   private final LinkedList<Task>   _tasks               = new LinkedList<Task>();

   private int                      _taskIDCounter       = 0;
   private final Map<Integer, Long> _lastSendsTimestamps = new HashMap<Integer, Long>();


   public GPointsStreamingServer(final String rootDirectoryName) {
      GAssert.notNull(rootDirectoryName, "rootDirectoryName");

      _rootDirectory = initializeRootDirectory(rootDirectoryName);

      initializeWorker();
   }


   private Thread initializeWorker() {

      final Thread worker = new Thread() {
         @Override
         public void run() {
            try {
               while (true) {
                  final Task task = selectTask();

                  if (task == null) {
                     Thread.sleep(100);
                  }
                  else {
                     synchronized (_lastSendsTimestamps) {
                        _lastSendsTimestamps.put(task._sessionID, System.currentTimeMillis());
                     }
                     final GFileData result = task.execute();
                     if (result != null) {
                        firePropertyChange("FileData_" + task._sessionID, null, result);
                     }
                  }
               }
            }
            catch (final InterruptedException e) {
               // do nothing, just exit from run()
            }
         }
      };

      worker.setDaemon(true);
      worker.setPriority(Thread.MAX_PRIORITY);
      worker.start();

      return worker;
   }


   private File initializeRootDirectory(final String rootDirectoryName) {
      final File rootDirectory = new File(rootDirectoryName);

      if (!rootDirectory.exists() || !rootDirectory.canRead()) {
         throw new RuntimeException("Invalid rootDirectoryName (" + rootDirectory.getAbsolutePath() + ")");
      }

      return rootDirectory;
   }


   @Override
   protected List<IDProperty> defaultProperties() {
      return Collections.emptyList();
   }


   @Override
   public List<String> getPointsCloudsNames() {
      final String[] directoriesNames = _rootDirectory.list(new FilenameFilter() {
         @Override
         public boolean accept(final File dir,
                               final String name) {
            final File file = new File(dir, name);
            if (!file.isDirectory()) {
               return false;
            }

            final File treeFile = new File(file, "tree.object.gz");
            return treeFile.exists();
         }
      });

      return Arrays.asList(directoriesNames);
   }


   //   @Override
   //   public GPCPointsCloud getPointsCloud(final String pointsCloudName) {
   //      try {
   //         return _pointsCloudCache.get(pointsCloudName);
   //      }
   //      catch (final IOException e) {
   //         return null;
   //      }
   //   }


   private class Task {

      private final String  _fileName;
      private final long    _from;
      private final long    _to;
      private int           _priority;
      private final int     _taskID;
      private final int     _sessionID;
      private final boolean _lastPacket;


      private Task(final String fileName,
                   final long from,
                   final long to,
                   final boolean lastPacket,
                   final int priority,
                   final int taskID,
                   final int sessionID) {
         _fileName = fileName;
         _from = from;
         _to = to;
         _lastPacket = lastPacket;
         _priority = priority;
         _taskID = taskID;
         _sessionID = sessionID;
      }


      private GFileData execute() {
         final File file = new File(_rootDirectory, _fileName);

         BufferedInputStream input = null;
         try {
            input = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()), 16 * 1024);

            final byte[] bytes = new byte[GMath.toInt(_to - _from + 1)];

            try {
               if (input.skip(_from) == _from) {
                  input.read(bytes);
               }
            }
            catch (final EOFException eof) {

            }


            return new GFileData(_taskID, _from, _to, _lastPacket, bytes);
         }
         catch (final IOException e) {
            e.printStackTrace();
            return null;
         }
         finally {
            GIOUtils.gentlyClose(input);
         }
      }
   }


   @Override
   public int loadFile(final int sessionID,
                       final GFileName fileName,
                       final long fromBytes,
                       final long toBytes,
                       final int priority) {
      long from = fromBytes;

      final long normalizedToBytes;
      if (toBytes < 0) {
         normalizedToBytes = new File(_rootDirectory, fileName.buildPath()).length();
      }
      else {
         normalizedToBytes = toBytes;
      }

      synchronized (_tasks) {
         final int taskID = calculateTaskID();


         while (from < normalizedToBytes) {
            final long to = Math.min(from + POINTS_GROUP_SIZE, normalizedToBytes) - 1;

            final boolean lastPacket = (to == (normalizedToBytes - 1));
            _tasks.add(new Task(fileName.buildPath(), from, to, lastPacket, priority, taskID, sessionID));

            from += POINTS_GROUP_SIZE;
         }

         return taskID;
      }
   }


   private int calculateTaskID() {
      return _taskIDCounter++;
   }


   private Task selectTask() {
      Task selectedTask = null;

      final long threshold = System.currentTimeMillis() - 50;

      synchronized (_tasks) {
         for (final Task task : _tasks) {
            if ((selectedTask == null) || (selectedTask._priority > task._priority)) {
               if (lastSend(task._sessionID) < threshold) {
                  selectedTask = task;
               }
            }
         }

         if (selectedTask != null) {
            _tasks.remove(selectedTask);
         }
      }

      return selectedTask;
   }


   private long lastSend(final int sessionID) {
      synchronized (_lastSendsTimestamps) {
         final Long lastSend = _lastSendsTimestamps.get(sessionID);
         if (lastSend == null) {
            return Long.MIN_VALUE;
         }
         return lastSend;
      }
   }


   @Override
   public void cancel(final int taskID) {
      synchronized (_tasks) {
         final Iterator<Task> iterator = _tasks.iterator();
         while (iterator.hasNext()) {
            final Task task = iterator.next();
            if (task._taskID == taskID) {
               iterator.remove();
            }
         }
      }
   }


   @Override
   public void setPriority(final int taskID,
                           final int priority) {
      synchronized (_tasks) {
         for (final Task task : _tasks) {
            if (task._taskID == taskID) {
               task._priority = priority;
            }
         }
      }
   }


   private void sessionClosed(final int sessionID) {
      synchronized (_lastSendsTimestamps) {
         _lastSendsTimestamps.remove(Integer.valueOf(sessionID));
      }
   }


   @SuppressWarnings("unused")
   public static void main(final String[] args) {
      System.out.println("GPointsStreamingServer 0.1");
      System.out.println("--------------------------\n");


      if ((args.length < 1) || (args.length > 2)) {
         System.err.println("Usage: " + GPointsStreamingServer.class + " pointsCloudDirectoryName [<port>]");
         return;
      }

      final String pointsCloudDirectoryName = args[0];
      final int port = (args.length >= 2) ? Integer.parseInt(args[1]) : 8000;

      final GPointsStreamingServer model = new GPointsStreamingServer(pointsCloudDirectoryName);

      new GDServer(port, model, true) {
         @Override
         public void channelClosed(final Channel channel,
                                   final int sessionID) {
            super.channelClosed(channel, sessionID);

            model.sessionClosed(sessionID);
         }
      };
   }


}
