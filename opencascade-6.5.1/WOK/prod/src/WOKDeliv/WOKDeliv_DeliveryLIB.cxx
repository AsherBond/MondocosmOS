// File:	WOKMake_DeliveryLIB.cxx
// Created:	Fri Mar 29 17:04:00 1996
// Author:	Arnaud BOUZY
//		<adn>


#include <WOKDeliv_DeliveryLIB.ixx>
#include <WOKMake_AdmFileTypes.hxx>
#include <WOKDeliv_ParseDelivery.hxx>
#include <WOKDeliv_DeliveryStep.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_HSequenceOfOutputFile.hxx>

#include <WOKernel_DevUnit.hxx>
#include <WOKernel_BasicUnitTypes.hxx>

#include <WOKernel_File.hxx>
#include <WOKernel_Locator.hxx>

#include <WOKUtils_Path.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKTools_MapIteratorOfMapOfHAsciiString.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>


WOKDeliv_DeliveryLIB::WOKDeliv_DeliveryLIB(const Handle(WOKMake_BuildProcess)& aprocess,
					   const Handle(WOKernel_DevUnit)& aunit,
					   const Handle(TCollection_HAsciiString)& acode,
					   const Standard_Boolean checked,
					   const Standard_Boolean hidden)
: WOKDeliv_DeliveryMetaStep(aprocess,aunit,acode,checked,hidden)
{
}

void WOKDeliv_DeliveryLIB::Execute(const Handle(WOKMake_HSequenceOfInputFile)&)
{
  SetList();
  Standard_Boolean okexec=Standard_False;
  if (!myList.IsNull()) {
    Handle(WOKernel_Parcel) theParcel = WOKDeliv_DeliveryStep::GetParcel(Unit(),myList->GetName());
    Handle(WOKMake_InputFile) infileCOMPONENTS = GetInFileCOMPONENTS();
    if (!theParcel.IsNull()) {
      okexec = Standard_True;
      Standard_Boolean needobj = NeedsObjects();
      if (SubCode().IsNull()) { // Meta Step

	WOKTools_MapIteratorOfMapOfHAsciiString itpck(myList->GetMap());
	while (itpck.More()) {
	  Handle(WOKernel_DevUnit) thesourceunit = Locator()->LocateDevUnit(itpck.Key());
	  if (thesourceunit.IsNull()) {
	    okexec = Standard_False;
	    ErrorMsg() << "WOKDeliv_DeliveryLIB::Execute" << "Cannot locate unit : " << itpck.Key()->ToCString() << endm;
	  }
	  else {
	    if (IsAvailable(thesourceunit)) {
	      thesourceunit->Open();

	      Handle(TCollection_HAsciiString) id = WOKMake_Step::StepOutputID(Unit()->Name(),
									       Code(),
									       thesourceunit->Name());
	      Handle(WOKMake_OutputFile) outfile 
		= new WOKMake_OutputFile(id, 
					 Handle(WOKernel_File)(), 
					 Handle(WOKBuilder_Entity)(),
					 Handle(WOKUtils_Path)());
	      
	      outfile->SetProduction();
	      outfile->SetLocateFlag(Standard_True);
	      outfile->SetPhysicFlag(Standard_False);
	      outfile->SetStepID(Standard_True);
	      
	      Handle(WOKMake_Step) astep = BuildProcess()->GetAndAddStep(Unit(), 
									 Code(), 
									 thesourceunit->Name());
	      astep->DoExecute();
	      astep->SetPrecedenceSteps(PrecedenceSteps());
	      astep->SetTargets(Targets());
	      astep->SetOptions(Options());
	      
	      switch(astep->Make())
		{
		case WOKMake_Failed:
		case WOKMake_Unprocessed:
		  okexec = Standard_False;
		  break;
                 default: break;
		}
	      AddExecDepItem(infileCOMPONENTS,outfile, Standard_True);
	    }
	  }
	  itpck.Next();
	} 
      }
      else { // Execute a step list
	Handle(WOKBuilder_Entity) bidon;
      
	Handle(WOKernel_DevUnit) thesourceunit = Locator()->LocateDevUnit(SubCode());
	if (thesourceunit.IsNull()) {
	  okexec = Standard_False;
	  ErrorMsg() << "WOKDeliv_DeliveryLIB::Execute" << "Cannot locate unit : " << SubCode() << endm;
	}
	else {
	  // Find file ImplDep
	  Handle(TCollection_HAsciiString) nameidep = new TCollection_HAsciiString(thesourceunit->Name());
	  nameidep->AssignCat(".");
	  nameidep->AssignCat(thesourceunit->Params().Eval("%FILENAME_IMPLDEP"));

	  Handle(WOKernel_File) idep = Locator()->Locate(thesourceunit->Name(),
							 new TCollection_HAsciiString("stadmfile"),
							 nameidep);
	  if (!idep.IsNull()) {
	    idep->GetPath();
	    Handle(WOKMake_OutputFile) outfile = 
	      new WOKMake_OutputFile(idep->LocatorName(),
				     idep,
				     bidon,
				     idep->Path());
	    outfile->SetReference();
	    outfile->SetExtern();
	    outfile->SetLocateFlag(Standard_True);
	    AddExecDepItem(infileCOMPONENTS,outfile,Standard_True);
	  }
	  if (WOKernel_IsToolkit(thesourceunit)) {
	    // Find file PACKAGES
	    Handle(TCollection_HAsciiString) pkgstype = new TCollection_HAsciiString("PACKAGES");
	    Handle(TCollection_HAsciiString) PACKAGESname = thesourceunit->Params().Eval("%FILENAME_PACKAGES");
	    Handle(WOKernel_File) filepack = Locator()->Locate(thesourceunit->Name(),
							       pkgstype, 
							       PACKAGESname);
	    if (filepack.IsNull()) {
	      okexec = Standard_False;
	      ErrorMsg() << "WOKDeliv_DeliveryLIB::Execute"
		       << "Cannot locate file PACKAGES for Toolkit " << thesourceunit->Name()
		       << endm;
	    }
	    else {
	      filepack->GetPath();
	      
	      Handle(WOKMake_OutputFile) outfile = 
		new WOKMake_OutputFile(filepack->LocatorName(),
				       filepack,
				       bidon,
				       filepack->Path());
	      outfile->SetReference();
	      outfile->SetExtern();
	      outfile->SetLocateFlag(Standard_True);
	      AddExecDepItem(infileCOMPONENTS,outfile,Standard_True);
	    }
	  }

#ifndef WNT
	  // EXTERNLIB stuff is not needed on NT
	  // Find file EXTERNLIB
	  Handle(TCollection_HAsciiString) nameexternlib = new TCollection_HAsciiString("EXTERNLIB");
	  Handle(WOKernel_File) extfile = Locator()->Locate(thesourceunit->Name(),
							    nameexternlib,
							    nameexternlib);
	  if (!extfile.IsNull()) {
	    extfile->GetPath();
	    Handle(WOKMake_OutputFile) outfile = 
	      new WOKMake_OutputFile(extfile->LocatorName(),
				     extfile,
				     bidon,
				     extfile->Path());
	    outfile->SetReference();
	    outfile->SetExtern();
	    outfile->SetLocateFlag(Standard_True);
	    AddExecDepItem(infileCOMPONENTS,outfile,Standard_True);
	  }
#endif // WNT

	  
	  // Treat library

	  if (needobj) {
	    Handle(TCollection_HAsciiString) namestep;
	    if (!WOKernel_IsToolkit(thesourceunit)) {
	      namestep = new TCollection_HAsciiString("obj.lib");
	    }
	    else {
	      namestep = new TCollection_HAsciiString("lib.build");
	    }

	    BuildProcess()->ComputeSteps(thesourceunit);
	    Handle(WOKMake_Step) thestep = BuildProcess()->Find(thesourceunit,
								namestep,
								Handle(TCollection_HAsciiString) ());
	    if (!thestep.IsNull()) {
	      Handle(TColStd_HSequenceOfHAsciiString) namprec = thestep->PrecedenceSteps();
	      for (Standard_Integer j=1; j<= namprec->Length(); j++) {
		Handle(WOKMake_Step) apstep = BuildProcess()->Find(namprec->Value(j));
		TreatStep(apstep,infileCOMPONENTS);
	      }
	    }
	  }
	  else {
	    ComputeOutputLIB(thesourceunit,infileCOMPONENTS);
	  }
	}
      }
    }
    else {
      ErrorMsg() <<  "WOKDeliv_DeliveryCDL::Execute" << "Cannot find delivery : " << myList->GetName()->ToCString() << endm;
    }
  }
  
  if (okexec) {
    SetSucceeded();
  }
  else {
    SetFailed();
  }
}


Standard_Boolean WOKDeliv_DeliveryLIB::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  Handle(WOKernel_File)     file   = infile->File();
  if (file.IsNull()) return Standard_False;

  if(!strcmp(file->Name()->ToCString(), "COMPONENTS")) {
    return Standard_True;
  }

  return Standard_False;

}


Handle(TCollection_HAsciiString) WOKDeliv_DeliveryLIB::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STADMFILE);
  return result;
}


void WOKDeliv_DeliveryLIB::TreatStep(const Handle(WOKMake_Step)& thestep,
				     const Handle(WOKMake_InputFile)& infileCOMPONENTS) 
{
  Handle(WOKBuilder_Entity) bidon;
  if (!thestep.IsNull()) {
    if (thestep->IsKind(STANDARD_TYPE(WOKMake_MetaStep))) {
      Handle(TColStd_HSequenceOfHAsciiString) theusteps = Handle(WOKMake_MetaStep)::DownCast(thestep)->UnderlyingSteps();
      for (Standard_Integer i=1; i<= theusteps->Length(); i++) {
	const Handle(WOKMake_Step)& substep = BuildProcess()->Find(theusteps->Value(i));
	TreatStep(substep,infileCOMPONENTS);
      }
    }
    else {
      Handle(WOKMake_HSequenceOfOutputFile) thefiles = thestep->OutputFileList();
      if (thefiles.IsNull()) {
	ErrorMsg() << "WOKDeliv_DeliveryLIB::Execute" 
	  << "Step " << thestep->Code() << " unprocessed for unit "
	    << thestep->Unit()->Name() << endm;
      }
      else {
	for (Standard_Integer i=1; i<= thefiles->Length(); i++) {
	  Handle(WOKernel_File) theinfile = thefiles->Value(i)->File();
	  if (theinfile.IsNull()) {
	    ErrorMsg() << "WOKDeliv_DeliveryLIB::Execute"
	      << "Null file for output file : " << thefiles->Value(i)->ID() << endm;
	  }
	  else {
	    theinfile->GetPath();
	    if ((theinfile->Path()->Extension() == WOKUtils_ObjectFile) ||
		(theinfile->Path()->Extension() == WOKUtils_ArchiveFile)) {
	      Handle(WOKMake_OutputFile) outfile = 
		new WOKMake_OutputFile(theinfile->LocatorName(),
				       theinfile,
				       bidon,
				       theinfile->Path());
	      outfile->SetReference();
	      outfile->SetExtern();
	      outfile->SetLocateFlag(Standard_True);
	      AddExecDepItem(infileCOMPONENTS,outfile,Standard_True);
	    }
	  }
	}
      }
    }
  }
}
