

package es.igosoftware.experimental.vectorial.symbolizer;

import javax.swing.Icon;

import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ISurface2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GConstantExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GSurface2DStyleExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.IExpression;
import es.igosoftware.globe.attributes.GColorExpressionLayerAttribute;
import es.igosoftware.globe.attributes.GCompositeAttribute;
import es.igosoftware.globe.attributes.GFloatLayerAttribute;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.util.GPair;


public abstract class GSurfaceStyleExpressionAttribute<GeometryT extends ISurface2D<? extends IFinite2DBounds<?>>>
         extends
            GCompositeAttribute<IExpression<GeometryT, ISurface2DStyle>> {


   private IColor                         _initialColor;
   private IExpression<GeometryT, IColor> _colorExpression;
   private float                          _opacity;


   public GSurfaceStyleExpressionAttribute(final String label,
                                           final Icon icon,
                                           final String description,
                                           final String propertyName,
                                           final IColor initialColor,
                                           final IExpression<GeometryT, IColor> initialColorExpression,
                                           final float initialOpacity) {

      super(label, icon, description, propertyName);

      _initialColor = initialColor;
      _colorExpression = initialColorExpression;
      _opacity = initialOpacity;
   }


   @Override
   protected ILayerAttribute<?>[] initializeChildren() {


      final GColorExpressionLayerAttribute<GeometryT> colorExpressionAttribute = new GColorExpressionLayerAttribute<GeometryT>(
               "Color ", "", "SurfaceColor") {
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

      //return new ILayerAttribute<?>[] { colorAttribute, opacityAttribute };
      return new ILayerAttribute<?>[] {
                        colorExpressionAttribute,
                        opacityAttribute
      };
   }


   private void updateExpression() {
      final IExpression<GeometryT, ISurface2DStyle> surfaceNewStyleExpression = new GSurface2DStyleExpression<GeometryT>( //
               //new GConstantExpression<GeometryT, IColor>(_initialColor), //
               _colorExpression, //
               new GConstantExpression<GeometryT, Float>(_opacity));

      set(surfaceNewStyleExpression);
   }


}
