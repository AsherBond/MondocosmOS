// File:	WOKBuilder_IDLCompiler.cxx
// Created:	Wed Aug 23 20:09:00 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <WOKOrbix_IDLCompiler.ixx>


#include <WOKBuilder_HSequenceOfEntity.hxx>


#include <WOKUtils_Path.hxx>
#include <WOKUtils_Shell.hxx>
#include <WOKUtils_Param.hxx>
#include <WOKTools_Messages.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <EDL_API.hxx>

#include <stdio.h>

//=======================================================================
//function : WOKOrbix_IDLCompiler
//purpose  : 
//=======================================================================
WOKOrbix_IDLCompiler::WOKOrbix_IDLCompiler(const Handle(TCollection_HAsciiString)& aname, const WOKUtils_Param& params)
  : WOKBuilder_ToolInShell(aname, params)
{
}

//=======================================================================
//function : IncludeDirectories
//purpose  : 
//=======================================================================
Handle(WOKUtils_HSequenceOfPath) WOKOrbix_IDLCompiler::IncludeDirectories() const 
{
  return myincdirs;
}

//=======================================================================
//function : SetIncludeDirectories
//purpose  : 
//=======================================================================
void WOKOrbix_IDLCompiler::SetIncludeDirectories(const Handle(WOKUtils_HSequenceOfPath)& incdirs)
{
  Handle(TCollection_HAsciiString) afile;
  Handle(TCollection_HAsciiString) atempl;
  Handle(TCollection_HAsciiString) astr = new TCollection_HAsciiString;

  Load();

  myincdirs = incdirs;

  atempl = new TCollection_HAsciiString("ORBIX_IncDirective");

  for(Standard_Integer i=1; i<= IncludeDirectories()->Length(); i++)
    {
      Params().Set("%IncDirectory", myincdirs->Value(i)->Name()->ToCString());

      astr->AssignCat(Params().Eval("ORBIX_IncDirective"));
    }
  Params().Set("%IncDirectives", astr->ToCString());
  return;
}

//=======================================================================
//function : Compilable
//purpose  : 
//=======================================================================
Handle(WOKOrbix_IDLFile) WOKOrbix_IDLCompiler::IDLFile() const
{
  return mysource;
}

//=======================================================================
//function : SetCompilable
//purpose  : 
//=======================================================================
void WOKOrbix_IDLCompiler::SetIDLFile(const Handle(WOKOrbix_IDLFile)& afile)
{
  mysource = afile;
}

//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKOrbix_IDLCompiler::Execute()
{
  int start;

#ifdef WNT
  start = 2;
#else
  start = 1;
#endif  // WNT

  Handle(TCollection_HAsciiString) astr;
  Handle(WOKBuilder_HSequenceOfEntity) aseq = new WOKBuilder_HSequenceOfEntity;
  

  if(Shell()->IsLaunched() == Standard_False) Shell()->Launch();

  Load();

  
  Params().Set("%Source",    IDLFile()->Path()->Name()->ToCString());
  Params().Set("%BaseName",  IDLFile()->Path()->BaseName()->ToCString());
  Params().Set("%TmpFile",   tmpnam(NULL));
  Params().Set("%OutputDir", OutputDir()->Name()->ToCString());

  astr = EvalToolTemplate(Template()->ToCString());

  WOK_TRACE {
    VerboseMsg()("WOK_ORBIX") << "WOKOrbix_IDLCompiler::Execute" << "Compilation line : " << endm;
    VerboseMsg()("WOK_ORBIX") << "WOKOrbix_IDLCompiler::Execute" << astr << endm;
  }

  Shell()->ClearOutput();
  Shell()->Execute(astr);

  Handle(TColStd_HSequenceOfHAsciiString) resseq = Shell()->Errors();

  if(Shell()->Status())
    {
      Standard_Boolean ph = ErrorMsg().PrintHeader();

      ErrorMsg() << "WOKOrbix_IDLCompiler::Execute" << "Errors occured in Shell" << endm;
      ErrorMsg().DontPrintHeader();
      for(Standard_Integer i=start; i<= resseq->Length(); i++)
	{
	  ErrorMsg() << "WOKOrbix_IDLCompiler::Execute" << resseq->Value(i) << endm;
	}
      if(ph) ErrorMsg().DoPrintHeader();
      return WOKBuilder_Failed;
    }
  else
    {
      Standard_Boolean ph = InfoMsg().PrintHeader();
      InfoMsg().DontPrintHeader();
      for(Standard_Integer i=start; i<= resseq->Length(); i++)
	{
	  InfoMsg() << "WOKOrbix_IDLCompiler::Execute" << resseq->Value(i) << endm;
	}
      if(ph) InfoMsg().DoPrintHeader();
    }
  Shell()->ClearOutput();

  SetProduction(EvalProduction());
  return WOKBuilder_Success;
}

