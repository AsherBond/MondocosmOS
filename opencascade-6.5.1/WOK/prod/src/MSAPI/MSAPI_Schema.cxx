// File:	MSAPI_Schema.cxx
// Created:	Thu Apr 25 16:12:07 1996
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <Standard_ErrorHandler.hxx>

#include <TCollection_HAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Options.hxx>
#include <WOKTools_Return.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKBuilder_MSTool.hxx>
#include <WOKBuilder_MSchema.hxx>

#include <MS.hxx>
#include <MS_MetaSchema.hxx>
#include <MS_Schema.hxx>

#include <MSAPI_Schema.ixx>


void MSAPI_Schema_Info_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " -c <schname>\n";
  cerr << endl;
  cerr << "Options are : " << endl;
  cerr << "         -c : Classes listed in <schname>.cdl" << endl;
  cerr << "         -p : Packages listed in <schname>.cdl" << endl;
  cerr << "         -C : All classes listed in Schema" << endl;
  cerr << "         -d : All classes needed not listed in schema" << endl;
  cerr << "         -a : All classes in schema" << endl;
  cerr << "         -s : All classes in sorted in \"DDL\" order" << endl;
}



//=======================================================================
//Author   : Jean Gautier (jga)
//function : Info
//purpose  : 
//=======================================================================
Standard_Integer MSAPI_Schema::Info(const Standard_Integer argc, const WOKTools_ArgTable& argv, WOKTools_Return& values)
{
  WOKTools_Options opts(argc, argv, "cpCdash", MSAPI_Schema_Info_Usage, "cpCdash");
  Handle(TCollection_HAsciiString) schname;
  Handle(MS_Schema)                asch;
  Standard_Boolean getclasses        = Standard_False;
  Standard_Boolean getpks            = Standard_False;
  Standard_Boolean getallclasses     = Standard_False;
  Standard_Boolean getdeepclasses    = Standard_False;
  Standard_Boolean getAllClasses     = Standard_False;
  Standard_Boolean getSortedClasses  = Standard_False;
  Standard_Integer i;

  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'c':
	  getclasses       = Standard_True;
	  break;
	case 'p':
	  getpks           = Standard_True;
	  break;
	case 'C':
	  getallclasses    = Standard_True;
	  break;
	case 'd':
	  getdeepclasses   = Standard_True;
	  break;
	case 'a':
	  getAllClasses    = Standard_True;
	  break;
	case 's':
	  getSortedClasses = Standard_True;
	  break;
	default:
	  break;
	}
      opts.Next();
    }
  
  if(opts.Failed() == Standard_True) return 1;

  if(opts.Arguments().IsNull() == Standard_True) {MSAPI_Schema_Info_Usage(argv[0]); return 1;}
  
  switch(opts.Arguments()->Length())
    {
    case 1:
      schname = opts.Arguments()->Value(1);
      break;
    default:
      MSAPI_Schema_Info_Usage(argv[0]);
      return 1;
    }
  
  
  if(WOKBuilder_MSTool::GetMSchema()->MetaSchema()->IsSchema(schname) == Standard_False)
    {
      ErrorMsg() << argv[0] << "Given name (" << schname->ToCString() << ") is not a known Schema" << endm;
    }

  asch = WOKBuilder_MSTool::GetMSchema()->MetaSchema()->GetSchema(schname);

  if(getclasses)
    {
      Handle(TColStd_HSequenceOfHAsciiString) aseq = asch->GetClasses();

      for(i=1; i<=aseq->Length(); i++)
	{
	  values.AddStringValue(aseq->Value(i));
	}
      return 0;
    }
  if(getpks)
    {
      Handle(TColStd_HSequenceOfHAsciiString) aseq = asch->GetPackages();

      for(i=1; i<=aseq->Length(); i++)
	{
	  values.AddStringValue(aseq->Value(i));
	}
      return 0;
    }

  if(getallclasses)
    {
      Handle(TColStd_HSequenceOfHAsciiString) aseq = 
	WOKBuilder_MSTool::GetMSchema()->MetaSchema()->GetPersistentClassesFromSchema(asch->Name());

      for(i=1; i<=aseq->Length(); i++)
	{
	  values.AddStringValue(aseq->Value(i));
	}
      return 0;
    }
  if(getdeepclasses)
    {      
      Handle(TColStd_HSequenceOfHAsciiString) theseq = 
	WOKBuilder_MSTool::GetMSchema()->SchemaDescrMissingClasses(asch->Name());

      for(i=1; i<=theseq->Length(); i++)
	{
	  values.AddStringValue(theseq->Value(i));
	}
      return 0;
    }
  if(getAllClasses)
    {
      Handle(TColStd_HSequenceOfHAsciiString) aseq = WOKBuilder_MSTool::GetMSchema()->SchemaClasses(asch->Name());

      for(i=1; i<=aseq->Length(); i++)
	{
	  values.AddStringValue(aseq->Value(i));
	}
      return 0;
    }
  if(getSortedClasses)
    {
      Handle(TColStd_HSequenceOfHAsciiString) aseq = WOKBuilder_MSTool::GetMSchema()->SortedSchemaClasses(asch->Name());

      for(i=1; i<=aseq->Length(); i++)
	{
	  values.AddStringValue(aseq->Value(i));
	}
      return 0;
    }
  return 0;
}

