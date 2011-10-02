// File:	BRepTest_ChamferCommands.cxx
// Created:	Mon Sep 25 14:07:55 1995
// Author:	Stagiaire Flore Lautheanne
//		<fla@phylox>

#ifdef HAVE_CONFIG_H
# include <config.h>
#endif

#include <Draw_Interpretor.hxx>
#include <BRepTest.hxx>
#include <DBRep.hxx>
#include <BRepFilletAPI_MakeChamfer.hxx>
#include <TopAbs_ShapeEnum.hxx>
#include <TopoDS.hxx>
#include <TopoDS_Face.hxx>
#include <TopoDS_Edge.hxx>
#include <TopoDS_Vertex.hxx>
#include <TopoDS_Shape.hxx>

#include <Precision.hxx>
#ifdef HAVE_STRINGS_H
# include <strings.h>
#endif



//=========================================================================
// function : chamfer
// purpose  : command to construct chamfers on several edges
//            Here the chamfer is propagated on tangential edges to the 
//            required edge
//=========================================================================
 
static Standard_Integer chamfer(Draw_Interpretor& di,
				Standard_Integer narg, 
				const char** a)
     
{
  // check the argument number of the command
  if (narg == 1) {
    //cout <<" help for chamf : "<< endl;
    //cout <<"   Construction by equal distances from edge          :  chamf newname shape edge face S dist"<< endl;
    //cout <<"   Construction by two distances from edge            :  chamf newname shape edge face dist1 dist2"<< endl;
    //cout <<"   Construction by distance from edge and given angle :  chamf newname shape edge face A dist angle"<< endl;
    di <<" help for chamf : "<< "\n";
    di <<"   Construction by equal distances from edge          :  chamf newname shape edge face S dist"<< "\n";
    di <<"   Construction by two distances from edge            :  chamf newname shape edge face dist1 dist2"<< "\n";
    di <<"   Construction by distance from edge and given angle :  chamf newname shape edge face A dist angle"<< "\n";
  }
  else {
    if (narg < 7) return 1;
  
    TopoDS_Shape S = DBRep::Get(a[2]);
    if (S.IsNull()) return 1;

    TopoDS_Edge E;
    TopoDS_Face F;
    Standard_Real d1,d2, angle;
    Standard_Integer i      = 3;
    Standard_Integer Method = 1;
    Standard_Integer NbArg  = 3;

    BRepFilletAPI_MakeChamfer aMCh(S);

    if (!strcasecmp(a[i + 2], "S") ) Method = 0;

    if (Method != 0) {
      if (!strcasecmp(a[i + 2], "A")) {
        Method = 2;
        NbArg++;
      }
    }

    while (i + NbArg < narg) {
      TopoDS_Shape aLocalEdge(DBRep::Get(a[i], TopAbs_EDGE));
      E = TopoDS::Edge(aLocalEdge);
      TopoDS_Shape aLocalFace(DBRep::Get(a[i + 1], TopAbs_FACE));
      F = TopoDS::Face(aLocalFace);
//      E = TopoDS::Edge(DBRep::Get(a[i], TopAbs_EDGE));
//      F = TopoDS::Face(DBRep::Get(a[i + 1], TopAbs_FACE));
      if (Method == 0) {
        if (!E.IsNull() && !F.IsNull() && (aMCh.Contour(E) == 0) )  {
          d1 = atof(a[i + 3]);

          if ( d1 > Precision::Confusion()) 
	    aMCh.Add(d1,E ,F);
        }
        i += 4;
      }
      else if (Method == 1) {
        if (!E.IsNull() && !F.IsNull() && (aMCh.Contour(E) == 0) )  {
          d1 = atof(a[i + 2]);
          d2 = atof(a[i + 3]);
      
          if (   (d1 > Precision::Confusion())
	      && (d2 > Precision::Confusion()) )
	    aMCh.Add(d1,d2,E,F);
        }
        i += 4;
      }
      else {
        if (!E.IsNull() && !F.IsNull() && (aMCh.Contour(E) == 0) )  {
          d1     = atof(a[i + 3]);
          angle  = atof(a[i + 4]);
          angle *= PI / 180.;
      
          if (   (d1 > Precision::Confusion())
	      && (angle > Precision::Confusion())
              && (PI /  2.-  angle > Precision::Confusion()) )
	    aMCh.AddDA(d1, angle, E, F);
        }
        i += 5;
      }

      if ( i < narg) {
        Method = 1;
        NbArg  = 3;
        if (!strcasecmp(a[i + 2], "S") ) Method = 0;

        if (Method != 0) {
          if (!strcasecmp(a[i + 2], "A")) {
            Method = 2;
            NbArg++;
          }
        }

      }
    }

    // compute the chamfer and display the result
    if (aMCh.NbContours() == 0 )
      {
	//cout<<"No suitable edges to chamfer"<<endl;
	di<<"No suitable edges to chamfer"<<"\n";
	return 1;
      }
    else aMCh.Build();

    if (aMCh.IsDone()){
      DBRep::Set(a[1],aMCh);
      return 0;
    }
    else {
      //cout<<"compute of chamfer failed"<<endl;
      di<<"compute of chamfer failed"<<"\n";
      return 1;
    }
  }
      return 0;

}



//=======================================================================
//function : ChamferCommands
//purpose  : 
//=======================================================================

void  BRepTest::ChamferCommands(Draw_Interpretor& theCommands)
{
  static Standard_Boolean done = Standard_False;
  if (done) return;
  done = Standard_True;

  DBRep::BasicCommands(theCommands);

  const char* g = "TOPOLOGY Fillet construction commands";

//  theCommands.Add("chamf",
//		  "chamf newname shape edge1 face1 dist1_1 dist1_2 edge2 face2 dist2_1 dist2_2 ... ",__FILE__,chamfer,g);

  theCommands.Add("chamf",
		  "for help call chamf without arguments",__FILE__,chamfer,g);

}
