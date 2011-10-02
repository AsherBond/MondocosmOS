// File:	WOKBuilder_ExecutableLinker.cxx<2>
// Created:	Thu Feb  8 12:41:16 1996
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <EDL_API.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_HSequenceOfPath.hxx>

#include <WOKBuilder_Executable.hxx>

#include <WOKBuilder_ExecutableLinker.ixx>

//=======================================================================
//function : WOKBuilder_ExecutableLinker
//purpose  : 
//=======================================================================
WOKBuilder_ExecutableLinker::WOKBuilder_ExecutableLinker(const Handle(TCollection_HAsciiString)& aname, 
							 const WOKUtils_Param& params)
   : WOKBuilder_Linker(aname,params)
{
}

//=======================================================================
//function : EvalHeader
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKBuilder_ExecutableLinker::EvalHeader()
{
  
  Handle(TCollection_HAsciiString) line;
  Handle(TCollection_HAsciiString) templ;
  Handle(TCollection_HAsciiString) templname  = new TCollection_HAsciiString("Header");
  Handle(TCollection_HAsciiString) target;
  Handle(WOKUtils_Path) targetpath;

  if(!IsLoaded()) Load();
    
  templ = EvalToolParameter(templname);

  if(templ.IsNull())
    {
      ErrorMsg() << "WOKBuilder_ExecutableLinker::EvalHeader" << "Could not eval parameter : " << templname << endm;
      return line;
    }

  if( TargetName().IsNull())
    target = new TCollection_HAsciiString("a.out");
  else
    target = TargetName();

  targetpath = new WOKUtils_Path(OutputDir()->Name(), target);
  
  Params().Set("%Target", targetpath->Name()->ToCString());
  
  line = Params().Eval(templ->ToCString());

  if(line.IsNull()) return line;
  
  line->AssignCat(EvalLibSearchDirectives());
  line->AssignCat(EvalDatabaseDirectives());

  return line;
}

//=======================================================================
//function : GetLinkerProduction
//purpose  : 
//=======================================================================
Handle(WOKBuilder_HSequenceOfEntity) WOKBuilder_ExecutableLinker::GetLinkerProduction()
{
  Handle(WOKBuilder_Executable) anent;
  Handle(WOKBuilder_HSequenceOfEntity) result = new WOKBuilder_HSequenceOfEntity;
  Handle(TCollection_HAsciiString) target;
  Handle(WOKUtils_Path) targetpath;

  if( TargetName().IsNull())
    target = new TCollection_HAsciiString("a.out");
  else
    target = TargetName();

  targetpath = new WOKUtils_Path(OutputDir()->Name(), target);

  anent = new WOKBuilder_Executable(targetpath);

  result->Append(anent);

  return result;
}
