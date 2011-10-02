// File:	WOKStep_TransitiveLinkList.cxx
// Created:	Tue Dec  2 18:19:36 1997
// Author:	Jean GAUTIER
//		<jga@hourax.paris1.matra-dtv.fr>


#include <WOKStep_TransitiveLinkList.ixx>


//=======================================================================
//function : WOKStep_TransitiveLinkList
//purpose  : 
//=======================================================================
 WOKStep_TransitiveLinkList::WOKStep_TransitiveLinkList(const Handle(WOKMake_BuildProcess)& abp,
							const Handle(WOKernel_DevUnit)& aunit, 
							const Handle(TCollection_HAsciiString)& acode,
							const Standard_Boolean checked, 
							const Standard_Boolean hidden) 
   : WOKStep_ComputeLinkList(abp,aunit, acode, checked, hidden)
{ 
}


//=======================================================================
//function : ComputeDependency
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKStep_TransitiveLinkList::ComputeDependency(const Handle(TCollection_HAsciiString)& acode, 
										      const Handle(TColStd_HSequenceOfHAsciiString)& directlist) const
{
  return WOKernel_DevUnit::ImplementationDep(UnitGraph(), acode, directlist);
}
