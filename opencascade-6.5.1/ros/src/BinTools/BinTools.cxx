// File:	BinTools.cxx
// Created:	Tue May 18 17:00:25 2004
// Author:	Sergey ZARITCHNY <szy@opencascade.com>
// Copyright:	Open CasCade S.A. 2004

#include <BinTools.ixx>
#include <FSD_FileHeader.hxx>
#include <Storage_StreamTypeMismatchError.hxx>

//=======================================================================
//function : PutBool
//purpose  : 
//=======================================================================

Standard_OStream& BinTools::PutBool(Standard_OStream& OS, const Standard_Boolean aValue)
{
  OS.put((Standard_Byte)aValue);
  return OS;
}


//=======================================================================
//function : PutInteger
//purpose  : 
//=======================================================================

Standard_OStream& BinTools::PutInteger(Standard_OStream& OS, const Standard_Integer aValue)
{
  Standard_Integer anIntValue = aValue;
#if DO_INVERSE
      anIntValue = InverseInt (aValue);
#endif
  OS.write((char*)&anIntValue, sizeof(Standard_Integer));  
  return OS;
}


//=======================================================================
//function : PutReal
//purpose  : 
//=======================================================================

Standard_OStream& BinTools::PutReal(Standard_OStream& OS, const Standard_Real aValue)
{
  Standard_Real aRValue = aValue;
#if DO_INVERSE
      aRValue = InverseReal (aValue);
#endif
  OS.write((char*)&aRValue, sizeof(Standard_Real));  
  return OS;
}

//=======================================================================
//function : PutExtChar
//purpose  : 
//=======================================================================

Standard_OStream& BinTools::PutExtChar(Standard_OStream& OS, const Standard_ExtCharacter aValue)
{
  Standard_ExtCharacter aSValue = aValue;
#if DO_INVERSE
      aSValue = InverseExtChar (aValue);
#endif
  OS.write((char*)&aSValue, sizeof(Standard_ExtCharacter));  
  return OS;
}
//=======================================================================
//function : GetReal
//purpose  : 
//=======================================================================

Standard_IStream& BinTools::GetReal(Standard_IStream& IS, Standard_Real& aValue)
{
  if(!IS.read ((char*)&aValue, sizeof(Standard_Real)))
    Storage_StreamTypeMismatchError::Raise();
#if DO_INVERSE
  aValue = InverseReal (aValue);
#endif
  return IS;
}

//=======================================================================
//function : GetInteger
//purpose  : 
//=======================================================================

Standard_IStream& BinTools::GetInteger(Standard_IStream& IS, Standard_Integer& aValue)
{
  if(!IS.read ((char*)&aValue, sizeof(Standard_Integer)))
    Storage_StreamTypeMismatchError::Raise();;
#if DO_INVERSE
  aValue = InverseInt (aValue);
#endif
  return IS;
}

//=======================================================================
//function : GetExtChar
//purpose  : 
//=======================================================================

Standard_IStream& BinTools::GetExtChar(Standard_IStream& IS, Standard_ExtCharacter& theValue)
{
  if(!IS.read ((char*)&theValue, sizeof(Standard_ExtCharacter)))
    Storage_StreamTypeMismatchError::Raise();;
#if DO_INVERSE
  theValue = InverseExtChar (theValue);
#endif
  return IS;
}

//=======================================================================
//function : GetBool
//purpose  : 
//=======================================================================

Standard_IStream& BinTools::GetBool(Standard_IStream& IS, Standard_Boolean& aValue)
{
  aValue = (Standard_Boolean)IS.get();
  return IS;
}
