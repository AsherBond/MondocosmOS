#ifndef _Select3D_Box2d_HeaderFile
#define _Select3D_Box2d_HeaderFile

#include<Bnd_Box2d.hxx>
#include<Standard_ShortReal.hxx>
#include<Select3D_Macro.hxx>

struct Select3D_Box2d
{
 Standard_ShortReal xmin, ymin, xmax, ymax;

 Select3D_Box2d()
 { 
   SetVoid();
 }

 Select3D_Box2d(const Bnd_Box2d& theBox)
 { 
   SetField(theBox);
 }

 inline operator Bnd_Box2d() const
 {
   Bnd_Box2d aBox;
   aBox.SetVoid();
   if( !IsVoid() )
     aBox.Update(xmin, ymin, xmax, ymax);
   return aBox;
 }

 inline Select3D_Box2d operator = ( const Bnd_Box2d& theBox)
 { 
   SetField(theBox); 
   return *this;
 }

 inline void Update(const gp_Pnt2d& thePnt)
 {
  Bnd_Box2d aBox;
  aBox.Set(thePnt);
  if( !IsVoid() )
    aBox.Update(xmin, ymin, xmax, ymax);
  SetField(aBox);
 }

 inline void SetVoid()
 {
   xmin = ymin = ShortRealLast();
   xmax = ymax = ShortRealFirst();
 }

 inline Standard_Boolean IsVoid() const
 {
   return ( xmin == ShortRealLast() && ymin == ShortRealLast() && xmax == ShortRealFirst() && ymax == ShortRealFirst() );
 }

private: 
 inline void SetField(const Bnd_Box2d& theBox)
 {
  if( theBox.IsVoid() )
    SetVoid();
  else {
    Standard_Real x, y, x1, y1;
    theBox.Get(x, y, x1, y1);   

    xmin = DToF(x);
    ymin = DToF(y);
    xmax = DToF(x1);
    ymax = DToF(y1);
  }
 }

};

#endif




