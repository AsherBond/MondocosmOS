

package es.igosoftware.euclid.experimental.vectorial.rendering.coloring;

import java.util.HashMap;

import es.igosoftware.euclid.colors.IColor;


public class GUniqueValuesDataSet {

   public final IColor                  _defaultColor;
   public final GColorScheme            _colorScheme;
   public final HashMap<String, IColor> _colors;
   public final String                  _fieldName;


   public GUniqueValuesDataSet(final IColor defaultColor,
                               final GColorScheme colorScheme,
                               final String fieldName,
                               final HashMap<String, IColor> colors) {

      _defaultColor = defaultColor;
      _colorScheme = colorScheme;
      _fieldName = fieldName;
      _colors = colors;

   }

}
