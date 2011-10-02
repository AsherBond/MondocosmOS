// File:	woktools.cxx
// Created:	Tue Aug 13 10:43:29 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>

#include <tcl.h>

#include <OSD.hxx>

#include <WOKTclTools_Interpretor.hxx>
#include <WOKTclTools_Messages.hxx>
#include <WOKTclTools_MsgAPI.hxx>
#include <WOKTclTools_Package.hxx>

#ifdef WNT
# ifdef _DEBUG
extern "C" void _debug_break ( char* );
# endif  // _DEBUG
# define WOKTOOLS_EXPORT __declspec( dllexport )
#else
# define WOKTOOLS_EXPORT
#endif  // WNT

extern "C" int WOKTOOLS_EXPORT Woktools_Init(WOKTclTools_PInterp);

int Woktools_Init(WOKTclTools_PInterp interp)
{
  OSD::SetSignal();                  //==== Armed the signals. =============

  Handle(WOKTclTools_Interpretor)& CurrentInterp = WOKTclTools_Interpretor::Current();

  if(WOKTclTools_Interpretor::Current().IsNull())
    {
      CurrentInterp = new WOKTclTools_Interpretor(interp);
    }

  CurrentInterp->Add("msgsetcmd",      "Set Message handler",        WOKTclTools_MessageCmdSet,           "WOK COMMAND\n");
  CurrentInterp->Add("msgunsetcmd",    "UnSet Message handler",      WOKTclTools_MessageCmdUnSet,         "WOK COMMAND\n");
  CurrentInterp->Add("msgissetcmd",    "Set Message handler",        WOKTclTools_MessageCmdIsSet,         "WOK COMMAND\n");
  CurrentInterp->Add("msgset",         "enable message",             WOKTclTools_MsgAPI::Set,             "WOK COMMAND\n");
  CurrentInterp->Add("msgunset",       "disable message",            WOKTclTools_MsgAPI::UnSet,           "WOK COMMAND\n");
  CurrentInterp->Add("msgisset",       "message status",             WOKTclTools_MsgAPI::IsSet,           "WOK COMMAND\n");
  CurrentInterp->Add("msgsetlong",     "print msg context",          WOKTclTools_MsgAPI::DoPrintContext,  "WOK COMMAND\n");
  CurrentInterp->Add("msgunsetlong",   "dont print msg context",     WOKTclTools_MsgAPI::DontPrintContext,"WOK COMMAND\n");
  CurrentInterp->Add("msgissetlong",   "long format ?",              WOKTclTools_MsgAPI::IsPrintContext,  "WOK COMMAND\n");
  CurrentInterp->Add("msgsetheader",   "print msg head",             WOKTclTools_MsgAPI::DoPrintHeader,   "WOK COMMAND\n");
  CurrentInterp->Add("msgunsetheader", "dont print msg head",        WOKTclTools_MsgAPI::DontPrintHeader, "WOK COMMAND\n");
  CurrentInterp->Add("msgissetheader", "print header ???",           WOKTclTools_MsgAPI::IsPrintHeader,   "WOK COMMAND\n");
  CurrentInterp->Add("msgprint",       "print a msg",                WOKTclTools_MsgAPI::PrintMessage,    "WOK COMMAND\n");
  CurrentInterp->Add("msginfo",        "info about message handler", WOKTclTools_MsgAPI::MessageInfo,     "WOK COMMAND\n");

  WOKTclTools_Package woktools(CurrentInterp, "woktools", "2.0");

  woktools.Provide();
  
  return TCL_OK;
}


