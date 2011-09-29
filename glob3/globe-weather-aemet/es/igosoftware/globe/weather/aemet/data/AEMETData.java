

package es.igosoftware.globe.weather.aemet.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.jzy3d.plot3d.builder.delaunay.jdt.Delaunay_Triangulation;
import org.jzy3d.plot3d.builder.delaunay.jdt.Point_dt;
import org.jzy3d.plot3d.builder.delaunay.jdt.Triangle_dt;

import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.globe.weather.aemet.GInterpolatedArea;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GPredicate;


public class AEMETData {

   private final Map<String, AEMETVariable<?>> _variables = new HashMap<String, AEMETVariable<?>>();

   private final List<AEMETStation>            _stations;

   private HashMap<String, AEMETStation>       _stationsMap;

   private List<GInterpolatedArea>             _areas;


   private void createVariables() {
      createVariable("Velocidad media del viento", "VV10m", "m/s", Double.class);
      //      createVariable("Dirección media del viento", "DV10m", "º", GAngle.class);
      createVariable("Velocidad máxima del viento", "VMAX10m", "m/s", Double.class);
      //      createVariable("Dirección de la velocidad máxima del viento", "DMAX10m", "º", GAngle.class);
      createVariable("Temperatura del aire", "TA", "C", Double.class);
      createVariable("Humedad relativa", "HR", "%", Double.class);
      //      createVariable("Temperatura del punto de rocío", "TPR", "ºC", Double.class);
      createVariable("Presión", "PRES", "hPa", Double.class);
      //      createVariable("Precipitación", "PREC", "mm", Double.class);
      //      createVariable("Presión reducida al nivel del mar", "PRES_nmar", "hPa", Double.class);
      //      createVariable("Capa nieve", "NIEVE", "cm", Double.class);
      //      createVariable("Temperatura máxima en 10 min", "TAMAX10m", "ºC", Double.class);
      //      createVariable("Temperatura mínima en 10 min", "TAMIN10m", "ºC", Double.class);
      //      createVariable("Temperatura máxima en 1 hora", "TAMAX1h", "ºC", Double.class);
      //      createVariable("Temperatura mínima en 1 hora", "TAMIN1h", "ºC", Double.class);
      //      createVariable("Reducción de la presión a altura del geopotencial ...", "GEO925", "m", Double.class);
      //      createVariable("Reducción de la presión a altura del geopotencial ...", "GEO850", "m", Double.class);
      //      createVariable("Reducción de la presión a altura del geopotencial ...", "GEO700", "m", Double.class);
      //      createVariable("Hora y minuto de la temperatura máxima en 1 hora", "HTAMAX1h", "hora y minuto", String.class);
      //      createVariable("Hora y minuto de la temperatura mínima en 1 hora", "HTAMIN1h", "hora y minuto", String.class);
      createVariable("Visibilidad", "VIS", "Km", Double.class);
      //      createVariable("Tiempo presente", "TPRE", "*Tabla 4680", String.class);
      //      createVariable("Insolación", "INSO", "min.", Double.class);
      //      createVariable("Radiación global", "RAGLOB", "KJ/m2", Double.class);
      createVariable("Temperatura suelo", "TS", "ºC", Double.class);
      createVariable("Temperatura subsuelo 5cm", "TSS5cm", "ºC", Double.class);
      //      createVariable("Precipitación acumulada líquida", "PLIQTP", "mm", Double.class);
      //      createVariable("Precipitación acumulada sólida", "PSOLTP", "mm", Double.class);
      createVariable("Recorrido del viento", "RVIENTO", "Hm", Double.class);
      //      createVariable("Carga baterías", "BAT", "V", Double.class);
      //      createVariable("Carga baterías (horaria)", "BATH", "V", Double.class);
   }


   private <T> AEMETVariable<T> createVariable(final String name,
                                               final String acronym,
                                               final String unit,
                                               final Class<T> type) {
      final AEMETVariable<T> variable = new AEMETVariable<T>(name, acronym, unit, type);

      final AEMETVariable<?> previous = _variables.put(acronym, variable);
      if (previous != null) {
         throw new RuntimeException("Duplicate variable: " + acronym);
      }

      return variable;
   }


   @SuppressWarnings("unchecked")
   public <T> AEMETVariable<T> getVariable(final String string) {
      return (AEMETVariable<T>) _variables.get(string);
   }


   public AEMETData(final List<AEMETStation> stations) {
      createVariables();
      _stations = Collections.unmodifiableList(stations);
   }


   private HashMap<String, AEMETStation> getStationsMap() {
      if (_stationsMap == null) {
         _stationsMap = new HashMap<String, AEMETStation>(_stations.size());
         for (final AEMETStation station : _stations) {
            if (!station.getId1().isEmpty()) {
               _stationsMap.put(station.getId1(), station);
            }

            if (!station.getId2().isEmpty()) {
               _stationsMap.put(station.getId2(), station);
            }
         }
      }

      return _stationsMap;
   }


   public List<AEMETStation> getStations() {
      return _stations;
   }


   @SuppressWarnings("unchecked")
   public AEMETVariable<Double>[] getDoubleVariables() {

      final Collection<AEMETVariable<?>> doubleVariables = GCollections.select(_variables.values(),
               new GPredicate<AEMETVariable<?>>() {
                  @Override
                  public boolean evaluate(final AEMETVariable<?> element) {
                     return element.getType() == Double.class;
                  }
               });

      final AEMETVariable<Double>[] result = new AEMETVariable[doubleVariables.size()];
      int i = 0;
      for (final AEMETVariable<?> variable : doubleVariables) {
         result[i] = (AEMETVariable<Double>) variable;
         i++;
      }

      return result;
   }


   public <T> TreeSet<Lapse> getLapses(final AEMETVariable<T> variable) {
      final TreeSet<Lapse> result = new TreeSet<Lapse>();

      for (final AEMETStation station : _stations) {
         if (station.hasValue(variable)) {
            final TreeSet<Lapse> stationLapses = station.getLapses(variable);
            result.addAll(stationLapses);
         }
      }

      return result;
   }


   public AEMETStation getStationById(final String stationId) {
      return getStationsMap().get(stationId);
   }


   public List<GInterpolatedArea> getAreas() {
      if (_areas == null) {
         _areas = createAreas(getStations(), true);
      }
      return Collections.unmodifiableList(_areas);
   }


   public static List<GInterpolatedArea> createAreas(final List<AEMETStation> stations,
                                                     final boolean removeLargeTriangles) {
      final Delaunay_Triangulation triangulator = createTriangulator(stations);

      final List<GInterpolatedArea> areas = new ArrayList<GInterpolatedArea>();

      final Iterator<Triangle_dt> trianglesIterator = triangulator.trianglesIterator();
      while (trianglesIterator.hasNext()) {
         final Triangle_dt triangle_dt = trianglesIterator.next();
         if (triangle_dt.isHalfplane()) {
            continue;
         }

         final AEMETStation station1 = searchStation(stations, toVector3(triangle_dt.p1()));
         final AEMETStation station2 = searchStation(stations, toVector3(triangle_dt.p2()));
         final AEMETStation station3 = searchStation(stations, toVector3(triangle_dt.p3()));

         final boolean ignoreTriangle = removeLargeTriangles && isLarge(station1, station2, station3);
         if (!ignoreTriangle) {
            areas.add(new GInterpolatedArea(station1, station2, station3));
         }
      }

      return areas;
   }

   private static final double SQUARED_DISTANCE_THRESHOLD = Math.pow(0.02, 2);


   private static boolean isLarge(final AEMETStation station1,
                                  final AEMETStation station2,
                                  final AEMETStation station3) {
      final IVector2 position1 = station1.getPosition().asVector2();
      final IVector2 position2 = station2.getPosition().asVector2();
      final IVector2 position3 = station3.getPosition().asVector2();

      return (position1.squaredDistance(position2) > SQUARED_DISTANCE_THRESHOLD) || //
             (position1.squaredDistance(position3) > SQUARED_DISTANCE_THRESHOLD) || //
             (position2.squaredDistance(position3) > SQUARED_DISTANCE_THRESHOLD);
   }


   private static Delaunay_Triangulation createTriangulator(final List<AEMETStation> stations) {
      final Point_dt[] pointsDT = new Point_dt[stations.size()];

      for (int i = 0; i < stations.size(); i++) {
         pointsDT[i] = to(stations.get(i).getPosition());
      }

      return new Delaunay_Triangulation(pointsDT);
   }


   private static IVector3 toVector3(final Point_dt point) {
      return new GVector3D(point.x(), point.y(), point.z());
   }


   private static Point_dt to(final IVector3 point) {
      return new Point_dt(point.x(), point.y(), point.z());
   }


   private static AEMETStation searchStation(final List<AEMETStation> stations,
                                             final IVector3 position) {
      final Iterator<AEMETStation> iterator = stations.iterator();
      if (!iterator.hasNext()) {
         throw new RuntimeException("Can't find an station at: " + position);
      }

      AEMETStation closestStation = iterator.next();
      double closestDistance = position.squaredDistance(closestStation.getPosition());

      while (iterator.hasNext()) {
         final AEMETStation candidate = iterator.next();
         final double candidateDistance = position.squaredDistance(candidate.getPosition());
         if (candidateDistance < closestDistance) {
            closestStation = candidate;
            closestDistance = candidateDistance;
         }
      }

      return closestStation;

      //      for (final AEMETStation station : stations) {
      //         if (position.equals(station.getPosition())) {
      //            return station;
      //         }
      //      }
      //      throw new RuntimeException("Can't find an station at: " + position);
   }


   public AEMETVariable<Double> getDefaultVariable() {
      return getVariable("TA");
   }


}
