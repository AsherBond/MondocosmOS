// File:      XmlMDF_ReferenceDriver.cxx
// Created:   04.09.01 14:47:31
// Author:    Julia DOROVSKIKH
// Copyright: Open Cascade 2001
// History:   AGV 150202: Changed prototype XmlObjMgt::SetStringValue()

#include <XmlMDF_ReferenceDriver.ixx>
#include <XmlObjMgt.hxx>

#include <TDF_Reference.hxx>
#include <TDF_Tool.hxx>

//=======================================================================
//function : XmlMDF_ReferenceDriver
//purpose  : Constructor
//=======================================================================
XmlMDF_ReferenceDriver::XmlMDF_ReferenceDriver
                        (const Handle(CDM_MessageDriver)& theMsgDriver)
      : XmlMDF_ADriver (theMsgDriver, NULL)
{}

//=======================================================================
//function : NewEmpty
//purpose  : 
//=======================================================================
Handle(TDF_Attribute) XmlMDF_ReferenceDriver::NewEmpty() const
{
  return (new TDF_Reference());
}

//=======================================================================
//function : Paste
//purpose  : persistent -> transient (retrieve)
//=======================================================================
Standard_Boolean XmlMDF_ReferenceDriver::Paste
                (const XmlObjMgt_Persistent&   theSource,
                 const Handle(TDF_Attribute)&  theTarget,
                 XmlObjMgt_RRelocationTable&   ) const
{
  XmlObjMgt_DOMString anXPath = XmlObjMgt::GetStringValue(theSource);

  if (anXPath == NULL)
  {
    WriteMessage ("Cannot retrieve reference string from element");
    return Standard_False;
  }

  TCollection_AsciiString anEntry;
  if (XmlObjMgt::GetTagEntryString (anXPath, anEntry) == Standard_False)
  {
    TCollection_ExtendedString aMessage =
      TCollection_ExtendedString ("Cannot retrieve reference from \"")
        + anXPath + '\"';
    WriteMessage (aMessage);
    return Standard_False;
  }

  Handle(TDF_Reference) aRef = Handle(TDF_Reference)::DownCast(theTarget);

  // find label by entry
  TDF_Label tLab; // Null label.
  if (anEntry.Length() > 0)
  {
    TDF_Tool::Label(aRef->Label().Data(), anEntry, tLab, Standard_True);
  }

  // set referenced label
  aRef->Set(tLab);

  return Standard_True;
}

//=======================================================================
//function : Paste
//purpose  : transient -> persistent (store)
//           <label tag='1'>     <This is label entry 0:4:1>
//           ...
//           <label tag='8'>     <This is label entry 0:4:1:8>
//
//           <TDF_Reference id="621"> /document/label/label[@tag="4"]/label[@tag="1"]
//           </TDF_Reference>    <This is reference to label 0:4:1>
//=======================================================================
void XmlMDF_ReferenceDriver::Paste (const Handle(TDF_Attribute)&  theSource,
                                    XmlObjMgt_Persistent&         theTarget,
                                    XmlObjMgt_SRelocationTable&   ) const
{
  Handle(TDF_Reference) aRef = Handle(TDF_Reference)::DownCast(theSource);
  if (!aRef.IsNull())
  {
    const TDF_Label& lab = aRef->Label();
    const TDF_Label& refLab = aRef->Get();
    if (!lab.IsNull() && !refLab.IsNull())
    {
      if (lab.IsDescendant(refLab.Root()))
      {
        // Internal reference
        TCollection_AsciiString anEntry;
        TDF_Tool::Entry(refLab, anEntry);

        XmlObjMgt_DOMString aDOMString;
        XmlObjMgt::SetTagEntryString (aDOMString, anEntry);
        // No occurrence of '&', '<' and other irregular XML characters
        XmlObjMgt::SetStringValue (theTarget, aDOMString, Standard_True);
      }
    }
  }
}
