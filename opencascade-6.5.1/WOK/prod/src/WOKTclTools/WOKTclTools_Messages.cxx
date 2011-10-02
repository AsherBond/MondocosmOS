// File:	WOKTCL_Messages.cxx
// Created:	Wed Oct 18 13:08:16 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <tcl.h>

#include <WOKTclTools_Messages.hxx>
#include <WOKTclTools_Interpretor.hxx>

#include <WOKTclTools_MsgAPI.hxx>

#include <TCollection_HAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <WOKTools_Messages.hxx>
#include <WOKTools_Options.hxx>

WOKTools_Message& TclEndMsgHandler(WOKTools_Message& amsgq, const Standard_Boolean newline)
{
  if(!amsgq.Message().IsNull())
    {
      Standard_CString astr;
      astr = amsgq.ToPrint();

      Handle(WOKTclTools_Interpretor)&  theinterp =  WOKTclTools_Interpretor::Current();

      if(!theinterp.IsNull())
	{
	  theinterp->TreatMessage(newline, amsgq.Code(),  astr);
	}
    }
  return amsgq;
}

void WOKTclTools_Usage_MessageCmdSet(char *cmd)
{
  cerr << "usage: " << cmd << " <atclprocname> [<args>]\n";
}

Standard_Integer WOKTclTools_MessageCmdSet(const Handle(WOKTclTools_Interpretor)& aninterp,  Standard_Integer argc,  WOKTools_ArgTable argv)
{
  WOKTools_Options opts(argc, argv, "", WOKTclTools_Usage_MessageCmdSet);

  while(opts.More())
    {
      switch(opts.Option())
	{
	default:
	  break;
	}
      opts.Next();
    }
  
  if(opts.Failed()) return 1;

  switch(opts.Arguments()->Length())
    {
    case 1:
      aninterp->SetEndMessageProc(opts.Arguments()->Value(1)->ToCString());
      break;
    case 2:
      aninterp->SetEndMessageProc(opts.Arguments()->Value(1)->ToCString());
      aninterp->SetEndMessageArgs(opts.Arguments()->Value(2)->ToCString());
      break;
    default:
      WOKTclTools_Usage_MessageCmdSet(argv[0]);
      return 1;
    }

  WOKTools_MsgHandler ahandler = TclEndMsgHandler;

  InfoMsg().SetEndMsgHandler(ahandler);
  WarningMsg().SetEndMsgHandler(ahandler);
  ErrorMsg().SetEndMsgHandler(ahandler);
  VerboseMsg().SetEndMsgHandler(ahandler);
  return 0;
}

Standard_Integer WOKTclTools_MessageCmdUnSet(const Handle(WOKTclTools_Interpretor)& aninterp,  Standard_Integer ,  WOKTools_ArgTable )
{
  aninterp->Reset();
  aninterp->UnSetEndMessageProc();
  aninterp->UnSetEndMessageArgs();
  return 0;
}

Standard_Integer WOKTclTools_MessageCmdIsSet(const Handle(WOKTclTools_Interpretor)& aninterp,  Standard_Integer ,  WOKTools_ArgTable )
{
  aninterp->Reset();
  if(aninterp->EndMessageProc() != NULL)
    {
      aninterp->Append(1);
    }
  else
    {
      aninterp->Append(0);
    }
  return 0;
}
