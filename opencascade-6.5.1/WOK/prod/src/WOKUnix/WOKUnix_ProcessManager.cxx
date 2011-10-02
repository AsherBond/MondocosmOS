#ifndef WNT

#include <sys/wait.h>

#include <OSD_SIGINT.hxx>

#include <WOKUnix_Signal.hxx>

#include <WOKUnix_ProcessManager.ixx>

#include <Standard_ProgramError.hxx>

#include <WOKTools_Messages.hxx>

static Standard_Integer CriticPid = 0;

//=======================================================================
//function : Arm
//purpose  : 
//=======================================================================
void WOKUnix_ProcessManager::Arm()
{
  WOKUnix_Signal::Arm(WOKUnix_SIGINT,   (WOKUnix_SigHandler) WOKUnix_ProcessManager::InteruptHandler);
  WOKUnix_Signal::Arm(WOKUnix_SIGCHILD, (WOKUnix_SigHandler) WOKUnix_ProcessManager::ChildDeathHandler);
  WOKUnix_Signal::Arm(WOKUnix_SIGPIPE,  (WOKUnix_SigHandler) WOKUnix_ProcessManager::PipeHandler);
}


//=======================================================================
//function : UnArm
//purpose  : 
//=======================================================================
void WOKUnix_ProcessManager::UnArm()
{
  WOKUnix_Signal::Arm(WOKUnix_SIGINT,   (WOKUnix_SigHandler) NULL);
  WOKUnix_Signal::Arm(WOKUnix_SIGCHILD, (WOKUnix_SigHandler) NULL);
  WOKUnix_Signal::Arm(WOKUnix_SIGPIPE,  (WOKUnix_SigHandler) NULL);
}

//=======================================================================
//function : Processes
//purpose  : 
//=======================================================================
WOKUnix_SequenceOfProcess  &WOKUnix_ProcessManager::Processes()
{
  static WOKUnix_SequenceOfProcess processes;
  
  return processes;
}

//=======================================================================
//function : InteruptHandler
//purpose  : 
//=======================================================================
void WOKUnix_ProcessManager::InteruptHandler()
{
#ifdef WOK_VERBOSE
  VerboseMsg()("WOK_PROCESS") << "WOKUnix_ProcessManager::InteruptHandler" 
			    << "Interupt Signal Launched !!" << endm;
#endif

  WOKUnix_ProcessManager::KillAll();
  OSD_SIGINT::Raise("SIGINT 'interrupt' detected.");
  return;
}

//=======================================================================
//function : ChildDeathHandler
//purpose  : 
//=======================================================================
void WOKUnix_ProcessManager::ChildDeathHandler()
{
  int	pid, status;

  pid	= wait(&status);

  if(pid == CriticPid)
    {
      ErrorMsg() << "WOKUnix_ProcessManager::ChildDeathHandler"
	       << "Child " << pid << " died" << endm;
      Standard_ProgramError::Raise("unexpected child shell death");
    }

  for(Standard_Integer ind = 1; ind <= Processes().Length(); ind ++)
    {
      if(Processes().Value(ind)->Pid() == pid)
	{
#ifdef WOK_VERBOSE
	  VerboseMsg()("WOK_PROCESS") << "WOKUnix_ProcessManager::ChildDeathHandler"
				    << "Process " << pid << " died" << endm;
#endif
	  Processes().Value(ind)->Kill();
	  Processes().Remove(ind);
	  return;
	}
    }
  return;
}

//=======================================================================
//function : PipeHandler
//purpose  : 
//=======================================================================
void WOKUnix_ProcessManager::PipeHandler()
{
#ifdef WOK_VERBOSE
  VerboseMsg()("WOK_PROCESS") << "WOKUnix_ProcessManager::PipeHandler" << "SIGPIPE received" << endm;
#endif
  return;
}

//=======================================================================
//function : KillAll
//purpose  : 
//=======================================================================
void WOKUnix_ProcessManager::KillAll()
{
#ifdef WOK_VERBOSE
  VerboseMsg()("WOK_PROCESS") << "WOKUnix_ProcessManager::PipeHandler" << "Killing all sub proccesses" << endm;
#endif

  for(Standard_Integer ind = 1; ind <= Processes().Length(); ind ++)
    {
#ifdef WOK_VERBOSE
      VerboseMsg()("WOK_PROCESS") << "WOKUnix_ProcessManager::KillAll"
				<< "Process " << Processes().Value(ind)->Pid() << " requested to die (interrupt)" << endm;
#endif
      Processes().Value(ind)->Kill();
    }
  Processes().Clear();
}

//=======================================================================
//function : SetCriticPid
//purpose  : 
//=======================================================================
void WOKUnix_ProcessManager::SetCriticalPid(const Standard_Integer pid)
{
  CriticPid = pid;
}

//=======================================================================
//function : AddProcess
//purpose  : 
//=======================================================================
void WOKUnix_ProcessManager::AddProcess(const Handle(WOKUnix_Process)& aprocess)
{
  Processes().Append(aprocess);
}

//=======================================================================
//function : RemoveProcess
//purpose  : 
//=======================================================================
void WOKUnix_ProcessManager::RemoveProcess(const Handle(WOKUnix_Process)& aprocess)
{
  
  for(Standard_Integer ind = 1; ind <= Processes().Length(); ind ++)
    {
      if(Processes().Value(ind)->Pid() == aprocess->Pid())
	{
	  Processes().Remove(ind);
	  break;
	}
    }
}

//=======================================================================
//function : WaitProcess
//purpose  : 
//=======================================================================
void WOKUnix_ProcessManager::WaitProcess(const Handle(WOKUnix_Process)& aprocess)
{
  Standard_Integer status, pid;
  // ignore SIGCHILD for a while
  WOKUnix_Signal::Arm(WOKUnix_SIGCHILD, (WOKUnix_SigHandler)NULL);

  
#ifdef WOK_VERBOSE
  VerboseMsg()("WOK_PROCESS") << "WOKUnix_ProcessManager::WaitProcess"
			    << "Waiting for process " << aprocess->Pid() << " to die" << endm;
#endif
 
  pid = waitpid(aprocess->Pid(), &status, 0);

#ifdef WOK_VERBOSE
  VerboseMsg()("WOK_PROCESS") << "WOKUnix_ProcessManager::WaitProcess"
			    << "Process " << aprocess->Pid() << " died" << endm;
#endif

  if(pid == aprocess->Pid()) 
    {
      WOKUnix_ProcessManager::RemoveProcess(aprocess);
    }

  WOKUnix_Signal::Arm(WOKUnix_SIGCHILD, (WOKUnix_SigHandler) WOKUnix_ProcessManager::ChildDeathHandler);
  return;
}
#endif
