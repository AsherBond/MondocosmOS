// File:	WOKMake_StepConstructPtr.hxx
// Created:	Thu Jun 27 14:01:54 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#ifndef WOKMake_StepConstructPtr_HeaderFile
#define WOKMake_StepConstrucPtr_HeaderFile

#include <TCollection_HAsciiString.hxx>

#include <WOKernel_DevUnit.hxx>

#include <WOKMake_Step.hxx>


typedef Handle(WOKMake_Step) (*WOKMake_StepConstructPtr) (const Handle(WOKMake_BuildProcess)&,
							  const Handle(WOKernel_DevUnit)&,
							  const Handle(TCollection_HAsciiString)&,
							  Standard_Boolean , Standard_Boolean );


#endif
