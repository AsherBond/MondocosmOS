// File:	MSAPI_GenClass.cxx
// Created:	Tue Sep 19 21:06:38 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>



#include <Standard_ErrorHandler.hxx>

#include <WOKTools_Options.hxx>

#include <MS_MetaSchema.hxx>
#include <MS_GenClass.hxx>
#include <MS_HSequenceOfGenClass.hxx>
#include <MS_GenType.hxx>
#include <MS_HSequenceOfGenType.hxx>

#include <MSAPI_Class.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKBuilder_MSchema.hxx>
#include <WOKBuilder_MSTool.hxx>

#include <TCollection_HAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>


#include <MSAPI_GenClass.ixx>

extern void  MSAPI_Class_Info_Usage(char *cmd);
extern char *MSAPI_Class_Info_Options;

void MSAPI_GenClass_Info_Usage(char *cmd)
{
  MSAPI_Class_Info_Usage(cmd);
  cerr << "        -n : nested  classes\n";
  cerr << "        -g : generic type\n";
  return;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Info
//purpose  : 
//=======================================================================
Standard_Integer MSAPI_GenClass::Info(const Standard_Integer argc, const WOKTools_ArgTable& argv, WOKTools_Return& values)
{
  Standard_Boolean nesteds     = Standard_False;
  Standard_Boolean gentypes    = Standard_False;
  Standard_Boolean classoption = Standard_False;
  Handle(TCollection_HAsciiString) name;
  Standard_Integer i;
  TCollection_AsciiString astr(MSAPI_Class_Info_Options);
  
  astr.AssignCat("ng");
  WOKTools_Options opts(argc, argv, astr.ToCString(), MSAPI_GenClass_Info_Usage, astr.ToCString());

  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'd':
	case 'p':
	case 'i':
	case 'I':
	case 'u':
	case 'c':
	case 'C':
	case 'm':
	case 'r':
	case 'f':
	case 'M':
	  classoption = Standard_True;
	  break;
	case 'n':
	  nesteds     = Standard_True;
	  break;
	case 'g':
	  gentypes    = Standard_True;
	}
      opts.Next();
    }
  
  if(opts.Failed() == Standard_True) return 1;

  
  if(opts.Arguments().IsNull() == Standard_True) {MSAPI_GenClass_Info_Usage(argv[0]); return 1;}
  
  switch(opts.Arguments()->Length())
    {
    case 1:
      name = opts.Arguments()->Value(1);
      break;
    default:
      MSAPI_GenClass_Info_Usage(argv[0]);
      return 1;
    }
  
  if(WOKBuilder_MSTool::GetMSchema()->MetaSchema()->IsDefined(name) == Standard_False)
    {
      ErrorMsg() << argv[0] << "Name (" << name->ToCString() << ") is not a known type name" << endm;
      return 1;
    }

  Handle(MS_Type) type = WOKBuilder_MSTool::GetMSchema()->MetaSchema()->GetType(name);

  if(type->IsKind(STANDARD_TYPE(MS_GenClass)) == Standard_False)
    {
      ErrorMsg() << argv[0] << "Name (" << name->ToCString() << ") is not a class name" << endm;
      return 1;
    }

  if(classoption)
    {
      return MSAPI_Class::Info(argc, argv, values);
    }

  Handle(MS_GenClass) aclass = Handle(MS_GenClass)::DownCast(type);

  if(nesteds)
    {
      Handle(TColStd_HSequenceOfHAsciiString) aseq = aclass->GetNestedName();

      for(i=1; i<=aseq->Length(); i++)
	{
	  values.AddStringValue(aseq->Value(i));
	}
      return 0;
    }

  if(gentypes)
    {
      Handle(MS_HSequenceOfGenType) aseq = aclass->GenTypes();

      for(i=1; i<=aseq->Length(); i++)
	{
	  values.AddStringValue(aseq->Value(i)->FullName());
	}
      return 0;
    }

  return 0;
}

