// File:	WOKMake_BuildProcessIterator.cxx
// Created:	Thu Jun 19 11:08:47 1997
// Author:	Jean GAUTIER
//		<jga@hourax.paris1.matra-dtv.fr>

#include <Standard_ErrorHandler.hxx>
#include <Standard_Macro.hxx>

#include <TCollection_HAsciiString.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <TColStd_SequenceOfInteger.hxx>

#ifndef WNT
#include <WOKUtils_ProcessManager.hxx>
#include <OSD_SIGINT.hxx>
#else
#include <WOKUtils_ShellManager.hxx>
#define WOKUtils_ProcessManager WOKUtils_ShellManager
#include <OSD_Exception_CTRL_BREAK.hxx>
#endif

#include <WOKTools_Messages.hxx>
#include <WOKTools_MapIteratorOfMapOfHAsciiString.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_AdmFile.hxx>

#include <WOKernel_DevUnit.hxx>
#include <WOKernel_Locator.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_BasicUnitTypes.hxx>

#include <WOKernel_SortedImpldepFromIterator.hxx>
#include <WOKernel_UnitGraph.hxx>

#include <WOKMake_Step.hxx>
#include <WOKMake_MetaStep.hxx>
#include <WOKMake_HSequenceOfOutputFile.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_DataMapIteratorOfDataMapOfHAsciiStringOfSequenceOfHAsciiString.hxx>
#include <WOKMake_DataMapOfHAsciiStringOfSequenceOfHAsciiString.hxx>

#include <WOKMake_BuildProcessIterator.ixx>



//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKMake_BuildProcessIterator
//purpose  : 
//=======================================================================
WOKMake_BuildProcessIterator::WOKMake_BuildProcessIterator(const Handle(WOKMake_BuildProcess)& aprocess,
							   const Standard_Boolean alogflag)
: myprocess(aprocess),
  mystatus(WOKMake_Unprocessed),
  mygrpidx(1),
  mystepidx(1),
  mylogflag(alogflag)
{
  Handle(WOKMake_Step) curstep = CurStep();
  
  while(curstep.IsNull() && More())
    {
      Next();
      curstep = CurStep();
    }
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : CurGroup
//purpose  : 
//=======================================================================
const Handle(WOKMake_BuildProcessGroup)& WOKMake_BuildProcessIterator::CurGroup() const
{
  if(mygrpidx <= myprocess->Groups().Extent())
    {
      return myprocess->Groups()(mygrpidx);
    }
  else
    {
      static Handle(WOKMake_BuildProcessGroup) NULLRESULT;
      return NULLRESULT;
    }
  
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : CurStep
//purpose  : 
//=======================================================================
const Handle(WOKMake_Step)& WOKMake_BuildProcessIterator::CurStep() const
{
  if(mygrpidx <= myprocess->Groups().Extent())
    {
      const Handle(WOKMake_BuildProcessGroup)& group = myprocess->Groups()(mygrpidx);
      
      if(mystepidx <= group->Steps().Length())
	{
	  return myprocess->Find(group->Steps().Value(mystepidx));
	}
      else
	{
	  static Handle(WOKMake_Step) NULLRESULT;
	  return NULLRESULT;
	}
    }
  else
    {
      static Handle(WOKMake_Step) NULLRESULT;
      return NULLRESULT;
    }
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : MakeStep
//purpose  : 
//=======================================================================
WOKMake_Status WOKMake_BuildProcessIterator::MakeStep() 
{
  const Handle(WOKMake_Step)& step = CurStep();

  if(step.IsNull())
    {
      ErrorMsg() << "WOKMake_BuildProcessIterator::MakeStep" 
	       << "Invalid NULL step in iterator" << endm;
      mystatus = WOKMake_Failed;
      return mystatus;
    }

  const Handle(TColStd_HSequenceOfHAsciiString)& precsteps = step->PrecedenceSteps();

  if(!precsteps.IsNull())
    {
      for(Standard_Integer i=1; i<=precsteps->Length(); i++)
	{
	  const Handle(WOKMake_Step)& precstep = myprocess->Find(precsteps->Value(i));

	  if(precstep.IsNull())
	    {
	      ErrorMsg() << "WOKMake_BuildProcessIterator::MakeStep" 
		       << "Could not find precedence step : " << precsteps->Value(i) << endm;
	      mystatus = WOKMake_Failed;
	      return mystatus;
	    }
	  
	  switch(precstep->Status())
	    {
	    case WOKMake_Failed:
	    case WOKMake_Incomplete:
	      ErrorMsg()   << "WOKMake_BuildProcessIterator::MakeStep" 
			 << "Step " << step->Code() << " not done : almost " << precsteps->Value(i) << " failed" << endm;
	      if(myprocessed.Contains(step->Unit()->Name())) myprocessed.Remove(step->Unit()->Name());
	      step->SetStatus(WOKMake_Failed);
	      return WOKMake_Failed;
	    default:
	      break;
	    }
	}
    }

  if(step->IsToExecute())
    {
      WOKTools_MsgStreamPtr astream = NULL;
      Handle(WOKernel_File) logfile;

      WOKTools_Info theinfo = InfoMsg();

      if(!mylogflag) 
	{
	  theinfo .DontPrintHeader();
	  theinfo << "WOKMake_BuildProcessIterator::MakeStep" << " " << endm;
	  theinfo << "WOKMake_BuildProcessIterator::MakeStep" << "=====> " << step->UniqueName() << endm;
	  theinfo << "WOKMake_BuildProcessIterator::MakeStep" << " " << endm;
	  theinfo.DoPrintHeader();
	}
      else
	{
	  theinfo.DontPrintHeader();
	  theinfo << "WOKMake_BuildProcessIterator::MakeStep" << "=====> " << step->UniqueName() << flushm;
	  theinfo.DoPrintHeader();
	}

      if(mylogflag) 
	{
	  logfile = step->AdmFile(step->LogFileName());
	  
	  logfile->GetPath();
	  astream = new ofstream(logfile->Path()->Name()->ToCString(), ios::out);

	  if(astream->good())
	    {
	      InfoMsg().LogToStream(astream);
	      WarningMsg().LogToStream(astream);
	      ErrorMsg().LogToStream(astream);
	      VerboseMsg().LogToStream(astream);
	    }
	}

      try {
        OCC_CATCH_SIGNALS
	
	step->Make();

	//myprocess->TranslatorIterator().Reset();
	//WOKBuilder_MSTool::GetMSchema()->Clear();

      }
      catch  (Standard_Failure) {
	
	step->SetFailed();
	
	Handle(Standard_Failure) E = Standard_Failure::Caught();
	 
#ifndef WNT
	if(E->IsKind(STANDARD_TYPE(OSD_SIGINT)))
#else
	if(E->IsKind(STANDARD_TYPE(OSD_Exception_CTRL_BREAK)))
#endif
	  {
	    ErrorMsg() << "WOKMake_BuildProcessIterator::MakeStep" << "Process received interupt signal" << endm;
	    WOKUtils_ProcessManager::KillAll();
	    mygrpidx =  myprocess->Groups().Extent()+1;
	    
	  }
	else
	  {
	    ErrorMsg() << "WOKMake_BuildProcessIterator::MakeStep" << "Exception was raised : " << E->GetMessageString() << endm;
	  }	
      }
      
      
      if(mylogflag && astream) 
	{
	  InfoMsg().EndLogging();
	  WarningMsg().EndLogging();
	  ErrorMsg().EndLogging();
	  VerboseMsg().EndLogging();
	  astream->close();
	  delete astream;
	}

      if(!mylogflag)
	{
	  theinfo  << "WOKMake_BuildProcessIterator::MakeStep" << "Step " << step->Code();
	}

      switch(step->Status())
	{
	case WOKMake_Uptodate:
	  theinfo  << " is up to date" << endm;
	  break;
	case WOKMake_Success:
	  if(!myprocessed.Contains(step->Unit()->Name())) myprocessed.Add(step->Unit()->Name());
	  theinfo  << " is successfull" << endm;
	  break;
	case WOKMake_Processed:
	  if(!myprocessed.Contains(step->Unit()->Name())) myprocessed.Add(step->Unit()->Name());
	  //theinfo  << " is processed" << endm;
	  break;
	case WOKMake_Incomplete:
	  if(myprocessed.Contains(step->Unit()->Name())) myprocessed.Remove(step->Unit()->Name());
	  theinfo << endm;
	  WarningMsg() << " is incomplete" << endm;
	  if(mylogflag)
	    {WarningMsg()   << "WOKMake_BuildProcessIterator::MakeStep" 
	       << "Consult " << logfile->Path()->Name()->ToCString() << " for details" << endm;}
	  return WOKMake_Incomplete;
	  
	case WOKMake_Failed:
	  if(myprocessed.Contains(step->Unit()->Name())) myprocessed.Remove(step->Unit()->Name());
	  theinfo << endm;
	  ErrorMsg()   << "WOKMake_BuildProcessIterator::MakeStep" << "Step " << step->Code() << " failed" << endm;
	  if(mylogflag)
	    {ErrorMsg()   << "WOKMake_BuildProcessIterator::MakeStep" 
	       << "Consult " << logfile->Path()->Name()->ToCString() << " for details" << endm;}
	  return WOKMake_Failed;
	  
	case WOKMake_Unprocessed:
	  theinfo << endm;
	  WarningMsg() << "WOKMake_BuildProcessIterator::MakeStep" << "Step " << step->Code() << " is still unprocessed" << endm;
	  if(mylogflag)
	    {WarningMsg()   << "WOKMake_BuildProcessIterator::MakeStep" 
	       << "Consult " << logfile->Path()->Name()->ToCString() << " for details" << endm;}
	  break;
	}

    }
  else
    {
      step->Make();

      switch(step->Status())
	{
	case WOKMake_Uptodate:
	  InfoMsg()    << "WOKMake_BuildProcessIterator::MakeStep" << "Step " << step->Code() << " is up to date" << endm;
	  break;
	case WOKMake_Success:
	  if(!myprocessed.Contains(step->Unit()->Name())) myprocessed.Add(step->Unit()->Name());
	  InfoMsg()    << "WOKMake_BuildProcessIterator::MakeStep" << "Step " << step->Code() << " is successfull" << endm;
	  break;
	case WOKMake_Processed:
	  if(!myprocessed.Contains(step->Unit()->Name())) myprocessed.Add(step->Unit()->Name());
	  //InfoMsg()    << "WOKMake_BuildProcessIterator::MakeStep" << "Step " << step->Code() << " is processed" << endm;
	  break;
	case WOKMake_Incomplete:
	  if(myprocessed.Contains(step->Unit()->Name())) myprocessed.Remove(step->Unit()->Name());
	  WarningMsg() << "WOKMake_BuildProcessIterator::MakeStep" << "Step " << step->Code() << " is incomplete" << endm;
	  return WOKMake_Incomplete;
	  
	case WOKMake_Failed:
	  if(myprocessed.Contains(step->Unit()->Name())) myprocessed.Remove(step->Unit()->Name());
	  ErrorMsg()   << "WOKMake_BuildProcessIterator::MakeStep" << "Step " << step->Code() << " failed" << endm;
	  return WOKMake_Failed;
	  
	case WOKMake_Unprocessed:
	  break;
	}
    }

  return WOKMake_Success;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Next
//purpose  : 
//=======================================================================
void WOKMake_BuildProcessIterator::Next() 
{
  const Handle(WOKMake_BuildProcessGroup)& curgroup = CurGroup();
  
  if(!curgroup.IsNull())
    {
      if(mystepidx >= curgroup->Steps().Length())
	{
	  mygrpidx++;
	  if(mygrpidx <= myprocess->Groups().Extent())
	    {
	      if(myprocess->Groups().FindFromIndex(mygrpidx)->Steps().Length()) {
		mystepidx = 1;
	        ReorderCurrentGroup();
	      }
	      else {
		Next();
	      }
	    }
	}
      else
	{
	  mystepidx++;
	}
    }
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : More
//purpose  : 
//=======================================================================
Standard_Boolean WOKMake_BuildProcessIterator::More() const
{
  if(mygrpidx < myprocess->Groups().Extent())
    {
      return Standard_True;
    }
  else if(mygrpidx == myprocess->Groups().Extent())
    {
      if(mystepidx <= myprocess->Groups().FindFromIndex(mygrpidx)->Steps().Length())
	{
	  return Standard_True;
	}
    }
  return Standard_False;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Terminate
//purpose  : 
//=======================================================================
WOKMake_Status WOKMake_BuildProcessIterator::Terminate() 
{
  WOKMake_Status result = WOKMake_Success;
  myprocess->ClearGroups();

  WOKMake_DataMapIteratorOfDataMapOfHAsciiStringOfSequenceOfHAsciiString anit(myprocess->Units());

  InfoMsg() << "WOKMake_BuildProcessIterator::Terminate" 
          << "------------------ Process report ------------------" << endm;

  while(anit.More())
    {
      Handle(WOKernel_DevUnit) aunit = myprocess->Locator()->LocateDevUnit(anit.Key());
      
      Handle(TColStd_HSequenceOfHAsciiString) unitfiles = aunit->FileList();
      
      if(unitfiles.IsNull())
	{
	  unitfiles = new TColStd_HSequenceOfHAsciiString;
	  aunit->SetFileList(unitfiles);
	}
      
      const TColStd_SequenceOfHAsciiString& steps = anit.Value();

      // UPDATE :
      //           0 : nothing decided
      //           1 : decided to update
      //           2 : decided to not update
      Standard_Integer update = 0;
      Handle(TCollection_HAsciiString) failedlist = new TCollection_HAsciiString;

      for(Standard_Integer i=1; i<=steps.Length(); i++)
	{
	  const Handle(WOKMake_Step)& step = myprocess->Find(steps.Value(i));
	  
	  if(!step.IsNull())
	    {
	      switch(step->Status())
		{
		case WOKMake_Success:
		case WOKMake_Processed:
		  if(update < 1 ) update = 1;
		  break;
		case WOKMake_Unprocessed:
		case WOKMake_Uptodate:
		  break;
		case WOKMake_Incomplete:
		case WOKMake_Failed:
		  if(update < 2 ) update = 2;
		  failedlist->AssignCat(step->Code());
		  failedlist->AssignCat(" ");
		  break;
		default:
		  break;
		}
	    }
	}

      switch(update)
	{
	case 0:
	  //InfoMsg() << "WOKMake_BuildProcessIterator::Terminate" 
	  //	  << "Not done " << anit.Key() << endm;
	  break;
	case 1:
	  {
	    InfoMsg() << "WOKMake_BuildProcessIterator::Terminate" 
		    << "Success  " << anit.Key() << endm;
	    for(Standard_Integer i=1; i<=steps.Length(); i++) {
	      const Handle(WOKMake_Step)& step = myprocess->Find(steps.Value(i));
	      
	      if(!step.IsNull()) {
		const Handle(WOKMake_HSequenceOfOutputFile)& outlist = step->OutputFileList();
		    
		if(!outlist.IsNull()) {
		  for(Standard_Integer j=1; j<=outlist->Length(); j++) {
		    const Handle(WOKMake_OutputFile)& outfile = outlist->Value(j);
		    
		    if(outfile->IsProduction() && 
		       outfile->IsPhysic()     && 
		       outfile->IsLocateAble() &&
		       outfile->IsMember()) {
		      unitfiles->Append(outfile->ID());
		    }
		  }
		  Handle(WOKMake_MetaStep) mstep = Handle(WOKMake_MetaStep)::DownCast(step);
		  if (!mstep.IsNull()) {
		    Handle(TColStd_HSequenceOfHAsciiString) substeps = mstep->UnderlyingSteps();
		    if (!substeps.IsNull()) {
		      for (Standard_Integer k=1; k<= substeps->Length(); k++) {
			const Handle(WOKMake_Step)& substep = myprocess->Find(substeps->Value(k));
			if(!substep.IsNull()) {
			  const Handle(WOKMake_HSequenceOfOutputFile)& outslist = substep->OutputFileList();
			  if(!outslist.IsNull()) {
			    for(Standard_Integer l=1; l<=outslist->Length(); l++) {
			      const Handle(WOKMake_OutputFile)& outfile = outslist->Value(l);
			      if(outfile->IsProduction() && 
				 outfile->IsPhysic()     && 
				 outfile->IsLocateAble() &&
				 outfile->IsMember()) {
				unitfiles->Append(outfile->ID());
			      }
			    }
			  }
			}
		      }
		    }
		  }
		}
	      }
	      else {
		ErrorMsg() << "WOKMake_BuildProcessIterator::Terminate" 
		  << "Could not obtain step " << steps.Value(i) << endm;
		return WOKMake_Failed;
	      }
	    }
	    aunit->DumpFileList(myprocess->Locator());
	  }
	  break;
	case 2:
	  {
	    result = WOKMake_Failed;
	    InfoMsg() << "WOKMake_BuildProcessIterator::Terminate" 
		    << "Failed   " << anit.Key() << " (" << failedlist << ")"<< endm;
	  }
	}

      // On va arreter de bouffer la memoire pour rien
      myprocess->RemoveUnit(aunit->Name());
      aunit->Close();

      anit.Next();
    }
  
  myprocess->ClearUnits();

  InfoMsg() << "WOKMake_BuildProcessIterator::Terminate" 
          << "----------------------------------------------------" << endm;

  WOKUtils_ProcessManager::KillAll();
  //aStorageManager.Purge();
  
  return result;
}

void WOKMake_BuildProcessIterator::ReorderCurrentGroup()
{
  if (!strcmp(CurGroup()->Name()->ToCString(),"Lib")) {
    if (!CurGroup()->IsOrdered()) {

      TColStd_SequenceOfHAsciiString thesteps;
      Handle(WOKernel_Locator) theloc = myprocess->Locator();
      Handle(TCollection_HAsciiString) pkgstype = new TCollection_HAsciiString("PACKAGES");
      WOKernel_SortedImpldepFromIterator algo;
      WOKMake_DataMapOfHAsciiStringOfSequenceOfHAsciiString lsttksteps;
      WOKTools_MapOfHAsciiString udadded;
      Standard_Integer i;
      Standard_Boolean toadd;

      for (i=1; i<= CurGroup()->Steps().Length(); i++) {
	Handle(WOKMake_Step) astep = myprocess->Find(CurGroup()->Steps().Value(i));
	// on traite uniquement que les UDS ad hoc (default shema et toolkit)
	toadd = Standard_False;
	if (!astep->IsToExecute()) {
	  toadd = Standard_True;
	}
	else {
	  // on sait si une UD convient par %WOKSteps_IsOrdered
	  if (strcmp(astep->Unit()->Params().Eval("%WOKSteps_IsOrdered")->ToCString(),"Yes")) {
	    toadd = Standard_True;
	  }
	}
	if (toadd) {
	  thesteps.Append(CurGroup()->Steps().Value(i));
	}
	else {
	  
	  if (lsttksteps.IsBound(astep->Unit()->Name())) {
	    lsttksteps(astep->Unit()->Name()).Append(CurGroup()->Steps().Value(i));
	  }
	  else {
	    TColStd_SequenceOfHAsciiString aseq;
	    lsttksteps.Bind(astep->Unit()->Name(),aseq);
	  
	    lsttksteps(astep->Unit()->Name()).Append(CurGroup()->Steps().Value(i));
	    
	    algo.FromVertex(astep->Unit()->Name());
	    
	    
	    // ajout des ImplDep dans le UnitGraph
	    
	    Handle(WOKernel_File) impldepfile = astep->Unit()->ImplDepFile(theloc,astep->Unit()->Name());
	    if (impldepfile.IsNull()) {
	      ErrorMsg() << "WOKMake_BuildProcessIterator::ReorderCurrentGroup" <<
		"Unable to get ImplDep file for unit " << astep->Unit()->Name()->ToCString() << endm;
	    }
	    else {
	      impldepfile->GetPath();
	      Handle(TColStd_HSequenceOfHAsciiString) deps = astep->Unit()->ReadImplDepFile(impldepfile->Path(),
											    theloc,
											    Standard_False);
	      myprocess->UnitGraph()->Add(astep->Unit()->Name(),deps);
	    }
	    
	    if (WOKernel_IsToolkit(astep->Unit())) {
	      // ajout du package dans UnitGraph
	      // le tk est fournisseur des uds qui le composent
	    
	      Handle(TCollection_HAsciiString) PACKAGESname = astep->Unit()->Params().Eval("%FILENAME_PACKAGES");
	      Handle(WOKernel_File) filepack = theloc->Locate(astep->Unit()->Name(),
							      pkgstype, 
							      PACKAGESname);
	      if (filepack.IsNull()) {
		ErrorMsg() << "WOKMake_BuildProcessIterator::ReorderCurrentGroup" <<
		  "Unable to get PACKAGES file for unit " << astep->Unit()->Name()->ToCString() << endm;
	      }
	      else {
		filepack->GetPath();
		WOKUtils_AdmFile afile(filepack->Path());
		Handle(TColStd_HSequenceOfHAsciiString) udsoftk = afile.Read();
		
		for (Standard_Integer j=1;j<= udsoftk->Length(); j++) {
		  myprocess->UnitGraph()->Add(udsoftk->Value(j),astep->Unit()->Name());
		  udadded.Add(udsoftk->Value(j));
		}
	      }
	    }
	  }
	}
      }
      
      
      // on y va !
      
      if (lsttksteps.Extent() > 1) {
	algo.Perform(myprocess->UnitGraph());
	
	Standard_Boolean IsCyclic = Standard_False;
	Handle(TColStd_HSequenceOfHAsciiString)  orderedtks = new TColStd_HSequenceOfHAsciiString ;
	while(algo.More()) {      
	  if(algo.NbVertices() > 1) {
	    ErrorMsg() << "WOKMake_BuildProcessIterator::ReorderCurrentGroup"
	      << "Cyclic dependency detected between: ";
	    
	    for(i=1; i<= algo.NbVertices(); i++) {
	      ErrorMsg() << algo.Value(i) << " ";
	    }
	    
	    ErrorMsg() << endm;
	    
	    IsCyclic = Standard_True;
	  }
	  else {
	    if (lsttksteps.IsBound(algo.Value(1))) {
	      orderedtks->Prepend(algo.Value(1));
	    }
	  }
	  algo.Next();
	}
	if (!IsCyclic) {
	  for (i=1;i<=orderedtks->Length();i++) {
	    const TColStd_SequenceOfHAsciiString& aseq = lsttksteps(orderedtks->Value(i));
	    for (Standard_Integer j=1; j<= aseq.Length(); j++) {
	      thesteps.Append(aseq(j));
	    }
	  }
	  CurGroup()->ChangeSteps(thesteps);      
	}
	WOKTools_MapIteratorOfMapOfHAsciiString itadd(udadded);
	// on remet le UnitGraph en etat
	while (itadd.More()) {
	  myprocess->UnitGraph()->Remove(itadd.Key());
	  itadd.Next();
	}
      }
      CurGroup()->SetOrdered();
    }
  }    
} 


