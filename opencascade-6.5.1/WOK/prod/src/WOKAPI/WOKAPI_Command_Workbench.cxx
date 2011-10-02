// File:	WOKAPI_Command_Workbench.cxx
// Created:	Wed Oct 23 12:01:45 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>

#include <TCollection_HAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_MapOfHAsciiString.hxx>
#include <WOKTools_Define.hxx>
#include <WOKTools_HSequenceOfDefine.hxx>
#include <WOKTools_Options.hxx>
#include <WOKTools_Messages.hxx>

#include <WOKUtils_HSequenceOfParamItem.hxx>
#include <WOKUtils_ParamItem.hxx>

#include <WOKAPI_Workbench.hxx>
#include <WOKAPI_SequenceOfWorkbench.hxx>
#include <WOKAPI_Unit.hxx>
#include <WOKAPI_SequenceOfUnit.hxx>
#include <WOKAPI_BuildProcess.hxx>
#include <WOKAPI_SequenceOfMakeStep.hxx>
#include <WOKAPI_MakeStep.hxx>
#include <WOKAPI_Process.hxx>

#include <WOKAPI_Command.jxx>

//=======================================================================
void WOKAPI_WorkbenchBuild_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " -f <father> [-P] [-d|-n] [-D] <name>\n";
  cerr << endl;
  cerr << "    Options are :\n";
  cerr << "       -f <father> : create workbench as son of <father>\n";
  cerr << "       -P : propose default parameters value" << endl;
  cerr << "       -d : use default values for parameters (this is the default)" << endl;
  cerr << "       -n : don't use default values for parameters" << endl;
  cerr << "       -Dparam=Value : override default value for parameter %<WorkshopName>_<param>" << endl;
  cerr << endl;
  return;
}

//=======================================================================
//function : WorkbenchCreate
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::WorkbenchCreate(const WOKAPI_Session& asession, 
						 const Standard_Integer argc, const WOKTools_ArgTable& argv, 
						 WOKTools_Return& returns)
{
  Standard_Integer                   i;
  WOKTools_Options                   opts(argc, argv, "D:hdPf:", WOKAPI_WorkbenchBuild_Usage, "dn");
  Handle(TCollection_HAsciiString)   name;
  Handle(TCollection_HAsciiString)   fathername;
  Standard_Boolean                   querydefault   = Standard_True;
  Standard_Boolean                   proposedefault = Standard_False;
  Handle(WOKUtils_HSequenceOfParamItem) aseq;
  
  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'd':
	  querydefault = Standard_True;
	  break;
	case 'n':
	  querydefault = Standard_False;
	  break;
	case 'f':
	  fathername = opts.OptionArgument();
	  break;
	case 'P':
	  querydefault   = Standard_True;
	  proposedefault = Standard_True;
	  break;
	default:
	  break;
	}
      opts.Next();
    }
  
  if(opts.Failed() == Standard_True) return 1;

  switch(opts.Arguments()->Length())
    {
    case 1:
      name     = opts.Arguments()->Value(1);
      break;
    default:
      WOKAPI_WorkbenchBuild_Usage(argv[0]);
      return 1;
    }

// imv & apv - March 21, 2002
    WOKAPI_Session * modsess = (WOKAPI_Session *) &asession;
    modsess->Close();
    modsess->Open();
// imv & apv - March 21, 2002

  WOKAPI_Workbench abench;


  if(proposedefault)
    {
      aseq = abench.BuildParameters(asession, name, fathername, opts.Defines(), querydefault);

      if (!aseq.IsNull())
	{
	  for(i =1 ; i <= aseq->Length(); i++)
	    {
	      returns.AddStringParameter(aseq->Value(i).Name(), aseq->Value(i).Value());
	    }
	}
    }
  else
    {
      if(!abench.Build(asession, name, fathername, opts.Defines(), querydefault))
	{

// imv & apv - March 21, 2002
    modsess->Close();
    modsess->Open();
// imv & apv - March 21, 2002

	  return 0;
	}
      else return 1;
    }

// imv & apv - March 21, 2002
    modsess->Close();
    modsess->Open();
// imv & apv - March 21, 2002

  return 0;
}
//=======================================================================
void WOKAPI_WorkbenchInfo_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " -<option> <aname>" << endl;
  cerr << endl;
  cerr << "    Options are :\n";
  cerr << "      -l            : list of units in WB\n";
  cerr << "      -a            : list of units in WB with their types\n";
  cerr << "      -k            : list of available toolkits from WB\n";
  cerr << "      -A            : list of ancestors of WB\n";
  cerr << "      -f            : father of WB\n";
  cerr << "      -C <unitname> : clients of unit in WB\n";
  cerr << "      -S <unitname> : suppliers of unit in WB\n";
  cerr << "      -S <execname:partname> : suppliers of executable in WB\n";
  cerr << "      -I <unitA,unitB,...>   : list of units in WB sorted by implementation dependences\n";
  cerr << "      -T <typename1,...,typenameN> : lists units of types listed\n";
  cerr << endl;
}

//=======================================================================
//function : WorkbenchInfo
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::WorkbenchInfo(const WOKAPI_Session& asession, 
					       const Standard_Integer argc, const WOKTools_ArgTable& argv, 
					       WOKTools_Return& returns)
{
  Standard_Integer i;
  Handle(TCollection_HAsciiString)  astr, name, unitname;
  Handle(TColStd_HSequenceOfHAsciiString) unitlist,sortedunitlist, typedunitlist;
  WOKTools_Options opts(argc, argv, "halkAfS:C:I:T:", WOKAPI_WorkbenchInfo_Usage, "halkAfSCI:");
  Standard_Boolean getuds        = Standard_False;
  Standard_Boolean gettypeduds   = Standard_False;
  Standard_Boolean typed         = Standard_False;
  Standard_Boolean getanc        = Standard_False;
  Standard_Boolean gettk         = Standard_False;
  Standard_Boolean getfather     = Standard_False;
  Standard_Boolean getsuppliers  = Standard_False;
  Standard_Boolean getclients    = Standard_False;
  Standard_Boolean getorder      = Standard_False;

  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'a':
	  getuds = Standard_True;
	  typed  = Standard_True;
	  break;
	case 'A':
	  getanc = Standard_True;
	  break;
	case 'l':
	  getuds = Standard_True;
	  break;
	case 'k':
	  gettk = Standard_True;
	  break;
	case 'f':
	  getfather = Standard_True;
	  break;
	case 'S':  
	  getsuppliers  = Standard_True;
	  unitname = opts.OptionArgument();
	  break;
	case 'C':
	  getclients  = Standard_True;
	  unitname = opts.OptionArgument();
	  break;
	case 'I':
	  getorder  = Standard_True;
	  unitlist = opts.OptionListArgument();
	  break;
	case 'T':
	  gettypeduds = Standard_True;
	  typedunitlist = opts.OptionListArgument();
	  break;
	default:
	  break;
	}
      opts.Next();
    }

  if(opts.Failed() == Standard_True) return 1;
  
  switch(opts.Arguments()->Length())
    {
    case 0:
      break;
    case 1:
      name = opts.Arguments()->Value(1);
      break;
    default:
      WOKAPI_WorkbenchInfo_Usage(argv[0]);
      return 1;
    }

  WOKAPI_Workbench abench(asession,name);


  if(!abench.IsValid())
    {
      ErrorMsg() << argv[0]
	       << "Could not determine workbench : Specify workbench in command line or use wokcd" << endm;
      return 1;
    }

  if(getuds)
    {
      WOKAPI_SequenceOfUnit unitseq;

      abench.Units(unitseq);

      for(i=1; i<= unitseq.Length() ; i++)
	  {
	    if(typed == Standard_True)
	      {
		astr = new TCollection_HAsciiString(unitseq.Value(i).Type());
		astr->AssignCat(" ");
		astr->AssignCat(unitseq.Value(i).Name());

		returns.AddStringValue(astr);
	      }
	    else
	      {
		returns.AddStringValue(unitseq.Value(i).Name());
	      }
	  }
	return 0;
    }
  
  if(gettypeduds)
    {
      WOKAPI_SequenceOfUnit unitseq;

      if(typedunitlist.IsNull()) return 1;

      for(Standard_Integer j=1; j<=typedunitlist->Length(); j++)
	abench.UnitsOfType(typedunitlist->Value(j), unitseq, Standard_False);

      for(i=1; i<= unitseq.Length() ; i++)
	{
	  returns.AddStringValue(unitseq.Value(i).Name());
	}
      return 0;
    }
  
  if(getanc)
    {
      WOKAPI_SequenceOfWorkbench benchseq;

      abench.Ancestors(benchseq);

      for(i=1; i<= benchseq.Length(); i++)
	{
	  returns.AddStringValue(benchseq.Value(i).Name());
	}
      return 0;
    }
  if(getfather)
    {
      WOKAPI_Workbench father = abench.Father();

      if(father.IsValid())
	{
	  returns.AddStringValue(father.Name());
	}
    }  
  if (gettk) 
    {
      WOKAPI_SequenceOfUnit tkseq;
      abench.Toolkits(tkseq);
      for(i=1; i<= tkseq.Length(); i++)
	{
	  returns.AddStringValue(tkseq.Value(i).Name());
	}
      return 0;
    }  
  if(getsuppliers)
    {  
      Standard_Integer i;
      WOKAPI_SequenceOfUnit unitseq;
      
      abench.ImplSuppliers(unitname,unitseq);
      
      for(i=1; i<=unitseq.Length(); i++)
	{
	  returns.AddStringValue(unitseq.Value(i).Name());
	}
      return 0;    
    }
  if(getclients)
    {  
      Standard_Integer i;
      WOKAPI_SequenceOfUnit unitseq;
      
      abench.ImplClients(unitname,unitseq);
      
      for(i=1; i<=unitseq.Length(); i++)
	{
	  returns.AddStringValue(unitseq.Value(i).Name());
	}
      return 0;    
    }
  if(getorder)
    {
      abench.SortUnitList(unitlist,sortedunitlist);
      if (!sortedunitlist.IsNull())
	{
	  for(i=1; i<=sortedunitlist->Length(); i++)
	    {
	      returns.AddStringValue(sortedunitlist->Value(i));
	    }
	}
      else return 1;
    }
 return 0;
}

//=======================================================================
void WOKAPI_WorkbenchMove_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " -f <new_father> <name>" << endl;
  cerr << endl;
  cerr << "    Options are : " << endl;
  cerr << "       -f : the new father of the workbench." << endl;
  cerr << endl;
}



//=======================================================================
//function : WorkbenchMove
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::WorkbenchMove(const WOKAPI_Session& asession, 
					       const Standard_Integer argc, const WOKTools_ArgTable& argv,
					       WOKTools_Return& )
{
  WOKTools_Options  opts(argc, argv, "hf:", WOKAPI_WorkbenchMove_Usage);
  Handle(TCollection_HAsciiString)   name,father;
  
  while(opts.More()) {
    switch(opts.Option()) {
    case 'f':
      father = opts.OptionArgument();
      break;
    default:
      break;
    }
    opts.Next();
  }
  
  if(opts.Failed() == Standard_True) return 1;

  switch(opts.Arguments()->Length())
    {
    case 1:
      name     = opts.Arguments()->Value(1);
      break;
    default:
      WOKAPI_WorkbenchMove_Usage(argv[0]);
      return 1;
    }

// imv & apv - March 20, 2002 (OCC47)
    WOKAPI_Session * modsess = (WOKAPI_Session *) &asession;
    modsess->Close();
    modsess->Open();
// imv & apv - March 20, 2002

  WOKAPI_Workbench abench(asession,name);
  
  if (!abench.IsValid()) {
    ErrorMsg() << "WOKAPI_Command::WorkbenchMove"
      << "Could not determine workbench : Specify workbench in command line or use wokcd" << endm;
    return 1;
  }

  WOKAPI_Workbench afather(asession,father);

  if (!abench.IsValid()) {
    ErrorMsg() << "WOKAPI_Command::WorkbenchMove"
      << "Unable to find father workbench : Try to specify the complete workbench path in command line" << endm;
    return 1;
  }

// imv & apv - March 20, 2002 (OCC47)
// return abench.ChangeFather(afather);
    Standard_Boolean result = abench.ChangeFather(afather);
    modsess->Close();
    modsess->Open();
    return result;
// imv & apv - March 20, 2002
}


//=======================================================================
void WOKAPI_WorkbenchDestroy_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " <WorkbenchName>\n";
  cerr << endl;
}



//=======================================================================
//function : WorkbenchDestroy
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::WorkbenchDestroy(const WOKAPI_Session& asession, 
						  const Standard_Integer argc, const WOKTools_ArgTable& argv,
						  WOKTools_Return& )
{
  WOKTools_Options  opts(argc, argv, "D:hdP", WOKAPI_WorkbenchDestroy_Usage);
  Handle(TCollection_HAsciiString)   name;
  
  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'R':
	  ErrorMsg() << "WOKAPI_Command::WorkbenchDestroy" << "-R not yet implemented" << endm;
	  return 1;
	default:
	  break;
	}
      opts.Next();
    }
  
  if(opts.Failed() == Standard_True) return 1;

  switch(opts.Arguments()->Length())
    {
    case 1:
      name     = opts.Arguments()->Value(1);
      break;
    default:
      WOKAPI_WorkbenchDestroy_Usage(argv[0]);
      return 1;
    }

// imv & apv - March 21, 2002
    WOKAPI_Session * modsess = (WOKAPI_Session *) &asession;
    modsess->Close();
    modsess->Open();
// imv & apv - March 21, 2002

  WOKAPI_Workbench abench(asession,name);
  
  if(!abench.IsValid())
    {
      ErrorMsg() << "WOKAPI_Command::WorkbenchDestroy"
	       << "Could not determine workbench : Specify workbench in command line or use wokcd" << endm;
      return 1;
    }

  abench.Destroy();

// imv & apv - March 21, 2002
    modsess->Close();
    modsess->Open();
// imv & apv - March 21, 2002

  return 0;
}


//=======================================================================
void WOKAPI_WorkbenchProcess_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " [<WorkbenchName>]\n";
  cerr << "    Options are : " << endl;
  cerr << "       -DGroups=Obj,Dep,Lib,Exec   : selects groups Obj, Lib, and Exec" << endl;
  cerr << "       -DUnits=MyUd1,MyUd2,..      : selects units MyUd1,MyUd2,.." << endl;
  cerr << "       -DXGroups=Src,Deliv         : Excludes groups Obj and Deliv" << endl;
  cerr << "       -DXUnits=MyUd1,MyUd2,..     : Excludes units MyUd1,MyUd2,.." << endl;
  cerr << "    Available groups are Src Xcpp SchXcpp Obj Dep Lib Exec Deliv" << endl;
  cerr << endl;
  cerr << "       -f    : forces all selected steps" << endl;
  cerr << "       -d|-o : switches debug|optimized mode" << endl;
  cerr << "       -B <profile> : selects extraction profile" << endl;
  cerr << endl;
  cerr << "       -P    : prints out selected steps" << endl;
  cerr << "       -S    : silent mode (does not print banner" << endl;
  cerr << "       -L    : logs output to MyUD_<step code>.Log in step administration directory" << endl;
  cerr << endl;
  cerr << "       -F <file> : " << cmd << " utilise alors un fichier de parametrage <file>" << endl;
  cerr << endl;
}


//=======================================================================
//function : WorkbenchProcess
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::WorkbenchProcess(const WOKAPI_Session& asession, 
						  const Standard_Integer argc, const WOKTools_ArgTable& argv,
						  WOKTools_Return& returns)
{
  
  WOKTools_Options  opts(argc, argv, "odB:PSLD:fhF:", WOKAPI_WorkbenchProcess_Usage);
  Handle(TCollection_HAsciiString)   name;
  Standard_Boolean logflag = Standard_False;
  Standard_Boolean silent  = Standard_False;
  //Standard_Boolean force   = Standard_False;
  Standard_Boolean propose = Standard_False;
  Standard_Boolean debug   = Standard_False;
  Standard_Boolean savedebug   = Standard_False;
  Standard_Boolean optim   = Standard_False;
  Standard_Boolean profile = Standard_False;
  Standard_Boolean useconfig = Standard_False;
  Handle(TCollection_HAsciiString)    cfgfile;
  Handle(TCollection_HAsciiString)   profilename, saveprofile;
  
  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'L':
	  logflag = Standard_True;
	  break;
	case 'S':
	  silent  = Standard_True;
	  break;
	case 'f':
	  //force   = Standard_True;
	  opts.Define(new TCollection_HAsciiString("Force"), new TCollection_HAsciiString("Yes"));
	  break;
	case 'o':
	  optim   = Standard_True;
	  break;
	case 'd':
	  debug   = Standard_True;
	  break;
	case 'B':
	  profile = Standard_True;
	  profilename = opts.OptionArgument();
	  break;
	case 'P':
	  propose = Standard_True;
	  break;
	case 'F':
	  useconfig = Standard_True;
	  cfgfile   = opts.OptionArgument();
	default:
	  break;
	}
      opts.Next();
    }
  
  if(opts.Failed() == Standard_True) return 1;

  switch(opts.Arguments()->Length())
    {
    case 0:
      break;
    case 1:
      name     = opts.Arguments()->Value(1);
      break;
    default:
      WOKAPI_WorkbenchProcess_Usage(argv[0]);
      return 1;
    }

  if(useconfig)
    {
      WOKAPI_Process aprocess(asession);

      aprocess.ExecuteFile(cfgfile);
      
      return 0;
    }
  else
    {
      if(debug && optim) 
	{
	  ErrorMsg() << "WOKAPI_Command::WorkbenchProcess"
	    << "Optimised and debug mode cannot be combined" << endm;
	  return 1;
	}
      
      if(debug||optim||profile)
	{
	  savedebug = asession.DebugMode();
	  saveprofile = asession.DBMSystem();
	}
      
      if(debug)
	{
	  WOKAPI_Session* sess = (WOKAPI_Session*)&asession;
	  sess->SetDebugMode(Standard_True);
	}
      if(optim)
	{
	  WOKAPI_Session* sess = (WOKAPI_Session*)&asession;
	  sess->SetDebugMode(Standard_False);
	}
      if(profile)
	{
	  WOKAPI_Session* sess = (WOKAPI_Session*)&asession;
	  sess->SetDBMSystem(profilename);
	}
      
      if(debug||optim||profile)
	{
	  WOKAPI_Session* sess = (WOKAPI_Session*)&asession;
	  sess->Close();
	  sess->Open();
	}
      
      WOKAPI_Workbench abench(asession,name);
      
      if(!abench.IsValid())
	{
	  ErrorMsg() << "WOKAPI_Command::WorkbenchProcess"
	    << "Could not determine workbench : Specify workbench in command line or use wokcd" << endm;
	  return 1;
	}
      
      
      WOKAPI_BuildProcess   aproc;
      
      aproc.Init(abench);
      //aproc.SetForceFlag(force);
      
      aproc.SelectOnDefines(opts.Defines());
      
      if(!aproc.SelectedStepsNumber())
	{
	  InfoMsg() << argv[0] << "No step to execute : check command line" << endm;
	}
      else
	{
	  if(!propose)
	    {
	      if(!silent) aproc.PrintBanner();
	      aproc.Execute(logflag);
	    }
	  else
	    {
	      WOKAPI_SequenceOfMakeStep steps;
	      
	      aproc.SelectedSteps(steps);
	      
	      for(Standard_Integer i=1; i<=steps.Length(); i++)
		{
		  const WOKAPI_MakeStep& step = steps.Value(i);
		  
		  returns.AddStringValue(step.UniqueName());
		}
	    }
	}
      
      if(debug||optim||profile)
	{
	  WOKAPI_Session* sess = (WOKAPI_Session*)&asession;
	  sess->SetDebugMode(savedebug);
	  sess->SetDBMSystem(saveprofile);
	  sess->Close();
	  sess->Open();
	}
    }
  return 0;
}



