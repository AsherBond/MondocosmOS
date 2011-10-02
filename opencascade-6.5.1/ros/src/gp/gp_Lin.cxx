// File gp_Lin.cxx, JCV O3/06/90
// JCV 30/08/90 Modif passage version C++ 2.0 sur Sun
// JCV 1/10/90 Changement de nom du package vgeom -> gp
// JCV 12/12/90 modifs suite a la premiere revue de projet

#include <gp_Lin.ixx>

Standard_Real gp_Lin::Distance (const gp_Lin& Other) const
{
  if (pos.IsParallel (Other.pos, gp::Resolution())) { 
    return Other.Distance(pos.Location());
  }
  else {
    gp_Dir dir(pos.Direction().Crossed(Other.pos.Direction()));
    Standard_Real D = gp_Vec (pos.Location(),Other.pos.Location())
      .Dot(gp_Vec(dir));
    if (D < 0) D = - D;
    return D;
  }
}

void gp_Lin::Mirror (const gp_Pnt& P)
{ pos.Mirror(P);  }

gp_Lin gp_Lin::Mirrored (const gp_Pnt& P)  const
{
  gp_Lin L = *this;    
  L.pos.Mirror (P);
  return L;
}

void gp_Lin::Mirror (const gp_Ax1& A1)
{ pos.Mirror(A1); }

gp_Lin gp_Lin::Mirrored (const gp_Ax1& A1) const
{
  gp_Lin L = *this;
  L.pos.Mirror (A1);
  return L;
}

void gp_Lin::Mirror (const gp_Ax2& A2)
{ pos.Mirror(A2); }

gp_Lin gp_Lin::Mirrored (const gp_Ax2& A2) const
{
  gp_Lin L = *this;
  L.pos.Mirror (A2);
  return L;
}

