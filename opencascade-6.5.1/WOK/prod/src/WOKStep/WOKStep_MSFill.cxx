// File:	WOKStep_MSFill.cxx
// Created:	Tue Aug 29 21:41:16 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#ifdef HAVE_CONFIG_H
# include <config.h>
#endif


#include <WOKTools_Messages.hxx>
#include <WOKUtils_Path.hxx>

#include <MS_MetaSchema.hxx>
#include <MS_Engine.hxx>

#include <WOKBuilder_HSequenceOfEntity.hxx>
#include <WOKBuilder_CDLFile.hxx>
#include <WOKBuilder_MSTranslator.hxx>
#include <WOKBuilder_MSEntity.hxx>
#include <WOKBuilder_MSAction.hxx>
#include <WOKBuilder_MSTranslatorIterator.hxx>
#include <WOKBuilder_MSchema.hxx>

#include <WOKernel_Workbench.hxx>
#include <WOKernel_Session.hxx>
#include <WOKernel_BasicUnitTypes.hxx>
#include <WOKernel_FileType.hxx>
#include <WOKernel_FileTypeBase.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_Locator.hxx>
#include <WOKernel_HSequenceOfFile.hxx>

#include <WOKMake_OutputFile.hxx>
#include <WOKMake_AdmFileTypes.hxx>

#include <WOKStep_MSFill.ixx>

#ifdef WNT
# include <WOKNT_WNT_BREAK.hxx>
# include <windows.h>
# define sleep( nSec ) Sleep (  1000 * ( nSec )  )
#endif  // WNT

//=======================================================================
//function : WOKStep_MSFill
//purpose  : 
//=======================================================================
 WOKStep_MSFill::WOKStep_MSFill(const Handle(WOKMake_BuildProcess)& abp,
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
Handle(TCollection_HAsciiString) WOKStep_MSFill::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)ADMFILE);
  return result;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutputDirTypeName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_MSFill::OutputDirTypeName() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)TMPDIR);
  return result;
}

//=======================================================================
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKStep_MSFill::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  if(infile->File()->Path()->Extension() == WOKUtils_CDLFile)
    {
      infile->SetBuilderEntity(new WOKBuilder_CDLFile(infile->File()->Path()));
      infile->SetDirectFlag(Standard_True);
      infile->SetLocateFlag(Standard_True);
      return Standard_True;
    }
  return Standard_False;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutOfDateEntities
//purpose  : 
//=======================================================================
Handle(WOKMake_HSequenceOfInputFile) WOKStep_MSFill::OutOfDateEntities()
{
  return ForceBuild();
}

//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
void WOKStep_MSFill::Execute(const Handle(WOKMake_HSequenceOfInputFile)& )
{
  Handle(WOKBuilder_MSTranslator) acdlt = new WOKBuilder_MSTranslator(new TCollection_HAsciiString("CDLTranslate"), Unit()->Params());
  Handle(WOKBuilder_MSchema) ams = WOKBuilder_MSTool::GetMSchema();
  Handle(WOKBuilder_Specification) aspec;
  Handle(WOKernel_Workbench) abench = Unit()->Session()->GetWorkbench(Unit()->Nesting());
  Handle(WOKernel_File) acdlfile, gefile;
  Handle(TCollection_HAsciiString) astr;
  Handle(TCollection_HAsciiString) aunitname;
  Handle(TCollection_HAsciiString) sourcetype = new TCollection_HAsciiString("source");
  Handle(TColStd_HSequenceOfHAsciiString) aseq;
  Standard_Integer i;
  Standard_Boolean stop = Standard_False;

  acdlt->Load();

  acdlt->SetMSchema(WOKBuilder_MSTool::GetMSchema());

  gefile = Locator()->Locate(Unit()->Name(), sourcetype, ams->AssociatedFile(Unit()->Name()));

  if(gefile.IsNull())
    {
      ErrorMsg() << "WOKStep_MSFill::Execute" << "Missing file : " << ams->AssociatedFile(Unit()->Name()) << endm;
      SetFailed();
      return;
    }

  
  WOKBuilder_MSTranslatorIterator& anit = BuildProcess()->TranslatorIterator();
  Handle(WOKBuilder_MSEntity) theentity;
  
  if(WOKernel_IsPackage(Unit()))
    {
      anit.AddInStack(Unit()->Name(), WOKBuilder_Package);
    }
  else if(WOKernel_IsSchema(Unit()))
    {
      anit.AddInStack(Unit()->Name(), WOKBuilder_Schema);
    }
  else if(WOKernel_IsInterface(Unit()))
    {
      anit.AddInStack(Unit()->Name(), WOKBuilder_Interface);
    }
  else if(WOKernel_IsClient(Unit()))
    {
      anit.AddInStack(Unit()->Name(), WOKBuilder_Client);
    }
  else if(WOKernel_IsEngine(Unit()))
    {
      anit.AddInStack(Unit()->Name(), WOKBuilder_Engine);
    }
  else if (Unit()->TypeCode() == 'j')
    {
      anit.AddInStack(Unit()->Name(), WOKBuilder_Client);
    }
      


  while(anit.More() && !stop)
    {
#ifdef WNT
_TEST_BREAK();
#endif  // WNT
      Handle(WOKBuilder_MSAction) anaction = anit.Value();

      astr = ams->AssociatedFile(anaction->Entity()->Name());
      aunitname = ams->AssociatedEntity(anaction->Entity()->Name());
      
      acdlfile = Locator()->Locate(aunitname, new TCollection_HAsciiString("source"), astr);
      
      if(!acdlfile.IsNull())
	{
	  aspec = new WOKBuilder_CDLFile(acdlfile->Path());
	  
	  switch(anit.Execute(acdlt, anaction, aspec))
	    {
	    case WOKBuilder_Unbuilt:
	    case WOKBuilder_Success:
	      break;
	    case WOKBuilder_Failed:
	      ErrorMsg() << "WOKStep_MSFill::Execute"
		       << "Errors occured while executing " << anaction->Entity()->Name() << endm;
	      anit.Reset();
	      stop = Standard_True;
	      break;
	    }
	}
      else
	{
	  ErrorMsg() << "WOKStep_MSFill::Execute" 
		   << "No file " << astr << " in " << aunitname << endm;
	  stop = Standard_True;
	}
      
      anit.Next();
    }
  
  if(!stop)
    {
      Handle(WOKBuilder_MSchema) ams = WOKBuilder_MSTool::GetMSchema();
      
      if(!ams->MetaSchema()->Check(Unit()->Name()))
	{
	  ErrorMsg() << "WOKStep_MSFill::Execute" 
		   << "Check of " << Unit()->Name() << " has failed" << endm;
	  stop = Standard_True;
	}
      else
	{
	  InfoMsg() << "WOKStep_MSFill::Execute" 
		   << "Check of " << Unit()->Name() << " succeeded" << endm;
	}
    }

  if(!stop)
    {
      Handle(TCollection_HAsciiString) msid;
      static Handle(TCollection_HAsciiString) msentity = new TCollection_HAsciiString("msentity");
      Handle(WOKernel_File) origin;
      Handle(WOKBuilder_MSchema) ameta = WOKBuilder_MSTool::GetMSchema();
      Standard_Integer j;

      aseq = WOKBuilder_MSTool::GetMSchema()->GetEntityTypes(Unit()->Name());
	      
      for(i=1; i<=aseq->Length(); i++)
	{
#ifdef WNT
_TEST_BREAK();
#endif  // WNT
	  origin = Locator()->Locate(Unit()->Name(), sourcetype, 
				     WOKBuilder_MSTool::GetMSchema()->AssociatedFile(aseq->Value(i)));
		  
	  Handle(WOKMake_InputFile)  infile;

	  if(myinflow.Contains(origin->LocatorName()))
	    {
	      infile = myinflow.FindFromKey(origin->LocatorName());
	    }
	  
	  if(infile.IsNull())
	    {
	      WarningMsg() << "WOKStep_MSFill::Execute" << origin->LocatorName()  << " is not an input of this step" << endm;
	      WarningMsg() << "WOKStep_MSFill::Execute" << "Perhaps performing step (src) is needed" << endm;
	    }
	  else
	    {
	      Handle(WOKernel_File) NULLFILE;
	      Handle(WOKUtils_Path) NULLPATH;
	      msid = WOKernel_File::FileLocatorName(Unit()->Name(), msentity, aseq->Value(i));


	      Handle(WOKBuilder_Specification)  specfile = Handle(WOKBuilder_Specification)::DownCast(infile->BuilderEntity());
	      Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(msid, NULLFILE,
									  new WOKBuilder_MSEntity(specfile, aseq->Value(i)),
									  NULLPATH);
	      outfile->SetLocateFlag(Standard_True);
	      outfile->SetProduction();
	      outfile->SetPhysicFlag(Standard_False);
	      AddExecDepItem(infile, outfile, Standard_True);

	      // Traitement du schema
	      Handle(MS_Exec) exec;
	      if(ameta->MetaSchema()->IsExecutable(Unit()->Name())) exec = ameta->MetaSchema()->GetExecutable(Unit()->Name());
	      if(ameta->MetaSchema()->IsEngine(Unit()->Name()))     exec = ameta->MetaSchema()->GetEngine(Unit()->Name());
	      
	      
	      if(!exec.IsNull())
		{
		  Handle(TCollection_HAsciiString) asch = exec->Schema();
		  if(!asch.IsNull())
		    {
		      Handle(WOKernel_File) NULLFILE;
		      Handle(WOKUtils_Path) NULLPATH;
		      msid = WOKernel_File::FileLocatorName(Unit()->Name(), msentity, asch);
		      
		      Handle(WOKBuilder_Specification)  specfile = Handle(WOKBuilder_Specification)::DownCast(infile->BuilderEntity());
		      Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(msid, NULLFILE,
										  new WOKBuilder_MSEntity(asch),
										  NULLPATH);
		      outfile->SetLocateFlag(Standard_True);
		      outfile->SetReference();
		      outfile->SetPhysicFlag(Standard_False);
		      AddExecDepItem(infile, outfile, Standard_True);
		    }
		}

	      // Traitement des Interfaces
	      if(ameta->MetaSchema()->IsEngine(Unit()->Name()))
		{
		  Handle(MS_Engine) engine = ameta->MetaSchema()->GetEngine(Unit()->Name());
		  Handle(TColStd_HSequenceOfHAsciiString) ints = engine->Interfaces();
		  
		  for(j=1; j<=ints->Length(); j++)
		    {
#ifdef WNT
_TEST_BREAK();
#endif  // WNT
		      Handle(WOKernel_DevUnit) aunit = Locator()->LocateDevUnit(ints->Value(j));

		      if(!aunit.IsNull())
			{
			  if(WOKernel_IsInterface(aunit))
			    {
			      Handle(WOKernel_File) NULLFILE;
			      Handle(WOKUtils_Path) NULLPATH;
			      msid = WOKernel_File::FileLocatorName(Unit()->Name(), msentity, ints->Value(j));
			      
			      Handle(WOKBuilder_Specification)  specfile = Handle(WOKBuilder_Specification)::DownCast(infile->BuilderEntity());
			      Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(msid, NULLFILE,
											  new WOKBuilder_MSEntity(aunit->Name()),
											  NULLPATH);
			      outfile->SetLocateFlag(Standard_True);
			      outfile->SetReference();
			      outfile->SetPhysicFlag(Standard_False);
			      AddExecDepItem(infile, outfile, Standard_True);
			    }
			  else
			    {
			      ErrorMsg() << "WOKStep_MSFill::Execute" 
				       << "Unit : " << aunit->Name() << " should be an interface and is not" << endm;
			      stop = Standard_True;
			    }
			}
		      else
			{
			  ErrorMsg() << "WOKStep_MSFill::Execute" 
				   << "Unit : " << ints->Value(i) << " could not be located" << endm;
			  stop = Standard_True;
			}
		    }


		  Handle(WOKernel_DevUnit) aunit = Locator()->LocateDevUnit(new TCollection_HAsciiString("EngineInterface"));
		  
		  if(!aunit.IsNull())
		    {
		      if(WOKernel_IsInterface(aunit))
			{
			  Handle(WOKernel_File) NULLFILE;
			  Handle(WOKUtils_Path) NULLPATH;
			  msid = WOKernel_File::FileLocatorName(Unit()->Name(), msentity, aunit->Name());
			  
			  Handle(WOKBuilder_Specification)  specfile = Handle(WOKBuilder_Specification)::DownCast(infile->BuilderEntity());
			  Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(msid, NULLFILE,
										      new WOKBuilder_MSEntity(aunit->Name()),
										      NULLPATH);
			  outfile->SetLocateFlag(Standard_True);
			  outfile->SetReference();
			  outfile->SetPhysicFlag(Standard_False);
			  AddExecDepItem(infile, outfile, Standard_True);
			}
		      else
			{
			  ErrorMsg() << "WOKStep_MSFill::Execute" 
				   << "Unit : " << aunit->Name() << " should be an interface and is not" << endm;
			  stop = Standard_True;
			}
		    }
		  else
		    {
		      ErrorMsg() << "WOKStep_MSFill::Execute" 
			       << "Unit : EngineInterface could not be located" << endm;
		      stop = Standard_True;
		    }
		}
	    }
	}
    }
  
  if(stop)
    SetFailed();
  else
    SetSucceeded();
}
