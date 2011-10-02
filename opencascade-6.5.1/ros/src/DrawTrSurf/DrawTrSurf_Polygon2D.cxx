// File:	DrawTrSurf_Polygon2D.cxx
// Created:	Tue Mar 14 16:13:44 1995
// Author:	Laurent PAINNOT
//		<lpa@metrox>


#include <DrawTrSurf_Polygon2D.ixx>
#include <Poly.hxx>
#include <Draw_Color.hxx>
#include <Draw_MarkerShape.hxx>

//=======================================================================
//function : DrawTrSurf_Polygon2D
//purpose  : 
//=======================================================================

DrawTrSurf_Polygon2D::DrawTrSurf_Polygon2D(const Handle(Poly_Polygon2D)& P):
   myPolygon2D(P),
   myNodes(Standard_False)
{
}

//=======================================================================
//function : Polygon2D
//purpose  : 
//=======================================================================

Handle(Poly_Polygon2D) DrawTrSurf_Polygon2D::Polygon2D() const 
{
  return myPolygon2D;
}

//=======================================================================
//function : ShowNodes
//purpose  : 
//=======================================================================

void DrawTrSurf_Polygon2D::ShowNodes(const Standard_Boolean B)
{
  myNodes = B;
}

//=======================================================================
//function : ShowNodes
//purpose  : 
//=======================================================================

Standard_Boolean DrawTrSurf_Polygon2D::ShowNodes() const 
{
  return myNodes;
}

//=======================================================================
//function : DrawOn
//purpose  : 
//=======================================================================

void DrawTrSurf_Polygon2D::DrawOn(Draw_Display& dis) const 
{
  dis.SetColor(Draw_jaune);

  
  const TColgp_Array1OfPnt2d& Points = myPolygon2D->Nodes();

  for (Standard_Integer i = Points.Lower(); i <= Points.Upper()-1; i++) {
    dis.Draw(Points(i), Points(i+1));
  }
  

  if (myNodes) { 
    for (Standard_Integer i = Points.Lower(); i <= Points.Upper(); i++) {
      dis.DrawMarker(Points(i), Draw_X);
    }
  }

}

//=======================================================================
//function : Copy
//purpose  : 
//=======================================================================

Handle(Draw_Drawable3D) DrawTrSurf_Polygon2D::Copy() const 
{
  return new DrawTrSurf_Polygon2D(myPolygon2D);
}

//=======================================================================
//function : Dump
//purpose  : 
//=======================================================================

void DrawTrSurf_Polygon2D::Dump(Standard_OStream& S) const 
{
  Poly::Dump(myPolygon2D, S);
}

//=======================================================================
//function : Whatis
//purpose  : 
//=======================================================================

void DrawTrSurf_Polygon2D::Whatis(Draw_Interpretor& I) const 
{
  I << "polygon2D";
}

