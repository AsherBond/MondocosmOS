

package es.igosoftware.euclid.experimental.vectorial.rendering.coloring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.igosoftware.util.GAssert;


public class GColorSchemeSet {

   private final String             _name;
   private final List<GColorScheme> _schemes = new ArrayList<GColorScheme>();


   public GColorSchemeSet(final String name) {
      GAssert.notNull(name, "");
      _name = name;
   }


   public void add(final GColorScheme scheme) {
      for (final GColorScheme current : _schemes) {
         if (current.getName().equals(scheme.getName()) && (current.getType() == scheme.getType())
             && (current.getDimensions() == scheme.getDimensions())) {
            throw new RuntimeException("Already exist a scheme similar to the given one");
         }
      }

      _schemes.add(scheme);
   }


   public List<GColorScheme> getSchemes(final int dimensions,
                                        final GColorScheme.Type type) {
      final ArrayList<GColorScheme> result = new ArrayList<GColorScheme>();

      for (final GColorScheme scheme : _schemes) {
         if ((scheme.getDimensions() == dimensions) && (scheme.getType() == type)) {
            result.add(scheme);
         }
      }

      result.trimToSize();
      return Collections.unmodifiableList(result);
   }


   public List<GColorScheme> getSchemes(final String name) {
      final ArrayList<GColorScheme> result = new ArrayList<GColorScheme>();

      for (final GColorScheme scheme : _schemes) {
         if (scheme.getName().equals(name)) {
            result.add(scheme);
         }
      }

      result.trimToSize();
      return Collections.unmodifiableList(result);
   }


   public List<GColorScheme> getSchemes() {
      return Collections.unmodifiableList(_schemes);
   }


   public String getName() {
      return _name;
   }

}
