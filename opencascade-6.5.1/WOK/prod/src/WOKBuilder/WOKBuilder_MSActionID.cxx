// File:	WOKBuilder_MSActionID.cxx
// Created:	Wed Dec 20 18:05:40 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <WOKTools_HAsciiStringHasher.hxx>

#include <WOKBuilder_MSEntity.hxx>

#include <WOKBuilder_MSActionID.ixx>

//=======================================================================
//function : SetName
//purpose  : 
//=======================================================================
void WOKBuilder_MSActionID::SetName(const Handle(TCollection_HAsciiString)& aname )  
{
  myname = aname;
}

//=======================================================================
//function : SetType
//purpose  : 
//=======================================================================
void WOKBuilder_MSActionID::SetType(const WOKBuilder_MSActionType atype) 
{
  mytype = atype;
}

//=======================================================================
//function : IsEqual
//purpose  : 
//=======================================================================
Standard_Boolean WOKBuilder_MSActionID::IsEqual(const WOKBuilder_MSActionID& K1, const WOKBuilder_MSActionID& K2)
{
  if(K1.mytype!=K2.mytype) return Standard_False;
  if(strcmp(K1.Name()->ToCString(), K2.Name()->ToCString())) return Standard_False;
  return Standard_True;
}

//=======================================================================
//function : HashCode
//purpose  : 
//=======================================================================
Standard_Integer WOKBuilder_MSActionID::HashCode(const WOKBuilder_MSActionID& K)
{
  return WOKTools_HAsciiStringHasher::HashCode(K.Name()) + K.mytype;
}

//=======================================================================
//function : IsEqual
//purpose  : 
//=======================================================================
Standard_Boolean WOKBuilder_MSActionID::IsEqual(const Handle(WOKBuilder_MSAction)& K1, const Handle(WOKBuilder_MSAction)& K2)
{
  if(K1->Type()!=K2->Type()) return Standard_False;
  if(strcmp(K1->Entity()->Name()->ToCString(), K2->Entity()->Name()->ToCString())) return Standard_False;
  return Standard_True;
}

//=======================================================================
//function : HashCode
//purpose  : 
//=======================================================================
Standard_Integer WOKBuilder_MSActionID::HashCode(const Handle(WOKBuilder_MSAction)& K)
{
  return WOKTools_HAsciiStringHasher::HashCode(K->Entity()->Name()) + K->Type();
}

