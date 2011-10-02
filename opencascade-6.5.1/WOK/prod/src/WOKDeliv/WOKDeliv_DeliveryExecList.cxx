// Copyright: 	Matra-Datavision 1996
// File:	WOKDeliv_DeliveryExecList.cxx
// Created:	Tue Sep 17 14:38:00 1996
// Author:	Arnaud BOUZY
//		<adn>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKDeliv_DeliveryExecList.ixx>
#include <WOKDeliv_ParseDelivery.hxx>
#include <WOKDeliv_DeliveryStep.hxx>
#include <WOKernel_Locator.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_BasicUnitTypes.hxx>
#include <WOKernel_Session.hxx>
#include <WOKernel_Parcel.hxx>
#include <WOKernel_Workbench.hxx>
#include <WOKernel_Workshop.hxx>
#include <WOKernel_Factory.hxx>
#include <WOKernel_Warehouse.hxx>

#include <WOKDeliv_DataMapOfParcel.hxx>

#include <WOKTools_MapIteratorOfMapOfHAsciiString.hxx>
#include <WOKTools_Messages.hxx>

#include <WOKMake_MetaStep.hxx>
#include <WOKMake_AdmFileTypes.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_HSequenceOfOutputFile.hxx>
#include <WOKUtils_Path.hxx>

WOKDeliv_DeliveryExecList::WOKDeliv_DeliveryExecList(const Handle(WOKMake_BuildProcess)& aprocess,
						     const Handle(WOKernel_DevUnit)& aunit,
						     const Handle(TCollection_HAsciiString)& acode,
						     const Standard_Boolean checked,
						     const Standard_Boolean hidden)
: WOKDeliv_DeliveryMetaStep(aprocess,aunit,acode,checked,hidden)
{
}

void WOKDeliv_DeliveryExecList::Execute(const Handle(WOKMake_HSequenceOfInputFile)&)
{
  Standard_Boolean okexec = Standard_False;
  if (SubCode().IsNull()) { // Meta Step
    myList = ParseCOMPONENTS(T_DYNAMIC);
    okexec = ExecuteMetaStep();
  }
  else {
    myList = ParseCOMPONENTS(T_BASE);
    okexec = TreatDynamic();
    okexec = okexec && CompleteEngine();
  }
  if (okexec) {
    SetSucceeded();
  }
  else {
    SetFailed();
  }
}

Standard_Boolean WOKDeliv_DeliveryExecList::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  Handle(WOKernel_File)     file   = infile->File();
  if (file.IsNull()) return Standard_False;

  if(!strcmp(file->Name()->ToCString(), "COMPONENTS")) {
    return Standard_True;
  }

  return Standard_False;

}


Handle(TCollection_HAsciiString) WOKDeliv_DeliveryExecList::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STADMFILE);
  return result;
}


Standard_Boolean WOKDeliv_DeliveryExecList::ExecuteMetaStep() 
{
  // Listing for MetaStep the list of dynamic executable to produce

  Standard_Boolean okexec = Standard_False;

  if (!myList.IsNull()) {
    Handle(WOKernel_Parcel) theParcel = WOKDeliv_DeliveryStep::GetParcel(Unit(),myList->GetName());
    Handle(WOKMake_InputFile) infileCOMPONENTS = GetInFileCOMPONENTS();
    if (!theParcel.IsNull()) {
      okexec = Standard_True;
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
	    okexec = okexec && ExploreMetaStep(thesourceunit,infileCOMPONENTS);
	  }
	}
	itpck.Next();
      }
    } 
  }
  return okexec;
}


Standard_Boolean WOKDeliv_DeliveryExecList::TreatDynamic() 
{


  Handle(TCollection_HAsciiString) namelnk = new TCollection_HAsciiString("exec.link");
  Handle(TCollection_HAsciiString) nameunit = SubCode()->Token("_",1);

  Handle(WOKernel_DevUnit) thesourceunit = Locator()->LocateDevUnit(nameunit);

  if (thesourceunit.IsNull()) {
    ErrorMsg() << "WOKDeliv_DeliveryExecList::Execute" 
      << "Cannot locate DevUnit : " << nameunit << endm;
    return Standard_False;
  }
  thesourceunit->Open();
  Handle(TCollection_HAsciiString) name = SubCode()->Token("_",2);
  WOK_TRACE {
    VerboseMsg()("WOK_DELIV") << "WOKDeliv_DeliveryExecList" 
			    << "Treating : " << nameunit << " exec : " << name << endm;
  }

  BuildProcess()->ComputeSteps(thesourceunit);  
  Handle(WOKMake_Step) thestep = BuildProcess()->Find(thesourceunit,
						      namelnk,
						      Handle(TCollection_HAsciiString) ());
  if (!name->IsEmpty()) {
    Handle(TColStd_HSequenceOfHAsciiString) theusteps = Handle(WOKMake_MetaStep)::DownCast(thestep)->UnderlyingSteps();
    thestep.Nullify();
    for (Standard_Integer k=1; k<=theusteps->Length(); k++) {
      const Handle(WOKMake_Step)& substep = BuildProcess()->Find(theusteps->Value(k));
      if (substep->SubCode()->IsSameString(name)) {
	thestep = substep;
      }
    }
  }

  if (thestep.IsNull()) {
    ErrorMsg() << "WOKDeliv_DeliveryExecList::TreatDynamic" 
      << "Enable to find linking step for unit : " << nameunit;
    if (!name.IsNull()) {
      ErrorMsg() << " for executable : " << name;
    }
    ErrorMsg() << endm;
    return Standard_False;
  }
  
  Handle(WOKMake_HSequenceOfOutputFile) thefiles = thestep->OutputFileList();

  Handle(WOKMake_InputFile) infileCOMPONENTS = GetInFileCOMPONENTS();

  static Handle(TCollection_HAsciiString) stadmfname = new TCollection_HAsciiString("stadmfile");
  Handle(WOKBuilder_Entity) bidon;
  for (Standard_Integer j=1; j<= thefiles->Length(); j++) {
    Handle(WOKernel_File) theinfile = thefiles->Value(j)->File();
    if (!theinfile.IsNull()) {
      if (!theinfile->TypeName()->IsSameString(stadmfname)) {
	theinfile->GetPath();
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
  return Standard_True;
}


Standard_Boolean WOKDeliv_DeliveryExecList::CompleteEngine() 
{
  Handle(TCollection_HAsciiString) nameunit = SubCode()->Token("_",1);
  Handle(WOKernel_DevUnit) thesourceunit = Locator()->LocateDevUnit(nameunit);
  if (thesourceunit.IsNull()) {
    ErrorMsg() << "WOKDeliv_DeliveryExecList::Execute" 
      << "Cannot locate DevUnit : " << nameunit << endm;
    return Standard_False;
  }
  if (!WOKernel_IsEngine(thesourceunit)) return Standard_True;
  thesourceunit->Open();
  Handle(WOKBuilder_Entity) bidon;
  Handle(TCollection_HAsciiString) namedat = new TCollection_HAsciiString("exec.dat");
  Handle(WOKMake_Step) thestep = BuildProcess()->GetAndAddStep(thesourceunit,
							       namedat,
							       Handle(TCollection_HAsciiString) ());
  if (thestep.IsNull()) {
    ErrorMsg() << "WOKDeliv_DeliveryExecList::Execute" 
      << "Step " << namedat << " not done for unit " << nameunit << endm;
    return Standard_False;
  }
  Handle(WOKMake_HSequenceOfOutputFile) thefiles = thestep->OutputFileList();
  if (thefiles.IsNull()) {
    ErrorMsg() << "WOKDeliv_DeliveryExecList::Execute"
      << "Step " << namedat << " unprocessed for unit " << nameunit << endm;
      return Standard_False;
  }
  Handle(WOKMake_InputFile) infileCOMPONENTS = GetInFileCOMPONENTS();
  Standard_Integer i;
  for (i=1; i<= thefiles->Length(); i++) {
    Handle(WOKernel_File) theinfile = thefiles->Value(i)->File();
    if (!theinfile.IsNull()) {
      theinfile->GetPath();
      if (theinfile->Path()->Extension() == WOKUtils_DATFile) {
	Handle(WOKMake_OutputFile) outfile = 
	  new WOKMake_OutputFile(theinfile->LocatorName(),
				 theinfile,
				 bidon,
				 theinfile->Path());
	outfile->SetReference();
	outfile->SetLocateFlag(Standard_True);
	outfile->SetExtern();
	AddExecDepItem(infileCOMPONENTS,outfile,Standard_True);
      }
    }
  }
  
  Handle(TCollection_HAsciiString) namell = new TCollection_HAsciiString("xcpp.eng");
  thestep = BuildProcess()->GetAndAddStep(thesourceunit,
					  namell,
					  Handle(TCollection_HAsciiString) ());
  if (thestep.IsNull()) {
    ErrorMsg() << "WOKDeliv_DeliveryExecList::Execute" 
      << "Step " << namell << " not done for unit " << nameunit << endm;
    return Standard_False;
  }
  thefiles = thestep->OutputFileList();
  if (thefiles.IsNull()) {
    ErrorMsg() << "WOKDeliv_DeliveryExecList::Execute"
      << "Step " << namell << " unprocessed for unit " << nameunit << endm;
      return Standard_False;
  }
  for (i=1; i<= thefiles->Length(); i++) {
    Handle(WOKernel_File) theinfile = thefiles->Value(i)->File();
    if (!theinfile.IsNull()) {
      theinfile->GetPath();
      if (theinfile->Path()->Extension() == WOKUtils_LispFile) {
	Handle(WOKMake_OutputFile) outfile = 
	  new WOKMake_OutputFile(theinfile->LocatorName(),
				 theinfile,
				 bidon,
				 theinfile->Path());
	outfile->SetReference();
	outfile->SetLocateFlag(Standard_True);
	outfile->SetExtern();
	AddExecDepItem(infileCOMPONENTS,outfile,Standard_True);
      }
    }
  }
  return Standard_True;
}



Standard_Boolean WOKDeliv_DeliveryExecList::IsAvailable(const Handle(WOKernel_DevUnit)& aunit) const
{
  if (WOKernel_IsExecutable(aunit)) return Standard_True;
  if (WOKernel_IsEngine(aunit)) return Standard_True;
  return Standard_False;
}


Standard_Boolean WOKDeliv_DeliveryExecList::ExploreMetaStep(const Handle(WOKernel_DevUnit)& thesourceunit, const Handle(WOKMake_InputFile)& infileCOMPONENTS)
{
  Standard_Boolean okexec = Standard_True;
  static Handle(TCollection_HAsciiString) namesexec = new TCollection_HAsciiString("exec.tks");

  if (WOKernel_IsExecutable(thesourceunit)) {
    BuildProcess()->ComputeSteps(thesourceunit);
    Handle(WOKMake_Step) themstep = BuildProcess()->Find(thesourceunit,
							 namesexec,
							 Handle(TCollection_HAsciiString) ());
    if (themstep.IsNull()) {
      ErrorMsg() << "WOKDeliv_DeliveryExecList::Execute" 
	<< "Cannot find linking step for DevUnit : " << thesourceunit->Name() << endm;
      return Standard_False;
    }

    Handle(WOKMake_MetaStep) themetastep = Handle(WOKMake_MetaStep)::DownCast(themstep);
    Handle(TColStd_HSequenceOfHAsciiString) theundersteps = themetastep->UnderlyingSteps();
    for (Standard_Integer i=1; i<= theundersteps->Length(); i++) {
      const Handle(WOKMake_Step)& substep = BuildProcess()->Find(theundersteps->Value(i));
      okexec = okexec && ExploreStep(substep,thesourceunit,infileCOMPONENTS);
    }
  }
  else {
    Handle(WOKMake_Step) theexecstep =  BuildProcess()->GetAndAddStep(thesourceunit,
								      namesexec,
								      Handle(TCollection_HAsciiString) ());
    if (theexecstep.IsNull()) {
      ErrorMsg() << "WOKDeliv_DeliveryExecList::Execute" 
	<< "Cannot find linking step for DevUnit : " << thesourceunit->Name() << endm;
      return Standard_False;
    }
    okexec = okexec && ExploreStep(theexecstep,thesourceunit,infileCOMPONENTS);
  }
  return okexec;
}

Standard_Boolean WOKDeliv_DeliveryExecList::ExploreStep(const Handle(WOKMake_Step)& thestep,
							const Handle(WOKernel_DevUnit)& aunit,
							const Handle(WOKMake_InputFile)& infileCOMPONENTS)
{
  Standard_Boolean okexec = Standard_True;
  Handle(TCollection_HAsciiString) namesubcode = new TCollection_HAsciiString(aunit->Name());
  if (!thestep->SubCode().IsNull()) {
    namesubcode->AssignCat("_");
    namesubcode->AssignCat(thestep->SubCode()->ToCString());
  }
  Handle(TCollection_HAsciiString) id = WOKMake_Step::StepOutputID(Unit()->Name(),
								   Code(),
								   namesubcode);
  Handle(WOKMake_OutputFile) outfile 
    = new WOKMake_OutputFile(id, 
			     Handle(WOKernel_File)(), 
			     Handle(WOKBuilder_Entity)(),
			     Handle(WOKUtils_Path)());
  
  outfile->SetProduction();
  outfile->SetLocateFlag(Standard_True);
  outfile->SetPhysicFlag(Standard_False);
  outfile->SetStepID(Standard_True);
  
  Handle(WOKDeliv_DeliveryExecList) astep = 
    Handle(WOKDeliv_DeliveryExecList)::DownCast(BuildProcess()->GetAndAddStep(Unit(),
									      Code(), 
									      namesubcode));
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
  return okexec;
}

