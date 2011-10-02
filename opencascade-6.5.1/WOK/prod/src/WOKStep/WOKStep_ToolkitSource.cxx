// File:	WOKStep_ToolkitSource.cxx
// Created:	Thu May 29 11:33:37 1997
// Author:	Jean GAUTIER
//		<jga@hourax.paris1.matra-dtv.fr>



#include <WOKStep_ToolkitSource.ixx>

#include <WOKernel_FileType.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_Locator.hxx>

#include <WOKUtils_AdmFile.hxx>
#include <WOKUtils_Param.hxx>
#include <WOKTools_Messages.hxx>

#include <WOKMake_AdmFileTypes.hxx>
#include <WOKMake_OutputFile.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <TCollection_HAsciiString.hxx>

#ifdef WNT
# include <WOKNT_WNT_BREAK.hxx>
#endif  // WNT

//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKStep_ToolkitSource
//purpose  : 
//=======================================================================
WOKStep_ToolkitSource::WOKStep_ToolkitSource(const Handle(WOKMake_BuildProcess)& abp,
					     const Handle(WOKernel_DevUnit)& aunit, 
					     const Handle(TCollection_HAsciiString)& acode, 
					     const Standard_Boolean checked, 
					     const Standard_Boolean hidden) 
: WOKStep_Source(abp, aunit,  acode, checked, hidden)
{
}

//=======================================================================
//function : GetFILES
//purpose  : 
//=======================================================================
Handle(WOKernel_File) WOKStep_ToolkitSource::GetPACKAGES() const
{
  Handle(TCollection_HAsciiString) astr = Unit()->Params().Eval("%FILENAME_PACKAGES");
  Handle(TCollection_HAsciiString) asourcetype = new TCollection_HAsciiString("source");
  Handle(WOKernel_File) afile = Locator()->Locate(Unit()->Name(), asourcetype, astr);
  return afile;
}


//=======================================================================
//function : AddPACKAGES
//purpose  : 
//=======================================================================
void WOKStep_ToolkitSource::AddPACKAGES(const Handle(WOKMake_InputFile)& PACKAGES)
{
  Handle(WOKernel_File) afile;
  Handle(TCollection_HAsciiString) sourcetype = new TCollection_HAsciiString("source");

  // le fichier PACKAGES
  WOKUtils_Param params = Unit()->Params();

  if(PACKAGES.IsNull() == Standard_False)
    {
      // le fichier PACKAGES  produit PACKAGES
      Handle(WOKMake_OutputFile) OUTFILES = new WOKMake_OutputFile(PACKAGES->File()->LocatorName(), PACKAGES->File(), 
								   Handle(WOKBuilder_Entity)(), PACKAGES->File()->Path());
      
      OUTFILES->SetProduction();
      OUTFILES->SetLocateFlag(Standard_True);
      AddExecDepItem(PACKAGES, OUTFILES, Standard_True);
    }
}

//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
void WOKStep_ToolkitSource::Execute(const Handle(WOKMake_HSequenceOfInputFile)& execlist)
{
  Handle(WOKernel_File) PACKAGES = GetPACKAGES();
 
  if(!PACKAGES.IsNull())
    {
      Handle(WOKMake_InputFile) infile = new WOKMake_InputFile(PACKAGES->LocatorName(), PACKAGES, 
							       Handle(WOKBuilder_Entity)(), PACKAGES->Path());
      infile->SetLocateFlag(Standard_True);
      infile->SetDirectFlag(Standard_True);
      execlist->Append(infile);
      AddPACKAGES(infile);
    }

  Handle(WOKernel_File) FILES = GetFILES();

  if(!FILES.IsNull())
    {
      Handle(WOKMake_InputFile) infile = new WOKMake_InputFile(FILES->LocatorName(), FILES, 
							       Handle(WOKBuilder_Entity)(), FILES->Path());
      infile->SetLocateFlag(Standard_True);
      infile->SetDirectFlag(Standard_True);
      execlist->Append(infile);
      ReadFILES(infile);
    }

  if (Status() != WOKMake_Failed)
    SetSucceeded();

  return;
}
