// File:	WOKBuilder_MSServerExtractor.cxx
// Created:	Tue Mar 19 20:49:35 1996
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <WOKBuilder_MSServerExtractor.ixx>

//=======================================================================
//function : WOKBuilder_MSServerExtractor
//purpose  : 
//=======================================================================
WOKBuilder_MSServerExtractor::WOKBuilder_MSServerExtractor(const Handle(TCollection_HAsciiString)& ashared, 
							   const Handle(TColStd_HSequenceOfHAsciiString)& searchlist)
  : WOKBuilder_MSExtractor(new TCollection_HAsciiString("CPPINT"), ashared, searchlist)
{
}

//=======================================================================
//function : WOKBuilder_MSServerExtractor
//purpose  : 
//=======================================================================
WOKBuilder_MSServerExtractor::WOKBuilder_MSServerExtractor(const WOKUtils_Param& params)
  : WOKBuilder_MSExtractor(new TCollection_HAsciiString("CPPINT"), params)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : ExtractorID
//purpose  : 
//=======================================================================
WOKBuilder_MSActionType WOKBuilder_MSServerExtractor::ExtractorID() const
{
  return WOKBuilder_ServerExtract;
}

//=======================================================================
//function : ExtractionStatus
//purpose  : 
//=======================================================================
WOKBuilder_MSActionStatus WOKBuilder_MSServerExtractor::ExtractionStatus(const Handle(WOKBuilder_MSAction)& )
{
  return WOKBuilder_OutOfDate;
}

