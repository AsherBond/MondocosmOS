//static const char* sccsid = "@(#)Expr_Tangent.cxx	3.2 95/01/10"; // Do not delete this line. Used by sccs.
// Copyright: 	Matra-Datavision 1991
// File:	Expr_Tangent.cxx
// Created:	Tue May 28 14:30:09 1991
// Author:	Arnaud BOUZY
//		<adn>

#include <Expr_Tangent.ixx>
#include <Expr_NumericValue.hxx>
#include <Expr_ArcTangent.hxx>
#include <Expr_Cosine.hxx>
#include <Expr_Square.hxx>
#include <Expr_Division.hxx>
#include <Expr_Operators.hxx>
#include <Expr.hxx>

Expr_Tangent::Expr_Tangent(const Handle(Expr_GeneralExpression)& exp)
{
  CreateOperand(exp);
}

Handle(Expr_GeneralExpression) Expr_Tangent::ShallowSimplified () const
{
  Handle(Expr_GeneralExpression) myexp = Operand();
  if (myexp->IsKind(STANDARD_TYPE(Expr_NumericValue))) {
    Handle(Expr_NumericValue) myNVexp = Handle(Expr_NumericValue)::DownCast(myexp);
    return new Expr_NumericValue(Tan(myNVexp->GetValue()));
  }
  if (myexp->IsKind(STANDARD_TYPE(Expr_ArcTangent))) {
    return myexp->SubExpression(1);
  }
  Handle(Expr_Tangent) me = this;
  return me;
}

Handle(Expr_GeneralExpression) Expr_Tangent::Copy () const
{
  return new Expr_Tangent(Expr::CopyShare(Operand()));
}

Standard_Boolean Expr_Tangent::IsIdentical (const Handle(Expr_GeneralExpression)& Other) const
{
  if (Other->IsKind(STANDARD_TYPE(Expr_Tangent))) {
    Handle(Expr_GeneralExpression) myexp = Operand();
    return myexp->IsIdentical(Other->SubExpression(1));
  }
  return Standard_False;
}

Standard_Boolean Expr_Tangent::IsLinear () const
{
  return !ContainsUnknowns();
}

Handle(Expr_GeneralExpression) Expr_Tangent::Derivative (const Handle(Expr_NamedUnknown)& X) const
{
  if (!Contains(X)) {
    return  new Expr_NumericValue(0.0);
  }
  Handle(Expr_GeneralExpression) myexp = Operand();
  Handle(Expr_GeneralExpression) myder = myexp->Derivative(X);
  Handle(Expr_Cosine) firstder = new Expr_Cosine(Expr::CopyShare(myexp));
  Handle(Expr_Square) sq = new Expr_Square(firstder->ShallowSimplified());
  Handle(Expr_Division) resu = myder / sq->ShallowSimplified();
  return resu->ShallowSimplified();
}

Standard_Real Expr_Tangent::Evaluate(const Expr_Array1OfNamedUnknown& vars, const TColStd_Array1OfReal& vals) const
{
  return ::Tan(Operand()->Evaluate(vars,vals));
}

TCollection_AsciiString Expr_Tangent::String() const
{
  TCollection_AsciiString str("Tan(");
  str += Operand()->String();
  str +=")";
  return str;
}
