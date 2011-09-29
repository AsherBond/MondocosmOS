

package es.igosoftware.couching.json.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import es.igosoftware.couching.json.JSArray;
import es.igosoftware.couching.json.JSBoolean;
import es.igosoftware.couching.json.JSDouble;
import es.igosoftware.couching.json.JSLong;
import es.igosoftware.couching.json.JSNull;
import es.igosoftware.couching.json.JSObject;
import es.igosoftware.couching.json.JSParser;
import es.igosoftware.couching.json.JSString;
import es.igosoftware.couching.json.JSValue;
import es.igosoftware.util.GMath;


public class JSParserTest {

   private static JSParser createParser() {
      return new JSParser();
   }


   @Test
   public void testNull() throws IOException {
      final JSValue parsed = createParser().parse("null");

      assertTrue("must be a JSNull", parsed instanceof JSNull);
   }


   @Test
   public void testBoolean() throws IOException {
      testBoolean("true", true);
      testBoolean("false", false);
   }


   private static void testBoolean(final String json,
                                   final boolean expected) throws IOException {
      final JSValue parsed = createParser().parse(json);

      assertTrue("must be a JSBoolean", parsed instanceof JSBoolean);

      assertEquals(Boolean.valueOf(expected), ((JSBoolean) parsed).getValue());
   }


   @Test
   public void testString() throws IOException {
      testString("\"foo\"", "foo");
      testString("\"bar\"", "bar");
   }


   private static void testString(final String json,
                                  final String expected) throws IOException {
      final JSValue parsed = createParser().parse(json);

      assertTrue("must be a JSString", parsed instanceof JSString);

      assertEquals(expected, ((JSString) parsed).getValue());
   }


   @Test
   public void testDouble() throws IOException {
      testDouble("1.0", 1.0);
      testDouble("2.0", 2.0);
   }


   private static void testDouble(final String json,
                                  final double expected) throws IOException {
      final JSValue parsed = createParser().parse(json);

      assertTrue("must be a JSDouble", parsed instanceof JSDouble);

      assertEquals(Double.valueOf(expected), ((JSDouble) parsed).getValue());
   }


   @Test
   public void testLong() throws IOException {
      testLong("1", 1L);
      testLong("2", 2L);
   }


   private static void testLong(final String json,
                                final long expected) throws IOException {
      final JSValue parsed = createParser().parse(json);

      assertTrue("must be a JSLong", parsed instanceof JSLong);

      assertEquals(Long.valueOf(expected), ((JSLong) parsed).getValue());
   }


   @Test
   public void testEmptyObject() throws IOException {
      final JSValue parsed = createParser().parse("{}");

      assertTrue("must be a JSObject", parsed instanceof JSObject);

      @SuppressWarnings("unchecked")
      final JSObject<JSValue> parsedObject = (JSObject<JSValue>) parsed;
      assertEquals(0, parsedObject.getFieldsCount());
   }


   @Test
   public void testSimpleObject() throws IOException {
      final JSValue parsed = createParser().parse(
               "{\"boolean\": true, \"string\": \"stringValue\", \"double\": 1.0, \"long\": 1, \"null\": null}");

      assertTrue("must be a JSObject", parsed instanceof JSObject);

      @SuppressWarnings("unchecked")
      final JSObject<JSValue> parsedObject = (JSObject<JSValue>) parsed;
      assertEquals(5, parsedObject.getFieldsCount());

      assertTrue(parsedObject.getBoolean(0));
      assertTrue(parsedObject.getBoolean("boolean"));

      assertEquals("stringValue", parsedObject.getString(1));
      assertEquals("stringValue", parsedObject.getString("string"));

      assertTrue(GMath.closeTo(1.0, parsedObject.getDouble(2)));
      assertTrue(GMath.closeTo(1.0, parsedObject.getDouble("double")));

      assertEquals(1L, parsedObject.getLong(3));
      assertEquals(1L, parsedObject.getLong("long"));

      assertEquals(null, parsedObject.getNull(4));
      assertEquals(null, parsedObject.getNull("null"));
   }


   @Test
   public void testObjectInsideObject() throws IOException {
      final JSValue parsed = createParser().parse("{\"obj0\": {}, \"obj1\": {\"foo\": 1}}");

      assertTrue("must be a JSObject", parsed instanceof JSObject);

      @SuppressWarnings("unchecked")
      final JSObject<JSValue> parsedObject = (JSObject<JSValue>) parsed;
      assertEquals(2, parsedObject.getFieldsCount());

      final JSObject<JSValue> obj0 = parsedObject.getObject(0);
      assertSame(obj0, parsedObject.getObject("obj0"));

      assertEquals(0, obj0.getFieldsCount());


      final JSObject<JSValue> obj1 = parsedObject.getObject(1);
      assertSame(obj1, parsedObject.getObject("obj1"));

      assertEquals(1, obj1.getFieldsCount());
      assertEquals(1L, obj1.getLong(0));
   }


   @Test
   public void testEmptyArray() throws IOException {
      final JSValue parsed = createParser().parse("[]");

      assertTrue("must be a JSArray", parsed instanceof JSArray);

      final JSArray<?> parsedArray = (JSArray<?>) parsed;
      assertEquals(0, parsedArray.size());
      assertTrue(parsedArray.isEmpty());
   }


   @Test
   public void testSimpleArray() throws IOException {
      final JSValue parsed = createParser().parse("[1, 2.0, true, null, \"string\", {\"foo\": 2}]");

      assertTrue("must be a JSArray", parsed instanceof JSArray);

      final JSArray<?> parsedArray = (JSArray<?>) parsed;
      assertEquals(6, parsedArray.size());

      assertEquals(1L, parsedArray.getLong(0));
      assertTrue(GMath.closeTo(2.0, parsedArray.getDouble(1)));
      assertEquals(true, parsedArray.getBoolean(2));
      assertEquals(null, parsedArray.getNull(3));
      assertEquals("string", parsedArray.getString(4));

      final JSObject<JSValue> object = parsedArray.getObject(5);
      assertEquals(2, object.getLong(0));
   }


}
