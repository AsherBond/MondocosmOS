// File:	WOKTools_EnvValue.cxx
// Created:	Wed Sep 27 18:28:14 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <WOKTools_EnvValue.ixx>

//=======================================================================
//function : WOKTools_EnvValue
//purpose  : To set in env
//=======================================================================
 WOKTools_EnvValue::WOKTools_EnvValue(const Handle(TCollection_HAsciiString)& avarname, const Handle(TCollection_HAsciiString)& avalue)
{
  SetType(WOKTools_Environment);
  myname  = avarname;
  myvalue = avalue;
  myflag  = Standard_True;
}

//=======================================================================
//function : WOKTools_EnvValue
//purpose  : to unset in env
//=======================================================================
 WOKTools_EnvValue::WOKTools_EnvValue(const Handle(TCollection_HAsciiString)& avarname)
{
  SetType(WOKTools_Environment);
  myname  = avarname;
  myflag  = Standard_False;
}

//=======================================================================
//function : SetName
//purpose  : 
//=======================================================================
void WOKTools_EnvValue::SetName(const Handle(TCollection_HAsciiString)& aname)
{
  myname  = aname;
}

//=======================================================================
//function : Name
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKTools_EnvValue::Name() const 
{
  return myname;
}

//=======================================================================
//function : SetValue
//purpose  : 
//=======================================================================
void WOKTools_EnvValue::SetValue(const Handle(TCollection_HAsciiString)& avalue)
{
  myvalue = avalue;
}

//=======================================================================
//function : Value
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKTools_EnvValue::Value() const 
{
  return myvalue;
}

//=======================================================================
//function : ToSet
//purpose  : 
//=======================================================================
Standard_Boolean WOKTools_EnvValue::ToSet() const
{
  return myflag;
}
