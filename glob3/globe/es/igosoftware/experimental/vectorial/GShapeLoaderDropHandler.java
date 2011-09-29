

package es.igosoftware.experimental.vectorial;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import javax.swing.JOptionPane;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.globe.GDragAndDropModule;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.IGlobeTranslator;
import es.igosoftware.io.IProgressProducer;
import es.igosoftware.util.GAssert;


public class GShapeLoaderDropHandler
         implements
            GDragAndDropModule.IDropFileHandler,
            IProgressProducer {


   private final IGlobeRunningContext _context;
   private final boolean              _confirmOpen;
   private final boolean              _verbose;
   private final AtomicLong           _tasksCount = new AtomicLong(0);


   public GShapeLoaderDropHandler(final IGlobeRunningContext context,
                                  final boolean confirmOpen,
                                  final boolean verbose) {
      GAssert.notNull(context, "context");

      _context = context;
      _confirmOpen = confirmOpen;
      _verbose = verbose;

      context.getProgressReporter().register(this);
   }


   @Override
   public String getDescription() {
      return "Shape file loading";
   }


   @Override
   public boolean acceptDirectories() {
      return false;
   }


   @Override
   public boolean acceptFile(final File droppedFile) {
      return droppedFile.getName().toLowerCase().endsWith(".shp");
   }


   private boolean confirmOpenFile(final File file) {
      final IGlobeApplication application = _context.getApplication();
      final IGlobeTranslator translator = _context.getTranslator();

      final String[] options = {
                        translator.getTranslation("Yes"),
                        translator.getTranslation("No")
      };
      final String title = translator.getTranslation("Are you sure to open the file?");
      final String message = file.toString();

      final int answer = JOptionPane.showOptionDialog(application.getFrame(), message, title, JOptionPane.YES_NO_OPTION,
               JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

      return (answer == 0);
   }


   @Override
   public boolean processFile(final File droppedFile) {

      if (_confirmOpen && !confirmOpenFile(droppedFile)) {
         return false;
      }

      final Thread worker = new Thread() {
         @Override
         public void run() {
            // TODO: read projection or ask user
            final GProjection projection = GProjection.EPSG_4326;

            try {
               final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> featuresCollection = GShapeLoader.readFeatures(
                        droppedFile, projection);

               final GVectorial2DLayer layer = new GVectorial2DLayer(droppedFile.getName(), featuresCollection, _verbose);
               _context.getWorldWindModel().addLayer(layer);
               layer.doDefaultAction(_context);
            }
            catch (final IOException e) {
               _context.getLogger().logSevere("Error trying to load: " + droppedFile, e);
            }
            finally {
               _tasksCount.decrementAndGet();
            }
         }
      };
      worker.setPriority(Thread.MIN_PRIORITY);
      worker.setDaemon(true);
      _tasksCount.incrementAndGet();
      worker.start();

      return true;
   }


   @Override
   public long runningTasksCount() {
      return _tasksCount.get();
   }

}
