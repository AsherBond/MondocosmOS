// File:	WOKTclTools_MsgAPI.cxx
// Created:	Tue Nov 28 11:56:01 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <stdlib.h>

#include <TCollection_HAsciiString.hxx>

#include <OSD_Environment.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Return.hxx>
#include <WOKTools_Options.hxx>
#include <WOKTools_Messages.hxx>
#include <WOKTools_MsgStreamPtr.hxx>

#include <WOKTclTools_Interpretor.hxx>

#include <WOKTclTools_MsgAPI.ixx>

//=======================================================================
void WOKTclTools_Message_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " [-i|-w|-e|-v|-V Class]" << endl;
  return ;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Set
//purpose  : 
//=======================================================================
Standard_Integer WOKTclTools_MsgAPI::Set(const Standard_Integer argc, const WOKTools_ArgTable& argv, WOKTools_Return &retval)
{
  WOKTools_Options opts(argc,argv, "hiwevV:L:", WOKTclTools_Message_Usage, "h");
  Standard_Boolean info      = Standard_False;
  Standard_Boolean warning   = Standard_False;
  Standard_Boolean error     = Standard_False;
  Standard_Boolean verbose   = Standard_False;
  Standard_Boolean Verbose   = Standard_False;
  Handle(TCollection_HAsciiString) logfile;
  Handle(TCollection_HAsciiString) VClass;

  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'i':
	  info = Standard_True;
	  break;
	case 'w':
	  warning = Standard_True;
	  break;
	case 'e':
	  error = Standard_True;
	  break;
	case 'v':
	  verbose = Standard_True;
	  break;
	case 'L':
	  logfile = opts.OptionArgument();
	  break;
	case 'V':
	  Verbose = Standard_True;
	  VClass  = opts.OptionArgument();
	  break;
	}
      opts.Next();
    }

  if(opts.Failed()) return 1;

  if(info)    {InfoMsg().Set();}
  if(warning) {WarningMsg().Set();}
  if(error)   {ErrorMsg().Set();}
  if(verbose) {VerboseMsg().Set();}
  if(Verbose) 
    {
      VerboseMsg().Set();

      TCollection_AsciiString astr(VClass->String());

      astr.AssignCat("=true");
      putenv((char*)astr.ToCString());
      //OSD_Environment anenv(VClass->String());
      //anenv.SetValue(TCollection_AsciiString("true"));
      
      retval.AddSetEnvironment(VClass, new TCollection_HAsciiString("true"));
    }
  
  if(!logfile.IsNull())
    {
      if(info||warning||error||verbose||Verbose)
	{
	  
	}
      if(info)    InfoMsg().LogToFile(logfile);
      if(warning) WarningMsg().LogToFile(logfile);
      if(error)   ErrorMsg().LogToFile(logfile);
      if(verbose||Verbose) VerboseMsg().LogToFile(logfile);
    }
  
  return 0;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : UnSet
//purpose  : 
//=======================================================================
Standard_Integer WOKTclTools_MsgAPI::UnSet(const Standard_Integer argc, const WOKTools_ArgTable& argv, WOKTools_Return &retval)
{
  WOKTools_Options opts(argc,argv, "hiwevV:L", WOKTclTools_Message_Usage, "h");
  Standard_Boolean info    = Standard_False;
  Standard_Boolean warning = Standard_False;
  Standard_Boolean error   = Standard_False;
  Standard_Boolean verbose = Standard_False;
  Standard_Boolean Verbose = Standard_False;
  Standard_Boolean endlogging = Standard_False;
  Handle(TCollection_HAsciiString) VClass;

  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'i':
	  info = Standard_True;
	  break;
	case 'w':
	  warning = Standard_True;
	  break;
	case 'e':
	  error = Standard_True;
	  break;
	case 'v':
	  verbose = Standard_True;
	  break;
	case 'V':
	  Verbose = Standard_True;
	  VClass  = opts.OptionArgument();
	  break;
	case 'L':
	  endlogging = Standard_True;
	}
      opts.Next();
    }

  if(opts.Failed()) return 1;
  if(info)    {InfoMsg().UnSet(); }
  if(warning) {WarningMsg().UnSet();}
  if(error)   {ErrorMsg().UnSet();}
  if(verbose) {VerboseMsg().UnSet();}
  if(Verbose) 
    {
      TCollection_AsciiString astr(VClass->ToCString());
      astr.AssignCat("=");
      putenv((char*)astr.ToCString());
	
      retval.AddUnSetEnvironment(VClass);
    }

  if(endlogging)
  {
    if(info)              {InfoMsg().EndLogging(); }
    if(warning)           {WarningMsg().EndLogging();}
    if(error)             {ErrorMsg().EndLogging();}
    if(verbose||Verbose)  {VerboseMsg().EndLogging();}
  }
  return 0;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : IsSet
//purpose  : 
//=======================================================================
Standard_Integer WOKTclTools_MsgAPI::IsSet(const Standard_Integer argc, const WOKTools_ArgTable& argv, WOKTools_Return &retval)
{
  WOKTools_Options opts(argc,argv, "hiwev", WOKTclTools_Message_Usage, "hiwev");
  Standard_Boolean info    = Standard_False;
  Standard_Boolean warning = Standard_False;
  Standard_Boolean error   = Standard_False;
  Standard_Boolean verbose = Standard_False;

  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'i':
	  info = Standard_True;
	  break;
	case 'w':
	  warning = Standard_True;
	  break;
	case 'e':
	  error = Standard_True;
	  break;
	case 'v':
	  verbose = Standard_True;
	  break;
	}
      opts.Next();
    }

  if(opts.Failed()) return 1;

  if(info)    {retval.AddBooleanValue(InfoMsg().IsSet());    return 0;}
  if(warning) {retval.AddBooleanValue(WarningMsg().IsSet()); return 0;}
  if(error)   {retval.AddBooleanValue(ErrorMsg().IsSet());   return 0;}
  if(verbose) {retval.AddBooleanValue(VerboseMsg().IsSet()); return 0;}
  return 0;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : DoPrintContext
//purpose  : 
//=======================================================================
Standard_Integer WOKTclTools_MsgAPI::DoPrintContext(const Standard_Integer argc, const WOKTools_ArgTable& argv, WOKTools_Return &retval)
{
  WOKTools_Options opts(argc,argv, "hiwev", WOKTclTools_Message_Usage, "h");
  Standard_Boolean info    = Standard_False;
  Standard_Boolean warning = Standard_False;
  Standard_Boolean error   = Standard_False;
  Standard_Boolean verbose = Standard_False;

  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'i':
	  info = Standard_True;
	  break;
	case 'w':
	  warning = Standard_True;
	  break;
	case 'e':
	  error = Standard_True;
	  break;
	case 'v':
	  verbose = Standard_True;
	  break;
	}
      opts.Next();
    }

  if(opts.Failed()) return 1;

  if(info)    {InfoMsg().DoPrintContext();    return 0;}
  if(warning) {WarningMsg().DoPrintContext(); return 0;}
  if(error)   {ErrorMsg().DoPrintContext();   return 0;}
  if(verbose) {VerboseMsg().DoPrintContext(); return 0;}
  return 0;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : DontPrintContext
//purpose  : 
//=======================================================================
Standard_Integer WOKTclTools_MsgAPI::DontPrintContext(const Standard_Integer argc, const WOKTools_ArgTable& argv, WOKTools_Return &retval)
{
  WOKTools_Options opts(argc,argv, "hiwev", WOKTclTools_Message_Usage, "h");
  Standard_Boolean info    = Standard_False;
  Standard_Boolean warning = Standard_False;
  Standard_Boolean error   = Standard_False;
  Standard_Boolean verbose = Standard_False;

  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'i':
	  info = Standard_True;
	  break;
	case 'w':
	  warning = Standard_True;
	  break;
	case 'e':
	  error = Standard_True;
	  break;
	case 'v':
	  verbose = Standard_True;
	  break;
	}
      opts.Next();
    }

  if(opts.Failed()) return 1;

  if(info)    {InfoMsg().DontPrintContext();}
  if(warning) {WarningMsg().DontPrintContext();}
  if(error)   {ErrorMsg().DontPrintContext();}
  if(verbose) {VerboseMsg().DontPrintContext();}
  return 0;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : IsPrintContext
//purpose  : 
//=======================================================================
Standard_Integer WOKTclTools_MsgAPI::IsPrintContext(const Standard_Integer argc, const WOKTools_ArgTable& argv, WOKTools_Return &retval)
{
  WOKTools_Options opts(argc,argv, "hiwev", WOKTclTools_Message_Usage, "hiwev");
  Standard_Boolean info    = Standard_False;
  Standard_Boolean warning = Standard_False;
  Standard_Boolean error   = Standard_False;
  Standard_Boolean verbose = Standard_False;

  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'i':
	  info = Standard_True;
	  break;
	case 'w':
	  warning = Standard_True;
	  break;
	case 'e':
	  error = Standard_True;
	  break;
	case 'v':
	  verbose = Standard_True;
	  break;
	}
      opts.Next();
    }

  if(opts.Failed()) return 1;

  if(info)    {retval.AddBooleanValue(InfoMsg().PrintContext());    return 0;}
  if(warning) {retval.AddBooleanValue(WarningMsg().PrintContext()); return 0;}
  if(error)   {retval.AddBooleanValue(ErrorMsg().PrintContext());   return 0;}
  if(verbose) {retval.AddBooleanValue(VerboseMsg().PrintContext()); return 0;}
  return 0;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : DoPrintHeader
//purpose  : 
//=======================================================================
Standard_Integer WOKTclTools_MsgAPI::DoPrintHeader(const Standard_Integer argc, const WOKTools_ArgTable& argv, WOKTools_Return &retval)
{
  WOKTools_Options opts(argc,argv, "hiwev", WOKTclTools_Message_Usage, "h");
  Standard_Boolean info    = Standard_False;
  Standard_Boolean warning = Standard_False;
  Standard_Boolean error   = Standard_False;
  Standard_Boolean verbose = Standard_False;

  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'i':
	  info = Standard_True;
	  break;
	case 'w':
	  warning = Standard_True;
	  break;
	case 'e':
	  error = Standard_True;
	  break;
	case 'v':
	  verbose = Standard_True;
	  break;
	}
      opts.Next();
    }

  if(opts.Failed()) return 1;

  if(info)    {InfoMsg().DoPrintHeader();    return 0;}
  if(warning) {WarningMsg().DoPrintHeader(); return 0;}
  if(error)   {ErrorMsg().DoPrintHeader();   return 0;}
  if(verbose) {VerboseMsg().DoPrintHeader(); return 0;}
  return 0;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : DontPrintHeader
//purpose  : 
//=======================================================================
Standard_Integer WOKTclTools_MsgAPI::DontPrintHeader(const Standard_Integer argc, const WOKTools_ArgTable& argv, WOKTools_Return &retval)
{
  WOKTools_Options opts(argc,argv, "hiwev", WOKTclTools_Message_Usage, "h");
  Standard_Boolean info    = Standard_False;
  Standard_Boolean warning = Standard_False;
  Standard_Boolean error   = Standard_False;
  Standard_Boolean verbose = Standard_False;

  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'i':
	  info = Standard_True;
	  break;
	case 'w':
	  warning = Standard_True;
	  break;
	case 'e':
	  error = Standard_True;
	  break;
	case 'v':
	  verbose = Standard_True;
	  break;
	}
      opts.Next();
    }

  if(opts.Failed()) return 1;

  if(info)    {InfoMsg().DontPrintHeader();}
  if(warning) {WarningMsg().DontPrintHeader();}
  if(error)   {ErrorMsg().DontPrintHeader();}
  if(verbose) {VerboseMsg().DontPrintHeader();}
  return 0;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : IsPrintHeader
//purpose  : 
//=======================================================================
Standard_Integer WOKTclTools_MsgAPI::IsPrintHeader(const Standard_Integer argc, const WOKTools_ArgTable& argv, WOKTools_Return &retval)
{
  WOKTools_Options opts(argc,argv, "hiwev", WOKTclTools_Message_Usage, "hiwev");
  Standard_Boolean info    = Standard_False;
  Standard_Boolean warning = Standard_False;
  Standard_Boolean error   = Standard_False;
  Standard_Boolean verbose = Standard_False;

  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'i':
	  info = Standard_True;
	  break;
	case 'w':
	  warning = Standard_True;
	  break;
	case 'e':
	  error = Standard_True;
	  break;
	case 'v':
	  verbose = Standard_True;
	  break;
	}
      opts.Next();
    }

  if(opts.Failed()) return 1;

  if(info)    {retval.AddBooleanValue(InfoMsg().PrintHeader());    return 0;}
  if(warning) {retval.AddBooleanValue(WarningMsg().PrintHeader()); return 0;}
  if(error)   {retval.AddBooleanValue(ErrorMsg().PrintHeader());   return 0;}
  if(verbose) {retval.AddBooleanValue(VerboseMsg().PrintContext()); return 0;}
  return 0;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : PrintMessage
//purpose  : 
//=======================================================================
Standard_Integer WOKTclTools_MsgAPI::PrintMessage(const Standard_Integer argc, const WOKTools_ArgTable& argv, WOKTools_Return &)
{
  WOKTools_Options opts(argc,argv, "hniwevc:V:", WOKTclTools_Message_Usage, "hiwev");

  Standard_Boolean info    = Standard_False;
  Standard_Boolean warning = Standard_False;
  Standard_Boolean error   = Standard_False;
  Standard_Boolean verbose = Standard_False;
  Standard_Boolean nonl    = Standard_False;
  Handle(TCollection_HAsciiString) context;
  Handle(TCollection_HAsciiString) Verbose;

  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'i':
	  info = Standard_True;
	  break;
	case 'w':
	  warning = Standard_True;
	  break;
	case 'e':
	  error = Standard_True;
	  break;
	case 'v':
	  verbose = Standard_True;
	  break;
	case 'V':
	  verbose = Standard_True;
	  Verbose = opts.OptionArgument();
	  break;
	case 'c':
	  context  = opts.OptionArgument();
	  break;
	case 'n':
	  nonl    = Standard_True;
	  break;
	}
      opts.Next();
    }

  if(opts.Failed()) return 1;

  if(context.IsNull())
    {
      context = new TCollection_HAsciiString("msgprint");
    }

  WOKTools_Message *mess = NULL;

  if(info)    mess = &InfoMsg();
  if(warning) mess = &WarningMsg();
  if(error)   mess = &ErrorMsg();
  if(verbose) 
    {
      if(!Verbose.IsNull())
	{
	  static WOKTools_Verbose averb;
	  
	  averb = VerboseMsg().LocalSwitcher(Verbose->ToCString());
	  mess  = &averb;
	}
      else
	{
	  mess = &VerboseMsg();
	}
    }

  if(mess == NULL)
    {
      mess = &InfoMsg();
    }

  const WOKTools_Message& mess1 = *mess;
  const Handle(TCollection_HAsciiString)& astr = mess1.Message();
  if(astr.IsNull())
    {
      *mess << context->ToCString();
    }

  Standard_Integer i;
  Handle(TColStd_HSequenceOfHAsciiString) argseq = opts.Arguments();
  
  for(i=1; i<=argseq->Length(); i++)
    {
      *mess << argseq->Value(i);
    }
  
  if(!nonl)
    {*mess << endm;}
  else
    {*mess << flushm;}
  return 0;
}

//=======================================================================
void WOKTclTools_MessageInfo_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " [-p|-a]"             << endl;
  cerr << endl;
  cerr << "    Options are : "                        << endl;
  cerr << "      -p : Message handler procedure name" << endl;
  cerr << "      -a : Arguments provided"             << endl;
  cerr << endl;
  return ;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : MessageInfo
//purpose  : 
//=======================================================================
Standard_Integer WOKTclTools_MsgAPI::MessageInfo(const Standard_Integer argc, const WOKTools_ArgTable& argv, WOKTools_Return &retval)
{  
  WOKTools_Options opts(argc,argv, "hpa", WOKTclTools_MessageInfo_Usage, "h");

  Standard_Boolean handler   = Standard_False;
  Standard_Boolean arguments = Standard_False;
  Handle(TCollection_HAsciiString) astr;

  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'p':
	  handler = Standard_True;
	  break;
	case 'a':
	  arguments = Standard_True;
	  break;
	}
      opts.Next();
    }

  if(opts.Failed()) return 1;

  Handle(WOKTclTools_Interpretor)&  Theinterp =  WOKTclTools_Interpretor::Current();

  if (Theinterp.IsNull())
    {
      ErrorMsg() << argv[0] << "Current TCL interpretor not initialized" << endm;
      return 1;
    }

  if (Theinterp->EndMessageProc()==NULL)
    {
      InfoMsg() << argv[0] << "No message handler procedure" << endm;
      return 0;
    }

  if(handler)
    {
      astr = new TCollection_HAsciiString(Theinterp->EndMessageProc());
      retval.AddStringValue(astr);      
    }

  if(arguments)
    {
      if (Theinterp->EndMessageArgs()!=NULL)
	astr = new TCollection_HAsciiString(Theinterp->EndMessageArgs());
      else
	astr = new TCollection_HAsciiString;
      retval.AddStringValue(astr);      
    }
  return 0;
 }
