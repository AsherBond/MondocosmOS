// File:	WOKStep_Extract.cxx
// Created:	Tue Aug 29 21:40:59 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <WOKStep_Extract.ixx>

#include <WOKTools_Messages.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_Shell.hxx>

#include <WOKBuilder_Entity.hxx>
#include <WOKBuilder_Include.hxx>
#include <WOKBuilder_Compilable.hxx>
#include <WOKBuilder_MSEntity.hxx>
#include <WOKBuilder_HSequenceOfEntity.hxx>
#include <WOKBuilder_MSExtractorIterator.hxx>
#include <WOKBuilder_MSExtractor.hxx>
#include <WOKBuilder_MSchema.hxx>
#include <WOKBuilder_MSActionID.hxx>
#include <WOKBuilder_MSAction.hxx>
#include <WOKBuilder_Command.hxx>

#include <WOKernel_FileType.hxx>
#include <WOKernel_FileTypeBase.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_Locator.hxx>
#include <WOKernel_HSequenceOfFile.hxx>


#include <WOKMake_OutputFile.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKMake_AdmFileTypes.hxx>

//=======================================================================
//function : WOKStep_Extract
//purpose  : 
//=======================================================================
WOKStep_Extract::WOKStep_Extract(const Handle(WOKMake_BuildProcess)& abp,
				 const Handle(WOKernel_DevUnit)& aunit, 
				 const Handle(TCollection_HAsciiString)& acode, 
				 const Standard_Boolean checked, 
				 const Standard_Boolean hidden) 
: WOKStep_MSStep(abp,aunit, acode, checked, hidden)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AdmFileType
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_Extract::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)DBADMFILE);
  return result;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutputDirTypeName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_Extract::OutputDirTypeName() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)DBTMPDIR);
  return result;
}

//=======================================================================
//function : SetExtractor
//purpose  : 
//=======================================================================
void WOKStep_Extract::SetExtractor(const Handle(WOKBuilder_MSExtractor)& anextractor) 
{
  myextractor = anextractor;
}

//=======================================================================
//function : Extractor
//purpose  : 
//=======================================================================
Handle(WOKBuilder_MSExtractor) WOKStep_Extract::Extractor() const 
{
  return myextractor;
}

//=======================================================================
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKStep_Extract::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  if(!infile->IsPhysic())
    {
      if(!strcmp("msentity", infile->ID()->Token(":",2)->ToCString()))
	{
	  infile->SetDirectFlag(Standard_True);
	  infile->SetBuilderEntity(new WOKBuilder_MSEntity(infile->ID()->Token(":",3)));
	  return Standard_True;
	}
    }
  return Standard_False;
}


//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
void WOKStep_Extract::Execute(const Handle(WOKMake_HSequenceOfInputFile)& tobuild)
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
      
      Handle(WOKBuilder_MSEntity) entity = Handle(WOKBuilder_MSEntity)::DownCast(tobuild->Value(j)->BuilderEntity());
      WOKBuilder_MSActionID anid(entity->Name(), Extractor()->ExtractorID());
      
      switch(anit.Execute(entity))
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
	  
	    Handle(WOKBuilder_MSAction) anaction = Extractor()->MSchema()->GetAction(anid);
	    Handle(WOKBuilder_Entity)   outent;

	    if (!anit.Produces()->Length()) 
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
		  }
	      
		Handle(WOKMake_OutputFile) out;

		switch(astatus)
		  {
		  case WOKBuilder_Success:
		    Extractor()->MSchema()->ChangeAddAction(anid, Handle(WOKBuilder_Specification)());

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
		    Extractor()->MSchema()->ChangeAddAction(anid, Handle(WOKBuilder_Specification)());

		    outent->Path()->RemoveFile();
		    outent->SetPath(basefile->Path());
		    out  = new WOKMake_OutputFile(basefile->LocatorName(), basefile, outent, basefile->Path());
		    out->SetLocateFlag(Standard_True);
		    out->SetProduction();
		    if(!istemplate) AddExecDepItem(tobuild->Value(j), out, Standard_True);
		    break;
		  case WOKBuilder_Failed:
		    Extractor()->MSchema()->ChangeActionToFailed(anid);

		    SetFailed();
		    ErrorMsg() << "WOKStep_Extract::Execute" << "Failed    : " << outfile->Name() << endm;
		    break;
		  }
	      }
	  }
	  break;
	case WOKBuilder_Failed:
	  Extractor()->MSchema()->ChangeActionToFailed(anid);

	  ErrorMsg() << "WOKStep_Extract::Execute" << "Failed    : " << entity->Name() << endm;          
	  break;
         default: break;
	}
    }

  ashell->UnLock();

  if(Status() == WOKMake_Unprocessed) SetSucceeded();
  return;
}
