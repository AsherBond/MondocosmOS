// File:	WOKernel_FileTypeHasher.cxx
// Created:	Tue Oct 10 20:11:36 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <WOKernel_FileTypeHasher.ixx>


//=======================================================================
//function : HashCode
//purpose  : 
//=======================================================================
Standard_Integer WOKernel_FileTypeHasher::HashCode(const Handle(WOKernel_FileType)& atype)
{
  Standard_Integer aHashCode =  (Standard_Integer ) (long) atype.operator->();
  return aHashCode;
}

Standard_Boolean WOKernel_FileTypeHasher::IsEqual(const Handle(WOKernel_FileType)& K1,
						  const Handle(WOKernel_FileType)& K2)
{
  if(K1.operator->() == K2.operator->()) return Standard_True;
  return Standard_False;
}
