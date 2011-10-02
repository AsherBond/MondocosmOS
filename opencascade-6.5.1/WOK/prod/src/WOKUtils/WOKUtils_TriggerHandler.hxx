// File:	WOKUtils_TriggerHandler.hxx
// Created:	Fri Oct 27 19:13:11 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#ifndef WOKUtils_TriggerHandler_HeaderFile
#define WOKUtils_TriggerHandler_HeaderFile

#include <Standard_Type.hxx>

#include <TCollection_HAsciiString.hxx>

#include <WOKTools_Return.hxx>

#include <WOKUtils_TriggerStatus.hxx>

class WOKUtils_Trigger;

typedef WOKUtils_TriggerStatus (*WOKUtils_TriggerHandler)(WOKUtils_Trigger& );

#endif
