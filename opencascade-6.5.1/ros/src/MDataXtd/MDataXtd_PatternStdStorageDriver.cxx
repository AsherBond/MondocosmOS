// File:	MDataXtd_PatternStdStorageDriver.cxx
// Created:	Mon Feb 16 14:29:45 1998
// Author:	Jing Cheng MEI
//		<mei@pinochiox.paris1.matra-dtv.fr>


#include <MDataXtd_PatternStdStorageDriver.ixx>
#include <PDataXtd_PatternStd.hxx>
#include <TDataXtd_PatternStd.hxx>
#include <CDM_MessageDriver.hxx>


//=======================================================================
//function : MDataXtd_PatternStdStorageDriver
//purpose  : 
//=======================================================================

MDataXtd_PatternStdStorageDriver::MDataXtd_PatternStdStorageDriver(const Handle(CDM_MessageDriver)& theMsgDriver):MDF_ASDriver(theMsgDriver)
{
}

//=======================================================================
//function : VersionNumber
//purpose  : 
//=======================================================================

Standard_Integer MDataXtd_PatternStdStorageDriver::VersionNumber() const
{
  return 0; 
}

//=======================================================================
//function : SourceType
//purpose  : 
//=======================================================================

Handle(Standard_Type) MDataXtd_PatternStdStorageDriver::SourceType() const
{ 
  return STANDARD_TYPE(TDataXtd_PatternStd);
}


//=======================================================================
//function : NewEmpty
//purpose  : 
//=======================================================================

Handle(PDF_Attribute) MDataXtd_PatternStdStorageDriver::NewEmpty() const
{
  return new PDataXtd_PatternStd ();
}

//=======================================================================
//function : Paste
//purpose  : 
//=======================================================================

void MDataXtd_PatternStdStorageDriver::Paste (const Handle(TDF_Attribute)&        Source,
					      const Handle(PDF_Attribute)&        Target,
					      const Handle(MDF_SRelocationTable)& SRelocTable) const
{
  Handle(TDataXtd_PatternStd) S = Handle(TDataXtd_PatternStd)::DownCast (Source);
  Handle(PDataXtd_PatternStd) T = Handle(PDataXtd_PatternStd)::DownCast (Target);
  
  T->Signature(S->Signature());
  T->Axis1Reversed(S->Axis1Reversed());
  T->Axis2Reversed(S->Axis2Reversed());
  
  Standard_Integer signature = S->Signature();
  
  Handle(TNaming_NamedShape) TNS;
  Handle(PNaming_NamedShape) PNS; 
  Handle(TDataStd_Real) TReal;
  Handle(PDataStd_Real) PReal;
  Handle(TDataStd_Integer) TInt;
  Handle(PDataStd_Integer) PInt;
  
  if (signature < 5) {
    TNS = S->Axis1();
    SRelocTable->HasRelocation(TNS, PNS);
    T->Axis1(PNS);
    TReal = S->Value1();
    SRelocTable->HasRelocation(TReal, PReal);
    T->Value1(PReal);
    TInt = S->NbInstances1();
    SRelocTable->HasRelocation(TInt, PInt);
    T->NbInstances1(PInt);
    
    if (signature > 2) {
      TNS = S->Axis2();
      SRelocTable->HasRelocation(TNS, PNS);
      T->Axis2(PNS);
      TReal = S->Value2();
      SRelocTable->HasRelocation(TReal, PReal);
      T->Value2(PReal);
      TInt = S->NbInstances2();
      SRelocTable->HasRelocation(TInt, PInt);
      T->NbInstances2(PInt);
    }
  }
  else {
    TNS = S->Mirror();
    SRelocTable->HasRelocation(TNS, PNS);
    T->Mirror(PNS);
  }
}



