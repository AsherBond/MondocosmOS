#ifndef WNT

#include <WOKTools_Messages.hxx>
#include <TCollection_HAsciiString.hxx>
#include <TCollection_AsciiString.hxx>

#include <WOKUnix_ASyncStatus.ixx>

//=======================================================================
//function : WOKUnix_ASyncStatus
//purpose  : 
//=======================================================================
 WOKUnix_ASyncStatus::WOKUnix_ASyncStatus()
{
  
}

//=======================================================================
//function : WOKUnix_ASyncStatus
//purpose  : 
//=======================================================================
 WOKUnix_ASyncStatus::WOKUnix_ASyncStatus(const TCollection_AsciiString & apath)
                     :WOKUnix_ShellStatus(apath)
{
  
}

//=======================================================================
//function : EndCmd
//purpose  : 
//=======================================================================
void WOKUnix_ASyncStatus::EndCmd(const Handle(WOKUnix_Shell)& ashell)
{
   static Handle(TCollection_HAsciiString) abuf = new TCollection_HAsciiString("\n@ wok_csh_status += $status\n");

   ashell->WOKUnix_Process::Send(abuf);
   
#ifdef WOK_VERBOSE
   VerboseMsg()("WOK_PROCESS") << "WOKUnix_ASyncStatus::EndCmd" << "Accumulating Status" << endm;
#endif
   return;
}

//=======================================================================
//function : Sync
//purpose  : 
//=======================================================================
void WOKUnix_ASyncStatus::Sync(const Handle(WOKUnix_Shell)& ashell)
{
#ifdef USE_SYNCHRO
  Handle(TCollection_HAsciiString) abuf = new TCollection_HAsciiString(TCollection_AsciiString("\n/tmp/synchro "));
  abuf->AssignCat(Name());
  abuf->AssignCat(" $wok_csh_status\n");
#else
  Handle(TCollection_HAsciiString) abuf = new TCollection_HAsciiString(TCollection_AsciiString("\necho $wok_csh_status > "));

  abuf->AssignCat(Name());
  abuf->AssignCat("\n");
#endif

  ashell->WOKUnix_Process::Send(abuf);
  
  mystatus = ashell->SyncAndStatus();
  
#ifdef WOK_VERBOSE
  VerboseMsg()("WOK_PROCESS") << "WOKUnix_ASyncStatus::Sync" << "GotStatus : " << mystatus << endm;
#endif

  Reset(ashell);

  return;
}

//=======================================================================
//function : Reset
//purpose  : 
//=======================================================================
void WOKUnix_ASyncStatus::Reset(const Handle(WOKUnix_Shell)& ashell)
{
   static Handle(TCollection_HAsciiString) abuf = new TCollection_HAsciiString("\nset wok_csh_status = 0\n");
   ashell->WOKUnix_Process::Send(abuf);
#ifdef WOK_VERBOSE
   VerboseMsg()("WOK_PROCESS") << "WOKUnix_ASyncStatus::Reset" << "Reset Shell" << endm;
#endif
   return;
}

#endif
