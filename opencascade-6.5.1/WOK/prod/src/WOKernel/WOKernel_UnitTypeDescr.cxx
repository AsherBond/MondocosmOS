// File:	WOKernel_UnitTypeDescr.cxx
// Created:	Fri Jun  6 16:56:43 1997
// Author:	Jean GAUTIER
//		<jga@hourax.paris1.matra-dtv.fr>

#include <WOKernel_UnitTypeDescr.ixx>

//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKernel_UnitTypeDescr
//purpose  : 
//=======================================================================
WOKernel_UnitTypeDescr::WOKernel_UnitTypeDescr(const Standard_Character akey,
					       const Handle(TCollection_HAsciiString)& atype) 
: mykey(akey), mytype(atype)
{
}

