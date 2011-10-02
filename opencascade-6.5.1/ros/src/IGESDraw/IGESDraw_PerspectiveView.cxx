//--------------------------------------------------------------------
//
//  File Name : IGESDraw_PerspectiveView.cxx
//  Date      :
//  Author    : CKY / Contract Toubro-Larsen
//  Copyright : MATRA-DATAVISION 1993
//
//--------------------------------------------------------------------

#include <IGESDraw_PerspectiveView.ixx>
#include <gp_GTrsf.hxx>


    IGESDraw_PerspectiveView::IGESDraw_PerspectiveView ()    {  }


// This class inherits from IGESData_ViewKindEntity

    void IGESDraw_PerspectiveView::Init
  (const Standard_Integer aViewNumber,
   const Standard_Real    aScaleFactor,
   const gp_XYZ&          aViewNormalVector,
   const gp_XYZ&          aViewReferencePoint,
   const gp_XYZ&          aCenterOfProjection,
   const gp_XYZ&          aViewUpVector,
   const Standard_Real    aViewPlaneDistance,
   const gp_XY&           aTopLeft,
   const gp_XY&           aBottomRight,
   const Standard_Integer aDepthClip,
   const Standard_Real    aBackPlaneDistance,
   const Standard_Real    aFrontPlaneDistance)
{
  theViewNumber         = aViewNumber;
  theScaleFactor        = aScaleFactor;
  theViewNormalVector   = aViewNormalVector;
  theViewReferencePoint = aViewReferencePoint;
  theCenterOfProjection = aCenterOfProjection;
  theViewUpVector       = aViewUpVector;
  theViewPlaneDistance  = aViewPlaneDistance;
  theTopLeft            = aTopLeft;
  theBottomRight        = aBottomRight;
  theDepthClip          = aDepthClip;
  theBackPlaneDistance  = aBackPlaneDistance;
  theFrontPlaneDistance = aFrontPlaneDistance;
  InitTypeAndForm(410,1);
}

    Standard_Boolean IGESDraw_PerspectiveView::IsSingle () const
{
  return Standard_True;
}

    Standard_Integer IGESDraw_PerspectiveView::NbViews () const
{  return 1;  }

    Handle(IGESData_ViewKindEntity)  IGESDraw_PerspectiveView::ViewItem
  (const Standard_Integer) const
{  return Handle(IGESData_ViewKindEntity)::DownCast (This());  }


    Standard_Integer IGESDraw_PerspectiveView::ViewNumber () const
{
  return theViewNumber;
}

    Standard_Real IGESDraw_PerspectiveView::ScaleFactor () const
{
  return theScaleFactor;
}

    gp_Vec IGESDraw_PerspectiveView::ViewNormalVector () const
{
  gp_Vec tempRes(theViewNormalVector);
  return tempRes;
}

    gp_Pnt IGESDraw_PerspectiveView::ViewReferencePoint () const
{
  gp_Pnt tempRes(theViewReferencePoint);
  return tempRes;
}

    gp_Pnt IGESDraw_PerspectiveView::CenterOfProjection () const
{
  gp_Pnt tempRes(theCenterOfProjection);
  return tempRes;
}

    gp_Vec IGESDraw_PerspectiveView::ViewUpVector () const
{
  gp_Vec tempRes(theViewUpVector);
  return tempRes;
}

    Standard_Real IGESDraw_PerspectiveView::ViewPlaneDistance () const
{
  return theViewPlaneDistance;
}

    gp_Pnt2d IGESDraw_PerspectiveView::TopLeft () const
{
  gp_Pnt2d tempRes(theTopLeft);
  return tempRes;
}

    gp_Pnt2d IGESDraw_PerspectiveView::BottomRight () const
{
  gp_Pnt2d tempRes(theBottomRight);
  return tempRes;
}

    Standard_Integer IGESDraw_PerspectiveView::DepthClip () const
{
  return theDepthClip;
}

    Standard_Real IGESDraw_PerspectiveView::BackPlaneDistance () const
{
  return theBackPlaneDistance;
}

    Standard_Real IGESDraw_PerspectiveView::FrontPlaneDistance () const
{
  return theFrontPlaneDistance;
}

    Handle(IGESData_TransfEntity) IGESDraw_PerspectiveView::ViewMatrix () const
{
  return (Transf());
}

    gp_XYZ IGESDraw_PerspectiveView::ModelToView
  (const gp_XYZ& coords) const
{
  gp_XYZ tempCoords = coords;
  Location().Transforms(tempCoords);
  return (tempCoords);
}
