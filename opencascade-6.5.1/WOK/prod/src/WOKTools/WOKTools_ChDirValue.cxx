// File:	WOKTools_ChDirValue.cxx
// Created:	Wed Sep 27 18:26:59 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <WOKTools_ChDirValue.ixx>

 WOKTools_ChDirValue::WOKTools_ChDirValue(const Handle(TCollection_HAsciiString)& apath)
{
  SetType(WOKTools_ChDir);
  mypath = apath;
}

void WOKTools_ChDirValue::SetPath(const Handle(TCollection_HAsciiString)& apath)
{
  mypath = apath;
}

Handle(TCollection_HAsciiString) WOKTools_ChDirValue::Path() const 
{
  return mypath;
}

