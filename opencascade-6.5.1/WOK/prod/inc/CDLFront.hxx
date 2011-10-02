#ifndef CDLFront_HeaderFile
#define CDLFront_HeaderFile

#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <MS_MetaSchema.hxx>

#ifndef _Standard_Macro_HeaderFile
# include <Standard_Macro.hxx>
#endif

extern "C" {

int Standard_EXPORT CDLTranslate(const Handle(MS_MetaSchema)&             aMetaSchema, 
		 const Handle(TCollection_HAsciiString)&  aFileName,
		 const Handle(TColStd_HSequenceOfHAsciiString)& aGlobalList,
		 const Handle(TColStd_HSequenceOfHAsciiString)& aTypeList,
		 const Handle(TColStd_HSequenceOfHAsciiString)& anInstList,
		 const Handle(TColStd_HSequenceOfHAsciiString)& anGenList);
}

#endif
