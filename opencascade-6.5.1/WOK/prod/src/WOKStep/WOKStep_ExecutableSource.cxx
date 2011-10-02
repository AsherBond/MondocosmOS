// File:	WOKStep_ExecutableSource.cxx
// Created:	Tue Feb  6 15:52:11 1996
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKBuilder_CDLFile.hxx>
#include <WOKBuilder_MSchema.hxx>
#include <WOKBuilder_MSEntity.hxx>
#include <WOKBuilder_MSTool.hxx>

#include <WOKernel_Locator.hxx>
#include <WOKernel_File.hxx>

#include <WOKMake_InputFile.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_HSequenceOfInputFile.hxx>

#include <WOKStep_ExecutableSource.ixx>

#ifdef WNT
# include <WOKNT_WNT_BREAK.hxx>
#endif  // WNT

//=======================================================================
//function : WOKStep_ExecutableSource
//purpose  : 
//=======================================================================
WOKStep_ExecutableSource::WOKStep_ExecutableSource(const Handle(WOKMake_BuildProcess)& abp,
						   const Handle(WOKernel_DevUnit)& aunit, 
						   const Handle(TCollection_HAsciiString)& acode, 
						   const Standard_Boolean checked, 
						   const Standard_Boolean hidden)
: WOKStep_CDLUnitSource(abp,aunit, acode, checked, hidden)
{
}

//=======================================================================
//function : ReadUnitDescr
//purpose  : 
//=======================================================================
void WOKStep_ExecutableSource::ReadUnitDescr(const Handle(WOKMake_InputFile)& EXECDL)
{
  Standard_Integer i;
  Handle(WOKBuilder_MSchema) ameta = WOKBuilder_MSTool::GetMSchema();
  Handle(TColStd_HSequenceOfHAsciiString) aseq;
  Handle(WOKernel_File) gefile, thefile, NULLFILE;

  WOKStep_CDLUnitSource::ReadUnitDescr(EXECDL);

  switch(Status())
    {
    case WOKMake_Failed:
      return;
    default:
      break;
    }
  
  Handle(TCollection_HAsciiString) msentity = new TCollection_HAsciiString("msentity");
  
  aseq = ameta->ExecutableParts(Unit()->Name());

  for(i=1; i<=aseq->Length(); i++)
    {
#ifdef WNT
_TEST_BREAK();
#endif  // WNT
      Handle(TCollection_HAsciiString) msid   = WOKernel_File::FileLocatorName(Unit()->Name(), msentity, aseq->Value(i));
      
      Handle(WOKBuilder_Specification) cdlent = Handle(WOKBuilder_Specification)::DownCast(EXECDL->BuilderEntity());
      Handle(WOKBuilder_MSEntity)      exeent = new WOKBuilder_MSEntity(cdlent, aseq->Value(i));
      
      Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(msid,
								  NULLFILE,
								  cdlent, cdlent->Path());
      outfile->SetLocateFlag(Standard_True);
      outfile->SetProduction();
      outfile->SetPhysicFlag(Standard_False);
      AddExecDepItem(EXECDL, outfile, Standard_True);
    }
}

