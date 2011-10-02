#ifndef _User_Cylinder_HeaderFile
	#define _User_Cylinder_HeaderFile
	#ifndef _Standard_Macro_HeaderFile
	#include <Standard_Macro.hxx>
#endif

#include <AIS_InteractiveObject.hxx>
#include <Standard_DefineHandle.hxx>
#include <Standard_Macro.hxx>
#include <Graphic3d_AspectFillArea3d.hxx>

// Handle definition
//
DEFINE_STANDARD_HANDLE(User_Cylinder,AIS_InteractiveObject)

class User_Cylinder: public AIS_InteractiveObject {
public:
	Standard_EXPORT User_Cylinder(const Standard_Real R, const Standard_Real H);
	Standard_EXPORT User_Cylinder(const gp_Ax2 CylAx2, const Standard_Real R, const Standard_Real H);

	~User_Cylinder(){};
	
	Standard_Boolean AcceptShapeDecomposition() const;
	void Standard_EXPORT SetCylindricalFaceColor(const Quantity_Color acolor);
	void Standard_EXPORT SetPlanarFaceColor(const Quantity_Color);
	Standard_Integer NbPossibleSelection() const;
	void SetColor(const Quantity_Color &aColor);

private:
	void Compute(const Handle_PrsMgr_PresentationManager3d& aPresentationManager,
				 const Handle_Prs3d_Presentation& aPresentation,
				 const Standard_Integer aMode = 0) ;
	void ComputeSelection(const Handle_SelectMgr_Selection& aSelection,
						  const Standard_Integer aMode) ;
	void Compute(const Handle_Prs3d_Projector& aProjector,const Handle_Prs3d_Presentation& aPresentation);
	Standard_Boolean TriangleIsValid(const gp_Pnt& P1,const gp_Pnt& P2,const gp_Pnt& P3) const;
	Quantity_Color Color(gp_Pnt& thePoint,Standard_Real AltMin,Standard_Real AltMax, const Standard_Integer ColorizationMode) ;
						
// some methods like DynamicType() or IsKind()
//
DEFINE_STANDARD_RTTI(User_Cylinder)
private:
//	Quantity_NameOfColor myCylindricalFaceColor;
//	Quantity_NameOfColor myPlanarFaceColor;
	Quantity_Color myCylindricalFaceColor;
	Quantity_Color myPlanarFaceColor;
	TopoDS_Shape myShape;

	Handle_Graphic3d_AspectFillArea3d myAspect;
	Standard_Real myDeflection;

	Standard_Boolean myX1OnOff;
	Standard_Boolean myXBlueOnOff;
	Standard_Boolean myXGreenOnOff;
	Standard_Boolean myXRedOnOff;
	Standard_Boolean myY1OnOff;
	Standard_Boolean myYBlueOnOff;
	Standard_Boolean myYGreenOnOff;
	Standard_Boolean myYRedOnOff;
	Standard_Boolean myZ1OnOff;
	Standard_Boolean myZBlueOnOff;
	Standard_Boolean myZGreenOnOff;
	Standard_Boolean myZRedOnOff;

	gp_Pnt BAR;

	Standard_Real Umin;
	Standard_Real Umax;
	Standard_Real Vmin;
	Standard_Real Vmax;
	Standard_Real dUmax;
	Standard_Real dVmax;

	Standard_Real red;
	Standard_Real green;
	Standard_Real blue;



//Handle_Graphic3d_Texture2Dmanual mytexture;

};
#endif