// Copyright: 	Matra-Datavision 1996
// File:	WOKDeliv_DeliveryCopy.cxx
// Created:	Tue Aug  6 11:22:00 1996
// Author:	Arnaud BOUZY
//		<adn>

#include <WOKDeliv_DeliveryCopy.ixx>
#include <WOKDeliv_ParseDelivery.hxx>
#include <WOKMake_AdmFileTypes.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_HSequenceOfOutputFile.hxx>

#include <WOKernel_Session.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_FileType.hxx>
#include <WOKernel_FileTypeBase.hxx>
#include <WOKernel_Locator.hxx>

#include <WOKUtils_Path.hxx>

#include <WOKTools_Messages.hxx>

WOKDeliv_DeliveryCopy::WOKDeliv_DeliveryCopy(const Handle(WOKMake_BuildProcess)& aprocess,
					     const Handle(WOKernel_DevUnit)& aunit,
					     const Handle(TCollection_HAsciiString)& acode,
					     const Standard_Boolean checked,
					     const Standard_Boolean hidden)
: WOKDeliv_DeliveryStep(aprocess,aunit,acode,checked,hidden)
{
}

void WOKDeliv_DeliveryCopy::Execute(const Handle(WOKMake_HSequenceOfInputFile)& infiles) {
  if (myList.IsNull()) {
    myList = ParseCOMPONENTS(T_BASE);
  }
  Standard_Boolean okexec = Standard_False;
  Handle(WOKBuilder_Entity) bidon;
  Handle(WOKMake_OutputFile) outfile;
  Handle(WOKernel_File) fileout;

  if (!myList.IsNull()) {
    Handle(WOKernel_Parcel) theParcel = GetParcel(Unit(),myList->GetName());
    if (!theParcel.IsNull()) {
      okexec = Standard_True;
      Handle(WOKernel_Session) theSession = Unit()->Session();
      for (Standard_Integer i=1; i<= infiles->Length(); i++) {
	Handle(WOKernel_File) theinfile = infiles->Value(i)->File();
	Handle(TCollection_HAsciiString) nest = theinfile->Nesting();
	Handle(WOKernel_DevUnit) thesourceunit = theSession->GetDevUnit(nest);
	if (!thesourceunit.IsNull()) {
	  Handle(WOKernel_DevUnit) thedestunit = GetParcelUnit(Unit(),theParcel,thesourceunit);
	  if (!thedestunit.IsNull()) {
	    Handle(WOKernel_FileType) outtype = thedestunit->FileTypeBase()->Type(theinfile->TypeName()->ToCString());
	    if (theinfile->Type()->IsFileDependent()) {
	      fileout = new WOKernel_File(theinfile->Name(),
					  thedestunit,
					  outtype);
	    }
	    else {
	      fileout = new WOKernel_File(thedestunit,
					  outtype);
	    }
	    fileout->GetPath();
	    outfile = 
	      new WOKMake_OutputFile(fileout->LocatorName(),
				     fileout,
				     bidon,
				     fileout->Path());
	    outfile->SetProduction();
	    outfile->SetExtern();
	    outfile->SetLocateFlag(Standard_True);
	    AddExecDepItem(infiles->Value(i),outfile,Standard_True);
	    okexec = okexec && CopyAFile(Unit(),theinfile,fileout);
	  }
	}
      }
    }
    else {
      ErrorMsg() <<  "WOKDeliv_DeliveryCopy::Execute" << "Cannot find delivery : " << myList->GetName()->ToCString() << endm;
    }
  }
  if (okexec) {
    SetSucceeded();
  }
  else {
    SetFailed();
  }
}


//=======================================================================
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKDeliv_DeliveryCopy::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  const Handle(WOKernel_File)& file = infile->File();
  if(file.IsNull()) return Standard_False;

  Standard_CString atypename = file->TypeName()->ToCString();
  Standard_Boolean isadm = Standard_False;

  if(!strcmp(atypename, "stadmfile")) isadm = Standard_True;
  if(!isadm) if(!strcmp(atypename, "dbadmfile")) isadm = Standard_True;
  if(!isadm) if(!strcmp(atypename, "admfile"))   isadm = Standard_True;

  if(isadm)
    {
      Handle(TCollection_HAsciiString) extens = file->Path()->ExtensionName();
      Standard_CString anext = extens->ToCString();
      if(!strcmp(anext, ".In")) return Standard_False;
      if(!strcmp(anext, ".Out")) return Standard_False;
      if(!strcmp(anext, ".Dep")) return Standard_False;
    }

  return Standard_True;
}


Handle(TCollection_HAsciiString) WOKDeliv_DeliveryCopy::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) adm = new TCollection_HAsciiString((char*)ADMFILE);
  static Handle(TCollection_HAsciiString) dbadm = new TCollection_HAsciiString((char*)DBADMFILE);
  static Handle(TCollection_HAsciiString) stadm = new TCollection_HAsciiString((char*)STADMFILE);
  
  if (!strcmp(Code()->ToCString(),"cdl.copy")) return adm;
  if (!strcmp(Code()->ToCString(),"inc.copy")) return dbadm;
  return stadm;
}

WOKMake_Status WOKDeliv_DeliveryCopy::Make()
{
  if (IsToExecute()) {
    if (myOutLocator.IsNull()) {
      if (myList.IsNull()) {
	myList = ParseCOMPONENTS(T_BASE);
      }
      DefineOutLocator();
    }
  }
  return WOKMake_Step::Make();
}
