// OCCDemo_Presentation.h: interface for the OCCDemo_Presentation class.
// This is a base class for all presentations
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_OCCDEMO_PRESENTATION_H__790EED7F_7BA2_11D5_BA4A_0060B0EE18EA__INCLUDED_)
#define AFX_OCCDEMO_PRESENTATION_H__790EED7F_7BA2_11D5_BA4A_0060B0EE18EA__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000


#define WAIT_A_LITTLE WaitForInput(500)
#define WAIT_A_SECOND WaitForInput(1000)

//#include "TriangulationDoc.h"
//#include <OCCDemo.h>
class CTriangulationDoc;
class Handle_AIS_InteractiveObject;
class Handle_AIS_Point;
class Handle_Geom_Surface;
class Handle_Geom_Curve;
class Handle_Geom2d_Curve;
class Quantity_Color;

class OCCDemo_Presentation
{
public:
  // Construction
  OCCDemo_Presentation() : myIndex(0), myNbSamples(0), FitMode(false){}
  virtual ~OCCDemo_Presentation() {}

public:
  static OCCDemo_Presentation* Current;
  // this pointer must be initialized when realize a derivable class;
  // it is used by application to access to a presentation class instance

  void SetDocument (CTriangulationDoc* theDoc) {myDoc = theDoc;}
  // document must be set by the user of this class before first use of iterations
  CTriangulationDoc* GetDocument () {return myDoc;}

public:
  // Titling
  const CString& GetName() const {return myName;}

public:
  // Iteration on samples
  void FirstSample() {myIndex=0;}
  void LastSample() {myIndex=myNbSamples-1;}
  Standard_Boolean AtFirstSample() const {return myIndex <= 0;}
  Standard_Boolean AtLastSample() const {return myIndex >= myNbSamples-1;}
  void NextSample() {myIndex++;}
  void PrevSample() {myIndex--;}
  virtual void DoSample() = 0;
/*
  static void GetViewCenter(V3d_Coordinate& Xc, V3d_Coordinate& Yc);
  static void SetViewCenter(const V3d_Coordinate Xc, const V3d_Coordinate Yc);
  static void GetViewEye(V3d_Coordinate& X, V3d_Coordinate& Y, V3d_Coordinate& Z);
  static void SetViewEye(V3d_Coordinate X, V3d_Coordinate Y, V3d_Coordinate Z);
  static Quantity_Factor GetViewScale();
  static void SetViewScale(Quantity_Factor Coef);
  static void ResetView();
*/

  // place one-time initialization code in this function
  virtual void Init() {}

protected:
  // Methods to call from a derivable class
  void setName (const char* theName) {myName = CString(theName);}
  Handle_AIS_InteractiveContext getAISContext() const;
  Handle_V3d_Viewer getViewer() const;
  Standard_CString GetDataDir();

  Standard_Boolean WaitForInput (unsigned long aMilliSeconds);
  // Waits for a user input or a period of time has been elapsed

  Handle_AIS_InteractiveObject drawSurface (const Handle_Geom_Surface& theSurface,
         const Quantity_Color& theColor = Quantity_Color(Quantity_NOC_LEMONCHIFFON3),
         const Standard_Boolean toDisplay = Standard_True);
  // creates a finite face based on the given geometric surface 
  // and displays it in the viewer if toDisplay = Standard_True

  Handle_AIS_InteractiveObject drawCurve (const Handle_Geom_Curve& theCurve,
         const Quantity_Color& theColor = Quantity_Color(Quantity_NOC_RED),
         const Standard_Boolean toDisplay = Standard_True);
  // creates an ISession_Curve based on the given geometric curve
  // and displays it in the viewer if toDisplay = Standard_True

  Handle_AIS_InteractiveObject drawCurve (const Handle_Geom2d_Curve& theCurve,
         const Quantity_Color& theColor = Quantity_Color(Quantity_NOC_RED),
         const Standard_Boolean toDisplay = Standard_True,
         const gp_Ax2& aPosition = gp::XOY());
  // converts a given curve to 3d using aPosition and calls the previous method

  Handle_AIS_Point drawPoint (const gp_Pnt& thePnt,
         const Quantity_Color& theColor = Quantity_Color(Quantity_NOC_GREEN),
         const Standard_Boolean toDisplay = Standard_True);
  // creates a presentation of the given point
  // and displays it in the viewer if toDisplay = Standard_True

  Handle_AIS_InteractiveObject drawVector (const gp_Pnt& thePnt,
                                           const gp_Vec& theVec,
         const Quantity_Color& theColor = Quantity_Color(Quantity_NOC_YELLOW),
         const Standard_Boolean toDisplay = Standard_True);
  // creates a presentation of the given vector
  // and displays it in the viewer if toDisplay = Standard_True

  Handle_AIS_Shape drawShape (const TopoDS_Shape& theShape,
         const Quantity_Color& theColor,
         const Standard_Boolean toDisplay = Standard_True);
  // creates a presentation of the given shape
  // with material PLASTIC and a given color
  // and displays it in the viewer if toDisplay = Standard_True

  Handle_AIS_Shape drawShape (const TopoDS_Shape& theShape,
         const Graphic3d_NameOfMaterial theMaterial = Graphic3d_NOM_BRASS,
         const Standard_Boolean toDisplay = Standard_True);
  // creates a presentation of the given shape with the given material
  // (color is default for a given material)
  // and displays it in the viewer if toDisplay = Standard_True

protected:
  // Fields to use in a derivable class
  BOOL FitMode;
  int myIndex;
  int myNbSamples;

private:
  CTriangulationDoc* myDoc;
  CString myName;

};

#endif // !defined(AFX_OCCDEMO_PRESENTATION_H__790EED7F_7BA2_11D5_BA4A_0060B0EE18EA__INCLUDED_)
