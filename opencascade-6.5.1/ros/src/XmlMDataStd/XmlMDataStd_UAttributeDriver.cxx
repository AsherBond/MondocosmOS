// File:        XmlMDataStd_UAttributeDriver.cxx
// Created:     Fri Aug 24 20:46:58 2001
// Author:      Alexnder GRIGORIEV
// Copyright:   Open Cascade 2001
// History:

#include <XmlMDataStd_UAttributeDriver.ixx>
#include <TDataStd_UAttribute.hxx>

IMPLEMENT_DOMSTRING (GuidString, "guid")

//=======================================================================
//function : XmlMDataStd_UAttributeDriver
//purpose  : Constructor
//=======================================================================

XmlMDataStd_UAttributeDriver::XmlMDataStd_UAttributeDriver
                        (const Handle(CDM_MessageDriver)& theMsgDriver)
      : XmlMDF_ADriver (theMsgDriver, NULL)
{}

//=======================================================================
//function : NewEmpty
//purpose  : 
//=======================================================================
Handle(TDF_Attribute) XmlMDataStd_UAttributeDriver::NewEmpty() const
{
  return (new TDataStd_UAttribute());
}

//=======================================================================
//function : Paste
//purpose  : 
//=======================================================================
Standard_Boolean XmlMDataStd_UAttributeDriver::Paste
                                (const XmlObjMgt_Persistent&  theSource,
                                 const Handle(TDF_Attribute)& theTarget,
                                 XmlObjMgt_RRelocationTable&  ) const
{
  XmlObjMgt_DOMString aGuidDomStr =
    theSource.Element().getAttribute (::GuidString());
  Standard_CString aGuidStr = (Standard_CString)aGuidDomStr.GetString();
  if (aGuidStr[0] == '\0') {
    WriteMessage ("error retrieving GUID for type TDataStd_UAttribute");
    return Standard_False;
  }

  Handle(TDataStd_UAttribute)::DownCast (theTarget) -> SetID (aGuidStr);
  return Standard_True;
}


//=======================================================================
//function : Paste
//purpose  : 
//=======================================================================
void XmlMDataStd_UAttributeDriver::Paste(const Handle(TDF_Attribute)& theSource,
                                         XmlObjMgt_Persistent&        theTarget,
                                         XmlObjMgt_SRelocationTable&  ) const
{
  Handle(TDataStd_UAttribute) aName =
    Handle(TDataStd_UAttribute)::DownCast(theSource);

  //convert GUID into attribute value
  Standard_Character aGuidStr [40];
  Standard_PCharacter pGuidStr;
  pGuidStr=aGuidStr;
  aName->ID().ToCString (pGuidStr);

  theTarget.Element().setAttribute (::GuidString(), aGuidStr);
}
