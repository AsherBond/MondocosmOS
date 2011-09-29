

package es.igosoftware.euclid.experimental.vectorial.rendering.coloring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.util.GAssert;


public class GColorScheme {


   public static enum Type {
      Qualitative,
      Sequential,
      Diverging
   }


   public static String[] getTypeList() {

      final Type[] typeValues = Type.values();
      final String[] typeList = new String[typeValues.length];

      for (int index = 0; index < typeValues.length; index++) {
         typeList[index] = typeValues[index].toString();
      }

      return typeList;
   }


   final private String            _name;
   final private GColorScheme.Type _type;
   final private List<IColor>      _colors;


   public GColorScheme(final String name,
                       final GColorScheme.Type type,
                       final IColor[] colors) {
      GAssert.notNull(name, "name");
      GAssert.notNull(type, "type");
      GAssert.notEmpty(colors, "colors");

      _name = name.trim();
      _type = type;
      _colors = Arrays.asList(colors);
   }


   private GColorScheme(final String name,
                        final Type type,
                        final List<IColor> colors) {
      _name = name;
      _type = type;
      _colors = colors;
   }


   @Override
   public String toString() {
      //      return "GColorScheme [name=" + _name + ", type=" + _type + ", colors=" + Arrays.toString(_colors) + ", sets=" + _sets + "]";
      //      return "GColorScheme [name=" + _name + ", type=" + _type + ", colors=" + _colors.length + ", sets=" + _sets.size() + "]";
      return "GColorScheme [name=" + _name + ", type=" + _type + ", dimension=" + _colors.size() + "]";
   }


   public String getName() {
      return _name;
   }


   public List<IColor> getColors() {
      return Collections.unmodifiableList(_colors);
   }


   public GColorScheme subScheme(final int... indices) {
      GAssert.notEmpty(indices, "indices");

      final List<IColor> colors = new ArrayList<IColor>(indices.length);
      for (final int index : indices) {
         colors.add(_colors.get(index));
      }

      return new GColorScheme(_name, _type, colors);
   }


   public GColorScheme.Type getType() {
      return _type;
   }


   public int getDimensions() {
      return _colors.size();
   }


}
