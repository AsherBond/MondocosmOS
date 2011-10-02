// Copyright: 	Matra-Datavision 1997
// File:	WOKTools_CompareOfHAsciiString.cxx
// Created:	Thu Jan 30 18:02:41 1997
// Author:	Arnaud BOUZY
//		<adn>

#include <WOKTools_CompareOfHAsciiString.ixx>

WOKTools_CompareOfHAsciiString::WOKTools_CompareOfHAsciiString()
{
}

Standard_Boolean WOKTools_CompareOfHAsciiString::IsLower(const Handle(TCollection_HAsciiString)& Left,const Handle(TCollection_HAsciiString)& Right) const
{
  return Left->IsLess(Right);
}

Standard_Boolean WOKTools_CompareOfHAsciiString::IsGreater(const Handle(TCollection_HAsciiString)& Left,const Handle(TCollection_HAsciiString)& Right) const
{
  return Left->IsGreater(Right);
}

Standard_Boolean WOKTools_CompareOfHAsciiString::IsEqual(const Handle(TCollection_HAsciiString)& Left,const Handle(TCollection_HAsciiString)& Right) const
{
  return Left->IsSameString(Right);
}

