// File:	WOKTclTools_TriggerHandler.cxx
// Created:	Thu Nov 14 17:19:45 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>



#include <WOKUtils_Trigger.hxx>
#include <WOKTools_Messages.hxx>
#include <WOKTools_Return.hxx>
#include <WOKTools_StringValue.hxx>
#include <WOKTools_InterpFileValue.hxx>



#include <WOKTclTools_Interpretor.hxx>

#include <tcl.h>

Standard_EXPORT WOKUtils_TriggerStatus WOKTclTools_TriggerHandler(WOKUtils_Trigger &atrigger)
{  
  Standard_Integer i;

  const WOKTools_Return& args = atrigger.Args();

  for(i = 1; i <= args.Length() ; i++) 
    {
      Handle(WOKTools_ReturnValue) avalue = args.Value(i);
      
      switch(avalue->Type())
	{
	case WOKTools_InterpFile:
	  {
	    Handle(WOKTools_InterpFileValue) afile = Handle(WOKTools_InterpFileValue)::DownCast(avalue);
	    
	    if(afile->InterpType() == WOKTools_TclInterp)
	      {
		WOK_TRACE {
		  VerboseMsg()("WOK_TRIGGER") << "WOKTclTools_TriggerHandler" 
					    << "LoadFile : " << afile->File() << endm;
		}
		WOKTclTools_Interpretor::Current()->EvalFile(afile->File()->ToCString());
	      }
	  }
	break;
	default:
	  break;
	}
    }

  if(!atrigger.Name().IsNull())
    {
      if(WOKTclTools_Interpretor::Current()->IsCmdName(atrigger.Name()->ToCString()))
	{
	  Handle(TCollection_HAsciiString) acmd = new TCollection_HAsciiString(atrigger.Name());
	  
	  WOK_TRACE {

	    VerboseMsg()("WOK_TRIGGER") << "WOKTclTools_TriggerHandler"
				      << "Handler called for trigger : " << atrigger.Name() << endm;
	  
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
		      VerboseMsg()("WOK_TRIGGER") << "WOKTclTools_TriggerHandler" 
						<< "Arg " << i << " is : " << astrval->Value() << endm;
		    }
		    break;
		  default:
		    break;
		  }
	      }
	  }
	  
	  for(i = 1; i <= args.Length() ; i++) 
	    {
	      Handle(WOKTools_ReturnValue) avalue = args.Value(i);
	      
	      switch(avalue->Type())
		{
		case WOKTools_String:
		  {
		    Handle(WOKTools_StringValue) astrval = Handle(WOKTools_StringValue)::DownCast(avalue);
		    acmd->AssignCat(" \"");
		    acmd->AssignCat(astrval->Value());
		    acmd->AssignCat("\"");
		  }
		break;
		default:
		  break;
		}
	    }
	  
	  WOK_TRACE {
	    VerboseMsg()("WOK_TRIGGER") << "WOKTclTools_TriggerHandler" 
				      << "Command is : " << acmd << endm;
	  }
	  
	  if(WOKTclTools_Interpretor::Current()->Eval(acmd->ToCString())) 
	    return WOKUtils_Failed;
	  else
	    {
	      WOKTclTools_Interpretor::Current()->GetReturnValues(atrigger.ChangeReturn());

	      WOK_TRACE {
		if(VerboseMsg()("WOK_TRIGGER").IsSet())
		  {
		    Standard_Integer i;
		    VerboseMsg()("WOK_TRIGGER") << "WOKTclTools_TriggerHandler" 
					      << "Command returns : " << endm;
		    
		    const WOKTools_Return& rets = atrigger.Return();
		    
		    for(i=1; i<=rets.Length(); i++)
		      {
			Handle(WOKTools_ReturnValue) aval = rets.Value(i);
			
			VerboseMsg()("WOK_TRIGGER") << "WOKTclTools_TriggerHandler" 
						  << "             ";
			
			switch(aval->Type())
			  {
			  case WOKTools_String:
			    {
			      Handle(WOKTools_StringValue) astrval = Handle(WOKTools_StringValue)::DownCast(aval);
			      VerboseMsg()("WOK_TRIGGER") << "WOKTclTools_TriggerHandler" 
							<< "             " << i << " : " << astrval->Value() << endm;
			    }
			    break;
			  default:
			    break;
			  }
		      }
		  }
	      }
	    }
	  return WOKUtils_Succeeded;
	}
      else
	{
	  WOK_TRACE {
	    VerboseMsg()("WOK_TRIGGER") << "WOKTclTools_TriggerHandler" 
				      << "Trigger : " << atrigger.Name() << " not setted" << endm;
	  }
	  return WOKUtils_NotSetted;
	}
    }
  else
    {
      WOK_TRACE {
	VerboseMsg()("WOK_TRIGGER") << "WOKTclTools_TriggerHandler" 
				  << "No Trigger Name" << endm;
      }
      return WOKUtils_NotSetted;
    }
}

