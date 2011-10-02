// File:	StepToGeom_MakeDirection2d.cxx
// Created:	Wed Aug  4 11:50:31 1993
// Author:	Martine LANGLOIS
// sln 23.10.2001. CTS23496: Direction is not created if it has null magnitude (StepToGeom_MakeDirection2d(...) function)

#include <StepToGeom_MakeDirection2d.ixx>
#include <StepGeom_Direction.hxx>

//=============================================================================
// Creation d' un Direction de Geom2d a partir d' un Direction de Step
//=============================================================================

Standard_Boolean StepToGeom_MakeDirection2d::Convert (const Handle(StepGeom_Direction)& SD, Handle(Geom2d_Direction)& CD)
{
  if (SD->NbDirectionRatios() >= 2)
  {
    const Standard_Real X = SD->DirectionRatiosValue(1);
    const Standard_Real Y = SD->DirectionRatiosValue(2);
    // sln 23.10.2001. CTS23496: Direction is not created if it has null magnitude
    if(gp_XY(X,Y).SquareModulus() > gp::Resolution()*gp::Resolution())
    {
      CD = new Geom2d_Direction(X, Y);
      return Standard_True;
    }
  }
  return Standard_False;   
}
