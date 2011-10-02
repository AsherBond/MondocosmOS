// File:	WOKernel_EntityHasher.cxx
// Created:	Thu Jun 29 17:10:20 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <WOKernel_BaseEntityHasher.ixx>

#include <WOKTools_HAsciiStringHasher.hxx>

#include <TCollection_HAsciiString.hxx>

Standard_Integer WOKernel_BaseEntityHasher::HashCode(const Handle(WOKernel_BaseEntity)& akey)
{
  return WOKTools_HAsciiStringHasher::HashCode(akey->FullName());
}

Standard_Boolean WOKernel_BaseEntityHasher::IsEqual(const Handle(WOKernel_BaseEntity)& akey1, const Handle(WOKernel_BaseEntity)& akey2)
{
  if (strcmp(akey1->FullName()->ToCString(), akey2->FullName()->ToCString()) == 0) return(Standard_True);
  else                                                                             return(Standard_False);
}

