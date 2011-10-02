// Copyright: 	Matra-Datavision 1996
// File:	WOKDeliv_DeliverySTUBClient.cxx
// Created:	Mon Aug 19 14:50:32 1996
// Author:	Arnaud BOUZY
//		<adn>

#include <WOKDeliv_DeliverySTUBClient.ixx>
#include <WOKDeliv_ParseDelivery.hxx>
#include <WOKDeliv_DeliveryStep.hxx>
#include <WOKMake_AdmFileTypes.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_HSequenceOfOutputFile.hxx>

#include <WOKernel_File.hxx>
#include <WOKernel_Locator.hxx>
#include <WOKernel_BasicUnitTypes.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKTools_Messages.hxx>
#include <WOKTools_MapIteratorOfMapOfHAsciiString.hxx>

WOKDeliv_DeliverySTUBClient::WOKDeliv_DeliverySTUBClient(const Handle(WOKMake_BuildProcess)& abp,
							 const Handle(WOKernel_DevUnit)& aunit,
							 const Handle(TCollection_HAsciiString)& acode,
							 const Standard_Boolean checked,
							 const Standard_Boolean hidden)
: WOKDeliv_DeliveryMetaStep(abp,aunit,acode,checked,hidden)
{
}

void WOKDeliv_DeliverySTUBClient::Execute(const Handle(WOKMake_HSequenceOfInputFile)&)
{
  myList = ParseCOMPONENTS(T_STUB_CLIENT);
  Standard_Boolean okexec = Standard_False;
  if (!myList.IsNull()) {
    Handle(WOKernel_Parcel) theParcel = WOKDeliv_DeliveryStep::GetParcel(Unit(),myList->GetName());
    if (!theParcel.IsNull()) {
      okexec = Standard_True;
      Handle(WOKMake_InputFile) infileCOMPONENTS = GetInFileCOMPONENTS();
      if (SubCode().IsNull()) { // Meta Step
	WOKTools_MapIteratorOfMapOfHAsciiString itpck(myList->GetMap());
	while (itpck.More()) {
	  Handle(WOKernel_DevUnit) thesourceunit = Locator()->LocateDevUnit(itpck.Key());
	  if (thesourceunit.IsNull()) {
	    okexec = Standard_False;
	    ErrorMsg() << "WOKDeliv_DeliverySTUBClient::Execute" << "Cannot locate unit : " << itpck.Key()->ToCString() << endm;
	  }
	  else {
	    if (WOKernel_IsClient(thesourceunit)) {
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
	      
	      Handle(WOKMake_Step) astep = BuildProcess()->GetAndAddStep(Unit(), Code(), thesourceunit->Name());
	      astep->DoExecute();
	      //astep->SetPrecedenceSteps(PrecedenceSteps());
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
	Handle(WOKernel_DevUnit) thesourceunit = Locator()->LocateDevUnit(SubCode());
	if (thesourceunit.IsNull()) {
	  okexec = Standard_False;
	  ErrorMsg() << "WOKDeliv_DeliveryClient::Execute" << "Cannot locate unit : " << SubCode() << endm;
	}
	else {
	  Handle(WOKBuilder_Entity) bidon;
	  Handle(TCollection_HAsciiString) namestep;
	  WOKUtils_Extension theext;
	  Standard_Boolean mustbuild = Standard_True;
	  
	  Handle(TCollection_HAsciiString) thestat = Unit()->Params().Eval("%Station");
	  if ((!strcmp(thestat->ToCString(),"sun")) ||
	      (!strcmp(thestat->ToCString(),"hp"))) {
	    mustbuild = Standard_False;
	  }
	  
	  if (mustbuild) {
	    namestep = new TCollection_HAsciiString("obj.comp");
	    theext = WOKUtils_ObjectFile;
	  }
	  else {
	    namestep = new TCollection_HAsciiString("obj.lib");
	    theext = WOKUtils_DSOFile;
	  }
	  thesourceunit->Open();
	  Handle(WOKMake_Step) thestep = BuildProcess()->GetAndAddStep(thesourceunit,
								       namestep,
								       Handle(TCollection_HAsciiString)());
	  if (!thestep.IsNull()) {
	    Handle(WOKMake_HSequenceOfOutputFile) thefiles = thestep->OutputFileList();
	    if (thefiles.IsNull()) {
	      ErrorMsg() << "WOKDeliv_DeliverySTUBClient::Execute"
		<< "Step " << namestep << " not done for unit " << thesourceunit->Name() << endm;
	      okexec = Standard_False;
	    }
	    else {
	      for (Standard_Integer i=1; i<= thefiles->Length(); i++) {
		Handle(WOKernel_File) theinfile = thefiles->Value(i)->File();
		theinfile->GetPath();
		if (theinfile->Path()->Extension() == theext) {
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
	  }
	}
      }
    }
    else {
      ErrorMsg() <<  "WOKDeliv_DeliverySTUBClient::Execute" << "Cannot find delivery : " << myList->GetName()->ToCString() << endm;
      okexec = Standard_False;
    }
  }
  if (okexec) {
    SetSucceeded();
  }
  else {
    SetFailed();
  }
}


Standard_Boolean WOKDeliv_DeliverySTUBClient::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  Handle(WOKernel_File)     file   = infile->File();
  if (file.IsNull()) return Standard_False;

  if(!strcmp(file->Name()->ToCString(), "COMPONENTS")) {
    return Standard_True;
  }

  return Standard_False;

}


Handle(TCollection_HAsciiString) WOKDeliv_DeliverySTUBClient::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STADMFILE);
  return result;
}
