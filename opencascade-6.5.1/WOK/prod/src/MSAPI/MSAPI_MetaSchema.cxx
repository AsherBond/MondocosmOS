// File:	MSAPI_MetaSchema.cxx
// Created:	Fri Sep 15 14:33:17 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <MSAPI_MetaSchema.ixx>

#include <MS_MetaSchema.hxx>
#include <MS_InstClass.hxx>
#include <MS_Package.hxx>
#include <MS_HSequenceOfPackage.hxx>
#include <MS_Schema.hxx>
#include <MS_HSequenceOfSchema.hxx>
#include <MS_Interface.hxx>
#include <MS_HSequenceOfInterface.hxx>
#include <MS_Engine.hxx>
#include <MS_HSequenceOfEngine.hxx>
#include <MS_Executable.hxx>
#include <MS_HSequenceOfExecutable.hxx>
#include <MS_StdClass.hxx>
#include <MS_GenClass.hxx>
#include <MS_HSequenceOfType.hxx>
#include <MS_DataMapIteratorOfMapOfGlobalEntity.hxx>
#include <MS_DataMapIteratorOfMapOfType.hxx>

#include <WOKBuilder_MSTool.hxx>
#include <WOKBuilder_MSchema.hxx>
#include <WOKBuilder_Specification.hxx>
#include <WOKBuilder_CDLFile.hxx>
#include <WOKBuilder_MSTranslatorIterator.hxx>
#include <WOKBuilder_MSAction.hxx>
#include <WOKBuilder_MSActionID.hxx>
#include <WOKBuilder_MSTranslator.hxx>
#include <WOKBuilder_MSExtractor.hxx>
#include <WOKBuilder_MSExtractorIterator.hxx>
#include <WOKBuilder_MSEntity.hxx>
#include <WOKBuilder_HSequenceOfEntity.hxx>
#include <WOKBuilder_HSequenceOfEntity.hxx>
#include <WOKTools_Options.hxx>
#include <WOKTools_Return.hxx>

#include <WOKUtils_Path.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <Standard_ErrorHandler.hxx>

#include <WOKTools_Messages.hxx>

#define MAX_ARGCHAR              512

Standard_IMPORT void MS_ClearMapOfName();

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SearchFile
//purpose  : 
//=======================================================================
Handle(WOKUtils_Path) SearchFile(const Handle(TCollection_HAsciiString)& afile, const Handle(TColStd_HSequenceOfHAsciiString)& aseq)
{
  Standard_Integer i;
  Handle(WOKUtils_Path) apath;
  Handle(TCollection_HAsciiString) astr;

  // dabord regarder si on ne le trouve pas
  apath = new WOKUtils_Path(afile);
  if(apath->Exists() == Standard_True)
    {
      return apath;
    }


  // rechercher ensuite dans les -I
  for(i=1; i<=aseq->Length(); i++)
    {
      astr = new TCollection_HAsciiString(aseq->Value(i));

      astr->AssignCat("/");
      astr->AssignCat(afile);
      
      apath = new WOKUtils_Path(astr);

      if(apath->Exists() == Standard_True) return apath;

    }
  return Handle(WOKUtils_Path)();
}



//=======================================================================
//Author   : Jean Gautier (jga)
//function : SearchEntity
//purpose  : 
//=======================================================================
Handle(WOKUtils_Path) SearchEntity(const Handle(TCollection_HAsciiString)& anentity, const Handle(TColStd_HSequenceOfHAsciiString)& aseq)
{
  Handle(TCollection_HAsciiString) astr = new TCollection_HAsciiString(anentity);
  astr->AssignCat(".cdl");

  return SearchFile(astr, aseq);
}

void MSAPI_MetaSchema_CDLTranslate_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " [-v] [-I<searchdir>] -e <GlobalEntity> -t <Type>\n";
  cerr << "        -v : verbose mode\n";
  cerr << "        -I : directory to search files\n";
  cerr << "        -p : package translation\n";
  cerr << "        -i : interface translation\n";
  cerr << "        -t : Type translation\n";
  cerr << "        -L : shared object path\n";
  cerr << "        -T : Translator name\n";
  return;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : CDLTranslate
//purpose  : 
//=======================================================================
Standard_Integer MSAPI_MetaSchema::Translate(const Standard_Integer argc, const WOKTools_ArgTable& argv, WOKTools_Return& )
{
  WOKTools_Options opts(argc, argv, "vI:p:i:t:L:T:", MSAPI_MetaSchema_CDLTranslate_Usage);
  Standard_Boolean averbose = Standard_False;
  Handle(TColStd_HSequenceOfHAsciiString) incdirectives = new TColStd_HSequenceOfHAsciiString;
  Handle(TCollection_HAsciiString) package;
  Handle(TCollection_HAsciiString) inter;
  Handle(TCollection_HAsciiString) allentity;
  Handle(TCollection_HAsciiString) type;
  Handle(TCollection_HAsciiString) afile;
  Handle(TCollection_HAsciiString) thefile;
  Handle(TCollection_HAsciiString) shared;
  Handle(TCollection_HAsciiString) name;
  Handle(WOKUtils_Path) apath;

  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'v':
	  averbose = Standard_True;
	  break;
	case 'I':
	  incdirectives->Append(opts.OptionArgument());
	  break;
	case 'f':
	  thefile = opts.OptionArgument();
	  break;
	case 'p':
	  package  = opts.OptionArgument();
	  break;
	case 'i':
	  inter  = opts.OptionArgument();
	  break;
	case 't':
	  type    = opts.OptionArgument();
	  break;
	case 'L':
	  shared  = opts.OptionArgument();
	  break;
	case 'T':
	  name    = opts.OptionArgument();
	  break;
	}
      opts.Next();
    }
  
  if(opts.Failed() == Standard_True) return 1;
  
  Handle(TColStd_HSequenceOfHAsciiString) entities = new TColStd_HSequenceOfHAsciiString;
  Handle(TColStd_HSequenceOfHAsciiString) inctypes = new TColStd_HSequenceOfHAsciiString;
  Handle(TColStd_HSequenceOfHAsciiString) instypes = new TColStd_HSequenceOfHAsciiString;
  Handle(TColStd_HSequenceOfHAsciiString) gentypes = new TColStd_HSequenceOfHAsciiString;

  if(!opts.Arguments().IsNull())
    {
      
      switch(opts.Arguments()->Length())
	{
	case 0:
	  break;
	default:
	  MSAPI_MetaSchema_CDLTranslate_Usage(argv[0]);
	  return 1;
	}
    }
  
  Handle(WOKBuilder_MSTranslator) acdlt = new WOKBuilder_MSTranslator(name, shared);
  
  acdlt->Load();
  acdlt->SetMSchema(WOKBuilder_MSTool::GetMSchema());

  Handle(WOKBuilder_Specification) cdlfile;
  
  WOKBuilder_MSTranslatorIterator anit(WOKBuilder_MSTool::GetMSchema());

  if(!package.IsNull())
    {
      anit.AddInStack(package, WOKBuilder_Package);
    }
  if(!inter.IsNull())
    {
      anit.AddInStack(inter, WOKBuilder_Interface);
    }
  if(!type.IsNull())
    {
      anit.AddInStack(WOKBuilder_MSTool::GetMSchema()->AssociatedEntity(type), WOKBuilder_DirectUses);
      anit.AddInStack(type, WOKBuilder_CompleteType);
    }

  while(anit.More())
    {
      Handle(WOKBuilder_MSAction) action = anit.Value();
      
      afile =  WOKBuilder_MSTool::GetMSchema()->AssociatedFile(action->Entity()->Name());
      
      apath = SearchFile(afile,incdirectives);
      
      cdlfile = new WOKBuilder_CDLFile(apath);
      
      if(!apath.IsNull())
	{
	  anit.Execute(acdlt, action, cdlfile);
	}
      else
	{
	  WarningMsg() << argv[0] << "No file for : " << action->Entity()->Name() << endm;
	}
      anit.Next();
    }

  return 0;
}

void MSAPI_MetaSchema_Check_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " <Name>\n";
  return;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Check
//purpose  : 
//=======================================================================
Standard_Integer MSAPI_MetaSchema::Check(const Standard_Integer argc, const WOKTools_ArgTable& argv, WOKTools_Return&)
{
  WOKTools_Options opts(argc, argv, "", MSAPI_MetaSchema_Check_Usage);
  Handle(TCollection_HAsciiString) aname;
  
  while(opts.More())
    {
      switch(opts.Option())
	{
	default:
	  break;
	}
      opts.Next();
    }
  
  if(opts.Failed()) return 1;
 
  switch(opts.Arguments()->Length())
    {
    case 1:
      aname = opts.Arguments()->Value(1);
      break;
    default:
      MSAPI_MetaSchema_Check_Usage(argv[0]);
      return 1;
    }
 
  if(WOKBuilder_MSTool::GetMSchema()->MetaSchema()->Check(aname))
    {
      InfoMsg() << argv[0] << aname << " successfully checked" << endm;
    }
  else
    {
      ErrorMsg() << argv[0] << aname << " check has failed" << endm;
    }
  return 0;
}

void MSAPI_MetaSchema_Extract_Usage(char *cmd)
{
  cerr << "usage : " << cmd << "\n";
  cerr << "        -E : Extractor name\n";
  cerr << "        -I : Search path for EDL Files\n";
  cerr << "        -L : Load Dynamic Library\n";
  cerr << "        -o : Output dir\n";
  cerr << "        -e : Extract global entity\n";
  cerr << "        -t : Extract Type\n";
  cerr << "        -a : Extract entity and its nested types\n";
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : Extract
//purpose  : 
//=======================================================================
Standard_Integer MSAPI_MetaSchema::Extract(const Standard_Integer argc, const WOKTools_ArgTable& argv, WOKTools_Return& )
{
  WOKTools_Options opts(argc, argv, "E:I:L:o:e:t:a:", MSAPI_MetaSchema_Extract_Usage);
  Handle(TCollection_HAsciiString) aname;
  Handle(TCollection_HAsciiString) ashared;
  Handle(TCollection_HAsciiString) anoutdir;
  Standard_Boolean entity = Standard_False;
  Standard_Boolean type   = Standard_False;
  Standard_Boolean all    = Standard_False;
  Handle(TColStd_HSequenceOfHAsciiString) entities   = new TColStd_HSequenceOfHAsciiString;
  Handle(TColStd_HSequenceOfHAsciiString) types      = new TColStd_HSequenceOfHAsciiString;
  Handle(TColStd_HSequenceOfHAsciiString) alls       = new TColStd_HSequenceOfHAsciiString;
  Handle(TColStd_HSequenceOfHAsciiString) searchlist = new TColStd_HSequenceOfHAsciiString;
  Handle(MS_Package) apk;
  Standard_Integer i,j;

  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'E':
	  aname = opts.OptionArgument();
	  break;
	case 'I':
	  searchlist->Append(opts.OptionArgument());
	  break;
	case 'L':
	  ashared = opts.OptionArgument();
	  break;
	case 'o':
	  anoutdir = opts.OptionArgument();
	  break;
	case 'e':
	  entity = Standard_True;
	  entities->Append(opts.OptionArgument());
	  break;
	case 't':
	  type = Standard_True;
	  types->Append(opts.OptionArgument());
	  break;
	case 'a':
	  all = Standard_True;
	  alls->Append(opts.OptionArgument());
	  break;
	}
      opts.Next();
    }

  if(opts.Failed()) return 1;

  switch(opts.Arguments()->Length())
    {
    case 0:
      break;
    default:
      MSAPI_MetaSchema_Extract_Usage(argv[0]);
      return 1;
    }

  Handle(WOKBuilder_HSequenceOfEntity) extr_entities = new WOKBuilder_HSequenceOfEntity;

  if(entity)
    {
      for(i=1; i<=entities->Length(); i++)
	{
	  extr_entities->Append(new WOKBuilder_MSEntity(entities->Value(i)));
	}
    }
  if(type)
    {
      for(i=1; i<=types->Length(); i++)
	{
	   extr_entities->Append(new WOKBuilder_MSEntity(types->Value(i)));
	}
    }
  if(all)
    {
      Handle(TCollection_HAsciiString) fullname;

      for(i=1; i<=alls->Length(); i++)
	{
	  Handle(TColStd_HSequenceOfHAsciiString) aseq;

	  aseq = WOKBuilder_MSTool::GetMSchema()->GetEntityTypes(alls->Value(i));

	  for(j=1; j<=aseq->Length(); j++)
	    {
	      extr_entities->Append(new WOKBuilder_MSEntity(aseq->Value(j)));
	    }
	}
    }

  Handle(WOKBuilder_MSExtractor) anextractor;// = new WOKBuilder_MSExtractor(aname, ashared, searchlist);
  Handle(WOKBuilder_HSequenceOfEntity) aseq;
  Handle(WOKBuilder_MSEntity) theentity;

  anextractor->Load();
  anextractor->SetOutputDir(new WOKUtils_Path(anoutdir));


  WOKBuilder_MSExtractorIterator anit(WOKBuilder_MSTool::GetMSchema(), 
				      anextractor);
 
  
  for(j=1; j<=extr_entities->Length(); j++)
    {
      theentity = Handle(WOKBuilder_MSEntity)::DownCast(extr_entities->Value(j));
      
      InfoMsg() << argv[0] << "Extracting : " << theentity->Name() << endm;
      
      anit.Execute(theentity);
      
      aseq = anextractor->Produces();
      
      for(Standard_Integer i=1; i<=aseq->Length(); i++)
	{
	  InfoMsg() << argv[0] << "File " << aseq->Value(i)->Path()->Name() << " is extracted" << endm;
	}
      
    }
  return 0;
}

void MSAPI_MetaSchema_Info_Usage(char *cmd)
{
  cerr << "usage : " << cmd << "\n";
  cerr << "        -p : package list\n";
  cerr << "        -s : schema list\n";
  cerr << "        -i : interface list\n";
  cerr << "        -e : engine list\n";
  cerr << "        -x : executable list\n";
  cerr << "        -a : all global entity list\n";
  cerr << "        -t : all type list\n";
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Info
//purpose  : 
//=======================================================================
Standard_Integer MSAPI_MetaSchema::Info(const Standard_Integer argc, const WOKTools_ArgTable& argv, WOKTools_Return& values)
{
  WOKTools_Options opts(argc, argv, "psiexatf:", MSAPI_MetaSchema_Info_Usage, "atf");
  Standard_Boolean packages    = Standard_False;
  Standard_Boolean schemas     = Standard_False;
  Standard_Boolean interfaces  = Standard_False;
  Standard_Boolean engines     = Standard_False;
  Standard_Boolean executables = Standard_False;
  Standard_Boolean all         = Standard_False;
  Standard_Boolean types       = Standard_False;
  Handle(TCollection_HAsciiString) anentityname;
  
  
  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'p':
	  packages    = Standard_True;
	  break;
	case 's':
	  schemas     = Standard_True;
	  break;
	case 'i':
	  interfaces  = Standard_True;
	  break;
	case 'e':
	  engines     = Standard_True;
	  break;
	case 'x':
	  executables = Standard_True;
	  break;
	case 'a':
	  all         = Standard_True;
	  break;
	case 't':
	  types       = Standard_True;
	  break;
	}
      opts.Next();
    }
  
  if(opts.Failed() == Standard_True) return 1;
  
  if(opts.Arguments().IsNull() == Standard_False) 
    {
      switch(opts.Arguments()->Length())
	{
	case 0:
	  break;
	default:
	  MSAPI_MetaSchema_Info_Usage(argv[0]);
	  return 1;
	}
    }
  
  Handle(MS_MetaSchema) ameta = WOKBuilder_MSTool::GetMSchema()->MetaSchema();
  Handle(TColStd_HSequenceOfHAsciiString) aseq;
  
  if(types)  
    {
      MS_DataMapIteratorOfMapOfType anit = ameta->Types();
      
      while(anit.More())
	{
	  values.AddStringValue(anit.Key());
	  anit.Next();
	}
      return 0;
    }
  
  
  if(packages||interfaces||schemas||engines||executables||all) 
    {
      MS_DataMapIteratorOfMapOfGlobalEntity anit;

      if(packages||all)    {anit = ameta->Packages();    while(anit.More()){values.AddStringValue(anit.Key());anit.Next();}}
      if(interfaces||all)  {anit = ameta->Interfaces();  while(anit.More()){values.AddStringValue(anit.Key());anit.Next();}}
      if(schemas||all)     {anit = ameta->Schemas();     while(anit.More()){values.AddStringValue(anit.Key());anit.Next();}}
      if(executables||all) {anit = ameta->Executables(); while(anit.More()){values.AddStringValue(anit.Key());anit.Next();}}
      if(engines||all)     {anit = ameta->Engines();     while(anit.More()){values.AddStringValue(anit.Key());anit.Next();}}

      return 0;
    }
  
  return 0;
}

void MSAPI_MetaSchema_Remove_Usage(char *cmd)
{
  cerr << "usage : " << cmd << " [-e|-t] <Name>\n";
  cerr << "         -e : Remove global entity\n";
  cerr << "         -t : Remove Type\n";
  return;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Remove
//purpose  : 
//=======================================================================
Standard_Integer MSAPI_MetaSchema::Remove(const Standard_Integer argc, const WOKTools_ArgTable& argv, WOKTools_Return& )
{
  WOKTools_Options opts(argc, argv, "ie:t:", MSAPI_MetaSchema_Remove_Usage, "et");
  Standard_Boolean entity = Standard_False;
  Standard_Boolean type   = Standard_False;
  Handle(TCollection_HAsciiString) name;
  
  while(opts.More())
    {
      switch(opts.Option())
	{
	case 'e':
	  entity  = Standard_True;
	  name    = opts.OptionArgument();
	  break;
	case 't':
	  type = Standard_True;
	  name = opts.OptionArgument();
	  break;
	}
      opts.Next();
    }
  
  if(opts.Failed() == Standard_True) return 1;
  
  if(opts.Arguments().IsNull() == Standard_False) 
    {
      switch(opts.Arguments()->Length())
	{
	case 0:
	  break;
	default:
	  MSAPI_MetaSchema_Remove_Usage(argv[0]);
	  return 1;
	}
    }
  
  Handle(WOKBuilder_MSchema) ameta = WOKBuilder_MSTool::GetMSchema();
  
  if(entity)
    {
      if(ameta->IsDefined(name) == Standard_False)
	{
	  ErrorMsg() << argv[0] << "Name (" << name << ") is unknown" << endm;
	  return 1;
	}
      ameta->RemoveEntity(name);
      return 0;
    }
  if(type)
    {
      if(ameta->IsDefined(name) == Standard_False)
	{
	  ErrorMsg() << argv[0] << "Name (" << name << ") is unknown" << endm;
	  return 1;
	}
      ameta->RemoveType(name);
      return 0;
    }
  return 0;
} 

void MSAPI_MetaSchema_Clear_Usage(char *cmd)
{
  cerr << "usage : " << cmd << "\n";
  return;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Clear
//purpose  : 
//=======================================================================
Standard_Integer MSAPI_MetaSchema::Clear(const Standard_Integer argc, const WOKTools_ArgTable& argv, WOKTools_Return& )
{
  WOKTools_Options opts(argc, argv, "awmnp", MSAPI_MetaSchema_Clear_Usage);
  Standard_Boolean
    autotypes = Standard_False, woksd = Standard_False, meta = Standard_False, names = Standard_False, purge = Standard_False;
  

  while(opts.More())
    {
      switch (opts.Option())
	{
	case 'a':
	  autotypes = Standard_True;
	  break;
	case 'w':
	  woksd = Standard_True;
	  break;
	case 'm':
	  meta = Standard_True;
	  break;
	case 'n':
	  names = Standard_True;
	  break;
	case 'P':
	  purge = Standard_True;
	  break;
	default:
	  break;
	}
      opts.Next();
    }
  
  if(opts.Failed() == Standard_True) return 1;

  if(!autotypes && !woksd && !meta && !names && !purge)
    {
      MS_ClearMapOfName();
      WOKBuilder_MSTool::GetMSchema()->Clear();
      Standard::Purge();
    }
  else
    {
      if(autotypes)
	{
	  WOKBuilder_MSTool::GetMSchema()->RemoveAutoTypes();
	}
      if(woksd)
	{
	  Handle(MS_MetaSchema) ameta = WOKBuilder_MSTool::GetMSchema()->MetaSchema();
	  WOKBuilder_MSTool::GetMSchema()->Clear();

	  *((Handle(MS_MetaSchema) *) &(WOKBuilder_MSTool::GetMSchema()->MetaSchema())) = ameta;
	}
      if(meta)
	{
	  (*((Handle(MS_MetaSchema) *) &(WOKBuilder_MSTool::GetMSchema()->MetaSchema()))).Nullify();
	}
      if(names)
	{
	  MS_ClearMapOfName();
	}
      if(purge)
	{
	  Standard::Purge();
	}
    }
  
  return 0;
}

