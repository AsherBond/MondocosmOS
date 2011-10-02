#ifndef WNT
// File:	WOKUnix_ShellManager.cxx
// Created:	Thu Apr  4 23:21:02 1996
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <WOKUnix_ShellManager.ixx>

#include <WOKUnix_ProcessManager.hxx>
#include <WOKUnix_SequenceOfProcess.hxx>
#include <WOKUnix_Shell.hxx>
#include <WOKUnix_RemoteShell.hxx>

//=======================================================================
//function : GetShell
//purpose  : 
//=======================================================================
Handle(WOKUnix_Shell) WOKUnix_ShellManager::GetShell()
{
  Standard_Integer i;
  WOKUnix_SequenceOfProcess& procseq = WOKUnix_ProcessManager::Processes();
  Handle(WOKUnix_Shell) ashell;

  for(i=1; i<=procseq.Length(); i++)
    {
      ashell = Handle(WOKUnix_Shell)::DownCast(procseq.Value(i));
      if(!ashell.IsNull()) 
	{

	  if(!ashell->IsLocked())
	    {
	      return ashell;
	    }
	}
    }
  ashell = new WOKUnix_Shell;

  return ashell;
}

//=======================================================================
//function : GetShell
//purpose  : 
//=======================================================================
Handle(WOKUnix_Shell) WOKUnix_ShellManager::GetShell(const Standard_Integer apid)
{
  Standard_Integer i;
  WOKUnix_SequenceOfProcess& procseq = WOKUnix_ProcessManager::Processes();
  Handle(WOKUnix_Shell) ashell;
  
  for(i=1; i<=procseq.Length(); i++)
    {
      if(procseq.Value(i)->IsKind(STANDARD_TYPE(WOKUnix_Shell)))
	{
	  ashell = Handle(WOKUnix_Shell)::DownCast(procseq.Value(i));
	  
	  if(!ashell->Pid() == apid)
	    {
	      return ashell;
	    }
	  else ashell.Nullify();
	}
    }
  return ashell;
}

//=======================================================================
//function : GetRemoteShell
//purpose  : 
//=======================================================================
Handle(WOKUnix_RemoteShell) WOKUnix_ShellManager::GetRemoteShell(const Handle(TCollection_HAsciiString) & ahost,
								   const TCollection_AsciiString & apath)
{
  Standard_Integer i;
  WOKUnix_SequenceOfProcess& procseq = WOKUnix_ProcessManager::Processes();
  Handle(WOKUnix_RemoteShell) aremoteshell;

  for(i=1; i<=procseq.Length(); i++)
    {
      if(procseq.Value(i)->IsKind(STANDARD_TYPE(WOKUnix_RemoteShell)))
	{
	
	  aremoteshell = Handle(WOKUnix_RemoteShell)::DownCast(procseq.Value(i));

	  if( (!aremoteshell->IsLocked()) && (!strcmp(aremoteshell->Host()->ToCString(),ahost->ToCString()) ))
	    {
	      return aremoteshell;
	    }
	}
    }
  aremoteshell = new WOKUnix_RemoteShell(ahost,apath);

  return aremoteshell;
}







#endif
