

package es.igosoftware.experimental.wms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public enum GWMSDefaultServers {

   //   PNOA("PNOA", "http://www.idee.es/wms/PNOA/PNOA", "", ""),
   //   CATASTRO("CATASTRO", "http://ovc.catastro.meh.es/Cartografia/WMS/ServidorWMS.aspx", "", ""),
   //   NASA("NASA", "http://wms.jpl.nasa.gov/wms.cgi", "", ""),
   //   DM_SOLUTIONS("DM SOLUTIONS", "http://www2.dmsolutions.ca/cgi-bin/mswms_gmap", "", ""),
   //   CLIMA_IBERIA("CLIMA_IBERIA", "http://www.opengis.uab.es/cgi-bin/iberia/Miramon5_0.cgi?", "", ""),
   //   SIGPAC("SIGPAC", "http://wms.mapa.es/wms/wms.aspx?", "", ""),
   //   IGN_CORINE("IGN_CORINE", "http://www.idee.es/wms/IGN-Corine/IGN-Corine?", "", ""),
   //   IGN_IDEE("IGN_IDEE", "http://www.idee.es/wms/IDEE-Base/IDEE-Base?", "", "");

   // -- for AtlasElektronikDemo 
   GERMANY("GERMANY", "http://wms1.ccgis.de/cgi-bin/mapserv?map=/data/umn/germany/germany.map&", "", ""),
   NAUTHIS("NAUTHIS", "http://gdisrv.bsh.de/arcgis/services/Nauthis/NavigationalAids/MapServer/WMSServer", "", ""),
   METASPATIAL("METASPATIAL", "http://www.metaspatial.net/cgi-bin/germany-wms?", "", "");

   //   private final String _name;
   //   private final String _URL;
   //   private final String _user;
   //   private final String _password;
   private final GWMSServerData _serverData;


   /**
    * @param name
    * @param uRL
    */
   private GWMSDefaultServers(final String name,
                              final String uRL) {

      _serverData = new GWMSServerData(name, uRL);
   }


   /**
    * @param name
    * @param uRL
    * @param user
    * @param password
    */
   private GWMSDefaultServers(final String name,
                              final String uRL,
                              final String user,
                              final String password) {

      _serverData = new GWMSServerData(name, uRL, user, password);
   }


   /**
    * @return the server
    */
   public GWMSServerData getServer() {

      return _serverData;
   }


   /**
    * @return the name
    */
   public String getName() {
      return _serverData.getName();
   }


   /**
    * @return the uRL
    */
   public String getURL() {
      return _serverData.getURL();
   }


   /**
    * @return the user
    */
   public String getUser() {
      return _serverData.getUser();
   }


   /**
    * @return the password
    */
   public String getPassword() {
      return _serverData.getPassword();
   }


   /**
    * @return the server
    */
   public static GWMSServerData getServer(final String name) {
      for (final GWMSDefaultServers server : GWMSDefaultServers.values()) {
         if (server._serverData.getName().equals(name)) {
            return server._serverData;
         }
      }

      return null;
   }


   /**
    * @return the servers list
    */
   public static GWMSServerData[] getDefaultServersList() {

      final GWMSDefaultServers[] serverList = GWMSDefaultServers.values();
      final GWMSServerData[] serverDataList = new GWMSServerData[serverList.length];
      int index = 0;
      for (final GWMSDefaultServers server : serverList) {
         serverDataList[index] = server.getServer();
         index++;
      }

      return serverDataList;
   }


   /**
    * @return the servers set
    */
   public static Set<GWMSServerData> getDefaultServersSet() {

      final GWMSDefaultServers[] serverList = GWMSDefaultServers.values();
      final Set<GWMSServerData> serverDataSet = new HashSet<GWMSServerData>(serverList.length);
      for (final GWMSDefaultServers server : serverList) {
         serverDataSet.add(server.getServer());
      }

      return serverDataSet;
   }


   /**
    * @return the free access servers list
    */
   public static GWMSServerData[] getFreeAccessServersList() {

      final GWMSDefaultServers[] serverList = GWMSDefaultServers.values();
      final ArrayList<GWMSServerData> serverDatalist = new ArrayList<GWMSServerData>();
      for (final GWMSDefaultServers server : serverList) {
         if (server.getUser().equals("")) {
            serverDatalist.add(server.getServer());
         }
      }

      return serverDatalist.toArray(new GWMSServerData[0]);
   }


   /**
    * @return the free access servers list
    */
   public static GWMSServerData[] getRestrictedAccessServersList() {

      final GWMSDefaultServers[] serverList = GWMSDefaultServers.values();
      final ArrayList<GWMSServerData> serverDatalist = new ArrayList<GWMSServerData>();
      for (final GWMSDefaultServers server : serverList) {
         if (server.getUser() != "") {
            serverDatalist.add(server.getServer());
         }
      }

      return serverDatalist.toArray(new GWMSServerData[0]);
   }


   /**
    * @return the servers names list
    */
   public static String[] getDefaultServersNamesList() {

      final GWMSDefaultServers[] serverList = GWMSDefaultServers.values();
      final String[] serverNameList = new String[serverList.length];
      int index = 0;
      for (final GWMSDefaultServers server : serverList) {
         serverNameList[index] = server.getServer().getName();
         index++;
      }

      return serverNameList;
   }


   /**
    * @return the uRL
    */
   public static String getURL(final String name) {
      for (final GWMSDefaultServers server : GWMSDefaultServers.values()) {
         if (server._serverData.getName().equals(name)) {
            return server._serverData.getURL();
         }
      }

      return null;
   }


   /**
    * @return the name
    */
   public static String getName(final String uRL) {
      for (final GWMSDefaultServers server : GWMSDefaultServers.values()) {
         if (server._serverData.getURL().equals(uRL)) {
            return server._serverData.getName();
         }
      }

      return null;
   }


}
