// File:	WOKTools_MsgControl.hxx
// Created:	Thu Oct 24 14:07:52 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#ifndef WOKTools_MsgControl_HeaderFile
#define WOKTools_MsgControl_HeaderFile


class WOKTools_Message;

#include <Standard_Type.hxx>

const Handle(Standard_Type)& STANDARD_TYPE(WOKTools_MsgControl);

typedef  WOKTools_Message& (*WOKTools_MsgControl)(WOKTools_Message &);


#endif
