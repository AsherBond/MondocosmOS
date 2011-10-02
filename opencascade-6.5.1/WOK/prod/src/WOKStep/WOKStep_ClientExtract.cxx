// File:	WOKStep_ClientExtract.cxx
// Created:	Tue Aug 29 21:41:04 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <Standard_NotImplemented.hxx>

#include <Standard_ProgramError.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKTools_MapIteratorOfMapOfHAsciiString.hxx>

#include <WOKUtils_Shell.hxx>
#include <WOKUtils_Path.hxx>

#include <MS.hxx>

#include <WOKBuilder_MSClientExtractor.hxx>
#include <WOKBuilder_MSActionID.hxx>
#include <WOKBuilder_MSAction.hxx>
#include <WOKBuilder_MSchema.hxx>
#include <WOKBuilder_MSEntity.hxx>
#include <WOKBuilder_Command.hxx>
#include <WOKBuilder_MSExtractorIterator.hxx>
#include <WOKBuilder_HSequenceOfEntity.hxx>

#include <WOKernel_File.hxx>
#include <WOKernel_Locator.hxx>

#include <WOKMake_InputFile.hxx>
#include <WOKMake_OutputFile.hxx>

#include <WOKStep_ClientExtract.ixx>


//=======================================================================
//function : WOKStep_ClientExtract
//purpose  : 
//=======================================================================
WOKStep_ClientExtract::WOKStep_ClientExtract(const Handle(WOKMake_BuildProcess)& abp,
					     const Handle(WOKernel_DevUnit)& aunit, 
					     const Handle(TCollection_HAsciiString)& acode, 
					     const Standard_Boolean checked, 
					     const Standard_Boolean hidden)
: WOKStep_Extract(abp, aunit, acode, checked, hidden)
{
  Handle(WOKBuilder_MSClientExtractor) anextractor = new WOKBuilder_MSClientExtractor(Unit()->Params());
  anextractor->SetMSchema(WOKBuilder_MSTool::GetMSchema());
  SetExtractor(anextractor);
}


//=======================================================================
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKStep_ClientExtract::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
   if(!infile->IsPhysic())
    {
      if(!strcmp("CPPClient_COMPLETE", infile->ID()->Token(":",2)->ToCString()))
	{
	  infile->SetDirectFlag(Standard_True);
	  infile->SetBuilderEntity(new WOKBuilder_MSEntity(infile->ID()->Token(":",3)));
	  return Standard_True;
	}
      if(!strcmp("CPPClient_INCOMPLETE", infile->ID()->Token(":",2)->ToCString()))
	{
	  infile->SetDirectFlag(Standard_True);
	  infile->SetBuilderEntity(new WOKBuilder_MSEntity(infile->ID()->Token(":",3)));
	  return Standard_True;
	}
      if(!strcmp("CPPClient_SEMICOMPLETE", infile->ID()->Token(":",2)->ToCString()))
	{
	  infile->SetDirectFlag(Standard_True);
	  infile->SetBuilderEntity(new WOKBuilder_MSEntity(infile->ID()->Token(":",3)));
	  return Standard_True;
	}
      
    }
  return Standard_False;
 
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Init
//purpose  : 
//=======================================================================
void WOKStep_ClientExtract::Init()
{
  Handle(WOKBuilder_MSClientExtractor) extr = Handle(WOKBuilder_MSClientExtractor)::DownCast(Extractor());

  if(IsToExecute())
    {
      extr->Load();
      extr->Init(Unit()->Name());
    }
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetInputFlow
//purpose  : 
//=======================================================================
void WOKStep_ClientExtract::GetInputFlow()
{
  static Handle(TCollection_HAsciiString) COMP  = new TCollection_HAsciiString("CPPClient_COMPLETE");
  static Handle(TCollection_HAsciiString) ICOMP = new TCollection_HAsciiString("CPPClient_INCOMPLETE");
  static Handle(TCollection_HAsciiString) SCOMP = new TCollection_HAsciiString("CPPClient_SEMICOMPLETE");

  Handle(WOKernel_File) NULLFILE;
  Handle(WOKUtils_Path) NULLPATH;
  Handle(WOKBuilder_Entity) NULLENT;

  Handle(WOKBuilder_MSClientExtractor) ext = Handle(WOKBuilder_MSClientExtractor)::DownCast(Extractor());

  //WOKMake_Step::GetInputFlow();
    
  WOKTools_MapIteratorOfMapOfHAsciiString cit(ext->CompleteTypes());

  while(cit.More())
    {
      
      Handle(TCollection_HAsciiString) id = WOKernel_File::FileLocatorName(Unit()->Name(), COMP, cit.Key());

      Handle(WOKMake_InputFile) infile = new WOKMake_InputFile(id, NULLFILE,NULLENT, NULLPATH); 

      infile->SetLocateFlag(Standard_True);
      infile->SetDirectFlag(Standard_True);
      infile->SetPhysicFlag(Standard_False);

      if(HandleInputFile(infile))
	{
	  myinflow.Add(infile->ID(), infile);
	}
      cit.Next();
    }

  WOKTools_MapIteratorOfMapOfHAsciiString iit(ext->IncompleteTypes());

  while(iit.More())
    {
      
      Handle(TCollection_HAsciiString) id = WOKernel_File::FileLocatorName(Unit()->Name(), ICOMP, iit.Key());

      Handle(WOKMake_InputFile) infile = new WOKMake_InputFile(id, NULLFILE,NULLENT, NULLPATH); 

      infile->SetLocateFlag(Standard_True);
      infile->SetDirectFlag(Standard_True);
      infile->SetPhysicFlag(Standard_False);

      if(HandleInputFile(infile))
	{
	  myinflow.Add(infile->ID(), infile);
	}
      iit.Next();
    }

  WOKTools_MapIteratorOfMapOfHAsciiString sit(ext->SemiCompleteTypes());
  while(sit.More())
    {
      
      Handle(TCollection_HAsciiString) id = WOKernel_File::FileLocatorName(Unit()->Name(), SCOMP, sit.Key());

      Handle(WOKMake_InputFile) infile = new WOKMake_InputFile(id, NULLFILE,NULLENT, NULLPATH); 

      infile->SetLocateFlag(Standard_True);
      infile->SetDirectFlag(Standard_True);
      infile->SetPhysicFlag(Standard_False);

      if(HandleInputFile(infile))
	{
	  myinflow.Add(infile->ID(), infile);
	}
      sit.Next();
    }
  
}

//=======================================================================
//function : OutOfDateEntities
//purpose  : 
//=======================================================================
Handle(WOKMake_HSequenceOfInputFile) WOKStep_ClientExtract::OutOfDateEntities()
{
  Handle(WOKMake_HSequenceOfInputFile) result = new WOKMake_HSequenceOfInputFile;
  Standard_Integer i;
  
  LoadDependencies();

  Handle(WOKBuilder_MSchema) ameta = Extractor()->MSchema();
  
  for(i=1; i<=myinflow.Extent(); i++)
    {
      Handle(WOKMake_InputFile) infile = myinflow(i);
      Handle(WOKBuilder_MSEntity) anent = Handle(WOKBuilder_MSEntity)::DownCast(infile->BuilderEntity());
      
      if(anent.IsNull())
	{
	  ErrorMsg() << "WOKStep_ClientExtract::OutOfDateEntities" 
		   << infile->ID() << " is not a MS Entity" << endm;
	  SetFailed();
	  return result;
	}
      
      WOKBuilder_MSActionID anid(anent->Name(), Extractor()->ExtractorID());
      
      Handle(WOKBuilder_MSAction) anaction = ameta->GetAction(anid);
      
      switch(Extractor()->ExtractionStatus(anaction))
	{
	case WOKBuilder_OutOfDate:
	  result->Append(infile);
	  break;
	case WOKBuilder_UpToDate:
	  break;
	case WOKBuilder_NotDefined:
	  SetFailed();
	  return result;
        default: break;
        }
    }
  return result;
}



//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
void WOKStep_ClientExtract::Execute(const Handle(WOKMake_HSequenceOfInputFile)& tobuild)
{
  Standard_Integer i,j;

  // Used Types
  Handle(WOKernel_FileType) sourcetype       = Unit()->GetFileType("source");
  Handle(WOKernel_FileType) privincludetype  = Unit()->GetFileType("privinclude");
  Handle(WOKernel_FileType) pubincludetype   = Unit()->GetFileType("pubinclude");
  Handle(WOKernel_FileType) derivatedtype    = Unit()->GetFileType("derivated");
  Handle(WOKernel_FileType) englispfiletype  = Unit()->GetFileType("englisp");

  Handle(WOKBuilder_Command) acmd   = new WOKBuilder_Command(new TCollection_HAsciiString("COMMAND"), Unit()->Params());
  Handle(WOKUtils_Shell)     ashell = Shell();

  ashell->Lock();
  acmd->SetShell(ashell);

  Handle(WOKernel_File) outfile, basefile;
  Extractor()->Load();
  Extractor()->SetOutputDir(OutputDir());

  WOKBuilder_MSExtractorIterator anit(WOKBuilder_MSTool::GetMSchema(), Extractor());
  
  for(j=1; j<=tobuild->Length(); j++)
    {
      
      Handle(WOKBuilder_MSEntity)     entity = Handle(WOKBuilder_MSEntity)::DownCast(tobuild->Value(j)->BuilderEntity());
      Handle(TCollection_HAsciiString) amode = tobuild->Value(j)->ID()->Token(":", 2);
      
      switch(anit.Execute(entity, amode->ToCString()))
	{
	case WOKBuilder_Success:
	  {
	    WOK_TRACE {
	      if(VerboseMsg()("WOK_EXTRACT").IsSet())
		{
		  VerboseMsg() << "WOKStep_Extract::Execute"
			     << entity->Name() << " produces : " << endm;
		  for(i=1; i<=anit.Produces()->Length(); i++)
		    {
		      VerboseMsg() << "WOKStep_Extract::Execute" 
				 << "\t\t" << anit.Produces()->Value(i)->Path()->Name() << endm;
		    }
		}
	    }
	  
	    WOKBuilder_MSActionID anid(entity->Name(), WOKBuilder_ClientExtract);
	    Handle(WOKBuilder_MSAction) anaction = Extractor()->MSchema()->GetAction(anid);
	    Handle(WOKBuilder_Entity)   outent;

	    Extractor()->MSchema()->ChangeAddAction(anid, Handle(WOKBuilder_Specification)());

	    for(i=1; i<=anit.Produces()->Length(); i++)
	      {
		Standard_Boolean istemplate = Standard_False;

		outent = anit.Produces()->Value(i);
		switch(outent->Path()->Extension())
		  {
		  case WOKUtils_HXXFile:
		    // a .hxx file is a public include
		    outfile = new WOKernel_File(outent->Path()->FileName(), Unit(), pubincludetype);
		    break;
		  case WOKUtils_IXXFile:
		  case WOKUtils_JXXFile:
		  case WOKUtils_DDLFile:
		    // Private includes
		    outfile = new WOKernel_File(outent->Path()->FileName(), Unit(), privincludetype);
		    break;
		  case WOKUtils_CXXFile:
		    // Devivated Cxx file
		    outfile = new WOKernel_File(outent->Path()->FileName(), Unit(), derivatedtype);
		    break;
		  case WOKUtils_DATFile:
		    // Derivated datafile
		    outfile = new WOKernel_File(outent->Path()->FileName(), Unit(), derivatedtype);
		    break;
		  case WOKUtils_LispFile:
		    // Engine Lisp File
		    outfile = new WOKernel_File(outent->Path()->FileName(), Unit(), englispfiletype);
		    break;
		  case WOKUtils_TemplateFile:
		    outfile = new WOKernel_File(outent->Path()->FileName(), Unit(), sourcetype);
		    istemplate = Standard_True;
		    break;
                   default: break;
		  }
	      
		outfile->GetPath();
		basefile = Locator()->Locate(Unit()->Name(), outfile->TypeName(), outfile->Name());
	      
		WOKBuilder_BuildStatus astatus = WOKBuilder_Unbuilt;
		      
		if(basefile.IsNull())
		  {
		    // pas encore de Fichier : Simply Move
		    astatus = acmd->Move(outent->Path(), outfile->Path());
		  }
		else
		  {
		    if(!outent->Path()->IsSameFile(basefile->Path()))
		      {
			astatus = acmd->Move(outent->Path(), outfile->Path());
		      }
		    //astatus = acmd->ReplaceIfChangedWith(outent->Path(), basefile->Path(), outfile->Path());
		  }
	      
		Handle(WOKMake_OutputFile) out;

		switch(astatus)
		  {
		  case WOKBuilder_Success:
		    outent->SetPath(outfile->Path());
		    out  = new WOKMake_OutputFile(outfile->LocatorName(), outfile, outent, outfile->Path());
		    out->SetLocateFlag(Standard_True);
		    out->SetProduction();
		    if(!istemplate) AddExecDepItem(tobuild->Value(j), out, Standard_True);

		    InfoMsg()  << "WOKStep_Extract::Execute" << "File : " << outfile->Path()->Name() << " is modified" << endm;
		    break;
		  case WOKBuilder_Unbuilt:
		    WOK_TRACE {
		      VerboseMsg()("WOK_EXTRACT")  << "WOKStep_Extract::Execute" 
						 << "File : " << outfile->Path()->Name() << " is unchanged" << endm;
		    }
		    
		    outent->SetPath(basefile->Path());
		    out  = new WOKMake_OutputFile(basefile->LocatorName(), basefile, outent, basefile->Path());
		    out->SetLocateFlag(Standard_True);
		    out->SetProduction();
		    if(!istemplate) AddExecDepItem(tobuild->Value(j), out, Standard_True);
		    break;
		  case WOKBuilder_Failed:
		    SetFailed();
		    ErrorMsg() << "WOKStep_Extract::Execute" << "Failed    : " << outfile->Name() << endm;
		    break;
		  }
	      }
	  }
	  break;
	case WOKBuilder_Failed:
	  ErrorMsg() << "WOKStep_Extract::Execute" << "Failed    : " << entity->Name() << endm;          
	  break;
        default: break;
	}
    }

  ashell->UnLock();

  if(Status() == WOKMake_Unprocessed) SetSucceeded();
  return;
}
