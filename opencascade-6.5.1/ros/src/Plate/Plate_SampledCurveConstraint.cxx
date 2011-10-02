// File:	Plate_SampledCurveConstraint.cxx
// Created:	Mon May 18 16:55:59 1998
// Author:	Andre LIEUTIER
//		<alr@sgi63>


#include <Plate_SampledCurveConstraint.ixx>
#include <Plate_PinpointConstraint.hxx>
#include <Plate_SequenceOfPinpointConstraint.hxx>

static inline Standard_Real B0( Standard_Real t)
{
  Standard_Real s = t;
  if(s<0.) s = -s;
  s = 1. - s;
  if(s<0.) s = 0;
  return s;
}

Plate_SampledCurveConstraint::Plate_SampledCurveConstraint(const Plate_SequenceOfPinpointConstraint &SOPPC,
							   const Standard_Integer n)
:myLXYZC(n,SOPPC.Length())
{
  Standard_Integer m = SOPPC.Length();

  if (n > m)  Standard_DimensionMismatch::Raise();
  for(Standard_Integer index =1; index <= m;index++)
    myLXYZC.SetPPC(index,SOPPC(index));

  Standard_Real ratio = Standard_Real(n+1) /Standard_Real(m+1); 
  for (Standard_Integer i=1;i<=n;i++)
    for (Standard_Integer j=1;j<=m;j++)
      {
	myLXYZC.SetCoeff(i,j,B0(ratio*j - i));
      }
}
