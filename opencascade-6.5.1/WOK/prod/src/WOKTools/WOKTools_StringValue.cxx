// File:	WOKTools_StringValue.cxx
// Created:	Wed Sep 27 18:30:07 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <WOKTools_StringValue.ixx>

 WOKTools_StringValue::WOKTools_StringValue(const Handle(TCollection_HAsciiString)& astr)
{
  SetType(WOKTools_String);
  myvalue = astr;
}

Handle(TCollection_HAsciiString) WOKTools_StringValue::Value() const 
{
  return myvalue;
}

void WOKTools_StringValue::SetValue(const Handle(TCollection_HAsciiString)& avalue)
{
  myvalue = avalue;
}

