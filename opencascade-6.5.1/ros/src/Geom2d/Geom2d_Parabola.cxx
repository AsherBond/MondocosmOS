// File:	Geom2d_Parabola.cxx
// Created:	Wed Mar 24 19:27:19 1993
// Author:	JCV
//		<fid@sdsun2>
// Copyright:	Matra Datavision 1993

//File Geom2d_Parabola.cxx, JCV 17/01/91

#include <Geom2d_Parabola.ixx>

#include <Precision.hxx>
#include <gp_XY.hxx>
#include <ElCLib.hxx>
#include <gp_Dir2d.hxx>
#include <Standard_ConstructionError.hxx>
#include <Standard_RangeError.hxx>

typedef Geom2d_Parabola         Parabola;
typedef Handle(Geom2d_Parabola) Handle(Parabola);
typedef gp_Ax2d   Ax2d;
typedef gp_Dir2d  Dir2d;
typedef gp_Pnt2d  Pnt2d;
typedef gp_Vec2d  Vec2d;
typedef gp_Trsf2d Trsf2d;
typedef gp_XY     XY;






//=======================================================================
//function : Copy
//purpose  : 
//=======================================================================

Handle(Geom2d_Geometry) Geom2d_Parabola::Copy() const 
{
  Handle(Parabola) Prb;
  Prb = new Parabola (pos, focalLength);
  return Prb;
}



//=======================================================================
//function : Geom2d_Parabola
//purpose  : 
//=======================================================================

Geom2d_Parabola::Geom2d_Parabola (const gp_Parab2d& Prb) 
{
  focalLength = Prb.Focal ();
  pos         = Prb.Axis();
}


//=======================================================================
//function : Geom2d_Parabola
//purpose  : 
//=======================================================================

Geom2d_Parabola::Geom2d_Parabola (const Ax2d& MirrorAxis, 
				  const Standard_Real Focal, 
				  const Standard_Boolean Sense) 
: focalLength (Focal) 
{
  if (Focal < 0.0) { Standard_ConstructionError::Raise(); }
  pos = gp_Ax22d(MirrorAxis, Sense);
}


//=======================================================================
//function : Geom2d_Parabola
//purpose  : 
//=======================================================================

Geom2d_Parabola::Geom2d_Parabola (const gp_Ax22d& Axis, const Standard_Real Focal)
: focalLength (Focal) 
{
  if (Focal < 0.0) { Standard_ConstructionError::Raise(); }
  pos  = Axis;
}


//=======================================================================
//function : Geom2d_Parabola
//purpose  : 
//=======================================================================

Geom2d_Parabola::Geom2d_Parabola (const Ax2d& D, const Pnt2d& F) {

  gp_Parab2d Prb (D, F);
  pos = Prb.Axis();
  focalLength = Prb.Focal();
}

//=======================================================================
//function : SetFocal
//purpose  : 
//=======================================================================

void Geom2d_Parabola::SetFocal (const Standard_Real Focal) 
{
  if (Focal < 0.0) Standard_ConstructionError::Raise();
  focalLength = Focal;
}


//=======================================================================
//function : SetParab2d
//purpose  : 
//=======================================================================

void Geom2d_Parabola::SetParab2d (const gp_Parab2d& Prb) 
{
  focalLength = Prb.Focal ();
  pos = Prb.Axis();
}

//=======================================================================
//function : Parab2d
//purpose  : 
//=======================================================================

gp_Parab2d Geom2d_Parabola::Parab2d () const 
{
  return gp_Parab2d (pos, focalLength);
}

//=======================================================================
//function : ReversedParameter
//purpose  : 
//=======================================================================

Standard_Real Geom2d_Parabola::ReversedParameter( const Standard_Real U) const
{
  return ( -U);
}

//=======================================================================
//function : FirstParameter
//purpose  : 
//=======================================================================

Standard_Real Geom2d_Parabola::FirstParameter () const   
{
  return -Precision::Infinite(); 
}

//=======================================================================
//function : LastParameter
//purpose  : 
//=======================================================================

Standard_Real Geom2d_Parabola::LastParameter () const    
{
  return Precision::Infinite(); 
}

//=======================================================================
//function : IsClosed
//purpose  : 
//=======================================================================

Standard_Boolean Geom2d_Parabola::IsClosed ()   const    
{
  return Standard_False; 
}

//=======================================================================
//function : IsPeriodic
//purpose  : 
//=======================================================================

Standard_Boolean Geom2d_Parabola::IsPeriodic () const    
{
  return Standard_False; 
}

//=======================================================================
//function : Directrix
//purpose  : 
//=======================================================================

Ax2d Geom2d_Parabola::Directrix () const 
{
  gp_Parab2d Prb (pos, focalLength);
  return Prb.Directrix();
}

//=======================================================================
//function : Eccentricity
//purpose  : 
//=======================================================================

Standard_Real Geom2d_Parabola::Eccentricity () const     
{
  return 1.0; 
}

//=======================================================================
//function : Focus
//purpose  : 
//=======================================================================

Pnt2d Geom2d_Parabola::Focus () const 
{
  XY Pxy = pos.XDirection().XY();
  Pxy.Multiply (focalLength);
  Pxy.Add (pos.Location().XY());
  return Pnt2d (Pxy);
}

//=======================================================================
//function : Focal
//purpose  : 
//=======================================================================

Standard_Real Geom2d_Parabola::Focal () const            
{
  return focalLength; 
}

//=======================================================================
//function : Parameter
//purpose  : 
//=======================================================================

Standard_Real Geom2d_Parabola::Parameter () const        
{
  return 2.0 * focalLength; 
}

//=======================================================================
//function : D0
//purpose  : 
//=======================================================================

void Geom2d_Parabola::D0 (const Standard_Real   U,
			        Pnt2d& P) const 
{
  P = ElCLib::ParabolaValue (U, pos, focalLength);
}

//=======================================================================
//function : D1
//purpose  : 
//=======================================================================

void Geom2d_Parabola::D1 (const Standard_Real U, Pnt2d& P, Vec2d& V1) const 
{
  ElCLib::ParabolaD1 (U, pos, focalLength, P, V1);
}

//=======================================================================
//function : D2
//purpose  : 
//=======================================================================

void Geom2d_Parabola::D2 (const Standard_Real U, 
			        Pnt2d& P, 
			        Vec2d& V1, Vec2d& V2) const 
{
  ElCLib::ParabolaD2 (U, pos, focalLength, P, V1, V2);
}

//=======================================================================
//function : D3
//purpose  : 
//=======================================================================

void Geom2d_Parabola::D3 (const Standard_Real U, 
			        Pnt2d& P, 
			        Vec2d& V1, Vec2d& V2, Vec2d& V3) const 
{
   ElCLib::ParabolaD2 (U, pos, focalLength, P, V1, V2);
   V3.SetCoord (0.0, 0.0);
}

//=======================================================================
//function : DN
//purpose  : 
//=======================================================================

Vec2d Geom2d_Parabola::DN (const Standard_Real U, const Standard_Integer N) const 
{
   Standard_RangeError_Raise_if (N < 1, " ");
   return ElCLib::ParabolaDN (U, pos, focalLength, N);
}


//=======================================================================
//function : Transform
//purpose  : 
//=======================================================================

void Geom2d_Parabola::Transform (const Trsf2d& T) 
{
  focalLength *= Abs(T.ScaleFactor());
   pos.Transform (T);
}

//=======================================================================
//function : TransformedParameter
//purpose  : 
//=======================================================================

Standard_Real Geom2d_Parabola::TransformedParameter(const Standard_Real U,
						    const gp_Trsf2d& T) const
{
  if (Precision::IsInfinite(U)) return U;
  return U * Abs(T.ScaleFactor());
}

//=======================================================================
//function : ParametricTransformation
//purpose  : 
//=======================================================================

Standard_Real Geom2d_Parabola::ParametricTransformation(const gp_Trsf2d& T) const
{
  return Abs(T.ScaleFactor());
}


