#ifndef WNT


#include <WOKTools_Messages.hxx>

#include <TCollection_HAsciiString.hxx>
#include <TCollection_AsciiString.hxx>
#include <WOKUnix_SyncStatus.ixx>


//=======================================================================
//function : WOKUnix_SyncStatus
//purpose  : 
//=======================================================================
 WOKUnix_SyncStatus::WOKUnix_SyncStatus()
{
}

//=======================================================================
//function : WOKUnix_SyncStatus
//purpose  : 
//=======================================================================
 WOKUnix_SyncStatus::WOKUnix_SyncStatus(const TCollection_AsciiString & apath)
                     :WOKUnix_ShellStatus(apath)
{
}

//=======================================================================
//function : EndCmd
//purpose  : 
//=======================================================================
void WOKUnix_SyncStatus::EndCmd(const Handle(WOKUnix_Shell)& ashell)
{
#ifdef USE_SYNCHRO
  Handle(TCollection_HAsciiString) abuf = new TCollection_HAsciiString("\n/tmp/synchro ");
  abuf->AssignCat(Name());
  abuf->AssignCat(" $status\n");
#else
  Handle(TCollection_HAsciiString) abuf = new TCollection_HAsciiString("\necho $status > ");
  abuf->AssignCat(Name());
  abuf->AssignCat("\n");
#endif
  ashell->WOKUnix_Process::Send(abuf);

  mystatus = ashell->SyncAndStatus();

#ifdef WOK_VERBOSE
  VerboseMsg()("WOK_PROCESS") << "WOKUnix_SyncStatus::EndCmd" << "Command ended with status : " << mystatus << endm;
#endif
}

//=======================================================================
//function : Sync
//purpose  : 
//=======================================================================
void WOKUnix_SyncStatus::Sync(const Handle(WOKUnix_Shell)& )
{
  
}

//=======================================================================
//function : Reset
//purpose  : 
//=======================================================================
void WOKUnix_SyncStatus::Reset(const Handle(WOKUnix_Shell)& )
{
}

#endif
