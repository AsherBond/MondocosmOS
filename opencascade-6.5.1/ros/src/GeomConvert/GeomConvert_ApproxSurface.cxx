#include <GeomConvert_ApproxSurface.ixx>

#include <GeomAdaptor_HSurface.hxx>
#include <Precision.hxx>
#include <AdvApp2Var_ApproxAFunc2Var.hxx>
#include <Standard_Real.hxx>
#include <TColStd_HArray2OfReal.hxx>
#include <TColStd_HArray1OfReal.hxx>
#include <AdvApprox_PrefAndRec.hxx>

static Handle(Adaptor3d_HSurface) fonct; 

extern "C" void mySurfEval1(Standard_Integer * Dimension,
			   // Dimension
			   Standard_Real    * UStartEnd,
			   // StartEnd[2] in U
			   Standard_Real    * VStartEnd,
			   // StartEnd[2] in V
			   Standard_Integer * FavorIso,
			   // Choice of constante, 1 for U, 2 for V
			   Standard_Real    * ConstParam,
			   // Value of constant parameter
			   Standard_Integer * NbParams,
			   // Number of parameters N
			   Standard_Real    * Parameters,
			   // Values of parameters,
			   Standard_Integer * UOrder,
			   // Derivative Request in U
			   Standard_Integer * VOrder,
			   // Derivative Request in V
			   Standard_Real    * Result, 
			   // Result[Dimension,N]
			   Standard_Integer * ErrorCode)
                           // Error Code
{ 
  *ErrorCode = 0;
//  Standard_Integer idim;
  Standard_Integer jpar;
  Standard_Real Upar,Vpar;

// Dimension incorrecte
  if (*Dimension!=3) {
    *ErrorCode = 1;
  }

// Parametres incorrects
/* if (*FavorIso==1) {
    Upar = *ConstParam;
    if (( Upar < UStartEnd[0] ) || ( Upar > UStartEnd[1] )) {
      *ErrorCode = 2;
    }
    for (jpar=1;jpar<=*NbParams;jpar++) {
      Vpar = Parameters[jpar-1];
      if (( Vpar < VStartEnd[0] ) || ( Vpar > VStartEnd[1] )) {
        *ErrorCode = 2;
      }
    }
 }
 else {
    Vpar = *ConstParam;
    if (( Vpar < VStartEnd[0] ) || ( Vpar > VStartEnd[1] )) {
      *ErrorCode = 2;
    }
    for (jpar=1;jpar<=*NbParams;jpar++) {
      Upar = Parameters[jpar-1];
      if (( Upar < UStartEnd[0] ) || ( Upar > UStartEnd[1] )) {
        *ErrorCode = 2;
      }
    }
 }*/

// Initialisation

  fonct = fonct->UTrim(UStartEnd[0], UStartEnd[1], Precision::PConfusion());
  fonct = fonct->VTrim(VStartEnd[0], VStartEnd[1], Precision::PConfusion());
/*
  for (idim=1;idim<=*Dimension;idim++) {
    for (jpar=1;jpar<=*NbParams;jpar++) {
      Result[idim-1+(jpar-1)*(*Dimension)] = 0.;
    }
  }*/
 

 Standard_Integer Order = *UOrder + *VOrder;
 gp_Pnt pnt;
 gp_Vec vect, v1, v2, v3, v4, v5, v6, v7, v8, v9;

 if (*FavorIso==1) {
  Upar = *ConstParam;
  switch (Order) {
  case 0 :
    for (jpar=1;jpar<=*NbParams;jpar++) {
	Vpar = Parameters[jpar-1];
	pnt = fonct->Value(Upar,Vpar);
	Result[(jpar-1)*(*Dimension)] = pnt.X();
	Result[1+(jpar-1)*(*Dimension)] = pnt.Y(); 
	Result[2+(jpar-1)*(*Dimension)] = pnt.Z();
    }
    break;
  case 1 :
    for (jpar=1;jpar<=*NbParams;jpar++) {
	Vpar = Parameters[jpar-1];
	fonct->D1(Upar, Vpar, pnt, v1, v2);
        if (*UOrder==1) {
	  Result[(jpar-1)*(*Dimension)] = v1.X();
	  Result[1+(jpar-1)*(*Dimension)] = v1.Y(); 
	  Result[2+(jpar-1)*(*Dimension)] = v1.Z();
	}
	else {
	  Result[(jpar-1)*(*Dimension)] = v2.X();
	  Result[1+(jpar-1)*(*Dimension)] = v2.Y(); 
	  Result[2+(jpar-1)*(*Dimension)] = v2.Z();
	}
    }
    break;
  case 2 :
    for (jpar=1;jpar<=*NbParams;jpar++) {
	Vpar = Parameters[jpar-1];
	fonct->D2(Upar, Vpar, pnt, v1, v2, v3, v4, v5);
        if (*UOrder==2) {
	  Result[(jpar-1)*(*Dimension)] = v3.X();
	  Result[1+(jpar-1)*(*Dimension)] = v3.Y(); 
	  Result[2+(jpar-1)*(*Dimension)] = v3.Z();
	}
	else if (*UOrder==1) {
	  Result[(jpar-1)*(*Dimension)] = v5.X();
	  Result[1+(jpar-1)*(*Dimension)] = v5.Y(); 
	  Result[2+(jpar-1)*(*Dimension)] = v5.Z();
	}
	else if (*UOrder==0) {
	  Result[(jpar-1)*(*Dimension)] = v4.X();
	  Result[1+(jpar-1)*(*Dimension)] = v4.Y(); 
	  Result[2+(jpar-1)*(*Dimension)] = v4.Z();
	}
    }
    break;
  case 3 :
    for (jpar=1;jpar<=*NbParams;jpar++) {
	Vpar = Parameters[jpar-1];
	fonct->D3(Upar, Vpar, pnt, v1, v2, v3, v4, v5, v6, v7, v8, v9);
        if (*UOrder==2) {
	  Result[(jpar-1)*(*Dimension)] = v8.X();
	  Result[1+(jpar-1)*(*Dimension)] = v8.Y(); 
	  Result[2+(jpar-1)*(*Dimension)] = v8.Z();
	}
	else if (*UOrder==1) {
	  Result[(jpar-1)*(*Dimension)] = v9.X();
	  Result[1+(jpar-1)*(*Dimension)] = v9.Y(); 
	  Result[2+(jpar-1)*(*Dimension)] = v9.Z();
	}
    }
    break;
  case 4 :
    for (jpar=1;jpar<=*NbParams;jpar++) {
	Vpar = Parameters[jpar-1];
	vect = fonct->DN(Upar, Vpar, *UOrder, *VOrder);
	Result[(jpar-1)*(*Dimension)] = vect.X();
	Result[1+(jpar-1)*(*Dimension)] = vect.Y(); 
	Result[2+(jpar-1)*(*Dimension)] = vect.Z();
    }
    break;
  }
 }
 else { 
  Vpar = *ConstParam;
  switch (Order) {
  case 0 :
    for (jpar=1;jpar<=*NbParams;jpar++) {
	Upar = Parameters[jpar-1];
	pnt = fonct->Value(Upar,Vpar);
	Result[(jpar-1)*(*Dimension)] = pnt.X();
	Result[1+(jpar-1)*(*Dimension)] = pnt.Y(); 
	Result[2+(jpar-1)*(*Dimension)] = pnt.Z();
    }
    break;
  case 1 :
    for (jpar=1;jpar<=*NbParams;jpar++) {
	Upar = Parameters[jpar-1];
	fonct->D1(Upar, Vpar, pnt, v1, v2);
        if (*UOrder==1) {
	  Result[(jpar-1)*(*Dimension)] = v1.X();
	  Result[1+(jpar-1)*(*Dimension)] = v1.Y(); 
	  Result[2+(jpar-1)*(*Dimension)] = v1.Z();
	}
	else {
	  Result[(jpar-1)*(*Dimension)] = v2.X();
	  Result[1+(jpar-1)*(*Dimension)] = v2.Y(); 
	  Result[2+(jpar-1)*(*Dimension)] = v2.Z();
	}
    }
    break;
  case 2 :
    for (jpar=1;jpar<=*NbParams;jpar++) {
	Upar = Parameters[jpar-1];
	fonct->D2(Upar, Vpar, pnt, v1, v2, v3, v4, v5);
        if (*UOrder==2) {
	  Result[(jpar-1)*(*Dimension)] = v3.X();
	  Result[1+(jpar-1)*(*Dimension)] = v3.Y(); 
	  Result[2+(jpar-1)*(*Dimension)] = v3.Z();
	}
	else if (*UOrder==1) {
	  Result[(jpar-1)*(*Dimension)] = v5.X();
	  Result[1+(jpar-1)*(*Dimension)] = v5.Y(); 
	  Result[2+(jpar-1)*(*Dimension)] = v5.Z();
	}
	else if (*UOrder==0) {
	  Result[(jpar-1)*(*Dimension)] = v4.X();
	  Result[1+(jpar-1)*(*Dimension)] = v4.Y(); 
	  Result[2+(jpar-1)*(*Dimension)] = v4.Z();
	}
    }
    break;
  case 3 :
    for (jpar=1;jpar<=*NbParams;jpar++) {
	Upar = Parameters[jpar-1];
	fonct->D3(Upar, Vpar, pnt, v1, v2, v3, v4, v5, v6, v7, v8, v9);
        if (*UOrder==2) {
	  Result[(jpar-1)*(*Dimension)] = v8.X();
	  Result[1+(jpar-1)*(*Dimension)] = v8.Y(); 
	  Result[2+(jpar-1)*(*Dimension)] = v8.Z();
	}
	else if (*UOrder==1) {
	  Result[(jpar-1)*(*Dimension)] = v9.X();
	  Result[1+(jpar-1)*(*Dimension)] = v9.Y(); 
	  Result[2+(jpar-1)*(*Dimension)] = v9.Z();
	}
    }
    break;
  case 4 :
    for (jpar=1;jpar<=*NbParams;jpar++) {
	Upar = Parameters[jpar-1];
	vect = fonct->DN(Upar, Vpar, *UOrder, *VOrder);
	Result[(jpar-1)*(*Dimension)] = vect.X();
	Result[1+(jpar-1)*(*Dimension)] = vect.Y(); 
	Result[2+(jpar-1)*(*Dimension)] = vect.Z();
    }
    break;
  }
 }    	

}


//=======================================================================
//function : GeomConvert_ApproxSurface
//purpose  : 
//=======================================================================

GeomConvert_ApproxSurface::GeomConvert_ApproxSurface(const Handle(Geom_Surface)& Surf,
							   const Standard_Real Tol3d,
							   const GeomAbs_Shape UContinuity,
							   const GeomAbs_Shape VContinuity,
							   const Standard_Integer MaxDegU,
							   const Standard_Integer MaxDegV,
							   const Standard_Integer MaxSegments,
							   const Standard_Integer PrecisCode)
{
  Standard_Real    U0, U1, V0, V1;

  fonct = new (GeomAdaptor_HSurface)(Surf); // Initialisation de la surface algorithmique
  Surf->Bounds(U0, U1, V0, V1);

// " Init des nombres de sous-espaces et des tolerances"
  Standard_Integer nb1 = 0, nb2 = 0, nb3 = 1;
  Handle(TColStd_HArray1OfReal) nul1 =
  		 new TColStd_HArray1OfReal(1,1);
  nul1->SetValue(1,0.);
  Handle(TColStd_HArray2OfReal) nul2 =
  		 new TColStd_HArray2OfReal(1,1,1,4);
  nul2->SetValue(1,1,0.);
  nul2->SetValue(1,2,0.);
  nul2->SetValue(1,3,0.);
  nul2->SetValue(1,4,0.);
  Handle(TColStd_HArray1OfReal) eps3D =
  		 new TColStd_HArray1OfReal(1,1);
  eps3D->SetValue(1,Tol3d);
  Handle(TColStd_HArray2OfReal) epsfr =
  		 new TColStd_HArray2OfReal(1,1,1,4);
  epsfr->SetValue(1,1,Tol3d);
  epsfr->SetValue(1,2,Tol3d);
  epsfr->SetValue(1,3,Tol3d);
  epsfr->SetValue(1,4,Tol3d);

// " Init du type d'iso"
  GeomAbs_IsoType IsoType = GeomAbs_IsoV; 
  Standard_Integer NbDec;

  NbDec = fonct->NbUIntervals(GeomAbs_C2);
  TColStd_Array1OfReal UDec_C2(1, NbDec+1);
  fonct->UIntervals(UDec_C2, GeomAbs_C2);
  NbDec = fonct->NbVIntervals(GeomAbs_C2);
  TColStd_Array1OfReal VDec_C2(1, NbDec+1);
  fonct->VIntervals(VDec_C2, GeomAbs_C2);

  NbDec = fonct->NbUIntervals(GeomAbs_C3);
  TColStd_Array1OfReal UDec_C3(1, NbDec+1);
  fonct->UIntervals(UDec_C3, GeomAbs_C3);

  NbDec = fonct->NbVIntervals(GeomAbs_C3);
  TColStd_Array1OfReal VDec_C3(1, NbDec+1);
  fonct->VIntervals(VDec_C3, GeomAbs_C3);
  // Approximation avec decoupe preferentiel 
  // aux lieux de discontinuitees C2
  AdvApprox_PrefAndRec pUDec(UDec_C2,UDec_C3);
  AdvApprox_PrefAndRec pVDec(VDec_C2,VDec_C3);

//POP pour WNT
  AdvApp2Var_EvaluatorFunc2Var ev = mySurfEval1;
  AdvApp2Var_ApproxAFunc2Var approx(nb1, nb2, nb3,
				    nul1,nul1,eps3D,
				    nul2,nul2,epsfr,
				    U0,U1,V0,V1,
				    IsoType,UContinuity,VContinuity,PrecisCode,
//				    MaxDegU,MaxDegV,MaxSegments,mySurfEval1,
				    MaxDegU,MaxDegV,MaxSegments,ev,
				    pUDec,pVDec);

  myMaxError  = approx.MaxError(3,1);
  myBSplSurf  = approx.Surface(1);
  myIsDone    = approx.IsDone();
  myHasResult = approx.HasResult();
}


//=======================================================================
//function : Surface
//purpose  : 
//=======================================================================

 Handle(Geom_BSplineSurface) GeomConvert_ApproxSurface::Surface() const
{
  return myBSplSurf;
}

//=======================================================================
//function : IsDone
//purpose  : 
//=======================================================================

 Standard_Boolean GeomConvert_ApproxSurface::IsDone() const
{
  return myIsDone;
}

//=======================================================================
//function : HasResult
//purpose  : 
//=======================================================================

 Standard_Boolean GeomConvert_ApproxSurface::HasResult() const
{
  return myHasResult;
}

//=======================================================================
//function : MaxError
//purpose  : 
//=======================================================================

 Standard_Real GeomConvert_ApproxSurface::MaxError() const
{
  return myMaxError;
}
//=======================================================================
//function : Dump
//purpose  : 
//=======================================================================

void GeomConvert_ApproxSurface::Dump(Standard_OStream& o) const 
{
  o<<endl;
  if (!myHasResult) { o<<"No result"<<endl; }
  else {
    o<<"Result max error :"<< myMaxError <<endl;
    }
  o<<endl;
}







