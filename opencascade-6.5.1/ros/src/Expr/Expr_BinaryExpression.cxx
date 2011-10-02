//static const char* sccsid = "@(#)Expr_BinaryExpression.cxx	3.2 95/01/10"; // Do not delete this line. Used by sccs.
// Copyright: 	Matra-Datavision 1991
// File:	Expr_BinaryExpression.cxx
// Created:	Fri Apr 12 10:41:21 1991
// Author:	Arnaud BOUZY
//		<adn>

#include <Expr_BinaryExpression.ixx>
#include <Expr_NamedUnknown.hxx>
#include <Expr_InvalidOperand.hxx>
#include <Standard_OutOfRange.hxx>


void Expr_BinaryExpression::SetFirstOperand (const Handle(Expr_GeneralExpression)& exp)
{
  Handle(Expr_BinaryExpression) me;
  me = this;
  if (exp == me) {
    Expr_InvalidOperand::Raise();
  }
  if (exp->Contains(me)) {
    Expr_InvalidOperand::Raise();
  }
  myFirstOperand = exp;
}

void Expr_BinaryExpression::SetSecondOperand (const Handle(Expr_GeneralExpression)& exp)
{
  Handle(Expr_BinaryExpression) me;
  me = this;
  if (exp == me) {
    Expr_InvalidOperand::Raise();
  }
  if (exp->Contains(me)) {
    Expr_InvalidOperand::Raise();
  }
  mySecondOperand = exp;
}

void Expr_BinaryExpression::CreateFirstOperand (const Handle(Expr_GeneralExpression)& exp)
{
  myFirstOperand = exp;
}

void Expr_BinaryExpression::CreateSecondOperand (const Handle(Expr_GeneralExpression)& exp)
{
  mySecondOperand = exp;
}

Standard_Integer Expr_BinaryExpression::NbSubExpressions () const
{
  return 2;
}

const Handle(Expr_GeneralExpression)& Expr_BinaryExpression::SubExpression (const Standard_Integer I) const
{
  if (I == 1) {
    return myFirstOperand;
  }
  else {
    if (I == 2) {
      return mySecondOperand;
    }
    else {
      Standard_OutOfRange::Raise();
    }
  }
#if defined (WNT) || !defined (DEB)
 return *(  ( Handle_Expr_GeneralExpression* )NULL  );
#endif  // WNT || !DEB
}

Standard_Boolean Expr_BinaryExpression::ContainsUnknowns () const
{
  if (myFirstOperand->IsKind(STANDARD_TYPE(Expr_NamedUnknown))) {
    return Standard_True;
  }
  if (mySecondOperand->IsKind(STANDARD_TYPE(Expr_NamedUnknown))) {
    return Standard_True;
  }
  if (myFirstOperand->ContainsUnknowns()) {
    return Standard_True;
  }
  if (mySecondOperand->ContainsUnknowns()) {
    return Standard_True;
  }
  return Standard_False;
}

Standard_Boolean Expr_BinaryExpression::Contains (const Handle(Expr_GeneralExpression)& exp) const
{
  if (myFirstOperand == exp) {
    return Standard_True;
  }
  if (mySecondOperand == exp) {
    return Standard_True;
  }
  if (myFirstOperand->Contains(exp)) {
    return Standard_True;
  }
  if (mySecondOperand->Contains(exp)) {
    return Standard_True;
  }
  return Standard_False;
}

void Expr_BinaryExpression::Replace (const Handle(Expr_NamedUnknown)& var, const Handle(Expr_GeneralExpression)& with)
{
  if (myFirstOperand == var) {
    SetFirstOperand(with);
  }
  else {
    if (myFirstOperand->Contains(var)) {
      myFirstOperand->Replace(var,with);
    }
  }
  if (mySecondOperand == var) {
    SetSecondOperand(with);
  }
  else {
    if (mySecondOperand->Contains(var)) {
      mySecondOperand->Replace(var,with);
    }
  }
}


Handle(Expr_GeneralExpression) Expr_BinaryExpression::Simplified() const
{
  Handle(Expr_BinaryExpression) cop = Handle(Expr_BinaryExpression)::DownCast(Copy());
  Handle(Expr_GeneralExpression) op1 = cop->FirstOperand();
  Handle(Expr_GeneralExpression) op2 = cop->SecondOperand();
  cop->SetFirstOperand(op1->Simplified());
  cop->SetSecondOperand(op2->Simplified());
  return cop->ShallowSimplified();
}
