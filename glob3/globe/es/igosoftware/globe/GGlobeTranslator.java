

package es.igosoftware.globe;

import java.util.HashMap;
import java.util.Map;


final class GGlobeTranslator
         implements
            IGlobeTranslator {

   private final GGlobeApplication                _application;
   private final String                           _initialLanguage;
   private String                                 _currentLanguage;
   private final Map<String, Map<String, String>> _translationsSets;


   GGlobeTranslator(final GGlobeApplication application,
                    final String initialLanguage) {
      _application = application;
      _initialLanguage = initialLanguage;
      _currentLanguage = _initialLanguage;

      _translationsSets = _application.initializeTranslations();
   }


   @Override
   public String getTranslation(final String string) {
      return getTranslation(_currentLanguage, string);
   }


   @Override
   public String getTranslation(final String language,
                                final String string) {
      if (string == null) {
         return null;
      }

      final Map<String, String> translations = _translationsSets.get(language);
      if (translations == null) {
         if (!language.equals("en")) {
            _application.getLogger().logWarning("Can't find a translations-set for language \"" + language + "\"");
         }
         return string;
      }

      final String translation = translations.get(string);
      if (translation == null) {
         _application.getLogger().logWarning("Can't find a translation for \"" + string + "\" in language \"" + language + "\"");
         //         new Exception().printStackTrace();
         return string;
      }

      return translation;
   }


   @Override
   public String getCurrentLanguage() {
      return _currentLanguage;
   }


   @Override
   public void setCurrentLanguage(final String currentLanguage) {
      if (_currentLanguage.equals(currentLanguage)) {
         return;
      }

      _currentLanguage = currentLanguage;
   }


   @Override
   public void addTranslation(final String language,
                              final String string,
                              final String translation) {
      Map<String, String> translations = _translationsSets.get(language);
      if (translations == null) {
         translations = new HashMap<String, String>();
         _translationsSets.put(language, translations);
      }
      translations.put(string, translation);
   }


}
