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

#ifdef HAVE_SYS_WAIT_H
# include <sys/wait.h>
#endif

#ifdef HAVE_SIGNAL_H
# include <signal.h>
#endif

#include <errno.h>
#include <stdio.h>

#include <Standard_ProgramError.hxx>

#include <WOKUnix_Process.ixx>
#include <WOKUnix_OutErrOutput.hxx>
#include <WOKUnix_MixedOutput.hxx>
#include <WOKUnix_Signal.hxx>
#include <WOKUnix_FDescr.hxx>
#include <WOKUnix_ProcessManager.hxx>
#include <WOKTools_Messages.hxx>
#include <WOKTools_ArgTable.hxx>


//=======================================================================
//function : WOKUnix_Process
//purpose  : constructs a process
//=======================================================================
 WOKUnix_Process::WOKUnix_Process(const Standard_Integer argcount, 
				    const WOKTools_ArgTable& cmdline, 
				    const WOKUnix_PopenOutputMode anoutputmode, 
				    const WOKUnix_PopenBufferMode abuffermode, 
				    const Standard_Integer atimeout)
{
  Standard_Integer i = 0;

  myargv = new char * [argcount];

  while(i < argcount)
    {
      myargv[i] =cmdline [i];
      i++;
    }

  mymode       = anoutputmode;
  mybuffermode = abuffermode;
  mylaunched   = Standard_False;
  mytimeout    = atimeout;

  WOKUnix_ProcessManager::AddProcess(this);
}

//=======================================================================
//function : SetCommand
//purpose  : 
//=======================================================================
void WOKUnix_Process::SetCommand(const Standard_Integer argcount, 
			          const WOKTools_ArgTable& cmdline)
{
  Standard_Integer i = 0;

  myargv = new char * [argcount];

  while(i < argcount)
    {
      if(cmdline [i])
	myargv[i] = strdup(cmdline [i]);
      else
	myargv[i] = NULL;
      i++;
    }
}

//=======================================================================
//function : SetTimeout
//purpose  : 
//=======================================================================
void WOKUnix_Process::SetTimeout(const Standard_Integer atimeout)
{
  mytimeout = atimeout;
}

//=======================================================================
//function : Launch
//purpose  : launches subprocess (uses fork+exec)
//=======================================================================
void WOKUnix_Process::Launch()  
{
  if (mylaunched) return;
  else
    {
      WOKUnix_FDescr Pin;  WOKUnix_FDescr Sin;
      WOKUnix_FDescr Pout; WOKUnix_FDescr Sout;
      WOKUnix_FDescr Perr; WOKUnix_FDescr Serr;

      /* ouverture des pipes */
      WOKUnix_FDescr::Pipe(Pin, Sin);
      WOKUnix_FDescr::Pipe(Sout, Pout);

      switch(mymode)
	{
	case WOKUnix_POPEN_MIX_OUT_ERR: 
	  break;
	case WOKUnix_POPEN_OUT_ERR:
	  WOKUnix_FDescr::Pipe(Serr, Perr);
	  break;
	default:
	  Standard_ProgramError::Raise("WOKUnix_Process::Launch : Unknown mode");
	}
  
      WOKUnix_FDescr Stdin(0);  WOKUnix_FDescr Stdout(1);  WOKUnix_FDescr Stderr(2);
  
      /* on cree le processus fils */
      mychildpid = fork();
  
      if(mychildpid < 0)
	{
	  Standard_ProgramError::Raise("WOKUnix_Process::Launch : Could not fork");
	}
  
      switch(mychildpid)
	{
	case 0:
	  /* dans le fils */  
	  /* fermeture des pipes inutiles */
	  /* redirection de stdin, stdout, stderr */
	
	  /* redirection stdin */
	  Stdin.Close();Sin.Dup();  Stdin = WOKUnix_FDescr(0);  Sin.Close(); Pin.Close();
	  Stdin.SetUnBuffered();
	  /* redirection stdout */
	  Stdout.Close();Sout.Dup();Stdout = WOKUnix_FDescr(1);  Sout.Close(); Pout.Close();
	  Stdout.SetUnBuffered();
	  switch(mymode)
	    {
	    case  WOKUnix_POPEN_MIX_OUT_ERR: 
	      /* redirection stderr */
	      Stderr.Close(); Stdout.Dup();
	      break;
	    case  WOKUnix_POPEN_OUT_ERR: 
	      /* redirection stderr */
	      Stderr.Close();	  Sout.Dup(); Stdout = WOKUnix_FDescr(2);Sout.Close();Pout.Close();
	      Stderr.SetUnBuffered();
	      break;
	    }
      
	  /* exec de la commande */
	  if(execvp(myargv[0], (char **) myargv)) 
	    {
	      perror("execvp");
	      exit(1);
	    }
	  break;
	default:
	  /* dans le pere */
      
	  /* fermeture des pipes inutiles */
	  /* ouverture des fstream su le processus fils */
	  Sin.Close();  
	  Pin.SetUnBuffered();
	  Pin.SetNonBlock();
	  myinput=Pin;
      
	  Sout.Close(); Pout.SetUnBuffered();
	  switch(mymode)
	    {
	    case  WOKUnix_POPEN_MIX_OUT_ERR: 
	      myoutput = new WOKUnix_MixedOutput(Pout, mybuffermode);
	      break;
	    case  WOKUnix_POPEN_OUT_ERR: 
	      Serr.Close(); Perr.SetUnBuffered();
	      myoutput = new WOKUnix_OutErrOutput(Pout, Perr, mybuffermode);
	      break;
	    }
	  break;
	}

      WOK_TRACE {
	VerboseMsg()("WOK_PROCESS") << "WOKUnix_Process::Launch" << "Process " << mychildpid << " launched" << endm;
      }

      mylaunched = Standard_True;
      return;
    }
}

//=======================================================================
//function : IsLaunched
//purpose  : 
//=======================================================================
Standard_Boolean WOKUnix_Process::IsLaunched() const
{
  return mylaunched;
}

//=======================================================================
//function : Pid
//purpose  : 
//=======================================================================
Standard_Integer WOKUnix_Process::Pid() const
{
  return mychildpid;
}

//=======================================================================
//function : Output
//purpose  : 
//=======================================================================
Handle(WOKUnix_ProcessOutput)& WOKUnix_Process::Output() 
{
  return myoutput;
}

//=======================================================================
//function : Timeout
//purpose  : 
//=======================================================================
Standard_Integer WOKUnix_Process::Timeout() const
{
  return mytimeout;
}


//=======================================================================
//function : Select
//purpose  : select() on the output of subprocess
//=======================================================================
void WOKUnix_Process::Select(Standard_Integer& afdmax, WOKUnix_Timeval& atimeout,  WOKUnix_FDSet& aset) const 
{
  myoutput->Select(afdmax, atimeout, aset);
}

//=======================================================================
//function : Acquit
//purpose  : select() acquitment
//=======================================================================
void WOKUnix_Process::Acquit(const Standard_Integer selectstatus, const WOKUnix_FDSet& aset) const 
{
  myoutput->Acquit(selectstatus, aset);
}

//=======================================================================
//function : SelectAndAcquit
//purpose  : selects and acquit
//=======================================================================
void WOKUnix_Process::SelectAndAcquit() const 
{
  WOKUnix_FDSet readfds;
  WOKUnix_Timeval s_timeout;
  Standard_Integer selectstatus;
  Standard_Integer fdmax = 0;


  FD_ZERO(&readfds);

  s_timeout.tv_sec = mytimeout;

  myoutput->Select(fdmax, s_timeout, readfds);
  
  if((selectstatus=select(fdmax, (WOKUnix_FDSet_CAST)&readfds, NULL, NULL , &s_timeout)) < 0)
    {
      perror("select");
      return ;
    }
  myoutput->Acquit(selectstatus, readfds);
  return ;
}

//=======================================================================
//function : Send
//purpose  : 
//=======================================================================
void WOKUnix_Process::Send(const Handle(TCollection_HAsciiString)& astring) 
{
  Standard_Integer nbwritten, nbwrite;
  Standard_CString ptr = astring->ToCString();

  WOK_TRACE {
    VerboseMsg()("WOK_PROCESS") << "WOKUnix_Process::Send" 
			      << "Writing : " << astring->ToCString() << "to file id : " << myinput.FileNo() << endm;
  }

  if(!IsLaunched())
    {
      ErrorMsg() << "WOKUnix_Process::Send" << "Trying to perform send to dead or unlaunched process" << endm;
      Standard_ProgramError::Raise("WOKUnix_Process::Send");
    }

  nbwritten=0;
  
  while(nbwritten!=astring->Length())
    {
      while((nbwrite = write(myinput.FileNo(), ptr, astring->Length()-nbwritten)) == -1)
	{
	  // write failed ... is 
	  if(errno == EAGAIN)
	    {
	      // le write aurait ete bloquant : Acquit 
	      // if(Select(shell, NULL)) return 1;
	      // il faut laisser le temps au temps 
	      
	      WOK_TRACE {
		VerboseMsg()("WOK_PROCESS") << "WOKUnix_Process::Send" 
					  << "Write failed : Acquit output" << endm;
	      }

	      SelectAndAcquit();
	      sleep(1);
	    }
	  else
	    {
	      // write failed 
	      perror("shell input:");
	      OSD_OSDError::Raise("Process::Send : Write to process failed\n");
	    }
	}
      ptr += nbwrite;
      nbwritten += nbwrite;
    }
  return;
}

//=======================================================================
//function : Kill
//purpose  : 
//=======================================================================
void WOKUnix_Process::Kill() 
{
  if(mylaunched == Standard_True)
    {
      kill(mychildpid, SIGTERM);
      
      if(myinput.FileNo() >= 0) myinput.Close();
      myoutput->Close();
      
      mylaunched = Standard_False;
    }
}

//=======================================================================
//function : Destroy
//purpose  : 
//=======================================================================
void WOKUnix_Process::Destroy()
{
 Kill();
 delete [] myargv;
}
#endif
