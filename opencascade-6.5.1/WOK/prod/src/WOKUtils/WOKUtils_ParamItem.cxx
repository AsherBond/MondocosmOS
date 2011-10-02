// File:	WOKUtils_ParamItem.cxx
// Created:	Tue May 30 09:19:37 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <WOKUtils_ParamItem.ixx>
#include <WOKTools_Messages.hxx>

//=======================================================================
//function : WOKUtils_ParamItem
//purpose  : 
//=======================================================================
WOKUtils_ParamItem::WOKUtils_ParamItem()
{
  
}

//=======================================================================
//function : WOKUtils_ParamItem
//purpose  : 
//=======================================================================
WOKUtils_ParamItem::WOKUtils_ParamItem(const Handle(TCollection_HAsciiString)& aname, const Handle(TCollection_HAsciiString)& avalue)
{
  myname = aname; myvalue = avalue;
}

//=======================================================================
//function : WOKUtils_ParamItem
//purpose  : 
//=======================================================================
WOKUtils_ParamItem::WOKUtils_ParamItem(const Standard_CString aname, const Standard_CString avalue)
{
  myname  = new TCollection_HAsciiString(aname);
  myvalue = new TCollection_HAsciiString(avalue);
}

//=======================================================================
//function : SetName
//purpose  : 
//=======================================================================
void WOKUtils_ParamItem::SetName(const Handle(TCollection_HAsciiString)& aname)
{
  myname = aname;
}

//=======================================================================
//function : SetValue
//purpose  : 
//=======================================================================
void WOKUtils_ParamItem::SetValue(const Handle(TCollection_HAsciiString)& avalue)
{
  myvalue = avalue;
}

//=======================================================================
//function : Name
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKUtils_ParamItem::Name() const 
{
  return myname;
}

//=======================================================================
//function : Value
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKUtils_ParamItem::Value() const 
{
  return myvalue;
}

