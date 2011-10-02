// File:	WOKTCL_Interpretor.cxx
// Created:	Mon Apr  1 19:26:43 1996
// Author:	Jean GAUTIER
//		<jga@cobrax>
#include <Standard_SStream.hxx>

#include <tcl.h>

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

#include <TCollection_HAsciiString.hxx>

#include <Standard_ErrorHandler.hxx>


#include <WOKTCL_Interpretor.ixx>


#include <WOKTCL_DefaultCommand.hxx>

//=======================================================================
//function : WOKTCL_Interpretor
//purpose  : 
//=======================================================================
WOKTCL_Interpretor::WOKTCL_Interpretor()
{
}
//=======================================================================
//function : WOKTCL_Interpretor
//purpose  : 
//=======================================================================
WOKTCL_Interpretor::WOKTCL_Interpretor(const WOKTclTools_PInterp& p)
: WOKTclTools_Interpretor(p)
{
}

//=======================================================================
//function : Add
//purpose  : 
//=======================================================================
void WOKTCL_Interpretor::Add(const Standard_CString Command, 
			     const Standard_CString Help, 
			     const WOKAPI_APICommand& Function,
			     const Standard_CString Group)
{
  CData* C = new CData(Function,this);
  Standard_PCharacter pCommand, pHelp, pGroup;
  //
  pCommand=(Standard_PCharacter)Command;
  pHelp=(Standard_PCharacter)Help;
  pGroup=(Standard_PCharacter)Group;
  Tcl_CreateCommand(Interp(), pCommand, DefaultCommand, (ClientData) C, DefaultCommandDelete);
  
  // add the help
  Tcl_SetVar2(Interp(),"WOKTCL_Helps", pCommand,pHelp,TCL_GLOBAL_ONLY);
  Tcl_SetVar2(Interp(),"WOKTCL_Groups",pGroup,pCommand,
	      TCL_GLOBAL_ONLY|TCL_APPEND_VALUE|TCL_LIST_ELEMENT);
}



//=======================================================================
//function : Session
//purpose  : 
//=======================================================================
const WOKAPI_Session& WOKTCL_Interpretor::Session() const
{
  return mysession;
}

//=======================================================================
//function : Session
//purpose  : 
//=======================================================================
WOKAPI_Session& WOKTCL_Interpretor::ChangeSession() 
{
  return mysession;
}

