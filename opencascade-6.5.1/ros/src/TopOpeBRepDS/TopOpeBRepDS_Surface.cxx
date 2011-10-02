// File:	TopOpeBRepDS_Surface.cxx
// Created:	Wed Jun 23 19:10:30 1993
// Author:	Jean Yves LEBEY
//		<jyl@zerox>

#include <TopOpeBRepDS_Surface.ixx>

//=======================================================================
//function : TopOpeBRepDS_Surface
//purpose  : 
//=======================================================================

TopOpeBRepDS_Surface::TopOpeBRepDS_Surface()
{
}

//=======================================================================
//function : TopOpeBRepDS_Surface
//purpose  : 
//=======================================================================

TopOpeBRepDS_Surface::TopOpeBRepDS_Surface
  (const Handle(Geom_Surface)& P,
   const Standard_Real T) :
       mySurface(P),
       myTolerance(T)
{
}

//
//modified by NIZNHY-PKV Tue Oct 30 09:28:33 2001 f
//=======================================================================
//function : TopOpeBRepDS_Surface::TopOpeBRepDS_Surface
//purpose  : 
//=======================================================================
TopOpeBRepDS_Surface::TopOpeBRepDS_Surface (const TopOpeBRepDS_Surface& Other) 
{
  Assign(Other);
}
//=======================================================================
//function : Assign
//purpose  : 
//=======================================================================
void TopOpeBRepDS_Surface::Assign(const TopOpeBRepDS_Surface& Other)
{
  mySurface=Other.mySurface;
  myTolerance=Other.myTolerance;
  myKeep=Other.myKeep;
}
//modified by NIZNHY-PKV Tue Oct 30 09:28:20 2001 t
//

//=======================================================================
//function : Surface
//purpose  : 
//=======================================================================

const Handle(Geom_Surface)&  TopOpeBRepDS_Surface::Surface()const 
{
  return mySurface;
}


//=======================================================================
//function : Tolerance
//purpose  : 
//=======================================================================

Standard_Real  TopOpeBRepDS_Surface::Tolerance()const 
{
  return myTolerance;
}

//=======================================================================
//function : Tolerance
//purpose  : 
//=======================================================================

void TopOpeBRepDS_Surface::Tolerance(const Standard_Real T)
{
  myTolerance = T;
}

//=======================================================================
//function : Keep
//purpose  : 
//=======================================================================

Standard_Boolean TopOpeBRepDS_Surface::Keep() const
{
  return myKeep;
}
//=======================================================================
//function : ChangeKeep
//purpose  : 
//=======================================================================

void TopOpeBRepDS_Surface::ChangeKeep(const Standard_Boolean b)
{
  myKeep = b;
}
