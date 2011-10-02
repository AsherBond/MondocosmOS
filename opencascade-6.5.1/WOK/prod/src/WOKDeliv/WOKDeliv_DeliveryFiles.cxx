// Copyright: 	Matra-Datavision 1997
// File:	WOKDeliv_DeliveryFiles.cxx
// Created:	Mon Jan  6 15:37:18 1997
// Author:	Arnaud BOUZY
//		<adn>

#include <WOKDeliv_DeliveryFiles.ixx>
#include <WOKDeliv_ParseDelivery.hxx>
#include <WOKDeliv_DataMapIteratorOfDataMapOfFiles.hxx>
#include <WOKMake_AdmFileTypes.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_HSequenceOfOutputFile.hxx>
#include <WOKMake_MetaStep.hxx>

#include <WOKernel_File.hxx>
#include <WOKernel_Locator.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKTools_Messages.hxx>
#include <WOKTools_MapIteratorOfMapOfHAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

WOKDeliv_DeliveryFiles::WOKDeliv_DeliveryFiles(const Handle(WOKMake_BuildProcess)& aprocess,
					       const Handle(WOKernel_DevUnit)& aunit,
					       const Handle(TCollection_HAsciiString)& acode,
					       const Standard_Boolean checked,
					       const Standard_Boolean hidden)
: WOKDeliv_DeliveryStep(aprocess,aunit,acode,checked,hidden)
{
}

void WOKDeliv_DeliveryFiles::Execute(const Handle(WOKMake_HSequenceOfInputFile)&)
{
  myList = ParseCOMPONENTS(T_BASE);
  Standard_Boolean okexec = Standard_False;
  if (!myList.IsNull()) {
    Handle(WOKernel_Parcel) theParcel = GetParcel(Unit(),myList->GetName());
    if (!theParcel.IsNull()) {
      okexec = Standard_True;
      
      WOKDeliv_DataMapOfFiles mapfiles;
      
      Handle(TColStd_HSequenceOfHAsciiString) aseq = new TColStd_HSequenceOfHAsciiString();
      
      mapfiles.Bind(Unit()->Name(),aseq);

      WOKTools_MapIteratorOfMapOfHAsciiString itpck(myList->GetMap());
      while (itpck.More()) {
	aseq = new TColStd_HSequenceOfHAsciiString();
	mapfiles.Bind(itpck.Key(),aseq);
	itpck.Next();
      }

      DefineOutLocator();

      // Recuperation des steps de la delivery 
      BuildProcess()->ComputeSteps(Unit());
      const TColStd_SequenceOfHAsciiString& thesteps = BuildProcess()->GetUnitSteps(Unit()->Name());
      for (Standard_Integer i=1; i<= thesteps.Length(); i++) {
	Handle(WOKMake_Step) astep = BuildProcess()->Find(thesteps.Value(i));
	if (!astep->IsKind(STANDARD_TYPE(WOKMake_MetaStep))) {
	  Handle(WOKernel_File) anout = astep->LocateAdmFile(OutLocator(),astep->OutputFilesFileName());
	  if (!anout.IsNull()) {
	    ReadAnOutputFile(anout,mapfiles);
	  }
	}
	else {
	  Handle(TColStd_HSequenceOfHAsciiString) substeps = Handle(WOKMake_MetaStep)::DownCast(astep)->UnderlyingSteps();
	  for (Standard_Integer j=1; j<= substeps->Length(); j++) {
	    Handle(WOKMake_Step) substep = BuildProcess()->Find(substeps->Value(j));
	    Handle(WOKernel_File) anout = 
	      substep->LocateAdmFile(OutLocator(),substep->OutputFilesFileName());
	    if (!anout.IsNull()) {
	      ReadAnOutputFile(anout,mapfiles);
	    }
	  }
	}
      }

      WOKDeliv_DataMapIteratorOfDataMapOfFiles it(mapfiles);
      while (it.More()) {
	Handle(WOKernel_DevUnit) unit = OutLocator()->LocateDevUnit(it.Key());
	if (!unit.IsNull()) {
	  AddFileListFiles(unit,it.Value());
	  unit->SetFileList(it.Value());
	  unit->DumpFileList(OutLocator());
	}
	it.Next();
      }
    }
    else {
      ErrorMsg() <<  "WOKDeliv_DeliveryFiles::Execute" << "Cannot find delivery : " << myList->GetName()->ToCString() << endm;
    }
  }
  if (okexec) {
    SetSucceeded();
  }
  else {
    SetFailed();
  }
}

void WOKDeliv_DeliveryFiles::ReadAnOutputFile(const Handle(WOKernel_File)& anout,
					      WOKDeliv_DataMapOfFiles& mapfiles) const
{
  
  anout->GetPath();
  WOKMake_IndexedDataMapOfHAsciiStringOfOutputFile outfilemap;
  WOKMake_OutputFile::ReadFile(anout->Path(),OutLocator(),outfilemap);
  for (Standard_Integer i=1; i<= outfilemap.Extent(); i++) {
    Handle(WOKMake_OutputFile) anoutfile = outfilemap(i);
    if (anoutfile->IsProduction() &&
	anoutfile->IsPhysic()) {
      Handle(TCollection_HAsciiString) unitname = anoutfile->ID()->Token(":",1);
      if (mapfiles.IsBound(unitname)) {
	mapfiles(unitname)->Append(anoutfile->ID());
      }
      else {
	mapfiles(Unit()->Name())->Append(anoutfile->ID());
      }
    }
  }
  // Add outfile itself
  mapfiles(Unit()->Name())->Append(anout->LocatorName());
}

void WOKDeliv_DeliveryFiles::AddFileListFiles(const Handle(WOKernel_DevUnit)& anoutunit,
					      const Handle(TColStd_HSequenceOfHAsciiString)& aSeq)
{
  Handle(TCollection_HAsciiString) afilelistfile;
  Handle(WOKernel_File) admfile, dbadmfile, stadmfile;

  afilelistfile = new TCollection_HAsciiString(anoutunit->Name());
  afilelistfile->AssignCat(anoutunit->Params().Eval("%FILENAME_FILELIST_EXT"));

  admfile = new WOKernel_File(afilelistfile, anoutunit, anoutunit->GetFileType("admfile"));

  dbadmfile = new WOKernel_File(afilelistfile, anoutunit, anoutunit->GetFileType("dbadmfile"));

  stadmfile = new WOKernel_File(afilelistfile, anoutunit, anoutunit->GetFileType("stadmfile"));
  
  aSeq->Append(admfile->LocatorName());
  aSeq->Append(dbadmfile->LocatorName());
  aSeq->Append(stadmfile->LocatorName());

}

Standard_Boolean WOKDeliv_DeliveryFiles::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  Handle(WOKernel_File)     file   = infile->File();
  if (file.IsNull()) return Standard_False;

  if(!strcmp(file->Name()->ToCString(), "COMPONENTS")) {
    return Standard_True;
  }

  return Standard_False;

}


Handle(TCollection_HAsciiString) WOKDeliv_DeliveryFiles::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)ADMFILE);
  return result;
}

