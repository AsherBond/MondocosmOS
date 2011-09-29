

package es.igosoftware.globe;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.io.GFileName;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GPair;
import es.igosoftware.util.IFunction;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.layers.Layer;


public class GDragAndDropModule
         implements
            IGlobeModule {


   public static interface IDropFileHandler {

      public String getDescription();


      public boolean acceptDirectories();


      public boolean acceptFile(final File droppedFile);


      public boolean processFile(final File droppedFile);

   }


   private DropTarget                   _dropTarget = null;
   private DataFlavor                   _uriListFlavor;

   private final List<IDropFileHandler> _handlers;


   public GDragAndDropModule(final IDropFileHandler handler,
                             final IDropFileHandler... extraHandlers) {
      GAssert.notNull(handler, "handler");

      _handlers = new ArrayList<IDropFileHandler>();
      _handlers.add(handler);
      for (final IDropFileHandler extraHandler : extraHandlers) {
         GAssert.notNull(extraHandler, "extraHandler");
         _handlers.add(extraHandler);
      }

      try {
         _uriListFlavor = new DataFlavor("text/uri-list;class=java.lang.String");
      }
      catch (final ClassNotFoundException e) {
         _uriListFlavor = null;
      }
   }


   private static List<File> textURIListToFileList(final String data) {
      final List<File> list = new ArrayList<File>(1);

      for (final StringTokenizer st = new StringTokenizer(data, "\r\n"); st.hasMoreTokens();) {
         final String s = st.nextToken();
         if (s.startsWith("#")) {
            // the line is a comment (as per the RFC 2483)
            continue;
         }
         try {
            final URI uri = new URI(s);
            final File file = new File(uri);
            list.add(file);
         }
         catch (final URISyntaxException e) {
            // malformed URI
         }
         catch (final IllegalArgumentException e) {
            // the URI is not a valid 'file:' URI
         }
      }

      return list;
   }


   @Override
   public void initialize(final IGlobeRunningContext context) {

      final DropTargetListener listener = new DropTargetListener() {

         @Override
         public void dropActionChanged(final DropTargetDragEvent event) {
         }


         @SuppressWarnings("unchecked")
         @Override
         public void drop(final DropTargetDropEvent event) {
            event.acceptDrop(DnDConstants.ACTION_COPY);

            final Transferable transferable = event.getTransferable();

            List<File> files = null;

            try {
               if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                  files = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
               }
               else if ((_uriListFlavor != null) && transferable.isDataFlavorSupported(_uriListFlavor)) {
                  final String stringData = (String) transferable.getTransferData(_uriListFlavor);
                  files = textURIListToFileList(stringData);
               }
            }
            catch (final IOException e) {
               e.printStackTrace();
            }
            catch (final UnsupportedFlavorException e) {
               e.printStackTrace();
            }

            if ((files != null) && !files.isEmpty()) {
               if (acceptDroppedFiles(context, files)) {
                  event.dropComplete(true);
               }
               else {
                  event.dropComplete(false);
               }
            }
         }


         @Override
         public void dragOver(final DropTargetDragEvent event) {

         }


         @Override
         public void dragExit(final DropTargetEvent event) {

         }


         @Override
         public void dragEnter(final DropTargetDragEvent event) {
            event.acceptDrag(DnDConstants.ACTION_COPY);
         }
      };

      final WorldWindowGLCanvas canvas = context.getWorldWindModel().getWorldWindowGLCanvas();
      _dropTarget = new DropTarget(canvas, listener);
   }


   @Override
   public void finalize(final IGlobeRunningContext context) {
      _dropTarget.setActive(false);
   }


   @Override
   public String getName() {
      return "Drag & Drop support module";
   }


   @Override
   public String getVersion() {
      return "0.1";
   }


   @Override
   public String getDescription() {
      return "Module to handle Drop of file from the host operative system into glob3";
   }


   @Override
   public List<? extends IGenericAction> getGenericActions(final IGlobeRunningContext context) {
      return null;
   }


   @Override
   public List<? extends ILayerAction> getLayerActions(final IGlobeRunningContext context,
                                                       final Layer layer) {
      return null;
   }


   @Override
   public List<? extends ILayerAttribute<?>> getLayerAttributes(final IGlobeRunningContext context,
                                                                final Layer layer) {
      return null;
   }


   @Override
   public List<GPair<String, Component>> getPanels(final IGlobeRunningContext context) {
      return null;
   }


   @Override
   public void initializeTranslations(final IGlobeRunningContext context) {

   }


   private boolean acceptDroppedFiles(final IGlobeRunningContext context,
                                      final List<File> files) {
      boolean processed = false;
      for (final File file : files) {
         if (file == null) {
            continue;
         }

         final List<IDropFileHandler> selectedHandlers = selectHandlers(file);

         if (selectedHandlers.isEmpty()) {
            System.out.println("No DropFileHandler found for: " + file);
            continue;
         }

         final IDropFileHandler selectedHandler = chooseOnlyOneHandler(context, file, selectedHandlers);

         if (selectedHandler == null) {
            continue;
         }

         if (selectedHandler.processFile(file)) {
            processed = true;
         }
      }

      return processed;

   }


   private List<IDropFileHandler> selectHandlers(final File file) {
      final List<IDropFileHandler> result = new ArrayList<IDropFileHandler>();

      final boolean isDirectory = file.isDirectory();
      for (final IDropFileHandler handler : _handlers) {
         if (!isDirectory || handler.acceptDirectories()) {
            if (handler.acceptFile(file)) {
               result.add(handler);
            }
         }
      }

      return result;
   }


   private static class DropFileHandlerWrapper {
      private final IDropFileHandler     _handler;
      private final IGlobeRunningContext _context;


      private DropFileHandlerWrapper(final IGlobeRunningContext context,
                                     final IDropFileHandler handler) {
         _handler = handler;
         _context = context;
      }


      @Override
      public String toString() {
         return _context.getTranslator().getTranslation(_handler.getDescription());
      }
   }


   private IDropFileHandler chooseOnlyOneHandler(final IGlobeRunningContext context,
                                                 final File file,
                                                 final List<IDropFileHandler> selectedHandlers) {

      if (selectedHandlers.size() == 1) {
         return selectedHandlers.get(0);
      }

      //      final GGlobeApplication application = GGlobeApplication.instance();

      final List<DropFileHandlerWrapper> wrappers = GCollections.collect(selectedHandlers,
               new IFunction<IDropFileHandler, DropFileHandlerWrapper>() {
                  @Override
                  public DropFileHandlerWrapper apply(final IDropFileHandler element) {
                     return new DropFileHandlerWrapper(context, element);
                  }
               });

      final IGlobeTranslator translator = context.getTranslator();

      final DropFileHandlerWrapper selectedHandler = (DropFileHandlerWrapper) JOptionPane.showInputDialog(
               context.getApplication().getFrame(), translator.getTranslation("Select an option for: " + file),
               translator.getTranslation("Choose an option"), JOptionPane.PLAIN_MESSAGE,
               context.getBitmapFactory().getIcon(GFileName.relative("add.png"), 32, 32), wrappers.toArray(), null);

      return (selectedHandler == null) ? null : selectedHandler._handler;
   }


}
