// File:	WOKStep_TransitiveTKReplace.cxx
// Created:	Tue Dec  2 18:19:36 1997
// Author:	Jean GAUTIER
//		<jga@hourax.paris1.matra-dtv.fr>


#include <WOKStep_TransitiveTKReplace.ixx>


//=======================================================================
//function : WOKStep_TransitiveTKReplace
//purpose  : 
//=======================================================================
 WOKStep_TransitiveTKReplace::WOKStep_TransitiveTKReplace(const Handle(WOKMake_BuildProcess)& abp,
							const Handle(WOKernel_DevUnit)& aunit, 
							const Handle(TCollection_HAsciiString)& acode,
							const Standard_Boolean checked, 
							const Standard_Boolean hidden) 
   : WOKStep_TKReplace(abp,aunit, acode, checked, hidden)
{ 
}


//=======================================================================
//function : ComputeDependency
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKStep_TransitiveTKReplace::ComputeDependency(const Handle(TCollection_HAsciiString)& acode, 
										      const Handle(TColStd_HSequenceOfHAsciiString)& directlist) const
{
  return WOKernel_DevUnit::ImplementationDep(UnitGraph(), acode, directlist);
}

