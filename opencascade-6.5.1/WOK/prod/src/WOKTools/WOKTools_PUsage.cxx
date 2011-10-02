// File:	WOKAPI_PUsage.cxx
// Created:	Wed Aug  2 13:49:45 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#ifndef _WOKTools_PUsage_HeaderFile
#include <WOKTools_PUsage.hxx>
#endif


const Handle(Standard_Type)& TYPE(WOKTools_PUsage)
{
  static Handle(Standard_Type) _aType = new Standard_Type("WOKTools_PUsage",sizeof(WOKTools_PUsage));
  
  return _aType;
}
