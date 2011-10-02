// File:	WOKStep_SourceExtract.cxx
// Created:	Thu Nov 16 11:23:48 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKUtils_Path.hxx>

#include <WOKBuilder_MSEntity.hxx>
#include <WOKBuilder_MSTool.hxx>
#include <WOKBuilder_MSchema.hxx>

#include <WOKernel_FileTypeBase.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_HSequenceOfFile.hxx>
#include <WOKernel_Locator.hxx>


#include <WOKMake_OutputFile.hxx>
#include <WOKMake_InputFile.hxx>
#include <WOKMake_AdmFileTypes.hxx>

#include <WOKStep_SourceExtract.ixx>

#ifdef WNT
# include <WOKNT_WNT_BREAK.hxx>
#endif  // WNT

//=======================================================================
//function : WOKStep_SourceExtract
//purpose  : 
//=======================================================================
WOKStep_SourceExtract::WOKStep_SourceExtract(const Handle(WOKMake_BuildProcess)& abp,
					     const Handle(WOKernel_DevUnit)& aunit, 
					     const Handle(TCollection_HAsciiString)& acode, 
					     const Standard_Boolean checked, 
					     const Standard_Boolean hidden)
: WOKStep_Extract(abp,aunit,acode, checked, hidden)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AdmFileType
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_SourceExtract::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)ADMFILE);
  return result;   
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutputDirTypeName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_SourceExtract::OutputDirTypeName() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)TMPDIR);
  return result;   
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutOfDateEntities
//purpose  : 
//=======================================================================
Handle(WOKMake_HSequenceOfInputFile) WOKStep_SourceExtract::OutOfDateEntities() 
{
  return ForceBuild();
}

//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
void WOKStep_SourceExtract::Execute(const Handle(WOKMake_HSequenceOfInputFile)& tobuild)
{
  Standard_Integer i,j;
  Handle(TColStd_HSequenceOfHAsciiString) aseq;
  Handle(TCollection_HAsciiString) sourcetype = new TCollection_HAsciiString("source");
  Handle(WOKBuilder_MSEntity) msent;
  Handle(WOKernel_File) afile;

  Handle(WOKMake_InputFile)            buildfile;
  Handle(WOKMake_OutputFile)           outfile;

  for(i=1; i<=tobuild->Length(); i++)
    {
#ifdef WNT
_TEST_BREAK();
#endif  // WNT
      buildfile = tobuild->Value(i);

      msent = Handle(WOKBuilder_MSEntity)::DownCast(buildfile->BuilderEntity());
      
      if(msent.IsNull())
	{
	  SetFailed();
	  ErrorMsg() << "WOKStep_SourceExtract::Execute" 
		   << buildfile->BuilderEntity()->Path()->Name() << " is not a MS Type" << endm;
	}
      else
	{
	  aseq = WOKBuilder_MSTool::GetMSchema()->TypeSourceFiles(msent->Name());
	  
	  for(j=1; j<= aseq->Length(); j++)
	    {
#ifdef WNT
_TEST_BREAK();
#endif  // WNT
	      afile = Locator()->Locate(Unit()->Name(), sourcetype, aseq->Value(j));
	      
	      if(afile.IsNull())
		{
		  WarningMsg() << "WOKStep_SourceExtract::Execute" 
			     << "Missing CDL deducted source file : " << aseq->Value(j) << endm;
		  afile = new WOKernel_File(aseq->Value(j), Unit(), Unit()->GetFileType("source"));
		  afile->GetPath();
		}

	      outfile = new WOKMake_OutputFile(afile->LocatorName(), afile, Handle(WOKBuilder_Entity)(), afile->Path());
	      outfile->SetLocateFlag(Standard_True);
	      outfile->SetProduction();
	      AddExecDepItem(buildfile, outfile, Standard_True);
	    }
	}
    }

  if(Status() == WOKMake_Unprocessed) SetSucceeded();
  return;
}
