// File:        TObj_TXYZ.cxx
// Created:     Tue Nov 23 12:57:32 2004
// Author:      Pavel TELKOV
// Copyright:   Open CASCADE  2007
// The original implementation Copyright: (C) RINA S.p.A

#include <TObj_TXYZ.hxx>
#include <Standard_GUID.hxx>
#include <TDF_Label.hxx>

IMPLEMENT_STANDARD_HANDLE(TObj_TXYZ,TDF_Attribute)
IMPLEMENT_STANDARD_RTTIEXT(TObj_TXYZ,TDF_Attribute)

//=======================================================================
//function : TObj_TXYZ
//purpose  : 
//=======================================================================

TObj_TXYZ::TObj_TXYZ()
{
}

//=======================================================================
//function : GetID
//purpose  : 
//=======================================================================

const Standard_GUID& TObj_TXYZ::GetID()
{
  static Standard_GUID theGUID ("3bbefb50-e618-11d4-ba38-0060b0ee18ea");
  return theGUID;
}

//=======================================================================
//function : ID
//purpose  : 
//=======================================================================

const Standard_GUID& TObj_TXYZ::ID() const
{
  return GetID();
}

//=======================================================================
//function : Set
//purpose  : 
//=======================================================================

Handle(TObj_TXYZ) TObj_TXYZ::Set (const TDF_Label& theLabel,
                                          const gp_XYZ& theXYZ)
{
  Handle(TObj_TXYZ) A;
  if (!theLabel.FindAttribute(TObj_TXYZ::GetID(), A))
  {
    A = new TObj_TXYZ;
    theLabel.AddAttribute(A);
  }
  A->Set(theXYZ);
  return A;
}

//=======================================================================
//function : Set
//purpose  : 
//=======================================================================

void TObj_TXYZ::Set (const gp_XYZ& theXYZ)
{
  Backup();
  myXYZ = theXYZ;
}

//=======================================================================
//function : Get
//purpose  : 
//=======================================================================

gp_XYZ TObj_TXYZ::Get () const
{
  return myXYZ;
}

//=======================================================================
//function : NewEmpty
//purpose  : 
//=======================================================================

Handle(TDF_Attribute) TObj_TXYZ::NewEmpty () const
{
  return new TObj_TXYZ();
}

//=======================================================================
//function : Restore
//purpose  : 
//=======================================================================

void TObj_TXYZ::Restore (const Handle(TDF_Attribute)& theWith)
{
  Handle(TObj_TXYZ) R = Handle(TObj_TXYZ)::DownCast(theWith);
  myXYZ = R->Get();
}

//=======================================================================
//function : Paste
//purpose  : 
//=======================================================================

void TObj_TXYZ::Paste (const Handle(TDF_Attribute)& theInto,
                           const Handle(TDF_RelocationTable)& /* RT */) const
{ 
  Handle(TObj_TXYZ) R = Handle(TObj_TXYZ)::DownCast (theInto);
  R->Set(myXYZ);
}

//=======================================================================
//function : Dump
//purpose  : 
//=======================================================================

Standard_OStream& TObj_TXYZ::Dump(Standard_OStream& theOS) const
{
  gp_XYZ aXYZ = Get();
  Standard_OStream& anOS = TDF_Attribute::Dump( theOS );
  anOS << "X: " << aXYZ.X() << "\tY: " << aXYZ.Y() << "\tZ: " << aXYZ.Z();
  return anOS;
}
