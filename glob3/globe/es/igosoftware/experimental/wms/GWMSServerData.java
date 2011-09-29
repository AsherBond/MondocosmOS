

package es.igosoftware.experimental.wms;

import java.io.Serializable;


public final class GWMSServerData
         implements
            Serializable {

   /**
    * 
    */
   private static final long serialVersionUID = 1L;


   private String            _name;
   private String            _URL;
   private String            _user;
   private String            _password;


   public GWMSServerData() {

   }


   public GWMSServerData(final String name,
                         final String uRL) {
      super();
      _name = name;
      _URL = uRL;
      _user = "";
      _password = "";
   }


   public GWMSServerData(final String name,
                         final String uRL,
                         final String user,
                         final String password) {
      super();
      if ((user == null) || (user.equals(""))) {
         _name = name;
         _URL = uRL;
         _user = "";
         _password = "";
      }
      else {
         _name = name;
         _URL = uRL;
         _user = user;
         _password = password;
      }

   }


   /**
    * @return the name
    */
   public String getName() {
      return _name;
   }


   /**
    * @param name
    *           the name to set
    */
   public void setName(final String name) {
      _name = name;
   }


   /**
    * @return the uRL
    */
   public String getURL() {
      return _URL;
   }


   /**
    * @param uRL
    *           the uRL to set
    */
   public void setURL(final String uRL) {
      _URL = uRL;
   }


   /**
    * @return the user
    */
   public String getUser() {
      return _user;
   }


   /**
    * @param user
    *           the user to set
    */
   public void setUser(final String user) {
      _user = user;
   }


   /**
    * @return the password
    */
   public String getPassword() {
      return _password;
   }


   /**
    * @param password
    *           the password to set
    */
   public void setPassword(final String password) {
      _password = password;
   }


   //   @Override
   //   public boolean equals(final Object obj) {
   //
   //      if (getClass() != obj.getClass()) {
   //         return false;
   //      }
   //
   //      final GWMSServerData server = (GWMSServerData) obj;
   //      System.out.println("POS ESTOY EJECUTANDO AQUI !!!!!!!!!!!!");
   //      if ((_name.compareTo(server._name) == 0) && (_user.compareTo(server._user) == 0) && (_URL.compareTo(server._URL) == 0)
   //          && (_password.compareTo(server._password) == 0)) {
   //         return true;
   //      }
   //
   //      return false;
   //   }


}
