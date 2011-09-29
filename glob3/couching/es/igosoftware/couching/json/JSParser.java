

package es.igosoftware.couching.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;


public class JSParser {
   private static final JsonFactory JSON_FACTORY = new JsonFactory();


   public JSValue parse(final String string) throws IOException {
      return parse(new StringReader(string));
   }


   public JSValue parse(final Reader reader) throws IOException {
      final BufferedReader br = new BufferedReader(reader);

      final JsonParser jsonParser = JSON_FACTORY.createJsonParser(br);

      final JsonToken token = jsonParser.nextToken();
      if (token == null) {
         return null;
      }
      return parseValue(token, jsonParser);
   }


   private JSValue parseValue(final JsonToken token,
                              final JsonParser jsonParser) throws IOException {
      switch (token) {
         case VALUE_STRING:
            return new JSString(jsonParser.getText());

         case VALUE_NULL:
            return JSNull.INSTANCE;

         case VALUE_NUMBER_INT:
            return new JSLong(jsonParser.getLongValue());

         case VALUE_NUMBER_FLOAT:
            return new JSDouble(jsonParser.getDoubleValue());

         case VALUE_TRUE:
            return JSBoolean.TRUE;

         case VALUE_FALSE:
            return JSBoolean.FALSE;

         case START_OBJECT:
            return parseObject(jsonParser);

         case START_ARRAY:
            return parseArray(jsonParser);

         default:
            throw new JSIOException("Unexpected token: " + token);
      }
   }


   private JSObject<JSValue> parseObject(final JsonParser jsonParser) throws IOException {
      JsonToken token;

      final List<JSObjectField<JSValue>> fields = new ArrayList<JSObjectField<JSValue>>();

      String currentFieldName = null;
      while ((token = jsonParser.nextToken()) != JsonToken.END_OBJECT) {
         if (token == JsonToken.FIELD_NAME) {
            currentFieldName = jsonParser.getCurrentName();
         }
         else {
            final JSValue value = parseValue(token, jsonParser);
            fields.add(new JSObjectField<JSValue>(currentFieldName, value));
         }
      }

      return new JSObject<JSValue>(fields);
   }


   private JSArray<JSValue> parseArray(final JsonParser jsonParser) throws IOException {
      final List<JSValue> values = new ArrayList<JSValue>();

      JsonToken token;
      while ((token = jsonParser.nextToken()) != JsonToken.END_ARRAY) {
         final JSValue value = parseValue(token, jsonParser);
         values.add(value);
      }

      return new JSArray<JSValue>(values);
   }


}
