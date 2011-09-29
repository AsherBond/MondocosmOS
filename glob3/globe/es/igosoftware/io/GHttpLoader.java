

package es.igosoftware.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import es.igosoftware.concurrent.GConcurrent;
import es.igosoftware.logging.GLogger;
import es.igosoftware.logging.ILogger;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GProgress;
import es.igosoftware.util.GStringUtils;
import es.igosoftware.util.GUtils;


public class GHttpLoader
         extends
            GAbstractLoader {


   private static final ILogger   logger                              = GLogger.instance();

   private static final int       MINIMUM_CONTENT_LENGTH_FOR_PROGRESS = 50 * 1024 /* 50Kb */;
   private static final int       DEFAULT_WORKERS_COUNT               = Math.max(GConcurrent.AVAILABLE_PROCESSORS * 2, 2 * 2);
   private static final GFileName DEFAULT_CACHE_DIRECTORY             = GIOUtils.getCacheDirectory("glob3-http-cache");


   private static class HandlerData {
      private final ILoader.LoadID   _loadID;
      private final ILoader.IHandler _handler;
      private final boolean          _reportIncompleteLoads;


      private HandlerData(final ILoader.LoadID loadID,
                          final ILoader.IHandler handler,
                          final boolean reportIncompleteLoads) {
         _loadID = loadID;
         _handler = handler;
         _reportIncompleteLoads = reportIncompleteLoads;
      }
   }


   private class Task {
      private final GFileName         _fileName;
      private int                     _priority;
      private final List<HandlerData> _handlersData  = new LinkedList<HandlerData>();

      private boolean                 _isCanceled    = false;
      private boolean                 _isDownloading = false;


      private Task(final GFileName fileName,
                   final int priority,
                   final HandlerData handlerData) {
         _fileName = fileName;
         _priority = priority;

         _handlersData.add(handlerData);
      }


      private void execute() {
         final long start = System.currentTimeMillis();

         final File cacheFile = getCacheFileFor(_fileName);

         final File cacheFileParentDirectory = cacheFile.getParentFile();
         if (!cacheFileParentDirectory.exists()) {
            synchronized (_rootCacheDirectory) {
               if (!cacheFileParentDirectory.exists()) {
                  if (!cacheFileParentDirectory.mkdirs()) {
                     notifyInternalError("can't create directory " + cacheFileParentDirectory);
                  }
               }
            }
         }

         File partFile = null;
         try {
            partFile = File.createTempFile(_fileName.buildPath(), ".part", _rootCacheDirectory);
         }
         catch (final IOException e) {
            notifyErrorToHandlers(e);
            return;
         }

         partFile.deleteOnExit(); // just in case...

         InputStream is = null;
         OutputStream out = null;
         try {
            final URL url = new URL(_rootURL, convertToURL(_fileName.buildPath('/')));

            //            is = new BufferedInputStream(url.openStream());
            final URLConnection connection = url.openConnection();

            final GProgress progress = getProgressFromConnection(url, connection);

            is = new BufferedInputStream(connection.getInputStream());

            out = new BufferedOutputStream(new FileOutputStream(partFile));

            copyDataToPartFile(is, out, partFile, progress);
            is.close();
            is = null;

            out.flush();
            out.close();
            out = null;


            if (!partFile.renameTo(cacheFile)) {
               notifyInternalError("can't rename " + partFile + " to " + cacheFile);
               return;
            }

            final long bytesLoaded = cacheFile.length();
            final long ellapsed = System.currentTimeMillis() - start;
            cacheMiss(bytesLoaded, ellapsed);

            if (!_isCanceled) {
               notifySuccessfullyLoadToHandlers(cacheFile, bytesLoaded, true);
            }
         }
         catch (final IOException e) {
            notifyErrorToHandlers(e);
         }
         finally {
            GIOUtils.gentlyClose(is);
            GIOUtils.gentlyClose(out);
         }
      }


      protected GProgress getProgressFromConnection(final URL url,
                                                   final URLConnection connection) {

         if (!(connection instanceof HttpURLConnection)) {
            logger.logWarning("url.openConnection() answered a connection BUT HttpURLConnection");
            return null;
         }

         final HttpURLConnection httpConnection = (HttpURLConnection) connection;
         httpConnection.setInstanceFollowRedirects(true);

         final int contentLength = httpConnection.getContentLength();

         final boolean createProcess = _verbose && (contentLength > MINIMUM_CONTENT_LENGTH_FOR_PROGRESS);
         return createProcess //
                             ? createProgress(url, contentLength) //
                             : null;
      }


      protected GProgress createProgress(final URL url,
                                         final int contentLength) {
         return new GProgress(contentLength, 5, true) {
            @Override
            public void informProgress(final long stepsDone,
                                       final double percent,
                                       final long elapsed,
                                       final long estimatedMsToFinish) {
               if (_verbose) {
                  logger.logInfo(url + ": " + progressString(stepsDone, percent, elapsed, estimatedMsToFinish));
               }
            }
         };
      }


      private void notifyInternalError(final String msg) {
         logger.logSevere(msg);
         notifyErrorToHandlers(new IOException(msg));
      }


      private String convertToURL(final String url) {
         return url.replace(" ", "%20");
      }


      private void copyDataToPartFile(final InputStream in,
                                      final OutputStream out,
                                      final File partFile,
                                      final GProgress progress) throws IOException {
         final byte[] buf = new byte[4096];
         int len;
         int read = 0;
         while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
            out.flush();
            read += len;

            if (progress != null) {
               progress.stepsDone(len);
            }

            if (_simulateSlowConnection) {
               GUtils.delay(250);
            }

            notifySuccessfullyLoadToHandlers(partFile, read, false);
         }

         if (progress != null) {
            progress.finish();
         }
      }


      private void notifySuccessfullyLoadToHandlers(final File cacheFile,
                                                    final long bytesLoaded,
                                                    final boolean completeLoaded) {
         if (completeLoaded) {
            removeTask();
         }

         synchronized (_handlersData) {
            for (final HandlerData handlerData : _handlersData) {
               final boolean reportIncompleteLoads = handlerData._reportIncompleteLoads;
               if (completeLoaded || reportIncompleteLoads) {
                  try {
                     handlerData._handler.loaded(cacheFile, bytesLoaded, completeLoaded);
                  }
                  catch (final Exception e) {
                     logger.logSevere("Error while notifying to " + handlerData._handler, e);
                  }
               }
            }

         }
      }


      private void notifyErrorToHandlers(final IOException e) {
         removeTask();

         synchronized (_handlersData) {
            for (final HandlerData handlerData : _handlersData) {
               try {
                  handlerData._handler.loadError(e);
               }
               catch (final Exception e2) {
                  logger.logSevere("Error while notifying loadError to " + handlerData._handler, e2);
               }
            }
         }
      }


      private void removeTask() {
         synchronized (_tasks) {
            // final int __remove_print;
            // System.out.println(">>>> removing " + _fileName + ", before: " + _tasks.size());
            _tasks.remove(_fileName);
            // System.out.println(">>>> removing " + _fileName + ", after: " + _tasks.size());
         }
      }


      private void cancel() {
         _isCanceled = true;
      }


      private void addHandler(final int priority,
                              final HandlerData handlerData) {
         synchronized (_handlersData) {
            _priority = _priority + priority;
            _handlersData.add(handlerData);
            _isCanceled = false;
         }
      }
   }


   private void cacheHit() {
      synchronized (_statisticsMutex) {
         _loadCounter++;
         _loadCacheHits++;

         tryToShowStatistics();
      }
   }


   private void cacheMiss(final long bytesLoaded,
                          final long ellapsedTimeInMS) {
      synchronized (_statisticsMutex) {
         _loadCounter++;
         _bytesDownloaded += bytesLoaded;
         _downloadEllapsedTime += ellapsedTimeInMS;

         tryToShowStatistics();
      }
   }


   private class Worker
            extends
               Thread
            implements
               UncaughtExceptionHandler {


      private Worker(final int id) {
         super("GHttpLoader " + _rootURL + ", worker #" + id);
         setDaemon(true);
         setPriority(MAX_PRIORITY);
         setUncaughtExceptionHandler(this);
      }


      @Override
      public void run() {
         try {
            while (true) {
               final Task task = selectTask();
               if (task != null) {
                  if (task._isCanceled) {
                     continue; // ignored the canceled task
                  }

                  task.execute();
               }
            }
         }
         catch (final InterruptedException e) {
            // do nothing, just exit from run()
         }
      }


      @Override
      public void uncaughtException(final Thread thread,
                                    final Throwable e) {
         logger.logSevere("Uncaught exception in thread " + thread, e);
      }


      private Task selectTask() throws InterruptedException {
         Task selected = null;

         synchronized (_tasks) {
            for (final Entry<GFileName, Task> entry : _tasks.entrySet()) {
               final Task currentTask = entry.getValue();
               if (!currentTask._isDownloading && !currentTask._isCanceled) {
                  if ((selected == null) || (currentTask._priority > selected._priority)) {
                     selected = currentTask;
                  }
               }
            }

            if (selected != null) {
               selected._isDownloading = true;
            }
         }

         if (selected == null) {
            Thread.sleep(25);
         }

         return selected;
      }
   }


   private final URL                  _rootURL;
   private final File                 _rootCacheDirectory;
   private final Map<GFileName, Task> _tasks                = new HashMap<GFileName, Task>();
   private final boolean              _verbose;
   private final boolean              _debug;
   private final boolean              _simulateSlowConnection;

   private final Object               _statisticsMutex      = new Object();
   private long                       _loadCounter          = 0;
   private long                       _loadCacheHits        = 0;
   private long                       _bytesDownloaded      = 0;
   private long                       _downloadEllapsedTime = 0;

   private int                        _loadID               = Integer.MIN_VALUE;


   public GHttpLoader(final URL root,
                      final boolean verbose,
                      final IProgressReporter progressReporter) {
      this(root, DEFAULT_WORKERS_COUNT, verbose, false, progressReporter);
   }


   public GHttpLoader(final URL root,
                      final int workersCount,
                      final boolean verbose,
                      final boolean debug,
                      final IProgressReporter progressReporter) {
      this(root, workersCount, verbose, debug, false, progressReporter);
   }


   public GHttpLoader(final URL root,
                      final int workersCount,
                      final boolean verbose,
                      final boolean debug,
                      final boolean simulateSlowConnection,
                      final IProgressReporter progressReporter) {
      super(progressReporter);

      GAssert.notNull(root, "root");
      GAssert.isPositive(workersCount, "workersCount");

      if (!root.getProtocol().equals("http")) {
         throw new RuntimeException("Only http URLs are supported (" + root.getProtocol() + ")");
      }

      _rootURL = root;
      _verbose = verbose;
      _debug = debug;
      _simulateSlowConnection = simulateSlowConnection;

      //      _rootCacheDirectory = new File(DEFAULT_CACHE_DIRECTORY, getDirectoryName(_rootURL));
      _rootCacheDirectory = GFileName.fromParentAndParts(DEFAULT_CACHE_DIRECTORY, getDirectoryName(_rootURL)).asFile();

      if (!_rootCacheDirectory.exists()) {
         if (!_rootCacheDirectory.mkdirs()) {
            throw new RuntimeException("Can't create cache directory \"" + _rootCacheDirectory.getAbsolutePath() + "\"");
         }
      }

      initializeWorkers(workersCount);
   }


   private static String getDirectoryName(final URL url) {
      String result = url.toString();

      if (result.endsWith("/")) {
         result = result.substring(0, result.length() - 1);
      }

      result = result.replace("http://", "");

      return GIOUtils.replaceIllegalFileNameCharacters(result);
   }


   private void initializeWorkers(final int workersCount) {
      for (int i = 0; i < workersCount; i++) {
         new Worker(i).start();
      }
   }


   @Override
   public ILoader.LoadID load(final GFileName fileName,
                              final long bytesToLoad,
                              final boolean reportIncompleteLoads,
                              final int priority,
                              final ILoader.IHandler handler) {
      GAssert.notNull(fileName, "fileName");
      GAssert.notNull(handler, "handler");

      if (bytesToLoad >= 0) {
         throw new RuntimeException("fragment downloading is not supported");
      }

      final String argsString = fileName + ", " + bytesToLoad + ", " + priority;
      if (_debug) {
         logger.logInfo("  -> DEBUG: load(" + argsString + ")");
      }


      // try to answer (synchronously) from the cache
      final File cacheFile = getCacheFileFor(fileName);
      if (cacheFile.exists()) {
         cacheHit();

         handler.loaded(cacheFile, cacheFile.length(), true);

         if (_debug) {
            logger.logInfo("  -> DEBUG: load(" + argsString + ") done from cache!");
         }

         return null;
      }

      // enqueue for asynchronously download
      synchronized (_tasks) {
         final ILoader.LoadID loadID = new ILoader.LoadID(_loadID++);
         final HandlerData handlerData = new HandlerData(loadID, handler, reportIncompleteLoads);

         final Task existingTask = _tasks.get(fileName);
         if (existingTask == null) {
            _tasks.put(fileName, new Task(fileName, priority, handlerData));
         }
         else {
            existingTask.addHandler(priority, handlerData);
         }

         return loadID;
      }
   }


   private void tryToShowStatistics() {
      if (_verbose && (_loadCounter != 0) && ((_loadCounter % 50) == 0)) {
         showStatistics();
      }
      else if (_debug) {
         showStatistics();
      }
   }


   private File getCacheFileFor(final GFileName fileName) {
      GAssert.notNull(fileName, "fileName");

      if (fileName.isAbsolute()) {
         throw new RuntimeException("Absolutes fileNames are not supported");
      }

      return new File(_rootCacheDirectory, fileName.buildPath());
   }


   private void showStatistics() {
      final double hitsPercent = (double) _loadCacheHits / _loadCounter;

      final String msg = "HttpLoader \"" + _rootURL + "\": " + //
                         "loads=" + _loadCounter + ", " + //
                         "cache hits=" + _loadCacheHits + " (" + GStringUtils.formatPercent(hitsPercent) + ")";

      if (_bytesDownloaded != 0) {
         final double throughput = (double) _bytesDownloaded / _downloadEllapsedTime * 1000;
         logger.logInfo(msg + ", downloaded=" + GStringUtils.getSpaceMessage(_bytesDownloaded) + ", throughput="
                        + GStringUtils.getSpaceMessage(throughput) + "/s");
      }
      else {
         logger.logInfo(msg);
      }
   }


   @Override
   public void cancelLoad(final ILoader.LoadID id) {
      synchronized (_tasks) {

         final Iterator<Entry<GFileName, Task>> tasksIterator = _tasks.entrySet().iterator();

         while (tasksIterator.hasNext()) {
            final Entry<GFileName, Task> entry = tasksIterator.next();
            final Task task = entry.getValue();

            synchronized (task._handlersData) {
               final Iterator<HandlerData> handlersDataIterator = task._handlersData.iterator();
               while (handlersDataIterator.hasNext()) {
                  final HandlerData handlerData = handlersDataIterator.next();
                  if (id.equals(handlerData._loadID)) {
                     handlersDataIterator.remove();
                  }
               }

               if (!task._isDownloading) {
                  if (task._handlersData.isEmpty()) {
                     tasksIterator.remove();
                     task.cancel();
                  }
               }
            }
         }
      }
   }


   @Override
   public void cancelAllLoads(final GFileName fileName) {
      synchronized (_tasks) {
         final Task task = _tasks.remove(fileName);
         if (task != null) {
            task.cancel();
         }
      }
   }


   @Override
   public boolean canLoadFromLocalCache(final GFileName fileName) {
      final File cacheFile = getCacheFileFor(fileName);
      final boolean result = cacheFile.exists();

      if (_debug) {
         logger.logInfo("  -> DEBUG: canLoadFromLocalCache(" + fileName + ") => " + result);
      }

      return result;
   }


   @Override
   public long runningTasksCount() {
      //      synchronized (_tasks) {
      //         long counter = 0;
      //
      //         for (final Entry<GFileName, Task> entry : _tasks.entrySet()) {
      //            if (!entry.getValue()._isCanceled) {
      //               counter++;
      //            }
      //         }
      //
      //         return counter;
      //
      //      }

      return _tasks.size();
   }


   @Override
   public void cleanCacheFor(final GFileName fileName) {
      final File cacheFile = getCacheFileFor(fileName);

      synchronized (_rootCacheDirectory) {
         if (cacheFile.exists()) {
            if (!cacheFile.delete()) {
               logger.logSevere("Can't remove cache entry for: " + fileName);
            }
         }
      }
   }


}
