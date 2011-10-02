// File:	GCPnts_TangentialDeflection.cxx
// Created:	Fri Nov  8 11:19:29 1996
// Author:	Jean Claude VAUTHIER
//		<jcv@brunox.paris1.matra-dtv.fr>


#include <GCPnts_TangentialDeflection.ixx>

#include <gp_Pnt2d.hxx>
#include <gp_Vec.hxx>
#include <gp_Vec2d.hxx>
#include <gp_XYZ.hxx>
#include <Precision.hxx>

inline static void D0 (const Adaptor3d_Curve& C, const Standard_Real U, gp_Pnt& P)
{
  C.D0 (U, P);
}

inline static void D2 (const Adaptor3d_Curve& C, const Standard_Real U, 
                       gp_Pnt& P, gp_Vec& V1, gp_Vec& V2)
{
  C.D2 (U, P, V1, V2);
}


static void D0 (const Adaptor2d_Curve2d& C, const Standard_Real U, gp_Pnt& PP)
{
  Standard_Real X, Y;
  gp_Pnt2d P;
  C.D0 (U, P);
  P.Coord (X, Y);
  PP.SetCoord (X, Y, 0.0);
}

static void D2 (const Adaptor2d_Curve2d& C, const Standard_Real U,
	        gp_Pnt& PP, gp_Vec& VV1, gp_Vec& VV2)
{
  Standard_Real X, Y;
  gp_Pnt2d P;
  gp_Vec2d V1,V2;
  C.D2 (U, P, V1, V2);
  P.Coord (X, Y);
  PP.SetCoord  (X, Y, 0.0);
  V1.Coord (X, Y);
  VV1.SetCoord (X, Y, 0.0);
  V2.Coord (X, Y);
  VV2.SetCoord (X, Y, 0.0);
}


//=======================================================================
//function : CPnts_TangentialDeflection
//purpose  : 
//=======================================================================

GCPnts_TangentialDeflection::GCPnts_TangentialDeflection () { }

//=======================================================================
//function : AddPoint
//purpose  : 
//=======================================================================

Standard_Integer GCPnts_TangentialDeflection::AddPoint
 (const gp_Pnt& thePnt,
  const Standard_Real theParam,
  const Standard_Boolean theIsReplace)
{
  const Standard_Real tol = Precision::PConfusion();
  Standard_Integer index = -1;
  const Standard_Integer nb = parameters.Length();
  for ( Standard_Integer i = 1; index == -1 && i <= nb; i++ )
  {
    Standard_Real dist = parameters.Value( i ) - theParam;
    if ( fabs( dist ) <= tol )
    {
      index = i;
      if ( theIsReplace )
      {
	points.ChangeValue(i) = thePnt;
	parameters.ChangeValue(i) = theParam;
      }
    }
    else if ( dist > tol )
    {
      points.InsertBefore( i, thePnt );
      parameters.InsertBefore( i, theParam );
      index = i;
    }
  }
  if ( index == -1 )
  {
    points.Append( thePnt );
    parameters.Append( theParam );
    index = parameters.Length();
  }
  return index;
}


#include <Geom_BezierCurve.hxx>
#include <Geom_BSplineCurve.hxx>
#include <gp_Circ.hxx>
#define TheCurve Adaptor3d_Curve
#define Handle_TheBezierCurve   Handle(Geom_BezierCurve)
#define Handle_TheBSplineCurve  Handle(Geom_BSplineCurve)
#include <GCPnts_TangentialDeflection.gxx>
#undef Handle_TheBezierCurve
#undef Handle_TheBSplineCurve
#undef TheCurve


#include <Geom2d_BezierCurve.hxx>
#include <Geom2d_BSplineCurve.hxx>
#include <gp_Circ2d.hxx>
#define TheCurve Adaptor2d_Curve2d
#define Handle_TheBezierCurve   Handle(Geom2d_BezierCurve)
#define Handle_TheBSplineCurve  Handle(Geom2d_BSplineCurve)
#include <GCPnts_TangentialDeflection.gxx>
#undef Handle_TheBezierCurve
#undef Handle_TheBSplineCurve
#undef TheCurve




