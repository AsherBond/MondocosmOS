// File:	MSAPI_Package.cxx
// Created:	Mon Sep 18 10:43:59 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <MSAPI_Package.ixx>

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
#include <MS_Package.hxx>
#include <MS_ExternMet.hxx>
#include <MS_HSequenceOfExternMet.hxx>


void MSAPI_Package_Info_Usage(char *cmd)
{
  cerr << "usage : " << cmd << "[-u] [-U <apk>] <pkname>\n";
  cerr << "    -u : uses of package\n";
  cerr << "    -U : 1 if pkname is used by apk\n";
  cerr << "    -c : classes of <pkname>\n";
  cerr << "    -x : exceptions of <pkname>\n";
  cerr << "    -e : enums of <pkname>\n";
  cerr << "    -a : aliases of <pkname>\n";
  cerr << "    -p : pointers of <pkname>\n";
  cerr << "    -i : importeds of <pkname>\n";
  cerr << "    -P : primitives of <pkname>\n";
  cerr << "    -m : methods of <pkname>\n";
}



//=======================================================================
//Author   : Jean Gautier (jga)
//function : Info
//purpose  : 
//=======================================================================
Standard_Integer MSAPI_Package::Info(const Standard_Integer argc, const WOKTools_ArgTable& argv, WOKTools_Return& values)
{
  WOKTools_Options opts(argc, argv, "uU:cxeapiPm", MSAPI_Package_Info_Usage, "uU:m");
  Handle(TCollection_HAsciiString) apkisused;
  Handle(TCollection_HAsciiString) pkname;
  Handle(MS_Package)               apk;
  Standard_Integer i;
  Standard_Boolean uses       = Standard_False;
  Standard_Boolean isused     = Standard_False;
  Standard_Boolean classes    = Standard_False;
  Standard_Boolean expections = Standard_False;
  Standard_Boolean enums      = Standard_False;
  Standard_Boolean aliases    = Standard_False;
  Standard_Boolean pointers   = Standard_False;
  Standard_Boolean importeds  = Standard_False;
  Standard_Boolean primitives = Standard_False;
  Standard_Boolean methods    = Standard_False;
  
  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'u':
	  uses       = Standard_True;
	  break;
	case 'U':
	  isused     = Standard_True;
	  apkisused  = opts.OptionArgument();
	  break;
	case 'c':
	  classes    = Standard_True;
	  break;
	case 'x':
	  expections = Standard_True;
	  break;
	case 'e':
	  enums      = Standard_True;
	  break;
	case 'a':
	  aliases    = Standard_True;
	  break;
	case 'p':
	  pointers   = Standard_True;
	  break;
	case 'i':
	  importeds  = Standard_True;
	  break;
	case 'P':
	  primitives = Standard_True;
	  break;
	case 'm':
	  methods    = Standard_True;
	  break;
	}
      opts.Next();
    }
  
  if(opts.Failed() == Standard_True) return 1;

  if(opts.Arguments().IsNull() == Standard_True) {MSAPI_Package_Info_Usage(argv[0]); return 1;}
  
  switch(opts.Arguments()->Length())
    {
    case 1:
      pkname = opts.Arguments()->Value(1);
      break;
    default:
      MSAPI_Package_Info_Usage(argv[0]);
      return 1;
    }
  
  
  if(WOKBuilder_MSTool::GetMSchema()->MetaSchema()->IsPackage(pkname) == Standard_False)
    {
      ErrorMsg() << argv[0] << "Given name (" << pkname->ToCString() << ") is not a known package" << endm;
    }

  apk = WOKBuilder_MSTool::GetMSchema()->MetaSchema()->GetPackage(pkname);


  if(isused)
    {
      if(apk->IsUsed(apkisused)) values.AddStringValue("1");
      else                       values.AddStringValue("0");
      return 0;
    }

  if(methods) 
    {
      for(i=1; i<= apk->Methods()->Length(); i++)
	{
	  values.AddStringValue(apk->Methods()->Value(i)->FullName());
	}
      return 0;
    }

  Handle(TColStd_HSequenceOfHAsciiString) aseq;

  if(uses)       aseq = apk->Uses();
  if(classes)    aseq = apk->Classes();
  if(expections) aseq = apk->Excepts();
  if(enums)      aseq = apk->Enums();
  if(aliases)    aseq = apk->Aliases();
  if(pointers)   aseq = apk->Pointers();
  if(importeds)  aseq = apk->Importeds();
  if(primitives) aseq = apk->Primitives();

  for(i=1; i<=aseq->Length(); i++)
    {
      values.AddStringValue(aseq->Value(i));
    }
  return 0;
}

