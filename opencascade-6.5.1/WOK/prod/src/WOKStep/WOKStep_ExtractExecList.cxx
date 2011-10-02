// File:	WOKStep_ExtractExecList.cxx
// Created:	Wed Aug 28 18:24:06 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#include <TColStd_HSequenceOfHAsciiString.hxx>


#include <WOKTools_Messages.hxx>

#include <WOKBuilder_MSEntity.hxx>
#include <WOKBuilder_MSchema.hxx>
#include <WOKBuilder_MSTool.hxx>

#ifndef WNT
# include <WOKBuilder_SharedLibrary.hxx>
#else
# include <WOKNT_WNT_BREAK.hxx>
# include <WOKBuilder_ImportLibrary.hxx>
#endif  // WNT

#include <WOKernel_File.hxx>
#include <WOKernel_Locator.hxx>


#include <WOKMake_InputFile.hxx>
#include <WOKMake_AdmFileTypes.hxx>
#include <WOKMake_OutputFile.hxx>

#include <WOKStep_ExtractExecList.ixx>

//=======================================================================
//function : WOKStep_ExtractExecList
//purpose  : 
//=======================================================================
WOKStep_ExtractExecList::WOKStep_ExtractExecList(const Handle(WOKMake_BuildProcess)& abp,
						 const Handle(WOKernel_DevUnit)& aunit, 
						 const Handle(TCollection_HAsciiString)& acode, 
						 const Standard_Boolean checked, 
						 const Standard_Boolean hidden) 
  : WOKMake_MetaStep(abp,aunit, acode, checked, hidden)
{
  
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AdmFileType
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_ExtractExecList::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)ADMFILE);
  return result;   
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutputDirTypeName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_ExtractExecList::OutputDirTypeName() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)TMPDIR);
  return result;   
}

//=======================================================================
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKStep_ExtractExecList::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  if(infile->IsStepID())
    return Standard_True;

 if(!infile.IsNull())
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
//Author   : Jean Gautier (jga)
//function : Execute
//purpose  : 
//=======================================================================
void WOKStep_ExtractExecList::Execute(const Handle(WOKMake_HSequenceOfInputFile)& execlist)
{
  Standard_Integer i;
  Handle(WOKBuilder_MSchema) ameta = WOKBuilder_MSTool::GetMSchema();
  Handle(TColStd_HSequenceOfHAsciiString) aseq;

  if(SubCode().IsNull())
    {
      Standard_Boolean ok = Standard_True;
      //  partie MetaStep
      
      for(i=1; i<=execlist->Length(); i++)
	{
#ifdef WNT
_TEST_BREAK();
#endif  // WNT
	  Handle(WOKMake_InputFile) infile = execlist->Value(i);
	  Handle(WOKBuilder_MSEntity) bent = Handle(WOKBuilder_MSEntity)::DownCast(infile->BuilderEntity());
	    
	  if(!bent.IsNull())
	    {
	      Handle(TCollection_HAsciiString) part = bent->Name();

	      Handle(TCollection_HAsciiString) id = WOKMake_Step::StepOutputID(Unit()->Name(),
									       Code(),
									       part);

	      Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(id, Handle(WOKernel_File)(), 
									  Handle(WOKBuilder_Entity)(), Handle(WOKUtils_Path)());
	      outfile->SetProduction();
	      outfile->SetLocateFlag(Standard_True);
	      outfile->SetPhysicFlag(Standard_False);
	      outfile->SetStepID(Standard_True);

//              cout << "WOKStep_ExtractExecList::Execute -> GetAndAddStep" << endl ;
	      Handle(WOKMake_Step) astep = BuildProcess()->GetAndAddStep(Unit(), Code(), bent->Name());
	      
	      astep->DoExecute();
	      astep->SetTargets(Targets());
	      astep->SetOptions(Options());
	      
	      InfoMsg() << "WOKStep_ExtractExecList::Execute" 
		      << "Extracting Executable list for part : "  << part << endm;
	      
	      switch(astep->Make())
		{
		case WOKMake_Uptodate:
		  InfoMsg() << "WOKMake_MetaStep::Execute"
			  << "========> " << astep->SubCode() << " is uptodate" << endm;
		  break;
		case WOKMake_Success:
		  InfoMsg() << "WOKMake_MetaStep::Execute"
			  << "========> " << astep->SubCode() << " succeeded" << endm;
		  break;
		case WOKMake_Incomplete:
		  WarningMsg() << "WOKMake_MetaStep::Execute"
			     << "========> " << astep->SubCode() << " is incomplete" << endm;
		  break;
		case WOKMake_Failed:
		  ErrorMsg() << "WOKMake_MetaStep::Execute"
			   << "========> " << astep->SubCode() << " failed" << endm;
		  ok = Standard_False;
		  break;
		case WOKMake_Unprocessed:
		  WarningMsg() << "WOKMake_MetaStep::Execute"
			     << "========> " << astep->SubCode() << " is still unprocessed" << endm;
		  ok=Standard_False;
		  break;
                default: break;
		}

	      AddExecDepItem(infile,outfile, Standard_True);
	    }
	}
      if(ok) SetSucceeded();
      else   SetFailed();
    }
  else
    {
      // SubStep (Output Files)
      Standard_Boolean missings = Standard_False;
      Handle(TCollection_HAsciiString) sourcetype    = new TCollection_HAsciiString("source");
      Handle(TCollection_HAsciiString) stadmfiletype = new TCollection_HAsciiString("stadmfile");

      for(i=1;i<=execlist->Length(); i++)
	{
	  Handle(WOKMake_InputFile) infile = execlist->Value(i);
	  Handle(WOKBuilder_MSEntity) bent = Handle(WOKBuilder_MSEntity)::DownCast(infile->BuilderEntity());
	  if(!bent.IsNull())
	    {
	      Handle(TCollection_HAsciiString) part = bent->Name();
	      
	      if(part->IsSameString(SubCode()))
		{
		  Handle(TColStd_HSequenceOfHAsciiString) files = ameta->ExecutableFiles(Unit()->Name(), SubCode());
		  Handle(WOKernel_File) thefile;
		  Standard_Integer j;

		  for(j=1; j<=files->Length(); j++)
		    {
		      thefile =  Locator()->Locate(Unit()->Name(), sourcetype, files->Value(j));
		      
		      if(!thefile.IsNull())
			{
			  Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(thefile->LocatorName(),
										      thefile,
										      Handle(WOKBuilder_Entity)(), thefile->Path());
			  outfile->SetLocateFlag(Standard_True);
			  outfile->SetProduction();
			  AddExecDepItem(infile, outfile, Standard_True);
			}
		      else
			{
			  WarningMsg() << "WOKStep_ExtractExecList::Execute"
				     << "Missing file : " <<  files->Value(j) << " in unit : " << Unit()->Name() << endm;
			  missings = Standard_True;
			}
		    }

		  Handle(TColStd_HSequenceOfHAsciiString) libs = ameta->ExecutableLibraries(Unit()->Name(), SubCode());
		  
		  for(j=1; j<=libs->Length(); j++)
		    {
		       Handle(WOKernel_DevUnit) unit = Locator()->LocateDevUnit(libs->Value(j));
		       
		       if(unit.IsNull())
			 {
			   WarningMsg() << "WOKStep_ExtractExecList::Execute"
				      << "Wrong (or not found) libray uses : " << libs->Value(j) << endm;
			   missings = Standard_True;
			 }
		       else
			 {
			   Handle(WOKernel_File)     NULLFILE;
			   Handle(WOKBuilder_Entity) NULLENTITY;
			   Handle(WOKUtils_Path)     NULLPATH;
			   Handle(TCollection_HAsciiString) libid = new TCollection_HAsciiString(libs->Value(j));

			   libid->AssignCat(":mslibrary:");
			   libid->AssignCat(libs->Value(j));

			   Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(libid, NULLFILE, NULLENTITY, NULLPATH);
			   
			   outfile->SetLocateFlag(Standard_True);
			   outfile->SetPhysicFlag(Standard_False);
			   outfile->SetReference();
			   outfile->SetExtern();
			   AddExecDepItem(infile, outfile, Standard_True); 
			 }
		    }

		  Handle(TColStd_HSequenceOfHAsciiString) externs = ameta->ExecutableExternals(Unit()->Name(), SubCode());
		  
		  for(j=1; j<=externs->Length(); j++)
		    {
		      Handle(WOKernel_File)     NULLFILE;
		      Handle(WOKBuilder_Entity) NULLENTITY;
		      Handle(WOKUtils_Path)     NULLPATH;
		      Handle(TCollection_HAsciiString) externid = new TCollection_HAsciiString(Unit()->Name());

		      externid->AssignCat(":external:");
		      externid->AssignCat(externs->Value(j));

		      Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(externid, NULLFILE, NULLENTITY, NULLPATH);

		      outfile->SetLocateFlag(Standard_True);
		      outfile->SetProduction();
		      outfile->SetPhysicFlag(Standard_False);
		      outfile->SetExtern();
		      
		      AddExecDepItem(infile, outfile, Standard_True); 
		    }
		}
	    }
	}
      if(missings) SetIncomplete();
      else         SetSucceeded();
    }
  return;
}
