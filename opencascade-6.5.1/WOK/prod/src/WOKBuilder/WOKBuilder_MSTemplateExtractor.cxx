// File:	WOKBuilder_MSTemplateExtractor.cxx
// Created:	Tue Mar 19 20:48:23 1996
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <WOKBuilder_MSTemplateExtractor.ixx>

//=======================================================================
//function : WOKBuilder_MSTemplateExtractor
//purpose  : 
//=======================================================================
WOKBuilder_MSTemplateExtractor::WOKBuilder_MSTemplateExtractor(const Handle(TCollection_HAsciiString)& ashared, 
							       const Handle(TColStd_HSequenceOfHAsciiString)& searchlist)
  : WOKBuilder_MSExtractor(new TCollection_HAsciiString("TCPP"), ashared, searchlist)
{
}

//=======================================================================
//function : WOKBuilder_MSTemplateExtractor
//purpose  : 
//=======================================================================
WOKBuilder_MSTemplateExtractor::WOKBuilder_MSTemplateExtractor(const WOKUtils_Param& params)
  : WOKBuilder_MSExtractor(new TCollection_HAsciiString("TCPP"), params)
{
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : ExtractorID
//purpose  : 
//=======================================================================
WOKBuilder_MSActionType WOKBuilder_MSTemplateExtractor::ExtractorID() const
{
  return WOKBuilder_TemplateExtract;
}

//=======================================================================
//function : ExtractionStatus
//purpose  : 
//=======================================================================
WOKBuilder_MSActionStatus WOKBuilder_MSTemplateExtractor::ExtractionStatus(const Handle(WOKBuilder_MSAction)& )
{
  return WOKBuilder_OutOfDate;
}

