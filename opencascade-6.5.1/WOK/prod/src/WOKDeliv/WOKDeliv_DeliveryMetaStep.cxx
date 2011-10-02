// Copyright: 	Matra-Datavision 1996
// File:	WOKDeliv_DeliveryMetaStep.cxx
// Created:	Wed Sep  4 11:41:32 1996
// Author:	Arnaud BOUZY
//		<adn>

#include <WOKDeliv_DeliveryMetaStep.ixx>

#include <WOKDeliv_ParseDelivery.hxx>
#include <WOKDeliv_DeliveryStep.hxx>
#include <WOKMake_AdmFileTypes.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKernel_Parcel.hxx>
#include <WOKernel_Locator.hxx>
#include <WOKBuilder_HSequenceOfObjectFile.hxx>
#include <WOKBuilder_ObjectFile.hxx>
#include <WOKBuilder_SharedLinker.hxx>
#include <WOKBuilder_HSequenceOfLibrary.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_HSequenceOfParamItem.hxx>
#include <WOKUtils_Shell.hxx>
#include <WOKUtils_ShellManager.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>

WOKDeliv_DeliveryMetaStep::WOKDeliv_DeliveryMetaStep(const Handle(WOKMake_BuildProcess)& aprocess,
						     const Handle(WOKernel_DevUnit)& aunit,
						     const Handle(TCollection_HAsciiString)& acode,
						     const Standard_Boolean checked,
						     const Standard_Boolean hidden)
  : WOKMake_MetaStep(aprocess,aunit,acode,checked,hidden)
{
}

Handle(WOKernel_File) WOKDeliv_DeliveryMetaStep::AdmFile(const Handle(TCollection_HAsciiString)& aname) const
{
  Handle(WOKernel_File) result;
  if (aname->IsSameString(OutputFilesFileName())) {
    if (!myOutLocator.IsNull()) {
      Handle(WOKernel_Parcel) theParcel = WOKDeliv_DeliveryStep::GetParcel(Unit(),myList->GetName());
      if (!theParcel.IsNull()) {
	Handle(WOKernel_DevUnit) parcelunit = WOKDeliv_DeliveryStep::GetParcelUnit(Unit(),theParcel,Unit());
	result = new WOKernel_File(aname, parcelunit, parcelunit->GetFileType(AdmFileType()));
	result->GetPath();
	return result;
      }
    }
  }
  result = new WOKernel_File(aname, Unit(), Unit()->GetFileType(AdmFileType()));
  result->GetPath();
  return result;
}

Handle(WOKernel_File) WOKDeliv_DeliveryMetaStep::GetCOMPONENTS() 
{
  // file COMPONENTS
  Handle(TCollection_HAsciiString) COMPName = new TCollection_HAsciiString("COMPONENTS");
  Handle(TCollection_HAsciiString) sourcetype = new TCollection_HAsciiString("source");
  return Locator()->Locate(Unit()->Name(), sourcetype, COMPName);
}

Handle(WOKDeliv_DeliveryList) WOKDeliv_DeliveryMetaStep::ParseCOMPONENTS(const Standard_Integer aStep)
{
  Handle(WOKDeliv_DeliveryList) dlist;
  Handle(WOKernel_File) filCOMP = GetCOMPONENTS();
  if (!filCOMP.IsNull()) {
    filCOMP->GetPath();
    if (WOKDeliv_Delivery_SetFile((char *)filCOMP->Path()->Name()->ToCString())) {
      dlist = WOKDeliv_Delivery_Parse(aStep);
      WOKDeliv_Delivery_CloseFile();
    }
    else {
      ErrorMsg() << "WOKDeliv_DeliveryMetaStep::Parse" << "Error getting file COMPONENTS" << endm;
      SetFailed();
    }
  }
  else {
    ErrorMsg() << "WOKDeliv_DeliveryMetaStep::Parse" << "Error locating file COMPONENTS" << endm;
    SetFailed();
  }
  return dlist;
}


Handle(TCollection_HAsciiString) WOKDeliv_DeliveryMetaStep::OutputDirTypeName() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STTMPDIR);
  return result;
}


Handle(WOKMake_InputFile) WOKDeliv_DeliveryMetaStep::GetInFileCOMPONENTS() const
{
  for (Standard_Integer i=1; i<= myinflow.Extent(); i++) {
    Handle(WOKernel_File) thefile = myinflow(i)->File();
    if (!strcmp(thefile->Name()->ToCString(),"COMPONENTS")) {
      return myinflow(i);
    }
  }
  return 0;
}

void WOKDeliv_DeliveryMetaStep::DefineOutLocator()
{
  if (!myList.IsNull()) {
    Handle(WOKernel_Parcel) theParcel = WOKDeliv_DeliveryStep::GetParcel(Unit(),myList->GetName());
    if (!theParcel.IsNull()) {
      Handle(TColStd_HSequenceOfHAsciiString) thevisib = new TColStd_HSequenceOfHAsciiString();
      thevisib->Append(theParcel->FullName());
      myOutLocator = new WOKernel_Locator(Unit()->Session(),thevisib);
    }
  }    
}


Handle(WOKernel_Locator) WOKDeliv_DeliveryMetaStep::OutLocator() const
{
  if (myOutLocator.IsNull()) {
    return WOKMake_Step::OutLocator();
  }
  return myOutLocator;
}


void WOKDeliv_DeliveryMetaStep::AcquitExecution(const Handle(WOKMake_HSequenceOfInputFile)& execlist)
{
  WOKMake_MetaStep::AcquitExecution(execlist);

  Handle(WOKernel_File) outfile = AdmFile(OutputFilesFileName());
  if (!myList.IsNull()) {
    Handle(WOKernel_Parcel) theparcel = WOKDeliv_DeliveryStep::GetParcel(Unit(),myList->GetName());
    Handle(WOKernel_DevUnit) theparcelunit = WOKDeliv_DeliveryStep::GetParcelUnit(Unit(),theparcel,Unit());
    Handle(WOKernel_File) unitoutfile = 
      new WOKernel_File(OutputFilesFileName(),
			theparcelunit,
			theparcelunit->GetFileType(AdmFileType()));
    WOKDeliv_DeliveryStep::CopyAFile(Unit(),outfile,unitoutfile,Standard_True);
  }
}


