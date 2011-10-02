// File vgeom_Ax2.cxx , JCV 30/08/90
// JCV 1/10/90 Changement de nom du package vgeom -> gp
// JCV 12/12/90 Modif mineur suite a la premiere revue de projet
// LPA, JCV  07/92 passage sur C1.
// JCV 07/92 Introduction de la method Dump 

#define No_Standard_OutOfRange

#include <gp_Ax2.ixx>
#include <gp.hxx>

gp_Ax2::gp_Ax2 (const gp_Pnt& P, const gp_Dir& V) :
axis(P,V)
{
  Standard_Real A = V.X();
  Standard_Real B = V.Y();
  Standard_Real C = V.Z();
  Standard_Real Aabs = A;
  if (Aabs < 0) Aabs = - Aabs;
  Standard_Real Babs = B;
  if (Babs < 0) Babs = - Babs;
  Standard_Real Cabs = C;
  if (Cabs < 0) Cabs = - Cabs;
  gp_Dir D;
  
  //  pour determiner l axe X :
  //  on dit que le produit scalaire Vx.V = 0. 
  //  et on recherche le max(A,B,C) pour faire la division.
  //  l'une des coordonnees du vecteur est nulle. 
  
  if      ( Babs <= Aabs && Babs <= Cabs) {
    if (Aabs > Cabs) D.SetCoord(-C,0., A);
    else             D.SetCoord( C,0.,-A);
  }
  else if ( Aabs <= Babs && Aabs <= Cabs) {
    if (Babs > Cabs) D.SetCoord(0.,-C, B);
    else             D.SetCoord(0., C,-B);
  }
  else {
    if (Aabs > Babs) D.SetCoord(-B, A,0.);
    else             D.SetCoord( B,-A,0.);
  }
  SetXDirection(D);
}

void gp_Ax2::Mirror (const gp_Pnt& P)
{
  gp_Pnt Temp = axis.Location();
  Temp.Mirror (P);
  axis.SetLocation (Temp);
  vxdir.Reverse ();
  vydir.Reverse ();
}

gp_Ax2 gp_Ax2::Mirrored(const gp_Pnt& P) const
{
  gp_Ax2 Temp = *this;
  Temp.Mirror (P);
  return Temp;
}

void gp_Ax2::Mirror (const gp_Ax1& A1)
{
  vydir.Mirror (A1);
  vxdir.Mirror (A1);
  gp_Pnt Temp = axis.Location();
  Temp.Mirror (A1);
  axis.SetLocation (Temp);
  axis.SetDirection (vxdir.Crossed (vydir));
}

gp_Ax2 gp_Ax2::Mirrored(const gp_Ax1& A1) const
{
  gp_Ax2 Temp = *this;
  Temp.Mirror (A1);
  return Temp;
}

void gp_Ax2::Mirror (const gp_Ax2& A2)
{
  vydir.Mirror (A2);
  vxdir.Mirror (A2);
  gp_Pnt Temp = axis.Location();
  Temp.Mirror (A2);
  axis.SetLocation (Temp);
  axis.SetDirection (vxdir.Crossed (vydir));
}

gp_Ax2 gp_Ax2::Mirrored(const gp_Ax2& A2) const 
{
  gp_Ax2 Temp = *this;
  Temp.Mirror (A2);
  return Temp;
}

