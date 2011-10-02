// File:	WOKBuilder_MSEntityHasher.cxx
// Created:	Mon Sep 18 15:01:57 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <WOKBuilder_MSEntityHasher.ixx>

#include <WOKTools_HAsciiStringHasher.hxx>
#include <WOKUtils_Path.hxx>

//=======================================================================
//function : HashCode
//purpose  : 
//=======================================================================
Standard_Integer WOKBuilder_MSEntityHasher::HashCode(const Handle(WOKBuilder_MSEntity)& E1)
{
  return WOKTools_HAsciiStringHasher::HashCode(E1->Name());
}

//=======================================================================
//function : IsEqual
//purpose  : 
//=======================================================================
Standard_Boolean  WOKBuilder_MSEntityHasher::IsEqual(const Handle(WOKBuilder_MSEntity)& E1, const Handle(WOKBuilder_MSEntity)& E2)
{
  return E1->Name()->IsSameString(E2->Name());
}
