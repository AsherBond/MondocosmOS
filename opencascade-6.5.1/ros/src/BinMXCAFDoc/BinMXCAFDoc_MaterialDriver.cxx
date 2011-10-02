// File:      BinMXCAFDoc_MaterialDriver.cxx
// Created:   10.12.08 13:15:38
// Author:    Pavel TELKOV
// Copyright: Open CASCADE 2008

#include <BinMXCAFDoc_MaterialDriver.ixx>
#include <XCAFDoc_Material.hxx>

#include <TCollection_HAsciiString.hxx>

//=======================================================================
//function : Constructor
//purpose  : 
//=======================================================================
BinMXCAFDoc_MaterialDriver::BinMXCAFDoc_MaterialDriver
  (const Handle(CDM_MessageDriver)& theMsgDriver)
: BinMDF_ADriver(theMsgDriver, STANDARD_TYPE(XCAFDoc_Material)->Name())
{
}

//=======================================================================
//function : NewEmpty
//purpose  : 
//=======================================================================
Handle(TDF_Attribute) BinMXCAFDoc_MaterialDriver::NewEmpty() const
{
  return new XCAFDoc_Material();
}

//=======================================================================
//function : Paste
//purpose  : 
//=======================================================================
Standard_Boolean BinMXCAFDoc_MaterialDriver::Paste(const BinObjMgt_Persistent& theSource,
                                                 const Handle(TDF_Attribute)& theTarget,
                                                 BinObjMgt_RRelocationTable& /*theRelocTable*/) const 
{
  Handle(XCAFDoc_Material) anAtt = Handle(XCAFDoc_Material)::DownCast(theTarget);
  Standard_Real aDensity;
  TCollection_AsciiString aName, aDescr, aDensName, aDensValType;
  if ( !(theSource >> aName >> aDescr >> aDensity >> aDensName >> aDensValType) )
    return Standard_False;

  anAtt->Set(new TCollection_HAsciiString( aName ),
             new TCollection_HAsciiString( aDescr ),
             aDensity,
             new TCollection_HAsciiString( aDensName ),
             new TCollection_HAsciiString( aDensValType ));
  return Standard_True;
}

static void pasteString( BinObjMgt_Persistent& theTarget,
                         Handle(TCollection_HAsciiString) theStr )
{
  if ( !theStr.IsNull() )
    theTarget << theStr->String();
  else
    theTarget << TCollection_AsciiString("");
}

//=======================================================================
//function : Paste
//purpose  : 
//=======================================================================
void BinMXCAFDoc_MaterialDriver::Paste(const Handle(TDF_Attribute)& theSource,
                                     BinObjMgt_Persistent& theTarget,
                                     BinObjMgt_SRelocationTable& /*theRelocTable*/) const
{
  Handle(XCAFDoc_Material) anAtt = Handle(XCAFDoc_Material)::DownCast(theSource);
  pasteString( theTarget, anAtt->GetName() );
  pasteString( theTarget, anAtt->GetDescription() );
  theTarget << anAtt->GetDensity();
  pasteString( theTarget, anAtt->GetDensName() );
  pasteString( theTarget, anAtt->GetDensValType() );
}
