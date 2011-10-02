// File:	Draft_VertexInfo.cxx
// Created:	Thu Sep  1 10:18:30 1994
// Author:	Jacques GOUSSARD
//		<jag@topsn2>


#include <Draft_VertexInfo.ixx>

#include <TColStd_ListIteratorOfListOfReal.hxx>
#include <TopoDS.hxx>

//=======================================================================
//function : Draft_VertexInfo
//purpose  : 
//=======================================================================

Draft_VertexInfo::Draft_VertexInfo () {}


//=======================================================================
//function : Add
//purpose  : 
//=======================================================================

void Draft_VertexInfo::Add(const TopoDS_Edge& E)
{
  for (myItEd.Initialize(myEdges); myItEd.More(); myItEd.Next()) {
    if (E.IsSame(myItEd.Value())) {
      break;
    }
  }
  if (!myItEd.More()) {
    myEdges.Append(E);
    myParams.Append(RealLast());
  }
}


//=======================================================================
//function : Geometry
//purpose  : 
//=======================================================================

const gp_Pnt& Draft_VertexInfo::Geometry () const
{
  return myGeom;
}


//=======================================================================
//function : ChangeGeometry
//purpose  : 
//=======================================================================

gp_Pnt& Draft_VertexInfo::ChangeGeometry ()
{
  return myGeom;
}


//=======================================================================
//function : Parameter
//purpose  : 
//=======================================================================

Standard_Real Draft_VertexInfo::Parameter (const TopoDS_Edge& E)
{
  TColStd_ListIteratorOfListOfReal itp(myParams);
  myItEd.Initialize(myEdges);
  for (; myItEd.More(); myItEd.Next(),itp.Next()) {
    if (myItEd.Value().IsSame(E)) {
      return itp.Value();
    }
  }
  Standard_DomainError::Raise(); return 0;
}


//=======================================================================
//function : ChangeParameter
//purpose  : 
//=======================================================================

Standard_Real& Draft_VertexInfo::ChangeParameter (const TopoDS_Edge& E)
{
  TColStd_ListIteratorOfListOfReal itp(myParams);
  myItEd.Initialize(myEdges);
  for (; myItEd.More(); myItEd.Next(),itp.Next()) {
    if (myItEd.Value().IsSame(E)) {
      return itp.Value();
    }
  }
  Standard_DomainError::Raise(); return itp.Value();
}


//=======================================================================
//function : InitEdgeIterator
//purpose  : 
//=======================================================================

void Draft_VertexInfo::InitEdgeIterator () 
{
  myItEd.Initialize(myEdges);
}


//=======================================================================
//function : Edge
//purpose  : 
//=======================================================================

const TopoDS_Edge& Draft_VertexInfo::Edge () const
{
  return TopoDS::Edge(myItEd.Value());
}


//=======================================================================
//function : MoreEdge
//purpose  : 
//=======================================================================

Standard_Boolean Draft_VertexInfo::MoreEdge() const
{
  return myItEd.More();
}


//=======================================================================
//function : NextEdge
//purpose  : 
//=======================================================================

void Draft_VertexInfo::NextEdge()
{
  myItEd.Next();
}


