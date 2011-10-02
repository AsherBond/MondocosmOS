// File:	BRepPrimAPI_MakeBox.cxx
// Created:	Fri Jul 23 15:51:42 1993
// Author:	Remi LEQUETTE
//		<rle@nonox>


#include <BRepPrimAPI_MakeBox.ixx>
#include <BRepBuilderAPI.hxx>
#include <TopoDS.hxx>
#include <Primitives_Direction.hxx>


inline gp_Pnt pmin(const gp_Pnt& p, 
		   const Standard_Real dx,
		   const Standard_Real dy, 
		   const Standard_Real dz)
{
  gp_Pnt P = p;
  if (dx < 0) P.SetX(P.X()+dx);
  if (dy < 0) P.SetY(P.Y()+dy);
  if (dz < 0) P.SetZ(P.Z()+dz);
  return P;
}

//=======================================================================
//function : BRepPrimAPI_MakeBox
//purpose  : 
//=======================================================================

BRepPrimAPI_MakeBox::BRepPrimAPI_MakeBox(const Standard_Real dx,
				 const Standard_Real dy,
				 const Standard_Real dz) :
       myWedge(gp_Ax2(pmin(gp_Pnt(0,0,0),dx,dy,dz),gp_Dir(0,0,1),gp_Dir(1,0,0)),
	       Abs(dx),Abs(dy),Abs(dz))
{
}


//=======================================================================
//function : BRepPrimAPI_MakeBox
//purpose  : 
//=======================================================================

BRepPrimAPI_MakeBox::BRepPrimAPI_MakeBox(const gp_Pnt& P, 
				 const Standard_Real dx,
				 const Standard_Real dy, 
				 const Standard_Real dz) :
       myWedge(gp_Ax2(pmin(P,dx,dy,dz),gp_Dir(0,0,1),gp_Dir(1,0,0)),
	       Abs(dx),Abs(dy),Abs(dz))
{
}


//=======================================================================
//function : BRepPrimAPI_MakeBox
//purpose  : 
//=======================================================================

inline gp_Pnt pmin(const gp_Pnt& p1, const gp_Pnt& p2)
{
  return gp_Pnt(Min(p1.X(),p2.X()),Min(p1.Y(),p2.Y()),Min(p1.Z(),p2.Z()));
}

BRepPrimAPI_MakeBox::BRepPrimAPI_MakeBox(const gp_Pnt& P1, const gp_Pnt& P2) :
       myWedge(gp_Ax2(pmin(P1,P2),gp_Dir(0,0,1),gp_Dir(1,0,0)),
	       Abs(P2.X()-P1.X()),
	       Abs(P2.Y()-P1.Y()),
	       Abs(P2.Z()-P1.Z()))
{
}


//=======================================================================
//function : BRepPrimAPI_MakeBox
//purpose  : 
//=======================================================================

BRepPrimAPI_MakeBox::BRepPrimAPI_MakeBox(const gp_Ax2& Axes, 
				 const Standard_Real dx, 
				 const Standard_Real dy, 
				 const Standard_Real dz) :
       myWedge(Axes,dx,dy,dz)
{
}


//=======================================================================
//function : Wedge
//purpose  : 
//=======================================================================

BRepPrim_Wedge&  BRepPrimAPI_MakeBox::Wedge()
{
  return myWedge;
}


//=======================================================================
//function : Shell
//purpose  : 
//=======================================================================

const TopoDS_Shell&  BRepPrimAPI_MakeBox::Shell()
{
  myShape = myWedge.Shell();
  Done();
  return TopoDS::Shell(myShape);
}

//=======================================================================
//function : Build
//purpose  : 
//=======================================================================

void BRepPrimAPI_MakeBox::Build()
{
  Solid();
}

//=======================================================================
//function : Solid
//purpose  : 
//=======================================================================

const TopoDS_Solid&  BRepPrimAPI_MakeBox::Solid()
{
  BRep_Builder B;
  B.MakeSolid(TopoDS::Solid(myShape));
  B.Add(myShape,myWedge.Shell());
  Done();
  return TopoDS::Solid(myShape);
}



//=======================================================================
//function : operator
//purpose  : 
//=======================================================================

BRepPrimAPI_MakeBox::operator TopoDS_Shell()
{
  return Shell();
}

//=======================================================================
//function : operator
//purpose  : 
//=======================================================================

BRepPrimAPI_MakeBox::operator TopoDS_Solid()
{
  return Solid();
}


//=======================================================================
//function : BottomFace
//purpose  : 
//=======================================================================

const TopoDS_Face& BRepPrimAPI_MakeBox::BottomFace () {

 return myWedge.Face (Primitives_ZMin);
}



//=======================================================================
//function : BackFace
//purpose  : 
//=======================================================================

const TopoDS_Face& BRepPrimAPI_MakeBox::BackFace () {

 return myWedge.Face (Primitives_XMin);
}


//=======================================================================
//function : FrontFace
//purpose  : 
//=======================================================================

const TopoDS_Face& BRepPrimAPI_MakeBox::FrontFace () {

 return myWedge.Face (Primitives_XMax);
}


//=======================================================================
//function : LeftFace
//purpose  : 
//=======================================================================

const TopoDS_Face& BRepPrimAPI_MakeBox::LeftFace () {

 return myWedge.Face (Primitives_YMin);
}


//=======================================================================
//function : RightFace
//purpose  : 
//=======================================================================

const TopoDS_Face& BRepPrimAPI_MakeBox::RightFace () {

 return myWedge.Face (Primitives_YMax);
}


//=======================================================================
//function : TopFace
//purpose  : 
//=======================================================================

const TopoDS_Face& BRepPrimAPI_MakeBox::TopFace () {

 return myWedge.Face (Primitives_ZMax);
}






