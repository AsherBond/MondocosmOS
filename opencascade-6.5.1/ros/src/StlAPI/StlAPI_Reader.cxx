#include <StlAPI_Reader.ixx>
#include <RWStl.hxx>
#include <StlMesh_Mesh.hxx>
#include <OSD_Path.hxx>
#include <BRep_Builder.hxx>
#include <BRepBuilderAPI_MakeVertex.hxx>
#include <BRepBuilderAPI_MakePolygon.hxx>
#include <BRepBuilderAPI_MakeFace.hxx>
#include <BRepBuilderAPI_Sewing.hxx>
#include <gp_Pnt.hxx>
#include <TopoDS_Compound.hxx>
#include <TopoDS_Wire.hxx>
#include <TopoDS_Edge.hxx>
#include <TopoDS_Vertex.hxx>
#include <TopoDS_Face.hxx>
#include <TopoDS_Shell.hxx>
#include <StlMesh_MeshExplorer.hxx>



StlAPI_Reader::StlAPI_Reader() {}

void StlAPI_Reader::Read(TopoDS_Shape& aShape, const Standard_CString aFileName) 
{
  OSD_Path aFile(aFileName);
  
  Handle(StlMesh_Mesh) aSTLMesh = RWStl::ReadFile(aFile);
  Standard_Integer NumberDomains = aSTLMesh->NbDomains();
  Standard_Integer iND;
  gp_XYZ p1, p2, p3;
  TopoDS_Vertex Vertex1, Vertex2, Vertex3;
  TopoDS_Face AktFace;
  TopoDS_Wire AktWire;
  BRepBuilderAPI_Sewing aSewingTool;
  Standard_Real x1, y1, z1;
  Standard_Real x2, y2, z2;
  Standard_Real x3, y3, z3;
  
  aSewingTool.Init(1.0e-06,Standard_True);
  
  TopoDS_Compound aComp;
  BRep_Builder BuildTool;
  BuildTool.MakeCompound( aComp );

  StlMesh_MeshExplorer aMExp (aSTLMesh);
  
  for (iND=1;iND<=NumberDomains;iND++) 
  {
    for (aMExp.InitTriangle (iND); aMExp.MoreTriangle (); aMExp.NextTriangle ()) 
    {
      aMExp.TriangleVertices (x1,y1,z1,x2,y2,z2,x3,y3,z3);
      p1.SetCoord(x1,y1,z1);
      p2.SetCoord(x2,y2,z2);
      p3.SetCoord(x3,y3,z3);
      
      if ((!(p1.IsEqual(p2,0.0))) && (!(p1.IsEqual(p3,0.0))))
      {
        Vertex1 = BRepBuilderAPI_MakeVertex(p1);
        Vertex2 = BRepBuilderAPI_MakeVertex(p2);
        Vertex3 = BRepBuilderAPI_MakeVertex(p3);
        
        AktWire = BRepBuilderAPI_MakePolygon( Vertex1, Vertex2, Vertex3, Standard_True);
        
        if( !AktWire.IsNull())
        {
          AktFace = BRepBuilderAPI_MakeFace( AktWire);
          if(!AktFace.IsNull())
            BuildTool.Add( aComp, AktFace );
        }
      }
    }
  }
  aSTLMesh->Clear();

  aSewingTool.Load( aComp );
  aSewingTool.Perform();
  aShape = aSewingTool.SewedShape();
  if ( aShape.IsNull() )
    aShape = aComp;
}

