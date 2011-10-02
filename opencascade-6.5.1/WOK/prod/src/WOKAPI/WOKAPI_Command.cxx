// File:	WOKAPI_Command.cxx
// Created:	Wed Apr  3 18:02:42 1996
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <Standard_Stream.hxx>

#include <TCollection_AsciiString.hxx>
#include <TCollection_HAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <TColStd_HSequenceOfAsciiString.hxx>

#include <OSD_Environment.hxx>

#include <WOKTools_Options.hxx>
#include <WOKTools_Messages.hxx>
#include <WOKTools_MapOfHAsciiString.hxx>
#include <WOKTools_EnvValue.hxx>
#include <WOKTools_InterpFileValue.hxx>
#include <WOKTools_ChDirValue.hxx>

#include <WOKUtils_HSequenceOfParamItem.hxx>
#include <WOKUtils_ParamItem.hxx>
#include <WOKUtils_Path.hxx>
#include <WOKUtils_Trigger.hxx>
#include <WOKUtils_Triggers.hxx>

#include <WOKAPI_Session.hxx>
#include <WOKAPI_SequenceOfFactory.hxx>
#include <WOKAPI_Factory.hxx>
#include <WOKAPI_Warehouse.hxx>
#include <WOKAPI_Parcel.hxx>
#include <WOKAPI_SequenceOfParcel.hxx>
#include <WOKAPI_Workshop.hxx>
#include <WOKAPI_Workbench.hxx>
#include <WOKAPI_SequenceOfWorkbench.hxx>
#include <WOKAPI_Unit.hxx>
#include <WOKAPI_File.hxx>
#include <WOKAPI_Locator.hxx>

#include <WOKAPI_Command.ixx>

//=======================================================================
void WOKAPI_SessionInfo_Usage(char *cmd)
{
  cerr << "usage : " << cmd << "-<option>\n";
  cerr << endl;
  cerr << "    Options are :\n";
  cerr << "       -F : Factory list\n";
  cerr << "       -f : current factory\n";
  cerr << "       -s : current workshop\n";
  cerr << "       -w : current workbench\n";
  cerr << "       -u : current development unit\n";
  cerr << endl;
  return;
}

//=======================================================================
//function : SessionInfo
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::SessionInfo(const WOKAPI_Session& asession, 
					     const Standard_Integer argc, const WOKTools_ArgTable& argv, 
					     WOKTools_Return& returns)
{
  Handle(TCollection_HAsciiString)  astr;
  WOKTools_Options opts(argc, argv, "hFfswu", WOKAPI_SessionInfo_Usage, "hFfswu");
  Standard_Boolean getfactories = Standard_False;
  Standard_Boolean getfactory   = Standard_False;
  Standard_Boolean getshop      = Standard_False;
  Standard_Boolean getbench     = Standard_False;
  Standard_Boolean getunit      = Standard_False;
  Handle(TCollection_HAsciiString) ahpath;
  
  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'F':
	  getfactories = Standard_True;
	  break;
	case 'f':
	  getfactory   = Standard_True;
	  break;
	case 's':
	  getshop      = Standard_True;
	  break;
	case 'w':
	  getbench     = Standard_True;
	  break;
	case 'u':
	  getunit      = Standard_True;
	  break;
	}
      opts.Next();
    }

  if(opts.Failed() == Standard_True) return 1;

  switch(opts.Arguments()->Length())
    {
    case 0:
      break;
    default:
      WOKAPI_SessionInfo_Usage(argv[0]);
      return 1;
    }

  if(!asession.IsValid())
    {
      return 1;
    }

  Handle(TCollection_HAsciiString) nullhandle;

  if(getfactories)
    {
      Standard_Integer i;
      WOKAPI_SequenceOfFactory theseq;
      
      asession.Factories(theseq);

      for(i = 1 ; i <= theseq.Length() ; i++)
	{
	  returns.AddStringValue(theseq.Value(i).Name());
	}
      return 0;
    }
  if(getfactory)
    {
      WOKAPI_Factory afact(asession,nullhandle);
      if(afact.IsValid())
	returns.AddStringValue(afact.Name());
      return 0;
    }
  if(getshop)
    {
      WOKAPI_Workshop ashop(asession,nullhandle);
      if(ashop.IsValid())
	returns.AddStringValue(ashop.Name());
      return 0;
    }
  if(getbench)
    {
      WOKAPI_Workbench abench(asession,nullhandle);
      if(abench.IsValid())
	returns.AddStringValue(abench.Name());
      return 0;
    }
  if(getunit)
    {
      WOKAPI_Unit aunit(asession,nullhandle);
      if(aunit.IsValid()) 
	returns.AddStringValue(aunit.Name());
      return 0;
    }
  return 0;
}
//=======================================================================
void WOKAPI_MoveTo_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " [-P<param>] [-T t<ype>]  [<apath>] " << endl;
  cerr << "    Options are :" << endl;
  cerr << "       -P : Move to directory pointed by %Entity_<param> parameter" << endl;
  cerr << "       -T : Move to directory pointed by <type> file type" << endl;
  cerr << endl;
  return;
}

//=======================================================================
//function : MoveTo
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::MoveTo(const WOKAPI_Session& asession,
					const Standard_Integer argc, const WOKTools_ArgTable& argv, 
					WOKTools_Return& returns)
{
  WOKTools_Options opts(argc,argv,"P:T:h", WOKAPI_MoveTo_Usage, "hPT");
  Standard_Boolean gotoparam = Standard_False, gototype = Standard_False;
  Standard_Boolean pwd       = Standard_False;
  Standard_Boolean move      = Standard_False;
  Handle(TCollection_HAsciiString) homedir;
  Handle(TCollection_HAsciiString) GOTO, apath;
  Handle(TCollection_HAsciiString) param, type;

  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'P':
	  param     = opts.OptionArgument();
	  gotoparam = Standard_True;
	  break;
	case 'T':
	  type      = opts.OptionArgument();
	  gototype  = Standard_True;
	  break;
	default:
	  break;
	}
      
      opts.Next();
    }
  
  if(opts.Failed() == Standard_True) 
      return 1;

  switch(opts.Arguments()->Length())
    {
    case 0:
      pwd  = Standard_True;
      break;
    case 1:
      move  = Standard_True;
      apath =  opts.Arguments()->Value(1);
      break;
    default:
      WOKAPI_MoveTo_Usage(argv[0]);
      return 1;
    }

  WOKAPI_Entity anentity;

  if(!asession.CWEntityName().IsNull())
    {
      if(!strcmp(asession.CWEntityName()->ToCString(), ":"))
	anentity = asession;
      else
	anentity = asession.GetCWEntity();
    }

  if(move)
    {
      if(strcmp(apath->ToCString(), ":"))
	{
	  anentity = WOKAPI_Entity(asession,apath,Standard_False);
	  
	  if(!anentity.IsValid()) 
	    {
	      ErrorMsg() << argv[0]
		       << "Could not move to entity  : " << apath << endm;
	      return 1;
	    }
	      
	  WOKAPI_Session* thesess = (WOKAPI_Session*)&asession;
	  thesess->SetCWEntity(anentity);
	}
      else
	{
	  anentity = asession;
	  WOKAPI_Session* thesess = (WOKAPI_Session*)&asession;
	  thesess->SetCWEntity(anentity);

	  move = Standard_False;

	}
      pwd = Standard_True;
    }

  if(anentity.IsValid())
    {
      if(gotoparam == Standard_True)
	{
	  homedir = anentity.ParameterEval(anentity.EntityParameterName(param));
	}
      else
	{
	  if(gototype)
	    {
	      if(!anentity.IsSession())
		{
		  homedir = anentity.GetFileTypeDirectory(type);
		}
	      else
		{
		  // Ignore movement!
		  homedir.Nullify();
		}
	    }
	  else
	    {
	      if(move)
		homedir = anentity.ParameterEval(anentity.EntityParameterName(new TCollection_HAsciiString("Home")));
	    }
	}
      if(!homedir.IsNull())
	returns.AddChDir(homedir);
    }

  if(pwd)
    {
      anentity = asession.GetCWEntity();

      if(anentity.IsValid())
	{
	  if(anentity.IsSession())
	    {
	      returns.AddStringValue(":");
	    }
	  else
	    {
	      returns.AddStringValue(anentity.UserPath());
	    }
	}
    }
  return 0;
}

//=======================================================================
void WOKAPI_EnvironmentMgr_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " -s -t [csh|emacs|tcl] -f <filename> [entity]" << endl;
  cerr << endl;
  cerr << "    Options are :\n";
  cerr << "        -s : sets run environment for <entity>" << endl;
  cerr << "        -t : target interpretor format for env settings" << endl;
  cerr << "        -f : filename for interpretor script file for env settings" << endl;
  cerr << endl;
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : EnvironmentMgr
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::EnvironmentMgr(const WOKAPI_Session& asession, 
						const Standard_Integer argc, const WOKTools_ArgTable& argv, 
						WOKTools_Return& returns)
{
  WOKTools_Options opts(argc,argv,"hsf:t:", WOKAPI_EnvironmentMgr_Usage, "hsf");
  Handle(TCollection_HAsciiString) aname;
  Handle(TCollection_HAsciiString) filename;
  Handle(TCollection_HAsciiString) format;
  Standard_Boolean set = Standard_False, file = Standard_False, target = Standard_False;

  while(opts.More())
    {
      switch(opts.Option())
	{
	case 's':
	  set = Standard_True; 
	  break;
	case 'f':
	  file = Standard_True;
	  filename = opts.OptionArgument();
	  break;
	case 't':
	  target = Standard_True;
	  format = opts.OptionArgument();
	  break;
	}
      opts.Next();
    }  

  if(opts.Failed()) return 1;


  switch(opts.Arguments()->Length())
    {
    case 0:
      break;
    case 1:
      aname = opts.Arguments()->Value(1);
      break;
    default:
      WOKAPI_EnvironmentMgr_Usage(argv[0]);
      return 1;
    }

  WOKAPI_Entity theent(asession,aname);

  if(!theent.IsValid()) 
    {
      ErrorMsg() << argv[0] << "Could not determine entity to operate on." << endm;
      return 1;
    }
  
  if(set)
    {
      return theent.GetEnvActions(asession, returns);
    }
  else
    {
      if(file || target) 
	{
	  if(filename.IsNull())
	    {
	      ErrorMsg() << argv[0] << "Missing file name for test environnement settings" << endm;
	      return 1;
	    }
	  if(format.IsNull())
	    {
	      ErrorMsg() << argv[0] << "Missing format for test environnement settings" << endm;
	      return 1;
	    }
	  
	  WOKTools_Return actions;

	  if(theent.GetEnvActions(asession, actions))
	    {
	      ErrorMsg() << argv[0] 
		       << "Could not obtain informations for test environnement" << endm;
	      return 1;
	    }

	  


	  {
	    ofstream stream(filename->ToCString());
	    
	    if(!stream.good())
	      {
		ErrorMsg() << argv[0] 
			 << "Could not open " << filename << " for writing" << endm;
		return 1;
	      }
	    
	    WOKTools_InterpFileType          efmt = WOKTools_InterpFileValue::InterpType(format);
	    Handle(TCollection_HAsciiString) sfmt = WOKTools_InterpFileValue::InterpFormat(efmt);
	      
	    
	    for(Standard_Integer i=1; i<=actions.Length(); i++)
	      {
		WOKUtils_Trigger trigger;
		Handle(TCollection_HAsciiString) cmd;
		Handle(WOKTools_ReturnValue) act = actions.Value(i);
		Handle(TCollection_HAsciiString) fmt = 
		  WOKTools_InterpFileValue::InterpFormat(WOKTools_InterpFileValue::InterpType(format));

		switch(act->Type())
		  {
		  case WOKTools_Environment:
		    {
		      Handle(WOKTools_EnvValue) envval = Handle(WOKTools_EnvValue)::DownCast(act);
		      
		      if(!envval.IsNull())
			{
			  trigger("wok_setenv_cmd") << fmt << envval->Name()  << envval->Value() << endt >> cmd;
			}
		    }
		    break;
		  case WOKTools_ChDir:
		    {
		      Handle(WOKTools_ChDirValue) dirval = Handle(WOKTools_ChDirValue)::DownCast(act);
		      
		      if(!dirval.IsNull())
			{
			  trigger("wok_cd_cmd") << fmt  << dirval->Path() << endt >> cmd;
			}
		    }
		    break;
		  case WOKTools_InterpFile:
		    {
		      Handle(WOKTools_InterpFileValue) interpval = 
			Handle(WOKTools_InterpFileValue)::DownCast(act);

		      if(!interpval.IsNull())
			{
			  if(efmt == interpval->InterpType())
			    {
			      trigger("wok_source_cmd") << fmt  << interpval->File() << endt >> cmd;
			    }
			}
		    }
		    break;
		  default:
		    break;
		  }

		if(!cmd.IsNull())
		  {
		    stream << cmd->ToCString();
		  }
		
	      }
	    stream.close();
	    return 0;
	  }
	}
    }
  return 0;
}



//=======================================================================
void WOKAPI_ProfileMgt_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " [-b|-m] [-D <adbms>] [-d|-o]" << endl;
  cerr << endl;
  cerr << "     -s : returns current station type" << endl;
  cerr << "     -b : returns current DbMS system"  << endl;
  cerr << "     -m : returns current compile mode" << endl;
  cerr << endl;
  cerr << "     -B <adbms> = DFLT|OBJY|OBJS|O2" << endl;
  cerr << "     -S <astation> = sun|ao1|sil|hp|wnt|... (Warning use this option carefully)" << endl;
  cerr << endl;
  cerr << "     -d : Set Debug Mode" << endl;
  cerr << "     -o : Set Optimized Mode" << endl;
  cerr << endl;
  cerr << "     -v : displays current/changed profile" << endl;
  cerr << "     noargs displays current profile (as a message)" << endl;
  cerr << endl;
  return;
}

//=======================================================================
//function : ProfileMgt
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::ProfileMgt(const WOKAPI_Session& asession, 
					    const Standard_Integer argc, const WOKTools_ArgTable& argv, 
					    WOKTools_Return& returns)
{
  WOKTools_Options opts(argc,argv,"hbsmS:B:dov", WOKAPI_ProfileMgt_Usage, "hbsm");
  Handle(TCollection_HAsciiString) adbms, astation;
  Standard_Boolean 
    getdbms    = Standard_False,
    getstation = Standard_False,
    getmode    = Standard_False,
    setdbms    = Standard_False,
    setstation = Standard_False,
    setdebug   = Standard_False,
    setoptim   = Standard_False,
    getprof    = Standard_False;
  

  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'b':
	  getdbms  = Standard_True;
	  break;
	case 's':
	  getstation  = Standard_True;
	  break;
	case 'm':
	  getmode  = Standard_True;
	  break;
	case 'B':
	  setdbms  = Standard_True;
	  adbms    = opts.OptionArgument();
	  break;
	case 'S':
	  setstation = Standard_True;
	  astation = opts.OptionArgument();
	  break;
	case 'd':
	  setdebug = Standard_True;
	  break;
	case 'o':
	  setoptim = Standard_True;
	  break;
	case 'v':
	  getprof  = Standard_True;
	  break;
	default:
	  break;
	}
      opts.Next();
    }
  
  if(opts.Failed()) return 1;

  switch(opts.Arguments()->Length())
    {
    case 0:
      if(!(getdbms||getstation||getmode||setdbms||setstation||setdebug||setoptim) )
	getprof = Standard_True;
      break;
    default:
      WOKAPI_ProfileMgt_Usage(argv[0]);
      return 1;
    }

  if(getdbms)
    {
      returns.AddStringValue(asession.DBMSystem());
      return 0;
    }
  if(getstation)
    {
      returns.AddStringValue(asession.Station());
      return 0;
    }
  if(getmode)
    {
      if(asession.DebugMode())
	{
	  returns.AddStringValue("Debug");
	}
      else
	{
	  returns.AddStringValue("Optimized");
	}
      return 0;
    }
  if(setdbms)
    {
      WOKAPI_Session sess = asession;
      if(sess.SetDBMSystem(adbms)) return 1;
    }
  if(setstation)
    {
      WOKAPI_Session sess = asession;
      if(sess.SetStation(astation)) return 1;
    }
  if(setdebug)
    {
      WOKAPI_Session sess = asession;
      sess.SetDebugMode(Standard_True);
    }
  if(setoptim)
    {
      WOKAPI_Session sess = asession;
      sess.SetDebugMode(Standard_False);
    }

  if(setstation)
    {
      WOKAPI_Session* sess = (WOKAPI_Session*)&asession;
      sess->Close();
      sess->Open(NULL,astation);
    }

  if(setdebug|setoptim|setdbms)
    {
      WOKAPI_Session* sess = (WOKAPI_Session*)&asession;
      sess->Close();
      sess->Open();
    }

  if(getprof)
    {
      InfoMsg() << argv[0] << "Profile in   : " << asession.GetCWEntity().UserPath() << endm;
      InfoMsg() << argv[0] << endm;
      InfoMsg() << argv[0] << "Extractor    : " << asession.DBMSystem() << endm;
      if(asession.DebugMode())
	{
	  InfoMsg() << argv[0] << "Compile Mode : Debug" << endm;
	}
      else
	{
	  InfoMsg() << argv[0] << "Compile Mode : Optimized" << endm;
	}
      InfoMsg() << argv[0] << "Station Type : " << asession.Station() << endm;
      return 0;
    }
  return 0;
}
//=======================================================================
void WOKAPI_ParametersMgr_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " [-s <Name>=<Value>|-u <Name>|-l <class>|-t <Name>|-L] [<name>]\n";
  cerr << endl;
  cerr << "     -s <Name>=<Value> : sets variable <Name> to Value" << endl;
  cerr << "     -u <Name>         : unsets variable <Name>" << endl;
  cerr << "     -l <class>        : lists parameters concerning class (prefix) class" << endl;
  cerr << "     -t <Name>         : tests if variable <Name> is setted" << endl;
  cerr << "     -v <Name>         : get Name value" << endl;
  cerr << "     -e <Name>         : eval Name" << endl;
  cerr << "     -a <Name>         : get Name arguments"  << endl;
  cerr << "     -L                : gives the directory search list" << endl;
  cerr << "     -C                : gives the subclasses list" << endl;
  cerr << "     -F <class>        : gives the files paths used for class <class>" << endl;
  cerr << "     -S <afile>        : searches file in directory search list" << endl;
  cerr << endl;
  cerr << " <name> is the path of entity you wish to operate on" << endl;
  cerr << endl;
}

//=======================================================================
//function : ParametersMgr
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::ParametersMgr(const WOKAPI_Session& asession,
					       const Standard_Integer argc, const WOKTools_ArgTable& argv,
					       WOKTools_Return& returns)
{
  WOKTools_Options opts(argc, argv, "hs:u:t:a:v:e:l:LS:F:C", WOKAPI_ParametersMgr_Usage, "hsultveLS:F:C");
  Handle(TCollection_HAsciiString) aoptarg, aname;
  Standard_Boolean set=Standard_False,  unset=Standard_False, list=Standard_False, args = Standard_False,
    test=Standard_False, eval=Standard_False,  value=Standard_False, slist = Standard_False, sfile = Standard_False,
    classes=Standard_False, files=Standard_False;
  // Code of API for Parameters Management
  
  while(opts.More())
    {
      switch(opts.Option())
	{
	case 's':
	  // Set Variable
	  set = Standard_True;
	  aoptarg = opts.OptionArgument();
	  break;
	case 'u':
	  // Unset Variable
	  unset = Standard_True;
	  aoptarg = opts.OptionArgument();
	  break;
	case 'l':
	  // List Variables
	  aoptarg = opts.OptionArgument();
	  list = Standard_True;
	  break;
	case 't':
	  // Test Variable
	  test = Standard_True;
	  aoptarg = opts.OptionArgument();
	  break;
	case 'e':
	  // eval Variable
	  eval = Standard_True;
	  aoptarg = opts.OptionArgument();
	  break;
	case 'v':
	  // value of Variable
	  value = Standard_True;
	  aoptarg = opts.OptionArgument();
	  break;
	case 'a':
	  // Arguments of parameter
	  args = Standard_True;
	  aoptarg = opts.OptionArgument();
	  break;
	case 'L':
	  slist = Standard_True;
	  break;
	case 'S':
	  sfile = Standard_True;
	  aoptarg = opts.OptionArgument();
	  break;
	case 'F':
	  files = Standard_True;
	  aoptarg = opts.OptionArgument();
	  break;
	case 'C':
	  classes=Standard_True;
	  break;
	default:
	  return 1;
	}
      opts.Next();
    }

  if(opts.Failed() == Standard_True) return 1;

  switch(opts.Arguments()->Length())
    {
    case 0:
      break;
    case 1:
      aname = opts.Arguments()->Value(1);
      break;
    default:
      WOKAPI_ParametersMgr_Usage(argv[0]);
      return 1;
    }

  WOKAPI_Entity theent(asession,aname,Standard_False);

  if(!theent.IsValid()) 
    {
      ErrorMsg() << argv[0] << "Could not determine entity to operate on." << endm;
      return 1;
    }
  
  if(set)
    {
      theent.ParameterSet(aoptarg->Token("=", 1),  aoptarg->Token("=", 2));
    }
  if(unset)
    {
      theent.ParameterUnSet(aoptarg);
    }
  if(list)
    {
      Handle(WOKUtils_HSequenceOfParamItem) aseq;
      Standard_Integer i;

      aseq = theent.ParameterClassValues(aoptarg);

      for(i=1; i<=aseq->Length(); i++)
	{
	  returns.AddStringParameter(aseq->Value(i).Name(), aseq->Value(i).Value());
	}
    }
  if(test)
    {
      returns.AddBooleanValue(theent.IsParameterSet(aoptarg));
    }
  if(eval)
    {
      Handle(TCollection_HAsciiString) result;
      
      result = theent.ParameterEval(aoptarg);

      if(result.IsNull())
	{
	  ErrorMsg() << argv[0] << "Could not eval " << aoptarg << " in " << theent.UserPath() << endm;
	  return 1;
	}

      returns.AddStringValue(result);
    }
  if(value)
    {
      
      Handle(TCollection_HAsciiString) result;
      
      result = theent.ParameterValue(aoptarg);
      
      if(result.IsNull())
	{
	  ErrorMsg() << argv[0] << "No value for " << aoptarg << " in " << theent.UserPath() << endm;
	  return 1;
	}

      returns.AddStringValue(result);
    }
  if(args)
    {
      Handle(TColStd_HSequenceOfHAsciiString) aseq = theent.ParameterArguments(aoptarg);
      Standard_Integer i;

      for(i=1; i<=aseq->Length(); i++)
	{
	  returns.AddStringValue(aseq->Value(i));
	}
    }
  if(slist)
    {
      Handle(TColStd_HSequenceOfHAsciiString) aseq;
      Standard_Integer i;

      aseq = theent.ParameterSearchList();

      for(i=1; i<=aseq->Length(); i++)
	{
	  returns.AddStringValue(aseq->Value(i));
	}
    }
  if(sfile)
    {
      Handle(TCollection_HAsciiString) afile = theent.FindParameterFile(aoptarg);

      if(!afile.IsNull())
	{
	  returns.AddStringValue(afile);
	  return 0;
	}
      else return 1;
    }
  if(classes)
    {
      Handle(TColStd_HSequenceOfHAsciiString) Classes = theent.ParameterClasses();

      if(!Classes.IsNull())
	{
	  for(Standard_Integer i=1; i<=Classes->Length(); i++)
	    {
	      returns.AddStringValue(Classes->Value(i));
	    }
	}
    }
  if(files)
    {
      Handle(TColStd_HSequenceOfHAsciiString) files = theent.ParameterClassFiles(aoptarg);

      if(!files.IsNull())
	{
	  for(Standard_Integer i=1; i<=files->Length(); i++)
	    {
	      returns.AddStringValue(files->Value(i));
	    }
	}
      return 0;
    }
  return 0;
}

//=======================================================================
void WOKAPI_EntityInfo_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " -[options] <WokPath>" << endl;
  cerr << endl;
  cerr << "     -t             : returns Entity Type" << endl;
  cerr << "     -T             : list available types for entity" << endl;
  cerr << "     -d <type>      : get type definition" << endl;
  cerr << "     -a <type>      : get type arguments" << endl;
  cerr << "     -p <type:name> : get path (for a %File dependent type)" << endl;
  cerr << "     -p <type>      : get path (for a non %File dependent type)" << endl;
  cerr << "     -n             : get entity name" << endl;
  cerr << "     -N             : get nesting path" << endl;
  cerr << "     -x             : test if entity exists" << endl;
  cerr << "     -f             : get factory   from path" << endl;
  cerr << "     -W             : get warehouse from path" << endl;
  cerr << "     -P             : get parcel    from path" << endl;
  cerr << "     -s             : get workshop  from path" << endl;
  cerr << "     -w             : get workbench from path" << endl;
  cerr << "     -u             : get dev.unit  from path" << endl;
  cerr << "     -F             : get entity files" << endl;
  cerr << "     -R             : get entity directories" << endl;
  cerr << endl;
}

//=======================================================================
//function : EntityInfo
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::EntityInfo(const WOKAPI_Session& asession,
					    const Standard_Integer argc, const WOKTools_ArgTable& argv,
					    WOKTools_Return& returns)
{
  WOKTools_Options opts(argc, argv, "hxTtd:xa:p:nNfWPswuFR", WOKAPI_EntityInfo_Usage, "hTtdapnNxfWPswuFR");
  Handle(TCollection_HAsciiString) aoptarg, aname;
  Standard_Boolean 
    getenttype   = Standard_False,
    gettypes     = Standard_False,
    gettypedef   = Standard_False,
    gettypeargs  = Standard_False,
    getname      = Standard_False,
    getnesting   = Standard_False,
    getpath      = Standard_False,
    getfactory   = Standard_False,
    getwarehouse = Standard_False,
    getparcel    = Standard_False,
    getworkshop  = Standard_False,
    getworkbench = Standard_False,
    getdevunit   = Standard_False,
    entexists    = Standard_False,
    getfiles     = Standard_False,
    getdirs      = Standard_False;
    
  while(opts.More())
    {
      switch(opts.Option())
	{
	case 't':
	  getenttype = Standard_True;
	  break;
	case 'T':
	  gettypes = Standard_True;
	  break;
	case 'd':
	  gettypedef = Standard_True;
	  aoptarg = opts.OptionArgument();
	  break;
	case 'a':
	  gettypeargs = Standard_True;
	  aoptarg = opts.OptionArgument();
	  break;
	case 'p':
	  getpath = Standard_True;
	  aoptarg = opts.OptionArgument();
	  break;
	case 'n':
	  getname = Standard_True;
	  break;
	case 'N':
	  getnesting = Standard_True;
	  break;
	case 'x':
	  entexists = Standard_True;
	  break;
	case 'f':
	  getfactory = Standard_True;
	  break;
	case 'W':
	  getwarehouse = Standard_True;
	  break;
	case 'P':
	  getparcel = Standard_True;
	  break;
	case 's':
	  getworkshop = Standard_True;
	  break;
	case 'w':
	  getworkbench = Standard_True;
	  break;
	case 'u':
	  getdevunit = Standard_True;
	  break;
	case 'F':
	  getfiles = Standard_True;
	  break;
	case 'R':
	  getdirs = Standard_True;
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
      aname = opts.Arguments()->Value(1);
      break;
    default:
      WOKAPI_EntityInfo_Usage(argv[0]);
      return 1;
    }
  
  if(entexists)
    {
      if(asession.IsValidPath(aname))
	returns.AddBooleanValue(Standard_True);
      else
	returns.AddBooleanValue(Standard_False);
      return 0;
    }
  
  WOKAPI_Entity theent(asession,aname);

  if(!theent.IsValid())
    {
      ErrorMsg() << argv[0] << "Invalid Entity specification" << endm;
      return 1;
    }

  if(getenttype)
    {
      returns.AddStringValue(theent.Code());
      return 0;
    }
  
  if(gettypes)
    {
      Standard_Integer i;
      TColStd_SequenceOfHAsciiString typeseq;

      theent.FileTypes(typeseq);

      for(i=1; i<=typeseq.Length(); i++)
	{
	  returns.AddStringValue(typeseq.Value(i));
	}
      
      return 0;
    }

  if(gettypedef)
    {
      if(!theent.IsFileType(aoptarg))
	{
	  ErrorMsg() << argv[0] << aoptarg << " is not a valid type for entity : " << theent.UserPath() << endm;
	  return 1;
	}
      returns.AddStringValue(theent.GetFileTypeDefinition(aoptarg));
      return 0;
    }
  if(gettypeargs)
    {
      if(!theent.IsFileType(aoptarg))
	{
	  ErrorMsg() << argv[0] << aoptarg << " is not a valid type for entity : " << theent.UserPath() << endm;
	  return 1;
	}

      TColStd_SequenceOfHAsciiString argseq;
      Standard_Integer i;

      theent.GetFileTypeArguments(aoptarg, argseq);

      for(i=1; i<=argseq.Length(); i++)
	{
	  returns.AddStringValue(argseq.Value(i));
	}
      
      return 0;
    }
  if(getpath)
    {
      
      Standard_Integer apos = aoptarg->Location(1, ':', 1, aoptarg->Length());

      if(apos>1 && apos<aoptarg->Length())
	{

	  Handle(TCollection_HAsciiString) type = aoptarg->SubString(1, apos-1);
	  Handle(TCollection_HAsciiString) name = aoptarg->SubString(apos+1, aoptarg->Length());

	  if(!theent.IsFileType(type))
	    {
	      ErrorMsg() << argv[0] << aoptarg << " is not a valid type for entity : " << theent.UserPath() << endm;
	      return 1;
	    }
      
	  returns.AddStringValue(theent.GetFilePath(type, name));
	}
      else
	{
	  if(!theent.IsFileType(aoptarg))
	    {
	      ErrorMsg() << argv[0] << aoptarg << " is not a valid type for entity : " << theent.UserPath() << endm;
	      return 1;
	    }

	  if(theent.IsFileTypeFileDependent(aoptarg))
	    {
	      ErrorMsg() << argv[0] << aoptarg << " is a FileDependant type for entity : " << theent.UserPath() << endm;
	      return 1;
	    }
	  returns.AddStringValue(theent.GetFilePath(aoptarg));
	  return 0;
	}
      return 0;
    }
  if(getname)
    {
      returns.AddStringValue(theent.Name());
      return 0;
    }

  if(getnesting)
    {
      WOKAPI_Entity nesting = theent.NestingEntity();
      
      if(!nesting.IsValid())
	{
	  ErrorMsg() << argv[0] << "Could not obtain nesting of " << theent.UserPath() << endm;
	  return 1;
	}

      Handle(TCollection_HAsciiString) result = nesting.UserPath();
      
      if(result->IsEmpty())
	{
	  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString(":");
	  returns.AddStringValue(result);
	}
      else
	{
	  returns.AddStringValue(result);
	}

      return 0;
    }

  if(getfiles)
    {
      TColStd_SequenceOfHAsciiString aseq;

      theent.GetFiles(aseq);

      for(Standard_Integer i=1; i<=aseq.Length(); i++)
	{
	  returns.AddStringValue(aseq.Value(i));
	}
      return 0;
    }
  if(getdirs)
    {
      TColStd_SequenceOfHAsciiString aseq;

      theent.GetDirs(aseq);

      for(Standard_Integer i=1; i<=aseq.Length(); i++)
	{
	  returns.AddStringValue(aseq.Value(i));
	}
      return 0;
    }

  if(getfactory || getwarehouse || getwarehouse || getparcel ||
     getworkshop || getworkbench || getdevunit ) 
    {
      Handle(TCollection_HAsciiString) nullhandle;

      if(getfactory) 
	{
	  WOKAPI_Factory fact(asession, aname, Standard_False, Standard_True);
	  if(fact.IsValid()) 
	    {
	      returns.AddStringValue(fact.UserPath());
	    }
	}
      if(getwarehouse) 
	{
	  WOKAPI_Warehouse ware(asession, aname, Standard_False, Standard_True);
	  if(ware.IsValid()) 
	    {
	      returns.AddStringValue(ware.UserPath());
	    }
	}
      if(getparcel) 
	{
	  WOKAPI_Parcel parcel(asession, aname, Standard_False, Standard_True);
	  if(parcel.IsValid()) 
	    {
	      returns.AddStringValue(parcel.UserPath());
	    }
	}

      if(getworkshop) 
	{
	  WOKAPI_Workshop shop(asession, aname, Standard_False, Standard_True);
	  if(shop.IsValid()) 
	    {
	      returns.AddStringValue(shop.UserPath());
	    }
	}

      if(getworkbench) 
	{
	  WOKAPI_Workbench bench(asession, aname, Standard_False, Standard_True);
	  if(bench.IsValid()) 
	    {
	      returns.AddStringValue(bench.UserPath());
	    }
	}
      
      if(getdevunit) 
	{
	  WOKAPI_Unit unit(asession, aname, Standard_False, Standard_True);
	  if(unit.IsValid()) 
	    {
	      returns.AddStringValue(unit.UserPath());
	    }
	}
    }
  return 0;
}

//=======================================================================
void WOKAPI_EntityClose_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " -a [<apath>] " << endl;
  cerr << endl;
  cerr << "     -a : Close all entities (Resets WOK) " << endl;
  cerr << endl;
}

//=======================================================================
//function : EntityClose
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::EntityClose(const WOKAPI_Session& asession,
					     const Standard_Integer argc, const WOKTools_ArgTable& argv,
					     WOKTools_Return& )
{
  WOKTools_Options opts(argc, argv, "ha", WOKAPI_EntityClose_Usage, "h");
  Handle(TCollection_HAsciiString) aoptarg, aname;
  Standard_Boolean all = Standard_False;
  

  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'a':
	  all = Standard_True;
	default:
	  break;
	} 
      opts.Next();
    }
  
  if(opts.Failed() == Standard_True) return 1;
  
  WOKAPI_Session * modsess = (WOKAPI_Session *) &asession;
  if(all) {modsess->Close();modsess->Open();return 0;}
  else
    {
      switch(opts.Arguments()->Length())
	{
	case 0:
	  break;
	case 1:
	  aname = opts.Arguments()->Value(1);
	  break;
	default:
	  WOKAPI_ParametersMgr_Usage(argv[0]);
	  return 1;
	}
      
      WOKAPI_Entity theent(asession,aname);
      
      if(!theent.IsValid())
	{
	  ErrorMsg() << argv[0] << "Invalid Entity specification" << endm;
	  return 1;
	}
      theent.Close();
      return 0;
    }
}


//=======================================================================
void WOKAPI_Locate_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " -<options> [<Workbench>]" << endl;
  cerr << endl;
  cerr << "    Options are : " << endl;
  cerr << "     -f <UD:type:File> : locates a file and gives its ID" << endl;   
  cerr << "     -p <UD:type:File> : locates a file and gives its path" << endl;
  cerr << "     -u <Unit>         : locates a dev unit" << endl;
  cerr << "     -V <UnitNesting>  : Visibility Unit Nesting" << endl;
  cerr << endl;
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : Locate
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Command::Locate(const WOKAPI_Session& asession,
		       const Standard_Integer argc, const WOKTools_ArgTable& argv,
		       WOKTools_Return& returns)
{
  WOKTools_Options opts(argc, argv, "hf:p:u:V:", WOKAPI_Locate_Usage, "hV");
  Standard_Boolean 
    file = Standard_False,
    path = Standard_False,
    unit = Standard_False;
  Handle(TCollection_HAsciiString) fileid, unitid, aname;
  Handle(TColStd_HSequenceOfHAsciiString) visi;

  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'f':
	  file   = Standard_True;
	  fileid = opts.OptionArgument();
	  break;
	case 'p':
	  path   = Standard_True;
	  fileid = opts.OptionArgument();
	  break;
	case 'u':
	  unit   = Standard_True;
	  unitid = opts.OptionArgument();
	  break;
	case 'V':
	  if(visi.IsNull()) visi = new TColStd_HSequenceOfHAsciiString;
	  visi->Append(opts.OptionArgument());
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
      aname = opts.Arguments()->Value(1);
      break;
    default:
      WOKAPI_Locate_Usage(argv[0]);
      return 1;
    }
  
  WOKAPI_Locator alocator;

  if(visi.IsNull())
    {
      WOKAPI_Workbench bench(asession,aname);
      
      if(!bench.IsValid())
	{
	  ErrorMsg() << argv[0]
		   << "Could not determine visibility : Specify workbench in command line or use wokcd" << endm;
	  return 1;
	}
      
      alocator.Set(bench);
    }
  else
    {
      ErrorMsg() << argv[0]
	       << "Option -V not yet implemented : use Workbench to determine visibility" << endm;
      return 1;
    }

  if(alocator.IsValid())
    {
      if(file|path)
	{
	  WOKAPI_File Thefile = alocator.Locate(fileid);
	  
	  if(Thefile.IsValid()) 
	    {
	      if(path)
		{
		  returns.AddStringValue(Thefile.Path());
		}
	      else
		{
		  returns.AddStringValue(Thefile.UserPath());
		}
	    }
	}
      else if(unit)
	{
	  WOKAPI_Unit Theunit = alocator.LocateUnit(unitid);
	  if(Theunit.IsValid()) returns.AddStringValue(Theunit.UserPath());
	}
    }
  return 0;

}

