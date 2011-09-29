

package es.igosoftware.globe.weather.aemet.data;

import java.awt.Color;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import es.igosoftware.euclid.GAngle;
import es.igosoftware.euclid.colors.GColorF;
import es.igosoftware.euclid.experimental.vectorial.rendering.coloring.GColorRamp;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.io.GFileName;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.IFunction;
import es.igosoftware.utils.GWWUtils;
import gov.nasa.worldwind.geom.Position;


public class AEMETStation {

   private static final Color                                          NULL_COLOR        = new Color(0, 0, 0, 0);
   private static GColorRamp                                           RAMP;
   private static final double                                         MAX_TEMPERATURE   = 50;
   private static final double                                         MIN_TEMPERATURE   = -50;
   private static final double                                         DELTA_TEMPERATURE = MAX_TEMPERATURE - MIN_TEMPERATURE;

   static {
      try {
         RAMP = GColorRamp.fromImage(GFileName.relative("..", "globe-weather-aemet", "data", "temperature-ramp.png"));
      }
      catch (final IOException e) {
         e.printStackTrace();
         RAMP = new GColorRamp( //
                  GColorF.CYAN, //
                  GColorF.YELLOW, //
                  GColorF.RED //
         );
      }
   }


   private final String                                                _id1;
   private final String                                                _id2;
   private final String                                                _name;
   private final IVector3                                              _position;
   private final Map<AEMETVariable<Object>, TreeSet<AEMETObservation>> _values           = new HashMap<AEMETVariable<Object>, TreeSet<AEMETObservation>>();


   AEMETStation(final String id1,
                final String id2,
                final String name,
                final GAngle latitute,
                final GAngle longitude,
                final double altitute) {
      _id1 = id1.trim();
      _id2 = id2.trim();
      _name = name;
      _position = new GVector3D(longitude.getRadians(), latitute.getRadians(), altitute);
   }


   public String getName() {
      return _name;
   }


   public IVector3 getPosition() {
      return _position;
   }


   public Position getWWPosition() {
      return GWWUtils.toPosition(getPosition(), GProjection.EPSG_4326);
   }


   public <T> TreeSet<AEMETObservation> getObservations(final AEMETVariable<T> variable) {
      final TreeSet<AEMETObservation> observations = _values.get(variable);
      if ((observations == null) || observations.isEmpty()) {
         return null;
      }
      return observations;
   }


   public <T> TreeSet<Lapse> getLapses(final AEMETVariable<T> variable) {
      final TreeSet<AEMETObservation> observations = getObservations(variable);
      if (observations == null) {
         return null;
      }

      final TreeSet<Lapse> result = new TreeSet<Lapse>();
      for (final AEMETObservation observation : observations) {
         final Lapse lapse = observation.getLapse();
         if (lapse != null) {
            result.add(lapse);
         }
      }
      return result;
   }


   public <T> Lapse getLapse(final AEMETVariable<T> variable) {
      final TreeSet<AEMETObservation> observations = getObservations(variable);
      if (observations == null) {
         return null;
      }

      final Collection<Lapse> ranges = GCollections.collect(observations, new IFunction<AEMETObservation, Lapse>() {
         @Override
         public Lapse apply(final AEMETObservation element) {
            return element.getLapse();
         }
      });

      return Lapse.merge(ranges);
   }


   public <T> T getLastValue(final AEMETVariable<T> variable) {
      final TreeSet<AEMETObservation> observations = getObservations(variable);
      if (observations == null) {
         return null;
      }
      return observations.last().getValue(variable.getType());
   }


   public <T> boolean hasValue(final AEMETVariable<T> variable) {
      final T value = getLastValue(variable);
      if (value == null) {
         return false;
      }

      if (value instanceof Double) {
         return !Double.isNaN((Double) value);
      }

      return true;
   }


   //   public Color getLastColor(final AEMETVariable<Double> variable) {
   //      final float opacity = 0.6f;
   //
   //      if (!hasValue(variable)) {
   //         final int TODO_Interpolate_color;
   //         return new Color(0, 0, 0, opacity);
   //      }
   //
   //      final double value = getLastValue(variable);
   //
   //      final float alpha = (float) ((value - MIN_TEMPERATURE) / DELTA_TEMPERATURE);
   //
   //      return RAMP.getColor(alpha).asAWTColor(opacity);
   //   }

   public <T> T getValue(final AEMETVariable<T> variable,
                         final Lapse lapse) {
      final TreeSet<AEMETObservation> observations = getObservations(variable);
      if (observations == null) {
         return null;
      }

      for (final AEMETObservation observation : observations) {
         if (lapse.equals(observation.getLapse())) {
            return observation.getValue(variable.getType());
         }
      }
      return null;
   }


   public <T> boolean hasValue(final AEMETVariable<T> variable,
                               final Lapse lapse) {
      final T value = getValue(variable, lapse);
      if (value == null) {
         return false;
      }

      if (value instanceof Double) {
         return !Double.isNaN((Double) value);
      }

      return true;
   }


   public Color getColor(final AEMETVariable<Double> variable,
                         final Lapse lapse,
                         final GInterpolator interpolator) {
      final float opacity = 0.6f;

      final double value;
      if (hasValue(variable, lapse)) {
         value = getValue(variable, lapse);
      }
      else {
         value = interpolator.interpolate(variable, lapse, this);
         if (Double.isNaN(value)) {
            return NULL_COLOR;
         }
      }

      final float alpha = (float) ((value - MIN_TEMPERATURE) / DELTA_TEMPERATURE);

      return RAMP.getColor(alpha).asAWTColor(opacity);
   }


   public <T> void addObservation(final AEMETVariable<T> variable,
                                  final Lapse lapse,
                                  final String value,
                                  final String quality) {
      final TreeSet<AEMETObservation> observations = getObversationsFor(variable);

      final T valueO = variable.parseValue(value, quality);
      final AEMETObservation observation = new AEMETObservation(valueO, lapse);
      observations.add(observation);
   }


   @SuppressWarnings("unchecked")
   private <T> TreeSet<AEMETObservation> getObversationsFor(final AEMETVariable<T> variable) {
      TreeSet<AEMETObservation> observations = _values.get(variable);
      if (observations == null) {
         observations = new TreeSet<AEMETObservation>();
         _values.put((AEMETVariable<Object>) variable, observations);
      }
      return observations;
   }


   public String getId1() {
      return _id1;
   }


   public String getId2() {
      return _id2;
   }


   @Override
   public String toString() {
      return "AEMETStation [id1=" + _id1 + ", id2=" + _id2 + ", name=" + _name + ", position=" + _position + ", values="
             + _values.size() + "]";
   }


}
