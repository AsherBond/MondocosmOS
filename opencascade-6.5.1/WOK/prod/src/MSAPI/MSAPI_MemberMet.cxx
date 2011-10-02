// File:	MSAPI_MemberMet.cxx
// Created:	Tue Sep 19 22:41:53 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <Standard_ErrorHandler.hxx>

#include <WOKTools_Options.hxx>

#include <MS_MetaSchema.hxx>
#include <MS_MemberMet.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKBuilder_MSchema.hxx>
#include <WOKBuilder_MSTool.hxx>

#include <TCollection_HAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <MSAPI_Method.hxx>
#include <MSAPI_MemberMet.ixx>


extern char *MSAPI_Method_Info_Options;
extern void  MSAPI_Method_Info_Usage(char *cmd);

void MSAPI_MemberMet_Info_Usage(char *cmd)
{
  MSAPI_Method_Info_Usage(cmd);
  cerr << "        -P : is protected ?\n";
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Info
//purpose  : 
//=======================================================================
Standard_Integer MSAPI_MemberMet::Info(const Standard_Integer argc, const WOKTools_ArgTable& argv, WOKTools_Return& values)
{
  Handle(TCollection_HAsciiString) name;
  Standard_Boolean methodoption = Standard_False;
  Standard_Boolean isprotected    = Standard_False;    
  TCollection_AsciiString astr(MSAPI_Method_Info_Options);

  astr.AssignCat("P");

  WOKTools_Options opts(argc, argv, astr.ToCString(), MSAPI_MemberMet_Info_Usage, astr.ToCString());

  while(opts.More())
    {
      if(strchr(MSAPI_Method_Info_Options, opts.Option()))
	{
	  methodoption = Standard_True;
	}
      else
	{
	  switch(opts.Option())
	    {
	    case 'P':
	      isprotected = Standard_True;
	      break;
	    }
	}
      opts.Next();
    }
  
  if(opts.Failed() == Standard_True) return 1;
  
  if(opts.Arguments().IsNull() == Standard_True) {MSAPI_MemberMet_Info_Usage(argv[0]); return 1;}
  
  switch(opts.Arguments()->Length())
    {
    case 1:
      name = opts.Arguments()->Value(1);
      break;
    default:
      MSAPI_MemberMet_Info_Usage(argv[0]);
      return 1;
    }
  
  if(WOKBuilder_MSTool::GetMSchema()->MetaSchema()->IsMethod(name) == Standard_False)
    {
      ErrorMsg() << argv[0] << "Name (" << name << ") is not a known method name" << endm;
      return 1;
    }

  Handle(MS_Method) method = WOKBuilder_MSTool::GetMSchema()->MetaSchema()->GetMethod(name);

  if(method->IsKind(STANDARD_TYPE(MS_MemberMet)) == Standard_False)
    {
      ErrorMsg() << argv[0] << "Name (" << name << ") is not a member method name" << endm;
      return 1;
    }

  if(methodoption)
    {
      return MSAPI_Method::Info(argc, argv, values);
    }
  
  Handle(MS_MemberMet) membermet = Handle(MS_MemberMet)::DownCast(method);

  if(isprotected)
    {
      if(membermet->IsProtected()) values.AddStringValue("1");
      else                         values.AddStringValue("0");
      return 0;
    }
  return 0;
}





