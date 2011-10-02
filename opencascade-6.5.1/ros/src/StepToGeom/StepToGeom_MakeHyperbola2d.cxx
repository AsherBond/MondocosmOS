// File:	StepToGeom_MakeHyperbola2d.cxx
// Created:	Thu Sep  1 13:57:24 1994
// Author:	Frederic MAUPAS

#include <StepToGeom_MakeHyperbola2d.ixx>
#include <StepGeom_Hyperbola.hxx>
#include <StepGeom_Axis2Placement2d.hxx>
#include <Geom2d_AxisPlacement.hxx>
#include <StepToGeom_MakeAxisPlacement.hxx>
#include <gp_Ax2.hxx>
#include <gp_Ax22d.hxx>

//=============================================================================
// Creation d' un Hyperbola de Geom2d a partir d' un Hyperbola de Step
//=============================================================================

Standard_Boolean StepToGeom_MakeHyperbola2d::Convert
    (const Handle(StepGeom_Hyperbola)& SC,
     Handle(Geom2d_Hyperbola)& CC)
{
  const StepGeom_Axis2Placement AxisSelect = SC->Position();
  if (AxisSelect.CaseNum(AxisSelect.Value()) == 1) {
    Handle(Geom2d_AxisPlacement) A1;
    if (StepToGeom_MakeAxisPlacement::Convert
          (Handle(StepGeom_Axis2Placement2d)::DownCast(AxisSelect.Value()),A1))
    {
      const gp_Ax22d A( A1->Ax2d() );
      CC = new Geom2d_Hyperbola(A, SC->SemiAxis(), SC->SemiImagAxis());
      return Standard_True;
    }
  }
  return Standard_False;
}
