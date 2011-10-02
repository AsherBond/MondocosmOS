// File:	WOKDeliv_ParseDelivery.hxx
// Created:	Fri Mar 29 11:15:48 1996
// Author:	Arnaud BOUZY
//		<adn>


#ifndef _WOKDeliv_ParseDelivery_HeaderFile
#define _WOKDeliv_ParseDelivery_HeaderFile

#include <WOKDeliv_DeliveryList.hxx>
#include <DELIVERY.tab.h>
#define T_BASE T_ALPHA
Standard_Boolean WOKDeliv_Delivery_SetFile(char* filename);
void WOKDeliv_Delivery_CloseFile();
Handle(WOKDeliv_DeliveryList) WOKDeliv_Delivery_Parse(int aStep);

#endif
