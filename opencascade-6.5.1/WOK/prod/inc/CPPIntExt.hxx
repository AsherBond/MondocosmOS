// ADN
//    
// 11/1995
//

#include <MS_MetaSchema.hxx>
#include <MS_Interface.hxx>
#include <MS_Engine.hxx>
#include <EDL_API.hxx>
#include <MS_MapOfType.hxx>
#include <MS_MapOfMethod.hxx>
#include <MS_MapOfGlobalEntity.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <TColStd_SequenceOfInteger.hxx>



void CPPIntExt_LoadMethods(const Handle(MS_MetaSchema)&,
			   const Handle(MS_Interface)&,
			   const Handle(EDL_API)&,
			   MS_MapOfMethod&,
			   MS_MapOfType&,
			   MS_MapOfType&,
			   MS_MapOfGlobalEntity&,
			   Standard_Boolean AddArgs=Standard_False);


void CPPIntExt_LoadMethods(const Handle(MS_MetaSchema)&,
			   const Handle(MS_Engine)&,
			   const Handle(EDL_API)&,
			   MS_MapOfMethod&,
			   MS_MapOfType&,
			   MS_MapOfGlobalEntity&,
			   const Handle(TColStd_HSequenceOfHAsciiString)&);


void CPPIntExt_ProcessIncludes(const Handle(MS_Interface)&,
			       const Handle(EDL_API)&,
			       const MS_MapOfType&,
			       const MS_MapOfGlobalEntity&);

void CPPIntExt_ProcessHeader(const Handle(MS_Interface)&,
			     const Handle(EDL_API)&);

void CPPIntExt_ProcessTypes(const Handle(MS_MetaSchema)&,
			    const Handle(MS_Interface)&,
			    const Handle(EDL_API)&,
			    const MS_MapOfType&);

void CPPIntExt_ProcessExec(const Handle(MS_Interface)&,
			   const Handle(EDL_API)&,
			   const MS_MapOfType&);

void CPPIntExt_ProcessMultiExec(const Handle(MS_Interface)&,
				const Handle(EDL_API)&,
				const TColStd_SequenceOfInteger&,
				const MS_MapOfType&);


void CPPIntExt_ProcessBottom(const Handle(MS_Interface)&,
			     const Handle(EDL_API)&);

void CPPIntExt_ProcessCases(const Handle(MS_MetaSchema)&,
			    const Handle(MS_Interface)&,
			    const Handle(EDL_API)&,
			    const MS_MapOfMethod&);

void CPPIntExt_ProcessMultiCases(const Handle(MS_MetaSchema)&,
				 const Handle(MS_Interface)&,
				 const Handle(EDL_API)&,
				 const MS_MapOfMethod&,
				 TColStd_SequenceOfInteger&);

void CPPIntExt_ProcessCxx(const Handle(MS_Engine)&,
			  const Handle(EDL_API)&,
			  const Handle(TColStd_HSequenceOfHAsciiString)&);

void CPPIntExt_ProcessCcl(const Handle(MS_MetaSchema)&,
			  const Handle(MS_Engine)&,
			  const Handle(EDL_API)&,
			  MS_MapOfType&,
			  MS_MapOfGlobalEntity&);


void CPPIntExt_ProcessEngineInit(const Handle(MS_MetaSchema)&,
				 const Handle(MS_Engine)&,
				 const Handle(EDL_API)&,
				 MS_MapOfType&);

