// File:	WOKDeliv_DeliverySource.cxx
// Created:	Wed Mar 20 16:43:50 1996
// Author:	Arnaud BOUZY
//		<adn>


#include <WOKDeliv_DeliverySource.ixx>

WOKDeliv_DeliverySource::WOKDeliv_DeliverySource(const Handle(WOKMake_BuildProcess)& aprocess,
						 const Handle(WOKernel_DevUnit)& aunit, 
						 const Handle(TCollection_HAsciiString)& acode,
						 const Standard_Boolean checked,
						 const Standard_Boolean hidden ) 
  : WOKStep_Source(aprocess,aunit,acode,checked,hidden)
{
}
