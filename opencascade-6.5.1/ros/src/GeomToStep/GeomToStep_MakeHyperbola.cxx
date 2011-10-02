#include <GeomToStep_MakeHyperbola.ixx>

#include <StepGeom_Hyperbola.hxx>
#include <gp_Hypr.hxx>
#include <gp_Hypr2d.hxx>
#include <Geom_Hyperbola.hxx>
#include <GeomToStep_MakeAxis2Placement2d.hxx>
#include <GeomToStep_MakeAxis2Placement3d.hxx>
#include <StdFail_NotDone.hxx>
#include <TCollection_HAsciiString.hxx>
#include <UnitsMethods.hxx>


//=============================================================================
// Creation d'une hyperbola de prostep a partir d'une hyperbola de
// Geom2d
//=============================================================================

 GeomToStep_MakeHyperbola::GeomToStep_MakeHyperbola(const Handle(Geom2d_Hyperbola)& C)
{
  gp_Hypr2d gpHyp;
  gpHyp = C->Hypr2d();

  Handle(StepGeom_Hyperbola) HStep = new StepGeom_Hyperbola;
  StepGeom_Axis2Placement            Ax2;
  Handle(StepGeom_Axis2Placement2d)  Ax2Step;
  Standard_Real                   majorR, minorR;
  
  GeomToStep_MakeAxis2Placement2d MkAxis2(gpHyp.Axis());
  Ax2Step = MkAxis2.Value();
  majorR = gpHyp.MajorRadius();
  minorR = gpHyp.MinorRadius();
  Ax2.SetValue(Ax2Step);
  Handle(TCollection_HAsciiString) name = new TCollection_HAsciiString("");
  HStep->Init(name, Ax2,majorR,minorR);
  theHyperbola = HStep;
  done = Standard_True;
}

//=============================================================================
// Creation d'une hyperbola de prostep a partir d'une hyperbola de
// Geom
//=============================================================================

 GeomToStep_MakeHyperbola::GeomToStep_MakeHyperbola(const Handle(Geom_Hyperbola)& C)
{
  gp_Hypr gpHyp;
  gpHyp = C->Hypr();

  Handle(StepGeom_Hyperbola) HStep = new StepGeom_Hyperbola;
  StepGeom_Axis2Placement            Ax2;
  Handle(StepGeom_Axis2Placement3d)  Ax2Step;
  Standard_Real                   majorR, minorR;
  
  GeomToStep_MakeAxis2Placement3d MkAxis2(gpHyp.Position());
  Ax2Step = MkAxis2.Value();
  majorR = gpHyp.MajorRadius();
  minorR = gpHyp.MinorRadius();
  Ax2.SetValue(Ax2Step);
  Handle(TCollection_HAsciiString) name = new TCollection_HAsciiString("");
  Standard_Real fact = UnitsMethods::LengthFactor();
  HStep->Init(name, Ax2,majorR/fact,minorR/fact);
  theHyperbola = HStep;
  done = Standard_True;
}

//=============================================================================
// return the result
//=============================================================================

const Handle(StepGeom_Hyperbola)& GeomToStep_MakeHyperbola::Value() const 
{
  StdFail_NotDone_Raise_if(!done == Standard_True,"");
  return theHyperbola;
}

