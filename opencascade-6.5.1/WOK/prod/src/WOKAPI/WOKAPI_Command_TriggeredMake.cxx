// File:	WOKAPI_Command_TriggeredMake.cxx
// Created:	Mon Nov 25 20:28:28 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#include <TCollection_HAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Options.hxx>
#include <WOKTools_Messages.hxx>

#include <WOKUtils_Path.hxx>

#include <WOKBuilder_Entity.hxx>

#include <WOKernel_Locator.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_File.hxx>

#include <WOKMake_TriggerStep.hxx>
#include <WOKMake_InputFile.hxx>
#include <WOKMake_OutputFile.hxx>

#include <WOKAPI_Command.jxx>

void WOKAPI_AddInputFile_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " <options> <InputFileID>" << endl;
  cerr << "    Options are :" << endl;
  cerr << "       -p <path> : the path for file" << endl;
  cerr << "       -L : input is locate able (default)" << endl;
  cerr << "       -N : !wok file (not locate able)" << endl;
  cerr << "       -P : physical file (on disk) (default)" << endl;
  cerr << "       -V : non physical file (i.e. MSEntity)" << endl;
  cerr << "       -S : <StepID>" << endl;
  
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AddInputFile
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::AddInputFile(const WOKAPI_Session& ,
					      const Standard_Integer argc, const WOKTools_ArgTable& argv, 
					      WOKTools_Return& )
{
  WOKTools_Options opts(argc,argv,"hp:LNFVS", WOKAPI_AddInputFile_Usage);
  
  Handle(TCollection_HAsciiString) apath;
  Standard_Boolean 
    locateable  = Standard_True,
    isphysical  = Standard_True,
    isstepid    = Standard_False;

  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'p':
	  apath = opts.OptionArgument();
	  break;
	case 'L':
	  locateable = Standard_True;
	  break;
	case 'N':
	  locateable = Standard_False;
	  break;
	case 'F':
	  isphysical = Standard_True;
	  break;
	case 'V':
	  isphysical = Standard_False;
	  break;
	case 'S':
	  isstepid   = Standard_True;
	  isphysical = Standard_False;
	  locateable = Standard_True;
	  break;
	default:
	  break;
	}
      opts.Next();
    }
  
  if(opts.Failed() == Standard_True) return 1;
  
  Handle(TCollection_HAsciiString) anid;
  
  switch(opts.Arguments()->Length())
    {
    case 1:
      anid = opts.Arguments()->Value(1);
      break;
    default:
      WOKAPI_AddInputFile_Usage(argv[0]);
      return 1;
    }

  Handle(WOKMake_TriggerStep) thestep = WOKMake_TriggerStep::CurrentTriggerStep();
  
  if(thestep.IsNull())
    {
      ErrorMsg() << argv[0] 
	       << "No Tiggered Step currently in run" << endm;
      ErrorMsg() << argv[0] 
	       << argv[0] << " can only be called during a umake process" << endm;
      return 1;
    }
  
  Handle(WOKernel_File) file;
  Handle(WOKernel_Locator) locator = thestep->Locator();

  if(isphysical && locateable) 
    {
      file = locator->Locate(anid);
      
      if(file.IsNull())
	{
	  ErrorMsg() << argv[0] 
		   << "Cannot locate file (locateable and physical) : " << anid 
		   << " while processing " << thestep->Unit()->UserPathName() << endm;
	  return 1;
	}
    }

  Handle(WOKUtils_Path) thepath;
  
  if(!apath.IsNull())
    {
      if(file.IsNull())
	{
	  thepath = new WOKUtils_Path(apath);
	}
      else
	{
	  WarningMsg() << argv[0]
		     << "Ingnoring given path for locateable physical file" << endm;
	}
    }
  else if(!file.IsNull())
    {
      thepath = file->Path();
    }

  Handle(WOKBuilder_Entity) anent;
  Handle(WOKMake_InputFile) infile = new WOKMake_InputFile(anid, file, anent, thepath);

  infile->SetDirectFlag(Standard_False);
  infile->SetLocateFlag(locateable);
  infile->SetPhysicFlag(isphysical);
  infile->SetStepID(isstepid);

  thestep->AddInputFile(infile);
  
  return 0;
}

//=======================================================================

void WOKAPI_InputFileInfo_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " <options> <InputFileID>" << endl;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : InputFileInfo
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::InputFileInfo(const WOKAPI_Session& ,
					       const Standard_Integer argc, const WOKTools_ArgTable& argv, 
					       WOKTools_Return& returns)
{
  WOKTools_Options opts(argc,argv,"Pph", WOKAPI_InputFileInfo_Usage);

  Standard_Boolean 
    getpath  = Standard_False,
    isphysic = Standard_False;
  
  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'P':
	  isphysic = Standard_True;
	  break;
	case 'p':
	  getpath  = Standard_True;
	  break;
	default:
	  break;
	}
      opts.Next();
    }
  
  if(opts.Failed() == Standard_True) return 1;

  Handle(TCollection_HAsciiString) anid;

  switch(opts.Arguments()->Length())
    {
    case 1:
      anid = opts.Arguments()->Value(1);
      break;
    default:
      WOKAPI_InputFileInfo_Usage(argv[0]);
      return 1;
    }


  Handle(WOKMake_TriggerStep) thestep = WOKMake_TriggerStep::CurrentTriggerStep();

  if(thestep.IsNull())
    {
      ErrorMsg() << argv[0] 
	       << "No Tiggered Step currently in run" << endm;
      ErrorMsg() << argv[0] 
	       << argv[0] << " can only be called during a umake process" << endm;
      return 1;
    }

  Handle(WOKMake_InputFile) infile = thestep->GetInputFile(anid);

  if(infile.IsNull())
    {
      ErrorMsg() << argv[0] 
	       << anid << " is not an input of step " << thestep->Code() << endm;
      return 1;
    }

  if(isphysic) 
    {
      if(infile->IsPhysic())
	returns.AddBooleanValue(Standard_True);
      else
	returns.AddBooleanValue(Standard_False);
      return 0;
    }

  if(getpath)
    {
      Handle(WOKUtils_Path) thepath = infile->BuilderEntity()->Path();
      if(!thepath.IsNull())
	{
	  returns.AddStringValue(thepath->Name());
	  return 0;
	}
      else
	{
	  ErrorMsg() << argv[0] 
		   << "Cannot obtain path for ID : " << anid << endm;
	  return 1;
	}
    }

  return 0;
}

//=======================================================================
void WOKAPI_AddOutputFile_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " <options> <OutputFileID>" << endl;
  cerr << "    Options are :" << endl;
  cerr << "       -p <path> : the path for file" << endl;
  cerr << "       -L : output is locate able (default)" << endl;
  cerr << "       -N : !wok file (not locate able)" << endl;
  cerr << "       -F : physical file (on disk) (default)" << endl;
  cerr << "       -V : non physical file (i.e. MSEntity)" << endl;
  cerr << "       -M : file is a member of unit (default)" << endl;
  cerr << "       -X : file is not a member of unit" << endl;
  cerr << "       -P : file is a production of step (default)" << endl;
  cerr << "       -R : step introduces a reference to file" << endl;
  cerr << "       -S : <StepID>" << endl;
  
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AddOutputFile
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::AddOutputFile(const WOKAPI_Session& ,
					       const Standard_Integer argc, const WOKTools_ArgTable& argv, 
					       WOKTools_Return& )
{
  WOKTools_Options opts(argc,argv,"hp:LNFVSMXPR", WOKAPI_AddOutputFile_Usage);
  
  Handle(TCollection_HAsciiString) apath;
  Standard_Boolean 
    locateable   = Standard_True,
    isphysical   = Standard_True,
    isproduction = Standard_True,
    ismember     = Standard_True,
    isstepid     = Standard_False;

  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'p':
	  apath        = opts.OptionArgument();
	  break;
	case 'L':
	  locateable   = Standard_True;
	  break;
	case 'N':
	  locateable   = Standard_False;
	  break;
	case 'F':
	  isphysical   = Standard_True;
	  break;
	case 'V':
	  isphysical   = Standard_False;
	  break;
	case 'M':
	  ismember     = Standard_True;
	  break;
	case 'X':
	  ismember     = Standard_False;
	  break;
	case 'P':
	  isproduction = Standard_True;
	  break;
	case 'R':
	  isproduction = Standard_False;
	  break;
	case 'S':
	  isstepid   = Standard_True;
	  isphysical = Standard_False;
	  locateable = Standard_True;
	  break;
	default:
	  break;
	}
      opts.Next();
    }
  
  if(opts.Failed() == Standard_True) return 1;
  
  Handle(TCollection_HAsciiString) anid;
  
  switch(opts.Arguments()->Length())
    {
    case 1:
      anid = opts.Arguments()->Value(1);
      break;
    default:
      WOKAPI_AddOutputFile_Usage(argv[0]);
      return 1;
    }

  Handle(WOKMake_TriggerStep) thestep = WOKMake_TriggerStep::CurrentTriggerStep();
  
  if(thestep.IsNull())
    {
      ErrorMsg() << argv[0] 
	       << "No Tiggered Step currently in run" << endm;
      ErrorMsg() << argv[0] 
	       << argv[0] << " can only be called during a umake process" << endm;
      return 1;
    }
  
  Handle(WOKernel_File) file;
  Handle(WOKernel_Locator) locator = thestep->Locator();

  if(isphysical && locateable) 
    {
      file = locator->Locate(anid);
      
      if(file.IsNull())
	{
	  ErrorMsg() << argv[0] 
		   << "Cannot locate file (locateable and physical) : " << anid 
		   << " while processing " << thestep->Unit()->UserPathName() << endm;
	  return 1;
	}
    }

  Handle(WOKUtils_Path) thepath;
  
  if(!apath.IsNull())
    {
      if(file.IsNull())
	{
	  thepath = new WOKUtils_Path(apath);
	}
      else
	{
	  WarningMsg() << argv[0]
		     << "Ingnoring given path for locateable physical file" << endm;
	}
    }
  else if(!file.IsNull())
    {
      thepath = file->Path();
    }

  Handle(WOKBuilder_Entity) anent;
  Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(anid, file, anent, thepath);


  outfile->SetLocateFlag(locateable);
  outfile->SetPhysicFlag(isphysical);
  outfile->SetStepID(isstepid);
  
  if(isproduction)
    outfile->SetProduction();
  else
    outfile->SetReference();

  if(ismember)
    outfile->SetMember();
  else
    outfile->SetExtern();

  thestep->AddOutputFile(outfile);
  
  return 0;
}

//=======================================================================

void WOKAPI_OutputFileInfo_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " <options> <OutputFileID>" << endl;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutputFileInfo
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::OutputFileInfo(const WOKAPI_Session& ,
					       const Standard_Integer argc, const WOKTools_ArgTable& argv, 
					       WOKTools_Return& returns)
{
  WOKTools_Options opts(argc,argv,"Pph", WOKAPI_OutputFileInfo_Usage);

  Standard_Boolean 
    getpath  = Standard_False,
    isphysic = Standard_False;
  
  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'P':
	  isphysic = Standard_True;
	  break;
	case 'p':
	  getpath  = Standard_True;
	  break;
	default:
	  break;
	}
      opts.Next();
    }
  
  if(opts.Failed() == Standard_True) return 1;

  Handle(TCollection_HAsciiString) anid;

  switch(opts.Arguments()->Length())
    {
    case 1:
      anid = opts.Arguments()->Value(1);
      break;
    default:
      WOKAPI_OutputFileInfo_Usage(argv[0]);
      return 1;
    }


  Handle(WOKMake_TriggerStep) thestep = WOKMake_TriggerStep::CurrentTriggerStep();

  if(thestep.IsNull())
    {
      ErrorMsg() << argv[0] 
	       << "No Tiggered Step currently in run" << endm;
      ErrorMsg() << argv[0] 
	       << argv[0] << " can only be called during a umake process" << endm;
      return 1;
    }

  Handle(WOKMake_OutputFile) infile = thestep->GetOutputFile(anid);

  if(infile.IsNull())
    {
      ErrorMsg() << argv[0] 
	       << anid << " is not an Output of step " << thestep->Code() << endm;
      return 1;
    }

  if(isphysic) 
    {
      if(infile->IsPhysic())
	returns.AddBooleanValue(Standard_True);
      else
	returns.AddBooleanValue(Standard_False);
      return 0;
    }

  if(getpath)
    {
      Handle(WOKUtils_Path) thepath = infile->BuilderEntity()->Path();
      if(!thepath.IsNull())
	{
	  returns.AddStringValue(thepath->Name());
	  return 0;
	}
      else
	{
	  ErrorMsg() << argv[0] 
		   << "Cannot obtain path for ID : " << anid << endm;
	  return 1;
	}
    }

  return 0;
}

//=======================================================================
void WOKAPI_AddExecDepItem_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " <options> <InputFileID> <OutputFileID>" << endl;
  cerr << "    Options are :" << endl;
  cerr << "       -d : Add as a direct dependency (default)" << endl;
  cerr << "       -i : Add as an indirect dependency" << endl;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AddExecDepItem
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::AddExecDepItem(const WOKAPI_Session& ,
					       const Standard_Integer argc, const WOKTools_ArgTable& argv, 
					       WOKTools_Return& )
{
  WOKTools_Options opts(argc,argv,"hid", WOKAPI_AddExecDepItem_Usage, "id");
  
  Handle(TCollection_HAsciiString) apath;
  Standard_Boolean isdirect = Standard_True;

  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'd':
	  isdirect = Standard_True;
	  break;
	case 'i':
	  isdirect = Standard_False;
	  break;
	}
      opts.Next();
    }
  
  if(opts.Failed() == Standard_True) return 1;
  
  Handle(TCollection_HAsciiString) inid;
  Handle(TCollection_HAsciiString) outid;
  
  switch(opts.Arguments()->Length())
    {
    case 2:
      inid = opts.Arguments()->Value(1);
      outid = opts.Arguments()->Value(2);
      break;
    default:
      WOKAPI_AddOutputFile_Usage(argv[0]);
      return 1;
    }

  Handle(WOKMake_TriggerStep) thestep = WOKMake_TriggerStep::CurrentTriggerStep();
  
  if(thestep.IsNull())
    {
      ErrorMsg() << argv[0] 
	       << "No Tiggered Step currently in run" << endm;
      ErrorMsg() << argv[0] 
	       << argv[0] << " can only be called during a umake process" << endm;
      return 1;
    }


  Handle(WOKMake_InputFile) infile = thestep->GetInputFile(inid);

  if(infile.IsNull())
    {
      ErrorMsg() << argv[0] 
	       << "Input file ID : " << inid << " is not an input of this step" << endm;
      return 1;
    }
  
  Handle(WOKMake_OutputFile) outfile = thestep->GetOutputFile(outid);

  if(outfile.IsNull())
    {
      ErrorMsg() << argv[0] 
	       << "Output file ID : " << outid << " is not an output of this step" << endm;
      return 1;
    }

  thestep->AddExecDepItem(infile, outfile, isdirect);

  return 0;
}
