// Copyright: 	Matra-Datavision 1996
// File:	WOKDeliv_DeliverySOURCES.cxx
// Created:	Mon Dec 30 16:35:29 1996
// Author:	Arnaud BOUZY
//		<adn>

#include <WOKDeliv_DeliverySOURCES.ixx>
#include <WOKDeliv_ParseDelivery.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_AdmFileTypes.hxx>

#include <WOKernel_Locator.hxx>
#include <WOKernel_File.hxx>

#include <WOKTools_MapIteratorOfMapOfHAsciiString.hxx>
#include <WOKTools_Messages.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

WOKDeliv_DeliverySOURCES::WOKDeliv_DeliverySOURCES(const Handle(WOKMake_BuildProcess)& aprocess,
						   const Handle(WOKernel_DevUnit)& aunit,
						   const Handle(TCollection_HAsciiString)& acode,
						   const Standard_Boolean checked,
						   const Standard_Boolean hidden)
: WOKDeliv_DeliveryMetaStep(aprocess,aunit,acode,checked,hidden)
{
}

void WOKDeliv_DeliverySOURCES::Execute(const Handle(WOKMake_HSequenceOfInputFile)&)
{
  Standard_Boolean okexec = Standard_False;
  myList = ParseCOMPONENTS(T_BASE);
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

Standard_Boolean WOKDeliv_DeliverySOURCES::ExecuteMetaStep()
{
  Standard_Boolean okexec = Standard_True;
  // Add Delivery sources
  myList->ChangeMap().Add(Unit()->Name());

  WOKTools_MapIteratorOfMapOfHAsciiString itpck(myList->GetMap());
  while (itpck.More()) {
    Handle(WOKernel_DevUnit) thesourceunit = Locator()->LocateDevUnit(itpck.Key());
    if (thesourceunit.IsNull()) {
      okexec = Standard_False;
      ErrorMsg() << "WOKDeliv_DeliverySOURCE::Execute" << "Cannot locate unit : " << itpck.Key()->ToCString() << endm;
    }
    else {
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
    itpck.Next();
  }
  return okexec;
} 


Standard_Boolean WOKDeliv_DeliverySOURCES::ExecuteSubStep()
{
  Standard_Boolean okexec = Standard_True;
  Handle(WOKernel_DevUnit) thesourceunit = Locator()->LocateDevUnit(SubCode());
  if (thesourceunit.IsNull()) {
    okexec = Standard_False;
    ErrorMsg() << "WOKDeliv_DeliverySOURCE::Execute" << "Cannot locate unit : " << SubCode() << endm;
  }
  else {
    Handle(WOKMake_InputFile) infileCOMPONENTS = GetInFileCOMPONENTS();
    thesourceunit->ReadFileList(Locator());
    Handle(TColStd_HSequenceOfHAsciiString) filelist = thesourceunit->FileList();
    Handle(WOKernel_File) afile;
    Handle(TCollection_HAsciiString) astr;
    Handle(TCollection_HAsciiString) aname;
    Handle(TCollection_HAsciiString) atype;
    Handle(WOKMake_OutputFile) outfile;
    Handle(WOKBuilder_Entity) bidon;
    Handle(TCollection_HAsciiString) srctype = new TCollection_HAsciiString("source");

    for(Standard_Integer i=1; i<=filelist->Length();i++)
    {
      astr = filelist->Value(i);
      atype     = astr->Token(":", 2);
      aname     = astr->Token(":", 3);

      if (atype->IsSameString(srctype)) {
	afile = Locator()->Locate(thesourceunit->Name(),
				  atype,
				  aname);
	if (afile.IsNull()) {
	  ErrorMsg() << "WOKDeliv_DeliverySOURCE::Execute"
	    << "Enable to locate source file " << astr << endm;
	  okexec = Standard_False;
	}
	else {
	  afile->GetPath();
	  outfile = new WOKMake_OutputFile(afile->LocatorName(),
					   afile,
					   bidon,
					   afile->Path());
	  outfile->SetReference();
	  outfile->SetExtern();
	  outfile->SetLocateFlag(Standard_True);
	  AddExecDepItem(infileCOMPONENTS,outfile,Standard_True);
	}
      }
    }
  }
  return okexec;
}

	

Standard_Boolean WOKDeliv_DeliverySOURCES::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  Handle(WOKernel_File)     file   = infile->File();
  if (file.IsNull()) return Standard_False;

  if(!strcmp(file->Name()->ToCString(), "COMPONENTS")) {
    return Standard_True;
  }

  return Standard_False;

}


Handle(TCollection_HAsciiString) WOKDeliv_DeliverySOURCES::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)ADMFILE);
  return result;
}

