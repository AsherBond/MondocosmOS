

package es.igosoftware.dmvc.transferring;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import es.igosoftware.dmvc.model.GDModel;
import es.igosoftware.dmvc.model.IDAsynchronousExecutionListener;
import es.igosoftware.dmvc.model.IDProperty;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.util.GAssert;


public class GDFileServer
         extends
            GDModel
         implements
            IDFileServer {


   private final File _rootDirectory;


   public GDFileServer(final String rootPath) {
      GAssert.notNull(rootPath, "rootPath");

      _rootDirectory = initializeRootDirectory(rootPath);
   }


   private File initializeRootDirectory(final String rootPath) {
      final File file = new File(rootPath);

      if (!file.exists()) {
         throw new RuntimeException("Root Directory (" + file.getAbsolutePath() + ") doesn't exist");
      }
      if (!file.canRead()) {
         throw new RuntimeException("Root Directory (" + file.getAbsolutePath() + ") is not readable");
      }

      if (!file.isDirectory()) {
         throw new RuntimeException("Root Directory (" + file.getAbsolutePath() + ") is not a directory");
      }

      if (file.list().length == 0) {
         throw new RuntimeException("Root Directory (" + file.getAbsolutePath() + ") is empty");
      }

      return file;
   }


   @Override
   protected List<IDProperty> defaultProperties() {
      return Collections.emptyList();
   }


   @Override
   public void getFile(final GDFileRequest request,
                       final IDAsynchronousExecutionListener<GDFileResponse, RuntimeException> listener) {
      listener.evaluated(createResponse(request), null);
   }


   private GDFileResponse createResponse(final GDFileRequest request) {
      final File file = new File(_rootDirectory, request.getFileName().buildPath());

      if (!file.exists() || !file.canRead()) {
         return new GDFileResponse(GDFileResponse.Status.FILE_NOT_FOUND);
      }

      if (!isInRootDirectory(file)) {
         return new GDFileResponse(GDFileResponse.Status.FILE_NOT_FOUND);
      }


      BufferedInputStream is = null;
      try {
         final long fileLength = file.length();
         if (fileLength > Integer.MAX_VALUE) {
            return new GDFileResponse(GDFileResponse.Status.FILE_TOO_BIG);
         }

         is = new BufferedInputStream(new FileInputStream(file));

         final byte[] bytes = new byte[(int) fileLength];

         int i = 0;
         int read;
         while ((read = is.read()) != -1) {
            bytes[i++] = (byte) read;
         }

         if (i != fileLength) {
            return new GDFileResponse(GDFileResponse.Status.CANT_READ_FILE);
         }

         return new GDFileResponse(GDFileResponse.Status.OK, bytes, file.lastModified());
      }
      catch (final IOException e) {
         return new GDFileResponse(e);
      }
      finally {
         GIOUtils.gentlyClose(is);
      }
   }


   private boolean isInRootDirectory(final File file) {
      // Check if the file recides insides the root directory. 
      return file.getAbsolutePath().startsWith(_rootDirectory.getAbsolutePath());
   }

}
