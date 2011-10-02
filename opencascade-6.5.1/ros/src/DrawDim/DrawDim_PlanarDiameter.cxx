// File:	DrawDim_PlanarDiameter.cxx
// Created:	Wed Nov 25 11:37:21 1998
// Author:	Denis PASCAL
//		<dp@dingox.paris1.matra-dtv.fr>


#include <DrawDim_PlanarDiameter.ixx>

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
//function : DrawDim_PlanarDiameter
//purpose  : 
//=======================================================================

DrawDim_PlanarDiameter::DrawDim_PlanarDiameter(const TopoDS_Face& face, const TopoDS_Shape& c)
{
  myPlane  = face;
  myCircle = c;
}


//=======================================================================
//function : DrawDim_PlanarDiameter
//purpose  : 
//=======================================================================

DrawDim_PlanarDiameter::DrawDim_PlanarDiameter(const TopoDS_Shape& c)
{
  myCircle = c;
}


//=======================================================================
//function : DrawOn
//purpose  : 
//=======================================================================

void DrawDim_PlanarDiameter::DrawOn(Draw_Display& dis) const
{  
  if (myCircle.ShapeType() == TopAbs_EDGE) {
    Standard_Real f,l;
    Handle(Geom_Curve) curve = BRep_Tool::Curve (TopoDS::Edge(myCircle),f,l);
    if (curve->IsKind(STANDARD_TYPE(Geom_Circle))) {
      gp_Circ circle = Handle(Geom_Circle)::DownCast(curve)->Circ();
      TopoDS_Vertex vf, vl;
      TopExp::Vertices(TopoDS::Edge(myCircle),vf,vl);
      const gp_Pnt first = BRep_Tool::Pnt(vf);
      Standard_Real parfirst = ElCLib::Parameter(circle,first);
      Standard_Real parlast = (parfirst + PI);
      gp_Pnt last = ElCLib::Value(parlast,circle);
      //
      dis.Draw (first,last);
      gp_Pnt p ((first.X()+ last.X())/2,(first.Y()+ last.Y())/2,(first.Z()+ last.Z())/2);
      DrawText(p,dis);
      return;
    }
  }  
  cout << " DrawDim_PlanarDiameter::DrawOn : dimension error" << endl;
}
