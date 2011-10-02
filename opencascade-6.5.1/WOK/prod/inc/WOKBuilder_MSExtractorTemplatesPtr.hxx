// File:	WOKBuilder_MSExtractorTemplatesPtr.hxx
// Created:	Wed Oct 11 21:30:08 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#ifndef WOKBuilder_MSExtractorTemplatesPtr_HeaderFile
#define WOKBuilder_MSExtractorTemplatesPtr_HeaderFile

#include <Standard_Type.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

const Handle(Standard_Type)& STANDARD_TYPE(WOKBuilder_MSExtractorTemplatesPtr);

extern "C" {

typedef Handle(TColStd_HSequenceOfHAsciiString) (*WOKBuilder_MSExtractorTemplatesPtr)();

}

#endif
