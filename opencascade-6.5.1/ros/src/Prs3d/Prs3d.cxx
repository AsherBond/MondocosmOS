// File:	Prs3d.cxx
// Created:	Fri Aug 27 09:48:54 1993
// Author:	Jean-Louis FRENKEL
//		<jlf@stylox>


#include <Prs3d.ixx>

Standard_Boolean Prs3d::MatchSegment 
                 (const Quantity_Length X,
		  const Quantity_Length Y,
		  const Quantity_Length Z,
		  const Quantity_Length aDistance,
		  const gp_Pnt& P1,
		  const gp_Pnt& P2,
                  Quantity_Length& dist) {

		   Standard_Real X1,Y1,Z1,X2,Y2,Z2;
  P1.Coord(X1,Y1,Z1); P2.Coord(X2,Y2,Z2);
  Standard_Real DX = X2-X1; 
  Standard_Real DY = Y2-Y1; 
  Standard_Real DZ = Z2-Z1;
  Standard_Real Dist = DX*DX + DY*DY + DZ*DZ;
  if (Dist == 0.) return Standard_False;
  
  Standard_Real Lambda = ((X-X1)*DX + (Y-Y1)*DY + (Z-Z1)*DZ)/Dist;
  if ( Lambda < 0. || Lambda > 1. ) return Standard_False;
  dist =  Abs(X-X1-Lambda*DX) +
	  Abs(Y-Y1-Lambda*DY) + 
	  Abs(Z-Z1-Lambda*DZ);
  return (dist < aDistance);

}
				      
