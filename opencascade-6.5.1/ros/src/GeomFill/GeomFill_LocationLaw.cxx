// File:	GeomFill_LocationLaw.cxx
// Created:	Fri Nov 21 15:19:24 1997
// Author:	Philippe MANGIN
//		<pmn@sgi29>


#include <GeomFill_LocationLaw.ixx>

 Standard_Boolean GeomFill_LocationLaw::D1(const Standard_Real, gp_Mat&, gp_Vec&,gp_Mat&, gp_Vec&,TColgp_Array1OfPnt2d&,TColgp_Array1OfVec2d&) 
{
  Standard_NotImplemented::Raise("GeomFill_LocationLaw::D1");
  return 0;
}

 Standard_Boolean GeomFill_LocationLaw::D2(const Standard_Real,
					   gp_Mat&,gp_Vec&,
					   gp_Mat&, gp_Vec&,
					   gp_Mat&, gp_Vec&, 
					   TColgp_Array1OfPnt2d&,TColgp_Array1OfVec2d&,TColgp_Array1OfVec2d&) 
{
  Standard_NotImplemented::Raise("GeomFill_LocationLaw::D2");
  return 0;
}

 Standard_Integer GeomFill_LocationLaw::Nb2dCurves() const
{
  Standard_Integer N = TraceNumber();
  if  (HasFirstRestriction()) N++;
  if  (HasLastRestriction()) N++;

  return N;
}

 Standard_Boolean GeomFill_LocationLaw::HasFirstRestriction() const
{
  return Standard_False;
}

 Standard_Boolean GeomFill_LocationLaw::HasLastRestriction() const
{
  return Standard_False;
}

 Standard_Integer GeomFill_LocationLaw::TraceNumber() const
{
  return 0;
}

//==================================================================
//Function : ErrorStatus
//Purpose :
//==================================================================
 GeomFill_PipeError GeomFill_LocationLaw::ErrorStatus() const
{
  return GeomFill_PipeOk;
}

// void GeomFill_LocationLaw::Resolution(const Standard_Integer Index,const Standard_Real Tol,Standard_Real& TolU,Standard_Real& TolV) const
 void GeomFill_LocationLaw::Resolution(const Standard_Integer ,const Standard_Real ,Standard_Real& ,Standard_Real& ) const
{
  Standard_NotImplemented::Raise("GeomFill_LocationLaw::Resolution");
}

 void GeomFill_LocationLaw::SetTolerance(const Standard_Real,
					 const Standard_Real ) 
{
 // Ne fait rien !!
}
 Standard_Boolean GeomFill_LocationLaw::IsTranslation(Standard_Real&) const
{
  return Standard_False;
}

 Standard_Boolean GeomFill_LocationLaw::IsRotation(Standard_Real&) const
{
  return Standard_False;
}
 void GeomFill_LocationLaw::Rotation(gp_Pnt&) const
{
  Standard_NotImplemented::Raise("GeomFill_SectionLaw::Rotation");
}
