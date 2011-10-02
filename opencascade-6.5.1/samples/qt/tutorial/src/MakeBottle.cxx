#include <BRep_Tool.hxx>

#include <BRepAlgoAPI_Fuse.hxx>

#include <BRepBuilderAPI_MakeEdge.hxx>
#include <BRepBuilderAPI_MakeFace.hxx>
#include <BRepBuilderAPI_MakeWire.hxx>
#include <BRepBuilderAPI_Transform.hxx>

#include <BRepFilletAPI_MakeFillet.hxx>

#include <BRepLib.hxx>

#include <BRepOffsetAPI_MakeThickSolid.hxx>
#include <BRepOffsetAPI_ThruSections.hxx>

#include <BRepPrimAPI_MakeCylinder.hxx>
#include <BRepPrimAPI_MakePrism.hxx>

#include <GC_MakeArcOfCircle.hxx>
#include <GC_MakeSegment.hxx>

#include <GCE2d_MakeSegment.hxx>

#include <gp.hxx>
#include <gp_Ax1.hxx>
#include <gp_Ax2.hxx>
#include <gp_Ax2d.hxx>
#include <gp_Dir.hxx>
#include <gp_Dir2d.hxx>
#include <gp_Pnt.hxx>
#include <gp_Pnt2d.hxx>
#include <gp_Trsf.hxx>
#include <gp_Vec.hxx>

#include <Geom_CylindricalSurface.hxx>
#include <Geom_Plane.hxx>
#include <Geom_Surface.hxx>
#include <Geom_TrimmedCurve.hxx>

#include <Geom2d_Ellipse.hxx>
#include <Geom2d_TrimmedCurve.hxx>

#include <TopExp_Explorer.hxx>

#include <TopoDS.hxx>
#include <TopoDS_Edge.hxx>
#include <TopoDS_Face.hxx>
#include <TopoDS_Wire.hxx>
#include <TopoDS_Shape.hxx>
#include <TopoDS_Compound.hxx>

#include <TopTools_ListOfShape.hxx>


TopoDS_Shape
MakeBottle(const Standard_Real myWidth , const Standard_Real myHeight ,
		   const Standard_Real myThickness)
{
	//Profile : Define Support Points
	gp_Pnt aPnt1(-myWidth / 2. , 0 , 0);
  gp_Pnt aPnt2(-myWidth / 2. , -myThickness / 4. , 0);
  gp_Pnt aPnt3(0 , -myThickness / 2. , 0);
  gp_Pnt aPnt4(myWidth / 2. , -myThickness / 4. , 0);
  gp_Pnt aPnt5(myWidth / 2. , 0 , 0);

	//Profile : Define the Geometry
	Handle(Geom_TrimmedCurve) aArcOfCircle = GC_MakeArcOfCircle(aPnt2,aPnt3 ,aPnt4);
  Handle(Geom_TrimmedCurve) aSegment1	   = GC_MakeSegment(aPnt1 , aPnt2);
  Handle(Geom_TrimmedCurve) aSegment2	   = GC_MakeSegment(aPnt4 , aPnt5);

	//Profile : Define the Topology
	TopoDS_Edge aEdge1 = BRepBuilderAPI_MakeEdge(aSegment1);
	TopoDS_Edge aEdge2 = BRepBuilderAPI_MakeEdge(aArcOfCircle);
  TopoDS_Edge aEdge3 = BRepBuilderAPI_MakeEdge(aSegment2);
	TopoDS_Wire aWire  = BRepBuilderAPI_MakeWire(aEdge1 , aEdge2 , aEdge3);

	//Complete Profile
	gp_Ax1 xAxis = gp::OX();
	gp_Trsf aTrsf;

  aTrsf.SetMirror(xAxis);

	BRepBuilderAPI_Transform aBRepTrsf(aWire , aTrsf);
	TopoDS_Shape aMirroredShape = aBRepTrsf.Shape();
	TopoDS_Wire aMirroredWire = TopoDS::Wire(aMirroredShape);

	BRepBuilderAPI_MakeWire mkWire;

  mkWire.Add(aWire);
  mkWire.Add(aMirroredWire);

  TopoDS_Wire myWireProfile = mkWire.Wire();

	//Body : Prism the Profile
	TopoDS_Face myFaceProfile = BRepBuilderAPI_MakeFace(myWireProfile);
	gp_Vec		aPrismVec(0 , 0 , myHeight);

	TopoDS_Shape myBody = BRepPrimAPI_MakePrism(myFaceProfile , aPrismVec);

	//Body : Apply Fillets
	BRepFilletAPI_MakeFillet mkFillet(myBody);
	TopExp_Explorer			 aEdgeExplorer(myBody , TopAbs_EDGE);

	while(aEdgeExplorer.More()){
		
		TopoDS_Edge aEdge = TopoDS::Edge(aEdgeExplorer.Current());

        //Add edge to fillet algorithm
		mkFillet.Add(myThickness / 12. , aEdge);

    aEdgeExplorer.Next();
	}

	myBody = mkFillet.Shape();

	//Body : Add the Neck	
	gp_Pnt neckLocation(0 , 0 , myHeight);
	gp_Dir neckNormal = gp::DZ();
  gp_Ax2 neckAx2(neckLocation , neckNormal);

	Standard_Real myNeckRadius = myThickness / 4.;
	Standard_Real myNeckHeight = myHeight / 10;

	BRepPrimAPI_MakeCylinder MKCylinder(neckAx2 , myNeckRadius , myNeckHeight);
	TopoDS_Shape myNeck = MKCylinder.Shape();

	myBody = BRepAlgoAPI_Fuse(myBody , myNeck);

	//Body : Create a Hollowed Solid
	TopoDS_Face   faceToRemove;
	Standard_Real zMax = -1;

	for(TopExp_Explorer aFaceExplorer(myBody , TopAbs_FACE) ; aFaceExplorer.More() ; aFaceExplorer.Next()){
		
		TopoDS_Face aFace = TopoDS::Face(aFaceExplorer.Current());

        //Check if <aFace> is the top face of the bottle's neck
		Handle(Geom_Surface) aSurface = BRep_Tool::Surface(aFace);

		if(aSurface->DynamicType() == STANDARD_TYPE(Geom_Plane)){

			Handle(Geom_Plane) aPlane = Handle(Geom_Plane)::DownCast(aSurface);

			gp_Pnt		  aPnt = aPlane->Location();
			Standard_Real aZ   = aPnt.Z();
			
			if(aZ > zMax){
				
				zMax		 = aZ;
        faceToRemove = aFace;
			}
		}
	}

	TopTools_ListOfShape facesToRemove;
	
	facesToRemove.Append(faceToRemove);

	myBody = BRepOffsetAPI_MakeThickSolid(myBody , facesToRemove , -myThickness / 50 , 1.e-3);


	//return myBody;
	//Threading : Create Surfaces
	Handle(Geom_CylindricalSurface) aCyl1 = new Geom_CylindricalSurface(neckAx2 , myNeckRadius * 0.99);
	Handle(Geom_CylindricalSurface) aCyl2 = new Geom_CylindricalSurface(neckAx2 , myNeckRadius * 1.05);

	//Threading : Define 2D Curves
	gp_Pnt2d aPnt(2. * PI , myNeckHeight / 2.);
  gp_Dir2d aDir(2. * PI , myNeckHeight / 4.);
  gp_Ax2d aAx2d(aPnt , aDir);

	Standard_Real aMajor = 2. * PI;
  Standard_Real aMinor = myNeckHeight / 10;

  Handle(Geom2d_Ellipse) anEllipse1 = new Geom2d_Ellipse(aAx2d , aMajor , aMinor);
  Handle(Geom2d_Ellipse) anEllipse2 = new Geom2d_Ellipse(aAx2d , aMajor , aMinor / 4);

	Handle(Geom2d_TrimmedCurve) aArc1 = new Geom2d_TrimmedCurve(anEllipse1 , 0 , PI);
	Handle(Geom2d_TrimmedCurve) aArc2 = new Geom2d_TrimmedCurve(anEllipse2 , 0 , PI);

	gp_Pnt2d anEllipsePnt1 = anEllipse1->Value(0);
	gp_Pnt2d anEllipsePnt2 = anEllipse1->Value(PI);
	
	Handle(Geom2d_TrimmedCurve) aSegment = GCE2d_MakeSegment(anEllipsePnt1 , anEllipsePnt2);

	//Threading : Build Edges and Wires
	TopoDS_Edge aEdge1OnSurf1 = BRepBuilderAPI_MakeEdge(aArc1 , aCyl1);
	TopoDS_Edge aEdge2OnSurf1 = BRepBuilderAPI_MakeEdge(aSegment , aCyl1);
	TopoDS_Edge aEdge1OnSurf2 = BRepBuilderAPI_MakeEdge(aArc2 , aCyl2);
	TopoDS_Edge aEdge2OnSurf2 = BRepBuilderAPI_MakeEdge(aSegment , aCyl2);

	TopoDS_Wire threadingWire1 = BRepBuilderAPI_MakeWire(aEdge1OnSurf1 , aEdge2OnSurf1);
	TopoDS_Wire threadingWire2 = BRepBuilderAPI_MakeWire(aEdge1OnSurf2 , aEdge2OnSurf2);

	BRepLib::BuildCurves3d(threadingWire1);
	BRepLib::BuildCurves3d(threadingWire2);

	//Create Threading
	BRepOffsetAPI_ThruSections aTool(Standard_True);
	
	aTool.AddWire(threadingWire1);
	aTool.AddWire(threadingWire2);
	aTool.CheckCompatibility(Standard_False);

	TopoDS_Shape myThreading = aTool.Shape();

	//Building the resulting compound
	TopoDS_Compound aRes;
	BRep_Builder aBuilder;
	aBuilder.MakeCompound (aRes);

	aBuilder.Add (aRes, myBody);
	aBuilder.Add (aRes, myThreading);

	return aRes;
}
