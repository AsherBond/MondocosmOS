// File:	WOKStep_EngLDFile.cxx
// Created:	Fri Feb 28 21:37:37 1997
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>
#include <Standard_Stream.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <WOKTools_Messages.hxx>
#include <WOKTools_MapOfHAsciiString.hxx>
#include <WOKTools_IndexedMapOfHAsciiString.hxx>

#include <WOKUtils_Extension.hxx>
#include <WOKUtils_Path.hxx>
#include <WOKUtils_Shell.hxx>

#include <WOKernel_Session.hxx>
#include <WOKernel_UnitNesting.hxx>
#include <WOKernel_Workbench.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_Parcel.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_Locator.hxx>

#include <WOKBuilder_Miscellaneous.hxx>

#include <WOKMake_InputFile.hxx>
#include <WOKMake_HSequenceOfInputFile.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_AdmFileTypes.hxx>

#include <WOKStep_EngLDFile.ixx>


//=======================================================================
//function : WOKStep_EngLDFile
//purpose  : 
//=======================================================================
 WOKStep_EngLDFile::WOKStep_EngLDFile(const Handle(WOKMake_BuildProcess)& abp,
				      const Handle(WOKernel_DevUnit)& aunit, 
				      const Handle(TCollection_HAsciiString)& acode, 
				      const Standard_Boolean checked, 
				      const Standard_Boolean hidden)
: WOKMake_Step(abp, aunit,  acode, checked, hidden)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AdmFileType
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_EngLDFile::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STADMFILE);
  return result; 
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutputDirTypeName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_EngLDFile::OutputDirTypeName() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STTMPDIR);
  return result; 
}

//=======================================================================
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKStep_EngLDFile::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  Handle(WOKBuilder_Entity) result;
  Handle(WOKUtils_Path)     apath;

  if(!infile->File().IsNull()) {
    apath = infile->File()->Path();
    if (apath->Extension() == WOKUtils_DSOFile) {
      return Standard_True;
    }
  }
  return Standard_False;
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : Execute
//purpose  : 
//=======================================================================
void WOKStep_EngLDFile::Execute(const Handle(WOKMake_HSequenceOfInputFile)& execlist)
{

  Handle(TCollection_HAsciiString) ldname = new TCollection_HAsciiString(Unit()->Name());

  ldname->AssignCat(".ld");

  Handle(WOKernel_File) ldfile = new WOKernel_File(ldname, Unit(), Unit()->GetFileType("library"));

  ldfile->GetPath();

  Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(ldfile->LocatorName(), ldfile,
							      new WOKBuilder_Miscellaneous(ldfile->Path()),ldfile->Path());

  outfile->SetLocateFlag(Standard_True);
  outfile->SetMember();
  outfile->SetProduction();
  Standard_Integer i;
  for (i=1; i<=execlist->Length(); i++) {
    const Handle(WOKMake_InputFile)& infile = execlist->Value(i);
    
    if(!infile->File().IsNull()) {
      AddExecDepItem(infile, outfile, Standard_True);
    }
  }

  ofstream astream(ldfile->Path()->Name()->ToCString(), ios::out);

  if(!astream.good())
    {
      ErrorMsg() << "WOKStep_EngLDFile::Execute"
	       << "Could not open " << ldfile->Path()->Name()->ToCString() << " for writing" << endm;
      SetFailed();
      return;
    }

  Handle(TCollection_HAsciiString) apref = Unit()->Params().Eval("%ENV_EngineLoadPath");

  if(!apref.IsNull())
    {
      if ( !apref->IsEmpty()) {
#ifndef WNT
	astream << apref->ToCString() << ":";
#else
	astream << apref->ToCString() << ";";
#endif
      }
    }

  WOKTools_IndexedMapOfHAsciiString amap;

  // recuperation des repertoires dans l'ordre de visibilite

  Handle(WOKernel_Session) theSession = Unit()->Session();
  Handle(TCollection_HAsciiString) thewb = Unit()->Nesting();
  Handle(WOKernel_Workbench) theworkbench = theSession->GetWorkbench(thewb);
  theworkbench->Open();
  Handle(TColStd_HSequenceOfHAsciiString) thevisib = theworkbench->Visibility();
  for (i=1; i <= thevisib->Length(); i++) {
    Handle(WOKernel_UnitNesting) anest = theSession->GetUnitNesting(thevisib->Value(i));
    if (!anest.IsNull()) {
      anest->Open();
      Handle(TCollection_HAsciiString) adir = anest->Params().Eval("WOKEntity_libdir");
      if (!adir.IsNull()) {
	if(!adir->IsEmpty()) {
#ifndef WNT
	  astream << ":" << adir->ToCString();
#else
	  astream << ";" << adir->ToCString();
#endif
	}
      }
    }
  }
  astream << endl;

  apref = Unit()->Params().Eval("%ENV_EngineStarterVersion");

  if(!apref.IsNull())
    {
      astream << apref->ToCString() << endl;
    }


  astream.close();

  SetSucceeded();
  return;
}
