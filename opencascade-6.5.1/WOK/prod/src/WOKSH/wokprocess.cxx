// File:	woksh.cxx<2>
// Created:	Tue Aug  1 23:26:26 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <tcl.h>

#ifdef WNT
#  pragma message( "Information: tcl"TCL_VERSION".lib is using as TCL library" )
#endif  // WNT



#include <Standard_ErrorHandler.hxx>
#include <Standard_Failure.hxx>
#include <Standard_Macro.hxx>

#include <OSD.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKTools_Return.hxx>

#ifdef WNT
#include <WOKUtils_ShellManager.hxx>
#define WOKUtils_ProcessManager WOKUtils_ShellManager
#else
#include <WOKUtils_Signal.hxx>
#include <WOKUtils_SigHandler.hxx>
#include <WOKUtils_ProcessManager.hxx>
#endif
#include <WOKUtils_Trigger.hxx>

#include <WOKAPI_Session.hxx>
#include <WOKAPI_Command.hxx>

#include <WOKTclTools_Package.hxx>

#include <WOKTCL_Interpretor.hxx>

#include <WOKTCL_TriggerHandler.hxx>

#ifdef WNT
# ifdef _DEBUG
extern "C" void _debug_break ( char* );
# endif  // _DEBUG
# define WOK_EXPORT __declspec( dllexport )
#else
# define WOK_EXPORT
#endif  // WNT

extern "C" WOK_EXPORT void Wok_ExitHandler(void *); 
extern "C" WOK_EXPORT int  Wok_Init(WOKTclTools_PInterp );

void Wok_ExitHandler(void *) 
{
  WOKUtils_ProcessManager::KillAll();
}


int main(int argc, char **argv)
{
#if defined( WNT ) && defined( _DEBUG )
  _debug_break ( "main" );
#endif  // WNT && _DEBUG

  Handle(WOKTclTools_Interpretor)& CurrentInterp = WOKTclTools_Interpretor::Current();

  WOKTclTools_PInterp interp  = Tcl_CreateInterp();
  
  if(WOKTclTools_Interpretor::Current().IsNull())
    {
      CurrentInterp = new WOKTCL_Interpretor;
      CurrentInterp->Set(interp);
    }
  else
    {
      if(WOKTclTools_Interpretor::Current()->Interp() != interp)
	{
	   CurrentInterp = new WOKTCL_Interpretor;
	   CurrentInterp->Set(interp);
	}
    }

  WOKTclTools_Package tcl(CurrentInterp, "Tcl", TCL_VERSION);
  tcl.Require();
  
  OSD::SetSignal();                  //==== Armed the signals. =============
#ifndef WNT
  WOKUtils_Signal::Arm(WOKUtils_SIGINT,    (WOKUtils_SigHandler) NULL);
#endif //WNT

  Handle(WOKTCL_Interpretor) WOKInter = Handle(WOKTCL_Interpretor)::DownCast(CurrentInterp);

  if(WOKInter.IsNull())
    {
      WOKInter = new WOKTCL_Interpretor(interp);
    }

  if(CurrentInterp->EndMessageProc() != NULL)
    WOKInter->SetEndMessageProc(CurrentInterp->EndMessageProc());

  CurrentInterp = WOKInter;

  WOKUtils_ProcessManager::Arm();

  WOKUtils_Trigger::SetTriggerHandler(WOKTCL_TriggerHandler);

  WOKInter->AddExitHandler(Wok_ExitHandler);

  try {
    OCC_CATCH_SIGNALS
    WOKTools_Return returns;
    
    WOKInter->ChangeSession().Open();

    WOKAPI_Command::WorkbenchProcess(WOKInter->Session(), argc, argv,  returns);

    WOKInter->TreatReturn(returns);
    
  }
  catch(Standard_Failure) {
    Handle(Standard_Failure) E = Standard_Failure::Caught();
    ErrorMsg() << "WOKTCL_AppInit" << "Exception was raised : " << E->GetMessageString() << endm;
    WOKUtils_ProcessManager::UnArm();
    return TCL_ERROR;
  }

  WOKUtils_ProcessManager::UnArm();


  return 0;
}

