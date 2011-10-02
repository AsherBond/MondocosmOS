// Copyright: 	Matra-Datavision 1996
// File:	WOKDeliv_DeliveryArchive.cxx
// Created:	Tue Aug 13 14:33:27 1996
// Author:	Arnaud BOUZY
//		<adn>


#include <WOKDeliv_DeliveryArchive.ixx>
#include <WOKDeliv_ParseDelivery.hxx>
#include <WOKMake_HSequenceOfOutputFile.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_BasicUnitTypes.hxx>
#include <WOKUtils_Path.hxx>

WOKDeliv_DeliveryArchive::WOKDeliv_DeliveryArchive(const Handle(WOKMake_BuildProcess)& aprocess,
						   const Handle(WOKernel_DevUnit)& aunit,
						   const Handle(TCollection_HAsciiString)& acode,
						   const Standard_Boolean checked,
						   const Standard_Boolean hidden)
  : WOKDeliv_DeliveryLIB(aprocess,aunit,acode,checked,hidden)
{
}

void WOKDeliv_DeliveryArchive::SetList()
{
  myList = ParseCOMPONENTS(T_ARCHIVE);
}

Standard_Boolean WOKDeliv_DeliveryArchive::NeedsObjects() const
{
  return Standard_True;
}


//void WOKDeliv_DeliveryArchive::ComputeOutputLIB(const Handle(WOKernel_DevUnit)& thesourceunit,
//					       const Handle(WOKMake_InputFile)& inCOMP)
void WOKDeliv_DeliveryArchive::ComputeOutputLIB(const Handle(WOKernel_DevUnit)& ,
					       const Handle(WOKMake_InputFile)& )
{
}


Standard_Boolean WOKDeliv_DeliveryArchive::IsAvailable(const Handle(WOKernel_DevUnit)& asource) const
{
  if (WOKernel_IsPackage(asource)) return Standard_True;
  if (WOKernel_IsNocdlpack(asource)) return Standard_True;
  if (WOKernel_IsSchema(asource)) return Standard_True;
  return Standard_False;
}
