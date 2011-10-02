#ifndef _CPPClient_Define_HeaderFile
#define _CPPClient_Define_HeaderFile

extern Standard_CString VClass,
                 VTICIncludes,
		 VInherits,
		 VTICPublicmets,
		 VTICPublicfriends,
		 VTICProtectedmets,
		 VTICProtectedfields,
		 VTICPrivatemets,
		 VTICPrivatefriends,
		 VTICDefines,
		 VTICInlineIncludes,
		 VTICUndefines,
		 VTICPrivatefields,
		 VTICSuppMethod,
                 VRetSpec,
                 VVirtual,
                 VReturn,
                 VAnd,
                 VMethodName,
                 VArgument,
                 VMetSpec,
                 VMethod,
		 VMBody,
		 VDName,
		 VDValue,
                 VIsInline,
                 VIsCreateMethod,
		 VIClass,
		 VSuffix,
                 VCxxFile,
                 VLxxFile,
                 VInlineMethod,
		 VoutClass,
		 VNb,
		 VValues,
		 VSupplement,
		 VTypeMgt,
		 VMethods,
		 VAncestors,
		 VFullPath,
		 VMethodHeader,
		 VConstructorHeader;

extern Handle(TCollection_HAsciiString) CPPClient_InterfaceName;

Handle(TCollection_HAsciiString)& CPPClient_TransientRootName();

void CPPClient_WriteFile(const Handle(EDL_API)& api,
		   const Handle(TCollection_HAsciiString)& aFileName,
		   const Standard_CString var);

void CPPClient_UsedTypes(const Handle(MS_MetaSchema)& aMeta,
		   const Handle(MS_Common)& aCommon,
		   const Handle(TColStd_HSequenceOfHAsciiString)& List,
		   const Handle(TColStd_HSequenceOfHAsciiString)& Incp);

Handle(TCollection_HAsciiString) CPPClient_BuildType(const Handle(MS_MetaSchema)& aMeta,
					       const Handle(TCollection_HAsciiString)& aTypeName);

class Handle(MS_HSequenceOfParam);
Handle(TCollection_HAsciiString) CPPClient_BuildParameterList(const Handle(MS_MetaSchema)& aMeta, 
							const Handle(MS_HSequenceOfParam)& aSeq,
							const Standard_Boolean withDefaultValue);

void CPPClient_BuildMethod(const Handle(MS_MetaSchema)& aMeta, 
		     const Handle(EDL_API)& api, 
		     const Handle(MS_Method)& m,
		     const Handle(TCollection_HAsciiString)& methodName,
		     const Standard_Boolean forDeclaration = Standard_True);

void CPPClient_ClassTypeMgt(const Handle(MS_MetaSchema)& aMeta,
		      const Handle(EDL_API)& api,
		      const Handle(MS_Class)& aClass,
		      const Standard_CString var);
void CPPClient_Enum(const Handle(MS_MetaSchema)& aMeta,
		    const Handle(EDL_API)& api,
		    const Handle(MS_Enum)& anEnum,
		    const Handle(TColStd_HSequenceOfHAsciiString)& outfile);

extern Handle(TCollection_HAsciiString) CPPClient_ErrorArgument;

enum ExtractionType {CPPClient_COMPLETE,CPPClient_INCOMPLETE,CPPClient_SEMICOMPLETE};
#endif
