// File:	TDataXtd_Point.cxx
// Created:	Mon Apr  6 18:27:28 2009
// Author:	Sergey ZARITCHNY
//		<szy@covox>


#include <TDataXtd_Point.ixx>
#include <TDataStd.hxx>
#include <TDataXtd.hxx>
#include <TNaming_NamedShape.hxx>
#include <TNaming_Tool.hxx>
#include <TNaming_Builder.hxx>
#include <BRep_Tool.hxx>
#include <TopoDS.hxx>
#include <TopoDS_Vertex.hxx>
#include <TopAbs.hxx>
#include <BRepBuilderAPI_MakeVertex.hxx>

#include <Geom_CartesianPoint.hxx>

#include <BRep_Tool.hxx>

#define OCC2932

//=======================================================================
//function : GetID
//purpose  : 
//=======================================================================

const Standard_GUID& TDataXtd_Point::GetID() 
{
  static Standard_GUID TDataXtd_PointID("2a96b60d-ec8b-11d0-bee7-080009dc3333");
  return TDataXtd_PointID;
}


//=======================================================================
//function : Set
//purpose  : 
//=======================================================================

Handle(TDataXtd_Point) TDataXtd_Point::Set (const TDF_Label& L)
{ 
  Handle(TDataXtd_Point) A; 
  if (!L.FindAttribute(TDataXtd_Point::GetID(),A)) {
    A = new TDataXtd_Point (); 
    L.AddAttribute(A);
  }
  return A;
}

//=======================================================================
//function : Set
//purpose  : 
//=======================================================================

Handle(TDataXtd_Point) TDataXtd_Point::Set (const TDF_Label& L, const gp_Pnt& P)
{ 
  Handle(TDataXtd_Point) A = Set (L);

#ifdef OCC2932
  Handle(TNaming_NamedShape) aNS;
  if(L.FindAttribute(TNaming_NamedShape::GetID(), aNS)) {
    if(!aNS->Get().IsNull())
       if(aNS->Get().ShapeType() == TopAbs_VERTEX) {
	 gp_Pnt anOldPnt = BRep_Tool::Pnt(TopoDS::Vertex(aNS->Get()));
	 if(anOldPnt.X() == P.X() &&
	    anOldPnt.Y() == P.Y() &&
	    anOldPnt.Z() == P.Z()
	    )
	   return A;
       }
  }
#endif

  TNaming_Builder B(L);
  B.Generated(BRepBuilderAPI_MakeVertex(P));
  return A;
}


//=======================================================================
//function : TDataXtd_Point
//purpose  : 
//=======================================================================

TDataXtd_Point::TDataXtd_Point () {}



//=======================================================================
//function : ID
//purpose  : 
//=======================================================================

const Standard_GUID& TDataXtd_Point::ID() const { return GetID ();}


//=======================================================================
//function : NewEmpty
//purpose  : 
//=======================================================================

Handle(TDF_Attribute) TDataXtd_Point::NewEmpty () const
{  
  return new TDataXtd_Point(); 
}


//=======================================================================
//function : Restore
//purpose  : 
//=======================================================================

void TDataXtd_Point::Restore(const Handle(TDF_Attribute)& With) { }


//=======================================================================
//function : Paste
//purpose  : 
//=======================================================================

void TDataXtd_Point::Paste (const Handle(TDF_Attribute)& Into, const Handle(TDF_RelocationTable)& RT) const { }


//=======================================================================
//function : Dump
//purpose  : 
//=======================================================================

Standard_OStream& TDataXtd_Point::Dump (Standard_OStream& anOS) const
{  
  anOS << "Point";
  return anOS;
}
