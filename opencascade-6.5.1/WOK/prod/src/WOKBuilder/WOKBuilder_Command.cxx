// File:	WOKBuilder_Command.cxx
// Created:	Wed Nov 15 11:38:46 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKUtils_Shell.hxx>

#include <EDL_API.hxx>

#include <WOKBuilder_Command.ixx>

//=======================================================================
//function : WOKBuilder_Command
//purpose  : 
//=======================================================================
 WOKBuilder_Command::WOKBuilder_Command(const Handle(TCollection_HAsciiString)& aname, 
					const WOKUtils_Param& params) 
: WOKBuilder_ToolInShell(aname, params)
{
}

//=======================================================================
//function : Load
//purpose  : 
//=======================================================================
void WOKBuilder_Command::Load()
{
  
}

//=======================================================================
//function : Copy
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_Command::Copy(const Handle(WOKUtils_Path)& afrom, 
					   const Handle(WOKUtils_Path)& ato)
{
  Handle(TCollection_HAsciiString) astr, afile;

  if(!Shell()->IsLaunched()) Shell()->Launch();
  Load();

  SetTemplate(new TCollection_HAsciiString("COMMAND_Copy"));

  Params().Set("%Source", afrom->Name()->ToCString());
  Params().Set("%Dest",   ato->Name()->ToCString());

  return Execute();
}

//=======================================================================
//function : PreserveCopy
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_Command::PreserveCopy(const Handle(WOKUtils_Path)& afrom, 
					   const Handle(WOKUtils_Path)& ato)
{
  Handle(TCollection_HAsciiString) astr, afile;

  if(!Shell()->IsLaunched()) Shell()->Launch();
  Load();

  SetTemplate(new TCollection_HAsciiString("COMMAND_PreserveCopy"));

  Params().Set("%Source", afrom->Name()->ToCString());
  Params().Set("%Dest",   ato->Name()->ToCString());

  return Execute();
}

//=======================================================================
//function : Move
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_Command::Move(const Handle(WOKUtils_Path)& afrom, 
					   const Handle(WOKUtils_Path)& ato)
{
  Handle(TCollection_HAsciiString) astr;

  if(!Shell()->IsLaunched()) Shell()->Launch();
  Load();

  SetTemplate(new TCollection_HAsciiString("COMMAND_Move"));

  Params().Set("%Source", afrom->Name()->ToCString());
  Params().Set("%Dest",   ato->Name()->ToCString());

  return Execute();
}

//=======================================================================
//function : ReplaceIfChanged
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_Command::ReplaceIfChanged(const Handle(WOKUtils_Path)& afrom, 
						       const Handle(WOKUtils_Path)& ato)
{
  Handle(TCollection_HAsciiString) astr, afile;

  if(!Shell()->IsLaunched()) Shell()->Launch();
  Load();

  SetTemplate(new TCollection_HAsciiString("COMMAND_ReplIfCh"));

  Params().Set("%Source", afrom->Name()->ToCString());
  Params().Set("%Dest",   ato->Name()->ToCString());

  astr = Params().Eval(Template()->ToCString());

  Shell()->Execute(astr);

  switch(Shell()->Status())
    {
    case 0:
      Shell()->ClearOutput();
      return WOKBuilder_Unbuilt;
    case 1:
      Shell()->ClearOutput();
      return WOKBuilder_Success;
    case 2:
      ErrorMsg() << "WOKBuilder_Command::Execute" << "Errors occured in Shell" << endm;
      Handle(TColStd_HSequenceOfHAsciiString) aseq = Shell()->Errors();
      
      for(Standard_Integer i=1; i<= aseq->Length(); i++)
	{
	  ErrorMsg() << "WOKBuilder_Command::Execute" << aseq->Value(i) << endm;
	}
      Shell()->ClearOutput();
      return WOKBuilder_Failed;
    }
  return WOKBuilder_Failed;
}

//=======================================================================
//function : ReplaceIfChangedWith
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_Command::ReplaceIfChangedWith(const Handle(WOKUtils_Path)& afrom, 
							   const Handle(WOKUtils_Path)& abase,
							   const Handle(WOKUtils_Path)& ato)
{
  Handle(TCollection_HAsciiString) astr, afile;

  if(!Shell()->IsLaunched()) Shell()->Launch();
  Load();

  SetTemplate(new TCollection_HAsciiString("COMMAND_ReplIfChWith"));

  Params().Set("%Source", afrom->Name()->ToCString());
  Params().Set("%Base",   abase->Name()->ToCString());
  Params().Set("%Dest",   ato->Name()->ToCString());

  astr = Params().Eval(Template()->ToCString());

  Shell()->Execute(astr);

  switch(Shell()->Status())
    {
    case 0:
      Shell()->ClearOutput();
      return WOKBuilder_Unbuilt;
    case 1:
      Shell()->ClearOutput();
      return WOKBuilder_Success;
    case 2:
      ErrorMsg() << "WOKBuilder_Command::Execute" << "Errors occured in Shell" << endm;
      Handle(TColStd_HSequenceOfHAsciiString) aseq = Shell()->Errors();
      
      for(Standard_Integer i=1; i<= aseq->Length(); i++)
	{
	  ErrorMsg() << "WOKBuilder_Command::Execute" << aseq->Value(i) << endm;
	}
      Shell()->ClearOutput();
      return WOKBuilder_Failed;
    }
  return WOKBuilder_Failed;
}

//=======================================================================
//function : Compress
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_Command::Compress(const Handle(WOKUtils_Path)& afile)
{
  Handle(TCollection_HAsciiString) astr;

  if(!Shell()->IsLaunched()) Shell()->Launch();

  Load();

  SetTemplate(new TCollection_HAsciiString("COMMAND_Compress"));

  Params().Set("%File", afile->Name()->ToCString());

  return Execute(); 
}

//=======================================================================
//function : CompressTo
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_Command::CompressTo(const Handle(WOKUtils_Path)& afile, const Handle(WOKUtils_Path)& adest)
{
  Handle(TCollection_HAsciiString) astr;

  if(!Shell()->IsLaunched()) Shell()->Launch();

  Load();

  SetTemplate(new TCollection_HAsciiString("COMMAND_CompressTo"));

  Params().Set("%File", afile->Name()->ToCString());
  Params().Set("%Dest", adest->Name()->ToCString());

  return Execute(); 
}

//=======================================================================
//function : UnCompress
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_Command::UnCompress(const Handle(WOKUtils_Path)& afile)
{
  Handle(TCollection_HAsciiString) astr;

  if(!Shell()->IsLaunched()) Shell()->Launch();

  Load();

  SetTemplate(new TCollection_HAsciiString("COMMAND_UnCompress"));

  Params().Set("%File", afile->Name()->ToCString());

  return Execute(); 
}

//=======================================================================
//function : UnCompressTo
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_Command::UnCompressTo(const Handle(WOKUtils_Path)& afile, const Handle(WOKUtils_Path)& adest)
{
  Handle(TCollection_HAsciiString) astr;

  if(!Shell()->IsLaunched()) Shell()->Launch();

  Load();

  SetTemplate(new TCollection_HAsciiString("COMMAND_UnCompressTo"));

  Params().Set("%File", afile->Name()->ToCString());
  Params().Set("%Dest", adest->Name()->ToCString());

  return Execute(); 
}

//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_Command::Execute()
{
  Handle(TCollection_HAsciiString) astr;

  if(Shell()->IsLaunched() == Standard_False) Shell()->Launch();

  astr = Params().Eval(Template()->ToCString());

  Shell()->Execute(astr);

  if(Shell()->Status())
    {
      ErrorMsg() << "WOKBuilder_Command::Execute" << "Errors occured in Shell" << endm;
      Handle(TColStd_HSequenceOfHAsciiString) aseq = Shell()->Errors();
      
      for(Standard_Integer i=1; i<= aseq->Length(); i++)
	{
	  ErrorMsg() << "WOKBuilder_Command::Execute" << aseq->Value(i) << endm;
	}
      Shell()->ClearOutput();
      return WOKBuilder_Failed;
    }

  Shell()->ClearOutput();
  return WOKBuilder_Success;
}

//=======================================================================
//function : CopyAndChmod
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_Command::CopyAndChmod(const Handle(WOKUtils_Path)& afrom, 
					   const Handle(WOKUtils_Path)& ato)
{
  Handle(TCollection_HAsciiString) astr, afile;

  if(!Shell()->IsLaunched()) Shell()->Launch();
  Load();

  SetTemplate(new TCollection_HAsciiString("COMMAND_CopyAndChmod"));

  Params().Set("%Source", afrom->Name()->ToCString());
  Params().Set("%Dest",   ato->Name()->ToCString());

  return Execute();
}
