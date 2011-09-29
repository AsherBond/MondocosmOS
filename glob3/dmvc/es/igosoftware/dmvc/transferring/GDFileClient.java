

package es.igosoftware.dmvc.transferring;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.igosoftware.dmvc.model.IDAsynchronousExecutionListener;
import es.igosoftware.io.GFileName;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.logging.GLogger;
import es.igosoftware.logging.ILogger;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GProcessor;


public class GDFileClient
         implements
            IDFileClient {

   private final class ResponseHandler
            implements
               IDAsynchronousExecutionListener<GDFileResponse, RuntimeException> {

      private final GFileName        _fileName;
      private final GProcessor<File> _processor;


      private ResponseHandler(final GFileName fileName,
                              final GProcessor<File> processor) {
         _fileName = fileName;
         _processor = processor;
      }


      @Override
      public void evaluated(final GDFileResponse response,
                            final RuntimeException exception) {

         File fileToProcess;
         if (response.getStatus() == GDFileResponse.Status.OK) {
            try {
               saveResponse(response);

               fileToProcess = new File(_cacheDirectory, _fileName.buildPath());
            }
            catch (final IOException e) {
               LOGGER.logSevere(e);
               fileToProcess = null;
            }
         }
         else {
            LOGGER.logSevere(response.toString());
            fileToProcess = null;
         }

         synchronized (_downloading) {
            _downloading.remove(_fileName);
         }

         processDownloadedFile(_fileName, fileToProcess, _processor);
      }


      private void saveResponse(final GDFileResponse response) throws IOException {
         final File file = new File(_cacheDirectory, _fileName.buildPath());
         final File parentFile = file.getParentFile();
         if (!parentFile.exists()) {
            if (!parentFile.mkdirs()) {
               throw new IOException("Can't create directory \"" + parentFile.getAbsolutePath() + "\"");
            }
         }

         BufferedOutputStream os = null;
         try {
            os = new BufferedOutputStream(new FileOutputStream(file));

            os.write(response.getBytes());

            os.flush();
         }
         finally {
            GIOUtils.gentlyClose(os);

            if (!file.setLastModified(response.getLastModified())) {
               throw new IOException("Can't change the lastModified attribute on \"" + file.getAbsolutePath() + "\"");
            }
         }
      }
   }


   private static final ILogger                         LOGGER                     = GLogger.instance();


   private final IDFileServer                           _fileServer;
   private final File                                   _cacheDirectory;

   private final Set<GFileName>                         _downloading               = new HashSet<GFileName>();
   private final Map<GFileName, List<GProcessor<File>>> _extraDownloadedProcessors = new HashMap<GFileName, List<GProcessor<File>>>();


   public GDFileClient(final IDFileServer fileServer,
                       final String cacheDirectoryName) {
      GAssert.notNull(fileServer, "fileServer");
      GAssert.notNull(cacheDirectoryName, "cacheDirectoryName");

      _fileServer = fileServer;
      _cacheDirectory = initializeCache(cacheDirectoryName);
   }


   private File initializeCache(final String cacheDirectoryName) {
      final File directory = new File(cacheDirectoryName);

      if (!directory.exists()) {
         if (!directory.mkdirs()) {
            throw new RuntimeException("Can't create directory \"" + cacheDirectoryName + "\"");
         }
      }

      return directory;
   }


   @Override
   public File getFile(final GFileName fileName) {

      synchronized (_downloading) {
         final boolean isDownloading = _downloading.contains(fileName);

         if (isDownloading) {
            return null;
         }

         final File file = new File(_cacheDirectory, fileName.buildPath());
         if (file.exists()) {
            return file;
         }

         _downloading.add(fileName);
      }

      final GDFileRequest request = new GDFileRequest(fileName);
      _fileServer.getFile(request, new ResponseHandler(fileName, null));

      return null;
   }


   @Override
   public void getFile(final GFileName fileName,
                       final GProcessor<File> processor) {

      synchronized (_downloading) {
         final boolean isDownloading = _downloading.contains(fileName);

         if (isDownloading) {
            if (processor != null) {
               addExtraDownloadedProcessor(fileName, processor);
            }
            return;
         }

         final File file = new File(_cacheDirectory, fileName.buildPath());
         if (file.exists()) {
            if (processor != null) {
               processor.process(file);
            }
            return;
         }

         _downloading.add(fileName);
      }

      final GDFileRequest request = new GDFileRequest(fileName);
      _fileServer.getFile(request, new ResponseHandler(fileName, processor));

   }


   private void addExtraDownloadedProcessor(final GFileName fileName,
                                            final GProcessor<File> processor) {
      synchronized (_extraDownloadedProcessors) {
         List<GProcessor<File>> currentProcessors = _extraDownloadedProcessors.get(fileName);

         if (currentProcessors == null) {
            currentProcessors = new ArrayList<GProcessor<File>>();
            _extraDownloadedProcessors.put(fileName, currentProcessors);
         }

         currentProcessors.add(processor);
      }
   }


   private void processDownloadedFile(final GFileName fileName,
                                      final File fileOrNull,
                                      final GProcessor<File> firstProcessor) {

      //      GUtils.delay(10000);

      if (firstProcessor != null) {
         firstProcessor.process(fileOrNull);
      }


      final List<GProcessor<File>> processors;
      synchronized (_extraDownloadedProcessors) {
         processors = _extraDownloadedProcessors.remove(fileName);
      }

      if (processors != null) {
         for (final GProcessor<File> each : processors) {
            each.process(fileOrNull);
         }
      }
   }


}
