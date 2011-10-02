

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKTools_Define.hxx>
#include <WOKTools_HSequenceOfDefine.hxx>

#include <WOKUtils_AdmFile.hxx>
#include <WOKUtils_Path.hxx>
#include <WOKUtils_Triggers.hxx>

#include <WOKernel_DBMSystem.hxx>
#include <WOKernel_Station.hxx>

#include <WOKAPI_Process.ixx>


//=======================================================================
//function : WOKAPI_Process
//purpose  : 
//=======================================================================
WOKAPI_Process::WOKAPI_Process(const WOKAPI_Session& session)
: mysession(session)
{
}


//=======================================================================
//function : Init
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Process::Init(const Handle(TCollection_HAsciiString)& abench, 
				      const Standard_Boolean  debug,
				      const Handle(TCollection_HAsciiString)& aprofile)
{
  Standard_Boolean initsession = Standard_False;

  if(mysession.DebugMode() != debug)
    {
      mysession.SetDebugMode(debug);
      initsession = Standard_True;
    }

  if(!aprofile.IsNull())
    {
      if(strcmp(aprofile->ToCString(), mysession.DBMSystem()->ToCString()))
	{
	  mysession.SetDBMSystem(aprofile);
	  initsession = Standard_True;
	}
    }

  if(initsession)
    {
      mysession.Close();
      mysession.Open(abench);
    }

  WOKAPI_Workbench thebench(mysession, abench);

  if(!mybp.Init(thebench))
    {
      ErrorMsg() << "WOKAPI_Process::Init"
	       << "Could not initialize build process" << endm;
      return Standard_False;
    }

  return Standard_True;
}


//=======================================================================
//function : ExploreInitSection
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Process::ExploreInitSection(const Handle(TColStd_HSequenceOfHAsciiString)& lines, const Standard_Integer fromindex)
{
  static char InitStr[]      = ":Init";
  static char EndInitStr[]   = ":EndInit";
  static char WorkbenchStr[] = "Workbench";

  static char ModeStr[]      = "Mode";

  static char DBMSStr[]      = "DBMS";

  static char InfoStr[]      = "InfoLog";
  static char WarningStr[]   = "WarningLog";
  static char ErrorStr[]     = "ErrorLog";

  // First lookup Init Section
  Standard_Boolean initcomplete = Standard_False;
  Standard_Integer i=fromindex;

  Handle(TCollection_HAsciiString) workbench;
  Handle(TCollection_HAsciiString) mode;
  Handle(TCollection_HAsciiString) dbms;
  Handle(TCollection_HAsciiString) info;
  Handle(TCollection_HAsciiString) warning;
  Handle(TCollection_HAsciiString) error;
  
  while( i<=lines->Length() && !initcomplete)
    {
      const Handle(TCollection_HAsciiString)& line = lines->Value(i);
      
      if(line->Value(1) == ':')
	{
	  line->RightAdjust();
      
	  if(!strcmp(line->ToCString(), InitStr))
	    {
	      i++;
	      
	      while(i<=lines->Length() && !initcomplete)
		{
		  const Handle(TCollection_HAsciiString)& line = lines->Value(i);
		  if(line->Value(1) == ':')
		    {
		      line->RightAdjust();
		      if(!strcmp(EndInitStr, line->ToCString()))
			initcomplete = Standard_True;
		      else
			{
			  ErrorMsg() << "WOKAPI_Process::ExploreInitSection"
			    << "Section flag " << line << " inside :Init section" << endm;
			  return -1;
			}
		    }
		  else
		    {
		      Handle(TCollection_HAsciiString) left  = line->Token("=", 1);
		      Handle(TCollection_HAsciiString) right = line->Token("=", 2);
		      
		      if(!left.IsNull() && !right.IsNull())
			{
			  left->LeftAdjust();  left->RightAdjust();
			  right->LeftAdjust(); right->RightAdjust();
			  
			  if(!strcmp(left->ToCString(), WorkbenchStr))
			    workbench = right;
			  else if(!strcmp(left->ToCString(), ModeStr))
			    mode = right;
			  else if(!strcmp(left->ToCString(), DBMSStr))
			    dbms = right;
			  else if(!strcmp(left->ToCString(), InfoStr))
			    info = right;
			  else if(!strcmp(left->ToCString(), WarningStr))
			    warning = right;
			  else if(!strcmp(left->ToCString(), ErrorStr))
			    error = right;
			  else
			    {
			      ErrorMsg() << "WOKAPI_Process::ExploreInitSection"
				<< "Unrecognized line in :Init section : " << line << endm;
			      return -1;
			    }
			}
		      else
			{
			  ErrorMsg() << "WOKAPI_Process::ExploreInitSection"
			    << "Malformed line in :Init section: " << line << endm;
			  return -1;
			}
		    }
		  if(!initcomplete) i++;
		}
	    }
	}
      i++;
    }

  if(!initcomplete)
    {
      ErrorMsg() << "WOKAPI_Process::ExploreInitSection"
	<< "Could not find :Init section in file" << endm;
      return Standard_False;
    }

  if(workbench.IsNull())
    {
      ErrorMsg() << "WOKAPI_Process::ExploreInitSection"
	<< "Could not find required field Workbench= in :Init section" << endm;
      return -1;
    }
  
  WOKAPI_Workbench abench(mysession, workbench);

  if(!abench.IsValid())
    {
      ErrorMsg() << "WOKAPI_Process::ExploreInitSection"
	<< "Could not find " << workbench << " or this is not a workbench name" << endm;
      return -1;
    }

  Standard_Boolean debugmode = mysession.DebugMode();

  if(!mode.IsNull())
    {
      if(!strcmp(mode->ToCString(), "Debug"))
	{
	  debugmode = Standard_True;
	}
      else if(!strcmp(mode->ToCString(), "Optimise"))
	{
	  debugmode = Standard_False;
	}
      else
	{
	  ErrorMsg() << "WOKAPI_Process::ExploreInitSection"
	    << "Invalid mode specification " << mode << " is ignored" << endm;
	  return -1;
	}
    }

  Handle(TCollection_HAsciiString) dbmsmode = mysession.DBMSystem();

  if(!dbms.IsNull())
    {
      if(!WOKernel_DBMSystem::IsNameKnown(dbms))
	{
	  ErrorMsg() << "WOKAPI_::SetDBMSystem" 
	    << dbms << " is not known as a DBMSystem" << endm;
	  return -1;
	}
      else
	{
	  dbmsmode = dbms;
	}
    }
  
  if(!Init(abench.UserPath(), debugmode, dbmsmode)) return -1;
  return i++;
}


//=======================================================================
//function : AdvanceToNextValidSection
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Process::AdvanceToNextValidSection(const Handle(TColStd_HSequenceOfHAsciiString)& lines, const Standard_Integer fromindex)
{

  Standard_Integer i = fromindex;


  while(i<=lines->Length())
    {
      const Handle(TCollection_HAsciiString)& line = lines->Value(i);

      if(line->Value(1) == ':')
	{
	  Standard_Integer j = 1;
	  Handle(TCollection_HAsciiString) token = line->Token(":", j);
	  
	  while(!token->IsEmpty())
	    {
	      if(!strcmp(token->ToCString(), "Build") || 
		 !strcmp(token->ToCString(), "Init")  || 
		 !strcmp(token->ToCString(), "Tcl"))
		{
		  // This is a new section
		  return i;
		}
	      else if(!strcmp(token->ToCString(), "EndBuild") || 
		      !strcmp(token->ToCString(), "EndInit")  || 
		      !strcmp(token->ToCString(), "EndTcl"))
		{
		  break;
		}
	      else
		{
		  if(WOKernel_DBMSystem::IsNameKnown(token))
		    {
		      if(!mysession.DBMSystem()->IsSameString(token)) break;
		    }
		  else if(WOKernel_Station::IsNameKnown(token))
		    {
		      if(!mysession.Station()->IsSameString(token)) break;
		    }
		  else
		    {
		      ErrorMsg() << "WOKAPI_Process::AdvanceToNextValidSection"
			<< "Unrecognized conditional (" << token << ") in line: " << line << endm;
		      return -1;
		    }
		}
	      j++;
	      token = line->Token(":", j);
	    }
	}
      i++;
    }
  return -1;
}


//=======================================================================
//function : ExecuteTcl
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Process::ExecuteTcl(const Handle(TCollection_HAsciiString)& atcl)
{

  WOKUtils_Trigger atrig;

  atrig("eval") << atcl << endt;

  return Standard_True;  
}

//=======================================================================
//function : ExploreTclSection
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Process::ExploreTclSection(const Handle(TColStd_HSequenceOfHAsciiString)& lines, const Standard_Integer fromindex)
{
  static char TclStr[]     = ":Tcl";
  static char EndTclStr[]  = ":EndTcl";

  Standard_Boolean tclcomplete = Standard_False;
  Standard_Integer i = fromindex;
  Handle(TCollection_HAsciiString) command = new TCollection_HAsciiString;

  while( i<=lines->Length() && !tclcomplete)
    {
      const Handle(TCollection_HAsciiString)& line = lines->Value(i);
      
      if(line->Value(1) == ':')
	{
	  line->RightAdjust();
	  Standard_CString sessflg = &(line->ToCString()[line->Length()-4]);

	  if(!strcmp(sessflg, TclStr))
	    {
	      i++;
	      
	      while(i<=lines->Length() && !tclcomplete)
		{
		  const Handle(TCollection_HAsciiString)& line = lines->Value(i);

		  if(line->Value(1) == ':')
		    {
		      line->RightAdjust();
		      if(!strcmp(EndTclStr, line->ToCString()))
			tclcomplete = Standard_True;
		      else
			{
			  ErrorMsg() << "WOKAPI_Process::ExploreTclSection"
			    << "Section flag " << line << " inside :Tcl section" << endm;
			  return -1;
			}
		    }
		  else
		    {
		      command->AssignCat(line);
		      command->AssignCat(";");
		    }
		  if(!tclcomplete) i++;
		}
	    }
	}
      i++;
    }
  
  if(!ExecuteTcl(command)) return -1;

  return i++;
}

//=======================================================================
//function : ExploreBuildSection
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Process::ExploreBuildSection(const Handle(TColStd_HSequenceOfHAsciiString)& lines, const Standard_Integer fromindex)
{
  static char BuildStr[]     = ":Build";
  static char EndBuildStr[]  = ":EndBuild";

  Standard_Boolean buildcomplete = Standard_False;
  Standard_Integer i = fromindex;
  Handle(WOKTools_HSequenceOfDefine) defines = new WOKTools_HSequenceOfDefine;

  while( i<=lines->Length() && !buildcomplete)
    {
      const Handle(TCollection_HAsciiString)& line = lines->Value(i);
      
      if(line->Value(1) == ':')
	{
	  line->RightAdjust();
      
	  if(!strcmp(line->ToCString(), BuildStr))
	    {
	      i++;
	      
	      while(i<=lines->Length() && !buildcomplete)
		{
		  const Handle(TCollection_HAsciiString)& line = lines->Value(i);

		  if(line->Value(1) == ':')
		    {
		      line->RightAdjust();
		      if(!strcmp(EndBuildStr, line->ToCString()))
			buildcomplete = Standard_True;
		      else
			{
			  ErrorMsg() << "WOKAPI_Process::ExploreBuildSection"
			    << "Section flag " << line << " inside :Build section" << endm;
			  return -1;
			}
		    }
		  else
		    {
		      Handle(TCollection_HAsciiString) left  = line->Token("=", 1);
		      Handle(TCollection_HAsciiString) right = line->Token("=", 2);
		      
		      if(!left.IsNull() && !right.IsNull())
			{
			  left->LeftAdjust();  left->RightAdjust();
			  right->LeftAdjust(); right->RightAdjust();

			  right->ChangeAll(',', ' ');
		
			  defines->Append(WOKTools_Define(left, right));
			}
		    }
		  if(!buildcomplete) i++;
		}
	    }
	}
      i++;
    }
  
  if(!ExecuteBuild(defines)) return -1;

  return i++;
}


//=======================================================================
//function : ExecuteBuild
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Process::ExecuteBuild(const Handle(WOKTools_HSequenceOfDefine)& params)
{


  Standard_Integer nbsteps = mybp.SelectOnDefines(params);

  if(nbsteps) 
    {
      mybp.PrintBanner();
      
      mybp.Execute();
    }
  return Standard_True;
}


//=======================================================================
//function : ExecuteFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Process::ExecuteFile(const Handle(TCollection_HAsciiString)& afile)
{
  static char BuildStr[]     = ":Build";
  static char TclStr[]       = ":Tcl";

  Standard_Integer index;
  WOKUtils_AdmFile configfile(new WOKUtils_Path(afile));

  Handle(TColStd_HSequenceOfHAsciiString) lines = configfile.Read();

  index = ExploreInitSection(lines);
 
  index = AdvanceToNextValidSection(lines, index);
  while(index > 0 && index <= lines->Length())
    {
      const Handle(TCollection_HAsciiString)& line = lines->Value(index);

      if(line->Search(BuildStr) > 0)
	{
	  index = ExploreBuildSection(lines, index);
	}
      else if(line->Search(TclStr) > 0)
	{
	  index = ExploreTclSection(lines,index);
	}
      index = AdvanceToNextValidSection(lines, index);
    }
 
  return Standard_True;
}
