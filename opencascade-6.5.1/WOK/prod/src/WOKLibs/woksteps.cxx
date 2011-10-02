// File:	WOKStep.cxx
// Created:	Thu Jun 27 15:02:13 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>

#include <WOKMake_StepAccessMacro.hxx>

#include <WOKStep_Source.hxx>
#include <WOKStep_CDLUnitSource.hxx>
#include <WOKStep_ExecutableSource.hxx>
#include <WOKStep_ResourceSource.hxx>
#include <WOKStep_ToolkitSource.hxx>
#include <WOKStep_MSFill.hxx>
#include <WOKStep_SourceExtract.hxx>
#include <WOKStep_HeaderExtract.hxx>
#include <WOKStep_ServerExtract.hxx>
#include <WOKStep_ClientExtract.hxx>
#include <WOKStep_JiniExtract.hxx>
#include <WOKStep_EngineExtract.hxx>
#include <WOKStep_TemplateExtract.hxx>
#include <WOKStep_ExtractExecList.hxx>
#include <WOKStep_Include.hxx>
#include <WOKStep_CodeGenerate.hxx>
#include <WOKStep_Compile.hxx>
#include <WOKStep_TKList.hxx>
#include <WOKStep_LibUnCompress.hxx>
#include <WOKStep_LibExtract.hxx>
#include <WOKStep_LibLimit.hxx>
#include <WOKStep_TransitiveTKReplace.hxx>
#include <WOKStep_DirectTKReplace.hxx>
#include <WOKStep_ArchiveLibrary.hxx>
#include <WOKStep_DynamicLibrary.hxx>
#include <WOKStep_ImplementationDep.hxx>
#include <WOKStep_TransitiveLinkList.hxx>
#include <WOKStep_DirectLinkList.hxx>
#include <WOKStep_LibLink.hxx>
#include <WOKStep_ExecLink.hxx>
#include <WOKStep_EngLinkList.hxx>
#include <WOKStep_EngDatFiles.hxx>
#include <WOKStep_EngLDFile.hxx>

#ifdef WNT
#include <WOKStep_DLLink.hxx>
#include <WOKStep_ImportLibrary.hxx>
#include <WOKStep_StaticLibrary.hxx>
#include <WOKStep_WNTK.hxx>
#include <WOKStep_EXELink.hxx>
#endif

DECLARE_STEP(WOKStep_Source)
DECLARE_STEP(WOKStep_CDLUnitSource)
DECLARE_STEP(WOKStep_ExecutableSource)
DECLARE_STEP(WOKStep_ResourceSource)
DECLARE_STEP(WOKStep_ToolkitSource)
DECLARE_STEP(WOKStep_MSFill)
DECLARE_STEP(WOKStep_SourceExtract)
DECLARE_STEP(WOKStep_HeaderExtract)
DECLARE_STEP(WOKStep_ServerExtract)
DECLARE_STEP(WOKStep_ClientExtract)
DECLARE_STEP(WOKStep_JiniExtract)
DECLARE_STEP(WOKStep_EngineExtract)
DECLARE_STEP(WOKStep_TemplateExtract)
DECLARE_STEP(WOKStep_ExtractExecList)
DECLARE_STEP(WOKStep_Include)
DECLARE_STEP(WOKStep_CodeGenerate)
DECLARE_STEP(WOKStep_Compile)
DECLARE_STEP(WOKStep_TKList)
DECLARE_STEP(WOKStep_LibUnCompress)
DECLARE_STEP(WOKStep_LibExtract)
DECLARE_STEP(WOKStep_LibLimit)
DECLARE_STEP(WOKStep_TransitiveTKReplace)
DECLARE_STEP(WOKStep_DirectTKReplace)
DECLARE_STEP(WOKStep_ArchiveLibrary)
DECLARE_STEP(WOKStep_DynamicLibrary)
DECLARE_STEP(WOKStep_ImplementationDep)
DECLARE_STEP(WOKStep_TransitiveLinkList)
DECLARE_STEP(WOKStep_DirectLinkList)
DECLARE_STEP(WOKStep_LibLink)
DECLARE_STEP(WOKStep_ExecLink)
DECLARE_STEP(WOKStep_EngLinkList)
DECLARE_STEP(WOKStep_EngDatFiles)
DECLARE_STEP(WOKStep_EngLDFile)

#ifdef WNT
DECLARE_STEP(WOKStep_DLLink)
DECLARE_STEP(WOKStep_ImportLibrary)
DECLARE_STEP(WOKStep_StaticLibrary)
DECLARE_STEP(WOKStep_WNTK)
DECLARE_STEP(WOKStep_EXELink)
#endif


