// Copyright: 	Matra-Datavision 1997
// File:	WOKDeliv_DeliveryOBJSSchema.cxx
// Created:	Tue Apr 29 10:28:47 1997
// Author:	Arnaud BOUZY
//		<adn>

#include <WOKDeliv_DeliveryOBJSSchema.ixx>
#include <WOKDeliv_ParseDelivery.hxx>
#include <WOKMake_AdmFileTypes.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_HSequenceOfOutputFile.hxx>

#include <WOKernel_DevUnit.hxx>
#include <WOKernel_BasicUnitTypes.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_Locator.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKTools_Messages.hxx>
#include <WOKTools_MapIteratorOfMapOfHAsciiString.hxx>

WOKDeliv_DeliveryOBJSSchema::WOKDeliv_DeliveryOBJSSchema(const Handle(WOKMake_BuildProcess)& aprocess,
							 const Handle(WOKernel_DevUnit)& aunit,
							 const Handle(TCollection_HAsciiString)& acode,
							 const Standard_Boolean checked,
							 const Standard_Boolean hidden)
: WOKDeliv_DeliveryStep(aprocess,aunit,acode,checked,hidden)
{
}

void WOKDeliv_DeliveryOBJSSchema::Execute(const Handle(WOKMake_HSequenceOfInputFile)&)
{
  myList = ParseCOMPONENTS(T_FDDB);
  Standard_Boolean okexec = Standard_False;
  if (!myList.IsNull()) {
    Handle(WOKernel_Parcel) theParcel = GetParcel(Unit(),myList->GetName());
    if (!theParcel.IsNull()) {
      okexec = Standard_True;

      WOKTools_MapIteratorOfMapOfHAsciiString itpck(myList->GetMap());
      Handle(WOKMake_InputFile) infileCOMPONENTS = GetInFileCOMPONENTS();
      Handle(WOKBuilder_Entity) bidon;
      Handle(TCollection_HAsciiString) namestep= new TCollection_HAsciiString("xcpp.ossg");
      Handle(TCollection_HAsciiString) asdbtype= new TCollection_HAsciiString("library");
      while (itpck.More()) {
	Handle(WOKernel_DevUnit) thesourceunit = Locator()->LocateDevUnit(itpck.Key());
	if (thesourceunit.IsNull()) {
	  okexec = Standard_False;
	  ErrorMsg() << "WOKDeliv_DeliveryOBJSSchema::Execute" << "Cannot locate unit : " << itpck.Key()->ToCString() << endm;
	  SetFailed();
	}
	else {
	  if (WOKernel_IsSchema(thesourceunit)) {
	    thesourceunit->Open();
	    Handle(WOKMake_Step) thestep = BuildProcess()->GetAndAddStep(thesourceunit,
									 namestep,
									 Handle(TCollection_HAsciiString) ());
	    if (!thestep.IsNull()) {
	      Handle(WOKMake_HSequenceOfOutputFile) thefiles = thestep->OutputFileList();
	      if (thefiles.IsNull()) {
		ErrorMsg() << "WOKDeliv_DeliveryOBJSSchema::Execute"
		  << "Step " << namestep << " not done for unit " << thesourceunit->Name() << endm;
		okexec = Standard_False;
	      }
	      else {
		for (Standard_Integer i=1; i<= thefiles->Length(); i++) {
		  Handle(WOKernel_File) theinfile = thefiles->Value(i)->File();
		  if (theinfile->TypeName()->IsSameString(asdbtype)) {
		    theinfile->GetPath();
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
	itpck.Next();
      }
    }
    else {
      ErrorMsg() <<  "WOKDeliv_DeliveryOBJSSchema::Execute" << "Cannot find delivery : " << myList->GetName()->ToCString() << endm;
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


Standard_Boolean WOKDeliv_DeliveryOBJSSchema::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  Handle(WOKernel_File)     file   = infile->File();
  if (file.IsNull()) return Standard_False;

  if(!strcmp(file->Name()->ToCString(), "COMPONENTS")) {
    return Standard_True;
  }

  return Standard_False;

}


Handle(TCollection_HAsciiString) WOKDeliv_DeliveryOBJSSchema::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STADMFILE);
  return result;
}
