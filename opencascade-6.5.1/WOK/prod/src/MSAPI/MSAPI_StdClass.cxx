// File:	MSAPI_StdClass.cxx
// Created:	Tue Sep 19 17:54:04 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <Standard_ErrorHandler.hxx>

#include <WOKTools_Options.hxx>

#include <MS_MetaSchema.hxx>
#include <MS_StdClass.hxx>

#include <MSAPI_Class.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKBuilder_MSchema.hxx>
#include <WOKBuilder_MSTool.hxx>

#include <TCollection_HAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>


#include <MSAPI_StdClass.ixx>

extern void  MSAPI_Class_Info_Usage(char *cmd);
extern char *MSAPI_Class_Info_Options;

void MSAPI_StdClass_Info_Usage(char *cmd)
{
  MSAPI_Class_Info_Usage(cmd);
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Info
//purpose  : 
//=======================================================================
Standard_Integer MSAPI_StdClass::Info(const Standard_Integer argc, const WOKTools_ArgTable& argv, WOKTools_Return& values)
{
  Standard_Boolean classoption = Standard_False;
  Handle(TCollection_HAsciiString) name;
  TCollection_AsciiString astr(MSAPI_Class_Info_Options);

  astr.AssignCat("");

  WOKTools_Options opts(argc, argv, astr.ToCString(), MSAPI_StdClass_Info_Usage, astr.ToCString());

  while(opts.More())
    {
      if(strchr(MSAPI_Class_Info_Options, opts.Option()))
	{
	  classoption = Standard_True;
	}
      else
	{
	  switch(opts.Option())
	    {
	    default:
	      break;
	    }
	}
      opts.Next();
    }
  
  if(opts.Failed() == Standard_True) return 1;
  
  if(opts.Arguments().IsNull() == Standard_True) {MSAPI_StdClass_Info_Usage(argv[0]); return 1;}
  
  switch(opts.Arguments()->Length())
    {
    case 1:
      name = opts.Arguments()->Value(1);
      break;
    default:
      MSAPI_StdClass_Info_Usage(argv[0]);
      return 1;
    }
  
  if(WOKBuilder_MSTool::GetMSchema()->MetaSchema()->IsDefined(name) == Standard_False)
    {
      ErrorMsg() << argv[0] << "Name (" << name->ToCString() << ") is not a known type name" << endm;
      return 1;
    }

  Handle(MS_Type) type = WOKBuilder_MSTool::GetMSchema()->MetaSchema()->GetType(name);

  if(type->IsKind(STANDARD_TYPE(MS_StdClass)) == Standard_False)
    {
      ErrorMsg() << argv[0] << "Name (" << name->ToCString() << ") is not a class name" << endm;
      return 1;
    }

  if(classoption)
    {
      return MSAPI_Class::Info(argc, argv, values);
    }

  return 0;
}

