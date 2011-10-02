// File:	woksh.cxx<2>
// Created:	Tue Aug  1 23:26:26 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>





#include <tcl.h>

#ifdef WNT
#  pragma message( "Information: tcl"TCL_VERSION".lib is using as TCL library" )
#endif  // WNT


//extern "C" {
//#ifdef NEED_MATHERR
//extern int matherr();
//int *tclDummyMathPtr = (int *) matherr;
//#endif
//}


int main(int argc, char **argv)
{
    Tcl_Main(argc, argv, Tcl_AppInit);
    return 0;			/* Needed only to prevent compiler warning. */
}


#include <Standard_ErrorHandler.hxx>
#include <Standard_Failure.hxx>
#include <Standard_Macro.hxx>

#include <OSD.hxx>

#include <WOKTools_Messages.hxx>

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

int Wok_Init(WOKTclTools_PInterp interp)
{
#if defined( WNT ) && defined( _DEBUG )
  _debug_break ( "Wok_Init" );
#endif  // WNT && _DEBUG
  
  Handle(WOKTclTools_Interpretor)& CurrentInterp = WOKTclTools_Interpretor::Current();

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

  WOKTclTools_Package tcl ( CurrentInterp, "Tcl", TCL_VERSION );
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

  // GENERAL PURPOSE COMMANDS
  WOKInter->Add("Sinfo",     "Information about session",   WOKAPI_Command::SessionInfo,      "WOK COMMAND\n");
  WOKInter->Add("wokenv",    "Set run environment",         WOKAPI_Command::EnvironmentMgr,   "WOK COMMAND\n");
  WOKInter->Add("wokcd",     "Moves in a path",             WOKAPI_Command::MoveTo,           "WOK COMMAND\n");
  WOKInter->Add("wokparam",  "Entity Parameters Mgt",       WOKAPI_Command::ParametersMgr,    "WOK COMMAND\n");
  WOKInter->Add("wokinfo",   "Entity Information",          WOKAPI_Command::EntityInfo,       "WOK COMMAND\n");
  WOKInter->Add("wokclose",  "Entity closing",              WOKAPI_Command::EntityClose,      "WOK COMMAND\n");
  WOKInter->Add("wokprofile","Manages DBMS current System", WOKAPI_Command::ProfileMgt,       "WOK COMMAND\n");
  WOKInter->Add("woklocate", "Locates WOK elements",        WOKAPI_Command::Locate,           "WOK COMMAND\n");

  // FACTORY COMMANDS
  WOKInter->Add("fcreate",   "creates a factory",           WOKAPI_Command::FactoryCreate,    "WOK COMMAND\n");
  WOKInter->Add("finfo",     "Information about factory",   WOKAPI_Command::FactoryInfo,      "WOK COMMAND\n");
  WOKInter->Add("frm",       "removes a factory",           WOKAPI_Command::FactoryDestroy,   "WOK COMMAND\n");

  // WAREHOUSE COMMANDS
  WOKInter->Add("Wcreate",   "creates a warehouse",         WOKAPI_Command::WarehouseCreate,  "WOK COMMAND\n");
  WOKInter->Add("Winfo",     "Information about Warehouse", WOKAPI_Command::WarehouseInfo,    "WOK COMMAND\n");
  WOKInter->Add("Wrm",       "removes a Warehouse",         WOKAPI_Command::WarehouseDestroy, "WOK COMMAND\n");
  WOKInter->Add("Wdeclare",       "Declares a parcel in a Warehouse",         WOKAPI_Command::WarehouseDeclare, "WOK COMMAND\n");

  // PARCEL COMMANDS  
  WOKInter->Add("pinfo",     "Information about parcel",    WOKAPI_Command::ParcelInfo,       "WOK COMMAND\n");

  // WORKSHOP COMMANDS
  WOKInter->Add("sinfo",     "Information about workshop",  WOKAPI_Command::WorkshopInfo,     "WOK COMMAND\n");
  WOKInter->Add("screate",   "creates a workshop",          WOKAPI_Command::WorkshopCreate,   "WOK COMMAND\n");
  WOKInter->Add("srm",       "removes a workshop",          WOKAPI_Command::WorkshopDestroy,  "WOK COMMAND\n");
  
  // WORKBENCH COMMANDS
  WOKInter->Add("w_info",    "Information about workbench", WOKAPI_Command::WorkbenchInfo,    "WOK COMMAND\n");
  WOKInter->Add("wcreate",   "creates de workbench",        WOKAPI_Command::WorkbenchCreate,  "WOK COMMAND\n");
  WOKInter->Add("wrm",       "removes a  workbench",        WOKAPI_Command::WorkbenchDestroy, "WOK COMMAND\n");
  WOKInter->Add("wmove",     "moves a  workbench",          WOKAPI_Command::WorkbenchMove,    "WOK COMMAND\n");
  WOKInter->Add("wprocess",  "builds a  workbench",         WOKAPI_Command::WorkbenchProcess, "WOK COMMAND\n");
  
  // UNIT COMMANDS
  WOKInter->Add("uinfo",      "Information about Unit",      WOKAPI_Command::UnitInfo,         "WOK COMMAND\n");
  WOKInter->Add("umake",      "Unit Construction command",   WOKAPI_Command::UnitMake,         "WOK COMMAND\n");
  WOKInter->Add("ucreate",    "Unit Creation command",       WOKAPI_Command::UnitCreate,       "WOK COMMAND\n");
  WOKInter->Add("urm",        "Unit Removal command",        WOKAPI_Command::UnitDestroy,      "WOK COMMAND\n");

  // Triggered step COMMANDS
  WOKInter->Add("stepinputadd",       "Trigger step input add",   WOKAPI_Command::AddInputFile,   "WOK COMMAND\n");
  WOKInter->Add("stepinputinfo",      "Trigger step input info",  WOKAPI_Command::InputFileInfo,  "WOK COMMAND\n");
  WOKInter->Add("stepoutputadd",      "Trigger step output add",  WOKAPI_Command::AddOutputFile,  "WOK COMMAND\n");
  WOKInter->Add("stepoutputinfo",     "Trigger step output info", WOKAPI_Command::OutputFileInfo, "WOK COMMAND\n");
  WOKInter->Add("stepaddexecdepitem", "Adds a depitem to step",   WOKAPI_Command::AddExecDepItem, "WOK COMMAND\n");

  WOKUtils_ProcessManager::Arm();

  try {
    OCC_CATCH_SIGNALS
    
    WOKInter->ChangeSession().Open();
    
  }
  catch(Standard_Failure) {
    Handle(Standard_Failure) E = Standard_Failure::Caught();
    ErrorMsg() << "WOKTCL_AppInit" << "Exception was raised : " << E->GetMessageString() << endm;
    WOKUtils_ProcessManager::UnArm();
    return TCL_ERROR;
  }

  WOKUtils_ProcessManager::UnArm();


  WOKUtils_Trigger::SetTriggerHandler(WOKTCL_TriggerHandler);

  WOKInter->AddExitHandler(Wok_ExitHandler);

  //
  // PROVIDE PACKAGE WOK
  //
  WOKTclTools_Package wok(WOKInter, "Wok", "1.5");

  if(wok.EvalInitFile()) return TCL_ERROR;
  if(wok.Provide())      return TCL_ERROR;
  return TCL_OK;
}


int Tcl_AppInit(Tcl_Interp *interp)
{
  
  if (Tcl_Init(interp) == TCL_ERROR) {
    return TCL_ERROR;
  }
  if (Wok_Init(interp) == TCL_ERROR) {
    return TCL_ERROR;
  }
  
  Tcl_SetVar(interp, "tcl_rcFileName", "~/.tclshrc", TCL_GLOBAL_ONLY);
  return TCL_OK;
}
