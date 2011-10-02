// Copyright: 	Matra-Datavision 1997
// File:	WOKDeliv_DeliveryDATA.cxx
// Created:	Wed Feb  5 14:39:14 1997
// Author:	Arnaud BOUZY
//		<adn>

#include <WOKDeliv_DeliveryDATA.ixx>
#include <WOKDeliv_ParseDelivery.hxx>
#include <WOKDeliv_DeliveryStep.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_AdmFileTypes.hxx>
#include <WOKMake_HSequenceOfOutputFile.hxx>
#include <WOKernel_Locator.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_BasicUnitTypes.hxx>
#include <WOKernel_FileTypeBase.hxx>
#include <WOKernel_Parcel.hxx>
#include <WOKUtils_Path.hxx>
#include <EDL_API.hxx>

#include <WOKTools_MapIteratorOfMapOfHAsciiString.hxx>
#include <WOKTools_Messages.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

WOKDeliv_DeliveryDATA::WOKDeliv_DeliveryDATA(const Handle(WOKMake_BuildProcess)& aprocess,
					     const Handle(WOKernel_DevUnit)& aunit,
					     const Handle(TCollection_HAsciiString)& acode,
					     const Standard_Boolean checked,
					     const Standard_Boolean hidden)
: WOKDeliv_DeliveryMetaStep(aprocess,aunit,acode,checked,hidden)
{
}

void WOKDeliv_DeliveryDATA::Execute(const Handle(WOKMake_HSequenceOfInputFile)&)
{
  Standard_Boolean okexec = Standard_False;
  myList = ParseCOMPONENTS(T_DATAOBJECT);
  if (myList.IsNull()) {
    SetFailed();
  }
  else {
    if (SubCode().IsNull()) { // Meta Step
      okexec = ExecuteMetaStep();
    }
    else {
      okexec = ExecuteSubStep();
    }
    if (okexec) {
      SetSucceeded();
    }
    else {
      SetFailed();
    }
  }
}

Standard_Boolean WOKDeliv_DeliveryDATA::ExecuteMetaStep()
{
  Standard_Boolean okexec = Standard_True;
  WOKTools_MapIteratorOfMapOfHAsciiString itpck(myList->GetMap());
  while (itpck.More()) {
    Handle(WOKernel_DevUnit) thesourceunit = Locator()->LocateDevUnit(itpck.Key());
    if (thesourceunit.IsNull()) {
      okexec = Standard_False;
      ErrorMsg() << "WOKDeliv_DeliveryDATA::Execute" << "Cannot locate unit : " << itpck.Key()->ToCString() << endm;
    }
    else {
      if (WOKernel_IsFrontal(thesourceunit)) {
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
	
	Handle(WOKMake_Step) astep = BuildProcess()->GetAndAddStep(Unit(),
								   Code(),
								   thesourceunit->Name());
	astep->DoExecute();
	astep->SetPrecedenceSteps(PrecedenceSteps());
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
	AddExecDepItem(GetInFileCOMPONENTS(),outfile, Standard_True);
      }
    }
    itpck.Next();
  }
  return okexec;
} 


Standard_Boolean WOKDeliv_DeliveryDATA::ExecuteSubStep()
{
  Standard_Boolean okexec = Standard_True;
  Handle(WOKernel_DevUnit) thesourceunit = Locator()->LocateDevUnit(SubCode());
  if (thesourceunit.IsNull()) {
    okexec = Standard_False;
    ErrorMsg() << "WOKDeliv_DeliveryDATA::Execute" 
      << "Cannot locate unit : " << SubCode() << endm;
  }
  else {
    Handle(WOKBuilder_Entity) bidon;
    Handle(WOKMake_InputFile) infileCOMPONENTS = GetInFileCOMPONENTS();
    // Process script file
    TCollection_AsciiString namehome(Unit()->Name()->ToCString());
    namehome.UpperCase();
    namehome += "HOME";
    Unit()->Params().Set("%DeliveryHomeName",namehome.ToCString());
    Unit()->Params().Set("%UnitName",thesourceunit->Name()->ToCString());
    Handle(TCollection_HAsciiString) nameedl = new TCollection_HAsciiString("WOKDeliv_FRONTALSCRIPT.edl");
    Unit()->Params().LoadFile(nameedl);
    Handle(TCollection_HAsciiString) reseval = Unit()->Params().Eval("WOKDeliv_FrontalScript");

    Handle(WOKernel_Parcel) theParcel = WOKDeliv_DeliveryStep::GetParcel(Unit(),myList->GetName());
    Handle(WOKernel_DevUnit) parcelunit = WOKDeliv_DeliveryStep::GetParcelUnit(thesourceunit,
							theParcel,
							Unit());
    Handle(WOKernel_File) filescript = new WOKernel_File(SubCode(),
							 parcelunit,
							 parcelunit->GetFileType("executable"));
    
    filescript->GetPath();

    Handle(EDL_API) anapi = new EDL_API();
    anapi->AddVariable("%MYVAR",reseval->ToCString());
    if (anapi->OpenFile("MYFILE",filescript->Path()->Name()->ToCString()) != EDL_NORMAL) {
      ErrorMsg() << "WOKDeliv_DeliveryDATA::Execute"
	<< "Cannot open file " << filescript->Path()->Name() << endm;
      okexec = Standard_False;
    }
    else {
      
      anapi->WriteFile("MYFILE","%MYVAR");
      anapi->CloseFile("MYFILE");
      Handle(WOKMake_OutputFile) outfile = 
	new WOKMake_OutputFile(filescript->LocatorName(),
			       filescript,
			       bidon,
			       filescript->Path());
      outfile->SetReference();
      outfile->SetExtern();
      outfile->SetLocateFlag(Standard_True);
      AddExecDepItem(infileCOMPONENTS,outfile,Standard_True);
    }
    

    // Home made CCLinterpretor (ADN 18/9/98)

    Handle(TCollection_HAsciiString) namenewscript = new TCollection_HAsciiString("CCL");
    Handle(TCollection_HAsciiString) namenewbinscript = new TCollection_HAsciiString("CCL");
    namenewscript->AssignCat(SubCode()->ToCString());
    namenewbinscript->AssignCat(SubCode()->ToCString());
    namenewbinscript->AssignCat("bin");

#ifdef WNT
    namenewscript->AssignCat(".cmd");
    namenewbinscript->AssignCat(".cmd");
#endif

    filescript = new WOKernel_File(namenewscript,
				   parcelunit,
				   parcelunit->GetFileType("executable"));
    
    filescript->GetPath();


    reseval = Unit()->Params().Eval("WOKDeliv_CCLScript");

    anapi->AddVariable("%MYVAR",reseval->ToCString());

    if (anapi->OpenFile("MYFILECCL",filescript->Path()->Name()->ToCString()) != EDL_NORMAL) {
      ErrorMsg() << "WOKDeliv_DeliveryDATA::Execute"
	<< "Cannot open file " << filescript->Path()->Name() << endm;
      okexec = Standard_False;
    }
    else {
      
      anapi->WriteFile("MYFILECCL","%MYVAR");
      anapi->CloseFile("MYFILECCL");
      Handle(WOKMake_OutputFile) outfile = 
	new WOKMake_OutputFile(filescript->LocatorName(),
			       filescript,
			       bidon,
			       filescript->Path());
      outfile->SetReference();
      outfile->SetExtern();
      outfile->SetLocateFlag(Standard_True);
      AddExecDepItem(infileCOMPONENTS,outfile,Standard_True);
    }


    filescript = new WOKernel_File(namenewbinscript,
				   parcelunit,
				   parcelunit->GetFileType("executable"));
    
    filescript->GetPath();

    Handle(TCollection_HAsciiString) bineval = Unit()->Params().Eval("WOKDeliv_BINScript");

    anapi->AddVariable("%MYVAR",bineval->ToCString());

    if (anapi->OpenFile("MYFILEBIN",filescript->Path()->Name()->ToCString()) != EDL_NORMAL) {
      ErrorMsg() << "WOKDeliv_DeliveryDATA::Execute"
	<< "Cannot open file " << filescript->Path()->Name() << endm;
      okexec = Standard_False;
    }
    else {
      
      anapi->WriteFile("MYFILEBIN","%MYVAR");
      anapi->CloseFile("MYFILEBIN");
      Handle(WOKMake_OutputFile) outfile = 
	new WOKMake_OutputFile(filescript->LocatorName(),
			       filescript,
			       bidon,
			       filescript->Path());
      outfile->SetReference();
      outfile->SetExtern();
      outfile->SetLocateFlag(Standard_True);
      AddExecDepItem(infileCOMPONENTS,outfile,Standard_True);
    }
    
    
    
    // get frontal unit production
    Handle(TCollection_HAsciiString) namestep = new TCollection_HAsciiString("frontal");
    Handle(WOKMake_Step) thestep = BuildProcess()->GetAndAddStep(thesourceunit,
								 namestep,
								 Handle(TCollection_HAsciiString) ());
    if (thestep.IsNull()) {
      okexec = Standard_False;
    }
    Handle(WOKMake_HSequenceOfOutputFile) thefiles = thestep->OutputFileList();
    if (thefiles.IsNull()) {
      ErrorMsg() << "WOKDeliv_DeliveryDATA::Execute"
	<< "Step " << namestep << " not done for unit " << thesourceunit->Name() << endm;
      okexec = Standard_False;
    }
    else {
      for (Standard_Integer i=1; i<= thefiles->Length(); i++) {
	Handle(WOKernel_File) theinfile = thefiles->Value(i)->File();
	if (!theinfile.IsNull()) {
	  if (!theinfile->Name()->IsSameString(thesourceunit->Name())
	      && !theinfile->Name()->IsSameString(namenewscript)
	      && !theinfile->Name()->IsSameString(namenewbinscript)) { // avoid scripts
	    Handle(WOKernel_FileType) outtype = parcelunit->FileTypeBase()->Type(theinfile->TypeName()->ToCString());
	    
	    Handle(WOKernel_File) theoutfile = new WOKernel_File(theinfile->Name(),
								 parcelunit,
								 outtype);
	    theoutfile->GetPath();
	    Handle(WOKMake_OutputFile) outfile = 
	      new WOKMake_OutputFile(theoutfile->LocatorName(),
				     theoutfile,
				     bidon,
				     theoutfile->Path());
	    outfile->SetReference();
	    outfile->SetExtern();
	    outfile->SetLocateFlag(Standard_True);
	    if (!WOKDeliv_DeliveryStep::CopyAFile(thesourceunit,theinfile,theoutfile)) okexec = Standard_False;
	    AddExecDepItem(infileCOMPONENTS,outfile,Standard_True);
	  }
	}
      }
    }
  }
  return okexec;
}

	

Standard_Boolean WOKDeliv_DeliveryDATA::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  Handle(WOKernel_File)     file   = infile->File();
  if (file.IsNull()) return Standard_False;

  if(!strcmp(file->Name()->ToCString(), "COMPONENTS")) {
    return Standard_True;
  }

  return Standard_False;

}


Handle(TCollection_HAsciiString) WOKDeliv_DeliveryDATA::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STADMFILE);
  return result;
}

