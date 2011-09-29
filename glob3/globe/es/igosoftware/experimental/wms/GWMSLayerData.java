

package es.igosoftware.experimental.wms;

import java.util.List;


public class GWMSLayerData {

   private String                   _id;
   private String                   _name;
   private String                   _title;
   private String                   _abstract;
   private List<GWMSLayerStyleData> _styles;


   public GWMSLayerData() {

   }


   public GWMSLayerData(final String id,
                        final String name,
                        final String title,
                        final String layerAbstract,
                        final List<GWMSLayerStyleData> styles) {
      super();
      _id = id;
      _name = name;
      _title = title;
      _abstract = layerAbstract;
      _styles = styles;
   }


   /**
    * @return the id
    */
   public String getId() {
      return _id;
   }


   /**
    * @param id
    *           the id to set
    */
   public void setId(final String id) {
      _id = id;
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
    * @return the title
    */
   public String getTitle() {
      return _title;
   }


   /**
    * @param title
    *           the title to set
    */
   public void setTitle(final String title) {
      _title = title;
   }


   /**
    * @return the abstract
    */
   public String getAbstract() {
      return _abstract;
   }


   /**
    * @param abstract1
    *           the abstract to set
    */
   public void setAbstract(final String layerAbstract) {
      _abstract = layerAbstract;
   }


   /**
    * @return the styles
    */
   public List<GWMSLayerStyleData> getStyles() {
      return _styles;
   }


   /**
    * @param styles
    *           the styles to set
    */
   public void setStyles(final List<GWMSLayerStyleData> styles) {
      _styles = styles;
   }


   public String[] getStylesNames() {

      final String[] styleNames = new String[_styles.size()];

      int index = 0;
      for (final GWMSLayerStyleData style : _styles) {
         styleNames[index] = style.getStyleName();
         index++;
      }

      return styleNames;
   }


   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      final StringBuffer b = new StringBuffer();
      for (final String s : getStylesNames()) {
         b.append(s);
         b.append(", ");
      }
      return "GWMSLayerData [_id=" + _id + ", _name=" + _name + ", _title=" + _title + ", _abstract=" + _abstract + ", _styles="
             + b.toString() + "]";
   }

}
