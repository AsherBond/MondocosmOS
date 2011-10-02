// File:	WOKBuilder_MSEngineExtractor.cxx
// Created:	Tue Mar 19 20:47:06 1996
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <WOKBuilder_MSEngineExtractor.ixx>

//=======================================================================
//function : WOKBuilder_MSEngineExtractor
//purpose  : 
//=======================================================================
WOKBuilder_MSEngineExtractor::WOKBuilder_MSEngineExtractor(const Handle(TCollection_HAsciiString)& ashared, 
							   const Handle(TColStd_HSequenceOfHAsciiString)& searchlist)
  : WOKBuilder_MSExtractor(new TCollection_HAsciiString("CPPENG"), ashared, searchlist)
{
}

//=======================================================================
//function : WOKBuilder_MSEngineExtractor
//purpose  : 
//=======================================================================
WOKBuilder_MSEngineExtractor::WOKBuilder_MSEngineExtractor(const WOKUtils_Param& params)
  : WOKBuilder_MSExtractor(new TCollection_HAsciiString("CPPENG"), params)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : ExtractorID
//purpose  : 
//=======================================================================
WOKBuilder_MSActionType WOKBuilder_MSEngineExtractor::ExtractorID() const
{
  return WOKBuilder_EngineExtract;
}

//=======================================================================
//function : ExtractionStatus
//purpose  : 
//=======================================================================
WOKBuilder_MSActionStatus WOKBuilder_MSEngineExtractor::ExtractionStatus(const Handle(WOKBuilder_MSAction)& )
{
  return WOKBuilder_OutOfDate;
}

