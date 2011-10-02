#ifndef _CPPClient_HeaderFile
#define _CPPClient_HeaderFile
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

extern "C" {

        Handle(TColStd_HSequenceOfHAsciiString) Standard_EXPORT CPPClient_TemplatesUsed();

	void Standard_EXPORT CPPClient_Init(const Handle(MS_MetaSchema)&,
			    const Handle(TCollection_HAsciiString)&, 
			    const Handle(MS_HSequenceOfExternMet)&,
			    const Handle(MS_HSequenceOfMemberMet)&);
	
	void Standard_EXPORT CPPClient_Extract(const Handle(MS_MetaSchema)& ams,
			       const Handle(TCollection_HAsciiString)& atypename,
			       const Handle(TColStd_HSequenceOfHAsciiString)& edlsfullpath,
			       const Handle(TCollection_HAsciiString)& outdir,
			       const Handle(TColStd_HSequenceOfHAsciiString)& outfile,
			       const Standard_CString Mode);
      }

enum ExtractionType {CPPClient_COMPLETE,CPPClient_INCOMPLETE,CPPClient_SEMICOMPLETE};

Handle(TCollection_HAsciiString) CPPClient_BuildType(const Handle(MS_MetaSchema)&,
					       const Handle(TCollection_HAsciiString)&);

void CPPClient_TransientHandle(const Handle(EDL_API)&,
			 const Handle(TCollection_HAsciiString)&,
			 const Handle(TCollection_HAsciiString)&,
			 const Handle(TCollection_HAsciiString)&);


void CPPClient_TransientClass(const Handle(MS_MetaSchema)&,
			      const Handle(EDL_API)&,
			      const Handle(MS_Class)&,
			      const Handle(TColStd_HSequenceOfHAsciiString)&,
			      const ExtractionType,
			      const Handle(MS_HSequenceOfMemberMet)&);

void CPPClient_MPVClass(const Handle(MS_MetaSchema)&,
			const Handle(EDL_API)&,
			const Handle(MS_Class)&,
			const Handle(TColStd_HSequenceOfHAsciiString)&,
			const ExtractionType,
			const Handle(MS_HSequenceOfMemberMet)&);

void CPPClient_Alias(const Handle(MS_MetaSchema)&,
	      const Handle(EDL_API)&,
	      const Handle(MS_Alias)&,
	      const Handle(TColStd_HSequenceOfHAsciiString)&);

void CPPClient_Package(const Handle(MS_MetaSchema)&,
		       const Handle(EDL_API)&,
		       const Handle(MS_Package)&,
		       const Handle(TColStd_HSequenceOfHAsciiString)&,
		       const ExtractionType,
		       const Handle(MS_HSequenceOfExternMet)&);

void CPPClient_BuildMethod(const Handle(MS_MetaSchema)& aMeta, 
		     const Handle(EDL_API)& api, 
		     const Handle(MS_Method)& m,
		     const Handle(TCollection_HAsciiString)& methodName,
		     const Standard_Boolean forDeclaration);

void CPPClient_Enum(const Handle(MS_MetaSchema)& aMeta,
		    const Handle(EDL_API)& api,
		    const Handle(MS_Enum)& anEnum,
		    const Handle(TColStd_HSequenceOfHAsciiString)& outfile);

// EDL variables
//
Standard_CString VClass              = "%Class",
                 VTICIncludes        = "%TICIncludes",
		 VInherits           = "%Inherits",
		 VTICPublicmets      = "%TICPublicmets",
		 VTICPublicfriends   = "%TICPublicfriends",
		 VTICProtectedmets   = "%TICProtectedmets",
		 VTICProtectedfields = "%TICProtectedfields",
		 VTICPrivatemets     = "%TICPrivatemets",
		 VTICPrivatefriends  = "%TICPrivatefriends",
		 VTICDefines         = "%TICDefines",
		 VTICInlineIncludes  = "%TICInlineIncludes",
		 VTICUndefines       = "%TICUndefines",
		 VTICPrivatefields   = "%TICPrivatefields",
                 VRetSpec            = "%RetSpec",
                 VVirtual            = "%Virtual",
                 VReturn             = "%Return",
                 VAnd                = "%And",
                 VMethodName         = "%MethodName",
                 VArgument           = "%Arguments",
                 VMetSpec            = "%MetSpec",
                 VMethod             = "%Method",
		 VMBody              = "%MBody",
		 VDName              = "%DName",
		 VDValue             = "%DValue",
                 VIsInline           = "%IsInline",
                 VIsCreateMethod     = "%IsCreateMethod",
		 VIClass             = "%IClass",
		 VSuffix             = "%Suffix",
                 VCxxFile            = "CxxFile",
                 VLxxFile            = "LxxFile",
                 VInlineMethod       = "%InlineMethod",
		 VoutClass           = "%outClass",
		 VNb                 = "%Nb",
		 VValues             = "%Values",
		 VSupplement         = "%Supplement",
		 VTypeMgt            = "%TypeMgt",
		 VMethods            = "%Methods",
		 VAncestors          = "%Ancestors",
		 VFullPath           = "%FullPath",
		 VMethodHeader       = "%MethodHeader",
		 VConstructorHeader  = "%ConstructorHeader",
		 VTICSuppMethod      = "%TICSuppMethod",
		 VInterface          = "%Interface";


#endif
