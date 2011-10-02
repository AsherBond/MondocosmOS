// File:	WOKUtils_TriggerControl.hxx
// Created:	Thu Nov 14 14:39:02 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#ifndef WOKUtils_TriggerControl_HeaderFile
#define WOKUtils_TriggerControl_HeaderFile



class WOKUtils_Trigger;

#include <Standard_Type.hxx>

const Handle(Standard_Type)& STANDARD_TYPE(WOKUtils_TriggerControl);

typedef  WOKUtils_Trigger& (*WOKUtils_TriggerControl)(WOKUtils_Trigger &);


#endif
