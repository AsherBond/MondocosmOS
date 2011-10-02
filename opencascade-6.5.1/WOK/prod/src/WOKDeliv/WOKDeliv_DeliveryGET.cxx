// File:	WOKDeliv_DeliveryGET.cxx
// Created:	Fri Mar 29 16:55:57 1996
// Author:	Arnaud BOUZY
//		<adn>

#include <WOKDeliv_DeliveryGET.ixx>
#include <WOKMake_AdmFileTypes.hxx>
#include <WOKDeliv_ParseDelivery.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_HSequenceOfOutputFile.hxx>

#include <WOKernel_DevUnit.hxx>
#include <WOKernel_BasicUnitTypes.hxx>
#include <WOKernel_Session.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_Locator.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_AdmFile.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKTools_MapIteratorOfMapOfHAsciiString.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>

WOKDeliv_DeliveryGET::WOKDeliv_DeliveryGET(const Handle(WOKMake_BuildProcess)& aprocess,
					   const Handle(WOKernel_DevUnit)& aunit,
					   const Handle(TCollection_HAsciiString)& acode,
					   const Standard_Boolean checked,
					   const Standard_Boolean hidden)
: WOKDeliv_DeliveryStep(aprocess,aunit,acode,checked,hidden)
{
}

void WOKDeliv_DeliveryGET::Execute(const Handle(WOKMake_HSequenceOfInputFile)&)
{
  
  myList = ParseCOMPONENTS(T_GET);
  Standard_Boolean okexec = Standard_False;
  Handle(WOKBuilder_Entity) bidon;
  Handle(WOKMake_InputFile) infileCOMPONENTS = GetInFileCOMPONENTS();
  if (!myList.IsNull()) {
    // Get from COMPONENTS
    Handle(WOKernel_Parcel) theParcel = GetParcel(Unit(),myList->GetName());
    if (!theParcel.IsNull()) {
      okexec = Standard_True;
    
      WOKTools_MapIteratorOfMapOfHAsciiString itget(myList->GetMap());
      Handle(TCollection_HAsciiString) aname;
      Handle(TCollection_HAsciiString) atypename;
      Handle(TCollection_HAsciiString) aunitname;
      Handle(WOKernel_DevUnit) aunit;
      Handle(WOKernel_FileType) atype;
      while (itget.More()) {
	Handle(WOKernel_File) thefile = Locator()->Locate(itget.Key());
	if (thefile.IsNull()) {
	  ErrorMsg() << "WOKDeliv_DeliveryGET::Execute" 
	    << "cannot locate file " << itget.Key() << endm;
	  okexec = Standard_False;
	}
	else {
	  thefile->GetPath();
	  Handle(WOKMake_OutputFile) outfile 
	    = new WOKMake_OutputFile(thefile->LocatorName(),
				     thefile,
				     bidon,
				     thefile->Path());
	  outfile->SetReference();
	  outfile->SetLocateFlag(Standard_True);
	  outfile->SetExtern();
	  AddExecDepItem(infileCOMPONENTS,outfile,Standard_True);
	}
	itget.Next();
      }
    }
  }
  myList = ParseCOMPONENTS(T_GETRES);
  // Resource or Documentation Units
  if (!myList.IsNull()) {
    WOKTools_MapIteratorOfMapOfHAsciiString itget(myList->GetMap());
    while (itget.More()) {
      Handle(WOKernel_DevUnit) thesourceunit = Locator()->LocateDevUnit(itget.Key());
      if (WOKernel_IsResource(thesourceunit) || WOKernel_IsDocumentation(thesourceunit)) {
	Handle(WOKernel_Workbench) abench   = thesourceunit->Session()->GetWorkbench(thesourceunit->Nesting());
	Handle(WOKernel_Locator)   alocator = new WOKernel_Locator(abench);
	Handle(TCollection_HAsciiString) theFILEStype = new TCollection_HAsciiString("source");
	Handle(TCollection_HAsciiString) theFILESname = new TCollection_HAsciiString("FILES");
	Handle(WOKernel_File) theFILES = alocator->Locate(thesourceunit->Name(),theFILEStype,theFILESname);
	if (!theFILES.IsNull()) {
	  theFILES->GetPath();
	  WOKUtils_AdmFile afiles(theFILES->Path());
	  Handle(TColStd_HSequenceOfHAsciiString) aasciiseq;
	  Handle(TCollection_HAsciiString) astr;

	  aasciiseq = afiles.Read();

	  if(!aasciiseq.IsNull()) {
	    for(Standard_Integer i=1; i<=aasciiseq->Length(); i++) {
	      astr = aasciiseq->Value(i);
	      
	      astr->LeftAdjust();
	      astr->RightAdjust();
	      
	      Standard_Integer first = astr->Search(":::");

	      if (first <= 1) {
		Handle(TCollection_HAsciiString) filenameFILES = thesourceunit->Params().Eval("%FILENAME_FILES");
		if (strcmp(filenameFILES->ToCString(),astr->ToCString())) {
		  ErrorMsg() << "WOKDeliv_DeliveryGET::Execute" 
		    << "No type specified for file " << astr << " in unit " << thesourceunit->Name() << endm;
		  okexec = Standard_False;
		}
	      }
	      else {
		Handle(TCollection_HAsciiString) type = astr->SubString(1,first-1);
		Handle(TCollection_HAsciiString) name = astr->SubString(first+3, astr->Length());
		
		Handle(WOKernel_File) thefile = Locator()->Locate(thesourceunit->Name(),type,name);
		if (thefile.IsNull()) {
		  ErrorMsg() << "WOKDeliv_DeliveryGET::Execute" 
		    << "cannot locate file " << name << endm;
		  okexec = Standard_False;
		}
		else {
		  thefile->GetPath();
		  Handle(WOKMake_OutputFile) outfile 
		    = new WOKMake_OutputFile(thefile->LocatorName(),
					     thefile,
					     bidon,
					     thefile->Path());
		  outfile->SetReference();
		  outfile->SetLocateFlag(Standard_True);
		  outfile->SetExtern();
		  AddExecDepItem(infileCOMPONENTS,outfile,Standard_True);
		}
	      }
	    }
	  }
	}
	else {
	  ErrorMsg() << "WOKDeliv_DeliveryGET::Execute" << "cannot find file FILES in resource unit " << thesourceunit->Name() << endm;
	  okexec = Standard_False;
	}
      }
      itget.Next();
    }
  }
  if (okexec) SetSucceeded();
}



Standard_Boolean WOKDeliv_DeliveryGET::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  Handle(WOKernel_File)     file   = infile->File();
  if (file.IsNull()) return Standard_False;

  if(!strcmp(file->Name()->ToCString(), "COMPONENTS")) {
    return Standard_True;
  }

  return Standard_False;

}


Handle(TCollection_HAsciiString) WOKDeliv_DeliveryGET::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STADMFILE);
  return result;
}

