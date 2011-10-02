#ifndef _CPPJini_Define_HeaderFile
#define _CPPJini_Define_HeaderFile

extern Standard_CString 
VJClass,
                 VJTICIncludes,
		 VJInherits,
		 VJTICPublicmets,
		 VJTICPublicfriends,
		 VJTICProtectedmets,
		 VJTICProtectedfields,
		 VJTICPrivatemets,
		 VJTICPrivatefriends,
		 VJTICDefines,
		 VJTICUndefines,
		 VJTICPrivatefields,
		 VJTICSuppMethod,
                 VJVirtual,
                 VJReturn,
                 VJMethodName,
                 VJArgument,
                 VJMetSpec,
VJMethod,
		 VJMBody,
		 VJDName,
		 VJDValue,
		 VJIClass,
                 VJCxxFile,
                 VJLxxFile,
VJoutClass,
		 VJNb,
		 VJValues,
		 VJSupplement,
		 VJMethods,
		 VJAncestors,
VJFullPath,
		 VJMethodHeader,
		 VJConstructorHeader;

extern Handle(TCollection_HAsciiString) CPPJini_InterfaceName;

Handle(TCollection_HAsciiString)& CPPJini_TransientRootName();

class TColStd_DataMapOfAsciiStringInteger;
class TColStd_Array1OfInteger;

void CPPJini_WriteFile(const Handle(EDL_API)& api,
		   const Handle(TCollection_HAsciiString)& aFileName,
		   const Standard_CString var);

void CPPJini_UsedTypes(const Handle(MS_MetaSchema)& aMeta,
		   const Handle(MS_Common)& aCommon,
		   const Handle(TColStd_HSequenceOfHAsciiString)& List,
		   const Handle(TColStd_HSequenceOfHAsciiString)& Incp);

void CPPJini_BuildMethod(const Handle(MS_MetaSchema)& aMeta, 
			 const Handle(EDL_API)& api, 
			 const Handle(TCollection_HAsciiString)& className,
			 const Handle(MS_Method)& m,
			 const Handle(TCollection_HAsciiString)& methodName,
			 const Standard_Integer MethodNumber);

void CPPJini_Enum(const Handle(MS_MetaSchema)& aMeta,
		    const Handle(EDL_API)& api,
		    const Handle(MS_Enum)& anEnum,
		    const Handle(TColStd_HSequenceOfHAsciiString)& outfile);
                    

void CPPJini_CheckMethod(const Standard_Integer index,
			 const Handle(TCollection_HAsciiString)& thename,
			 TColStd_DataMapOfAsciiStringInteger& themap,
			 TColStd_Array1OfInteger& theindexes);
			 

Standard_Boolean CPPJini_IsCasType(const Handle(TCollection_HAsciiString)& typname);

Handle(TCollection_HAsciiString) CPPJini_GetFullJavaType(const Handle(TCollection_HAsciiString)& className);


Standard_Boolean CPPJini_HaveEmptyConstructor(const Handle(MS_MetaSchema)& aMeta,
					      const Handle(TCollection_HAsciiString)& aClass,
					      const Handle(MS_HSequenceOfMemberMet)& methods);

void CPPJini_MethodBuilder(const Handle(MS_MetaSchema)& aMeta, 
			   const Handle(EDL_API)& api, 
			   const Handle(TCollection_HAsciiString)& className,
			   const Handle(MS_Method)& m,
			   const Handle(TCollection_HAsciiString)& methodName,
			   const Standard_Integer nummet);


Handle(TCollection_HAsciiString) CPPJini_UnderScoreReplace(const Handle(TCollection_HAsciiString)& name);


extern Handle(TCollection_HAsciiString) CPPJini_ErrorArgument;

#endif
