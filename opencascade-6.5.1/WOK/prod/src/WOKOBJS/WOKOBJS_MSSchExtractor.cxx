// File:	WOKOBJS_MSSchExtractor.cxx
// Created:	Mon Feb 24 15:48:57 1997
// Author:	Jean GAUTIER
//		<jga@hourax.paris1.matra-dtv.fr>

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

#include <WOKOBJS_MSSchExtractor.ixx>

//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKOBJS_MSSchExtractor
//purpose  : 
//=======================================================================
WOKOBJS_MSSchExtractor::WOKOBJS_MSSchExtractor(const Handle(TCollection_HAsciiString)& ashared,
					       const Handle(TColStd_HSequenceOfHAsciiString)& searchlist)
  : WOKBuilder_MSExtractor(new TCollection_HAsciiString("OBJSSCHEMA"), ashared, searchlist)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKOBJS_MSSchExtractor
//purpose  : 
//=======================================================================
WOKOBJS_MSSchExtractor::WOKOBJS_MSSchExtractor(const WOKUtils_Param& params)
  : WOKBuilder_MSExtractor(new TCollection_HAsciiString("OBJSSCHEMA"), params)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetTypeDepList
//purpose  : 
//=======================================================================
//Handle(TColStd_HSequenceOfHAsciiString) WOKOBJS_MSSchExtractor::GetTypeDepList(const Handle(TCollection_HAsciiString)& atype) const
Handle(TColStd_HSequenceOfHAsciiString) WOKOBJS_MSSchExtractor::GetTypeDepList(const Handle(TCollection_HAsciiString)& ) const
{
  return new TColStd_HSequenceOfHAsciiString;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetTypeMDate
//purpose  : 
//=======================================================================
//WOKUtils_TimeStat WOKOBJS_MSSchExtractor::GetTypeMDate(const Handle(TCollection_HAsciiString)& atype) const
WOKUtils_TimeStat WOKOBJS_MSSchExtractor::GetTypeMDate(const Handle(TCollection_HAsciiString)& ) const
{
  WOKUtils_TimeStat atruc = 0;

  return atruc;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : ExtractorID
//purpose  : 
//=======================================================================
WOKBuilder_MSActionType WOKOBJS_MSSchExtractor::ExtractorID() const
{
  return WOKBuilder_SchemaExtract;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : ExtractionStatus
//purpose  : 
//=======================================================================
//WOKBuilder_MSActionStatus WOKOBJS_MSSchExtractor::ExtractionStatus(const Handle(WOKBuilder_MSAction)& anaction) 
WOKBuilder_MSActionStatus WOKOBJS_MSSchExtractor::ExtractionStatus(const Handle(WOKBuilder_MSAction)& ) 
{
  return WOKBuilder_OutOfDate;
}

