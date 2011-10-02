// Copyright: 	Matra-Datavision 1998
// File:	WOKDeliv_DeliveryListShared.cxx
// Created:	Thu Jan 29 18:03:49 1998
// Author:	Arnaud BOUZY
//		<adn>

#include <WOKDeliv_DeliveryListShared.ixx>
#include <WOKDeliv_DeliveryList.hxx>
#include <WOKTools_MapIteratorOfMapOfHAsciiString.hxx>
#include <WOKernel_Locator.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_BasicUnitTypes.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKTools_Messages.hxx>

WOKDeliv_DeliveryListShared::WOKDeliv_DeliveryListShared(const Handle(WOKMake_BuildProcess)& aprocess,
							 const Handle(WOKernel_DevUnit)& aunit,
							 const Handle(TCollection_HAsciiString)& acode,
							 const Standard_Boolean checked,
							 const Standard_Boolean hidden)
: WOKDeliv_DeliveryStepList(aprocess,aunit,acode,checked,hidden)
{
}

void WOKDeliv_DeliveryListShared::Execute(const Handle(WOKMake_HSequenceOfInputFile)& bid)
{
  WOKDeliv_DeliveryStepList::Execute(bid);
  Standard_Boolean okexec = Standard_False;
  if (!myList.IsNull()) {
    Handle(WOKernel_Parcel) theParcel = GetParcel(Unit(),myList->GetName());
    if (!theParcel.IsNull()) {
      okexec = Standard_True;
      
      WOKTools_MapIteratorOfMapOfHAsciiString itpck(myList->GetMap());
      Handle(WOKMake_InputFile) infileCOMPONENTS = GetInFileCOMPONENTS();
      Handle(WOKBuilder_Entity) bidon;
      
      while (itpck.More()) {
	
	Handle(WOKernel_DevUnit) thesourceunit = Locator()->LocateDevUnit(itpck.Key());
	if (thesourceunit.IsNull()) {
	  okexec = Standard_False;
	  SetFailed();
	}
	else {
	  thesourceunit->Open();
	  
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


	  if (WOKernel_IsToolkit(thesourceunit)) {
	    // Find file PACKAGES
	    Handle(TCollection_HAsciiString) pkgstype = new TCollection_HAsciiString("PACKAGES");
	    Handle(TCollection_HAsciiString) PACKAGESname = thesourceunit->Params().Eval("%FILENAME_PACKAGES");
	    Handle(WOKernel_File) filepack = Locator()->Locate(thesourceunit->Name(),
							       pkgstype, 
							       PACKAGESname);
	    if (filepack.IsNull()) {
	      okexec = Standard_False;
	      ErrorMsg() << "WOKDeliv_DeliveryListShared::Execute"
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
	}
	itpck.Next();
      }
    }
  }
  if (!okexec) {
    SetFailed();
  }
}

	  



