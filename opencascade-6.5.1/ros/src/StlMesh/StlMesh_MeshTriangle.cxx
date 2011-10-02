//=======================================================================
// File:	StlMesh_MeshTriangle.cxx
// Created:	Mon Sep 25 11:24:02 1995
// Author:	Philippe GIRODENGO
// Copyright:    Matra Datavision	

#include <StlMesh_MeshTriangle.ixx>
#include <Precision.hxx>
#include <gp_XYZ.hxx>

//=======================================================================
//function : StlMesh_MeshTriangle
//design   : 
//warning  : 
//=======================================================================

StlMesh_MeshTriangle::StlMesh_MeshTriangle()
     : MyV1 (0), MyV2 (0), MyV3 (0), MyXn (0.0), MyYn (0.0), MyZn (0.0) { }


//=======================================================================
//function : StlMesh_MeshTriangle
//design   : 
//warning  : 
//=======================================================================

     StlMesh_MeshTriangle::StlMesh_MeshTriangle(const Standard_Integer V1, 
						const Standard_Integer V2, 
						const Standard_Integer V3, 
						const Standard_Real Xn, 
						const Standard_Real Yn, 
						const Standard_Real Zn)
: MyV1 (V1), MyV2 (V2), MyV3 (V3), MyXn (Xn), MyYn (Yn), MyZn (Zn) { }


//=======================================================================
//function : GetVertexAndOrientation
//design   : 
//warning  : 
//=======================================================================

void StlMesh_MeshTriangle::GetVertexAndOrientation(Standard_Integer& V1, 
						   Standard_Integer& V2, 
						   Standard_Integer& V3, 
						   Standard_Real& Xn, 
						   Standard_Real& Yn, 
						   Standard_Real& Zn) const 
{
  V1 = MyV1;
  V2 = MyV2;
  V3 = MyV3;
  Xn = MyXn;
  Yn = MyYn;
  Zn = MyZn;
}

//=======================================================================
//function : SetVertexAndOrientation
//design   : 
//warning  : 
//=======================================================================

void StlMesh_MeshTriangle::SetVertexAndOrientation(const Standard_Integer V1, const Standard_Integer V2, 
						   const Standard_Integer V3, const Standard_Real Xn, 
						   const Standard_Real Yn, const Standard_Real Zn)
{
  MyV1 = V1;
  MyV2 = V2;
  MyV3 = V3;
  MyXn = Xn;
  MyYn = Yn;
  MyZn = Zn;
}

//=======================================================================
//function : GetVertex
//design   : 
//warning  : 
//=======================================================================

void StlMesh_MeshTriangle::GetVertex(Standard_Integer& V1, Standard_Integer& V2, Standard_Integer& V3) const 
{
  V1 = MyV1;
  V2 = MyV2;
  V3 = MyV3;
}

//=======================================================================
//function : SetVertex
//design   : 
//warning  : 
//=======================================================================

void StlMesh_MeshTriangle::SetVertex(const Standard_Integer V1, const Standard_Integer V2, const Standard_Integer V3)
{
  MyV1 = V1;
  MyV2 = V2;
  MyV3 = V3;
}



