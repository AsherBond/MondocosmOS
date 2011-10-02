//static const char* sccsid = "@(#)Expr_Cosine.cxx	3.2 95/01/10"; // Do not delete this line. Used by sccs.
// Copyright: 	Matra-Datavision 1991
// File:	Expr_Cosine.cxx
// Created:	Mon May 27 17:24:38 1991
// Author:	Arnaud BOUZY
//		<adn>

#include <Expr_Cosine.ixx>
#include <Expr_NumericValue.hxx>
#include <Expr_ArcCosine.hxx>
#include <Expr_Sine.hxx>
#include <Expr_UnaryMinus.hxx>
#include <Expr_Product.hxx>
#include <Expr_Operators.hxx>
#include <Expr.hxx>

Expr_Cosine::Expr_Cosine(const Handle(Expr_GeneralExpression)& exp)
{
  CreateOperand(exp);
}

Handle(Expr_GeneralExpression) Expr_Cosine::ShallowSimplified () const
{
  Handle(Expr_GeneralExpression) myexp = Operand();
  if (myexp->IsKind(STANDARD_TYPE(Expr_NumericValue))) {
    Handle(Expr_NumericValue) myNVexp = Handle(Expr_NumericValue)::DownCast(myexp);
    return new Expr_NumericValue(Cos(myNVexp->GetValue()));
  }
  if (myexp->IsKind(STANDARD_TYPE(Expr_ArcCosine))) {
    return myexp->SubExpression(1);
  }
  Handle(Expr_Cosine) me = this;
  return me;
}

Handle(Expr_GeneralExpression) Expr_Cosine::Copy () const
{
  return new Expr_Cosine(Expr::CopyShare(Operand()));
}

Standard_Boolean Expr_Cosine::IsIdentical (const Handle(Expr_GeneralExpression)& Other) const
{
  if (Other->IsKind(STANDARD_TYPE(Expr_Cosine))) {
    Handle(Expr_GeneralExpression) myexp = Operand();
    return myexp->IsIdentical(Other->SubExpression(1));
  }
  return Standard_False;
}

Standard_Boolean Expr_Cosine::IsLinear () const
{
  return !ContainsUnknowns();
}

Handle(Expr_GeneralExpression) Expr_Cosine::Derivative (const Handle(Expr_NamedUnknown)& X) const
{
  if (!Contains(X)) {
    return new Expr_NumericValue(0.0);
  }
  Handle(Expr_GeneralExpression) myexp = Operand();
  Handle(Expr_GeneralExpression) myder = myexp->Derivative(X);
  Handle(Expr_Sine) firstder = new Expr_Sine(Expr::CopyShare(myexp));
  Handle(Expr_UnaryMinus) fder = - (firstder->ShallowSimplified());
  Handle(Expr_Product) resu = fder->ShallowSimplified() * myder;
  return resu->ShallowSimplified();
}

Standard_Real Expr_Cosine::Evaluate(const Expr_Array1OfNamedUnknown& vars, const TColStd_Array1OfReal& vals) const
{
  return ::Cos(Operand()->Evaluate(vars,vals));
}

TCollection_AsciiString Expr_Cosine::String() const
{
  TCollection_AsciiString str("Cos(");
  str += Operand()->String();
  str += ")";
  return str;
}
