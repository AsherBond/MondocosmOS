#ifndef _TCPPExt_HeaderFile
#define _TCPPExt_HeaderFile
#include <MS.hxx>

#include <EDL_API.hxx>

#include <MS_MetaSchema.hxx>

#include <MS_Class.hxx>
#include <MS_GenClass.hxx>
#include <MS_InstClass.hxx>
#include <MS_Package.hxx>
#include <MS_Error.hxx>
#include <MS_Imported.hxx>

#include <MS_InstMet.hxx>
#include <MS_ClassMet.hxx>
#include <MS_Construc.hxx>
#include <MS_ExternMet.hxx>
 
#include <MS_Param.hxx>
#include <MS_Field.hxx>
#include <MS_GenType.hxx>
#include <MS_Enum.hxx>
#include <MS_PrimType.hxx>
#include <MS_Alias.hxx>
#include <MS_Pointer.hxx>

#include <MS_HSequenceOfMemberMet.hxx>
#include <MS_HSequenceOfExternMet.hxx>
#include <MS_HSequenceOfParam.hxx>
#include <MS_HSequenceOfField.hxx>
#include <MS_HSequenceOfGenType.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <TColStd_HSequenceOfInteger.hxx>

#include <TCollection_HAsciiString.hxx>

#include <Standard_NoSuchObject.hxx>

#ifndef _Standard_Macro_HeaderFile
# include <Standard_Macro.hxx>
#endif

extern "C" {

        Standard_EXPORT Handle(TColStd_HSequenceOfHAsciiString) TCPP_TemplatesUsed();

        void Standard_EXPORT TCPP_Extract(const Handle(MS_MetaSchema)& ams,
			 const Handle(TCollection_HAsciiString)& aname,
			 const Handle(TColStd_HSequenceOfHAsciiString)& edlsfullpath,
			 const Handle(TCollection_HAsciiString)& outdir,
			 const Handle(TColStd_HSequenceOfHAsciiString)& outfile,
			 const Standard_CString);
	
}

// EDL variables
//
Standard_CString VClass              = "%Class",
                 VRetSpec            = "%RetSpec",
                 VVirtual            = "%Virtual",
                 VReturn             = "%Return",
                 VAnd                = "%And",
                 VMethodName         = "%MethodName",
                 VArgument           = "%Arguments",
                 VMetSpec            = "%MetSpec",
                 VMethod             = "%Method",
                 VIsInline           = "%IsInline",
                 VIsCreateMethod     = "%IsCreateMethod",
                 VCxxFile            = "CxxFile",
                 VLxxFile            = "LxxFile",
		 VMethodHeader       = "%MethodHeader",
		 VConstructorHeader  = "%ConstructorHeader";

#endif
