// AnimationDocApp.cpp : implementation of the CAnimationDoc class
//

#include "stdafx.h"

#include "AnimationApp.h"
#include "AnimationDoc.h"
#include "AnimationView3D.h"
#include "AISDialogs.h"
#include "ShadingDialog.h"
#include "ThreadDialog.h"
#include "Fonc.hxx"

#define DEFAULT_COLOR Quantity_NOC_CYAN1
#define DEFAULT_MATERIAL Graphic3d_NOM_PLASTER
#define DEFAULT_DEVIATIONCOEFFICIENT 0.001
#define DEFAULT_HILIGHTCOLOR Quantity_NOC_YELLOW


#ifdef _DEBUG
//#define new DEBUG_NEW  // by cascade
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CAnimationDoc

IMPLEMENT_DYNCREATE(CAnimationDoc, CDocument)

BEGIN_MESSAGE_MAP(CAnimationDoc, CDocument)
	//{{AFX_MSG_MAP(CAnimationDoc)
	ON_COMMAND(ID_SHADING, OnShading)
	ON_COMMAND(ID_Thread, OnThread)
	ON_COMMAND(ID_FILE_LOADGRID, OnFileLoadgrid)
	ON_UPDATE_COMMAND_UI(ID_WALK_WALKTHRU, OnUpdateWalkWalkthru)
	//}}AFX_MSG_MAP

END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CAnimationDoc construction/destruction

CAnimationDoc::CAnimationDoc()
{
	// TODO: add one-time construction code here

	static Standard_Integer StaticCount=1;
	StaticCount++;
	myCount = StaticCount;

	Handle(Graphic3d_WNTGraphicDevice) theGraphicDevice = 
		((CAnimationApp*)AfxGetApp())->GetGraphicDevice();

    TCollection_ExtendedString a3DName("Visu3D");
	myViewer = new V3d_Viewer(theGraphicDevice,a3DName.ToExtString(),"", 1000.0, 
                              V3d_XposYnegZpos, Quantity_NOC_GRAY30,
                              V3d_ZBUFFER,V3d_GOURAUD,V3d_WAIT, 
                              Standard_True, Standard_False);

	myViewer->SetDefaultLights();
	myViewer->SetLightOn();

	myAISContext =new AIS_InteractiveContext(myViewer);

	myDeviation = 0.0008;
	thread = 4;
	myAngle = 0;

	BRep_Builder B;
	TopoDS_Shape CrankArm;
	TopoDS_Shape CylinderHead;
	TopoDS_Shape Propeller;
	TopoDS_Shape Piston;
	TopoDS_Shape EngineBlock;
	
	char AbloluteExecutableFileName[200];
	HMODULE hModule = GetModuleHandle(NULL);
	GetModuleFileName (hModule, AbloluteExecutableFileName, 200);

	CString aString(AbloluteExecutableFileName);
	int index = aString.ReverseFind('\\');

	aString.Delete(index+1, aString.GetLength() - index - 1);

	TCHAR tchBuf[80];

	CString CASROOTValue = ((GetEnvironmentVariable("CASROOT", tchBuf, 80) > 0) ? tchBuf : NULL); 
	aString = (CASROOTValue + "\\..\\data\\occ");	

	char DataDirPath[200];
	strcpy_s(DataDirPath, aString);
	char temp[200];
	strcpy_s(temp, DataDirPath);

    strcat_s(temp,"\\CrankArm.rle");
	BRepTools::Read(CrankArm, temp, B);
	
    strcpy_s(temp, DataDirPath);
    strcat_s(temp,"\\CylinderHead.rle");
	BRepTools::Read(CylinderHead, temp, B);

	strcpy_s(temp, DataDirPath);
    strcat_s(temp,"\\Propeller.rle");
	BRepTools::Read(Propeller, temp, B);

	strcpy_s(temp, DataDirPath);
    strcat_s(temp,"\\Piston.rle");
	BRepTools::Read(Piston, temp, B);

	strcpy_s(temp, DataDirPath);
    strcat_s(temp,"\\EngineBlock.rle");
	BRepTools::Read(EngineBlock, temp, B);
	
	if (CrankArm.IsNull() || CylinderHead.IsNull() || 
        Propeller.IsNull() || Piston.IsNull() || EngineBlock.IsNull())
	{
		int rep = MessageBox(NULL, "Shape(s) not found.\nCheck the Data directory path!", "Error",MB_OK | MB_ICONERROR);
		if (rep == IDOK)
			exit(0);
	}
	myAISContext->SetDeviationCoefficient(myDeviation);

	myAisCylinderHead = new AIS_Shape (CylinderHead);
	myAISContext->SetColor    (myAisCylinderHead, Quantity_NOC_WHITE);
	myAISContext->SetMaterial (myAisCylinderHead, Graphic3d_NOM_PLASTIC);
	myAisEngineBlock  = new AIS_Shape (EngineBlock);
	myAISContext->SetColor(myAisEngineBlock,   Quantity_NOC_WHITE);
	myAISContext->SetMaterial(myAisEngineBlock,Graphic3d_NOM_PLASTIC);

	myAISContext->Display(myAisCylinderHead ,1,-1,Standard_False,Standard_False);
	myAISContext->Display(myAisEngineBlock  ,1,-1,Standard_False,Standard_False);

	myAisCrankArm     = new AIS_Shape (CrankArm);
	myAISContext->SetColor   (myAisCrankArm, Quantity_NOC_HOTPINK);
	myAISContext->SetMaterial(myAisCrankArm, Graphic3d_NOM_PLASTIC);
	myAisPiston       = new AIS_Shape (Piston);
	myAISContext->SetColor   (myAisPiston  , Quantity_NOC_WHITE);
	myAISContext->SetMaterial(myAisPiston  , Graphic3d_NOM_PLASTIC);
	myAisPropeller    = new AIS_Shape (Propeller);
	myAISContext->SetColor   (myAisPropeller, Quantity_NOC_RED);
	myAISContext->SetMaterial(myAisPropeller, Graphic3d_NOM_PLASTIC);

	myAISContext->Display(myAisCrankArm     ,1,-1,Standard_False,Standard_False);
	myAISContext->Display(myAisPropeller    ,1,-1,Standard_False,Standard_False);
	myAISContext->Display(myAisPiston       ,1,-1,Standard_True,Standard_False);

	m_Xmin = -300. ;
	m_Ymin = -300. ;
	m_Zmin = -300. ;
	m_Xmax = +300. ;
	m_Ymax = +300. ;
	m_Zmax = +300. ;

	m_bIsGridLoaded = FALSE;
}

CAnimationDoc::~CAnimationDoc()
{
}

/////////////////////////////////////////////////////////////////////////////
// CAnimationDoc diagnostics

#ifdef _DEBUG
void CAnimationDoc::AssertValid() const
{
	CDocument::AssertValid();
}

void CAnimationDoc::Dump(CDumpContext& dc) const
{
	CDocument::Dump(dc);
}
#endif //_DEBUG

/////////////////////////////////////////////////////////////////////////////
// CAnimationDoc commands
//-----------------------------------------------------------------------------------------
//
//-----------------------------------------------------------------------------------------
void CAnimationDoc::DragEvent(const Standard_Integer  x        ,
				                  const Standard_Integer  y        ,
				                  const Standard_Integer  TheState ,
                                  const Handle(V3d_View)& aView    )
{

    // TheState == -1  button down
	// TheState ==  0  move
	// TheState ==  1  button up

    static Standard_Integer theButtonDownX=0;
    static Standard_Integer theButtonDownY=0;

	if (TheState == -1)
    {
      theButtonDownX=x;
      theButtonDownY=y;
    }

	if (TheState == 1)
	  myAISContext->Select(theButtonDownX,theButtonDownY,x,y,aView);  
}

//-----------------------------------------------------------------------------------------
//
//-----------------------------------------------------------------------------------------
void CAnimationDoc::InputEvent(const Standard_Integer  x     ,
				                   const Standard_Integer  y     ,
                                   const Handle(V3d_View)& aView ) 
{
    myAISContext->Select(); 
}

//-----------------------------------------------------------------------------------------
//
//-----------------------------------------------------------------------------------------
void CAnimationDoc::MoveEvent(const Standard_Integer  x       ,
                                  const Standard_Integer  y       ,
                                  const Handle(V3d_View)& aView   ) 
{
      myAISContext->MoveTo(x,y,aView);
}

//-----------------------------------------------------------------------------------------
//
//-----------------------------------------------------------------------------------------
void CAnimationDoc::ShiftMoveEvent(const Standard_Integer  x       ,
                                  const Standard_Integer  y       ,
                                  const Handle(V3d_View)& aView   ) 
{
      myAISContext->MoveTo(x,y,aView);
}

//-----------------------------------------------------------------------------------------
//
//-----------------------------------------------------------------------------------------
void CAnimationDoc::ShiftDragEvent(const Standard_Integer  x        ,
									   const Standard_Integer  y        ,
									   const Standard_Integer  TheState ,
                                       const Handle(V3d_View)& aView    ) 
{
    static Standard_Integer theButtonDownX=0;
    static Standard_Integer theButtonDownY=0;

	if (TheState == -1)
    {
      theButtonDownX=x;
      theButtonDownY=y;
    }

	if (TheState == 0)
	  myAISContext->ShiftSelect(theButtonDownX,theButtonDownY,x,y,aView);  
}


//-----------------------------------------------------------------------------------------
//
//-----------------------------------------------------------------------------------------
void CAnimationDoc::ShiftInputEvent(const Standard_Integer  x       ,
									    const Standard_Integer  y       ,
                                        const Handle(V3d_View)& aView   ) 
{
	myAISContext->ShiftSelect(); 
}

//-----------------------------------------------------------------------------------------
//
//-----------------------------------------------------------------------------------------
void  CAnimationDoc::Popup(const Standard_Integer  x,
							   const Standard_Integer  y ,
                               const Handle(V3d_View)& aView   ) 
{
}

void CAnimationDoc::OnMyTimer() 
{
	// TODO: Add your message handler code here and/or call default
	
	Standard_Real angleA;
	Standard_Real angleB;
	Standard_Real X;
	gp_Ax1 Ax1(gp_Pnt(0,0,0),gp_Vec(0,0,1));

	myAngle++;

	angleA = thread*myAngle*PI/180;
	X = Sin(angleA)*3/8;
	angleB = atan(X / Sqrt(-X * X + 1));
	Standard_Real decal(25*0.6);

	
	//Build a transformation on the display
    gp_Trsf aPropellerTrsf;
    aPropellerTrsf.SetRotation(Ax1,angleA);
	myAISContext->SetLocation(myAisPropeller,aPropellerTrsf);
	
	gp_Ax3 base(gp_Pnt(3*decal*(1-Cos(angleA)),-3*decal*Sin(angleA),0),gp_Vec(0,0,1),gp_Vec(1,0,0));
    gp_Trsf aCrankArmTrsf;
    aCrankArmTrsf.SetTransformation(   base.Rotated(gp_Ax1(gp_Pnt(3*decal,0,0),gp_Dir(0,0,1)),angleB));
	myAISContext->SetLocation(myAisCrankArm,aCrankArmTrsf);

    gp_Trsf aPistonTrsf;
    aPistonTrsf.SetTranslation(gp_Vec(-3*decal*(1-Cos(angleA))-8*decal*(1-Cos(angleB)),0,0));
	myAISContext->SetLocation(myAisPiston,aPistonTrsf);

    myAISContext->UpdateCurrentViewer();
}

void CAnimationDoc::OnShading() 
{
	
	POSITION position = GetFirstViewPosition();
	CView* pCurrentView = (CView*)GetNextView(position);
	((CAnimationView3D *)pCurrentView)		->OnStop();

	CShadingDialog aDial(NULL);

	aDial.myvalue=int((myDeviation-0.00003)/0.00003);
	
	if (aDial.DoModal()==IDOK) {
		Standard_Real dev(aDial.myvalue);
		myDeviation = 0.00003+0.00003*dev;

		myAISContext->SetDeviationCoefficient(myDeviation);
        TopoDS_Shape Propeller=myAisPropeller->Shape();
		BRepTools::Clean(Propeller);
		
        myAisPropeller->Set(Propeller);
        myAISContext->Deactivate(myAisPropeller);
        myAISContext->Redisplay(myAisPropeller);
	}
	((CAnimationView3D *)pCurrentView)->OnRestart();
}


void CAnimationDoc::OnThread() 
{
	POSITION position = GetFirstViewPosition();
    CView* pCurrentView = (CView*)GetNextView(position);
	((CAnimationView3D *)pCurrentView)		->OnStop();
	CThreadDialog aThreadDial(NULL);
	if (aThreadDial.DoModal()==IDOK) {
		thread = aThreadDial.m_Angle;
	}

	((CAnimationView3D *)pCurrentView)->OnRestart();
}


void CAnimationDoc::OnFileLoadgrid() 
{
	// TODO: Add your command handler code here

  CFileDialog dlg(TRUE,
		  NULL,
		  NULL,
		  OFN_HIDEREADONLY | OFN_OVERWRITEPROMPT,
		  "Points Files (*.pnt;*.pnts)|*.pnt; *.pnts|All Files (*.*)|*.*||", 
		  NULL );

	CString initdir(((OCC_BaseApp*) AfxGetApp())->GetInitDataDir());
	initdir += "\\..\\..\\Data\\";

	dlg.m_ofn.lpstrInitialDir = initdir;


  if (dlg.DoModal() == IDOK)
    {
      CString C = dlg.GetPathName();
      SetCursor(AfxGetApp()->LoadStandardCursor(IDC_WAIT));

      Handle_Geom_BSplineSurface mySurface;

      if(grid2surf(C,mySurface ))
	{
	  //To set the minimum value of the surface to Z=0 
	  Standard_Real Tolerance = 0.0;
	  Bnd_Box B;
      TopoDS_Face myFace;
	  GeomAdaptor_Surface GAS(mySurface);

	  
	  BndLib_AddSurface::Add(GAS,Tolerance,B);

	  Standard_Real Xmin,Xmax,Ymin,Ymax,Zmin,Zmax;
	  B.Get(Xmin,Ymin,Zmin,Xmax,Ymax,Zmax);
	  TopoDS_Solid Box = BRepPrimAPI_MakeBox(gp_Pnt(Xmin,Ymin,0),Xmax-Xmin,Ymax-Ymin,50).Solid();

	  gp_Vec V(gp_Pnt(0,0,Zmin),gp_Pnt(0,0,0));
	  
	  gp_Trsf T;
	  T.SetTranslation(V);
	  mySurface->Transform(T);
	  BRepBuilderAPI_MakeFace aMkFace(mySurface);
	  myFace = aMkFace.Face();

	  // Remove all other shapes
	  myAISContext->RemoveAll();

	  Handle(AIS_Shape) myAISSurface = new AIS_Shape(myFace);

	  myAISContext->Display(myAISSurface, Standard_False);
	  myAISContext->Deactivate(myAISSurface,Standard_False);
	  myAISContext->SetColor (myAISSurface,Quantity_NOC_WHITE,Standard_False);
	  myAISContext->SetMaterial (myAISSurface,Graphic3d_NOM_STONE,Standard_False);
	  myAISContext->SetDisplayMode (myAISSurface,1,Standard_False);
	  myAISContext->SetDeviationCoefficient (0.001);
  	CMDIFrameWnd *pFrame =  (CMDIFrameWnd*)AfxGetApp()->m_pMainWnd;
	CMDIChildWnd *pChild =  (CMDIChildWnd *) pFrame->GetActiveFrame();
	CAnimationView3D *pView = (CAnimationView3D *) pChild->GetActiveView();
	pView->FitAll();

      Bnd_Box Boite;
      BRepBndLib::Add(myFace, Boite);
      Boite.Get(m_Xmin, m_Ymin, m_Zmin, m_Xmax, m_Ymax, m_Zmax);
	  
	  m_bIsGridLoaded = TRUE;
	}	
      SetCursor(AfxGetApp()->LoadStandardCursor(IDC_ARROW));
}


}

void CAnimationDoc::OnUpdateWalkWalkthru(CCmdUI* pCmdUI) 
{
	// TODO: Add your command update UI handler code here

	int i ;
	char szMsg [256] ;
	i = GetEnvironmentVariable( "CSF_WALKTHROUGH" , szMsg , sizeof szMsg ) ;
	if ( i )
		pCmdUI->SetCheck ( 1 ) ;
	else
		pCmdUI->SetCheck ( 0 ) ;
}
