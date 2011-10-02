// File:	WOKBuilder_MSExtractorExtractPtr.hxx
// Created:	Wed Oct 11 21:33:44 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#ifndef WOKBuilder_MSExtractorExtractPtr_HeaderFile
#define WOKBuilder_MSExtractorExtractPtr_HeaderFile

#include <Standard_Type.hxx>

#include <TCollection_HAsciiString.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <MS_MetaSchema.hxx>

const Handle(Standard_Type)& STANDARD_TYPE(WOKBuilder_MSExtractorExtractPtr);

extern "C" {

typedef void (*WOKBuilder_MSExtractorExtractPtr)(const Handle(MS_MetaSchema)& ,
						 const Handle(TCollection_HAsciiString)& ,
						 const Handle(TColStd_HSequenceOfHAsciiString)& ,
						 const Handle(TCollection_HAsciiString)& ,
						 const Handle(TColStd_HSequenceOfHAsciiString)& ,
						 const Standard_CString);
}

#endif
