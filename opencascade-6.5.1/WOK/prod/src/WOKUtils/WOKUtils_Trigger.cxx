// File:	WOKUtils_Trigger.cxx
// Created:	Fri Oct 27 18:23:27 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <Standard_RangeError.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKTools_Return.hxx>
#include <WOKTools_StringValue.hxx>

#include <WOKUtils_TriggerHandler.hxx>

#include <WOKUtils_Trigger.ixx>



//=======================================================================
//function : WOKUtils_Trigger
//purpose  : 
//=======================================================================
WOKUtils_Trigger::WOKUtils_Trigger()
  : myidx(1), mystat(WOKUtils_Unknown)
{
}

//=======================================================================
//function : SetTriggerHandler
//purpose  : 
//=======================================================================
void WOKUtils_Trigger::SetTriggerHandler(const WOKUtils_TriggerHandler ahandler)
{
  WOKUtils_Trigger::TriggerHandler() = ahandler;
}

//=======================================================================
//function : DefaultHandler
//purpose  : 
//=======================================================================
WOKUtils_TriggerStatus DefaultHandler(WOKUtils_Trigger& atrigger)
{
  Standard_Integer i;

  // Prise en compte des resultats

  const WOKTools_Return& args = atrigger.Args();

  for(i = 1; i <= args.Length() ; i++) 
    {
      Handle(WOKTools_ReturnValue) avalue = args.Value(i);
	
      switch(avalue->Type())
	{
	case WOKTools_String:
	  {
	    Handle(WOKTools_StringValue) astrval = Handle(WOKTools_StringValue)::DownCast(avalue);
	    InfoMsg() << "DefaultHandler" << "Arg " << i << " is : " << astrval->Value() << endm;
	  }
	  break;
        default: break;
	}
    }
  return WOKUtils_Unknown;
}


//=======================================================================
//function : TriggerHandler
//purpose  : 
//=======================================================================
WOKUtils_TriggerHandler& WOKUtils_Trigger::TriggerHandler( ) 
{
  static WOKUtils_TriggerHandler TheHandler = NULL;

  if(TheHandler == NULL)
    {
      TheHandler = DefaultHandler;
      return  TheHandler;
    }
  else
    return TheHandler;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetName
//purpose  : 
//=======================================================================
WOKUtils_Trigger& WOKUtils_Trigger::SetName(const Handle(TCollection_HAsciiString)& aname)
{
  myname = aname;
  return *this;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetName
//purpose  : 
//=======================================================================
WOKUtils_Trigger& WOKUtils_Trigger::SetName(const Standard_CString aname)
{
  myname = new TCollection_HAsciiString(aname);
  return *this;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AddFile
//purpose  : 
//=======================================================================
WOKUtils_Trigger& WOKUtils_Trigger::AddFile(const Handle(TCollection_HAsciiString)& afile,
					    const WOKUtils_Param& params, 
					    const WOKTools_InterpFileType atype)
{
  Handle(WOKUtils_Path) apath = params.SearchFile(afile);

  if(!apath.IsNull())
    {
      myargs.AddInterpFile(apath->Name(), atype);
    }
  return *this;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AddFile
//purpose  : 
//=======================================================================
WOKUtils_Trigger& WOKUtils_Trigger::AddFile(const Standard_CString afile,
					    const WOKUtils_Param& params, 
					    const WOKTools_InterpFileType atype)
{
  Handle(WOKUtils_Path) apath = params.SearchFile(new TCollection_HAsciiString(afile));

  if(!apath.IsNull())
    {
      myargs.AddInterpFile(apath->Name(), atype);
    }
  return *this;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AddArg
//purpose  : 
//=======================================================================
WOKUtils_Trigger& WOKUtils_Trigger::AddArg(const Handle(TCollection_HAsciiString)& astr)
{
  myargs.AddStringValue(astr);
  return *this;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AddArg
//purpose  : 
//=======================================================================
WOKUtils_Trigger& WOKUtils_Trigger::AddArg(const Standard_CString astr)
{
  myargs.AddStringValue(astr);
  return *this;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AddArg
//purpose  : 
//=======================================================================
WOKUtils_Trigger& WOKUtils_Trigger::AddArg(const Standard_Boolean abool)
{
  myargs.AddBooleanValue(abool);
  return *this;
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : AddArg
//purpose  : 
//=======================================================================
WOKUtils_Trigger& WOKUtils_Trigger::AddArg(const Standard_Integer anint)
{
  myargs.AddIntegerValue(anint);
  return *this;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AddArg
//purpose  : 
//=======================================================================
WOKUtils_Trigger& WOKUtils_Trigger::AddControl(const WOKUtils_TriggerControl anctrl)
{
  (*anctrl)(*this);
  return *this;
}

//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
WOKUtils_TriggerStatus WOKUtils_Trigger::Execute()  
{
  myidx=1;
  if(TriggerHandler()!=NULL)
    {
      return (mystat = (*TriggerHandler())(*this));
    }
  return (mystat = WOKUtils_Unknown);
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Addresult
//purpose  : 
//=======================================================================
WOKUtils_Trigger& WOKUtils_Trigger::AddResult(const Handle(TCollection_HAsciiString)& astr)
{
  myrets.AddStringValue(astr);
  return *this;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AddResult
//purpose  : 
//=======================================================================
WOKUtils_Trigger& WOKUtils_Trigger::AddResult(const Standard_CString astr)
{
  myrets.AddStringValue(astr);
  return *this;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AddResult
//purpose  : 
//=======================================================================
WOKUtils_Trigger& WOKUtils_Trigger::AddResult(const Standard_Boolean abool)
{
  myrets.AddBooleanValue(abool);
  return *this;
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : AddResult
//purpose  : 
//=======================================================================
WOKUtils_Trigger& WOKUtils_Trigger::AddResult(const Standard_Integer anint)
{
  myrets.AddIntegerValue(anint);
  return *this;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Getresult
//purpose  : 
//=======================================================================
WOKUtils_Trigger& WOKUtils_Trigger::GetResult(Handle(TCollection_HAsciiString)& astr) 
{
  if(mystat != WOKUtils_Succeeded) return *this;

  if(myidx>myrets.Length()) 
      Standard_RangeError::Raise("WOKUtils_Trigger::GetResult : No more args");


  Handle(WOKTools_StringValue) aret = Handle(WOKTools_StringValue)::DownCast(myrets.Value(myidx));
  
  if(!aret.IsNull())
    {
      astr = aret->Value();
    }
  else
    {
      astr.Nullify();
    }
  myidx++;
  return *this;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetResult
//purpose  : 
//=======================================================================
WOKUtils_Trigger& WOKUtils_Trigger::GetResult( Standard_Boolean& abool) 
{
  if(mystat != WOKUtils_Succeeded) return *this;

  if(myidx>myrets.Length()) 
      Standard_RangeError::Raise("WOKUtils_Trigger::GetResult : No more args");

  Handle(WOKTools_StringValue) aret = Handle(WOKTools_StringValue)::DownCast(myrets.Value(myidx));
  
  if(!aret.IsNull())
    {
      Handle(TCollection_HAsciiString) astr;
      astr = aret->Value();

      if(astr->Value(1) == '0')
	{
	  abool = Standard_False;
	}
      else if(astr->Value(1) == '1')
	{
	  abool = Standard_True;
	}
      else
	{
	  abool = Standard_False;
	}
    }
  else
    {
      abool = Standard_False;
    }
  myidx++;
  return *this;
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetResult
//purpose  : 
//=======================================================================
WOKUtils_Trigger& WOKUtils_Trigger::GetResult( Standard_Integer& anint)
{
  if(mystat != WOKUtils_Succeeded) return *this;

  if(myidx>myrets.Length()) 
      Standard_RangeError::Raise("WOKUtils_Trigger::GetResult : No more args");

  Handle(WOKTools_StringValue) aret = Handle(WOKTools_StringValue)::DownCast(myrets.Value(myidx));
  
  if(!aret.IsNull())
    {
      Handle(TCollection_HAsciiString) astr;
      astr = aret->Value();
      
      if(astr->IsIntegerValue())
	{
	  anint = astr->IntegerValue();
	}
      else
	{
	  anint = 0;
	}
    }
  else
    {
      anint = 0;
    }
  myidx++;
 return *this;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Name
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKUtils_Trigger::Name() const
{
  return myname;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Return
//purpose  : 
//=======================================================================
const WOKTools_Return& WOKUtils_Trigger::Return() const
{
  return myrets;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : ChangeReturn
//purpose  : 
//=======================================================================
WOKTools_Return& WOKUtils_Trigger::ChangeReturn() 
{
  return myrets;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Args
//purpose  : 
//=======================================================================
const WOKTools_Return& WOKUtils_Trigger::Args() const
{
  return myargs;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Status
//purpose  : 
//=======================================================================
WOKUtils_TriggerStatus WOKUtils_Trigger::Status() const
{
  return mystat;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : endt
//purpose  : 
//=======================================================================
Standard_EXPORT WOKUtils_Trigger& endt(WOKUtils_Trigger& trigger)
{
  trigger.Execute();
  return trigger;
}

