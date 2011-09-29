

package es.igosoftware.experimental.vectorial.symbolizer;

import javax.swing.Icon;

import es.igosoftware.euclid.IGeometry2D;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.experimental.measurement.GLength;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ICurve2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GConstantExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GCurve2DStyleExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GLengthToFloatExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.IExpression;
import es.igosoftware.globe.attributes.GColorExpressionLayerAttribute;
import es.igosoftware.globe.attributes.GCompositeAttribute;
import es.igosoftware.globe.attributes.GFloatLayerAttribute;
import es.igosoftware.globe.attributes.GLengthLayerAttribute;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.util.GPair;


public abstract class GCurveStyleExpressionAttribute<GeometryT extends IGeometry2D>
         extends
            GCompositeAttribute<IExpression<GeometryT, ICurve2DStyle>> {


   private IMeasure<GLength>              _width;
   private IColor                         _initialColor;
   private IExpression<GeometryT, IColor> _colorExpression;
   private float                          _opacity;


   public GCurveStyleExpressionAttribute(final String label,
                                         final Icon icon,
                                         final String description,
                                         final String propertyName,
                                         final IMeasure<GLength> initialWidth,
                                         final IColor initialColor,
                                         final IExpression<GeometryT, IColor> initialColorExpression,
                                         final float initialOpacity) {
      super(label, icon, description, propertyName);

      _width = initialWidth;
      _initialColor = initialColor;
      _colorExpression = initialColorExpression;
      _opacity = initialOpacity;
   }


   @Override
   protected ILayerAttribute<?>[] initializeChildren() {

      final GLengthLayerAttribute widthAttribute = new GLengthLayerAttribute("Width", "", "CurveWidth", 0, 1000, 1) {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public IMeasure<GLength> get() {
            return _width;
         }


         @Override
         public void set(final IMeasure<GLength> value) {
            _width = value;
            updateExpression();
         }
      };


      final GColorExpressionLayerAttribute<GeometryT> colorExpressionAttribute = new GColorExpressionLayerAttribute<GeometryT>(
               "Color ", "", "BorderColor") {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public GPair<IColor, IExpression<GeometryT, IColor>> get() {
            return new GPair<IColor, IExpression<GeometryT, IColor>>(_initialColor, _colorExpression);
         }


         @Override
         public void set(final GPair<IColor, IExpression<GeometryT, IColor>> colorData) {
            _initialColor = colorData._first;
            _colorExpression = colorData._second;
            updateExpression();
         }
      };


      final GFloatLayerAttribute opacityAttribute = new GFloatLayerAttribute("Opacity", "", "CurveOpacity", 0, 1,
               GFloatLayerAttribute.WidgetType.SLIDER, 0.1f) {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Float get() {
            return _opacity;
         }


         @Override
         public void set(final Float value) {
            _opacity = value;
            updateExpression();
         }
      };

      //return new ILayerAttribute<?>[] { widthAttribute, colorExpressionAttribute, opacityAttribute };
      return new ILayerAttribute<?>[] {
                        widthAttribute,
                        colorExpressionAttribute,
                        opacityAttribute
      };
   }


   private void updateExpression() {
      final IExpression<GeometryT, ICurve2DStyle> curveStyleExpression = new GCurve2DStyleExpression<GeometryT>( //
               new GLengthToFloatExpression<GeometryT>(_width), //
               //new GConstantExpression<GeometryT, IColor>(_initialColor), //
               _colorExpression, //
               new GConstantExpression<GeometryT, Float>(_opacity));

      set(curveStyleExpression);
   }


}
