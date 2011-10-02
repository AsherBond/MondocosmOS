#ifndef WNT
// File:	WOKUnix_RemoteShell.cxx
// Created:	Mon Nov  6 14:55:55 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

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
 
#include <Standard_ProgramError.hxx>
#include <WOKTools_Messages.hxx>
#include <OSD_Protection.hxx>

#include <WOKUnix_ShellStatus.hxx>
#include <WOKUnix_ProcessManager.hxx>
#include <TCollection_HAsciiString.hxx>
#include <TCollection_AsciiString.hxx>

#include <WOKUnix_RemoteShell.ixx>


//=======================================================================
//function : WOKUnix_RemoteShell
//purpose  : 
//=======================================================================
 WOKUnix_RemoteShell::WOKUnix_RemoteShell(const Handle(TCollection_HAsciiString)& ahost, 
					    const TCollection_AsciiString & apath,
					    const WOKUnix_ShellMode amode, 
					    const WOKUnix_PopenOutputMode outmode, 
					    const WOKUnix_PopenBufferMode bufmode) 
: WOKUnix_Shell(apath, amode, outmode, bufmode)
{ 

  char *rshellargv[]={"rsh" , (char*)ahost->ToCString(), "exec","/bin/csh", "-fs", NULL};
  SetCommand(6,rshellargv);
}

//=======================================================================
//function : SyncAndStatus
//purpose  : 
//=======================================================================
 Standard_Integer WOKUnix_RemoteShell::SyncAndStatus()
{
  WOKUnix_FDSet readfds;
  WOKUnix_Timeval s_timeout;
  Standard_Integer selectstatus;
  Standard_Integer fdmax;


  if(!IsLaunched())
    {
      ErrorMsg() << "WOKUnix_RemoteShell::SyncAndStatus()" << "Trying to perform sync to dead or unlaunched process" << endm;
      Standard_ProgramError::Raise("WOKUnix_RemoteShell::SyncAndStatus()");
    }

  // beginning of the critical section
  WOKUnix_ProcessManager::SetCriticalPid(Pid());

  while(1)
    {
      FD_ZERO(&readfds);
      
      //FD_SET(mystatus->No(), &readfds);

      fdmax = 0;

      // wait a moment ....
      Select(fdmax, s_timeout, readfds);

      
      s_timeout.tv_sec  = 0;
      s_timeout.tv_usec = 100000;

      if((selectstatus=select(fdmax, (WOKUnix_FDSet_CAST)&readfds, NULL, NULL ,&s_timeout ) < 0))
	{
	  perror("select");
	  WOKUnix_ProcessManager::SetCriticalPid(-1);
	  return 1;
	}

      mystatus->StatusFile().Close();
      mystatus->StatusFile().Open(OSD_ReadWrite, OSD_Protection());
      mystatus->StatusFile().Flush();

      if(mystatus->StatusFile().GetSize()) // le status file n'est pas vide
	{
	  //mystatus->StatusFile().Flush();

	  //lire le status file
	  mystatus->GetRemote();

	  Acquit(selectstatus, readfds); // Process output acquit

	  // vider le status file
	  mystatus->StatusFile().EmptyAndOpen();
	  
	  //end of the critical section
	  WOKUnix_ProcessManager::SetCriticalPid(-1);
	  return mystatus->Status();
	}

      Acquit(selectstatus, readfds); // Process output acquit

    }
}

//=======================================================================
//function : SetUser
//purpose  : 
//=======================================================================
void WOKUnix_RemoteShell::SetUser(const Handle(TCollection_HAsciiString)& auser)
{
  myuser = auser;
}

//=======================================================================
//function : User
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKUnix_RemoteShell::User() const 
{
  return myuser;
}

//=======================================================================
//function : SetPassword
//purpose  : 
//=======================================================================
void WOKUnix_RemoteShell::SetPassword(const Handle(TCollection_HAsciiString)& apassword)
{
  mypassword = apassword;
}

//=======================================================================
//function : Password
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKUnix_RemoteShell::Password() const 
{
  return mypassword;
}

#endif
