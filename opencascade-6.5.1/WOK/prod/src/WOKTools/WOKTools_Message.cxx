

#include <WOKTools_Message.ixx>

#include <WOKTools_Messages.hxx>

#include <TCollection_HAsciiString.hxx>

WOKTools_Message& DefaultEndMsgHandler(WOKTools_Message& amsgq, const Standard_Boolean newline)
{
  if(amsgq.Message().IsNull() == Standard_False)
    {
      if(newline)
	{
	  cerr << amsgq.ToPrint() << endl;
	}
      else
	{
	  cerr << amsgq.ToPrint() << flush;
	}
    }
  return amsgq;
}

//=======================================================================
//function : FileLogEndMsgHandler
//purpose  : 
//=======================================================================
WOKTools_Message& FileLogEndMsgHandler(WOKTools_Message& amsgq, const Standard_Boolean newline)
{
  if(amsgq.LogStream() == NULL) return amsgq;

  ofstream& astream = *(amsgq.LogStream());
  if(amsgq.Message().IsNull() == Standard_False)
    {
      astream << amsgq.ToPrint();
      if(newline) {
	astream << endl;
      } else {
	astream << flush;
      }
    }
  return amsgq;
}

//=======================================================================
//function : WOKTools_Message
//purpose  : 
//=======================================================================
 WOKTools_Message :: WOKTools_Message (
                      const Standard_CString aclass,
                      const Standard_CString aheader
                     ) : myheader(aheader),
                         myindex(1),
                         myison(Standard_True),
                         myprintcontext(Standard_False),
                         myprintheader(Standard_True), 
                         myswitcher(aclass),
                         myendmsghandlr(DefaultEndMsgHandler),
                         mylogstream(NULL) {}
//=======================================================================
//function : Init
//purpose  : 
//=======================================================================
void WOKTools_Message::Init() 
{
  mymessage.Nullify();
  myindex = 1;
}
//=======================================================================
//function : Set
//purpose  : 
//=======================================================================
void WOKTools_Message::Set()
{
  myison = Standard_True;
}

//=======================================================================
//function : UnSet
//purpose  : 
//=======================================================================
void WOKTools_Message::UnSet()
{
  myison = Standard_False;
}

//=======================================================================
//function : DoPrintContext
//purpose  : 
//=======================================================================
void WOKTools_Message::DoPrintContext()
{
  myprintcontext = Standard_True;
}

//=======================================================================
//function : DontPrintContext
//purpose  : 
//=======================================================================
void WOKTools_Message::DontPrintContext()
{
  myprintcontext = Standard_False;
}

//=======================================================================
//function : DoPrintHeader
//purpose  : 
//=======================================================================
void WOKTools_Message::DoPrintHeader()
{
  myprintheader = Standard_True;
}

//=======================================================================
//function : DontPrintHeader
//purpose  : 
//=======================================================================
void WOKTools_Message::DontPrintHeader()
{
  myprintheader = Standard_False;
}


//=======================================================================
//function : SetSwitcher
//purpose  : 
//=======================================================================
void WOKTools_Message::SetSwitcher(const Standard_CString aswitcher)
{
  myswitcher = aswitcher;
}


//=======================================================================
//function : Print
//purpose  : 
//=======================================================================
WOKTools_Message& WOKTools_Message::Print(const Standard_CString astr) 
{
  // se poser la question de savoir si un msg est commence ou non 
  if(myison == Standard_True)
    {
      if(mymessage.IsNull() == Standard_True) 
	{
	  if(myprintheader)
	    {
	      mymessage = new TCollection_HAsciiString(myheader);
	    }
	  else
	    {
	      mymessage = new TCollection_HAsciiString;
	    }
	  if(myprintcontext == Standard_True)
	    {
	      if(astr != NULL)
		{
		  mymessage->AssignCat(astr);
		  mymessage->AssignCat(" : ");
		}
	    }
	}
      else
	{
	  if(astr != NULL)
	    {
	      mymessage->AssignCat(astr);
	    }
	  else
	    {
	      mymessage->AssignCat("NULL");
	    }
	}
    }
  return *this;
}

//=======================================================================
//function : Print
//purpose  : 
//=======================================================================
WOKTools_Message& WOKTools_Message::Print(const Standard_Integer astr) 
{
  // se poser la question de savoir si un msg est commence ou non 
  if(myison == Standard_True)
    {
      if(mymessage.IsNull() == Standard_True) 
	{
	  if(myprintheader)
	    {
	      mymessage = new TCollection_HAsciiString(myheader);
	    }
	  else
	    {
	      mymessage = new TCollection_HAsciiString;
	    }
	  if(myprintcontext == Standard_True)
	    {
	      mymessage->AssignCat(new TCollection_HAsciiString(astr));
	      mymessage->AssignCat(" : ");
	    }
	}
      else
	{
	  mymessage->AssignCat(new TCollection_HAsciiString(astr));
	}
    }
  return *this;
}

//=======================================================================
//function : Print
//purpose  : 
//=======================================================================
WOKTools_Message& WOKTools_Message::Print(const Standard_Character achar) 
{
  // se poser la question de savoir si un msg est commence ou non 
  if(myison == Standard_True)
    {
      if(mymessage.IsNull() == Standard_True) 
	{
	  if(myprintheader)
	    {
	      mymessage = new TCollection_HAsciiString(myheader);
	    }
	  else
	    {
	      mymessage = new TCollection_HAsciiString;
	    }
	  if(myprintcontext == Standard_True)
	    {
	      mymessage->AssignCat(new TCollection_HAsciiString(achar));
	      mymessage->AssignCat(" : ");
	    }
	}
      else
	{
	  mymessage->AssignCat(new TCollection_HAsciiString(achar));
	}
    }
  return *this;
}

//=======================================================================
//function : Print
//purpose  : 
//=======================================================================
WOKTools_Message& WOKTools_Message::Print(const Handle(TCollection_HAsciiString)& astr) 
{
  // se poser la question de savoir si un msg est commence ou non 
  if(myison == Standard_True)
    {
      if(mymessage.IsNull()) 
	{
	  if(myprintheader)
	    {
	      mymessage = new TCollection_HAsciiString(myheader);
	    }
	  else
	    {
	      mymessage = new TCollection_HAsciiString;
	    }
	  if(myprintcontext == Standard_True)
	    {
	      if(astr.IsNull())
		mymessage->AssignCat("(nil)");
	      else
		{
		  mymessage->AssignCat(astr);
		}
	      mymessage->AssignCat(" : ");
	    }
	}
      else
	{
	  if(astr.IsNull())
	    {
	      mymessage->AssignCat("(Null String)");
	    }
	  else
	    {
	      mymessage->AssignCat(astr);
	    }
	}
    }
  return *this;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : MsgControl
//purpose  : 
//=======================================================================
WOKTools_Message& WOKTools_Message::MsgControl(const WOKTools_MsgControl ahandler) 
{
  return (*ahandler)(*this);
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetEndMsgHandler
//purpose  : 
//=======================================================================
void WOKTools_Message::SetEndMsgHandler(const WOKTools_MsgHandler& ahandler)
{
  myendmsghandlr = ahandler;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetIndex
//purpose  : 
//=======================================================================
void WOKTools_Message::SetIndex(const Standard_Integer anindex) 
{
  myindex = anindex;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : LogToFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKTools_Message::LogToFile(const Handle(TCollection_HAsciiString)& afile)
{
  if(!afile.IsNull())
    {
      mylogstream = new ofstream(afile->ToCString(),ios::out);

      if(mylogstream->good())
	{
	  mylogfile = afile;
	  mylogflag = Standard_True;
	  return Standard_True;
	}
    }

  return Standard_False;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : LogToFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKTools_Message::LogToStream(const WOKTools_MsgStreamPtr& astream)
{
  if(astream != NULL)
    {
      EndLogging();
      if(astream->good())
	{
	  mylogstream = astream;
	  mylogflag = Standard_True;
	  mylogfile.Nullify();
	  return Standard_True;
	}
    }

  return Standard_False;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : EndLogging
//purpose  : 
//=======================================================================
void WOKTools_Message::EndLogging()
{
  if(mylogstream != NULL)
    {
      if(!mylogfile.IsNull())
	{
	  mylogfile.Nullify();
	  mylogflag = Standard_False;
	  mylogstream->close();
	  delete mylogstream;
	}
      else
	{
	  mylogstream = NULL;
	  mylogflag = Standard_False;
	}
    }
}


WOKTools_Message& endm(WOKTools_Message& amsgq)
{
   WOKTools_Message& result = (*amsgq.EndMsgHandler())(amsgq, Standard_True);
   amsgq.Init();
   return result;
}

WOKTools_Message& flushm(WOKTools_Message& amsgq)
{
  WOKTools_Message& result = (*amsgq.EndMsgHandler())(amsgq, Standard_False);
  result.SetIndex(amsgq.Message()->Length()+1);
  return result;
}

WOKTools_Info& InfoMsg()
{
  static WOKTools_Info InfoMsg;
  return InfoMsg;
}

WOKTools_Warning& WarningMsg()
{
  static WOKTools_Warning WarningMsg;
  return WarningMsg;
}

WOKTools_Error& ErrorMsg()
{
  static WOKTools_Error ErrorMsg;
  return ErrorMsg;
}

WOKTools_Verbose& VerboseMsg()
{
  static WOKTools_Verbose VerboseMsg;
  return VerboseMsg;
}
