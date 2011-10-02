// File:	WOKBuilder_SharedLinker.cxx
// Created:	Tue Oct 24 13:34:12 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <EDL_API.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_HSequenceOfPath.hxx>
#include <WOKUtils_Shell.hxx>

#include <WOKBuilder_Entity.hxx>
#include <WOKBuilder_HSequenceOfEntity.hxx>
#include <WOKBuilder_SharedLibrary.hxx>

#include <WOKBuilder_SharedLinker.ixx>

//=======================================================================
//function : WOKBuilder_SharedLinker
//purpose  : 
//=======================================================================
 WOKBuilder_SharedLinker::WOKBuilder_SharedLinker(const Handle(TCollection_HAsciiString)& aname,
						  const WOKUtils_Param& params)
: WOKBuilder_Linker(aname, params)
{
}

//=======================================================================
//function : ExportList
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKBuilder_SharedLinker::ExportList() const 
{
  return myexports;
}

//=======================================================================
//function : SetExportList
//purpose  : 
//=======================================================================
void WOKBuilder_SharedLinker::SetExportList(const Handle(TColStd_HSequenceOfHAsciiString)& asymblist)
{
  myexports = asymblist;
}

//=======================================================================
//function : SetExportList
//purpose  : 
//=======================================================================
void WOKBuilder_SharedLinker::SetExportList(const Handle(WOKBuilder_ObjectFile)& )
{
  myexports = new TColStd_HSequenceOfHAsciiString;
}

//=======================================================================
//function : LogicalName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKBuilder_SharedLinker::LogicalName() const
{
  return mylogicname;
}

//=======================================================================
//function : SetLogicalName
//purpose  : 
//=======================================================================
void WOKBuilder_SharedLinker::SetLogicalName(const Handle(TCollection_HAsciiString)& aname)
{
  mylogicname = aname;
}

//=======================================================================
//function : EvalHeader
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKBuilder_SharedLinker::EvalHeader()
{
  Handle(TCollection_HAsciiString) line;
  Handle(TCollection_HAsciiString) templ;
  Handle(WOKBuilder_SharedLibrary) anent;

  if(!IsLoaded()) Load();
    
  templ = EvalToolParameter("Header");

  if(templ.IsNull()) {return templ;}

  // Target
  anent = new WOKBuilder_SharedLibrary(TargetName(), OutputDir(), WOKBuilder_FullPath);
  anent->GetPath(Params());

  Params().Set("%Target", anent->Path()->Name()->ToCString());

  // LogicalName
  if(LogicalName().IsNull())
    Params().Set("%LogicalName", anent->Path()->Name()->ToCString());
  else
    Params().Set("%LogicalName", LogicalName()->ToCString());

  // Apply Template
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
Handle(WOKBuilder_HSequenceOfEntity) WOKBuilder_SharedLinker::GetLinkerProduction()
{
  Handle(WOKBuilder_SharedLibrary) anent;
  Handle(WOKBuilder_HSequenceOfEntity) result = new WOKBuilder_HSequenceOfEntity;

  anent = new WOKBuilder_SharedLibrary(TargetName(), OutputDir(), WOKBuilder_FullPath);
  anent->GetPath(Params());

  result->Append(anent);

  return result;
}
