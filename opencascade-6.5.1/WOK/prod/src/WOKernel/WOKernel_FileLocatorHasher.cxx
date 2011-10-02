// File:	WOKernel_FileLocatorHasher.cxx
// Created:	Thu Apr 25 21:06:51 1996
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <TCollection_HAsciiString.hxx>

#include <WOKTools_HAsciiStringHasher.hxx>

#include <WOKernel_File.hxx>

#include <WOKernel_FileLocatorHasher.ixx>


//=======================================================================
//function : HashCode
//purpose  : 
//=======================================================================
Standard_Integer WOKernel_FileLocatorHasher::HashCode(const Handle(WOKernel_File)& akey)
{
  return WOKTools_HAsciiStringHasher::HashCode(akey->LocatorName());
}

//=======================================================================
//function : IsEqual
//purpose  : 
//=======================================================================
Standard_Boolean WOKernel_FileLocatorHasher::IsEqual(const Handle(WOKernel_File)& akey1, const Handle(WOKernel_File)& akey2)
{
  if (strcmp(akey1->LocatorName()->ToCString(), akey2->LocatorName()->ToCString()) == 0) return(Standard_True);
  else                                                                                   return(Standard_False);
}
