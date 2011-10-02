// File:	WOKStep_DirectTKReplace.cxx
// Created:	Tue Dec  2 18:22:53 1997
// Author:	Jean GAUTIER
//		<jga@hourax.paris1.matra-dtv.fr>

#include <WOKStep_DirectTKReplace.ixx>


//=======================================================================
//function : WOKStep_DirectTKReplace
//purpose  : 
//=======================================================================
 WOKStep_DirectTKReplace::WOKStep_DirectTKReplace(const Handle(WOKMake_BuildProcess)& abp,
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
Handle(TColStd_HSequenceOfHAsciiString) WOKStep_DirectTKReplace::ComputeDependency(
//    const Handle(TCollection_HAsciiString)& acode,
    const Handle(TCollection_HAsciiString)& ,
//    const Handle(TColStd_HSequenceOfHAsciiString)& directlist) const
    const Handle(TColStd_HSequenceOfHAsciiString)& ) const
{
  Handle(TColStd_HSequenceOfHAsciiString) ret = new TColStd_HSequenceOfHAsciiString();
  return  ret;
}
