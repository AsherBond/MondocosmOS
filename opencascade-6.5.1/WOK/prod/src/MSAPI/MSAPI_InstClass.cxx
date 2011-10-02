// File:	MSAPI_InstClass.cxx
// Created:	Tue Sep 19 21:26:20 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <Standard_ErrorHandler.hxx>

#include <WOKTools_Options.hxx>

#include <MS_MetaSchema.hxx>
#include <MS_InstClass.hxx>
#include <MS_StdClass.hxx>
#include <MS_HSequenceOfInstClass.hxx>
#include <MS_GenType.hxx>
#include <MS_HSequenceOfGenType.hxx>

#include <MSAPI_Class.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKBuilder_MSchema.hxx>
#include <WOKBuilder_MSTool.hxx>

#include <TCollection_HAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <MSAPI_InstClass.ixx>


extern void  MSAPI_Class_Info_Usage(char *cmd);
extern char *MSAPI_Class_Info_Options;

void MSAPI_InstClass_Info_Usage(char *cmd)
{
  MSAPI_Class_Info_Usage(cmd);
  cerr << "        -g : generic class\n";
  cerr << "        -G : generic types\n";
  cerr << "        -s : inStantiation types\n";
  cerr << "        -a : nested intantiation classes\n";
  return;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Info
//purpose  : 
//=======================================================================
Standard_Integer MSAPI_InstClass::Info(const Standard_Integer argc, const WOKTools_ArgTable& argv, WOKTools_Return& values)
{

  Standard_Boolean genclass    = Standard_False;
  Standard_Boolean insttypes   = Standard_False;
  Standard_Boolean gentypes    = Standard_False;
  Standard_Boolean nesteds     = Standard_False;
  Standard_Boolean classoption = Standard_False;
  Handle(TCollection_HAsciiString) name;
  Standard_Integer i;
  TCollection_AsciiString astr(MSAPI_Class_Info_Options);

  astr.AssignCat("gGsa");
  WOKTools_Options opts(argc, argv, astr.ToCString(), MSAPI_InstClass_Info_Usage, astr.ToCString());

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
	    case 'g':
	      genclass    = Standard_True;
	      break;
	    case 'G':
	      gentypes    = Standard_True;
	      break;
	    case 's':
	      insttypes   = Standard_True;
	      break;
	    case 'a':
	      nesteds     = Standard_True;
	      break;
	    }
	}
      opts.Next();
    }
  
  if(opts.Failed() == Standard_True) return 1;

  
  if(opts.Arguments().IsNull() == Standard_True) {MSAPI_InstClass_Info_Usage(argv[0]); return 1;}
  
  switch(opts.Arguments()->Length())
    {
    case 1:
      name = opts.Arguments()->Value(1);
      break;
    default:
      MSAPI_InstClass_Info_Usage(argv[0]);
      return 1;
    }
  
  if(WOKBuilder_MSTool::GetMSchema()->MetaSchema()->IsDefined(name) == Standard_False)
    {
      ErrorMsg() << argv[0] << "Name (" << name->ToCString() << ") is not a known type name" << endm;
      return 1;
    }

  Handle(MS_Type) type = WOKBuilder_MSTool::GetMSchema()->MetaSchema()->GetType(name);

  if(classoption)
    {
      return MSAPI_Class::Info(argc, argv, values);
    }

  Handle(MS_InstClass) aclass;


  if(type->IsKind(STANDARD_TYPE(MS_StdClass)))
    {
      aclass = Handle(MS_StdClass)::DownCast(type)->GetMyCreator();
    }
  else
    {
      aclass = Handle(MS_InstClass)::DownCast(type);
    }

  if(aclass.IsNull())
    {
      ErrorMsg() << argv[0] << "Name (" << name->ToCString() << ") is not a Instantiation name"  << endm;
      return 1;
    }
  
  if(genclass)
    {
      values.AddStringValue(aclass->GenClass());
      return 0;
    }

  if(insttypes)
    {
      Handle(TColStd_HSequenceOfHAsciiString) aseq = aclass->InstTypes();
      
      for(i=1; i<=aseq->Length(); i++)
	{
	  values.AddStringValue(aseq->Value(i));
	}
      return 0;
    }
  if(gentypes)
    {
      Handle(TColStd_HSequenceOfHAsciiString) aseq = aclass->GenTypes();
      
      for(i=1; i<=aseq->Length(); i++)
	{
	  values.AddStringValue(aseq->Value(i));
	}
      return 0;
    }

  if(nesteds)
    {
      Handle(TColStd_HSequenceOfHAsciiString) aseq = aclass->GetNestedStdClassesName();

      for(i=1; i<=aseq->Length(); i++)
	{
	  values.AddStringValue(aseq->Value(i));
	}

      aseq = aclass->GetNestedInsClassesName();
      for(i=1; i<=aseq->Length(); i++)
	{
	  values.AddStringValue(aseq->Value(i));
	}

      return 0;
    }

  return 0;
}

