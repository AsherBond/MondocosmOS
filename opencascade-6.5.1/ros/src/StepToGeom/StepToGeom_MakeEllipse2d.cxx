// File:	StepToGeom_MakeEllipse2d.cxx
// Created:	Thu Sep  1 13:57:24 1994
// Author:	Frederic MAUPAS
//:o9 abv 19.02.99: bm4_id_punch_b.stp #678: treatment of minR > maxR
//                  NOTE: parametrisation of resulting ellipse shifted on pi/2

#include <StepToGeom_MakeEllipse2d.ixx>
#include <StepGeom_Ellipse.hxx>
#include <StepGeom_Axis2Placement2d.hxx>
#include <Geom2d_AxisPlacement.hxx>
#include <StepToGeom_MakeAxisPlacement.hxx>
#include <gp_Ax2.hxx>
#include <gp_Ax22d.hxx>

//=============================================================================
// Creation d' un Ellipse de Geom2d a partir d' un Ellipse de Step
//=============================================================================

Standard_Boolean StepToGeom_MakeEllipse2d::Convert
    (const Handle(StepGeom_Ellipse)& SC,
     Handle(Geom2d_Ellipse)& CC)
{
  const StepGeom_Axis2Placement AxisSelect = SC->Position();
  if (AxisSelect.CaseNum(AxisSelect.Value()) == 1) {
    Handle(Geom2d_AxisPlacement) A1;
    if (StepToGeom_MakeAxisPlacement::Convert
          (Handle(StepGeom_Axis2Placement2d)::DownCast(AxisSelect.Value()),A1))
    {
      gp_Ax22d A( A1->Ax2d() );
      const Standard_Real majorR = SC->SemiAxis1();
      const Standard_Real minorR = SC->SemiAxis2();
      if ( majorR - minorR >= 0. ) { //:o9 abv 19 Feb 99: bm4_id_punch_b.stp #678: protection
        CC = new Geom2d_Ellipse(A, majorR, minorR);
      }
      else {
        const gp_Dir2d X = A.XDirection();
        A.SetXDirection ( gp_Dir2d ( X.X(), -X.Y() ) );
        CC = new Geom2d_Ellipse(A, minorR, majorR);
      }
      return Standard_True;
	}
  }
  return Standard_False;
}
