// File:	TopOpeBRepTool_face.cxx
// Created:	Thu Jan 14 10:20:08 1999
// Author:	Prestataire Xuan PHAM PHU
//		<xpu@poulopox.paris1.matra-dtv.fr>


#include <TopOpeBRepTool_face.ixx>
#include <TopOpeBRepTool_define.hxx>
#include <Standard_Failure.hxx>
#include <TopoDS.hxx>
#include <BRep_Tool.hxx>
#include <Precision.hxx>
#include <BRep_Builder.hxx>
#include <TopoDS_Iterator.hxx>
#include <BRepTopAdaptor_FClass2d.hxx>

//=======================================================================
//function : TopOpeBRepTool_face
//purpose  : 
//=======================================================================

TopOpeBRepTool_face::TopOpeBRepTool_face()
{
}

static void FUN_reverse(const TopoDS_Face& f, TopoDS_Face& frev)
{
  BRep_Builder B; 
  TopoDS_Shape aLocalShape = f.EmptyCopied();
  frev = TopoDS::Face(aLocalShape);
//  frev = TopoDS::Face(f.EmptyCopied());
  TopoDS_Iterator it(f);
  while (it.More()) {
    B.Add(frev,it.Value().Reversed());
    it.Next();
  }    
}

//=======================================================================
//function : Init
//purpose  : 
//=======================================================================

Standard_Boolean TopOpeBRepTool_face::Init(const TopoDS_Wire& W, const TopoDS_Face& Fref)
{
  myFfinite.Nullify();
  myW = W;

  // fres : 
//  TopoDS_Face fres;
//  Handle(Geom_Surface) su = BRep_Tool::Surface(Fref);  
//  BRep_Builder B; B.MakeFace(fres,su,Precision::Confusion());
  TopoDS_Shape aLocalShape = Fref.EmptyCopied();
  TopoDS_Face fres = TopoDS::Face(aLocalShape);
//  TopoDS_Face fres = TopoDS::Face(Fref.EmptyCopied());
  BRep_Builder B; B.Add(fres,W);
  B.NaturalRestriction(fres,Standard_True);

  // <myfinite> :
  BRepTopAdaptor_FClass2d FClass(fres,0.);
  Standard_Boolean infinite = ( FClass.PerformInfinitePoint() == TopAbs_IN);
  myfinite = !infinite;

  // <myFfinite> : 
  if (myfinite) myFfinite = fres;
  else          FUN_reverse(fres,myFfinite);
  return Standard_True;
}

//=======================================================================
//function : IsDone
//purpose  : 
//=======================================================================

Standard_Boolean TopOpeBRepTool_face::IsDone() const
{
  return (!myFfinite.IsNull());
}

//=======================================================================
//function : Finite
//purpose  : 
//=======================================================================

Standard_Boolean TopOpeBRepTool_face::Finite() const
{
  if (!IsDone()) Standard_Failure::Raise("TopOpeBRepTool_face NOT DONE");
  return myfinite;
}

//=======================================================================
//function : Ffinite
//purpose  : 
//=======================================================================

const TopoDS_Face& TopOpeBRepTool_face::Ffinite() const
{
  if (!IsDone()) Standard_Failure::Raise("TopOpeBRepTool_face NOT DONE");
  return myFfinite;
}

//=======================================================================
//function : W
//purpose  : 
//=======================================================================

const TopoDS_Wire& TopOpeBRepTool_face::W() const
{
  return myW;
}

//=======================================================================
//function : TopoDS_Face&
//purpose  : 
//=======================================================================

TopoDS_Face TopOpeBRepTool_face::RealF() const
{
  if (myfinite) return myFfinite;
  TopoDS_Face realf; FUN_reverse(myFfinite,realf);
  return realf;
}


