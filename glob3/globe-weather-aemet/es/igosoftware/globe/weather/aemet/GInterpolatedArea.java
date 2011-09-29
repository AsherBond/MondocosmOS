

package es.igosoftware.globe.weather.aemet;

import java.util.List;

import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.shape.GTriangle2D;
import es.igosoftware.euclid.vector.GVectorUtils;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.globe.weather.aemet.GInterpolatedSurface.ColoredVertex;
import es.igosoftware.globe.weather.aemet.data.AEMETStation;
import es.igosoftware.globe.weather.aemet.data.AEMETVariable;
import es.igosoftware.globe.weather.aemet.data.GInterpolator;
import es.igosoftware.globe.weather.aemet.data.Lapse;
import es.igosoftware.util.GCollections;


public class GInterpolatedArea {
   private final AEMETStation          _station1;
   private final AEMETStation          _station2;
   private final AEMETStation          _station3;
   private final IVector3              _referencePosition;
   private final GAxisAlignedRectangle _bounds;
   private GTriangle2D                 _triangle;


   public GInterpolatedArea(final AEMETStation station1,
                            final AEMETStation station2,
                            final AEMETStation station3) {
      _station1 = station1;
      _station2 = station2;
      _station3 = station3;

      _referencePosition = GVectorUtils.getAverage( //
               station1.getPosition(), //
               station2.getPosition(), //
               station3.getPosition());

      _bounds = GAxisAlignedRectangle.minimumBoundingRectangle( //
               station1.getPosition().asVector2(), //
               station2.getPosition().asVector2(), //
               station3.getPosition().asVector2());
   }


   List<ColoredVertex> getGeometry(final AEMETVariable<Double> variable,
                                   final Lapse lapse,
                                   final GInterpolator interpolator) {
      return GCollections.asList( //
               new ColoredVertex(_station1.getWWPosition(), _station1.getColor(variable, lapse, interpolator)), //
               new ColoredVertex(_station2.getWWPosition(), _station2.getColor(variable, lapse, interpolator)), //
               new ColoredVertex(_station3.getWWPosition(), _station3.getColor(variable, lapse, interpolator)) //
      );
   }


   public IVector3 getReferencePosition() {
      return _referencePosition;
   }


   public GAxisAlignedRectangle getBounds() {
      return _bounds;
   }


   public GTriangle2D getTriangle() {
      if (_triangle == null) {
         _triangle = new GTriangle2D( //
                  _station1.getPosition().asVector2(), //
                  _station2.getPosition().asVector2(), //
                  _station3.getPosition().asVector2());
      }
      return _triangle;
   }


   public AEMETStation getStation1() {
      return _station1;
   }


   public AEMETStation getStation2() {
      return _station2;
   }


   public AEMETStation getStation3() {
      return _station3;
   }


}
