// File:	TNaming_Scope.cxx
// Created:	Wed Nov  3 17:23:44 1999
// Author:	Denis PASCAL
//		<dp@dingox.paris1.matra-dtv.fr>


#include <TNaming_Scope.ixx>

#include <TDF_Label.hxx>
#include <TDF_ChildIterator.hxx>
#include <TNaming_Tool.hxx>

//=======================================================================
//function : TNaming_Scope
//purpose  : 
//=======================================================================

TNaming_Scope::TNaming_Scope () : myWithValid(Standard_False)
{
}

//=======================================================================
//function : TNaming_Scope
//purpose  : 
//=======================================================================

TNaming_Scope::TNaming_Scope (TDF_LabelMap& map)
{ 
  myWithValid = Standard_True;
  myValid = map;
}

//=======================================================================
//function : TNaming_Scope
//purpose  : 
//=======================================================================

TNaming_Scope::TNaming_Scope (const Standard_Boolean with) : myWithValid(with)
{
}


//=======================================================================
//function : WithValid
//purpose  : 
//=======================================================================
Standard_Boolean TNaming_Scope::WithValid() const
{
  return myWithValid;
}

//=======================================================================
//function : WithValid
//purpose  : 
//=======================================================================
void TNaming_Scope::WithValid(const Standard_Boolean mode) 
{
  myWithValid = mode;
}

//=======================================================================
//function : ClearValid
//purpose  : 
//=======================================================================
void TNaming_Scope::ClearValid() 
{
  myValid.Clear(); 
}

//=======================================================================
//function : Valid
//purpose  : 
//=======================================================================
void TNaming_Scope::Valid(const TDF_Label& L) 
{
  myValid.Add(L);
}

//=======================================================================
//function : ValidChildren
//purpose  : 
//=======================================================================

void TNaming_Scope::ValidChildren(const TDF_Label& L,
					  const Standard_Boolean withroot) 
{  
  if (L.HasChild()) {
    TDF_ChildIterator itc (L,Standard_True);
    for (;itc.More();itc.Next()) myValid.Add(itc.Value());
  }
  if (withroot) myValid.Add(L);
}

//=======================================================================
//function : Unvalid
//purpose  : 
//=======================================================================
void TNaming_Scope::Unvalid(const TDF_Label& L) 
{
  myValid.Remove(L);
}

//=======================================================================
//function : UnvalidChildren
//purpose  : 
//=======================================================================

void TNaming_Scope::UnvalidChildren(const TDF_Label& L,
					  const Standard_Boolean withroot) 
{  
  if (L.HasChild()) {
    TDF_ChildIterator itc (L,Standard_True);
    for (;itc.More();itc.Next()) myValid.Remove(itc.Value());
  }
  if (withroot) myValid.Remove(L);
}

//=======================================================================
//function : IsValid
//purpose  : 
//=======================================================================
Standard_Boolean TNaming_Scope::IsValid(const TDF_Label& L) const
{
  if (myWithValid) return myValid.Contains (L);
  return Standard_True;
}

//=======================================================================
//function : GetValid
//purpose  : 
//=======================================================================
const TDF_LabelMap& TNaming_Scope::GetValid() const
{
  return myValid;
}

//=======================================================================
//function : ChangeValid
//purpose  : 
//=======================================================================
TDF_LabelMap& TNaming_Scope::ChangeValid()
{
  return myValid;
}

//=======================================================================
//function : CurrentShape
//purpose  : 
//=======================================================================
TopoDS_Shape TNaming_Scope::CurrentShape(const Handle(TNaming_NamedShape)& NS) const     
{
  if (myWithValid) return TNaming_Tool::CurrentShape(NS,myValid);
  return TNaming_Tool::CurrentShape(NS);
}




