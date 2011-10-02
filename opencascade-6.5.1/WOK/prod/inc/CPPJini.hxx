#ifndef _CPPJini_HeaderFile
#define _CPPJini_HeaderFile
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

#include <MS_Interface.hxx>

#include <MS_HSequenceOfMemberMet.hxx>
#include <MS_HSequenceOfExternMet.hxx>
#include <MS_HArray1OfParam.hxx>
#include <MS_HSequenceOfField.hxx>
#include <MS_HSequenceOfGenType.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <TColStd_HSequenceOfInteger.hxx>

#include <TCollection_HAsciiString.hxx>

#include <Standard_NoSuchObject.hxx>

#include <CPPJini_ExtractionType.hxx>

extern "C" {

        Handle(TColStd_HSequenceOfHAsciiString) Standard_EXPORT CPPJini_TemplatesUsed();

	void Standard_EXPORT CPPJini_Init(const Handle(MS_MetaSchema)&,
			    const Handle(TCollection_HAsciiString)&, 
			    const Handle(MS_HSequenceOfExternMet)&,
			    const Handle(MS_HSequenceOfMemberMet)&,
                            const Handle( TColStd_HSequenceOfHAsciiString )&
                           );

	void Standard_EXPORT CPPJini_Extract(const Handle(MS_MetaSchema)& ams,
			       const Handle(TCollection_HAsciiString)& atypename,
			       const Handle(TColStd_HSequenceOfHAsciiString)& edlsfullpath,
			       const Handle(TCollection_HAsciiString)& outdir,
			       const Handle(TColStd_HSequenceOfHAsciiString)& outfile,
			       const Standard_CString Mode);
      }

void CPPJini_TransientHandle(const Handle(EDL_API)&,
			 const Handle(TCollection_HAsciiString)&,
			 const Handle(TCollection_HAsciiString)&,
			 const Handle(TCollection_HAsciiString)&);


void CPPJini_TransientClass(const Handle(MS_MetaSchema)&,
			      const Handle(EDL_API)&,
			      const Handle(MS_Class)&,
			      const Handle(TColStd_HSequenceOfHAsciiString)&,
			      const CPPJini_ExtractionType,
			      const Handle(MS_HSequenceOfMemberMet)&);

void CPPJini_MPVClass(const Handle(MS_MetaSchema)&,
			const Handle(EDL_API)&,
			const Handle(MS_Class)&,
			const Handle(TColStd_HSequenceOfHAsciiString)&,
			const CPPJini_ExtractionType,
			const Handle(MS_HSequenceOfMemberMet)&);

void CPPJini_Alias(const Handle(MS_MetaSchema)&,
	      const Handle(EDL_API)&,
	      const Handle(MS_Alias)&,
	      const Handle(TColStd_HSequenceOfHAsciiString)&);

void CPPJini_Package(const Handle(MS_MetaSchema)&,
		       const Handle(EDL_API)&,
		       const Handle(MS_Package)&,
		       const Handle(TColStd_HSequenceOfHAsciiString)&,
		       const CPPJini_ExtractionType,
		       const Handle(MS_HSequenceOfExternMet)&);

void CPPJini_BuildMethod(const Handle(MS_MetaSchema)& aMeta, 
			 const Handle(EDL_API)& api, 
			 const Handle(TCollection_HAsciiString)& className,
			 const Handle(MS_Method)& m,
			 const Handle(TCollection_HAsciiString)& methodName,
			 const Standard_Integer nbmeth);

void CPPJini_Enum(const Handle(MS_MetaSchema)& aMeta,
		    const Handle(EDL_API)& api,
		    const Handle(MS_Enum)& anEnum,
		    const Handle(TColStd_HSequenceOfHAsciiString)& outfile);

// EDL variables
//
Standard_CString 
VJClass              = "%Class",
                 VJTICIncludes        = "%TICIncludes",
		 VJInherits           = "%Inherits",
		 VJTICPublicmets      = "%TICPublicmets",
		 VJTICPublicfriends   = "%TICPublicfriends",
		 VJTICProtectedmets   = "%TICProtectedmets",
		 VJTICProtectedfields = "%TICProtectedfields",
		 VJTICPrivatemets     = "%TICPrivatemets",
		 VJTICPrivatefriends  = "%TICPrivatefriends",
		 VJTICDefines         = "%TICDefines",
		 VJTICUndefines       = "%TICUndefines",
		 VJTICPrivatefields   = "%TICPrivatefields",
VJVirtual            = "%Virtual",
VJReturn             = "%Return",
VJMethodName         = "%MethodName",
                 VJArgument           = "%Arguments",
VJMetSpec            = "%MetSpec",
VJMethod             = "%Method",
		 VJMBody              = "%MBody",
		 VJDName              = "%DName",
		 VJDValue             = "%DValue",
		 VJIClass             = "%IClass",
                 VJCxxFile            = "CxxFile",
                 VJLxxFile            = "LxxFile",
		 VJoutClass           = "%outClass",
VJNb                 = "%Nb",
		 VJValues             = "%Values",
		 VJSupplement         = "%Supplement",
		 VJMethods            = "%Methods",
VJAncestors          = "%Ancestors",
VJFullPath           = "%FullPath",
VJMethodHeader       = "%MethodHeader",
VJConstructorHeader  = "%ConstructorHeader",
		 VJTICSuppMethod      = "%TICSuppMethod",
VJInterface          = "%Interface",
VJNbConstr           = "%NbConstr";


#endif
