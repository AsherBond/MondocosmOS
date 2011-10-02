// File:	WOKStep_TKList.cxx
// Created:	Wed Jun 26 19:57:33 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKUtils_AdmFile.hxx>
#include <WOKUtils_Path.hxx>
#include <WOKUtils_Shell.hxx>

#include <WOKernel_Session.hxx>
#include <WOKernel_UnitNesting.hxx>
#include <WOKernel_Workbench.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_BasicUnitTypes.hxx>
#include <WOKernel_Parcel.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_Locator.hxx>


#include <WOKBuilder_ArchiveLibrary.hxx>
#include <WOKBuilder_CompressedFile.hxx>
#include <WOKBuilder_Command.hxx>

#include <WOKMake_AdmFileTypes.hxx>
#include <WOKMake_InputFile.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_HSequenceOfOutputFile.hxx>
#include <WOKMake_HSequenceOfStepOption.hxx>

#include <WOKStep_Compile.hxx>

#include <WOKStep_TKList.ixx>

//=======================================================================
//function : WOKStep_TKList
//purpose  : 
//=======================================================================
WOKStep_TKList::WOKStep_TKList(const Handle(WOKMake_BuildProcess)& abp,
			       const Handle(WOKernel_DevUnit)& aunit, 
			       const Handle(TCollection_HAsciiString)& acode, 
			       const Standard_Boolean checked, 
			       const Standard_Boolean hidden) 
  : WOKStep_LinkList(abp, aunit, acode, checked, hidden)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AdmFileType
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_TKList::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STADMFILE);
  return result;   
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutputDirTypeName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_TKList::OutputDirTypeName() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STTMPDIR);
  return result;   
}

//=======================================================================
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKStep_TKList::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  Handle(TCollection_HAsciiString) astr;
  if(infile->IsLocateAble() && infile->IsPhysic())
    {
      if(!infile->File().IsNull())
	{
	  astr = Unit()->Params().Eval("%FILENAME_PACKAGES");

	  if(astr.IsNull())
	    {
	      ErrorMsg() << "WOKStep_TKList::HandleInputFile"
		       << "Could not eval parameter %FILENAME_PACKAGES" << endm;
	      SetFailed();
	      return Standard_False;
	    }
	  else if (!strcmp(infile->File()->Name()->ToCString(), astr->ToCString()))
	    { 
	      infile->SetDirectFlag(Standard_True);
	      return Standard_True;
	    }
	}
    }
  return Standard_False;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutOfDateEntities
//purpose  : 
//=======================================================================
Handle(WOKMake_HSequenceOfInputFile) WOKStep_TKList::OutOfDateEntities() 
{
  return ForceBuild();
}
 

//=======================================================================
//function : GetUnitContributionCodes
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_TKList::GetUnitContributionCodes(const Handle(WOKernel_DevUnit)& aunit) const
{
  Handle(TCollection_HAsciiString) atype = Unit()->Type();
  Handle(TCollection_HAsciiString) paramname = new TCollection_HAsciiString("%WOKSteps_");
  
  paramname->AssignCat(Unit()->Type());
  paramname->AssignCat("_ListWith");

  Handle(TCollection_HAsciiString) codes = aunit->Params().Eval(paramname->ToCString());

  if(codes.IsNull())
    {
      WarningMsg() << "WOKStep_WNTK::GetUnitContributionCodes" 
		 << "Could not eval parameter " << paramname << " in unit " << aunit->UserPathName() << endm;
    }
  return codes;
}

//=======================================================================
//function : ComputeDependency
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKStep_TKList::ComputeDependency(
//       const Handle(TCollection_HAsciiString)& acode,
       const Handle(TCollection_HAsciiString)& ,
//       const Handle(TColStd_HSequenceOfHAsciiString)& directlist) const
       const Handle(TColStd_HSequenceOfHAsciiString)& ) const
{
  Handle(TColStd_HSequenceOfHAsciiString) SHOULDNOTBECALLED;
  return SHOULDNOTBECALLED;
}


//=======================================================================
//function : AddParcelUnitContribution
//purpose  : 
//=======================================================================
void WOKStep_TKList::AddParcelUnitContribution(const Handle(WOKMake_InputFile)& theinfile,
					       const Handle(TCollection_HAsciiString)& unit)
{
#ifndef WNT
  Handle(WOKernel_DevUnit) aunit = Locator()->LocateDevUnit(unit);  
  Handle(TCollection_HAsciiString) libtype = new TCollection_HAsciiString("library");
  
  Handle(TCollection_HAsciiString) libname = WOKBuilder_ArchiveLibrary::GetLibFileName(Unit()->Params(), aunit->Name());
  libname->AssignCat(".Z");
  Handle(WOKernel_File) lib = Locator()->Locate(aunit->Name(), libtype, libname);
  
  if(!lib.IsNull())
    {
      Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(lib->LocatorName(), lib, 
								  new WOKBuilder_CompressedFile(lib->Path()), 
								  lib->Path());
      outfile->SetReference();
      outfile->SetExtern();
      outfile->SetLocateFlag(Standard_True);
      AddExecDepItem(theinfile, outfile, Standard_True);
    }
  return;
#else

  ErrorMsg() <<  "WOKStep_TKList::AddParcelUnitContribution " 
    << "Cannot add unit " << unit << " from parcel on Windows NT: feature not available" << endm;
  SetFailed();
  return;

#endif
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : Execute
//purpose  : 
//=======================================================================
void WOKStep_TKList::Execute(const Handle(WOKMake_HSequenceOfInputFile)& execlist)
{
  if(execlist->Length() > 1)
    {
      ErrorMsg() << "WOKStep_TKList::Execute" << "Too many input files in step" << endm;
      SetFailed();
      return;
    }

  Handle(WOKMake_InputFile) PACKAGES = execlist->Value(1);
  WOKUtils_AdmFile afile(PACKAGES->File()->Path());
  Handle(TColStd_HSequenceOfHAsciiString) unitseq = afile.Read();
  Handle(TCollection_HAsciiString) astr;
  Standard_Integer i;

  Handle(WOKBuilder_Command) acommand = new WOKBuilder_Command(new TCollection_HAsciiString("COMMAND"),
							       Unit()->Params());

  acommand->SetShell(Shell());
  if(!Shell()->IsLaunched()) Shell()->Launch();

  for(i=1; i<=unitseq->Length(); i++)
    {
      astr = unitseq->Value(i);
      astr->LeftAdjust();
      astr->RightAdjust();

      Handle(WOKernel_DevUnit) unit = Locator()->LocateDevUnit(astr);

      if(unit.IsNull())
	{
	  ErrorMsg() << "WOKStep_TKList::Execute" 
		   << "Could not locate unit " << astr << " listed in file PACKAGES" << endm;
	  SetFailed();
	  return;
	}

      AddUnitContribution(PACKAGES, unit->Name());

    }
  SetSucceeded();
}
