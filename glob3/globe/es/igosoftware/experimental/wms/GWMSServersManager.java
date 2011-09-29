

package es.igosoftware.experimental.wms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import es.igosoftware.io.GFileName;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.logging.GLogger;
import es.igosoftware.logging.ILogger;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerStyle;
import gov.nasa.worldwind.wms.CapabilitiesRequest;


public class GWMSServersManager {

   private final static ILogger   logger           = GLogger.instance();
   private final static GFileName relativefileName = GFileName.relative("data", "wmsservers.dat");
   private final static String    fileName         = relativefileName.getName();

   protected final static int     CONNECTION_OK    = 1;
   protected final static int     INVALID_URL      = -1;
   protected final static int     XML_PARSE_ERROR  = -2;
   protected final static int     CONNECTION_ERROR = -3;


   //private final Set<GWMSServerData> _serversSet = new HashSet();


   /**
    * 
    */
   public GWMSServersManager() {

   }


   //   public static WMSCapabilities getCapabilitiesforServer(final String serverName) {
   //      return retrieveCapabilities(GWMSDefaultServers.getURL(serverName));
   //   }


   public static WMSCapabilities getCapabilitiesforServer(final GWMSServerData server) {

      return retrieveCapabilities(server.getURL());
   }


   private static WMSCapabilities retrieveCapabilities(final String uRL) {
      try {
         final CapabilitiesRequest request = new CapabilitiesRequest(new URI(uRL));
         logger.logInfo("GetCapabilities URI: " + request.getUri());
         //System.out.println();
         final WMSCapabilities caps = new WMSCapabilities(request);
         caps.parse();
         //TODO comprobar que soporta EPSG_4326, y si no lanzar error
         return caps;

      }
      catch (final URISyntaxException e) {
         logger.logInfo("Connection ERROR: Invalid URL");
         //e.printStackTrace();
      }
      catch (final MalformedURLException e) {
         logger.logInfo("Connection ERROR: Invalid URL");
         //e.printStackTrace();
      }
      catch (final XMLStreamException e) {
         logger.logInfo("Connection ERROR: Invalid XML stream");
         //e.printStackTrace();
      }
      catch (final WWRuntimeException e) {
         logger.logInfo("Server connection error");
         //e.printStackTrace();
      }

      return null;
   }


   public static GWMSLayerData[] getLayersForServer(final GWMSServerData server) {

      final WMSCapabilities caps = retrieveCapabilities(server.getURL());
      if (caps != null) {
         try {
            caps.parse();
            //System.out.println(caps.getCapabilityInformation().toString());
            logger.logInfo(caps.getServiceInformation().toString());
            final List<WMSLayerCapabilities> layersCapsList = caps.getNamedLayers();
            final GWMSLayerData[] layersDataList = new GWMSLayerData[layersCapsList.size()];

            int index = 0;
            final StringBuffer layers = new StringBuffer("LAYERS: ");
            for (final WMSLayerCapabilities WMSLayer : layersCapsList) {
               layers.append(WMSLayer.getName());
               if (layersCapsList.indexOf(WMSLayer) < layersCapsList.size() - 1) {
                  layers.append(", ");
               }

               final List<GWMSLayerStyleData> stylesList = new ArrayList<GWMSLayerStyleData>();
               final Set<WMSLayerStyle> stylesSet = WMSLayer.getStyles();

               for (final Object element : stylesSet) {
                  final WMSLayerStyle layerStyle = (WMSLayerStyle) element;
                  final GWMSLayerStyleData styleData = new GWMSLayerStyleData(layerStyle.getName(), layerStyle.getTitle(),
                           layerStyle.getStyleAbstract());
                  stylesList.add(styleData);
               }

               layersDataList[index] = new GWMSLayerData(Integer.toString(index), WMSLayer.getName(), WMSLayer.getTitle(),
                        WMSLayer.getLayerAbstract(), stylesList);
               index++;
            }
            logger.logInfo(layers.toString());
            return layersDataList;
         }
         catch (final XMLStreamException e) {
            logger.logInfo("Connection ERROR: invalid XML stream");
            //e.printStackTrace();
         }
      }

      return null;

   }


   public static String[] getWMSServersNames(final Map<String, GWMSServerData> serversMap) {

      final String[] serversNames = new String[serversMap.size()];
      final Collection<GWMSServerData> serversCollection = serversMap.values();

      int index = 0;
      for (final GWMSServerData serverData : serversCollection) {
         serversNames[index] = serverData.getName();
         index++;
      }

      return serversNames;
   }


   public static Map<String, GWMSServerData> getWMSAvailableServers() {

      final Map<String, GWMSServerData> serversMap = new HashMap<String, GWMSServerData>();
      final List<GWMSServerData> serversList = loadWMSServers();

      for (final GWMSServerData server : serversList) {
         serversMap.put(server.getName(), server);
      }

      return serversMap;
   }


   protected static List<GWMSServerData> loadWMSServers() {

      logger.logInfo("Loading WMS server list..");

      final File serversFile = new File(fileName);
      final List<GWMSServerData> emptyList = Collections.emptyList();

      if (serversFile.exists()) {

         if (serversFile.length() > 0) {

            ObjectInputStream input = null;
            try {
               input = new ObjectInputStream(new FileInputStream(fileName));
               @SuppressWarnings("unchecked")
               final List<GWMSServerData> serversList = (List<GWMSServerData>) input.readObject();

               return serversList;

            }
            catch (final IOException e) {
               logger.logInfo("IOException !");
               e.printStackTrace();
            }
            catch (final ClassNotFoundException e) {
               logger.logInfo("Class Not found Exception !");
               e.printStackTrace();
            }
            finally {
               GIOUtils.gentlyClose(input);
            }
         }

         return emptyList;

      }

      try {
         serversFile.createNewFile();
      }
      catch (final IOException e) {
         System.out.println("Invalid file: " + fileName);
         e.printStackTrace();
      }

      return emptyList;

   }


   public static void saveWMSServers(final Map<String, GWMSServerData> serversMap) {

      logger.logInfo("Saving " + serversMap.size() + " WMS servers");

      final Collection<GWMSServerData> serversCollection = serversMap.values();
      final List<GWMSServerData> serversList = new ArrayList<GWMSServerData>(serversCollection.size());
      serversList.addAll(serversCollection);

      ObjectOutputStream output = null;
      try {
         output = new ObjectOutputStream(new FileOutputStream(fileName));
         output.writeObject(serversList);
         logger.logInfo("Saved!");
      }
      catch (final IOException e) {
         logger.logInfo("IOException !");
         e.printStackTrace();
      }
      finally {
         GIOUtils.gentlyClose(output);
      }
   }
}
