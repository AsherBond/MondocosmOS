// File:	DrawDim_PlanarRadius.cxx
// Created:	Fri Jan 12 17:59:57 1996
// Author:	Denis PASCAL
//		<dp@zerox>


#include <DrawDim_PlanarRadius.ixx>
#include <DrawDim.hxx>
#include <Geom_Curve.hxx>
#include <Geom_Circle.hxx>
#include <gp_Pnt.hxx>
#include <gp_Circ.hxx>
#include <BRep_Tool.hxx>
#include <TCollection_AsciiString.hxx>
#include <Draw_Color.hxx>
#include <ElCLib.hxx>
#include <TopExp.hxx>
#include <TopoDS.hxx>
#include <TopoDS_Vertex.hxx>

//=======================================================================
//function : DrawDim_PlanarRadius
//purpose  : 
//=======================================================================

DrawDim_PlanarRadius::DrawDim_PlanarRadius(const TopoDS_Face& face, const TopoDS_Shape& c)
{
  myPlane  = face;
  myCircle = c;
}


//=======================================================================
//function : DrawDim_PlanarRadius
//purpose  : 
//=======================================================================

DrawDim_PlanarRadius::DrawDim_PlanarRadius(const TopoDS_Shape& c)
{
  myCircle = c;
}


//=======================================================================
//function : DrawOn
//purpose  : 
//=======================================================================

void DrawDim_PlanarRadius::DrawOn(Draw_Display& dis) const
{  
  if (myCircle.ShapeType() == TopAbs_EDGE) {
    Standard_Real f,l;
    Handle(Geom_Curve) curve = BRep_Tool::Curve (TopoDS::Edge(myCircle),f,l);
    if (curve->IsKind(STANDARD_TYPE(Geom_Circle))) {
      gp_Circ circle = Handle(Geom_Circle)::DownCast(curve)->Circ();
      const gp_Pnt& first = circle.Location();
      TopoDS_Vertex vf, vl;
      TopExp::Vertices(TopoDS::Edge(myCircle),vf,vl);    
      const gp_Pnt last = BRep_Tool::Pnt(vf);
      //
      dis.Draw (first,last);
      gp_Pnt p ((first.X()+ last.X())/2,(first.Y()+ last.Y())/2,(first.Z()+ last.Z())/2);
      DrawText(p,dis);
      return;
    }
  }  
  cout << " DrawDim_PlanarRadius::DrawOn : dimension error" << endl;
}
