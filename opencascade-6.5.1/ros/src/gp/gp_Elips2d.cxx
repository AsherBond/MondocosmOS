//File gp_Elips2d.cxx FID 30/10/90
//Modif JCV 10/01/91

#include <gp_Elips2d.ixx>

void gp_Elips2d::Coefficients (Standard_Real& A, 
			       Standard_Real& B, 
			       Standard_Real& C, 
			       Standard_Real& D, 
			       Standard_Real& E, 
			       Standard_Real& F) const 
{
  Standard_Real DMin = minorRadius * minorRadius;
  Standard_Real DMaj = majorRadius * majorRadius;
  if (DMin <= gp::Resolution() && DMaj <= gp::Resolution()) {
    A = B = C = D = E = F = 0.0;
  }
  else {
    gp_Trsf2d T;
    T.SetTransformation (pos.XAxis());
    Standard_Real T11 = T.Value (1, 1);
    Standard_Real T12 = T.Value (1, 2);
    Standard_Real T13 = T.Value (1, 3);
    if (DMin <= gp::Resolution()) {
      A = T11 * T11;    B = T12 * T12;   C = T11 * T12;
      D = T11 * T13;    E = T12 * T13;   F = T13 * T13 - DMaj;
    }
    else {
      Standard_Real T21 = T.Value (2, 1);
      Standard_Real T22 = T.Value (2, 2);
      Standard_Real T23 = T.Value (2, 3);
      A = (T11 * T11 / DMaj) + (T21 * T21 / DMin);
      B = (T12 * T12 / DMaj) + (T22 * T22 / DMin);
      C = (T11 * T12 / DMaj) + (T21 * T22 / DMin);
      D = (T11 * T13 / DMaj) + (T21 * T23 / DMin);
      E = (T12 * T13 / DMaj) + (T22 * T23 / DMin);
      F = (T13 * T13 / DMaj) + (T23 * T23 / DMin) - 1.0;
    }
  }
}

void gp_Elips2d::Mirror (const gp_Pnt2d& P)
{ pos.Mirror(P); }

gp_Elips2d gp_Elips2d::Mirrored (const gp_Pnt2d& P) const  
{
  gp_Elips2d E = *this;
  E.pos.Mirror (P);
  return E; 
}

void gp_Elips2d::Mirror (const gp_Ax2d& A)
{ pos.Mirror(A); }

gp_Elips2d gp_Elips2d::Mirrored (const gp_Ax2d& A) const  
{
  gp_Elips2d E = *this;
  E.pos.Mirror (A);
  return E; 
}

