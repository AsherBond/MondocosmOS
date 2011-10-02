// File:	WOKBuilder_MSTranslatorPtr.hxx
// Created:	Wed Oct 11 21:24:05 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#ifndef WOKBuilder_MSTranslatorPtr_HeaderFile
#define WOKBuilder_MSTranslatorPtr_HeaderFile

#include <Standard_Type.hxx>

#include <MS_MetaSchema.hxx>
#include <TCollection_HAsciiString.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>

const Handle(Standard_Type)& STANDARD_TYPE(WOKBuilder_MSTranslatorPtr);

extern "C" {

typedef Standard_Integer (*WOKBuilder_MSTranslatorPtr)(const Handle(MS_MetaSchema)&,
						       const Handle(TCollection_HAsciiString)&,
						       const Handle(TColStd_HSequenceOfHAsciiString)&,
						       const Handle(TColStd_HSequenceOfHAsciiString)&,
						       const Handle(TColStd_HSequenceOfHAsciiString)&,
						       const Handle(TColStd_HSequenceOfHAsciiString)&);
}

#endif
