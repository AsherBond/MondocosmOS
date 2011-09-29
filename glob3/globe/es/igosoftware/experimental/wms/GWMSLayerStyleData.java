

package es.igosoftware.experimental.wms;

public class GWMSLayerStyleData {

   private String _name;
   private String _title;
   private String _styleAbstract;


   public GWMSLayerStyleData() {

   }


   /**
    * @param name
    * @param title
    * @param styleAbstract
    */
   public GWMSLayerStyleData(final String name,
                             final String title,
                             final String styleAbstract) {
      //super();
      _name = name;
      _title = title;
      _styleAbstract = styleAbstract;
   }


   /**
    * @return the styleName
    */
   public String getStyleName() {
      return _name;
   }


   /**
    * @param name
    *           the styleName to set
    */
   public void setStyleName(final String name) {
      _name = name;
   }


   /**
    * @return the styleTitle
    */
   public String getStyleTitle() {
      return _title;
   }


   /**
    * @param title
    *           the styleTitle to set
    */
   public void setStyleTitle(final String title) {
      _title = title;
   }


   /**
    * @return the styleAbstract
    */
   public String getStyleAbstract() {
      return _styleAbstract;
   }


   /**
    * @param styleAbstract
    *           the styleAbstract to set
    */
   public void setStyleAbstract(final String styleAbstract) {
      _styleAbstract = styleAbstract;
   }


}
