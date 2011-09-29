

package es.igosoftware.euclid.experimental.measurement;


public interface IUnit<FamilyT extends IUnit<FamilyT>> {


   public GMeasurementSystem getSystem();


   public String getName();


   public double convertionFactor();


   public IMeasure<FamilyT> value(final double value);


}
