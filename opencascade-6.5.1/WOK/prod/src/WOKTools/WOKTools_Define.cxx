// File:	WOKTools_Define.cxx
// Created:	Fri Nov 24 14:55:54 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <WOKTools_Define.ixx>

#include <WOKTools_Messages.hxx>

#include <TCollection_HAsciiString.hxx>

#include <Standard_ProgramError.hxx>

//=======================================================================
//function : WOKTools_Define
//purpose  : 
//=======================================================================
 WOKTools_Define::WOKTools_Define()
{}

//=======================================================================
//function : WOKTools_Define
//purpose  : 
//=======================================================================
 WOKTools_Define::WOKTools_Define(const Handle(TCollection_HAsciiString)& aname, 
				  const Handle(TCollection_HAsciiString)& avalue)
: myname(aname), myvalue(avalue)
{
}

//=======================================================================
//function : GetDefineIn
//purpose  : 
//=======================================================================
void WOKTools_Define::GetDefineIn(const Handle(TCollection_HAsciiString)& aline)
{
    // recherche du premier = de la ligne
  Standard_Integer apos = aline->Location(1, '=', 1, aline->Length());
  
  if( apos == 0 )
    {
      // il n'y a pas de = dans la ligne
      ErrorMsg() << "WOKTools_Define::GetDefineIn" << "Missing = in line : " << aline << endm;
      Standard_ProgramError::Raise("WOKTools_Define::GetDefineIn");
    }

  if( apos == 1 )
    {
      // il n'y a pas de nom du parametre dans la ligne
      ErrorMsg() << "WOKTools_Define::GetDefineIn" << "Missing parameter name in line : " << aline << endm;
      Standard_ProgramError::Raise("WOKTools_Define::GetDefineIn");
    }  
  // evaluation de du nom
  myname = aline->SubString(1, apos-1);
  myname->LeftAdjust();
  myname->RightAdjust();
  
  // evaluation de la valeur
  if(apos < aline->Length())
    {
      myvalue = new TCollection_HAsciiString(aline->SubString(apos+1, aline->Length()));

      if(!IsValueValid(myvalue))
	  Standard_ProgramError::Raise("WOKTools_Define::GetDefineIn");
    }
  else
    {
      myvalue = new TCollection_HAsciiString;
    }
  myvalue->LeftAdjust();
}

//=======================================================================
//function : Name
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKTools_Define::Name() const 
{
  return myname;
}

//=======================================================================
//function : Value
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKTools_Define::Value() const 
{
  return myvalue;
}

//=======================================================================
//function : SetName
//purpose  : 
//=======================================================================
void WOKTools_Define::SetName(const Handle(TCollection_HAsciiString)& aname)
{
  myname = aname;
}

//=======================================================================
//function : SetValue
//purpose  : 
//=======================================================================
void WOKTools_Define::SetValue(const Handle(TCollection_HAsciiString)& avalue)
{
  myvalue = avalue;
}

//=======================================================================
//function : AddValue
//purpose  : 
//=======================================================================
void WOKTools_Define::AddValue(const Handle(TCollection_HAsciiString)& aline)
{
  // evaluation d'une autre valeur de l'item

  Handle(TCollection_HAsciiString) othervalue = new TCollection_HAsciiString(aline);
  if(!IsValueValid(othervalue))
    Standard_ProgramError::Raise("WOKTools_Define::AddValue");

  othervalue->LeftAdjust();

  myvalue->AssignCat(" ");
  myvalue->AssignCat(othervalue);
}

//=======================================================================
//function : IsValueValid
//purpose  : 
//=======================================================================
Standard_Boolean WOKTools_Define::IsValueValid(const Handle(TCollection_HAsciiString)& avalue) const
{
  Standard_Integer   i;
  Standard_Character c;
  Standard_Boolean   valid = Standard_True;
  
  for (i=1; i<= avalue->Length() && valid; i++)
    {
      c = avalue->Value(i);
      if (IsEqual(c,'~') || IsEqual(c,'*'))
	valid = Standard_False;
    }
  
  if(!valid)
    {
      ErrorMsg() << "WOKTools_Define::IsValueValid" 
	       << "Parameter value with ~ or * in : " << avalue << " is illegal" << endm;
    }
  return valid;
}
