// File:	WOKStep_HeaderExtract.cxx
// Created:	Tue Aug 29 21:41:04 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <WOKTools_Messages.hxx>

#include <WOKBuilder_MSHeaderExtractor.hxx>
#include <WOKBuilder_MSActionID.hxx>
#include <WOKBuilder_MSAction.hxx>
#include <WOKBuilder_MSchema.hxx>
#include <WOKBuilder_MSEntity.hxx>

#include <WOKMake_InputFile.hxx>
#include <WOKMake_OutputFile.hxx>

#include <WOKStep_HeaderExtract.ixx>

#ifdef WNT
# include <WOKNT_WNT_BREAK.hxx>
#endif  // WNT

//=======================================================================
//function : WOKStep_HeaderExtract
//purpose  : 
//=======================================================================
WOKStep_HeaderExtract::WOKStep_HeaderExtract(const Handle(WOKMake_BuildProcess)& abp,
					     const Handle(WOKernel_DevUnit)& aunit, 
					     const Handle(TCollection_HAsciiString)& acode, 
					     const Standard_Boolean checked, 
					     const Standard_Boolean hidden)
: WOKStep_Extract(abp,aunit, acode, checked, hidden)
{

  Handle(WOKBuilder_MSHeaderExtractor) anextractor = new WOKBuilder_MSHeaderExtractor(aunit->Params());

  anextractor->SetMSchema(WOKBuilder_MSTool::GetMSchema());
  SetExtractor(anextractor);
}


//=======================================================================
//function : OutOfDateEntities
//purpose  : 
//=======================================================================
Handle(WOKMake_HSequenceOfInputFile) WOKStep_HeaderExtract::OutOfDateEntities()
{
  Handle(WOKMake_HSequenceOfInputFile) result = new WOKMake_HSequenceOfInputFile;
  Standard_Integer i;

  LoadDependencies();

  Handle(WOKBuilder_MSchema) ameta = Extractor()->MSchema();

  for(i=1; i<=myinflow.Extent(); i++)
    {
#ifdef WNT
_TEST_BREAK();
#endif  // WNT
      Handle(WOKMake_InputFile) infile = myinflow(i);
      Handle(WOKBuilder_MSEntity) anent = Handle(WOKBuilder_MSEntity)::DownCast(infile->BuilderEntity());
      
      if(anent.IsNull())
	{
	  ErrorMsg() << "WOKStep_HeaderExtract::OutOfDateEntities" 
		   << infile->ID() << " is not a MS Entity" << endm;
	  SetFailed();
	  return result;
	}
      
      WOKBuilder_MSActionID anid(anent->Name(),  Extractor()->ExtractorID());
      
      Handle(WOKBuilder_MSAction) anaction = ameta->GetAction(anid);
      
      switch(Extractor()->ExtractionStatus(anaction))
	{
	case WOKBuilder_OutOfDate:
	  result->Append(infile);
	  break;
	case WOKBuilder_UpToDate:
	  break;
	case WOKBuilder_NotDefined:
	  SetFailed();
	  return result;
        default: break;
	}
    }
  return result;
}
