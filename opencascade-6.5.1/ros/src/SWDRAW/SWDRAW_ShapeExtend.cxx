// File:	SWDRAW_ShapeExtend.cxx
// Created:	Tue Mar  9 18:23:40 1999
// Author:	data exchange team
//		<det@kinox.nnov.matra-dtv.fr>


#include <SWDRAW_ShapeExtend.ixx>
#include <DBRep.hxx>
#include <SWDRAW.hxx>
#include <TopTools_HSequenceOfShape.hxx>
#include <ShapeExtend_Explorer.hxx>
#include <TopoDS_Shape.hxx>


static Standard_Integer sortcompound (Draw_Interpretor& di, Standard_Integer argc, const char** argv)
{
  Standard_CString arg1 = argv[1];
  if (argc < 2) {
    di<<"Give shape name. As is, just displays shape type"<<"\n";
    di<<" or add result_name type [mode]"<<"\n";
    di<<" Type : v (vertex)  e (edge)  w (wire)  f (face)  s (shell)  so (solid)"<<"\n";
    di<<" Mode : n (normal, by default)  e (explore)  c (compound)  x (explore+comp)"<<"\n";
    return 0;
  }
  TopoDS_Shape Shape = DBRep::Get(arg1);
  if (Shape.IsNull()) { di<<"Shape unknown : "<<arg1<<"\n"; return 1 /* Error */; }

  if (argc < 4) {
    ShapeExtend_Explorer sbx;
    for (int i = 0; i < 2; i ++) {
      TopAbs_ShapeEnum ts = sbx.ShapeType(Shape,i);
      //cout<<"   Type "<<(i ? " (via Compound) : " : "(True) : ");
      if (i == 0) {
	di<<"   Type "<<"(True) : ";
      } else {
	di<<"   Type "<<" (via Compound) : ";
      }
      switch (ts) {
	case TopAbs_SHAPE  : di<<"SHAPE (empty)"; break;
	case TopAbs_VERTEX : di<<"VERTEX"; break;
	case TopAbs_EDGE   : di<<"EDGE"; break;
	case TopAbs_WIRE   : di<<"WIRE"; break;
	case TopAbs_FACE   : di<<"FACE"; break;
	case TopAbs_SHELL  : di<<"SHELL"; break;
	case TopAbs_SOLID  : di<<"SOLID"; break;
	case TopAbs_COMPSOLID : di<<"COMPSOLID"; break;
	case TopAbs_COMPOUND  : di<<"COMPOUND"; if (i>0) di<<" (heterogeneous)"; break;
      }
    }
    di<<"\n";
    return 0;
  }

  TopAbs_ShapeEnum tse = TopAbs_COMPOUND;
  char tsc = argv[3][0];
  switch (tsc) {
    case 'v' : tse = TopAbs_VERTEX; break;
    case 'e' : tse = TopAbs_EDGE;   break;
    case 'w' : tse = TopAbs_WIRE;   break;
    case 'f' : tse = TopAbs_FACE;   break;
    case 's' : tse = TopAbs_SHELL;
      if (argv[3][1] == 'o')  tse = TopAbs_SOLID; break;
    default : break;
  }
  Standard_Boolean modexp = Standard_False;
  Standard_Boolean modcom = Standard_False;
  if (argc > 4) {
    switch (argv[4][0]) {
      case 'e' : modexp = Standard_True;  break;
      case 'c' : modcom = Standard_True;  break;
      case 'x' : modexp = modcom = Standard_True;  break;
      default  : break;
    }
    ShapeExtend_Explorer sbx;
    Shape = sbx.SortedCompound (Shape,tse,modexp,modcom);
    if ( Shape.IsNull()) { di<<"Empty result"<<"\n"; return 0; }
    DBRep::Set(argv[2],Shape);
  }
  return 0; // Done
}

//=======================================================================
//function : InitCommands
//purpose  : 
//=======================================================================

 void SWDRAW_ShapeExtend::InitCommands(Draw_Interpretor& theCommands) 
{
  static Standard_Integer initactor = 0;
  if (initactor) return;  initactor = 1;
  
  Standard_CString g = SWDRAW::GroupName();
  theCommands.Add ("sortcompound","shape_entree shape_result type=v-e-w-f-s-so [mode=n-e-c-x]",
		   __FILE__,sortcompound,g);
}
