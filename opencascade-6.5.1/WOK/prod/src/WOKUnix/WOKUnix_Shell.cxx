#ifndef WNT

#ifdef HAVE_CONFIG_H
# include <config.h>
#endif

#ifdef HAVE_UNISTD_H
# include <unistd.h>
#endif

#ifdef HAVE_SYS_TYPES_H
# include <sys/types.h>
#endif

#ifdef HAVE_BSTRING_H
# include <bstring.h>
#endif

#ifdef HAVE_SYS_PARAM_H
# include <sys/param.h>
#endif

#ifdef HAVE_SYS_TIME_H
# include <sys/time.h>
#endif

#ifdef HAVE_STRINGS_H
# include <strings.h>
#endif

#include <stdio.h>
#include <Standard_Stream.hxx>
#include <WOKTools_Messages.hxx>

#include <WOKUnix_ASyncStatus.hxx>
#include <WOKUnix_SyncStatus.hxx>
#include <WOKUnix_DumpScript.hxx>
#include <WOKUnix_ProcessOutput.hxx>
#include <WOKUnix_ProcessManager.hxx>

#include <WOKUnix_Shell.ixx>

#ifndef WOK_IDLE_TRIGGER
#define WOK_IDLE_TRIGGER 1
#endif

#ifdef WOK_IDLE_TRIGGER
#include <WOKUtils_Triggers.hxx>
#endif

static  char *ShellArgv[] = {"/bin/csh", "-f", NULL};

//=======================================================================
//function : WOKUnix_Shell
//purpose  : 
//=======================================================================
WOKUnix_Shell::WOKUnix_Shell(const WOKUnix_ShellMode amode,
			       const WOKUnix_PopenOutputMode outmode, 
			       const WOKUnix_PopenBufferMode bufmode) 
:  WOKUnix_Process(3, ShellArgv, outmode, bufmode, -1), 
    mymode(amode), mylocked(Standard_False), myecho(Standard_False)
{
  switch(amode)
    {
    case WOKUnix_SynchronousMode:
      mystatus = new WOKUnix_SyncStatus;
      break;
    case WOKUnix_AsynchronousMode:
      mystatus = new WOKUnix_ASyncStatus;
      break;
    case WOKUnix_DumpScriptMode:
      mystatus = new WOKUnix_DumpScript;
      break;
    }
  
}

//=======================================================================
//function : WOKUnix_Shell
//purpose  : 
//=======================================================================
WOKUnix_Shell::WOKUnix_Shell(const TCollection_AsciiString& apath,
			       const WOKUnix_ShellMode       amode,
			       const WOKUnix_PopenOutputMode outmode, 
			       const WOKUnix_PopenBufferMode bufmode) 
:  WOKUnix_Process(3, ShellArgv, outmode, bufmode, -1),  mymode(amode), mylocked(Standard_False), myecho(Standard_False)
{
  switch(amode)
    {
    case WOKUnix_SynchronousMode:
      mystatus = new WOKUnix_SyncStatus(apath);
      break;
    case WOKUnix_AsynchronousMode:
      mystatus = new WOKUnix_ASyncStatus(apath);
      break;
    case WOKUnix_DumpScriptMode:
      mystatus = new WOKUnix_DumpScript(apath);
      break;
    }
  
}


//=======================================================================
//function : SetSynchronous
//purpose  : 
//=======================================================================
void WOKUnix_Shell::SetSynchronous()
{
  // si deja en synchrone : ne rien faire
  if(mymode ==  WOKUnix_SynchronousMode) return;
  if(IsLaunched() == Standard_True) 
    {
      // i have to forget what has be done before
      mystatus->Reset(this);

      // le nouveau est un synchrone
      mystatus = new WOKUnix_SyncStatus();
      mystatus->Reset(this);
    }
  mymode = WOKUnix_SynchronousMode;
}

//=======================================================================
//function : SetEcho
//purpose  : 
//=======================================================================
void WOKUnix_Shell::SetEcho() 
{
  myecho = Standard_True;
}

//=======================================================================
//function : UnsetEcho
//purpose  : 
//=======================================================================
void WOKUnix_Shell::UnsetEcho() 
{
  myecho = Standard_False;
}

//=======================================================================
//function : IsEchoed
//purpose  : 
//=======================================================================
Standard_Boolean WOKUnix_Shell::IsEchoed() const
{
  return myecho;
}

//=======================================================================
//function : Echo
//purpose  : 
//=======================================================================
void WOKUnix_Shell::Echo(const Handle(TCollection_HAsciiString)& astr) const
{
  if(myecho)
    {
      WOKTools_Info shellinfo = InfoMsg();
      shellinfo.Init();
      shellinfo.DontPrintHeader();
      shellinfo.DontPrintContext();
      
      shellinfo << "WOKUnix_Shell::Echo" << astr << endm;
    }
}

//=======================================================================
//function : Lock
//purpose  : 
//=======================================================================
void WOKUnix_Shell::Lock()
{
  mylocked = Standard_True;
}

//=======================================================================
//function : UnLock
//purpose  : 
//=======================================================================
void WOKUnix_Shell::UnLock()
{
  mylocked = Standard_False;
}

//=======================================================================
//function : IsLocked
//purpose  : 
//=======================================================================
Standard_Boolean WOKUnix_Shell::IsLocked() const
{ 
  return mylocked;
}

//=======================================================================
//function : LogInFile
//purpose  : 
//=======================================================================
void WOKUnix_Shell::LogInFile(const Handle(WOKUnix_Path)& apath)
{
  if(apath.IsNull()) return;

  if(!apath->Exists())
    {
      apath->CreateFile(Standard_True);
      if(!apath->Exists()) return;
    }

  mylogfile = apath;
}

//=======================================================================
//function : NoLog
//purpose  : 
//=======================================================================
void WOKUnix_Shell::NoLog()
{
  mylogfile.Nullify();
}

//=======================================================================
//function : LogFile
//purpose  : 
//=======================================================================
Handle(WOKUnix_Path) WOKUnix_Shell::LogFile() const
{
  return mylogfile;
}

//=======================================================================
//function : Log
//purpose  : 
//=======================================================================
void WOKUnix_Shell::Log(const Handle(TCollection_HAsciiString)& astr) const
{
  if(!mylogfile.IsNull())
    {
      ofstream logfile(mylogfile->Name()->ToCString(), ios::app);
      if(!logfile.bad())
	{
	  logfile << astr->ToCString();
	  logfile.close();
	}
    }
}

//=======================================================================
//function : SetASynchronous
//purpose  : 
//=======================================================================
void WOKUnix_Shell::SetASynchronous()
{
  // si deja en synchrone : ne rien faire
  if(mymode == WOKUnix_AsynchronousMode) return;
  if(IsLaunched() == Standard_True) 
    {
      // i have to forget what has be done before
      mystatus->Reset(this);

      // le nouveau est un asynchrone
      mystatus = new WOKUnix_ASyncStatus();
      mystatus->Reset(this);
    }
  mymode = WOKUnix_AsynchronousMode;
}

//=======================================================================
//function : SyncAndStatus
//purpose  : 
//=======================================================================
 Standard_Integer WOKUnix_Shell::SyncAndStatus()
{
  WOKUnix_FDSet readfds;
  WOKUnix_Timeval s_timeout;
  WOKUnix_Timeval* p_timeout;
  Standard_Integer selectstatus;
  Standard_Integer fdmax;
  
  WOKUnix_ProcessManager::SetCriticalPid(Pid());

  WOK_TRACE {
    VerboseMsg()("WOK_PROCESS") << "WOKUnix_Shell::SyncAndStatus"
			      << "Entering SyncAndStatus" << endm;
  }

  while(1)
    {
      FD_ZERO(&readfds);

      FD_SET(mystatus->No(), &readfds);

      fdmax = mystatus->No();

      if(Timeout() > 0)
	{
	  s_timeout.tv_sec = Timeout();
	  p_timeout = &s_timeout;
	}
      else
	{
	  p_timeout = NULL;
	}
      

      Select(fdmax, s_timeout, readfds); // process output Select
      
#ifdef WOK_VERBOSE
      if(VerboseMsg()("WOK_PROCESS").IsSet()) {
	Standard_Integer i;

	if(p_timeout) {
	  VerboseMsg()("WOK_PROCESS") << "WOKUnix_Shell::SyncAndStatus"
	    << "Entering select : timeout : " << (const int) s_timeout.tv_sec << "s " << (const int) s_timeout.tv_usec << "ms" << endm;
	} else {
	  VerboseMsg()("WOK_PROCESS") << "WOKUnix_Shell::SyncAndStatus"
	    << "Entering select : infinite wait" << endm;
	}
	

	for(i=0; i<fdmax; i++) {
	  if(FD_ISSET(i,&readfds)) {
	    VerboseMsg()("WOK_PROCESS") << "WOKUnix_Shell::SyncAndStatus" << "FD " << (const int) i << " is setted" << endm;
	  }
	}
      }

#endif

      if((selectstatus=select(fdmax, (WOKUnix_FDSet_CAST) &readfds, NULL, NULL , &s_timeout)) < 0)
	{
	  perror("select");
	  WOKUnix_ProcessManager::SetCriticalPid(-1);
	  return 1;
	}
      
#ifdef WOK_IDLE_TRIGGER
      
      WOKUtils_Trigger idle;

      idle("WOK_DoWhenIdle") << endt;

#endif
      
      if(FD_ISSET(mystatus->No(), &readfds))
	{
	  // le status pipe est en select ---> ca m'interesse avant tout 
	  mystatus->Get();
	  Acquit(selectstatus, readfds); // Process output acquit
	  WOKUnix_ProcessManager::SetCriticalPid(-1);
	  return mystatus->Status();
	}

#ifdef WOK_VERBOSE
      VerboseMsg()("WOK_PROCESS") << "WOKUnix_Shell::SyncAndStatus"
	<< "Aquit output" << endm;
#endif

      Acquit(selectstatus, readfds); // Process output acquit
    }
}

//=======================================================================
//function : Status
//purpose  : 
//=======================================================================
Standard_Integer WOKUnix_Shell::Status() const 
{
  return mystatus->Status();
}

//=======================================================================
//function : Errors
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKUnix_Shell::Errors()
{
  return Output()->Errors();
}

//=======================================================================
//function : ClearOutput
//purpose  : 
//=======================================================================
void WOKUnix_Shell::ClearOutput() 
{
  Output()->Clear();
}

//=======================================================================
//function : Send
//purpose  : 
//=======================================================================
void WOKUnix_Shell::Send(const Handle(TCollection_HAsciiString)& astring)
{
  Log(astring);
  Echo(astring);
  WOKUnix_Process::Send(astring);
  return;
}

//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
Standard_Integer WOKUnix_Shell::Execute(const Handle(TCollection_HAsciiString)& astring)
{
  Log(astring);
  Echo(astring);
  WOKUnix_Process::Send(astring);
  mystatus->EndCmd(this);
  mystatus->Sync(this);
  return mystatus->Status();
}


//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
Standard_Integer WOKUnix_Shell::Execute(const Handle(TColStd_HSequenceOfHAsciiString)& somestrings)
{
  Handle(TCollection_HAsciiString) astring;
  
  for(Standard_Integer i = 1; i <= somestrings->Length(); i++)
    {
      astring = somestrings->Value(i);

      Log(astring);
      Echo(astring);

      WOKUnix_Process::Send(astring);
      mystatus->EndCmd(this);
    }
  mystatus->Sync(this);
  return mystatus->Status();
}

//=======================================================================
//function : SetHost
//purpose  : 
//=======================================================================
void WOKUnix_Shell::SetHost(const Handle(TCollection_HAsciiString)& ahost)
{

  myhost = ahost;
}

//=======================================================================
//function : Host
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKUnix_Shell::Host() const 
{
  return myhost;
}

#endif
