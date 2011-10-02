// Copyright: 	Matra-Datavision 1991
// File:	Draw_Segment2D.cxx
// Created:	Thu Apr 25 11:25:22 1991
// Author:	Arnaud BOUZY
//		<adn>

#include <Draw_Segment2D.ixx>

//=======================================================================
//function : Draw_Segment2D
//purpose  : 
//=======================================================================

Draw_Segment2D::Draw_Segment2D(const gp_Pnt2d& p1, 
			       const gp_Pnt2d& p2, 
			       const Draw_Color& col) :
       myFirst(p1),
       myLast(p2),
       myColor(col)
{
}


//=======================================================================
//function : DrawOn
//purpose  : 
//=======================================================================

void  Draw_Segment2D::DrawOn(Draw_Display& dis)const 
{
  dis.SetColor(myColor);
  dis.Draw(myFirst,myLast);
}


//=======================================================================
//function : First
//purpose  : 
//=======================================================================

const gp_Pnt2d&  Draw_Segment2D::First() const
{
  return myFirst;
}


//=======================================================================
//function : First
//purpose  : 
//=======================================================================

void  Draw_Segment2D::First(const gp_Pnt2d& P)
{
  myFirst = P;
}


//=======================================================================
//function : Last
//purpose  : 
//=======================================================================

const gp_Pnt2d&  Draw_Segment2D::Last() const
{
  return myLast;
}


//=======================================================================
//function : Last
//purpose  : 
//=======================================================================

void  Draw_Segment2D::Last(const gp_Pnt2d& P)
{
  myLast = P;
}

//=======================================================================
//function : Whatis
//purpose  : 
//=======================================================================

void  Draw_Segment2D::Whatis(Draw_Interpretor& S) const
{
  S << "segment 2d";
}

//=======================================================================
//function : Dump
//purpose  : 
//=======================================================================

void  Draw_Segment2D::Dump(Standard_OStream& S) const
{
  S << setw(17) << myFirst.X() << " " << setw(17) << myFirst.Y() << " - "
    << setw(17) << myLast.X() << " " << setw(17) << myLast.Y() << "\n";
}
