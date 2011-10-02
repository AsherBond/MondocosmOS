// File:	WOKTools_Warning.cxx
// Created:	Wed Jun 28 20:21:03 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <WOKTools_Warning.ixx>

Standard_EXPORT WOKTools_Warning WarningMsg();

WOKTools_Warning::WOKTools_Warning() : WOKTools_Message("WOK_WARNING", "Warning : ")
{
  Set();
}

Standard_Character WOKTools_Warning::Code() const 
{return 'W';}
