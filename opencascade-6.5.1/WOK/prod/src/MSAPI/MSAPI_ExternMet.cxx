// File:	MSAPI_ExternMet.cxx
// Created:	Tue Sep 19 22:42:02 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <Standard_ErrorHandler.hxx>

#include <WOKTools_Options.hxx>

#include <MS_MetaSchema.hxx>
#include <MS_ExternMet.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKBuilder_MSchema.hxx>
#include <WOKBuilder_MSTool.hxx>

#include <TCollection_HAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <MSAPI_Method.hxx>
#include <MSAPI_ExternMet.ixx>

extern char *MSAPI_Method_Info_Options;
extern void  MSAPI_Method_Info_Usage(char *cmd);

void MSAPI_ExternMet_Info_Usage(char *cmd)
{
  MSAPI_Method_Info_Usage(cmd);
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Info
//purpose  : 
//=======================================================================
Standard_Integer MSAPI_ExternMet::Info(const Standard_Integer argc, const WOKTools_ArgTable& argv, WOKTools_Return& values)
{

  Handle(TCollection_HAsciiString) name;
  Standard_Boolean methodoption = Standard_False;
  TCollection_AsciiString astr(MSAPI_Method_Info_Options);

  astr.AssignCat("");

  WOKTools_Options opts(argc, argv, astr.ToCString(), MSAPI_ExternMet_Info_Usage, astr.ToCString());

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
	    }
	}
      opts.Next();
    }
  
  if(opts.Failed() == Standard_True) return 1;
  
  if(opts.Arguments().IsNull() == Standard_True) {MSAPI_ExternMet_Info_Usage(argv[0]); return 1;}
  
  switch(opts.Arguments()->Length())
    {
    case 1:
      name = opts.Arguments()->Value(1);
      break;
    default:
      MSAPI_ExternMet_Info_Usage(argv[0]);
      return 1;
    }
  
  if(WOKBuilder_MSTool::GetMSchema()->MetaSchema()->IsMethod(name) == Standard_False)
    {
      ErrorMsg() << argv[0] << "Name (" << name->ToCString() << ") is not a known method name" << endm;
      return 1;
    }

  Handle(MS_Method) method = WOKBuilder_MSTool::GetMSchema()->MetaSchema()->GetMethod(name);

  if(method->IsKind(STANDARD_TYPE(MS_ExternMet)) == Standard_False)
    {
      ErrorMsg() << argv[0] << "Name (" << name->ToCString() << ") is not a extern method name" << endm;
      return 1;
    }

  if(methodoption)
    {
      return MSAPI_Method::Info(argc, argv, values);
    }

  return 0;
}

