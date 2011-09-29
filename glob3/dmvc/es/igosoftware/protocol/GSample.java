

package es.igosoftware.protocol;

import java.util.Arrays;
import java.util.Calendar;


public class GSample
         extends
            GProtocolObject {

   private int                                _answer;
   private final GIntProtocolField            _answerField = new GIntProtocolField(false);

   private float                              _foo;
   private final GFloatProtocolField          _fooField    = new GFloatProtocolField(false);

   private String                             _bar;
   private final GStringProtocolField         _barField    = new GStringProtocolField();

   private Object                             _object;
   private final GObjectProtocolField<Object> _objectField = new GObjectProtocolField<Object>();


   @Override
   protected IProtocolField[] getProtocolFields() {
      return new IProtocolField[] {
                        _answerField,
                        _fooField,
                        _barField,
                        _objectField
      };
   }


   @Override
   protected void initializeFromFields() {
      _answer = _answerField.get();
      _foo = _fooField.get();
      _bar = _barField.get();
      _object = _objectField.get();
   }


   @Override
   protected void storeIntoFields() {
      _answerField.set(_answer);
      _fooField.set(_foo);
      _barField.set(_bar);
      _objectField.set(_object);
   }


   public void setAnswer(final int foo) {
      _answer = foo;
   }


   private void setFoo(final float foo) {
      _foo = foo;
   }


   private void setBar(final String bar) {
      _bar = bar;
   }


   private void setObject(final Object object) {
      _object = object;
   }


   @SuppressWarnings("unchecked")
   public static void main(final String[] args) {
      System.out.println("PROTOCOL Sample 0.1");
      System.out.println("-------------------\n");

      final GProtocolMultiplexor multiplexor = new GProtocolMultiplexor(GSample.class);

      final GSample sample = new GSample();
      sample.setAnswer(42);
      sample.setFoo(0.5f);
      //      sample.setBar("Hello World!");
      sample.setBar(null);
      sample.setObject(Calendar.getInstance().getTime());

      //final byte[] bytes = sample.getProtocolBytes();
      final byte[] bytes = multiplexor.getProtocolBytes(sample);

      System.out.println(bytes.length + " bytes " + Arrays.toString(bytes));

      //final GSample materialized = GProtocolObject.createObject(GSample.class, bytes);
      final GSample materialized = (GSample) multiplexor.createObject(bytes);
      System.out.println(materialized._answer);
      System.out.println(materialized._foo);
      System.out.println(materialized._bar);
      System.out.println(materialized._object);

      //      final double d = Double.longBitsToDouble(-1);
      //      System.out.println(Double.POSITIVE_INFINITY - 22);
   }


}
