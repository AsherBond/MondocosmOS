// File:	Geom_Ellipse.cxx
// Created:	Wed Mar 10 09:43:49 1993
// Author:	JCV
//		<fid@phylox>
// Copyright:	Matra Datavision 1993

//File Geom_Ellipse.cxx, JCV 17/01/91

#include <Geom_Ellipse.ixx>
#include <gp_XYZ.hxx>
#include <ElCLib.hxx>
#include <Standard_ConstructionError.hxx>
#include <Standard_RangeError.hxx>

typedef Geom_Ellipse         Ellipse;
typedef Handle(Geom_Ellipse) Handle(Ellipse);
typedef gp_Ax1  Ax1;
typedef gp_Ax2  Ax2;
typedef gp_Pnt  Pnt;
typedef gp_Vec  Vec;
typedef gp_Trsf Trsf;
typedef gp_XYZ  XYZ;





//=======================================================================
//function : Copy
//purpose  : 
//=======================================================================

Handle(Geom_Geometry) Geom_Ellipse::Copy() const
{
  Handle(Ellipse) E;
  E = new Ellipse (pos, majorRadius, minorRadius);
  return E;
}




//=======================================================================
//function : Geom_Ellipse
//purpose  : 
//=======================================================================

Geom_Ellipse::Geom_Ellipse (const gp_Elips& E) 
  : majorRadius (E.MajorRadius()), minorRadius (E.MinorRadius()) 
{
  pos = E.Position ();
}


//=======================================================================
//function : Geom_Ellipse
//purpose  : 
//=======================================================================

Geom_Ellipse::Geom_Ellipse ( const Ax2& A, 
                             const Standard_Real MajorRadius,
                             const Standard_Real MinorRadius) 
  : majorRadius (MajorRadius), minorRadius (MinorRadius) {

   if (MajorRadius < MinorRadius || MinorRadius < 0.0 ) {
     Standard_ConstructionError::Raise();
   }
   pos = A;
}


//=======================================================================
//function : IsClosed
//purpose  : 
//=======================================================================

Standard_Boolean Geom_Ellipse::IsClosed () const      { return Standard_True; }

//=======================================================================
//function : IsPeriodic
//purpose  : 
//=======================================================================

Standard_Boolean Geom_Ellipse::IsPeriodic () const    { return Standard_True; }

//=======================================================================
//function : FirstParameter
//purpose  : 
//=======================================================================

Standard_Real Geom_Ellipse::FirstParameter () const   { return 0.0; }

//=======================================================================
//function : LastParameter
//purpose  : 
//=======================================================================

Standard_Real Geom_Ellipse::LastParameter () const    { return 2.0 * PI; }

//=======================================================================
//function : MajorRadius
//purpose  : 
//=======================================================================

Standard_Real Geom_Ellipse::MajorRadius () const      { return majorRadius; }

//=======================================================================
//function : MinorRadius
//purpose  : 
//=======================================================================

Standard_Real Geom_Ellipse::MinorRadius () const      { return minorRadius; }

//=======================================================================
//function : SetElips
//purpose  : 
//=======================================================================

void Geom_Ellipse::SetElips (const gp_Elips& E) {

  majorRadius = E.MajorRadius();
  minorRadius = E.MinorRadius();
  pos = E.Position();
}


//=======================================================================
//function : SetMajorRadius
//purpose  : 
//=======================================================================

void Geom_Ellipse::SetMajorRadius (const Standard_Real MajorRadius) {

  if (MajorRadius < minorRadius)  Standard_ConstructionError::Raise ();
  else                            majorRadius = MajorRadius; 
}


//=======================================================================
//function : SetMinorRadius
//purpose  : 
//=======================================================================

void Geom_Ellipse::SetMinorRadius (const Standard_Real MinorRadius) {

   if (MinorRadius < 0 || majorRadius < MinorRadius) {
     Standard_ConstructionError::Raise();
   }
   else { minorRadius = MinorRadius; }
}


//=======================================================================
//function : Elips
//purpose  : 
//=======================================================================

gp_Elips Geom_Ellipse::Elips () const {

  return gp_Elips (pos, majorRadius, minorRadius);
}


//=======================================================================
//function : ReversedParameter
//purpose  : 
//=======================================================================

Standard_Real Geom_Ellipse::ReversedParameter( const Standard_Real U) const 
{
  return ( 2. * PI - U);
}


//=======================================================================
//function : Directrix1
//purpose  : 
//=======================================================================

Ax1 Geom_Ellipse::Directrix1 () const {

   gp_Elips Ev (pos, majorRadius, minorRadius);
   return Ev.Directrix1();
}


//=======================================================================
//function : Directrix2
//purpose  : 
//=======================================================================

Ax1 Geom_Ellipse::Directrix2 () const {

  gp_Elips Ev (pos, majorRadius, minorRadius);
  return Ev.Directrix2();
}


//=======================================================================
//function : D0
//purpose  : 
//=======================================================================

void Geom_Ellipse::D0 (const Standard_Real U, gp_Pnt& P) const {

  P = ElCLib::EllipseValue (U, pos, majorRadius, minorRadius);
}


//=======================================================================
//function : D1
//purpose  : 
//=======================================================================

void Geom_Ellipse::D1 (const Standard_Real U, Pnt& P, Vec& V1) const {

  ElCLib::EllipseD1 (U, pos, majorRadius, minorRadius, P, V1);
}


//=======================================================================
//function : D2
//purpose  : 
//=======================================================================

void Geom_Ellipse::D2 (const Standard_Real U, Pnt& P, Vec& V1, Vec& V2) const {

  ElCLib::EllipseD2 (U, pos, majorRadius, minorRadius, P, V1, V2);
}


//=======================================================================
//function : D3
//purpose  : 
//=======================================================================

void Geom_Ellipse::D3 (const Standard_Real U, Pnt& P, Vec& V1, Vec& V2, Vec& V3) const {

  ElCLib::EllipseD3 (U, pos, majorRadius, minorRadius, P, V1, V2, V3);
}


//=======================================================================
//function : DN
//purpose  : 
//=======================================================================

Vec Geom_Ellipse::DN (const Standard_Real U, const Standard_Integer N) const {

   Standard_RangeError_Raise_if (N < 1, " ");
   return ElCLib::EllipseDN (U, pos, majorRadius, minorRadius, N);
}


//=======================================================================
//function : Eccentricity
//purpose  : 
//=======================================================================

Standard_Real Geom_Ellipse::Eccentricity () const {

  if (majorRadius == 0.0) { return 0.0; }
  else {
    return (Sqrt(majorRadius*majorRadius-minorRadius*minorRadius))/majorRadius;
  }
}


//=======================================================================
//function : Focal
//purpose  : 
//=======================================================================

Standard_Real Geom_Ellipse::Focal () const {

  return 2.0 * Sqrt(majorRadius * majorRadius - minorRadius * minorRadius);
}


//=======================================================================
//function : Focus1
//purpose  : 
//=======================================================================

Pnt Geom_Ellipse::Focus1 () const {

  Standard_Real C = Sqrt (majorRadius * majorRadius - minorRadius * minorRadius);
  Standard_Real Xp, Yp, Zp, Xd, Yd, Zd;
  pos.Location().Coord (Xp, Yp, Zp);
  pos.XDirection().Coord (Xd, Yd, Zd);
  return Pnt (Xp + C * Xd,  Yp + C * Yd,  Zp + C * Zd);
}


//=======================================================================
//function : Focus2
//purpose  : 
//=======================================================================

Pnt Geom_Ellipse::Focus2 () const {

  Standard_Real C = Sqrt (majorRadius * majorRadius - minorRadius * minorRadius);
  Standard_Real Xp, Yp, Zp, Xd, Yd, Zd;
  pos.Location().Coord (Xp, Yp, Zp);
  pos.XDirection().Coord (Xd, Yd, Zd);
  return Pnt (Xp - C * Xd,  Yp - C * Yd,  Zp - C * Zd);
}


//=======================================================================
//function : Parameter
//purpose  : 
//=======================================================================

Standard_Real Geom_Ellipse::Parameter () const {

  if (majorRadius == 0.0)  return 0.0;
  else                     return (minorRadius * minorRadius) / majorRadius;
}


//=======================================================================
//function : Transform
//purpose  : 
//=======================================================================

void Geom_Ellipse::Transform (const Trsf& T) {

  majorRadius = majorRadius * Abs(T.ScaleFactor());
  minorRadius = minorRadius * Abs(T.ScaleFactor());
  pos.Transform(T);
}
