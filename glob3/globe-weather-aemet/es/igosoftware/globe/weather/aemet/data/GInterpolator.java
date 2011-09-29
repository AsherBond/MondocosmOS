

package es.igosoftware.globe.weather.aemet.data;

import java.util.List;

import es.igosoftware.euclid.shape.GTriangle2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.globe.weather.aemet.GInterpolatedArea;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GPair;
import es.igosoftware.util.GPredicate;
import es.igosoftware.util.GTriplet;
import es.igosoftware.util.LRUCache;


public class GInterpolator {

   private final AEMETData                                                                                _data;
   private final LRUCache<GPair<AEMETVariable<Double>, Lapse>, List<GInterpolatedArea>, RuntimeException> _areasCache;
   private final LRUCache<GTriplet<AEMETVariable<Double>, Lapse, AEMETStation>, Double, RuntimeException> _interpolationCache;


   public GInterpolator(final AEMETData data) {
      _data = data;

      _areasCache = new LRUCache<GPair<AEMETVariable<Double>, Lapse>, List<GInterpolatedArea>, RuntimeException>(1000,
               new LRUCache.ValueFactory<GPair<AEMETVariable<Double>, Lapse>, List<GInterpolatedArea>, RuntimeException>() {
                  @Override
                  public List<GInterpolatedArea> create(final GPair<AEMETVariable<Double>, Lapse> key) {
                     return createAreas(key._first, key._second);
                  }
               });


      _interpolationCache = new LRUCache<GTriplet<AEMETVariable<Double>, Lapse, AEMETStation>, Double, RuntimeException>(1000,
               new LRUCache.ValueFactory<GTriplet<AEMETVariable<Double>, Lapse, AEMETStation>, Double, RuntimeException>() {
                  @Override
                  public Double create(final GTriplet<AEMETVariable<Double>, Lapse, AEMETStation> key) throws RuntimeException {
                     return calculate(key._first, key._second, key._third);
                  }
               }, 10);
   }


   public double interpolate(final AEMETVariable<Double> variable,
                             final Lapse lapse,
                             final AEMETStation station) {


      final GTriplet<AEMETVariable<Double>, Lapse, AEMETStation> key = new GTriplet<AEMETVariable<Double>, Lapse, AEMETStation>(
               variable, lapse, station);

      return _interpolationCache.get(key);

      //      final List<GInterpolatedArea> areas = getAreas(variable, lapse);
      //
      //      final GInterpolatedArea nearestArea = selectNearestArea(areas, station.getPosition().asVector2());
      //      if (nearestArea == null) {
      //         return Double.NaN;
      //      }
      //
      //      final IVector3 weight = nearestArea.getTriangle().getBarycentricCoordinates(station.getPosition().asVector2());
      //
      //      final double value1 = nearestArea.getStation1().getValue(variable, lapse);
      //      final double value2 = nearestArea.getStation2().getValue(variable, lapse);
      //      final double value3 = nearestArea.getStation3().getValue(variable, lapse);
      //
      //      return (value1 * weight.z()) + //
      //             (value2 * weight.y()) + //
      //             (value3 * weight.x());
   }


   private double calculate(final AEMETVariable<Double> variable,
                            final Lapse lapse,
                            final AEMETStation station) {


      final List<GInterpolatedArea> areas = getAreas(variable, lapse);

      final GInterpolatedArea nearestArea = selectNearestArea(areas, station.getPosition().asVector2());
      if (nearestArea == null) {
         return Double.NaN;
      }

      final IVector3 weight = nearestArea.getTriangle().getBarycentricCoordinates(station.getPosition().asVector2());

      final double value1 = nearestArea.getStation1().getValue(variable, lapse);
      final double value2 = nearestArea.getStation2().getValue(variable, lapse);
      final double value3 = nearestArea.getStation3().getValue(variable, lapse);

      return (value1 * weight.z()) + //
             (value2 * weight.y()) + //
             (value3 * weight.x());
   }


   private List<GInterpolatedArea> getAreas(final AEMETVariable<Double> variable,
                                            final Lapse lapse) {
      return _areasCache.get(new GPair<AEMETVariable<Double>, Lapse>(variable, lapse));
   }


   private List<GInterpolatedArea> createAreas(final AEMETVariable<Double> variable,
                                               final Lapse lapse) {
      final List<AEMETStation> stations = GCollections.select(_data.getStations(), new GPredicate<AEMETStation>() {
         @Override
         public boolean evaluate(final AEMETStation each) {
            return each.hasValue(variable, lapse);
         }
      });

      return AEMETData.createAreas(stations, false);
   }


   private static GInterpolatedArea selectNearestArea(final List<GInterpolatedArea> areas,
                                                      final IVector2 position) {

      double minDistance = Double.POSITIVE_INFINITY;
      GInterpolatedArea nearest = null;

      for (final GInterpolatedArea area : areas) {
         final GTriangle2D triangle = area.getTriangle();

         if (triangle.contains(position)) {
            return area;
         }

         final double distance = triangle.squaredDistanceToBoundary(position);
         if (distance < minDistance) {
            nearest = area;
            minDistance = distance;
         }
      }

      return nearest;
   }


}
