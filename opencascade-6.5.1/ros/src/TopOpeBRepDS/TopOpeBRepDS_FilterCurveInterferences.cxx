// File:	TopOpeBRepDS_FilterCurveInterferences.cxx
// Created:	Tue Apr 22 09:54:02 1997
// Author:	Prestataire Mary FABIEN
//		<fbi@langdox.paris1.matra-dtv.fr>


#include <TopOpeBRepDS_Filter.ixx>

#include <TopOpeBRepDS_Interference.hxx>
#include <TopOpeBRepDS_ListOfInterference.hxx>
#include <TopOpeBRepDS_ListIteratorOfListOfInterference.hxx>
#include <TopOpeBRepDS_ProcessInterferencesTool.hxx>

#ifdef DEB
Standard_IMPORT Standard_Boolean TopOpeBRepDS_GettracePI();
Standard_IMPORT Standard_Boolean TopOpeBRepDS_GettracePCI();
static Standard_Boolean TRCC() {
  Standard_Boolean b2 = TopOpeBRepDS_GettracePI();
  Standard_Boolean b3 = TopOpeBRepDS_GettracePCI();
  return (b2 || b3);
}
#endif

//=======================================================================
//function : ProcessCurveInterferences
//purpose  : 
//=======================================================================

void TopOpeBRepDS_Filter::ProcessCurveInterferences
(const Standard_Integer CIX)
{
  TopOpeBRepDS_DataStructure& BDS = myHDS->ChangeDS();
  TopOpeBRepDS_ListOfInterference& LI = BDS.ChangeCurveInterferences(CIX);
  TopOpeBRepDS_ListIteratorOfListOfInterference it1(LI);
  
  // process interferences of LI with VERTEX geometry
  while( it1.More() ) {
    const Handle(TopOpeBRepDS_Interference)& I1 = it1.Value();
    Standard_Integer G1 = I1->Geometry();
    TopOpeBRepDS_Kind GT1 = I1->GeometryType();
    TopAbs_Orientation O1 = I1->Transition().Orientation(TopAbs_IN);
    
    if ( GT1 == TopOpeBRepDS_VERTEX ) {
      
      TopOpeBRepDS_ListIteratorOfListOfInterference it2(it1);
      it2.Next();
      
      while ( it2.More() ) {
	const Handle(TopOpeBRepDS_Interference)& I2 = it2.Value();
	Standard_Integer G2 = I2->Geometry();
	TopOpeBRepDS_Kind GT2 = I2->GeometryType();
	TopAbs_Orientation O2 = I2->Transition().Orientation(TopAbs_IN);
	
//	Standard_Boolean remove = (GT2 == GT1) && (G2 == G1);
	// xpu140898 : USA60111 : CPI(FORWARD,v10,C1) + CPIREV(REVERSED,v10,C1)
	//             do NOT delete CPIREV!!
	Standard_Boolean remove = (GT2 == GT1) && (G2 == G1) && (O1 == O2);
	if ( remove ) {
#ifdef DEB
	  if ( TRCC() ){
	    cout<<"remove ";I2->Dump(cout);cout<<" from C "<<CIX<<endl;
	  }
#endif
	  LI.Remove(it2);
	}
	else it2.Next();
      }
    }
    it1.Next();
  }
}
