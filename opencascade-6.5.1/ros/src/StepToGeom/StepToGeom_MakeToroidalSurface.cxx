// File:	StepToGeom_MakeToroidalSurface.cxx
// Created:	Mon Jul  5 11:06:16 1993
// Author:	Martine LANGLOIS

#include <StepToGeom_MakeToroidalSurface.ixx>
#include <StepGeom_ToroidalSurface.hxx>
#include <StepGeom_Axis2Placement3d.hxx>
#include <StepToGeom_MakeAxis2Placement.hxx>
#include <Geom_Axis2Placement.hxx>
#include <gp_Ax2.hxx>
#include <Geom_ToroidalSurface.hxx>
#include <UnitsMethods.hxx>

//=============================================================================
// Creation d' une ToroidalSurface de Geom a partir d' une 
// ToroidalSurface de Step
//=============================================================================

Standard_Boolean StepToGeom_MakeToroidalSurface::Convert (const Handle(StepGeom_ToroidalSurface)& SS, Handle(Geom_ToroidalSurface)& CS)
{
  Handle(Geom_Axis2Placement) A;
  if (StepToGeom_MakeAxis2Placement::Convert(SS->Position(),A))
  {
    const Standard_Real LF = UnitsMethods::LengthFactor();
    CS = new Geom_ToroidalSurface(A->Ax2(), Abs(SS->MajorRadius() * LF), Abs(SS->MinorRadius() * LF));
    return Standard_True;
  }
  return Standard_False;
}
