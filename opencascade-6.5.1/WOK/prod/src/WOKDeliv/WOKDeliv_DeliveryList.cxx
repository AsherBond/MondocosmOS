// File:	WOKDeliv_DeliveryList.cxx
// Created:	Fri Mar 29 11:10:15 1996
// Author:	Arnaud BOUZY
//		<adn>


#include <WOKDeliv_DeliveryList.ixx>

WOKDeliv_DeliveryList::WOKDeliv_DeliveryList(const Standard_Integer aDelivStep):myStep(aDelivStep),myPutPath(Standard_False),myPutLib(Standard_False),myPutInclude(Standard_False)
{
}

Standard_Integer WOKDeliv_DeliveryList::GetStep() const
{
  return myStep;
}

WOKTools_MapOfHAsciiString& WOKDeliv_DeliveryList::ChangeMap()
{
  return myMap;
}

const WOKTools_MapOfHAsciiString& WOKDeliv_DeliveryList::GetMap() const
{
  return myMap;
}

WOKTools_MapOfHAsciiString& WOKDeliv_DeliveryList::ChangeRequireMap()
{
  return myReqMap;
}

const WOKTools_MapOfHAsciiString& WOKDeliv_DeliveryList::GetRequireMap() const
{
  return myReqMap;
}

void WOKDeliv_DeliveryList::SetName(const Standard_CString name)
{
  myName = new TCollection_HAsciiString(name);
}

Handle(TCollection_HAsciiString) WOKDeliv_DeliveryList::GetName() const
{
  return myName;
}

