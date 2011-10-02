// Copyright: 	Matra-Datavision 1996
// File:	WOKDeliv_DeliveryShared.cxx
// Created:	Tue Aug 13 14:33:27 1996
// Author:	Arnaud BOUZY
//		<adn>


#include <WOKDeliv_DeliveryShared.ixx>
#include <WOKDeliv_ParseDelivery.hxx>
#include <WOKMake_HSequenceOfOutputFile.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKernel_File.hxx>

#include <WOKernel_DevUnit.hxx>
#include <WOKernel_BasicUnitTypes.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKTools_Messages.hxx>

#ifdef WNT
# include <WOKUtils_Extension.hxx>
#endif  // WNT

WOKDeliv_DeliveryShared::WOKDeliv_DeliveryShared(const Handle(WOKMake_BuildProcess)& aprocess,
						 const Handle(WOKernel_DevUnit)& aunit,
						 const Handle(TCollection_HAsciiString)& acode,
						 const Standard_Boolean checked,
						 const Standard_Boolean hidden)
  : WOKDeliv_DeliveryLIB(aprocess,aunit,acode,checked,hidden)
{
}

void WOKDeliv_DeliveryShared::SetList()
{
  myList = ParseCOMPONENTS(T_SHARED);
}

Standard_Boolean WOKDeliv_DeliveryShared::NeedsObjects() const
{
  return Standard_False;
}


void WOKDeliv_DeliveryShared::ComputeOutputLIB(const Handle(WOKernel_DevUnit)& thesourceunit,
					       const Handle(WOKMake_InputFile)& inCOMP)
{
  static Handle(TCollection_HAsciiString) namestep = new TCollection_HAsciiString("obj.lib");
  static Handle(TCollection_HAsciiString) namesteptk = new TCollection_HAsciiString("lib.build");
  Handle(WOKBuilder_Entity) bidon;
#ifdef WNT
  WOKUtils_Extension ext;
#endif  // WNT


  Handle(WOKMake_Step) thestep;
  if (WOKernel_IsToolkit(thesourceunit)) {
    thestep = BuildProcess()->GetAndAddStep(thesourceunit,
					    namesteptk,
					    Handle(TCollection_HAsciiString) ());
  }
  else {
    thestep = BuildProcess()->GetAndAddStep(thesourceunit,
					    namestep,
					    Handle(TCollection_HAsciiString) ());
  }
  if (!thestep.IsNull()) {
    Handle(WOKMake_HSequenceOfOutputFile) thefiles = thestep->OutputFileList();
    if (thefiles.IsNull()) {
      ErrorMsg() << "WOKDeliv_DeliveryShared::Execute"
	<< "Step " << thestep->Code() << " unprocessed for unit "
	  << thesourceunit->Name() << endm;
    }
    else {
      for (Standard_Integer i=1; i<= thefiles->Length(); i++) {
	Handle(WOKernel_File) theinfile = thefiles->Value(i)->File();
	theinfile->GetPath();
#ifndef WNT
	if (theinfile->Path()->Extension() == WOKUtils_DSOFile) {
#else
    ext = theinfile -> Path () -> Extension ();

    if ( ext == WOKUtils_LIBFile || ext == WOKUtils_DLLFile ||
         ext == WOKUtils_PDBFile || ext == WOKUtils_EXEFile
    ) {
#endif  // WNT
	  Handle(WOKMake_OutputFile) outfile = 
	    new WOKMake_OutputFile(theinfile->LocatorName(),
				   theinfile,
				   bidon,
				   theinfile->Path());
	  outfile->SetReference();
	  outfile->SetLocateFlag(Standard_True);
	  outfile->SetExtern();
	  AddExecDepItem(inCOMP,outfile,Standard_True);
	}
      }
    }
  }
}

Standard_Boolean WOKDeliv_DeliveryShared::IsAvailable(const Handle(WOKernel_DevUnit)& asource) const
{
  if (WOKernel_IsPackage(asource)) return Standard_True;
  if (WOKernel_IsNocdlpack(asource)) return Standard_True;
  if (WOKernel_IsToolkit(asource)) return Standard_True;
  if (WOKernel_IsSchema(asource)) return Standard_True;
  return Standard_False;
}
