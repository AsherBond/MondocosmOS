// File:	WOKTools_MsgHandler.hxx
// Created:	Tue Oct 17 16:35:18 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#ifndef WOKTools_MsgHandler_HeaderFile
#define WOKTools_MsgHandler_HeaderFile

class WOKTools_Message;

#include <Standard_Type.hxx>

const Handle(Standard_Type)& STANDARD_TYPE(WOKTools_MsgHandler);

typedef  WOKTools_Message& (*WOKTools_MsgHandler)(WOKTools_Message &, const Standard_Boolean newline);

#endif
