

package es.igosoftware.couching.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class JSObject<T extends JSValue>
         extends
            JSValue {


   private final List<JSObjectField<T>> _fields;


   public JSObject(final JSObjectField<T>... fields) {
      _fields = Arrays.asList(fields);
   }


   public JSObject(final List<JSObjectField<T>> fields) {
      _fields = fields.isEmpty() ? //
                                Collections.<JSObjectField<T>> emptyList() : //
                                new ArrayList<JSObjectField<T>>(fields);
   }


   public int getFieldsCount() {
      return _fields.size();
   }


   public boolean hasField(final String fieldName) {
      for (final JSObjectField<T> field : _fields) {
         if (field.getName().equals(fieldName)) {
            return true;
         }
      }
      return false;
   }


   private JSObjectField<T> getField(final int fieldIndex) {
      if ((fieldIndex < 0) || (fieldIndex >= _fields.size())) {
         throw new JSInvalidField("Field #" + fieldIndex + " not found");
      }
      return _fields.get(fieldIndex);
   }


   private JSObjectField<T> getField(final String fieldName) {
      for (final JSObjectField<T> field : _fields) {
         if (field.getName().equals(fieldName)) {
            return field;
         }
      }
      throw new JSInvalidField("Field '" + fieldName + "' not found");
   }


   public boolean isNull(final int fieldIndex) {
      return (getField(fieldIndex).getValue() == JSNull.INSTANCE);
   }


   @SuppressWarnings("unchecked")
   public boolean getBoolean(final int fieldIndex) {
      return ((JSObjectField<JSBoolean>) getField(fieldIndex)).getValue().getValue();
   }


   @SuppressWarnings("unchecked")
   public String getString(final int fieldIndex) {
      return ((JSObjectField<JSString>) getField(fieldIndex)).getValue().getValue();
   }


   @SuppressWarnings("unchecked")
   public double getDouble(final int fieldIndex) {
      return ((JSObjectField<JSDouble>) getField(fieldIndex)).getValue().getValue();
   }


   @SuppressWarnings("unchecked")
   public long getLong(final int fieldIndex) {
      return ((JSObjectField<JSLong>) getField(fieldIndex)).getValue().getValue();
   }


   @SuppressWarnings("unchecked")
   public Void getNull(final int fieldIndex) {
      return ((JSObjectField<JSNull>) getField(fieldIndex)).getValue().getValue();
   }


   @SuppressWarnings("unchecked")
   public <ObjectT extends JSValue> JSObject<ObjectT> getObject(final int fieldIndex) {
      return ((JSObjectField<JSObject<ObjectT>>) getField(fieldIndex)).getValue();
   }


   public boolean isNull(final String fieldName) {
      return (getField(fieldName).getValue() == JSNull.INSTANCE);
   }


   @SuppressWarnings("unchecked")
   public boolean getBoolean(final String fieldName) {
      return ((JSObjectField<JSBoolean>) getField(fieldName)).getValue().getValue();
   }


   @SuppressWarnings("unchecked")
   public String getString(final String fieldName) {
      return ((JSObjectField<JSString>) getField(fieldName)).getValue().getValue();
   }


   @SuppressWarnings("unchecked")
   public double getDouble(final String fieldName) {
      return ((JSObjectField<JSDouble>) getField(fieldName)).getValue().getValue();
   }


   @SuppressWarnings("unchecked")
   public long getLong(final String fieldName) {
      return ((JSObjectField<JSLong>) getField(fieldName)).getValue().getValue();
   }


   @SuppressWarnings("unchecked")
   public Void getNull(final String fieldName) {
      return ((JSObjectField<JSNull>) getField(fieldName)).getValue().getValue();
   }


   @SuppressWarnings("unchecked")
   public <ObjectT extends JSValue> JSObject<ObjectT> getObject(final String fieldName) {
      return ((JSObjectField<JSObject<ObjectT>>) getField(fieldName)).getValue();
   }


   @SuppressWarnings("unchecked")
   public <ArrayT extends JSValue> JSArray<ArrayT> getArray(final String fieldName) {
      return ((JSObjectField<JSArray<ArrayT>>) getField(fieldName)).getValue();
   }


   @SuppressWarnings("unchecked")
   public <ArrayT extends JSValue> JSArray<ArrayT> getArray(final int fieldIndex) {
      return ((JSObjectField<JSArray<ArrayT>>) getField(fieldIndex)).getValue();
   }


   @Override
   public String toJSONString() {
      final StringBuilder builder = new StringBuilder();
      builder.append('{');

      boolean isFirst = true;
      for (final JSObjectField<T> field : _fields) {
         if (isFirst) {
            isFirst = false;
         }
         else {
            builder.append(',');
         }
         builder.append('"');
         builder.append(field.getName());
         builder.append("\":");
         builder.append(field.getValue().toJSONString());
      }

      builder.append('}');

      return builder.toString();
   }


   @Override
   protected String toPrettyJSONString(final String prefix,
                                       final int level) {

      if (_fields.isEmpty()) {
         return identation(level) + prefix + "{}";
      }

      final StringBuilder builder = new StringBuilder();
      builder.append(identation(level));
      builder.append(prefix);
      builder.append("{\n");

      for (final JSObjectField<T> field : _fields) {
         builder.append(field.getValue().toPrettyJSONString("\"" + field.getName() + "\": ", level + 1));
         builder.append(", \n");
      }
      builder.setLength(builder.length() - 3);
      builder.append('\n');

      builder.append(identation(level));
      builder.append('}');

      return builder.toString();
   }


   public JSObject<T> copyWithoutFields(final String... excludedFieldNames) {
      if (excludedFieldNames.length == 0) {
         return this;
      }

      final List<JSObjectField<T>> fieldsToCopy = new ArrayList<JSObjectField<T>>(_fields.size() - excludedFieldNames.length);

      for (final JSObjectField<T> field : _fields) {
         boolean ignore = false;
         for (final String excludedFieldName : excludedFieldNames) {
            if (excludedFieldName.equals(field.getName())) {
               ignore = true;
               break;
            }
         }

         if (!ignore) {
            fieldsToCopy.add(field.clone());
         }
      }

      return new JSObject<T>(fieldsToCopy);
   }


}
