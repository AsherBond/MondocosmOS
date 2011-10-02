// File:	GCE2d_MakeArcOfEllipse.cxx
// Created:	Fri Oct  2 16:31:13 1992
// Author:	Remi GILET
//		<reg@topsn3>

#include <GCE2d_MakeArcOfEllipse.ixx>
#include <Geom2d_Ellipse.hxx>
#include <ElCLib.hxx>
#include <StdFail_NotDone.hxx>

GCE2d_MakeArcOfEllipse::GCE2d_MakeArcOfEllipse(const gp_Elips2d&      Elips ,
					       const gp_Pnt2d&        P1    ,
					       const gp_Pnt2d&        P2    ,
					       const Standard_Boolean Sense ) 
{
  Standard_Real Alpha1 = ElCLib::Parameter(Elips,P1);
  Standard_Real Alpha2 = ElCLib::Parameter(Elips,P2);
  Handle(Geom2d_Ellipse) E = new Geom2d_Ellipse(Elips);
  TheArc = new Geom2d_TrimmedCurve(E,Alpha1,Alpha2,Sense);
  TheError = gce_Done;
}

GCE2d_MakeArcOfEllipse::GCE2d_MakeArcOfEllipse(const gp_Elips2d&      Elips ,
					       const gp_Pnt2d&        P     ,
					       const Standard_Real    Alpha ,
					       const Standard_Boolean Sense ) 
{
  Standard_Real Alphafirst = ElCLib::Parameter(Elips,P);
  Handle(Geom2d_Ellipse) E = new Geom2d_Ellipse(Elips);
  TheArc = new Geom2d_TrimmedCurve(E,Alphafirst,Alpha,Sense);
  TheError = gce_Done;
}

GCE2d_MakeArcOfEllipse::GCE2d_MakeArcOfEllipse(const gp_Elips2d&      Elips  ,
					       const Standard_Real    Alpha1 ,
					       const Standard_Real    Alpha2 ,
					       const Standard_Boolean Sense  ) 
{
  Handle(Geom2d_Ellipse) E = new Geom2d_Ellipse(Elips);
  TheArc = new Geom2d_TrimmedCurve(E,Alpha1,Alpha2,Sense);
  TheError = gce_Done;
}

const Handle(Geom2d_TrimmedCurve)& GCE2d_MakeArcOfEllipse::Value() const
{ 
  StdFail_NotDone_Raise_if(!TheError == gce_Done,"");
  return TheArc;
}

const Handle(Geom2d_TrimmedCurve)& GCE2d_MakeArcOfEllipse::Operator() const 
{
  return Value();
}

GCE2d_MakeArcOfEllipse::operator Handle(Geom2d_TrimmedCurve) () const
{
  return Value();
}




