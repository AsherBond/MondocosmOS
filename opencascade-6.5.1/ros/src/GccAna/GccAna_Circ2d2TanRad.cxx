// File GccAna_Circ2d2TanRad.cxx, REG 08/07/91
// Modified:	Thu Dec  5 09:46:23 1996
//    by:	Joelle CHAUVET
//		Indice de boucle pour par1sol,par2sol,pararg1,pararg2
//              (PRO6126)
// Modified:	Mon May 11 10:42:16 1998
// Author:	Joelle CHAUVET
//		Permutation pnttg1sol<->pnttg2sol oubliee dans certains cas
//              (CTS20080)

#include <GccAna_Circ2d2TanRad.ixx>

#include <ElCLib.hxx>
#include <gp_Ax2d.hxx>
#include <gp_Circ2d.hxx>
#include <gp_Lin2d.hxx>
#include <IntAna2d_AnaIntersection.hxx>
#include <IntAna2d_IntPoint.hxx>
#include <Standard_NegativeValue.hxx>
#include <Standard_OutOfRange.hxx>
#include <StdFail_NotDone.hxx>
#include <GccEnt_BadQualifier.hxx>
#include <Precision.hxx>

// circulaire tangent a deux cercles et de rayon donne
//====================================================
//========================================================================
// On initialise WellDone a false.                                       +
// On recupere le cercle C1 et le cercle C2.                             +
// On sort en erreur dans les cas ou la construction est impossible.     +
// On distingue les cas limites pour les triater separement.             +
// On fait la parallele a C1 dans le bon sens.                           +
// On fait la parallele a C2 dans le bon sens.                           +
// On intersecte les paralleles ==> point de centre de la solution.      +
// On cree la solution qu on ajoute aux solutions deja trouvees.         +
// On remplit les champs.                                                +
//========================================================================

GccAna_Circ2d2TanRad::
   GccAna_Circ2d2TanRad (const GccEnt_QualifiedCirc& Qualified1 ,
                         const GccEnt_QualifiedCirc& Qualified2 ,
                         const Standard_Real         Radius     ,
                         const Standard_Real         Tolerance  ):
   qualifier1(1,8) ,
   qualifier2(1,8),
   TheSame1(1,8)   ,
   TheSame2(1,8)   ,
   cirsol(1,8)     ,
   pnttg1sol(1,8)  ,
   pnttg2sol(1,8)  ,
   par1sol(1,8)    ,
   par2sol(1,8)    ,
   pararg1(1,8)    ,
   pararg2(1,8)    
{

   Standard_Real Tol = Abs(Tolerance);
   gp_Dir2d dirx(1.,0.);
   WellDone = Standard_False;
   NbrSol = 0;
   if (!(Qualified1.IsEnclosed() || Qualified1.IsEnclosing() || 
	 Qualified1.IsOutside() || Qualified1.IsUnqualified()) ||
       !(Qualified2.IsEnclosed() || Qualified2.IsEnclosing() || 
	 Qualified2.IsOutside() || Qualified2.IsUnqualified())) {
     GccEnt_BadQualifier::Raise();
     return;
   }
   Standard_Boolean invers = Standard_False;
   gp_Circ2d C1 = Qualified1.Qualified();
   gp_Circ2d C2 = Qualified2.Qualified();
   Standard_Real R1 = C1.Radius();
   Standard_Real R2 = C2.Radius();
   Standard_Real rbid = 0.;
   Standard_Integer signe=0;
   Standard_Real R3;
   Standard_Real dist;
   TColgp_Array1OfCirc2d C(1,8);
   C(1) = gp_Circ2d(C1);
   C(2) = gp_Circ2d(C2);
   Standard_Integer nbsol = 0;
   gp_Pnt2d center1(C1.Location());
   gp_Pnt2d center2(C2.Location());
   gp_Pnt2d center3;
   if (Radius < 0.0) { Standard_NegativeValue::Raise(); }
   else if ( C(2).Location().IsEqual(C(1).Location(),Precision::Confusion())){
     WellDone = Standard_True;
   }
   else {
     gp_Dir2d dir1(C(2).Location().XY()-C(1).Location().XY());
     dist = (center2).Distance(center1);
     if ((Qualified1.IsEnclosed()) && Qualified2.IsEnclosed()) {
//   =========================================================
       if (Radius*2.0-Abs(R1+R2-dist) > Tol) { WellDone = Standard_True; }
       else {
         if ((dist-R1-R2>Tol)||(Tol<Max(R1,R2)-dist-Min(R1,R2))) {
	   WellDone = Standard_True;
	 }
	 else if (Abs(dist-R1-R2)<=Tol||Abs(Max(R1,R2)-dist-Min(R1,R2))<=Tol) {
	   if (Abs(dist-R1-R2) <= Tol) {
	     rbid = R2+(dist-R1-R2)/2.0;
	     signe = -1;
	   }
	   else if (Abs(Max(R1,R2)-dist-Min(R1,R2)) <= Tol) {
	     if (R1 > R2) { R3 = R2; }
	     else {
	       C2 = gp_Circ2d(C1);
	       R3 = R1;
	     }
	     rbid = -R3+(Min(R1,R2)+dist-Max(R1,R2))/2.;
	     signe = 1;
	   }
	   gp_Ax2d axe(gp_Pnt2d(center2.XY()-rbid*dir1.XY()),dirx);
           cirsol(1) = gp_Circ2d(axe,Radius);
//         =================================
	   qualifier1(1) = Qualified1.Qualifier();
	   qualifier2(1) = Qualified2.Qualifier();
	   pnttg1sol(1)=gp_Pnt2d(center2.XY()+signe*R2*dir1.XY());
	   pnttg2sol(1)=gp_Pnt2d(center1.XY()+R1*dir1.XY());
	   TheSame1(1)  = 0;
	   TheSame2(1)  = 0;
	   WellDone = Standard_True;
	   NbrSol = 1;
	 }
         else {
	   C(1) = gp_Circ2d(gp_Ax2d(C(1).Location(),dirx),Abs(Radius-R1));
	   C(2) = gp_Circ2d(gp_Ax2d(C(2).Location(),dirx),Abs(Radius-R2));
	   nbsol = 1;
         }
       }
     }
     else if (((Qualified1.IsEnclosed()) && Qualified2.IsEnclosing()) ||
//   ===================================================================
	      ((Qualified1.IsEnclosing()) && Qualified2.IsEnclosed())) {
//            ========================================================
       if (Qualified1.IsEnclosing()) {
	 R3 = R1;
	 C(1) = gp_Circ2d(C2);
	 R1 = R2;
	 C(2) = gp_Circ2d(C1);
	 R2 = R3;
	 center3 = center1;
	 center1 = center2;
	 center2 = center3;
	 dir1.Reverse();
	 // il faudra permuter les points de tangence resultats
	 invers = Standard_True;
       }
       if ((R2-Radius>Tol) || (Tol<Radius-R1) || (Tol>R1-dist-R2) ||
	   ((Tol<Radius*2-R1-dist-R2)||(Tol<R1+R2-dist-Radius*2.0))) {
	 WellDone = Standard_True;
       }
       else if ((Abs(R2-Radius)<=Tol) || (Abs(R1-Radius)<=Tol)) {
	 if (Abs(R2-Radius) <= Tol) {
	   C(2) = gp_Circ2d(C2);
	   R3 = R2;
	   TheSame2(1) = 1;
	   TheSame1(1) = 0;
	   pnttg1sol(1) = gp_Pnt2d(C(2).Location().XY()+R3*dir1.XY());
	 }
	 else if (Abs(Radius-R1) <= Tol) {
	   C(2) = gp_Circ2d(C1);
	   R3 = R1;
	   TheSame1(1) = 1;
	   TheSame2(1) = 0;
	   pnttg2sol(1) = gp_Pnt2d(C(2).Location().XY()+R3*dir1.XY());
	 }
	 WellDone = Standard_True;
	 NbrSol = 1;
	 gp_Ax2d axe(C(2).Location(),dirx);
	 cirsol(1) = gp_Circ2d(axe,Radius);
//       =================================
	 qualifier1(1) = Qualified1.Qualifier();
	 qualifier2(1) = Qualified2.Qualifier();
       }
       else {
	 if ((Abs(R2+dist-R1) <= Tol) || (Abs(Radius*2.0-R1-dist-R2) < Tol)) {
	   if (Abs(R2+dist-R1) <= Tol) { signe = 1; }
	   else if (Abs(Radius*2.0-R1-dist-R2) < Tol) {signe = -1; }
	   WellDone = Standard_True;
	   NbrSol = 1;
	   gp_Ax2d axe(gp_Pnt2d(center1.XY()+
				signe*(R1-Radius)*dir1.XY()),dirx);
	   cirsol(1) = gp_Circ2d(axe,Radius);
//         =================================
	   qualifier1(1) = Qualified1.Qualifier();
	   qualifier2(1) = Qualified2.Qualifier();
	   pnttg1sol(1) = gp_Pnt2d(center1.XY()+signe*R1*dir1.XY());
	   pnttg2sol(1)=gp_Pnt2d(center2.XY()+R2*dir1.XY());
	   TheSame1(1)  = 0;
	   TheSame2(1)  = 0;
	 }
	 else {
	   C(1) = gp_Circ2d(gp_Ax2d(C(1).Location(),dirx),Abs(Radius-R1));
	   C(2) = gp_Circ2d(gp_Ax2d(C(2).Location(),dirx),Abs(Radius-R2));
	   nbsol = 1;
	 }
       }
     }
     else if (((Qualified1.IsEnclosed()) && (Qualified2.IsOutside())) ||
//   ===================================================================
	      ((Qualified1.IsOutside()) && (Qualified2.IsEnclosed()))) {
//            ========================================================
       if (Qualified1.IsOutside()) {
	 C(2) = gp_Circ2d(C1);
	 C(1) = gp_Circ2d(C2);
	 R3 = R1;
	 R1 = R2;
	 R2 = R3;
	 center3 = center1;
	 center1 = center2;
	 center2 = center3;
	 dir1.Reverse();
	 // il faudra permuter les points de tangence resultats
	 invers = Standard_True;
       }
       if ((Radius-R1>Tol)||(dist-R2-R1>Tol)||
	   ((R2-dist-R1+Radius*2>Tol)||(R1-R2-dist-Radius*2>Tol))){
	 WellDone = Standard_True;
       }
       else {
         if (((Radius-R1 > 0.0) && (Abs(dist-R1-R2) <= Tol)) ||
	     (Abs(R1-R2+dist-Radius*2.0) <= Tol) ||
	     (Abs(R1-R2-dist-Radius*2.0) <= Tol) || (Abs(dist-R1-R2) <= Tol)) {
	   if (Abs(R1-R2+dist-Radius*2.0) <= Tol) {
	     signe = -1;
	   }
	   else {
	     signe = 1;
	     if ((Radius-R1 > 0.0) && (Abs(dist-R1-R2) <= Tol)) {
	       R2 = R1;
	     }
	     else if (Abs(dist-R1-R2) <= Tol) {
	       R2 = R1;
	       if (Abs(R1-Radius) <= Tol) {
		 TheSame1(1) = 1;
	       }
	     }
	   }
	   WellDone = Standard_True;
	   NbrSol = 1;
	   gp_Ax2d axe(gp_Pnt2d(center1.XY()+
				signe*(R1-Radius)*dir1.XY()),dirx);
	   cirsol(1) = gp_Circ2d(axe,Radius);
//         =================================
	   qualifier1(1) = Qualified1.Qualifier();
	   qualifier2(1) = Qualified2.Qualifier();
	   pnttg1sol(1) = gp_Pnt2d(center1.XY()+signe*R1*dir1.XY());
	   pnttg2sol(1) = gp_Pnt2d(center2.XY()+signe*R2*dir1.XY());
           TheSame1(1)  = 0;
	   TheSame2(1)  = 0;
	 }
         else {
	   C(1) = gp_Circ2d(gp_Ax2d(C(1).Location(),dirx),Abs(Radius-R1));
	   C(2) = gp_Circ2d(gp_Ax2d(C(2).Location(),dirx),Radius+R2);
	   nbsol = 1;
	 }
       }
     }
     else if (((Qualified1.IsEnclosed()) && (Qualified2.IsUnqualified())) ||
//   =======================================================================
	      ((Qualified1.IsUnqualified()) && (Qualified2.IsEnclosed()))) {
//            ============================================================
       if (Qualified1.IsUnqualified()) {
	 C(1) = gp_Circ2d(C2);
	 C(2) = gp_Circ2d(C1);
	 R3 = R1;
	 R1 = R2;
	 R2 = R3;
	 center3 = center1;
	 center1 = center2;
	 center2 = center3;
	 dir1.Reverse();
	 // il faudra permuter les points de tangence resultats
	 invers = Standard_True;
       }
       if ((Radius-R1 > Tol) || (dist-R2-R1 > Tol)) { WellDone = Standard_True; }
       else {
	 if ((Abs(dist-R2-R1) <= Tol) || (Abs(Radius-R1) <= Tol)) {
	   WellDone = Standard_True;
	   NbrSol = 1;
	   gp_Ax2d ax(gp_Pnt2d(center1.XY()+(R1-Radius)*dir1.XY()),dirx);
           cirsol(1) = gp_Circ2d(ax,Radius);
//         ================================
	   qualifier1(1) = Qualified1.Qualifier();
	   qualifier2(1) = Qualified2.Qualifier();
	   pnttg2sol(1) = gp_Pnt2d(center1.XY()+(dist-R2)*dir1.XY());
	   TheSame2(1)  = 0;
           if (Abs(Radius-R1) > 0.0) {
	     TheSame1(1)  = 1;
	   }
	   else {
	     TheSame1(1)  = 0;
	     pnttg1sol(1) = gp_Pnt2d(center1.XY()+R1*dir1.XY());
	   }
	 }
         else {
	   C(3) = gp_Circ2d(gp_Ax2d(C(1).Location(),dirx),Abs(Radius-R1));
	   C(4) = gp_Circ2d(gp_Ax2d(C(2).Location(),dirx),Radius+R2);
	   C(1) = gp_Circ2d(gp_Ax2d(C(1).Location(),dirx),Abs(Radius-R1));
	   C(2) = gp_Circ2d(gp_Ax2d(C(2).Location(),dirx),Abs(Radius-R2));
	   nbsol = 2;
	 }
       }
     }
     else if ((Qualified1.IsEnclosing()) && (Qualified2.IsEnclosing())) {
//   ==================================================================
       if ((Tol < Max(R1,R2)-Radius) || (Tol < Max(R1,R2)-dist-Min(R1,R2)) ||
           (dist+R1+R2-Radius*2.0 > Tol)) { WellDone = Standard_True; }
       else {
         if ((Abs(dist+Min(R1,R2)-Max(R1,R2)) <= Tol) ||
	     (Abs(R1+R2+dist-2.0*Radius) <= Tol)) {
	   if (Abs(dist+Min(R1,R2)-Max(R1,R2)) <= Tol) {
	     signe = 1;
	   }
	   else {
	     signe = -1;
	   }
           WellDone = Standard_True;
           NbrSol = 1;
	   gp_Ax2d axe(gp_Pnt2d(center1.XY()+
				signe*(R1-Radius)*dir1.XY()),dirx);
	   cirsol(1) = gp_Circ2d(axe,Radius);
//         =================================
	   qualifier1(1) = Qualified1.Qualifier();
	   qualifier2(1) = Qualified2.Qualifier();
	   pnttg1sol(1) = gp_Pnt2d(center1.XY()+R1*dir1.XY());
	   pnttg2sol(1) = gp_Pnt2d(center2.XY()+signe*R2*dir1.XY());
	   TheSame1(1)  = 0;
	   TheSame2(1)  = 0;
         }
         else if (Abs(Radius-Max(R1,R2)) <= Tol) {
           WellDone = Standard_True;
           NbrSol = 1;
           if (R1 > R2) {
	     C(1) = gp_Circ2d(C1);
	     C(2) = gp_Circ2d(C2);
	     TheSame1(1) = 1;
	     TheSame2(1) = 0;
	     pnttg2sol(1) = gp_Pnt2d(center1.XY()+(dist+R2)*dir1.XY());
	   }
	   else {
	     C(1) = gp_Circ2d(C2);
	     C(2) = gp_Circ2d(C1);
	     TheSame1(1) = 0;
	     TheSame2(1) = 1;
	     pnttg1sol(1) = gp_Pnt2d(center1.XY()-R1*dir1.XY());
	   }
	   gp_Ax2d axe(C(1).Location(),dirx);
	   cirsol(1) = gp_Circ2d(axe,Radius);
//         =================================
	   qualifier1(1) = Qualified1.Qualifier();
	   qualifier2(1) = Qualified2.Qualifier();
	 }
	 else {
	   C(1) = gp_Circ2d(gp_Ax2d(C(1).Location(),dirx),Abs(Radius-R1));
	   C(2) = gp_Circ2d(gp_Ax2d(C(2).Location(),dirx),Abs(Radius-R2));
	   nbsol = 1;
	 }
       }
     }
     else if (((Qualified1.IsEnclosing()) && (Qualified2.IsOutside())) ||
//   ====================================================================
	      ((Qualified1.IsOutside()) && (Qualified2.IsEnclosing()))) {
//            =========================================================
       if (Qualified1.IsOutside()) {
	 C(1) = gp_Circ2d(C2);
	 C(2) = gp_Circ2d(C1);
	 R3 = R1;
	 R1 = R2;
	 R2 = R3;
	 center3 = center1;
	 center1 = center2;
	 center2 = center3;
	 dir1.Reverse();
	 // il faudra permuter les point de tangence resultats
	 invers = Standard_True;
       }
       if ((R1-Radius > Tol) || (Tol < R1+R2-dist) ||
           (dist-R2+R1-Radius*2.0>Tol)) {
	 WellDone = Standard_True;
       }
       else if (((Abs(R1-Radius)<=Tol) || (Abs(R1+R2-dist)<=Tol))||
		(Abs(dist-R2+R1-Radius*2.0) <= Tol)) {
	 WellDone = Standard_True;
	 NbrSol = 1;
	 if((Abs(R1-Radius) <= Tol) || (Abs(R1+R2-dist) <= Tol)){
	   TheSame1(1) = 1;
	 }
	 else {
	   TheSame1(1) = 0;
	 }
	 TheSame2(1) = 0;
	 gp_Ax2d axe(gp_Pnt2d(center1.XY()+(R1-Radius)*dir1.XY()),dirx);
	 cirsol(1) = gp_Circ2d(axe,Radius);
//       =================================
	 qualifier1(1) = Qualified1.Qualifier();
	 qualifier2(1) = Qualified2.Qualifier();
	 pnttg1sol(1) = gp_Pnt2d(center1.XY()-R1*dir1.XY());
	 pnttg2sol(1) = gp_Pnt2d(center1.XY()+(dist-R2)*dir1.XY());
       }
       else {
	 C(1) = gp_Circ2d(gp_Ax2d(C(1).Location(),dirx),Abs(Radius-R1));
	 C(2) = gp_Circ2d(gp_Ax2d(C(2).Location(),dirx),Radius+R2);
	 nbsol = 1;
       }
     }
     else if (((Qualified1.IsEnclosing()) && (Qualified2.IsUnqualified())) ||
//   ========================================================================
	      ((Qualified1.IsUnqualified()) && (Qualified2.IsEnclosing()))) {
//            =============================================================
       if (Qualified1.IsUnqualified()) {
	 C(1) = gp_Circ2d(C2);
	 C(2) = gp_Circ2d(C1);
	 R3 = R1;
	 R1 = R2;
	 R2 = R3;
	 center3 = center1;
	 center1 = center2;
	 center2 = center3;
	 invers = Standard_True;
	 dir1.Reverse();
       }
       if ((Tol < R1-dist-R2) || (R1-Radius > Tol)) { WellDone = Standard_True; }
       else if ((Abs(R1-Radius) <= Tol) || (Abs(R1-dist-R2) > 0.0)) {
         if (Abs(R1-Radius) <= Tol) {
	   TheSame1(1) = 1;
	   if((Abs(Radius-R2) <= Tol) && 
	      (center1.Distance(center2) <= Tol)) {
	     TheSame2(1) = 1;
	   }
         }
         else if (Abs(R1-dist-R2) > 0.0) {
	   TheSame1(1)  = 0;
	   TheSame2(1)  = 0;
         }
	 WellDone = Standard_True;
	 NbrSol = 1;
	 gp_Ax2d axe(gp_Pnt2d(center1.XY()+(Radius-R1)*dir1.XY()),dirx);
	 cirsol(1) = gp_Circ2d(axe,Radius);
//       =================================
	 qualifier1(1) = Qualified1.Qualifier();
	 qualifier2(1) = Qualified2.Qualifier();
	 pnttg1sol(1) = gp_Pnt2d(center1.XY()+R1*dir1.XY());
	 pnttg2sol(1) = gp_Pnt2d(center1.XY()+(dist+R2)*dir1.XY());
       }
       else {
	 C(3) = gp_Circ2d(gp_Ax2d(C(1).Location(),dirx),Abs(Radius-R1));
	 C(4) = gp_Circ2d(gp_Ax2d(C(2).Location(),dirx),Radius+R2);
	 C(1) = gp_Circ2d(gp_Ax2d(C(1).Location(),dirx),Abs(Radius-R1));
	 C(2) = gp_Circ2d(gp_Ax2d(C(2).Location(),dirx),Abs(Radius-R2));
	 nbsol    = 2;
       }
     }
     else if ((Qualified1.IsOutside()) && (Qualified2.IsOutside())) {
//   ==============================================================
       if (Tol < Max(R1,R2)-dist-Min(R1,R2)) { WellDone = Standard_True; }
       else if (dist-R1-R2-Radius*2.0 > Tol) { WellDone = Standard_True; }
       else {
         if (Abs(dist+Min(R1,R2)-Max(R1,R2)) <= Tol) {
           WellDone = Standard_True;
           NbrSol = 1;
           if (R1 < R2) { signe = -1; }
	   else { signe = -1; }
	   gp_Ax2d axe(gp_Pnt2d(center1.XY()-(Radius+R1)*dir1.XY()),
		       dirx);
	   cirsol(1) = gp_Circ2d(axe,Radius);
//         =================================
	   qualifier1(1) = Qualified1.Qualifier();
	   qualifier2(1) = Qualified2.Qualifier();
	   pnttg1sol(1) = gp_Pnt2d(center1.XY()+signe*R1*dir1.XY());
	   pnttg2sol(1) = gp_Pnt2d(pnttg1sol(1));
	   TheSame1(1)  = 0;
	   TheSame2(1)  = 0;
	 }
         else if (Abs(dist-R1-R2-Radius*2.0) <= Tol) {
           WellDone = Standard_True;
           NbrSol = 1;
	   gp_Ax2d ax(gp_Pnt2d(center1.XY()+(R1+Radius)*dir1.XY()),dirx);
	   cirsol(1) = gp_Circ2d(ax,Radius);
//         ================================
	   qualifier1(1) = Qualified1.Qualifier();
	   qualifier2(1) = Qualified2.Qualifier();
	   pnttg1sol(1) = gp_Pnt2d(center1.XY()+R1*dir1.XY());
	   pnttg2sol(1) = gp_Pnt2d(center2.XY()-R2*dir1.XY());
	   TheSame1(1)  = 0;
	   TheSame2(1)  = 0;
         }
	 else {
	   C(1) = gp_Circ2d(gp_Ax2d(C(1).Location(),dirx),Radius+R1);
	   C(2) = gp_Circ2d(gp_Ax2d(C(2).Location(),dirx),Radius+R2);
	   nbsol    = 1;
	 }
       }
     }
     else if (((Qualified1.IsOutside()) && (Qualified2.IsUnqualified())) ||
//   ======================================================================
	      ((Qualified1.IsUnqualified()) && (Qualified2.IsOutside()))) {
//            ===========================================================
       if (Qualified1.IsUnqualified()) {
	 C(1) = gp_Circ2d(C2);
	 R3 = R1;
	 R1 = R2;
	 C(2) = gp_Circ2d(C1);
	 R2 = R3;
	 center3 = center1;
	 center1 = center2;
	 center2 = center3;
	 dir1.Reverse();
	 // il faudra permuter les points de tangence resultats
	 invers = Standard_True;
       }
       if (Tol < R1-dist-R2) { WellDone = Standard_True; }
       else if ((Tol < R2-dist-R1) && (Tol < Radius*2.0-R2-dist+R1)) {
	 WellDone = Standard_True;
       }
       else if ((dist-R1-R2 > Tol) && (Tol < dist-R1-R2-Radius*2.0)) {
	 WellDone = Standard_True;
       }
       else {
         if ((Abs(R1-R2-dist)<=Tol) || 
	     ((Abs(dist-R1-R2)<=Tol) && (Abs(Radius*2.0-dist+R1+R2) <= Tol)) ||
	     ((Abs(dist+R1-R2)<=Tol) && (Abs(R2+dist-R1-Radius*2.0)<=Tol))) {
           WellDone = Standard_True;
           NbrSol = 1;
	   gp_Ax2d axe(gp_Pnt2d(center1.XY()+(Radius+R1)*dir1.XY()),
		       dirx);
	   cirsol(1) = gp_Circ2d(axe,Radius);
//         =================================
	   qualifier1(1) = Qualified1.Qualifier();
	   qualifier2(1) = Qualified2.Qualifier();
	   pnttg1sol(1) = gp_Pnt2d(center1.XY()+R1*dir1.XY());
	   pnttg2sol(1) = gp_Pnt2d(center1.XY()+(dist+R2)*dir1.XY());
	   TheSame1(1)  = 0;
	   TheSame2(1)  = 0;
         }
	 else {
	   C(3) = gp_Circ2d(gp_Ax2d(C(1).Location(),dirx),Radius+R1);
	   C(4) = gp_Circ2d(gp_Ax2d(C(2).Location(),dirx),Radius+R2);
	   C(1) = gp_Circ2d(gp_Ax2d(C(1).Location(),dirx),Radius+R1);
	   C(2) = gp_Circ2d(gp_Ax2d(C(1).Location(),dirx),Abs(Radius-R2));
	   nbsol    = 2;
	 }
       }
     }
     else if ((Qualified1.IsUnqualified()) && (Qualified2.IsUnqualified())) {
//   ======================================================================
       if ((dist-R1-R2>Tol)&&(Tol<(dist-R1-R2-Radius*2))) { 
	 WellDone = Standard_True; 
       }
       else if ((Max(R1,R2)-dist-Min(R1,R2)>Tol) &&
                (((Max(R1,R2)-dist-Min(R1,R2))-Radius*2.0 > Tol))) {
         WellDone = Standard_True;
       }
       else {
         Standard_Real p3 = Max(R1,R2)-Min(R1,R2)-dist-Radius*2.0;
         Standard_Real p4 = dist-R1-R2;
         Standard_Real p5 = Radius*2.0-dist+R1+R2;
         if (p3 > 0.0) {
           dist = Max(R1,R2)-Min(R1,R2)-Radius*2.0;
         }
         else if (p4 > 0.0 && p5 < 0.0) {
           R1 = dist-R2-Radius*2.0;
         }
	 C(1) = gp_Circ2d(gp_Ax2d(center1,dirx),Abs(Radius-R1));
	 C(2) = gp_Circ2d(gp_Ax2d(center2,dirx),Abs(Radius-R2));
	 C(3) = gp_Circ2d(gp_Ax2d(center1,dirx),Abs(Radius-R1));
	 C(4) = gp_Circ2d(gp_Ax2d(center2,dirx),Radius+R2);
	 C(5) = gp_Circ2d(gp_Ax2d(center1,dirx),Radius+R1);
	 C(6) = gp_Circ2d(gp_Ax2d(center2,dirx),Abs(Radius-R2));
	 C(7) = gp_Circ2d(gp_Ax2d(center1,dirx),Radius+R1);
	 C(8) = gp_Circ2d(gp_Ax2d(center2,dirx),Radius+R2);
         nbsol    = 4;
       }
     }
     if (nbsol > 0) {
       for (Standard_Integer j = 1 ; j <= nbsol ; j++) {
	 IntAna2d_AnaIntersection Intp(C(2*j-1),C(2*j));
	 if (Intp.IsDone()) {
	   if (!Intp.IsEmpty()) {
	     for (Standard_Integer i = 1 ; i <= Intp.NbPoints() ; i++) {
	       NbrSol++;
	       gp_Pnt2d Center(Intp.Point(i).Value());
	       cirsol(NbrSol) = gp_Circ2d(gp_Ax2d(Center,dirx),Radius);
//             =======================================================
	       dir1 = gp_Dir2d(Center.XY()-center1.XY());
	       gp_Dir2d dir2(Center.XY()-center2.XY());
	       Standard_Real distcc1 = Center.Distance(center1);
	       Standard_Real distcc2 = Center.Distance(center2);
	       if (!Qualified1.IsUnqualified()) { 
		 qualifier1(NbrSol) = Qualified1.Qualifier();
	       }
	       else if (Abs(distcc1+Radius-R1) < Tol) {
		 qualifier1(NbrSol) = GccEnt_enclosed;
	       }
	       else if (Abs(distcc1-R1-Radius) < Tol) {
		 qualifier1(NbrSol) = GccEnt_outside;
	       }
	       else { qualifier1(NbrSol) = GccEnt_enclosing; }
	       if (!Qualified2.IsUnqualified()) { 
		 qualifier2(NbrSol) = Qualified2.Qualifier();
	       }
	       else if (Abs(distcc2+Radius-R2) < Tol) {
		 qualifier2(NbrSol) = GccEnt_enclosed;
	       }
	       else if (Abs(distcc2-R2-Radius) < Tol) {
		 qualifier2(NbrSol) = GccEnt_outside;
	       }
	       else { qualifier2(NbrSol) = GccEnt_enclosing; }
	       if ((Center.Distance(center1) > R1) &&
		   (Radius < Center.Distance(center1)+R1)) {
		 pnttg1sol(NbrSol) = gp_Pnt2d(Center.XY()-Radius*dir1.XY());
	       }
	       else if ((Center.Distance(center1) < R1) &&
		   (Radius < R1)) {
		 pnttg1sol(NbrSol) = gp_Pnt2d(Center.XY()+Radius*dir1.XY());
	       }
	       else {
		 pnttg1sol(NbrSol) = gp_Pnt2d(Center.XY()-Radius*dir1.XY());
	       }
	       if ((Center.Distance(center2) > R2) &&
		   (Radius < Center.Distance(center2)+R2)) {
		 pnttg2sol(NbrSol) = gp_Pnt2d(Center.XY()-Radius*dir2.XY());
	       }
	       else if ((Center.Distance(center2) < R2) &&
		   (Radius < R2)) {
		 pnttg2sol(NbrSol) = gp_Pnt2d(Center.XY()+Radius*dir2.XY());
	       }
	       else {
		 pnttg2sol(NbrSol) = gp_Pnt2d(Center.XY()-Radius*dir2.XY());
	       }
	       TheSame1(NbrSol)  = 0;
	       TheSame2(NbrSol)  = 0;
	     }
	   }
	   WellDone = Standard_True;
	 }
       }
     }
   }
   // permutation des points de tangence resultats si necessaire
   if (invers) {
     gp_Pnt2d Psav;
     for (Standard_Integer i = 1 ; i <= NbrSol ; i++) {
       Psav = pnttg1sol(i);
       pnttg1sol(i) = pnttg2sol(i);
       pnttg2sol(i) = Psav;
     }
   }
   // calcul des parametres des points de tangence
   for (Standard_Integer i = 1 ; i <= NbrSol ; i++) {
     par1sol(i)=ElCLib::Parameter(cirsol(i),pnttg1sol(i));
     if (TheSame1(i) == 0) {
       pararg1(i)=ElCLib::Parameter(C1,pnttg1sol(i));
     }
     par2sol(i)=ElCLib::Parameter(cirsol(i),pnttg2sol(i));
     if (TheSame2(i) == 0) {
       pararg2(i)=ElCLib::Parameter(C2,pnttg2sol(i));
     }
   }
 }

Standard_Boolean GccAna_Circ2d2TanRad::
   IsDone () const { return WellDone; }

Standard_Integer GccAna_Circ2d2TanRad::
   NbSolutions () const { return NbrSol; }

gp_Circ2d GccAna_Circ2d2TanRad::
   ThisSolution (const Standard_Integer Index) const 
{
  if (!WellDone) { StdFail_NotDone::Raise(); }
  if (Index <= 0 ||Index > NbrSol) { Standard_OutOfRange::Raise(); }
  return cirsol(Index);
}

void GccAna_Circ2d2TanRad::
  WhichQualifier(const Standard_Integer Index   ,
		       GccEnt_Position& Qualif1 ,
		       GccEnt_Position& Qualif2 ) const
{
  if (!WellDone) { StdFail_NotDone::Raise(); }
  if (Index <= 0 ||Index > NbrSol) { Standard_OutOfRange::Raise(); }
  
  Qualif1 = qualifier1(Index);
  Qualif2 = qualifier2(Index);
}

void GccAna_Circ2d2TanRad::
   Tangency1 (const Standard_Integer Index,
              Standard_Real& ParSol,
              Standard_Real& ParArg,
              gp_Pnt2d& PntSol) const{
   if (!WellDone) { StdFail_NotDone::Raise(); }
   else if (Index <= 0 ||Index > NbrSol) { Standard_OutOfRange::Raise(); }
   else {
     if (TheSame1(Index) == 0) {
       ParSol = par1sol(Index);
       ParArg = pararg1(Index);
       PntSol = gp_Pnt2d(pnttg1sol(Index));
     }
     else { StdFail_NotDone::Raise(); }
   }
 }

void GccAna_Circ2d2TanRad::
   Tangency2 (const Standard_Integer Index,
              Standard_Real& ParSol,
              Standard_Real& ParArg,
              gp_Pnt2d& PntSol) const{
   if (!WellDone) { StdFail_NotDone::Raise(); }
   else if (Index <= 0 ||Index > NbrSol) { Standard_OutOfRange::Raise(); }
   else {
     if (TheSame2(Index) == 0) {
       ParSol = par2sol(Index);
       ParArg = pararg2(Index);
       PntSol = gp_Pnt2d(pnttg2sol(Index));
     }
     else { StdFail_NotDone::Raise(); }
   }
 }

Standard_Boolean GccAna_Circ2d2TanRad::
   IsTheSame1 (const Standard_Integer Index) const
{
  if (!WellDone) { StdFail_NotDone::Raise(); }
  if (Index <= 0 ||Index > NbrSol) { Standard_OutOfRange::Raise(); }

  if (TheSame1(Index) == 0) { return Standard_False; }
  
  return Standard_True;
}

Standard_Boolean GccAna_Circ2d2TanRad::
   IsTheSame2 (const Standard_Integer Index) const
{
  if (!WellDone) { StdFail_NotDone::Raise(); }
  if (Index <= 0 ||Index > NbrSol) { Standard_OutOfRange::Raise(); }
  
  if (TheSame2(Index) == 0) { return Standard_False; }
  return Standard_True; 
}
