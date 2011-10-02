// File:	WOKDFLT_MSDFLTExtractor.cxx
// Created:	Fri Jun  7 11:34:22 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <MS_GenClass.hxx>
#include <MS_InstClass.hxx>
#include <MS_Pointer.hxx>
#include <MS_Alias.hxx>
#include <MS_Error.hxx>
#include <MS_Package.hxx>
#include <MS.hxx>


#include <WOKTools_Messages.hxx>
#include <WOKTools_MapOfHAsciiString.hxx>

#include <WOKBuilder_MSActionID.hxx>
#include <WOKBuilder_MSAction.hxx>
#include <WOKBuilder_MSEntity.hxx>
#include <WOKBuilder_MSchema.hxx>

#include <WOKDFLT_MSDFLTExtractor.ixx>


//=======================================================================
//function : WOKDFLT_MSDFLTExtractor
//purpose  : 
//=======================================================================
 WOKDFLT_MSDFLTExtractor::WOKDFLT_MSDFLTExtractor(const Handle(TCollection_HAsciiString)& ashared,
						   const Handle(TColStd_HSequenceOfHAsciiString)& searchlist)
  : WOKBuilder_MSExtractor(new TCollection_HAsciiString("CSFDBSCHEMA"), ashared, searchlist)
{
}


//=======================================================================
//function : WOKDFLT_MSDFLTExtractor
//purpose  : 
//=======================================================================
WOKDFLT_MSDFLTExtractor::WOKDFLT_MSDFLTExtractor(const WOKUtils_Param& params)
  : WOKBuilder_MSExtractor(new TCollection_HAsciiString("CSFDBSCHEMA"), params)
{
}

//=======================================================================
//function : GetTypeDepList
//purpose  : 
//=======================================================================
//Handle(TColStd_HSequenceOfHAsciiString) WOKDFLT_MSDFLTExtractor::GetTypeDepList(const Handle(TCollection_HAsciiString)& aname) const
Handle(TColStd_HSequenceOfHAsciiString) WOKDFLT_MSDFLTExtractor::GetTypeDepList(const Handle(TCollection_HAsciiString)& ) const
{
  return new TColStd_HSequenceOfHAsciiString;
}


//=======================================================================
//function : GetTypeMDate
//purpose  : 
//=======================================================================
//WOKUtils_TimeStat WOKDFLT_MSDFLTExtractor::GetTypeMDate(const Handle(TCollection_HAsciiString)& aname) const
WOKUtils_TimeStat WOKDFLT_MSDFLTExtractor::GetTypeMDate(const Handle(TCollection_HAsciiString)& ) const
{
  WOKUtils_TimeStat atruc;

  memset (  &atruc, 0, sizeof ( atruc )  );

  return atruc;
}

//=======================================================================
//function : ExtractionStatus
//purpose  : 
//=======================================================================
//WOKBuilder_MSActionStatus WOKDFLT_MSDFLTExtractor::ExtractionStatus(const Handle(WOKBuilder_MSAction)& anaction)
WOKBuilder_MSActionStatus WOKDFLT_MSDFLTExtractor::ExtractionStatus(const Handle(WOKBuilder_MSAction)& )
{
  return WOKBuilder_OutOfDate;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : ExtractorID
//purpose  : 
//=======================================================================
WOKBuilder_MSActionType WOKDFLT_MSDFLTExtractor::ExtractorID() const
{
  return WOKBuilder_SchemaExtract;
}
