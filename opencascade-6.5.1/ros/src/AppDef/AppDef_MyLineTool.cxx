// AppDef_MyLineTool.cxx
// 24-06-96 : JPI : implementation des methodes AppDef_MyLineTool::Curvature
//                  pour le lissage variationnel 

#include <AppDef_MyLineTool.ixx>
#include <AppDef_MultiPointConstraint.hxx>
#include <gp_Pnt.hxx>
#include <gp_Pnt2d.hxx>
#include <gp_Vec.hxx>
#include <gp_Vec2d.hxx>

Standard_Integer AppDef_MyLineTool::FirstPoint(const AppDef_MultiLine&)
{
  return 1;
}

Standard_Integer AppDef_MyLineTool::LastPoint(const AppDef_MultiLine& ML)
{
  return ML.NbMultiPoints();
}

Standard_Integer AppDef_MyLineTool::NbP2d(const AppDef_MultiLine& ML)
{
  return ML.Value(1).NbPoints2d();
}

Standard_Integer AppDef_MyLineTool::NbP3d(const AppDef_MultiLine& ML)
{
  return ML.Value(1).NbPoints();
}


void AppDef_MyLineTool::Value(const AppDef_MultiLine& ML, 
			      const Standard_Integer MPointIndex,
			      TColgp_Array1OfPnt& tabPt)
{
  AppDef_MultiPointConstraint MPC = ML.Value(MPointIndex);
  Standard_Integer nbp3d = MPC.NbPoints(), low = tabPt.Lower();
  for (Standard_Integer i = 1; i <= nbp3d; i++) {
    tabPt(i+low-1) = MPC.Point(i);
  }
}

void AppDef_MyLineTool::Value(const AppDef_MultiLine& ML, 
			      const Standard_Integer MPointIndex,
			      TColgp_Array1OfPnt2d& tabPt2d)
{
  AppDef_MultiPointConstraint MPC = ML.Value(MPointIndex);
  Standard_Integer nbp2d = MPC.NbPoints2d(), low = tabPt2d.Lower();
  for (Standard_Integer i = 1; i <= nbp2d; i++) {
    tabPt2d(i+low-1) = MPC.Point2d(i);
  }
}

void AppDef_MyLineTool::Value(const AppDef_MultiLine& ML, 
			      const Standard_Integer MPointIndex,
			      TColgp_Array1OfPnt& tabPt,
			      TColgp_Array1OfPnt2d& tabPt2d)
{
  AppDef_MultiPointConstraint MPC = ML.Value(MPointIndex);
  Standard_Integer i, nbp2d = MPC.NbPoints2d(), low2d = tabPt2d.Lower();
  Standard_Integer nbp3d = MPC.NbPoints(), low = tabPt.Lower();
  for (i = 1; i <= nbp3d; i++) {
    tabPt(i+low-1) = MPC.Point(i);
  }
  for (i = 1; i <= nbp2d; i++) {
    tabPt2d(i+low2d-1) = MPC.Point2d(nbp3d+i);
  }
}


Standard_Boolean  AppDef_MyLineTool::Tangency(const AppDef_MultiLine& ML, 
				 const Standard_Integer MPointIndex,
				 TColgp_Array1OfVec& tabV)
{
  AppDef_MultiPointConstraint MPC = ML.Value(MPointIndex);
  if (MPC.IsTangencyPoint()) {
    Standard_Integer nbp3d = MPC.NbPoints(), low = tabV.Lower();
    for (Standard_Integer i = 1; i <= nbp3d; i++) {
      tabV(i+low-1) = MPC.Tang(i);
    }
    return Standard_True;
  }
  else return Standard_False;
}

Standard_Boolean AppDef_MyLineTool::Tangency(const AppDef_MultiLine& ML, 
				 const Standard_Integer MPointIndex,
				 TColgp_Array1OfVec2d& tabV2d)
{
  AppDef_MultiPointConstraint MPC = ML.Value(MPointIndex);
  if (MPC.IsTangencyPoint()) {
    Standard_Integer nbp2d = MPC.NbPoints2d(), low = tabV2d.Lower();
    for (Standard_Integer i = 1; i <= nbp2d; i++) {
      tabV2d(i+low-1) = MPC.Tang2d(i);
    }
    return Standard_True;
  }
  else return Standard_False;
}

Standard_Boolean AppDef_MyLineTool::Tangency(const AppDef_MultiLine& ML, 
				 const Standard_Integer MPointIndex,
				 TColgp_Array1OfVec& tabV,
				 TColgp_Array1OfVec2d& tabV2d)
{
  AppDef_MultiPointConstraint MPC = ML.Value(MPointIndex);
  if (MPC.IsTangencyPoint()) {
    Standard_Integer i, nbp3d = MPC.NbPoints(), low = tabV.Lower();
    Standard_Integer nbp2d = MPC.NbPoints2d(), low2d = tabV2d.Lower();
    for (i = 1; i <= nbp3d; i++) {
      tabV(i+low-1) = MPC.Tang(i);
    }
    for (i = 1; i <= nbp2d; i++) {
      tabV2d(i+low2d-1) = MPC.Tang2d(nbp3d+i);
    }
    return Standard_True;
  }
  else return Standard_False;
  
}


AppDef_MultiLine& AppDef_MyLineTool::MakeMLBetween(const AppDef_MultiLine&,
						   const Standard_Integer ,
						   const Standard_Integer ,
						   const Standard_Integer )
{
  return *((AppDef_MultiLine*) 0);
}

Approx_Status AppDef_MyLineTool::WhatStatus(const AppDef_MultiLine&,
					    const Standard_Integer,
					    const Standard_Integer)
{
  return Approx_NoPointsAdded;
}


Standard_Boolean  AppDef_MyLineTool::Curvature(const AppDef_MultiLine& ML, 
				 const Standard_Integer MPointIndex,
				 TColgp_Array1OfVec& tabV)
{
  AppDef_MultiPointConstraint MPC = ML.Value(MPointIndex);
  if (MPC.IsCurvaturePoint()) {
    Standard_Integer nbp3d = MPC.NbPoints(), low = tabV.Lower();
    for (Standard_Integer i = 1; i <= nbp3d; i++) {
      tabV(i+low-1) = MPC.Curv(i);
    }
    return Standard_True;
  }
  else return Standard_False;
}

Standard_Boolean AppDef_MyLineTool::Curvature(const AppDef_MultiLine& ML, 
				 const Standard_Integer MPointIndex,
				 TColgp_Array1OfVec2d& tabV2d)
{
  AppDef_MultiPointConstraint MPC = ML.Value(MPointIndex);
  if (MPC.IsCurvaturePoint()) {
    Standard_Integer nbp2d = MPC.NbPoints2d(), low = tabV2d.Lower();
    for (Standard_Integer i = 1; i <= nbp2d; i++) {
      tabV2d(i+low-1) = MPC.Curv2d(i);
    }
    return Standard_True;
  }
  else return Standard_False;
}


Standard_Boolean AppDef_MyLineTool::Curvature(const AppDef_MultiLine& ML, 
				 const Standard_Integer MPointIndex,
				 TColgp_Array1OfVec& tabV,
				 TColgp_Array1OfVec2d& tabV2d)

{
  AppDef_MultiPointConstraint MPC = ML.Value(MPointIndex);
  if (MPC.IsCurvaturePoint()) {
    Standard_Integer i, nbp3d = MPC.NbPoints(), low = tabV.Lower();
    Standard_Integer nbp2d = MPC.NbPoints2d(), low2d = tabV2d.Lower();
    for (i = 1; i <= nbp3d; i++) {
      tabV(i+low-1) = MPC.Curv(i);
    }
    for (i = 1; i <= nbp2d; i++) {
      tabV2d(i+low2d-1) = MPC.Curv2d(nbp3d+i);
    }
    return Standard_True;
  }
  else return Standard_False;
  
}


