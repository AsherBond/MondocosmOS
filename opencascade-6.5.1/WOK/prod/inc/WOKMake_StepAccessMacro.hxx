// File:	WOKMake_StepAccessMacro.hxx
// Created:	Thu Jun 27 20:28:11 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#ifndef WOKMake_StepAccessMacro_HeaderFile
#define WOKMake_StepAccessMacro_HeaderFile

#ifndef _Standard_Macro_HeaderFile
# include <Standard_Macro.hxx>
#endif

#define DECLARE_STEP(STEPMAME) \
extern "C" { \
  Standard_EXPORT Handle(WOKMake_Step) STEPMAME##_(const Handle(WOKMake_BuildProcess)&,const Handle(WOKernel_DevUnit)&, const Handle(TCollection_HAsciiString)&, \
				      Standard_Boolean, Standard_Boolean); \
} \
Handle(WOKMake_Step) STEPMAME##_(const Handle(WOKMake_BuildProcess)&aprocess, const Handle(WOKernel_DevUnit)& aunit, const Handle(TCollection_HAsciiString)& acode, \
				     Standard_Boolean checked, Standard_Boolean hidden) \
{return new STEPMAME(aprocess,aunit,acode,checked,hidden);} 


#endif
