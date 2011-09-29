

package es.igosoftware.globe;


public interface IGlobeTranslator {

   public String getTranslation(final String string);


   public String getTranslation(final String language,
                                final String string);


   public String getCurrentLanguage();


   public void setCurrentLanguage(final String currentLanguage);


   public void addTranslation(final String language,
                              final String string,
                              final String translation);

}
