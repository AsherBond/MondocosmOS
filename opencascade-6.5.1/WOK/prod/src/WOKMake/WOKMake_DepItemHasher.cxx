// File:	WOKMake_DepItemHasher.cxx
// Created:	Fri Oct  3 16:15:43 1997
// Author:	Jean GAUTIER
//		<jga@hourax.paris1.matra-dtv.fr>


#include <TCollection_HAsciiString.hxx>


#include <WOKTools_HAsciiStringHasher.hxx>

#include <WOKMake_DepItemHasher.ixx>


Standard_Integer WOKMake_DepItemHasher::HashCode (const Handle(WOKMake_DepItem) &Value)
{
  return 
    (WOKTools_HAsciiStringHasher::HashCode(Value->IssuedFrom()) + 
     WOKTools_HAsciiStringHasher::HashCode(Value->OutputFile()));
}

Standard_Boolean WOKMake_DepItemHasher::IsEqual(const Handle(WOKMake_DepItem) &K1, const Handle(WOKMake_DepItem) &K2)
{
  if (!strcmp(K1->IssuedFrom()->ToCString(), K2->IssuedFrom()->ToCString()) &&
      !strcmp(K1->OutputFile()->ToCString(), K2->OutputFile()->ToCString()))
    return Standard_True;
  else 
    return Standard_False;
}

