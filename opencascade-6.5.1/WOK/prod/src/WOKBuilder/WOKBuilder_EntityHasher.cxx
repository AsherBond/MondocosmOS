// File:	WOKBuilder_EntityHasher.cxx
// Created:	Mon Sep 11 17:03:43 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <WOKBuilder_EntityHasher.ixx>

#include <WOKTools_HAsciiStringHasher.hxx>
#include <WOKUtils_Path.hxx>

//=======================================================================
//function : HashCode
//purpose  : 
//=======================================================================
Standard_Integer WOKBuilder_EntityHasher::HashCode(const Handle(WOKBuilder_Entity)& E1)
{
  return WOKTools_HAsciiStringHasher::HashCode(E1->Path()->Name());
}

//=======================================================================
//function : IsEqual
//purpose  : 
//=======================================================================
Standard_Boolean  WOKBuilder_EntityHasher::IsEqual(const Handle(WOKBuilder_Entity)& E1, const Handle(WOKBuilder_Entity)& E2)
{
  return E1->Path()->Name()->IsSameString(E2->Path()->Name());
}
