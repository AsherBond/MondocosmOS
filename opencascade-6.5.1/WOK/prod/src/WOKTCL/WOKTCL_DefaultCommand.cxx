// File:	WOKTCL_DefaultCommand.cxx
// Created:	Mon Oct 28 17:29:44 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>

#include <Standard_SStream.hxx>
#include <Standard_Macro.hxx>

#include <tcl.h>

#ifdef WNT
#  pragma message( "Information: tcl"TCL_VERSION".lib is using as TCL library" )
#endif  // WNT


#include <Standard_ErrorHandler.hxx>

#include <TCollection_HAsciiString.hxx>

#include <WOKTools_StringValue.hxx>
#include <WOKTools_EnvValue.hxx>
#include <WOKTools_ChDirValue.hxx>
#include <WOKTools_Messages.hxx>

#ifdef WNT
#include <WOKUtils_ShellManager.hxx>
#define WOKUtils_ProcessManager WOKUtils_ShellManager
#else
#include <WOKUtils_ProcessManager.hxx>
#endif  // WNT

#include <WOKAPI_Session.hxx>
#include <WOKAPI_APICommand.hxx>

#include <WOKTCL_Interpretor.hxx>

#include <WOKTCL_DefaultCommand.hxx>


#ifndef WOK_SESSION_KEEP
#define WOK_SESSION_KEEP 1
#endif

// MKV 24.08.05
#if ((TCL_MAJOR_VERSION > 8) || ((TCL_MAJOR_VERSION == 8) && (TCL_MINOR_VERSION >= 4))) && !defined(USE_NON_CONST)
Standard_Integer DefaultCommand(ClientData clientData, Tcl_Interp *, 
				Standard_Integer argc, const char* argv[])
#else
Standard_Integer DefaultCommand(ClientData clientData, Tcl_Interp *, 
				Standard_Integer argc, char* argv[])
#endif
{
  Handle(WOKTclTools_Interpretor)& CurrentInterp = WOKTclTools_Interpretor::Current();

  volatile Standard_Integer status = 0;

  CData* C = (CData*) clientData;
  
  // set de l'interprete en cours
  CurrentInterp           = C->i;

  WOKAPI_APICommand  acmd = C->f;

#ifndef WOK_SESSION_KEEP
  WOKAPI_Session* asess = (WOKAPI_Session *) &(C->i->Session());
  asess->Open();
#endif

  try {
    OCC_CATCH_SIGNALS

    WOKTools_Return returns;

    WOKUtils_ProcessManager::Arm();

    // appel de la fonction API
    // MKV 24.08.05
#if ((TCL_MAJOR_VERSION > 8) || ((TCL_MAJOR_VERSION == 8) && (TCL_MINOR_VERSION >= 4))) && !defined(USE_NON_CONST)
    if(!(*acmd)(C->i->Session(), argc, (char**)argv, returns))
#else
    if(!(*acmd)(C->i->Session(), argc, argv, returns))
#endif
      {
	if(!C->i->TreatReturn(returns)) 
	  {
	    WOKUtils_ProcessManager::UnArm();
	    status = TCL_OK;
	  }
      }
    else
      {
	WOKUtils_ProcessManager::UnArm();
	status = TCL_ERROR;
      }
  }
  catch (Standard_Failure) {
    Handle(Standard_Failure) E = Standard_Failure::Caught();

    // MKV 24.08.05
#if ((TCL_MAJOR_VERSION > 8) || ((TCL_MAJOR_VERSION == 8) && (TCL_MINOR_VERSION >= 4))) && !defined(USE_NON_CONST)
    ErrorMsg() << (char*)argv[0] << "Exception was raised : " << E->GetMessageString() << endm;
#else
    ErrorMsg() << argv[0] << "Exception was raised : " << E->GetMessageString() << endm;
#endif

    WOKAPI_Session* asess = (WOKAPI_Session *) &(C->i->Session());
    asess->GeneralFailure(E);

    WOKUtils_ProcessManager::UnArm();

    status = TCL_ERROR;
  }

#ifndef WOK_SESSION_KEEP
  asess->Close();
#endif

  return status;
}

void DefaultCommandDelete (ClientData clientData)
{
  CData *C = (CData*) clientData;
  delete C;
}


