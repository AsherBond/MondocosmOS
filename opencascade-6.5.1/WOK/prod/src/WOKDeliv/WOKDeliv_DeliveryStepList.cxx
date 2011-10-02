// Copyright: 	Matra-Datavision 1998
// File:	WOKDeliv_DeliveryStepList.cxx
// Created:	Mon Jan 12 10:17:15 1998
// Author:	Arnaud BOUZY
//		<adn>

#include <WOKDeliv_DeliveryStepList.ixx>
#include <WOKDeliv_ParseDelivery.hxx>
#include <WOKMake_AdmFileTypes.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_HSequenceOfOutputFile.hxx>

#include <WOKernel_File.hxx>
#include <WOKernel_Locator.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKTools_Messages.hxx>
#include <WOKTools_MapIteratorOfMapOfHAsciiString.hxx>

WOKDeliv_DeliveryStepList::WOKDeliv_DeliveryStepList(const Handle(WOKMake_BuildProcess)& aprocess,
					   const Handle(WOKernel_DevUnit)& aunit,
					   const Handle(TCollection_HAsciiString)& acode,
					   const Standard_Boolean checked,
					   const Standard_Boolean hidden)
: WOKDeliv_DeliveryStep(aprocess,aunit,acode,checked,hidden)
{
}

void WOKDeliv_DeliveryStepList::Execute(const Handle(WOKMake_HSequenceOfInputFile)&)
{
  myList = ParseCOMPONENTS(T_BASE);
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
	  ErrorMsg() << "WOKDeliv_DeliveryStepList::Execute" << "Cannot locate unit : " << itpck.Key()->ToCString() << endm;
	  SetFailed();
	}
	else {
	  thesourceunit->Open();
	  Handle(TCollection_HAsciiString) paramsteps = ParameterCodeName();
	  paramsteps->AssignCat("steps");
	  Handle(TCollection_HAsciiString) namesteps = thesourceunit->Params().Eval(paramsteps->ToCString());
	  if (!namesteps.IsNull()) {
	    Handle(TCollection_HAsciiString) extcode = ParameterCodeName();
	    extcode->AssignCat("extensions");
	    Handle(TCollection_HAsciiString) extens = thesourceunit->Params().Eval(extcode->ToCString());
	    Handle(TCollection_HAsciiString) typcode = ParameterCodeName();
	    typcode->AssignCat("types");
	    Handle(TCollection_HAsciiString) goodtypes = thesourceunit->Params().Eval(typcode->ToCString());
	    
	    BuildProcess()->ComputeSteps(thesourceunit);

	    Standard_Integer nums = 1;
	    Handle(TCollection_HAsciiString) namestep = namesteps->Token(" \t",nums);
	    while (!namestep->IsEmpty()) {
	      
	      Handle(WOKMake_Step) thestep = BuildProcess()->Find(thesourceunit,
								  namestep,
								  Handle(TCollection_HAsciiString) ());
	      if (!thestep.IsNull()) {
		thestep->DontExecute();
		thestep->Make();
		Handle(WOKMake_HSequenceOfOutputFile) thefiles = thestep->OutputFileList();
		if (thefiles.IsNull()) {
		  ErrorMsg() << "WOKDeliv_DeliveryStepList::Execute"
		    << "Step " << namestep << " not done for unit " << thesourceunit->Name() << endm;
		  okexec = Standard_False;
		}
		else {
		  for (Standard_Integer i=1; i<= thefiles->Length(); i++) {
		    Handle(WOKernel_File) theinfile = thefiles->Value(i)->File();
		    if (!theinfile.IsNull()) {
		      theinfile->GetPath();
		      
		      if (IsToCopy(theinfile,goodtypes,extens)) {
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
	      nums++;
	      namestep = namesteps->Token(" \t",nums);
	    }
	  }
	}
	itpck.Next();
      }
    }
    else {
      ErrorMsg() <<  "WOKDeliv_DeliveryStepList::Execute" << "Cannot find delivery : " << myList->GetName()->ToCString() << endm;
    }
  }
  if (okexec) {
    SetSucceeded();
  }
  else {
    SetFailed();
  }
}


Standard_Boolean WOKDeliv_DeliveryStepList::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  Handle(WOKernel_File)     file   = infile->File();
  if (file.IsNull()) return Standard_False;

  if(!strcmp(file->Name()->ToCString(), "COMPONENTS")) {
    return Standard_True;
  }

  return Standard_False;

}


Handle(TCollection_HAsciiString) WOKDeliv_DeliveryStepList::AdmFileType() const
{
  Handle(TCollection_HAsciiString) pcode = ParameterCodeName();
  pcode->AssignCat("admfiletype");
  Handle(TCollection_HAsciiString) admres = Unit()->Params().Eval(pcode->ToCString());
  if (!admres.IsNull()) {
    return admres;
  }
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)ADMFILE);
  return result;
}

Handle(TCollection_HAsciiString) WOKDeliv_DeliveryStepList::ParameterCodeName() const
{
  Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString("%WOKSteps_");
  result->AssignCat("delivery_");
  Handle(TCollection_HAsciiString) code = new TCollection_HAsciiString(Code()->ToCString());
  code->ChangeAll('.','_');
  result->AssignCat(code);
  result->AssignCat("_");
  return result;
}

Standard_Boolean WOKDeliv_DeliveryStepList::IsToCopy(const Handle(WOKernel_File)& file,
						     const Handle(TCollection_HAsciiString)& goodtypes,
						     const Handle(TCollection_HAsciiString)& extens) const
{
  Standard_Integer i;

  if (!goodtypes.IsNull()) {
    i = 1;
    Handle(TCollection_HAsciiString) atype = goodtypes->Token(" \t",i);
    Standard_Boolean found = Standard_False;
    while ((!atype->IsEmpty()) && !found) {
      found = atype->IsSameString(file->TypeName());
      i++;
      atype = goodtypes->Token(" \t",i);
    }
    if (!found) return Standard_False;
  }

  Handle(WOKUtils_Path) thepath = file->Path();
  if (thepath.IsNull()) return Standard_False;

  if (!extens.IsNull()) {

    Handle(TCollection_HAsciiString) fileext = thepath->ExtensionName();
    i = 1;
    Handle(TCollection_HAsciiString) atype = extens->Token(" \t",i);
    Standard_Boolean found = Standard_False;
    while ((!atype->IsEmpty()) && !found) {
      found = atype->IsSameString(fileext,Standard_False);
      i++;
      atype = extens->Token(" \t",i);
    }
    if (!found) return Standard_False;
  }

  return Standard_True;

}
