// File:	DDataStd_NamedShapeCommands.cxx
// Created:	Wed Jul 30 16:47:00 1997
// Author:	Denis PASCAL
//		<dp@dingox.paris1.matra-dtv.fr>

#include <DDataStd.hxx>
#include <DDataStd_DrawPresentation.hxx>
#include <DDF.hxx>
#include <Draw_Interpretor.hxx>
#include <Draw_Appli.hxx>

#include <TDF_Data.hxx>
#include <TDF_Label.hxx>

#include <DBRep.hxx>
#include <TopAbs.hxx>
#include <TopoDS.hxx>


// LES ATTRIBUTES

#include <TNaming_NamedShape.hxx>
#include <TNaming_Builder.hxx>
#include <TNaming_Tool.hxx>


//=======================================================================
//function : DDataStd_SetShape
//purpose  : SetShape (DF, entry, drawshape)
//=======================================================================

static Standard_Integer DDataStd_SetShape (Draw_Interpretor& di,
					Standard_Integer nb, 
					const char** arg) 
{ 
  if (nb == 4) {    
    Handle(TDF_Data) DF;
    if (!DDF::GetDF(arg[1],DF)) return 1;  
    TopoDS_Shape s = DBRep::Get(arg[3]);  
    if (s.IsNull()) { di <<"shape not found"<< "\n"; return 1;}  
    TDF_Label L;
    DDF::AddLabel(DF, arg[2], L);
    TNaming_Builder SI (L);
    SI.Generated(s);
    return 0;
  }
  di << "DDataStd_SetShape : Error" << "\n";
  return 1;
}


//=======================================================================
//function : NamedShapeCommands
//purpose  : 
//=======================================================================

void DDataStd::NamedShapeCommands (Draw_Interpretor& theCommands)
{  

  static Standard_Boolean done = Standard_False;
  if (done) return;
  done = Standard_True;
  const char* g = "DData : Standard Attribute Commands";
  

  theCommands.Add ("SetShape", 
                   "SetShape (DF, entry, drawname)",
		   __FILE__, DDataStd_SetShape, g);

}
