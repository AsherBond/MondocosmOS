// File:	TopOpeBRepDS_Interference.cxx
// Created:	Wed Jun 23 15:01:01 1993
// Author:	Jean Yves LEBEY
//		<jyl@zerox>

#include <TCollection_AsciiString.hxx>
#include <TopOpeBRepDS.hxx>
#include <TopAbs.hxx>
#include <TopOpeBRepDS_Interference.ixx>


//=======================================================================
//function : TopOpeBRepDS_Interference
//purpose  : 
//=======================================================================

TopOpeBRepDS_Interference::TopOpeBRepDS_Interference()
{
}

//=======================================================================
//function : TopOpeBRepDS_Interference
//purpose  : 
//=======================================================================

TopOpeBRepDS_Interference::TopOpeBRepDS_Interference
    (const TopOpeBRepDS_Transition& T, 
     const TopOpeBRepDS_Kind ST,
     const Standard_Integer S, 
     const TopOpeBRepDS_Kind GT, 
     const Standard_Integer G) :
     myTransition(T),
     mySupport(S),
     myGeometry(G),
     mySupportType(ST),
     myGeometryType(GT)
{
}

//=======================================================================
//function : TopOpeBRepDS_Interference
//purpose  : 
//=======================================================================

TopOpeBRepDS_Interference::TopOpeBRepDS_Interference
    (const Handle(TopOpeBRepDS_Interference)& I ) :
    myTransition(I->Transition()),
    mySupport(I->Support()),
    myGeometry(I->Geometry()),
    mySupportType(I->SupportType()),
    myGeometryType(I->GeometryType())
{
}


//=======================================================================
//function : Transition
//purpose  : 
//=======================================================================

const TopOpeBRepDS_Transition& TopOpeBRepDS_Interference::Transition() const 
{
  return myTransition;
}


//=======================================================================
//function : ChangeTransition
//purpose  : 
//=======================================================================

TopOpeBRepDS_Transition&  TopOpeBRepDS_Interference::ChangeTransition()
{
  return myTransition;
}

//=======================================================================
//function : Transition
//purpose  : 
//=======================================================================

void  TopOpeBRepDS_Interference::Transition(const TopOpeBRepDS_Transition& T)
{
  myTransition = T;
}

//=======================================================================
//function : SupportType
//purpose  : 
//=======================================================================

void TopOpeBRepDS_Interference::GKGSKS(TopOpeBRepDS_Kind& GK,
				       Standard_Integer& G,
				       TopOpeBRepDS_Kind& SK,
				       Standard_Integer& S) const
{
  GK = myGeometryType;
  G = myGeometry;
  SK = mySupportType;
  S = mySupport;
}

//=======================================================================
//function : SupportType
//purpose  : 
//=======================================================================

TopOpeBRepDS_Kind  TopOpeBRepDS_Interference::SupportType()const 
{
  return mySupportType;
}


//=======================================================================
//function : Support
//purpose  : 
//=======================================================================

Standard_Integer  TopOpeBRepDS_Interference::Support()const 
{
  return mySupport;
}


//=======================================================================
//function : GeometryType
//purpose  : 
//=======================================================================

TopOpeBRepDS_Kind  TopOpeBRepDS_Interference::GeometryType()const 
{
  return myGeometryType;
}


//=======================================================================
//function : Geometry
//purpose  : 
//=======================================================================

Standard_Integer  TopOpeBRepDS_Interference::Geometry()const 
{
  return myGeometry;
}

//=======================================================================
//function : SetGeometry
//purpose  : 
//=======================================================================

void TopOpeBRepDS_Interference::SetGeometry(const Standard_Integer GI)
{
  myGeometry = GI;
}


//=======================================================================
//function : SupportType
//purpose  : 
//=======================================================================

void  TopOpeBRepDS_Interference::SupportType(const TopOpeBRepDS_Kind ST)
{
  mySupportType = ST;
}


//=======================================================================
//function : Support
//purpose  : 
//=======================================================================

void  TopOpeBRepDS_Interference::Support(const Standard_Integer S)
{
  mySupport = S;
}


//=======================================================================
//function : GeometryType
//purpose  : 
//=======================================================================

void  TopOpeBRepDS_Interference::GeometryType(const TopOpeBRepDS_Kind GT)
{
  myGeometryType = GT;
}


//=======================================================================
//function : Geometry
//purpose  : 
//=======================================================================

void  TopOpeBRepDS_Interference::Geometry(const Standard_Integer G)
{
  myGeometry = G;
}

//=======================================================================
//function : HasSameSupport 
//purpose  : 
//=======================================================================

Standard_Boolean  TopOpeBRepDS_Interference::HasSameSupport(const Handle(TopOpeBRepDS_Interference)& I)
const
{
  return (mySupportType == I->mySupportType && mySupport == I->mySupport);
}

//=======================================================================
//function : HasSameGeometry
//purpose  : 
//=======================================================================

Standard_Boolean  TopOpeBRepDS_Interference::HasSameGeometry(const Handle(TopOpeBRepDS_Interference)& I)
const
{
  return (myGeometryType == I->myGeometryType && myGeometry == I->myGeometry);
}


//=======================================================================
//function : DumpS
//purpose  : 
//=======================================================================

Standard_OStream& TopOpeBRepDS_Interference::DumpS(Standard_OStream& OS) const
{
#ifdef DEB
  TopOpeBRepDS::Print(mySupportType,mySupport,OS,"S=","");
  OS.flush();
#endif
  return OS;
}


//=======================================================================
//function : DumpG
//purpose  : 
//=======================================================================

Standard_OStream& TopOpeBRepDS_Interference::DumpG(Standard_OStream& OS) const
{
#ifdef DEB
  TopOpeBRepDS::Print(myGeometryType,myGeometry,OS,"G=","");
#endif
  return OS;
}


//=======================================================================
//function : Dump
//purpose  : 
//=======================================================================

Standard_OStream& TopOpeBRepDS_Interference::Dump
(Standard_OStream& OS) const
{
#ifdef DEB
  myTransition.Dump(OS); OS<<" "; DumpG(OS); OS<<" on "; DumpS(OS);
#endif
  return OS;
}

//=======================================================================
//function : Dump
//purpose  : 
//=======================================================================

Standard_OStream& TopOpeBRepDS_Interference::Dump
(Standard_OStream& OS,
 const TCollection_AsciiString& s1,
 const TCollection_AsciiString& s2) const
{
#ifdef DEB
  OS<<s1;
  Dump(OS);
  OS<<s2;
  OS.flush();
#endif
  return OS;
}
