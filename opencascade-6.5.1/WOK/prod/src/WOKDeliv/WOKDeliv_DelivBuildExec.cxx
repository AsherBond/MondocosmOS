// Copyright: 	Matra-Datavision 1996
// File:	WOKDeliv_DelivBuildExec.cxx
// Created:	Tue Oct 29 14:56:11 1996
// Author:	Arnaud BOUZY
//		<adn>


#include <WOKDeliv_DelivBuildExec.ixx>
#include <WOKDeliv_ParseDelivery.hxx>
#include <WOKDeliv_DataMapOfParcel.hxx>

#include <WOKMake_OutputFile.hxx>

#include <WOKernel_File.hxx>
#include <WOKernel_Locator.hxx>
#include <WOKernel_Session.hxx>
#include <WOKernel_Warehouse.hxx>
#include <WOKernel_Workbench.hxx>
#include <WOKernel_Factory.hxx>
#include <WOKernel_Workshop.hxx>
#include <WOKUtils_Path.hxx>

#include <WOKTools_IndexedMapOfHAsciiString.hxx>
#include <WOKTools_MapIteratorOfMapOfHAsciiString.hxx>
#include <WOKTools_Messages.hxx>

#include <EDL_API.hxx>

WOKDeliv_DelivBuildExec::WOKDeliv_DelivBuildExec(const Handle(WOKMake_BuildProcess)& aprocess,
						 const Handle(WOKernel_DevUnit)& aunit,
						 const Handle(TCollection_HAsciiString)& acode,
						 const Standard_Boolean checked,
						 const Standard_Boolean hidden)
: WOKDeliv_DeliveryCopy(aprocess,aunit,acode,checked,hidden)
{
}


void WOKDeliv_DelivBuildExec::Execute(const Handle(WOKMake_HSequenceOfInputFile)& infiles)
{
  WOKDeliv_DeliveryCopy::Execute(infiles);
  Handle(WOKernel_DevUnit) thesourceunit;
  if (myList.IsNull()) {
    myList = ParseCOMPONENTS(T_DYNAMIC);
  }
  Handle(WOKernel_Parcel) theParcel = GetParcel(Unit(),myList->GetName());
  Standard_Boolean okexec = Standard_False;
    
  if (!theParcel.IsNull()) {
    // SetOutputDir
    Handle(WOKernel_DevUnit) parcelunit = GetParcelUnit(Unit(),theParcel,Unit());
    Handle(WOKernel_File) afile = new WOKernel_File(parcelunit, parcelunit->GetFileType(OutputDirTypeName()));
    afile->GetPath();
    SetOutputDir(afile->Path());
    
    Handle(TCollection_HAsciiString) nameunit = SubCode()->Token("_",1);
    Handle(TCollection_HAsciiString) nameexec = SubCode()->Token("_",2);
    thesourceunit = Locator()->LocateDevUnit(nameunit);
    if (nameexec->IsEmpty()) { // Engine
      nameexec = thesourceunit->Name();
      Handle(WOKernel_DevUnit) destunit = GetParcelUnit(Unit(),theParcel,thesourceunit);
      okexec = MakeldFile(theParcel,thesourceunit,destunit,nameexec,infiles);
    }
    else {
      okexec = Standard_True;
    }
    if (okexec) {
      SetSucceeded();
    }
    else {
      SetFailed();
    }
  }
}



Standard_Boolean WOKDeliv_DelivBuildExec::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  if(!infile->File().IsNull()) {
    return Standard_True;
  }
  return Standard_False;
}



Standard_Boolean WOKDeliv_DelivBuildExec::MakeldFile
//(const Handle(WOKernel_Parcel)& theParcel,
(const Handle(WOKernel_Parcel)& ,
 const Handle(WOKernel_DevUnit)& thesourceunit, 
 const Handle(WOKernel_DevUnit)& thedestunit, 
 const Handle(TCollection_HAsciiString)& nameexec,
 const Handle(WOKMake_HSequenceOfInputFile)& infiles)
{
  Standard_Boolean okexec = Standard_True;
  Handle(WOKBuilder_Entity) bidon;

  // Find all parcels involved

  Handle(TColStd_HSequenceOfHAsciiString) thevisib = VisibleParcels();

  // Locate resulting file 

  Handle(TCollection_HAsciiString) ldname = new TCollection_HAsciiString(nameexec);
  ldname->AssignCat(".ldt");
  Handle(WOKernel_File) resfileld = new WOKernel_File(ldname,
						      thedestunit,
						      thedestunit->GetFileType("library"));
  resfileld->GetPath();
  
  // Write template file

  Handle(TCollection_HAsciiString) nameedl = new TCollection_HAsciiString("WOKDeliv_LDSCRIPT.edl");
  Unit()->Params().LoadFile(nameedl);
  
  Handle(EDL_API) anapi = new EDL_API();
  if (anapi->OpenFile("MYFILE",resfileld->Path()->Name()->ToCString()) != EDL_NORMAL) {
    ErrorMsg() << "WOKDeliv_DelivBuilExec::Execute"
      << "Cannot open file " << resfileld->Path()->Name() << endm;
    okexec = Standard_False;
  }
  else {
    Unit()->Params().Set("%EngineName",thedestunit->Name()->ToCString());
    
    Handle(TCollection_HAsciiString) curval = Unit()->Params().Eval("WOKDeliv_LDInit");
    anapi->AddVariable("%MYVAR",curval->ToCString());
    anapi->WriteFile("MYFILE","%MYVAR");
    Handle(TCollection_HAsciiString) curhome;

    Handle(TCollection_HAsciiString) apref = thesourceunit->Params().Eval("%ENV_EngineLoadPath");
    if (!apref.IsNull()) {
      Unit()->Params().Set("%Value",apref->ToCString());
      Handle(TCollection_HAsciiString) prevld = Unit()->Params().Eval("WOKDeliv_AddingValue");
      anapi->AddVariable("%MYVAR",prevld->ToCString());
      anapi->WriteFile("MYFILE","%MYVAR");
    }
    curval = Unit()->Params().Eval("WOKDeliv_MAKELDInit");
    anapi->AddVariable("%MYVAR",curval->ToCString());
    anapi->WriteFile("MYFILE","%MYVAR");
    Standard_Integer i;
    for (i=1; i<= thevisib->Length(); i++) {
      Unit()->Params().Set("%UlName",thevisib->Value(i)->ToCString());
      if (i == 1) {
	curval = Unit()->Params().Eval("WOKDeliv_FirstValue");
      }
      else {
	Unit()->Params().Set("%PreviousVal",curval->ToCString());
	curval = Unit()->Params().Eval("WOKDeliv_NextValue");
      }
      curhome = Unit()->Params().Eval("WOKDeliv_DeclHome");
      anapi->AddVariable("%MYVAR",curhome->ToCString());
      anapi->WriteFile("MYFILE","%MYVAR");
    }
    anapi->AddVariable("%MYVAR",curval->ToCString());
    anapi->WriteFile("MYFILE","%MYVAR");
    curval = Unit()->Params().Eval("WOKDeliv_LDEnd");
    anapi->AddVariable("%MYVAR",curval->ToCString());
    anapi->WriteFile("MYFILE","%MYVAR");

    curval = Unit()->Params().Eval("WOKDeliv_MAKELDList");
    anapi->AddVariable("%MYVAR",curval->ToCString());
    anapi->WriteFile("MYFILE","%MYVAR");
    for (i=1; i<= thevisib->Length(); i++) {
      Unit()->Params().Set("%UlName",thevisib->Value(i)->ToCString());
      curhome = Unit()->Params().Eval("WOKDeliv_AddULList");
      anapi->AddVariable("%MYVAR",curhome->ToCString());
      anapi->WriteFile("MYFILE","%MYVAR");
    }
    curval = Unit()->Params().Eval("WOKDeliv_EndLDList");
    anapi->AddVariable("%MYVAR",curval->ToCString());
    anapi->WriteFile("MYFILE","%MYVAR");
    
    
    anapi->CloseFile("MYFILE");
    
    
    Handle(WOKMake_OutputFile) outfile =  new WOKMake_OutputFile(resfileld->LocatorName(),
								 resfileld,
								 bidon,
								 resfileld->Path());
    outfile->SetProduction();
    outfile->SetExtern();
    outfile->SetLocateFlag(Standard_True);
    for (i=1; i <= infiles->Length(); i++) {
      AddExecDepItem(infiles->Value(i),outfile,Standard_True);
    }
    
  }
  return okexec;
}
  
  

Handle(TColStd_HSequenceOfHAsciiString) WOKDeliv_DelivBuildExec::VisibleParcels() const 
{
  Handle(TColStd_HSequenceOfHAsciiString) thevisib = new TColStd_HSequenceOfHAsciiString();
  if (myList.IsNull()) return thevisib;
  Handle(WOKernel_Session) theSession = Unit()->Session();

  Handle(TCollection_HAsciiString) thename;

  Handle(TCollection_HAsciiString) thewb = Unit()->Nesting();

  Handle(TCollection_HAsciiString) thews = theSession->GetWorkbench(thewb)->Nesting();
  
  Handle(TCollection_HAsciiString) thef = theSession->GetWorkshop(thews)->Nesting();
  Handle(TCollection_HAsciiString) thewh =  theSession->GetFactory(thef)->Warehouse();
  Handle(WOKernel_Warehouse) theWarehouse = theSession->GetWarehouse(thewh);

  WOKDeliv_DataMapOfParcel parcelmap;
  Handle(TColStd_HSequenceOfHAsciiString) theparcs = theWarehouse->Parcels();
  for (Standard_Integer i=1; i<= theparcs->Length(); i++) {
    Handle(WOKernel_Parcel) theparcel = theSession->GetParcel(theparcs->Value(i));
    if (!theparcel.IsNull()) {
      parcelmap.Bind(theparcel->Name(),theparcel);
    }
  }
  
  Handle(WOKernel_Parcel) theparcel;

  WOKTools_MapOfHAsciiString tofind,found;
  tofind.Add(myList->GetName());
  while (!tofind.IsEmpty()) {
    WOKTools_MapIteratorOfMapOfHAsciiString it(tofind);
    thename = it.Key();
    tofind.Remove(thename);
    if (parcelmap.IsBound(thename)) {
      theparcel = parcelmap(thename);
      theparcel->Open();
      thevisib->Append(theparcel->Name());
      found.Add(thename);
      Handle(TCollection_HAsciiString) deliv = theparcel->Delivery();
      Handle(WOKernel_DevUnit) thedeliv = theSession->GetDevUnit(theparcel->NestedUniqueName(deliv));
      if (!thedeliv.IsNull()) {
	thedeliv->Open();
	Handle(TCollection_HAsciiString) allreq = thedeliv->EvalParameter("AllRequisites",Standard_False);
	if (allreq.IsNull()) {
	  WarningMsg() << "WOKDeliv_DelivBuildExec::VisibleParcels"
	    << "Unable to get dependencies from parcel : << theparcel->Name()->ToCString()" << endm;
	  WarningMsg() << "WOKDeliv_DelivBuildExec::VisibleParcels"
	    << "Missing parameter AllRequisites" << endm;
	}
	else {
	  Standard_Integer ind=1;
	  thename = allreq;
	  while (!thename->IsEmpty()) {
	    thename = allreq->Token(" ",ind);
	    ind++;
	    if (!thename->IsEmpty()) {
	      if (!found.Contains(thename)) {
		tofind.Add(thename);
	      }
	    }
	  }
	}
      }
      else {
	ErrorMsg() << "WOKDeliv_DelivBuildExec::VisibleParcels"
	  << " Unable to get delivery " << deliv
	    << " from parcel " << theparcel->Name() << endm;
      }
    }
  }
  return thevisib;
}
  
