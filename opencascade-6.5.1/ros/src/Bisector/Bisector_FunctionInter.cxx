// File:	Bisector_FunctionInter.cxx
// Created:	Tue Apr  5 14:33:16 1994
// Author:	Yves FRICAUD
//		<yfr@phylox>


#include <Bisector_FunctionInter.ixx>
#include <Geom2d_Curve.hxx>
#include <Bisector_BisecCC.hxx>
#include <Bisector_BisecPC.hxx>
#include <gp_Pnt2d.hxx>
#include <gp_Vec2d.hxx>
#include <gp.hxx>
#include <Precision.hxx>

//=============================================================================
//function :
// purpose :
//=============================================================================
Bisector_FunctionInter::Bisector_FunctionInter ()
{
}

//=============================================================================
//function :
// purpose :
//=============================================================================
Bisector_FunctionInter::Bisector_FunctionInter (const Handle(Geom2d_Curve)&   C  ,
						const Handle(Bisector_Curve)& B1 ,
						const Handle(Bisector_Curve)& B2 )
{
  curve     = C;
  bisector1 = B1;
  bisector2 = B2;
}

//=============================================================================
//function :
// purpose :
//=============================================================================
void Bisector_FunctionInter::Perform (const Handle(Geom2d_Curve)&     C  ,
				      const Handle(Bisector_Curve)&   B1 ,
				      const Handle(Bisector_Curve)&   B2 )
{
  curve     = C;
  bisector1 = B1;
  bisector2 = B2;
}

//=============================================================================
// function : Value
// purpose :
///=============================================================================
Standard_Boolean Bisector_FunctionInter::Value (const Standard_Real  X,
						      Standard_Real& F)
{
  gp_Pnt2d PC  = curve     ->Value(X);
  gp_Pnt2d PB1 = bisector1 ->Value(X);
  gp_Pnt2d PB2 = bisector2 ->Value(X);

  F = PC.Distance(PB1) - PC.Distance(PB2);

  return Standard_True;
}

//=============================================================================
//function : Derivative
// purpose :
//=============================================================================
Standard_Boolean Bisector_FunctionInter::Derivative(const Standard_Real  X,
						          Standard_Real& D)
{
  Standard_Real F;
  return Values (X,F,D);
}

//=============================================================================
//function : Values
// purpose :
//=============================================================================
Standard_Boolean Bisector_FunctionInter::Values (const Standard_Real  X,
					               Standard_Real& F,
					               Standard_Real& D)
{
  gp_Pnt2d PC, PB1, PB2;
  gp_Vec2d TC, TB1, TB2;
  Standard_Real F1, F2, DF1, DF2;

  curve     ->D1(X,PC ,TC);
  bisector1 ->D1(X,PB1,TB1);
  bisector2 ->D1(X,PB2,TB2);
  F1 = PC.Distance(PB1);
  F2 = PC.Distance(PB2);
  F  = F1 - F2;
  if (Abs(F1) < gp::Resolution()) {
    DF1 = Precision::Infinite();
  }
  else {
    DF1 = ((PC.X() - PB1.X())*(TC.X() - TB1.X()) +
	   (PC.Y() - PB1.Y())*(TC.Y() - TB1.Y()) )/F1;
  }
  if (Abs(F2) < gp::Resolution()) {
    DF2 = Precision::Infinite();
  }
  else {
    DF2 = ((PC.X() - PB2.X())*(TC.X() - TB2.X()) +
	   (PC.Y() - PB2.Y())*(TC.Y() - TB2.Y()) )/F2;
  }
  D = DF1 - DF2;

  return Standard_True;
}

