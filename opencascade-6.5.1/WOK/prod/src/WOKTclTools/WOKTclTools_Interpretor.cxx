// File:	WOKTclTools_Interpretor.cxx
// Created:	Fri Jul 28 22:00:34 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <TCollection_HAsciiString.hxx>
#include <Standard_Macro.hxx>

#include <WOKTools_ReturnValue.hxx>
#include <WOKTools_StringValue.hxx>
#include <WOKTools_EnvValue.hxx>
#include <WOKTools_ChDirValue.hxx>
#include <WOKTools_InterpFileValue.hxx>
#include <WOKTools_HSequenceOfReturnValue.hxx>
#include <WOKTools_Messages.hxx>
#include <Standard_PCharacter.hxx>
#include <tcl.h>

#ifdef WNT
#include <WOKUtils_ShellManager.hxx>
#define WOKUtils_ProcessManager WOKUtils_ShellManager
# ifdef _DEBUG
#  include <windows.h>
typedef void ( *FREE_FUNC ) ( void* );
void Free ( void* );
# endif  // _DEBUG
#else
#include <WOKUtils_ProcessManager.hxx>
#endif  // WNT
#include <WOKTclTools_Interpretor.ixx>

#include <Standard_RangeError.hxx>
#include <Standard_ErrorHandler.hxx>


#ifdef WNT
#  pragma message( "Information: tcl"TCL_VERSION".lib is using as TCL library" )
#endif  // WNT



//
// Call backs for TCL
//

struct CData {
  CData(WOKTclTools_CommandFunction ff, Handle(WOKTclTools_Interpretor) ii) : f(ff), i(ii) {}
  WOKTclTools_CommandFunction f;
  Handle(WOKTclTools_Interpretor) i;
};

struct WOKCData {
  WOKCData(WOKTclTools_WokCommand ff, Handle(WOKTclTools_Interpretor) ii) : f(ff), i(ii) {}
  WOKTclTools_WokCommand   f;
  Handle(WOKTclTools_Interpretor) i;
};

// MKV 24.08.05
#if ((TCL_MAJOR_VERSION > 8) || ((TCL_MAJOR_VERSION == 8) && (TCL_MINOR_VERSION >= 4))) && !defined(USE_NON_CONST)
static Standard_Integer CommandCmd (ClientData clientData, Tcl_Interp *,
				    Standard_Integer argc, const char* argv[])
#else
static Standard_Integer CommandCmd (ClientData clientData, Tcl_Interp *,
				    Standard_Integer argc, char* argv[])
#endif
{
  CData* C = (CData*) clientData;
  
  Handle(WOKTclTools_Interpretor)& CurrentInterp = WOKTclTools_Interpretor::Current();

 // set de l'interprete en cours
 CurrentInterp = C->i;

  // MKV 24.08.05
#if ((TCL_MAJOR_VERSION > 8) || ((TCL_MAJOR_VERSION == 8) && (TCL_MINOR_VERSION >= 4))) && !defined(USE_NON_CONST)
  if (C->f(C->i,argc,(char**)argv) == 0) 
#else
  if (C->f(C->i,argc,argv) == 0) 
#endif
    {
     CurrentInterp.Nullify();
      return TCL_OK;
    }
  else
    {
     CurrentInterp.Nullify();
      return TCL_ERROR;
    }
}

// MKV 24.08.05
#if ((TCL_MAJOR_VERSION > 8) || ((TCL_MAJOR_VERSION == 8) && (TCL_MINOR_VERSION >= 4))) && !defined(USE_NON_CONST)
static Standard_Integer WOKCommand(ClientData clientData, Tcl_Interp *, 
				   Standard_Integer argc, const char* argv[])
#else
static Standard_Integer WOKCommand(ClientData clientData, Tcl_Interp *, 
				   Standard_Integer argc, char* argv[])
#endif
{
  WOKCData* C = (WOKCData*) clientData;
  
  // set de l'interprete en cours
  WOKTclTools_Interpretor::Current()           = C->i;

  WOKTclTools_WokCommand  acmd = C->f;

  {
    try {
      OCC_CATCH_SIGNALS
      
      WOKTools_Return returns;
      
      WOKUtils_ProcessManager::Arm();
      
      // appel de la fonction API
      // MKV 24.08.05
#if ((TCL_MAJOR_VERSION > 8) || ((TCL_MAJOR_VERSION == 8) && (TCL_MINOR_VERSION >= 4))) && !defined(USE_NON_CONST)
      if(!(*acmd)(argc, (char**)argv, returns))
#else
      if(!(*acmd)(argc, argv, returns))
#endif
	{
	  if(!C->i->TreatReturn(returns)) 
	    {
	      WOKUtils_ProcessManager::UnArm();
	      return TCL_OK;
	    }
	}
      WOKUtils_ProcessManager::UnArm();
      return TCL_ERROR;
    }
    catch (Standard_Failure) {
      Handle(Standard_Failure) E = Standard_Failure::Caught();
      
      // MKV 24.08.05
#if ((TCL_MAJOR_VERSION > 8) || ((TCL_MAJOR_VERSION == 8) && (TCL_MINOR_VERSION >= 4))) && !defined(USE_NON_CONST)
      ErrorMsg() << (char*)argv[0] << "Exception was raised : " << E->GetMessageString() << endm;
#else
      ErrorMsg() << argv[0] << "Exception was raised : " << E->GetMessageString() << endm;
#endif
      WOKUtils_ProcessManager::UnArm();
      return TCL_ERROR;
    }
  }

  return TCL_OK;
}

static void CommandDelete (ClientData clientData)
{
  CData *C = (CData*) clientData;
  delete C;
}

//=======================================================================
//function : WOKTclTools_Interpretor
//purpose  : 
//=======================================================================
WOKTclTools_Interpretor::WOKTclTools_Interpretor() :
  isAllocated(Standard_True)
{
  myInterp  = Tcl_CreateInterp();
}

//=======================================================================
//function : WOKTclTools_Interpretor
//purpose  : 
//=======================================================================
WOKTclTools_Interpretor::WOKTclTools_Interpretor(const WOKTclTools_PInterp& p) :
  isAllocated(Standard_False),
  myInterp(p)
{
}

//=======================================================================
//function : Add
//purpose  : 
//=======================================================================
void WOKTclTools_Interpretor::Add(const Standard_CString n,
				  const Standard_CString help,
				  const WOKTclTools_CommandFunction f,
				  const Standard_CString group)
{
  CData* C = new CData(f,this);
  Standard_PCharacter pN, pHelp, pGroup;
  //
  pN=(Standard_PCharacter)n;
  pHelp=(Standard_PCharacter)help;
  pGroup=(Standard_PCharacter)group;
  //
  Tcl_CreateCommand(myInterp,pN,CommandCmd, (ClientData) C, CommandDelete);

  // add the help
  Tcl_SetVar2(myInterp,"WOKTclTools_Helps",pN,pHelp,TCL_GLOBAL_ONLY);
  Tcl_SetVar2(myInterp,"WOKTclTools_Groups",pGroup,pN,
	      TCL_GLOBAL_ONLY|TCL_APPEND_VALUE|TCL_LIST_ELEMENT);
}


//=======================================================================
//function : Add
//purpose  : 
//=======================================================================
void WOKTclTools_Interpretor::Add(const Standard_CString n,
				  const Standard_CString help,
				  const WOKTclTools_WokCommand f,
				  const Standard_CString group)
{
  WOKCData* C = new WOKCData(f,this);
  Standard_PCharacter pN, pHelp, pGroup;
  //
  pN=(Standard_PCharacter)n;
  pHelp=(Standard_PCharacter)help;
  pGroup=(Standard_PCharacter)group;
  //
  Tcl_CreateCommand(myInterp,pN,WOKCommand, (ClientData) C, CommandDelete);

  // add the help
  Tcl_SetVar2(myInterp,"WOKTclTools_Helps",pN,pHelp,TCL_GLOBAL_ONLY);
  Tcl_SetVar2(myInterp,"WOKTclTools_Groups",pGroup,pN,
	      TCL_GLOBAL_ONLY|TCL_APPEND_VALUE|TCL_LIST_ELEMENT);
}


//=======================================================================
//function : AddExitHandler
//purpose  : 
//=======================================================================
void WOKTclTools_Interpretor::AddExitHandler(const WOKTclTools_ExitHandler f)
{
  Tcl_CreateExitHandler(f, NULL);
}


//=======================================================================
//function : DeleteExitHandler
//purpose  : 
//=======================================================================
void WOKTclTools_Interpretor::DeleteExitHandler(const WOKTclTools_ExitHandler f)
{
  Tcl_DeleteExitHandler(f, NULL);
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : IsCmdName
//purpose  : 
//=======================================================================
Standard_Boolean WOKTclTools_Interpretor::IsCmdName(Standard_CString const n)
{
  Tcl_CmdInfo Info;
  Standard_PCharacter pN;
  //
  pN=(Standard_PCharacter)n;
  return (Tcl_GetCommandInfo(myInterp, pN, &Info)) != 0;
}

//=======================================================================
//function : Remove
//purpose  : 
//=======================================================================
Standard_Boolean WOKTclTools_Interpretor::Remove(Standard_CString const n)
{
  Standard_PCharacter pN;
  //
  pN=(Standard_PCharacter)n;
  Standard_Integer result = Tcl_DeleteCommand(myInterp,pN);
  return (result == 0);
}


//=======================================================================
//function : TreatResult
//purpose  : 
//=======================================================================
Standard_Integer WOKTclTools_Interpretor::TreatReturn(const WOKTools_Return& returns) 
{ 
  Standard_Integer i;

  WOK_TRACE {
    for(i = 1; i <= returns.Length() ; i++) 
      {
	Handle(WOKTools_ReturnValue) avalue = returns.Value(i);
	
	switch(avalue->Type())
	  {
	  case WOKTools_String:
	    {
	      Handle(WOKTools_StringValue) astrval = Handle(WOKTools_StringValue)::DownCast(avalue);
	      InfoMsg() << "WOKTclTools_Intepretor::TreatResult" 
		      << "Returned String " << astrval->Value() << endm;
	    }
	    break;
	  case WOKTools_Environment:
	    {
	      Handle(WOKTools_EnvValue) aenval =  Handle(WOKTools_EnvValue)::DownCast(avalue);
	      if(aenval->ToSet())
		{
		  InfoMsg() << "WOKTclTools_Intepretor::TreatResult"
			  << "Returned SetEnvironment " << aenval->Name() << "=" << aenval->Value() << endm;
		}
	      else
		{
		  InfoMsg() << "WOKTclTools_Intepretor::TreatResult" 
			  << "Returned UnSetEnvironment " << aenval->Name() << endm;
		}
	    }
	    break;
	  case WOKTools_ChDir:
	    {
	      Handle(WOKTools_ChDirValue) achdirval =  Handle(WOKTools_ChDirValue)::DownCast(avalue);
	      InfoMsg() << "WOKTclTools_Intepretor::TreatResult" 
		      << "Returned Change dir " << achdirval->Path() << endm;
	    }
	    break;
	  case WOKTools_InterpFile:
	    {
	      Handle(WOKTools_InterpFileValue) aifile =  Handle(WOKTools_InterpFileValue)::DownCast(avalue);
	      InfoMsg() << "WOKTclTools_Intepretor::TreatResult" 
		      << "Returned Source File " << aifile->File() << endm;
	    }
	    break;
	  }
      }
  }

  Reset(); 
  
  // Prise en compte des resultats
  for(i = 1; i <= returns.Length() ; i++) 
    {
      Handle(WOKTools_ReturnValue) avalue = returns.Value(i);
	
      switch(avalue->Type())
	{
	case WOKTools_String:
	  {
	    Handle(WOKTools_StringValue) astrval = Handle(WOKTools_StringValue)::DownCast(avalue);
	    if (!astrval->Value().IsNull()) {
	      AppendElement(astrval->Value()->ToCString());
	    }
	  }
	  break;
	case WOKTools_Environment:
	  { 
	    Handle(WOKTools_EnvValue) aenval;
	    Handle(TCollection_HAsciiString) acmd;
	      
	    aenval = Handle(WOKTools_EnvValue)::DownCast(avalue);

	    if(aenval->ToSet())
	      {
		if(IsCmdName("wok_setenv_proc"))
		  {
		    acmd = new TCollection_HAsciiString("wok_setenv_proc ");
		    acmd->AssignCat(aenval->Name()->ToCString());
		    acmd->AssignCat(" \"");
		    acmd->AssignCat(aenval->Value()->ToCString());
		    acmd->AssignCat("\"");
		    if(Eval(acmd->ToCString())) return 1;
		  }
		else
		  {
		    acmd = new TCollection_HAsciiString("set env(");
		    acmd->AssignCat(aenval->Name()->ToCString());
		    acmd->AssignCat(") \"");
		    acmd->AssignCat(aenval->Value()->ToCString());
		    acmd->AssignCat("\"");
		    if(Eval(acmd->ToCString())) return 1;
		  }
	      }
	    else
	      {
		if(IsCmdName("wok_unsetenv_proc"))
		  {
		    acmd = new TCollection_HAsciiString("wok_unsetenv_proc ");
		    acmd->AssignCat(aenval->Name()->ToCString());
		    if(Eval(acmd->ToCString())) return 1;
		  }
		else
		  {
		    acmd = new TCollection_HAsciiString("unset env(");
		    acmd->AssignCat(aenval->Name()->ToCString());
		    acmd->AssignCat(")");
		    if(Eval(acmd->ToCString())) return 1;
		  }
	      }
	  }
	  break;
	case WOKTools_ChDir:
	  {
	    Handle(WOKTools_ChDirValue) achdirval;
	    Handle(TCollection_HAsciiString) acmd;
	      
	    achdirval =  Handle(WOKTools_ChDirValue)::DownCast(avalue);
	      
	      
	    if(IsCmdName("wok_cd_proc"))
	      {
		acmd = new TCollection_HAsciiString("wok_cd_proc ");
		acmd->AssignCat(achdirval->Path());
		if(Eval(acmd->ToCString())) return 1;
	      }
	    else
	      {
		acmd = new TCollection_HAsciiString("cd ");
		acmd->AssignCat(achdirval->Path());
		if(Eval(acmd->ToCString())) return 1;
	      }
	      
	  }
	  break;
	case WOKTools_InterpFile:
	  {
	    Handle(WOKTools_InterpFileValue) afilevalue = Handle(WOKTools_InterpFileValue)::DownCast(avalue);
	    Handle(TCollection_HAsciiString) acmd;

	    if(IsCmdName("wok_source_proc"))
	      {
		
		switch(afilevalue->InterpType())
		  {
		  case WOKTools_CShell:
		    acmd = new TCollection_HAsciiString("wok_source_proc csh ");
		    break;
		  case WOKTools_BourneShell:
		    acmd = new TCollection_HAsciiString("wok_source_proc sh ");
		    break;
		  case WOKTools_KornShell:
		    acmd = new TCollection_HAsciiString("wok_source_proc ksh ");
		    break;
		  case WOKTools_TclInterp:
		    acmd = new TCollection_HAsciiString("wok_source_proc tcl ");
		    break;
		  case WOKTools_EmacsLisp:
		    acmd = new TCollection_HAsciiString("wok_source_proc emacs ");
		    break;
		  case WOKTools_WNTCmd:
		    acmd = new TCollection_HAsciiString("wok_source_proc wnt ");
		    break;
		  }
		acmd->AssignCat(afilevalue->File());
		if(Eval(acmd->ToCString())) return 1;
	      }
	    else
	      {
		if(afilevalue->InterpType() != WOKTools_TclInterp)
		  {
		    ErrorMsg() << "WOKTclTools_Intepretor::TreatResult" 
			     << "Cannot eval not Tcl script without wok_source_proc defined" << endm;
		    ErrorMsg() << "WOKTclTools_Intepretor::TreatResult" 
			     << "Please provide wok_source_proc to use feature" << endm;
		    return 1;
		  }
		else
		  {
		    if(EvalFile(afilevalue->File()->ToCString()) != TCL_OK) return 1;
		  }
	      }

	  }
	}
    }
  return 0;
}

//=======================================================================
//function : Result
//purpose  : 
//=======================================================================
Standard_CString WOKTclTools_Interpretor::Result() const
{
  return myInterp->result;
}

//=======================================================================
//function : GetReturnValues
//purpose  : 
//=======================================================================
Standard_Boolean WOKTclTools_Interpretor::GetReturnValues(WOKTools_Return& retval) const
{
  Standard_Integer  argc,i;
  // MKV 24.08.05
#if ((TCL_MAJOR_VERSION > 8) || ((TCL_MAJOR_VERSION == 8) && (TCL_MINOR_VERSION >= 4))) && !defined(USE_NON_CONST)
  const char** argv;
#else
  Standard_PCharacter* argv;
#endif
  
  if(Tcl_SplitList(myInterp, myInterp->result, &argc, &argv)) return Standard_True;

  for(i=0; i<argc; i++)
    {
      // MKV 24.08.05
#if ((TCL_MAJOR_VERSION > 8) || ((TCL_MAJOR_VERSION == 8) && (TCL_MINOR_VERSION >= 4))) && !defined(USE_NON_CONST)
      retval.AddStringValue((char*)argv[i]);
#else
      retval.AddStringValue(argv[i]);
#endif
    }
  if ( argc > 1 ) {
#if defined( WNT ) && defined( _DEBUG )
    Free ( argv );
#else
    free(argv);
#endif  // WNT && _DEBUG
  }
  return Standard_False;
}

//=======================================================================
//function : Reset
//purpose  : 
//=======================================================================
void WOKTclTools_Interpretor::Reset()
{
  Tcl_ResetResult(myInterp);
}

//=======================================================================
//function : Append
//purpose  : 
//=======================================================================
void WOKTclTools_Interpretor::Append(const Standard_CString s)
{
  Tcl_AppendResult(myInterp,s,(Standard_CString)0);
}

//=======================================================================
//function : Append
//purpose  : 
//=======================================================================
void WOKTclTools_Interpretor::Append(const Standard_Integer i)
{
  char c[100];
  sprintf(c,"%d",i);
  Tcl_AppendResult(myInterp,c,(Standard_CString)0);
}

//=======================================================================
//function : Append
//purpose  : 
//=======================================================================
void WOKTclTools_Interpretor::Append(const Standard_Real i)
{
  char s[100];
  sprintf(s,"%.17g",i);
  Tcl_AppendResult(myInterp,s,(Standard_CString)0);
}

//=======================================================================
//function : AppendElement
//purpose  : 
//=======================================================================
void WOKTclTools_Interpretor::AppendElement(const Standard_CString s)
{
#ifdef IRIX
  //AppendElement is declared as (Tcl_Interp *interp, char *string)
  //on SGI 32
  Tcl_AppendElement(myInterp,(char*) s);
#else
  Tcl_AppendElement(myInterp, s);
#endif
}

//=======================================================================
//function : Eval
//purpose  : 
//=======================================================================
Standard_Integer WOKTclTools_Interpretor::Eval(const Standard_CString line)
{
  Standard_PCharacter pLine;
  //
  pLine=(Standard_PCharacter)line;
  return Tcl_Eval(myInterp,pLine);
}


//=======================================================================
//function : Eval
//purpose  : 
//=======================================================================
Standard_Integer WOKTclTools_Interpretor::RecordAndEval(const Standard_CString line,
							const Standard_Integer flags)
{
  Standard_PCharacter pLine;
  //
  pLine=(Standard_PCharacter)line;
  return Tcl_RecordAndEval(myInterp,pLine,flags);
}

//=======================================================================
//function : EvalFile
//purpose  : 
//=======================================================================
Standard_Integer WOKTclTools_Interpretor::EvalFile(const Standard_CString fname)
{
  Standard_PCharacter pFname;
  //
  pFname=(Standard_PCharacter)fname;
  return Tcl_EvalFile(myInterp,pFname);
}

//=======================================================================
//function :Complete
//purpose  : 
//=======================================================================
Standard_Boolean WOKTclTools_Interpretor::Complete(const Standard_CString line)
{ 
  Standard_PCharacter pLine;
  //
  pLine=(Standard_PCharacter)line;
  return Tcl_CommandComplete(pLine);
}

//=======================================================================
//function : Destroy
//purpose  : 
//=======================================================================
void WOKTclTools_Interpretor::Destroy()
{
  if (isAllocated)
    Tcl_DeleteInterp(myInterp);
}

//=======================================================================
//function : Interp
//purpose  : 
//=======================================================================
WOKTclTools_PInterp WOKTclTools_Interpretor::Interp() const
{
  return myInterp;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Set
//purpose  : 
//=======================================================================
void WOKTclTools_Interpretor::Set(const WOKTclTools_PInterp& PI)
{
  if (isAllocated)
    Tcl_DeleteInterp(myInterp);
  isAllocated = Standard_False;
  myInterp = PI;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Current
//purpose  : 
//=======================================================================
Handle(WOKTclTools_Interpretor)& WOKTclTools_Interpretor::Current()
{  
  static Handle(WOKTclTools_Interpretor) CurrentInterp;
  return CurrentInterp;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetEndMessageProc
//purpose  : 
//=======================================================================
void WOKTclTools_Interpretor::SetEndMessageProc(const Standard_CString aproc)
{
  EndMessageProc() = strdup(aproc);
  return;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : UnSetEndMessageProc
//purpose  : 
//=======================================================================
void WOKTclTools_Interpretor::UnSetEndMessageProc()
{
  EndMessageProc() = NULL;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : EndMessageProc
//purpose  : 
//=======================================================================
Standard_CString& WOKTclTools_Interpretor::EndMessageProc() 
{
  static Standard_CString myendmsgproc;
  return myendmsgproc;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetEndMessageArgs
//purpose  : 
//=======================================================================
void WOKTclTools_Interpretor::SetEndMessageArgs(const Standard_CString aArgs)
{
  EndMessageArgs() = strdup(aArgs);
  return;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : UnSetEndMessageArgs
//purpose  : 
//=======================================================================
void WOKTclTools_Interpretor::UnSetEndMessageArgs()
{
  EndMessageArgs() = NULL;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : EndMessageArgs
//purpose  : 
//=======================================================================
Standard_CString& WOKTclTools_Interpretor::EndMessageArgs() 
{
  static Standard_CString myendmsgArgs;
  return myendmsgArgs;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : TreatMessage
//purpose  : 
//=======================================================================
void WOKTclTools_Interpretor::TreatMessage(const Standard_Boolean newline, 
					   const Standard_Character achar, 
					   const Standard_CString astr) const
{
  Tcl_CmdInfo infoPtr;
  Standard_Integer status = 0;

  if(EndMessageProc() != NULL)
    {
      // MKV 24.08.05
#if ((TCL_MAJOR_VERSION > 8) || ((TCL_MAJOR_VERSION == 8) && (TCL_MINOR_VERSION >= 4))) && !defined(USE_NON_CONST)
      char* argv[5];
#else
      Standard_Character* argv[5];
#endif
      Standard_Character  tmp[2];
      int argc;
      argv[0]    = (Standard_PCharacter)EndMessageProc();
      argv[1]    = tmp;
      argv[1][0] = achar;
      argv[1][1] = '\0';
      argv[2]    = (Standard_PCharacter)astr;
      if(EndMessageArgs() == NULL)
	{
	  argc       = 3;
	  argv[3]    = NULL;
	}
      else
	{
	  argc       = 4;
	  argv[3]    = (Standard_PCharacter)EndMessageArgs();
	  argv[4]    = NULL;
	}

      if (Tcl_GetCommandInfo(myInterp, argv[0], &infoPtr) != 0)
	{
	  // MKV 24.08.05
#if ((TCL_MAJOR_VERSION > 8) || ((TCL_MAJOR_VERSION == 8) && (TCL_MINOR_VERSION >= 4))) && !defined(USE_NON_CONST)
	  infoPtr.proc (infoPtr.clientData, myInterp, argc, (const char**)argv);
#else
	  infoPtr.proc (infoPtr.clientData, myInterp, argc, argv);
#endif
	  if (status==1)
	    Tcl_AddErrorInfo(myInterp,"Invalid message");
	}
      else 
	Tcl_AddErrorInfo(myInterp,"Unknown message handler procedure");
    }
  else
    {
      if (!newline)
	{
	  Standard_CString argv[5];
	  int argc   = 4;
	  argv[0]    = "puts";
	  argv[2]    = "stderr";
	  argv[1]    = "-nonewline";
	  argv[3]    = (Standard_PCharacter)astr;
	  argv[4]    = NULL;
	  
	  if (Tcl_GetCommandInfo(myInterp, argv[0], &infoPtr) != 0)
	    {
	      // MKV 24.08.05
#if ((TCL_MAJOR_VERSION > 8) || ((TCL_MAJOR_VERSION == 8) && (TCL_MINOR_VERSION >= 4))) && !defined(USE_NON_CONST)
	      status = infoPtr.proc (infoPtr.clientData, myInterp, argc, (const char**)argv);
#else
	      status = infoPtr.proc (infoPtr.clientData, myInterp, argc, argv);
#endif
	      if (status==1)
		Tcl_AddErrorInfo(myInterp,"Invalid message");
	    }
	  else 
	    Tcl_AddErrorInfo(myInterp,"Unknown message handler procedure");
	}
      else
	{
	  Standard_CString argv[4];
	  int argc   = 3;
	  argv[0]    = "puts";
	  argv[1]    = "stderr";
	  argv[2]    = (Standard_PCharacter)astr;
	  argv[3]    = NULL;
	  
	  if (Tcl_GetCommandInfo(myInterp, argv[0], &infoPtr) != 0)
	    {
	      // MKV 24.08.05
#if ((TCL_MAJOR_VERSION > 8) || ((TCL_MAJOR_VERSION == 8) && (TCL_MINOR_VERSION >= 4))) && !defined(USE_NON_CONST)
	      status = infoPtr.proc (infoPtr.clientData, myInterp, argc, (const char**)argv);
#else
	      status = infoPtr.proc (infoPtr.clientData, myInterp, argc, argv);
#endif
	      if (status==1)
		Tcl_AddErrorInfo(myInterp,"Invalid message");
	    }
	  else
	    Tcl_AddErrorInfo(myInterp,"Unknown message handler procedure");
	}
    }
  return;
}
//=======================================================================
#if defined( WNT ) && defined( _DEBUG )
void Free ( void* ptr ) {

 static HMODULE   hCrt;
 static FREE_FUNC func;
 
 if ( hCrt == NULL ) {
 
  hCrt = GetModuleHandle ( "MSVCRT" );
  func = ( FREE_FUNC )GetProcAddress ( hCrt, "free" );

 }  // end if

 if ( hCrt != NULL && func != NULL ) ( *func ) ( ptr );

}  // end Free
#endif  // WNT && _DEBUG
