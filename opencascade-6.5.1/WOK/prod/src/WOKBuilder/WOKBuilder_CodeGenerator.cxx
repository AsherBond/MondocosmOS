// File:	WOKBuilder_CodeGenerator.cxx
// Created:	Wed Aug 23 20:08:57 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_Shell.hxx>

#include <WOKBuilder_HSequenceOfExtension.hxx>
#include <WOKBuilder_HSequenceOfEntity.hxx>

#include <WOKBuilder_CodeGenerator.ixx>

//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKBuilder_CodeGenerator
//purpose  : 
//=======================================================================
WOKBuilder_CodeGenerator::WOKBuilder_CodeGenerator(const Handle(TCollection_HAsciiString)& aname, const WOKUtils_Param& params)
  : WOKBuilder_ToolInShell(aname, params)
{
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetCodeGenFile
//purpose  : 
//=======================================================================
void  WOKBuilder_CodeGenerator::SetCodeGenFile(const Handle(WOKBuilder_CodeGenFile)& afile)
{
  myfile = afile;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : CodeGenFile
//purpose  : 
//=======================================================================
Handle(WOKBuilder_CodeGenFile) WOKBuilder_CodeGenerator::CodeGenFile() const
{
  return myfile;
}


//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_CodeGenerator::Execute()
{
  Handle(TCollection_HAsciiString) astr;
  Handle(WOKBuilder_HSequenceOfEntity) aseq = new WOKBuilder_HSequenceOfEntity;

  if(Shell()->IsLaunched() == Standard_False) Shell()->Launch();

  Load();

  Params().Set("%Source",        CodeGenFile()->Path()->Name()->ToCString());
  Params().Set("%BaseName",      CodeGenFile()->Path()->BaseName()->ToCString());
  Params().Set("%OutputDir",     OutputDir()->Name()->ToCString());

  astr = Params().Eval(Template()->ToCString(), Standard_True);

  WOK_TRACE {
    VerboseMsg()("WOK_CODEGEN") << "WOKBuilder_Compiler::Execute" << "Compilation line : " << endm;
    VerboseMsg()("WOK_CODEGEN") << "WOKBuilder_Compiler::Execute" << astr << endm;
  }

  Shell()->Execute(astr);


  Handle(TColStd_HSequenceOfHAsciiString) resseq = Shell()->Errors();

  if(Shell()->Status())
    {
      Standard_Boolean ph = ErrorMsg().PrintHeader();

      ErrorMsg() << "WOKBuilder_Compiler::Execute" << "Errors occured in Shell" << endm;
      ErrorMsg().DontPrintHeader();
      for(Standard_Integer i=1; i<= resseq->Length(); i++)
	{
	  ErrorMsg() << "WOKBuilder_Compiler::Execute" << resseq->Value(i) << endm;
	}
      if(ph) ErrorMsg().DoPrintHeader();
      return WOKBuilder_Failed;
    }
  else
    {
      Standard_Boolean ph = InfoMsg().PrintHeader();
      InfoMsg().DontPrintHeader();
      for(Standard_Integer i=1; i<= resseq->Length(); i++)
	{
	  InfoMsg() << "WOKBuilder_Compiler::Execute" << resseq->Value(i) << endm;
	}
      if(ph) InfoMsg().DoPrintHeader();
    }
  Shell()->ClearOutput();

  SetProduction(EvalProduction());

  return WOKBuilder_Success;
}

