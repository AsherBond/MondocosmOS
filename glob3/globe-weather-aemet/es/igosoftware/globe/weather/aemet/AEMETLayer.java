

package es.igosoftware.globe.weather.aemet;

import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.IGlobeSymbolizer;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.GBooleanLayerAttribute;
import es.igosoftware.globe.attributes.GFloatLayerAttribute;
import es.igosoftware.globe.attributes.GGroupAttribute;
import es.igosoftware.globe.attributes.GSelectionLayerAttribute;
import es.igosoftware.globe.attributes.GStringLayerAttribute;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.globe.weather.aemet.data.AEMETData;
import es.igosoftware.globe.weather.aemet.data.AEMETStation;
import es.igosoftware.globe.weather.aemet.data.AEMETVariable;
import es.igosoftware.globe.weather.aemet.data.GInterpolator;
import es.igosoftware.globe.weather.aemet.data.Lapse;
import es.igosoftware.io.GFileName;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GPredicate;
import es.igosoftware.util.GUtils;
import es.igosoftware.util.IFunction;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.AbstractLayer;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.GeographicText;
import gov.nasa.worldwind.render.GeographicTextRenderer;
import gov.nasa.worldwind.render.UserFacingText;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Icon;


public class AEMETLayer
         extends
            AbstractLayer
         implements
            IGlobeLayer {


   private static final GeographicTextRenderer TEXT_RENDERER               = new GeographicTextRenderer();
   // private static final long                   ANIMATION_DELAY             = 1000 /* 1 second */;
   private static final long                   ANIMATION_DELAY             = 125 /* 1/8 second */;


   private final AEMETData                     _data;
   private final List<AEMETStation>            _stations;
   private final Sector                        _sector;


   private GInterpolatedSurface                _surface                    = null;

   private boolean                             _renderTriangulationSurface = true;
   private boolean                             _renderTriangulationBorder  = false;
   private boolean                             _renderLabels               = false;
   private Collection<GeographicText>          _labels;

   private AEMETVariable<Double>               _variable;
   private List<Lapse>                         _lapses;
   private Lapse                               _lapse;
   private int                                 _time;
   private boolean                             _animation                  = false;

   private Timer                               _timer;
   final GInterpolator                         _interpolator;


   public AEMETLayer(final IGlobeRunningContext context,
                     final AEMETData data) {
      GAssert.notNull(context, "context");
      GAssert.notNull(data, "data");

      _interpolator = new GInterpolator(data);

      _data = data;
      _stations = initializeStations(data);

      _sector = initializeBounds();

      pvtSetVariable(data.getDefaultVariable());

      _surface = new GInterpolatedSurface(this, getVariable(), getLapse(), _data.getAreas());
   }


   private void pvtSetVariable(final AEMETVariable<Double> newValue) {
      _variable = newValue;
      //      _lapses = _data.getLapses(_variable);
      //      _lapsesL = new ArrayList<Lapse>(_lapses);
      _lapses = new ArrayList<Lapse>(_data.getLapses(_variable));
      setTime(_lapses.size() - 1);
   }


   private List<AEMETStation> initializeStations(final AEMETData data) {

      final Sector balearesBounds = Sector.boundingSector(LatLon.fromDegrees(38.5, 1), LatLon.fromDegrees(40, 4.5));
      final double tarifaLatitude = 35.9;

      final List<AEMETStation> stations = GCollections.select(data.getStations(), new GPredicate<AEMETStation>() {
         @Override
         public boolean evaluate(final AEMETStation element) {
            final Position position = element.getWWPosition();
            return !balearesBounds.contains(position) && (position.getLatitude().degrees > tarifaLatitude);
         }
      });

      return stations;
   }


   private Sector initializeBounds() {
      final Collection<Position> positions = GCollections.collect(_stations, new IFunction<AEMETStation, Position>() {
         @Override
         public Position apply(final AEMETStation element) {
            return element.getWWPosition();
         }
      });

      return Sector.boundingSector(positions);
   }


   @Override
   public Sector getExtent() {
      return _sector;
   }


   @Override
   public void doDefaultAction(final IGlobeRunningContext context) {
      if (isEnabled()) {
         context.getCameraController().animatedZoomToSector(getExtent());
      }
   }


   @Override
   public Icon getIcon(final IGlobeRunningContext context) {
      return context.getBitmapFactory().getSmallIcon(GFileName.relative("weather.png"));
   }


   @Override
   public void redraw() {
      firePropertyChange(AVKey.LAYER, null, this);
   }


   public boolean isRenderLabels() {
      return _renderLabels;
   }


   public void setRenderLabels(final boolean newValue) {
      if (newValue == _renderLabels) {
         return;
      }
      _renderLabels = newValue;

      firePropertyChange("RenderLabels", !newValue, newValue);
   }


   public AEMETVariable<Double> getVariable() {
      return _variable;
   }


   public void setVariable(final AEMETVariable<Double> newValue) {
      if (newValue == _variable) {
         return;
      }

      final AEMETVariable<Double> oldVariable = _variable;
      pvtSetVariable(newValue);

      firePropertyChange("Variable", oldVariable, _variable);

      _surface.setVariable(_variable);
      _surface.setLapse(_lapse);

      recreateLabels();
   }


   private void recreateLabels() {
      _labels = null;
   }


   public boolean isRenderTriangulationSurface() {
      return _renderTriangulationSurface;
   }


   public void setRenderTriangulationSurface(final boolean newValue) {
      if (newValue == _renderTriangulationSurface) {
         return;
      }
      _renderTriangulationSurface = newValue;

      firePropertyChange("RenderTriangulationSurface", !newValue, newValue);

      invalidateSurface();
   }


   public boolean isRenderTriangulationEdges() {
      return _renderTriangulationBorder;
   }


   public void setRenderTriangulationEdges(final boolean newValue) {
      if (newValue == _renderTriangulationBorder) {
         return;
      }
      _renderTriangulationBorder = newValue;

      firePropertyChange("RenderTriangulationEdges", !newValue, newValue);

      invalidateSurface();
   }


   private void invalidateSurface() {
      _surface.invalidate();
   }


   @SuppressWarnings("unchecked")
   @Override
   public List<? extends ILayerAttribute<?>> getLayerAttributes(final IGlobeRunningContext context) {

      final ILayerAttribute<?> variable = new GSelectionLayerAttribute<AEMETVariable<Double>>("",
               "Choose the variable to render", "Variable", _data.getDoubleVariables()) {

         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public AEMETVariable<Double> get() {
            return getVariable();
         }


         @Override
         public void set(final AEMETVariable<Double> value) {
            setVariable(value);
         }
      };

      return Arrays.asList(variable, createAnimationGroup(), createShowGroup());
   }


   private GGroupAttribute createAnimationGroup() {
      final GFloatLayerAttribute lapsesAtr = new GFloatLayerAttribute("T", "", "Time", 0, _lapses.size() - 1,
               GFloatLayerAttribute.WidgetType.SLIDER, 1) {

         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Float get() {
            return Float.valueOf(getTime());
         }


         @Override
         public void set(final Float value) {
            setTime(Math.round(value));
         }


         @Override
         public String getLabel() {
            return null;
         }
      };


      final GStringLayerAttribute lapseAtr = new GStringLayerAttribute("Lapse", "Lapse for Rendering", "Lapse", true) {
         private final SimpleDateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");


         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public String get() {
            return _dateFormat.format(getLapse().getUpper());
         }


         @Override
         public void set(final String value) {
         }
      };


      final GBooleanLayerAttribute animate = new GBooleanLayerAttribute("Animation",
               "Iterates over the time line to create an animation", "Animation") {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Boolean get() {
            return isAnimation();
         }


         @Override
         public void set(final Boolean value) {
            setAnimation(value);
         }
      };


      return new GGroupAttribute("Time", "", lapseAtr, lapsesAtr, animate);
   }


   private boolean isAnimation() {
      return _animation;
   }


   private void setAnimation(final boolean newValue) {
      if (newValue == _animation) {
         return;
      }

      _animation = newValue;

      if (_animation) {
         startTimer();
      }
      else {
         stopTimer();
      }

      firePropertyChange("Animation", !newValue, newValue);
   }


   private void stopTimer() {
      if (_timer != null) {
         _timer.cancel();
         _timer = null;
      }
   }


   private void startTimer() {
      stopTimer();

      _timer = new Timer(true);
      _timer.scheduleAtFixedRate(new TimerTask() {
         @Override
         public void run() {
            animationStep();
         }
      }, ANIMATION_DELAY, ANIMATION_DELAY);
   }


   private void animationStep() {
      final int newTime = (_time + 1) % _lapses.size();
      setTime(newTime);
   }


   public int getTime() {
      return _time;
   }


   public void setTime(final int newValue) {
      if (newValue == _time) {
         return;
      }

      final int oldValue = _time;
      _time = newValue;

      setLapse(_lapses.get(_time));

      firePropertyChange("Time", oldValue, newValue);
   }


   private void setLapse(final Lapse newLapse) {
      if (GUtils.equals(_lapse, newLapse)) {
         return;
      }

      final Lapse oldLapse = _lapse;
      _lapse = newLapse;
      if (_surface != null) {
         _surface.setLapse(_lapse);
      }

      recreateLabels();
      redraw();

      firePropertyChange("Lapse", oldLapse, newLapse);
   }


   private Lapse getLapse() {
      return _lapse;
   }


   private GGroupAttribute createShowGroup() {
      final GBooleanLayerAttribute showSurface = new GBooleanLayerAttribute("Interpolation Surface",
               "Visualize the interpolation surface", "RenderTriangulationSurface") {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Boolean get() {
            return isRenderTriangulationSurface();
         }


         @Override
         public void set(final Boolean value) {
            setRenderTriangulationSurface(value);
         }
      };


      final GBooleanLayerAttribute showTriangulation = new GBooleanLayerAttribute("Triangulation",
               "Visualize the triangulation edges", "RenderTriangulationBorder") {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Boolean get() {
            return isRenderTriangulationEdges();
         }


         @Override
         public void set(final Boolean value) {
            setRenderTriangulationEdges(value);
         }
      };


      final GBooleanLayerAttribute showLabels = new GBooleanLayerAttribute("Labels", "Visualize the variable values as labels",
               "RenderLabels") {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Boolean get() {
            return isRenderLabels();
         }


         @Override
         public void set(final Boolean value) {
            setRenderLabels(value);
         }
      };

      return new GGroupAttribute("Show", "", showSurface, showTriangulation, showLabels);
   }


   @Override
   public List<? extends ILayerAction> getLayerActions(final IGlobeRunningContext context) {
      return null;
   }


   @Override
   public IGlobeSymbolizer getSymbolizer() {
      return null;
   }


   @Override
   public String getName() {
      return "AEMET Weather";
   }


   @Override
   protected void doPreRender(final DrawContext dc) {
      super.doPreRender(dc);

      _surface.preRender(dc);
   }


   @Override
   protected void doRender(final DrawContext dc) {
      _surface.render(dc);

      if (_renderLabels) {
         renderLabels(dc);
      }
   }


   private void renderLabels(final DrawContext dc) {
      if (_labels == null) {
         _labels = initializeLabels();
      }

      TEXT_RENDERER.render(dc, _labels);
   }


   private Collection<GeographicText> initializeLabels() {
      final List<AEMETStation> stationsWithVariables = GCollections.select(_stations, new GPredicate<AEMETStation>() {
         @Override
         public boolean evaluate(final AEMETStation element) {
            return element.hasValue(getVariable(), getLapse());
         }
      });

      return GCollections.collect(stationsWithVariables, new IFunction<AEMETStation, GeographicText>() {
         @Override
         public GeographicText apply(final AEMETStation station) {
            return createLabel(station);
         }
      });
   }


   private GeographicText createLabel(final AEMETStation station) {
      final AEMETVariable<Double> variable = getVariable();
      final Lapse lapse = getLapse();

      final String text = station.getValue(variable, lapse) + variable.getUnit();

      final UserFacingText label = new UserFacingText(text, station.getWWPosition());

      final Color labelColor = station.getColor(variable, lapse, getInterpolator());
      label.setColor(labelColor.darker().darker().darker());
      label.setBackgroundColor(labelColor.brighter().brighter().brighter());

      return label;
   }


   private GInterpolator getInterpolator() {
      return _interpolator;
   }


}
