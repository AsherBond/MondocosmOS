// File:  V3d_LayerMgr.cxx
// Created: Thu Apr 17 12:34:25 2008
// Author:  Customer Support
//Copyright:  Open Cascade 2008

#include <V3d_LayerMgr.ixx>

#include <Aspect_Window.hxx>
#include <Visual3d_View.hxx>
#include <V3d_ColorScale.hxx>
#include <Graphic3d_NameOfFont.hxx>


V3d_LayerMgr::V3d_LayerMgr( const Handle(V3d_View)& AView )
: myView(AView.operator->())
{
  Handle(Visual3d_View) theView = View()->View();
  if ( !theView.IsNull() ) {
    Handle(Visual3d_ViewManager) theViewMgr = theView->ViewManager();
    if ( !theViewMgr.IsNull() ) {
      V3d_LayerMgr* that = (V3d_LayerMgr*)this;
      that->myOverlay = new Visual3d_Layer( theViewMgr, Aspect_TOL_OVERLAY, Standard_False );
    }
  }
}

void V3d_LayerMgr::Compute()
{
  if (Begin())
  {
    Redraw();
    End();
  }
}

void V3d_LayerMgr::Resized()
{
  Compute();
}

void V3d_LayerMgr::ColorScaleDisplay()
{
  ColorScale();
  myColorScale->Display();
  myOverlay->AddLayerItem( myColorScaleLayerItem );
}

void V3d_LayerMgr::ColorScaleErase()
{
  if ( !myColorScale.IsNull() )
    myColorScale->Erase();
  myOverlay->RemoveLayerItem( myColorScaleLayerItem );
}

Standard_Boolean V3d_LayerMgr::ColorScaleIsDisplayed() const
{
  return ( myColorScale.IsNull() ? Standard_False : myColorScale->IsDisplayed() );
}

Handle(Aspect_ColorScale) V3d_LayerMgr::ColorScale() const
{
  if ( myColorScale.IsNull() ) {
    Handle(V3d_LayerMgr) that = this;
    that->myColorScale = new V3d_ColorScale( this );
    that->myColorScaleLayerItem = new V3d_ColorScaleLayerItem( that->myColorScale );
  }

  return myColorScale;
}

Standard_Boolean V3d_LayerMgr::Begin()
{
  if ( myOverlay.IsNull() )
    return Standard_False;

  const Handle(Aspect_Window) &theWin = View()->Window();
  if ( theWin.IsNull() )
    return Standard_False;

  Standard_Integer aW( 0 ), aH( 0 );
  theWin->Size( aW, aH );

  myOverlay->Clear();
  myOverlay->SetViewport( aW, aH ); //szv:!!!
  myOverlay->Begin();
  myOverlay->SetTextAttributes( Graphic3d_NOF_ASCII_MONO, Aspect_TODT_SUBTITLE, Quantity_Color() );
  myOverlay->SetOrtho( 0, Max( aW, aH ), Max( aW, aH ), 0, Aspect_TOC_TOP_LEFT );

  return Standard_True;
}

void V3d_LayerMgr::Redraw()
{

}

void V3d_LayerMgr::End()
{
  if ( !myOverlay.IsNull() )
    myOverlay->End();
}
