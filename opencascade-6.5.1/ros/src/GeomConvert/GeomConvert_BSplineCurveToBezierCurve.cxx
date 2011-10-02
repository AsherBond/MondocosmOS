// File:	GeomConvert_BSplineCurveToBezierCurve.cxx
// Created:	Tue Mar 12 17:14:53 1996
// Author:	Bruno DUMORTIER
//		<dub@fuegox>


#include <GeomConvert_BSplineCurveToBezierCurve.ixx>

#include <TColgp_Array1OfPnt.hxx>
#include <TColStd_Array1OfReal.hxx>


//=======================================================================
//function : GeomConvert_BSplineCurveToBezierCurve
//purpose  : 
//=======================================================================

GeomConvert_BSplineCurveToBezierCurve::GeomConvert_BSplineCurveToBezierCurve (const Handle(Geom_BSplineCurve)& BasisCurve)
{
  myCurve = Handle(Geom_BSplineCurve)::DownCast(BasisCurve->Copy());
  Standard_Real Uf = myCurve->FirstParameter();
  Standard_Real Ul = myCurve->LastParameter();
  myCurve->Segment(Uf,Ul);
  myCurve->IncreaseMultiplicity(myCurve->FirstUKnotIndex(),
				myCurve->LastUKnotIndex(),
				myCurve->Degree());
}


//=======================================================================
//function : GeomConvert_BSplineCurveToBezierCurve
//purpose  : 
// 01/12/1997 PMN: On elimine d'eventuelles micro-courbe PRO11516
//=======================================================================Real I

GeomConvert_BSplineCurveToBezierCurve::GeomConvert_BSplineCurveToBezierCurve
(const Handle(Geom_BSplineCurve)& BasisCurve, 
 const Standard_Real U1, 
 const Standard_Real U2,
 const Standard_Real ParametricTolerance)
{
  if (U2 - U1 < ParametricTolerance)
      Standard_DomainError::Raise("GeomConvert_BSplineCurveToBezierSurface");

  Standard_Real Uf = U1, Ul = U2; 
  Standard_Real PTol = ParametricTolerance/2 ;

  Standard_Integer I1, I2;
  myCurve = Handle(Geom_BSplineCurve)::DownCast(BasisCurve->Copy());

  myCurve->LocateU(U1, PTol, I1, I2);
  if (I1==I2) { // On est sur le noeud
    if ( myCurve->Knot(I1) > U1) Uf = myCurve->Knot(I1);
  }

  myCurve->LocateU(U2, PTol, I1, I2);
  if (I1==I2) { // On est sur le noeud
    if ( myCurve->Knot(I1) < U2) Ul = myCurve->Knot(I1);
  }

  myCurve->Segment(Uf,Ul);
  myCurve->IncreaseMultiplicity(myCurve->FirstUKnotIndex(),
				myCurve->LastUKnotIndex(),
				myCurve->Degree());
}


//=======================================================================
//function : Arc
//purpose  : 
//=======================================================================

Handle(Geom_BezierCurve) GeomConvert_BSplineCurveToBezierCurve::Arc
(const Standard_Integer Index)
{
  if ( Index < 1 || Index > myCurve->NbKnots()-1) {
    Standard_OutOfRange::Raise("GeomConvert_BSplineCurveToBezierCurve");
  }
  Standard_Integer Deg = myCurve->Degree();

  TColgp_Array1OfPnt Poles(1,Deg+1);

  Handle(Geom_BezierCurve) C;
  if ( myCurve->IsRational()) {
    TColStd_Array1OfReal Weights(1,Deg+1);
    for ( Standard_Integer i = 1; i <= Deg+1; i++) {
      Poles(i)   = myCurve->Pole(i+Deg*(Index-1));
      Weights(i) = myCurve->Weight(i+Deg*(Index-1));
    }
    C = new Geom_BezierCurve(Poles,Weights);
  }
  else {
    for ( Standard_Integer i = 1; i <= Deg+1; i++) {
      Poles(i)   = myCurve->Pole(i+Deg*(Index-1));
    }
    C = new Geom_BezierCurve(Poles);
  }
  return C;
}


//=======================================================================
//function : Arcs
//purpose  : 
//=======================================================================

void GeomConvert_BSplineCurveToBezierCurve::Arcs
(TColGeom_Array1OfBezierCurve& Curves)
{
  Standard_Integer n = NbArcs();
  for ( Standard_Integer i = 1; i <= n; i++) {
    Curves(i) = Arc(i);
  } 
}

//=======================================================================
//function : Knots
//purpose  : 
//=======================================================================

void GeomConvert_BSplineCurveToBezierCurve::Knots
     (TColStd_Array1OfReal& TKnots) const
{
 Standard_Integer ii, kk;
  for (ii = 1, kk = TKnots.Lower();
       ii <=myCurve->NbKnots(); ii++, kk++)
    TKnots(kk) = myCurve->Knot(ii);
}

//=======================================================================
//function : NbArcs
//purpose  : 
//=======================================================================

Standard_Integer GeomConvert_BSplineCurveToBezierCurve::NbArcs() const 
{
  return (myCurve->NbKnots()-1);
}
