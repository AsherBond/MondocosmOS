// File:	WOKDeliv_DeliveryBase.cxx
// Created:	Tue Mar 26 16:12:47 1996
// Author:	Arnaud BOUZY
//		<adn>
#include <Standard_Stream.hxx>

#include <WOKDeliv_DeliveryBase.ixx>
#include <WOKMake_InputFile.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_AdmFileTypes.hxx>
#include <WOKDeliv_ParseDelivery.hxx>

#include <WOKernel_Parcel.hxx>
#include <WOKernel_Session.hxx>
#include <WOKernel_Workbench.hxx>
#include <WOKernel_Workshop.hxx>
#include <WOKernel_Factory.hxx>
#include <WOKernel_Warehouse.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_Locator.hxx>

#include <WOKUtils_ParamItem.hxx>
#include <WOKUtils_HSequenceOfParamItem.hxx>
#include <WOKUtils_Path.hxx>

#include <WOKTools_MapIteratorOfMapOfHAsciiString.hxx>
#include <WOKTools_Messages.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <TColStd_HSequenceOfAsciiString.hxx>
#include <Standard_ProgramError.hxx>


WOKDeliv_DeliveryBase::WOKDeliv_DeliveryBase(const Handle(WOKMake_BuildProcess)& aprocess,
					     const Handle(WOKernel_DevUnit)& aunit,
					     const Handle(TCollection_HAsciiString)& acode,
					     const Standard_Boolean checked,
					     const Standard_Boolean hidden)
  : WOKDeliv_DeliveryStep(aprocess,aunit,acode,checked,hidden)
{
}

void WOKDeliv_DeliveryBase::Execute(const Handle(WOKMake_HSequenceOfInputFile)&)
{
  
  Standard_Boolean okexec = Standard_True;
  if (!myList.IsNull()) {
    InfoMsg() << "WOKDeliv_DeliveryBase" << "Process UL : " << myList->GetName()->ToCString() << endm;
    Handle(TCollection_HAsciiString) thename = myList->GetName();
    Handle(WOKernel_Warehouse) theWarehouse;
    Handle(WOKernel_Session) theSession = Unit()->Session();

    Handle(WOKernel_Parcel) theParcel = GetParcel(Unit(),thename);
    if (!theParcel.IsNull()) {
      InfoMsg() << "WOKDeliv_DeliveryBase" << "Parcel already created : update" << endm;
      theParcel->Open();
      theWarehouse = theSession->GetWarehouse(theParcel->Nesting());
    }
    else {
      // creation of parcel tree
      Handle(TCollection_HAsciiString) thewb = Unit()->Nesting();
      Handle(WOKernel_Workbench) theworkbench = theSession->GetWorkbench(thewb);
      Handle(TCollection_HAsciiString) thews = theworkbench->Nesting();
      Handle(WOKernel_Workshop) theworkshop = theSession->GetWorkshop(thews);
      Handle(TCollection_HAsciiString) thef = theworkshop->Nesting();
      Handle(WOKernel_Factory) thefactory = theSession->GetFactory(thef);
      Handle(TCollection_HAsciiString) thewh = thefactory->Warehouse();
      theWarehouse = theSession->GetWarehouse(thewh);
      theParcel = new WOKernel_Parcel(thename,
				      theWarehouse);
      Handle(WOKUtils_HSequenceOfParamItem) params = new WOKUtils_HSequenceOfParamItem();
      


      // Set Home parameter
      Handle(TCollection_HAsciiString) defaulthome = theWarehouse->EvalParameter("Home");
      Handle(WOKUtils_Path) homepath = new WOKUtils_Path(defaulthome,thename);
      Handle(TCollection_HAsciiString) namehome = new TCollection_HAsciiString("%");
      namehome->AssignCat(thename);
      namehome->AssignCat("_Home");
      WOKUtils_ParamItem itemhome(namehome,homepath->Name());
      params->Append(itemhome);

      // Set Delivery parameter

      Handle(TCollection_HAsciiString) namedel =  new TCollection_HAsciiString("%");
      namedel->AssignCat(thename);
      namedel->AssignCat("_Delivery");
      WOKUtils_ParamItem itemdel(namedel,Unit()->Name());
      params->Append(itemdel);

      Handle(WOKUtils_HSequenceOfParamItem) buildpars = theParcel->BuildParameters(params,Standard_True);

      theParcel->Build(buildpars);
      theWarehouse->Close();
      theWarehouse->Open();
      theWarehouse->AddParcel(theParcel);
    }


    // base creation 
    // File DELIVERIES

    Handle(WOKernel_DevUnit) parcelunit = GetParcelUnit(Unit(),theParcel,Unit());

    Handle(WOKMake_InputFile) infileCOMPONENTS = GetInFileCOMPONENTS();
    if (infileCOMPONENTS.IsNull()) {
      ErrorMsg() << "WOKDeliv_DeliveryBase::Execute"
	<< "Missing File COMPONENTS, Check file FILES" << endm;
      SetFailed();
      return;
    }

    WOKUtils_Param parcelparam =  theParcel->Params();
    
    Handle(WOKBuilder_Entity) bidon;
    Handle(WOKMake_InputFile) infile;
    Handle(WOKMake_OutputFile) outfile;

    Handle(WOKernel_File) DELIVERIESFile = new WOKernel_File(new TCollection_HAsciiString("DELIVERIES"),parcelunit,parcelunit->GetFileType("DELIVERIES"));
    DELIVERIESFile->GetPath();
    Handle(TCollection_HAsciiString) delivfilename = DELIVERIESFile->Path()->Name();
    ofstream delstream(delivfilename->ToCString(), ios::out);
    if (!delstream) {
      ErrorMsg() << "WOKDeliv_DeliveryBase::Execute" << "Could not open " << delivfilename->ToCString() << endm;
      SetFailed();
      okexec = Standard_False;
    }
    else {
      delstream << Unit()->Name()->ToCString() << endl;
      delstream.close();
      outfile = new WOKMake_OutputFile(DELIVERIESFile->LocatorName(),
				       DELIVERIESFile,
				       bidon,
				       DELIVERIESFile->Path());
      outfile->SetProduction();
      outfile->SetExtern();
      outfile->SetLocateFlag(Standard_True);
      AddExecDepItem(infileCOMPONENTS,outfile,Standard_True);
    }
    
    // Copying files COMPONENTS, [Patchs], [ReleaseNotes]

    for (Standard_Integer i=1; i<= myinflow.Extent(); i++) {
      infile = myinflow(i);
      Handle(WOKernel_File) thefile = infile->File();
      if (!strcmp(thefile->Name()->ToCString(),"COMPONENTS")) {
	Handle(WOKernel_File) COMPONENTSFile = new WOKernel_File(new TCollection_HAsciiString("COMPONENTS"),parcelunit,parcelunit->GetFileType("COMPONENTSFile"));
	COMPONENTSFile->GetPath();
	outfile = new WOKMake_OutputFile(COMPONENTSFile->LocatorName(),
					 COMPONENTSFile,
					 bidon,
					 COMPONENTSFile->Path());
	outfile->SetProduction();
	outfile->SetLocateFlag(Standard_True);
	outfile->SetExtern();
	AddExecDepItem(infile,outfile,Standard_True);
	if (!CopyAFile(Unit(),thefile,COMPONENTSFile)) okexec = Standard_False;
      }
      else if (!strcmp(thefile->Name()->ToCString(),"Patchs")) {
	Handle(WOKernel_File) PatchsFile = new WOKernel_File(new TCollection_HAsciiString("Patchs"),parcelunit,parcelunit->GetFileType("patchs"));
	PatchsFile->GetPath();
	outfile = new WOKMake_OutputFile(PatchsFile->LocatorName(),
					 PatchsFile,
					 bidon,
					 PatchsFile->Path());
	outfile->SetProduction();
	outfile->SetLocateFlag(Standard_True);
	outfile->SetExtern();
	AddExecDepItem(infile,outfile,Standard_True);
	if (!CopyAFile(Unit(),thefile,PatchsFile)) okexec = Standard_False;
      }
      if (!strcmp(thefile->Name()->ToCString(),"ReleaseNotes")) {
	Handle(WOKernel_File) ReleaseFile = new WOKernel_File(new TCollection_HAsciiString("ReleaseNotes"),parcelunit,parcelunit->GetFileType("releasenote"));
	ReleaseFile->GetPath();
	outfile = new WOKMake_OutputFile(ReleaseFile->LocatorName(),
					 ReleaseFile,
					 bidon,
					 ReleaseFile->Path());
	outfile->SetProduction();
	outfile->SetLocateFlag(Standard_True);
	outfile->SetExtern();
	AddExecDepItem(infile,outfile,Standard_True);
	if (!CopyAFile(Unit(),thefile,ReleaseFile)) okexec = Standard_False;

      }
    }

    // creation of Define file and depul File

    
    Handle(WOKernel_File) DefineFile = new WOKernel_File(new TCollection_HAsciiString("DEFINE"),parcelunit,parcelunit->GetFileType("DefineFile"));
    DefineFile->GetPath();
    Handle(WOKUtils_Path) pathreq = DefineFile->Path();
    WOKTools_MapOfHAsciiString totreat,allreqmap;

    Handle(WOKernel_File) depulFile = new WOKernel_File(parcelunit,parcelunit->GetFileType("DepULFile"));
    depulFile->GetPath();
    Handle(TCollection_HAsciiString) namedepulfilename = depulFile->Path()->Name();
    ofstream depulstream(namedepulfilename->ToCString(), ios::out);
    if (!depulstream) {
      ErrorMsg() << "WOKDeliv_DeliveryBase::Execute" << "Could not open " << namedepulfilename->ToCString() << endm;
      SetFailed();
      okexec = Standard_False;
    }

    
    WOKTools_MapIteratorOfMapOfHAsciiString itreq(myList->GetRequireMap());
    TCollection_AsciiString thereqstr;
    while (itreq.More()) {
      thereqstr += itreq.Key()->ToCString();
      thereqstr += " ";
      Handle(TCollection_HAsciiString) fullname = GetFullParcelName(itreq.Key());
      if (!fullname.IsNull()) {
	totreat.Add(fullname);
	if (depulstream) {
	  Handle(TCollection_HAsciiString) versparcel = GetVersionFromParcel(itreq.Key(),fullname);
	  depulstream << versparcel->ToCString() << endl;
	}
      }
      else {
	okexec = Standard_False;
      }
      itreq.Next();
    }
    if (depulstream) {
      depulstream.close();
      outfile = new WOKMake_OutputFile(depulFile->LocatorName(),
				       depulFile,
				       bidon,
				       depulFile->Path());
      outfile->SetProduction();
      outfile->SetLocateFlag(Standard_True);
      outfile->SetExtern();
      AddExecDepItem(infileCOMPONENTS,outfile,Standard_True);
    }

    WOKUtils_Param thepars = Unit()->Params();
    Handle(TCollection_HAsciiString) thenamevar = new TCollection_HAsciiString("%");
    thenamevar->AssignCat(Unit()->Name());
    thenamevar->AssignCat("_Requires");
    thepars.Set(thenamevar->ToCString(),thereqstr.ToCString());

    Handle(TColStd_HSequenceOfHAsciiString) thevars = new TColStd_HSequenceOfHAsciiString();
    thevars->Append(thenamevar);

    WOKUtils_Param par;
    par.SetSearchDirectories(theParcel->Params().SearchDirectories());
    while (!totreat.IsEmpty()) {
      okexec = okexec && GetRequisites(totreat,allreqmap,par);
    }
    TCollection_AsciiString theallreqstr;
    WOKTools_MapIteratorOfMapOfHAsciiString itallreq(allreqmap);
    while (itallreq.More()) {
      theallreqstr += itallreq.Key()->ToCString();
      theallreqstr += " ";
      itallreq.Next();
    }
    thenamevar = new TCollection_HAsciiString("%");
    thenamevar->AssignCat(Unit()->Name());
    thenamevar->AssignCat("_AllRequisites");
    thepars.Set(thenamevar->ToCString(),theallreqstr.ToCString());

    thevars->Append(thenamevar);

    thenamevar = new TCollection_HAsciiString("%");
    thenamevar->AssignCat(Unit()->Name());
    thenamevar->AssignCat("_Parcel");
    thepars.Set(thenamevar->ToCString(),theParcel->Name()->ToCString());

    thevars->Append(thenamevar);

    // Treat Put Path/Lib/Include
    thenamevar = new TCollection_HAsciiString("%");
    thenamevar->AssignCat(Unit()->Name());
    thenamevar->AssignCat("_PutPath");
    if (myList->IsPutPath()) {
      thepars.Set(thenamevar->ToCString(),"yes");
    }
    else {
      thepars.Set(thenamevar->ToCString(),"no");
    }
    thevars->Append(thenamevar);


    thenamevar = new TCollection_HAsciiString("%");
    thenamevar->AssignCat(Unit()->Name());
    thenamevar->AssignCat("_PutLib");
    if (myList->IsPutLib()) {
      thepars.Set(thenamevar->ToCString(),"yes");
    }
    else {
      thepars.Set(thenamevar->ToCString(),"no");
    }
    thevars->Append(thenamevar);

    thenamevar = new TCollection_HAsciiString("%");
    thenamevar->AssignCat(Unit()->Name());
    thenamevar->AssignCat("_PutInclude");
    if (myList->IsPutInclude()) {
      thepars.Set(thenamevar->ToCString(),"yes");
    }
    else {
      thepars.Set(thenamevar->ToCString(),"no");
    }
    thevars->Append(thenamevar);


    thepars.Write(pathreq,thevars);

    outfile = new WOKMake_OutputFile(DefineFile->LocatorName(),
				     DefineFile,
				     bidon,
				     DefineFile->Path());
    outfile->SetProduction();
    outfile->SetLocateFlag(Standard_True);
    outfile->SetExtern();
    AddExecDepItem(infileCOMPONENTS,outfile,Standard_True);
    
    

    // creation of file .NAME

    Handle(WOKernel_File) NAMEFile = new WOKernel_File(new TCollection_HAsciiString("NAME"),parcelunit,parcelunit->GetFileType("NameFile"));
    NAMEFile->GetPath();
    Handle(TCollection_HAsciiString) namefilename = NAMEFile->Path()->Name();
    ofstream namestream(namefilename->ToCString(), ios::out);
    if (!namestream) {
      ErrorMsg() << "WOKDeliv_DeliveryBase::Execute" << "Could not open " << namefilename->ToCString() << endm;
      SetFailed();
      okexec = Standard_False;
    }
    else {
      namestream << myList->GetName()->ToCString() << endl;
      namestream.close();
      outfile = new WOKMake_OutputFile(NAMEFile->LocatorName(),
				       NAMEFile,
				       bidon,
				       NAMEFile->Path());
      outfile->SetProduction();
      outfile->SetLocateFlag(Standard_True);
      outfile->SetExtern();
      AddExecDepItem(infileCOMPONENTS,outfile,Standard_True);
    }

    // creation of file .allcomponents

    Handle(TCollection_HAsciiString) allcmpname = new TCollection_HAsciiString(Unit()->Name());
    allcmpname->AssignCat(parcelparam.Eval("%FILENAME_AllComponentsSuffix"));
    Handle(WOKernel_File) allcomps = new WOKernel_File(allcmpname, theParcel, theParcel->GetFileType("UnitListFile"));
    allcomps->GetPath();
    ofstream cmpstream(allcomps->Path()->Name()->ToCString(), ios::out);
    WOKTools_MapIteratorOfMapOfHAsciiString itpck(myList->GetMap());
    while (itpck.More()) {
      Handle(WOKernel_DevUnit) thesourceunit = Locator()->LocateDevUnit(itpck.Key());
      if (thesourceunit.IsNull()) {
	ErrorMsg() << "WOKDeliv_DeliveryBase::Execute" << "cannot locate unit : " << itpck.Key()->ToCString() << endm;
	SetFailed();
	okexec = Standard_False;
      }
      else {
	cmpstream << thesourceunit->TypeCode() << " " << itpck.Key()->ToCString() << "\n";
      }
      itpck.Next();
    }
    // add delivery unit itself
    cmpstream << Unit()->TypeCode() << " " << Unit()->Name()->ToCString() << "\n";

    cmpstream.close();
    
    outfile = new WOKMake_OutputFile(allcomps->LocatorName(),
				     allcomps,
				     bidon,
				     allcomps->Path());
    outfile->SetProduction();
    outfile->SetLocateFlag(Standard_True);
    outfile->SetExtern();
    AddExecDepItem(infileCOMPONENTS,outfile,Standard_True);


    theParcel->Close();
    theParcel->Open();

    if (okexec) {
      SetSucceeded();
    }
    else {
      SetFailed();
    }
    
  }
  else {
    SetFailed();
  }
}


Standard_Boolean WOKDeliv_DeliveryBase::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  Handle(WOKernel_File)     file   = infile->File();
  if (file.IsNull()) return Standard_False;

  if(!strcmp(file->TypeName()->ToCString(), "source")) {
    return Standard_True;
  }

  return Standard_False;

}

Handle(TCollection_HAsciiString) WOKDeliv_DeliveryBase::GetVersionFromParcel(const Handle(TCollection_HAsciiString)& delivname, const Handle(TCollection_HAsciiString)& parcelname) const
{
  Handle(WOKernel_Parcel) theparcel = GetParcel(Unit(),parcelname);
  if (!theparcel.IsNull()) {
    theparcel->Open();
    Handle(WOKernel_DevUnit) thedeliv;
    Handle(WOKernel_Session) theSession = Unit()->Session();
    Handle(TColStd_HSequenceOfHAsciiString) thenames = theparcel->Units();
    for (Standard_Integer i=1; (i<= thenames->Length()) && thedeliv.IsNull(); i++) {
      thedeliv = theSession->GetDevUnit(thenames->Value(i));
      if (!thedeliv->Name()->IsSameString(delivname)) {
	thedeliv.Nullify();
      }
    }
    
    if (!thedeliv.IsNull()) {
      Handle(WOKernel_File) VersionFile = new WOKernel_File(new TCollection_HAsciiString("bidon"),thedeliv,thedeliv->GetFileType("VersionFile"));
      VersionFile->GetPath();
      Handle(TCollection_HAsciiString) nameversionfilename = VersionFile->Path()->Name();
      ifstream versionstream(nameversionfilename->ToCString(), ios::in);
      if (versionstream) {
	char fullname[200];
	versionstream >> fullname;
	Handle(TCollection_HAsciiString) res = new TCollection_HAsciiString(fullname);
	versionstream.close();
	return res;
      }
    }
  }
  return parcelname;
}

    
Standard_Boolean WOKDeliv_DeliveryBase::GetRequisites(WOKTools_MapOfHAsciiString& totreat,
						      WOKTools_MapOfHAsciiString& treated,
						      WOKUtils_Param& thepar) const
{
  Standard_Boolean okexec = Standard_True;
  WOKTools_MapIteratorOfMapOfHAsciiString it(totreat);
  if (it.More()) {
    Handle(TCollection_HAsciiString) current = it.Key();
    Handle(WOKernel_Parcel) theparcel = GetParcel(Unit(),current);
    totreat.Remove(current);
    if (!theparcel.IsNull()) {
      theparcel->Open();
      treated.Add(current);
      TCollection_AsciiString namevar = "%";
      namevar += theparcel->EvalParameter("Delivery")->ToCString();
      namevar += "_AllRequisites";
      Handle(TColStd_HSequenceOfAsciiString) searchdir = new TColStd_HSequenceOfAsciiString();
      for (Standard_Integer i=1; i<=thepar.SearchDirectories()->Length(); i++) {
	searchdir->Append(thepar.SearchDirectories()->Value(i));
      }
      searchdir->Append(theparcel->Params().SearchDirectories());
      thepar.SetSearchDirectories(searchdir);
      Handle(TCollection_HAsciiString) allreq = thepar.Eval(namevar.ToCString());
      if (!allreq.IsNull()) {
	Handle(TCollection_HAsciiString) current;
	Standard_Boolean finished = allreq->IsEmpty();
	Standard_Integer ntok = 1;
	while (!finished) {
	  current = allreq->Token(" ",ntok);
	  ntok++;
	  if (current->IsEmpty()) {
	    finished = Standard_True;
	  }
	  else {
	    treated.Add(current);
	  }
	}
      }
    }
    else {
      okexec = Standard_False;
    }
  }
  return okexec;
}

Handle(TCollection_HAsciiString) WOKDeliv_DeliveryBase::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)ADMFILE);
  return result;
}

WOKMake_Status WOKDeliv_DeliveryBase::Make()
{
  if (IsToExecute()) {
    if (myOutLocator.IsNull()) {
      if (myList.IsNull()) {
	myList = ParseCOMPONENTS(T_BASE);
	DefineOutLocator();
      }
    }
  }
  return WOKMake_Step::Make();
}

