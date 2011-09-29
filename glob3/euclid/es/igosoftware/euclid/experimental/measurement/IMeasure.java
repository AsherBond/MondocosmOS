

package es.igosoftware.euclid.experimental.measurement;


public interface IMeasure<UnitT extends IUnit<UnitT>> {


   public double getValue();


   public UnitT getUnit();


   public IMeasure<UnitT> add(final IMeasure<UnitT> that);


   public IMeasure<UnitT> sub(final IMeasure<UnitT> that);


   //   public IMeasure<UnitT> simplified();


   public double getValueInReferenceUnits();


   public IMeasure<UnitT> max(final IMeasure<UnitT> that);


   public IMeasure<UnitT> min(final IMeasure<UnitT> that);


}
