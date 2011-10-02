// Copyright: 	Matra-Datavision 1996
// File:	WOKDeliv_DelivBuildSource.cxx
// Created:	Tue Dec 31 13:17:24 1996
// Author:	Arnaud BOUZY
//		<adn>


#include <WOKDeliv_DelivBuildSource.ixx>
#include <WOKDeliv_DeliveryList.hxx>
#include <WOKMake_AdmFileTypes.hxx>
#include <WOKMake_OutputFile.hxx>

#include <WOKernel_File.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_Locator.hxx>

#include <WOKUtils_Triggers.hxx>
#include <WOKUtils_Path.hxx>

#include <WOKTools_Messages.hxx>

WOKDeliv_DelivBuildSource::WOKDeliv_DelivBuildSource(const Handle(WOKMake_BuildProcess)& aprocess,
						     const Handle(WOKernel_DevUnit)& aunit,
						     const Handle(TCollection_HAsciiString)& acode,
						     const Standard_Boolean checked,
						     const Standard_Boolean hidden)
: WOKDeliv_DeliveryCopy(aprocess,aunit,acode,checked,hidden)
{
}

void WOKDeliv_DelivBuildSource::Execute(const Handle(WOKMake_HSequenceOfInputFile)& infiles)
{
  Handle(TCollection_HAsciiString) unitname = SubCode();
  Handle(WOKernel_DevUnit) thesourceunit = Locator()->LocateDevUnit(unitname);
  if (thesourceunit.IsNull()) {
    ErrorMsg() << "WOKDeliv_DelivBuildSource"
      << "Enable to locate unit " << unitname << endm;
    SetFailed();
  }
  else {
    if (!myList.IsNull()) {
      Handle(WOKernel_Parcel) theParcel = GetParcel(Unit(),myList->GetName());
      if (!theParcel.IsNull()) {
	Handle(TCollection_HAsciiString) typunit = thesourceunit->Type();
	Handle(WOKernel_DevUnit) thedestunit = GetParcelUnit(Unit(),theParcel,thesourceunit);
	if (!thedestunit.IsNull()) {
	  Handle(TCollection_HAsciiString) filename = new TCollection_HAsciiString(unitname->ToCString());
	  filename->AssignCat(".");
	  filename->AssignCat(typunit);
	  Handle(WOKernel_File) basefile = new WOKernel_File(filename,
							     thedestunit,
							     thedestunit->GetFileType("source"));
	  basefile->GetPath();
	  Handle(TCollection_HAsciiString) cmdname = new TCollection_HAsciiString("WOKDeliv_DelivExecSource:Process");
	  WOKUtils_Trigger execute;
	  Handle(TCollection_HAsciiString) tclfilename = new TCollection_HAsciiString("WOKDeliv_DelivExecSource.tcl");
	  
	  execute(tclfilename, Unit()->Params(), WOKTools_TclInterp) << endt;
	  
	  Handle(TCollection_HAsciiString) result;
	  execute(cmdname) << thesourceunit->UserPathName()
	    << basefile->Path()->Name()
	      << endt >> result;
	  switch(execute.Status()) {
	  case WOKUtils_Unknown:
	  case WOKUtils_NotSetted:
	    {
	      ErrorMsg() << "WOKDeliv_DelivBuildSource::Execute" 
		<< "Unable to execute source on unit " << unitname << endm;
	      SetFailed();
	    }
	    break;
	  case WOKUtils_Succeeded:
	    {
	      WOK_TRACE {
		VerboseMsg()("WOK_DELIV") << "WOKDeliv_DelivBuildSource::Execute"
					<< "File " << result << " created." << endm;
	      }
	      Handle(WOKUtils_Path) pathres = new WOKUtils_Path(result);
	      Handle(WOKernel_File) fileres = new WOKernel_File(pathres->FileName(),
								thedestunit,
								thedestunit->GetFileType("source"));
	      fileres->GetPath();
	      Handle(WOKBuilder_Entity) bidon;
	      Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(fileres->LocatorName(),
									  fileres,
									  bidon,
									  fileres->Path());
	      outfile->SetProduction();
	      outfile->SetLocateFlag(Standard_True);
	      outfile->SetExtern();
	      for (Standard_Integer i=1; i<= infiles->Length(); i++ ) {
		AddExecDepItem(infiles->Value(i),outfile,Standard_True);
	      }
	      SetSucceeded();
	    }
	    break;
	  case WOKUtils_Failed:
	    ErrorMsg() << "WOKDeliv_DelivBuildSource::Execute" 
	      << "Failed to execute source on unit " << unitname << endm;
	    SetFailed();
	    break;
	  }
	}
      }
    }
  }
}

Handle(TCollection_HAsciiString) WOKDeliv_DelivBuildSource::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) adm = new TCollection_HAsciiString((char*)ADMFILE);
  return adm;
}
