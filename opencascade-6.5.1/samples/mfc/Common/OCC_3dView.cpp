// OCC_3dView.cpp: implementation of the OCC_3dView class.
//

#include "stdafx.h"

#include "OCC_3dView.h"

#include "OCC_3dApp.h"
#include "OCC_3dBaseDoc.h"
#include <res\OCC_Resource.h>

#include <Visual3d_View.hxx>
#include <Graphic3d_ExportFormat.hxx>

#define ValZWMin 1

IMPLEMENT_DYNCREATE(OCC_3dView, CView)

BEGIN_MESSAGE_MAP(OCC_3dView, CView)
	//{{AFX_MSG_MAP(OCC_3dView)
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
    ON_COMMAND(ID_FILE_EXPORT_IMAGE, OnFileExportImage)
	ON_COMMAND(ID_BUTTONZoomProg, OnBUTTONZoomProg)
	ON_COMMAND(ID_BUTTONZoomWin, OnBUTTONZoomWin)
	ON_WM_LBUTTONDOWN()
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
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// OCC_3dView construction/destruction

OCC_3dView::OCC_3dView()
{
	// TODO: add construction code here
    myXmin=0;
    myYmin=0;  
    myXmax=0;
    myYmax=0;
    myCurZoom=0;
	myWidth=0;
	myHeight=0;
    // will be set in OnInitial update, but, for more security :
    myCurrentMode = CurAction3d_Nothing;
    myDegenerateModeIsOn=Standard_True;
    m_Pen = NULL;
	
}

OCC_3dView::~OCC_3dView()
{
	if ( myView )
		myView->Remove();
   	if (m_Pen) delete m_Pen;
}

BOOL OCC_3dView::PreCreateWindow(CREATESTRUCT& cs)
{
	// TODO: Modify the Window class or styles here by modifying
	//  the CREATESTRUCT cs

	return CView::PreCreateWindow(cs);
}

/////////////////////////////////////////////////////////////////////////////
// OCC_3dView drawing
void OCC_3dView::OnInitialUpdate() 
{
	CView::OnInitialUpdate();
	
    myView = GetDocument()->GetViewer()->CreateView();

    // set the default mode in wireframe ( not hidden line ! )
    myView->SetDegenerateModeOn();
    // store for restore state after rotation (witch is in Degenerated mode)
    myDegenerateModeIsOn = Standard_True;


	Handle(Graphic3d_WNTGraphicDevice) theGraphicDevice = 
		((OCC_3dApp*)AfxGetApp())->GetGraphicDevice();
    
    Handle(WNT_Window) aWNTWindow = new WNT_Window(theGraphicDevice,GetSafeHwnd ());
    myView->SetWindow(aWNTWindow);
    if (!aWNTWindow->IsMapped()) aWNTWindow->Map();

    // store the mode ( nothing , dynamic zooming, dynamic ... )
    myCurrentMode = CurAction3d_Nothing;
	
}

void OCC_3dView::OnDraw(CDC* pDC)
{
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
// OCC_3dView diagnostics

#ifdef _DEBUG
void OCC_3dView::AssertValid() const
{
	CView::AssertValid();
}

void OCC_3dView::Dump(CDumpContext& dc) const
{
	CView::Dump(dc);
}

OCC_3dBaseDoc* OCC_3dView::GetDocument() // non-debug version is inline
{
//	ASSERT(m_pDocument->IsKindOf(RUNTIME_CLASS(OCC_3dBaseDoc)));
	return (OCC_3dBaseDoc*)m_pDocument;
}

#endif //_DEBUG

/////////////////////////////////////////////////////////////////////////////
// OCC_3dView message handlers
void OCC_3dView::OnFileExportImage()
{
  LPCTSTR filter;
  filter = _T("BMP Files (*.BMP)|*.bmp|GIF Files (*.GIF)|*.gif|XWD Files (*.XWD)|*.xwd|PS Files (*.PS)|*.ps|EPS Files (*.EPS)|*.eps|TEX Files (*.TEX)|*.tex|PDF Files (*.PDF)|*.pdf|SVG Files (*.SVG)|*.svg|PGF Files (*.PGF)|*.pgf||");
  CFileDialog dlg(FALSE,_T("*.BMP"),NULL,OFN_HIDEREADONLY | OFN_OVERWRITEPROMPT,
                  filter, 
                  NULL );

  if (dlg.DoModal() == IDOK) 
  {
    SetCursor(AfxGetApp()->LoadStandardCursor(IDC_WAIT));
    CString filename = dlg.GetPathName();
    char* theFile = new char[filename.GetLength()+1];
    //_tcscpy(theFile,filename);
    strcpy_s(theFile,filename.GetLength()+1,filename);
    CString ext = dlg.GetFileExt();
    if (ext == "ps" || ext == "emf")
    {
      Graphic3d_ExportFormat exFormat;
      if (ext == "ps") exFormat = Graphic3d_EF_PostScript;
      else             exFormat = Graphic3d_EF_EnhPostScript;
      myView->View()->Export( theFile, exFormat );
      return;
    }
    Handle(Aspect_Window) anAspectWindow = myView->Window();
    Handle(WNT_Window) aWNTWindow = Handle(WNT_Window)::DownCast(anAspectWindow);
    if (ext == "bmp")     aWNTWindow->SetOutputFormat ( WNT_TOI_BMP );
    if (ext == "gif")     aWNTWindow->SetOutputFormat ( WNT_TOI_GIF );
    if (ext == "xwd")     aWNTWindow->SetOutputFormat ( WNT_TOI_XWD );
    aWNTWindow->Dump ((Standard_CString)(LPCTSTR)filename);
    SetCursor(AfxGetApp()->LoadStandardCursor(IDC_ARROW));
  }
}

void OCC_3dView::OnSize(UINT nType, int cx, int cy) 
{
  if (!myView.IsNull())
   myView->MustBeResized();
}

void OCC_3dView::OnBUTTONBack() 
{ myView->SetProj(V3d_Xneg); } // See the back View
void OCC_3dView::OnBUTTONFront() 
{ myView->SetProj(V3d_Xpos); } // See the front View

void OCC_3dView::OnBUTTONBottom() 
{ myView->SetProj(V3d_Zneg); } // See the bottom View
void OCC_3dView::OnBUTTONTop() 
{ myView->SetProj(V3d_Zpos); } // See the top View	

void OCC_3dView::OnBUTTONLeft() 
{ myView->SetProj(V3d_Ypos); } // See the left View	
void OCC_3dView::OnBUTTONRight() 
{ myView->SetProj(V3d_Yneg); } // See the right View

void OCC_3dView::OnBUTTONAxo() 
{ myView->SetProj(V3d_XposYnegZpos); } // See the axonometric View

void OCC_3dView::OnBUTTONHlrOff() 
{
  myView->SetDegenerateModeOn();
  myDegenerateModeIsOn = Standard_True;
}

void OCC_3dView::OnBUTTONHlrOn() 
{
  SetCursor(AfxGetApp()->LoadStandardCursor(IDC_WAIT));
  myView->SetDegenerateModeOff();
  myDegenerateModeIsOn = Standard_False;
  SetCursor(AfxGetApp()->LoadStandardCursor(IDC_ARROW));
}

void OCC_3dView::OnBUTTONPan() 
{  myCurrentMode = CurAction3d_DynamicPanning; }

void OCC_3dView::OnBUTTONPanGlo() 
{
  // save the current zoom value 
  myCurZoom = myView->Scale();
  // Do a Global Zoom 
  //myView->FitAll();
  // Set the mode 
  myCurrentMode = CurAction3d_GlobalPanning;
}

void OCC_3dView::OnBUTTONReset() 
{   myView->Reset(); }

void OCC_3dView::OnBUTTONRot() 
{   myCurrentMode = CurAction3d_DynamicRotation; }


void OCC_3dView::OnBUTTONZoomAll() 
{
  myView->FitAll();
  myView->ZFitAll();
}

void OCC_3dView::OnBUTTONZoomProg() 
{  myCurrentMode = CurAction3d_DynamicZooming; }

void OCC_3dView::OnBUTTONZoomWin() 
{  myCurrentMode = CurAction3d_WindowZooming; }

void OCC_3dView::OnLButtonDown(UINT nFlags, CPoint point) 
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
         case CurAction3d_WindowZooming : // noting
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
        default :
           Standard_Failure::Raise(" incompatible Current Mode ");
        break;
        }
    }
}

void OCC_3dView::OnLButtonUp(UINT nFlags, CPoint point) 
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
            myXmax=point.x;    myYmax=point.y;
            DrawRectangle(myXmin,myYmin,myXmax,myYmax,Standard_False);
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
           myXmax=point.x;        myYmax=point.y;
           DrawRectangle(myXmin,myYmin,myXmax,myYmax,Standard_False);
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
		   if (!myDegenerateModeIsOn)
			{  
			  CWaitCursor aWaitCursor;
			  myView->SetDegenerateModeOff();
			  myDegenerateModeIsOn = Standard_False;
			}
		   else
			{
			  myView->SetDegenerateModeOn();
			  myDegenerateModeIsOn = Standard_True;
			}
		break;
        default :
           Standard_Failure::Raise(" incompatible Current Mode ");
        break;
        } //switch (myCurrentMode)
    } //	else // if ( Ctrl )
}

void OCC_3dView::OnMButtonDown(UINT nFlags, CPoint point) 
{
   if ( nFlags & MK_CONTROL ) 
	  {
      	// Button MB2 down Control : panning init  
        // SetCursor(AfxGetApp()->LoadStandardCursor());   
	  }
}

void OCC_3dView::OnMButtonUp(UINT nFlags, CPoint point) 
{
   if ( nFlags & MK_CONTROL ) 
	  {
      	// Button MB2 down Control : panning init  
        // SetCursor(AfxGetApp()->LoadStandardCursor());   
	  }
}

void OCC_3dView::OnRButtonDown(UINT nFlags, CPoint point) 
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

void OCC_3dView::OnRButtonUp(UINT nFlags, CPoint point) 
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

void OCC_3dView::OnMouseMove(UINT nFlags, CPoint point) 
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
		   myXmax = point.x;  myYmax = point.y;
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
        default :
           Standard_Failure::Raise(" incompatible Current Mode ");
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
	  if (nFlags & MK_SHIFT)
		GetDocument()->ShiftMoveEvent(point.x,point.y,myView);
	  else
		GetDocument()->MoveEvent(point.x,point.y,myView);
   }
}

void OCC_3dView::DrawRectangle(const Standard_Integer  MinX    ,
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
     clientDC.MoveTo(StoredMinX,StoredMinY); clientDC.LineTo(StoredMinX,StoredMaxY); 
     clientDC.LineTo(StoredMaxX,StoredMaxY); 
	 clientDC.LineTo(StoredMaxX,StoredMinY); clientDC.LineTo(StoredMinX,StoredMinY);
     m_IsVisible = false;
    }

    StoredMinX = min ( MinX, MaxX );
    StoredMinY = min ( MinY, MaxY );
    StoredMaxX = max ( MinX, MaxX );
    StoredMaxY = max ( MinY, MaxY);

    if (Draw) // move : draw
    {
     clientDC.MoveTo(StoredMinX,StoredMinY); clientDC.LineTo(StoredMinX,StoredMaxY); 
     clientDC.LineTo(StoredMaxX,StoredMaxY); 
	 clientDC.LineTo(StoredMaxX,StoredMinY); clientDC.LineTo(StoredMinX,StoredMinY);
     m_IsVisible = true;
   }

    if (m_Pen) clientDC.SelectObject(aOldPen);
}



void OCC_3dView::OnUpdateBUTTONHlrOff(CCmdUI* pCmdUI) 
{
    pCmdUI->SetCheck (myDegenerateModeIsOn);
	pCmdUI->Enable   (!myDegenerateModeIsOn);	
}

void OCC_3dView::OnUpdateBUTTONHlrOn(CCmdUI* pCmdUI) 
{
    pCmdUI->SetCheck (!myDegenerateModeIsOn);
	pCmdUI->Enable   (myDegenerateModeIsOn);	
}

void OCC_3dView::OnUpdateBUTTONPanGlo(CCmdUI* pCmdUI) 
{
    pCmdUI->SetCheck (myCurrentMode == CurAction3d_GlobalPanning);
	pCmdUI->Enable   (myCurrentMode != CurAction3d_GlobalPanning);	
	
}

void OCC_3dView::OnUpdateBUTTONPan(CCmdUI* pCmdUI) 
{
    pCmdUI->SetCheck (myCurrentMode == CurAction3d_DynamicPanning);
	pCmdUI->Enable   (myCurrentMode != CurAction3d_DynamicPanning );	
}

void OCC_3dView::OnUpdateBUTTONZoomProg(CCmdUI* pCmdUI) 
{
    pCmdUI->SetCheck (myCurrentMode == CurAction3d_DynamicZooming );
	pCmdUI->Enable   (myCurrentMode != CurAction3d_DynamicZooming);	
}

void OCC_3dView::OnUpdateBUTTONZoomWin(CCmdUI* pCmdUI) 
{
    pCmdUI->SetCheck (myCurrentMode == CurAction3d_WindowZooming);
	pCmdUI->Enable   (myCurrentMode != CurAction3d_WindowZooming);	
}

void OCC_3dView::OnUpdateBUTTONRot(CCmdUI* pCmdUI) 
{
    pCmdUI->SetCheck (myCurrentMode == CurAction3d_DynamicRotation);
	pCmdUI->Enable   (myCurrentMode != CurAction3d_DynamicRotation);	
}

void OCC_3dView::OnModifyChangeBackground() 
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
