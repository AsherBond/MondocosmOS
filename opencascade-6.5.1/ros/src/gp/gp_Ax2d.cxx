// File gp_Ax2d.cxx, JCV 06/90 
// File gp_Ax2d.cxx,  REG 27/10/90 nouvelle version
// JCV 08/01/91 modif introduction des classes XY et Mat dans le package
// LPA, JCV  07/92 passage sur C1.
// JCV 07/92 Introduction de la method Dump 

#define No_Standard_OutOfRange

#include <gp_Ax2d.ixx>
#include <gp_XY.hxx>

Standard_Boolean gp_Ax2d::IsCoaxial (const gp_Ax2d& Other, 
				     const Standard_Real AngularTolerance,
				     const Standard_Real LinearTolerance) const
{
  gp_XY XY1 = loc.XY();
  XY1.Subtract (Other.loc.XY());
  Standard_Real D1 = XY1.Crossed (Other.vdir.XY());
  if (D1 < 0) D1 = - D1;
  gp_XY XY2 = Other.loc.XY();
  XY2.Subtract (loc.XY());
  Standard_Real D2 = XY2.Crossed (vdir.XY());
  if (D2 < 0) D2 = - D2;
  return (vdir.IsParallel (Other.vdir, AngularTolerance) &&
	  D1 <= LinearTolerance && D2 <= LinearTolerance);
}

void gp_Ax2d::Scale (const gp_Pnt2d& P,
		     const Standard_Real S)
{
  loc.Scale(P, S);
  if (S < 0.0)  vdir.Reverse();
}

void gp_Ax2d::Mirror (const gp_Pnt2d& P)
{
  loc.Mirror(P);
  vdir.Reverse();
}

gp_Ax2d gp_Ax2d::Mirrored (const gp_Pnt2d& P) const
{
  gp_Ax2d A = *this;    
  A.Mirror (P);
  return A;
}

void gp_Ax2d::Mirror (const gp_Ax2d& A)
{
  loc.Mirror (A);
  vdir.Mirror (A.vdir); 
}

gp_Ax2d gp_Ax2d::Mirrored (const gp_Ax2d& A) const
{
  gp_Ax2d AA = *this;
  AA.Mirror (A);
  return AA;
}

