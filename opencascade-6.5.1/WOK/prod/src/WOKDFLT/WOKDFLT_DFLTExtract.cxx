// File:	WOKDFLT_DFLTExtract.cxx
// Created:	Fri Jun  7 11:22:58 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>



#include <WOKTools_Messages.hxx>

#include <WOKBuilder_MSHeaderExtractor.hxx>
#include <WOKBuilder_MSActionID.hxx>
#include <WOKBuilder_MSAction.hxx>
#include <WOKBuilder_MSchema.hxx>
#include <WOKBuilder_MSEntity.hxx>

#include <WOKMake_InputFile.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_HSequenceOfOutputFile.hxx>


#include <WOKDFLT_MSDFLTExtractor.hxx>

#include <WOKDFLT_DFLTExtract.ixx>

//=======================================================================
//function : WOKDFLT_DFLTExtract
//purpose  : 
//=======================================================================
WOKDFLT_DFLTExtract::WOKDFLT_DFLTExtract(const Handle(WOKMake_BuildProcess)& abp,
					 const Handle(WOKernel_DevUnit)& aunit, 
					 const Handle(TCollection_HAsciiString)& acode, 
					 const Standard_Boolean checked, 
					 const Standard_Boolean hidden)
: WOKStep_Extract(abp,aunit, acode, checked, hidden)
{

  Handle(WOKDFLT_MSDFLTExtractor) anextractor = new WOKDFLT_MSDFLTExtractor(Unit()->Params());

  anextractor->SetMSchema(WOKBuilder_MSTool::GetMSchema());
  SetExtractor(anextractor);

}


//=======================================================================
//function : OutOfDateEntities
//purpose  : 
//=======================================================================
Handle(WOKMake_HSequenceOfInputFile) WOKDFLT_DFLTExtract::OutOfDateEntities()
{
  Handle(WOKMake_HSequenceOfInputFile) result = new WOKMake_HSequenceOfInputFile;
  Handle(WOKBuilder_MSchema) ameta = Extractor()->MSchema();
  Standard_Integer i;

  LoadDependencies();

  for(i=1; i<=myinflow.Extent(); i++)
    {
      Handle(WOKMake_InputFile) infile = myinflow(i);
      
Handle(WOKBuilder_MSEntity) anent = Handle(WOKBuilder_MSEntity)::DownCast(infile->BuilderEntity());
      
      if(anent.IsNull())
	{
	  ErrorMsg() << "WOKStep_HeaderExtract::OutOfDateEntities" 
		   << infile->ID() << " is not a MS Entity" << endm;
	  SetFailed();
	  return result;
	}
      
      WOKBuilder_MSActionID anid(anent->Name(), WOKBuilder_HeaderExtract);
      
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

