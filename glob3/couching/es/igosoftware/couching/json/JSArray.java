

package es.igosoftware.couching.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class JSArray<T extends JSValue>
         extends
            JSValue
         implements
            Iterable<T> {


   private final List<T> _values;


   public JSArray(final T... values) {
      _values = Arrays.asList(values);
   }


   public JSArray(final List<T> values) {
      _values = new ArrayList<T>(values);
   }


   public List<T> getValues() {
      return Collections.unmodifiableList(_values);
   }


   @Override
   public Iterator<T> iterator() {
      return getValues().iterator();
   }


   public int size() {
      return _values.size();
   }


   public boolean isEmpty() {
      return _values.isEmpty();
   }


   public String getString(final int fieldIndex) {
      return ((JSString) _values.get(fieldIndex)).getValue();
   }


   public boolean getBoolean(final int fieldIndex) {
      return ((JSBoolean) _values.get(fieldIndex)).getValue();
   }


   public long getLong(final int fieldIndex) {
      return ((JSLong) _values.get(fieldIndex)).getValue();
   }


   public double getDouble(final int fieldIndex) {
      return ((JSDouble) _values.get(fieldIndex)).getValue();
   }


   public Void getNull(final int fieldIndex) {
      return ((JSNull) _values.get(fieldIndex)).getValue();
   }


   @SuppressWarnings("unchecked")
   public <ObjectT extends JSValue> JSObject<ObjectT> getObject(final int fieldIndex) {
      return (JSObject<ObjectT>) _values.get(fieldIndex);
   }


   @SuppressWarnings("unchecked")
   public <ArrayT extends JSValue> JSArray<ArrayT> getArray(final int fieldIndex) {
      return (JSArray<ArrayT>) _values.get(fieldIndex);
   }


   public boolean isNull(final int fieldIndex) {
      return (_values.get(fieldIndex) == JSNull.INSTANCE);
   }


   @Override
   public String toJSONString() {
      final StringBuilder builder = new StringBuilder();
      builder.append('[');

      boolean isFirst = true;
      for (final T value : _values) {
         if (isFirst) {
            isFirst = false;
         }
         else {
            builder.append(',');
         }
         builder.append(value.toJSONString());
      }

      builder.append(']');

      return builder.toString();
   }


   @Override
   protected String toPrettyJSONString(final String prefix,
                                       final int level) {

      if (_values.isEmpty()) {
         return identation(level) + prefix + "[]";
      }

      final StringBuilder builder = new StringBuilder();
      builder.append(identation(level));
      builder.append(prefix);
      builder.append("[\n");

      for (final T value : _values) {
         builder.append(value.toPrettyJSONString("", level + 1));
         builder.append(", \n");
      }
      builder.setLength(builder.length() - 3);
      builder.append('\n');

      builder.append(identation(level));
      builder.append(']');

      return builder.toString();
   }


}
