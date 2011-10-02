// Viewer3dView.cpp : implementation of the CViewer3dView class
//

#include "stdafx.h"

#include "Viewer3dView.h"

#include "OCC_MainFrame.h"
#include "Viewer3dApp.h"
#include "Viewer3dDoc.h"
#include "ZClippingDlg.h"
#include "ZCueingDlg.h"
#include "ScaleDlg.h"
#include "ShadingModelDlg.h"
#include "ModelClippingDlg.h"
#include "TrihedronDlg.h"

#include <V3d_AmbientLight.hxx>
#include <V3d_DirectionalLight.hxx>
#include <V3d_PositionalLight.hxx>
#include <V3d_PositionLight.hxx>
#include <V3d_SpotLight.hxx>
#include <TopoDS_Solid.hxx>
#include <Precision.hxx>
#include <Graphic3d_GraphicDriver.hxx>

#define ValZWMin 1
#define X_Key 0x58
#define Y_Key 0x59
#define Z_Key 0x5A

#define ModelClipping

#ifdef _DEBUG
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

//gp_Pnt ConvertClickToPoint(Standard_Real x, Standard_Real y, Handle(V3d_View) aView);

	gp_Pnt p1,p2,p3;
	Handle(AIS_Shape) spotConeShape=new AIS_Shape(TopoDS_Solid());
	Handle(AIS_Shape) directionalEdgeShape=new AIS_Shape(TopoDS_Edge());

/////////////////////////////////////////////////////////////////////////////
// CViewer3dView

IMPLEMENT_DYNCREATE(CViewer3dView, CView)

BEGIN_MESSAGE_MAP(CViewer3dView, CView)
	//{{AFX_MSG_MAP(CViewer3dView)
	ON_COMMAND(ID_BUTTONAxo, OnBUTTONAxo)
	ON_COMMAND(ID_BUTTONBack, OnBUTTONBack)
	ON_COMMAND(ID_BUTTONBottom, OnBUTTONBottom)
	ON_COMMAND(ID_BUTTONFront, OnBUTTONFront)
	ON_COMMAND(ID_BUTTONHlrOff, OnBUTTONHlrOff)
	ON_COMMAND(ID_BUTTONHlrOn, OnBUTTONHlrOn)
	ON_COMMAND(ID_BUTTONLeft, OnBUTTONLeft)
	ON_COMMAND(ID_BUTTONPan, OnBUTTONPan)
	ON_COMMAND(ID_BUTTONPanGlo, OnBUTTONPanGlo)
	ON_COMMAND(ID_BUTTONReset, OnBUTTONReset)
	ON_COMMAND(ID_BUTTONRight, OnBUTTONRight)
	ON_COMMAND(ID_BUTTONRot, OnBUTTONRot)
	ON_COMMAND(ID_BUTTONTop, OnBUTTONTop)
	ON_COMMAND(ID_BUTTONZoomAll, OnBUTTONZoomAll)
	ON_WM_SIZE()
	ON_COMMAND(ID_BUTTONZoomProg, OnBUTTONZoomProg)
	ON_COMMAND(ID_BUTTONZoomWin, OnBUTTONZoomWin)
	ON_WM_LBUTTONDOWN()
  ON_WM_KEYDOWN()
	ON_WM_LBUTTONUP()
	ON_WM_MBUTTONDOWN()
	ON_WM_MBUTTONUP()
	ON_WM_MOUSEMOVE()
	ON_WM_RBUTTONDOWN()
	ON_WM_RBUTTONUP()
	ON_UPDATE_COMMAND_UI(ID_BUTTONHlrOff, OnUpdateBUTTONHlrOff)
	ON_UPDATE_COMMAND_UI(ID_BUTTONHlrOn, OnUpdateBUTTONHlrOn)
	ON_UPDATE_COMMAND_UI(ID_BUTTONPanGlo, OnUpdateBUTTONPanGlo)
	ON_UPDATE_COMMAND_UI(ID_BUTTONPan, OnUpdateBUTTONPan)
	ON_UPDATE_COMMAND_UI(ID_BUTTONZoomProg, OnUpdateBUTTONZoomProg)
	ON_UPDATE_COMMAND_UI(ID_BUTTONZoomWin, OnUpdateBUTTONZoomWin)
	ON_UPDATE_COMMAND_UI(ID_BUTTONRot, OnUpdateBUTTONRot)
	ON_COMMAND(ID_Modify_ChangeBackground     , OnModifyChangeBackground)
	ON_COMMAND(ID_DIRECTIONAL_LIGHT, OnDirectionalLight)
	ON_COMMAND(ID_SPOT_LIGHT, OnSpotLight)
	ON_COMMAND(ID_POSITIONAL_LIGHT, OnPositionalLight)
	ON_COMMAND(ID_AMBIENT_LIGHT, OnAmbientLight)
	ON_COMMAND(ID_ZCUEING, OnZcueing)
	ON_COMMAND(ID_SCALE, OnScale)
	ON_COMMAND(ID_ZCLIPPING, OnZclipping)
	ON_COMMAND(ID_SHADINGMODEL, OnShadingmodel)
	ON_COMMAND(ID_ANTIALIASINGONOFF, OnAntialiasingonoff)
	ON_COMMAND(ID_CLEAR_LIGHTS, OnClearLights)
	ON_COMMAND(ID_MODELCLIPPING, OnModelclipping)
	ON_COMMAND(ID_OPTIONS_TRIHEDRON_STATIC_TRIHEDRON, OnOptionsTrihedronStaticTrihedron)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CViewer3dView construction/destruction

CViewer3dView::CViewer3dView()
{
	// TODO: add construction code here
    myXmin=0;
    myYmin=0;  
    myXmax=0;
    myYmax=0;
    myCurZoom=0;
	myWidth=0;
	myHeight=0;

    scaleX = 1;
    scaleY = 1;
    scaleZ = 1;

	myAxisKey = 0;
	myScaleDirection = 0;


	myVisMode = VIS_SHADE;

    // will be set in OnInitial update, but, for more security :
    myCurrentMode = CurAction3d_Nothing;
    myDegenerateModeIsOn=Standard_True;
    m_Pen = NULL;
	NbActiveLights=2; // There are 2 default active lights
	myGraphicDriver = Handle(Graphic3d_GraphicDriver)::DownCast( 
		((CViewer3dApp*)AfxGetApp())->GetGraphicDevice()->GraphicDriver() );
}

CViewer3dView::~CViewer3dView()
{
	myView->Remove();
    if (m_Pen) delete m_Pen;
}

BOOL CViewer3dView::PreCreateWindow(CREATESTRUCT& cs)
{
	// TODO: Modify the Window class or styles here by modifying
	//  the CREATESTRUCT cs

	return CView::PreCreateWindow(cs);
}

/////////////////////////////////////////////////////////////////////////////
// CViewer3dView drawing
void CViewer3dView::OnInitialUpdate() 
{
	CView::OnInitialUpdate();
	
	
    myView = GetDocument()->GetViewer()->CreateView();

    // set the default mode in wireframe ( not hidden line ! )
    myView->SetDegenerateModeOn();
    // store for restore state after rotation (witch is in Degenerated mode)
    myDegenerateModeIsOn = Standard_True;


	Handle(Graphic3d_WNTGraphicDevice) theGraphicDevice = 
		((CViewer3dApp*)AfxGetApp())->GetGraphicDevice();
    
    Handle(WNT_Window) aWNTWindow = new WNT_Window(theGraphicDevice,GetSafeHwnd ());
    myView->SetWindow(aWNTWindow);
    if (!aWNTWindow->IsMapped()) aWNTWindow->Map();

//	Standard_Integer w=100 , h=100 ;   /* Debug Matrox                         */
//	aWNTWindow->Size (w,h) ;           /* Keeps me unsatisfied (rlb).....      */
	                                   /* Resize is not supposed to be done on */
	                                   /* Matrox                               */
	                                   /* I suspect another problem elsewhere  */
//	::PostMessage ( GetSafeHwnd () , WM_SIZE , SIZE_RESTORED , w + h*65536 ) ;

    // store the mode ( nothing , dynamic zooming, dynamic ... )
    myCurrentMode = CurAction3d_Nothing;
	myVisMode = VIS_SHADE;
	RedrawVisMode();
}

void CViewer3dView::OnDraw(CDC* pDC)
{
	CViewer3dDoc* pDoc = GetDocument();
	ASSERT_VALID(pDoc);
	CRect aRect;
	GetWindowRect(aRect);
	if(myWidth != aRect.Width() || myHeight != aRect.Height()) {
		myWidth = aRect.Width();
		myHeight = aRect.Height();
		::PostMessage ( GetSafeHwnd () , WM_SIZE , SW_SHOW , myWidth + myHeight*65536 );
	}
	myView->Redraw();

}

/////////////////////////////////////////////////////////////////////////////
// CViewer3dView diagnostics

#ifdef _DEBUG
void CViewer3dView::AssertValid() const
{
	CView::AssertValid();
}

void CViewer3dView::Dump(CDumpContext& dc) const
{
	CView::Dump(dc);
}

CViewer3dDoc* CViewer3dView::GetDocument() // non-debug version is inline
{
	ASSERT(m_pDocument->IsKindOf(RUNTIME_CLASS(CViewer3dDoc)));
	return (CViewer3dDoc*)m_pDocument;
}
#endif //_DEBUG

/////////////////////////////////////////////////////////////////////////////
// CViewer3dView message handlers

gp_Pnt ConvertClickToPoint(Standard_Real x, Standard_Real y, Handle(V3d_View) aView)
{
	V3d_Coordinate XEye,YEye,ZEye,XAt,YAt,ZAt;
	aView->Eye(XEye,YEye,ZEye);
	aView->At(XAt,YAt,ZAt);
	gp_Pnt EyePoint(XEye,YEye,ZEye);
	gp_Pnt AtPoint(XAt,YAt,ZAt);

	gp_Vec EyeVector(EyePoint,AtPoint);
	gp_Dir EyeDir(EyeVector);

	gp_Pln PlaneOfTheView = gp_Pln(AtPoint,EyeDir);
	Standard_Real X,Y,Z;
	aView->Convert(int(x),int(y),X,Y,Z);
	gp_Pnt ConvertedPoint(X,Y,Z);
	gp_Pnt2d ConvertedPointOnPlane = ProjLib::Project(PlaneOfTheView,ConvertedPoint);
	
	gp_Pnt ResultPoint = ElSLib::Value(ConvertedPointOnPlane.X(),
									ConvertedPointOnPlane.Y(),
									PlaneOfTheView);
	return ResultPoint;
}

void CViewer3dView::OnSize(UINT nType, int cx, int cy) 
{
  if (!myView.IsNull())
   myView->MustBeResized();
}

void CViewer3dView::OnBUTTONBack() 
{ 
	myView->SetProj(V3d_Xneg); 

TCollection_AsciiString Message("\
myView->SetProj(V3d_Xneg);\n\
  ");

  // Update The Result Message Dialog
GetDocument()->UpdateResultMessageDlg("SetProj",Message);
} // See the back View
void CViewer3dView::OnBUTTONFront() 
{ 
	myView->SetProj(V3d_Xpos); 

TCollection_AsciiString Message("\
myView->SetProj(V3d_Xpos);\n\
  ");

  // Update The Result Message Dialog
GetDocument()->UpdateResultMessageDlg("SetProj",Message);
} // See the front View

void CViewer3dView::OnBUTTONBottom() 
{ 
	myView->SetProj(V3d_Zneg); 

TCollection_AsciiString Message("\
myView->SetProj(V3d_Zneg);\n\
  ");

  // Update The Result Message Dialog
GetDocument()->UpdateResultMessageDlg("SetProj",Message);
} // See the bottom View
void CViewer3dView::OnBUTTONTop() 
{ 
	myView->SetProj(V3d_Zpos); 

TCollection_AsciiString Message("\
myView->SetProj(V3d_Zpos);\n\
  ");

  // Update The Result Message Dialog
GetDocument()->UpdateResultMessageDlg("SetProj",Message);
} // See the top View	

void CViewer3dView::OnBUTTONLeft() 
{ 
	myView->SetProj(V3d_Ypos); 

TCollection_AsciiString Message("\
myView->SetProj(V3d_Ypos);\n\
  ");

  // Update The Result Message Dialog
GetDocument()->UpdateResultMessageDlg("SetProj",Message);
} // See the left View	
void CViewer3dView::OnBUTTONRight() 
{ 
	myView->SetProj(V3d_Yneg); 

TCollection_AsciiString Message("\
myView->SetProj(V3d_Yneg);\n\
  ");

  // Update The Result Message Dialog
GetDocument()->UpdateResultMessageDlg("SetProj",Message);
} // See the right View

void CViewer3dView::OnBUTTONAxo() 
{ 
	myView->SetProj(V3d_XposYnegZpos); 

TCollection_AsciiString Message("\
myView->SetProj(V3d_XposYnegZpos);\n\
  ");

  // Update The Result Message Dialog
GetDocument()->UpdateResultMessageDlg("SetProj",Message);
} // See the axonometric View

void CViewer3dView::OnBUTTONHlrOff() 
{
  myView->SetDegenerateModeOn();
  myDegenerateModeIsOn = Standard_True;

TCollection_AsciiString Message("\
myView->SetDegenerateModeOn();\n\
  ");

  // Update The Result Message Dialog
GetDocument()->UpdateResultMessageDlg("SetDegenerateModeOn",Message);
}

void CViewer3dView::OnBUTTONHlrOn() 
{
  SetCursor(AfxGetApp()->LoadStandardCursor(IDC_WAIT));
  myView->SetDegenerateModeOff();
  myDegenerateModeIsOn = Standard_False;
  SetCursor(AfxGetApp()->LoadStandardCursor(IDC_ARROW));

TCollection_AsciiString Message("\
myView->SetDegenerateModeOff();\n\
  ");

  // Update The Result Message Dialog
GetDocument()->UpdateResultMessageDlg("SetDegenerateModeOff",Message);
}

void CViewer3dView::OnBUTTONPan() 
{  myCurrentMode = CurAction3d_DynamicPanning; }

void CViewer3dView::OnBUTTONPanGlo() 
{
  // save the current zoom value 
  myCurZoom = myView->Scale();
  // Do a Global Zoom 
  myView->FitAll();
  // Set the mode 
  myCurrentMode = CurAction3d_GlobalPanning;
}

void CViewer3dView::OnBUTTONReset() 
{   myView->Reset(); }

void CViewer3dView::OnBUTTONRot() 
{   myCurrentMode = CurAction3d_DynamicRotation; }


void CViewer3dView::OnBUTTONZoomAll() 
{
  myView->FitAll();
  myView->ZFitAll();
}

void CViewer3dView::OnBUTTONZoomProg() 
{  myCurrentMode = CurAction3d_DynamicZooming; }

void CViewer3dView::OnBUTTONZoomWin() 
{  myCurrentMode = CurAction3d_WindowZooming; }

void CViewer3dView::OnLButtonDown(UINT nFlags, CPoint point) 
{
  //  save the current mouse coordinate in min 
  myXmin=point.x;  myYmin=point.y;
  myXmax=point.x;  myYmax=point.y;

  if ( nFlags & MK_CONTROL ) 
	  {
	    // Button MB1 down Control :start zomming 
        // SetCursor(AfxGetApp()->LoadStandardCursor());
	  }
	else // if ( Ctrl )
	  {
        switch (myCurrentMode)
        {
         case CurAction3d_Nothing : // start a drag
           if (nFlags & MK_SHIFT)
       	        GetDocument()->ShiftDragEvent(myXmax,myYmax,-1,myView);
           else
                GetDocument()->DragEvent(myXmax,myYmax,-1,myView);
        break;
         break;
         case CurAction3d_DynamicZooming : // noting
         break;
         case CurAction3d_WindowZooming : 
         break;
         case CurAction3d_DynamicPanning :// noting
         break;
         case CurAction3d_GlobalPanning :// noting
        break;
        case  CurAction3d_DynamicRotation :
			if (!myDegenerateModeIsOn)
			  myView->SetDegenerateModeOn();
			myView->StartRotation(point.x,point.y);  
        break;
        case  CurAction3d_BeginPositionalLight :
			{
			p1 = ConvertClickToPoint(point.x,point.y,myView);
			myCurrent_PositionalLight->SetPosition(p1.X(),p1.Y(),p1.Z()) ;
			GetDocument()->GetAISContext()->CloseLocalContext();
			((OCC_MainFrame*)AfxGetMainWnd())->SetStatusMessage("Ready");
			myCurrentMode = CurAction3d_Nothing;

TCollection_AsciiString Message("\
myCurrent_PositionalLight->SetPosition(Xp, Yp, Zp) ;\n\
");
// Update The Result Message Dialog
GetDocument()->UpdateResultMessageDlg("SetPosition",Message);
			}
        break;
        case  CurAction3d_BeginSpotLight :
			{
			p1 = ConvertClickToPoint(point.x,point.y,myView);
			myCurrent_SpotLight = new V3d_SpotLight(myView->Viewer(),0.,0.,1., p1.X(),p1.Y(),p1.Z(),Quantity_NOC_RED);
			myView->SetLightOn(myCurrent_SpotLight);
			NbActiveLights++;
			p2 = gp_Pnt(p1.X(),p1.Y(),p1.Z()+1.);
			Standard_Real coneHeigth=p1.Distance(p2);
			BRepPrimAPI_MakeCone MakeCone(gp_Ax2(p1, gp_Dir(gp_Vec(p1, p2))), 
				0, (p1.Distance(p2))/tan(1.04), coneHeigth);
			spotConeShape->Set(MakeCone.Solid());
			GetDocument()->GetAISContext()->Display(spotConeShape,0,-1);
			((OCC_MainFrame*)AfxGetMainWnd())->SetStatusMessage("Pick the target point");
			myCurrentMode = CurAction3d_TargetSpotLight;

TCollection_AsciiString Message("\
myCurrent_SpotLight->SetDirection(Xv, Yv, Zv);\n\
");
// Update The Result Message Dialog
GetDocument()->UpdateResultMessageDlg("SetDirection",Message);
			}
        break;
        case  CurAction3d_TargetSpotLight :
			{
			p2 = ConvertClickToPoint(point.x,point.y,myView);
			((OCC_MainFrame*)AfxGetMainWnd())->SetStatusMessage("Pick a third point (to define the angle)");
			myCurrentMode = CurAction3d_EndSpotLight;

TCollection_AsciiString Message("\
myCurrent_SpotLight->SetAngle(Angle) ;\n\
");
// Update The Result Message Dialog
GetDocument()->UpdateResultMessageDlg("SetAngle",Message);
			}
		break;
        case  CurAction3d_EndSpotLight :
			GetDocument()->GetAISContext()->Erase(spotConeShape);
			GetDocument()->GetAISContext()->CloseLocalContext();
			((OCC_MainFrame*)AfxGetMainWnd())->SetStatusMessage("Ready");
			myCurrentMode = CurAction3d_Nothing;
        break;
		case CurAction3d_BeginDirectionalLight:
			{
				p1 = ConvertClickToPoint(point.x,point.y,myView);
				p2 = gp_Pnt(p1.X(),p1.Y(),p1.Z()+1.);
				BRepBuilderAPI_MakeEdge MakeEdge(p1, p2);
				directionalEdgeShape->Set(MakeEdge.Edge());
				GetDocument()->GetAISContext()->Display(directionalEdgeShape,0,-1);
			// Create a directional light
				myCurrent_DirectionalLight = new V3d_DirectionalLight(myView->Viewer(), p1.X(),p1.Y(),p1.Z(),0.,0.,1.);
				myView->SetLightOn(myCurrent_DirectionalLight);
				NbActiveLights++;
				((OCC_MainFrame*)AfxGetMainWnd())->SetStatusMessage("Pick the target point");
				myCurrentMode = CurAction3d_EndDirectionalLight;


TCollection_AsciiString Message("\
myCurrent_DirectionalLight->SetDirection(Xv, Yv, Zv);\n\
");
// Update The Result Message Dialog
GetDocument()->UpdateResultMessageDlg("SetDirection",Message);
			}
		break;
		case CurAction3d_EndDirectionalLight:
			GetDocument()->GetAISContext()->Erase(directionalEdgeShape);
			GetDocument()->GetAISContext()->CloseLocalContext();
			((OCC_MainFrame*)AfxGetMainWnd())->SetStatusMessage("Ready");
			myCurrentMode = CurAction3d_Nothing;
		break;
        }
    }
}

void CViewer3dView::OnKeyDown(UINT nChar, UINT nRepCnt, UINT nFlags) 
{
  
  if( nChar == X_Key || nChar == Y_Key || nChar == Z_Key )
    myAxisKey = nChar;
  else if( (nChar == VK_ADD || nChar == VK_SUBTRACT) && myAxisKey )
  {
    myScaleDirection = nChar;
    if( myAxisKey && myScaleDirection )
    {
      switch( myAxisKey )
      {
      case X_Key:
        if( myScaleDirection == VK_ADD ) scaleX ++;
        if( myScaleDirection == VK_SUBTRACT ) scaleX --;
        break;
      case Y_Key:
        if( myScaleDirection == VK_ADD ) scaleY ++;
        if( myScaleDirection == VK_SUBTRACT ) scaleY --;
        break;
      case Z_Key:
        if( myScaleDirection == VK_ADD ) scaleZ ++;
        if( myScaleDirection == VK_SUBTRACT ) scaleZ --;
        break;
      default:
        break;
      }
      if( scaleX < 1 ) scaleX = 1;
      if( scaleY < 1 ) scaleY = 1;
      if( scaleZ < 1 ) scaleZ = 1;
      
      myView->SetAxialScale( scaleX, scaleY, scaleZ );
    }
  }
}

void CViewer3dView::OnLButtonUp(UINT nFlags, CPoint point) 
{
   if ( nFlags & MK_CONTROL ) 
	  {
        return;
	  }
	else // if ( Ctrl )
	  {
        switch (myCurrentMode)
        {
         case CurAction3d_Nothing :
         if (point.x == myXmin && point.y == myYmin)
         { // no offset between down and up --> selectEvent
            myXmax=point.x;  
            myYmax=point.y;
            if (nFlags & MK_SHIFT )
              GetDocument()->ShiftInputEvent(point.x,point.y,myView);
            else
              GetDocument()->InputEvent     (point.x,point.y,myView);
         } else
         {
            DrawRectangle(myXmin,myYmin,myXmax,myYmax,Standard_False);
            myXmax=point.x;  
            myYmax=point.y;
		    if (nFlags & MK_SHIFT)
				GetDocument()->ShiftDragEvent(point.x,point.y,1,myView);
			else
				GetDocument()->DragEvent(point.x,point.y,1,myView);
         }
         break;
         case CurAction3d_DynamicZooming :
             // SetCursor(AfxGetApp()->LoadStandardCursor());         
	       myCurrentMode = CurAction3d_Nothing;
         break;
         case CurAction3d_WindowZooming :
           DrawRectangle(myXmin,myYmin,myXmax,myYmax,Standard_False);
           myXmax=point.x;  
           myYmax=point.y;
	       if ((abs(myXmin-myXmax)>ValZWMin) || (abs(myYmin-myYmax)>ValZWMin))
					 // Test if the zoom window is greater than a minimale window.
			{
			  // Do the zoom window between Pmin and Pmax
			  myView->WindowFitAll(myXmin,myYmin,myXmax,myYmax);  
			}  
	       myCurrentMode = CurAction3d_Nothing;
         break;
         case CurAction3d_DynamicPanning :
           myCurrentMode = CurAction3d_Nothing;
         break;
         case CurAction3d_GlobalPanning :
	       myView->Place(point.x,point.y,myCurZoom); 
	       myCurrentMode = CurAction3d_Nothing;
        break;
        case  CurAction3d_DynamicRotation :
	       myCurrentMode = CurAction3d_Nothing;
        break;
        } //switch (myCurrentMode)
    } //	else // if ( Ctrl )
}

void CViewer3dView::OnMButtonDown(UINT nFlags, CPoint point) 
{
   if ( nFlags & MK_CONTROL ) 
	  {
      	// Button MB2 down Control : panning init  
        // SetCursor(AfxGetApp()->LoadStandardCursor());   
	  }
}

void CViewer3dView::OnMButtonUp(UINT nFlags, CPoint point) 
{
   if ( nFlags & MK_CONTROL ) 
	  {
      	// Button MB2 down Control : panning init  
        // SetCursor(AfxGetApp()->LoadStandardCursor());   
	  }
}

void CViewer3dView::OnRButtonDown(UINT nFlags, CPoint point) 
{
   if ( nFlags & MK_CONTROL ) 
	  {
        // SetCursor(AfxGetApp()->LoadStandardCursor());   
	    if (!myDegenerateModeIsOn)
	      myView->SetDegenerateModeOn();
	      myView->StartRotation(point.x,point.y);  
	  }
	else // if ( Ctrl )
	  {
	    GetDocument()->Popup(point.x,point.y,myView);
      }	
}

void CViewer3dView::OnRButtonUp(UINT nFlags, CPoint point) 
{
    SetCursor(AfxGetApp()->LoadStandardCursor(IDC_WAIT));
    if (!myDegenerateModeIsOn)
    {  
      myView->SetDegenerateModeOff();
      myDegenerateModeIsOn = Standard_False;
    } else
    {
      myView->SetDegenerateModeOn();
      myDegenerateModeIsOn = Standard_True;
    }
    SetCursor(AfxGetApp()->LoadStandardCursor(IDC_ARROW));
}

void CViewer3dView::OnMouseMove(UINT nFlags, CPoint point) 
{
    //   ============================  LEFT BUTTON =======================
  if ( nFlags & MK_LBUTTON)
    {
     if ( nFlags & MK_CONTROL ) 
	  {
	    // move with MB1 and Control : on the dynamic zooming  
	    // Do the zoom in function of mouse's coordinates  
	    myView->Zoom(myXmax,myYmax,point.x,point.y); 
	    // save the current mouse coordinate in min 
		myXmax = point.x; 
        myYmax = point.y;	
	  }
	  else // if ( Ctrl )
	  {
        switch (myCurrentMode)
        {
         case CurAction3d_Nothing :
		   myXmax = point.x;      myYmax = point.y;
       	   DrawRectangle(myXmin,myYmin,myXmax,myYmax,Standard_False);
           if (nFlags & MK_SHIFT)		
       	     GetDocument()->ShiftDragEvent(myXmax,myYmax,0,myView);
           else
             GetDocument()->DragEvent(myXmax,myYmax,0,myView);
           DrawRectangle(myXmin,myYmin,myXmax,myYmax,Standard_True);
           break;
         case CurAction3d_DynamicZooming :
	       myView->Zoom(myXmax,myYmax,point.x,point.y); 
	       // save the current mouse coordinate in min \n";
	       myXmax=point.x;  myYmax=point.y;
         break;
         case CurAction3d_WindowZooming :
		   myXmax = point.x; myYmax = point.y;	
       	   DrawRectangle(myXmin,myYmin,myXmax,myYmax,Standard_False,LongDash);
       	   DrawRectangle(myXmin,myYmin,myXmax,myYmax,Standard_True,LongDash);

         break;
         case CurAction3d_DynamicPanning :
		   myView->Pan(point.x-myXmax,myYmax-point.y); // Realize the panning
		   myXmax = point.x; myYmax = point.y;	
         break;
         case CurAction3d_GlobalPanning : // nothing           
        break;
        case  CurAction3d_DynamicRotation :
          myView->Rotation(point.x,point.y);
	      myView->Redraw();
        break;
        }//  switch (myCurrentMode)
      }// if ( nFlags & MK_CONTROL )  else 
    } else //   if ( nFlags & MK_LBUTTON) 
    //   ============================  MIDDLE BUTTON =======================
    if ( nFlags & MK_MBUTTON)
    {
     if ( nFlags & MK_CONTROL ) 
	  {
		myView->Pan(point.x-myXmax,myYmax-point.y); // Realize the panning
		myXmax = point.x; myYmax = point.y;	

	  }
    } else //  if ( nFlags & MK_MBUTTON)
    //   ============================  RIGHT BUTTON =======================
    if ( nFlags & MK_RBUTTON)
    {
     if ( nFlags & MK_CONTROL ) 
	  {
      	 myView->Rotation(point.x,point.y);
	  }
    }else //if ( nFlags & MK_RBUTTON)
    //   ============================  NO BUTTON =======================
    {  // No buttons 
		myXmax = point.x; myYmax = point.y;	
		if (myCurrentMode ==  CurAction3d_EndDirectionalLight) 
		{
			p2 = ConvertClickToPoint(point.x,point.y,myView);
			//Update the light dynamically
			if( p1.Distance(p2)>Precision::Confusion())
			{
				BRepBuilderAPI_MakeEdge MakeEdge(p1, p2);
				directionalEdgeShape->Set(MakeEdge.Edge());
				GetDocument()->GetAISContext()->Redisplay(directionalEdgeShape,0,-1);
				myCurrent_DirectionalLight->SetDirection(p2.X()-p1.X(),p2.Y()-p1.Y(),p2.Z()-p1.Z());
				myView->UpdateLights();
			}
		}
		else if (myCurrentMode ==  CurAction3d_BeginPositionalLight) 
		{
			p2 = ConvertClickToPoint(point.x,point.y,myView);
			//Update the light dynamically
			myCurrent_PositionalLight->SetPosition(p2.X(),p2.Y(),p2.Z());
			myView->UpdateLights();
		}
		else if (myCurrentMode ==  CurAction3d_TargetSpotLight) 
		{
			p2 = ConvertClickToPoint(point.x,point.y,myView);
			//Update the light dynamically
			Standard_Real coneHeigth=p1.Distance(p2);
			if( coneHeigth>Precision::Confusion())
			{
				BRepPrimAPI_MakeCone MakeCone(gp_Ax2(p1, gp_Dir(gp_Vec(p1, p2))), 
					0, (p1.Distance(p2))/tan(1.04), coneHeigth);
				spotConeShape->Set(MakeCone.Solid());
				GetDocument()->GetAISContext()->Redisplay(spotConeShape,0,-1);
				myCurrent_SpotLight->SetDirection(p2.X()-p1.X(),p2.Y()-p1.Y(),p2.Z()-p1.Z());
				myView->UpdateLights();
			}
		}
		else if (myCurrentMode ==  CurAction3d_EndSpotLight) 
		{
			p3 = ConvertClickToPoint(point.x,point.y,myView);
			//Update the light dynamically
			Standard_Real coneHeigth=p1.Distance(p2);
			if( (p2.Distance(p3))>Precision::Confusion())
			{
				BRepPrimAPI_MakeCone MakeCone(gp_Ax2(p1, gp_Dir(gp_Vec(p1, p2))), 
					0, p2.Distance(p3), coneHeigth);
				spotConeShape->Set(MakeCone.Solid());
				GetDocument()->GetAISContext()->Redisplay(spotConeShape,0,-1);
				myCurrent_SpotLight->SetAngle(atan(p2.Distance(p3)/p1.Distance(p2))) ;
				myView->UpdateLights();
			}
		}
		if (nFlags & MK_SHIFT)
			GetDocument()->ShiftMoveEvent(point.x,point.y,myView);
		else
			GetDocument()->MoveEvent(point.x,point.y,myView);
	}
}

void CViewer3dView::OnUpdateBUTTONHlrOff(CCmdUI* pCmdUI) 
{
    pCmdUI->SetCheck (myDegenerateModeIsOn);
	pCmdUI->Enable   (!myDegenerateModeIsOn);	
}

void CViewer3dView::OnUpdateBUTTONHlrOn(CCmdUI* pCmdUI) 
{
    pCmdUI->SetCheck (!myDegenerateModeIsOn);
	pCmdUI->Enable   (myDegenerateModeIsOn);	
}

void CViewer3dView::OnUpdateBUTTONPanGlo(CCmdUI* pCmdUI) 
{
    pCmdUI->SetCheck (myCurrentMode == CurAction3d_GlobalPanning);
	pCmdUI->Enable   (myCurrentMode != CurAction3d_GlobalPanning);	
	
}

void CViewer3dView::OnUpdateBUTTONPan(CCmdUI* pCmdUI) 
{
    pCmdUI->SetCheck (myCurrentMode == CurAction3d_DynamicPanning);
	pCmdUI->Enable   (myCurrentMode != CurAction3d_DynamicPanning );	
}

void CViewer3dView::OnUpdateBUTTONZoomProg(CCmdUI* pCmdUI) 
{
    pCmdUI->SetCheck (myCurrentMode == CurAction3d_DynamicZooming );
	pCmdUI->Enable   (myCurrentMode != CurAction3d_DynamicZooming);	
}

void CViewer3dView::OnUpdateBUTTONZoomWin(CCmdUI* pCmdUI) 
{
    pCmdUI->SetCheck (myCurrentMode == CurAction3d_WindowZooming);
	pCmdUI->Enable   (myCurrentMode != CurAction3d_WindowZooming);	
}

void CViewer3dView::OnUpdateBUTTONRot(CCmdUI* pCmdUI) 
{
    pCmdUI->SetCheck (myCurrentMode == CurAction3d_DynamicRotation);
	pCmdUI->Enable   (myCurrentMode != CurAction3d_DynamicRotation);	
}

void CViewer3dView::DrawRectangle(const Standard_Integer  MinX    ,
					                    const Standard_Integer  MinY    ,
                                        const Standard_Integer  MaxX ,
					                    const Standard_Integer  MaxY ,
					                    const Standard_Boolean  Draw , 
                                        const LineStyle aLineStyle)
{
    static int m_DrawMode;
    if  (!m_Pen && aLineStyle ==Solid )
        {m_Pen = new CPen(PS_SOLID, 1, RGB(0,0,0)); m_DrawMode = R2_MERGEPENNOT;}
    else if (!m_Pen && aLineStyle ==Dot )
        {m_Pen = new CPen(PS_DOT, 1, RGB(0,0,0));   m_DrawMode = R2_XORPEN;}
    else if (!m_Pen && aLineStyle == ShortDash)
        {m_Pen = new CPen(PS_DASH, 1, RGB(255,0,0));	m_DrawMode = R2_XORPEN;}
    else if (!m_Pen && aLineStyle == LongDash)
        {m_Pen = new CPen(PS_DASH, 1, RGB(0,0,0));	m_DrawMode = R2_NOTXORPEN;}
    else if (aLineStyle == Default) 
        { m_Pen = NULL;	m_DrawMode = R2_MERGEPENNOT;}

    CPen* aOldPen;
    CClientDC clientDC(this);
    if (m_Pen) aOldPen = clientDC.SelectObject(m_Pen);
    clientDC.SetROP2(m_DrawMode);

    static		Standard_Integer StoredMinX, StoredMaxX, StoredMinY, StoredMaxY;
    static		Standard_Boolean m_IsVisible;

    if ( m_IsVisible && !Draw) // move or up  : erase at the old position 
    {
     clientDC.MoveTo(StoredMinX,StoredMinY); 
     clientDC.LineTo(StoredMinX,StoredMaxY); 
     clientDC.LineTo(StoredMaxX,StoredMaxY); 
	   clientDC.LineTo(StoredMaxX,StoredMinY); 
     clientDC.LineTo(StoredMinX,StoredMinY);
     m_IsVisible = false;
    }

    StoredMinX = min ( MinX, MaxX );
    StoredMinY = min ( MinY, MaxY );
    StoredMaxX = max ( MinX, MaxX );
    StoredMaxY = max ( MinY, MaxY);

    if (Draw) // move : draw
    {
     clientDC.MoveTo(StoredMinX,StoredMinY); 
     clientDC.LineTo(StoredMinX,StoredMaxY); 
     clientDC.LineTo(StoredMaxX,StoredMaxY); 
	   clientDC.LineTo(StoredMaxX,StoredMinY); 
     clientDC.LineTo(StoredMinX,StoredMinY);
     m_IsVisible = true;
   }

    if (m_Pen) 
      clientDC.SelectObject(aOldPen);
}

void CViewer3dView::OnModifyChangeBackground() 
{
	Standard_Real R1;
	Standard_Real G1;
	Standard_Real B1;
    myView->BackgroundColor(Quantity_TOC_RGB,R1,G1,B1);
	COLORREF m_clr ;
	m_clr = RGB(R1*255,G1*255,B1*255);

	CColorDialog dlgColor(m_clr);
	if (dlgColor.DoModal() == IDOK)
	{
		m_clr = dlgColor.GetColor();
		R1 = GetRValue(m_clr)/255.;
		G1 = GetGValue(m_clr)/255.;
		B1 = GetBValue(m_clr)/255.;
        myView->SetBackgroundColor(Quantity_TOC_RGB,R1,G1,B1);
	}
    myView->Redraw();
}

void CViewer3dView::OnDirectionalLight() 
{
// Directional light source creation

// Checking if the Active lights limit number is not reached
	if( NbActiveLights>=myGraphicDriver->InquireLightLimit() )
	{
		char	tmpStr[128];
		sprintf_s(tmpStr, "You have reach the limit number of active lights (%d).\n Clear lights to create new ones.",
			myGraphicDriver->InquireLightLimit());
		MessageBox(tmpStr,"Light creation", MB_OK);
		return;
	}

	UpdateData(TRUE);
	((OCC_MainFrame*)AfxGetMainWnd())->SetStatusMessage("Pick a first point");
	GetDocument()->GetAISContext()->OpenLocalContext();
	myCurrentMode = CurAction3d_BeginDirectionalLight;

TCollection_AsciiString Message("\
myCurrent_DirectionalLight = new V3d_DirectionalLight(myView->Viewer(), Xt, Yt, Zt, Xp, Yp, Zp);\n\
\n\
myView->SetLightOn(myCurrent_DirectionalLight);\n\
\n\
");
// Update The Result Message Dialog
	GetDocument()->UpdateResultMessageDlg("V3d_DirectionalLight",Message);
}

void CViewer3dView::OnSpotLight() 
{
// Spot light source creation

// Checking if the Active lights limit number is not reached
	if( NbActiveLights>=myGraphicDriver->InquireLightLimit() )
	{
		char	tmpStr[128];
		sprintf_s(tmpStr, "You have reach the limit number of active lights (%d).\n Clear lights to create new ones.",
			myGraphicDriver->InquireLightLimit());
		MessageBox(tmpStr,"Light creation", MB_OK);
		return;
	}

	GetDocument()->GetAISContext()->OpenLocalContext();
	((OCC_MainFrame*)AfxGetMainWnd())->SetStatusMessage("Pick the light position");
	myCurrentMode = CurAction3d_BeginSpotLight;

TCollection_AsciiString Message("\
myCurrent_SpotLight = new V3d_SpotLight(myView->Viewer(), Xt, Yt, Zt, Xp, Yp, Zp,Quantity_NOC_RED);\n\
\n\
myView->SetLightOn(myCurrent_SpotLight);\n\
\n\
");
// Update The Result Message Dialog
GetDocument()->UpdateResultMessageDlg("V3d_SpotLight",Message);
}

void CViewer3dView::OnPositionalLight() 
{
// Positional light source creation

// Checking if the Active lights limit number is not reached
	if( NbActiveLights>=myGraphicDriver->InquireLightLimit() )
	{
		char	tmpStr[128];
		sprintf_s(tmpStr, "You have reach the limit number of active lights (%d).\n Clear lights to create new ones.",
			myGraphicDriver->InquireLightLimit());
		MessageBox(tmpStr,"Light creation", MB_OK);
		return;
	}

	GetDocument()->GetAISContext()->OpenLocalContext();
	myCurrent_PositionalLight=new V3d_PositionalLight(myView->Viewer(),0,0,0,Quantity_NOC_GREEN,1,0);
	myView->SetLightOn(myCurrent_PositionalLight);
	NbActiveLights++;
	((OCC_MainFrame*)AfxGetMainWnd())->SetStatusMessage("Pick the light position");
	myCurrentMode = CurAction3d_BeginPositionalLight;

TCollection_AsciiString Message("\
myCurrent_PositionalLight=new V3d_PositionalLight(myView->Viewer(),Xp, Yp, Zp,Quantity_NOC_GREEN,1,0);\n\
\n\
myView->SetLightOn(myCurrent_PositionalLight) ;\n\
  ");

  // Update The Result Message Dialog
GetDocument()->UpdateResultMessageDlg("V3d_PositionalLight",Message);
}

void CViewer3dView::OnAmbientLight() 
{
// Ambiant light source creation

// Checking if the Active lights limit number is not reached
	if( NbActiveLights>=myGraphicDriver->InquireLightLimit() )
	{
		char	tmpStr[128];
		sprintf_s(tmpStr, "You have reach the limit number of active lights (%d).\n Clear lights to create new ones.",
			myGraphicDriver->InquireLightLimit());
		MessageBox(tmpStr,"Light creation", MB_OK);
		return;
	}

	GetDocument()->GetAISContext()->OpenLocalContext();
	myCurrent_AmbientLight=new V3d_AmbientLight(myView->Viewer(), Quantity_NOC_GRAY);
	myView->SetLightOn(myCurrent_AmbientLight) ;	
	NbActiveLights++;
	GetDocument()->GetAISContext()->CloseLocalContext();

	myView->UpdateLights();

TCollection_AsciiString Message("\
myCurrent_AmbientLight=new V3d_AmbientLight(myView->Viewer(), Quantity_NOC_GRAY);\n\
\n\
myView->SetLightOn(myCurrent_AmbientLight) ;\n\
  ");

  // Update The Result Message Dialog
GetDocument()->UpdateResultMessageDlg("V3d_AmbientLight",Message);
}


void CViewer3dView::OnScale() 
{
	ScaleDlg Dlg(myView, this);
	Dlg.DoModal();
	myView->Redraw();
}

void CViewer3dView::OnZcueing() 
{
	ZCueingDlg Dlg(myView, GetDocument());
	Dlg.DoModal();
	myView->Redraw();
}

void CViewer3dView::OnZclipping() 
{
	ZClippingDlg Dlg(myView, GetDocument());
	Dlg.DoModal();
	myView->Redraw();
}

//V3d_View.hxx

void CViewer3dView::OnShadingmodel() 
{
TCollection_AsciiString Message("\
myView->SetShadingModel(V3d_TypeOfShadingModel myTypeOfShadingModel);\n\
\n\
myCurrent_V3d_View->Update();\n\
  ");

  // Update The Result Message Dialog
GetDocument()->UpdateResultMessageDlg("SetShadingModel",Message);

	CShadingModelDlg Dlg(myView);
	Dlg.DoModal();
	myView->Redraw();
}


void CViewer3dView::OnAntialiasingonoff() 
{
	if(!myView->Antialiasing())
		myView->SetAntialiasingOn();
	else
		myView->SetAntialiasingOff();

	myView->Update();

TCollection_AsciiString Message("\
if(!myView->Antialiasing())\n\
	myView->SetAntialiasingOn();\n\
else\n\
	myView->SetAntialiasingOff();\n\
  ");

  // Update The Result Message Dialog
GetDocument()->UpdateResultMessageDlg("SetAntialiasingOn/SetAntialiasingOff",Message);
}

void CViewer3dView::OnClearLights() 
{
//	Setting Off all viewer active lights
	for(myView->Viewer()->InitActiveLights(); myView->Viewer()->MoreActiveLights(); myView->Viewer()->NextActiveLights())
		myView->Viewer()->SetLightOff(myView->Viewer()->ActiveLight());
//	Setting Off all view active lights
	for(myView->InitActiveLights(); myView->MoreActiveLights(); myView->NextActiveLights())
		myView->SetLightOff(myView->ActiveLight());

	myView->Viewer()->SetDefaultLights();// Setting the default lights on

	NbActiveLights = 2;// There are 2 default active lights

	myView->Update();

TCollection_AsciiString Message("\
for(myView->Viewer()->InitActiveLights(); myView->Viewer()->MoreActiveLights(); myView->Viewer()->NextActiveLights())\n\
	myView->Viewer()->SetLightOff(myView->Viewer()->ActiveLight()); //Setting Off all viewer active lights\n\
for(myView->InitActiveLights(); myView->MoreActiveLights(); myView->NextActiveLights())\n\
	myView->SetLightOff(myView->ActiveLight()); //Setting Off all view active lights\n\
\n\
myView->Viewer()->SetDefaultLights();// Setting the default lights on\n\
  ");

  // Update The Result Message Dialog
GetDocument()->UpdateResultMessageDlg("SetLightOff",Message);
}

void CViewer3dView::OnModelclipping() 
{
	if( myPlane.IsNull() ) 
	{
//creates a plane defined : center of the box ( 50,50,50) and 1 direction
		gp_Pln tmpPln(gp_Pnt(0,0,0),gp_Dir(1,0,0));
//getting the coefficients of the gp_Pln ( ax+by+cz+d = 0 )
		Standard_Real A,B,C,D;
		tmpPln.Coefficients(A,B,C,D);
//with these coefficients, creating a V3d_Plane
		myPlane = new V3d_Plane(A,B,C,D);
//		GetDocument()->GetViewer(),A,B,C,D);
//creates the Face
//NOTE : the face must be behind the clipping plane !!	  
		tmpPln = gp_Pln(gp_Pnt(0.1,0,0),gp_Dir(1,0,0));
		BRepBuilderAPI_MakeFace MakeFace(tmpPln, 200, -200, 410, -410);
		TopoDS_Face S = MakeFace.Face();
//display the face
		myShape = new AIS_Shape(S);
	}
  
	CModelClippingDlg Dlg(myView, myPlane, myShape, GetDocument());
	Dlg.DoModal();
}

void CViewer3dView::OnOptionsTrihedronStaticTrihedron() 
{
	CTrihedronDlg Dlg(myView, GetDocument());
	Dlg.DoModal();
}

void CViewer3dView::InitButtons()
{
  myXmin=0;
  myYmin=0;  
  myXmax=0;
  myYmax=0;
  myCurZoom=0;
  myCurrentMode = CurAction3d_Nothing;
}

void CViewer3dView::Reset()
{
  InitButtons();
  myVisMode = VIS_SHADE;
  if (!myView.IsNull())
  {
    RedrawVisMode();
    myView->Reset();
  }
}

void CViewer3dView::GetViewCenter(V3d_Coordinate& Xc, V3d_Coordinate& Yc)
{
	myView->Center(Xc,Yc);
}

void CViewer3dView::SetViewCenter(V3d_Coordinate Xc, V3d_Coordinate Yc)
{
	myView->SetCenter(Xc,Yc);
}

void CViewer3dView::GetViewEye(V3d_Coordinate& X, V3d_Coordinate& Y, V3d_Coordinate& Z)
{
	myView->Eye(X,Y,Z);
}

void CViewer3dView::SetViewEye(V3d_Coordinate X, V3d_Coordinate Y, V3d_Coordinate Z)
{
	myView->SetEye(X,Y,Z);
}

Quantity_Factor CViewer3dView::GetViewScale()
{
	return myView->Scale();
}

void CViewer3dView::SetViewScale(Quantity_Factor Coef)
{
	myView->SetScale(Coef);
}

void CViewer3dView::RedrawVisMode()
{
  switch (myVisMode)
  {
  case VIS_WIREFRAME:
    GetDocument()->GetAISContext()->SetDisplayMode(AIS_WireFrame);
    myView->SetComputedMode (Standard_False);
    break;
  case VIS_SHADE:
    GetDocument()->GetAISContext()->SetDisplayMode(AIS_Shaded);
    myView->SetComputedMode (Standard_False);
    break;
  case VIS_HLR:
    SetCursor(AfxGetApp()->LoadStandardCursor(IDC_WAIT));
    myView->SetComputedMode (Standard_True);
    SetCursor(AfxGetApp()->LoadStandardCursor(IDC_ARROW));
    GetDocument()->GetAISContext()->SetDisplayMode(AIS_WireFrame);
    break;
  }
}
