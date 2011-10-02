// File:	WOKStep_DirectLinkList.cxx
// Created:	Tue Dec  2 18:22:53 1997
// Author:	Jean GAUTIER
//		<jga@hourax.paris1.matra-dtv.fr>

#include <WOKStep_DirectLinkList.ixx>


//=======================================================================
//function : WOKStep_DirectLinkList
//purpose  : 
//=======================================================================
 WOKStep_DirectLinkList::WOKStep_DirectLinkList(const Handle(WOKMake_BuildProcess)& abp,
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
//Handle(TColStd_HSequenceOfHAsciiString) WOKStep_DirectLinkList::ComputeDependency(const Handle(TCollection_HAsciiString)& acode, 
Handle(TColStd_HSequenceOfHAsciiString) WOKStep_DirectLinkList::ComputeDependency(const Handle(TCollection_HAsciiString)& , 
										  const Handle(TColStd_HSequenceOfHAsciiString)& directlist) const
{
  return  directlist;
}
