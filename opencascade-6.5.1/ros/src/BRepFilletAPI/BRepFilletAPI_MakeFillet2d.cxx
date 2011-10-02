// File:	BRepFilletAPI_MakeFillet2d.cxx
// Created:	Thu Aug 31 15:04:34 1995
// Author:	Remi LEQUETTE
//		<rle@mentox>


#include <BRepFilletAPI_MakeFillet2d.ixx>

//=======================================================================
//function : BRepFilletAPI_MakeFillet2d
//purpose  : 
//=======================================================================

BRepFilletAPI_MakeFillet2d::BRepFilletAPI_MakeFillet2d()
{
}

//=======================================================================
//function : BRepFilletAPI_MakeFillet2d
//purpose  : 
//=======================================================================

BRepFilletAPI_MakeFillet2d::BRepFilletAPI_MakeFillet2d(const TopoDS_Face& F)
{
  myMakeChFi2d.Init(F);
}

//=======================================================================
//function : Init
//purpose  : 
//=======================================================================

void BRepFilletAPI_MakeFillet2d::Init(const TopoDS_Face& F)
{
  myMakeChFi2d.Init(F);
}

//=======================================================================
//function : Init
//purpose  : 
//=======================================================================

void BRepFilletAPI_MakeFillet2d::Init(const TopoDS_Face& RefFace, const TopoDS_Face& ModFace)
{
  myMakeChFi2d.Init(RefFace, ModFace);
}

//=======================================================================
//function : AddFillet
//purpose  : 
//=======================================================================

TopoDS_Edge BRepFilletAPI_MakeFillet2d::AddFillet(const TopoDS_Vertex& V, const Standard_Real Radius)
{
  return myMakeChFi2d.AddFillet(V, Radius);
}

//=======================================================================
//function : ModifyFillet
//purpose  : 
//=======================================================================

TopoDS_Edge BRepFilletAPI_MakeFillet2d::ModifyFillet(const TopoDS_Edge& Fillet, const Standard_Real Radius)
{
  return myMakeChFi2d.ModifyFillet(Fillet, Radius);
}

//=======================================================================
//function : RemoveFillet
//purpose  : 
//=======================================================================

TopoDS_Vertex BRepFilletAPI_MakeFillet2d::RemoveFillet(const TopoDS_Edge& Fillet)
{
  return myMakeChFi2d.RemoveFillet(Fillet);
}

//=======================================================================
//function : AddChamfer
//purpose  : 
//=======================================================================

TopoDS_Edge BRepFilletAPI_MakeFillet2d::AddChamfer(const TopoDS_Edge& E1, const TopoDS_Edge& E2, const Standard_Real D1, const Standard_Real D2)
{
  return myMakeChFi2d.AddChamfer(E1, E2, D1, D2);
}

//=======================================================================
//function : AddChamfer
//purpose  : 
//=======================================================================

TopoDS_Edge BRepFilletAPI_MakeFillet2d::AddChamfer(const TopoDS_Edge& E, const TopoDS_Vertex& V, const Standard_Real D, const Standard_Real Ang)
{
  return myMakeChFi2d.AddChamfer(E, V, D, Ang);
}

//=======================================================================
//function : ModifyChamfer
//purpose  : 
//=======================================================================

TopoDS_Edge BRepFilletAPI_MakeFillet2d::ModifyChamfer(const TopoDS_Edge& Chamfer, const TopoDS_Edge& E1, const TopoDS_Edge& E2, const Standard_Real D1, const Standard_Real D2)
{
  return myMakeChFi2d.ModifyChamfer(Chamfer, E1, E2, D1, D2);
}

//=======================================================================
//function : ModifyChamfer
//purpose  : 
//=======================================================================

TopoDS_Edge BRepFilletAPI_MakeFillet2d::ModifyChamfer(const TopoDS_Edge& Chamfer, const TopoDS_Edge& E, const Standard_Real D, const Standard_Real Ang)
{
  return myMakeChFi2d.ModifyChamfer(Chamfer, E, D, Ang);
}

//=======================================================================
//function : RemoveChamfer
//purpose  : 
//=======================================================================

TopoDS_Vertex BRepFilletAPI_MakeFillet2d::RemoveChamfer(const TopoDS_Edge& Chamfer)
{
  return myMakeChFi2d.RemoveChamfer(Chamfer);
}

//=======================================================================
//function : BasisEdge
//purpose  : 
//=======================================================================

const TopoDS_Edge& BRepFilletAPI_MakeFillet2d::BasisEdge(const TopoDS_Edge& E) const 
{
  return myMakeChFi2d.BasisEdge(E);
}


//=======================================================================
//function : Build
//purpose  : 
//=======================================================================

void BRepFilletAPI_MakeFillet2d::Build()
{
  // test if the operation is done
  if (Status() == ChFi2d_IsDone) {
    Done();
    myShape = myMakeChFi2d.Result();
  }
  else
    NotDone();
}

//=======================================================================
//function : Modified
//purpose  : 
//=======================================================================

const TopTools_ListOfShape& BRepFilletAPI_MakeFillet2d::Modified(const TopoDS_Shape& E)
{
  myGenerated.Clear();
  myGenerated.Append(DescendantEdge(TopoDS::Edge(E)));
  return myGenerated;
}

//=======================================================================
//function : NbCurves
//purpose  : 
//=======================================================================

Standard_Integer BRepFilletAPI_MakeFillet2d::NbCurves() const 
{
  return NbFillet() + NbChamfer();
}

//=======================================================================
//function : NewEdges
//purpose  : 
//=======================================================================

const TopTools_ListOfShape& BRepFilletAPI_MakeFillet2d::NewEdges(const Standard_Integer I)
{
  myGenerated.Clear();
  if (I <= NbFillet())
    myGenerated.Append(FilletEdges()(I));
  else
    myGenerated.Append(ChamferEdges()(I-NbFillet()));

  return myGenerated;
}


