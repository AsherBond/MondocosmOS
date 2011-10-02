// File:	WOKTools_ReturnValue.cxx
// Created:	Wed Sep 27 18:26:20 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <WOKTools_ReturnValue.ixx>

 WOKTools_ReturnValue::WOKTools_ReturnValue()
{
}

WOKTools_ReturnType WOKTools_ReturnValue::Type() const 
{
  return mytype;
}

void WOKTools_ReturnValue::SetType(const WOKTools_ReturnType atype)
{
  mytype = atype;
}

