// File:	StepToGeom_MakeHyperbola.cxx
// Created:	Thu Sep  8 08:25:12 1994
// Author:	Frederic MAUPAS

#include <StepToGeom_MakeHyperbola.ixx>
#include <StepGeom_Hyperbola.hxx>
#include <StepGeom_Axis2Placement3d.hxx>
#include <Geom_Axis2Placement.hxx>
#include <StepToGeom_MakeAxis2Placement.hxx>
#include <gp_Ax2.hxx>
#include <UnitsMethods.hxx>

//=============================================================================
// Creation d' un Hyperbola de Geom a partir d' un Hyperbola de Step
//=============================================================================

Standard_Boolean StepToGeom_MakeHyperbola::Convert
    (const Handle(StepGeom_Hyperbola)& SC,
     Handle(Geom_Hyperbola)& CC)
{
  const StepGeom_Axis2Placement AxisSelect = SC->Position();
  if (AxisSelect.CaseNum(AxisSelect.Value()) == 2)
  {
    Handle(Geom_Axis2Placement) A1;
    if (StepToGeom_MakeAxis2Placement::Convert
          (Handle(StepGeom_Axis2Placement3d)::DownCast(AxisSelect.Value()),A1))
    {
      const gp_Ax2 A( A1->Ax2() );
      const Standard_Real LF = UnitsMethods::LengthFactor();
      CC = new Geom_Hyperbola(A, SC->SemiAxis() * LF, SC->SemiImagAxis() * LF);
      return Standard_True;
    }
  }
  return Standard_False;
}
