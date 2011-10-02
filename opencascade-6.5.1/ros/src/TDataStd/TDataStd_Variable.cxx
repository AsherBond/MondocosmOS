// File:        TDataStd_Variable.cxx
// Created:     Wed Dec 10 11:25:50 1997
// Author:      Denis PASCAL
//              <dp@dingox.paris1.matra-dtv.fr>


#include <TDataStd_Variable.ixx>
#include <TDF_Label.hxx> 
#include <TDataStd_Real.hxx>
#include <TDataStd_Name.hxx>
#include <Standard_DomainError.hxx>


//=======================================================================
//function : GetID
//purpose  : 
//=======================================================================

const Standard_GUID& TDataStd_Variable::GetID() 
{  
  static Standard_GUID TDataStd_VariableID("ce241469-8e57-11d1-8953-080009dc4425");
  return TDataStd_VariableID;
}

//=======================================================================
//function : Set
//purpose  : 
//=======================================================================

Handle(TDataStd_Variable) TDataStd_Variable::Set(const TDF_Label& L)
{  
  Handle(TDataStd_Variable) A;
  if (!L.FindAttribute (TDataStd_Variable::GetID(), A)) {
    A = new TDataStd_Variable (); 
    L.AddAttribute(A);
  }
  return A;
}

//=======================================================================
//function : TDataStd_Variable
//purpose  : 
//=======================================================================

TDataStd_Variable::TDataStd_Variable() 
  :isConstant(Standard_False),
   myUnit("SCALAR")
{
}

//=======================================================================
//function : Name
//purpose  : 
//=======================================================================

void TDataStd_Variable::Name (const TCollection_ExtendedString& string)
{ 
  TDataStd_Name::Set(Label(),string);  
}

//=======================================================================
//function : Name
//purpose  : 
//=======================================================================
const TCollection_ExtendedString& TDataStd_Variable::Name () const 
{
  Handle(TDataStd_Name) N;
  if (!Label().FindAttribute(TDataStd_Name::GetID(),N)) {
    Standard_DomainError::Raise("TDataStd_Variable::Name : invalid model");
  }
  return N->Get();
}

//=======================================================================
//function : IsValued
//purpose  : 
//=======================================================================

Standard_Boolean TDataStd_Variable::IsValued () const
{
  return (Label().IsAttribute (TDataStd_Real::GetID()));
}

//=======================================================================
//function : Real
//purpose  : 
//=======================================================================

Handle(TDataStd_Real) TDataStd_Variable::Real() const
{
  Handle(TDataStd_Real) R;
  if (!Label().FindAttribute(TDataStd_Real::GetID(),R)) {
    Standard_DomainError::Raise("TDataStd_Variable::Real : invalid model");
  }
  return R;
}

//=======================================================================
//function : Set
//purpose  : 
//=======================================================================

void TDataStd_Variable::Set (const Standard_Real value, const TDataStd_RealEnum dimension) const
{  
  if (!IsValued()) {
    Handle(TDataStd_Real) R = TDataStd_Real::Set(Label(),value);
    R->SetDimension (dimension);
  }
  else {
    Handle(TDataStd_Real) R = TDataStd_Real::Set(Label(),value);
  }
}


//=======================================================================
//function : Get
//purpose  : 
//=======================================================================

Standard_Real TDataStd_Variable::Get () const
{  
  Handle(TDataStd_Real) R;
  if (!Label().FindAttribute(TDataStd_Real::GetID(),R)) {
    Standard_DomainError::Raise("TDataStd_Variable::Get : invalid model");
  }
  return R->Get();
}



//=======================================================================
//function : IsAssigned
//purpose  : 
//=======================================================================

Standard_Boolean TDataStd_Variable::IsAssigned () const
{
  return (Label().IsAttribute(TDataStd_Expression::GetID()));
}

//=======================================================================
//function : Assign
//purpose  : 
//=======================================================================

Handle(TDataStd_Expression) TDataStd_Variable::Assign () const
{
  Handle(TDataStd_Expression) E = TDataStd_Expression::Set(Label());
  return E;
}

//=======================================================================
//function : Desassign
//purpose  : 
//=======================================================================

void TDataStd_Variable::Desassign () const
{  
  Handle(TDataStd_Expression) E;
  if (!Label().FindAttribute(TDataStd_Expression::GetID(),E)) {
    Standard_DomainError::Raise("TDataStd_Variable::Deassign");
  }   
  Label().ForgetAttribute(E);
}

//=======================================================================
//function : Expression
//purpose  : 
//=======================================================================

Handle(TDataStd_Expression) TDataStd_Variable::Expression () const
{
  Handle(TDataStd_Expression) E;
  if (!Label().FindAttribute(TDataStd_Expression::GetID(),E)) {
    Standard_DomainError::Raise("TDataStd_Variable::GetExpression");
  }
  return E;
}

//=======================================================================
//function : IsCaptured
//purpose  : 
//=======================================================================

Standard_Boolean TDataStd_Variable::IsCaptured() const
{  
  return Real()->IsCaptured();
}

//=======================================================================
//function : IsConstant
//purpose  : 
//=======================================================================

Standard_Boolean TDataStd_Variable::IsConstant () const
{
  return isConstant;
}

//=======================================================================
//function : Constant
//purpose  : 
//=======================================================================

void TDataStd_Variable::Constant (const Standard_Boolean status) 
{
  // OCC2932 correction
  if(isConstant == status) return;

  Backup();
  isConstant = status;
}

//=======================================================================
//function : Unit
//purpose  : 
//=======================================================================
void TDataStd_Variable::Unit(const TCollection_AsciiString& unit)
{
  // OCC2932 correction
  if(myUnit == unit)
    return;

  Backup();
  myUnit = unit;
}
//=======================================================================
//function : Unit
//purpose  : 
//=======================================================================
const TCollection_AsciiString& TDataStd_Variable::Unit() const 
{
  return myUnit;
}

//=======================================================================
//function : ID
//purpose  : 
//=======================================================================

const Standard_GUID& TDataStd_Variable::ID() const
{
  return GetID();
}

//=======================================================================
//function : Restore
//purpose  : 
//=======================================================================

void TDataStd_Variable::Restore (const Handle(TDF_Attribute)& With) 
{
  Handle(TDataStd_Variable) V = Handle(TDataStd_Variable)::DownCast (With);
  isConstant = V->IsConstant();
  myUnit = V->Unit();
}

//=======================================================================
//function : NewEmpty
//purpose  : 
//=======================================================================

Handle(TDF_Attribute) TDataStd_Variable::NewEmpty() const
{
  return new TDataStd_Variable();
}

//=======================================================================
//function : Paste
//purpose  : 
//=======================================================================

void TDataStd_Variable::Paste (const Handle(TDF_Attribute)& Into,
                               const Handle(TDF_RelocationTable)& /*RT*/) const
{   
  Handle(TDataStd_Variable) V = Handle(TDataStd_Variable)::DownCast (Into); 
  V->Constant(isConstant);
  V->Unit(myUnit); 
}


//=======================================================================
//function : References
//purpose  : 
//=======================================================================

void TDataStd_Variable::References(const Handle(TDF_DataSet)& DS) const
{
  Handle(TDataStd_Name) N;
  if (Label().FindAttribute(TDataStd_Name::GetID(),N)) {
    DS->AddAttribute(N);
  }    
}

//=======================================================================
//function : Dump
//purpose  : 
//=======================================================================

Standard_OStream& TDataStd_Variable::Dump(Standard_OStream& anOS) const
{  
  anOS << "Variable";
  return anOS;
}

