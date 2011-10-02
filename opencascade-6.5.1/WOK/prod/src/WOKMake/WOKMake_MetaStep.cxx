// File:	WOKMake_MetaStep.cxx
// Created:	Mon Aug 26 17:12:32 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>

#include <WOKTools_Messages.hxx>

#include <WOKernel_Locator.hxx>
#include <WOKernel_File.hxx>

#include <WOKUtils_Path.hxx>

#include <WOKMake_AdmFileTypes.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_HSequenceOfOutputFile.hxx>

#include <WOKMake_MetaStep.ixx>



//=======================================================================
//function : WOKMake_MetaStep
//purpose  : 
//=======================================================================
WOKMake_MetaStep::WOKMake_MetaStep(const Handle(WOKMake_BuildProcess)& aprocess,
				   const Handle(WOKernel_DevUnit)& aunit, 
				   const Handle(TCollection_HAsciiString)& acode, 
				   const Standard_Boolean checked, 
				   const Standard_Boolean hidden) 
  : WOKMake_Step(aprocess, aunit, acode, checked, hidden)
{
  
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetAdmFileType
//purpose  : 
//=======================================================================
void WOKMake_MetaStep::SetAdmFileType(const Handle(TCollection_HAsciiString)& atype)
{
  myadmtype = atype;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AdmFileType
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKMake_MetaStep::AdmFileType() const
{
  return myadmtype;   
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetOutputDirTypeName
//purpose  : 
//=======================================================================
void WOKMake_MetaStep::SetOutputDirTypeName(const Handle(TCollection_HAsciiString)& atype)
{
  myouttype = atype;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutputDirTypeName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKMake_MetaStep::OutputDirTypeName() const
{
  return myadmtype;   
}

//=======================================================================
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKMake_MetaStep::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  if(infile->IsStepID())
    return Standard_True;
  return Standard_False;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetUnderlyingSteps
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKMake_MetaStep::GetUnderlyingSteps()
{
  Standard_Integer i,j;
  Handle(TColStd_HSequenceOfHAsciiString) subcodeseq = new TColStd_HSequenceOfHAsciiString;
  Handle(TColStd_HSequenceOfHAsciiString) result     = new TColStd_HSequenceOfHAsciiString;
  Handle(TColStd_HSequenceOfHAsciiString) NULLRESULT;

  for(i=1; i<=myinflow.Extent(); i++)
    {
      Handle(WOKMake_InputFile) infile = myinflow(i);
      
      if(infile->IsStepID())
	{
	  Handle(TCollection_HAsciiString) aunit  = infile->ID()->Token(":", 1);
	  Handle(TCollection_HAsciiString) acode  = infile->ID()->Token(":", 2);
	  Handle(TCollection_HAsciiString) apart  = infile->ID()->Token(":", 3);
	  Standard_Boolean known = Standard_False;
	  
	  if(!apart->IsEmpty())
	    {
	      for(j=1;j<=subcodeseq->Length(); j++) 
		if(!strcmp(apart->ToCString(), subcodeseq->Value(j)->ToCString()))
		  known = Standard_True;
	      
	      if(!known)
		{
		  subcodeseq->Append(apart);
		}
	    }
	}
    }

  for(i=1; i<=subcodeseq->Length(); i++)
    {
      Handle(TCollection_HAsciiString) thesubcode = subcodeseq->Value(i);
//      cout << "WOKMake_MetaStep::GetUnderlyingSteps1 -> GetAndAddStep" << endl ;
      Handle(WOKMake_Step) thestep = BuildProcess()->GetAndAddStep(Unit(), Code(), thesubcode);

      if(thestep.IsNull())
	{
	  ErrorMsg() << "WOKMake_MetaStep::GetUnderlyingSteps" 
		   << "Could not obtain step for code : " << Code() << " in unit " << Unit()->Name() << endm;
	  SetFailed();
	  return NULLRESULT;
	}
      
      Handle(TColStd_HSequenceOfHAsciiString)  precsteps = new TColStd_HSequenceOfHAsciiString;

      for(j=1; j<=myinflow.Extent(); j++)
	{
	  Handle(WOKMake_InputFile) infile = myinflow(j);
	  Handle(TCollection_HAsciiString) aunit  = infile->ID()->Token(":", 1);
	  Handle(TCollection_HAsciiString) acode  = infile->ID()->Token(":", 2);
	  Handle(TCollection_HAsciiString) apart  = infile->ID()->Token(":", 3);
	  Handle(WOKMake_Step) precstep;


	  Handle(WOKernel_DevUnit) precunit = Locator()->LocateDevUnit(aunit);
	  
	  if(precunit.IsNull())
	    {
	      ErrorMsg() << "WOKMake_MetaStep::Execute" 
		       << "Specified unit (" << aunit << ") in input of step " << Code() 
		       << " of " << Unit()->Name() << " could not be found" << endm;
	      SetFailed();
	      return NULLRESULT;
	    }
	  
	  if(apart->IsEmpty())
	    {
//              cout << "WOKMake_MetaStep::GetUnderlyingSteps2 -> GetAndAddStep" << endl ;
	      precstep = BuildProcess()->GetAndAddStep(precunit, acode, Handle(TCollection_HAsciiString)());
	    }
	  else
	    {
//              cout << "WOKMake_MetaStep::GetUnderlyingSteps3 -> GetAndAddStep" << endl ;
	      precstep = BuildProcess()->GetAndAddStep(precunit, acode, apart);
	    }
	  
	  if(precstep.IsNull())
	    {
	      ErrorMsg() << "WOKMake_MetaStep::GetUnderlyingSteps" 
		       << "Could not obtain step for code : " << acode << " in unit " << precunit->Name() << endm;
	      SetFailed();
	      return NULLRESULT;
	    }

	  if(!strcmp(thesubcode->ToCString(), apart->ToCString()) || apart->IsEmpty())
	    {
	      precsteps->Append(precstep->UniqueName());
	    }
	}

      // verifer que chaque precedence step de la MetaStep est une precedence step de 
      // la sous step. sinon l'ajouter telle quelle.

      for(Standard_Integer l=1; l<=PrecedenceSteps()->Length(); l++)
	{
	  const Handle(WOKMake_Step)& precstep = BuildProcess()->Find(PrecedenceSteps()->Value(l));
	  const Standard_CString      preccode = precstep->Code()->ToCString();
	  Standard_Boolean found = Standard_False;

	  for(Standard_Integer k=1; k<=precsteps->Length() && !found; k++)
	    {
	      if(!strcmp(preccode, BuildProcess()->Find(precsteps->Value(k))->Code()->ToCString()))
		found = Standard_True;
	    }

	  if(!found)
	    {
	      precsteps->Append(precstep->UniqueName());
	      Handle(WOKMake_InputFile) infile = new WOKMake_InputFile(precstep->StepOutputID(),
								       Handle(WOKernel_File)(),
								       Handle(WOKBuilder_Entity)(),
								       Handle(WOKUtils_Path)());
	      infile->SetLocateFlag(Standard_True);
	      infile->SetStepID(Standard_True);
	      infile->SetDirectFlag(Standard_True);
	      infile->SetPhysicFlag(Standard_False);
	      myinflow.Add(infile->ID(), infile);
	    }
	}

      if(IsToExecute())
	thestep->DoExecute();

      thestep->SetPrecedenceSteps(precsteps);
      thestep->SetTargets(Targets());
      thestep->SetOptions(Options());

      result->Append(thestep->UniqueName());

    }

  return result;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetLastUnderlyingSteps
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKMake_MetaStep::GetLastUnderlyingSteps()
{
  Standard_Integer i;

  Handle(WOKMake_HSequenceOfOutputFile) out = OutputFileList();
  Handle(TColStd_HSequenceOfHAsciiString)       result = new TColStd_HSequenceOfHAsciiString;
  Handle(TColStd_HSequenceOfHAsciiString) NULLRESULT;
  
  
  if (out.IsNull()) return result;
 
  for(i=1; i<=out->Length(); i++)
    {
      const Handle(WOKMake_OutputFile)& outfile = out->Value(i);

      if(outfile->IsStepID())
	{
	  Handle(TCollection_HAsciiString) aunit  = outfile->ID()->Token(":", 1);
	  Handle(TCollection_HAsciiString) acode  = outfile->ID()->Token(":", 2);
	  Handle(TCollection_HAsciiString) apart  = outfile->ID()->Token(":", 3);

//          cout << "WOKMake_MetaStep::GetUnderlyingSteps4 -> GetAndAddStep" << endl ;
	  Handle(WOKMake_Step) thestep = BuildProcess()->GetAndAddStep(Unit(), acode, apart);

	  if(thestep.IsNull())
	    {
	      return NULLRESULT;
	    }

	  thestep->DontExecute();
	  
	  result->Append(thestep->UniqueName());
	}

    }
  return result;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetUnderlyingSteps
//purpose  : 
//=======================================================================
void WOKMake_MetaStep::SetUnderlyingSteps(const Handle(TColStd_HSequenceOfHAsciiString)& steps)
{
  myparts = new TColStd_HSequenceOfHAsciiString;
  Standard_Integer i;

  if(steps.IsNull())
    {
      myparts.Nullify();
      myundersteps.Nullify();
    }

  for(i=1; i<=steps->Length(); i++)
    {
      myparts->Append(BuildProcess()->Find(steps->Value(i))->SubCode());
    }
  myundersteps = steps;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : UnderlyingSteps
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKMake_MetaStep::UnderlyingSteps() 
{
  if(!myundersteps.IsNull())
    {
      return myundersteps;
    }
  if(!IsToExecute())
    {
      SetUnderlyingSteps(GetLastUnderlyingSteps());
      return myundersteps;
    }
  else
    {
      SetUnderlyingSteps(GetUnderlyingSteps());
      return myundersteps;
    }
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Execute
//purpose  : 
//=======================================================================
void WOKMake_MetaStep::Execute(const Handle(WOKMake_HSequenceOfInputFile)& execlist)
{
  Standard_Integer i,j;
  Standard_Boolean ok = Standard_True;
  Handle(TColStd_HSequenceOfHAsciiString) subcodeseq = new TColStd_HSequenceOfHAsciiString;

  Handle(TColStd_HSequenceOfHAsciiString) thesteps;

  thesteps = UnderlyingSteps();

  for(j=1;j<=thesteps->Length(); j++) 
    {
      Handle(WOKMake_Step) thestep = BuildProcess()->Find(thesteps->Value(j));
      
      thestep->DontExecute();
    }

  for(i=1; i<=execlist->Length(); i++)
    {
      const Handle(WOKMake_InputFile)& infile = execlist->Value(i);

      if(infile->IsStepID())
	{
	  Handle(TCollection_HAsciiString) aunit  = infile->ID()->Token(":", 1);
	  Handle(TCollection_HAsciiString) acode  = infile->ID()->Token(":", 2);
	  Handle(TCollection_HAsciiString) apart  = infile->ID()->Token(":", 3);
	  
	  if(!apart->IsEmpty())
	    {
	      for(j=1;j<=thesteps->Length(); j++) 
		{
		  Handle(WOKMake_Step) thestep = BuildProcess()->Find(thesteps->Value(j));
		  
		  if(!thestep->SubCode().IsNull())
		    {
		      if(!strcmp(apart->ToCString(), thestep->SubCode()->ToCString()))
			{
			  thestep->DoExecute();
			}
		    }
		}
	    }
	}
    }


  for(i=1; i<=thesteps->Length(); i++)
    {
      Handle(WOKMake_Step) thestep = BuildProcess()->Find(thesteps->Value(i));

      if (thestep->IsToExecute()) 
	{
	  InfoMsg() << "WOKMake_MetaStep::Execute"
	    << "========> " << thestep->SubCode() << endm;
	}

      thestep->Make();

      if (thestep->IsToExecute()) 
	{  
	  switch(thestep->Status())
	    {
	    case WOKMake_Uptodate:
	      InfoMsg() << "WOKMake_MetaStep::Execute"
		      << "========> " << thestep->SubCode() << " is uptodate" << endm;
	      break;
	    case WOKMake_Success:
	      InfoMsg() << "WOKMake_MetaStep::Execute"
		      << "========> " << thestep->SubCode() << " succeeded" << endm;
	      break;
	    case WOKMake_Incomplete:
	      WarningMsg() << "WOKMake_MetaStep::Execute"
		         << "========> " << thestep->SubCode() << " is incomplete" << endm;
	      break;
	    case WOKMake_Failed:
	      ErrorMsg() << "WOKMake_MetaStep::Execute"
		       << "========> " << thestep->SubCode() << " failed" << endm;
	      ok = Standard_False;
	      break;
	    case WOKMake_Unprocessed:
	      WarningMsg() << "WOKMake_MetaStep::Execute"
		         << "========> " << thestep->SubCode() << " is still unprocessed" << endm;
	      ok=Standard_False;
	      break;
             default: break;
	    }
	}

      Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile;
      
      outfile->SetID(thestep->StepOutputID());
      outfile->SetLocateFlag(Standard_True);
      outfile->SetPhysicFlag(Standard_False);
      outfile->SetStepID(Standard_True);

      Handle(TColStd_HSequenceOfHAsciiString) precsteps = thestep->PrecedenceSteps();

      for(j=1; j<=precsteps->Length(); j++)
	{
	  Handle(TCollection_HAsciiString) unitname = precsteps->Value(j)->Token(":", 1);
	  Handle(TCollection_HAsciiString) precid = BuildProcess()->Find(precsteps->Value(j))->StepOutputID();
	  Handle(WOKMake_InputFile) infile = myinflow.FindFromKey(precid);
	  
	  if(infile.IsNull())
	    {
	      WarningMsg() << "WOKMake_MetaStep::Execute"
			 << "Ignoring precedence step dependence on " << precid << " (not in input list)" << endm;
	    }
	  else
	    {
	      AddExecDepItem(infile, outfile, Standard_True);
	    }
	}
    }

  thesteps.Nullify();

  if(ok) SetSucceeded();
  else   SetFailed();
  return;
}



//=======================================================================
//function : Make
//purpose  : 
//=======================================================================
WOKMake_Status WOKMake_MetaStep::Make()
{
  
  if(IsToExecute())
    {
      GetInputFlow();
      if(CheckStatus("getting input list")) return Status();

      Handle(WOKMake_HSequenceOfInputFile) execlist;
      
      execlist = ExecutionInputList();
      
      if(CheckStatus("determine exec list")) return Status();
      
      if(execlist->Length() || IsChecked())
	{
	  Execute(execlist);
	  
	  if(CheckStatus("execution")) return Status();
	}
      else
	{
	  SetUptodate();
	}
      
      AcquitExecution(execlist);
      
      if(CheckStatus("acquit execution")) return Status();
    }
  else
    {
      Standard_Integer i;

      Handle(TColStd_HSequenceOfHAsciiString) steps = GetLastUnderlyingSteps();

      if(!steps.IsNull())
	{
	  for(i=1;i<=steps->Length(); i++)
	    {
	      BuildProcess()->Find(steps->Value(i))->Make();
	    }
	}
    }

  Handle(WOKMake_HSequenceOfOutputFile)   outlist   = OutputFileList();
  Handle(TColStd_HSequenceOfHAsciiString) unitfiles = Unit()->FileList();

  if(unitfiles.IsNull())
    {
      unitfiles = new TColStd_HSequenceOfHAsciiString;
      Unit()->SetFileList(unitfiles);
    }

  if(!outlist.IsNull())
    {
      for(Standard_Integer j=1; j<=outlist->Length(); j++)
	{
	  Handle(WOKMake_OutputFile) outfile = outlist->Value(j);
	  
	  if(outfile->IsProduction() && 
	     outfile->IsPhysic()     && 
	     outfile->IsLocateAble() &&
	     outfile->IsMember())
	    {
	      unitfiles->Append(outfile->ID());
	    }
	}
    }

  return Status();
}

//=======================================================================
//function : HandleOutputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKMake_MetaStep::HandleOutputFile(const Handle(WOKMake_OutputFile)& afile) 
{
  if(afile.IsNull()) return Standard_False;

  if(afile->IsStepID())
    {
      switch(afile->Status())
	{
	case WOKMake_New:
	case WOKMake_Same:
	case WOKMake_Moved:
	  break;
	case WOKMake_Disappeared:
	  {  
	    Handle(TCollection_HAsciiString) apart  = afile->ID()->Token(":", 3);
//            cout << "WOKMake_MetaStep::HandleOutputFile -> GetAndAddStep" << endl ;
	    Handle(WOKMake_Step) thesubstep = BuildProcess()->GetAndAddStep(Unit(), Code(), apart);
	    if (!thesubstep.IsNull())
	      {
		Handle(WOKernel_File) asubout;
      		asubout = thesubstep->LocateAdmFile(thesubstep->OutLocator(),thesubstep->OutputFilesFileName());
		
		if (!asubout.IsNull())
		  {
		    //substep production has to be removed
		    Handle(WOKMake_HSequenceOfOutputFile) outsubfiles = new WOKMake_HSequenceOfOutputFile;
		    
		    WOKMake_OutputFile::ReadFile(asubout->Path(), thesubstep->OutLocator(), outsubfiles);
		    
		    for (Standard_Integer i=1; i<= outsubfiles->Length(); i++)
		      {
			Handle(WOKMake_OutputFile) outsubfile = outsubfiles->Value(i);
			if (!outsubfile.IsNull()) 
			  outsubfile->SetStatus(WOKMake_Disappeared);
			
			thesubstep->HandleOutputFile(outsubfile);
		      }
		  }
	      }
	  }
	  break;
          default: break;
	}
    }
  return Standard_False;
}
