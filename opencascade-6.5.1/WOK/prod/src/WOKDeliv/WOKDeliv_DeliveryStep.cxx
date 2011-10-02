// File:	WOKDeliv_DeliveryStep.cxx
// Created:	Fri Mar 29 13:55:59 1996
// Author:	Arnaud BOUZY
//		<adn>


#include <WOKDeliv_DeliveryStep.ixx>

#include <WOKDeliv_ParseDelivery.hxx>
#include <WOKMake_AdmFileTypes.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKTools_MapIteratorOfMapOfHAsciiString.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_Locator.hxx>
#include <WOKernel_Session.hxx>
#include <WOKernel_Workbench.hxx>
#include <WOKernel_Workshop.hxx>
#include <WOKernel_Factory.hxx>
#include <WOKernel_Warehouse.hxx>
#include <WOKBuilder_HSequenceOfObjectFile.hxx>
#include <WOKBuilder_ObjectFile.hxx>
#include <WOKBuilder_SharedLinker.hxx>
#include <WOKBuilder_HSequenceOfLibrary.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_HSequenceOfParamItem.hxx>
#include <WOKUtils_Shell.hxx>
#include <WOKUtils_ShellManager.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>

WOKDeliv_DeliveryStep::WOKDeliv_DeliveryStep(const Handle(WOKMake_BuildProcess)& aprocess,
					     const Handle(WOKernel_DevUnit)& aunit,
					     const Handle(TCollection_HAsciiString)& acode,
					     const Standard_Boolean checked,
					     const Standard_Boolean hidden)
  : WOKMake_Step(aprocess,aunit,acode,checked,hidden)
{
}

Handle(WOKernel_File) WOKDeliv_DeliveryStep::AdmFile(const Handle(TCollection_HAsciiString)& aname) const
{
  Handle(WOKernel_File) result;
  if (aname->IsSameString(OutputFilesFileName())) {
    if (!myOutLocator.IsNull()) {
      Handle(WOKernel_Parcel) theParcel = GetParcel(Unit(),myList->GetName());
      if (!theParcel.IsNull()) {
	Handle(WOKernel_DevUnit) parcelunit = GetParcelUnit(Unit(),theParcel,Unit());
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


Handle(WOKernel_File) WOKDeliv_DeliveryStep::GetCOMPONENTS() 
{
  // file COMPONENTS
  Handle(TCollection_HAsciiString) COMPName = new TCollection_HAsciiString("COMPONENTS");
  Handle(TCollection_HAsciiString) sourcetype = new TCollection_HAsciiString("source");
  return Locator()->Locate(Unit()->Name(), sourcetype, COMPName);
}

Handle(WOKDeliv_DeliveryList) WOKDeliv_DeliveryStep::ParseCOMPONENTS(const Standard_Integer aStep)
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
      ErrorMsg() << "WOKDeliv_DeliveryStep::Parse" << "Error getting file COMPONENTS" << endm;
      SetFailed();
    }
  }
  else {
    ErrorMsg() << "WOKDeliv_DeliveryStep::Parse" << "Error locating file COMPONENTS" << endm;
    SetFailed();
  }
  return dlist;
}

Standard_Boolean WOKDeliv_DeliveryStep::CopyAFile(const Handle(WOKernel_DevUnit)& delivunit,
						  const Handle(WOKernel_File)& fromFile,
						  const Handle(WOKernel_File)& toFile,
						  const Standard_Boolean silent)
{
  static Handle(TCollection_HAsciiString) theshellscripttype = new TCollection_HAsciiString("shellscript");
  fromFile->GetPath();
  toFile->GetPath();
  Handle(WOKUtils_Path) fromP = fromFile->Path();
  Handle(WOKUtils_Path) toP = toFile->Path();
  Standard_Boolean tocopy = Standard_True;
  if (toP->Exists()) {
    if (!fromP->IsNewer(toP)) {
      tocopy = !fromP->IsSameFile(toP);
      if (!tocopy && !silent) {
	WOK_TRACE {
	  VerboseMsg()("WOK_DELIV") << "WOKDeliv_DeliveryStep::CopyAFile" 
				  << "Identical file : " << fromP->Name() << " not copied." << endm;
	}
      }
    }
  }
  if (tocopy) {
    Handle(WOKUtils_Shell) ashell = WOKUtils_ShellManager::GetShell();
    if(!ashell->IsLaunched()) ashell->Launch();
    ashell->ClearOutput();
    delivunit->ChangeParams().Set("%Source",fromP->Name()->ToCString());
    
    Handle(TCollection_HAsciiString) thecomm;
    if (!fromP->IsDirectory()) {
      delivunit->ChangeParams().Set("%Dest",toP->Name()->ToCString());
      if (fromFile->TypeName()->IsSameString(theshellscripttype)) {
	thecomm = delivunit->ChangeParams().Eval("COMMAND_PreserveCopyAndChmodExecute");
      }
      else {
	thecomm = delivunit->ChangeParams().Eval("COMMAND_PreserveCopyAndChmod");
      }
    }
    else {
      delivunit->ChangeParams().Set("%Dest",toP->DirName()->ToCString());
      thecomm = delivunit->ChangeParams().Eval("COMMAND_CopyAndChmodRecursive");
    }

    ashell->Execute(thecomm);
    if (ashell->Status() == 0) {
      if (!silent) {
	InfoMsg() << "WOKDeliv_DeliveryStep::CopyAFile" 
	  << fromP->Name() << " copied to " << toP->Name() << endm;
      }
      ashell->ClearOutput();
      return Standard_True;
    }
    ErrorMsg() << "WOKDeliv_DeliveryStep::CopyAFile" 
      << "Error occured in shell while copying " << fromP->Name() << " to " << toP->Name() << endm;
    Handle(TColStd_HSequenceOfHAsciiString) aseq = ashell->Errors();
    for(Standard_Integer i=1; i<= aseq->Length(); i++) {
      ErrorMsg() << "WOKDeliv_DeliveryStep::CopyAFile" 
	<< aseq->Value(i) << endm;
    }
    ashell->ClearOutput();
    return Standard_False;
  }
  return Standard_True;
}

Handle(WOKernel_Parcel) WOKDeliv_DeliveryStep::GetParcel(const Handle(WOKernel_DevUnit)& delivunit, const Handle(TCollection_HAsciiString)& thename) 
{
  Handle(WOKernel_Session) theSession = delivunit->Session();
  Handle(TCollection_HAsciiString) thewb = delivunit->Nesting();

  Handle(TCollection_HAsciiString) thews = theSession->GetWorkbench(thewb)->Nesting();
  
  Handle(TCollection_HAsciiString) thef = theSession->GetWorkshop(thews)->Nesting();
  Handle(TCollection_HAsciiString) thewh =  theSession->GetFactory(thef)->Warehouse();
  Handle(WOKernel_Warehouse) theWarehouse = theSession->GetWarehouse(thewh);

  Handle(TColStd_HSequenceOfHAsciiString) theparcs = theWarehouse->Parcels();
  for (Standard_Integer i=1; i<= theparcs->Length(); i++) {
    Handle(WOKernel_Parcel) theparcel = theSession->GetParcel(theparcs->Value(i));
    if (theparcel->Name()->IsSameString(thename)) {
      return theparcel;
    }
  }
  return 0;
}

Handle(WOKernel_DevUnit) WOKDeliv_DeliveryStep::GetParcelUnit(const Handle(WOKernel_DevUnit)& delivunit,
							      const Handle(WOKernel_Parcel)& theParcel,
							      const Handle(WOKernel_DevUnit)& aUnit)
{
  Handle(WOKernel_DevUnit) theunit;

  if (!theParcel.IsNull()) {
    theParcel->Open();
    Handle(WOKernel_Session) theSession = delivunit->Session();
    Handle(TColStd_HSequenceOfHAsciiString) thenames = theParcel->Units();

    for (Standard_Integer i=1; i<= thenames->Length(); i++) {
      theunit = theSession->GetDevUnit(thenames->Value(i));
      if (theunit->Name()->IsSameString(aUnit->Name())) {
	theunit->Open();
	return theunit;
      }
    }

    
    theunit = theParcel->GetDevUnit(aUnit->Type(),aUnit->Name());
    Handle(WOKUtils_HSequenceOfParamItem) params = new WOKUtils_HSequenceOfParamItem();
    Handle(WOKUtils_HSequenceOfParamItem) buildpars = theunit->BuildParameters(params,Standard_True);
    theunit->Build(buildpars);
    theunit->Open();
    theParcel->AddUnit(theunit);
  }
  return theunit;
}

Handle(TCollection_HAsciiString) WOKDeliv_DeliveryStep::GetFullParcelName(const Handle(TCollection_HAsciiString)& aname) 
{
  if (myList.IsNull()) return 0;
  if (aname->IsSameString(Unit()->Name())) {
    return myList->GetName();
  }    
  Handle(WOKernel_Locator) theloc = DefineLocator();
  Handle(WOKernel_DevUnit) theunit = theloc->LocateDevUnit(aname);
  if (theunit.IsNull()) {
    ErrorMsg() << "WOKDeliv_DeliveryStep::GetFullParcelName" << "cannot locate delivery " << aname->ToCString() << endm;
    return 0;
  }
  Handle(WOKernel_UnitNesting) theNesting = Unit()->Session()->GetUnitNesting(theunit->Nesting());
  return theNesting->Name();
}

Handle(WOKernel_Locator) WOKDeliv_DeliveryStep::DefineLocator()
{
  if (myList.IsNull()) return myParcelLocator;
  if (myParcelLocator.IsNull()) {
    Handle(WOKernel_Session) theSession = Unit()->Session();
    Handle(TColStd_HSequenceOfHAsciiString) thevisib = new TColStd_HSequenceOfHAsciiString();
    Handle(TCollection_HAsciiString) thename;
    // Contains current parcel
    thename = GetParcel(Unit(),myList->GetName())->FullName();
    thevisib->Append(thename);
    WOKTools_MapIteratorOfMapOfHAsciiString itreq(myList->GetRequireMap());
    while (itreq.More()) {
      
      Handle(WOKernel_DevUnit) theunit = Locator()->LocateDevUnit(itreq.Key());
      if (!theunit.IsNull()) {
	Handle(WOKernel_UnitNesting) theNesting = Unit()->Session()->GetUnitNesting(theunit->Nesting());
	if (!theNesting->IsKind(STANDARD_TYPE(WOKernel_Workbench))) {
	  thevisib->Append(theNesting->FullName());
	}
	else {
	  Handle(TCollection_HAsciiString) COMPName = new TCollection_HAsciiString("COMPONENTS");
	  Handle(TCollection_HAsciiString) TypeName = new TCollection_HAsciiString("source");
	  Handle(WOKernel_File) theCOMPFile = Locator()->Locate(theunit->Name(),TypeName,COMPName);
	  if (!theCOMPFile.IsNull()) {
	    theCOMPFile->GetPath();
	    if (WOKDeliv_Delivery_SetFile((char *)theCOMPFile->Path()->Name()->ToCString())) {
	      Handle(WOKDeliv_DeliveryList) alist = WOKDeliv_Delivery_Parse(T_BASE);
	      WOKDeliv_Delivery_CloseFile();
	      if (!alist.IsNull()) {
		Handle(WOKernel_Parcel) theparcel = GetParcel(Unit(),alist->GetName());
		if (!theparcel.IsNull()) {
		  thevisib->Append(theparcel->FullName());
		}
	      }
	      else {
		ErrorMsg() << "WOKDeliv_DeliveryStep::DefineLocator" << "Error while parsing file COMPONENTS for unit " << theunit->Name()->ToCString() << endm;
	      }
	    }
	  }
	}
      }
      itreq.Next();
    }
    myParcelLocator = new WOKernel_Locator(theSession,thevisib);
    // a constituer
  }
  return myParcelLocator;
}

Handle(TCollection_HAsciiString) WOKDeliv_DeliveryStep::OutputDirTypeName() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STTMPDIR);
  return result;
}


Handle(WOKMake_InputFile) WOKDeliv_DeliveryStep::GetInFileCOMPONENTS() const
{
  for (Standard_Integer i=1; i<= myinflow.Extent(); i++) {
    Handle(WOKernel_File) thefile = myinflow(i)->File();
    if (!strcmp(thefile->Name()->ToCString(),"COMPONENTS")) {
      return myinflow(i);
    }
  }
  return 0;
}

void WOKDeliv_DeliveryStep::DefineOutLocator()
{
  if (!myList.IsNull()) {
    Handle(WOKernel_Parcel) theParcel = GetParcel(Unit(),myList->GetName());
    if (!theParcel.IsNull()) {
      Handle(TColStd_HSequenceOfHAsciiString) thevisib = new TColStd_HSequenceOfHAsciiString();
      thevisib->Append(theParcel->FullName());
      myOutLocator = new WOKernel_Locator(Unit()->Session(),thevisib);
    }
  }    
}


Handle(WOKernel_Locator) WOKDeliv_DeliveryStep::OutLocator() const
{
  if (myOutLocator.IsNull()) {
    return WOKMake_Step::OutLocator();
  }
  return myOutLocator;
}

Standard_Boolean WOKDeliv_DeliveryStep::HandleOutputFile(const Handle(WOKMake_OutputFile)& afile) 
{
  if(afile.IsNull()) return Standard_False;
  if(afile->IsProduction())
    {
      switch(afile->Status())
	{
	case WOKMake_Disappeared:
	  {
	    if(afile->File().IsNull()) {
	      Handle(WOKUtils_Path) oldpath = afile->LastPath();
	      if (oldpath.IsNull()) return Standard_False;
	      oldpath->RemoveFile();
	      InfoMsg() << "WOKDeliv_DeliveryStep" 
		<< "Remove file " << oldpath->Name() << endm;
	      
	      return Standard_True;
	    }
	    
	    Handle(WOKUtils_Shell) ashell = Shell();
	    Handle(TCollection_HAsciiString) astr, atempl, acmd;
	    
	    if(!ashell->IsLaunched()) ashell->Launch();
	    ashell->Lock();
	    
	    astr = new TCollection_HAsciiString("%WOKSteps_Del_");
	    astr->AssignCat(afile->File()->TypeName());
	    
	    if(! Unit()->Params().IsSet(astr->ToCString()))
	      {
		astr = new TCollection_HAsciiString("%WOKSteps_Del_Default");
		if(Unit()->Params().IsSet(astr->ToCString()))
		  {
		    atempl =  Unit()->Params().Eval(astr->ToCString(),Standard_True);
		  }
	      }
	    else
	      {
		atempl = Unit()->Params().Eval(astr->ToCString(),Standard_True);
	      }
	    
	    if(atempl.IsNull())
	      {
		WarningMsg() << "WOKDeliv_DeliveryStep::HandleOutputFile"
		  << "Could not determine Del action for type : " << afile->File()->TypeName() << endm;
		ashell->UnLock();
		return Standard_False;
	      }
	    
	    if(! Unit()->Params().IsSet(atempl->ToCString()))
	      {
		ErrorMsg() << "WOKDeliv_DeliveryStep::HandleOutputFile"
		  << "Could not eval Del action (" << atempl << ") for type : " << afile->File()->TypeName() << endm;
		ashell->UnLock();
		return Standard_False;
	      }
	    
	    
	    if(afile->File()->Path()->Exists() || afile->File()->Path()->IsSymLink())
	      {
		Unit()->Params().Set("%FilePath", afile->File()->Path()->Name()->ToCString());
		
		acmd = Unit()->Params().Eval(atempl->ToCString(),Standard_True);
//                cout << "WOKDeliv_DeliveryStep : " << afile->File()->Path()->Name()->ToCString() << endl ;
		
		if(!acmd.IsNull())
		  {
		    InfoMsg() << "WOKDeliv_DeliveryStep::HandleOutputFile"
		      << "Invoking " << atempl << " on " << afile->File()->Path()->Name() << endm;
		    
		    ashell->Execute(acmd);
		    
		    if(ashell->Status())
		      {
			Handle(TColStd_HSequenceOfHAsciiString) resseq = ashell->Errors();
			Standard_Boolean ph = ErrorMsg().PrintHeader();
			
			ErrorMsg() << "WOKDeliv_DeliveryStep::HandleOutputFile" << "Errors occured in Shell" << endm;
			ErrorMsg().DontPrintHeader();
			for(Standard_Integer i=1; i<= resseq->Length(); i++)
			  {
			    ErrorMsg() << "WOKMake_Step::HandleOutputFile" << resseq->Value(i) << endm;
			  }
			if(ph) ErrorMsg().DoPrintHeader();
		      }
		    OutLocator()->ChangeRemove(afile->File());
		    ashell->ClearOutput();
		    return Standard_True;
		  }
	      }
	    else
	      {
		if(afile->File()->Path()->IsSymLink())
		  {
		    WarningMsg() << "WOKDeliv_DeliveryStep::HandleOutputFile"
		      << "Disappeared File (" << afile->File()->UserPathName() << ") does not exists " << endm;
		  }
	      }
	    ashell->UnLock();
	    
	  }
          default: break;
	}
    }
  return Standard_False;
}

void WOKDeliv_DeliveryStep::AcquitExecution(const Handle(WOKMake_HSequenceOfInputFile)& execlist)
{
  WOKMake_Step::AcquitExecution(execlist);

  Handle(WOKernel_File) outfile = AdmFile(OutputFilesFileName());
  if (!myList.IsNull()) {
    Handle(WOKernel_Parcel) theparcel = GetParcel(Unit(),myList->GetName());
    Handle(WOKernel_DevUnit) theparcelunit = GetParcelUnit(Unit(),theparcel,Unit());
    Handle(WOKernel_File) unitoutfile = 
      new WOKernel_File(OutputFilesFileName(),
			theparcelunit,
			theparcelunit->GetFileType(AdmFileType()));
    CopyAFile(Unit(),outfile,unitoutfile,Standard_True);
  }
}
