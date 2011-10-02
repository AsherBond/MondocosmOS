// File:	TopOpeBRepBuild_Pave.cxx
// Created:	Mon Nov 14 10:00:39 1994
// Author:	Jean Yves LEBEY
//		<jyl@bravox>

#include <TopOpeBRepBuild_Pave.ixx>
#include <TopAbs.hxx>

//=======================================================================
//function : TopOpeBRepBuild_Pave
//purpose  : 
//=======================================================================

TopOpeBRepBuild_Pave::TopOpeBRepBuild_Pave
(const TopoDS_Shape& V, const Standard_Real P, const Standard_Boolean B) :
TopOpeBRepBuild_Loop(V),
myVertex(V),myParam(P),myIsShape(B),myHasSameDomain(Standard_False),myIntType(TopOpeBRepDS_FACE)
{
}

//=======================================================================
//function : HasSameDomain
//purpose  : 
//=======================================================================

void TopOpeBRepBuild_Pave::HasSameDomain(const Standard_Boolean B) 
{
  myHasSameDomain = B;
}

//=======================================================================
//function : SameDomain
//purpose  : 
//=======================================================================

void TopOpeBRepBuild_Pave::SameDomain(const TopoDS_Shape& VSD) 
{
  mySameDomain = VSD;
}

//=======================================================================
//function : HasSameDomain
//purpose  : 
//=======================================================================

Standard_Boolean TopOpeBRepBuild_Pave::HasSameDomain() const
{
  return myHasSameDomain;
}

//=======================================================================
//function : SameDomain
//purpose  : 
//=======================================================================

const TopoDS_Shape& TopOpeBRepBuild_Pave::SameDomain() const
{
  return mySameDomain;
}

//=======================================================================
//function : Vertex
//purpose  : 
//=======================================================================

const TopoDS_Shape& TopOpeBRepBuild_Pave::Vertex() const 
{
  return myVertex;
}

//=======================================================================
//function : ChangeVertex
//purpose  : 
//=======================================================================

TopoDS_Shape& TopOpeBRepBuild_Pave::ChangeVertex()
{
  return myVertex;
}

//=======================================================================
//function : Parameter
//purpose  : 
//=======================================================================

Standard_Real TopOpeBRepBuild_Pave::Parameter() const 
{
  return myParam;
}

//modified by NIZHNY-MZV  Mon Feb 21 14:11:40 2000
//=======================================================================
//function : Parameter
//purpose  : 
//=======================================================================
void TopOpeBRepBuild_Pave::Parameter(const Standard_Real Par)  
{
  myParam = Par;
}

//=======================================================================
//function : IsShape
//purpose  : 
//=======================================================================

Standard_Boolean TopOpeBRepBuild_Pave::IsShape() const 
{
  return myIsShape;
}

//=======================================================================
//function : Shape
//purpose  : 
//=======================================================================

const TopoDS_Shape& TopOpeBRepBuild_Pave::Shape() const 
{
  return myVertex;
}

//=======================================================================
//function : Dump
//purpose  : 
//=======================================================================

void TopOpeBRepBuild_Pave::Dump() const 
{
#ifdef DEB
  cout<<Parameter()<<" ";TopAbs::Print(Vertex().Orientation(),cout);
#endif
}

//modified by NIZHNY-MZV  Mon Feb 21 14:27:48 2000
//=======================================================================
//function : ChangeVertex
//purpose  : 
//=======================================================================

TopOpeBRepDS_Kind& TopOpeBRepBuild_Pave::InterferenceType()
{
  return myIntType;
}
